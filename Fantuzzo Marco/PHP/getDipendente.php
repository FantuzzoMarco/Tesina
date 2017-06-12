<?php
  require_once __DIR__. "/Utils.php";
  $idDipendente = Utils::check_login();

  $conn=Utils::getDBConnection();
  $stmt = $conn->prepare("SELECT username, CF, nome, cognome from Dipendente where id = ?");
  $stmt->bind_param("i", $idDipendente);
  $stmt->execute();
  $result = $stmt->get_result();

  $row = $result->fetch_assoc();
  if($row){
    header("Content-type:application/json");
    echo json_encode($row);
  } else{
    http_response_code(500);
  }
