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