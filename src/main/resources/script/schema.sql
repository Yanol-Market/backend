CREATE TABLE `users`
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    email          VARCHAR(100) NOT NULL COMMENT '이메일',
    name           VARCHAR(100) NOT NULL COMMENT '이름',
    nickname       VARCHAR(100) NOT NULL COMMENT '닉네임',
    password       VARCHAR(255) NOT NULL COMMENT '비밀번호',
    phone_number   VARCHAR(100) NOT NULL COMMENT '전화번호',
    image_url      VARCHAR(255) COMMENT '이미지 URL',
    bank_name      VARCHAR(100) COMMENT '은행 이름',
    account_number VARCHAR(100) COMMENT '계좌 번호',
    role           VARCHAR(50)  NOT NULL COMMENT '역할',
    yanolja_id     BIGINT COMMENT '야놀자 ID',
    deleted        BOOLEAN      NOT NULL COMMENT '삭제 여부',
    created_at     DATETIME COMMENT '생성 일시',
    updated_at     DATETIME COMMENT '수정 일시'
) COMMENT '사용자' ENGINE = InnoDB
                DEFAULT CHARSET = utf8mb4
                COLLATE = utf8mb4_bin;

CREATE TABLE `product`
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    user_id               BIGINT       NOT NULL COMMENT '사용자 ID',
    reservation_id        BIGINT COMMENT '예약 ID',
    reservation_type      VARCHAR(50)  NOT NULL COMMENT '예약 유형 확인',
    reservation_date      DATE COMMENT '예약 날짜',
    accommodation_name    VARCHAR(255) NOT NULL COMMENT '숙소 이름',
    accommodation_address VARCHAR(255) NOT NULL COMMENT '숙소 주소',
    accommodation_image   VARCHAR(255) NOT NULL COMMENT '숙소 이미지',
    room_name             VARCHAR(255) NOT NULL COMMENT '방 이름',
    check_in_date         DATE         NOT NULL COMMENT '체크인 날짜',
    check_in_time         TIME(6)      NOT NULL COMMENT '체크인 시간',
    check_out_date        DATE         NOT NULL COMMENT '체크아웃 날짜',
    check_out_time        TIME(6)      NOT NULL COMMENT '체크아웃 시간',
    standard_number       INT          NOT NULL COMMENT '기본 인원',
    maximum_number        INT          NOT NULL COMMENT '최대 인원',
    yanolja_price         INT COMMENT '야놀자 가격',
    origin_price          INT          NOT NULL COMMENT '원래 가격',
    golden_price          INT          NOT NULL COMMENT '등록 가격',
    area_code             VARCHAR(50)  NOT NULL COMMENT '시도 코드',
    content               VARCHAR(500) NOT NULL COMMENT '내용',
    product_status        VARCHAR(50) CHECK (product_status IN ('SELLING', 'RESERVED',
                                                                'SOLD_OUT', 'EXPIRED')) COMMENT '상품 상태',
    view_count            INT          NOT NULL COMMENT '조회수',
    seller_view_check     BOOLEAN      NOT NULL COMMENT '판매자 VIEW',
    created_at            DATETIME COMMENT '생성 일시',
    updated_at            DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '상품' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `agreement`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '동의내역 ID',
    user_id    BIGINT  NOT NULL COMMENT '사용자 ID',
    marketing  BOOLEAN NOT NULL COMMENT '마케팅 설정 여부',
    created_at DATETIME COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '동의내역' ENGINE = InnoDB
                 DEFAULT CHARSET = utf8mb4
                 COLLATE = utf8mb4_bin;

CREATE TABLE `deleted`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    user_id    BIGINT       NOT NULL COMMENT '사용자 ID',
    reason     VARCHAR(500) NOT NULL COMMENT '지역',
    created_at DATETIME COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '사용자' ENGINE = InnoDB
                DEFAULT CHARSET = utf8mb4
                COLLATE = utf8mb4_bin;

CREATE TABLE `wish_region`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '관심 지역 ID',
    user_id    BIGINT      NOT NULL COMMENT '사용자 ID',
    region     VARCHAR(50) NOT NULL COMMENT '지역',
    created_at DATETIME COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '관심지역' ENGINE = InnoDB
                 DEFAULT CHARSET = utf8mb4
                 COLLATE = utf8mb4_bin;

CREATE TABLE `wish_product`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '찜 ID',
    user_id    BIGINT NOT NULL COMMENT '사용자 ID',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    created_at DATETIME COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) COMMENT '찜' ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4
              COLLATE = utf8mb4_bin;

CREATE TABLE `alert`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '알림 ID',
    user_id    BIGINT       NOT NULL COMMENT '사용자 ID',
    content    VARCHAR(500) NOT NULL COMMENT '내용',
    viewed     BOOLEAN      NOT NULL COMMENT '읽음 여부',
    created_at DATETIME COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '알림' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `nego`
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '네고 ID',
    product_id      BIGINT      NOT NULL COMMENT '상품 ID',
    user_id         BIGINT      NOT NULL COMMENT '구매자 ID',
    price           INTEGER     NOT NULL COMMENT '네고 가격',
    count           INTEGER     NOT NULL COMMENT '네고 횟수',
    status          VARCHAR(50) NOT NULL COMMENT '네고 상태',
    consent         BOOLEAN     NOT NULL COMMENT '승낙 여부',
    expiration_time DATETIME COMMENT '만료 일시',
    created_at      DATETIME COMMENT '생성 일시',
    updated_at      DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT '네고' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `orders`
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '주문 ID',
    product_id          BIGINT      NOT NULL COMMENT '상품 ID',
    user_id             BIGINT      NOT NULL COMMENT '구매자 ID',
    `status`            VARCHAR(50) NOT NULL COMMENT '주문 상태',
    nego_status         VARCHAR(50) COMMENT '네고 상태',
    price               INT         NOT NULL COMMENT '주문 가격',
    customer_view_check BOOLEAN     NOT NULL COMMENT '구매자 VIEW',
    created_at          DATETIME COMMENT '생성 일시',
    updated_at          DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) COMMENT '주문' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `payment`
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결제 ID',
    order_id            BIGINT      NOT NULL COMMENT '주문 ID',
    imp_uid             VARCHAR(255) COMMENT '포트원 거래 ID',
    payment_method      VARCHAR(100) COMMENT '결제 수단',
    pg_tid              VARCHAR(255) COMMENT 'PG사 거래 ID',
    escrow              BOOLEAN COMMENT '에스크로 여부',
    apply_num           VARCHAR(255) COMMENT '신용카드 승인번호',
    bank_code           VARCHAR(255) COMMENT '은행 표준코드',
    bank_name           VARCHAR(255) COMMENT '은행명',
    card_code           VARCHAR(255) COMMENT '카드사 코드번호',
    card_name           VARCHAR(255) COMMENT '카드사명',
    card_number         VARCHAR(255) COMMENT '카드 번호',
    card_quota          INT COMMENT '할부 개월 수',
    name                VARCHAR(255) COMMENT '상품명',
    amount              INT         NOT NULL COMMENT '결제 금액',
    buyer_name          VARCHAR(100) COMMENT '주문자명',
    buyer_email         VARCHAR(255) COMMENT '주문자 EMAIL',
    buyer_tel           VARCHAR(255) COMMENT '주문자 전화번호',
    `status`            VARCHAR(50) NOT NULL COMMENT '결제 상태',
    started_at          DATETIME COMMENT '요청 시각',
    paid_at             DATETIME COMMENT '결제 시각',
    failed_at           DATETIME COMMENT '실패 시각',
    fail_reason         VARCHAR(500) COMMENT '결제실패 사유',
    receipt_url         VARCHAR(255) COMMENT '매출전표 URL',
    cash_receipt_issued BOOLEAN COMMENT '현금영수증 발급 여부',
    created_at          DATETIME COMMENT '생성 일시',
    updated_at          DATETIME COMMENT '수정 일시',
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) COMMENT '결제' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `payment_cancel_detail`
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '취소내역 ID',
    pg_tid      VARCHAR(255) NOT NULL COMMENT 'PG사 승인취소 번호',
    amount      INT          NOT NULL COMMENT '취소 금액',
    cancelledAt DATETIME     NOT NULL COMMENT '취소 시각',
    reason      VARCHAR(500) NOT NULL COMMENT '취소 사유',
    receipt_url VARCHAR(255) NOT NULL COMMENT '취소 매출전표 URL',
    created_at  DATETIME COMMENT '생성 일시',
    updated_at  DATETIME COMMENT '수정 일시'
) COMMENT '취소내역' ENGINE = InnoDB
                 DEFAULT CHARSET = utf8mb4
                 COLLATE = utf8mb4_bin;

CREATE TABLE `chat`
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅 ID',
    chat_room_id BIGINT       NOT NULL COMMENT '채팅 방 ID',
    sender_type  VARCHAR(50)  NOT NULL COMMENT '작성자 타입',
    user_id      BIGINT       NULL COMMENT '사용자 ID',
    content      VARCHAR(500) NOT NULL COMMENT '채팅 내용',
    viewed_by_seller BOOLEAN      NOT NULL COMMENT '판매자 읽음 여부',
    viewed_by_buyer  BOOLEAN      NOT NULL COMMENT '구매자 읽음 여부',
    created_at   DATETIME     NOT NULL COMMENT '생성 일시',
    updated_at   DATETIME     NOT NULL COMMENT '수정 일시'
) COMMENT '채팅' ENGINE = InnoDB
               DEFAULT CHARSET = utf8mb4
               COLLATE = utf8mb4_bin;

CREATE TABLE `chat_room`
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅 방 ID',
    product_id BIGINT   NOT NULL COMMENT '상품 ID',
    buyer_id   BIGINT   NOT NULL COMMENT '구매자 ID',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    updated_at DATETIME NOT NULL COMMENT '수정 일시'
) COMMENT '채팅방' ENGINE = InnoDB
                DEFAULT CHARSET = utf8mb4
                COLLATE = utf8mb4_bin;


-- 더미 테이블
CREATE TABLE reservation
(
    id                    INT AUTO_INCREMENT PRIMARY KEY COMMENT '예약 ID (자동 증가)',
    accommodation_address VARCHAR(255) COMMENT '숙소 주소',
    accommodation_image   VARCHAR(255) COMMENT '숙소 이미지',
    accommodation_name    VARCHAR(255) COMMENT '숙소 이름',
    area_code             VARCHAR(50) NOT NULL COMMENT '지역 코드',
    check_in_date         DATE COMMENT '체크인 날짜',
    check_in_time         TIME(6) COMMENT '체크인 시간',
    check_out_date        DATE COMMENT '체크아웃 날짜',
    check_out_time        TIME(6) COMMENT '체크아웃 시간',
    yanolja_price         INT COMMENT '야놀자 가격',
    maximum_number        INT         NOT NULL COMMENT '최대 수용 가능 인원',
    origin_price          INT COMMENT '원래 가격',
    reservation_date      DATE COMMENT '예약 날짜',
    reservation_type      VARCHAR(50) NOT NULL COMMENT '예약 유형',
    room_name             VARCHAR(255) COMMENT '방 이름',
    standard_number       INT         NOT NULL COMMENT '표준 수용 가능 인원',
    ya_user_id            MEDIUMTEXT COMMENT '야놀자 사용자 ID',
    reservation_status    VARCHAR(50) NOT NULL COMMENT '예약 상태'
);

CREATE TABLE ya_user
(
    id           INT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID (자동 증가)',
    email        VARCHAR(255) COMMENT '이메일',
    name         VARCHAR(255) COMMENT '이름',
    password     VARCHAR(255) COMMENT '비밀번호',
    phone_number VARCHAR(20) COMMENT '전화번호'
);
