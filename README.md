# Spring Boot Service

[![Build Status](https://travis-ci.org/apycazo/spring-boot-service.svg?branch=master)](https://travis-ci.org/apycazo/spring-boot-service)

An attempt on a simple but complete spring boot based rest service. This service includes the following:

* An embedded apache derby database as persistence.
* A spring security filter, stateless and based on JWT tokens.
* Simple filter working as a rate limiter, based on the token-bucket algorithm.

TBD:

* Include an embedded web application for the service.
* Include optional cloud embedding mechanisms (eureka, cloud config).
* Create simple service to work as a demo.
* Document each step properly.