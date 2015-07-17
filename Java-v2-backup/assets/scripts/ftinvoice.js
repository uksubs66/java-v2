/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


ftinvoice = (function(){
    
    function _formatText(text){
        return text.replace(/(\r\n|\n|\r)/g,"<br />");
    }
    
    function _formatDate(stringDate){
        return ftcalendar.stringToDate(stringDate).format('mm/dd/yy');
    }
    
    function _formatCurrency(n,emptyZero){
        if(typeof n == "undefined" || (!n && emptyZero)){
            return '';
        } else if(typeof n == "string"){
            if(isNaN(n)){
               return n; 
            } else {
                n = parseFloat(n);
            }
        }
        return '$' + n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,");
    }
    
    function _getChildrenHeight($obj){
        var totalHeight = 0;
        $obj.children().each(function(){
            totalHeight = totalHeight + $(this).outerHeight();
        });
        return totalHeight;
    }
    
    var _display = function($obj,data){
        
        var defaultNote = "Thank you!";
        
        var page = 1, invoiceTemplate = ''
            +'<div class="ftInvoiceBeforePage"></div>'
            +'<div class="ftInvoicePage">'
            + '<div class="ftInvoiceHeader">'
            +  '<div class="ftInvoiceCompany">ForeTees, LLC<br />550 Village Center Drive<br />Suite 400<br />St. Paul, MN 55127-3004<br />651-486-0715<br />TIN #: 03-0493751</div>'
            +  '<div class="ftInvoiceBillTo ftInvoiceBlock"><div>Bill To:</div><div>'+_formatText(data.bill_to)+'</div></div>'
            +  '<div class="ftInvoiceLogo"><img src="/v5/assets/images/ftRwdLogoGolf.png" /></div>'
            +  '<div class="ftInvoiceType">Invoice</div>'
            +  '<div class="ftInvoiceInfo">'
            +   '<div class="ftInvoiceDueDate ftInvoiceBlock"><div>Due Date</div><div>'+_formatDate(data.due_date)+'</div></div>'
            +   '<div class="ftInvoiceDate ftInvoiceBlock"><div>Date</div><div>'+_formatDate(data.date)+'</div></div>'
            +   '<div class="ftInvoiceNumber ftInvoiceBlock"><div>Invoice No.</div><div>'+data.id+'</div></div>'
            +   '<div class="ftInvoiceTerms ftInvoiceBlock"><div>Terms</div><div>'+data.terms+'</div></div>'
            +   '<div class="ftInvoiceRepCode ftInvoiceBlock"><div>REP</div><div>'+((data.rep_codes)?data.rep_codes:'&nbsp;')+'</div></div>'
            +  '</div>'
            + '</div>'
            + '<div class="ftInvoiceTables">'
            +  '<div class="ftInvoiceColumns">'
            +   '<table class="ftInvoiceColumns">'
            +    '<thead><tr><th class="ftInvoiceDescription">Description</th><th class="ftInvoiceAmount">Amount</th></tr></thead>'
            +   '</table>'
            +   '<div class="ftInvoiceAmountDivider"></div>'
            +  '</div>'
            +  '<div class="ftInvoiceDetails">'
            +   '<table class="ftInvoiceDetails">'
            +    '<tbody></tbody>'
            +   '</table>'
            +   '<div class="ftInvoiceAmountDivider"></div>'
            +   '<div class="ftInvoiceLeftDivider"></div>'
            +   '<div class="ftInvoiceRightDivider"></div>'
            +  '</div>'
            +  '<div class="ftInvoiceTotals">'
            +   '<table class="ftInvoiceTotals">'
            +    '<tfoot>'
            +     '<tr class="ftInvoiceTax"><td class="ftInvoiceNotes" rowspan="4">'+(data.notes?data.notes:defaultNote)+'</td><td class="ftInvoiceTag">Tax</td><td class="ftInvoiceValue"></td></tr>'
            +     '<tr class="ftInvoiceTotal"><td class="ftInvoiceTag">Total</td><td class="ftInvoiceValue"></td></tr>'
            +     '<tr class="ftInvoiceAmountPaid"><td class="ftInvoiceTag">Amount Paid</td><td class="ftInvoiceValue"></td></tr>'
            +     '<tr class="ftInvoiceAmountDue"><td class="ftInvoiceTag">Balance Due</td><td class="ftInvoiceValue"></td></tr>'
            +    '</tfoot>'
            +   '</table>'
            +   '<div class="ftInvoiceAmountDivider"></div>'
            +  '</div>'
            +  '<div class="ftInvoiceFooter"><div class="ftInvoicePager">Page <span class="ftInvoicePageNumber"></span> of <span class="ftInvoicePageCount"></span></div></div>'
            + '</div>'
            +'</div>'
            +'<div class="ftInvoiceAfterPage"></div>';
        
        function __getInvoicePage($invoice){
            var $invoicePage = $(invoiceTemplate), $pageObj = $invoicePage.filter('.ftInvoicePage');
            
            if(data.voided){
                //console.log("VOIDED");
                $pageObj.append('<div class="ftInvoiceStamp ftInvoiceVoided"><div><span>VOID</span><span>'+_formatDate(data.voided)+'</span></div></div>');
            } else if(data.last_payment_date && !(data.amount_due > 0)){
                //console.log("PAID");
                $pageObj.append('<div class="ftInvoiceStamp ftInvoicePaid"><div><span>PAID</span><span>'+_formatDate(data.last_payment_date)+'</span></div></div>');
            } else if(data.days_past_due > 0){
                //console.log("PAST DUE");
                $pageObj.append('<div class="ftInvoiceStamp ftInvoicePastDue"><div><span>PAST DUE</span></div></div>');
            }
            $invoice.append($invoicePage);
            return $invoicePage;
        }
        
        var $invoiceContainer = $('<div class="ftInvoiceContainer"><div class="ftInvoice ftInvoiceBuilding"></div></div>'), pages = [];
        $obj.append($invoiceContainer);
        var tax_total = 0, total = 0, $invoice = $invoiceContainer.find('.ftInvoice'), $invoicePage = __getInvoicePage($invoice);
        var i, $tbody = $invoicePage.find('table.ftInvoiceDetails tbody'), $table = $invoicePage.find('table.ftInvoiceDetails'), $tableContainer = $invoicePage.find('div.ftInvoiceDetails');
        pages.push($invoicePage);
        
        for(i = 0; i < data.details.length; i++){
            var detail = data.details[i], detail_amount = detail.rate * detail.quantity, detail_tax = detail_amount * detail.tax_rate;
            tax_total += detail_tax;
            total += detail_amount + detail_tax;
            var $detail_row = $('<tr><td class="ftInvoiceDescription">'+_formatText(detail.description)+'</td><td class="ftInvoiceAmount">'+_formatCurrency(detail_amount)+'</td></tr>');
            $tbody.append($detail_row);
            if($table.height() > $tableContainer.height()){
                // Need to move detail to next page
                //console.log('move detail '+(i+1)+' from page '+page+' to page '+(page+1));
                page ++;
                
                $invoicePage = __getInvoicePage($invoice);
                $tbody = $invoicePage.find('table.ftInvoiceDetails tbody');
                $table = $invoicePage.find('table.ftInvoiceDetails'); 
                $tableContainer = $invoicePage.find('div.ftInvoiceDetails');
                $tbody.append($detail_row);
                pages.push($invoicePage);
            }
        }
        for(i = 0; i < pages.length; i++){
            var $page = pages[i];
            $page.find('.ftInvoicePageNumber').html(i+1);
            $page.find('.ftInvoicePageCount').html(page);
        }
        $invoicePage.find(".ftInvoiceTax .ftInvoiceValue").html(_formatCurrency(data.tax_total));
        $invoicePage.find(".ftInvoiceTotal .ftInvoiceValue").html(_formatCurrency(data.gross_total));
        $invoicePage.find(".ftInvoiceAmountPaid .ftInvoiceValue").html(_formatCurrency(data.payment_amount));
        $invoicePage.find(".ftInvoiceAmountDue .ftInvoiceValue").html(_formatCurrency(data.amount_due));
        
        $obj.find('.ftInvoice').removeClass("ftInvoiceBuilding").addClass("ftInvoiceComplete");
        
        //window.status = "ftinvoicecomplete";
    }
    
    var _loadAndDisplay = function($obj,invoice_id,callback){
        if(typeof callback != "function"){
            callback = function(){};
        }
        ftapi.get({
            command:'invoice', 
            id:invoice_id, 
            success:function(result){
                _display($obj,result);
                callback(result);
            },
            error: function(errorTxt){
                ftapi.error("API Error:\n\n"+errorTxt, 'ftinvoice.loadAndPrint');
                callback();
            }
        });
    }
    
    var _displayJsonString = function($obj,invoice_json){
        var invoice_data;
        try{
            invoice_data = JSON.parse(invoice_json);
        } catch(e) {
            invoice_data = false;
        }
        if(!invoice_data){
            return false;
        } else {
            _display($obj,invoice_data);
            return invoice_data;
        }
    }
    
    var _loadAndPrint = function(invoice_id,callback){
        
        ftapi.get({
            command:'invoice', 
            id:invoice_id, 
            success:function(result){
                $('.ftInvoiceHidden').remove();
                var $ftHidden = $('<div class="ftInvoiceHidden"></div>');
                $('body').append($ftHidden);
                 _display($ftHidden,result);
                 var invoiceHtml = $ftHidden.html();
                 var parentWindow = window;
                $('#ftInvoicePrintFrame').remove();
                var $iframe = $('<iframe id="ftInvoicePrintFrame" name="ftInvoicePrintFrame"/>').load(function(){
                    var $iframeinner = $(this), $iframe_body = $iframeinner.contents().find('body');
                    $iframe_body.append(invoiceHtml);
                    $iframe_body.addClass('ftInvoiceBody');
                    var $head = $iframeinner.contents().find("head");
                    $head.append('<title>ForeTees Invoice #'+invoice_id+'</title>');
                    $.ajax("/v5/assets/stylesheets/ftinvoice.css",
                    {
                        //cache: true,
                        success:function(cssresponse){
                            $head.append('<style id="ftinvoicecss"></style>');
                            $head.find('#ftinvoicecss').text(cssresponse);
                            
                            setTimeout(function(){
                                
                                if(document.all || window.PointerEvent){
                                    // IE needs to print this way
                                    var target = parentWindow.document.getElementById('ftInvoicePrintFrame');
                                    target.focus();
                                    target.contentWindow.document.execCommand('print', false, null);
                                } else {
                                    // Firefox needs to print this way.  Webkit should work as well
                                    parentWindow.frames['ftInvoicePrintFrame'].focus();
                                    parentWindow.frames['ftInvoicePrintFrame'].print();
                                } //else {
                                 //   target.contentWindow.print();
                                //}
                                callback(result);
                            },100);
                        },
                        error:function(xhr, status, error){
                            ftapi.error("API Error:\n\n"+xhr.responseText, 'ftinvoice.loadAndPrint');
                            callback();
                        }
                    });
                    
                }).appendTo('body');
            },
            error: function(errorTxt){
                ftapi.error("API Error:\n\n"+errorTxt, 'ftinvoice.loadAndPrint');
                callback();
            }
        });
    }
    
    var _downloadPdf = function(invoice_id){
        // First, get a single use key, so our user's auth token isn't leaked in the browser's download history
        ftapi.get({
            command:"singleUseKey",
            success:function(singleUseKey){
                // Now use the key to request the file
                ftapi.downloadFile(ftapi.uriWithToken({command:'invoiceGetPdf',id:invoice_id},{singleUseKey:singleUseKey}));
            }
        });
        
    }
    
    return {
        display:_display,
        downloadPdf:_downloadPdf,
        displayJsonString:_displayJsonString,
        loadAndDisplay:_loadAndDisplay,
        loadAndPrint:_loadAndPrint
    }
    
})();
