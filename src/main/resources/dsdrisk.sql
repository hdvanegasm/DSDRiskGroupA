-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 17, 2017 at 10:12 PM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 7.1.1

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
('carlos', '2341', 'carlos@hola.com', 0, 0, 0, 'OFFLINE'),
('hernan', '2341', 'hernan@hola.com', 0, 0, 0, 'PLAYING'),
('NuevoUser1', '1234', 's@uasdsadnal1', 0, 0, 0, 'OFFLINE'),
('spinos', '1234', 's@unal', 0, 0, 0, 'OFFLINE');

-- --------------------------------------------------------

--
-- Table structure for table `host`
--

CREATE TABLE `host` (
  `player` varchar(80) NOT NULL,
  `session` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `host`
--

INSERT INTO `host` (`player`, `session`) VALUES
('hernan', 1);

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
('america');

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

--
-- Dumping data for table `player`
--

INSERT INTO `player` (`user`, `color`, `captureState`, `territoryAmount`, `continentAmount`, `cardAmount`, `turn`, `type`, `sessionID`) VALUES
('dochoau', 'RED', 'non-captured', 0, 0, 0, 0, NULL, 1),
('edalpin', 'GREEN', 'non-captured', 0, 0, 0, 0, NULL, 1),
('hernan', 'YELLOW', 'non-captured', 0, 0, 0, 0, 'class server.gamebuilder.model.Host', 1),
('sareiza', 'BLUE', 'non-captured', 0, 0, 0, 0, NULL, 1);

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

--
-- Dumping data for table `session`
--

INSERT INTO `session` (`map`, `id`, `state`, `type`) VALUES
('america', 1, 'CREATING', 'WORLD_DOMINATION_RISK');

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
('class server.gamebuilder.model.Player', 'hernan'),
(NULL, 'NuevoUser'),
(NULL, 'NuevoUser1'),
(NULL, 'sareiza'),
(NULL, 'spinos');

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
-- Indexes for table `host`
--
ALTER TABLE `host`
  ADD PRIMARY KEY (`player`);

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
-- Constraints for table `host`
--
ALTER TABLE `host`
  ADD CONSTRAINT `host_ibfk_1` FOREIGN KEY (`player`) REFERENCES `player` (`user`);

--
-- Constraints for table `player`
--
ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`username`),
  ADD CONSTRAINT `player_ibfk_2` FOREIGN KEY (`sessionID`) REFERENCES `session` (`id`);

--
-- Constraints for table `session`
--
ALTER TABLE `session`
  ADD CONSTRAINT `session_ibfk_1` FOREIGN KEY (`map`) REFERENCES `map` (`name`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
