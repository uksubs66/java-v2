/***************************************************************************************
 *   htmlTags:  return different HTML tags based on RWD mode
 *   called by:  several
 *
 *   created: 01/27/2014   John Kielkopf
 * 
 *   IE9 and Windows Phone 7.5/7.8 have a bug that stops display:block from working correctly on TR tags.
 *   To work around this, we'll need to build tables out of all divs, and use css to convert the divs to
 *   to tables using display:table, table-row, table-cell, etc.
 *   
 *   This will not work for IE7, so we'll only do this conversion in responsive mode (rwd is IE8+), 
 *   leaving desktop mode with IE7 compatibility.
 *
 *   last updated:
 *
 */
package com.foretees.common;

import org.apache.commons.lang.*;
import javax.servlet.http.HttpServletRequest;

public class htmlTags {

    public String caption, table, thead, tbody, tr, th, td;
    private final String rwd_div = "div";
    private final String std_caption = "caption";
    private final String std_table = "table";
    private final String std_thead = "thead";
    private final String std_tbody = "tbody";
    private final String std_tr = "tr";
    private final String std_th = "th";
    private final String std_td = "td";
    private boolean rwd;

   public htmlTags(boolean rwd) {
       loadTags(rwd);
   }
   
   public htmlTags(HttpServletRequest req) {
       loadTags(reqUtil.getRequestBoolean(req, ProcessConstants.RQA_RWD, false));
   }
    
   private void loadTags(boolean rwd) {
        this.rwd = rwd;
        if (rwd) {
            caption = rwd_div;
            table = rwd_div;
            thead = rwd_div;
            tbody = rwd_div;
            tr = rwd_div;
            th = rwd_div;
            td = rwd_div;
        } else {
            caption = std_caption;
            table = std_table;
            thead = std_thead;
            tbody = std_tbody;
            tr = std_tr;
            th = std_th;
            td = std_td;
        }
    }
    
    public final String getDatePicker(String label, String value, String min, String max, String classes){
        return getDatePicker(label, value, min, max, classes, "");
    }
    
    public final String getDatePicker(String label, String value, String min, String max, String classes, String extra){
        return getTag("label",getTag("span",getTag("b",label))+getInput("text", value, "ft_date_picker_field "+classes,new String[]{"data-ftstartdate=\""+min+"\"","data-ftenddate=\""+max+"\"",extra}),new String[]{"onclick=\"\""});
    }
    
    public final String openCaption(){
        return openCaption(null, null, null);
    }
    public final String openCaption(String classes){
        return openCaption(classes, null, null);
    }
    public final String openCaption(String classes, String extra){
        return openCaption(classes, null, null);
    }
    public final String openCaption(String classes, String style, String extra){
        return getTag(caption, null, "rwdCaption "+classes, style, extra, false);
    }
    public final String closeCaption(){
        return closeTag(caption);
    }
    public final String getCaption(String content){
        return getCaption(content, null, null, null);
    }
    public final String getCaption(String content, String classes){
        return getCaption(content, classes, null, null);
    }
    public final String getCaption(String content, String classes, String extra){
        return getCaption(content, classes, null, null);
    }
    public final String getCaption(String content, String classes, String style, String extra){
        return getTag(caption, content, "rwdCaption "+classes, style, extra, true);
    }
    
    public final String openTable(String[] classes){
        return openTable(StringUtils.join(classes, " "), null, null);
    }
    public final String openTable(){
        return openTable(null, null, null);
    }
    public final String openTable(String classes){
        return openTable(classes, null, null);
    }
    public final String openTable(String classes, String extra){
        return openTable(classes, null, null);
    }
    public final String openTable(String classes, String style, String extra){
        return getTag(table, null, "rwdTable "+classes, style, extra, false);
    }
    public final String closeTable(){
        return closeTag(table);
    }
    
    public final String openThead(String[] classes){
        return openThead(StringUtils.join(classes, " "), null, null);
    }
    public final String openThead(){
        return openThead( null, null, null);
    }
    public final String openThead(String classes){
        return openThead(classes, null, null);
    }
    public final String openThead(String classes, String extra){
        return openThead(classes, null, null);
    }
    public final String openThead(String classes, String style, String extra){
        return getTag(thead, null, "rwdThead "+classes, style, extra, false);
    }
    public final String closeThead(){
        return closeTag(thead);
    }
    
    public final String openTbody(String[] classes){
        return openTbody(StringUtils.join(classes, " "), null, null);
    }
    public final String openTbody(){
        return openTbody(null, null, null);
    }
    public final String openTbody(String classes){
        return openTbody(classes, null, null);
    }
    public final String openTbody(String classes, String extra){
        return openTbody(classes, null, null);
    }
    public final String openTbody(String classes, String style, String extra){
        return getTag(tbody, null, "rwdTbody "+classes, style, extra, false);
    }
    public final String closeTbody(){
        return closeTag(tbody);
    }
    
    public final String openTr(String[] classes){
        return openTr(StringUtils.join(classes, " "), null, null);
    }
    public final String openTr(){
        return openTr(null, null, null);
    }
    public final String openTr(String classes){
        return openTr(classes, null, null);
    }
    public final String openTr(String classes, String extra){
        return openTr(classes, null, null);
    }
    public final String openTr(String classes, String style, String extra){
        return getTag(tr, null, "rwdTr "+classes, style, extra, false);
    }
    public final String closeTr(){
        return closeTag(tr);
    }
    
    public final String getTh(String content){
        return getTh(content, null, null, null);
    }
    public final String getTh(String content, String classes){
        return getTh(content, classes, null, null);
    }
    public final String getTh(String content, String[] classes){
        return getTh(content, StringUtils.join(classes, " "), null, null);
    }
    public final String getTh(String content, String classes, String extra){
        return getTh(content, classes, null, null);
    }
    public final String getTh(String content, String classes, String style, String extra){
        return getTag(th, content, "rwdTh "+classes, style, extra, true);
    }
    
    public final String getTd(String content){
        return getTd(content, null, null, null);
    }
    public final String getTd(String content, String classes){
        return getTd(content, classes, null, null);
    }
    public final String getTd(String content, String[] classes){
        return getTd(content, StringUtils.join(classes, " "), null, null);
    }
    public final String getTd(String content, String classes, String extra){
        return getTd(content, classes, null, null);
    }
    public final String getTd(String content, String classes, String style, String extra){
        return getTag(td, content, "rwdTd "+classes, style, extra, true);
    }

    public final String getFtLink(String name, String data, String classes){
        return getFtLink(name, data, classes, new String[]{});
    }
    
    public final String getFtLink(String name, String data, String classes, String[] extra){
        return getTag("a", name, "ftCsLink "+classes, null, "href=\"#\" data-fthref=\""+data+"\" "+StringUtils.join(extra, " "), true);
    }
    
    public final String getJsonLink(String name, String data, String classes){
        return getJsonLink(name, data, classes, new String[]{});
    }
    
    public final String getJsonLink(String name, String data, String classes, String[] extra){
        return getTag("a", name, classes, null, "href=\"#\" data-ftjson=\""+data+"\" "+StringUtils.join(extra, " "), true);
    }
    
    public final String getSubInst(String content){
        return getSubInst(content, null);
    }
    
    public final String getSubInst(String content, String classes){
        return getSubInst(content, classes, new String[]{});
    }
    
    public final String getSubInst(String content, String classes, String[] extra){
        return getTag("div", content, "sub_instructions "+classes, null, StringUtils.join(extra, " "), true);
    }
    
    public final String getMainInst(String content){
        return getMainInst(content, null);
    }
    
    public final String getMainInst(String content, String classes){
        return getMainInst(content, classes, new String[]{});
    }
    
    public final String getMainInst(String content, String classes, String[] extra){
        return getTag("div", content, "main_instructions "+classes, null, StringUtils.join(extra, " "), true);
    }
    
    public final String getInput(String type, String value, String classes, String[] extra){
        return getTag("input", null, classes, null, "type=\""+type+"\" value=\""+value+"\" "+StringUtils.join(extra, " "), false);
    }
    
    public final String getTag(String tag, String content){
        return getTag(tag, content, null, null, null, true);
    }
    public final String getTag(String tag, String content, String[] extra){
        return getTag(tag, content, null, null, StringUtils.join(extra, " "), true);
    }
    public final String getTag(String tag, String content, String classes){
        return getTag(tag, content, classes, null, null, true);
    }
    public final String getTag(String tag, String content, String classes, String[] extra){
        return getTag(tag, content, classes, null, StringUtils.join(extra, " "), true);
    }
    public final String getTag(String tag, String content, String classes, String style){
        return getTag(tag, content, classes, style, null, true);
    }
    public final String getTag(String tag, String content, String classes, String style, String extra){
        return getTag(tag, content, classes, style, extra, true);
    }
    public final String getTag(String tag, String content, String classes, String style, String extra, boolean close){
        
        StringBuilder result = new StringBuilder();
        
        result.append("<");
        result.append(tag);
        if(classes!=null && !classes.trim().isEmpty()){
            result.append(" class=\"");
            result.append(classes.trim());
            result.append("\"");
        }
        if(style!=null && !style.trim().isEmpty()){
            result.append(" style=\"");
            result.append(style);
            result.append("\"");
        }
        if(extra!=null && !extra.trim().isEmpty()){
            result.append(" ");
            result.append(extra);
        }
        if(content == null && (tag.equalsIgnoreCase("input"))){
            result.append(" />");
        } else {
            result.append(">");
        }
        
        if(content != null){
            result.append(content);
        }
        if(close){
            result.append("</");
            result.append(tag);
            result.append(">");
        }
        
        return result.toString();
        
    }
    public final String closeTag(String tag){
        
        StringBuilder result = new StringBuilder();
        
        result.append("</");
        result.append(tag);
        result.append(">");
        
        return result.toString();
        
    }
    
    
}
