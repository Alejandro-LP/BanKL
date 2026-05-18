CREATE DATABASE  IF NOT EXISTS `bankl` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bankl`;
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
-- Table structure for table `beneficiario`
--

DROP TABLE IF EXISTS `beneficiario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `beneficiario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) DEFAULT NULL,
  `banco` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero_cuenta` varchar(255) DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6t6ax4eub88v5jf2a98dxn7il` (`cliente_id`),
  CONSTRAINT `FK6t6ax4eub88v5jf2a98dxn7il` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `beneficiario`
--

LOCK TABLES `beneficiario` WRITE;
/*!40000 ALTER TABLE `beneficiario` DISABLE KEYS */;
/*!40000 ALTER TABLE `beneficiario` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,1234,'BanKL','Admin123','Sede principal','admin@bankl.com','00000000','Admin','0000000000','admin'),(4,9355,'Lopez Paez','Alejandro2112/','Carrera 79 #19a -56','joral2112@hotmail.com','1058352717','Jorge Alejandro','3155836691','alejolp'),(5,9627,'Suarez','SantiagoS123','Calle 154 # 53\'36','santiago@gmail.com','12344686769','Santiago','123124551423','SantiagoS'),(6,5860,'Quimbayo','Contraseña123','Carrera 100 # 5-6','juan@gmail.com','97856432234','Juan Esteban','123124124','JuanQ');
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
INSERT INTO `cliente_natural` VALUES (4),(5),(6);
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta`
--

LOCK TABLES `cuenta` WRITE;
/*!40000 ALTER TABLE `cuenta` DISABLE KEYS */;
INSERT INTO `cuenta` VALUES (_binary '\0',4,0,324,3,48548806,10000000,'08/2029','7624 8259 1771 8989','Jorge Alejandro Jorge Lopez','DEBITO'),(_binary '\0',4,55114.3,769,4,37178994,0,'05/2027','5414 6733 7488 5369','Jorge Alejandro Jorge Lopez','CREDITO'),(_binary '\0',5,0,268,5,26420160,10000,'11/2028','5354 2202 5306 8158','Santiago Suarez','DEBITO'),(_binary '\0',5,192787.18,192,6,18446508,0,'06/2028','9534 0310 6768 5489','Santiago Suarez','CREDITO'),(_binary '\0',6,0,368,7,69003315,103124,'05/2028','2051 2223 1327 8635','Juan Esteban Quimbayo','DEBITO'),(_binary '\0',6,82436.88,671,8,64531322,10,'02/2028','1683 4311 9972 1923','Juan Esteban Quimbayo','CREDITO');
/*!40000 ALTER TABLE `cuenta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inversion`
--

DROP TABLE IF EXISTS `inversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inversion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `monto` double NOT NULL,
  `plazo_meses` int NOT NULL,
  `rendimiento_total` double NOT NULL,
  `tasa_interes` double NOT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg5ca1mjqsbaiq0nb71378bkyq` (`cliente_id`),
  CONSTRAINT `FKg5ca1mjqsbaiq0nb71378bkyq` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inversion`
--

LOCK TABLES `inversion` WRITE;
/*!40000 ALTER TABLE `inversion` DISABLE KEYS */;
INSERT INTO `inversion` VALUES (2,'ACTIVA','2026-05-18','2027-05-18',200,12,16,8,'CDT',6);
/*!40000 ALTER TABLE `inversion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificacion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` datetime(6) DEFAULT NULL,
  `leida` bit(1) NOT NULL,
  `mensaje` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6joahdy7nh295ddmy4qtarl2q` (`cliente_id`),
  CONSTRAINT `FK6joahdy7nh295ddmy4qtarl2q` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificacion`
--

LOCK TABLES `notificacion` WRITE;
/*!40000 ALTER TABLE `notificacion` DISABLE KEYS */;
INSERT INTO `notificacion` VALUES (4,'2026-05-18 16:23:10.511020',_binary '','Tu préstamo de $200000.0 fue aprobado. Cuota mensual: $6642.86','INFO','Préstamo aprobado',6),(5,'2026-05-18 16:23:38.368253',_binary '','Tu CDT de $200.0 fue creado. Rendimiento: $16.0','INFO','Inversión creada',6),(6,'2026-05-18 19:37:04.704391',_binary '','Transferiste $10000.0 a Santiago Suarez','ALERTA','Transferencia enviada',4),(7,'2026-05-18 19:45:35.982891',_binary '','Recibiste $10000.0 de Santiago Suarez','INFO','Transferencia recibida',4),(8,'2026-05-18 19:45:36.013905',_binary '','Transferiste $10000.0 a Alejandro Lopez','ALERTA','Transferencia enviada',5);
/*!40000 ALTER TABLE `notificacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prestamo`
--

DROP TABLE IF EXISTS `prestamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prestamo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cuotas_pagadas` int NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `monto` double NOT NULL,
  `numero_cuotas` int NOT NULL,
  `tasa_interes` double NOT NULL,
  `valor_cuota` double NOT NULL,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnuxm078vsm77xihcgtkk4cnns` (`cliente_id`),
  CONSTRAINT `FKnuxm078vsm77xihcgtkk4cnns` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prestamo`
--

LOCK TABLES `prestamo` WRITE;
/*!40000 ALTER TABLE `prestamo` DISABLE KEYS */;
INSERT INTO `prestamo` VALUES (2,1,'ACTIVO','2026-05-18','2029-05-18',200000,36,12,6642.86,6);
/*!40000 ALTER TABLE `prestamo` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaccion`
--

LOCK TABLES `transaccion` WRITE;
/*!40000 ALTER TABLE `transaccion` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transferencia`
--

DROP TABLE IF EXISTS `transferencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transferencia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `cuenta_destino_numero` varchar(255) DEFAULT NULL,
  `cuenta_origen_numero` varchar(255) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `monto` double NOT NULL,
  `nombre_destinatario` varchar(255) DEFAULT NULL,
  `cliente_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrhuqd882w8h2qlgcfu80ky53i` (`cliente_id`),
  CONSTRAINT `FKrhuqd882w8h2qlgcfu80ky53i` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`iddb`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transferencia`
--

LOCK TABLES `transferencia` WRITE;
/*!40000 ALTER TABLE `transferencia` DISABLE KEYS */;
INSERT INTO `transferencia` VALUES (1,'18446508','48548806','Transferencia','FALLIDA','2026-05-18 16:29:56.447870',999,'Santiago Suarez',4),(2,'18446508','48548806','Computador','FALLIDA','2026-05-18 16:30:28.469799',1000,'Santiago Suarez',4),(3,'48548806','18446508','Transferencia','FALLIDA','2026-05-18 16:32:52.886911',20,'Alejandro Lopez',5),(4,'18446508','48548806','Computador','EXITOSA','2026-05-18 19:37:04.684325',10000,'Santiago Suarez',4),(5,'48548806','26420160','Devolucion','EXITOSA','2026-05-18 19:45:36.005895',10000,'Alejandro Lopez',5);
/*!40000 ALTER TABLE `transferencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'bankl'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-18 15:34:00
