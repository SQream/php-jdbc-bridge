# PHP-JDBC Bridge

Fork from [JCotton](https://github.com/JCotton1123/php-jdbc-bridge)'s bridge.

This is a java ServerSocket that can be operated from PHP code. The java code than performs JDBC operations, and returns results to the calling PHP code.

## Requirements

* Java 1.8+
* PHP 5.3+
* A JDBC driver

## Quick start for SQream users

- Clone this repo and go to it's folder:

`git clone https://github.com/SQream/php-jdbc-bridge && cd php-jdbc-bridge`

- Build the java bridge file, which also downloads a dependency (`apache-commons`):

`cd java && ./build.sh && cd ..`

- Copy the 2 jar files created above to the repo root folder:

` cp java/lib/pjbridge.jar java/lib/commons-daemon-1.2.2.jar . `

- Copy SQream JDBC jar to the same location:

` cp /path/to/sqream-jdbc-4.2.1.jar .`

- Start the bridge to allow calling JDBC from PHP code:

``` java -cp .:sqream-jdbc-4.1.jar:pjbridge.jar:commons-daemon-1.0.15.jar Server com.sqream.jdbc.SQDriver 4444 ```

- Run the complementary sample check (Assuming SQreamd is up on the local machine):

`php check.php`

- Bonus - to install a recent stable version of php on ubuntu:

```
sudo apt install software-properties-common
sudo add-apt-repository ppa:ondrej/php
sudo apt update
sudo apt install php7.4
```



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
