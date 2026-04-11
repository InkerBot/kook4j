package bot.inker.kook4j.entity;

import bot.inker.kook4j.scope.AbstractScope;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PagedList<T> extends AbstractList<T> {
    private final List<T> items;
    private final Meta meta;
    private final @Nullable PagedListProvider<T> provider;

    public PagedList(List<T> items, Meta meta, @Nullable PagedListProvider<T> provider) {
        this.items = items;
        this.meta = meta;
        this.provider = provider;
    }

    @Override
    public T get(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    public Meta meta() {
        return meta;
    }

    public boolean hasNextPage() {
        return meta.page() < meta.pageTotal();
    }

    public CompletableFuture<PagedList<T>> nextPageAsync() {
        if (provider == null) {
            throw new UnsupportedOperationException("No provider available for fetching next page");
        }
        if (!hasNextPage()) {
            throw new IllegalStateException("No more pages available");
        }
        return provider.get(meta.page() + 1, meta.pageSize());
    }

    public PagedList<T> nextPage() {
        return AbstractScope.sync(nextPageAsync());
    }

    @FunctionalInterface
    public interface PagedListProvider<T> {
        CompletableFuture<PagedList<T>> get(int page, int pageSize);
    }

    public static final class Meta {

        private int page;
        private int pageTotal;
        private int pageSize;
        private int total;

        Meta() {
        }

        public int page() {
            return page;
        }

        public int pageTotal() {
            return pageTotal;
        }

        public int pageSize() {
            return pageSize;
        }

        public int total() {
            return total;
        }
    }
}
