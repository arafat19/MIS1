<script type="text/javascript">
    var jsonBeneficiaryListModel, jsonBeneficiaryList, strBeneficiaryGridUrl;

    $(document).ready(function (e){
        onLoadBeneficiary();
    });

    function onLoadBeneficiary() {
       // initializeForm($('#beneficiaryForm'),onSubmitBeneficiary);
        initializeForm($('#basicInfoForm'),validateBasicInfo);
        initializeForm($('#disbursementInfoForm'),validateDisbursementInfo);

        jsonBeneficiaryListModel = ${modelJson};
        jsonBeneficiaryList = jsonBeneficiaryListModel.beneficiaryListJSON;
        var customerId = jsonBeneficiaryListModel.customerId;
        var customerName = jsonBeneficiaryListModel.customerName;
        $('#customerId').val(customerId);
        $('#customerName').val(customerName);
        strBeneficiaryGridUrl = "${createLink(controller: 'exhBeneficiary', action: 'listForCustomer')}?isApproved=false";
        initBeneficiaryGrid();

        loadFlexGrid();

        // update page title
        $(document).attr('title', "ARMS - Create Beneficiary");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhBeneficiary/showNewForCustomer");
    }


    function onSubmitBeneficiary() {

        if (executePreCondition() == false) {
            return false;
        }
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhBeneficiary', action: 'createForCustomer')}";
        } else {
            actionUrl = "${createLink(controller: 'exhBeneficiary', action: 'updateForCustomer')}";
        }
        setButtonDisabled($('.save'), true);
        var formData = $('#basicInfoForm').serializeArray();
        formData = formData.concat($('#disbursementInfoForm').serializeArray());
        jQuery.ajax({
            type: 'post',
            data: formData,
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('.save'), false);
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(jsonBeneficiaryList.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (jsonBeneficiaryList.rows.length > 0) {
                        firstSerial = jsonBeneficiaryList.rows[0].cell[0];
                        regenerateSerial($(jsonBeneficiaryList.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    jsonBeneficiaryList.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        jsonBeneficiaryList.rows.pop();
                    }

                    jsonBeneficiaryList.total = ++previousTotal;
                    $("#flex1").flexAddData(jsonBeneficiaryList);

                } else if (result.entity != null) { // updated existing
                    updateListModel(jsonBeneficiaryList, result.entity, 0);
                    $("#flex1").flexAddData(jsonBeneficiaryList);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
            }
        }
    }

    function executePreCondition() {

        if (!validateBasicInfo()) {
            return false;
        }
        return true;
    }

    function validateBasicInfo() {
        if (!validateForm($("#basicInfoForm"))) {
            $('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }
        if (isEmpty($('#customerId').val())) {
	        $('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
            showError('Please select a customer');
            return false;
        }

        if (isEmpty($('#firstName').val())) {
	        $('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }
        return true;
    }
    function validateDisbursementInfo(){
        trimFormValues($("#disbursementInfoForm"));
        return true;
    }

    function initBeneficiaryGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 100, sortable: true, align: "right", hide: true},
                        {display: "Full Name", name: "firstName", width: 200, sortable: true, align: "left"},
                        {display: "Bank Info", name: "bankInfo", width: 280, sortable: false, align: "left"},
                        {display: "A/C No", name: "accountNo", width: 140, sortable: true, align: "left"},
                        {display: "Photo ID Type", name: "photoIdType", width: 110, sortable: false, align: "left"},
                        {display: "Updated By", name: "updatedBy", width: 110, sortable: false, align: "left"},
                        {display: "Updated On", name: "updatedOn", width: 110, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editBeneficiary},
                        {name: 'Create Task', bclass: 'rename', onpress: doCreateTask},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Full Name", name: "full_name", width: 180, sortable: true, align: "left"},
                        {display: "Account No", name: "account_no", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "firstName",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Beneficiaries',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getGridHeight()-15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    }
                }
        );
    }

    function resetForm() {
        clearForm($("#basicInfoForm"));
        clearForm($("#disbursementInfoForm"));

        setButtonDisabled($('.save'), false);
        $('#customerId').val(jsonBeneficiaryListModel.customerId);
        $('#customerName').val(jsonBeneficiaryListModel.customerName);
	    $('#beneficiaryTabs a[href="#fragmentBasicInfo"]').tab('show');
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function editBeneficiary(com, grid) {
       if(executeCommonPreConditionForSelect($('#flex1'),'beneficiary')==false){
           return;
       }

        resetForm();
        showLoadingSpinner(true);
        var beneficiaryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'exhBeneficiary', action: 'edit')}?id=" + beneficiaryId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall,    // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function doCreateTask(com, grid) {
        if(executeCommonPreConditionForSelect($('#flex1'),'beneficiary')==false){
            return;
        }
        showLoadingSpinner(true);
        var beneficiaryId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'exhTask', action: 'showForCustomer')}?beneficiaryId=" + beneficiaryId + "&customerId=" + jsonBeneficiaryListModel.customerId;
        $.history.load(formatLink(loc));
        return false;
    }


    function executePostConditionForEdit(data) {
        if (data.entity == null) {
            showError(result.message);
        } else {
            populateCustomer(data);
        }
    }

    function populateCustomer(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        $('#customerId').val(jsonBeneficiaryListModel.customerId);
        $('#customerName').val(jsonBeneficiaryListModel.customerName);
        $('#firstName').val(entity.firstName);
        $('#middleName').val(entity.middleName);
        $('#lastName').val(entity.lastName);
        $('#relation').val(entity.relation);
        $('#accountNo').val(entity.accountNo);
        $('#phone').val(entity.phone);
        $('#email').val(entity.email);
        $('#address').val(entity.address);
        $('#bank').val(entity.bank);
        $('#bankBranch').val(entity.bankBranch);
        $('#district').val(entity.district);
        $('#thana').val(entity.thana);
        $('#photoIdType').val(entity.photoIdType);
        $('#photoIdNo').val(entity.photoIdNo);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function loadFlexGrid() {
        $("#flex1").flexOptions({url: strBeneficiaryGridUrl});
        if (jsonBeneficiaryList) {
            $("#flex1").flexAddData(jsonBeneficiaryList);
        }
    }
</script>