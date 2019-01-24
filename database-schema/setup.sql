-- To be run in psql
-- e.g. psql -U myDatabaseUsername -***REMOVED*** -d myDatabaseName -f setup.sql -h myHostName
CREATE USER developer ***REMOVED***;
CREATE DATABASE bb_dev OWNER developer;
\connect bb_dev
CREATE SCHEMA payment AUTHORIZATION developer;