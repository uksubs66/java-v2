<?php
/**
 * Class Minify_ClosureStylesheets
 * @package Minify
 */

/**
 * Compress Javascript using the Closure Stylesheet
 *
 * You must set $jarFile and $tempDir before calling the minify functions.
 * Also, depending on your shell's environment, you may need to specify
 * the full path to java in $javaExecutable or use putenv() to setup the
 * Java environment.
 *
 * <code>
 * Minify_ClosureStylesheets::$jarFile = '/path/to/closure-stylesheet-20120123.jar';
 * Minify_ClosureStylesheets::$tempDir = '/tmp';
 * $code = Minify_ClosureStylesheets::minify(
 *   $code,
 *   array('allow-unrecognized-functions' => true)
 * );
 *
 * --compilation_level WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS
 *
 * </code>
 *
 * @todo unit tests, $options docs
 * @todo more options support (or should just passthru them all?)
 *
 * @package Minify
 * @author Stephen Clay <steve@mrclay.org>
 * @author Elan Ruusamäe <glen@delfi.ee>
 */

class Minify_ClosureStylesheets {

    /**
     * Filepath of the Closure Stylesheets jar file. This must be set before
     * calling minifyJs().
     *
     * @var string
     */
    public static $jarFile = null;

    /**
     * Writable temp directory. This must be set before calling minifyJs().
     *
     * @var string
     */
    public static $tempDir = null;

    /**
     * Filepath of "java" executable (may be needed if not in shell's PATH)
     *
     * @var string
     */
    public static $javaExecutable = 'java';

    /**
     * Minify a Javascript string
     *
     * @param string $js
     *
     * @param array $options (verbose is ignored)
     *
     * @return string
     */
    public static function minify($js, $options = array())
    {
        self::_prepare();
        if (! (($tmpFile = tempnam(self::$tempDir, 'cs_')) && ($tmpErr = tempnam(self::$tempDir, 'cse_')))) {
            throw new Exception('Minify_ClosureStylesheets : could not create temp file.');
        }
        file_put_contents($tmpFile, $js);
        $cmd = self::_getCmd($options, $tmpFile, $tmpErr);
        exec($cmd, $output, $result_code);
        unlink($tmpFile);
        if ($result_code != 0) {
            $error = file_get_contents($tmpErr);
            unlink($tmpErr);
            throw new Exception('Minify_ClosureStylesheets : Closure Compiler execution failed:\n' . $error);
        }
        unlink($tmpErr);
        return implode("\n", $output);
    }

    private static function _getCmd($userOptions, $tmpFile, $tmpErr)
    {
        $o = array_merge(
            array(
                'allow-unrecognized-functions' => true,
                'allow-unrecognized-properties' => true
            ),
            $userOptions
        );
        $cmd = self::$javaExecutable . ' -jar ' . escapeshellarg(self::$jarFile);

        foreach ($o as $opt => $value) {
            if ($value === true ) {
                $cmd .= " --$opt";
            } elseif ($o[$opt] !== false) {
                $cmd .= " --$opt ". escapeshellarg($value);
            }
        }
        return $cmd . ' ' . escapeshellarg($tmpFile) . ' 2> ' . escapeshellarg($tmpErr);
    }

    private static function _prepare()
    {
        if (! is_file(self::$jarFile)) {
            throw new Exception('Minify_ClosureStylesheets : $jarFile('.self::$jarFile.') is not a valid file.');
        }
        if (! is_readable(self::$jarFile)) {
            throw new Exception('Minify_ClosureStylesheets : $jarFile('.self::$jarFile.') is not readable.');
        }
        if (! is_dir(self::$tempDir)) {
            throw new Exception('Minify_ClosureStylesheets : $tempDir('.self::$tempDir.') is not a valid direcotry.');
        }
        if (! is_writable(self::$tempDir)) {
            throw new Exception('Minify_ClosureStylesheets : $tempDir('.self::$tempDir.') is not writable.');
        }
    }
}