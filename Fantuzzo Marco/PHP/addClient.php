<?php
  require_once __DIR__. "/Utils.php";
  $id_dipendente = Utils::check_login();

  if (isset($_GET["nome"], $_GET["indirizzo"], $_GET["PIVA"])) {
    $nome = $_GET["nome"];
    $indirizzo = $_GET["indirizzo"];
    $PIVA = $_GET["PIVA"];

    $conn=Utils::getDBConnection();
    $stmt = $conn->prepare("INSERT INTO Cliente (nome, indirizzo, PIVA, id_dipendente) values (?, ?, ?, ?)");

    $stmt->bind_param("sssi", $nome, $indirizzo, $PIVA, $id_dipendente);
    if(!$stmt->execute()) {
      http_response_code(500);
    } else {
      echo json_encode([
        "nome"      => $nome,
        "indirizzo" => $indirizzo,
        "PIVA"      => $PIVA,
        "id"        => $conn->insert_id
      ]);
    }

  } else {
    http_response_code(400);
  }
