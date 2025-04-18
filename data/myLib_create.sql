/*
Navicat MySQL Data Transfer

Source Server         : Mysql
Source Server Version : 50735
Source Host           : localhost:3306
Source Database       : demo

Target Server Type    : MYSQL
Target Server Version : 50735
File Encoding         : 65001

Date: 2023-03-03 11:06:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for roots
-- ----------------------------

DROP TABLE IF EXISTS `roots`;
CREATE TABLE roots (
                       root_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(64) UNIQUE NOT NULL,
                       password VARCHAR(128) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Records of root(for test case)
-- root default password: 1
-- ----------------------------

INSERT INTO `roots` VALUES ('1', 'root', '*E6CC90B878B948C35E92B003C792C46C58C4AF40', '2023-01-11 15:45:16');

-- ----------------------------
-- Table structure for supervisors
-- ----------------------------

DROP TABLE IF EXISTS `supervisors`;
CREATE TABLE supervisors (
                             supervisor_id INT AUTO_INCREMENT PRIMARY KEY,
                             username VARCHAR(64) UNIQUE NOT NULL,
                             password VARCHAR(128) NOT NULL,
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Table structure for user_privileges
-- ----------------------------

DROP TABLE IF EXISTS `user_privileges`;
CREATE TABLE user_privileges (
                                 user_type ENUM('root', 'supervisor', 'reader') NOT NULL,
                                 user_id INT NOT NULL,
                                 mod_id VARCHAR(50) NOT NULL,
                                 priv VARCHAR(50) NOT NULL,
                                 suspended TINYINT(1) DEFAULT 0,
                                 PRIMARY KEY (user_type, user_id, mod_id, priv)
);

-- ----------------------------
-- Table structure for readers
-- ----------------------------

DROP TABLE IF EXISTS `readers`;
CREATE TABLE readers (
                         reader_id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(64) NOT NULL,
                         student_id VARCHAR(46) UNIQUE NOT NULL,
                         contact VARCHAR(128) NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         borrowing JSON, -- ABANDONED
                         due_time JSON -- ABANDONED
);

-- ----------------------------
-- Table structure for login_log
-- ----------------------------

DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_code` varchar(64) NOT NULL,
  `ip_address` varchar(46) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `os` varchar(128) DEFAULT NULL,
  `browser` varchar(128) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of login_log
-- ----------------------------

INSERT INTO `login_log` VALUES ('1', 'root', '127.0.0.1', '管理员', 'Windows 10', 'Chrome 10 108.0.0.0', '2023-01-11 15:45:16');

-- ----------------------------
-- Table structure for recordings
-- ----------------------------

DROP TABLE IF EXISTS `recordings`;
CREATE TABLE recordings (
                            id INT NOT NULL AUTO_INCREMENT,
                            reader_id INT NOT NULL,
                            isbn VARCHAR(13) NOT NULL,
                            borrowed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            returned_time DATETIME NULL,
                            PRIMARY KEY (id),
                            INDEX idx_reader (reader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table of books
-- ----------------------------

DROP TABLE IF EXISTS `books`;
CREATE TABLE books (
  isbn VARCHAR(13) NOT NULL COMMENT 'isbn as identifications',
  title VARCHAR(50),
  author VARCHAR(100),
  publisher VARCHAR(255),
  added_at DATE,
  updated_at DATE,
  total_copies_in_stock INT,
  updated_by VARCHAR(50),
  cover_url VARCHAR(255),
  borrowed_number INT COMMENT 'Cumulative count of book borrows'
);