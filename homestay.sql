create database homestay;
use homestay;
-- 1. NHÓM PHÂN QUYỀN 
CREATE TABLE permission (
    name VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE role (
    name VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_name VARCHAR(255),
    permissions_name VARCHAR(255),
    PRIMARY KEY (role_name, permissions_name),
    FOREIGN KEY (role_name) REFERENCES role(name),
    FOREIGN KEY (permissions_name) REFERENCES permission(name)
);

CREATE TABLE user (
    id VARCHAR(36) PRIMARY KEY, -- Sử dụng UUID string
    username VARCHAR(255) UNIQUE COLLATE utf8mb4_unicode_ci,
    password VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dob DATE,
    phone VARCHAR(20),
    address VARCHAR(255),
    customer_status VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id VARCHAR(36),
    roles_name VARCHAR(255),
    PRIMARY KEY (user_id, roles_name),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (roles_name) REFERENCES role(name)
);

-- 2. NHÓM ĐỊA LÝ 
CREATE TABLE location (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50), -- CITY, DISTRICT, WARD
    slug VARCHAR(255) UNIQUE,
    parent_id BIGINT NULL,
    FOREIGN KEY (parent_id) REFERENCES location(id)
);

-- 3. NHÓM HOMESTAY & TIỆN NGHI
CREATE TABLE amenity (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    icon_url VARCHAR(255)
);

CREATE TABLE homestay (
    id VARCHAR(36) PRIMARY KEY, -- UUID đồng bộ với User
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    checkin_time VARCHAR(20), -- Ví dụ: "14:00"
    checkout_time VARCHAR(20),
    status VARCHAR(50), -- PENDING, ACTIVE, REJECTED
    host_id VARCHAR(36) NOT NULL,
    location_id BIGINT,
    FOREIGN KEY (host_id) REFERENCES user(id),
    FOREIGN KEY (location_id) REFERENCES location(id)
);

CREATE TABLE homestay_amenities (
    homestay_id VARCHAR(36),
    amenities_id INT,
    PRIMARY KEY (homestay_id, amenities_id),
    FOREIGN KEY (homestay_id) REFERENCES homestay(id),
    FOREIGN KEY (amenities_id) REFERENCES amenity(id)
);

-- 4.  PHÒNG & GIÁ 
CREATE TABLE room_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    homestay_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL, -- Ví dụ: "Phòng Deluxe", "Nguyên căn"
    base_price DOUBLE NOT NULL,
    max_adults INT,
    max_children INT,
    total_inventory INT, -- Số lượng căn/phòng thực tế có
    FOREIGN KEY (homestay_id) REFERENCES homestay(id)
);

-- Bảng quản lý lịch và giá theo ngày 
CREATE TABLE room_availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_type_id BIGINT NOT NULL,
    date DATE NOT NULL,
    price_override DOUBLE NULL, -- Giá đặc biệt cho ngày này
    booked_count INT DEFAULT 0,
    is_blocked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (room_type_id) REFERENCES room_type(id),
    UNIQUE (room_type_id, date)
);

-- 5.  ĐẶT PHÒNG & ĐÁNH GIÁ
CREATE TABLE booking (
    id VARCHAR(36) PRIMARY KEY, -- UUID
    customer_id VARCHAR(36) NOT NULL,
    room_type_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL,
    checkout_date DATE NOT NULL,
    num_adults INT,
    num_children INT,
    total_price DOUBLE,
    status VARCHAR(50), -- PENDING, PAID, COMPLETED, CANCELLED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES user(id),
    FOREIGN KEY (room_type_id) REFERENCES room_type(id)
);

CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id VARCHAR(36) UNIQUE,
    user_id VARCHAR(36) NOT NULL,
    homestay_id VARCHAR(36) NOT NULL,
    rating INT,
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES booking(id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (homestay_id) REFERENCES homestay(id)
);

-- 6. HÌNH ẢNH 
CREATE TABLE image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    homestay_id VARCHAR(36) NULL,
    room_type_id BIGINT NULL,
    FOREIGN KEY (homestay_id) REFERENCES homestay(id),
    FOREIGN KEY (room_type_id) REFERENCES room_type(id)
);