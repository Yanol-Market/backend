package site.goldenticket.domain.product.repository;

import java.util.List;

public class CustomSlice<T> {

    private final List<T> content;
    private final boolean hasNext;
    private final long totalCount;

    public CustomSlice(List<T> content, boolean hasNext, long totalCount) {
        this.content = content;
        this.hasNext = hasNext;
        this.totalCount = totalCount;
    }

    public List<T> getContent() {
        return content;
    }

    public boolean hasContent() {
        return !content.isEmpty();
    }

    public boolean isLast() {
        return !hasNext;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public long getTotalElements() {
        return totalCount;
    }
}
