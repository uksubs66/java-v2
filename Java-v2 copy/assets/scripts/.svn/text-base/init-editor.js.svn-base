/* 
 * Initialization for tinymce editor (only include on pages with tiny mce)
 */

/*************************************************************
 *
 * Initialize jQuery objects
 *  
 **************************************************************/
// jQuery start on DOM ready
$(document).ready(function() {

    // Activate advanced text editor
    $("textarea.text_editor.advanced").each(function(){
        
        $(this).foreTeesModal("pleaseWait");
        var alternate_options = {};
        try{    
            alternate_options = JSON.parse($(this).attr("data-ftjson"));
        }catch(e){
        // // console.log("bad json");
        }
        var editor_options = {
            // Location of TinyMCE script
            script_url : $.fn.foreTeesSession("get","editor_path"),
            oninit : $.proxy(function(){
                $(this).foreTeesModal("pleaseWait","close");
            },$(this)),

            // General options
            valid_elements : "*[*]",
            extended_valid_elements : "*[*]",
            element_format : "html",
            mode : "textareas",
            height : "400",
            width : "1000",
            theme : "advanced",
            //skin : "o2k7",
            fix_list_elements : true,
            
            // Plugins
            plugins : "safari,splitdiv,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,autoresize",
      
            // Theme options
            theme_advanced_buttons1 : "save,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,undo,redo,|,tablecontrols,|,removeformat,visualaid,|,charmap,insertdate,inserttime,hr,advhr,|,ltr,rtl,|,iespell,spellchecker,|,fullscreen",
            theme_advanced_buttons2 : "add_block_container_after,add_block_container_before,move_block_container_up,move_block_container_down,remove_block_container,|,add_slide_element_after,move_slide_element_up,move_slide_element_down,remove_slide_element,|,add_slide_container,move_slide_container_up,move_slide_container_down,remove_slide_container,|,add_text_block,remove_text_block,|,insertlayer,moveforward,movebackward,absolute,|,link,unlink,anchor,image,insertimage,|,cleanup,code,|,template",
            theme_advanced_buttons3 : "formatselect,fontselect,fontsizeselect,styleprops,|,bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote,|,sub,sup",    
            //theme_advanced_buttons3 : "",
            theme_advanced_buttons4 : "",
            theme_advanced_blockformats : "p,address,pre,h1,h2,h3,h4,h5,h6,blockquote",
            theme_advanced_styles : "White Box=white_box;Grey Box=grey_box;Rounded Corners=rounded_corners",
            
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_path : true,
            theme_advanced_statusbar_location : "bottom",
            theme_advanced_resizing : true,
            theme_advanced_fonts : "Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats",
            theme_advanced_font_sizes: "10px,12px,13px,14px,16px,18px,20px",
            font_size_style_values : "10px,12px,13px,14px,16px,18px,20px",

            // Example content CSS (should be your site CSS)
            content_css : "css/content.css",

            // Drop lists for link/image/media/template dialogs
            //template_external_list_url : "../assets/jquery/tiny_mce_lists/template_list.js",
            //external_link_list_url : "../assets/jquery/tiny_mce_lists/link_list.js",
            //external_image_list_url : "../assets/jquery/tiny_mce_lists/image_list.js",
            //media_external_list_url : "../assets/jquery/tiny_mce_lists/media_list.js",         
            
            //handle_node_change_callback : "ft_nodeChange",
            
            //forced_root_block: 'div',

            // Replace values for the template plugin
            //template_replace_values : {
            //    username : "Some User",
            //    staffid : "991234"
            //}
            
            // Template definitions
            template_templates : [
            {
                title : "Standard Widget",
                src : "../assets/editor_templates/standard_widget.html",
                description: "Adds a standard widget with example content"
            },
            {
                title : "Standard Widget With Heading",
                src : "../assets/editor_templates/standard_widget_with_heading.html",
                description: "Adds a standard widget with a heading and example content"
            },
            {
                title : "Standard Widget With List and Heading",
                src : "../assets/editor_templates/standard_widget_list_with_heading.html",
                description: "Adds a standard widget with an example heading and content"
            },
            {
                title : "Accuweather Wide Widget",
                src : "../assets/editor_templates/accuweather_widget_wide.html",
                description: "Adds a Wide Accuweather Widget"
            },
            {
                title : "Slide Show Widget",
                src : "../assets/editor_templates/slide_widget.html",
                description: "Adds a slide show widget"
            },
            {
                title : "Slide Show Block",
                src : "../assets/editor_templates/slide_block.html",
                description: "Adds a slide show block that can be used inside existing widgets"
            },
            {
                title : "White Box Block",
                src : "../assets/editor_templates/white_box.html",
                description: "Adds a simple white box with rounded corners (on newer browsers)."
            },
            {
                title : "Empty Block Container List",
                src : "../assets/editor_templates/block_container.html",
                description: "A collections of containers you can add content to."
            },
            {
                title : "Paragraph",
                src : "../assets/editor_templates/paragraph.html",
                description: "Adds a paragraph block to an empty container."
            },
            {
                title : "Block/Div",
                src : "../assets/editor_templates/div.html",
                description: "Adds a 'DIV' element."
            }
            
            /*,
            {
                title : "Wide Weather Widget",
                src : "../assets/editor_templates/weather_wide.html",
                description: "Adds a wide weather widget."
            }
                */
            ]
        }
        // Merge extra options with default options
        var options = $.extend(true,{},editor_options,alternate_options);
        
        /*
        var asset_map = $.fn.foreTeesSession("get","asset_name_map");
        
        if(options.css_includes){
            options.css_includes.replace("/sitewide.css", "/" + asset_map.sitewide_css);
            options.css_includes.replace("/sitewide_dining.css", "/" + asset_map.dining_css);
        }
        */
        // Activiate tinymce
        $(this).tinymce(options);
        
    });
    
    // Activate standard editor
    $("textarea.text_editor.standard").each(function(){
        
        $(this).foreTeesModal("pleaseWait");
        var alternate_options = {};
        try{    
            alternate_options = JSON.parse($(this).attr("data-ftjson"));
        }catch(e){
        // // console.log("bad json");
        }
        var editor_options = {
            // Location of TinyMCE script
            script_url : $.fn.foreTeesSession("get","editor_path"),
            oninit : $.proxy(function(){
                $(this).foreTeesModal("pleaseWait","close");
            },$(this)),

            // General options
            document_base_url : "http://www1.foretees.com/",

            mode : "textareas",
            theme : "advanced",
            //skin : "o2k7",
            // Plugins
            plugins : "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager",
      
            // Theme options
            theme_advanced_buttons1 : "cut,copy,paste,pastetext,pasteword,|,undo,redo,|,hr,advhr,|,link,unlink,anchor,image,insertimage,|,code",
            theme_advanced_buttons2 : "bold,italic,underline,strikethrough,|,forecolor,backcolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,bullist,numlist,|,outdent,indent,blockquote",
            theme_advanced_buttons3 : "formatselect,fontselect,fontsizeselect,styleprops",
            
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_resizing : true,
            theme_advanced_fonts : "Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Palatino Linotype=palatino linotype,palatino,book antiqua;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats",

            
            template_external_list_url : "js/template_list.js",
            external_link_list_url : "js/link_list.js",
            external_image_list_url : "js/image_list.js",
            media_external_list_url : "js/media_list.js"

        }
        // Merge extra options with default options
        var options = $.extend(true,{},editor_options,alternate_options);

        // Activiate tinymce
        $(this).tinymce(options);
        
    });


});

// Used by TinyMCE
function ft_nodeChange(editor_id, node, undo_index, unlo_levels, visial_aid, any_selection){
    //console.log(node.nodeName);
}