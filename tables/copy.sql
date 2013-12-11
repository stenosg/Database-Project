

--
-- Load Table          : address
-- Hasdata: true
--
COPY address (
    street,
    city,
    state,
    zip)
FROM '../data/address.dat'
WITH DELIMITER  ',';

COPY language (
    description)
FROM '../data/language.dat'
WITH DELIMITER  ',';

COPY trip_status (
    description)
FROM '../data/trip_status.dat'
WITH DELIMITER  ',';

COPY trip_type (
    description)
FROM '../data/trip_type.dat'
WITH DELIMITER  ',';

COPY destination (
    description,
    travel_method)
FROM '../data/destination.dat'
WITH DELIMITER  ',';

--
-- Load Table     : salary_rate
-- Hasdata: true
--
COPY salary_rate (
    description,
    hourly_rate,
    commission_rate)
FROM '../data/salary_rate.dat'
WITH DELIMITER ',';

--
-- Load Table	: employee
-- Hasdata: true
--
COPY employee (
    password,
    first_name,
    last_name,
    ssn,
    email,
    phone,
    user_name,
    addressid,
    salary_rateid)
FROM '../data/employee.dat'
WITH DELIMITER ',';

--
-- Table			: broker
-- Hasdata: true
--
COPY broker(
	employeeid,
	specialty)
FROM '../data/broker.dat'
WITH DELIMITER ',';

--
-- Table			: agent
-- Hasdata: true
--
COPY agent(
	employeeid,
	comment)
FROM '../data/agent.dat'
WITH DELIMITER ',';

--
-- Table			: manager
-- Hasdata: true
--
COPY manager(
	employeeid,
	comment)
FROM '../data/manager.dat'
WITH DELIMITER ',';

--
-- Table              : 		attendance
-- Hasdata: true
--
COPY attendance (
    check_in,
    check_out,
    employeeid)
FROM '../data/attendance.dat'
WITH DELIMITER ',';

--
-- Table              : 	payroll_log
-- Hasdata: true
--
COPY payroll_log (
    date,
    amount,
    employeeid)
FROM '../data/payroll_log.dat'
WITH DELIMITER ',';

--
-- Load Table          : customer
-- Hasdata: true
--
COPY customer (
    password,
    first_name,
    last_name,
    email,
    phone,
    addressid,
    user_name)
FROM '../data/customer.dat'
WITH DELIMITER ',';

COPY speaks (
    employeeid,
    languageid)
FROM '../data/speaks.dat'
WITH DELIMITER  ',';

COPY travel_order (
    creation_date,
    group_size,
    price,
    comission,
    trip_date_start,
    trip_date_end,
    agentid,
    customerid,
    trip_statusid)
FROM '../data/travel_order.dat'
WITH DELIMITER  ',';

COPY trip_package (
    name,
    description,
    price,
    trip_length,
    availability,
    brokerid,
    supervisorid,
    typeid)
FROM '../data/trip_package.dat'
WITH DELIMITER  ',';

COPY trip_destination (
    trip_packageid,
    destinationid)
FROM '../data/trip_destination.dat'
WITH DELIMITER  ',';

COPY order_packages (
    packageid,
    orderid)
FROM '../data/order_packages.dat'
WITH DELIMITER  ',';
