<?php

$operacion =$_POST['codigo'];

switch($operacion) {
case 0:
    echo checkIfExistsRegistration($_POST['telefono']);
    break;
case 1:
    $telefono = $_POST['telefono'];
    $regid = $_POST['regid'];
    addRegistration($telefono, $regid);
    break;
case 2:
    $cabecera = array(
        'Authorization: key=AIzaSyDfgT87fvHf9E2Ad_wQsLEmjEkdafiHPvY',
        'Content-type: Application/json');
    #llamamos siempre al mismo registration id, para ver que funciona
    $info = array(
        'registration_ids' => array('APA91bFn8if7-ixbf4fKDuP7LcGIMciE_0wvQUV3K00VbJQcjE1dhFD_H2_2o95e-axVk0pHUT0tV-ikh2JKcd8GqeX2OAgVJAjfCATFeQjAOnMRw4nGLSK0H9Rw9tiZbG0Xt05_8_TZ4vgnzBcIrbplDPA5HnIEBg'),
        'data' => array('mensaje' => 'hola')); 
    peticionCurl($info, $cabecera);
    break;
 
}

function peticionCurl($info, $cabecera) {
    $ch = curl_init(); #inicializar el handler de curl

    #indicar el destino de la peticion, el servicio GCM de google
    curl_setopt($ch, CURLOPT_URL, 'https://android.googleapis.com/gcm/send');

    #indicar que la conexion es de tipo POST
    curl_setopt($ch, CURLOPT_POST, true);

    #agregar las cabeceras
    curl_setopt($ch, CURLOPT_HTTPHEADER, $cabecera);

    #indicar que se desea recibir la respuesta a la conexion en forma de string
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($info));

    #ejecutar la llamada
    echo $resultado = curl_exec($ch);

    #cerrar el handler de curl
    curl_close($ch);
}

function conectaDB() {
    $hostname = 'localhost';
    $username = 'Xragudo001';
    $password = 'I9tPTsWi55';
    try {
        $db = new PDO("mysql:host=$hostname;dbname=Xragudo001_NinjaMessaging", $username, $password);
        $db->exec("set names utf8");
        $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return($db);
    } catch (PDOException $e) {
        echo $e->getMessage();
        exit();
    }
}

function checkIfExistsRegistration($telefono) {
    $dbh = conectaDB();
	$consult_registro= $dbh->prepare("SELECT regid FROM registro where telefono=:telf");
    $consult_registro->bindParam(':telf', $telefono, PDO::PARAM_STR);
	$consult_registro->execute();
	$data_proximas = $consult_registro->fetch();
	if (!empty($data_proximas)) {
		return $data_proximas['regid'];
	} else {
		return null;
	}
}
 
function addRegistration($telefono, $regid) {
    $dbh = conectaDB();
    $consult_registro=$dbh->prepare("INSERT INTO registro (telefono, regid) VALUES (:telf, :regid)");
    $consult_registro->bindParam(':telf', $telefono, PDO::PARAM_STR);
    $consult_registro->bindParam(':regid', $regid, PDO::PARAM_STR);
    $consult_registro->execute();
}
?>
