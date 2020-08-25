<?php
    require "PJBridge.php";  // Should be in the same folder as this eample (Originally under php folder)

    $con2 = new PJBridge();
    [$dbHost, $dbPort, $dbName, $dbUser, $dbPass] = ["localhost", "5000", "master", "sqream", "sqream"];

    // Connect
    $connStr = "jdbc:Sqream://${dbHost}:${dbPort}/${dbName};user=${dbUser};password=${dbPass}";
    $result = $con2->connect($connStr, $dbUser, $dbPass);
    if(!$result){
        die("Failed to connect");
    }        

    $cursor = $con2->exec("select * from big", 10);
    while($row = $con2->fetch_array($cursor)){
        print_r($row);
    }
?>

