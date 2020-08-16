# PHP-JDBC Bridge

Fork from [JCotton](https://github.com/JCotton1123/php-jdbc-bridge)'s bridge.

The java component runs as a service which accepts socket requests from 
the PHP component allowing the transfer of request and response between PHP 
and the JDBC database. 

## Requirements

* Java 1.8+
* PHP 5.3+
* A JDBC driver

## Build (Java Service)

To build the PHP-JDBC bridge jar for the first time:

```sh
cd java
./build.sh
```

This downloads an apache-commons jar into `lib/` as well.

For consecutive builds:

``` cd java && ./quick_build.sh && cd .. ```


## Usage

### Java Service

To run the service:

```sh 
java -cp 'lib/pjbridge.jar:lib/commons-daemon-1.0.15.jar:lib/<JDBC driver>.jar Server <JDBC driver entry point> 4444
```

where the lib directory contains the php-jdbc jar, the commons-daemon jar and your JDBC driver jar.

Example for sqream, with all jars in the root folder of the repo:

``` java -cp .:sqream-jdbc-4.1.jar:pjbridge.jar:commons-daemon-1.0.15.jar Server com.sqream.jdbc.SQDriver 4444 ```



### PHP

Roundtrip example (`check.php`):

```php
<?php
    // set_include_path(get_include_path() . PATH_SEPARATOR . $'');
    require "PJBridge.php";  // Should be in the same folder as this eample (Originally under php folder)

    $dbHost = "localhost";
    $dbName = "master";
    $dbPort = "5000";
    $dbUser = "sqream";
    $dbPass = "sqream";

    $connStr = "jdbc:Sqream://${dbHost}:${dbPort}/${dbName};user=${dbUser};password=${dbPass}";
    // java -cp .:lib/sqream-jdbc-4.2.1.jar:lib/pjbridge.jar:lib/commons-daemon-1.0.15.jar Server com.sqream.jdbc.SQDriver 5000

    $conn = new PJBridge();

    // Connect
    $result = $conn->connect($connStr, $dbUser, $dbPass);
    if(!$result){
        die("Failed to connect");
    }       

    // Create table
    $cursor = $conn->exec("create or replace table test (x int)");

    // Insert   
    $cursor = $conn->exec("insert into test values (5), (6)");

    // Get data back
    $cursor = $conn->exec("select * from test");
    while($row = $conn->fetch_array($cursor)){
        print_r($row);
    }
    $conn->free_result($cursor);
?>
```
