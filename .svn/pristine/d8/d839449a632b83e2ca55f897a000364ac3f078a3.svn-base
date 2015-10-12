<%@ page import="com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf" %>
<script type="text/javascript">
    var customerListModel;
    var modelJsonForCustomer;
    var loggedInUser;
    var companyCountryId;
    var isSanctionStatus;
    var dropDownPhotoIdType;
    var dropDownCountry, dropDownGender;
    $(document).ready(function () {
        modelJsonForCustomer = ${modelJson};

        initializeForm($('#basicInfoForm'), isValidBasicInfo);
        initializeForm($('#additionalInfoForm'), isValidAdditionalInfo);
        initializeForm($('#identityInfoForm'), isValidIdentityInfo);

        onLoadCreateCustomer();

        $('#addressVerifiedBy').click(function () {
            showVerifierName();
        });

        $('#isSanctionException').click(function () {
            showSanctionVerifierName();
        });

        $('#isCorporate').click(function (e) {
            return checkCorporatePanel();
        });

    });

    function onSubmitCustomer() {
        if (executePreCondition() == false) {
            return false;
        }
        // Spinner Show on AJAX Call

        showLoadingSpinner(true);
        var formData = $('#basicInfoForm').serializeArray();
        formData = formData.concat($('#additionalInfoForm').serializeArray());
        formData = formData.concat($('#identityInfoForm').serializeArray());
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhCustomer', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhCustomer', action: 'update')}";
        }
        //disable button before success the work
        setButtonDisabled($('.save'), true);

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
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function showSanctionVerifierName() {
        if ($('#isSanctionException').attr("checked")) {
            if (getSanctionCountForCustomer() == false) {
                $('#isSanctionException').attr("checked", false);
            }
        } else {
            $('#sanctionVerifierNameDisplay').text('');
        }
    }

    function checkCorporatePanel(regNo, dateIncorp) {
        $('#lblCompanyRegNo').removeClass('label-required');
        $('#lblDateOfIncorporation').removeClass('label-required');
        $('#lblCompanyRegNo').addClass('label-optional');
        $('#lblDateOfIncorporation').addClass('label-optional');
        $('#companyRegNo').attr('required', false);
        $('#companyRegNo').attr('disabled', true);
        $('#dateOfIncorporation').attr('required', false);
        $("#dateOfIncorporation").data("kendoDatePicker").enable(false);
        $('#companyRegNo').val('');
        $('#dateOfIncorporation').val('');
        var checked = $('#isCorporate').attr('checked');
        if (checked) {
            $('#lblCompanyRegNo').addClass('label-required');
            $('#lblDateOfIncorporation').addClass('label-required');
            $('#companyRegNo').attr('required', true);
            $('#companyRegNo').attr('disabled', false);
            $('#dateOfIncorporation').attr('required', true);
            $("#dateOfIncorporation").data("kendoDatePicker").enable(true);
        }
        if (regNo) $('#companyRegNo').val(regNo);
        if (dateIncorp) $("#dateOfIncorporation").val(dateIncorp);
    }

    function checkVisaExpire(expDate) {
        var countryId = $('#countryId').val();
        $('#lblVisaExpireDate').removeClass('label-required');
        $("#visaExpireDate").data("kendoDatePicker").enable(true);
        $('#visaExpireDate').val('');

        if (countryId == companyCountryId) {
            $("#visaExpireDate").data("kendoDatePicker").enable(false);
            $("#visaExpireDate").attr("required", false);
            $('#lblVisaExpireDate').removeClass('label-required');
            $('#lblVisaExpireDate').addClass('label-optional');
        } else {
            $('#lblVisaExpireDate').removeClass('label-optional');
            $('#lblVisaExpireDate').addClass('label-required');
            $("#visaExpireDate").attr("required", true);
        }
        if (expDate) $('#visaExpireDate').val(expDate);    // used in edit
    }

    function showVerifierName() {
        if ($('#addressVerifiedBy').attr("checked")) {
            $('#verifierNameDisplay').text("By " + loggedInUser.username);
        } else {
            $('#verifierNameDisplay').text('');
        }
    }

    function executePreConditionForSanctionCount() {
        // trim field vales before process.
        trimFormValues($("#basicInfoForm"));

        if ($('#name').val().isEmpty()) {
            showError("Please enter customer name");
            $('#name').focus();
            return false;
        }

        return true;
    }

    function getSanctionCountForCustomer() {

        if (executePreConditionForSanctionCount() == false) {
            return false;
        }

        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var name = $('#name').val();

        var actionUrl = "${createLink(controller: 'exhSanction', action: 'sanctionCountFromCustomer')}?name=" + name;

        setButtonDisabled($('.save'), true);
        jQuery.ajax({
            type: 'post',
            data: jQuery("#customerForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForForSanctionCount(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
                setButtonDisabled($('.save'), false);
            },
            dataType: 'json'
        });

        return true;
    }

    function executePostConditionForForSanctionCount(data) {
        var verificationMsg = '  by ' + loggedInUser.username + "<span style='color: green;'>" + ' ( No match found )' + "</span>";

        if (data.isError == false) {
            // create link to sanction list
            var customerName = $('#name').val();
            $('#hidName').val(customerName);

            var loc = '/exhSanction/showFromCustomer?customerName=' + customerName;
            loc = encodeURI(loc);
            if (data.count > 0) {
                verificationMsg = '  by ' + loggedInUser.username + '<a target="_blank" style="color: red;" title="Click to view details" href=' + loc + '> ( ' + data.count + ' match(es) found )</a>';
            }
        } else {
            verificationMsg = "<span style='color: rgba(255,255,255,0);'>  " + data.message + " </span>";
        }
        $('#sanctionVerifierNameDisplay').html(verificationMsg);
    }

    function onLoadCreateCustomer() {

        try {   // Model will be supplied to grid on load _list.gsp
            var isError = modelJsonForCustomer.isError;
            if (isError == true) {
                var message = modelJsonForCustomer.message;
                showError(message);
                return;
            }
            customerListModel = modelJsonForCustomer.customerListJSON ? modelJsonForCustomer.customerListJSON : false;
        } catch (e) {
            customerListModel = false;
        }

        loggedInUser = modelJsonForCustomer.loggedInUser;
        companyCountryId = modelJsonForCustomer.companyCountryId;
        phoneNoPattern = modelJsonForCustomer.phoneNoPattern;
        isSanctionStatus = modelJsonForCustomer.isSanctionStatus;

        $('#agentId').val(modelJsonForCustomer.agentId);
        $('#isdCode').text(modelJsonForCustomer.isdCode);

        $('#companyRegNo').attr('disabled', 'disabled');
        //$("#dateOfIncorporation").data("kendoDatePicker").enable(false);
        $('#smsSubscription').attr('checked', 'checked');

        $('#mailSubscription').attr('checked', 'checked');

        // update page title
        $(document).attr('title', "ARMS - Create Customer");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhCustomer/show");
    }

    function executePreCondition() {
        if (!isValidBasicInfo()) {
            return false;
        }

        if (!isValidAdditionalInfo()) {
            return false;
        }

        if (!isValidIdentityInfo()) {
            return false;
        }

        return true;
    }

    function isValidBasicInfo() {
        if (!validateForm($("#basicInfoForm"))) {
            $('#customerTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }

        if (checkDOB($('#customerDateOfBirth')) == false) {
            $('#customerTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }

        if ((isSanctionStatus == true) && (!$('#isSanctionException').attr("checked"))) {
            showError("Please confirm the customer as verified");
            $('#customerTabs a:first').tab('show');
            return false;
        }

        if ((($('#name').val() != $('#hidName').val())) && (isSanctionStatus == true)) {
            $('#isSanctionException').attr("checked", false);
            $('#sanctionVerifierNameDisplay').text('');
            showError("Change detected in customer name. Please verify again!");
            $('#customerTabs a[href="#fragmentBasicInfo"]').tab('show');
            return false;
        }

        return true;
    }

    function validatePhoneNo() {
        var phoneNo = $.trim($('#phone').val());
        var pattern = decodeURIComponent(phoneNoPattern);
        var phoneRegex = new RegExp(pattern);
        if (!phoneRegex.test(phoneNo)) {
            showError('Invalid phone number');
            return false;
        }
        return true;
    }

    function isValidAdditionalInfo() {
        if (!validateForm($("#additionalInfoForm"))) {
            $('#customerTabs a[href="#fragmentAdditionalInfo"]').tab('show');
            return false;
        }
        if (!validatePhoneNo()) return false;

        <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="0">
        if ((isEmpty($('#declarationStart').val())) && (isEmpty($('#declarationEnd').val()))) {
            return true;
        }
        if (checkDeclarationDates($('#declarationStart'), $('#declarationEnd')) == false) return false;
        </exh:checkSysConfig>

        <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_DECLARATION_AMOUNT}" value="1">
        if (checkDeclarationDates($('#declarationStart'), $('#declarationEnd')) == false) return false;
        </exh:checkSysConfig>
        return true;
    }

    function isValidIdentityInfo() {
        if (!validateForm($("#identityInfoForm"))) {
            $('#customerTabs a[href="#fragmentIdentityInfo"]').tab('show');
            return false;
        }

        var countryId = $('#countryId').val();
        if (countryId != companyCountryId) {
            if ($.trim($('#visaExpireDate').val()).length == 0) {
                $('#customerTabs a[href="#fragmentIdentityInfo"]').tab('show');
                return false;
            }
        }

        var checked = $('#isCorporate').attr('checked');
        if (checked) {
            if (isEmpty($('#companyRegNo').val()) || isEmpty($('#dateOfIncorporation').val().isEmpty())) {
                $('#customerTabs a[href="#fragmentIdentityInfo"]').tab('show');
                return false;
            }
        }
        return true
    }

    function checkDOB() {
        var dateOfBirth = null;
        if ($.trim($('#customerDateOfBirth').val()) != '') {
            dateOfBirth = getDate($('#customerDateOfBirth'), "Date");
        }
        if ((dateOfBirth == false) || (dateOfBirth == null)) {
            dateOfBirth = "";
            return false;
        }
        if (dateOfBirth > new Date()) {
            showError('Date of birth can not be future date');
            return false;
        }
        return true;
    }

    function checkDeclarationDates(control_start, control_end) {
        if (($.trim(control_start.val()).length != 10) || ($.trim(control_end.val()).length != 10)) {
            showError("Please enter Declaration Start and End");
            $('#customerTabs a[href="#fragmentAdditionalInfo"]').tab('show');
            return false;
        }

        var retDateFrom = getDate(control_start, "Declaration Start");
        var retDateTo = getDate(control_end, "Declaration End");
        if (retDateFrom == false || retDateTo == false || retDateFrom == undefined || retDateTo == undefined) {
            return false;
        }
        if (retDateFrom > new Date()) {
            showError('Declaration Start can not be future date');
            return false;
        }

        if (retDateFrom && retDateTo && retDateFrom > retDateTo) {
            showError('Declaration Start can not be greater than Declaration End');
            return false;
        }
        return true;
    }


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(customerListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (customerListModel.rows.length > 0) {
                        firstSerial = customerListModel.rows[0].cell[0];
                        regenerateSerial($(customerListModel.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;

                    customerListModel.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        customerListModel.rows.pop();
                    }

                    customerListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(customerListModel);
                    $('#customerTabs a[href="#fragmentBasicInfo"]').tab('show');

                } else if (result.entity != null) { // updated existing
                    updateListModel(customerListModel, result.entity, 0);
                    $("#flex1").flexAddData(customerListModel);
                    $('#customerTabs a[href="#fragmentBasicInfo"]').tab('show');
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // @todo-Azam remove this alert before production
            }
            // @todo-Azam to add entity in grid without round trip, following line commented out
            //$("#flex1").flexReload();
        }
    }

    function resetForm() {
        $('#customerTabs a:first').tab('show') // Select first tab
        clearForm($("#basicInfoForm"), $('#name'));
        clearForm($("#additionalInfoForm"));
        clearForm($("#identityInfoForm"));

        $('#verifierNameDisplay').text('');
        $('#sanctionVerifierNameDisplay').text('');
        $('#smsSubscription').attr('checked', true);
        $('#mailSubscription').attr('checked', true);
        $('#companyRegNo').attr('disabled', 'disabled');
        $("#dateOfIncorporation").data("kendoDatePicker").enable(false);
        $("#visaExpireDate").data("kendoDatePicker").enable(true);
        $('#lblVisaExpireDate').removeClass('label-optional');
        $('#lblVisaExpireDate').addClass('label-required');
        $("#visaExpireDate").attr("required", true);
        $('#lblCompanyRegNo').removeClass('label-required');
        $('#lblDateOfIncorporation').removeClass('label-required');
        $('#companyRegNo').removeClass('required');
        $('#dateOfIncorporation').removeClass('required');

        setButtonDisabled($('.save'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
        var actionUrl = "${createLink(controller: 'exhCustomer',action: 'create')}";
        $("#customerForm").attr('action', actionUrl);
        $('#agentId').val(modelJsonForCustomer.agentId);
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "Customer A/C No", name: "code", width: 100, sortable: true, align: "left"},
                    {display: "Full Name", name: "fullName", width: 190, sortable: true, align: "left"},
                    {display: "Date Of Birth", name: "dateOfBirth", width: 190, sortable: false, align: "left"},
                    {display: "Photo ID Type", name: "photoIdTypeId", width: 150, sortable: false, align: "left"},
                    {display: "Photo ID No", name: "photoIdNo", width: 120, sortable: false, align: "left"},
                    {display: "Phone No", name: "phone", width: 120, sortable: false, align: "left"}
                ],
                buttons: [
                    {name: 'Edit', bclass: 'edit', onpress: editCustomer},
                    {name: 'Beneficiary', bclass: 'rename', onpress: viewBeneficiary},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Customer A/C No", name: "code", width: 100, sortable: true, align: "left"},
                    {display: "Full Name", name: "fullName", width: 180, sortable: true, align: "left"},
                    {display: "Photo Id No", name: "photo_id_no", width: 120, sortable: true, align: "left"},
                    {display: "Phone No", name: "phone", width: 120, sortable: true, align: "left"}
                ],
                sortname: "name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Customers',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                //width: 725,
                height: getGridHeight() - 15,
                customPopulate: customPopulateGrid,
                afterAjax: function () {
                    showLoadingSpinner(false);// Spinner hide after AJAX Call
                }
            }
    );

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            customerListModel = data ? data : getEmptyGridModel();
        }
        $("#flex1").flexAddData(customerListModel);
    }

    function editCustomer(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'customer') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var customerId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'exhCustomer', action: 'edit')}?id=" + customerId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
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
        $('#name').val(entity.name);
        $('#surname').val(entity.surname);
        $('#hidName').val(entity.name);
        dropDownPhotoIdType.value(entity.photoIdTypeId);
        dropDownCountry.value(entity.countryId);
        dropDownGender.value(entity.genderId);
        $('#photoIdNo').val(entity.photoIdNo);
        $('#phone').val(entity.phone ? entity.phone : '');
        $('#customerDateOfBirth').val(data.customerDateOfBirth);
        $('#customerPhotoIdExpiryDate').val(data.photoIdExpiryDate);
        $('#email').val(entity.email);
        $('#address').val(entity.address);
        $('#postCode').val(entity.postCode);
        $('#profession').val(entity.profession);
        $('#sourceOfFund').val(entity.sourceOfFund);
        $('#declarationAmount').val(entity.declarationAmount);
        $('#declarationStart').val(data.declarationStart);
        $('#declarationEnd').val(data.declarationEnd);
        $('#agentId').val(entity.agentId ? entity.agentId : '');
        $('#isSanctionException').attr('checked', false);
        $('#smsSubscription').attr('checked', false);
        $('#mailSubscription').attr('checked', false);
        if (data.addressVerifiedStatus == "true") {
            $('#addressVerifiedBy').attr('checked', 'checked')
        } else {
            $('#addressVerifiedBy').removeAttr("checked");
        }

        if (entity.smsSubscription) {
            $('#smsSubscription').attr('checked', 'checked');
        }
        if (entity.mailSubscription) {
            $('#mailSubscription').attr('checked', 'checked');
        }

        checkVisaExpire(data.visaExpireDate);
        if (entity.companyRegNo && entity.dateOfIncorporation) {
            $('#isCorporate').attr('checked', 'checked');
        }
        checkCorporatePanel(entity.companyRegNo, entity.dateOfIncorporation);

        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
        var actionUrl = "${createLink(controller: 'exhCustomer',action: 'update')}";
        $("#customerForm").attr('action', actionUrl);
    }

    function viewBeneficiary(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForViewBeneficiary(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var customerId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var loc = "${createLink(controller: 'exhBeneficiary', action: 'show')}?customerId=" + customerId;
        $.history.load(formatLink(loc));
        return false;
    }

    function executePreConditionForViewBeneficiary(ids) {
        if (ids.length == 0) {
            showError("Please select a customer to view beneficiary");
            return false;
        }
        return true;
    }


    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }


    window.onload = populateCustomerGridOnPageLoad();

    function populateCustomerGridOnPageLoad() {
        var strUrl = "${createLink(controller: 'exhCustomer', action: 'listForAgent')}";
        $("#flex1").flexOptions({url: strUrl});

        if (customerListModel) {
            $("#flex1").flexAddData(customerListModel);
        }

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
    // update page title

    $(document).attr('title', "ARMS - Show Customer User");
    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhCustomer/showForAgent");
</script>