<?php

  const SPECIAL_PASSWORD = "pitteri";


  if(isset($_POST["nome"], $_POST["cognome"], $_POST["CF"], $_POST["password"], $_POST["username"], $_POST["special_password"])){

    if($_POST["special_password"] === SPECIAL_PASSWORD){
      require_once __DIR__."/Utils.php";

      $password =  password_hash($_POST["password"], PASSWORD_DEFAULT);

      $conn = Utils::getDBConnection();
      $stmt = $conn->prepare("INSERT INTO Dipendente (CF, nome, cognome, username, password) VALUES (?, ?, ?, ?, ?)");
      $stmt->bind_param("sssss",  $_POST["CF"],  $_POST["nome"],  $_POST["cognome"],  $_POST["username"], $password);
      if(!$stmt->execute()) {
        http_response_code(409);
      }
    } else {
      http_response_code(403);
    }
  } else {
    http_response_code(400);
  }
