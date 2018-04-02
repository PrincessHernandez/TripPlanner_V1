<?php
$servername = "localhost";
$username = "justlik6_travel";
$password = " ";
$dbname = "justlik6_trippie";

$sql = "SELECT * FROM data ORDER BY data_id DESC LIMIT 1";

$con = mysqli_connect($servername, $username, $password, $dbname);

$result = mysqli_query($con, $sql);

$response = array();

while($row = mysqli_fetch_array($result)){
	array_push($response, array("id"=>$row[0], "lat"=>$row[1], "lon"=>$row[2], "temp"=>$row[3], "time_stamp"=>$row[4]));
}

echo json_encode(array("TripPi_response"=>$response));
?>
