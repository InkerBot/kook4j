package bot.inker.kook4j.http;

import bot.inker.kook4j.exception.KookConnectionException;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public final class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        var t = new Thread(r, "kook4j-ratelimit");
        t.setDaemon(true);
        return t;
    });

    private final ConcurrentHashMap<String, BucketState> buckets = new ConcurrentHashMap<>();
    private volatile long globalResetAt = 0;

    public static String bucketFromPath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private static int parseIntHeader(Response response, String name, int defaultValue) {
        var value = response.header(name);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public CompletableFuture<Void> acquirePermitAsync(String bucket) {
        long waitMs = computeWaitMs(bucket);
        if (waitMs <= 0) {
            return CompletableFuture.completedFuture(null);
        }
        if (waitMs > 0) {
            log.debug("Rate limit wait {}ms for bucket [{}]", waitMs, bucket);
        }
        return delayAsync(waitMs);
    }

    public CompletableFuture<Void> delayAsync(long millis) {
        if (millis <= 0) {
            return CompletableFuture.completedFuture(null);
        }
        var future = new CompletableFuture<Void>();
        SCHEDULER.schedule(() -> future.complete(null), millis, TimeUnit.MILLISECONDS);
        return future;
    }

    public void acquirePermit(String bucket) {
        try {
            acquirePermitAsync(bucket).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KookConnectionException("Interrupted while waiting for rate limit reset", e);
        } catch (ExecutionException e) {
            throw new KookConnectionException("Rate limit wait failed", e.getCause());
        }
    }

    public void update(Response response) {
        var bucketName = response.header("X-Rate-Limit-Bucket");
        if (bucketName == null || bucketName.isEmpty()) {
            return;
        }

        int limit = parseIntHeader(response, "X-Rate-Limit-Limit", -1);
        int remaining = parseIntHeader(response, "X-Rate-Limit-Remaining", -1);
        int resetSeconds = parseIntHeader(response, "X-Rate-Limit-Reset", -1);

        if (limit < 0 || remaining < 0 || resetSeconds < 0) {
            return;
        }

        long resetAt = System.currentTimeMillis() + resetSeconds * 1000L;

        buckets.compute(bucketName, (key, existing) -> {
            if (existing == null) {
                return new BucketState(limit, remaining, resetAt);
            }
            existing.limit = limit;
            existing.remaining = remaining;
            existing.resetAt = resetAt;
            return existing;
        });

        if (response.header("X-Rate-Limit-Global") != null) {
            globalResetAt = resetAt;
        }
    }

    public long getRetryAfterMs(Response response) {
        int resetSeconds = parseIntHeader(response, "X-Rate-Limit-Reset", 0);
        return Math.max(resetSeconds, 0) * 1000L;
    }

    private long computeWaitMs(String bucket) {
        long now = System.currentTimeMillis();
        long globalWait = globalResetAt - now;

        var state = buckets.get(bucket);
        long bucketWait = (state != null && state.remaining == 0) ? state.resetAt - now : 0L;

        return Math.max(globalWait, bucketWait);
    }

    private static final class BucketState {
        volatile int limit;
        volatile int remaining;
        volatile long resetAt;

        BucketState(int limit, int remaining, long resetAt) {
            this.limit = limit;
            this.remaining = remaining;
            this.resetAt = resetAt;
        }
    }
}
