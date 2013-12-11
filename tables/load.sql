--
-- Create Tables
-- =======================================================
-- Project : Travel4u Database Project
--
-- CS 166: Database Management Systems (Fall 2009)
-- Department of Computer Science & Engineering
-- University of California - Riverside
-- 
-- Target DBMS: 'Postgres' 
--

--
-- Table         : address
-- Description   : 
-- addressid     : address autonumber
-- street        : e.g. 3500 Canyon Crest Dr Apt#891
-- city          : e.g. Riverside
-- state         : e.g. CA
-- zip           : e.g. 92507
--
CREATE TABLE address (
    addressid       serial NOT NULL,
    street          TEXT NOT NULL,
    city            TEXT NOT NULL,
    state           CHAR(2) NOT NULL,
    zip             INTEGER,
    PRIMARY KEY (addressid));

--
-- Table          : salary_rate
-- Description    :
-- salary_rateid  : salary rate autonumber
-- description    : e.g. Waiter II
-- hourly_rate    : e.g. 20.00
-- bonus          : e.g. 0.20
--
CREATE TABLE salary_rate (
    salary_rateid     serial NOT NULL,
    description       TEXT NOT NULL,
    hourly_rate       NUMERIC(6,2) NOT NULL,
    commission_rate   NUMERIC(3,3) NOT NULL,
    PRIMARY KEY (salary_rateid));

--
-- Table          : trip_type
-- Description    : the destination for the dishes
-- catalogid      : trip_type autonumber
-- description    : e.g. cruise
--
CREATE TABLE trip_type(
    trip_typeid        serial NOT NULL,
    description        TEXT NOT NULL,
    PRIMARY KEY (trip_typeid));

--
-- Table          : destination
-- tableid        : destination autonumber
-- description    : e.g. Beijing
-- travel_method  : e.g. plane
--
CREATE TABLE destination(
    destinationid  serial NOT NULL,
    description    TEXT NOT NULL,
    travel_method  TEXT NOT NULL,
    PRIMARY KEY (destinationid));

--
-- Table             : trip_status
-- Description       :
-- statusid          : trip status autonumber
-- description       : e.g. created
--
CREATE TABLE trip_status(
    statusid       serial NOT NULL,
    description    TEXT NOT NULL,
    PRIMARY KEY (statusid));

--
-- Table        : Language
-- Description  :
-- languageid   : language autonumber
-- description  : e.g. english
--
CREATE TABLE language (
    languageid     serial NOT NULL,
    description    TEXT NOT NULL,
    PRIMARY KEY (languageid));

--
-- Table            : customer
-- Description      :
-- customerid       : customer autonumber
-- password            : 
-- fisrt_name       : e.g. John
-- last_name        : e.g. Smith
-- email            : e.g. smith@hatmail.com
-- phone            : e.g. 909-369-7317
-- user_name        : e.g. johnsmith
-- addressid        : address autonumber (Foreign Key references address.addressid)
--
CREATE TABLE customer (
    customerid      serial NOT NULL,
    password        TEXT NOT NULL,
    first_name      TEXT NOT NULL,
    last_name       TEXT NOT NULL,
    email           TEXT,
    phone           CHAR(12),
    user_name       TEXT NOT NULL,
    addressid       INTEGER NOT NULL,
    PRIMARY KEY (customerid),
    FOREIGN KEY (addressid) REFERENCES address (addressid));

--
-- Table            : employee
-- Description      :
-- employeeid       : employee autonumber
-- password         :
-- first_name       : e.g. Carl
-- last_name        : e.g. Jameson
-- ssn              : e.g. 234-43-5454
-- email            : e.g. jameson@hatmail.com
-- phone            : e.g. 909-369-7317
-- user_name	    : e.g. cjameson
-- addressid        : address autonumber (Foreign Key references address.addressid)
-- salary_rateid    : salary rate autonumber (Foreign Key references salary_rate.salary_rateid)
--
CREATE TABLE employee (
    employeeid      serial NOT NULL,
    password        TEXT NOT NULL,
    first_name      TEXT NOT NULL,
    last_name       TEXT NOT NULL,
    ssn             CHAR(11) NOT NULL,
    email           TEXT,
    phone           CHAR(12),
    user_name       TEXT NOT NULL,
    addressid       INTEGER NOT NULL,
    salary_rateid   INTEGER NOT NULL,
    PRIMARY KEY (employeeid),
    FOREIGN KEY (addressid) REFERENCES address (addressid),
    FOREIGN KEY (salary_rateid) REFERENCES salary_rate (salary_rateid));

--
-- Table            : agent
-- Description      :
-- employeeid       : employee autonumber (Foreign Key references employee.employeeid)
-- comment          :
--
CREATE TABLE agent(
    employeeid      INTEGER NOT NULL,
    comment        TEXT NOT NULL,
    PRIMARY KEY (employeeid),
    FOREIGN KEY (employeeid) REFERENCES employee (employeeid));

--
-- Table            : broker
-- Description      :
-- employeeid       : employee autonumber (Foreign Key references employee.employeeid)
-- specialty        : e.g. Asia
--
CREATE TABLE broker(
    employeeid       INTEGER NOT NULL,
    specialty        TEXT NOT NULL,
    PRIMARY KEY (employeeid),
    FOREIGN KEY (employeeid) REFERENCES employee (employeeid));

--
-- Table            : manager
-- Description      :
-- employeeid       : employee autonumber (Foreign Key references employee.employeeid)
-- comment          : 
--
CREATE TABLE manager(
    employeeid         INTEGER NOT NULL,
    comment            TEXT NOT NULL,
    PRIMARY KEY (employeeid),
    FOREIGN KEY (employeeid) REFERENCES employee (employeeid));

--
-- Table            : speaks
-- Description      :
-- employeeid       : employee autonumber (Foreign Key references employee.employeeid)
-- languageid       : language autonumber (Foreign key references language.languageid) 
--
CREATE TABLE speaks(
    employeeid         INTEGER NOT NULL,
    languageid         INTEGER NOT NULL,
    PRIMARY KEY (employeeid, languageid),
    FOREIGN KEY (employeeid) REFERENCES agent (employeeid),
    FOREIGN KEY (languageid) REFERENCES language (languageid));

--
-- Table              : attendance
-- Description        :
-- attendanceid       : attendance autattendanceonumber
-- check_in           : e.g. '2008-11-5 11:00:00.00' arrival time
-- check_out          : e.g. '2008-11-5 15:30:00.00' time left
-- employeeid         : employee autonumber (Foreign Key references employee.employeeid)
--
CREATE TABLE attendance (
    attendanceid      serial NOT NULL,
    check_in          TIMESTAMP DEFAULT now() NOT NULL,
    check_out         TIMESTAMP DEFAULT now() NOT NULL,
    employeeid        INTEGER NOT NULL,
    PRIMARY KEY (attendanceid),
    FOREIGN KEY (employeeid) REFERENCES employee (employeeid));

--
-- Table              : payroll_log
-- Description        :
-- payrollid          : payroll autonumber
-- date_paid          : e.g. 2008-11-5 17:00:00.00
-- amount             : e.g. 8534.32
-- employeeid         : system autonumber (Foreign Key references employee.employeeid)
--
CREATE TABLE payroll_log (
    payrollid       serial NOT NULL,
    date            TIMESTAMP DEFAULT now() NOT NULL,
    amount          NUMERIC(6,2) NOT NULL,
    employeeid      INTEGER NOT NULL,
    PRIMARY KEY (payrollid),
    FOREIGN KEY (employeeid) REFERENCES employee (employeeid));

--
-- Table            : tavel_order
-- Description      :
-- orderid          : order autonumber
-- creation_date    : e.g. '2008-11-5 12:06:00' when the order is created
-- group_size       : the number of cutomers e.g. 6
-- price            : e.g. 123.45
-- tips             : e.g. 20.00
-- trip_date_start  : e.g. '2008-11-5 12:06:00' when the trip starts
-- trip_date_end    : e.g. '2008-11-5 12:06:00' when the trip ends
-- agentid         : employee autonumber (Foreign Key references waiter.employeeid)
-- customerid       : customer autonumber (Foreign Key references customer.customerid)
-- trip_statusid   : order status autonumber (Foreign Key references trip_status.statusid)
--
CREATE TABLE travel_order(
    orderid         serial NOT NULL,
    creation_date   TIMESTAMP DEFAULT now() NOT NULL,
    group_size      INTEGER NOT NULL,
    price           NUMERIC(6, 2) NOT NULL,
    comission       NUMERIC(6, 2) NOT NULL,
    trip_date_start TIMESTAMP NOT NULL,
    trip_date_end   TIMESTAMP NOT NULL,
    agentid         INTEGER NOT NULL,
    customerid      INTEGER NOT NULL,
    trip_statusid   INTEGER NOT NULL,
    PRIMARY KEY (orderid),
    FOREIGN KEY (agentid) REFERENCES agent (employeeid),
    FOREIGN KEY (customerid) REFERENCES customer (customerid),
    FOREIGN KEY (trip_statusid) REFERENCES trip_status (statusid));

--
-- Table                : trip_package
-- Description          :
-- packageid            : trip package autonumber
--
CREATE TABLE trip_package(
    packageid           serial NOT NULL,
    name                TEXT NOT NULL,
    description         TEXT NOT NULL,
    price               NUMERIC(5, 2) NOT NULL,
    trip_length         INTEGER NOT NULL,
    availability        BOOLEAN NOT NULL,
    brokerid            INTEGER NOT NULL,
    supervisorid        INTEGER NOT NULL,
    typeid              INTEGER NOT NULL,
    PRIMARY KEY (packageid),
    FOREIGN KEY (brokerid) REFERENCES broker (employeeid),
    FOREIGN KEY (supervisorid) REFERENCES manager (employeeid),
    FOREIGN KEY (typeid) REFERENCES trip_type(trip_typeid));

CREATE TABLE trip_destination(
    trip_packageid      INTEGER NOT NULL,
    destinationid       INTEGER NOT NULL,
    PRIMARY KEY (trip_packageid, destinationid),
    FOREIGN KEY (trip_packageid) REFERENCES trip_package (packageid),
    FOREIGN KEY (destinationid) REFERENCES destination (destinationid));

CREATE TABLE order_packages(
    packageid           INTEGER NOT NULL,
    orderid             INTEGER NOT NULL,
    PRIMARY KEY (packageid, orderid),
    FOREIGN KEY (packageid) REFERENCES trip_package (packageid),
    FOREIGN KEY (orderid) REFERENCES travel_order (orderid));

