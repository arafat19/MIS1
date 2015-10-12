<table id="flex1" style="display:none"></table>
<script type="text/javascript">

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url:false,
                    dataType:'json',
                    colModel:[
                        {display:"Serial", name:"serial", width:40, sortable:false, align:"right"},
                        {display:"ID", name:"id", width:30, sortable:false, align:"right", hide:true},
                        {display:"Ref No", name:"refNo", width:100, sortable:true, align:"left"},
                        {display:"Amount(BDT)", name:"amountInForeignCurrency", width:90, sortable:true, align:"right"},
                        {display:"Amount("+ localCurrencyName + ")", name:"amountInLocalCurrency", width:90, sortable:true, align:"right"},
                        {display:"Total Due", name:"total_due", width:90, sortable:false, align:"right"},
                        {display:"Customer Name", name:"customerName", width:150, sortable:true, align:"left"},
                        {display:"Beneficiary Name", name:"beneficiaryName", width:150, sortable:true, align:"left"},
                        {display:"Payment Method", name:"paymentMethod", width:100, sortable:true, align:"left"},
                        {display:"Regular Fee", name:"regularFee", width:80, sortable:true, align:"right"},
                        {display:"Discount", name:"discount", width:80, sortable:true, align:"right"}
                    ],
                    buttons:[
                        {name:'Select All', bclass:'select-all', onpress:doSelectAll},
                        {name:'Deselect All', bclass:'deselect-all', onpress:doDeselectAll},
                        {name:'Send To Bank', bclass:'send', onpress:sendTaskToBank},
                        {name:'Cancel Task', bclass:'delete', onpress:doCancelTask},
                        {name:'Show Task Details', bclass:'do_search', onpress:viewTaskDetails},
                        {name:'Show Invoice', bclass:'rename', onpress:viewInvoice},
                        {name: 'Note', bclass: 'note', onpress: viewTaskNote},
                        {name:'Clear Results', bclass:'clear-results', onpress:reloadGrid},
                        {separator:true}
                    ],
                    searchitems:[
                        {display:"Ref No", name:"refNo", width:100, sortable:true, align:"left"},
                        {display:"Customer Name", name:"customerName", width:180, sortable:true, align:"left"},
                        {display:"Amount(BDT)", name:"amountInForeignCurrency", width:120, sortable:true, align:"left"}
                    ],
                    sortname:"refNo",
                    sortorder:"desc",
                    usepager:true,
                    singleSelect:false,
                    title:'All Tasks',
                    useRp:true,
                    rp:15,
                    showTableToggleBtn:false,
                    //width: 725,
                    height:getGridHeight(),
                    initHidden:['do_search', 'send', 'cancel'],
                    afterAjax:function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    }
                }
        );
    }


    function doSelectAll(com, grid) {
        try {
            var rows = $('table#flex1 > tbody > tr');
            if (rows && rows.length > 0) {
                rows.addClass('trSelected');
            }
        } catch (e) {
        }
    }
    function doDeselectAll(com, grid) {
        try {
            var rows = $('table#flex1 > tbody > tr');
            if (rows && rows.length > 0) {
                rows.removeClass('trSelected');
            }
        } catch (e) {
        }
    }

    function executePreConditionForSentToBank(selectedIds) { //
        if (selectedIds.length == 0) {
            showError("Please select a Task to send");
            return false;
        }

        if (!confirm('Are you sure you want to send ' + selectedIds.length + ' task(s) to Bank?')) {
            return false;
        }
        return true;

    }
     //
    function executePostConditionForSentToBank(selectedIds) {
        selectedIds.each(function (e) {
            $(this).remove();
        });
        $('#flex1').decreaseCount(selectedIds.length);
    }

    function sendTaskToBank(com, grid) {
        var selectedIds = $('.trSelected', grid);

        if (!executePreConditionForSentToBank(selectedIds)) {
            return;
        }

        var ids = '';
        selectedIds.each(function (e) {
            var id = $(this).attr('id').replace('row', '');
            ids += id + '_';
        });


        $('span.send').hide();

        showLoadingSpinner(true);
        $.ajax({
            type:'post',
            dataType:'json',
            url:"${createLink(controller: 'exhTask',action: 'sendToBank')}?ids=" + ids,
            success:function (data) {
                if (data.isError == false) {
                    showSuccess(data.message);
                    executePostConditionForSentToBank(selectedIds);
                } else {
                    showError(data.message);
                }
            },
            complete:function (XMLHttpRequest, textStatus) {
                $('span.send').show();
                onCompleteAjaxCall();
            }
        });
    }

    // Cancel Task Script Block Start here
    function doCancelTask(com, grid) {
        var selectedIds = $('.trSelected', grid);
        if (!executePreConditionForCancelTask(selectedIds)) {
            return false;
        }
	    $('#exhCancelTaskConfirmationModal').modal('show');    // show Modal
	    return false;
    }

    function executeCancelTask(reason) {
        var ids = '';
        var selectedIds = $('.trSelected', $('#flex1'));
        selectedIds.each(function (e) {
            var id = $(this).attr('id').replace('row', '');
            ids += id + '_';
        });

        $('span.cancel').hide();

        showLoadingSpinner(true);
        $.ajax({
            type:'post',
            dataType:'json',
            url:"${createLink(controller: 'exhTask',action: 'cancelSpecificTask')}?ids=" + ids + '&reason=' + reason,
            success:function (data) {
                if (data.isError == false) {
                    showSuccess(data.message);
                    executePostConditionForCancelTask(selectedIds);
                } else {
                    showError(data.message);
                }
            },
            complete:function (XMLHttpRequest, textStatus) {
                $('span.cancel').show();
                onCompleteAjaxCall();
            }
        });
    }

    function executePreConditionForCancelTask(selectedIds) {
        if (selectedIds.length == 0) {
            showError("Please select a Task to Cancel");
            return false;
        }
	    return true;
    }

    function executePostConditionForCancelTask(selectedIds) {
        selectedIds.each(function (e) {
            $(this).remove();
        });
        $('#cancellationReason').val('');
        $('#flex1').decreaseCount(selectedIds.length);
    }
    // Cancel Task Script Block end here

    function viewInvoice(com, grid) {
        var ids = $('.trSelected', grid);
        if (!executeCommonPreConditionForGrid(ids)) {
            return false;
        }
        var taskId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhReport', action: 'showInvoiceFromTaskGrid')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewTaskNote(com, grid) {

        var ids = $('.trSelected', grid);
        if (!executeCommonPreConditionForGrid(ids)) {
            return false;
        }
        var taskId = $(ids[ids.length - 1]).attr('id').replace('row', '');

        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhTask', action: 'showEntityNoteForTask')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }

    // view task details
    function viewTaskDetails(com, grid) {
        var ids = $('.trSelected', grid);
        if (!executeCommonPreConditionForGrid(ids)) {
            return false;
        }

        var securityNo = $(ids[ids.length - 1]).find("td").eq(2).find("div").html();
        var securityType = "Ref No";

        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhTask', action: 'showTaskDetailsForAdmin')}?securityNo=" + securityNo + "&securityType=" + securityType + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;
        $.history.load(formatLink(loc));
        return false;
    }

    function executeCommonPreConditionForGrid(selectedIds) {
        if (selectedIds.length == 0) {
            showError("Please select a row to perform this operation");
            return false;
        } else if (selectedIds.length > 1) {
            showError("Multiple rows can not be selected for this operation.");
            return false;
        } else {
            return true;
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query:''}).flexReload();
        populateGridButtons();
    }

    function populateGridButtons() {
        var status = $('#taskStatus').val();
        if (status == statusNewTask) {
            $('span.send').show();
            $('span.cancel').show();
            $('span.do_search').hide();
        }
        else if (status == statusSentToBank) {
            $('span.send').hide();
            $('span.cancel').hide();
            $('span.do_search').show();
        } else if (status == statusCancelledTask) {
            $('span.send').hide();
            $('span.cancel').hide();
            $('span.do_search').hide();
        }
    }

    //    window.onload = loadFlexGrid();
    function loadFlexGrid() {

        var strUrl = "${createLink(controller: 'exhTask', action: 'listExhTaskForAdmin')}" + paramForSearch;
        $("#flex1").flexOptions({url:strUrl});
        if (taskGridModel) {
            $("#flex1").flexAddData(taskGridModel);
            populateGridButtons();
        }
    }

</script>