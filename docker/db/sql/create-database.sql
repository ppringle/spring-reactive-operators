CREATE ROLE orders WITH LOGIN PASSWORD 'orders' ;
ALTER ROLE orders CREATEDB ;
CREATE DATABASE orders;