
# Spring RentalCar

Back-end Spring boot project of a simple rental car application.

## Table of Contents

 - [General Info](#general-info)
 - [Introduction](#introduction)
 - [Technologies](#technologies)
 - [Setup](#setup)
 - [Database Schema](#database-schema)
 - [API Reference](#api-reference)
 - [Links](#-links)
 

## General Info

A back-end project made of three entities: vehicles, users, bookings. Each user can rent a vehicles for a period of time. Some users are 'SUPERUSER' and they are the admin. They can insert, update, delete each data in the database.
## Introduction

The goal of this project is to learn how [Spring](https://spring.io/projects/spring-boot) and [REST API](https://it.wikipedia.org/wiki/Representational_state_transfer) works. The data are fetched from a [MySQL](https://www.mysql.com) database and thanks to  [PostMan](https://www.postman.com/) the requests can be simulated like a classic client otherwise clone one of this two repo to have a local front-end: [react-front-end](https://github.com/LVisir/react-rental-car), [angular-front-end](https://github.com/LVisir/angular-rental-car).
## Technologies
- Spring 2.6.*
- Java 17
- MySQL
## Setup
You need a MySQL database called ``` rental_car_db ``` that is running on localhost:3306 (check [ application.properties](https://github.com/LVisir/spring-rental-car/blob/master/src/main/resources/application.properties) for more details and to set the username and password of your db). To insert the table in the database just copy paste exactly in the following order: 

``` 
create table vehicles(
id int auto_increment not null primary key,
license_plate varchar(7) not null unique,
manufacturer varchar(50) not null,
typology enum('SUV','MINIVAN','COMPACT')  not null default 'COMPACT',
model varchar(50) not null,
registr_year date not null
);

create table users(
id int auto_increment not null primary key,
name varchar(50) not null,
surname varchar(50) not null,
birth_date date not null,
role enum('CUSTOMER','SUPERUSER') not null default 'CUSTOMER',
email varchar(50) not null unique,
password varchar(50) not null,
cf varchar(15) not null unique
);

create table bookings(
id int auto_increment not null primary key,
start date not null,
end date not null,
user_id int not null,
approval boolean not null default 0,
vehicle_id int not null,
foreign key (user_id)
references users(id)
	on delete cascade
	on update cascade,
foreign key (vehicle_id)
references vehicles(id)
	on delete cascade
	on update cascade
);
```

Now that you have the database defined, clone the repository, install dependencies with ``` mvn install ``` and execute. The back-end is running on port 8091.

Note: because all the request are available only for ```CUSTOMER``` or for ```SUPERUSER```, they have a specified header (**access_token**). Check next sections for more details.
## Database schema

![](./diagram%20e_r%20final.png)
## API Reference

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

**\*** Because at the beginning there is no data, insert some mock users and vehicles in the MySQL db to test the API:

```
insert into users (name, surname, birth_date, role, email, password, cf)
values ('Jhon', 'McDonald', '1997-03-01', 'SUPERUSER', 'test@gmail.com', '1234', 'AAABBBCCCEEERRR');

insert into users (name, surname, birth_date, role, email, password, cf)
values ('Giani', 'Caccamo', '1956-11-15', 'CUSTOMER', 'caccamo@gmail.com', '1234', 'ZZZKKKSSSDDDWWW');

insert into vehicles (license_plate, manufacturer, typology, model, registr_year)
values ('AA123BK', 'Toyota', 'COMPACT', 'SummerMoon', '2000-01-01');
```

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

