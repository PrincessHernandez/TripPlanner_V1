<?php
$servername = "localhost";
$username = "justlik6_travel";
$password = " ";
$dbname = "justlik6_trippie";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$lat = 0;
$lon = 0;

if($_POST['lat'] == 'NULL' || $_POST['lon'] == 'NULL'){
    $sql = "SELECT * FROM data ORDER BY data_id DESC LIMIT 1";
	$result = mysqli_query($conn, $sql);

	if ($result->num_rows > 0) {
		while($row = $result->fetch_assoc()) {
			$lat = $row["lat"];
			$lon = $row["lng"];
		}
	} else {
		echo "0 results";
	}
}else{
	$lat = $_POST['lat'];
	$lon = $_POST['lon'];
}

$sql = "INSERT INTO data (lat, lng, temp) VALUES ('".$lat."', '".$lon."', '".$_POST['temp']."')";
echo $sql;

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>