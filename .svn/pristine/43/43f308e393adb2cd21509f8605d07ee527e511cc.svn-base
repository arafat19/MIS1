<script type="text/javascript">
    var taskListModel, dropDownCurrency, dropDownPaymentMethod;
    var payMethodBankDepositId, payMethodCashCollectionId, currentDate;

    $(document).ready(function () {
        onLoadTask();
    });

    // method called on page load
    function onLoadTask() {
        payMethodBankDepositId = $('#hidBankDepositId').val();
        payMethodCashCollectionId = $('#hidCashCollectionId').val();

        initializeForm($("#rmsTaskBasicInfoForm"), validateBasicInfo);
        initializeForm($("#rmsTaskDisbursementInfoForm"), validateDisbursementInfo);
        initializeForm($("#rmsTaskAdditionalInfoForm"), validateAdditionalInfo);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);              // show error message in case of error
        } else {
            taskListModel = output.gridObj;        // set data in a global variable to populate
            currentDate = output.currentDate;
            $('#spanExh').text(output.exhName);
            $('#spanCountry').text(output.countryName);
        }

        $(".for-bank").hide();
        $(".for-cash").hide();
        $("#accountNo").attr("required", false);
        $("#pinNo").attr("required", false);
        $("#identityType").attr("required", false);

        $("#amount").kendoNumericTextBox({
            min: 1,
            decimals: 2,
            format: "0.00"
        });
        $("#amountInLocalCurrency").kendoNumericTextBox({
            min: 1,
            decimals: 2,
            format: "0.00"
        });
        numericAmountModel = $("#amount").data("kendoNumericTextBox");
        numericAmountInLocalCurrencyModel = $("#amountInLocalCurrency").data("kendoNumericTextBox");

        initFlex();
        populateFlex();
        $(document).attr('title', "ARMS - Create Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForExh");
    }

    // method call on change of PaymentMethod dropdown
    function onChangePayment() {
        clearDependentField();
        var paymentMethod = dropDownPaymentMethod.value();
        if (paymentMethod == payMethodBankDepositId) {
            $(".for-bank").show();
            $(".for-cash").hide();
            $("#accountNo").attr("required", true);
            $("#pinNo").attr("required", false);
            $("#identityType").attr("required", false);
        }
        else if (paymentMethod == payMethodCashCollectionId) {
            $(".for-bank").hide();
            $(".for-cash").show();
            $("#accountNo").attr("required", false);
            $("#pinNo").attr("required", true);
            $("#identityType").attr("required", true);
        }
        else {
            $(".for-bank").hide();
            $(".for-cash").hide();
            $("#accountNo").attr("required", false);
            $("#pinNo").attr("required", false);
            $("#identityType").attr("required", false);
        }
    }
    function clearDependentField() {
        $('#accountNo').val('');
        $('#pinNo').val('');
        $('#identityType').val('');
        $('#identityNo').val('');
    }
    // method called  on submit of the form
    function onSubmitTask() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);          // disable the create button
        showLoadingSpinner(true);                       // show loading spinner
        var formData = $('#rmsTaskBasicInfoForm').serializeArray();
        formData = formData.concat($('#rmsTaskDisbursementInfoForm').serializeArray());
        formData = formData.concat($('#rmsTaskAdditionalInfoForm').serializeArray());
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'rmsTask', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'rmsTask', action: 'update')}";
        }
        // fire ajax method for create or update
        jQuery.ajax({
            type: 'post',
            data: formData,             // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);         // enable the create button
                showLoadingSpinner(false);                      // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }
    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateBasicInfo()) {
            return false;
        }
        if (!validateDisbursementInfo()) {
            return false;
        }
        if (!validateAdditionalInfo()) {
            return false;
        }
        var retDateFrom = getDate($("#valueDate"), "value date");
        if (retDateFrom > new Date()) {
            showError('Value-Date can not be future date');
            return false;
        }
        return true;
    }

    function validateBasicInfo() {
        if (!validateForm($("#rmsTaskBasicInfoForm"))) {
            $('#taskTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }
        return true;
    }
    function validateDisbursementInfo() {
        if (!validateForm($("#rmsTaskDisbursementInfoForm"))) {
            $('#taskTabs a[href="#fragmentDisbursementInfo"]').tab('show');
            return false;
        }
        return true;
    }
    function validateAdditionalInfo() {
        if (!validateForm($("#rmsTaskAdditionalInfoForm"))) {
            $('#taskTabs a[href="#fragmentAdditionalInfo"]').tab('show');
            return false;
        }
        return true;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);              // show error message in case of error
            showLoadingSpinner(false);              // stop loading spinner
        }
        else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {      // show newly created object in a grid row

                    var previousTotal = parseInt(taskListModel.total);
                    var firstSerial = 1;

                    if (taskListModel.rows.length > 0) {
                        firstSerial = taskListModel.rows[0].cell[0];
                        regenerateSerial($(taskListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    taskListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        taskListModel.rows.pop();
                    }

                    taskListModel.total = ++previousTotal;
                    $("#flex").flexAddData(taskListModel);

                } else if (newEntry.entity != null) {           // updated existing object data in the grid
                    updateListModel(taskListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(taskListModel);
                }

                clearTaskForm();                        // reset the form
                $('#taskTabs a[href="#fragmentBasicInfo"]').tab('show');
                showSuccess(result.message);        // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }
    // clear the form
    function clearTaskForm() {
        clearForm($("#rmsTaskBasicInfoForm"), $('#refNo'));  // clear errors & form values
        clearForm($("#rmsTaskDisbursementInfoForm"), $('#beneficiaryName'));  // clear errors & form values
        onChangePayment();
        clearForm($("#rmsTaskAdditionalInfoForm"), $('#beneficiaryAddress'));  // clear errors & form values
        $('#taskTabs a[href="#fragmentBasicInfo"]').tab('show');
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");       // reset create button text
        $("#valueDate").val(currentDate);
        $("#isExhUser").val("true");
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
                        {display: "Ref No", name: "ref_no", width: 120, sortable: true, align: "left"},
                        {display: "Amount", name: "amount", width: 100, sortable: false, align: "right"},
                        {display: "Value Date", name: "value_date", width: 80, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiary_name", width: 100, sortable: false, align: "left"},
                        {display: "Outlet", name: "outlet", width: 200, sortable: false, align: "left"},
                        {display: "Payment Method", name: "payment_method", width: 110, sortable: false, align: "left"},
                        {display: "Created On", name: "created_on", width: 80, sortable: false, align: "left"},
                        {display: "Exchange House", name: "exchange_house", width: 150, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        <app:ifAllUrl urls="/rmsTask/select,/rmsTask/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectRmsTask},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsTask/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteRmsTask},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsTask/sendRmsTaskToBank">
                        {name: 'Send To Bank', bclass: 'send', onpress: sendRmsTaskToBank},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Ref No", name: "ref_no", width: 180, sortable: true, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiary_name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "ref_no",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: false,
                    title: 'All Task',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
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
            taskListModel = getEmptyGridModel();
        } else {
            taskListModel = data;
        }
        $("#flex").flexAddData(taskListModel);
    }
    // select rmsTask object for update
    function selectRmsTask(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'task', true) == false) {
            return;
        }
        clearTaskForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'rmsTask', action: 'select')}?id=" + id + "&isExhUser=true",
            success: executePostConditionForEdit,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showRmsTask(data);
        }
    }
    // show property of selected rmsTask object on UI
    function showRmsTask(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#refNo').val(entity.refNo);
        $('#valueDate').val(data.valueDate);
        $('#countryId').val(entity.countryId);
        $('#beneficiaryName').val(entity.beneficiaryName);
        numericAmountModel.value(entity.amount);
        $('#outletBank').val(entity.outletBank);
        $('#outletBranch').val(entity.outletBranch);
        $('#outletDistrict').val(entity.outletDistrict);
        dropDownCurrency.value(entity.localCurrencyId);
        numericAmountInLocalCurrencyModel.value(entity.amountInLocalCurrency);
        dropDownPaymentMethod.value(entity.paymentMethod);
        onChangePayment();
        $('#accountNo').val(entity.accountNo);
        $('#pinNo').val(entity.pinNo);
        $('#identityType').val(entity.identityType);
        $('#identityNo').val(entity.identityNo);
        $('#beneficiaryAddress').val(entity.beneficiaryAddress);
        $('#beneficiaryPhone').val(entity.beneficiaryPhone);
        $('#senderName').val(entity.senderName);
        $('#senderMobile').val(entity.senderMobile);

        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }
    // delete rmsTask object
    function deleteRmsTask(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var rmsTaskId = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'rmsTask', action:  'delete')}?id=" + rmsTaskId + "&isExhUser=true",
            success: executePostConditionForDelete,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task', true) == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected task?')) {
            return false;
        }
        return true;
    }
    // removing selected row and clean input form
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            clearTaskForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            taskListModel.total = parseInt(taskListModel.total) - 1;
            removeEntityFromGridRows(taskListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }
    // send rmsTask to bank
    function sendRmsTaskToBank(com, grid) {
        if (executePreConditionForSend() == false) {
            return;
        }
        showLoadingSpinner(true);
        var rmsTaskIds = getSelectedIdFromGrid($('#flex'));
        var selectedIds = $('.trSelected', grid);
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'rmsTask', action:  'sendRmsTaskToBank')}?taskIds=" + rmsTaskIds,
            success: function (result) {
                executePostConditionForSend(result, selectedIds);
            },
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForSend() {
        if (executeCommonPreConditionForSelect($('#flex'), 'task') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to send the selected task to bank?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForSend(data, ids) {
        if (data.isError == false) {
            $(ids).each(function (e) {
                $(this).remove();
                removeEntityFromGridRows(taskListModel, $(this));
            });
            $('#flex').decreaseCount(ids.length);
            taskListModel.total = parseInt(taskListModel.total) - ids.length;
            console.log(taskListModel);
            showSuccess(data.message);
            clearTaskForm();
        } else {
            showError(data.message);
        }
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }
    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'rmsTask',action:  'list')}?isExhUser=true";
        $("#flex").flexOptions({url: strUrl});

        if (taskListModel) {
            $("#flex").flexAddData(taskListModel);
        }
    }

</script>