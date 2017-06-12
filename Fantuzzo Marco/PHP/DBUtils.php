<<?php

/**
 *
 */
class DBUtils
{

  private function __construct() {}

  public static function getDBConnection(){
    $conn = new mysqli("localhost", "pitteri", "abcd");
    if ($conn->connect_error){
      http_response_code(500);
      exit();
    }
    return $conn;
  }
}
