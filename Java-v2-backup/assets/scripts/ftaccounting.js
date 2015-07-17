/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


ftaccounting = (function(){
    
    var _exec = function(action,source){
        switch(action){
            case 'users_manage':
                _users.manage();
                break;
            
            case 'tax_rates_manage':
                _taxRates.manageRate();
                break;
                
            case 'tax_groups_manage':
                _taxRates.manageGroup();
                break;
                
            case 'invoice_items_manage':
                _invoiceItems.manageItems();
                break;
                
            case 'invoice_item_types_manage':
                _invoiceItems.manageTypes();
                break;
                
            case 'club_invoicing_manage':
                _clubs.manage();
                break;
                
            case 'invoices_list_unpaid':
                _invoicing.manageInvoices({mode:'unpaid'});
                break;
                
            case 'invoices_list_outstanding':
                _invoicing.manageInvoices({mode:'pastdue'});
                break;
                
            case 'invoices_list_unsent':
                _invoicing.manageInvoices({mode:'unsent'});
                break;
                
            case 'invoices_run_rules':
                _invoicing.manageInvoices({mode:'run_rules'});
                break;
                
            case 'invoices_list_all':
                _invoicing.manageInvoices({mode:'list_all'});
                break;
                
            case 'commission_unpaid':
                _reports.unpaidCommission();
                break;
                
            case 'commission_by_date':
                _reports.commissionBySalesPersonAndDate();
                break;
                
            case 'settings_foretees':
                _settings.manageForeteesSettings();
                break;
                
            case 'settings_foretees_announcements':
                _settings.manageForeteesAnnouncements();
                break;
            
            default:
                alert('Unimplemented command: '+action);
                break;
        }
    }
    
    var _defaults = {
        tax_group_id:1
    }
    
    var _objects = {
        list_menu_id:'fta-wl'
    }
    
    var _ftaInitClass = 'fta-init';
    
    var _init = function(obj){
        var  hrefPrefix = '#ftact!', $fta_menu_items = $(obj).find('[href^="'+hrefPrefix+'"]');
        $fta_menu_items.filter(function(){
            return !($(this).data(_ftaInitClass));
        })
        .click(function(e){
            var o = $(this);
            _exec(o.attr('href').split('!')[1], o);
            e.preventDefault();
        })
        .data(_ftaInitClass,true);
        if($fta_menu_items.length){
            var list_menu_id = 'fta-wl', $list_menu = $('#'+list_menu_id);

            if(!$list_menu.length){
                // create the list menu
                $list_menu = $('<li aria-haspopup="true" class="topnav_item ftRightAlignMenu" id="'+list_menu_id+'"><a href="#" class=""><span class="topnav_item">Window</span></a><ul class="fta-list-container"></ul></li>');
                $list_menu.click(function(e){return false});
                $('#rwdNav>ul').append($list_menu);

            }
            
        }
    }
    
    function _objOrNull(obj){
        if(typeof obj == "string"){
            obj = obj.trim();
        } 
        return obj?obj:null;
    }
            
    
    function _getDialogGridContiner(id, title){
        var result = $('#'+id);
        if(result.length > 0){
            return result;
        } else{
            result = $('<div id="'+id+'" title="'+title+'" style="overflow:hidden;"><div class="ftact-grid"></div></div>');
            $('body').append(result);
            return result;
        } 
    }
    
    function _setDialogTitle($dialog,title){
        var window_id = $dialog.attr('id'), 
        list_menu_id = _objects.list_menu_id, list_id = list_menu_id + '-' + window_id;
        $dialog.dialog("option","title",title );
        $('#'+list_id+' .fta-list-item-text').text(title);
    }
    
    function _openDialog($dialog, $grid, gridModel, width, height, position, beforeClose){
        
        var window_id = $dialog.attr('id'), selected_cls = 'fta-selected',
        list_menu_id = _objects.list_menu_id, list_id = list_menu_id + '-' + window_id, $list_menu = $('#'+list_menu_id);
        
        var wwidth = $(window).width() - 8, wheight = $(window).height() - 8;
        if(width > wwidth){
            width = wwidth;
        }
        if(height > wheight){
            height = wheight;
        }
        var dlgObj = {}, pulse = false;

        if(!$dialog.hasClass('ui-dialog-content')){
            dlgObj = {
                open: function(){
                    //console.log('open');
                    
                    if(!$('#'+list_id).length){
                        // This dialog window isn't in the window list yet.
                        var title = $dialog.attr('title');
                        title = (title?title:$dialog.closest('.ui-dialog').find('.ui-dialog-title').first().text());
                        var $list_menu_item = $('<li aria-haspopup="false" class="fta-list-item" id="'+list_id+'"><a href="#" class=""><span class="fta-list-item-text">'+title+'</span></a></li>');
                        var $list_items = $list_menu.find('.fta-list-item');
                        $list_menu_item.click(function(e){
                            _openDialog($dialog, $grid, gridModel, width, height, position);
                        });
                        var didit = false;
                        $list_items.each(function(){
                            var $el = $(this);
                            if($el.find('.fta-list-item-text').text() > title && !didit){
                                didit = true;
                                $el.before($list_menu_item);
                            }
                        });
                        if(!didit){
                            $list_menu.find('.fta-list-container').append($list_menu_item);
                        }
                        $('.fta-list-item').removeClass(selected_cls)
                        $('#'+list_id).addClass(selected_cls);
                        
                    }
                    $grid.pqGrid(gridModel);
                },
                close:function(){
                    $('#'+list_id).remove();
                    $grid.pqGrid('destroy');
                },
                focus:function(){
                    //console.log("focus:"+list_id);
                   // var $lastdialog = $('.ui-dialog').filter(function(){
                   //     return $(this).css('display') != 'none';
                   // }).last();
                   // var $thisdialog = $(this).closest('.ui-dialog');
                   // setTimeout(function(){$lastdialog.after($thisdialog);},1000);
                    
                    $('.fta-list-item').removeClass(selected_cls)
                    $('#'+list_id).addClass(selected_cls);
                }
            }
            dlgObj.height = height;
            dlgObj.width = width;
            if(typeof beforeClose == "function"){
                dlgObj.beforeClose = beforeClose;
            }
            if(position){
                dlgObj.position = position;
            }
        } else {
            pulse = true;
        }
        $dialog.dialog(dlgObj);
        if(pulse){
            $dialog.closest('.ui-dialog').addClass('ftWindowSelectPulse').removeClass('ftWindowSelectPulse',500);
        }
    }
    
    function _promptYesNo($obj,options){
        
        var onCancel = options.onCancel, 
        onContinue = options.onContinue, 
        cancelButton = options.cancelButton,
        continueButton = options.continueButton;
        
        cancelButton = cancelButton?cancelButton:'No';
        continueButton = continueButton?continueButton:'Yes';
        
        var opt = {
            title:options.title,
            alertMode:false,
            message:options.message,
            closeButton:cancelButton,
            onClose:function($modalObj){
                if(typeof onCancel == "function"){
                    onCancel.call($modalObj);
                }
            },
            allowContinue:true,
            continueButton:continueButton,
            continueAction:function($modalObj){
                if(typeof onContinue == "function"){
                    onContinue.call($modalObj);
                }
            }
        }
        
        $obj.foreTeesModal('alertNotice',opt);
    }
    
    function _setDefaults(target,source){
        source.forEach(function(k){
            if(!target[k]){
                target[k] = source[k];
            }
        });
    }
    
    function _getAllRowData($grid){
        return $grid.pqGrid("option","dataModel.data");
    }
    
    function _isEditing($grid){
        var rows = $grid.pqGrid('getRowsByClass', {
            cls: 'pq-row-edit'
        });
        if(rows.length > 0){
            var rowIndx = rows[0].rowIndx;
            $grid.pqGrid('goToPage',{
                rowIndx:rowIndx
            });
            $grid.pqGrid('editFirstCellInRow', {
                rowIndx:rowIndx
            });
            return true;
        }
        return false;
    }
    
    function _getTitleOfColumn($grid, dataIndx){
        var col, colModel = $grid.pqGrid('getColModel');
        for(var i = 0; i < colModel.length; i++){
            col = colModel[i];
            if(col.dataIndx == dataIndx){
                return col.title;
            }
        }
        return '';
    }
    
    //called by delete button.
    function _deleteRow(rowIndx, $grid, callback) {
        $grid.pqGrid("addClass", {
            rowIndx: rowIndx, 
            cls: 'pq-row-delete'
        });
        
        var rowData = $grid.pqGrid("getRowData", {
            rowIndx: rowIndx
        }),id = $grid.pqGrid("getRecId", {
            rowIndx: rowIndx
        }),message = "Are you sure you want to delete ", 
        idCol = _getTitleOfColumn($grid, 'id');
        
        if(rowData.name){
            message += rowData.name + "?";
        } else if(rowData.title){
            message += rowData.title + "?";
        } else if(idCol){
            message += idCol + ' ' + rowData.id + "?";
        } else {
            message += "row #" + (rowIndx + 1) + "?";
        }
        _promptYesNo($grid,{
            title:"Confirm Deletion",
            message:message,
            onContinue:function(){
                setTimeout(function(){callback(rowIndx, $grid, rowData, id)},1);
                $(this).dialog("close");
            },
            onCancel:function(){
                $grid.pqGrid("removeClass", {
                    rowIndx: rowIndx, 
                    cls: 'pq-row-delete'
                });
            }
        });
        
    }
    
    function _addRow($grid,rowData){
        if(_isEditing($grid)){
            return;
        }
        // append empty row in first row.
        var rowOptions = {
            rowIndxPage:0,
            rowData:rowData,
            checkEditable:false
        }
        if(typeof rowData == 'function'){
            rowOptions.rowData = rowData();
        }
        $grid.pqGrid('addRow',rowOptions);
        $grid.pqGrid('refreshView');

        var $tr = $grid.pqGrid('getRow',{
            rowIndxPage:0
        });
        if($tr){
            $tr.find('button.edit_btn').first().click();
        }
    }
    
    var _defaultRowDelete = function(e, ui, options){
        if(ui.isEditing){
            return;
        }
        _deleteRow(ui.rowIndx,ui.$grid,options.deleteRecord);
    }
    
    
    var _defaultRowEdit = function(e, ui, options){
        if(ui.isEditing){
            return;
        }
        ui.$grid.pqGrid("addClass", {
            rowIndx: ui.rowIndx, 
            cls: 'pq-row-edit'
        });
        //_editRow(ui.rowIndx, ui.$grid, options.updateRecord);
    }
    
    var _defaultRowCancel = function(e, ui, options){
        ui.$grid.pqGrid("quitEditMode")
            .pqGrid("removeClass", {
                rowIndx: ui.rowIndx, 
                cls: 'pq-row-edit'
            })
            .pqGrid("rollback");
    }
    
    var _defaultRowSave = function(e, ui, options){
        
        var $grid = ui.$grid, rowIndx = ui.rowIndx, rowData = ui.rowData;
        
        if ($grid.pqGrid("saveEditCell") == false){
            return ;
        }
        var isValid = $grid.pqGrid("isValid",{
            rowIndx:rowIndx
        }).valid;
        if(!isValid){
            return ;
        }
        if($grid.pqGrid("isDirty")){
            // Save the date
            var recIndx = $grid.pqGrid("option","dataModel.recIndx"),
            type;

            if(rowData[recIndx] == null){
                // Adding record
                type = 'add';
            } else {
                // Updating record
                type = 'update';
            }
            options.updateRecord(rowIndx, $grid, rowData, type);
            return;
        } else {
            // Get out of edit mode
            $grid.pqGrid("quitEditMode");
            $grid.pqGrid("removeClass",{
                rowIndx:rowIndx, 
                cls: 'pq-row-edit'
            });
            $grid.pqGrid("refreshRow",{
                rowIndx: rowIndx
            });
        }
        
    }
    
    var _viewRuleData = function(e, ui, options){
        var $this = $(this), $tgrid = ui.$grid, $tr = ui.$tr, rowIndx = ui.rowIndx, rowData = ui.rowData,
        dataIndx = "invoicing_rule_type_data";
        if(ui.isEditing){
            return;
        } else {
            //console.log(rowData);
            var save_data, lock_edit = $this.hasClass('fta-lock-edit'), no_view = $this.hasClass('fta-no-view');
            if(typeof options.getRuleTypeDataValues == "function"){
                options.getRuleTypeDataValues(ui, options);
            }   
            setTimeout(function(){
                if(_isEditing($tgrid) || no_view){
                    return;
                }
                _invoicingRuleTypeModal({
                    club_invoicing_rule_detail_id:options.club_invoicing_rule_detail_id,
                    club_invoicing_rule_id:options.club_invoicing_rule_id,
                    club_invoicing_id:options.club_invoicing_id,
                    invoice_item_id:options.invoice_item_id,
                    date_start:options.date_start,
                    invoicing_rule_type_data_json:options.invoicing_rule_type_data_json,
                    $grid:$tgrid,
                    rowData:rowData,
                    load_counts:!$this.hasClass('fta-skip-counts'),
                    lock_edit:lock_edit,
                    save:save_data
                });
            },1);
            if(lock_edit){
                save_data = function(invoicing_rule_type_data){
                    var rowData = {
                        rowIndx:rowIndx,
                        row:{}
                    }
                    rowData.row[dataIndx] = JSON.stringify(invoicing_rule_type_data);
                    $tgrid.pqGrid('updateRow',rowData);
                    return true;
                }
            }
        }
    }
    
    var _viewRuleDataButtonInit = function(){
        return {
            selector:'.rule_type_data_view_btn',
            icon:'ui-icon-newwin',
            click: _viewRuleData
        }
    }
    
    function _getUiObject(obj){
        // Build a "ui" like object for use in event calls
        var $this = $(obj),
        $grid = $this.closest('.pq-grid'),
        $tr = $this.closest('tr'),
        $cell = $this.closest('td'),
        rowIndx = $grid.pqGrid('getRowIndx',{
            $tr:$tr
        }).rowIndx,
        rowData = $grid.pqGrid('getRowData',{
            rowIndx:rowIndx
        });
        return {rowIndx:rowIndx,rowData:rowData,isEditing:_isEditing($grid),$cell:$cell,$tr:$tr,$grid:$grid};
    }
    
    function _selectGridElements($grid, selector){
        // Select only the elements that are part of this grid (exclude elements that are in a child grid)
        var depth = $grid.children().first().parents(".pq-grid").length;
        return $grid.find(selector).filter(function(){
                return $(this).parents(".pq-grid").length == depth;
            });
    }
    
    function _getBreadCrumb($grid, bread_crumb){
        
        if(!$.isArray(bread_crumb)){
            bread_crumb = [];
        }
        
        var title;
        var $parent = $grid.parent().closest('.pq-grid');
        if($parent.length){
            var $ptr = $grid.closest('tr'),
            pRowIndx = $parent.pqGrid('getRowIndx',{
                $tr:$ptr
            }),
            pRowData = $parent.pqGrid('getRowData',{
                rowIndx:pRowIndx.rowIndx
                });
            title = pRowData.name;
            if(!title){
                title = pRowData.id;
            }
            if(title){
                bread_crumb.unshift(title);
            }
            return _getBreadCrumb($parent, bread_crumb);
        } else {
            title = $grid.closest('.ui-dialog').find('.ui-dialog-title').text();
            if(title){
                bread_crumb.unshift(title);
            }
            return bread_crumb.join(' / ');
        }
        
    }
    
    function _getMaximizedGrids(){
        return $('.pq-grid').filter(function(){
            return $(this).css('position') == 'fixed'
        });
    }
    
    function _activateGridElements(options){
        
        options.$grid.not('.'+_ftaInitClass).addClass(_ftaInitClass).on('pqgridrefresh pqgridrefreshrow', function(){
            
            /*
            if(options.parentRefresh){
                console.log('stop!');
                options.parentRefresh = false;
                return;
            }
            */

            var $grid = $(this),
            $gridButtons = _selectGridElements($grid, '.pq-grid-cell button:not(.'+_ftaInitClass+')').addClass(_ftaInitClass);
            
            if(_getMaximizedGrids().length){
                $('body').addClass('fta-has-maximized-grids');
            } else {
                $('body').removeClass('fta-has-maximized-grids');
            }
            
            if($grid.css('position') == 'fixed'){
                //if(!$grid.data('fta-show-bottom')){
                //    $grid_bottom.addClass('fta-no-footer');
                //}
                $grid.pqGrid('option','title',_getBreadCrumb($grid));
                $grid.pqGrid('option','showTitle',true);
                $grid.data('fta-did-max',true);
                $grid.pqGrid('option','height', "100%-39");
            } else {
                if(!$grid.data('fta-did-max')){
                    //$grid.data('fta-show-bottom',$grid.pqGrid('option','showBottom'));
                    $grid.data('fta-grid-height',$grid.pqGrid('option','height'));
                }
                if(typeof $grid.data('fta-grid-height') != "undefined"){
                    //$grid.pqGrid('option','showBottom', $grid.data('fta-show-bottom'));
                    $grid.pqGrid('option','height', $grid.data('fta-grid-height'));
                }
                $grid.pqGrid('option','showTitle',false);
            }
            
            
            
            
            // Get buttons for this grid only (not children's buttons)
            $grid.find('.pq-grid-top .pq-slider-icon:not(.fta-expand-fix)').first().addClass('fta-expand-fix').click(function(){
                // Force grid to refresh after expansion.
                var $tgrid = $(this).closest('.pq-grid');// $tgrids = $tgrid.find('.pq-grid');
                setTimeout(function(){$tgrid.pqGrid('refreshView');},1);
                //setTimeout(function(){$(this).pqGrid('refreshView')},1);
            });
            
            // Set delete button behavior
            $gridButtons.filter('.fta-btn-delete-row').button({
                icons:{
                    primary:'ui-icon-trash'
                },
                text:false
            })
            .unbind('click')
            .bind('click', function(e){
                
                if(typeof options.ondelete == "function"){
                    options.ondelete.call(this,e,_getUiObject(this),options);
                } else {
                    _defaultRowDelete.call(this,e,_getUiObject(this),options);
                }
            })
            
            // Set edit row buttons
            $gridButtons.filter('.edit_btn').button({
                icons:{
                    primary:'ui-icon-pencil'
                },
                text:false
            })
            .unbind('click')
            .bind('click', function(e){
                if(typeof options.onedit == "function"){
                    options.onedit.call(this,e,_getUiObject(this),options);
                } else {
                    _defaultRowEdit.call(this,e,_getUiObject(this),options);
                }
                //return;
            });
            
            // Set save row buttons
            $gridButtons.filter('.fta-btn-save-row').button({
                icons:{
                    primary:'ui-icon-check'
                },
                text:false
            })
            .unbind('click')
            .bind('click', function(e){
                if(typeof options.onsave == "function"){
                    options.onsave.call(this,e,_getUiObject(this),options);
                } else {
                    _defaultRowSave.call(this,e,_getUiObject(this),options);
                }
            });
            
            // Set cancel row buttons
            $gridButtons.filter('.fta-btn-cancel-row').button({
                icons:{
                    primary:'ui-icon-cancel'
                },
                text:false
            })
            .unbind('click')
            .bind('click', function(e){
                if(typeof options.onedit == "function"){
                    options.oncancel.call(this,e,_getUiObject(this),options);
                } else {
                    _defaultRowCancel.call(this,e,_getUiObject(this),options);
                }
            });
            
            // Set other button elements
            var initButtons = options.initButtons, initButton;
            if ($.isArray(initButtons)){
                for(var i = 0; i < initButtons.length; i++){
                    initButton = initButtons[i];
                    $gridButtons.filter(initButton.selector).button({
                        icons:{
                            primary:initButton.icon
                        },
                        text:initButton.text
                    })
                    .unbind('click')
                    .bind('click', function(e){
                        initButton.click.call(this,e,_getUiObject(this),options);
                    });
                }
            }
            
            /*
            // Refresh parents too
            var $pdiag = $grid.closest('.ui-dialog');
            if($pdiag.length){
                var $pscroll = $pdiag.find('.ui-dialog-content>.pq-grid>.pq-grid-center>.pq-vscroll'),
                $pcont = $pdiag.find('.ui-dialog-content>.pq-grid>.pq-grid-center>.pq-grid-cont-outer>.pq-grid-cont>.pq-grid-cont-inner'),
                $ptbl = $pdiag.find('.ui-dialog-content>.pq-grid>.pq-grid-center>.pq-grid-cont-outer>.pq-grid-cont>.pq-grid-cont-inner>.pq-grid-table'),
                pdisp = $pscroll.css('display'),
                pconh = $pcont.height(),
                ptblh = $ptbl.height();
                //console.log('display:'+$pscroll.css('display'));
                if((pdisp == "none" && pconh < ptblh)){
                    //console.log('Refresh parent to force scrolbar to show! (must be a pqgrid bug)');
                    setTimeout(function(){$pdiag.find('.ui-dialog-content>.pq-grid').pqGrid('refreshView');},1);
                }
            }
            */
           
           // Do stuff to row headers
           _selectGridElements($grid, 'tr.pq-grid-header-search-row td.pq-grid-col').removeClass('ftaSmallHeader').filter(function(){return $(this).width() < 160}).addClass('ftaSmallHeader');
           //console.log('Height:'+_selectGridElements($grid, '.pq-grid-header-table').height());

        });
    }
    
    
    function _rollBackDelete($grid, rowData, rowIndx){
        $grid.pqGrid("removeClass", {
            rowData: rowData, 
            cls: 'pq-row-delete'
        });
        $grid.pqGrid("rollback");
    }
    
    function _commitDelete($grid, rowData, rowIndx){
        //$grid.pqGrid("rollback");
        //console.log("rowData");
        //console.log($grid.pqGrid("getRowData",{rowIndx:rowIndx}));
        //console.log(rowData);
        //if(!rowData.pq_detail){
        //    delete rowData.pq_detail;
        //}
        //$grid.pqGrid("updateRow", {
        //    rowIndx: rowIndx, row:{}
        //});
        //console.log("didUpdate");
        try{
            $grid.pqGrid("deleteRow", {
                rowIndx: rowIndx
            });
        } catch(e){
            //console.log("Error deleting.  Bug in pqGrid library?");
        }
        
        $grid.pqGrid("commit");
    //$grid.pqGrid("refreshDataAndView");
    }
    
    function _commitEdit($grid, rowData, rowIndx, type, response){
        $grid.pqGrid("updateRow",{
            rowIndx:rowIndx,
            row:response,
            track:false,
            checkEditable:false
        });
        $grid.pqGrid("commit",{
            type:type
        });
        
        $grid.pqGrid("refreshRow",{
            rowIndx:rowIndx
        });
        $grid.pqGrid("quitEditMode");
        $grid.pqGrid("removeClass",{
            rowIndx:rowIndx, 
            cls: 'pq-row-edit'
        });
        $grid.pqGrid("refreshRow",{
            rowIndx: rowIndx
        });
    }
    
    function _buildLookup(array){
        var result = {
            byId:{},
            select:[],
            list:array
        },
        byId = result.byId,
        select = result.select;
        array.forEach(function(r){
            byId[r.id] = r;
            var option = {};
            option[r.id] = r.name;
            select.push(option);
        })
        return result;
    }
    
    function _remapObject(source, field_map, reverse){
        var result = {};
        if(!reverse){
            field_map.forEach(function(fm){
                result[fm[1]] = source[fm[0]];
            });
        } else {
            field_map.forEach(function(fm){
                result[fm[0]] = source[fm[1]];
            });
        }
        return result;
    }
    
    function _filterObject(source, filter){
        var result = {};
        filter.forEach(function(f){
            result[f] = source[f];
        });
        return result;
    }
    
    function _setObjectWithObject(source, target){
        for(var key in source){
            if(typeof target[key] != "undefined"){
                target[key] = source[key];
            }
        }
    }
    
    function _refreshGrid($grid){
        $grid.pqGrid("refreshDataAndView");
    }
    
    
    function _generateUUID(){
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    }
    
    function _undoLast($grid){
        $grid.pqGrid("refreshView"); 
        $grid.pqGrid('history',{method:'undo'}); 
        $grid.pqGrid("refreshView"); 
    }
    
    function _updateRow($grid, rowIndx, rowData, undo_last){
        //setTimeout(function(){
            $grid.pqGrid("refreshView"); // Update the grid (gets the history state in order)
            if(undo_last){
                $grid.pqGrid('history',{
                    method:'undo'
                }); // Roll back the previous change and re-apply it with all others so this all happens in one go
            }
            $grid.pqGrid('updateRow',{
                rowIndx:rowIndx, 
                row:rowData
            });
            $grid.pqGrid("refreshView"); // Update the grid again (again, to deal with the history state)
        //},1);
        
    }
    
    function _getCurrentValueFromDataIndx(dataIndx, rowData, $grid){
        var editCellData = $grid.pqGrid("getEditCellData"), $editCell = $grid.pqGrid("getEditCell"), value = rowData[dataIndx];
        if(editCellData != null){
            var editInd = $grid.pqGrid("getCellIndices",$editCell);
            if(editInd.dataIndx == dataIndx){
                value = editCellData;
            }
        }
        return value;
    }
    
    function _makeCheckbox(label, name, value, checked, readonly){
        return '<label><input type="checkbox" name="'+name+'" value="'+value+'"'+(checked?' checked':'')+(readonly?' disabled':'')+'> '+label+'</label>';
    }
    
    function _inputWraper(label, html, cls){
        return '<div class="fta-input-wrapper'+(cls?' '+cls:'')+'"><label><span>'+label+'</span>'+html+'</label></div>';
    }
    
    
    function _invoicingRuleTypeModal(options, reentry, force_recount, unlocked){
        
        var $body = $('body');
        $body.foreTeesModal('pleaseWait');
        
        //console.log(options);
        //console.trace();
        

        //function __parseRuleData(invoicing_rule_type_data_json, invoicing_rule_type){
        function __parseRuleData(invoicing_rule_type_data_json){
            var invoicing_rule_type_data;
            try{
                invoicing_rule_type_data = JSON.parse(invoicing_rule_type_data_json);
            } catch(e) {
                invoicing_rule_type_data = false;
            }
            if(invoicing_rule_type_data && $.isArray(invoicing_rule_type_data.member_types)){
            } else {
                return false;
            }
            //delete invoicing_rule_type.last_error;
            //delete invoicing_rule_type_data.last_error;
            //invoicing_rule_type_data.invoicing_rule_type = invoicing_rule_type;
                                
            return invoicing_rule_type_data;
        }
        
        var //rowData = options.rowData,
            //$grid = options.$grid,
            //club_invoicing_id = (options.club_invoicing_id?options.club_invoicing_id:rowData.club_invoicing_id),
            //club_invoicing_rule_id = (options.club_invoicing_rule_id?options.club_invoicing_rule_id:rowData.club_invoicing_rule_id),
            //club_invoicing_rule_detail_id = (options.club_invoicing_rule_detail_id?options.club_invoicing_rule_detail_id:rowData.club_invoicing_rule_detail_id),
            //invoice_item_id = (options.invoice_item_id?options.invoice_item_id:_getCurrentValueFromDataIndx('invoice_item_id', rowData, $grid)),
            club_invoicing_id = options.club_invoicing_id,
            club_invoicing_rule_id = options.club_invoicing_rule_id,
            club_invoicing_rule_detail_id = options.club_invoicing_rule_detail_id,
            invoice_item_id = options.invoice_item_id,
            date_start = options.date_start,
            callback = options.save,
            onerror = options.error,
            lock_edit = options.lock_edit,
            allowEdit = typeof callback == "function" && (!lock_edit || unlocked),
            isEditable = false;
        
        if(typeof onerror != "function"){
            onerror = function(){}
        }
        
        if(!invoice_item_id){
            $body.foreTeesModal('pleaseWait','close');
            return;
        }
        
        var //invoicing_rule_type_id = _lookups.invoiceItemByClubInvoicingId[club_invoicing_id].byId[invoice_item_id].invoicing_rule_type_id,
            //invoicing_rule_type = _lookups.invoicingRuleType.byId[invoicing_rule_type_id],
            invoicing_rule_type_data_json = options.invoicing_rule_type_data_json,
            invoicing_rule_type_data = options.invoicing_rule_type_data, 
            command1  = 'invoicingRuleTypeDataByClubInvoicingRuleDetailId', 
            command2  = 'invoicingRuleTypeDataByClubInvoicingRuleId', 
            command3  = 'invoicingRuleTypeDataByClubInvoicingId',
            command = (club_invoicing_rule_detail_id?command1:(club_invoicing_rule_id?command2:command3)),
            command_id = (club_invoicing_rule_detail_id?club_invoicing_rule_detail_id:(club_invoicing_rule_id?club_invoicing_rule_id:club_invoicing_id));
            
        if(invoicing_rule_type_data 
            && invoicing_rule_type_data.invoicing_rule_type 
            && invoicing_rule_type_data.billable_count_details && (!force_recount || reentry)){
            //We have what we need.
            $body.foreTeesModal('pleaseWait','close');
        } else if(!reentry) {
            if(!invoicing_rule_type_data){
                //invoicing_rule_type_data = __parseRuleData(invoicing_rule_type_data_json, invoicing_rule_type);
                invoicing_rule_type_data = __parseRuleData(invoicing_rule_type_data_json);
            }
                
            if(invoicing_rule_type_data){
                
                if(options.load_counts || force_recount || !invoicing_rule_type_data.billable_count 
                    || !invoicing_rule_type_data.invoice_item){
                    ftapi.get({
                        command:command,
                        id:command_id,
                        detail_id:invoice_item_id,
                        data:JSON.stringify(invoicing_rule_type_data),
                        force_refresh:true,
                        date_start:date_start,
                        success:function(invoicing_rule_type_data){
                            options.invoicing_rule_type_data = invoicing_rule_type_data;
                            _invoicingRuleTypeModal(options, true);
                            $body.foreTeesModal('pleaseWait','close');
                        }
                    },{
                        //ccq:true,
                        beforeError:function(){
                            onerror();
                            $body.foreTeesModal('pleaseWait','close');
                        }
                    });
                    return;
                }
            } else {
                ftapi.get({
                    command:command,
                    id:command_id,
                    detail_id:invoice_item_id,
                    date_start:date_start,
                    success:function(invoicing_rule_type_data){
                        options.invoicing_rule_type_data = invoicing_rule_type_data;
                        _invoicingRuleTypeModal(options, true);
                        $body.foreTeesModal('pleaseWait','close');
                    }
                },{
                    //ccq:true,
                    beforeError:function(){
                        onerror();
                        $body.foreTeesModal('pleaseWait','close');
                    }
                });
                return;
            }
        } else {
            ftapi.error("Unexpected condition [ACCTN001]: Please notify your systems administrator.");
            $body.foreTeesModal('pleaseWait','close');
            return;
        }
        
        
        $body.foreTeesModal('pleaseWait','close');
        
        
        var invoicing_rule_type = invoicing_rule_type_data.invoicing_rule_type;
                                    
        var use_membership_types = invoicing_rule_type_data.use_membership_types,
        
        use_member_types = invoicing_rule_type_data.use_member_types,
        membership_types = invoicing_rule_type_data.membership_types,
        member_types = invoicing_rule_type_data.member_types,
        selected_mships = {}, selected_mtypes = {}, mship_html = '', mtype_html = '', key, content ='';
        
        if(!use_membership_types){
            ftapi.error("Unexpected condition [ACCTN002]: Please notify your systems administrator.");
            return;
        }
        
        
        use_membership_types.forEach(function(e){
            selected_mships[e.name.toLowerCase()] = true;
        });
        use_member_types.forEach(function(e){
            selected_mtypes[e.name.toLowerCase()] = true;
        });
                                    
        membership_types.forEach(function(e){
            key = e.name.toLowerCase();
            mship_html += '<div>'+_makeCheckbox(e.name + ' (' + e.mtimes + ' per ' + e.period +')', key, 1, selected_mships[key], !allowEdit)+'</div>';
        });
                                    
        member_types.forEach(function(e){
            key = e.name.toLowerCase();
            mtype_html += '<div>'+_makeCheckbox(e.name, key, 1, selected_mtypes[key], !allowEdit)+'</div>';
        });
                                    
        function __countStats(billable_count){
            var content = '';
            content += '<fieldset id="fta_count_stats"><legend>'+billable_count.name+'</legend>';
            content += '<div>Billable: '+billable_count.billable+'</div>';
            content += '<div>Inactive: '+billable_count.inactive+'</div>';
            content += '<div>Non Billable: '+billable_count.non_billable+'</div>';
            content += '<div>Total: '+billable_count.total+'</div>';
            content += '</fieldset>';
            return content;
        }
        
        function __getBillingStats(invoicing_rule_type_data){
            var billable_count = invoicing_rule_type_data.billable_count,
            billable_count_details = invoicing_rule_type_data.billable_count_details,
            content = '';
            
            billable_count_details.forEach(function(bc){
                content += __countStats(bc);
            });
                                
            // Total
            content += __countStats(billable_count);
            
            return content;
            
        }
        
        var updated = new Date(invoicing_rule_type_data.updated), 
            invoice_item = invoicing_rule_type_data.invoice_item,
            qty = invoicing_rule_type_data.quantity,
            rate = invoice_item.rate,
            min = invoice_item.minimum_qty,
            max = invoice_item.maximum_qty,
            skipped = invoicing_rule_type_data.billable_count.skipped_before_min;
            
        content += '<div class="fta_header"><span>Counts from: '+updated.format('m/d/yyyy HH:MM:ss')+'</span></div>';
                
        content += '<div class="fta_header"><span>For: '+invoice_item.name+'</span></div>';
        
        content += '<hr>';
        
        content += '<div class="fta_header"><span>Calculated quantity: '+invoicing_rule_type_data.quantity
            +' : '+_formatCurrency(rate*qty)+' @ '+_formatCurrency(rate)+' per.</span></div>';
        
        content += '<div class="fta_header"><span>('+(min?min:'No')+' Min., '+(max?max:'No')+' Max.)</span></div>';
        
        if(skipped > 0){
            content += '<div class="fta_header"><span>(Did not count '+skipped+' items, because they were below the minimum of '+min+'.)</span></div>';
        }
        
        content += '<hr>';
                
        content += '<div class="fta_stats_group fta_side_by_side">';
                            
        content += __getBillingStats(invoicing_rule_type_data);
        
        content += '</div>';
        
        
        
        
        if(invoicing_rule_type.count_adults || invoicing_rule_type.count_members){
            isEditable = true;
            content += '<hr>';
            content += '<div class="fta_select_group fta_side_by_side">';
            content += '<fieldset id="fta_membership_types"><legend>Membership Types</legend>'+mship_html+'</fieldset>';
                                    
            if(invoicing_rule_type.count_adults){
                content += '<fieldset id="fta_member_types"><legend>Member Types</legend>'+mtype_html+'</fieldset>';                  
            }
            content += '</div>';
        } else {
            allowEdit = false; // nothing to edit for others
        }
        
        function __setNewData($modalObj){
            var selected_mships = {}, selected_mtypes = {}, use_membership_types = [], use_member_types = [];
            var $mships = $modalObj.find('#fta_membership_types input:checked'), $mtypes = $modalObj.find('#fta_member_types input:checked');
            $mships.each(function(){
                selected_mships[$(this).attr('name')] = true;
            });
            $mtypes.each(function(){
                selected_mtypes[$(this).attr('name')] = true;
            });

            membership_types.forEach(function(e){
                if(selected_mships[e.name.toLowerCase()]){
                    use_membership_types.push(e);
                }
            });

            member_types.forEach(function(e){
                if(selected_mtypes[e.name.toLowerCase()]){
                    use_member_types.push(e);
                }
            });

            invoicing_rule_type_data.use_membership_types = use_membership_types;
            invoicing_rule_type_data.use_member_types = use_member_types;
        }
        
        var __continueAction, __continueButton;
        
        if(!unlocked && lock_edit && typeof callback == "function" && isEditable){
            __continueButton = "Edit";
            __continueAction = function($modalObj){
                _invoicingRuleTypeModal(options, reentry, force_recount, true);
            }
        } else if(allowEdit && isEditable) {
            __continueButton = "Done";
            __continueAction = function($modalObj){
                if(allowEdit){
                    
                    __setNewData($modalObj);
                    
                    if(callback(invoicing_rule_type_data)){
                        $modalObj.ftDialog("close");
                    };
                }
            }
        }
                                    
        $body.foreTeesModal('alertNotice',{
            width:550,
            title:invoicing_rule_type.name + ': Rule ' + (allowEdit?'Edit':'View'),
            alertMode:false,
            message:'<div class="fta_member_type_selection">'+content+'</div>',
            closeButton:allowEdit?'Cancel':'Close',
            continueButton:__continueButton,
            allowContinue:!!__continueButton,
            continueAction:__continueAction,
            reloadButton:allowEdit?'Refresh Counts':false,
            reloadAction:function($modalObj){
                __setNewData($modalObj);
                //options.invoicing_rule_type_data = invoicing_rule_type_data;
                _invoicingRuleTypeModal(options, false, true);
            }
        });                      
    }
    
    var _pqDateFilter = function(ui) {
        var $this = $(this), $clearFilter = $("<div class=\"ui-icon ui-icon-closethick ftaClearFilter\"></div>");
                    $this.click(function(){
                        $(this).datepicker("show");
                    });
                    $clearFilter.click(function(){
                        var $el = $(this), $inp = $el.siblings("input").val("").change();
                    });
                    $this.after($clearFilter);
        $this.css({
            zindex:9999,
            position:"relative"
        }).datepicker({
            changeYear:true, 
            changeMonth: true, 
            showAnim:'',
            onClose:function(evt,ui){
                $(this).focus()
            },
            beforeShow: function (input, inst) {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
            }
        });
        $this.filter(".pq-from").datepicker("option","defaultDate", new Date());
        $this.filter(".pq-to").datepicker("option","defaultDate", new Date());
    }   
    
    
    var _tabs = function(tabs){
        var tabHtml = [], tabContentHtml = [], tabIDs = [];
        tabs.forEach(function(t){
            var tabID = 'tab-'+_generateUUID();
            tabIDs.push(tabID);
            var classes = t.classes;
            if(!classes){
                classes = [];
            } else if (typeof classes == 'string'){
                classes = [classes];
            }
            tabHtml.push('<li><a href="#'+tabID+'">'+t.tab+'</a></li>');
            tabContentHtml.push('<div id="'+tabID+'" style="'+(t.style?t.style:'')+'" class="'+(classes.join(' '))+'"></div>');
        });
        var $tabs = $('<div class="pq-tabs"><ul>'+tabHtml.join('')+'</ul>'+tabContentHtml.join('')+'</div>');
        tabs.forEach(function(t,i){
            $tabs.find('#'+tabIDs[i]).append(t.content);
        });
        return $tabs.tabs();
    }

    var _addRowButton = function(name, rowData){
        return { 
            type:'button', 
            icon: 'ui-icon-plus', 
            label: name, 

            listener:{
                click:function(evt,ui){
                    var $grid = $(this).closest('.pq-grid');
                    _addRow($grid,rowData);
                }
            }
        }
    }
    
    var _customButton = function(name, onclick, icon){
        if(!icon){
            icon = 'ui-icon-newwin';
        }
        return { 
            type:'button', 
            icon: icon, 
            label: name, 

            listener:{
                click:onclick
            }
        }
    }
    
    var _refreshGridButton = function(){
        return { 
            type:'button', 
            icon: 'ui-icon-refresh', 
            label: 'Refresh', 
                            
            listener:{
                click:function(evt,ui){
                    var $grid = $(this).closest('.pq-grid');
                    _refreshGrid($grid);
                    //$grid.pqGrid("refreshDataAndView");
                //$grid.pqGrid("refreshView");
                }
            }
        }
    }
    
    function _getCurrentView($grid){
        //var $body = $('body');
        //$body.foreTeesModal("pleaseWait");
        var data = _getAllRowData($grid), CM = $grid.pqGrid("getColModel"), header = [], rows = [], width, i;
        for (i = 0; i < CM.length; i++) {
            var column = CM[i], title = column.title;
            if (!column.hidden && typeof title != "undefined" && title.length) {
                width = column._width;
                if (!width) {
                    width = parseInt(column.width);
                    if (!width) {
                        width = 100
                    }
                }
                header.push({
                    width:width,
                    value:title,
                    index:column.dataIndx
                    });
            }
        }
        if(header.length){
            rows.push(header);
        }
        for(i = 0; i < data.length; i++){
            var row  = [];
            for(var i2 = 0; i2 < header.length; i2++){
                var $td = $grid.pqGrid("getCell",{
                    rowIndx:i,
                    dataIndx:header[i2].index
                    });
                row.push({
                    value:$td.text()
                    });
            }
            rows.push(row);
        }
        //$body.foreTeesModal("pleaseWait","close");
        return rows;
    }
    
    function _generateCsv(data){
        
        var __processRow = function (row) {
            var finalVal = '';
            for (var j = 0; j < row.length; j++) {
                var cell = row[j], cellValue = cell.value, innerValue = cellValue === null ? '' : cellValue.toString();
                if (cellValue instanceof Date) {
                    innerValue = cellValue.toLocaleString();
                };
                var result = innerValue.replace(/"/g, '""');
                if (result.search(/("|,|\n)/g) >= 0)
                    result = '"' + result + '"';
                if (j > 0)
                    finalVal += ',';
                finalVal += result;
            }
            return finalVal + '\n';
        };

        var csvFile = '';
        for (var i = 0; i < data.length; i++) {
            csvFile += __processRow(data[i]);
        }
        
        return csvFile;
        
    }
    
    var _exportCsvButton = function($grid, fileName){
        return {
            type: 'button',
            label: "Export to CSV",
            icon: 'ui-icon-document',
            listeners: [{
                "click": function (evt) {
                    var data = _getCurrentView($grid);
                   ftapi.downloadData(_cleanFilename(_getValue(fileName)) + '_' + new Date().format('yyyymmdd_hhmmss') + ".csv","text/csv",_generateCsv(data));
                }
            }]
        }
    }
    
    
    var _rowModButtons = function(width, edit_class, delete_class){
        
        if(!edit_class){
            edit_class = 'edit_btn';
        }
        
        if(!delete_class){
            delete_class = 'delete_btn';
        }
        
        var mwidth = 62;
        if(!width){
            width = mwidth;
        }
        
        return {
            title:'',
            editable: false,
            minWidth: mwidth,
            cls:'fta-no-summary',
            width:width,
            dataIndx: 'fta_mod_buttons',
            maxWidth: width,
            resizable: false,
            sortable: false,
            render: function (ui){
                if(ui.rowData[ui.dataIndx]){
                    return ui.rowData[ui.dataIndx];
                }
                return '<div style="white-space:nowrap;"><button type="button" class="ftact-row-button fta-btn-edit-row '+edit_class+'">Edit</button>'
                    +'<button type="button" class="ftact-row-button fta-btn-save-row">Save</button>\
                    <button type="button" class="ftact-row-button fta-btn-delete-row '+delete_class+'">Delete</button>'
                    +'<button type="button" class="ftact-row-button fta-btn-cancel-row">Cancel</button></div>';
            }
        }
    }
    
    function _getIntOrNull(string){
        var result = null;
        if(string){
            result = parseInt(string,10);
        }
        if(!result){
            result = null;
        }
        return result;
    }
    
    function _getValue(object){
        if(typeof object == "function"){
            return object();
        } else {
            return object;
        }
    }
    
    function _cleanFilename(value){
        if(typeof value == "undefined"){
            value = "undefined";
        } else {
            value = value + "";
        }
        return value.replace(/^[\s\uFEFF\xA0]+|^[^a-zA-Z0-9]+|[\s\uFEFF\xA0]+$|[^a-zA-Z0-9]+$/g, '').replace(/[^a-zA-Z0-9]/gi, '_')
        
    }
    
    var _rowDeleteButton = function(width){
        
        var mwidth = 32;
        if(!width){
            width = mwidth;
        }
        
        return {
            title:'',
            editable: false,
            minWidth: mwidth,
            cls:'fta-no-summary',
            dataIndx: 'fta_mod_buttons',
            width:width,
            maxWidth: width,
            resizable: false,
            sortable: false,
            render: function (ui){
                if(ui.rowData[ui.dataIndx]){
                    return ui.rowData[ui.dataIndx];
                }
                return '<button type="button" class="ftact-row-button fta-btn-delete-row delete_btn">Delete</button></div>';
            }
        }
    }
    
    var _button = function(name, icon, action, hide_text){
        return $('<button type="button" class="ftact-detail-button">'+name+'</button>')
        .button({
            text:!hide_text,
            icon:icon
        })
        .click(action);
    }
    
    var _boolean = function(name, row, width){
        
        if(!width){
            width = 65;
        }

        return {
            title:name,
            width:width,
            maxWidth:width,
            minWidth:width,
            dataType: "bool",
            dataIndx: row,
            align:'center',
            cls:'fta-no-summary',
            editor: {
                type:'checkbox',
                style:'margin:3px 5px;'
            },
            render: function(ui){
                if(ui.rowData[ui.dataIndx]){
                    return "Yes";
                } else {
                    return "No";
                }
            },
            filter: {
                type: "checkbox", 
                subtype: 'triple', 
                condition: "equal", 
                listeners: ['click']
            }
        }
    }
    
    var _editable = function(ui){
        var $grid = $(this);
        var rowIndx = ui.rowIndx;
        if($grid.pqGrid('hasClass',{
            rowIndx:rowIndx, 
            cls: 'pq-row-edit'
        }) == true){
            return true;
        } else {
            return false;
        }
    }
    
    var _editor = function(){
        return {
            type:'textbox',
            select: true,
            style:'outline:none;'
        }
    }
    
    var _stringDateEditor = function (ui) {
        var r = ui.rowData[ui.dataIndx],
        date = ftcalendar.stringToDate(r),
        dateString = date.format('m/d/yyyy'),
        $inp = ui.$cell.find("input"),
        $grid = $(this),
        validate = function (that) {
            var valid = $grid.pqGrid("isValid", {
                dataIndx: ui.dataIndx,
                value: $inp.val(),
                rowIndx: ui.rowIndx 
            }).valid;
            if (!valid) {
                that.firstOpen = false;
            }
        };
 
        //initialize the editor
        $inp.val(dateString)
        .on("input", function (evt) {
            validate(this);
        })
        .datepicker({
            changeMonth: true,
            changeYear: true,
            showAnim: '',
            onSelect: function () {
                this.firstOpen = true;
            //validate(this);
            },
            beforeShow: function (input, inst) {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
                return !this.firstOpen;
            },
            onClose: function () {
                this.focus();
            }
        });
    }
    
    var _dateEditor = function (ui) {
        var r = ui.rowData[ui.dataIndx],
        date = new Date(r),
        dateString = date.format('m/d/yyyy'),
        $inp = ui.$cell.find("input"),
        $grid = $(this),
        validate = function (that) {
            var valid = $grid.pqGrid("isValid", {
                dataIndx: ui.dataIndx,
                value: $inp.val(),
                rowIndx: ui.rowIndx 
            }).valid;
            if (!valid) {
                that.firstOpen = false;
            }
        };
 
        //initialize the editor
        $inp.val(dateString)
        .on("input", function (evt) {
            validate(this);
        })
        .datepicker({
            changeMonth: true,
            changeYear: true,
            showAnim: '',
            onSelect: function () {
                this.firstOpen = true;
            //validate(this);
            },
            beforeShow: function (input, inst) {
                setTimeout(function(){
                    $('.ui-datepicker').css('z-index', 99999999999999);
                }, 0);
                return !this.firstOpen;
            },
            onClose: function () {
                this.focus();
            }
        });
    }
    
    var _invoicingRuleTypeDataEditor = function(name,col,width,options){
        if(typeof options != "object"){
            options = {};
        }
        var skip_counts = options.skip_counts, lock_edit = options.lock_edit, no_view = options.no_view;
        //lock_edit = false;
        
        return {
            title:name,
            width:width,
            dataType: "string",
            dataIndx: col,
            cls:'fta-no-summary',
            //editable:!lock_edit,
            editor:{
                init:function(ui){
                    
                    var $grid = $(this),
                        $inp = ui.$cell.find("input"),
                        rowIndx = ui.rowIndx,
                        dataIndx = ui.dataIndx,
                        rowData = ui.rowData;
                        
                        if(typeof options.getRuleTypeDataValues == "function"){
                            options.getRuleTypeDataValues(ui, options);
                        }
 
                    //initialize the editor
                    $inp.focus(function () {
                        var json = $inp.val();
                        $inp.css('display','none');
                        //if(!json){
                        //    json = _getCurrentValueFromDataIndx(dataIndx, ui.rowData, $grid);
                        //}
                        
                        _invoicingRuleTypeModal({
                            club_invoicing_rule_detail_id:options.club_invoicing_rule_detail_id,
                            club_invoicing_rule_id:options.club_invoicing_rule_id,
                            club_invoicing_id:options.club_invoicing_id,
                            invoice_item_id:options.invoice_item_id,
                            invoicing_rule_type_data_json:options.invoicing_rule_type_data_json,
                            date_start:options.date_start,
                            rowData:rowData,
                            $grid:$grid,
                            load_counts:!skip_counts,
                            lock_edit:lock_edit,
                            save:function(invoicing_rule_type_data){
                                var rowData = {
                                    rowIndx:rowIndx,
                                    row:{}
                                }
                                rowData.row[dataIndx] = JSON.stringify(invoicing_rule_type_data);
                                $grid.pqGrid('updateRow',rowData);
                                return true;
                            },
                            error: function(){
                                $inp.css('display','none');
                            }
                        });

                    });
                }
            },
            editModel:{
                //saveKey: $.ui.keyCode.ENTER,
                saveKey: ''
            //onSave: 'next'
            },
            render: function(ui){
                var invoice_item_id = _getCurrentValueFromDataIndx("invoice_item_id", ui.rowData, $(this));
                if(invoice_item_id){
                    var club_invoicing = _lookups.invoiceItemByClubInvoicingId[ui.rowData.club_invoicing_id];
                    if(club_invoicing){
                        var invoicing_rule_type_id = club_invoicing.byId[invoice_item_id].invoicing_rule_type_id;
                        var name = _lookups.invoicingRuleType.byId[invoicing_rule_type_id].name;
                        return '<button class="rule_type_data_view_btn'+(skip_counts?' fta-skip-counts':'')+(lock_edit?' fta-lock-edit':'')+(no_view?' fta-no-view':'')+'">'+name+'</button>';
                    } else {
                        return '';
                    }
                    
                } else {
                    return 'Select an Item';
                }
                
            }
        }
    }
    
    var _textArea = function(name,col,style,editable,getValue){
        if(!style){
            if(style===false){
                style = null;
            } else {
                style = 'min-width:300px;min-height:150px';
            }
        }
        var obj =  {
            title:name,
            dataType: "string",
            dataIndx: col,
            editor: {
                type:'textarea',
                style:style
                
            },
            editModel:{
                //saveKey: $.ui.keyCode.ENTER,
                saveKey: $.ui.keyCode.TAB
            //onSave: 'next'
            }
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        if(getValue !== false){
            obj.render = function(ui){
                var r = ui.rowData[ui.dataIndx];
                if(typeof getValue == "function"){
                    return getValue(ui, r);
                } else if(r){
                    return _formatText(r);
                } else {
                    return '';
                }
            }
        }
        return obj;
    }
    
    var _textBox = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "string",
            dataIndx: col,
            cls:'ftaCls-'+col,
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return value;
            }
        }
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        return obj;
    }
    
    function _updateSelectFilter($grid,col,refresh){
        var grid = $grid.pqGrid("getInstance").grid;

        var column = grid.getColumn({dataIndx: col});
        var filter = column.filter;
        filter.cache = null;
        filter.options = grid.getData({dataIndx: [col]});
        if(refresh){
            grid.refreshView();
        }

    }
    
    var _textBoxSelectFilter = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "string",
            dataIndx: col,
            cls:'ftaCls-'+col,
            filter: {type: "select",
		        condition: 'equal',
		        prepend: {'': '--All--'},
		        valueIndx: col,
		        labelIndx: col,
		        listeners: ['change']
		    },
            render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return value;
            }
        }
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        return obj;
    }
    
    var _integer = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "integer",
            align:'right',
            dataIndx: col,
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            cls:'ftaCls-'+col,
            render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return value;
            }
        }
        
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        return obj;
    }
    
    var _id = function(name,col,width){
        var obj = {
            title:name,
            dataType: "string",
            dataIndx: col,
            cls:'ftaCls-'+col,
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            align:'right',
            editable:false
        }
        if(width){
            obj.width = width;
        }
        return obj;
    }
    
    var _checkBoxSelection = function(name,col,width){
        width = !width?'1%':width;
        col = !col?'checked':col;
        return {title:name, width: width, type: 'checkBoxSelection', cls:'ftGridChk',dataIndx:col};
    }
    
    function _getCheckedRows($grid, col){
        col = !col?'checked':col;
        var i, checked = [], row, rows = _getAllRowData($grid);
        for (i = 0; i < rows.length; i++){
            row = rows[i];
            if(row.checked){
                checked.push(row);
            }
        }
        return checked;
    }
    
    var _percentage = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "float",
            align:'right',
            dataIndx: col,
            cls:'ftaCls-'+col,
            /*
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            */
            render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return _formatPercentage(value);
            }
        }
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        return obj;
    }
    
    function _formatText(t){
        if(typeof t == "undefined" || t === null){
            return '';
        } else {
            return t.replace(/(\r\n|\n|\r)/g,"<br />");
        }
    }
    
    function _formatCurrency(n){
        if(typeof n == "undefined" || n === null){
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
    
    function _formatPercentage(n){
        if(typeof n == "undefined" || n === null){
            return '';
        } else if(typeof n == "string"){
            if(isNaN(n)){
               return n; 
            } else {
                n = parseFloat(n);
            }
        }
        return (n*100).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,")+'%';
    }
    
    function _formatDecimal(n){
        if(typeof n == "undefined" || n === null){
            return '';
        } else if(typeof n == "string"){
            if(isNaN(n)){
               return n; 
            } else {
                n = parseFloat(n);
            }
        }
        return n.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,");
    }
    
    var _currency = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "float",
            align:'right',
            dataIndx: col,
            cls:'ftaCls-'+col,
            /*
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            */
            render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return _formatCurrency(value);
            }
        }
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        } else if(typeof editable == "object"){
            obj.editor = editable;
        }
        return obj;
    }
    
    var _float = function(name,col,width,editable,getValue){
        var obj = {
            title:name,
            dataType: "float",
            align:'right',
            dataIndx: col,
            cls:'ftaCls-'+col,
            /*
            filter: {
                type: 'textbox', 
                condition: 'begin', 
                listeners: ['keyup']
            },
            */
           render: function(ui){
                var value;
                if(typeof getValue == "function"){
                    value = getValue(ui);
                } else {
                    value = ui.rowData[ui.dataIndx];
                }
                return _formatDecimal(value);
            }
            
        }
        if(width){
            obj.width = width;
        }
        if(editable === false){
            obj.editable = false;
        }
        return obj;
    }
    
    var _idSelectList = function (name, col, width, listSource, ls_id, change, nullSelect, render){
        
        function __getListSource(ui){
            var source = {};
            if(typeof listSource == 'string'){
                if(ls_id){
                    var id = ls_id;
                    if(typeof ls_id == "function"){
                        id = ls_id();
                    } else if(typeof ls_id == "string"){
                        id = ui.rowData[ls_id];
                    } else {
                        id = ls_id;
                    }
                    var sl = _lookups[listSource][id]
                    if(sl){
                        source = sl.byId;
                    }
                } else {
                    source = _lookups[listSource].byId;
                }
            }
            return source;
        }
        
        function __getData(ui){
            var source = __getListSource(ui);
            var id = ui.rowData[ui.dataIndx], data = source[id];
            return data;
        }
        
        var oninit;
        if($.isArray(change)){ // is change an array?
            oninit = function(ui){
                var $grid = $(this);
                ui.$cell.find("select").change(function(){
                    change.forEach(function(r){
                        $grid.pqGrid("refreshCell",{rowIndx:ui.rowIndx,dataIndx:r})
                    });
                });
            };
        } else if(typeof change == 'function'){
            oninit = function(ui){
                var $grid = $(this);
                ui.$cell.find("select").change(function(){
                    var $this = $(this);
                    change(ui, __getListSource(ui)[$this.val()], $this, $grid);
                    setTimeout(function(){$grid.pqGrid("refreshRow",{rowIndx:ui.rowIndx})},1);
                });
            };
        } else {
            oninit = function(ui){
                var $grid = $(this);
                ui.$cell.find("select").change(function(){
                    setTimeout(function(){$grid.pqGrid("refreshRow",{rowIndx:ui.rowIndx})},1);
                });
            };
        }
        
        

        var result = {
            title:name,
            width:width,
            maxWidth:width,
            minWidth:width,
            dataType: "integer",
            dataIndx: col,
            cls:'fta-no-summary ftaCls-'+col,
            editor: {
                type:'select',
                options:function(ui){
                    var selectList;
                    var data;
                    
                    if(typeof listSource == 'string'){
                        if(ls_id){
                            var id = ls_id;
                            if(typeof ls_id == "function"){
                                id = ls_id();
                            } else if(typeof ls_id == "string"){
                                id = ui.rowData[ls_id];
                            } else {
                                id = ls_id;
                            }
                            data = _lookups[listSource][id].select;
                        } else {
                            data = _lookups[listSource].select;
                        }
                    } else {
                        data = listSource;
                    }
                    if(typeof nullSelect == "string"){
                        var nsobj = {};
                        var ndata = [];
                        
                        nsobj[null] = nullSelect;
                        ndata.push(nsobj);
                        for(var i = 0; i< data.length; i++){
                            ndata.push(data[i]);
                        }
                        data = ndata;
                    }
                    selectList = data
                    return selectList;
                }
            },
            render: function(ui){
                var data = __getData(ui); 
                if(data){
                    var $grid = $(this);
                    if(typeof render == "function"){
                        return render(ui, data, $grid);
                    } else {
                        return data.name;
                    }
                    
                } else {
                    /*
                     *if(ui.rowData[ui.dataIndx]){
                        // We have data, it's just not in the select list
                        return "item not found";
                    } else */if(typeof nullSelect == "string") {
                        return nullSelect;
                    } else {
                        return "--";
                    }
                    
                }
            }
        }
        if(typeof oninit == 'function'){
            result.editor.init = oninit;
        }
        return result;
    }
    
    var _stringDate = function (name, col, width, editable){
        return {
            title: name, 
            width: width, 
            dataIndx: col,
            cls:'ftaDateRange ftaCls-'+col, 
            dataType: 'string',
            editable:editable,
            editor: {
                type: 'textbox',
                init: _stringDateEditor,
                getData: function(ui){
                    return new Date(ui.$cell.find("input").val()).format('yyyy-mm-dd');
                }
            },
            /*
            render: function (ui) {
                //return "hello";
                var d = ui.cellData;
                if (d) {
                    return $.datepicker.formatDate('yy-mm-dd', ftcalendar.stringToDate(d));
                }
                else {
                    return "";
                }
            },
            */
            filter: {
                type: 'textbox',
                condition: 'between',
                cls:'ftaDateRange',
                init: function(ui) {
                    var $this = $(this), $clearFilter = $("<div class=\"ui-icon ui-icon-closethick ftaClearFilter\"></div>");
                    $this.click(function(){
                        $(this).datepicker("show");
                    });
                    $clearFilter.click(function(){
                        var $el = $(this), $inp = $el.siblings("input").val("").change();
                    });
                    $this.after($clearFilter);
                    $this
                    //.css({ zIndex: 3, position: "relative" })
                    .datepicker({
                        yearRange: "-15:+0", //20 years prior to present.
                        changeYear: true,
                        changeMonth: true,
                        showButtonPanel: true,
                        showAnim:'',
                        onClose: function (evt, ui) {
                            $(this).focus();
                        }
                    });
                    
                    
                    //default From date
                    //$this.filter(".pq-from").datepicker("option", "defaultDate", new Date());
                    //default To date
                    //$this.filter(".pq-to").datepicker("option", "defaultDate", new Date());
                },
                listeners: [{
                    'change':function(evt, ui){
                        if(ui.value){
                            ui.value = $.datepicker.formatDate('yy-mm-dd', ftcalendar.stringToDate(ui.value));
                        } else {
                            ui.value = '';
                        }
                        if(ui.value2){
                            ui.value2 = $.datepicker.formatDate('yy-mm-dd 59:59:59.999', ftcalendar.stringToDate(ui.value2));
                        } else {
                            var endDate = new Date();
                            endDate.setDate(endDate.getDate()+(365*50)); //50 years in the future
                            ui.value2 = $.datepicker.formatDate('yy-mm-dd 59:59:59.999', ftcalendar.stringToDate(endDate));
                        }
                        var $grid = $(this).closest('.pq-grid');
                        $grid.pqGrid("filter", {
                            oper: 'add',
                            data: [ui]
                        })
                    }
                }]
            }
        /*,
                            validations: [
                            {
                                type: 'regexp', 
                                value: '^[0-9]{2}/[0-9]{2}/[0-9]{4}$', 
                                msg: 'Not in mm/dd/yyyy format'
                            }
                            ]*/
        }
    }
    
    var _unixDate = function (name, col, width){
        return {
            title: name, 
            width: width, 
            dataIndx: col,
            cls:'ftaDateRange ftaCls-'+col, 
            dataType: 'integer',
            editor: {
                type: 'textbox',
                init: _dateEditor,
                getData: function(ui){
                    return new Date(ui.$cell.find("input").val()).getTime();
                }
            },
            render: function (ui) {
                //return "hello";
                var cellData = ui.cellData;
                if (cellData) {
                    return $.datepicker.formatDate('yy-mm-dd', new Date(cellData));
                }
                else {
                    return "";
                }
            },
            filter: {
                type: 'textbox',
                condition: 'between',
                init: _pqDateFilter,
                cls:'ftaDateRange',
                listeners: [{
                    'change':function(evt, ui){
                        if(ui.value){
                            ui.value = new Date(ui.value).getTime();
                        } else {
                            ui.value = 0;
                        }
                        if(ui.value2){
                            ui.value2 = new Date(ui.value2).getTime();
                        } else {
                            var endDate = new Date();
                            endDate.setDate(endDate.getDate()+(365*50)); //50 years in the future
                            ui.value2 = endDate.getTime();
                        }
                        var $grid = $(this).closest('.pq-grid');
                        $grid.pqGrid("filter", {
                            oper: 'add',
                            data: [ui]
                        })
                    }
                }]
            }
        /*,
                            validations: [
                            {
                                type: 'regexp', 
                                value: '^[0-9]{2}/[0-9]{2}/[0-9]{4}$', 
                                msg: 'Not in mm/dd/yyyy format'
                            }
                            ]*/
        }
    }
    
    var _detailArrow = function(){
        var obj = {
            title:"",
            minWidth:27, 
            maxWidth:27, 
            width:27, 
            type: "detail", 
            resizable:false, 
            editable:false, 
            sortable: false
        }
        return obj;
    }
    
    var _hidden = function(col, type){
        var obj = {
            dataType: type,
            dataIndx: col,
            hidden: true
        }
        return obj;
    }
    
    var _separator = function(){
        return {type: 'separator'};
    }
    
    var _rootGridDefaults = function(){
        return {
            virtualX: true
        }
    }
    
    var _gridDefaults = function(){
        return {

            height:"100%-4",
            width:"auto",
            //freezeCols: 1,
            
            numberCell:{
                show:false
            },
            autoSizeInterval: 0,
            showTitle: false,
            showBottom: false,
            collapsible: {
                on: false
            },
            dragColumns:{
                enabled: false
            },
            scrollModel:{
                autoFit:true,
                flexContent: true
            },
            selectionModel: {
                type:'cell',
                mode:'single'
            },
            trackModel: {
                on: true
            },
        
            track: true,
            editModel:{
                saveKey: $.ui.keyCode.ENTER,
                onSave: 'next',
                clicksToEdit:1
            },
            editor: _editor(),
            editable: _editable
        }
    }
    
    function _setGridDefault(obj,defaults){
        for(var k in defaults){
            if(typeof obj[k] == "undefined"){
                obj[k] = defaults[k];
            }
        }
    }

    
    var _filterModel = function(){
        return {
            on: true, 
            mode: "AND", 
            header: true, 
            type: 'local'
        }
    }

    var _lookups = {
        taxRate:{
            byId:{},
            select:[],
            list:[]
        },
        taxGroup:{
            byId:{},
            select:[],
            list:[]
        },
        userAccess:{
            byId:{},
            select:[],
            list:[]
        },
        users:{
            byId:{},
            select:[],
            list:[]
        },
        salesPeople:{
            byId:{},
            select:[],
            list:[]
        },
        interval:{
            byId:{},
            select:[],
            list:[]
        },
        terms:{
            byId:{},
            select:[],
            list:[]
        },
        invoiceItem:{
            byId:{},
            select:[],
            list:[]
        },
        invoiceItemType:{
            byId:{},
            select:[],
            list:[]
        },
        invoicingRuleType:{
            byId:{},
            select:[],
            list:[]
        },
        invoiceItemByClubInvoicingId:[],
        clubInvoicingRuleByParentId:[]
    }
    
    var _users = (function(){
        
        var _setUserLookup = function(array){
            if(array){
                _lookups.users = _buildLookup(array);
                _setSalesPeopleLookup = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'users',
                    success:function(data){
                        _lookups.users = _buildLookup(data);
                        _setSalesPeopleLookup = _buildLookup(data);
                    }
                },true);
            }
        }
        
        var _setSalesPeopleLookup = function(array){
            
            if(array){
                _lookups.salesPeople = _buildLookup(_filterSalesPeople(array));
            } else {
                ftapi.get({
                    command:'users',
                    success:function(data){
                        _lookups.salesPeople = _buildLookup(_filterSalesPeople(data));
                    }
                },true);
            }
        }
        
        var _filterSalesPeople = function(array){
            var filtered = [];
            array.forEach(function(u){
                if(u.access && u.access.use_commission){
                    filtered.push(u);
                }
            });
            return filtered;
        }

        var _manage = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-users","Manage Users");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'user',
                        id:id,
                        success:function(){
                            _setSalesPeopleLookup();
                            _commitDelete($_grid, rowData, rowIndx);
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    var password = rowData.password;
                    if(typeof password == "undefined"){
                        password = "";
                    }
                    if(type == 'add' && password.trim() == ""){
                        alert("You must enter a password for new users.");
                        return
                    } else if(password.trim() != "") {
                        var verify = window.prompt("Please confirm password modification by entering it again: ");
                        if(verify.trim() != password.trim()){
                            alert("Passwords do not match.  Please try again.");
                            return;
                        }
                    }
                    ftapi.put({
                        command:'user',
                        data:_filterObject(rowData, ['id','name','email','password','rep_code','user_access_id','default_commission','disabled']),
                        success:function(response){
                            _setSalesPeopleLookup();
                            _commitEdit($grid, rowData, rowIndx, type, response);
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                load: function(e, ui){
                     _updateSelectFilter($grid,"rep_code", true);
                },
                filterModel:_filterModel(),
                toolbar: {
                    items:[
                    _addRowButton('Add User',function(){
                        return {
                            id:null, 
                            name:null, 
                            email:null, 
                            rep_code:null, 
                            password:null, 
                            default_commission:0, 
                            user_access_id:_lookups.userAccess.list[0]['id'], 
                            disabled:false
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([{
                                command:'users'
                            },{
                                command:'userAccess'
                            }])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            _lookups.userAccess = _buildLookup(data.results.userAccess);
                            return {
                                data: data.results.users
                            };
                        }
                    }
                },
                colModel: [
                _hidden("id","integer"),
                _textBox("Name","name"),            
                _textBox("Email","email"),
                {
                    title:"Set Password",
                    width:'15%',
                    dataType: "string",
                    dataIndx: "password"
                },
                _textBoxSelectFilter("Rep Code","rep_code",'30px'),
                _idSelectList("Access", "user_access_id", 110, "userAccess"),
                _percentage("Commission","default_commission",85),
                _boolean("Disabled","disabled"),
                _rowModButtons()
                ]
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 920, 500);
 
        }
        
        
        return {
            manage:_manage,
            setUserLookup:_setUserLookup,
            setSalesPeopleLookup:_setSalesPeopleLookup
        }
    })();
    
    
    
    var _taxRates = (function(){
        
        function _updateParentRow($grid, parentRowData){
            ftapi.get({
                command:'taxGroups',
                id:parentRowData.id,
                success:function(data){
                    var $parent_grid = $grid.parent().closest(".pq-grid"),
                    parentRowIndx = $parent_grid.pqGrid("getRowIndx",{
                        rowData:parentRowData
                    }).rowIndx;
                    // Update data of parent row to match that of source
                    _setObjectWithObject(data[0],parentRowData);
                    $parent_grid.pqGrid("refreshRow",{
                        rowIndx:parentRowIndx
                    });

                }
            },true);
        }

        var _setTaxRateLookup = function(array){
            if(array){
                _lookups.taxRate = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'taxRates',
                    success:function(data){
                        _lookups.taxRate = _buildLookup(data);
                    }
                },true);
            }
        }
        
        var _setTaxGroupLookup = function(array){
            if(array){
                _lookups.taxGroup = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'taxGroups',
                    success:function(data){
                        _lookups.taxGroup = _buildLookup(data);
                    }
                },true);
            }
        }
        
        var _manageRate = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-tax-rates","Tax Rates");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'taxRate',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                            _setTaxRateLookup();
                            _setTaxGroupLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'taxRate',
                        data:_filterObject(rowData, ['id','name','rate','disabled']),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                            _setTaxRateLookup();
                            _setTaxGroupLookup();
                        }
                    });

                }
            });
            
            var gridModel ={
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Tax Rate',function(){
                        return{
                            id:null, 
                            name:null,  
                            rate:0,
                            disabled:false
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([{
                                command:'taxRates'
                            }])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            return {
                                data: data.results.taxRates
                            };
                        }
                    }
                },
                colModel: [
                {
                    title:"ID",
                    dataType: "integer",
                    dataIndx: "id",
                    hidden: true
                },
                                        
                {
                    title:"Name",
                    //width:'70%',
                    dataType: "string",
                    dataIndx: "name"
                },
                {
                    title:"Rate",
                    align:'right',
                    width:70,
                    maxWidth:70,
                    minWidth:70,
                    dataType: "float",
                    dataIndx: "rate",
                    render: function(ui){
                        return (ui.rowData[ui.dataIndx] * 100).toFixed(3).toString() + '%';
                    }
                },
                _hidden("disabled","bool"),
                _rowModButtons()
                ]
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 500, 400);

        }
        
        var _manageGroup = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-tax-groups","Tax Groups");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'taxGroup',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'taxGroup',
                        data:_filterObject(rowData, ['id','name','disabled']),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Tax Group',function(){
                        return{
                            id:null, 
                            name:null,  
                            group_rate:0,
                            disabled:false,
                            tax_rates:[]
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([{
                                command:'taxGroups'
                            }])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            var taxGroupArray = data.results.taxGroups;
                            _setTaxGroupLookup(taxGroupArray);
                            return {
                                data: taxGroupArray
                            };
                        }
                    }
                },
                detailModel: {
                    cache: false,
                    //collapseIcon: "ui-icon-plus",
                    //expandIcon: "ui-icon-minus",
                    init: function (ui) {
                        var parentRowData = ui.rowData;   
                        var $grid2 = $("<div></div>");
                                
                        _activateGridElements({
                            $grid:$grid2,
                            deleteRecord:function(rowIndx, $_grid, rowData, id){
                                ftapi.del({
                                    command:'taxGroupDetail',
                                    id:rowData.id,
                                    success:function(){
                                        _commitDelete($_grid, rowData, rowIndx);
                                        _updateParentRow($_grid, parentRowData);
                                        _setTaxGroupLookup();
                                    },
                                    beforeError:function(errortxt, errorraw){
                                        //alert(errortxt);
                                        _rollBackDelete($_grid, rowData, rowIndx);
                                    }
                                });
    
                            },
                            updateRecord:function(rowIndx, $_grid, rowData, type){
                                ftapi.put({
                                    command:'taxGroupDetail',
                                    data:_filterObject(rowData,['id','tax_group_id','tax_rate_id']),
                                    success:function(response){
                                        _commitEdit($_grid, rowData, rowIndx, type, response);
                                        _updateParentRow($grid, parentRowData);
                                        _setTaxGroupLookup();
                                    }
                                });

                            }
                        });
                        
                        var gridModel2 = {
                            height:"auto",
                            flexHeight:true,
                            toolbar: {
                                items:[
                                _addRowButton('Add Tax Rate To Group',function(){
                                    return {
                                        id:null, 
                                        tax_group_detail_id:null,
                                        tax_group_id:parentRowData.id
                                    }
                                }),
                                _separator(),
                                _refreshGridButton()
                                ]
                            },
                            dataModel:{
                                dataType: 'JSON',
                                location: 'remote',
                                recIndx: 'id',
                                beforeSend:ftapi.setHeaderAuthToken,
                                error:ftapi.xhrError,
                                getUrl: function(){
                                    return {
                                        url:ftapi.uri([
                                        {
                                            command:'taxRates'
                                        },
                                        {
                                            command:'taxGroupDetailsByGroupId',
                                            id:parentRowData.id
                                        }
                                        ])
                                    }
                                },
                                getData: function(data) {
                                    if(!data.success){
                                        ftapi.error(data.error);
                                        return {};
                                    } else {
                                        ftapi.processResponse(data); // get/change api keys, etc.
                                        _setTaxRateLookup(data.results.taxRates);
                                        return {
                                            data: data.results.taxGroupDetailsByGroupId
                                        };
                                    }
                                },
                                error: function () {
                                    $grid.pqGrid("rowInvalidate", {
                                        rowData: parentRowData
                                    });
                                }
                                        
                            },
                            colModel: [
                            {
                                title:"TAX GROUP DETAIL ID",
                                dataType: "integer",
                                dataIndx: "id",
                                hidden: true
                            },
                            {
                                title:"TAX GROUP ID",
                                dataType: "integer",
                                dataIndx: "tax_group_id",
                                hidden: true
                            },
                            {
                                title:"Tax Rate Type",
                                width:110,
                                dataType: "integer",
                                dataIndx: "tax_rate_id",
                                editor: {
                                    init:function(ui){
                                        ui.$cell.find("select").change(function(){
                                            $(this).addClass("ftpq-sourcefor-rate").closest(".pq-grid").pqGrid("refreshCell",{
                                                rowIndx:ui.rowIndx, 
                                                dataIndx:"rate"
                                            });
                                        });
                                    },
                                    type:'select',
                                    options:function(){
                                        return _lookups.taxRate.select;
                                    }
                                },
                                render: function(ui){
                                                
                                    var id = ui.rowData[ui.dataIndx],
                                    r = _lookups.taxRate.byId[id];
                                                
                                    if(r){
                                        return r.name;
                                    } else {
                                        return "undefined: " + id;
                                    }
                                }
                            },
                            {
                                title:"Rate",
                                align:'right',
                                editable: false,
                                width:70,
                                maxWidth:70,
                                minWidth:70,
                                render: function(ui){
                                    var rowData = ui.rowData,
                                    id = rowData['tax_rate_id'],
                                    $boundselect = $(this).closest(".pq-grid").find("select.ftpq-sourcefor-rate");
                                    if($boundselect.length){
                                        id = parseInt($boundselect.val(),10);
                                    }
                                    var r = _lookups.taxRate.byId[id];
                                    if(r){
                                        return (r.rate * 100).toFixed(3).toString() + '%';
                                    } else {
                                        return "N/A"
                                    }
                                                
                                }
                            },
                            _rowModButtons()
                            ]
                        }
                        
                        _setGridDefault(gridModel2, _gridDefaults());
                        
                        $grid2.pqGrid(gridModel2);
                                
                        return $grid2;
                    }
                },
                colModel: [
                {
                    title:"",
                    minWidth:27, 
                    maxWidth:27, 
                    width:27, 
                    type: "detail", 
                    resizable:false, 
                    editable:false, 
                    sortable: false
                },
                {
                    title:"ID",
                    dataType: "integer",
                    dataIndx: "id",
                    hidden: true
                },             
                {
                    title:"Name",
                    width:'25%',
                    dataType: "string",
                    dataIndx: "name"
                },
                {
                    title:"Tax Rates in Group",
                    width: 100,
                    sortable:false,
                    dataIndx: "tax_rates",
                    dataType: "array",
                    render: function(ui){
                        var items = [];
                        var itemData = ui.rowData[ui.dataIndx];
                        if(typeof itemData == "object"){
                            itemData.forEach(function(r){
                                items.push('<span style="white-space:nowrap;">'+r.name+'</span>');
                            });
                        }
                        return items.join(', ');
                    },
                    editable: false
                },
                {
                    title:"Rate Total",
                    align:'right',
                    //width:'13%',
                    width:70,
                    maxWidth:70,
                    minWidth:70,
                    resizable: false,
                    dataType: "float",
                    dataIndx: "group_rate",
                    render: function(ui){
                        return (ui.rowData[ui.dataIndx] * 100).toFixed(3).toString() + '%';
                    },
                    editable: false
                },
                _hidden("disabled","bool"),
                _rowModButtons()
                ]
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 600, 400);
            
 
        }
        
        return {
            manageRate:_manageRate,
            manageGroup:_manageGroup,
            setTaxGroupLookup:_setTaxGroupLookup
        }
    })();
    
    var _settings = (function(){
        
        var _manageForeteesSettings = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-foretees-settings","ForeTees Settings");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'foreteesSetting',
                        id:rowData.id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });

                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'foreteesSetting',
                        data:_filterObject(rowData,[
                            'id','name','text_value','int_value','float_value']),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Setting',function(){
                        return {
                            id:null, 
                            name:null,  
                            text_value:null,
                            int_value:null,
                            float_value:null
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel:{
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'foreteesSettings'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            return {
                                data: data.results.foreteesSettings
                            };
                        }
                    }
                                        
                },
                colModel: [
                _hidden("id","integer"),
                _textBox("Setting Name","name",'30%'),
                _textArea("Text Value", "text_value", '70%', true, function(ui,value){
                    if(value){
                        return value.substring(0,600).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;') + (value.length>600?"...":"");
                    } else {
                        return value;
                    }
                    
                }),
                _float("Float Value","float_value",'5%'),
                _integer('Integer Value','int_value','5%'),
                _rowModButtons()
                ],
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                }
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 800, 500);

        }
        
        var _manageForeteesAnnouncements = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-foretees-announcements","Manage ForeTees Announcements");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'foreteesAnnouncement',
                        id:rowData.id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });

                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'foreteesAnnouncement',
                        data:_filterObject(rowData,[
                            'id','title','file_name','html','date_start','date_end',
                            'golf','flxrez','dining','premier','ftapp', 'publish'
                        ]),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Announcement',function(){
                        return {
                            id:null, 
                            title:null,  
                            file_name:null,
                            html:null,
                            date_start:null,
                            date_end:null,
                            golf:0,
                            flxrez:0,
                            dining:0,
                            premier:0,
                            ftapp:0,
                            publish:0
                        }
                    }),
                    _separator(),
                    _exportCsvButton($grid,function(){return "ForeTees Announcements"}),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel:{
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'foreteesAnnouncements'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            return {
                                data: data.results.foreteesAnnouncements
                            };
                        }
                    }
                                        
                },
                colModel: [
                _hidden("id","integer"),
                _textBox("Title","title"),
                _textBox("File (Deprecated)","file_name"),
                _textArea("HTML Content", "html",'40%',true,function(ui,value){
                    if(value){
                        return value.substring(0,50).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;') + (value.length>50?"...":"");
                    } else {
                        return value;
                    }
                    
                }),
                _stringDate("Start Date","date_start","8%"),
                _stringDate("End Date","date_end","8%"),
                _boolean("Golf","golf"),
                _boolean("FlxRez","flxrez"),
                _boolean("Dining","dining"),
                _boolean("Premier","premier"),
                _boolean("FT App","ftapp"),
                _boolean("Publish","publish"),
                _stringDate("Last View","last_view","8%", false),
                _integer('Total Views','total_views','5%', false),
                _integer('Distinct User Views','distinct_user_views','5%', false),
                _integer('Distinct Club Views','distinct_club_views','5%', false),
                _rowModButtons()
                ],
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                }
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 1280, 500);

        }
        
        return {
            manageForeteesSettings:_manageForeteesSettings,
            manageForeteesAnnouncements:_manageForeteesAnnouncements
        }
        
    })();
    
    
    var _invoiceItems = (function(){

        var _setInvoiceItemLookup = function(array){
            if(array){
                _lookups.invoiceItem = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoiceItems',
                    success:function(data){
                        _lookups.invoiceItem = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setInvoiceItemLookupByClubInvoicingId = function(id, array){
            if(array){
                _lookups.invoiceItemByClubInvoicingId[id] = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoiceItemsByClubInvoicingId',
                    id:id,
                    success:function(data){
                        _lookups.invoiceItemByClubInvoicingId[id] = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setInvoiceItemTypeLookup = function(array){
            if(array){
                _lookups.invoiceItemType = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoiceItemTypes',
                    success:function(data){
                        _lookups.invoiceItemType = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setInvoicingRuleTypeLookup = function(array){
            if(array){
                _lookups.invoicingRuleType = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoicingRuleTypes',
                    success:function(data){
                        _lookups.invoicingRuleType = _buildLookup(data);
                    }
                });
            }
        }
        
        var _manageItems = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-invoice-items","Invoice Items");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'invoiceItem',
                        id:rowData.id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                            _setInvoiceItemLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });

                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'invoiceItem',
                        data:_filterObject(rowData,[
                            'id','invoice_item_type_id','invoicing_rule_type_id','name','rate','minimum_qty',
                            'maximum_qty','description','disabled','count_before_min']),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                            _setInvoiceItemLookup();
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Invoice Item',function(){
                        return {
                            id:null, 
                            invoice_item_type_id:_lookups.invoiceItemType.list.length?_lookups.invoiceItemType.list[0].id:null,
                            invoicing_rule_type_id:_lookups.invoicingRuleType.list[0].id,
                            name:null,  
                            rate:0,
                            minimum_qty:0,
                            maximum_qty:0,
                            description:'',
                            count_before_min:true,
                            disabled:false
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel:{
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'invoiceItems'
                            },
                            {
                                command:'invoiceItemTypes'
                            },
                            {
                                command:'invoicingRuleTypes'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            _setInvoiceItemTypeLookup(data.results.invoiceItemTypes);
                            _setInvoicingRuleTypeLookup(data.results.invoicingRuleTypes);
                            return {
                                data: data.results.invoiceItems
                            };
                        }
                    }
                                        
                },
                colModel: [
                _hidden("id","integer"),
                _textBox("Item Name","name",'20%'),
                _idSelectList("Item Type", "invoice_item_type_id", '15%', "invoiceItemType"),
                _idSelectList("Rule Type", "invoicing_rule_type_id", '10%', "invoicingRuleType"),
                _float("Rate","rate",'5%'),
                _integer('<span title="Use with automatic invoice generation.">Min. QTY</span>','minimum_qty','1%'),
                _integer('<span title="Use with automatic invoice generation.">Max. QTY</span>','maximum_qty','1%'),
                _textArea("Description","description",'60%'),
                _boolean('<span title="Minimum quantity will be subtracted from calculated billable quantity if this is not set.">Count Below Min.</span>',"count_before_min"),
                _boolean("Disabled","disabled"),
                _rowModButtons()
                ]
            }
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 1200, 500);

        }
        
        var _manageTypes = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-invoice-items-by-type","Invoice Item Types");
            var $grid = $dialog.find('.ftact-grid');
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'invoiceItemType',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                            _setInvoiceItemTypeLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'invoiceItemType',
                        data:_filterObject(rowData, ['id','name','disabled']),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                            _setInvoiceItemTypeLookup();
                        }
                    });

                }
            });
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Invoice Item Type',function(){
                        return{
                            id:null, 
                            name:null,
                            disabled:false
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([{
                                command:'invoiceItemTypes'
                            }])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            var itemTypeArray = data.results.invoiceItemTypes;
                            //_setitemTypeArrayLookup(itemTypeArray);
                            return {
                                data: itemTypeArray
                            };
                        }
                    }
                },
                detailModel: {
                    cache: false,
                    //collapseIcon: "ui-icon-plus",
                    //expandIcon: "ui-icon-minus",
                    init: function (ui) {
                        var parentRowData = ui.rowData;   
                        var $grid2 = $("<div></div>");
                                
                        _activateGridElements({
                            $grid:$grid2,
                            deleteRecord:function(rowIndx, $_grid, rowData, id){
                                ftapi.del({
                                    command:'invoiceItem',
                                    id:rowData.id,
                                    success:function(){
                                        _commitDelete($_grid, rowData, rowIndx);
                                        //_updateParentRow($grid, parentRowData);
                                        _setInvoiceItemLookup();
                                    },
                                    beforeError:function(errortxt, errorraw){
                                        //alert(errortxt);
                                        _rollBackDelete($_grid, rowData, rowIndx);
                                    }
                                });
    
                            },
                            updateRecord:function(rowIndx, $_grid, rowData, type){
                                ftapi.put({
                                    command:'invoiceItem',
                                    data:_filterObject(rowData,[
                                        'id','invoice_item_type_id','invoicing_rule_type_id',
                                        'name','rate','minimum_qty',
                                        'maximum_qty','description','disabled']),
                                    success:function(response){
                                        _commitEdit($_grid, rowData, rowIndx, type, response);
                                        //_updateParentRow($grid, parentRowData);
                                        _setInvoiceItemLookup();
                                    }
                                });

                            }
                        });
                        
                        var gridModel2 = {
                            height:160,
                            flexHeight: true,
                            toolbar: {
                                items:[
                                _addRowButton('Add Invoice Item',function(){
                                    return {
                                        id:null, 
                                        invoice_item_type_id:parentRowData.id,
                                        invoicing_rule_type_id:_lookups.invoicingRuleType.list[0].id,
                                        name:null,  
                                        rate:0,
                                        minimum_qty:0,
                                        maximum_qty:0,
                                        description:'',
                                        disabled:false
                                    }
                                }),
                                _separator(),
                                _refreshGridButton()
                                ]
                            },
                            dataModel:{
                                dataType: 'JSON',
                                location: 'remote',
                                recIndx: 'id',
                                beforeSend:ftapi.setHeaderAuthToken,
                                error:ftapi.xhrError,
                                getUrl: function(){
                                    return {
                                        url:ftapi.uri([
                                        {
                                            command:'invoiceItemsByTypeId',
                                            id:parentRowData.id
                                        },
                                        {
                                            command:'invoicingRuleTypes'
                                        }
                                        ])
                                    }
                                },
                                getData: function(data) {
                                    if(!data.success){
                                        ftapi.error(data.error);
                                        return {};
                                    } else {
                                        ftapi.processResponse(data); // get/change api keys, etc.
                                        _setInvoicingRuleTypeLookup(data.results.invoicingRuleTypes);
                                        return {
                                            data: data.results.invoiceItemsByTypeId
                                        };
                                    }
                                }
                                        
                            },
                            colModel: [
                            _hidden('id','integer'),
                            _hidden('invoice_item_type_id','integer'),
                            _textBox('Item Name','name','20%'),
                            _idSelectList("Rule Type", "invoicing_rule_type_id", 110, "invoicingRuleType"),
                            _currency('Rate', 'rate', '8%'),
                            _integer('<span title="Use with automatic invoice generation.">Min. QTY</span>', 'minimum_qty', '8%'),
                            _integer('<span title="Use with automatic invoice generation.">Max. QTY</span>', 'maximum_qty', '8%'),
                            _textArea("Description","description",300),
                            _boolean("Disabled","disabled"),
                            _rowModButtons()
                            ]
                        }
                        
                        _setGridDefault(gridModel2, _gridDefaults());
                        
                        $grid2.pqGrid(gridModel2);   
                        return $grid2;
                    }
                },
                colModel: [
                {
                    title:"",
                    minWidth:27, 
                    maxWidth:27, 
                    width:27, 
                    type: "detail", 
                    resizable:false, 
                    editable:false, 
                    sortable: false
                },
                {
                    title:"ID",
                    dataType: "integer",
                    dataIndx: "id",
                    hidden: true
                },             
                {
                    title:"Item Type",
                    width:400,
                    dataType: "string",
                    dataIndx: "name"
                },
                _boolean("Disabled","disabled"),
                _rowModButtons()
                ]
            }
            
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 820, 500);
            
 
        }
        
        return {
            manageTypes:_manageTypes, 
            manageItems:_manageItems,
            setInvoiceItemLookup:_setInvoiceItemLookup,
            setInvoiceItemTypeLookup:_setInvoiceItemTypeLookup,
            setInvoicingRuleTypeLookup:_setInvoicingRuleTypeLookup,
            setInvoiceItemLookupByClubInvoicingId:_setInvoiceItemLookupByClubInvoicingId
        }
    })();



    var _clubs = (function(){

        var _setClubLookup = function(array){
            if(array){
                _lookups.club = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'clubs',
                    success:function(data){
                        _lookups.club = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setClubExtendedLookup = function(array){
            if(array){
                _lookups.clubExtended = _buildLookup(array);
            } else {
        //ftapi.get({
        //    command:'clubs',
        //    success:function(data){
        //        _lookups.clubExtended = _buildLookup(data);
        //    }
        //});
        }
        }
        
        var _setClubInvoicingLookup = function(array){
            if(array){
                _lookups.clubInvoicing = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'clubInvoicing',
                    success:function(data){
                        _lookups.clubInvoicing = _buildLookup(data);
                    }
                });
            }
        }
  
        
        var _manage = function(){

            var $dialog = _getDialogGridContiner("ftact-popup-invoice-clubs","Club Invoicing Accounts");
            var $grid = $dialog.find('.ftact-grid');
            /* Clubs are read only (for now)
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $grid, rowData, id){
                    ftapi.del({
                        command:'club',
                        id:id,
                        success:function(){
                            _commitDelete($grid, rowData, rowIndx);
                            _setInvoiceItemTypeLookup();
                        },
                        error:function(errortxt, errorraw){
                            alert(errortxt);
                            _rollBackDelete($grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $grid, rowData, type){
                    ftapi.put({
                        command:'club',
                        data:_filterObject(rowData, ['id','name','disabled']),
                        success:function(response){
                            _commitEdit($grid, rowData, rowIndx, type, response);
                            _setInvoiceItemTypeLookup();
                        }
                    });

                }
            });
            */
           _activateGridElements({$grid:$grid});
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    /*
                            _addRowButton('Add Club',function(){
                                return{
                                    id:null, 
                                    name:null,
                                    disabled:false
                                }
                            }),*/
                    
                    _exportCsvButton($grid,function(){return "Clubs"}),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([{
                                command:'clubs'
                            }])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            return {
                                data: data.results.clubs
                            };
                        }
                    }
                },
                detailModel: {
                    cache: false,
                    //collapseIcon: "ui-icon-plus",
                    //expandIcon: "ui-icon-minus",
                    init: function (ui) {
                        var parentRowData = ui.rowData;   
                        var club_invoicing_id = parentRowData.id;
                        var $grid2 = $("<div></div>"); 
                        _activateGridElements({
                            $grid:$grid2,
                            deleteRecord:function(rowIndx, $_grid, rowData, id){
                                ftapi.del({
                                    command:'clubInvoicing',
                                    id:rowData.id,
                                    success:function(){
                                        _commitDelete($_grid, rowData, rowIndx);
                                    //_updateParentRow($grid, parentRowData);
                                    //_setInvoiceItemLookup();
                                    },
                                    beforeError:function(errortxt, errorraw){
                                        //alert(errortxt);
                                        _rollBackDelete($_grid, rowData, rowIndx);
                                    }
                                });
    
                            },
                            updateRecord:function(rowIndx, $_grid, rowData, type){
                                ftapi.put({
                                    command:'clubInvoicing',
                                    data:_filterObject(rowData,[
                                        'id','club_id',
                                        'name','address','phone','email','notes',
                                        'default_po','show_to_pro','disabled']),
                                    success:function(response){
                                        _commitEdit($_grid, rowData, rowIndx, type, response);
                                    //_updateParentRow($grid, parentRowData);
                                    //_setInvoiceItemLookup();
                                    }
                                });

                            }
                        });
                        var gridModel2 = {
                            height:"auto",
                            flexHeight: true,
                            toolbar: {
                                items:[
                                _addRowButton('Add Club Invoicing Account',function(){
                                    return {
                                        id:null, 
                                        club_id:club_invoicing_id,
                                        name:'',
                                        address:'',
                                        phone:'',
                                        email:_lookups.clubExtended.byId[club_invoicing_id].email,
                                        notes:'',
                                        default_po:'',
                                        show_to_pro:1,
                                        disabled:false
                                    }
                                }),
                                _separator(),
                                _refreshGridButton()
                                ]
                            },
                            dataModel:{
                                dataType: 'JSON',
                                location: 'remote',
                                beforeSend:ftapi.setHeaderAuthToken,
                                error:ftapi.xhrError,
                                recIndx: 'id',
                                getUrl: function(){
                                    return {
                                        url:ftapi.uri([
                                        {
                                            command:'clubInvoicing',
                                            club_id:club_invoicing_id
                                        },
                                        {
                                            command:'clubs',
                                            id:club_invoicing_id
                                        }
                                        ])
                                    }
                                },
                                getData: function(data) {
                                    if(!data.success){
                                        ftapi.error(data.error);
                                        return {};
                                    } else {
                                        ftapi.processResponse(data); // get/change api keys, etc.
                                        _setClubExtendedLookup(data.results.clubs);
                                        return {
                                            data: data.results.clubInvoicing
                                        };
                                    }
                                }
                                        
                            },
                            colModel: [
                            _detailArrow(),
                            _hidden("id","integer"),
                            _hidden("club_id","integer"),
                            _textBox("Name","name",'120px'),
                            _textArea("Bill To (Address)","address", 200),
                            _textBox("Phone","phone",'90px'),
                            _textBox("Email","email",'100px'),
                            _textArea("Notes","notes"),
                            _hidden("default_po","string"),
                            //_textBox("Default PO","default_po",'50px'),
                            _boolean("Proshop View","show_to_pro"),
                            _boolean("Disabled","disabled"),
                            _rowModButtons()
                            ],
                            detailModel: {
                                cache: false,
                                init: function(ui){
                                    /*
                                    var $detail = $('<div></div>');
                                    $detail.append(_button("Invoicing Rules","ui-icon-extlink",function(){
                                        _invoicing.manageRules(ui.rowData.id);
                                    }));
                                    return $detail;
                                    */
                                   /*
                                    return _tabs(
                                        [
                                        {
                                            tab:'Invoicing Rules',
                                            content:_invoicing.manageRules(ui.rowData.id, ui),
                                            classes:['pq-gridtab']
                                        },
                                        {
                                            tab:'Invoices',
                                            content:'TEST'
                                        }
                                        ]
                                        );
                                    */
                                   return _invoicing.manageRules(ui.rowData.id, ui);
                                }
                            }
                        }
                        _setGridDefault(gridModel2, _gridDefaults());
                        
                        $grid2.pqGrid(gridModel2);
                                
                        return $grid2;
                    }
                },
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                },
                colModel: [
                _detailArrow(),
                _hidden("id","integer"),
                _textBox("Club","name"),
                _textBox("DB Name","code",'100px'),
                _unixDate("Start Date","start_date","80px"),
                _stringDate("Next Billing Date","next_billing_date","80px"),
                _integer("Unsent","unsent_invoices","1%"),
                _integer("Unpaid","unpaid_invoices","1%"),
                _integer("Past Due","max_days_past_due","1%"),
                _boolean("Invoicing","club_invoicing_enabled"),
                _boolean("Non Billable","non_billable"),
                _boolean("Disabled","disabled")
                //_rowModButtons()
                ]
            };
            _setGridDefault(gridModel, _gridDefaults());
            _setGridDefault(gridModel, _rootGridDefaults());
            _openDialog($dialog, $grid, gridModel, 1320, 700);
            
 
        }
         
        
        return {
            manage:_manage,
            setClubExtendedLookup:_setClubExtendedLookup
        }
    })();
    
    
    var _invoicing = (function(){

        var _setIntervalLookup = function(array){
            if(array){
                _lookups.interval = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'intervals',
                    success:function(data){
                        _lookups.interval = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setTermsLookup = function(array){
            if(array){
                _lookups.terms = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoiceTerms',
                    success:function(data){
                        _lookups.terms = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setInvoicingRuleTypeLookup = function(array){
            if(array){
                _lookups.invoicingRuleType = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoicingRuleTypes',
                    success:function(data){
                        _lookups.invoicingRuleType = _buildLookup(data);
                    }
                });
            }
        }
        
        var _setClubInvoicingRuleLookupByParentId = function(id, array){
            if(array){
                _lookups.clubInvoicingRuleByParentId[id] = _buildLookup(array);
            } else {
                ftapi.get({
                    command:'invoicingRuleTypes',
                    success:function(data){
                        _lookups.clubInvoicingRuleByParentId[id] = _buildLookup(data);
                    }
                });
            }
        }
        
        var _getfullTaxGroupName = function(taxGroup){
            return taxGroup.name + ': ' + _formatPercentage(taxGroup.group_rate);
        }
        
        var _clubRecord = {};
        var _clubInvoicingRecord = {};
        
        var _manageRules = function(club_invoicing_id, asDetail){

            var $dialog,$grid;
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner("ftact-popup-invoicing-rules-"+club_invoicing_id,"Club Invoicing Rules");
                $grid = $dialog.find('.ftact-grid');
            }
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'clubInvoicingRule',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        //_setInvoiceItemTypeLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'clubInvoicingRule',
                        data:_filterObject(rowData, [
                            'id','club_invoicing_id','interval_id','terms_id',
                            'name','next_date','auto_send','disabled'
                            ]),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        //_setInvoiceItemTypeLookup();
                        }
                    });

                }
            });
            
            var gridModel = {
                toolbar: {
                    items:[
                    _addRowButton('Add Invoicing Rule',function(){
                        return{
                            id:null,
                            club_invoicing_id:club_invoicing_id,
                            interval_id:_lookups.interval.list[0].id,
                            terms_id:_lookups.terms.list[0].id,
                            name:null,
                            next_date:new Date().getTime(),
                            auto_send:false,
                            disabled:false
                        }
                    }),
                    _separator(),
                    _customButton('Manage Invoices',function(){
                        _invoicing.manageInvoices({
                            club_invoicing_id:club_invoicing_id,
                            afterSave:function(){
                                _refreshGrid($grid);
                            }
                        });
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'clubInvoicingRules',
                                parent_id:club_invoicing_id
                            },
                            {
                                command:'clubInvoicing',
                                parent_id:club_invoicing_id
                            },
                            {
                                command:'clubs',
                                parent_id:club_invoicing_id
                            },
                            {
                                command:'intervals'
                            },
                            {
                                command:'invoiceTerms'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            _clubRecord = data.results.clubs[0];
                            _clubInvoicingRecord = data.results.clubInvoicing[0];
                            _setIntervalLookup(data.results.intervals);
                            _setTermsLookup(data.results.invoiceTerms);
                            if(!asDetail){
                                _setDialogTitle($dialog,_clubRecord.name + ": " + _clubInvoicingRecord.name + " - Invoicing Rules");
                            }
                            return {
                                data: data.results.clubInvoicingRules
                            };
                        }
                    }
                },
                colModel: [
                _detailArrow(),
                _hidden("id","integer"),
                _hidden("club_invoicing_id","integer"),
                _textBox("Rule","name"),
                _idSelectList("Interval", "interval_id", 110, "interval"),
                _idSelectList("Terms", "terms_id", 110, "terms"),
                _unixDate("Next Run","next_date",100),
                _boolean("Auto Send","auto_send",62),
                _boolean("Disabled","disabled"),
                _rowModButtons()
                ],
                detailModel: {
                    cache: false,
                    init: function(ui){
                        return _manageRuleDetails(ui.rowData.id, ui);
                        //return $("<span>TEST</span>");
                    }
                }
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, 500, 920);
            }
 
        }
        
        var _manageRuleDetails = function(club_invoicing_rule_id, asDetail){

            var $dialog,$grid;
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner("ftact-popup-invoicing-rule-details-"+club_invoicing_rule_id,"Club Invoicing Rule Details");
                $grid = $dialog.find('.ftact-grid');
            }
            
            var values = {
                club_invoicing_id:null
            }
            
            
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'clubInvoicingRuleDetail',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        //_setInvoiceItemTypeLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    ftapi.put({
                        command:'clubInvoicingRuleDetail',
                        data:_filterObject(rowData, [
                            'id','club_invoicing_rule_id','invoice_item_id','tax_group_id','sales_person_id',
                            'invoicing_rule_type_data','commission','commission_on_first','reoccur_limit',
                            'quantity','rate','description','disabled'
                            ]),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        //_setInvoiceItemTypeLookup();
                        }
                    });

                },
                initButtons:[
                    _viewRuleDataButtonInit()
                ],
                getRuleTypeDataValues:function(ui, options){
                    var rowData = ui.rowData;
                    options.club_invoicing_id = values.club_invoicing_id;
                    options.club_invoicing_rule_id = club_invoicing_rule_id;
                    options.club_invoicing_rule_detail_id = rowData.id;
                    options.invoicing_rule_type_data_json = rowData.invoicing_rule_type_data;
                    options.invoice_item_id = rowData.invoice_item_id;
                }
            });
            
            
            var gridModel = {
                freezeCols: 1,
                toolbar: {
                    items:[
                    _addRowButton('Add Invoicing Rule Detail',function(){
                        return{
                            id:null,
                            club_invoicing_id:values.club_invoicing_id,
                            club_invoicing_rule_id:club_invoicing_rule_id,
                            invoice_item_id:null,
                            tax_group_id:_defaults.tax_group_id,
                            sales_person_id:null,
                            invoicing_rule_type_data:null,
                            commission:null,
                            commission_on_first:null,
                            reoccur_limit:null,
                            quantity:null,
                            rate:null,
                            description:null,
                            disabled:false
                        }
                    }),
                    _separator(),
                    _refreshGridButton()
                    ]
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'clubInvoicingRuleDetails',
                                parent_id:club_invoicing_rule_id
                            },
                            {
                                command:'clubInvoicingRules',
                                id:club_invoicing_rule_id
                            },
                            {
                                command:'invoiceItemsByClubInvoicingRuleId',
                                id:club_invoicing_rule_id
                            },
                            {
                                command:'taxGroups'
                            },
                            {
                                command:'invoicingRuleTypes'
                            },
                            {
                                command:'users'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            var results = data.results, 
                            clubInvoicingRule = results.clubInvoicingRules[0],
                            club_invoicing_id = clubInvoicingRule.club_invoicing_id;
                            values.club_invoicing_id = club_invoicing_id;
                            _users.setSalesPeopleLookup(results.users);
                            _taxRates.setTaxGroupLookup(results.taxGroups);
                            _setInvoicingRuleTypeLookup(results.invoicingRuleTypes);
                            _invoiceItems.setInvoiceItemLookupByClubInvoicingId(club_invoicing_id, results.invoiceItemsByClubInvoicingRuleId);
                            if(!asDetail){
                                _setDialogTitle($dialog,clubInvoicingRule.name + " - Invoicing Rule Details");
                            }
                            return {
                                data: results.clubInvoicingRuleDetails
                            };
                        }
                    }
                },
                colModel: [
                _hidden("id","integer"),
                _hidden("club_invoicing_id","integer"),
                _hidden("club_invoicing_rule_id","integer"),
                _hidden("run_count","integer"),
                _idSelectList("Item", "invoice_item_id", 150, "invoiceItemByClubInvoicingId", "club_invoicing_id"),
                _idSelectList("Tax Group", "tax_group_id", 80, "taxGroup"),
                _invoicingRuleTypeDataEditor("Billing Rules", "invoicing_rule_type_data", 80, {
                    getRuleTypeDataValues:function(ui, options){
                        var rowData = ui.rowData;
                        options.club_invoicing_id = values.club_invoicing_id;
                        options.club_invoicing_rule_id = club_invoicing_rule_id;
                        options.club_invoicing_rule_detail_id = rowData.id;
                        options.invoicing_rule_type_data_json = rowData.invoicing_rule_type_data;
                        options.invoice_item_id = rowData.invoice_item_id;
                    }
                }),
                //_idSelectList("Sales Rep.", "sales_person_id", 120, _lookups.users.select, _lookups.users.byId, {0:'None'}, [{dataIndx:'commission',sourceIndx:'default_commission'}]),
                _idSelectList("Sales Rep.", "sales_person_id", 100, "salesPeople", null, 
                    function(ui, salesPerson, $this, $grid){
                        if(!salesPerson){
                            salesPerson = {id:null,default_commission:0};
                        }
                        var rowData = ui.rowData, rowIndx = ui.rowIndx, $head = $grid.find('.fta-grid-top');
                        _updateRow($grid, rowIndx, {
                            sales_person_id:salesPerson.id,
                            commission:salesPerson.default_commission
                        }, true);
                    },'--'),
                _percentage("Commission","commission",50),
                _integer("Commission On First","commission_on_first",50),
                _integer("Reoccur Limit","reoccur_limit",50,true,function(ui){
                        var rowData = ui.rowData, val = rowData.reoccur_limit;
                        if(!val){
                            val = 'Unlimited';
                        }
                        return val + ' / ' + rowData.run_count;
                }),
                _textArea("Description","description",false,true,function(ui){
                        var rowData = ui.rowData, val = rowData.description;
                        if(typeof val == "undefined" || !val.length){
                            return 'Auto';
                        }
                        return _formatText(val);
                }),
                //_textBox("Count Rule","club_invoicing_rule_type_data",80),
                _float("Qty.","quantity",'1%',true,function(ui){
                        var rowData = ui.rowData, val = rowData.quantity;
                        if(!val){
                            return 'Auto';
                        }
                        return _formatDecimal(val);
                }),
                _float("Rate","rate",'1%',true,function(ui){
                        var rowData = ui.rowData, val = rowData.rate;
                        if(!val){
                            return 'Auto';
                        }
                        return _formatDecimal(val);
                }),
                
                _boolean("Disabled","disabled"),
                _rowModButtons()
                ]
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, 500, 920);
            }
 
        }
        
        var _manageInvoices = function(options, asDetail){
            
            
            if(typeof options != "object"){
                options = {club_invoicing_id:options};
            }
            var dialog_id = '';
            switch(options.mode){
                case 'pastdue':
                    dialog_id += '-pastdueinv';
                    break;
            
                case 'unpaid':
                    dialog_id += '-unpaidinv';
                    break;
                    
                case 'unsent':
                    dialog_id += '-unsentinv';
                    break;
                    
                case 'list_all':
                    dialog_id += '-allinv';
                    options.club_invoicing_id = null; // All doesn't use club_invoicing_id
                    break;
                    
                case 'run_rules':
                    dialog_id += '-runrulesinv';
                    break;
            }
            
            var club_invoicing_id = options.club_invoicing_id;
            if(club_invoicing_id){
                dialog_id += '-clubinv'+club_invoicing_id;
            }
            
            options.title = "Invoice Listing ...";
            
            var $dialog,$grid, window_width = 1090;
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner("ftact-popup-invoices"+dialog_id,options.title);
                $grid = $dialog.find('.ftact-grid');
            }
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    ftapi.del({
                        command:'invoice',
                        id:id,
                        success:function(){
                            _commitDelete($_grid, rowData, rowIndx);
                        //_setInvoiceItemTypeLookup();
                        },
                        beforeError:function(errortxt, errorraw){
                            //alert(errortxt);
                            _rollBackDelete($_grid, rowData, rowIndx);
                        }
                    });
    
                },
                /*
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    
                    ftapi.put({
                        command:'invoice',
                        data:_filterObject(rowData, [
                            'id','club_invoicing_id','due_date','sent_to_club','voided',
                            'purchase_order','bill_to','notes'
                            ]),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        //_setInvoiceItemTypeLookup();
                        }
                    });
                    
                },
                */
                onedit:function(e, ui, options){
                    var $grid = ui.$grid, rowData = ui.rowData;
                    _invoicing.editInvoice({
                        club_invoicing_id:rowData.club_invoicing_id,
                        invoice_id:rowData.id,
                        save:function(){
                            $grid.pqGrid('refreshDataAndView');
                        }
                    });
                    return;
                }
            });
            
            var columns = [
            _checkBoxSelection(),
            _id("Invoice #","id",80),
            _hidden("club_invoicing_id","integer"),
            _hidden("purchase_order","PO"),
            //_textBox("PO","purchase_order",50,false),
            _textBoxSelectFilter("Sales Reps.","sales_people",80,false),
            _idSelectList("Terms", "terms_id", 80, "terms"),
            _stringDate("Date","date",80,false),
            _stringDate("Voided","voided",80,false),
            _stringDate("First Sent","sent_to_club",80,false),
            _stringDate("Last Email","sent_email_to_club",80,false),
            _currency("Total","total","7%",false),
            _stringDate("Payment Date","last_payment_date",80,false),
            _currency("Paid","payment_amount","7%",false),
            _currency("Due","amount_due","7%"),
            _stringDate("Date Due","due_date",80,false),
            _integer("Past Due","days_past_due","1%",false),
            _rowModButtons()
            ];
            
            if(!club_invoicing_id){
                columns.splice(3, 0, _textBox("Club","club_name",160,false));
                window_width = 1300;
            }
            
            var toolbar = {
                items:[
                _customButton('Create New Invoice',function(){
                    _editInvoice({
                        club_invoicing_id:club_invoicing_id,
                        save:function(){
                            _refreshGrid($grid);
                        }
                    });
                }),
                _separator(),
                {
                    type: 'button', 
                    icon: 'ui-icon-mail-closed', 
                    label: 'Send to Club', 
                    cls: 'send-to-club', 
                    listener:

                    {
                        "click": function (evt, ui){
                            var $body = $('body');
                            /*
                            
                                */
                            //var selection = $grid.pqGrid('selection',{type:'row',method:'getSelection'});
                            var i, selected = [], row, rows = _getCheckedRows($grid), date = new Date().format('yyyy-mm-dd HH:MM:ss');
                            for(i = 0; i < rows.length; i++){
                                row = $.extend({},rows[i]); // Copy row, so we don't modify existing data
                                if(!row.sent_to_club && !row.voided){
                                    row.last_payment_date = date;
                                    selected.push(row);
                                }
                            }
                                
                            if(!selected.length){
                                $body.foreTeesModal('alertNotice',{
                                    width:550,
                                    title:'Nothing to Send',
                                    alertMode:false,
                                    message:(!rows.length)?'You must first select the invoices you wish to send.':'All of the invoices selected are either already sent or voided.'
                                });   
                            } else {
                                var message = "Are you sure you want to send " + selected.length + " invoices?";
                                    
                                if(rows.length > selected.length){
                                    message += "<br><br><b>NOTE:</b> " + (rows.length - selected.length) + " of the "+rows.length+" selected are either already sent or voided and will not be processed.";
                                }
                                
                                _promptYesNo($grid,{
                                    title:"Confirm Send",
                                    message:message,
                                    onContinue:function(){
                                            
                                        $body.foreTeesModal('pleaseWait');
                                        ftapi.put({
                                            command:'invoiceSendToClub',
                                            data:selected,
                                            success:function(response){
                                                //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                                $grid.pqGrid('refreshDataAndView');
                                                $body.foreTeesModal('pleaseWait','close');
                                            },
                                            beforeError:function(){
                                                $body.foreTeesModal('pleaseWait','close');
                                            }
                                        });
                                        $(this).dialog("close");
                                    },
                                    onCancel:function(){
                                        
                                    }
                                });
                            }
                        }
                    }
                },
                _separator(),
                {
                    type: 'button', 
                    icon: 'ui-icon-check', 
                    label: 'Set as Paid', 
                    cls: 'paid', 
                    listener:

                    {
                        "click": function (evt, ui){
                            var $body = $('body');
                            /*
                            
                                */
                            //var selection = $grid.pqGrid('selection',{type:'row',method:'getSelection'});
                            var i, selected = [], row, rows = _getCheckedRows($grid), date = new Date().format('yyyy-mm-dd HH:MM:ss');
                            for(i = 0; i < rows.length; i++){
                                row = $.extend({},rows[i]); // Copy row, so we don't modify existing data
                                if(row.amount_due > 0 && !row.voided){
                                    row.last_payment_date = date;
                                    selected.push(row);
                                }
                            }
                                
                            if(!selected.length){
                                $body.foreTeesModal('alertNotice',{
                                    width:550,
                                    title:'Nothing to set as Paid',
                                    alertMode:false,
                                    message:(!rows.length)?'You must first select the invoices you wish to mark as paid.':'All of the invoices selected are either already marked as paid or are voided.'
                                });   
                            } else {
                                var message = "Are you sure you want to mark " + selected.length + " invoices as paid?";
                                    
                                if(rows.length > selected.length){
                                    message += "<br><br><b>NOTE:</b> " + (rows.length - selected.length) + " of the "+rows.length+" selected are either already marked as paid or are voided and will not be processed.";
                                }
                                
                                _promptYesNo($grid,{
                                    title:"Confirm Setting Paid",
                                    message:message,
                                    onContinue:function(){
                                            
                                        $body.foreTeesModal('pleaseWait');
                                        ftapi.put({
                                            command:'invoiceSetPaid',
                                            data:selected,
                                            success:function(response){
                                                //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                                $grid.pqGrid('refreshDataAndView');
                                                $body.foreTeesModal('pleaseWait','close');
                                            },
                                            beforeError:function(){
                                                $body.foreTeesModal('pleaseWait','close');
                                            }
                                        });
                                        $(this).dialog("close");
                                    },
                                    onCancel:function(){
                                        
                                    }
                                });
                            }
                        }
                    }
                },
                _separator(),
                _exportCsvButton($grid,function(){return options.title}),
                _separator(),
                _refreshGridButton()
                ]
            }
            
            if(!club_invoicing_id){
                // Remove toolbar buttons that require club_invoicing_id
                toolbar.items.shift();
                toolbar.items.shift();
            }
            
            var gridModel = {
                freezeCols: 2,
                load: function(e, ui){
                     _updateSelectFilter($grid,"sales_people", true);
                    var i, row, rows = _getAllRowData($grid);
                    for (i = 0; i < rows.length; i++){
                        row = rows[i];
                        if(row.voided){
                            $grid.pqGrid('addClass',{
                                cls:'ftaVoided',
                                rowIndx:i
                            });
                        } else if(row.amount_due <= 0) {
                            $grid.pqGrid('addClass',{
                                cls:'ftaPaid',
                                rowIndx:i
                            });
                        } else {
                            
                            if(row.days_past_due > 0){
                                $grid.pqGrid('addClass',{
                                    cls:'ftaPastDue',
                                    rowIndx:i
                                });
                            }
                            
                            if(row.sent_to_club) {
                                $grid.pqGrid('addClass',{
                                    cls:'ftaSentToClub',
                                    rowIndx:i
                                });
                            } else {
                                $grid.pqGrid('addClass',{
                                    cls:'ftaNotSentToClub',
                                    rowIndx:i
                                });
                            }
                        }
                    }
                },
                toolbar: toolbar,
                selectionModel: {
                    type:null
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        var cmd = [
                        
                        {
                            command:'invoiceTerms'
                        },
                        {
                            command:'intervals'
                        }
                        ];
                        if(club_invoicing_id){
                            cmd.push({
                                command:'clubInvoicing',
                                id:club_invoicing_id
                            });
                        }
                        switch(options.mode){
                            case 'pastdue':
                                cmd.push({
                                    command:'invoicesPastdue',
                                    id:club_invoicing_id
                                });
                                break;
            
                            case 'unpaid':
                                cmd.push({
                                    command:'invoicesUnpaid',
                                    id:club_invoicing_id
                                });
                                break;
                                
                           case 'unsent':
                                cmd.push({
                                    command:'invoicesUnsent',
                                    id:club_invoicing_id
                                });
                                break;
                                
                           case 'run_rules':
                                cmd.push({
                                    command:'invoicesFromClubInvoicingRules',
                                    id:club_invoicing_id
                                });
                                break;
                                
                            case 'list_all':
                                cmd.push({
                                    command:'invoices'
                                });
                                break;
                                
                            default:
                                cmd.push({
                                    command:'invoicesByClubInvoicingId',
                                    id:club_invoicing_id
                                });
                                break;
                        }
                        return {
                            url:ftapi.uri(cmd)
                            };
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            _setTermsLookup(data.results.invoiceTerms);
                            _setIntervalLookup(data.results.intervals);
                            
                            var result, title;
                            switch(options.mode){
                                case 'pastdue':
                                    result = {
                                        data: data.results.invoicesPastdue
                                    };
                                    title = "Past Due Invoices";
                                    break;
            
                                case 'unpaid':
                                    result = {
                                        data: data.results.invoicesUnpaid
                                    };
                                    title = "Unpaid Invoices";
                                    break;
                                    
                                case 'unsent':
                                    result = {
                                        data: data.results.invoicesUnsent
                                    };
                                    title = "Unsent Invoices";
                                    break;
                                    
                                case 'list_all':
                                    result = {
                                        data: data.results.invoices
                                    };
                                    title = "All Invoices";
                                    break;
                                    
                                case 'run_rules':
                                    result = {
                                        data: data.results.invoicesFromClubInvoicingRules
                                    };
                                    title = "Invoices From Last Rule Execution";
                                    break;
                                    
                                default:
                                    result =  {
                                        data: data.results.invoicesByClubInvoicingId
                                        };
                                    title = "Invoice Listing";
                                    break;
                            }
                            
                            
                            if(club_invoicing_id){
                                var ci = data.results.clubInvoicing[0];
                                if(ci){
                                    title += ": " + ci.club_name + ' - ' + ci.name;
                                }
                            }
                            options.title = title;
                            if(!asDetail){
                                _setDialogTitle($dialog,title);
                            }
                            
                            return result;
                        }
                    }
                },
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                },
                colModel: columns
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, window_width, 500);
            }
 
        }
        
        
        var _editInvoice = function(options, asDetail){
            
            var position = options.position, height = options.height, width = options.width;
            if(!width){
                width = 1200;
            }
            if(!height){
                height = 600;
            }
            
            
            
            if(typeof options != "object"){
                options = {invoice_id:options};
            }

            var invoice_id = options.invoice_id, club_invoicing_id = options.club_invoicing_id;
            if(!invoice_id){
                invoice_id = 0;
            }
            
            var title = !invoice_id?'New Invoice':'Invoice #'+invoice_id+' ';
            var popup_id = "ftact-popup-invoice-"+invoice_id+"-"+club_invoicing_id;

            var $dialog,$grid;
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner(popup_id,title);
                $grid = $dialog.find('.ftact-grid');
            }
            
            var values = {
                changed: false,
                isNew: !invoice_id,
                invoice_id: invoice_id,
                club_invoicing_rule_id:null,
                asDetail:asDetail,
                payment:0,
                forceClose:false,
                isVoided: false,
                sentToClub: false,
                isPaid: false
            }
            
            var $summary;
            
            _activateGridElements({
                $grid:$grid,
                deleteRecord:function(rowIndx, $_grid, rowData, id){
                    _commitDelete($_grid, rowData, rowIndx);                    
    
                },
                updateRecord:function(rowIndx, $_grid, rowData, type){
                    /*
                    ftapi.put({
                        command:'clubInvoicingRuleDetail',
                        data:_filterObject(rowData, [
                            'id','club_invoicing_rule_id','invoice_item_id','tax_group_id','sales_person_id',
                            'club_invoicing_rule_type_data','commission','commission_on_first','disabled'
                            ]),
                        success:function(response){
                            _commitEdit($_grid, rowData, rowIndx, type, response);
                        //_setInvoiceItemTypeLookup();
                        }
                    });
                    */

                },
                initButtons:[
                    _viewRuleDataButtonInit()
                ],
                getRuleTypeDataValues:function(ui, options){
                    var rowData = ui.rowData, $head = $grid.find('.fta-grid-top');
                    options.club_invoicing_id = club_invoicing_id;
                    options.club_invoicing_rule_id = _getIntOrNull($head.find('[name=club_invoicing_rule_id]').val());
                    options.club_invoicing_rule_detail_id = rowData.club_invoicing_rule_detail_id;
                    options.invoicing_rule_type_data_json = rowData.invoicing_rule_type_data;
                    options.invoice_item_id = rowData.invoice_item_id;
                    options.date_start = $head.find('[name=date]').val();
                }
            });
            
            
            
            var toolbar = {
                items:[
                _addRowButton('Add Invoice Detail',function(){
                    return{
                        id:null,
                        invoice_id:null,
                        invoice_item_id:null,
                        club_invoicing_id:club_invoicing_id,
                        club_invoicing_rule_detail_id:null,
                        description:null,
                        invoicing_rule_type_data:null,
                        tax_rate:0,
                        tax_group_id:null,
                        quantity:1,
                        sales_person_id:null,
                        commission:0,
                        rate:0
                    }
                }),
                {type: 'separator'},
                {type: 'button', icon: 'ui-icon-mail-closed', label: 'Send to Club', cls: 'send-to-club', listener:
                    {"click": function (evt, ui) {
                        var $body = $('body');
                            
                            
                            function __sendToClub(){
                                ftapi.put({
                                    command:'invoiceSendToClub',
                                    data:{
                                        id:invoice_id?invoice_id:null
                                    },
                                    success:function(response){
                                        //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                        $grid.addClass('invoiceIsSentToClub');
                                        $grid.pqGrid('history',{
                                            method:'reset'
                                        });
                                        $grid.pqGrid('refreshDataAndView');
                                        if(typeof options.save == "function"){
                                            options.save(response);
                                        }
                                        $body.foreTeesModal('pleaseWait','close');
                                    },
                                    beforeError:function(){
                                        $body.foreTeesModal('pleaseWait','close');
                                    }
                                });
                            }
                            
                            //if(true){
                            _promptYesNo($grid,{
                                title:"Confirm Send To Club",
                                message:'Send email and notify the club of this invoice?',
                                onContinue:function(){    
                                    $body.foreTeesModal('pleaseWait');
                                    __sendToClub();
                                    $(this).dialog("close");
                                },
                                onCancel:function(){
                                        
                                }
                            });
                        //}
                            
                            
                            
                    }
                    },
                    options: {disabled: values.isNew}
                },
                {type: 'button', icon: 'ui-icon-document', label: 'View Invoice', cls: 'view-invoice', listener:
                    {
                        "click": function (evt, ui) {
                            //saveChanges();
                            var $body = $('body');
                            $body.foreTeesModal('alertNotice',{
                                width:900,
                                title:'Invoice #'+invoice_id,
                                alertMode:false,
                                message:'',
                                init:function($modalObj){
                                    $body.foreTeesModal('pleaseWait');
                                    ftinvoice.loadAndDisplay($modalObj,invoice_id, function(){
                                        $modalObj.foreTeesModal("triggerResize");
                                        $body.foreTeesModal('pleaseWait','close');
                                    });
                                },
                                allowContinue:true,
                                continueButton:'Print Invoice',
                                continueAction:function($modalObj){
                                    $body.foreTeesModal('pleaseWait');
                                    ftinvoice.loadAndPrint(invoice_id,  function(){
                                        $body.foreTeesModal('pleaseWait','close');
                                    });
                                }
                            });   
                    }
                    },
                    options: {disabled: values.isVoided || values.isNew}
                },
                {type: 'button', icon: 'ui-icon-document', label: 'Download PDF', cls: 'view-invoice', listener:
                    {"click": function (evt, ui) {
                        ftinvoice.downloadPdf(invoice_id);  
                    }
                    },
                    options: {disabled: values.isVoided || values.isNew}
                },
                
                {type: 'separator'},
                {type: 'button', icon: 'ui-icon-arrowreturn-1-s', label: 'Undo', cls: 'changes', listener:
                    {"click": function (evt, ui) {
                        $grid.pqGrid("history", {method: 'undo'});
                        $grid.pqGrid('refreshView');
                    }
                    },
                    options: {disabled: true}
                },
                {type: 'button', icon: 'ui-icon-arrowrefresh-1-s', label: 'Redo', listener:
                    {"click": function (evt, ui) {
                        $grid.pqGrid("history", {method: 'redo'});
                        $grid.pqGrid('refreshView');
                    }
                    },
                    options: {disabled: true}
                },
                
                {type: 'separator'},
                {type: 'button', icon: 'ui-icon-check', label: 'Set as Paid', cls: 'paid', listener:
                    {"click": function (evt, ui) {
                        var $body = $('body');
                            $body.foreTeesModal('pleaseWait');
                            ftapi.put({
                                command:'invoiceSetPaid',
                                data:{id:invoice_id?invoice_id:null,last_payment_date:new Date().format('yyyy-mm-dd HH:MM:ss')},
                                success:function(response){
                                    //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                    $grid.addClass('invoiceIsPaid');
                                    $grid.pqGrid('history',{
                                        method:'reset'
                                    });
                                    $grid.pqGrid('refreshDataAndView');
                                    if(typeof options.save == "function"){
                                        options.save(response);
                                    }
                                    $body.foreTeesModal('pleaseWait','close');
                                },
                                beforeError:function(){
                                    $body.foreTeesModal('pleaseWait','close');
                                }
                            });
                    }
                    },
                    options: {disabled: values.isNew}
                },
                
                {type: 'button', icon: 'ui-icon-check', label: 'Set as Unpaid', cls: 'unpaid', listener:
                    {"click": function (evt, ui) {
                        var $body = $('body');
                            $body.foreTeesModal('pleaseWait');
                            ftapi.put({
                                command:'invoiceSetPaid',
                                data:{id:invoice_id?invoice_id:null,last_payment_date:null},
                                success:function(response){
                                    //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                    $grid.removeClass('invoiceIsPaid');
                                    $grid.pqGrid('history',{
                                        method:'reset'
                                    });
                                    $grid.pqGrid('refreshDataAndView');
                                    if(typeof options.save == "function"){
                                        options.save(response);
                                    }
                                    $body.foreTeesModal('pleaseWait','close');
                                },
                                beforeError:function(){
                                    $body.foreTeesModal('pleaseWait','close');
                                }
                            });
                    }
                    },
                    options: {disabled: values.isNew}
                },
                
                {type: 'separator'},
                {type: 'button', icon: 'ui-icon-closethick', label: 'Void Invoice', cls: 'void', listener:
                    {"click": function (evt, ui) {
                        var $body = $('body');
                            $body.foreTeesModal('pleaseWait');
                            ftapi.put({
                                command:'invoiceSetVoided',
                                data:{id:invoice_id?invoice_id:null,voided:new Date().format('yyyy-mm-dd HH:MM:ss')},
                                success:function(response){
                                    //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                    $grid.addClass('invoiceIsVoided');
                                    $grid.pqGrid('history',{
                                        method:'reset'
                                    });
                                    $grid.pqGrid('refreshDataAndView');
                                    if(typeof options.save == "function"){
                                        options.save(response);
                                    }
                                    $body.foreTeesModal('pleaseWait','close');
                                },
                                beforeError:function(){
                                    $body.foreTeesModal('pleaseWait','close');
                                }
                            });
                    }
                    },
                    options: {disabled: values.isNew}
                },
                
                {type: 'button', icon: 'ui-icon-closethick', label: 'Unvoid Invoice', cls: 'unvoid', listener:
                    {"click": function (evt, ui) {
                        var $body = $('body');
                            $body.foreTeesModal('pleaseWait');
                            ftapi.put({
                                command:'invoiceSetVoided',
                                data:{id:invoice_id?invoice_id:null,voided:null},
                                success:function(response){
                                    //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                    $grid.removeClass('invoiceIsVoided');
                                    $grid.pqGrid('history',{
                                        method:'reset'
                                    });
                                    $grid.pqGrid('refreshDataAndView');
                                    if(typeof options.save == "function"){
                                        options.save(response);
                                    }
                                    $body.foreTeesModal('pleaseWait','close');
                                },
                                beforeError:function(){
                                    $body.foreTeesModal('pleaseWait','close');
                                }
                            });
                    }
                    },
                    options: {disabled: values.isNew}
                },

                {type: 'separator'},
                {
                    type: 'button', 
                    icon: 'ui-icon-disk', 
                    label: 'Save Changes', 
                    cls: 'save', 
                    listener:

                    {
                        "click": function (evt, ui) {
                            //saveChanges();
                            var invoice_id = values.invoice_id, asDetail = values.asDetail,
                            //$this = $(this), 
                            $head = $grid.find('.fta-grid-top'), 
                            $top = $grid.find('.pq-grid-top'),
                            //$rows = $grid.find('tr.pq-grid-row'),
                            iid = invoice_id?invoice_id:null,
                            $purchase_order = $head.find('[name=purchase_order]'), 
                            //$sent_to_club = $head.find('[name=sent_to_club]'), // read only
                            //$voided = $head.find('[name=voided]'), // read only 
                            $address = $head.find('[name=address]'), 
                            $notes = $head.find('[name=notes]'),
                            $terms = $head.find('[name=terms_id]'),
                            $rules = $head.find('[name=club_invoicing_rule_id]'),
                            $date = $head.find('[name=date]'),
                            $due_date = $head.find('[name=due_date]'),
                            dateFormat = 'yyyy-mm-dd',
                            details = [],
                            rows = _getAllRowData($grid);
                            
                            
                            for(var i = 0; i < rows.length; i++){
                                details.push(_filterObject(rows[i], [
                                        'id','club_invoicing_rule_detail_id','invoice_id','invoice_item_id','tax_group_id','sales_person_id',
                                        'invoicing_rule_type_id','invoicing_rule_type_data','quantity','rate',
                                        'tax_rate','commission','invoice_item_name','tax_group_name','description'
                                        ]));
                            }
                            
                            var date = _objOrNull($date.val());
                            var due_date = _objOrNull($due_date.val());
                            if(date){
                                date = ftcalendar.stringToDate(date).format(dateFormat);
                            }
                            if(due_date){
                                due_date = ftcalendar.stringToDate(due_date).format(dateFormat);
                            }
                            
                            var invoice = {
                                id:iid,
                                club_invoicing_id:club_invoicing_id,
                                club_invoicing_rule_id:_objOrNull($rules.val()),
                                terms_id:_objOrNull($terms.val()),
                                date:date,
                                due_date:due_date,
                                bill_to:_objOrNull($address.val()),
                                notes:_objOrNull($notes.val()),
                                invoice_item_name:null,
                                tax_group_name:null,
                                purchase_order:_objOrNull($purchase_order.val()),
                                details:details
                            }
                            
                            //console.log(invoice);
                            var $body = $('body');
                            $body.foreTeesModal('pleaseWait');
                        
                            ftapi.put({
                                command:'invoiceAndDetails',
                                data:invoice,
                                success:function(response){
                                    var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                    //console.log('DID IT!');
                                    if(!invoice_id){
                                        // This was a new invoice.
                                        //console.log("New invoice? Was: "+invoice_id);
                                        invoice_id = response.id;//, new_popup_id = "ftact-popup-invoice-"+invoice_id+"-"+club_invoicing_id;
                                        //$('#'+popup_id).attr('id',new_popup_id);
                                        //$grid.pqGrid('history',{method:'reset'});
                                        //$grid.pqGrid('refreshView');
                                        if(!asDetail){
                                            var $dlg = $dialog.closest('ui.dialog');
                                            //console.log("changing dialog");
                                            options.width = $dlg.width();
                                            options.height = $dlg.height();
                                            options.position = $dialog.dialog('option','position');
                                            options.invoice_id = invoice_id;
                                            values.forceClose = true;
                                            $dialog.dialog('close');  // Close and re-open the dialog as an edit dialog
                                            _editInvoice(options, asDetail);
                                        }
                                    } else {
                                        //console.log("exisiting invoice");
                                        values.changed = false;
                                        $top.find("button.save", $grid).button("option", {
                                            disabled: true
                                        });
                                        $grid.pqGrid('history',{
                                            method:'reset'
                                        });
                                        
                                        $grid.pqGrid('refreshDataAndView');
                                    }
                                    if(typeof options.save == "function"){
                                        options.save(response);
                                    }
                                    
                                    $body.foreTeesModal('pleaseWait','close');
                                },
                                beforeError:function(){
                                    $body.foreTeesModal('pleaseWait','close');
                                }
                            });
                        }
                    },
                    options: {
                        disabled: true
                    }
                }
                ]
            }
            if(invoice_id){
                //toolbar.items.push(_refreshGridButton());
            }
            
            
            var gridModel = {
                
                freezeCols: 1,
                showBottom:true,
                editable:true,
                editModel:{
                    clicksToEdit:1,
                    saveKey: $.ui.keyCode.ENTER,
                    //onSave: 'next',
                    onBlur:'validate'
                },
                history: function (evt, ui) {
                    var $grid = $(this), 
                        $top = $grid.find('.pq-grid-top'),
                        hideSave = !ui.canUndo && (!values.changed || values.isNew),
                        showVoid = hideSave && !values.isVoided && !values.isNew
                        ;
                    if (ui.canUndo != null) {
                        $top.find("button.changes", $grid).button("option", {
                            disabled: !ui.canUndo
                        });
                        $top.find("button.save", $grid).button("option", {
                            disabled: hideSave
                        });
                        $top.find("button.void", $grid).button("option", {
                            disabled: !showVoid
                        });
                        $top.find("button.unvoid", $grid).button("option", {
                            disabled: hideSave && !values.isNew && values.isVoided
                        });
                        $top.find("button.paid", $grid).button("option", {
                            disabled: !hideSave
                        });
                        $top.find("button.unpaid", $grid).button("option", {
                            disabled: !hideSave
                        });
                        $top.find("button.send-to-club", $grid).button("option", {
                            disabled: !showVoid
                        });
                        $top.find("button.view-invoice", $grid).button("option", {
                            disabled: !showVoid
                        });
                    }
                    if (ui.canRedo != null) {
                        $top.find("button:contains('Redo')", $grid).button("option", "disabled", !ui.canRedo);
                    }
                    $top.find("button:contains('Undo')", $grid).button("option", {
                        label: 'Undo (' + ui.num_undo + ')'
                    });
                    $top.find("button:contains('Redo')", $grid).button("option", {
                        label: 'Redo (' + ui.num_redo + ')'
                    });
                },
                editor: {
                    select: true
                },
                //editable: true,
                toolbar: toolbar,
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        return {
                            url:ftapi.uri([
                            {
                                command:'invoice',
                                id:invoice_id,
                                parent_id:club_invoicing_id
                            },
                            {
                                command:'invoiceItemsByClubInvoicingId',
                                id:club_invoicing_id
                            },
                            {
                                command:'clubInvoicingRules',
                                parent_id:club_invoicing_id
                            },
                            
                            {
                                command:'invoicingRuleTypes'
                            },
                            {
                                command:'clubInvoicing',
                                id:club_invoicing_id
                            },
                            {
                                command:'taxGroups'
                            },
                            {
                                command:'users'
                            },
                            {
                                command:'invoiceTerms'
                            }
                            ])
                        }
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            var $grid = $(this), $head = $grid.find('.fta-grid-top'), 
                            results = data.results,
                            clubInvoicing = results.clubInvoicing[0], 
                            terms = results.invoiceTerms,
                            rules = results.clubInvoicingRules,
                            invoice = results.invoice;
                            _users.setUserLookup(results.users); // (also sets sales perople lookup)
                            _users.setSalesPeopleLookup(results.users);
                            _taxRates.setTaxGroupLookup(results.taxGroups);
                            _setInvoicingRuleTypeLookup(results.invoicingRuleTypes);
                            _setClubInvoicingRuleLookupByParentId(club_invoicing_id,rules);
                            _setTermsLookup(terms);
                            _invoiceItems.setInvoiceItemLookupByClubInvoicingId(club_invoicing_id, results.invoiceItemsByClubInvoicingId);
                            if(!asDetail){
                                _setDialogTitle($dialog,title+ ': '+ clubInvoicing.club_name + ' - ' + clubInvoicing.name);
                            }
                            
                            var $purchase_order = $head.find('[name=purchase_order]'), 
                                $sent_to_club = $head.find('[name=sent_to_club]'),
                                $sent_email_to_club = $head.find('[name=sent_email_to_club]'),
                                $voided = $head.find('[name=voided]'), 
                                $last_payment = $head.find('[name=last_payment]'), 
                                $address = $head.find('[name=address]'), 
                                $notes = $head.find('[name=notes]'),
                                $terms = $head.find('[name=terms_id]'),
                                $rules = $head.find('[name=club_invoicing_rule_id]'),
                                $date = $head.find('[name=date]'),
                                $due_date = $head.find('[name=due_date]'),
                                dateFormat = 'mm/dd/yyyy', dateFormatLong = 'yyyy-mm-dd HH:MM', initClass = 'fta-init';
                            
                            values.isVoided = invoice.voided;
                            values.sentToClub = invoice.sent_to_club;
                            values.isPaid = invoice.amount_due <= 0;
                            
                            
                            
                            $rules.empty();
                            //$rules.append('<option value="">--</option>');
                            rules.forEach(function(t){
                                $rules.append('<option value="'+t.id+'">'+t.name+'</option>');
                            });
                            var rule_id = invoice.club_invoicing_rule_id;
                            if(!rule_id && rules.length){
                                rule_id = rules[0].id;
                            } else {
                                $rules.attr('disabled','disabled');
                            }
                            $rules.val(rule_id);
                            $rules.change(function(){
                                values.club_invoicing_rule_id = _getIntOrNull($(this).val());
                            });
                            
                            values.club_invoicing_rule_id = rule_id;
                            
                            // Must do terms first, else pre-selected dates could change
                            $terms.empty();
                            terms.forEach(function(t){
                                $terms.append('<option value="'+t.id+'">'+t.name+'</option>');
                            });
                            var terms_id = invoice.terms_id;
                            if(!terms_id){
                                terms_id = terms[0].id;
                            }
                            $terms.val(terms_id);
                            $terms.not('.'+initClass).addClass(initClass).change(function(){
                                __updateDueDate(_lookups.terms.byId[$(this).val()].days);
                            });
                            
                            $address.val(invoice.bill_to);
                            $notes.val(invoice.notes);
                            $purchase_order.val(invoice.purchase_order);
                            
                            $date.val(ftcalendar.stringToDate(invoice.date).format(dateFormat));
                            $date.not('.'+initClass).addClass(initClass).change(function(){
                                __updateDueDate(_lookups.terms.byId[$terms.val()].days);
                            });
                            
                            if(!invoice.due_date){
                                __updateDueDate(_lookups.terms.byId[terms_id].days);
                            } else {
                                $due_date.val(ftcalendar.stringToDate(invoice.due_date).format(dateFormat));
                            }
                            
                            
                            $voided.attr("disabled","disabled");
                            if(invoice.voided){
                                $voided.val(ftcalendar.stringToDate(invoice.voided).format(dateFormatLong));
                                $grid.addClass('invoiceIsVoided');
                            } else {
                                $voided.val('');
                                $grid.removeClass('invoiceIsVoided');
                            }
                            $last_payment.attr("disabled","disabled");
                            if(invoice.last_payment_date){
                                $last_payment.val(ftcalendar.stringToDate(invoice.last_payment_date).format(dateFormatLong));
                                $grid.addClass('invoiceIsPaid');
                            } else {
                                $last_payment.val('');
                                $grid.removeClass('invoiceIsPaid');
                            }
                            $sent_to_club.attr("disabled","disabled");
                            if(invoice.sent_to_club){
                                $sent_to_club.val(ftcalendar.stringToDate(invoice.sent_to_club).format(dateFormatLong));
                                $grid.addClass('invoiceIsSentToClub');
                            } else {
                                $sent_to_club.val('');
                                $grid.removeClass('invoiceIsSentToClub');
                            }
                            $sent_email_to_club.attr("disabled","disabled");
                            if(invoice.sent_email_to_club){
                                $sent_email_to_club.val(ftcalendar.stringToDate(invoice.sent_email_to_club).format(dateFormatLong));
                            } else {
                                $sent_email_to_club.val('');
                            }
                            
                            
                            
                            function __updateDueDate(days){
                                var date = ftcalendar.stringToDate($date.val());
                                var due_date = ftcalendar.addDays(date, days);
                                $due_date.val(ftcalendar.stringToDate(due_date).format(dateFormat));
                            }
                            
                            $head.find('input, select, textarea').change(function(){
                                values.changed = true;
                                var $top = $grid.find('.pq-grid-top');
                                $top.find("button.save", $grid).button("option", {
                                    disabled: (!$grid.pqGrid('isDirty') && (!values.changed || values.isNew))
                                });
                            });
                            
                            var payment = invoice.payment_amount;
                            
                            values.payment = typeof payment == "undefined"?0:payment;
                            values.forceClose = false; // It's strange that it must be set here again.
                            
                            ftActivateElements($grid);
                            
                            return {
                                data: invoice.details
                            };
                        }
                    }
                },
                editorEnd:function(evt,ui){
                    $grid.pqGrid("refreshRow",{rowIndx:ui.rowIndx});
                },
                
                colModel: [
                _hidden("id","integer"),
                _hidden("invoice_id","integer"),
                _hidden("club_invoicing_id","integer"),
                _hidden("club_invoicing_rule_detail_id","integer"),
                _hidden("invoicing_rule_type_id","integer"),
                _hidden("tax_rate",'float'),
                _hidden("tax_group_name","string"),
                _hidden("invoice_item_name","string"),
                _float("Qty.","quantity","6%"),
                _idSelectList("Item", "invoice_item_id", 150, "invoiceItemByClubInvoicingId",club_invoicing_id,
                    function(ui, invoiceItem, $this, $grid){
                        var rowData = ui.rowData, rowIndx = ui.rowIndx, $head = $grid.find('.fta-grid-top'),
                        rule_id = _getIntOrNull($head.find('[name=club_invoicing_rule_id]').val()),
                        item_id = invoiceItem.id;
                        $grid.pqGrid('showLoading');
                        var command, id;
                        if(rowData.club_invoicing_rule_detail_id){
                            command = 'invoicingRuleTypeDataByClubInvoicingRuleDetailId';
                            id = rowData.club_invoicing_rule_detail_id;
                        } else if(rule_id){
                            command = 'invoicingRuleTypeDataByClubInvoicingRuleId';
                            id = rule_id;
                        } else {
                            // This will result in an error
                            command = 'invoicingRuleTypeDataByClubInvoicingRuleId';
                            id = null;
                        }
                        ftapi.get({
                            command:command,
                            id:id,
                            detail_id:item_id,
                            date_start:$head.find('[name=date]').val(),
                            data:rowData.invoicing_rule_type_data,
                            success:function(invoicing_rule_type_data){
                                $grid.pqGrid('hideLoading');
                                //console.log(invoicing_rule_type_data);
                                var ii = invoicing_rule_type_data.invoice_item;
                                _updateRow($grid, rowIndx, {
                                    invoice_item_id:item_id,
                                    invoice_item_name:ii.name,
                                    quantity:invoicing_rule_type_data.quantity,
                                    rate:ii.rate,
                                    tax_group_id:ii.default_tax_group_id,
                                    tax_rate:ii.default_tax_rate,
                                    description:ii.description,
                                    invoicing_rule_type_id:invoicing_rule_type_data.invoicing_rule_type.id,
                                    invoicing_rule_type_data:JSON.stringify(invoicing_rule_type_data)
                                }, true);
                            }
                        },{
                            beforeError:function(){
                                $grid.pqGrid('hideLoading');
                                _undoLast($grid);
                            }
                        });
                        
                    },
                    null,
                    function(ui, invoiceItem, $grid){
                        //return invoiceItem.name;
                        var text = ui.rowData.invoice_item_name;
                        if(!text){
                            //text = invoiceItem.name;
                            if(!invoiceItem.name){
                                text = "--";
                            } else {
                                text = invoiceItem.name;
                            }
                        }
                        return text;
                    }),
                _textArea("Description","description", 200),
                _invoicingRuleTypeDataEditor("Rule Used", "invoicing_rule_type_data", "10%", {
                    no_view:true, 
                    lock_edit:true, 
                    skip_counts:true,
                    getRuleTypeDataValues:function(ui, options){
                        var rowData = ui.rowData, $head = $grid.find('.fta-grid-top');
                        options.club_invoicing_id = club_invoicing_id;
                        options.club_invoicing_rule_id = _getIntOrNull($head.find('[name=club_invoicing_rule_id]').val());
                        options.club_invoicing_rule_detail_id = rowData.club_invoicing_rule_detail_id;
                        options.invoicing_rule_type_data_json = rowData.invoicing_rule_type_data;
                        options.invoice_item_id = rowData.invoice_item_id;
                        options.date_start = $head.find('[name=date]').val();
                    }
                }),
                _idSelectList("Sales Rep.", "sales_person_id", 100, "salesPeople", null, 
                    function(ui, salesPerson, $this, $grid){
                        if(!salesPerson){
                            salesPerson = {id:null,default_commission:0};
                        }
                        var rowData = ui.rowData, rowIndx = ui.rowIndx, $head = $grid.find('.fta-grid-top');
                        //console.log(salesPerson);
                        _updateRow($grid, rowIndx, {
                            sales_person_id:salesPerson.id,
                            commission:salesPerson.default_commission
                        }, true);
                    },'--'),
                _percentage("Commission","commission","6%",true,function(ui){
                        var rowData = ui.rowData, qty = rowData.quantity, rate = rowData.rate, crate = rowData.commission, cval = '--';
                        if(typeof crate != "undefined"){
                            cval = _formatPercentage(crate);
                        }
                        if(typeof qty == "undefined" || typeof rate == "undefined" || typeof crate == "undefined" ){
                            return cval;
                        }
                        return _formatCurrency((rate*qty)*crate) + ' ['+cval+']';
                }),
                _idSelectList("Tax Group", "tax_group_id", "6%", "taxGroup", null,
                    function(ui, taxGroup, $this, $grid){
                        var rowData = ui.rowData, rowIndx = ui.rowIndx, $head = $grid.find('.fta-grid-top');
                        //console.log(taxGroup);
                        _updateRow($grid, rowIndx, {
                            tax_group_id:taxGroup.id,
                            tax_rate:taxGroup.group_rate,
                            tax_group_name: _getfullTaxGroupName(taxGroup)
                        }, true);
                    },
                    null,
                    function(ui, taxGroup, $grid){
                        //return taxGroup.name + ': ' + _formatPercentage(taxGroup.group_rate);
                        var text = ui.rowData.tax_group_name;
                        if(!text){
                            if(!taxGroup.name){
                                text = "--";
                            } else {
                                text = _getfullTaxGroupName(taxGroup);
                            }
                        }
                        return text;
                    }),
                _currency("Price Ea.","rate","6%"),
                _currency("Amount","cost","7%",false,function(ui){
                        var rowData = ui.rowData, qty = rowData.quantity;
                        if(typeof qty == "undefined"){
                            return rowData[ui.dataIndx];
                        }
                        return rowData.rate*qty;
                }),
                _currency("Tax","tax","5%",false,function(ui){
                        var rowData = ui.rowData, qty = rowData.quantity;
                        if(typeof qty == "undefined"){
                            return rowData[ui.dataIndx];
                        }
                        return (rowData.rate*qty)*rowData.tax_rate;
                }),
                _rowDeleteButton()
                ],
                render:function(){
                    var headHtml = '';
                    
                    headHtml += '<div class="fta-left">'
                    +_inputWraper("Invoice To", '<textarea name="address"></textarea>', 'fta-field-address')
                    +_inputWraper("Notes (Visible on Invoice)", '<textarea name="notes"></textarea>', 'fta-field-address')
                    +'</div>'
                    +'<div class="fta-right">'
                    + _inputWraper("P.O. Number", '<input type="text" name="purchase_order" class="ft_standard_input"></input>', 'fta-field-id ftaHidden')
                    + _inputWraper("Last Payment", '<input type="text" name="last_payment"></input>', 'fta-field-long-date')
                    + _inputWraper("First Sent", '<input type="text" name="sent_to_club"></input>', 'fta-field-long-date')
                    + _inputWraper("Last Email", '<input type="text" name="sent_email_to_club"></input>', 'fta-field-long-date')
                    + _inputWraper("Voided", '<input type="text" name="voided"></input>', 'fta-field-long-date')
                    +'<br/>'
                    + _inputWraper("Invoicng Rule", '<select name="club_invoicing_rule_id" class="ft_standard_input"></select>', 'fta-field-rules')
                    + _inputWraper("Terms", '<select name="terms_id" class="ft_standard_input"></select>', 'fta-field-terms')
                    + _inputWraper("Date", '<input type="text" name="date" class="ft_date_picker_field"></input>', 'fta-field-date')
                    + _inputWraper("Date Due", '<input type="text" name="due_date" class="ft_date_picker_field"></input>', 'fta-field-date')
                    +'</div>'
                    
                    var $grid = $(this), 
                    $head = $('<div class="fta-grid-top">'+headHtml+'</div>');
                    _selectGridElements($grid, '.pq-grid-top').css("background-image","none").append($head);
                    $summary = $('<div class="pq-grid-summary"></div>');
                    _selectGridElements($grid, '.pq-grid-bottom').prepend($summary);
                    
                },
                refresh:function(e, ui){
                    var $grid = $(this), total_amount = 0, total_tax = 0, 
                    rows = _getAllRowData($grid), row, amt, data = [], total;
                    if(rows){
                        for(var i = 0; i < rows.length; i++){
                            row = rows[i];
                            amt = row.rate*row.quantity;
                            total_amount += amt;
                            total_tax += amt*row.tax_rate
                        }
                        total = total_amount+total_tax;
                        data = [
                        {
                            rate:"Total + Tax",
                            cost:total,
                            fta_mod_buttons:'-'
                        },

                        {
                            rate:"Payments",
                            cost:values.payment,
                            fta_mod_buttons:'-'
                        },

                        {
                            rate:"Balance",
                            cost:total - values.payment,
                            fta_mod_buttons:'-'
                        }
                        
                        ];
                    }
                    
                    $grid.pqGrid("createTable",{data:data, $cont:$summary});
                },
                cellBeforeSave:function(e, ui){
                    //gridModel.refresh.call(this, e, ui);
                    setTimeout(function(){$grid.pqGrid("refreshView")},1);
                }
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            //console.log(gridModel);
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, width, height, position, function(e,ui){
                    if((!$grid.pqGrid('history',{method:'canUndo'}) && !values.changed) || values.forceClose){
                        //console.log("canUndo:"+$grid.pqGrid('history',{method:'canUndo'}));
                        //console.log("changed"+values.changed);
                        //console.log("forceClose:"+values.forceClose);
                        return true;
                    }
                    _promptYesNo($dialog,{
                        title:"Discard Changes",
                        message:"Are you sure you want to discard changes to this invoice?",
                        onContinue:function(){
                            values.forceClose = true;
                            //$(this).dialog("close");
                            $(this).dialog("close"),
                            $dialog.dialog("close");
                        }
                    });
                    return false;
                });
            }
 
        }
        
        return {
            manageRules:_manageRules, 
            manageRuleDetails:_manageRuleDetails,
            manageInvoices:_manageInvoices,
            editInvoice:_editInvoice
        }
    })();
    
    var _reports = (function(){
        
        var _unpaidCommission = function(options, asDetail){
            
            if(typeof options != "object"){
                options = {sales_person_id:options};
            }
            var dialog_id = '-unpaidcms';
            
            var sales_person_id = options.sales_person_id;
            if(sales_person_id){
                dialog_id += '-slsprsn'+sales_person_id;
            }
            
            options.title = "Unpaid Commission ...";
            
            
            var $dialog,$grid, window_width = 1020;
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner("ftact-popup-report"+dialog_id,options.title);
                $grid = $dialog.find('.ftact-grid');
            }
            _activateGridElements({
                $grid:$grid,
                onedit:function(e, ui, options){
                    var $grid = ui.$grid, rowData = ui.rowData;
                    _invoicing.editInvoice({
                        club_invoicing_id:rowData.club_invoicing_id,
                        invoice_id:rowData.invoice_id,
                        save:function(){
                            $grid.pqGrid('refreshDataAndView');
                        }
                    });
                    return;
                }
            });
            
            var columns = [
            _checkBoxSelection(),
            _hidden("id","integer"),
            _hidden("quantity","float"),
            _hidden("rate","float"),
            _hidden("tax_rate","float"),
            _hidden("club_invoicing_id","integer"),
            _hidden("commission_paid","string"),
            _textBoxSelectFilter("Sales Rep.","sales_person_name",80,false),
            _id("Invoice #","invoice_id",80),
            _stringDate("Invoice Date","invoice_date",80,false),
            _textBox("Item","invoice_item_name",80,false),
            _textBox("Description","description",80,false),
            _currency("Item Total","item_total",60,false,function(ui){
                var rowData = ui.rowData;
                return rowData.rate*rowData.quantity;
            }),
            _percentage("Commission Rate","commission",40,false),
            _currency("Commission","commission_total",40,false,function(ui){
                var rowData = ui.rowData;
                return (rowData.rate*rowData.quantity) * rowData.commission;
            }),
            _rowModButtons()
            ];
            
            var toolbar = {
                items:[
                {
                    type: 'button', 
                    icon: 'ui-icon-check', 
                    label: 'Set Commission as Paid', 
                    cls: 'paid', 
                    listener:

                    {
                        "click": function (evt, ui){
                            var $body = $('body');
                            var i, selected = [], row, rows = _getCheckedRows($grid), date = new Date().format('yyyy-mm-dd HH:MM:ss');
                            for(i = 0; i < rows.length; i++){
                                row = $.extend({},rows[i]); // Copy row, so we don't modify existing data
                                //if(row.amount_due > 0 && !row.voided){
                                    row.commission_paid = date;
                                    selected.push(row);
                                //}
                            }
                            
                            if(!selected.length){
                                $body.foreTeesModal('alertNotice',{
                                    width:550,
                                    title:'Nothing to set as Paid',
                                    alertMode:false,
                                    message:'You must first select the invoice items you wish to mark as paid.'
                                });   
                            } else {
                                var message = "Are you sure you want to mark the commission on " + selected.length + " invoice item(s) as paid?";
                                    
                                //if(rows.length > selected.length){
                                //    message += "<br><br><b>NOTE:</b> " + (rows.length - selected.length) + " of the "+rows.length+" selected are either already marked as paid or are voided and will not be processed.";
                                //}
                                
                                _promptYesNo($grid,{
                                    title:"Confirm Setting Commission Paid",
                                    message:message,
                                    onContinue:function(){
                                            
                                        $body.foreTeesModal('pleaseWait');
                                        ftapi.put({
                                            command:'invoiceDetailSetCommissionPaid',
                                            data:selected,
                                            success:function(response){
                                                //var invoice_id = values.invoice_id, asDetail = values.asDetail;
                                                $grid.pqGrid('refreshDataAndView');
                                                $body.foreTeesModal('pleaseWait','close');
                                            },
                                            beforeError:function(){
                                                $body.foreTeesModal('pleaseWait','close');
                                            }
                                        });
                                        $(this).dialog("close");
                                    },
                                    onCancel:function(){
                                        
                                    }
                                });
                            }
                        }
                    }
                },
                _separator(),
                _exportCsvButton($grid,function(){return options.title}),
                _separator(),
                _refreshGridButton()
                ]
            }
            
            var gridModel = {
                freezeCols: 2,
                load: function(e, ui){
                    _updateSelectFilter($grid,"sales_person_name", true);
                },
                toolbar: toolbar,
                selectionModel: {
                    type:null
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        var cmd = [
                        {
                            command:'invoiceDetailsByUnpaidCommission',
                            id:sales_person_id
                        }
                        ];
                        
                        if(sales_person_id){
                            cmd.push({
                                command:'users',
                                id:sales_person_id
                            });
                        }
                        
                        return {
                            url:ftapi.uri(cmd)
                            };
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            
                            var title = "Unpaid Commission";
                            
                            if(sales_person_id){
                                var sales_person = data.results.sales_person_id[0];
                                if(sales_person){
                                    _setDialogTitle($dialog,title+ ": " + sales_person.name);
                                }
                            }
                                
                            if(!asDetail){_setDialogTitle($dialog,title)}
                            
                            options.title = title;
                            
                            return {
                                data: data.results.invoiceDetailsByUnpaidCommission
                            };
                        }
                    }
                },
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                },
                colModel: columns
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, window_width, 500);
            }
 
        }
        
        
        var _commissionBySalesPersonAndDate = function(options, asDetail){
            
            if(typeof options != "object"){
                options = {sales_person_id:options,start_date: null, end_date: null};
            }
            
            var $dialog, $grid, window_width = 800, dialog_id = '-cmsbydate', sales_person_id = options.sales_person_id, start_date = options.start_date, end_date = options.end_date, sd, ed;
            
            if(sales_person_id){
                dialog_id += '-slsprsn'+sales_person_id;
            }
            if(start_date instanceof Date){
                dialog_id += '-sd'+start_date.format('yyyymmdd');
                sd = start_date.format('yyyy-mm-dd');
            }
            if(end_date instanceof Date){
                dialog_id += '-ed'+end_date.format('yyyymmdd');
                ed = end_date.format('yyyy-mm-dd');
            }
            options.title = "Commission By Date";
            
            if(asDetail){
                $dialog = $(''); // Just incase we try to access $dialog below
                $grid = $('<div></div>');
            } else {
                $dialog = _getDialogGridContiner("ftact-popup-report"+dialog_id,options.title);
                $grid = $dialog.find('.ftact-grid');
            }
            
            _activateGridElements({
                $grid:$grid
            });
            
            var columns = [
            _hidden("id","integer"),
            _textBoxSelectFilter("Sales Rep.","sales_person_name",80,false),
            _stringDate("Commission Date","start_date",160,false),
            _integer("Item Count","detail_count",50,false),
            _currency("Sales","sales_total",50,false),
            _currency("Commission","commission_total",80,false)
            ];
            
            var toolbar = {
                items:[
                _exportCsvButton($grid,function(){return options.title}),
                _separator(),
                _refreshGridButton()
                ]
            }
            
            var gridModel = {
                freezeCols: 2,
                toolbar: toolbar,
                selectionModel: {
                    type:null
                },
                load: function(e, ui){
                    _updateSelectFilter($grid,"sales_person_name", true);
                },
                dataModel: {
                    dataType: 'JSON',
                    location: 'remote',
                    recIndx: 'id',
                    beforeSend:ftapi.setHeaderAuthToken,
                    error:ftapi.xhrError,
                    getUrl: function(){
                        var cmd = [
                        {
                            command:'commissionBySalesPersonAndDate',
                            id:sales_person_id,
                            start_date:sd,
                            end_date:ed
                        }
                        ];
                        
                        if(sales_person_id){
                            cmd.push({
                                command:'users',
                                id:sales_person_id
                            });
                        }
                        
                        return {
                            url:ftapi.uri(cmd)
                            };
                    },
                    getData: function(data) {
                        if(!data.success){
                            ftapi.error(data.error);
                            return {};
                        } else {
                            ftapi.processResponse(data); // get/change api keys, etc.
                            
                            var title = "Commission By Date: ", result = data.results.commissionBySalesPersonAndDate;
                            
                            if(sales_person_id){
                                var sales_person = data.results.sales_person_id[0];
                                if(sales_person){
                                    title += sales_person.name;
                                }
                            }
                            if(sd){
                                title += sd;
                                if(ed){
                                    title += " to ";
                                }
                            }
                            if(ed){
                                title += ed;
                            }
                            options.title = title;
                            if(!asDetail){
                                _setDialogTitle($dialog,title);
                            }
                            
                            return {
                                data: result
                            };
                        }
                    }
                },
                filterModel: {
                    on: true, 
                    mode: "AND", 
                    header: true, 
                    type: 'local'
                },
                colModel: columns
            };
            
            _setGridDefault(gridModel, _gridDefaults());
            
            if(asDetail){
                gridModel.height = 'auto';
                gridModel.flexHeight = true;
                return $grid.pqGrid(gridModel);
            } else {
                _setGridDefault(gridModel, _rootGridDefaults());
                _openDialog($dialog, $grid, gridModel, window_width, 500);
            }
 
        }
        
        return {
            commissionBySalesPersonAndDate:_commissionBySalesPersonAndDate,
            unpaidCommission:_unpaidCommission
        }
    })();

    return {
        exec:_exec, 
        init:_init, 
        users:_users,
        taxRates:_taxRates,
        invoiceItems:_invoiceItems,
        reports:_reports,
        settings:_settings
    }
    
})();

