package site.goldenticket.common.pagination.slice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class CustomSlice<T> implements Slice<T> {

    private final List<T> content;
    private final Pageable pageable;
    private final boolean hasNext;
    private final long totalCount;

    public CustomSlice(List<T> content, Pageable pageable, boolean hasNext, long totalCount) {
        this.content = content;
        this.pageable = pageable;
        this.hasNext = hasNext;
        this.totalCount = totalCount;
    }

    @Override
    public int getNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getSize() {
        return pageable.getPageSize();
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public List<T> getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !hasNext;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public boolean hasPrevious() {
        return pageable.getPageNumber() > 0;
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? pageable.next() : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
    }

    @Override
    public <U> Slice<U> map(Function<? super T, ? extends U> converter) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }

    public long getTotalElements() {
        return totalCount;
    }
}
