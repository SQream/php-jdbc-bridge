<?php
    // set_include_path(get_include_path() . PATH_SEPARATOR . $'');
    require "PJBridge.php";

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
    $cursor2 = $conn->exec("insert into test values (5), (6)");

    
    $cursor3 = $conn->exec("select * from test");
    while($row = $conn->fetch_array($cursor3)){
        print_r($row);
    }
    $conn->free_result($cursor3);
    //
?>