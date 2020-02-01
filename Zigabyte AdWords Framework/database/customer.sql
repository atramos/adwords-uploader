-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.84-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema adwords
--

CREATE DATABASE IF NOT EXISTS adwords;
USE adwords;

--
-- Definition of table `products`
--
CREATE TABLE `products` (
  `Name` varchar(50) NOT NULL,
  `category` varchar(50) NOT NULL,
  `Normal Price` double NOT NULL,
  `Promo Price` double NOT NULL,
  PRIMARY KEY  (`Name`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `products_advs`
--
CREATE TABLE `products_advs` (
  `product_name` varchar(50) NOT NULL,
  `group` varchar(50) NOT NULL,
  `adv` varchar(50) NOT NULL,
  `price` varchar(50) NOT NULL,
  PRIMARY KEY  (`product_name`,`adv`,`group`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `products_keywords`
--
CREATE TABLE `products_keywords` (
  `product_name` varchar(50) NOT NULL,
  `group` varchar(50) NOT NULL,
  `keyword` varchar(50) NOT NULL,
  `price` varchar(50) NOT NULL,
  PRIMARY KEY  (`product_name`,`group`,`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
