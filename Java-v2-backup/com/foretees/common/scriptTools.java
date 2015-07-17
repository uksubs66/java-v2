/***************************************************************************************
 *   scriptTools:  Used for compressing and processing css and js includes
 *
 *   created:    01/26/2012   John K.
 *
 *   last updated:
 *        01/26/2012  Created
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.util.*;
import java.io.*;
import org.apache.commons.lang.*;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;

public class scriptTools {

  /**
   * @param code JavaScript source code to compile.
   * @return The compiled version of the code.
   */
  public static String compileJs(String code) {
    Compiler compiler = new Compiler();

    CompilerOptions options = new CompilerOptions();
    // Advanced mode is used here, but additional options could be set, too.
    // SIMPLE_OPTIMIZATIONS
    // ADVANCED_OPTIMIZATIONS
    // WHITESPACE_ONLY
    CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(
        options);

    // To get the complete set of externs, the logic in
    // CompilerRunner.getDefaultExterns() should be used here.
    JSSourceFile extern = JSSourceFile.fromCode("externs.js",
        "function alert(x) {}");

    // The dummy input name "input.js" is used here so that any warnings or
    // errors will cite line numbers in terms of input.js.
    JSSourceFile input = JSSourceFile.fromCode("input.js", code);

    // compile() returns a Result, but it is not needed here.
    compiler.compile(extern, input, options);

    // The compiler is responsible for generating the compiled code; it is not
    // accessible via the Result.
    return compiler.toSource();
  }

  public static void main(String[] args) {
    String compiledCode = compileJs(
        "function hello(name) {" +
          "alert('Hello, ' + name);" +
        "}" +
        "hello('New user');");
    System.out.println(compiledCode);
  }

}