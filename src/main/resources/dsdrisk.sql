-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: May 30, 2017 at 04:41 AM
-- Server version: 10.1.19-MariaDB
-- PHP Version: 5.6.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dsdrisk`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `username` varchar(80) NOT NULL,
  `password` varchar(80) NOT NULL,
  `email` varchar(80) NOT NULL,
  `numberOfSessionLost` int(11) NOT NULL,
  `numberOfSessionswon` int(11) NOT NULL,
  `percentageOfWins` double NOT NULL,
  `status` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`username`, `password`, `email`, `numberOfSessionLost`, `numberOfSessionswon`, `percentageOfWins`, `status`) VALUES
('carlos', '2341', 'carlos@hola.com', 0, 0, 0, 'ONLINE'),
('edalpin', '1234', 'edalpin@unal.edu.co', 0, 0, 0, 'ONLINE'),
('hernan', '1234', 'hdvanegasm@unal.edu.co', 0, 0, 0, 'PLAYING'),
('spinos', '1234', 's@unal', 0, 0, 0, 'ONLINE');

-- --------------------------------------------------------

--
-- Table structure for table `contact`
--

CREATE TABLE `contact` (
  `username` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contact`
--

INSERT INTO `contact` (`username`) VALUES
('spinos');

-- --------------------------------------------------------

--
-- Table structure for table `contactlist`
--

CREATE TABLE `contactlist` (
  `user_username` varchar(80) NOT NULL,
  `contact_username` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contactlist`
--

INSERT INTO `contactlist` (`user_username`, `contact_username`) VALUES
('edalpin', 'spinos');

-- --------------------------------------------------------

--
-- Table structure for table `host`
--

CREATE TABLE `host` (
  `player` varchar(80) NOT NULL,
  `session` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `invitation`
--

CREATE TABLE `invitation` (
  `id` int(11) NOT NULL,
  `host` varchar(80) NOT NULL,
  `contact_username` varchar(80) NOT NULL,
  `state` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `map`
--

CREATE TABLE `map` (
  `name` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `map`
--

INSERT INTO `map` (`name`) VALUES
('Prado Centro');

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE `player` (
  `user` varchar(80) NOT NULL,
  `color` varchar(80) NOT NULL,
  `captureState` varchar(80) NOT NULL,
  `territoryAmount` int(11) NOT NULL,
  `continentAmount` int(11) NOT NULL,
  `cardAmount` int(11) NOT NULL,
  `turn` tinyint(1) NOT NULL,
  `type` varchar(80) DEFAULT NULL,
  `sessionID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `request`
--

CREATE TABLE `request` (
  `id` int(11) NOT NULL,
  `session` int(11) NOT NULL,
  `state` varchar(80) NOT NULL,
  `username` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `session`
--

CREATE TABLE `session` (
  `map` varchar(80) NOT NULL,
  `id` int(11) NOT NULL,
  `state` varchar(80) NOT NULL,
  `type` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `typeOfUser` varchar(80) DEFAULT NULL,
  `username` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`typeOfUser`, `username`) VALUES
(NULL, 'carlos'),
(NULL, 'dochoau'),
(NULL, 'edalpin'),
(NULL, 'hernan'),
(NULL, 'NuevoUser'),
(NULL, 'NuevoUser1'),
(NULL, 'sareiza'),
('CONTACT', 'spinos');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `contact`
--
ALTER TABLE `contact`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `contactlist`
--
ALTER TABLE `contactlist`
  ADD PRIMARY KEY (`user_username`,`contact_username`),
  ADD KEY `contact_username` (`contact_username`);

--
-- Indexes for table `host`
--
ALTER TABLE `host`
  ADD PRIMARY KEY (`player`),
  ADD KEY `session` (`session`);

--
-- Indexes for table `invitation`
--
ALTER TABLE `invitation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `host` (`host`),
  ADD KEY `contact_username` (`contact_username`);

--
-- Indexes for table `map`
--
ALTER TABLE `map`
  ADD PRIMARY KEY (`name`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`user`),
  ADD KEY `sessionID` (`sessionID`);

--
-- Indexes for table `request`
--
ALTER TABLE `request`
  ADD PRIMARY KEY (`id`),
  ADD KEY `session` (`session`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `session`
--
ALTER TABLE `session`
  ADD PRIMARY KEY (`id`),
  ADD KEY `map` (`map`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `account`
--
ALTER TABLE `account`
  ADD CONSTRAINT `account_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`);

--
-- Constraints for table `contact`
--
ALTER TABLE `contact`
  ADD CONSTRAINT `contact_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`);

--
-- Constraints for table `contactlist`
--
ALTER TABLE `contactlist`
  ADD CONSTRAINT `contactlist_ibfk_1` FOREIGN KEY (`user_username`) REFERENCES `account` (`username`),
  ADD CONSTRAINT `contactlist_ibfk_2` FOREIGN KEY (`contact_username`) REFERENCES `contact` (`username`);

--
-- Constraints for table `host`
--
ALTER TABLE `host`
  ADD CONSTRAINT `host_ibfk_1` FOREIGN KEY (`player`) REFERENCES `player` (`user`),
  ADD CONSTRAINT `host_ibfk_2` FOREIGN KEY (`session`) REFERENCES `session` (`id`);

--
-- Constraints for table `invitation`
--
ALTER TABLE `invitation`
  ADD CONSTRAINT `invitation_ibfk_1` FOREIGN KEY (`host`) REFERENCES `host` (`player`),
  ADD CONSTRAINT `invitation_ibfk_2` FOREIGN KEY (`contact_username`) REFERENCES `contact` (`username`);

--
-- Constraints for table `player`
--
ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`username`),
  ADD CONSTRAINT `player_ibfk_2` FOREIGN KEY (`sessionID`) REFERENCES `session` (`id`);

--
-- Constraints for table `request`
--
ALTER TABLE `request`
  ADD CONSTRAINT `request_ibfk_1` FOREIGN KEY (`session`) REFERENCES `session` (`id`),
  ADD CONSTRAINT `request_ibfk_2` FOREIGN KEY (`username`) REFERENCES `user` (`username`);

--
-- Constraints for table `session`
--
ALTER TABLE `session`
  ADD CONSTRAINT `session_ibfk_1` FOREIGN KEY (`map`) REFERENCES `map` (`name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
