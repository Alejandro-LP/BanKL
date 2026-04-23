-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: bankl
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `iddb` int NOT NULL AUTO_INCREMENT,
  `pin_seguridad` int NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `contrasena` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL,
  `nombres` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `usuariois` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`iddb`),
  UNIQUE KEY `UK7mreyt1y7pyau7b5axisumplx` (`usuariois`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,1234,'BanKL','Admin123','Sede principal','admin@bankl.com','00000000','Admin','0000000000','admin'),(3,1557,'Jorge Lopez','Alejandro2112/','Carrera 79 #19a -56','joral2112@hotmail.com','1058352717','Jorge Alejandro','3155836691','alejolp');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente_admin`
--

DROP TABLE IF EXISTS `cliente_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_admin` (
  `iddb` int NOT NULL,
  PRIMARY KEY (`iddb`),
  CONSTRAINT `FK33ep459i389h2g50oo63dkpai` FOREIGN KEY (`iddb`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_admin`
--

LOCK TABLES `cliente_admin` WRITE;
/*!40000 ALTER TABLE `cliente_admin` DISABLE KEYS */;
INSERT INTO `cliente_admin` VALUES (1);
/*!40000 ALTER TABLE `cliente_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente_natural`
--

DROP TABLE IF EXISTS `cliente_natural`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente_natural` (
  `iddb` int NOT NULL,
  PRIMARY KEY (`iddb`),
  CONSTRAINT `FKm1tfc88470a8bhrtxwlunubw7` FOREIGN KEY (`iddb`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente_natural`
--

LOCK TABLES `cliente_natural` WRITE;
/*!40000 ALTER TABLE `cliente_natural` DISABLE KEYS */;
INSERT INTO `cliente_natural` VALUES (3);
/*!40000 ALTER TABLE `cliente_natural` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuenta`
--

DROP TABLE IF EXISTS `cuenta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta` (
  `bloqueada` bit(1) NOT NULL,
  `cliente_id` int DEFAULT NULL,
  `cupo` double NOT NULL,
  `cvv` int NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_cuenta` int NOT NULL,
  `saldo` double NOT NULL,
  `fecha_expiracion` varchar(255) DEFAULT NULL,
  `numero_tarjeta` varchar(255) DEFAULT NULL,
  `propietario` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4p224uogyy5hmxvn8fwa2jlug` (`cliente_id`),
  CONSTRAINT `FK4p224uogyy5hmxvn8fwa2jlug` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta`
--

LOCK TABLES `cuenta` WRITE;
/*!40000 ALTER TABLE `cuenta` DISABLE KEYS */;
INSERT INTO `cuenta` VALUES (_binary '\0',3,0,313,1,99915266,19999,'12/2029','3653 8253 5319 2085','Jorge Alejandro Jorge Lopez','DEBITO'),(_binary '\0',3,14310.1,126,2,72033717,0,'09/2028','6427 9952 0184 0729','Jorge Alejandro Jorge Lopez','CREDITO');
/*!40000 ALTER TABLE `cuenta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaccion`
--

DROP TABLE IF EXISTS `transaccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaccion` (
  `cuenta_id` int DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `valor` double NOT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkkale73n3p5vwbgxa49yiyqgx` (`cuenta_id`),
  CONSTRAINT `FKkkale73n3p5vwbgxa49yiyqgx` FOREIGN KEY (`cuenta_id`) REFERENCES `cuenta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaccion`
--

LOCK TABLES `transaccion` WRITE;
/*!40000 ALTER TABLE `transaccion` DISABLE KEYS */;
INSERT INTO `transaccion` VALUES (1,1,19999,'2026-04-23 02:32:47.962949','CONSIGNACION');
/*!40000 ALTER TABLE `transaccion` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-23  9:26:55
