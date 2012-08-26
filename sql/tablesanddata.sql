-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 26, 2012 at 07:51 AM
-- Server version: 5.5.24
-- PHP Version: 5.4.4-2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `pi`
--

-- --------------------------------------------------------

--
-- Table structure for table `configuration`
--

DROP TABLE IF EXISTS `configuration`;
CREATE TABLE IF NOT EXISTS `configuration` (
  `key` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `value` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` enum('int','string','boolean','long') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'string',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `configuration`
--

INSERT INTO `configuration` (`key`, `value`, `type`) VALUES
('boostTime', '60', 'int'),
('heatingBoost', 'true', 'boolean'),
('heatingBoostOffTime', '1345970934722', 'long'),
('waterBoost', 'true', 'boolean'),
('waterBoostOffTime', '1345970786701', 'long');

-- --------------------------------------------------------

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
CREATE TABLE IF NOT EXISTS `schedule` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group` smallint(6) unsigned DEFAULT NULL,
  `day` int(2) NOT NULL,
  `hourOn` smallint(6) NOT NULL,
  `minuteOn` smallint(6) NOT NULL,
  `hourOff` smallint(6) NOT NULL,
  `minuteOff` smallint(6) NOT NULL,
  `heatingOn` tinyint(1) NOT NULL,
  `waterOn` tinyint(1) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `group` (`group`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=53 ;

--
-- Dumping data for table `schedule`
--

INSERT INTO `schedule` (`id`, `group`, `day`, `hourOn`, `minuteOn`, `hourOff`, `minuteOff`, `heatingOn`, `waterOn`, `enabled`) VALUES
(47, 1, 1, 0, 0, 0, 0, 0, 1, 1),
(48, 1, 2, 0, 0, 0, 0, 1, 0, 1),
(49, 1, 3, 0, 0, 0, 0, 0, 1, 1),
(50, 1, 4, 0, 0, 0, 0, 1, 0, 1),
(51, 1, 6, 0, 0, 0, 0, 1, 0, 1),
(52, 2, 7, 19, 0, 20, 0, 1, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `schedule_groups`
--

DROP TABLE IF EXISTS `schedule_groups`;
CREATE TABLE IF NOT EXISTS `schedule_groups` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=3 ;

--
-- Dumping data for table `schedule_groups`
--

INSERT INTO `schedule_groups` (`id`, `name`) VALUES
(1, 'Normal'),
(2, 'Nights');
