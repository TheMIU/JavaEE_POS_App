create database if not exists testdb;

use testdb;

create table if not exists customer
(
    cusID varchar(10) not null
    primary key,
    cusName varchar(50) null,
    cusAddress varchar(100) null
    );

INSERT INTO customer (cusID, cusName, cusAddress)
VALUES
    ('C00-001', 'Sachin Thamalsha', 'Matara'),
    ('C00-002', 'Ranjith Perera', 'Panadura'),
    ('C00-003', 'Kamal Bandu', 'Galle'),
    ('C00-004', 'Kavindu Perera', 'Panadura');


create table if not exists item
(
    code varchar(10) not null
    primary key,
    itemName varchar(50) null,
    qty int null,
    unitPrice decimal(10,2) null
    );

INSERT INTO item (code, itemName, qty, unitPrice)
VALUES
    ('I00-001', 'Lux', 100, 145.00),
    ('I00-002', 'Sunlight', 150, 345.00),
    ('I00-003', 'Lifebuoy', 400, 245.00),
    ('I00-004', 'Keerisamba', 50, 500.00);



CREATE TABLE orders (
                        orderID VARCHAR(10) PRIMARY KEY,
                        date DATE,
                        customerID VARCHAR(10),
                        discount INT,
                        total DECIMAL(10, 2)
);

CREATE TABLE order_items (
                             orderID VARCHAR(10),
                             itemID VARCHAR(10),
                             qty INT,
                             PRIMARY KEY (orderID, itemID)
);


INSERT INTO orders (orderID, date, customerID, discount, total)
VALUES
    ('O00-001', '2023-02-05', 1, 10, 1000),
    ('O00-002', '2023-02-07', 2, 10, 1000);

INSERT INTO order_items (orderID, itemID, qty)
VALUES
    ('O00-001', 1, 2),
    ('O00-001', 2, 1),
    ('O00-002', 3, 1),
    ('O00-002', 2, 1),
    ('O00-002', 1, 1);


insert into orders (orderID, "date", customerID, discount, total)
values ();
insert into order_items (orderID, itemID, qty)
values ();