package bot.inker.kook4j.http;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }

    @Test
    void bucketFromPath_stripsLeadingSlash() {
        assertEquals("guild/list", RateLimiter.bucketFromPath("/guild/list"));
    }

    @Test
    void bucketFromPath_noLeadingSlash_unchanged() {
        assertEquals("guild/list", RateLimiter.bucketFromPath("guild/list"));
    }

    @Test
    void bucketFromPath_rootSlash_becomesEmpty() {
        assertEquals("", RateLimiter.bucketFromPath("/"));
    }

    @Test
    void bucketFromPath_deepPath_onlyStripsFirst() {
        assertEquals("api/v3/user/me", RateLimiter.bucketFromPath("/api/v3/user/me"));
    }

    @Test
    void acquirePermitAsync_completesImmediately_whenNoBucketState() {
        var future = rateLimiter.acquirePermitAsync("unknown-bucket");
        assertTrue(future.isDone(), "Should complete immediately with no state");
    }

    @Test
    void acquirePermitAsync_completesImmediately_whenBucketHasRemaining() {
        var response = mockResponse(200, "bucket-a", "5", "3", "60", null);
        rateLimiter.update(response);

        var future = rateLimiter.acquirePermitAsync("bucket-a");
        assertTrue(future.isDone(), "Should not wait when remaining > 0");
    }

    @Test
    void acquirePermitAsync_waits_whenBucketExhausted() {
        // remaining=0, resetAt ≈ now + 60s
        var response = mockResponse(200, "bucket-b", "5", "0", "60", null);
        rateLimiter.update(response);

        var future = rateLimiter.acquirePermitAsync("bucket-b");
        assertFalse(future.isDone(), "Should not be done immediately when bucket exhausted");
        future.cancel(true);
    }

    @Test
    void acquirePermitAsync_waits_whenGlobalLimitActive() {
        var response = mockResponse(429, "bucket-c", "5", "0", "60", "true");
        rateLimiter.update(response);

        // Different bucket should still be blocked by global limit
        var future = rateLimiter.acquirePermitAsync("other-bucket");
        assertFalse(future.isDone(), "Should wait due to global rate limit");
        future.cancel(true);
    }

    @Test
    void acquirePermitAsync_completesImmediately_afterResetElapsed() throws Exception {
        // remaining=0 but reset is already in the past (reset=0 seconds from now)
        var response = mockResponse(200, "bucket-d", "5", "0", "0", null);
        rateLimiter.update(response);

        var future = rateLimiter.acquirePermitAsync("bucket-d");
        // resetAt = now + 0*1000 = now (already elapsed)
        future.get(100, TimeUnit.MILLISECONDS);
        assertTrue(future.isDone());
    }

    @Test
    void delayAsync_completesImmediately_forZeroMs() {
        var future = rateLimiter.delayAsync(0);
        assertTrue(future.isDone(), "delayAsync(0) should complete immediately");
    }

    @Test
    void delayAsync_completesImmediately_forNegativeMs() {
        var future = rateLimiter.delayAsync(-100);
        assertTrue(future.isDone(), "delayAsync(<0) should complete immediately");
    }

    @Test
    void delayAsync_completesAfterDelay() throws Exception {
        long start = System.currentTimeMillis();
        rateLimiter.delayAsync(150).get(1, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 100, "Should wait at least ~150ms, waited " + elapsed + "ms");
    }

    @Test
    void update_ignoresResponse_whenBucketHeaderMissing() {
        var response = mockResponseNoHeaders(200);
        assertDoesNotThrow(() -> rateLimiter.update(response));

        var future = rateLimiter.acquirePermitAsync("no-bucket");
        assertTrue(future.isDone(), "Should still complete immediately");
    }

    @Test
    void update_ignoresResponse_whenIncompleteHeaders() {
        // Has bucket name but no limit/remaining/reset
        var response = new Response.Builder()
                .request(new Request.Builder().url("https://example.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("OK")
                .header("X-Rate-Limit-Bucket", "partial")
                .body(ResponseBody.create("", MediaType.get("application/json")))
                .build();
        assertDoesNotThrow(() -> rateLimiter.update(response));
    }

    @Test
    void update_updatesExistingBucketState() {
        // First update: remaining=3 (no wait)
        rateLimiter.update(mockResponse(200, "bucket-e", "5", "3", "60", null));
        assertTrue(rateLimiter.acquirePermitAsync("bucket-e").isDone());

        // Second update: remaining=0 (should wait now)
        rateLimiter.update(mockResponse(200, "bucket-e", "5", "0", "60", null));
        var future = rateLimiter.acquirePermitAsync("bucket-e");
        assertFalse(future.isDone());
        future.cancel(true);
    }

    @Test
    void getRetryAfterMs_returnsZero_whenResetZero() {
        var response = mockResponse(429, "bucket", "5", "0", "0", null);
        assertEquals(0L, rateLimiter.getRetryAfterMs(response));
    }

    @Test
    void getRetryAfterMs_convertsSecondsToMs() {
        var response = mockResponse(429, "bucket", "5", "0", "3", null);
        assertEquals(3000L, rateLimiter.getRetryAfterMs(response));
    }

    @Test
    void getRetryAfterMs_defaultsToZero_whenHeaderMissing() {
        var response = mockResponseNoHeaders(429);
        assertEquals(0L, rateLimiter.getRetryAfterMs(response));
    }

    @Test
    void acquirePermit_doesNotBlock_whenNoBucketState() {
        long start = System.currentTimeMillis();
        rateLimiter.acquirePermit("no-state-bucket");
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 500, "Should return almost immediately, took " + elapsed + "ms");
    }

    private static Response mockResponse(int code, String bucket, String limit,
                                         String remaining, String reset, String global) {
        var builder = new Response.Builder()
                .request(new Request.Builder().url("https://example.com/api/v3/test").build())
                .protocol(Protocol.HTTP_1_1)
                .code(code).message("Test")
                .body(ResponseBody.create("{}", MediaType.get("application/json")));

        if (bucket != null)    builder.header("X-Rate-Limit-Bucket", bucket);
        if (limit != null)     builder.header("X-Rate-Limit-Limit", limit);
        if (remaining != null) builder.header("X-Rate-Limit-Remaining", remaining);
        if (reset != null)     builder.header("X-Rate-Limit-Reset", reset);
        if (global != null)    builder.header("X-Rate-Limit-Global", global);

        return builder.build();
    }

    private static Response mockResponseNoHeaders(int code) {
        return new Response.Builder()
                .request(new Request.Builder().url("https://example.com/api/v3/test").build())
                .protocol(Protocol.HTTP_1_1)
                .code(code).message("Test")
                .body(ResponseBody.create("{}", MediaType.get("application/json")))
                .build();
    }
}
