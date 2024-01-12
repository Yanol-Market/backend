package site.goldenticket.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import site.goldenticket.common.constants.AreaCode;
import site.goldenticket.common.constants.PriceRange;
import site.goldenticket.common.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.model.QProduct;
import site.goldenticket.common.pagination.slice.CustomSlice;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public CustomSlice<Product> getProductsBySearch(
            AreaCode areaCode, String keyword, LocalDate checkInDate, LocalDate checkOutDate,
            PriceRange priceRange, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable
    ) {
        QProduct product = QProduct.product;

        BooleanBuilder predicate = new BooleanBuilder()
                .and(buildRegionCondition(product, areaCode))
                .and(buildAccommodationNameCondition(product, keyword))
                .and(buildCheckInCheckOutCondition(product, checkInDate, checkOutDate))
                .and(buildPriceRangeCondition(product, priceRange))
                .and(buildStatusCondition(product));

        long totalCount = jpaQueryFactory
                .selectFrom(product)
                .where(predicate)
                .fetchCount();

        predicate.and(buildCursorCondition(product, cursorCheckInDate, cursorId));

        OrderSpecifier[] orderSpecifiers = {product.checkInDate.asc(), product.id.desc()};

        List<Product> content = jpaQueryFactory
                .selectFrom(product)
                .where(predicate)
                .orderBy(orderSpecifiers)
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new CustomSlice<>(content, pageable, hasNext, totalCount);
    }

    private BooleanExpression buildRegionCondition(QProduct product, AreaCode areaCode) {
        return areaCode != null ? product.areaCode.eq(areaCode) : null;
    }

    private BooleanExpression buildAccommodationNameCondition(QProduct product, String keyword) {
        return Expressions.booleanTemplate(
                "lower(replace({0}, ' ', '')) like '%' || lower(replace({1}, ' ', '')) || '%'",
                product.accommodationName,
                keyword
        );
    }

    private BooleanExpression buildCheckInCheckOutCondition(QProduct product, LocalDate checkInDate, LocalDate checkOutDate) {
        return product.checkInDate.between(checkInDate, checkOutDate).and(product.checkOutDate.between(checkInDate, checkOutDate));
    }

    private BooleanExpression buildPriceRangeCondition(QProduct product, PriceRange priceRange) {
        return priceRange != null ? product.goldenPrice.between(priceRange.getMinPrice(), priceRange.getMaxPrice()) : null;
    }

    private BooleanExpression buildStatusCondition(QProduct product) {
        return product.productStatus.in(ProductStatus.SELLING, ProductStatus.RESERVED);
    }

    private BooleanExpression buildCursorCondition(QProduct product, LocalDate cursorCheckInDate, Long cursorId) {
        return (cursorCheckInDate != null && cursorId != null) ?
                product.checkInDate.gt(cursorCheckInDate)
                    .or(product.checkInDate.eq(cursorCheckInDate).and(product.id.lt(cursorId)))
                : null;
    }
}
