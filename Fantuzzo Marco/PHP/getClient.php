<?php
  require_once __DIR__. "/Utils.php";
  Utils::check_login();

  if (isset($_GET["query"])) {
    $query = $_GET["query"];
    $conn=Utils::getDBConnection();
    $stmt = $conn->prepare("SELECT id,PIVA,nome,indirizzo from Cliente where PIVA = ? OR nome LIKE ?");
    $query2 = "%$query%";
    $stmt->bind_param("ss", $query, $query2);
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
