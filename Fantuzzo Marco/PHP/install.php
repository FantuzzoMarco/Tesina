<?php

  http_response_code(403);
  exit();

  require_once __DIR__ . "/Utils.php";

  $conn=Utils::getDBConnection();

  $queries = [
    "CREATE TABLE Dipendente(
      id int unsigned primary key auto_increment,
      CF CHAR(16) not null unique,
      email varchar(64) not null unique,
      nome varchar(64) not null,
      cognome varchar(64) not null,
      username varchar(16) not null unique,
      password varchar(255) not null
    )",
    "CREATE TABLE Cliente(
      id int unsigned primary key auto_increment,
      PIVA varchar(128) not null unique,
      nome varchar (128) not null,
      indirizzo varchar(256) not null,
      id_dipendente int unsigned references Dipendente(id) on update cascade on delete set null
    )",
    "CREATE TABLE Lavoro(
      id int unsigned primary key auto_increment,
      data timestamp not null,
      dipendente int unsigned not null references Dipendente(id),
      cliente int unsigned not null references Cliente(id),
      n_ore int unsigned not null,
      prezzo int unsigned not null,
      descrizione text not null
    )"
  ];

  try{
    foreach ($queries as $query) {
        if($conn->query($query)===false){
          echo $query;
          http_response_code(500);
          exit();
        }
    }
    } finally {
    $conn->close();
  }
