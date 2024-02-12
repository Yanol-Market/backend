package site.goldenticket.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import site.goldenticket.domain.product.constants.AreaCode;
import site.goldenticket.domain.product.constants.PriceRange;
import site.goldenticket.domain.product.constants.ProductStatus;
import site.goldenticket.domain.product.model.Product;
import site.goldenticket.domain.product.model.QProduct;
import site.goldenticket.domain.product.wish.entity.QWishProduct;

import java.time.LocalDate;
import java.util.ArrayList;
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
            PriceRange priceRange, LocalDate cursorCheckInDate, Long cursorId, Pageable pageable, Long userId
    ) {
        QProduct product = QProduct.product;
        QWishProduct wishProduct = QWishProduct.wishProduct;

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(buildRegionCondition(product, areaCode));
        predicate.and(buildAccommodationNameCondition(product, keyword));
        predicate.and(buildCheckInCheckOutCondition(product, checkInDate, checkOutDate));
        predicate.and(buildPriceRangeCondition(product, priceRange));
        predicate.and(buildStatusCondition(product));

        long totalCount = jpaQueryFactory
                .selectFrom(product)
                .where(predicate)
                .fetchResults()
                .getTotal();

        predicate.and(buildCursorCondition(product, cursorCheckInDate, cursorId));

        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(product, pageable);

        QueryResults<Product> results;

        if (userId != null) {
            results = jpaQueryFactory
                    .selectFrom(product)
                    .leftJoin(product.wishProducts, wishProduct)
                    .on(wishProduct.userId.eq(userId))
                    .where(predicate)
                    .orderBy(orderSpecifiers)
                    .limit(pageable.getPageSize() + 1)
                    .fetchResults();
        } else {
            results = jpaQueryFactory
                    .selectFrom(product)
                    .where(predicate)
                    .orderBy(orderSpecifiers)
                    .limit(pageable.getPageSize() + 1)
                    .fetchResults();
        }

        List<Product> content = results.getResults();
        boolean hasNext = results.getTotal() > pageable.getPageSize();

        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new CustomSlice<>(content, hasNext, totalCount);
    }

    @Override
    public CustomSlice<Product> getProductsByAreaCode(
            AreaCode areaCode,  LocalDate cursorCheckInDate, Long cursorId, Pageable pageable, Long userId
    ) {
        QProduct product = QProduct.product;
        QWishProduct wishProduct = QWishProduct.wishProduct;

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(buildRegionCondition(product, areaCode));

        long totalCount = jpaQueryFactory
                .selectFrom(product)
                .where(predicate)
                .fetchResults()
                .getTotal();

        predicate.and(buildCursorCondition(product, cursorCheckInDate, cursorId));

        OrderSpecifier[] orderSpecifiers = createOrderSpecifier(product, pageable);

        QueryResults<Product> results;

        if (userId != null) {
            results = jpaQueryFactory
                    .selectFrom(product)
                    .leftJoin(product.wishProducts, wishProduct)
                    .on(wishProduct.userId.eq(userId))
                    .where(predicate)
                    .orderBy(orderSpecifiers)
                    .limit(pageable.getPageSize() + 1)
                    .fetchResults();
        } else {
            results = jpaQueryFactory
                    .selectFrom(product)
                    .where(predicate)
                    .orderBy(orderSpecifiers)
                    .limit(pageable.getPageSize() + 1)
                    .fetchResults();
        }

        List<Product> content = results.getResults();
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new CustomSlice<>(content, hasNext, totalCount);
    }

    private BooleanExpression buildRegionCondition(QProduct product, AreaCode areaCode) {
        return (areaCode != AreaCode.ALL) ? product.areaCode.eq(areaCode) : null;
    }

    private BooleanExpression buildAccommodationNameCondition(QProduct product, String keyword) {
        return Expressions.booleanTemplate("lower(replace({0}, ' ', '')) like '%' || lower(replace({1}, ' ', '')) || '%'", product.accommodationName, keyword);
    }

    private BooleanExpression buildCheckInCheckOutCondition(QProduct product, LocalDate checkInDate, LocalDate checkOutDate) {
        return product.checkInDate.between(checkInDate, checkOutDate)
                .and(product.checkOutDate.between(checkInDate, checkOutDate));
    }

    private BooleanExpression buildPriceRangeCondition(QProduct product, PriceRange priceRange) {
        return (priceRange != PriceRange.FULL_RANGE) ? product.goldenPrice.between(priceRange.getMinPrice(), priceRange.getMaxPrice()) : null;
    }

    private BooleanExpression buildStatusCondition(QProduct product) {
        return product.productStatus.in(ProductStatus.SELLING, ProductStatus.RESERVED);
    }

    private BooleanExpression buildCursorCondition(QProduct product, LocalDate cursorCheckInDate, Long cursorId) {
        return (cursorCheckInDate != null && cursorId != null) ? product.checkInDate.gt(cursorCheckInDate).or(product.checkInDate.eq(cursorCheckInDate).and(product.id.lt(cursorId))) : null;
    }

    private OrderSpecifier[] createOrderSpecifier(QProduct product, Pageable pageable) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        Sort sort = pageable.getSort();
        String property = sort.stream().findFirst().map(Sort.Order::getProperty).orElse(null);

        if ("checkInDate".equals(property)) {
            Sort.Direction direction = sort.stream().findFirst().map(Sort.Order::getDirection).orElse(Sort.Direction.ASC);

            orderSpecifiers.add(new OrderSpecifier(direction == Sort.Direction.DESC ? Order.DESC : Order.ASC, product.checkInDate));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.id));
        }

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
