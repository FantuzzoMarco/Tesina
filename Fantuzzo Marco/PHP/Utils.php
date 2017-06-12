<?php

class Utils
{

  const DATABASE_HOST = "localhost";
  const DATABASE_USERNAME = "";
  const DATABASE_PASSWORD = "";
  const DATABASE_NAME = "my_pitteriapp";


  private function __construct() {}

  public static function getDBConnection(){
    $conn = new mysqli(self::DATABASE_HOST, self::DATABASE_USERNAME, self::DATABASE_PASSWORD, self::DATABASE_NAME);
    if ($conn->connect_error){
      http_response_code(500);
      exit();
    }
    return $conn;
  }

  public static function check_login() {
    if(isset($_POST["login"],$_POST["password"])) {
      $login = $_POST["login"];

      $conn=self::getDBConnection();
      $stmt = $conn->prepare("SELECT id,password from Dipendente where username = ?");
      $stmt->bind_param("s", $login);
      $stmt->execute();
      $result = $stmt->get_result();
      if($result === false) {
        http_response_code(500);
        exit();
      }

      if($row=$result->fetch_assoc()) {
        $password=$row["password"];
        if(!password_verify($_POST["password"], $password)){
          http_response_code(403);
          exit();
        } else {
          return $row['id'];
        }
      } else {
        http_response_code(403);
        exit();
      }
    } else {
      http_response_code(400);
      exit();
    }
  }
}
