/*
SQLyog Professional v12.09 (64 bit)
MySQL - 8.0.28 : Database - seckill
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`seckill` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `seckill`;

/*Table structure for table `t_goods` */

DROP TABLE IF EXISTS `t_goods`;

CREATE TABLE `t_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` varchar(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` varchar(64) DEFAULT NULL COMMENT '商品图片',
  `goods_detail` longtext COMMENT '商品详情',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品价格',
  `goods_stock` int DEFAULT '0' COMMENT '商品库存，-1代表没限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_goods` */

insert  into `t_goods`(`id`,`goods_name`,`goods_title`,`goods_img`,`goods_detail`,`goods_price`,`goods_stock`) values (1,'IPHONE 12','IPHONE 12 64G','/img/IPHONE12.png','IPHONE 12 64G','6299.00',100),(2,'IPHONE 12 PRO','IPHONE 12 PRO 128G','/img/IPHONE12PRO.png','IPHONE 12 PRO 128G','9299.00',100);

/*Table structure for table `t_order` */

DROP TABLE IF EXISTS `t_order`;

CREATE TABLE `t_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `goods_id` bigint NOT NULL COMMENT '商品ID',
  `delivery_addr_id` bigint NOT NULL COMMENT '收货地址ID',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '商品名称',
  `goods_count` int DEFAULT '0' COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
  `order_channel` tinyint DEFAULT '0' COMMENT '1pc， 2android， 3ios',
  `status` tinyint DEFAULT '0' COMMENT '订单状态 0新建未支付， 1已支付， 2已发货， 3已收货， 4已退款， 5已完成',
  `create_date` datetime DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_order` */

insert  into `t_order`(`id`,`user_id`,`goods_id`,`delivery_addr_id`,`goods_name`,`goods_count`,`goods_price`,`order_channel`,`status`,`create_date`,`pay_date`) values (26,13185621828,1,0,'IPHONE 12',1,'6299.00',1,0,'2022-03-25 16:58:02',NULL),(27,13185621828,2,0,'IPHONE 12 PRO',1,'9299.00',1,0,'2022-03-25 17:42:03',NULL);

/*Table structure for table `t_seckill_goods` */

DROP TABLE IF EXISTS `t_seckill_goods`;

CREATE TABLE `t_seckill_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀商品ID',
  `goods_id` bigint DEFAULT NULL COMMENT '商品ID',
  `seckill_price` decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
  `stock_count` int DEFAULT NULL COMMENT '库存数量',
  `start_date` datetime DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` datetime DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_seckill_goods` */

insert  into `t_seckill_goods`(`id`,`goods_id`,`seckill_price`,`stock_count`,`start_date`,`end_date`) values (3,1,'100.00',9,'2022-03-20 14:54:46','2022-03-31 14:54:59'),(4,2,'200.00',9,'2022-03-20 14:55:03','2022-03-31 14:55:08');

/*Table structure for table `t_seckill_order` */

DROP TABLE IF EXISTS `t_seckill_order`;

CREATE TABLE `t_seckill_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '秒杀订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `goods_id` bigint NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_seckill_uid_gid` (`user_id`,`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_seckill_order` */

insert  into `t_seckill_order`(`id`,`user_id`,`order_id`,`goods_id`) values (17,13185621828,26,1),(18,13185621828,27,2);

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` bigint NOT NULL COMMENT '用户ID：手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt)+salt)',
  `salt` varchar(10) DEFAULT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像',
  `register_date` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`nickname`,`password`,`salt`,`head`,`register_date`,`last_login_date`,`login_count`) values (13185621828,'admin','b7797cce01b4b131b433b6acf4add449','1a2b3c4d',NULL,NULL,NULL,0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
