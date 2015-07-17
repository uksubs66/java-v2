/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(function(){
    
   //Hybrid Init (after page load)
    
    // Init view invoice buttons
    $('.ftViewInvoiceButon').click(function(){
        var $el = $(this);
        var invoice_id = parseInt($el.attr("data-ftinvoiceid"),10);
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
            },
            reloadButton:'Download PDF',
            reloadAction:function($modalObj){
                ftinvoice.downloadPdf(invoice_id);  
            }
        });   
    });
    
    // Init Proshop Hybrid Slot Page Member Selection
    var memberSelectObj = $('body').find(".hybridSlotMemberSelect.slotMemberSelect");
    if(memberSelectObj.length){
        var parmc;
        try{
            parmc = JSON.parse(memberSelectObj.attr("data-ftparmc"));
        }catch(e){
            parmc = false;
        }
        memberSelectObj.foreTeesMemberSelect("init",{
            partnerSelect: false,
            memberSearch: true,
            searchByMemberNumber: true,
            guestTbd: false,
            guestTypes: {},
            showGhin: false,
            showGender:true, // Show M/F in member search list, if avail.
            showMtypeStat:true, // Show abreviation of mtype, if avail.  Currently needs customs to be configured in com/foretees/api/records/MemberType
            filterMship:true, // Show mship/mtype selection filters, if more than one mship/mtype
            selectedUsers: [], // Proshop slot doesn't have usernames, so can't use this
            selectedNames: function(){
                /* Not yet complete.  Eventually we'll allow MemberSelect to callback and check selected members by player name -- but more work needs to be done */
                var names = {};
                var $players = $('form[name=playerform] input[type=text][name^=player]');
                $players.each(function(){
                    var name = $(this).val().trim();
                    if(name.length){
                        names[name.toLowerCase()] = true;
                    }
                });
                return names;
            },
            onMemberSelect:function(record, o){
                var wc = record.wc, tmode, i = 0;
                if (parmc && parmc.tmode_limit > 0) {
                    while (i < parmc.tmode_limit) {             // make sure wc is supported
                        tmode = parmc.tmodea[i];
                        if (tmode && tmode == wc) {
                            break;
                        }
                        i++;
                    }
                    if (i > (parmc.tmode_limit - 1)) {       // if we went all the way without a match
                        wc = parmc.tmodea[0];    // use default option
                    }
                }

                movename(record.display+':'+wc+':'+(record.gender?record.gender:record.mtype_gender)+':'+record.ghin);
            }
        });
    }
    
    
    // Set up some stuff so hybrid slot page doesn't throw errors
    var $slotPageForm = $('form[name=playerform]');
    if($slotPageForm.length){
        var f = $slotPageForm.get(0);
        if(!f.DYN_search){
            f.DYN_search = {
                focus:function(){},
                select:function(){}
            }
        }
    }
    
    
});
