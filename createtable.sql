USE moviedb;

CREATE TABLE movies(
	id VARCHAR(10) NOT NULL,
	title VARCHAR(100) NOT NULL DEFAULT '',
	year INT NOT NULL,
	director VARCHAR(100) NOT NULL DEFAULT '',
	PRIMARY KEY (id)
	);

CREATE TABLE stars(
	id VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL DEFAULT '',
    birthYear INT DEFAULT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE stars_in_movies(
    starId VARCHAR(10) NOT NULL DEFAULT '' REFERENCES stars(id),
    movieId VARCHAR(10) NOT NULL DEFAULT '' REFERENCES movies(id) 
    );
    
 CREATE TABLE genres(
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL DEFAULT '',
    PRIMARY KEY (id)
    );

CREATE TABLE genres_in_movies(
    genreId INT NOT NULL REFERENCES genres(id),
    movieId VARCHAR(10) NOT NULL DEFAULT '' REFERENCES movies(id) 
    );

CREATE TABLE customers(
    id INT NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    ccId VARCHAR(20) NOT NULL DEFAULT '' REFERENCES creditcards(id),
    address VARCHAR(200) NOT NULL DEFAULT '',
    email VARCHAR(50) NOT NULL DEFAULT '',
    password VARCHAR(20) NOT NULL DEFAULT '',
    PRIMARY KEY (id)
    );

CREATE TABLE sales(
    id INT NOT NULL AUTO_INCREMENT,
    customerId INT NOT NULL REFERENCES customers(id),
    movieId VARCHAR(10) NOT NULL DEFAULT '' REFERENCES movies(id),
    saleDate DATE NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE creditcards(
    id VARCHAR(20) NOT NULL,
    firstName VARCHAR(50) NOT NULL DEFAULT '',
    lastName VARCHAR(50) NOT NULL DEFAULT '',
    expiration DATE NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE ratings(
    movieId VARCHAR(10) NOT NULL DEFAULT '' REFERENCES movies(id),
    rating FLOAT NOT NULL,
    numVotes INT NOT NULL
    );
