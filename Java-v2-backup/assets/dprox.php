<?php
header ("Content-Type:application/xml");
/*

CURL Proxy Hack for Dining System

*/
$url='';
$id = -1;
if (isset($_POST['foretees_login'])) {
	if (isset($_POST['reservation'])) {
		$tmp = $_POST['reservation'];
		$event = ($tmp[event_id] > 0);
	}
	if (!$event) {
		$url = "http://dining.foretees.com/self_service/dining_reservations";
	    //if (is_numeric($_POST['reservation_id'])) $id = $_POST['reservation_id'];
	} else {
		$url = "http://dining.foretees.com/self_service/event_reservations";
            //if (isset($_POST['reservation'])) {
	        //$tmp = $_POST['reservation'];
        	//if (is_numeric($tmp[id])) $id = $tmp[id];
		//}
	}
	if (is_numeric($_POST['reservation_id'])) $id = $_POST['reservation_id'];
	if ($id == -1 && is_numeric($_POST['cancel_id'])) $id = $_POST['cancel_id'];
	if ($id > 0) $url .= "/$id";
	//if (isset($_POST['cancel_id'])) {
	//}
	$url .= ".xml";
}else if(isset($_GET['test'])){
    $url = "http://dining.foretees.com/portal/reservations/available_time_options.json?reservation_category=dining&organization_id=4&location_id=124&reservation_date=2014-2-3";
}

if(strlen($url)) {
	//echo "<!-- $url -->";
	//echo "<br>".url_encode_array($_POST);
	
	//echo load_page($url, $_POST);
	
	$xml = trim(load_page($url, $_POST));
	if ($xml == "") {
		$xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		$xml .= "<success>1</success>";
	}
	
	echo $xml;
	
} else {
	echo "Bad target selection. $id";	
}

function load_page($url, $post, $ref_url = false) {
	$ch = curl_init();
	if(strlen($ref_url)){
		curl_setopt($ch, CURLOPT_REFERER, $ref_url);
	}
	curl_setopt($ch, CURLOPT_URL, $url);
	//curl_setopt($ch, CURLOPT_VERBOSE, 1); 
	curl_setopt($ch, CURLOPT_FAILONERROR, 1); 
	curl_setopt($ch, CURLOPT_USERAGENT, "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)");
	curl_setopt($ch, CURLOPT_HEADER, 0);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
	curl_setopt($ch, CURLOPT_POST, 1); // set POST method
	curl_setopt($ch, CURLOPT_POSTFIELDS, url_encode_array($post)); // add POST fields 
	$result = curl_exec($ch);
	curl_close($ch);
	
	// log all responses
	$fp = fopen('/usr/local/tomcat/webapps/v5/dprox.log', 'w');
	fwrite($fp, '-------------------------------');
	fwrite($fp, $result);
	fclose($fp);

	return $result;	
}

function url_encode_array($arr,$parent = ''){
	$string = "";
	foreach ($arr as $key => $value) {
		if(is_array($value)){
			if(strlen($parent)){
				$string .= url_encode_array($value,$parent . '[' . $key . ']');
			}else{
				$string .= url_encode_array($value,$key);
			}
		}else{
			if(strlen($parent)){
				$string .= urlencode($parent . '[' . $key . ']') . "=" . urlencode($value) . "&";
			}else{
				$string .= $key . "=" . urlencode($value) . "&";
			}
		}
	}
	trim($string,"&");
	return $string;
}
?>