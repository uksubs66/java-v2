import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.ParamPart;


/** 
 *
 *   last updated:
 *
 *       11/11/04   Ver 5 - add file names of 'probio_' where the _ is the pro id (1 - n).
 *        1/09/04   Change to Proshop since Admin does not use.
 *                  Add 'club' parm to test file name.
 *        7/18/03   Enhancements (none) for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 * This class is a copy of Oreilly's MultiPartRequest class for copying files.
 * We have added a test for the filename to ensure that improper files
 * are not uploaded.   BP 1/02/2002
 *
 *
 * A utility class to handle <code>multipart/form-data</code> requests,
 * the kind of requests that support file uploads.  This class emulates the 
 * interface of <code>HttpServletRequest</code>, making it familiar to use. 
 * It uses a "push" model where any incoming files are read and saved directly
 * to disk in the constructor. If you wish to have more flexibility, e.g. 
 * write the files to a database, use the "pull" model 
 * <code>MultipartParser</code> instead.
 * <p>
 * This class can receive arbitrarily large files (up to an artificial limit 
 * you can set), and fairly efficiently too.  
 * It cannot handle nested data (multipart content within multipart content)
 * or internationalized content (such as non Latin-1 filenames).
 * <p>
 * See the included <a href="upload.war">upload.war</a> 
 * for an example of how to use this class.
 * <p>
 * The full file upload specification is contained in experimental RFC 1867,
 * available at <a href="http://www.ietf.org/rfc/rfc1867.txt">
 * http://www.ietf.org/rfc/rfc1867.txt</a>.
 *
 * @see MultipartParser
 * 
 * @author Jason Hunter
 * @author Geoff Soutter
 * @version 1.7, 01/02/07, made fields protected to increase user flexibility
 * @version 1.6, 00/07/21, redid internals to use MultipartParser,
 *                         thanks to Geoff Soutter
 * @version 1.5, 00/02/04, added auto MacBinary decoding for IE on Mac
 * @version 1.4, 00/01/05, added getParameterValues(),
 *                         WebSphere 2.x getContentType() workaround,
 *                         stopped writing empty "unknown" file
 * @version 1.3, 99/12/28, IE4 on Win98 lastIndexOf("boundary=") workaround
 * @version 1.2, 99/12/20, IE4 on Mac readNextPart() workaround
 * @version 1.1, 99/01/15, JSDK readLine() bug workaround
 * @version 1.0, 98/09/18
 */
public class ProshopMultipartRequest {

  private static final int DEFAULT_MAX_POST_SIZE = 512 * 1024;  // 1 Meg (changed to 512KB)

  protected Hashtable parameters = new Hashtable();  // name - Vector of values
  protected Hashtable files = new Hashtable();       // name - UploadedFile

  /**
   * Constructs a new MultipartRequest to handle the specified request, 
   * saving any uploaded files to the given directory, and limiting the 
   * upload size to the specified length.  If the content is too large, an 
   * IOException is thrown.  This constructor actually parses the 
   * <tt>multipart/form-data</tt> and throws an IOException if there's any 
   * problem reading or parsing the request.
   *
   * @param request the servlet request.
   * @param saveDirectory the directory in which to save any uploaded files.
   * @param maxPostSize the maximum size of the POST content.
   * @exception IOException if the uploaded content is larger than 
   * <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
   */
  public ProshopMultipartRequest(HttpServletRequest request,
                          String saveDirectory,
                          int maxPostSize, String club) throws IOException {
    // Sanity check values
    if (request == null)
      throw new IllegalArgumentException("request cannot be null");
    if (saveDirectory == null)
      throw new IllegalArgumentException("saveDirectory cannot be null");
    if (maxPostSize <= 0) {
      throw new IllegalArgumentException("maxPostSize must be positive");
    }

    // Establish the file name values for test below ************************ ForeTees Mod *************
    String Fname1 = club + "_announce.htm";
    String Fname2 = club + "_announce.html";
    String Fname3 = club + "_bio";
      
    boolean nameOK = false;

    // Save the dir
    File dir = new File(saveDirectory);

    // Check saveDirectory is truly a directory
    if (!dir.isDirectory())
      throw new IllegalArgumentException("Not a directory: " + saveDirectory);

    // Check saveDirectory is writable
    if (!dir.canWrite())
      throw new IllegalArgumentException("Not writable: " + saveDirectory);

    // Parse the incoming multipart, storing files in the dir provided, 
    // and populate the meta objects which describe what we found
    MultipartParser parser = new MultipartParser(request, maxPostSize);

    Part part;
    while ((part = parser.readNextPart()) != null) {
      String name = part.getName();
      if (part.isParam()) {
        // It's a parameter part, add it to the vector of values
        ParamPart paramPart = (ParamPart) part;
        String value = paramPart.getStringValue();
        Vector existingValues = (Vector)parameters.get(name);
        if (existingValues == null) {
          existingValues = new Vector();
          parameters.put(name, existingValues);
        }
        existingValues.addElement(value);
      }
      else if (part.isFile()) {
        // It's a file part
        FilePart filePart = (FilePart) part;
        String fileName = filePart.getFileName();
          
        //
        //***********************************************************************
        // ***** This section modified to check for specific file name.  BP *****
        //***********************************************************************
        //
        if (fileName != null) {
          //
          // Check for file name = club_announce.htm or club_announce.html
          //
          nameOK = false;          // name is NOT ok
             
          if (fileName.endsWith( Fname1 ) || fileName.endsWith( Fname2 )) {

             nameOK = true;          // name is ok
               
          } else {
            
             if (fileName.startsWith( Fname3 ) && (fileName.endsWith ( ".htm" ) || fileName.endsWith ( ".html" ))) { 

                nameOK = true;          // name is ok
             }
          }

          if (nameOK == false) {

             throw new IllegalArgumentException("Invalid file name received. " + fileName);
          }

          filePart.writeTo(dir);
          files.put(name, new UploadedFile(
                      dir.toString(), fileName, filePart.getContentType()));
        }
        else { 
          // The field did not contain a file
          files.put(name, new UploadedFile(null, null, null));
        }
      }
    }
  }

  /**
   * Returns the names of all the parameters as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no parameters.
   *
   * @return the names of all the parameters as an Enumeration of Strings.
   */
  public Enumeration getParameterNames() {
    return parameters.keys();
  }

  /**
   * Returns the names of all the uploaded files as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no uploaded 
   * files.  Each file name is the name specified by the form, not by 
   * the user.
   *
   * @return the names of all the uploaded files as an Enumeration of Strings.
   */
  public Enumeration getFileNames() {
    return files.keys();
  }

  /**
   * Returns the value of the named parameter as a String, or null if 
   * the parameter was not sent or was sent without a value.  The value 
   * is guaranteed to be in its normal, decoded form.  If the parameter 
   * has multiple values, only the last one is returned (for backward 
   * compatibility).  For parameters with multiple values, it's possible
   * the last "value" may be null.
   *
   * @param name the parameter name.
   * @return the parameter value.
   */
  public String getParameter(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String value = (String)values.elementAt(values.size() - 1);
      return value;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the values of the named parameter as a String array, or null if 
   * the parameter was not sent.  The array has one entry for each parameter 
   * field sent.  If any field was sent without a value that entry is stored 
   * in the array as a null.  The values are guaranteed to be in their 
   * normal, decoded form.  A single value is returned as a one-element array.
   *
   * @param name the parameter name.
   * @return the parameter values.
   */
  public String[] getParameterValues(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String[] valuesArray = new String[values.size()];
      values.copyInto(valuesArray);
      return valuesArray;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the filesystem name of the specified file, or null if the 
   * file was not included in the upload.  A filesystem name is the name 
   * specified by the user.  It is also the name under which the file is 
   * actually saved.
   *
   * @param name the file name.
   * @return the filesystem name of the file.
   */
  public String getFilesystemName(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getFilesystemName();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the content type of the specified file (as supplied by the 
   * client browser), or null if the file was not included in the upload.
   *
   * @param name the file name.
   * @return the content type of the file.
   */
  public String getContentType(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getContentType();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns a File object for the specified file saved on the server's 
   * filesystem, or null if the file was not included in the upload.
   *
   * @param name the file name.
   * @return a File object for the named file.
   */
  public File getFile(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getFile();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }
}


// A class to hold information about an uploaded file.
//
class UploadedFile {

  private String dir;
  private String filename;
  private String type;

  UploadedFile(String dir, String filename, String type) {
    this.dir = dir;
    this.filename = filename;
    this.type = type;
  }

  public String getContentType() {
    return type;
  }

  public String getFilesystemName() {
    return filename;
  }

  public File getFile() {
    if (dir == null || filename == null) {
      return null;
    }
    else {
      return new File(dir + File.separator + filename);
    }
  }
}

