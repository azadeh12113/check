[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=azadeh12113_check&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=azadeh12113_check)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=azadeh12113_check&metric=coverage)](https://sonarcloud.io/summary/new_code?id=azadeh12113_check)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=azadeh12113_check&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=azadeh12113_check)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=azadeh12113_check&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=azadeh12113_check)
![Coverage Status](https://coveralls.io/repos/github/azadeh12113/check/badge.svg?branch=main&v=2)

Introduction
Artwork Project is a Spring Boot application designed as a simple interface for participants in an art competition to submit their artwork. In this application, an artist can enter their name and the name of their artwork. A judge can then review the submitted entries, compare them, and assign scores.
This project is only a simplified model of the idea. It is not intended to represent a complete or realistic real-world system. Instead, it serves as a basic example that can be used to demonstrate the concept and to implement and test its functionality.
The following section presents how the application starts and how the pages are connected. What is shown here, is only a simple structure and a general overview of the application flow.
The application begins with a start page that contains two buttons: Judge and Artist. The user can choose either option and will then be redirected to the corresponding page.
If the user chooses Artist, they are directed to the artist page, where they can enter their name and the name of their artwork, and then submit this information to the system.
If the user chooses Judge, they are directed to the judge page, where they can view a list of artists and their artwork names. The judge can then assign a score to each artwork, with scores ranging from 0 to 3.
This structure provides a clear and simple overview of the main functions of the application.
There is also a feature for the judge to view the entries that received the highest score. In this state, the system displays a list of artworks that were assigned 3 points, which is the maximum score.( http://localhost:8080/artists/highestScore)

Technologies and frameworks used in the application
The project was implemented with the following technologies:
1.	Java 17
•	The application uses Java 17.

2.	Spring Boot
•	Spring Boot is the main application framework.

3.	Thymeleaf
•	The web interface is implemented through:
•	Spring MVC controllers;
•	Thymeleaf HTML templates.

4.	Spring Data JPA

5.	MySQL and H2
•	The application uses:
•	MySQL for the main runtime configuration and E2E test.
•	H2 in-memory database for test configurations.

6.	Maven
•	Maven is used for build automation and dependency management. 

7.	Docker and Docker Compose
•	A Dockerfile for running the Spring Boot application.

•	a docker-compose.yml file to start both the application and a MySQL database.

8.	Testing libraries

•	The test suite uses several tools:
o	JUnit;
o	Spring Boot Test;
o	AssertJ;
o	Rest Assured;
o	HtmlUnit;
o	Selenium-related dependencies;

9.	 Mockwire
•	WireMock dependency for mocking-related scenarios.
