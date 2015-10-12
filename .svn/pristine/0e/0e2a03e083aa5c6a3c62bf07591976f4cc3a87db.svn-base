<script type="text/javascript">
    var dropDownExchangeHouse, dropDownTaskList, dropDownPaymentMethod, currentStatus;
    var lstTaskModel = false;
    var dropDownProcessType, dropDownInstrumentType, dropDownBank, dropDownDistrict, dropDownBranch;
    var pbBankId, pbBranchId, pbDistrictId;
    var issueId, forwardId, purchaseId;

    $(document).ready(function () {
        onLoadMapTaskPage();
    });

    // method called on page load
    function onLoadMapTaskPage() {
        initializeForm($("#filterPanelMapTaskForm"), onSubmitMapTaskFilterPanel);
        initializeForm($("#mapTaskForm"), onSubmitMapTaskForm);
        currentStatus = $("#currentStatus").val();
        $("#isRevised").change(function () {
            populateExchangeHouse();
        });
        var model = ${modelJson? modelJson: null};
        if (model) {
            if (model.isError) {
                showError(model.message);
            } else {
                pbBankId = model.pbBankId;
                pbBranchId = model.pbBranchId;
                pbDistrictId = model.pbDistrictId;
                issueId = model.issueId;
                forwardId = model.forwardId;
                purchaseId = model.purchaseId;
            }
        }
        initFlex();
        $(document).attr('title', "ARMS - Map Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForMapTask");
    }

    // Populate exchange house list onClick of exchange house refresh button
    function populateExchangeHouse() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = false;
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = true;
        }
        $('#exhHouseId').attr('from_date', fromDate);
        $('#exhHouseId').attr('to_date', toDate);
        $('#exhHouseId').attr('is_revised', isRevised);
        $('#exhHouseId').reloadMe();
        dropDownTaskList.setDataSource(getKendoEmptyDataSource());
        dropDownTaskList.refresh();
    }

    // Method call onchange of exchange house dropdown
    function populateOnchangeExhHouse() {
        populateTaskList();
        if (dropDownExchangeHouse.value() == '') {
            $("#lblBalance").text('');
            return false;
        }
        $("#lblBalance").text("BDT " + dropDownExchangeHouse.dataItem().balance);
    }

    // Populate task list onchange of exchange house dropdown
    function populateTaskList() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = false;
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = true;
        }
        $('#taskListId').attr('from_date', fromDate);
        $('#taskListId').attr('to_date', toDate);
        $('#taskListId').attr('is_revised', isRevised);
        $('#taskListId').attr('exchange_house_id', exhHouseId);
        $('#taskListId').reloadMe();
    }

    // method called  on submit of the filter panel form
    function onSubmitMapTaskFilterPanel() {
        if (executePreConditionForSubmitMapTaskFilterPanel() == false) {
            return false;
        }
        var exhHouseId = dropDownExchangeHouse.value();
        var taskListId = dropDownTaskList.value();
        var paymentMethod = dropDownPaymentMethod.value();
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var isRevised = "";
        if ($("#isRevised").attr('checked') == 'checked') {
            isRevised = "on";
        }
        var params = "?exhHouseId=" + exhHouseId + "&taskListId=" + taskListId + "&paymentMethod=" + paymentMethod + "&fromDate=" + fromDate + "&toDate=" + toDate + "&currentStatus=" + currentStatus + "&isRevised=" + isRevised;
        var strUrl = "${createLink(controller: 'rmsTask', action: 'listTaskForMap')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $('#flex').flexOptions({url: strUrl, query: ''}).flexReload();
        return false;
    }
    // check pre condition before submitting the filter panel form
    function executePreConditionForSubmitMapTaskFilterPanel() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        if (dropDownExchangeHouse.value() == '') {
            showError('Please select exchange house.');
            return false;
        }
        if (dropDownTaskList.value() == '') {
            showError('Please select task list.');
            return false;
        }
        if (dropDownPaymentMethod.value() == '') {
            showError('Please select payment method.');
            return false;
        }
        return true;
    }


    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 40, sortable: false, align: "right", hide: true},
                        {display: "Ref No", name: "refNo", width: 120, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                        {display: "Value Date", name: "valueDate", width: 100, sortable: false, align: "left"},
                        {display: "Outlet", name: "outlet", width: 280, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        {name: 'Cancel Task', bclass: 'delete', onpress: addCancelNote},
                        {name: 'Revise', bclass: 'delete', onpress: addRevisionNote},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid}
                    ],
                    searchitems: [
                        {display: "Ref No", name: "refNo", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "refNo",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task for mapping',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 40,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    //select all rows of the grid
    function doSelectAll(com, grid) {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.addClass('trSelected');
            }
        } catch (e) {
        }
    }
    // deselect all rows of the grid
    function doDeselectAll() {
        try {
            var rows = $('table#flex > tbody > tr');
            if (rows && rows.length > 0) {
                rows.removeClass('trSelected');
            }
        } catch (e) {
        }
    }
    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            lstTaskModel = getEmptyGridModel();
        } else {
            lstTaskModel = data.gridObj;
        }
        $("#flex").flexAddData(lstTaskModel);
    }

    // Populate instrument onchange of process dropdown
    function populateInstrument() {
        if (dropDownProcessType.value() == '') {
            dropDownInstrumentType.setDataSource(getKendoEmptyDataSource(dropDownInstrumentType, null));
            return false;
        }
        resetBankDropDown();
        resetDistrictDropDown();
        resetBankBranchDropDown();
        var processTypeId = dropDownProcessType.dataItem().reservedId;
        $('#instrumentType').attr('process_type_id', processTypeId);
        $('#instrumentType').reloadMe();
    }

    function populateDistrict() {
        resetBankBranchDropDown();
        if (dropDownBank.value() == '') {
            resetDistrictDropDown();
            return false;
        }
        var bankId = dropDownBank.value();
        $('#mappingDistrict').attr('bank_id', bankId);
        $('#mappingDistrict').reloadMe();
    }

    // Populate branch onchange of district dropdown
    function populateBranch() {
        if (dropDownDistrict.value() == '') {
            dropDownBranch.setDataSource(getKendoEmptyDataSource(dropDownBranch, null));
            return false;
        }
        var bankId = dropDownBank.value();
        var districtId = dropDownDistrict.value();
        $('#mappingBranch').attr('bank_id', bankId);
        $('#mappingBranch').attr('district_id', districtId);
        $('#mappingBranch').reloadMe();
    }
    // check pre condition for onchange of district dropdown
    function executePreConditionForPopulateBranch() {
        if (dropDownBank.value() == '') {
            showError('Please select bank.');
            dropDownDistrict.value('');
            return false;
        }
        return true;
    }
    // method called  on submit of the mapTaskform
    function onSubmitMapTaskForm() {
        if (executePreConditionForSubmitMapTaskForm() == false) {
            return false;
        }
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var processType = dropDownProcessType.value();
        var instrumentType = dropDownInstrumentType.value();
        var bank = dropDownBank.value();
        var district = dropDownDistrict.value();
        var branch = dropDownBranch.value();
        var params = "?taskIds=" + taskIds + "&processType=" + processType + "&instrumentType=" + instrumentType + "&bank=" + bank + "&district=" + district + "&branch=" + branch + "&currentStatus=" + currentStatus;
        var actionUrl = "${createLink(controller: 'rmsTask', action: 'mapTask')}" + params;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        // fire ajax method for map task
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data) {
                executePostConditionForSubmitMapTaskForm(data);
            },
            complete: function () {
                showLoadingSpinner(false);      // stop loading spinner
            },
            dataType: 'json'
        });

        return false;
    }
    // check pre condition before submitting the mapTaskform
    function executePreConditionForSubmitMapTaskForm() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return false;
        }
        if (dropDownProcessType.value() == '') {
            showError('Please select process.');
            return false;
        }
        if (dropDownInstrumentType.value() == '') {
            showError('Please select instrument.');
            return false;
        }
        if (dropDownBank.value() == '') {
            showError('Please select bank.');
            return false;
        }
        if (dropDownDistrict.value() == '') {
            showError('Please select district.');
            return false;
        }
        if (dropDownBranch.value() == '') {
            showError('Please select branch.');
            return false;
        }
        return true;
    }
    // execute post condition after map task
    function executePostConditionForSubmitMapTaskForm(data) {
        if (data.isError == false) {
            var selectedRow = null;
            var selectedRowCount = $('#flex tr.trSelected').size();
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(selectedRowCount);
            showSuccess(data.message);
            lstTaskModel.total = parseInt(lstTaskModel.total) - selectedRowCount;
            removeEntityFromGridRows(lstTaskModel, selectedRow);
            resetMapTaskForm();
        } else {
            showError(data.message);
        }
    }

    // reset mapTaskForm
    function resetMapTaskForm() {
        dropDownProcessType.value('');
        dropDownInstrumentType.value('');
        dropDownInstrumentType.setDataSource(getKendoEmptyDataSource(dropDownInstrumentType, null));
        resetBankDropDown();
        resetDistrictDropDown();
        resetBankBranchDropDown();
    }

    function addRevisionNote() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        $('#reviseConfirmationModalTask').modal('show');    // show Modal
    }
    function executePreConditionForRevise() {
        $('#txtTaskReviseReason').val($.trim($('#txtTaskReviseReason').val()));
        if ($('#txtTaskReviseReason').val() == '') {
            showError('Revision note needed');
            return false;
        }
        return true;
    }
    function reviseTask() {
        if (executePreConditionForRevise() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var revisionNote = $('#txtTaskReviseReason').val();
        var params = "?taskIds=" + taskIds + "&revisionNote=" + revisionNote + "&currentStatus=" + currentStatus;
        var strUrl = "${createLink(controller:'rmsTask',action: 'reviseTask')}" + params;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForReviseTask,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        $('#txtTaskReviseReason').val('');
        $('#reviseConfirmationModalTask').modal('hide');
    }
    // execute post condition for revise task
    function executePostConditionForReviseTask(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(1);
            $("#lblBalance").text('');
            showSuccess(data.message);
            lstTaskModel.total = parseInt(lstTaskModel.total) - 1;
            removeEntityFromGridRows(lstTaskModel, selectedRow);
        } else {
            showError(data.message);
        }
    }
    // Hide modal
    function cancelReviseTask() {
        $('#txtTaskReviseReason').val('');
        $('#reviseConfirmationModalTask').modal('hide');
        return false;
    }


    //cancel task from grid
    function addCancelNote() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return;
        }
        $('#noteForCancelModalTask').modal('show');    // show Modal
    }
    function cancelTask() {
        if (executePreConditionForCancelTaskFromGrid() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var taskIds = getSelectedIdFromGrid($('#flex'));
        var exchangeHouseId = dropDownExchangeHouse.value();
        var revisionNote = $('#txtTaskCancelReason').val();
        var params = "?taskIds=" + taskIds + "&exchangeHouseId=" + exchangeHouseId + "&currentStatus=" + currentStatus + "&revisionNote=" + revisionNote;
        var strUrl = "${createLink(controller:'rmsTask',action: 'cancelRmsTask')}" + params;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForCancelTask,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        $('#txtTaskCancelReason').val('');
        $('#noteForCancelModalTask').modal('hide');
    }
    function executePreConditionForCancelTaskFromGrid() {
        $('#txtTaskCancelReason').val($.trim($('#txtTaskCancelReason').val()));
        if ($('#txtTaskCancelReason').val() == '') {
            showError('Cancellation note needed');
            return false;
        }
        return true;
    }
    function executePostConditionForCancelTask(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            lstTaskModel.total = parseInt(lstTaskModel.total) - 1;
            removeEntityFromGridRows(lstTaskModel, selectedRow);
        } else {
            showError(data.message);
        }
    }
    function closeCancelModal() {
        $('#txtTaskCancelReason').val('');
        $('#noteForCancelModalTask').modal('hide');
        return false;
    }

    function reloadGrid() {
        $('#flex').flexOptions({query: ''}).flexReload();
    }

    function onChangeInstrument() {
        var dropDownProcessValue = dropDownProcessType.value();
        var reservedProcessValue = dropDownProcessType.dataItem().reservedId;
        var reservedInstrumentValue = dropDownInstrumentType.dataItem().reservedId;

        resetBankDropDown();
        resetDistrictDropDown();
        resetBankBranchDropDown();

        $('#mappingBank').attr('process', reservedProcessValue);
        $('#mappingDistrict').attr('process', reservedProcessValue);
        $('#mappingBranch').attr('process', reservedProcessValue);

        if (dropDownProcessValue == issueId) {
            $('#mappingBank').attr('default_value', pbBankId);
            $('#mappingBank').reloadMe();

            $('#mappingDistrict').attr('default_value', pbDistrictId);
            $('#mappingDistrict').attr('bank_id', pbBankId);
            $('#mappingDistrict').reloadMe();

            $('#mappingBranch').attr('default_value', pbBranchId);
            $('#mappingBranch').attr('bank_id', pbBankId);
            $('#mappingBranch').attr('district_id', pbDistrictId);
            $('#mappingBranch').reloadMe();
        } else if (dropDownProcessValue == forwardId) {
            $('#mappingBank').attr('default_value', pbBankId);
            $('#mappingBank').reloadMe();

            $('#mappingDistrict').removeAttr('default_value');
            $('#mappingDistrict').attr('bank_id', pbBankId);
            $('#mappingDistrict').reloadMe();

        } else if (dropDownProcessValue == purchaseId) {
            $('#mappingBank').removeAttr('default_value');
            $('#mappingBank').attr('instrument', reservedInstrumentValue);
            $('#mappingBank').reloadMe();

            $('#mappingDistrict').removeAttr('default_value');
            $('#mappingDistrict').attr('instrument', reservedInstrumentValue);

            $('#mappingBranch').removeAttr('default_value');
            $('#mappingBranch').attr('instrument', reservedInstrumentValue);
        }
    }

    function resetBankDropDown() {
        dropDownBank.setDataSource(getKendoEmptyDataSource());
        dropDownBank.value('');
    }
    function resetBankBranchDropDown() {
        dropDownBranch.setDataSource(getKendoEmptyDataSource());
        dropDownBranch.value('');
    }
    function resetDistrictDropDown() {
        dropDownDistrict.setDataSource(getKendoEmptyDataSource());
        dropDownDistrict.value('');
    }

</script>