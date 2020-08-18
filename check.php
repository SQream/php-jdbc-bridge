<?php
    require "PJBridge.php";  // Should be in the same folder as this eample (Originally under php folder)

    $conn = new PJBridge();
    [$dbHost, $dbPort, $dbName, $dbUser, $dbPass] = ["localhost", "5000", "master", "sqream", "sqream"];

    // Connect
    $connStr = "jdbc:Sqream://${dbHost}:${dbPort}/${dbName};user=${dbUser};password=${dbPass}";
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

    // Check timeout
    $cursor = $conn->exec("select sleep(10)", 3);
?>

