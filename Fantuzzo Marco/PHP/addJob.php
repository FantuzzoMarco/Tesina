<?php
  require_once __DIR__. "/Utils.php";
  $id_dipendente = Utils::check_login();

  if (isset($_GET["data"], $_GET["numeroOre"], $_GET["prezzo"], $_GET["cliente"], $_GET["descrizione"])) {
    $data = $_GET["data"];
    $numeroOre = $_GET["numeroOre"];
    $prezzo = $_GET["prezzo"];
    $cliente = $_GET["cliente"];
    $descrizione = $_GET["descrizione"];

    $conn=Utils::getDBConnection();
    $stmt = $conn->prepare("INSERT INTO Lavoro (data, dipendente, prezzo, cliente, n_ore, descrizione) values (?, ?, ?, ?, ?, ?)");

    $stmt->bind_param("siiiis", $data, $id_dipendente, $prezzo, $cliente, $numeroOre, $descrizione);
    if(!$stmt->execute()) {
      http_response_code(500);
    } else {
      echo json_encode([
        "data"            => $data,
        "cliente"         => $cliente,
        "numeroOre"       => $numeroOre,
        "descrizione"     => $descrizione,
        "prezzo"          => $prezzo,
        "id"              => $conn->insert_id,
        "dipendente"      => $id_dipendente
      ]);
    }

  } else {
    http_response_code(400);
  }
