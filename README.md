
# Spring RentalCar

Back-end Spring boot project of a simple rental car application.

## Table of Contents

 - [General Info](#general-info)
 - [Introduction](#introduction)
 - [Technologies](#technologies)
 - [Setup](#setup)
 - [Database Schema](#database-schema)
 - [Swagger](#swagger)
 - [If Swagger is not enough](#api-reference)
 - [Links](#-links)
 

## General Info

A back-end project made of three entities: vehicles, users, bookings. Each user can rent a vehicles for a period of time. Some users are 'SUPERUSER' and they are the admin. They can insert, update, delete each data in the database. Other users are 'CUSTOMER'. An user cannot rent a car in the period he has rented another car. When an user rent a car the request must be approved first before renting another one. Only 'SUPERUSER' can approve the bookings.
## Introduction

The goal of this project is to provide a small-scale demonstration of how REST architectural style are structured, offering an overview of [Spring Boot](https://spring.io/projects/spring-boot), [REST API](https://it.wikipedia.org/wiki/Representational_state_transfer), [JWT authentication](https://www.jwt.io/introduction), [Docker](https://docs.docker.com/manuals/), and [Swagger](https://swagger.io/). The project uses a database spun up via the images provided by the MySQL Docker Hub. 

Note: since this is for educational purposes, the database is not persistent. When the container is removed, all data will be lost. For the fron-end part please take a look one of this two repo: [react-front-end](https://github.com/LVisir/react-rental-car), [angular-front-end](https://github.com/LVisir/angular-rental-car).
## Technologies
- Spring 2.6.3
- Java 17
- MySQL 8
- Docker
## Setup
Easy to use, you just need **docker** installed and ports 8091, 3306 free (one for spring boot and one for mysql respectively).

```
git clone https://github.com/LVisir/spring-rental-car.git
cd spring-rental-car
./deploy.sh
```
Don't panic if some errors pops up, just wait. When the log will say "Started RentalCarApplication" it means that is ready to use:

![](./readme_pictures/app_started.png)

Check [http://localhost:8091/swagger-ui/index.html](http://localhost:8091/swagger-ui/index.html) to see the API exposed. Check the [Swagger](#swagger) chapter to have more info on how the app works. Check the repo x to try it out with the front-end integrated.

The script will build the app based on what is written inside the **docker-compose.yml** file. First, it will create an instance of mysql; inside, it will automatically create the database schema filled with some data to instantly try it out thanks on what is inside the folder ```initdb```. The ```SUPERUSER``` created will be accessible via ```test@gmail.com``` as email and ```1234``` as password (Check the [Swagger](#swagger) chapter to have more info on how the app works or use the repo x to try it out with the front-end integrated). Secondly, it will build and run the spring boot app.

To clean the docker containers and images generated from the ```./deploy.sh``` script just run ```./clean_deploy.sh```.
## Database schema

![](./readme_pictures/diagram%20e_r%20final.png)
## Swagger
API documentation in [http://localhost:8091/swagger-ui/index.html](http://localhost:8091/swagger-ui/index.html).

All the API could be reached only with the token. Try the ```/login``` with ```test@gmail.com``` as email and ```1234``` as password. With the access token obtained authorize the user. **Note:** The token in this app is not called ```Bearer``` but ```LoginToken```:
![](./readme_pictures/swagger_authorize.png)

Example of a curl to better understand the structure of a request:
```
curl -X 'GET' \
  'http://localhost:8091/vehicles' \
  -H 'accept: application/json' \
  -H 'Authorization: LoginToken eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYXJsb0BnbWFpbC5jb20iLCJyb2xlcyI6WyJTVVBFUlVTRVIiXX0.dWcLRKOCefdoqfgF44_qQU6ESd_rgfMWgOzEcbf_FX8'
```

SUPERUSER credentials:
```
test@gmail.com
```
```
1234
```

CUSTOMER credentials:
```
caccamo@gmail.com
```
```
1234
```

## If Swagger is not enough

#### Login (ANYONE)

```http
  POST /login
```

| Header Parameter | Value     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `Content-type`      | `application/x-www-form-urlencoded` | It tells which kind of data is sent in a single HTTP message body |

| Body Parameter | Value     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `email`      | `test@gmail.com` | The email of the SUPERUSER **\***|
| `password`      | `1234` | The password of the SUPERUSER **\***|

The returned value is the **access_token**, that represent the alphanumeric code needed to send any other request.

## API Reference: USER

Every request must have a specified header telling the db wich user is calling  (**access_token**). Near to the API call it is written the role that has that permission and no role it means that every role can request that API.

### SUPERUSER, CUSTOMER

| Header Parameter | Value     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `Authorization`      | `LoginToken `**access_token** | The custom header that a user must have to make any requests |

#### Get all users (SUPERUSER)

```http
  GET /users
```

#### Get user (SUPERUSER)

```http
  GET /users/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to fetch |

#### Insert user (SUPERUSER)

```http
  POST /users/addUser
```

| Body | Description                       |
| :-------- | :-------------------------------- |
| `user`   | **Required**. Json format of the user entity |

#### Insert user with role CUSTOMER (SUPERUSER)

```http
  POST /users/customers/add
```

| Body |  Description                       |
| :--------  | :-------------------------------- |
| `user`       | **Required**. Json format of the user |

#### Delete user

```http
  DELETE /users/deleteUser/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to delete |

#### Delete user with role CUSTOMER (SUPERUSER)

```http
  DELETE /users/customers/delete/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of the user to delete |

#### Update user

```http
  PUT /users/update/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to update |

| Body | Description                       |
| :-------- | :-------------------------------- |
| `user`   | **Required**. Json format of the user entity |

#### Update user with role CUSTOMER

```http
  PUT /users/customers/update/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to update |

| Body | Description                       |
| :-------- | :-------------------------------- |
| `user`   | **Required**. Json format of the user entity |

#### Get users sorted at a certain page (SUPERUSER)

```http
  GET /users/customers/paging/sortBy
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `_page`      | `string` | **Required**. Offset |
| `_limit`      | `string` | **Required**. Number of users per page |
| `_sort`      | `string` | **Required**. List of fields to sort by |
| `_order`      | `string` | **Required**. Ascendant or Descendant: ASC, DESC |

#### Get users with role CUSTOMER (SUPERUSER)

```http
  GET /users/customers
```

#### Get user with role CUSTOMER

```http
  GET /users/customers/id/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to get |

#### Get user with role CUSTOMER by his email

```http
  GET /users/customers/email/${email}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `email`      | `string` | **Required**. Email of user to get |

#### Get user by his email

```http
  GET /users/email/${email}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `email`      | `string` | **Required**. Email of user to get |

#### Get users at a certain page

```http
  GET /users/customers/paging
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `_page`      | `string` | **Required**. Offset |
| `_limit`      | `string` | **Required**. Number of users for page |

#### Get a certain number of users at a certain page by a certain field

```http
  GET /users/customers/search
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `_page`      | `string` | **Required**. Offset |
| `_limit`      | `string` | **Required**. Number of users for page |
| `_field`      | `string` | **Required**. Attribute to search by |
| `_value`      | `string` | **Required**. Value of the attribute |

#### Get a certain number of users that match a certain field value at a certain page sorted by a certain fields order

```http
  GET /users/customers/search/sort
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `_page`      | `string` | **Required**. Offset |
| `_limit`      | `string` | **Required**. Number of users for page |
| `_field`      | `string` | **Required**. Attribute to search by |
| `_value`      | `string` | **Required**. Value of the attribute |
| `_fields`      | `string` | **Required**. Sort priority by this fields |
| `_order`      | `string` | **Required**. Ascendant or Descendant: ASC, DESC |

## API Reference: VEHICLE

Every request must have a specified header telling the db wich user is calling  (**access_token**). Near to the API call it is written the role that has that permission and no role it means that every role can request that API.

#### Get all vehicles

```http
  GET /vehicles
```

#### Get vehicle

```http
  GET /vehicles/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of vehicle to fetch |

#### Update vehicle (SUPERUSER)

```http
  UPDATE /vehicles/update/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of vehicle to update |

| Body      | Description                       |
| :--------  | :-------------------------------- |
| `vehicle` | **Required**. Json format of the vehicle entity |

#### Delete vehicle (SUPERUSER)

```http
  DELETE /vehicles/delete/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of vehicle to delete |

#### Insert vehicle (SUPERUSER)

```http
  POST /vehicles/add
```

| Body     | Description                       |
| :-------- | :-------------------------------- |
| `vehicle`       | **Required**. Json format of vehicle entity |

#### Get last booked date of a vehicle of a certain user

```http
  GET /vehicles/lastBooking
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of vehicle to fetch |
| `id`      | `string` | **Required**. Id of user to fetch |

#### Get vehicle searched by

```http
  GET /vehicles/search
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `field`      | `string` | **Required**. The attribute search by |
| `value`      | `string` | **Required**. The value of the attribute |

## API Reference: BOOKING

Every request must have a specified header telling the db wich user is calling  (**access_token**). Near to the API call it is written the role that has that permission and no role it means that every role can request that API.

#### Get all bookings (SUPERUSER)

```http
  GET /bookings
```

#### Get booking

```http
  GET /bookings/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of booking to fetch |

#### Insert booking

```http
  POST /bookings/add
```

| Body     | Description                       |
| :-------- | :-------------------------------- |
| `booking`      | **Required**. Json format of the booking entity |

#### Update booking

```http
  PUT /bookings/update/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of booking to fetch |

| Body     | Description                       |
| :-------- | :-------------------------------- |
| `booking`      | **Required**. Json format of the booking entity |

#### Delete booking

```http
  DELETE /bookings/delete/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of booking to delete |

#### Get booking of an user with CUSTOMER role

```http
  GET /bookings/customers/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to fetch |

#### Get booking from a certain email of an user with CUSTOMER role

```http
  GET /bookings/customers/email/${email}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `email`      | `string` | **Required**. Email of user to fetch |

#### Get booking searched by

```http
  GET /bookings/search
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `field`      | `string` | **Required**. The attribute search by |
| `value`      | `string` | **Required**. The value of the attribute |

#### Get booking of a certain user with CUSTOMEr role searched by

```http
  GET /customers/${id}/search
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `field`      | `string` | **Required**. The attribute search by |
| `value`      | `string` | **Required**. The value of the attribute |
| `id`      | `string` | **Required**. Id of user to fetch |






## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://github.com/LVisir)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/edoardo-mariani-2903a5262/)

