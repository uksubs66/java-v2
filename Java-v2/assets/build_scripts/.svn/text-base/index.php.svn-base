<?php

/*
 * 
 * Build foretees core js and css files
 * 
 * Author: John Kielkopf
 * Created: 9-24-2013
 * 
 * 
 */


error_reporting(E_ALL);
ini_set('display_errors', 1);

set_time_limit(300); // 5 min to run

$pathToLib = dirname(__FILE__) . '/min/lib';
$pathToTmp = dirname(__FILE__) . '/tmp';
$pathToCssTidy = dirname(__FILE__) . '/CSSTidy1.4';
$pathToYui = $pathToLib.'/yuicompressor-2.4.8.jar';
$pathToClosureCompiler = $pathToLib.'/closure-compiler-20130823.jar';
$pathToClosureStylesheets = $pathToLib.'/closure-stylesheets-20111230.jar';
//$pathToClosureStylesheets = $pathToLib.'/closure-stylesheets-custom.jar';
$pathToAssets = dirname(dirname(__FILE__));
$pathToRoot = dirname(dirname(dirname(dirname(__FILE__))));
$baseVersionFile = $pathToAssets + "/script.ver";
//$pathToCss = $pathToAssets . '/stylesheets/sitewide.css';
$getScripts = "http://127.0.0.1/v5/servlet/getScripts";
$pathToCore = $pathToAssets .'/core';

if(!file_exists($pathToTmp)){
    if(!mkdir($pathToTmp)){
        die('unable to create tmp folder');
    }
}

// Set up Minify

require "$pathToLib/Minify/Loader.php";
Minify_Loader::register();

//$javaExecPath = 'java'; // dev2
$javaExecPath = '/opt/java/jdk1.6.0_35/jre/bin/java'; // testing.foretees.com

Minify_YUICompressor::$javaExecutable = $javaExecPath;
Minify_ClosureCompiler::$javaExecutable = $javaExecPath;
Minify_ClosureStylesheets::$javaExecutable = $javaExecPath;

Minify_YUICompressor::$jarFile = $pathToYui;
Minify_YUICompressor::$tempDir = $pathToTmp;

Minify_ClosureCompiler::$jarFile = $pathToClosureCompiler;
Minify_ClosureCompiler::$tempDir = $pathToTmp;

Minify_ClosureStylesheets::$jarFile = $pathToClosureStylesheets;
Minify_ClosureStylesheets::$tempDir = $pathToTmp;


// Set up CSSTidy

define( 'ABSPATH',$pathToCssTidy);

require($pathToCssTidy.'/class.csstidy.php');
require($pathToCssTidy.'/lang.inc.php');


/*
$css->settings['optimise_shorthands'] = true;
$css->settings['template'] = 'highest';
//$css->settings['css_level'] = 'CSS3.1';
$css->settings['remove_bslash'] = true;
$css->settings['compress_colors'] = true;
$css->settings['compress_font-weight'] = true;
$css->settings['lowercase_s'] = true;
$css->settings['remove_last_;'] = true;
$css->settings['case_properties'] = true;
$css->settings['sort_properties'] = false;
$css->settings['sort_selectors'] = false;
$css->settings['merge_selectors'] = true;
$css->settings['discard_invalid_properties'] = false;
$css->settings['preserve_css'] = false;
$css->settings['timestamp'] = false;
*/
//print_r($css->settings);

//die();


// Get scripts from servlet
$json = file_get_contents($getScripts);
$scripts = json_decode($json, true);

//print_r($scripts);

//$css_core = array();
$core_files = array();
$errors = array();
$warnings = array();
$completed = array();
$script_hash = array();
$script_size = array();
$full_script_size = array();

$list_open = false;

header('Content-Encoding: none;');
header('X-Accel-Buffering: no;');

while (ob_get_level()) ob_end_flush();

if(!count($errors)){
    $list_open = true;
    echo "<p>Processing CSS:</p>";
    echo "<ul>";
    flush();
}

// Load, concat and minify css
foreach ($scripts['css'] as $core_name => $core_group) {
    $cssTidy = new csstidy();
    $cssTidy->load_template('highest_compression');
    $cssTidy->settings['merge_selectors'] = 2;
    $core_files[$core_name] = "";
    $script_hash[$core_name] = getFileHash($pathToCore . "/$core_name");
    $script_size[$core_name] = getFileSize($pathToCore . "/$core_name");
    $full_script_size[$core_name] = 0;
    echo "<li>$core_name</li>";
    flush();
    foreach ($core_group as $script) {
        $filePath = $pathToRoot . $script;
        if(file_exists($filePath)){
            /*
            $core_files[$core_name] .= Minify_CSS::minify(file_get_contents($filePath), array(
                'currentDir' => dirname($filePath)
                ));
            */
            //echo "Proccessing: $filePath<br />\n";
            $full_script_size[$core_name] += getFileSize($filePath);
            $core_files[$core_name] .= Minify_CSS_UriRewriter::rewrite(file_get_contents($filePath), dirname($filePath));
           //$core_files[$core_name] .= Minify_YUICompressor::minifyCss(file_get_contents($filePath), array());
           // $core_files[$core_name] .= Minify_ClosureStylesheets::minify(file_get_contents($filePath), array());
        } else {
            $errors[] = "Unable to find CSS file for $core_name: $filePath";
            break;
        }
    }
    if(!count($errors)){
        $core_files[$core_name] = Minify_YUICompressor::minifyCss($core_files[$core_name], array());
        //
        //$core_files[$core_name] = Minify_ClosureStylesheets::minify($core_files[$core_name], array());
        /*
        if($core_name == "core_dining.css"){
            $result = $cssTidy->parse($core_files[$core_name]);
            if($result){
                $core_files[$core_name] = $cssTidy->print->plain();
            } else {
                $errors[] = "Error compressing with CSS Tidy!";
            }
             die();
        }
         * 
         */
    }
}

if($list_open){
    echo "</ul>";
    flush();
}

$list_open = false;

while (ob_get_level()) ob_end_flush();

if(!count($errors)){
    $list_open = true;
    echo "<p>Processing Javascript:</p>";
    echo "<ul>";
    flush();
}

// Load, concat and minify js
foreach ($scripts['js'] as $core_name => $core_group) {
    $core_files[$core_name] = "";
    $script_hash[$core_name] = getFileHash($pathToCore . "/$core_name");
    $script_size[$core_name] = getFileSize($pathToCore . "/$core_name");
    $full_script_size[$core_name] = 0;
    echo "<li>$core_name</li>";
    flush();
    foreach ($core_group as $script) {
        $filePath = $pathToRoot . $script;
        if(file_exists($filePath)){
            $contents = file_get_contents($filePath);
            //$core_files[$core_name] .= JSMin::minify(file_get_contents($filePath));
            //$core_files[$core_name] .= Minify_YUICompressor::minifyJs(file_get_contents($filePath), array());
            $full_script_size[$core_name] += getFileSize($filePath);
            //$core_files[$core_name] .= JSMin::minify(file_get_contents($filePath));
            $core_files[$core_name] .= $contents;
            //$test = array();
            //if(preg_match('/(^|;|{)\s*console\.(log|debug|info|warn|error|assert|dir|dirxml|trace|group|groupEnd|time|timeEnd|profile|profileEnd|count)\s*\(/',$contents,$test,PREG_OFFSET_CAPTURE)){
            //    //print_r($test);
            //    list($before) = str_split($contents, $test[2][1]+1); // fetches all the text before the match
            //    $line_number = strlen($before) - strlen(str_replace("\n", "", $before)) + 1;
            //    $warnings[] = "Uncommented console output found building $core_name in: $filePath on line: $line_number";
            //}
            //$core_files[$core_name] .= Minify_ClosureCompiler::minify(file_get_contents($filePath), array());
            checkConsoleLog($contents, $warnings, "Console output found in: $filePath on line:");
        } else {
            $errors[] = "Unable to find Javascript file for $core_name: $filePath";
        }
    }
    if(!count($errors)){
        // Strip console.xxx
        //$core_files[$core_name] = preg_replace('/(;|\s)*console\.(log|debug|info|warn|error|assert|dir|dirxml|trace|group|groupEnd|time|timeEnd|profile|profileEnd|count)\s*\(.*?\)\s*;/m',';',$core_files[$core_name]);
        // Compile
        $core_files[$core_name] = Minify_ClosureCompiler::minify($core_files[$core_name], array());
        //$core_files[$core_name] = Minify_YUICompressor::minifyJs($core_files[$core_name], array());
    }
    
}

if($list_open){
    echo "</ul>";
    flush();
}


$change_count = 0;
if(!count($errors)){
    // Create Core path if needed:
    if(!file_exists($pathToCore)){
        if(!@mkdir($pathToCore)){
            $errors[] = "Unable to access or create core script path: $pathToCore";
        }
    }
    // Create Core CSS and JS files
    if(!count($errors)){
        foreach ($core_files as $core_name => $core) {
            $file = $pathToCore . "/$core_name";
            $core_hash = md5($core);
            if($script_hash[$core_name] === $core_hash){
                    $mode = "No change";
                    $full_size = $full_script_size[$core_name];
                    $size = getFileSize($file);
                    $savings =  sprintf("%.2f%%", 100-(($size/$full_size) * 100));//$size/$full_size ;//sprintf("%.2f%%", $x * 100)
                    $completed[] = "$mode: $file - ($savings reduction)";
            } elseif(@file_put_contents($file, $core) !== false){
                $change_count ++;
                $mode = "Updated";
                $full_size = $full_script_size[$core_name];
                $size = getFileSize($file);
                $savings =  sprintf("%.2f%%", 100-(($size/$full_size) * 100));//$size/$full_size ;//sprintf("%.2f%%", $x * 100)
                $diff = $size - $script_size[$core_name];
                if($script_hash[$core_name] == false){
                    $mode = "Created";
                }
                $completed[] = "$mode: $file - ($savings reduction; $diff byte change.)";
            } else {
                $errors[] = "Unable to create core file: $file";
                break;
            }
        }
    }
}


if(count($errors)){
    echo "<p>Errors:</p>";
    echo "<ul><li>";
    echo implode("</li><li>",$errors);
    echo "</li></ul>";
}

if(count($warnings)){
    echo "<p>Warnings:</p>";
    echo "<ul><li>";
    echo implode("</li><li>",$warnings);
    echo "</li></ul>";
}

if(count($completed)){
    echo "<p>Results:</p>";
    echo "<ul><li>";
    echo implode("</li><li>",$completed);
    if($change_count> 0){
        @touch($baseVersionFile);
        echo "</li><li>Updated script.ver modification date.";
    }
    echo "</li></ul>";
}

function getFileHash($file){
    $md5 = false;
    $filec = @file_get_contents($file);
    if($filec !== false){
        $md5 = md5($filec);
    }
    return $md5;
}

function getFileSize($file){
    return @filesize($file);
}

function checkConsoleLog($content, &$array, $message, $offset = 0, $comment_segs = null){
    
    $test = array();
    if(preg_match('/(^|;|\{|\n|\r)\s*console\.(log|debug|info|warn|error|assert|dir|dirxml|trace|group|groupEnd|time|timeEnd|profile|profileEnd|count)\s*\(/',$content,$test,PREG_OFFSET_CAPTURE,$offset)){
        //print_r($test);
        $startpos = $test[0][1];
        $endpos = $startpos + strlen($test[0][0]);
        list($before) = str_split($content, $endpos); // fetches all the text before the match
        $line_number = strlen($before) - strlen(str_replace("\n", "", $before)) + 1;
        
        if($comment_segs == null){
            $comment_segs = array();
            getCommentRanges($content, $comment_segs);
        }
        //print_r($comment_segs);
        $in_comment = false;
        foreach($comment_segs as $start => $end){
            if($startpos >= $start && $startpos <= $end){
                $in_comment = true;
            }
        }
        if(!$in_comment){
            $error = "$message $line_number";
            $array[md5($error)] = $error;
        }
        checkConsoleLog($content, $array, $message, $endpos+1, $comment_segs);
    } 
}

function getCommentRanges($content, &$array, $offset = 0){
    $test = array();
    if(preg_match('/\/\*.+?(\*\/|$)/s',$content,$test,PREG_OFFSET_CAPTURE,$offset)){
        //print_r($test);
        $startpos = $test[0][1];
        $endpos = $startpos + strlen($test[0][0]);
        $array[$startpos] = $endpos;
        getCommentRanges($content, $array, $endpos+1);
    } 
    return $array;
}

