/*this sql query should be appeneded to the end of "CREATE TABLE <table name> "*/

(
	seg_id BIGINT PRIMARY KEY,
	node1 varchar(25),
	node2 varchar(25),
	lat1 DOUBLE, 
	lon1 DOUBLE,
	lat2 DOUBLE, 
	lon2 DOUBLE);