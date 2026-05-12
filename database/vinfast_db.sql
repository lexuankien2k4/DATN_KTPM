-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: Vinfast_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `banks`
--

DROP TABLE IF EXISTS `banks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `logo_url` varchar(512) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banks`
--

LOCK TABLES `banks` WRITE;
/*!40000 ALTER TABLE `banks` DISABLE KEYS */;
INSERT INTO `banks` VALUES (1,'Techcombank','/images/banks/tcb_logo.png',1),(2,'VPBank','/images/banks/vpb_logo.png',1),(3,'Vietcombank','/images/banks/vcb_logo.png',1),(4,'BIDV','/images/banks/bidv_logo.png',1),(5,'ACB','/images/banks/acb.png',1),(6,'MB Bank','/images/banks/mb.png',1),(7,'Sacombank','/images/banks/scb.png',1),(8,'TPBank','/images/banks/tpb.png',1),(9,'SHB','/images/banks/shb.png',1);
/*!40000 ALTER TABLE `banks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_categories`
--

DROP TABLE IF EXISTS `car_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_categories`
--

LOCK TABLES `car_categories` WRITE;
/*!40000 ALTER TABLE `car_categories` DISABLE KEYS */;
INSERT INTO `car_categories` VALUES (3,'Dòng xe dịch vụ'),(2,'Động cơ xăng '),(1,'Động cơ điện');
/*!40000 ALTER TABLE `car_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_images`
--

DROP TABLE IF EXISTS `car_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `variant_id` bigint NOT NULL COMMENT 'Khóa ngoại, trỏ đến phiên bản xe trong car_variants',
  `image_url` varchar(512) NOT NULL COMMENT 'Đường dẫn đến file ảnh',
  PRIMARY KEY (`id`),
  KEY `variant_id` (`variant_id`),
  CONSTRAINT `car_images_ibfk_1` FOREIGN KEY (`variant_id`) REFERENCES `car_variants` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_images`
--

LOCK TABLES `car_images` WRITE;
/*!40000 ALTER TABLE `car_images` DISABLE KEYS */;
INSERT INTO `car_images` VALUES (1,1,'images/limogreen/limo-img-01.webp'),(2,1,'images/limogreen/limo-img-04.webp'),(3,1,'images/limogreen/limo-img-07.webp'),(4,1,'images/limogreen/limo-img-10.webp'),(5,2,'images/miniogreen/minio-img-black.webp'),(6,2,'images/miniogreen/minio-img-red.webp'),(7,2,'images/miniogreen/minio-img-while.webp'),(8,2,'images/miniogreen/minio-img-yellow.webp'),(9,4,'images/neriogreen/nerio-img-07.webp'),(10,4,'images/neriogreen/nerio-img-10.webp'),(11,4,'images/neriogreen/nerio-img-black.webp'),(12,4,'images/neriogreen/nerio-img-yellow.webp'),(13,3,'images/heriogreen/herio-img-black.webp'),(14,3,'images/heriogreen/herio-img-red.webp'),(15,3,'images/heriogreen/herio-img-sillev.webp'),(16,3,'images/heriogreen/herio-img-yellow.webp'),(17,5,'images/ecvan/ecvan-jade.webp'),(18,5,'images/ecvan/ecvan-red.webp'),(19,5,'images/ecvan/ecvan-white.webp'),(20,5,'images/ecvan/ecvan-yellow.webp'),(21,6,'images/EBUS/anh-1-ebus.webp'),(22,7,'images/vf3/VF3_Interior_Hero.webp'),(23,7,'images/vf3/vf3-1.webp'),(24,7,'images/vf3/vf3section-4.webp'),(25,7,'images/vf3/vf3section-5.webp'),(26,8,'images/vf5/bg-thong-so.webp'),(27,9,'images/vf6/exterior-vf6-2.webp'),(28,10,'images/vf6/exterior-vf6-2.webp'),(29,11,'images/vf7/product-CE1V.webp'),(30,11,'images/vf7/product-CE1M.webp'),(31,11,'images/vf7/product-CE1W.webp'),(32,11,'images/vf7/product-CE11.webp'),(33,12,'images/vf7/product-CE18.webp'),(34,13,'images/vf8/product-1V18.webp'),(35,14,'images/vf8/product-171V.webp'),(36,14,'images/vf8/product-2927.webp'),(37,14,'images/vf8/product-2911.webp'),(38,15,'images/vf9/CE1M.webp'),(39,15,'images/vf9/CE1V.webp'),(40,16,'images/vf9/CE18.webp'),(41,16,'images/vf9/CE22.webp'),(52,1,'https://example.com/vf8-eco.png'),(53,3,'https://example.com/vf9-plus.png'),(57,27,'/images/66331b11-aa7a-4674-bf6e-1f966d0501a3.webp'),(58,26,'/images/6c124643-8e10-4c4f-99ff-ace5e043fc85.webp'),(59,25,'/images/7ee8b9cd-74ed-469a-9abf-915ad6116f42.webp'),(60,22,'/images/e19c49cc-fbcd-420c-84e7-af1690276213.webp'),(61,22,'/images/5dff75df-65a6-406e-8c8f-6e14f42d6207.webp'),(62,20,'/images/854d4ca9-442c-4569-85fc-bbd4c5058d38.webp'),(63,19,'/images/4e1f6436-eaa3-41c6-800c-2fe62cfc302c.webp'),(64,18,'/images/8f9f3289-91f5-47ab-9b4f-39d32bc70db9.webp'),(65,17,'/images/5079edea-d699-4b99-9a88-9489217f53fd.webp');
/*!40000 ALTER TABLE `car_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_models`
--

DROP TABLE IF EXISTS `car_models`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_models` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` int DEFAULT NULL COMMENT 'Khóa ngoại đến bảng car_categories',
  `body_type` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `car_models_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `car_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_models`
--

LOCK TABLES `car_models` WRITE;
/*!40000 ALTER TABLE `car_models` DISABLE KEYS */;
INSERT INTO `car_models` VALUES (1,'VinFast VF 3',1,'Mini SUV',1),(2,'VinFast VF 5',1,'A-SUV',1),(3,'VinFast VF 6',1,'B-SUV',1),(4,'VinFast VF 7',1,'C-SUV',1),(5,'VinFast VF 8',1,'D-SUV',1),(6,'VinFast VF 9',1,'E-SUV',1),(7,'VinFast VF e34',1,'C-SUV',1),(8,'VinFast Fadil',2,'Hatchback',1),(9,'VinFast Lux A2.0',2,'Sedan',1),(10,'VinFast Lux SA2.0',2,'D-SUV',1),(11,'VinFast President',2,'E-SUV',1),(12,'Minio Green',3,'Mini Van',1),(13,'Herio Green',3,'Compact Van',1),(14,'Nerio Green',3,'Crossover Van',1),(15,'Limo Green',3,'SUV',1),(16,'EC Van',3,'EC Van',1),(17,'EBus',3,'Bus',1),(18,'VinFast VF Test',1,'Test SUV',1),(19,'VF8',1,'SUV',1),(20,'VF9',1,'SUV',1),(21,'VF6',1,'SUV',1);
/*!40000 ALTER TABLE `car_models` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_stock`
--

DROP TABLE IF EXISTS `car_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `engine_number` varchar(255) DEFAULT NULL,
  `import_date` date DEFAULT NULL,
  `manufacturing_year` int DEFAULT NULL,
  `status` enum('CANCELLED_RETURN','DEFECTIVE','DELIVERED','IN_STOCK','IN_TRANSIT','PDI_PENDING','RESERVED') DEFAULT NULL,
  `storage_location` varchar(255) DEFAULT NULL,
  `vin_number` varchar(255) NOT NULL,
  `color_id` bigint DEFAULT NULL,
  `variant_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjq4hlns3wid8xvuwnenuqqn48` (`vin_number`),
  UNIQUE KEY `UKhdrfoinj36wkebm0s3wtle9pg` (`engine_number`),
  KEY `FKar63fwp017nmvs2phwptsuhrf` (`color_id`),
  KEY `FKi8b1gn08qiv4q6gi4we78reb4` (`variant_id`),
  CONSTRAINT `FKar63fwp017nmvs2phwptsuhrf` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`),
  CONSTRAINT `FKi8b1gn08qiv4q6gi4we78reb4` FOREIGN KEY (`variant_id`) REFERENCES `car_variants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_stock`
--

LOCK TABLES `car_stock` WRITE;
/*!40000 ALTER TABLE `car_stock` DISABLE KEYS */;
INSERT INTO `car_stock` VALUES (1,'ENG99901','2024-01-10',2024,'IN_STOCK','Khu A','VINVF820240001',1,1),(2,'ENG99902','2024-01-15',2024,'IN_STOCK','Khu A','VINVF820240002',3,1),(3,'ENG99903','2026-04-27',NULL,'IN_STOCK',NULL,'VIN1234566',6,22),(4,'ENG99904','2026-04-27',NULL,'IN_STOCK',NULL,'VIN9999',8,25),(15,'ENG10015','2024-01-05',2024,'IN_STOCK','Khu A','VIN10015',3,12),(16,'ENG10016','2024-01-10',2023,'IN_STOCK','Khu B','VIN10016',7,5),(17,'ENG10017','2024-02-01',2024,'IN_STOCK','Khu C','VIN10017',1,20),(18,'ENG10018','2024-02-15',2024,'IN_STOCK','Khu A','VIN10018',9,8),(19,'ENG10019','2024-03-01',2025,'IN_STOCK','Khu B','VIN10019',2,25),(20,'ENG10020','2024-03-10',2025,'IN_STOCK','Khu D','VIN10020',5,3),(21,'ENG10021','2024-04-05',2024,'IN_STOCK','Khu A','VIN10021',10,14),(22,'ENG10022','2024-04-18',2023,'IN_STOCK','Khu C','VIN10022',6,9),(23,'ENG10023','2024-05-01',2024,'IN_STOCK','Khu B','VIN10023',4,27),(24,'ENG10024','2024-05-12',2025,'IN_STOCK','Khu D','VIN10024',8,6),(25,'ENG10025','2024-06-01',2025,'IN_STOCK','Khu A','VIN10025',2,11),(26,'ENG10026','2024-06-20',2024,'IN_STOCK','Khu B','VIN10026',7,18),(27,'ENG10027','2024-07-05',2023,'RESERVED','Khu C','VIN10027',1,7),(29,'ENG10029','2024-08-01',2025,'IN_STOCK','Khu A','VIN10029',3,2),(30,'ENG10030','2024-08-10',2025,'IN_STOCK','Khu B','VIN10030',6,26),(31,'ENG10031','2024-09-01',2024,'IN_STOCK','Khu C','VIN10031',5,16),(32,'ENG10032','2024-09-15',2023,'IN_STOCK','Khu D','VIN10032',8,4),(34,'ENG10034','2024-10-20',2025,'IN_STOCK','Khu B','VIN10034',4,1);
/*!40000 ALTER TABLE `car_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `car_variants`
--

DROP TABLE IF EXISTS `car_variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car_variants` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_id` bigint NOT NULL COMMENT 'Khóa ngoại đến car_models(id)',
  `name` varchar(255) NOT NULL,
  `price` decimal(38,2) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1' COMMENT 'True nếu phiên bản này hiển thị',
  `length` int DEFAULT NULL COMMENT 'Chiều dài xe (mm)',
  `width` int DEFAULT NULL COMMENT 'Chiều rộng xe (mm)',
  `height` int DEFAULT NULL COMMENT 'Chiều cao xe (mm)',
  `wheelbase` int DEFAULT NULL COMMENT 'Chiều dài cơ sở (mm)',
  `ground_clearance` int DEFAULT NULL COMMENT 'Khoảng sáng gầm xe (mm)',
  `max_power` int DEFAULT NULL COMMENT 'Công suất tối đa (kW)',
  `max_torque` int DEFAULT NULL COMMENT 'Mô-men xoắn cực đại (Nm)',
  `drivetrain` varchar(255) DEFAULT NULL,
  `drive_modes` varchar(255) DEFAULT NULL,
  `battery` decimal(38,2) DEFAULT NULL,
  `range_nedc` int DEFAULT NULL COMMENT 'Quãng đường di chuyển theo chuẩn NEDC (km)',
  `charge_time_detail` varchar(255) DEFAULT NULL,
  `suspension_system` varchar(255) DEFAULT NULL,
  `brake_system` varchar(255) DEFAULT NULL,
  `wheel_size` int DEFAULT NULL COMMENT 'Kích thước la-zăng (inch)',
  `front_light_type` varchar(255) DEFAULT NULL,
  `trunk_access` varchar(255) DEFAULT NULL,
  `air_conditioning_system` varchar(255) DEFAULT NULL,
  `entertainment_screen` decimal(38,2) DEFAULT NULL,
  `speaker_system` varchar(255) DEFAULT NULL,
  `driver_seat_adjustment` varchar(255) DEFAULT NULL,
  `discounted_price` bigint DEFAULT NULL,
  `seat_capacity` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `model_id` (`model_id`),
  CONSTRAINT `car_variants_ibfk_1` FOREIGN KEY (`model_id`) REFERENCES `car_models` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car_variants`
--

LOCK TABLES `car_variants` WRITE;
/*!40000 ALTER TABLE `car_variants` DISABLE KEYS */;
INSERT INTO `car_variants` VALUES (1,15,'Limo Green',749000000.00,1,4740,1872,1723,2840,170,150,280,'FWD/Cầu trước','Eco/Normal',60.13,450,'30 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa',18,'LED','Chỉnh cơ','Tự động 1 vùng',10.10,'4 loa','Chỉnh cơ 6 hướng',50000000,7),(2,12,'Minio Green',549000000.00,1,3090,1496,1659,2065,165,20,65,'FWD/Cầu trước','Eco/Normal',14.70,170,'Khoảng 3 giờ (Sạc thường)','MacPherson/MacPherson','Đĩa/Tang trống',13,'LED','Chỉnh cơ','Máy lạnh, chỉnh cơ',7.00,'2 loa','Chỉnh cơ 4 hướng',50000000,4),(3,13,'Herio Green',849000000.00,1,3967,1723,1579,2514,160,100,135,'FWD/Cầu trước','Eco/Sport',37.23,326,'Khoảng 5 giờ (Sạc thường)','MacPherson/Dầm xoắn','Đĩa/Đĩa',16,'Bi-halogen, projector','Chỉnh cơ','Chỉnh cơ',8.00,'2 loa','Chỉnh cơ 6 hướng',50000000,5),(4,14,'Nerio Green',949000000.00,1,4300,1768,1615,2840,170,150,280,'FWD/Cầu trước','Eco/Normal',60.13,450,'30 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa',18,'LED','Chỉnh cơ','Tự động 1 vùng',10.10,'4 loa','Chỉnh cơ 6 hướng',50000000,5),(5,16,'EC Van',949000000.00,1,4238,1820,1950,2730,170,100,220,'FWD/Cầu trước','Eco/Normal',59.60,350,'Khoảng 6 giờ (Sạc thường)','MacPherson/Nhíp lá','Đĩa thông gió/Tang trống',16,'Halogen','Cửa lùa','Chỉnh cơ',7.00,'2 loa','Chỉnh nỉ',50000000,2),(6,17,'EBus',1949000000.00,1,12000,2500,3200,6800,180,140,450,'RWD/Cầu sau','Normal',228.00,260,'Khoảng 2 giờ (Sạc nhanh)','Hệ thống treo khí nén','Phanh khí nén',22,'LED','Cửa tự động','Điều hòa công suất lớn',10.10,'Hệ thống loa thông báo','Ghế lái đệm khí',50000000,30),(7,1,'Tiêu chuẩn',315000000.00,1,3190,1679,1622,2075,191,32,110,'RWD/Cầu sau','Eco/Normal',18.64,210,'36 phút (10%-70%)','MacPherson/Trục cứng','Đĩa/Tang trống',16,'Halogen','Chỉnh cơ','Điều hòa 1 vùng',10.00,'2 loa','Chỉnh cơ 4 hướng',50000000,4),(8,2,'Plus',458000000.00,1,3967,1723,1579,2513,182,100,135,'FWD/Cầu trước','Eco/Normal/Sport',37.23,326,'30 phút (10%-70%)','MacPherson/Thanh xoắn','Đĩa thông gió/Đĩa',17,'Halogen','Chỉnh cơ','Tự động 1 vùng',8.00,'4 loa','Chỉnh cơ 6 hướng',50000000,4),(9,3,'Eco',675000000.00,1,4238,1820,1594,2730,170,100,135,'FWD/Cầu trước','Eco/Normal/Sport',59.60,399,'24 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa/Đĩa',17,'LED','Chỉnh cơ','Tự động 1 vùng',12.90,'6 loa','Chỉnh cơ 8 hướng',50000000,4),(10,3,'Plus',765000000.00,1,4238,1820,1594,2730,170,150,310,'FWD/Cầu trước','Eco/Normal/Sport',59.60,381,'24 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa/Đĩa',19,'LED','Chỉnh điện','Tự động 2 vùng',12.90,'8 loa','Chỉnh điện 8 hướng',50000000,4),(11,4,'S',850000000.00,1,4545,1890,1635,2840,190,130,250,'FWD/Cầu trước','Eco/Normal/Sport',59.60,375,'24 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa',19,'LED','Chỉnh cơ','Tự động 2 vùng',12.90,'8 loa','Chỉnh cơ 8 hướng',50000000,5),(12,4,'TC (Plus)',999000000.00,1,4545,1890,1635,2840,190,260,500,'AWD/2 cầu toàn thời gian','Eco/Normal/Sport',75.30,431,'26 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa',20,'LED','Chỉnh điện','Tự động 2 vùng',12.90,'8 loa','Chỉnh điện 8 hướng',50000000,5),(13,5,'Eco',1090000000.00,1,4750,1934,1667,2950,175,260,500,'AWD/2 cầu toàn thời gian','Eco/Normal/Sport',87.70,471,'31 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa thông gió',19,'LED','Chỉnh điện','Tự động 2 vùng',15.60,'8 loa','Chỉnh điện 8 hướng',50000000,5),(14,5,'Plus',1270000000.00,1,4750,1934,1667,2950,175,300,620,'AWD/2 cầu toàn thời gian','Eco/Normal/Sport',87.70,457,'31 phút (10%-70%)','MacPherson/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix','Chỉnh điện','Tự động 2 vùng',15.60,'10 loa','Chỉnh điện 12 hướng',50000000,5),(15,6,'VF9 Eco',1491000000.00,1,5118,2254,1696,3150,197,300,620,'AWD/2 cầu toàn thời gian','Eco/Normal/Sport',92.00,423,'26 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix','Chỉnh điện, đá cốp','Tự động 3 vùng',15.60,'13 loa','Chỉnh điện 8 hướng',50000000,5),(16,6,'VF9 Plus',1676000000.00,1,5118,2254,1696,3150,197,300,620,'AWD/2 cầu toàn thời gian','Eco/Normal/Sport',92.00,423,'26 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',21,'LED Matrix','Chỉnh điện, đá cốp','Tự động 3 vùng',15.60,'13 loa','Chỉnh điện 12 hướng, massage',50000000,5),(17,8,'Fadil Tiêu chuẩn',425000000.00,1,3676,1632,1530,2385,150,72,135,'FWD/Cầu trước','Normal',59.60,375,'24 phút (10%-70%)','MacPherson/Thanh xoắn','Đĩa/Tang trống',15,'Halogen','Chỉnh cơ','Chỉnh cơ',7.00,'6 loa','Chỉnh cơ 6 hướng',50000000,5),(18,8,'Fadil Plus',459000000.00,1,3676,1632,1530,2385,150,72,135,'FWD/Cầu trước','Normal',59.60,375,'24 phút (10%-70%)','MacPherson/Thanh xoắn','Đĩa/Tang trống',15,'Halogen','Chỉnh cơ','Tự động, có cảm biến độ ẩm',7.00,'6 loa','Chỉnh cơ 6 hướng',50000000,5),(19,9,'Lux 2.0 Tiêu chuẩn',1115000000.00,1,4973,1900,1464,2968,150,130,300,'RWD/Cầu sau','Normal',59.60,375,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa',18,'LED','Chỉnh điện','Tự động 2 vùng',10.40,'8 loa','Chỉnh điện 4 hướng',50000000,6),(20,9,'Lux 2.0 Premium',1358000000.00,1,4973,1900,1500,2968,150,170,350,'RWD/Cầu sau','Normal',59.60,375,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa',19,'LED Matrix','Chỉnh điện, đá cốp','Tự động 2 vùng, lọc không khí ion',10.40,'13 loa có âm-ly','Chỉnh điện 12 hướng',50000000,6),(22,10,'Lux SA Cao cấp',1835000000.00,1,4940,1960,1773,2933,192,170,350,'AWD/2 cầu toàn thời gian','Normal',59.60,375,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix','Chỉnh điện, đá cốp','Tự động 2 vùng, lọc không khí ion',10.40,'13 loa có âm-ly','Chỉnh điện 12 hướng',50000000,4),(25,1,'VF8 Eco',1090000000.00,1,4940,1960,1773,2933,192,170,350,'AWD/2 cầu toàn thời gian','Normal',82.00,420,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix',NULL,'Tự động 2 vùng, lọc không khí ion',10.40,'13 loa có âm-ly','Chỉnh điện 12 hướng',NULL,5),(26,1,'VF8 Plus',1270000000.00,1,4940,1960,1773,2933,192,170,350,'AWD/2 cầu toàn thời gian','Normal',82.00,400,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix',NULL,'Tự động 2 vùng, lọc không khí ion',10.40,'13 loa có âm-ly','Chỉnh điện 12 hướng',NULL,5),(27,2,'VF9 Plus 6-seat',2000000000.00,1,4940,1960,1773,2933,192,170,350,'AWD/2 cầu toàn thời gian','Normal',92.00,594,'24 phút (10%-70%)','Tay đòn kép/Đa liên kết','Đĩa thông gió/Đĩa thông gió',20,'LED Matrix',NULL,'Tự động 2 vùng, lọc không khí ion',10.40,'13 loa có âm-ly','Chỉnh điện 12 hướng',NULL,6);
/*!40000 ALTER TABLE `car_variants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `colors`
--

DROP TABLE IF EXISTS `colors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `color_code` varchar(255) NOT NULL,
  `color_name` varchar(255) NOT NULL,
  `hex_code` varchar(255) DEFAULT NULL,
  `price_extra` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7y3kha7h6vhnqtpdkcm5j8fgr` (`color_code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `colors`
--

LOCK TABLES `colors` WRITE;
/*!40000 ALTER TABLE `colors` DISABLE KEYS */;
INSERT INTO `colors` VALUES (1,'VFW','VinFast White','#FFFFFF',0.00),(2,'VFB','VinFast Blue','#0033A0',5000000.00),(3,'VFR','VinFast Red','#C8102E',5000000.00),(4,'VFBK','VinFast Black','#000000',3000000.00),(5,'VFS','VinFast Silver','#C0C0C0',3000000.00),(6,'VFG','VinFast Green','#008000',7000000.00),(7,'VFY','VinFast Yellow','#FFD700',8000000.00),(8,'VFO','VinFast Orange','#FF6600',8000000.00),(9,'VFP','VinFast Purple','#6A0DAD',9000000.00),(10,'VFGD','VinFast Gold','#D4AF37',10000000.00);
/*!40000 ALTER TABLE `colors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_requests`
--

DROP TABLE IF EXISTS `consultation_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `province` varchar(255) DEFAULT NULL,
  `variant_id` bigint NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `scheduled_at` datetime(6) DEFAULT NULL,
  `showroom_id` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `note` text,
  `staff_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `variant_id` (`variant_id`),
  KEY `FK_consultation_requests_showroom` (`showroom_id`),
  KEY `FK7if08ohg45u0n8xspgs7i2hp6` (`staff_id`),
  CONSTRAINT `consultation_requests_ibfk_1` FOREIGN KEY (`variant_id`) REFERENCES `car_variants` (`id`),
  CONSTRAINT `FK7if08ohg45u0n8xspgs7i2hp6` FOREIGN KEY (`staff_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_consultation_requests_showroom` FOREIGN KEY (`showroom_id`) REFERENCES `showrooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_requests`
--

LOCK TABLES `consultation_requests` WRITE;
/*!40000 ALTER TABLE `consultation_requests` DISABLE KEYS */;
INSERT INTO `consultation_requests` VALUES (1,'Nguyễn Văn A','0912345678','Hà Nội',1,'Đã hẹn lịch',NULL,NULL,NULL,NULL,NULL,'6cd17fa9-9024-4304-ae12-309646143aef'),(2,'Le Xuan Kien','0963347562','Hà Nội',3,'Đã hẹn lịch','2025-12-06 01:22:00.000000',2,'2025-11-25 09:19:41.537100','Lexuankien11082004@gmail.com','toi muon lai thu xe',NULL),(3,'kiên','0963563695','Hà Nội',1,'Đã hẹn lịch','2025-12-18 22:17:00.000000',1,'2025-12-12 19:14:35.569140','lexuankien@gmail.com','',NULL),(4,'fafaf','0224252527','Hà Nội',3,'Đã hẹn lịch','2025-12-24 01:09:00.000000',2,'2025-12-21 21:06:14.697281','le@gmail.com','vvss',NULL),(5,'le','023424324','Hà Nội',9,'Đã hẹn lịch','2026-04-11 15:14:00.000000',1,'2026-04-11 22:11:52.150966','lexuankien11082004@gmail.com','',NULL),(6,'Nguyễn Văn A','0905123456','Hà Nội',1,'PENDING','2026-05-01 09:00:00.000000',NULL,'2026-04-27 09:33:05.000000','vana@gmail.com',NULL,NULL),(7,'Nguyen Van A','0905123456','Ha Noi',1,'PENDING',NULL,1,'2026-04-27 09:36:22.000000','vana@gmail.com',NULL,NULL);
/*!40000 ALTER TABLE `consultation_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contracts`
--

DROP TABLE IF EXISTS `contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contracts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contract_number` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `final_price` decimal(38,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `deposit_id` bigint NOT NULL,
  `battery_cost` decimal(38,2) DEFAULT NULL,
  `battery_service_type` varchar(255) DEFAULT NULL,
  `expected_delivery_date` datetime(6) DEFAULT NULL,
  `interest_rate` decimal(38,2) DEFAULT NULL,
  `is_signed` bit(1) DEFAULT NULL,
  `loan_term_months` int DEFAULT NULL,
  `monthly_installment` decimal(38,2) DEFAULT NULL,
  `prepayment_amount` decimal(38,2) DEFAULT NULL,
  `signing_appointment_date` datetime(6) DEFAULT NULL,
  `vin_number` varchar(255) DEFAULT NULL,
  `vnp_transaction_no` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbx9jyu2cccdntb3ehrf0ojpfd` (`contract_number`),
  UNIQUE KEY `UKsr7ivdgwc7lcxxm4vrc3uxokh` (`deposit_id`),
  CONSTRAINT `FK9rqgp33jnrbwimq114epr9q6u` FOREIGN KEY (`deposit_id`) REFERENCES `deposits` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contracts`
--

LOCK TABLES `contracts` WRITE;
/*!40000 ALTER TABLE `contracts` DISABLE KEYS */;
INSERT INTO `contracts` VALUES (1,'HD-554811','2026-03-27 13:22:52.231402','6cd17fa9-9024-4304-ae12-309646143aef',600000000.00,'DELIVERED','2026-04-27 16:28:07.952131',3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'HD-1777282009248-21','2026-04-27 16:26:49.256164','SYSTEM_VNPAY',315000000.00,'BANK_APPROVING','2026-05-04 13:04:31.613696',21,NULL,NULL,NULL,NULL,_binary '\0',NULL,NULL,94500000.00,NULL,NULL,'15515149'),(3,'HD-2026-001','2026-04-27 09:33:05.000000','admin_staff',1100000000.00,'DELIVERED',NULL,1,0.00,'BATTERY_SUBSCRIPTION','2026-06-01 10:00:00.000000',8.50,_binary '\0',60,15000000.00,300000000.00,NULL,'VINVF820240002',NULL),(6,'HD-1777345417305-27','2026-04-28 10:03:37.311906','SYSTEM_VNPAY',315000000.00,'BANK_APPROVING','2026-05-04 14:35:43.348682',27,NULL,NULL,'2026-05-21 14:35:00.000000',NULL,_binary '\0',NULL,NULL,63000000.00,'2026-05-14 14:35:00.000000','VIN10027','15515764');
/*!40000 ALTER TABLE `contracts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deposits`
--

DROP TABLE IF EXISTS `deposits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposits` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `cccd` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `note` text,
  `payment_method` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `selected_color` varchar(255) DEFAULT NULL,
  `staff_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `showroom_id` int DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  `variant_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt0uvg1k2c7btuue9fx9lgbff9` (`showroom_id`),
  KEY `FKbrcu4hcxeg2mk50w470erud1x` (`user_id`),
  KEY `FKjcahgmrh5u8ys5lj3iu9t6n44` (`variant_id`),
  CONSTRAINT `FKbrcu4hcxeg2mk50w470erud1x` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKjcahgmrh5u8ys5lj3iu9t6n44` FOREIGN KEY (`variant_id`) REFERENCES `car_variants` (`id`),
  CONSTRAINT `FKt0uvg1k2c7btuue9fx9lgbff9` FOREIGN KEY (`showroom_id`) REFERENCES `showrooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposits`
--

LOCK TABLES `deposits` WRITE;
/*!40000 ALTER TABLE `deposits` DISABLE KEYS */;
INSERT INTO `deposits` VALUES (1,15000000.00,'034204004282','2026-03-26 20:40:34.206593','Lê Xuân Kiên','lexuankien11082004@gmail.com','','VNPAY','0963347562','Brahminy White',NULL,'PENDING','2026-03-26 20:40:34.206593',1,'d2946cc4-1e8d-4d31-b1db-c47ff3e8b053',2),(2,50000000.00,'034204004282','2026-03-27 12:09:10.705873','NGUYEN VAN A','lexuankien11082004@gmail.com','','VNPAY','0963347562','Brahminy White',NULL,'PENDING','2026-03-27 12:09:10.705873',1,'63318339-e1cf-4550-8398-b35d0a03a6c5',4),(3,10000000.00,'034204004282','2026-03-27 12:13:56.620412','Kiên Lê Xuân','lexuankien11082004@gmail.com','','VNPAY','0963347562','Brahminy White',NULL,'APPROVED','2026-03-27 13:21:14.038269',1,'54e462af-5706-456a-808a-f80540908075',1),(4,10000000.00,'01312313123','2026-04-03 15:25:24.359507','kien','lexuankien11082004@gmail.com','','VNPAY','0964324244','Brahminy White',NULL,'PENDING','2026-04-03 15:25:24.359507',1,'508fa61e-9d66-4c53-bc66-8ef11cb133dc',7),(5,50000000.00,'234234242343','2026-04-11 22:10:56.937311','le xuan kien','lexuankien11082004@gmail.com','','VNPAY','0942342432','Brahminy White',NULL,'PENDING','2026-04-11 22:10:56.937311',1,'a79fa71a-fd91-41fe-bddb-81fb7ebc48a2',13),(6,50000000.00,NULL,'2026-04-16 16:52:04.154314',NULL,'',NULL,'VNPAY','','VinFast Blue',NULL,'PENDING','2026-04-16 16:52:04.154314',3,'60807160-bc03-46ce-857b-8b365325aefe',7),(7,50000000.00,NULL,'2026-04-16 16:54:41.852874',NULL,'',NULL,'VNPAY','','VinFast Blue',NULL,'PENDING','2026-04-16 16:54:41.852874',5,'ce192f24-a155-407a-8666-8ceb6ebf0fb5',7),(8,50000000.00,NULL,'2026-04-16 16:55:05.975102',NULL,'',NULL,'VNPAY','0964453535','Brahminy White',NULL,'PENDING','2026-04-16 16:55:05.975102',6,'36fca282-22ac-4bbc-82be-36a3ad04a601',7),(9,50000000.00,NULL,'2026-04-16 16:55:36.849278',NULL,'',NULL,'VNPAY','0963347563','Brahminy White',NULL,'PENDING','2026-04-16 16:55:36.849278',7,'b9ff237e-7ff2-43d5-a77a-40e2e4eba9ce',7),(10,50000000.00,'098402607311','2026-04-16 21:49:16.549273','le xuan kien',NULL,NULL,'VNPAY','0984026073',NULL,NULL,'PENDING','2026-04-16 21:49:16.549273',1,'dd496404-d85e-4dfa-ae14-7bdb201e1dca',13),(11,50000000.00,'124214124141','2026-04-17 00:19:12.863694','kien','kienconcac078@gmail.com','Ký HĐ VF622283 - Lãi suất 9.5%','VNPAY','0414141433',NULL,NULL,'REJECTED','2026-04-19 23:03:45.510589',1,'3c0e8845-74a2-469c-b960-3a3bbfdff886',11),(12,50000000.00,'0325242342','2026-04-18 14:19:32.917291','Kiên Lê Xuân','lexuankien11082004@gmail.com','Ký HĐ VF566650 - Lãi suất 8.9%','VNPAY','0242342344',NULL,NULL,'PENDING','2026-04-18 14:19:32.917291',1,'a7e5d781-e7be-4f1b-850c-dc28384da846',15),(13,126000000.00,'034204004282','2026-04-25 17:06:28.291388','Lê Xuân Kiên','kienle@gmail.com','Thanh toán trả trước 40% - Ký HĐ VF584481','VNPAY','0963347562',NULL,NULL,'PAID','2026-04-25 17:08:14.768072',1,'6bcba56d-bd9d-4d19-b98c-7fb469122f61',7),(14,436000000.00,'034204004282','2026-04-25 17:17:05.052022','LÊ XUÂN KIÊN','Lexuankien@gmail.com','Thanh toán trả trước 40% - Ký HĐ VF413621','VNPAY','0963347562',NULL,NULL,'PENDING','2026-04-25 17:17:05.052022',1,'220cf88c-181e-4d26-b9f3-878fe4d64e3d',13),(15,327000000.00,'2313123213213','2026-04-25 17:17:48.990760','Nguyen Van A','kienconcac078@gmail.com','Thanh toán trả trước 30% - Ký HĐ VF674542','VNPAY','0963347562',NULL,NULL,'PENDING','2026-04-25 17:17:48.990760',1,'57768b3f-316b-414e-bcf4-ddd1f9037577',13),(16,327000000.00,'0123131532142','2026-04-25 17:25:13.674647','lekien','kienconcac078@gmail.com','Thanh toán trả trước 30% - Ký HĐ VF271549','VNPAY','9999999999',NULL,NULL,'PENDING','2026-04-25 17:25:13.674647',1,'34951dae-3ca8-4391-a5cb-ea7b7dcb5fc1',13),(17,340000000.00,'111111111111','2026-04-25 17:26:20.199661','lekien','kienconcac078@gmail.com','Thanh toán trả trước 40% - Ký HĐ VF475494','VNPAY','9999999999',NULL,NULL,'PENDING','2026-04-25 17:26:20.199661',1,'2f8611b1-5959-4e82-9528-e62566212d15',11),(18,340000000.00,'111111111111','2026-04-25 17:33:31.328870','lekien','kienconcac078@gmail.com','Thanh toán trả trước 40% - Ký HĐ VF338604','VNPAY','9999999990',NULL,NULL,'PENDING','2026-04-25 17:33:31.328870',1,'86728479-1ce3-46db-b5ec-09452f69bff1',11),(19,255000000.00,'111111111111','2026-04-25 17:36:33.623803','lekien','kienconcac078@gmail.com','Thanh toán trả trước 30% - Ký HĐ VF560805','VNPAY','99999999999',NULL,NULL,'PENDING','2026-04-25 17:36:33.623803',2,'1d4add0d-6590-4df0-9275-dd8246456b8c',11),(20,153000000.00,'66666666666','2026-04-25 17:39:24.016070','lekien','kienconcac078@gmail.com','Thanh toán trả trước 20% - Ký HĐ VF91104','VNPAY','0909999999',NULL,NULL,'APPROVED','2026-04-27 09:57:53.372729',1,'5c670969-1da0-4f84-be7d-143de6776e16',10),(21,94500000.00,'034204004282','2026-04-27 16:26:00.339749','Lê Xuân Kiên','lekien@gmail.com','Thanh toán trả trước 30% - Ký HĐ VF561478','VNPAY','0963347562',NULL,NULL,'PAID','2026-04-27 16:26:49.229182',1,'3ca2abe0-5207-407b-b767-05c8af5fe149',7),(24,327000000.00,'1231414141414','2026-04-27 23:17:35.877067','lekien','kien078@gmail.com','Thanh toán trả trước 30% - Ký HĐ VF698385','VNPAY','0983347561',NULL,NULL,'PENDING','2026-04-27 23:17:35.877067',2,'a478e5a9-7175-44d1-9670-a07aab6b7e2b',25),(25,218000000.00,'31424234324234','2026-04-27 23:20:23.302740','lekien','kien078@gmail.com','Thanh toán trả trước 20% - Ký HĐ VF520953','VNPAY','0983347562',NULL,NULL,'PENDING','2026-04-27 23:20:23.302740',1,'38cabff6-8641-46b4-a9c6-bec484be23e0',25),(26,135000000.00,'43535354354353','2026-04-28 09:51:58.660706','lekien11','kienc@gmail.com','Thanh toán trả trước 20% - Ký HĐ VF895714','VNPAY','09675676567',NULL,NULL,'PENDING','2026-04-28 09:51:58.660706',10,'c24230bd-99ed-41e3-9ce5-cb7e284e6691',9),(27,63000000.00,'123454678984','2026-04-28 10:03:12.353112','lekiens','kien@gmail.com','Thanh toán trả trước 20% - Ký HĐ VF645683','VNPAY','0123456789',NULL,NULL,'PAID','2026-04-28 10:03:37.295695',1,'031823d1-d6b2-4b28-90b1-de7963fd57be',7);
/*!40000 ALTER TABLE `deposits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invalidated_token`
--

DROP TABLE IF EXISTS `invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiry_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invalidated_token`
--

LOCK TABLES `invalidated_token` WRITE;
/*!40000 ALTER TABLE `invalidated_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_policies`
--

DROP TABLE IF EXISTS `loan_policies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_policies` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bank_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `min_down_payment_rate` decimal(38,2) NOT NULL,
  `max_loan_term_years` int NOT NULL COMMENT 'Kỳ hạn vay tối đa (năm)',
  `preferential_rate_percent` decimal(38,2) DEFAULT NULL,
  `preferential_rate_months` int DEFAULT NULL COMMENT 'Thời gian ưu đãi (tháng)',
  `floating_rate_margin` decimal(38,2) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bank_id` (`bank_id`),
  CONSTRAINT `loan_policies_ibfk_1` FOREIGN KEY (`bank_id`) REFERENCES `banks` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan_policies`
--

LOCK TABLES `loan_policies` WRITE;
/*!40000 ALTER TABLE `loan_policies` DISABLE KEYS */;
INSERT INTO `loan_policies` VALUES (1,1,'Ưu đãi Lãi suất Vàng (12T)',0.20,8,8.90,12,3.50,_binary ''),(2,1,'Gói Vay Linh hoạt (36T)',0.30,7,9.50,36,3.00,_binary ''),(3,2,'Lãi suất Cố định 6 tháng',0.25,7,7.50,6,4.00,_binary ''),(4,2,'Vay tối đa 8 năm',0.20,8,9.90,24,3.20,_binary ''),(5,3,'Chính sách VCB Xanh',0.30,10,8.00,12,3.80,_binary ''),(6,3,'Lãi suất Cơ sở',0.20,5,10.50,0,0.00,_binary ''),(7,4,'Hỗ trợ Khách hàng Mới',0.20,7,7.80,6,3.70,_binary ''),(8,4,'Vay trả góp dài hạn',0.25,10,9.20,12,3.30,_binary ''),(9,1,'Gói siêu ưu đãi 6T',0.20,8,7.50,6,3.20,_binary ''),(10,1,'Gói cố định 12T',0.30,7,8.20,12,3.50,_binary ''),(11,2,'Vay nhanh VPBank',0.25,6,9.10,12,3.80,_binary ''),(12,3,'Ưu đãi khách hàng VIP',0.20,10,7.90,24,3.10,_binary ''),(13,4,'Vay linh hoạt BIDV+',0.30,9,8.70,12,3.40,_binary ''),(14,5,'Gói xanh tiết kiệm',0.20,8,7.80,6,3.00,_binary '');
/*!40000 ALTER TABLE `loan_policies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES ('CREATE_DATA','Quyền tạo mới dữ liệu'),('DELETE_DATA','Quyền xóa dữ liệu'),('EXPORT_DATA','Xuất dữ liệu'),('IMPORT_DATA','Nhập dữ liệu'),('MANAGE_USER','Quyền quản lý người dùng'),('UPDATE_DATA','Quyền cập nhật dữ liệu'),('VIEW_DATA','Quyền xem dữ liệu');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('ADMIN','Admin role'),('STAFF','Staff role'),('USER','User role');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_name` varchar(255) NOT NULL,
  `permissions_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_name`,`permissions_name`),
  KEY `FKf5aljih4mxtdgalvr7xvngfn1` (`permissions_name`),
  CONSTRAINT `FKcppvu8fk24eqqn6q4hws7ajux` FOREIGN KEY (`role_name`) REFERENCES `role` (`name`),
  CONSTRAINT `FKf5aljih4mxtdgalvr7xvngfn1` FOREIGN KEY (`permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES ('ADMIN','CREATE_DATA'),('STAFF','CREATE_DATA'),('ADMIN','DELETE_DATA'),('ADMIN','EXPORT_DATA'),('STAFF','EXPORT_DATA'),('ADMIN','IMPORT_DATA'),('ADMIN','MANAGE_USER'),('ADMIN','UPDATE_DATA'),('STAFF','UPDATE_DATA'),('ADMIN','VIEW_DATA'),('STAFF','VIEW_DATA'),('USER','VIEW_DATA');
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `showrooms`
--

DROP TABLE IF EXISTS `showrooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `showrooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `district` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `province` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `showrooms`
--

LOCK TABLES `showrooms` WRITE;
/*!40000 ALTER TABLE `showrooms` DISABLE KEYS */;
INSERT INTO `showrooms` VALUES (1,'Khu đô thị Vinhomes Ocean Park 1, Đa Tốn','Gia Lâm','VinFast Ocean Park','Hà Nội'),(2,'Tầng B1, TTTM Vincom Mega Mall Times City, 458 Minh Khai','Hai Bà Trưng','VinFast Times City','Hà Nội'),(3,'Tầng B2, TTTM Vincom Mega Mall Royal City, 72A Nguyễn Trãi','Thanh Xuân','VinFast Royal City','Hà Nội'),(4,'Tầng B1, TTTM Vincom Center Landmark 81, 720A Điện Biên Phủ','Bình Thạnh','VinFast Landmark 81','Hồ Chí Minh'),(5,'107 Nguyễn Lương Bằng, Tân Phú','Quận 7','VinFast Phú Mỹ Hưng','Hồ Chí Minh'),(6,'Tầng L1, TTTM Vincom Mega Mall Thảo Điền, 159 Xa Lộ Hà Nội','TP. Thủ Đức','VinFast Thảo Điền','Hồ Chí Minh'),(7,'Tầng L1, TTTM Vincom Plaza Ngô Quyền, 910A Ngô Quyền','Sơn Trà','VinFast Ngô Quyền','Đà Nẵng'),(8,'255 Nguyễn Văn Linh, Vĩnh Trung','Thanh Khê','VinFast Nguyễn Văn Linh','Đà Nẵng'),(9,'Khu đô thị Vinhomes Imperia, Thượng Lý','Hồng Bàng','VinFast Imperia Hải Phòng','Hải Phòng'),(10,'Khu đô thị Vinhomes Star City, Phường Đông Hải','TP. Thanh Hóa','VinFast Star City','Thanh Hóa'),(11,'12 Nguyễn Trãi','Quận 1','VinFast Quận 1','Hồ Chí Minh'),(12,'45 Lê Lợi','Hải Châu','VinFast Đà Nẵng Center','Đà Nẵng'),(13,'99 Trần Phú','Nha Trang','VinFast Nha Trang','Khánh Hòa'),(14,'10 Hùng Vương','Ninh Kiều','VinFast Cần Thơ','Cần Thơ'),(15,'88 Lý Thường Kiệt','Huế','VinFast Huế','Thừa Thiên Huế');
/*!40000 ALTER TABLE `showrooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_logs`
--

DROP TABLE IF EXISTS `system_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `detail` text,
  `ip_address` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_logs`
--

LOCK TABLES `system_logs` WRITE;
/*!40000 ALTER TABLE `system_logs` DISABLE KEYS */;
INSERT INTO `system_logs` VALUES (1,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 09:11:31.175096','admin'),(2,'deleteUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 09:13:27.425197','admin'),(3,'createUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','ERROR: User existed','2026-03-28 09:16:13.414651','admin'),(4,'createUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 09:16:19.483306','admin'),(5,'createUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 09:25:42.626403','admin'),(6,'deleteUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 09:31:36.751708','admin'),(7,'createUser','Hành động thực hiện tại lớp: UserService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 20:49:05.758122','anonymousUser'),(8,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:21:47.748752','admin'),(9,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:22:02.482538','admin'),(10,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:22:11.428800','admin'),(11,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:22:19.420289','admin'),(12,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:22:50.464564','admin'),(13,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-03-28 21:22:57.819897','admin'),(14,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-03 15:25:24.397784','anonymousUser'),(15,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-11 22:10:56.984446','anonymousUser'),(16,'createConsultation','Hành động thực hiện tại lớp: ConsultationService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-11 22:11:52.165284','anonymousUser'),(17,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-16 16:52:04.175749','anonymousUser'),(18,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-16 16:54:41.856077','anonymousUser'),(19,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-16 16:55:05.978299','anonymousUser'),(20,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-16 16:55:36.852514','anonymousUser'),(21,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-16 21:49:16.557263','anonymousUser'),(22,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-17 00:19:12.872848','anonymousUser'),(23,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-18 14:19:32.955934','anonymousUser'),(24,'deleteVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-19 22:58:38.826366','admin'),(25,'updateDepositStatus','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-19 23:03:45.534182','admin'),(26,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-24 14:10:58.449235','admin'),(27,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-24 14:11:07.844703','admin'),(28,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-24 14:11:12.708832','admin'),(29,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-24 14:11:19.562416','admin'),(30,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:06:28.313267','anonymousUser'),(31,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:17:05.056672','anonymousUser'),(32,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:17:48.994331','anonymousUser'),(33,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:25:13.693405','anonymousUser'),(34,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:26:20.218345','anonymousUser'),(35,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:33:31.348217','anonymousUser'),(36,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:36:33.642159','anonymousUser'),(37,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-25 17:39:24.020950','anonymousUser'),(38,'updateDepositStatus','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 09:57:53.447280','admin'),(39,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 10:15:34.509245','admin'),(40,'login','User login success','192.168.1.1','SUCCESS','2026-04-27 06:17:16.000000','user1'),(41,'createOrder','Create new order','192.168.1.2','SUCCESS','2026-04-27 06:17:16.000000','user2'),(42,'payment','Payment VNPay','192.168.1.3','SUCCESS','2026-04-27 06:17:16.000000','user3'),(43,'updateProfile','Update user info','192.168.1.4','SUCCESS','2026-04-27 06:17:16.000000','user4'),(44,'login','Wrong password','192.168.1.5','ERROR','2026-04-27 06:17:16.000000','user5'),(45,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 16:26:00.362638','anonymousUser'),(46,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 16:27:42.125892','admin'),(47,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 16:28:07.944683','admin'),(48,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 23:17:35.923486','anonymousUser'),(49,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-27 23:20:23.306918','anonymousUser'),(50,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-28 09:47:59.542196','admin'),(51,'updateConsultationStatus','Hành động thực hiện tại lớp: ConsultationService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-28 09:48:39.540259','admin'),(52,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-28 09:51:58.668737','anonymousUser'),(53,'createPayment','Hành động thực hiện tại lớp: PaymentService','0:0:0:0:0:0:0:1','SUCCESS','2026-04-28 10:03:12.372600','anonymousUser'),(54,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-04 13:03:14.235117','admin'),(55,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-04 13:04:31.606404','admin'),(56,'updateStatus','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-04 14:13:19.849715','admin'),(57,'updateContractAdmin','Hành động thực hiện tại lớp: ContractService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-04 14:35:43.296239','admin'),(58,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:06:29.461886','admin'),(59,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:06:42.603668','admin'),(60,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:06:54.607791','admin'),(61,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:07:09.122718','admin'),(62,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:07:21.816658','admin'),(63,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:07:36.225028','admin'),(64,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:07:50.127576','admin'),(65,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:08:11.097806','admin'),(66,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:08:37.888022','admin'),(67,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:01.520619','admin'),(68,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:14.197037','admin'),(69,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:25.691867','admin'),(70,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:37.730510','admin'),(71,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:49.096903','admin'),(72,'updateVariant','Hành động thực hiện tại lớp: CarVariantService','0:0:0:0:0:0:0:1','SUCCESS','2026-05-06 09:09:58.548801','admin');
/*!40000 ALTER TABLE `system_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `dob` date DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `customer_status` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('031823d1-d6b2-4b28-90b1-de7963fd57be',NULL,'','lekiens','$2a$10$VkT6mH6NqvgMnq0YsmmrNu6.G72BL8T4A/kh4BVnWfhCzWQXCHmiC','0123456789_1777345392257',NULL,'NEW','0123456789'),('1d4add0d-6590-4df0-9275-dd8246456b8c',NULL,'','lekien','$2a$10$M1FCgIZ7iVzgGO4bhfK8le6iwSmhl9rOLESazOmICy3KoiSS9LONW','99999999999_1777113393521',NULL,'NEW','99999999999'),('220cf88c-181e-4d26-b9f3-878fe4d64e3d',NULL,'LÊ XUÂN','KIÊN','$2a$10$qCi4lXsp6e.ihoqzJwFYDOtsnnYITrEFQ7wZqGNNv.Hxhf6vU8Trm','0963347562_1777112224976',NULL,'NEW','0963347562'),('2f8611b1-5959-4e82-9528-e62566212d15',NULL,'','lekien','$2a$10$ccqvX8B5TD03kGUnlR7wU.02qH.sDQk1kwMbMv4DWHvezrhz1B9D.','9999999999_1777112780095',NULL,'NEW','9999999999'),('34951dae-3ca8-4391-a5cb-ea7b7dcb5fc1',NULL,'','lekien','$2a$10$UaV4/Qq1y.paLp.H4PnkOOyDG1OOb8Qa37NFvCLQgahB7IYZmzdO.','9999999999_1777112713570',NULL,'NEW','9999999999'),('36fca282-22ac-4bbc-82be-36a3ad04a601',NULL,'','','$2a$10$K8kTd28rVwOXwRHlx.TRsuVy5rAMxAJ.aFBIVVhooL.HqIBP73USi','0964453535_1776333305908',NULL,'NEW','0964453535'),('38cabff6-8641-46b4-a9c6-bec484be23e0',NULL,'','lekien','$2a$10$6rz08dbithQJ2suoH0mCM.jGY/fFKvcSbL6JvLfIbbPtBSr8WPiAm','0983347562_1777306823227',NULL,'NEW','0983347562'),('3c0e8845-74a2-469c-b960-3a3bbfdff886',NULL,'','kien','$2a$10$e6qKqJBMUJq4Z3Y3yhxUze0Z3K0SUb8fbi/ndmiQtFjXgWuFM6/KO','0414141433_1776359952787',NULL,'NEW','0414141433'),('3ca2abe0-5207-407b-b767-05c8af5fe149',NULL,'Lê Xuân','Kiên','$2a$10$udAk5Ie.OpSJSOSp/SPJAurEq4CXzrHZ71y6gS4tb8p4j9Y8lR33e','0963347562_1777281960221',NULL,'NEW','0963347562'),('508fa61e-9d66-4c53-bc66-8ef11cb133dc',NULL,'','kien','$2a$10$xz53KXilln5XzyZEQr6bxedCgbVJO2E9XeqD4UvM6jSerb.uzON1a','0964324244_1775204724248',NULL,'NEW','0964324244'),('54c1d0a3-fa44-4ecb-a423-7f8d9eaf3e6e','2004-11-08','Xuân','Kiên','$2a$10$zrdB8s9XxqesyjuUwrqRv.blh.mPOu3J9k4LW0bMhwSBv3OE0Dbc6','kien2004',NULL,NULL,NULL),('54e462af-5706-456a-808a-f80540908075',NULL,'Kiên Lê','Xuân','$2a$10$5S4QebsmXL0afug4POQq0OXApW/o7oZG.Jg3WZ1gDia9.O5x8cw7.','0963347562_1774588436553',NULL,'NEW','0963347562'),('57768b3f-316b-414e-bcf4-ddd1f9037577',NULL,'Nguyen Van','A','$2a$10$OufBcw9OLB5OnnhgsVYR0e5CUayYkXiOQoe/gF34IoqweJN2MSWT2','0963347562_1777112268922',NULL,'NEW','0963347562'),('5c670969-1da0-4f84-be7d-143de6776e16',NULL,'','lekien','$2a$10$PFEA99IwAAJSbeB0cdVODeI62L14bYvFz9VHm/6su4yvGAVKAlwg2','0909999999_1777113563946',NULL,'NEW','0909999999'),('5db10fa3-7e72-4c09-95dc-b203558b4bec','2004-11-08','le','kien','$2a$10$bEG0LGfvCTkDjm3ZGFNYTeZv1UNjB/EcePbQC7dkCemgfzDqXwKa.','kienlexuan',NULL,NULL,NULL),('60807160-bc03-46ce-857b-8b365325aefe',NULL,'','','$2a$10$lU4YFWqJ.nS6In.kKC4BCOQlZOJ3JB6S.YdNWo1ZThEStE/OWndTe','_1776333124041',NULL,'NEW',''),('63318339-e1cf-4550-8398-b35d0a03a6c5',NULL,'NGUYEN VAN','A','$2a$10$8bTwx8b0WZvWxkNhk.12.OHIm.bhrDo.aE8TSVVHsmAbXvwO9xp4q','0963347562_1774588150580',NULL,'NEW','0963347562'),('6bcba56d-bd9d-4d19-b98c-7fb469122f61',NULL,'Lê Xuân','Kiên','$2a$10$iYFM/fEIYkgKiy.SlsA4A.EYJzub33jYCAaCgVSArPS/nr1EJvW7u','0963347562_1777111588164',NULL,'NEW','0963347562'),('6cd17fa9-9024-4304-ae12-309646143aef','2004-08-11','Le','Kien','$2a$10$hirhIj3RTx6sN0B4CEvkIukevneY1rHsZv3hcUsDjf7SKzitOgOZC','admin',NULL,NULL,NULL),('73a03e16-de28-496e-b1b0-c6173f02e950','2004-11-08','K','L','$2a$10$ZIifKSmRgrcNF83ox9ySQ.Y28wzzYuPc.FE9EIcTnafv9yI/q0xeO','customer4',NULL,NULL,NULL),('86728479-1ce3-46db-b5ec-09452f69bff1',NULL,'','lekien','$2a$10$ewlVhoc.j1Svj0qYcjaE8u8ajPLzxsgECm.b.mNJt2Cn33FNgevdS','9999999990_1777113211220',NULL,'NEW','9999999990'),('891e3838-edea-488b-ab33-aff766656b3f','2004-08-11','admin','admin','$2a$10$v6Iuj1iPRowbyT1dKa90XuiSCuZAvbhl0lSKEM5vD4IHWBcq8fhAC','kien',NULL,NULL,NULL),('991e3838-edea-488b-ab33-aff766656b3f','2004-08-11','xuan','kien','$2a$10$v6Iuj1iPRowbyT1dKa90XuiSCuZAvbhl0lSKEM5vD4IHWBcq8fhAC','xuankien',NULL,NULL,NULL),('a478e5a9-7175-44d1-9670-a07aab6b7e2b',NULL,'','lekien','$2a$10$YglqqUq4PRinEBmasejVZ.Ntz/OsjY/04hAkN7/8IoXys6TmJxIbW','0983347561_1777306655751',NULL,'NEW','0983347561'),('a79fa71a-fd91-41fe-bddb-81fb7ebc48a2',NULL,'le xuan','kien','$2a$10$J8H8RqZo5X0hHXZOvDk4bO3sHDyEv6e8wS.WTRKPX0J4.DnFPEuBC','0942342432_1775920256772',NULL,'NEW','0942342432'),('a7e5d781-e7be-4f1b-850c-dc28384da846',NULL,'Kiên Lê','Xuân','$2a$10$0/.0EkqAXnBFOb0Vl9BtAOnxjFv8UHxqF7YZSY4vP478.27VoSsDS','0242342344_1776496772782',NULL,'NEW','0242342344'),('b9ff237e-7ff2-43d5-a77a-40e2e4eba9ce',NULL,'','','$2a$10$.CEw2KvHONaCfl00N4sSCu/RPs61/ezeqia9hVuVij6ocMZLIZFS2','0963347563_1776333336782',NULL,'NEW','0963347563'),('bd3308e4-4200-11f1-9f03-92f0eedc8cc1','1999-05-10','Nguyen','Van A','123456','user1','HCM','ACTIVE','0901111111'),('bd33231a-4200-11f1-9f03-92f0eedc8cc1','2000-08-20','Tran','Thi B','123456','user2','HCM','ACTIVE','0902222222'),('bd3328d7-4200-11f1-9f03-92f0eedc8cc1','1995-03-15','Le','Van C','123456','user3','Ha Noi','ACTIVE','0903333333'),('bd332a0e-4200-11f1-9f03-92f0eedc8cc1','1998-07-25','Pham','Thi D','123456','user4','Da Nang','ACTIVE','0904444444'),('bd332ac7-4200-11f1-9f03-92f0eedc8cc1','2001-12-01','Hoang','Van E','123456','user5','Can Tho','NEW','0905555555'),('c24230bd-99ed-41e3-9ce5-cb7e284e6691',NULL,'','lekien11','$2a$10$JALbwK/kihGY9HHdxq5PHe83FrCsP3cJAbIk9xJflmFmNkYmPZC6i','09675676567_1777344718583',NULL,'NEW','09675676567'),('ce192f24-a155-407a-8666-8ceb6ebf0fb5',NULL,'','','$2a$10$6TYd2FUAgoZtdm7U2OD5CedHedT5nz1DCkcz3KasjntaE/yv1xkQC','_1776333281785',NULL,'NEW',''),('d2946cc4-1e8d-4d31-b1db-c47ff3e8b053',NULL,'Lê Xuân','Kiên','$2a$10$OPt2Z2H2RZf1d8MedjT0Xel2soyqV5fLhOLi7idUFG.jcO6v0yS2K','0963347562_1774532434117',NULL,'NEW','0963347562'),('dd496404-d85e-4dfa-ae14-7bdb201e1dca',NULL,'le xuan','kien','$2a$10$3KsVimP/1JJafcoFaX11OeX/ly3wAeBu1DULrv57RNOlx0z7.djM6','0984026073_1776350956465',NULL,'NEW','0984026073'),('user-uuid-002','1995-05-15','Trần','Nhân Viên','$2a$10$8.UnVuG9HHgffUDAlk8qfOpauQIUVvHEOmwuryqkkvSu/m6DAW7y.','staff',NULL,NULL,NULL),('user-uuid-003','2000-10-20','Lê','Khách Hàng','$2a$10$8.UnVuG9HHgffUDAlk8qfOpauQIUVvHEOmwuryqkkvSu/m6DAW7y.','customer1',NULL,NULL,NULL),('user-uuid-004','1998-12-12','Phạm','Văn A','$2a$10$8.UnVuG9HHgffUDAlk8qfOpauQIUVvHEOmwuryqkkvSu/m6DAW7y.','customer2',NULL,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` varchar(255) NOT NULL,
  `roles_name` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`,`roles_name`),
  KEY `FK6pmbiap985ue1c0qjic44pxlc` (`roles_name`),
  CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK6pmbiap985ue1c0qjic44pxlc` FOREIGN KEY (`roles_name`) REFERENCES `role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('54c1d0a3-fa44-4ecb-a423-7f8d9eaf3e6e','ADMIN'),('6cd17fa9-9024-4304-ae12-309646143aef','ADMIN'),('891e3838-edea-488b-ab33-aff766656b3f','ADMIN'),('54c1d0a3-fa44-4ecb-a423-7f8d9eaf3e6e','STAFF'),('60807160-bc03-46ce-857b-8b365325aefe','STAFF'),('73a03e16-de28-496e-b1b0-c6173f02e950','STAFF'),('991e3838-edea-488b-ab33-aff766656b3f','STAFF'),('ce192f24-a155-407a-8666-8ceb6ebf0fb5','STAFF'),('user-uuid-002','STAFF'),('3c0e8845-74a2-469c-b960-3a3bbfdff886','USER'),('54c1d0a3-fa44-4ecb-a423-7f8d9eaf3e6e','USER'),('5c670969-1da0-4f84-be7d-143de6776e16','USER'),('5db10fa3-7e72-4c09-95dc-b203558b4bec','USER'),('60807160-bc03-46ce-857b-8b365325aefe','USER'),('73a03e16-de28-496e-b1b0-c6173f02e950','USER'),('a7e5d781-e7be-4f1b-850c-dc28384da846','USER'),('ce192f24-a155-407a-8666-8ceb6ebf0fb5','USER'),('user-uuid-003','USER'),('user-uuid-004','USER');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-12 20:37:46
