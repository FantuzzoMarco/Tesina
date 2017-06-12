<?php
  require_once __DIR__. "/Utils.php";
  Utils::check_login();

  if (isset($_GET["cliente"])) {
    $cliente = $_GET["cliente"];
    $conn=Utils::getDBConnection();
    $stmt = $conn->prepare("SELECT id, data, dipendente, prezzo, n_ore AS numeroOre, descrizione from Lavoro where cliente = ? ORDER BY data DESC");
    $stmt->bind_param("i", $cliente);
    $stmt->execute();
    $result = $stmt->get_result();

    $ret=[];
    while($row=$result->fetch_assoc()){
      $ret[] = $row;
    }
    header("Content-type:application/json");
    echo json_encode($ret);
  } else {
    http_response_code(400);
  }
