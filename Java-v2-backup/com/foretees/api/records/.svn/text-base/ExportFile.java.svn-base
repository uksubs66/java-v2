/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.records;

import com.foretees.api.ApiConstants;
import com.foretees.api.ApiCommon;
import com.foretees.common.ArrayUtil;
import com.foretees.common.Connect;
import com.foretees.common.AESencrypt;
import com.foretees.common.reqUtil;
import java.sql.*;          // mysql
import java.util.UUID;
//import javax.naming.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;
/**
 *
 * @author John Kielkopf
 */
public class ExportFile {
    
    public Long id;
    public Long user_id;
    public Long club_id;
    public Long member_id;
    
    public String file_id;
    public String file_name;
    public String media_type;
    public String file_data;
    public byte[] byte_data = new byte[0];
    
    public Long updated;
    
    public String last_error;
    
    
    // Users
    private final static String sql_select = ""
            + "SELECT * "
            + "  FROM export_files ef ";
    private final static String sql_order = " ORDER BY ef.club_id, ef.member_id, ef.user_id, ef.id ";
    
    private final static String sql_all = sql_select + sql_order;
    private final static String sql_by_id = sql_select + "  WHERE ef.id = ? " + sql_order;
    private final static String sql_by_file_id = sql_select + "  WHERE ef.file_id = ? " + sql_order;
    
    private final static String path_to_wkhtmltopdf = "/usr/local/bin/wkhtmltopdf";
    

    public ExportFile(){}; // Empty parm
    
    public ExportFile(Long user_id, Long club_id, Long member_id, String file_name, String media_type, String file_data){
        this.user_id = user_id;
        this.club_id = club_id;
        this.member_id = member_id;
        this.file_name = file_name;
        this.media_type = media_type;
        this.file_data = file_data;
        this.file_id = UUID.randomUUID().toString();
        this.save();
    };
    
    public ExportFile(ResultSet rs){
        loadFromResultSet(rs);
    }
    
    public ExportFile(long id){
        Connection con_ft = ApiCommon.getConnection();
        loadById(id, con_ft);
        Connect.close(con_ft);
    }
    
    public ExportFile(long id, Connection con_ft){
        loadById(id, con_ft);
    }
    
    public ExportFile(String file_id){
        Connection con_ft = ApiCommon.getConnection();
        loadByFileId(file_id, con_ft);
        Connect.close(con_ft);
    }
    
    public ExportFile(String file_id, Connection con_ft){
        loadByFileId(file_id, con_ft);
    }
    
    public final ExportFile loadPdfByUrl(String url, String filename) {
        return loadPdfByUrl(url, filename, null);
    }
    
    public final ExportFile loadPdfByUrl(String url, String filename, String waitForStatus) {

        try {
            //stop wkhtmltopdf from accessing local files.
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "http://" + url;
            }
            // For piping in and out, we need separate args for this for some reason
            String[] command;
            //if(waitForStatus != null){
                //String[] command_list = {path_to_wkhtmltopdf, "--javascript-delay", "1000", "--window-status", waitForStatus, "--print-media-type", "--page-size", "Letter", url, "-"};
                //String[] command_list = {path_to_wkhtmltopdf, "--javascript-delay", "500", "--print-media-type", "--page-size", "Letter", url, "-"};
                //command = command_list;
            //} else {
                String[] command_list = {path_to_wkhtmltopdf, "--cache-dir","/tmp","--lowquality","--print-media-type", "--no-stop-slow-scripts", "--page-size", "Letter", url, "-"};
                command = command_list;
            //}
            ProcessBuilder builder = new ProcessBuilder(command);
            // this eats up stderr but prevents us from having to handle it ourselves
            builder.redirectErrorStream(false);

            Process process = builder.start();
            
            BufferedInputStream stdout = new BufferedInputStream(process.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true) {
                int x = stdout.read();
                if (x == -1) {
                    break;
                }
                outputStream.write(x);
            }
            this.byte_data = outputStream.toByteArray();
            process.waitFor();
            
            this.media_type = "application/pdf";
            this.file_name = filename;
            this.file_id = UUID.randomUUID().toString();

        } catch (Exception e) {
            Connect.logError(className() + ".loadPdfByUrl: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        }
        return this;
        
    }
    
    public final ExportFile loadPdfByHtml(String html, String filename) {

        try {
            // For piping in and out, we need separate args for this for some reason
            String[] command = {path_to_wkhtmltopdf, "--print-media-type", "-", "-"};
            ProcessBuilder builder = new ProcessBuilder(command);
            // this eats up stderr but prevents us from having to handle it ourselves
            builder.redirectErrorStream(false);

            Process process = builder.start();

            BufferedOutputStream stdin = new BufferedOutputStream(process.getOutputStream());
               stdin.write(html.getBytes());

            BufferedInputStream stdout = new BufferedInputStream(process.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true) {
                int x = stdout.read();
                if (x == -1) {
                    break;
                }
                outputStream.write(x);
            }
            this.byte_data = outputStream.toByteArray();
            process.waitFor();
            
            this.media_type = "application/pdf";
            this.file_name = filename;
            this.file_id = UUID.randomUUID().toString();

        } catch (Exception e) {
            Connect.logError(className() + ".loadPdfByHtml: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        }
        return this;
        
    }
    
    public final Long loadById(long id, Connection con_ft){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con_ft.prepareStatement(sql_by_id);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                result = loadFromResultSet(rs);
            } else {
                last_error = String.format(ApiConstants.error_finding_by_id, classNameLower(), id);
            }  
        } catch(Exception e) {
            Connect.logError(className()+".loadById: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = String.format(ApiConstants.error_loading_by_id, classNameLower(), e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        return result;
        
    }
    
    public final Long loadByFileId(String file_id, Connection con_ft){
        
        Long result = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con_ft.prepareStatement(sql_by_file_id);
            pstmt.setString(1, file_id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = loadFromResultSet(rs);
            } else {
                last_error = "Unable to find Export File: " + file_id;
            }
        } catch (Exception e) {
            Connect.logError(className() + ".loadByFileId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            last_error = "Error Loading Export File: " + e.toString();
        } finally {
            Connect.close(rs, pstmt);
        }

        
        return result;
        
    }
    
    private Long loadFromResultSet(ResultSet rs){
        return loadFromResultSet(rs, false);
    }
    
    private Long loadFromResultSet(ResultSet rs, boolean skip_loading){
        
        Long result = null;
        try {
                this.id = rs.getLong("id");
                this.user_id = rs.getLong("user_id");
                this.member_id = rs.getLong("member_id");
                this.club_id = rs.getLong("club_id");
                this.file_id = rs.getString("file_id");
                this.file_name = rs.getString("file_name");
                this.file_data = rs.getString("file_data");
                this.byte_data = rs.getBytes("byte_data");
                this.media_type = rs.getString("media_type");
                this.updated = rs.getTimestamp("updated").getTime();
                
                result = id;
        } catch(Exception e) {
            last_error = String.format(ApiConstants.error_resultset, classNameLower(), e.toString());
            Connect.logError(className()+".loadFromResultSet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            return null;
        }
        return result;
        
    }
    
    public final Long save(){
        Long result = null;
        Connection con_ft = ApiCommon.getConnection();
        result = save(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final Long save(Connection con_ft){
        Long result = null;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Inserting new record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "INSERT INTO export_files "
                        + "  (user_id, club_id, member_id, file_id,"
                        + "    file_name, file_data, byte_data, media_type) "
                        + "  VALUES"
                        + "  (?, ?, ?, ?,"
                        + "   ?, ?, ?, ?)");
                
                int i = 1;
                ApiCommon.setOrNull(i++, pstmt, user_id);
                ApiCommon.setOrNull(i++, pstmt, club_id);
                ApiCommon.setOrNull(i++, pstmt, member_id);
                pstmt.setString(i++, file_id);
                pstmt.setString(i++, file_name);
                pstmt.setString(i++, file_data);
                pstmt.setBytes(i++, byte_data);
                pstmt.setString(i++, media_type);
                pstmt.executeUpdate();
                
                result = Connect.getLastInsertId(con_ft);
                
                id = result;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                ExportFile test = new ExportFile(file_id, con_ft);
                if(test.last_error == null){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), file_id);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_insert, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        } else {
            // Update existing record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "UPDATE export_files "
                        + " SET"
                        + "  user_id = ?,"
                        + "  club_id = ?,"
                        + "  member_id = ?,"
                        + "  file_id = ?, "
                        + "  file_name = ?, "
                        + "  file_data = ?, "
                        + "  byte_data = ?, "
                        + "  media_type = ? "
                        + " WHERE id = ? ");
                
                int i = 1;
                ApiCommon.setOrNull(i, pstmt, user_id);
                ApiCommon.setOrNull(i, pstmt, club_id);
                ApiCommon.setOrNull(i, pstmt, member_id);
                pstmt.setString(i++, file_id);
                pstmt.setString(i++, file_name);
                pstmt.setString(i++, file_data);
                pstmt.setBytes(i++, byte_data);
                pstmt.setString(i++, media_type);
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = id;
                
                loadById(id, con_ft); // Refresh results
                
            } catch(Exception e) {
                ExportFile test = new ExportFile(file_id, con_ft);
                if(test.last_error == null && test.id != null && !test.id.equals(id)){
                    last_error = String.format(ApiConstants.error_update_duplicate_name, classNameLower(), file_id);
                } else {
                    Connect.logError(className()+".save: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                    last_error = String.format(ApiConstants.error_update, classNameLower(), e.toString());
                }
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }

    public final boolean delete(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = delete(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public final boolean delete(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        
        if(id == null){
            // Can't delete a record with a null id
            last_error = String.format(ApiConstants.error_delete_null_id, classNameLower(), className());
            
        } else {
            // Update existing record
            try {
                    pstmt = con_ft.prepareStatement(""
                        + "DELETE FROM export_files "
                        + " WHERE id = ? ");
                
                int i = 1;
                pstmt.setLong(i++, id);
                
                pstmt.executeUpdate();
                
                result = true;
                
            } catch(Exception e) {
                Connect.logError(className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                last_error = String.format(ApiConstants.error_delete, classNameLower(), e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }

        return result;
    }
    
    // Purge records older than 10 minutes
    public static boolean purge(){
        Connection con_ft = ApiCommon.getConnection();
        boolean result = purge(con_ft);
        Connect.close(con_ft);
        return result;
    }
    
    public static boolean purge(Connection con_ft){
        boolean result = false;
        
        PreparedStatement pstmt = null;
        // Update existing record
        try {
                pstmt = con_ft.prepareStatement(""
                    + "DELETE FROM export_files "
                    + " WHERE updated < (NOW() - INTERVAL 10 MINUTE) ");

            int i = 1;

            pstmt.executeUpdate();

            result = true;

        } catch(Exception e) {
            Connect.logError(new ExportFile().className()+".delete: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        } finally {
            Connect.close(pstmt);
        }

        return result;
    }
    
    
    
    
    public final String className(){
        return this.getClass().getSimpleName();
    }
    
    public final String classNameProper(){
        return ApiCommon.formatClassName(className());
    }
    
    public final String classNameLower(){
        return ApiCommon.formatClassNameLower(className());
    }
    
    
    
}
