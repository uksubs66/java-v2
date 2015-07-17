/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.awt.Color;
import java.util.*;

/**
 *
 * @author John Kielkopf 4/10/2014
 */
public class htmlUtil {
    
    public static String joinList(List<String> list){
        
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            if(i+1 == list.size() && list.size() > 1){
                result.append(" and ");
            } else if(i > 0){
                result.append(", ");
            }
            result.append(list.get(i).trim());
        }
        return result.toString();
        
    }
    
    public static String isOrAre(int i){
        return (i>1?"are":"is");
    }
    
    public static String addS(int i){
        return (i>1?"s":"");
    }
    
    public static String getColorClass(String color){
        if(color == null || color.isEmpty() || htmlColorMap.get(color.toLowerCase()) == null){
            return "";
        } else {
            return "ftc" + Character.toUpperCase(color.charAt(0)) + color.toLowerCase().substring(1);
        }
    }
    
    public static Color getColor(String color){
        
        return getColor(color, null);
        
        
    }
    
    public static Color getColor(String color, Integer alpha){
        
        // convert color name to hex (if it's a color name),
        // defaulting to white if it can't parse the color
        color = getHexColorFromHtmlColor(color, "#FFFFFF");
        
        // remove hash character from string
        String rawColor = color.substring(1,color.length());
        if(alpha != null){
            rawColor += Integer.toHexString(0x100 | alpha).substring(1) + rawColor;
        }

        // convert hex string to int
        int rgb;
        try{
            rgb = Integer.parseInt(rawColor, 16);
        } catch (Exception e) {
            rgb = 16777215; // default to white on error
        }

        if(rawColor.length() == 8){
            // Color has alpha channel
            return new Color(rgb,true);
        } else {
            return new Color(rgb);
        }
        
        
    }
    
    public static String whitenColor(String color, double strength){
        
        int alpha = clampChannel(Math.round((float) (strength * 255.0)));
        
        Color s = getColor(color);
        Color w = new Color(255,255,255,alpha);
        
        Color c = blendColor(s,w);
        
        return "#"+Integer.toHexString(c.getRGB()).substring(2);
        //return "rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")";
    }
    
    public static String blackenColor(String color, double strength){
        
        int alpha = clampChannel(Math.round((float) (strength * 255.0)));

        Color s = getColor(color);
        Color b = new Color(0,0,0,alpha);
        
        Color c = blendColor(s,b);
        
        return "#"+Integer.toHexString(c.getRGB()).substring(2);
        //return "rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")";
    }
    
    public static Color blendColor(Color background, Color foreground) {
        
        float mask = (float)(foreground.getAlpha() / 255.0);
        int red = clampChannel(background.getRed() * (1-mask) + foreground.getRed() * mask);
        int green = clampChannel(background.getGreen() * (1-mask) + foreground.getGreen() * mask);
        int blue = clampChannel(background.getBlue() * (1-mask) + foreground.getBlue() * mask);

        return new Color(red, green, blue, background.getAlpha());
        //return new Color(background.getRed(), background.getGreen(), background.getBlue(), background.getAlpha());
    }
    
    private static int clampChannel(float value){
        int result = Math.round(value);
        return clampChannel(result);
    }
    
    private static int clampChannel(double value){
        int result = Math.round((float)value);
        return clampChannel(result);
    }
    
    private static int clampChannel(int value){
        if(value<0){
            value = 0;
        }
        if(value>255){
            value = 255;
        }
        return value;
    }

    public static float getColorBrightness(String color){

        Color c = getColor(color);
        
        int perBrightness = (int)Math.sqrt(
                (c.getRed() * c.getRed() * .241) + 
                (c.getGreen() * c.getGreen() * .691) + 
                (c.getBlue() * c.getBlue() * .068));
        
        return (float)(perBrightness / 255.0);

        //float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        //return hsb[2];
        
    }
    
    public static String getTextColorFromHex(String color){
        
        if(getColorBrightness(color) <= 0.55){ 
            // Dark color -- use white text
            return "#FFFFFF";
        } else {
            // Light color -- use dark text
            return "#000000";
        }
        
    }
    
    public static String getColorStyle(String bgcolor, String compColor, boolean ignoreOldSkinDefault) {

        if(compColor == null){
            compColor = bgcolor;
        }
        if(ignoreOldSkinDefault && compColor.equalsIgnoreCase("#F5F5DC")){
            return "";
        } else {
            return getColorStyle(bgcolor, compColor);
        }

    }
    
    public static String getColorStyle(String bgcolor, boolean ignoreOldSkinDefault) {

        if(ignoreOldSkinDefault && bgcolor.equalsIgnoreCase("#F5F5DC")){
            return "";
        } else {
            return getColorStyle(bgcolor);
        }

    }
    
    public static String getColorStyle(String bgcolor) {
        return getColorStyle(bgcolor,null);
    }
    
    public static String getColorStyle(String bgcolor, String compColor) {
        
        if(compColor == null){
            compColor = bgcolor;
        }
        if(getHexColorFromHtmlColor(bgcolor, null)==null || getHexColorFromHtmlColor(compColor, null)==null ){
            return "";
        }

        return " style=\"background-color:" + bgcolor + "; color:"+getTextColorFromHex(compColor)+"\"";

    }
    
    public static String getColorCss(String bgcolor) {
        return getColorCss(bgcolor, null, false);
    }
    
    public static String getColorCss(String bgcolor, boolean ignoreOldSkinDefault) {
        return getColorCss(bgcolor, null, ignoreOldSkinDefault);
    }
    
    public static String getColorCss(String bgcolor, String compColor) {
        return getColorCss(bgcolor, compColor, false);
    }
    
    public static String getColorCss(String bgcolor, String compColor, boolean ignoreOldSkinDefault) {
        
        
        if(compColor == null){
            compColor = bgcolor;
        }
        if(ignoreOldSkinDefault && compColor.equalsIgnoreCase("#F5F5DC")){
            return "";
        }
        if(getHexColorFromHtmlColor(bgcolor, null)==null || getHexColorFromHtmlColor(compColor, null)==null ){
            return "";
        }

        return " background-color:" + bgcolor + "; color:"+getTextColorFromHex(compColor)+";";

    }
    
    public static final Map<String, String> htmlColorMap = buildColorNameMap();
    
    public static String getHexColorFromHtmlColor(String htmlColor, String defaultColor){
        if(htmlColor == null){
            return defaultColor;
        }
        String result = htmlColorMap.get(htmlColor.toLowerCase());
        if(result == null){
            if(htmlColor.matches("#[A-Fa-f0-9]{3}")){
                StringBuilder fullHex = new StringBuilder();
                fullHex.append("#");
                for(int i = 1; i < htmlColor.length(); i++){
                    fullHex.append(htmlColor.charAt(i));
                    fullHex.append(htmlColor.charAt(i));
                }
                return fullHex.toString();
            } else if(htmlColor.matches("#[A-Fa-f0-9]{6}")) {
                return htmlColor;
            } else if(htmlColor.matches("#[A-Fa-f0-9]{8}")) {
                return htmlColor;
            } else {
                return defaultColor;
            }
        } else {
            return result;
        }
    }
    
    private static Map<String, String> buildColorNameMap() {

        Map<String, String> colorMap = new HashMap<String, String>();
        colorMap.put("aliceblue", "#F0F8FF");
        colorMap.put("antiquewhite", "#FAEBD7");
        colorMap.put("aqua", "#00FFFF");
        colorMap.put("aquamarine", "#7FFFD4");
        colorMap.put("azure", "#F0FFFF");
        colorMap.put("beige", "#F5F5DC");
        colorMap.put("bisque", "#FFE4C4");
        colorMap.put("black", "#000000");
        colorMap.put("blanchedalmond", "#FFEBCD");
        colorMap.put("blue", "#0000FF");
        colorMap.put("blueviolet", "#8A2BE2");
        colorMap.put("brown", "#A52A2A");
        colorMap.put("burlywood", "#DEB887");
        colorMap.put("cadetblue", "#5F9EA0");
        colorMap.put("chartreuse", "#7FFF00");
        colorMap.put("chocolate", "#D2691E");
        colorMap.put("coral", "#FF7F50");
        colorMap.put("cornflowerblue", "#6495ED");
        colorMap.put("cornsilk", "#FFF8DC");
        colorMap.put("crimson", "#DC143C");
        colorMap.put("cyan", "#00FFFF");
        colorMap.put("darkblue", "#00008B");
        colorMap.put("darkcyan", "#008B8B");
        colorMap.put("darkgoldenrod", "#B8860B");
        colorMap.put("darkgray", "#A9A9A9");
        colorMap.put("darkgreen", "#006400");
        colorMap.put("darkkhaki", "#BDB76B");
        colorMap.put("darkmagenta", "#8B008B");
        colorMap.put("darkolivegreen", "#556B2F");
        colorMap.put("darkorange", "#FF8C00");
        colorMap.put("darkorchid", "#9932CC");
        colorMap.put("darkred", "#8B0000");
        colorMap.put("darksalmon", "#E9967A");
        colorMap.put("darkseagreen", "#8FBC8F");
        colorMap.put("darkslateBlue", "#483D8B");
        colorMap.put("darkslateGray", "#2F4F4F");
        colorMap.put("darkturquoise", "#00CED1");
        colorMap.put("darkviolet", "#9400D3");
        colorMap.put("deeppink", "#FF1493");
        colorMap.put("deepskyblue", "#00BFFF");
        colorMap.put("dimgray", "#696969");
        colorMap.put("dodgerblue", "#1E90FF");
        colorMap.put("firebrick", "#B22222");
        colorMap.put("floralwhite", "#FFFAF0");
        colorMap.put("forestgreen", "#228B22");
        colorMap.put("fuchsia", "#FF00FF");
        colorMap.put("gainsboro", "#DCDCDC");
        colorMap.put("ghostwhite", "#F8F8FF");
        colorMap.put("gold", "#FFD700");
        colorMap.put("goldenrod", "#DAA520");
        colorMap.put("gray", "#808080");
        colorMap.put("green", "#008000");
        colorMap.put("greenyellow", "#ADFF2F");
        colorMap.put("honeydew", "#F0FFF0");
        colorMap.put("hotpink", "#FF69B4");
        colorMap.put("indianred", "#CD5C5C");
        colorMap.put("indigo", "#4B0082");
        colorMap.put("ivory", "#FFFFF0");
        colorMap.put("khaki", "#F0E68C");
        colorMap.put("lavender", "#E6E6FA");
        colorMap.put("lavenderblush", "#FFF0F5");
        colorMap.put("lawngreen", "#7CFC00");
        colorMap.put("lemonchiffon", "#FFFACD");
        colorMap.put("lightblue", "#ADD8E6");
        colorMap.put("lightcoral", "#F08080");
        colorMap.put("lightcyan", "#E0FFFF");
        colorMap.put("lightgoldenrodyellow", "#FAFAD2");
        colorMap.put("lightgray", "#D3D3D3");
        colorMap.put("lightgreen", "#90EE90");
        colorMap.put("lightpink", "#FFB6C1");
        colorMap.put("lightsalmon", "#FFA07A");
        colorMap.put("lightseagreen", "#20B2AA");
        colorMap.put("lightskyblue", "#87CEFA");
        colorMap.put("lightslategray", "#778899");
        colorMap.put("lightsteelblue", "#B0C4DE");
        colorMap.put("lightyellow", "#FFFFE0");
        colorMap.put("lime", "#00FF00");
        colorMap.put("limegreen", "#32CD32");
        colorMap.put("linen", "#FAF0E6");
        colorMap.put("magenta", "#FF00FF");
        colorMap.put("maroon", "#800000");
        colorMap.put("mediumaquamarine", "#66CDAA");
        colorMap.put("mediumblue", "#0000CD");
        colorMap.put("mediumorchid", "#BA55D3");
        colorMap.put("mediumpurple", "#9370DB");
        colorMap.put("mediumseagreen", "#3CB371");
        colorMap.put("mediumslateblue", "#7B68EE");
        colorMap.put("mediumspringgreen", "#00FA9A");
        colorMap.put("mediumturquoise", "#48D1CC");
        colorMap.put("mediumvioletred", "#C71585");
        colorMap.put("midnightblue", "#191970");
        colorMap.put("mintcream", "#F5FFFA");
        colorMap.put("mistyrose", "#FFE4E1");
        colorMap.put("moccasin", "#FFE4B5");
        colorMap.put("navajowhite", "#FFDEAD");
        colorMap.put("navy", "#000080");
        colorMap.put("oldlace", "#FDF5E6");
        colorMap.put("olive", "#808000");
        colorMap.put("olivedrab", "#6B8E23");
        colorMap.put("orange", "#FFA500");
        colorMap.put("orangered", "#FF4500");
        colorMap.put("orchid", "#DA70D6");
        colorMap.put("palegoldenrod", "#EEE8AA");
        colorMap.put("palegreen", "#98FB98");
        colorMap.put("paleturquoise", "#AFEEEE");
        colorMap.put("palevioletred", "#DB7093");
        colorMap.put("papayawhip", "#FFEFD5");
        colorMap.put("peachpuff", "#FFDAB9");
        colorMap.put("peru", "#CD853F");
        colorMap.put("pink", "#FFC0CB");
        colorMap.put("plum", "#DDA0DD");
        colorMap.put("powderblue", "#B0E0E6");
        colorMap.put("purple", "#800080");
        colorMap.put("red", "#FF0000");
        colorMap.put("rosybrown", "#BC8F8F");
        colorMap.put("royalblue", "#4169E1");
        colorMap.put("saddlebrown", "#8B4513");
        colorMap.put("salmon", "#FA8072");
        colorMap.put("sandybrown", "#F4A460");
        colorMap.put("seagreen", "#2E8B57");
        colorMap.put("seashell", "#FFF5EE");
        colorMap.put("sienna", "#A0522D");
        colorMap.put("silver", "#C0C0C0");
        colorMap.put("skyblue", "#87CEEB");
        colorMap.put("slateblue", "#6A5ACD");
        colorMap.put("slategray", "#708090");
        colorMap.put("snow", "#FFFAFA");
        colorMap.put("springgreen", "#00FF7F");
        colorMap.put("steelblue", "#4682B4");
        colorMap.put("tan", "#D2B48C");
        colorMap.put("teal", "#008080");
        colorMap.put("thistle", "#D8BFD8");
        colorMap.put("tomato", "#FF6347");
        colorMap.put("turquoise", "#40E0D0");
        colorMap.put("violet", "#EE82EE");
        colorMap.put("wheat", "#F5DEB3");
        colorMap.put("white", "#FFFFFF");
        colorMap.put("whitesmoke", "#F5F5F5");
        colorMap.put("yellow", "#FFFF00");
        colorMap.put("yellowgreen", "#9ACD32");
        return colorMap;
    }
}
