-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pdi
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
-- Table structure for table `activities`
--

DROP TABLE IF EXISTS `activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activities` (
  `act_id` int NOT NULL AUTO_INCREMENT,
  `act_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `act_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `act_status` enum('canceled','pending','in_progress','completed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending',
  `act_deadline` date DEFAULT NULL,
  `act_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `act_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `goal_id` int NOT NULL,
  PRIMARY KEY (`act_id`),
  KEY `idx_activities_goal` (`goal_id`),
  CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`goa_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activities`
--

LOCK TABLES `activities` WRITE;
/*!40000 ALTER TABLE `activities` DISABLE KEYS */;
/*!40000 ALTER TABLE `activities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_tags`
--

DROP TABLE IF EXISTS `activity_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_tags` (
  `activity_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`activity_id`,`tag_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `activity_tags_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`act_id`) ON DELETE CASCADE,
  CONSTRAINT `activity_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_tags`
--

LOCK TABLES `activity_tags` WRITE;
/*!40000 ALTER TABLE `activity_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activity_templates`
--

DROP TABLE IF EXISTS `activity_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_templates` (
  `act_tmp_id` int NOT NULL AUTO_INCREMENT,
  `act_tmp_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `act_tmp_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `act_tmp_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `goal_template_id` int NOT NULL,
  PRIMARY KEY (`act_tmp_id`),
  KEY `idx_activity_templates_goal_template` (`goal_template_id`),
  CONSTRAINT `activity_templates_ibfk_1` FOREIGN KEY (`goal_template_id`) REFERENCES `goal_templates` (`goa_tmp_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_templates`
--

LOCK TABLES `activity_templates` WRITE;
/*!40000 ALTER TABLE `activity_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attachments`
--

DROP TABLE IF EXISTS `attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachments` (
  `att_id` int NOT NULL AUTO_INCREMENT,
  `att_file_path` varchar(255) NOT NULL,
  `att_uploaded_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `uploaded_by` int NOT NULL,
  `goal_id` int DEFAULT NULL,
  `activity_id` int DEFAULT NULL,
  PRIMARY KEY (`att_id`),
  KEY `uploaded_by` (`uploaded_by`),
  KEY `goal_id` (`goal_id`),
  KEY `activity_id` (`activity_id`),
  CONSTRAINT `attachments_ibfk_1` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`use_id`) ON DELETE CASCADE,
  CONSTRAINT `attachments_ibfk_2` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`goa_id`) ON DELETE CASCADE,
  CONSTRAINT `attachments_ibfk_3` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`act_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attachments`
--

LOCK TABLES `attachments` WRITE;
/*!40000 ALTER TABLE `attachments` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collaborator_goal_templates`
--

DROP TABLE IF EXISTS `collaborator_goal_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collaborator_goal_templates` (
  `collaborator_id` int NOT NULL,
  `goal_template_id` int NOT NULL,
  `assigned_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`collaborator_id`,`goal_template_id`),
  KEY `goal_template_id` (`goal_template_id`),
  CONSTRAINT `collaborator_goal_templates_ibfk_1` FOREIGN KEY (`collaborator_id`) REFERENCES `collaborators` (`col_id`) ON DELETE CASCADE,
  CONSTRAINT `collaborator_goal_templates_ibfk_2` FOREIGN KEY (`goal_template_id`) REFERENCES `goal_templates` (`goa_tmp_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaborator_goal_templates`
--

LOCK TABLES `collaborator_goal_templates` WRITE;
/*!40000 ALTER TABLE `collaborator_goal_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `collaborator_goal_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collaborators`
--

DROP TABLE IF EXISTS `collaborators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collaborators` (
  `col_id` int NOT NULL AUTO_INCREMENT,
  `col_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `col_cpf` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `col_email` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `col_status` enum('active','on_leave','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `col_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `col_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `col_deleted_at` timestamp NULL DEFAULT NULL,
  `department_id` int NOT NULL,
  PRIMARY KEY (`col_id`),
  UNIQUE KEY `col_cpf` (`col_cpf`),
  UNIQUE KEY `col_email` (`col_email`),
  KEY `idx_collaborators_department` (`department_id`),
  CONSTRAINT `collaborators_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`dep_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collaborators`
--

LOCK TABLES `collaborators` WRITE;
/*!40000 ALTER TABLE `collaborators` DISABLE KEYS */;
INSERT INTO `collaborators` VALUES (1,'João Santos Martins','12352778910','email1@gmail.com','active','2025-10-02 12:12:40','2025-10-02 12:12:40',NULL,2),(2,'Débora de Assis Ferreira','98735678910','email2@gmail.com','inactive','2025-10-02 12:12:40','2025-10-02 12:12:40',NULL,2),(3,'Atair Meireiras da Costa','12345623730','email3@gmail.com','on_leave','2025-10-02 12:12:40','2025-10-02 12:12:40',NULL,4),(17,'Nome','12345678909','nome@gmail.com','active','2025-10-02 12:16:14','2025-10-02 12:16:14',NULL,1),(18,'kauan domingues','44784753850','kauandomingues931@gmail.com','active','2025-10-16 11:28:57','2025-10-16 11:28:57',NULL,1);
/*!40000 ALTER TABLE `collaborators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `com_id` int NOT NULL AUTO_INCREMENT,
  `com_entity_type` enum('goal','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `com_entity_id` int NOT NULL,
  `com_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `com_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  PRIMARY KEY (`com_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`use_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `dep_id` int NOT NULL AUTO_INCREMENT,
  `dep_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `dep_status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `dep_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dep_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `dep_deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`dep_id`),
  UNIQUE KEY `dep_name` (`dep_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,'Desenvolvimento','active','2025-09-28 02:40:00','2025-09-28 02:40:00',NULL),(2,'UX/UI Design','active','2025-09-28 02:40:18','2025-09-28 02:40:18',NULL),(3,'Inteligência de Dados','active','2025-09-28 02:40:48','2025-09-28 02:40:48',NULL),(4,'Infraestrutura de TI','active','2025-09-28 02:41:41','2025-09-28 02:41:41',NULL),(5,'Segurança da Informação','active','2025-09-28 02:42:08','2025-09-28 02:42:08',NULL);
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goal_tags`
--

DROP TABLE IF EXISTS `goal_tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goal_tags` (
  `goal_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`goal_id`,`tag_id`),
  KEY `tag_id` (`tag_id`),
  CONSTRAINT `goal_tags_ibfk_1` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`goa_id`) ON DELETE CASCADE,
  CONSTRAINT `goal_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goal_tags`
--

LOCK TABLES `goal_tags` WRITE;
/*!40000 ALTER TABLE `goal_tags` DISABLE KEYS */;
INSERT INTO `goal_tags` VALUES (1,1),(2,1),(5,1),(6,1),(7,1),(3,2),(7,2),(8,2),(4,3),(9,3);
/*!40000 ALTER TABLE `goal_tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goal_templates`
--

DROP TABLE IF EXISTS `goal_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goal_templates` (
  `goa_tmp_id` int NOT NULL AUTO_INCREMENT,
  `goa_tmp_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `goa_tmp_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `goa_tmp_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`goa_tmp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goal_templates`
--

LOCK TABLES `goal_templates` WRITE;
/*!40000 ALTER TABLE `goal_templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `goal_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goals`
--

DROP TABLE IF EXISTS `goals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goals` (
  `goa_id` int NOT NULL AUTO_INCREMENT,
  `goa_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `goa_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `goa_status` enum('canceled','pending','in_progress','completed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'pending',
  `goa_deadline` date DEFAULT NULL,
  `goa_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `goa_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `collaborator_id` int NOT NULL,
  PRIMARY KEY (`goa_id`),
  KEY `idx_goals_collaborator` (`collaborator_id`),
  CONSTRAINT `goals_ibfk_1` FOREIGN KEY (`collaborator_id`) REFERENCES `collaborators` (`col_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goals`
--

LOCK TABLES `goals` WRITE;
/*!40000 ALTER TABLE `goals` DISABLE KEYS */;
INSERT INTO `goals` VALUES (1,'Ganhar promoção','ganha a promoção','completed',NULL,'2025-10-21 20:39:58','2025-10-22 19:12:24',1),(2,'Ganhar promoção 2','ganha a promoção 2','pending',NULL,'2025-10-21 20:40:08','2025-10-21 20:40:08',1),(3,'Ganhar promoção 3','ganha a promoção 3','canceled',NULL,'2025-10-21 20:40:14','2025-10-22 19:12:33',1),(4,'Ganhar promoção 4','ganha a promoção 4','pending',NULL,'2025-10-21 21:58:38','2025-10-21 21:58:38',1),(5,'Ganhar promoção 5','ganha a promoção 5','canceled','2025-10-22','2025-10-22 19:41:52','2025-10-22 19:57:06',18),(6,'Ganhar promoção 6','ganha a promoção 6','completed',NULL,'2025-10-22 19:41:59','2025-10-22 19:42:53',18),(7,'Ganhar promoção 7','ganha a promoção 7','pending',NULL,'2025-10-22 19:42:06','2025-10-22 19:42:06',18),(8,'Ganhar promoção 8','ganha a promoção 8','pending',NULL,'2025-10-22 19:42:11','2025-10-22 19:42:11',18),(9,'Ganhar promoção 9','ganha a promoção 9','in_progress',NULL,'2025-10-22 20:22:24','2025-10-22 20:22:58',18);
/*!40000 ALTER TABLE `goals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `log_action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `log_details` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `log_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  PRIMARY KEY (`log_id`),
  KEY `idx_logs_user` (`user_id`),
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`use_id`),
  CONSTRAINT `logs_chk_1` CHECK (json_valid(`log_details`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `not_id` int NOT NULL AUTO_INCREMENT,
  `not_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `not_type` enum('deadline','goal_update','activity_update','system') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `not_is_read` tinyint(1) DEFAULT '0',
  `not_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  PRIMARY KEY (`not_id`),
  KEY `idx_notifications_user` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`use_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progress_history`
--

DROP TABLE IF EXISTS `progress_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progress_history` (
  `his_id` int NOT NULL AUTO_INCREMENT,
  `his_entity_type` enum('goal','activity') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `his_entity_id` int NOT NULL,
  `his_old_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `his_new_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `his_changed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `changed_by` int NOT NULL,
  PRIMARY KEY (`his_id`),
  KEY `changed_by` (`changed_by`),
  CONSTRAINT `progress_history_ibfk_1` FOREIGN KEY (`changed_by`) REFERENCES `users` (`use_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progress_history`
--

LOCK TABLES `progress_history` WRITE;
/*!40000 ALTER TABLE `progress_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `progress_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessions` (
  `ses_id` int NOT NULL AUTO_INCREMENT,
  `ses_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ses_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ses_expires_at` timestamp NULL DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`ses_id`),
  UNIQUE KEY `ses_token` (`ses_token`),
  KEY `idx_sessions_user` (`user_id`),
  CONSTRAINT `sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`use_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tags` (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tag_type` enum('HARD','SOFT') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (1,'Estudo','HARD'),(2,'Comunicação','SOFT'),(3,'Programação','HARD');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `use_id` int NOT NULL AUTO_INCREMENT,
  `use_name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `use_email` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `use_password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `use_role` enum('hr_manager','general_manager','department_manager') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'department_manager',
  `use_status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'active',
  `use_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `use_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `use_deleted_at` timestamp NULL DEFAULT NULL,
  `department_id` int DEFAULT NULL,
  PRIMARY KEY (`use_id`),
  UNIQUE KEY `use_email` (`use_email`),
  KEY `idx_users_department` (`department_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `departments` (`dep_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Cauã','caua@gmail.com','123','department_manager','active','2025-10-24 16:56:23','2025-10-24 16:56:23',NULL,1),(2,'Ramon','ramon@gmail.com','321','hr_manager','active','2025-10-24 23:18:06','2025-10-24 23:18:06',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-24 20:47:31
