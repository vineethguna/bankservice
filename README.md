# Bank Service

Bank Service provides a REST API server which simulates a bank. It exposes the following operations
* Authentication and authorisation of admin and normal users using [JWT Tokens](https://jwt.io/)
* Addition of new users by admin
* Querying user details
* Get balance of user
* Add beneficiary for user
* Delete beneficiary for user
* List beneficiaries for user
* Transfer funds to beneficiaries for a user
* Get all transactions for a user
* Get future amount of balance for a particular interest rate and time

## Api Documentation and Testing

This project is integrated with [Swagger](https://swagger.io/) to provide API documentation and testing

To explore API and test
* Start the server by following the steps below
* Visit `http://localhost:8888/swagger-ui.html`

## Prerequisites

* Java 8
* Docker
* MongoDB

## Running the Server

The project is integrated with [maven](https://maven.apache.org/) and [docker](https://www.docker.com/)

To start the server just execute the below command

```
docker-compose pull
docker-compose up -d
```
The above command does the following

* Starts bank service server in a docker container
* Starts bank service mongo database in another docker container
* It Links both the containers

Once the above step is executed you can access REST API's of bank service at `http://localhost:8888`


To stop the server just execute the below command
```
docker-compose stop
```

## Running the tests

Explain how to run the automated tests for this system

## Workflow to follow for executing the APIS

* Authenticate as admin user with default username `admin` and default password `admin12`, it returns an access token to use for other operations
* Create a multiple users by using accessToken from step 1
* Authenticate as normal user by provided username and password in step2, it returns an access token to use for other operations
* Add beneficiary
* Transfer funds to the beneficiary
* Check balance for the current user after fund transfer

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Web Framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - For Deployment
* [Jetty](https://www.eclipse.org/jetty/) - Web Server
* [MongoDB](https://www.mongodb.com/) - Database for storing data
* [JJWT](https://github.com/jwtk/jjwt) - JSON Web Token Library by Okta
* [Springfox Swagger 2](https://springfox.github.io/springfox/docs/current/#springfox-swagger-ui) - API Documentation and testing
* [Junit](https://junit.org/junit4/) - Test framework used