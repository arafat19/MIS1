<script type="text/javascript">
    var output = ${modelJson};
    var taskModel, beneficiaryModel, bankDepositId, cashCollectionId, onlinePaymentId, systemCurrency, bdtCurrency;

    $(document).ready(function () {
        onLoadReplaceTask();
    });

    function onLoadReplaceTask(){
        initializeForm($('#replaceTaskForm'), onSubmitReplaceTaskForm);
        if (output.isError) {
            showError(output.message);
            return false;
        }
        taskModel = output.exhTask;
        beneficiaryModel = output.exhBeneficiary;
        bankDepositId = $('#hiddenBankDeposit').val();
        cashCollectionId = $('#hiddenCashCollection').val();
        onlinePaymentId = $('#hiddenOnlinePaidBy').val();
        systemCurrency = $('#hiddenSystemCurrencyId').val();
        bdtCurrency = output.bdtCurrency;
        populateTaskDetails();
        $('#grandTotalLink').click(function () {
            refreshRegularFeeAndCommission();
        });
        loadNumberedMenu(MENU_ID_SARB, "#sarbTaskModel/showForReplaceTask");
    }

    function onSubmitReplaceTaskForm() {
        if (executePreForReplace() == false) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = "${createLink(controller: 'sarbTaskModel', action: 'updateTaskForReplaceTask')}";
        var formData = $("#replaceTaskForm").serializeArray();

        var selectedCurrency = dropDownCurrency.value();
        var toCurrencyId = 0;
        if (selectedCurrency == systemCurrency) {
            toCurrencyId = bdtCurrency;
        } else {
            toCurrencyId = systemCurrency;
        }

        formData.push({name: 'toCurrencyId', value: toCurrencyId});

        $.ajax({
            type:'post',
            data: formData,
            url: actionUrl,
            success:function(data, textStatus) {
                executePostForReplace(data);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown) {
            },
            complete:function(XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                // Spinner Hide on AJAX Call
                onCompleteAjaxCall();
            },
            dataType:'json'
        });
        return false;
    }

    function executePostForReplace(data) {
        if(data.isError) {
            showError(data.message);
            return;
        }
        showSuccess(data.message);
    }

    function executePreForReplace() {
        if (!validateForm($('#replaceTaskForm'))) {
            return false;
        }
        if(!isValidAmountInfo()) {
            return false;
        }
        return true;
    }

    function isValidAmountInfo() {
        if (!checkAmountFields()) {
            return false;
        }

        var strGBPAmount = $('#hidAmountInLocalCurrency').val();
        if ((isNaN(strGBPAmount)) || (isEmpty(strGBPAmount)) || strGBPAmount <= 0) {
            showError('Please Calculate the Fees & Charges');
            return false;
        }
        var actualVal, hiddenVal;

        actualVal = $('#fromAmount').val();
        hiddenVal = $('#hidFromAmount').val();
        if (actualVal != hiddenVal) {
            showError('Change detected in From Amount field, press calculate to update');
            $('#fromAmount').focus();
            return false;
        }

        actualVal = $('#conversionRate').val();
        hiddenVal = $('#hidConversionRate').val();
        if (actualVal != hiddenVal) {
            showError('Change detected in conversion rate field, press calculate to update');
            $('#conversionRate').focus();
            return false;
        }

        actualVal = $('#discount').val();
        hiddenVal = $('#hidDiscount').val();
        if (actualVal != hiddenVal) {
            showError('Change detected in discount field, press calculate to update');
            $('#discount').focus();
            return false;
        }

        return true;
    }

    function populateTaskDetails() {
        $('#id').val(taskModel.id);
        $('#lblCustomerName').text(output.customerName);
        $('#lblBeneficiaryName').text(output.beneficiaryName);
        dropDownRemittancePurpose.value(taskModel.remittancePurpose);
        if (taskModel.outletDistrictId && taskModel.outletBranchId) {
            $('#isOtherBank').attr('checked', 'checked');
            $('#isOtherBank').change();
            populateOtherBankInfo();
        } else{
            $('#isOtherBank').attr('checked', false);
            $('#isOtherBank').change();
        }
        dropDownPaymentMethod.value(taskModel.paymentMethod);
        dropDownPaymentMethod.trigger('change');

        $('#hidAmountInForeignCurrency').val(taskModel.amountInForeignCurrency);
        $('#hidAmountInLocalCurrency').val(taskModel.amountInLocalCurrency);
        $('#lblAmountInForeignCurrency').text(parseFloat(taskModel.amountInForeignCurrency).toFixed(2));
        $('#lblAmountInLocalCurrency').text(parseFloat(taskModel.amountInLocalCurrency).toFixed(2));
        dropDownCurrency.value(taskModel.fromCurrencyId);
        dropDownCurrency.trigger("change");
        if (taskModel.fromCurrencyId == systemCurrency) {
            $('#fromAmount').val(taskModel.amountInLocalCurrency);
        } else {
            $('#fromAmount').val(taskModel.amountInForeignCurrency);
        }
        dropDownPaidBy.value(taskModel.paidBy);
        dropDownPaidBy.trigger("change");
        $('#conversionRate').val(taskModel.conversionRate);
        $('#hidConversionRate').val(taskModel.conversionRate);
        $('#lblRegularFee').text(taskModel.regularFee);

        var regularFee = taskModel.regularFee ? parseFloat(taskModel.regularFee).toFixed(2) : 0.00;
        $('#lblRegularFee').text(regularFee);
        refreshGrandTotalAmount();
    }

    function updatePaymentMethod() {
        disableAccountInfo();
        disablePinInfo();
        var paymentMethod = dropDownPaymentMethod.value();
        if(paymentMethod == bankDepositId) {
            enableAccountInfo();
        }
        else if(paymentMethod == cashCollectionId) {
            enablePinInfo();
        }
    }

    function displayOtherBankPanel() {
        if ($('#isOtherBank').attr('checked') == 'checked') {
            enableOtherBankRequired();
        }
        else {
            disableOtherBankRequired();
        }
    }

    function disableAccountInfo() {
        $('#accountInfo').hide();
        $('#accountNumber').val('');
        $('#bankName').val('');
        $('#bankBranchName').val('');
        $('#districtName').val('');

        $('#accountNumber').attr('required', false);
        $('#bankName').attr('required', false);
        $('#bankBranchName').attr('required', false);
        $('#districtName').attr('required', false);
    }

    function enableAccountInfo() {
        $('#accountInfo').show();
        $('#accountNumber').val(beneficiaryModel.accountNo);
        $('#bankName').val(beneficiaryModel.bank);
        $('#bankBranchName').val(beneficiaryModel.bankBranch);
        $('#districtName').val(beneficiaryModel.district);

        $('#accountNumber').attr('required', true);
        $('#bankName').attr('required', true);
        $('#bankBranchName').attr('required', true);
        $('#districtName').attr('required', true);
    }

    function disablePinInfo() {
        $('#pinInfo').hide();
        $('#identityType').val('');
        $('#identityNo').val('');
        $('#identityType').attr('required', false);
    }

    function enablePinInfo() {
        $('#pinInfo').show();
        $('#identityType').val(beneficiaryModel.photoIdType);
        $('#identityNo').val(beneficiaryModel.photoIdNo);
        $('#identityType').attr('required', true);
    }

    function enableOtherBankRequired() {
        $('#outletDistrictId').attr("required", true);
        $('#outletBankId').attr("required", true);
        $('#outletBranchId').attr("required", true);
        dropDownBank.enable(true);
        dropDownDistrict.enable(true);
        dropDownBankBranch.enable(true);
        $('#lblBank').addClass('label-required');
        $('#lblBankBranch').addClass('label-required');
        $('#lblDistrict').addClass('label-required');
        populateOtherBankInfo();
    }

    function disableOtherBankRequired() {
        $('#outletDistrictId').attr("required", true);
        $('#outletBankId').attr("required", true);
        $('#outletBranchId').attr("required", true);
        dropDownBank.enable(false);
        dropDownDistrict.enable(false);
        dropDownBankBranch.enable(false);
        dropDownBank.value('');
        dropDownDistrict.setDataSource(getKendoEmptyDataSource());
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource());
        $('#lblBank').removeClass('label-required');
        $('#lblBankBranch').removeClass('label-required');
        $('#lblDistrict').removeClass('label-required');
    }

    function showPaidByNo(paidByNo) {
        var paidBy = dropDownPaidBy.value();
        $('#paidByNo').attr("required",false);
        $('#lblPaidByNo').removeClass("label-required");
        $('#paidByNo').val('');
        $("#paidByNo").prop("disabled", true).addClass("k-state-disabled");

        if (paidBy == onlinePaymentId) {
            $('#paidByNo').attr("required",true);
            $('#lblPaidByNo').addClass("label-required");
            $('#paidByNo').val(taskModel.paidByNo);
            $("#paidByNo").prop("disabled", false).removeClass("k-state-disabled");
        }
    }

    function setConversionRate() {
        var rate = dropDownCurrency.dataItem().rate;
        $('#conversionRate').val(rate);
    }

    function populateOtherBankInfo() {
        dropDownBank.value(taskModel.outletBankId);
        $('#outletDistrictId').attr('bank_id', taskModel.outletBankId);
        $('#outletDistrictId').attr('default_value', taskModel.outletDistrictId);
        $('#outletDistrictId').reloadMe();

        $('#outletBranchId').attr('bank_id', taskModel.outletBankId);
        $('#outletBranchId').attr('district_id', taskModel.outletDistrictId);
        $('#outletBranchId').attr('default_value', taskModel.outletBranchId);
        $('#outletBranchId').reloadMe();
    }

    function onChangeBank() {
        dropDownDistrict.setDataSource(getKendoEmptyDataSource(dropDownDistrict,null));
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch,null));
        if(dropDownBank.value()==''){
            return;
        }
        var bankId= dropDownBank.value();
        $('#outletDistrictId').attr('bank_id',bankId);
        $('#outletDistrictId').reloadMe();
    }
    //update bank branch drop down
    function onChangeDistrict() {
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch,null));
        if(dropDownDistrict.value()==''){
            return;
        }
        var bankId= dropDownBank.value();
        var districtId= dropDownDistrict.value();
        $('#outletBranchId').attr('bank_id',bankId);
        $('#outletBranchId').attr('district_id',districtId);
        $('#outletBranchId').reloadMe();
    }

    function refreshGrandTotalAmount() {

        // check the validation of amount fields
        if (!checkAmountFields()) {
            return false;
        }

        var rate, bdtAmount = 0, localAmount = 0, regularFee;

        // populate unconditional amount fields
        rate = parseFloat($('#conversionRate').val());
        regularFee = parseFloat($('#lblRegularFee').text());

        //  check which amount selected as from amount
        var fromCurrency = $('#fromCurrencyId').val();

        if (fromCurrency == systemCurrency) {  // i.e. GBP, USD etc. selected
            // check rate variation with setting
            var systemCurrencyToBdtRate = dropDownCurrency.dataItem().rate;
            if (rate > systemCurrencyToBdtRate) {
                showError('Conversion rate can not be higher than ' + systemCurrencyToBdtRate);
                return false;
            }
            localAmount = parseFloat($("#fromAmount").val());
            bdtAmount = localAmount * rate;
        } else if (fromCurrency == bdtCurrency) {
            // check rate variation with setting
            var bdtCurrencyToSystemRate = dropDownCurrency.dataItem().rate;
            if (rate < bdtCurrencyToSystemRate) {
                showError('Conversion rate can not be lower than ' + bdtCurrencyToSystemRate);
                return false;
            }
            bdtAmount = parseFloat($("#fromAmount").val());
            localAmount = bdtAmount * rate;
        } else {
            showError('Currency not supported');
            return false;
        }

        var total = localAmount + regularFee;

        var grandTotal = total;
        // trim amounts upto 2 decimal
        bdtAmount = bdtAmount.toFixed(2);
        //rate = rate.toFixed(8);
        regularFee = regularFee.toFixed(2);

        localAmount = localAmount.toFixed(2);
        total = total.toFixed(2);
        grandTotal = grandTotal.toFixed(2);

        // populate hidden fields
        if (bdtAmount > 0) {
            $('#hidAmountInForeignCurrency').val(bdtAmount);
        } else {
            $('#hidAmountInForeignCurrency').val('');
        }
        $('#hidConversionRate').val(rate);
        $('#hidFromAmount').val($("#fromAmount").val());
        if (localAmount > 0) {
            $('#hidAmountInLocalCurrency').val(localAmount);
        } else {
            $('#hidAmountInLocalCurrency').val('');
        }

        // populate formatted text fields
        $('#conversionRate').val(rate);
        $('#regularFee').val(regularFee);

        // populate labels
        $('#lblAmountInForeignCurrency').text(bdtAmount);
        $('#lblAmountInLocalCurrency').text(localAmount);
        $('#lblTotal').text(total);
        $('#lblGrandTotal').text(grandTotal);
        return true;
    }

    function checkArmsAmountExceptZero(value) {
        return (/^[0-9]\d{0,7}(\.\d{1,2})?$/.test(value));
    }

    function checkAmountFields() {

        var success = true;


        if (!checkConversionRate()) {
            success = false;
        }

        if (!checkSpecificCustomField('#fromAmount')) {
            success = false;
        }
        return success;
    }
    function checkConversionRateAmount(value) {
        return (/^[0-9]\d{0,7}(\.\d{1,8})?$/.test(value) && (parseFloat(value) > 0));
    }
    function checkConversionRate() {
        var conversionRate = $.trim($('#conversionRate').val());
        $('#conversionRate').val(conversionRate);
        if (!checkConversionRateAmount(conversionRate)) {
            showError('Conversion rate is invalid amount');
            return false;
        }
        return true;
    }

    function checkSpecificCustomField(control) {
        var errorLabel, success = true;

        $(control).val($.trim($(control).val()));

        if ($(control).val().length == 0) {
            errorLabel = 'Required';
            showError("From amount is required");
            success = false;
        } else if (!checkExchangeHouseAmount($(control).val())) {
            errorLabel = 'Invalid Amount';
            showError("From amount is invalid");
            success = false;
        }

        return success;
    }
    function checkExchangeHouseAmount(value) {
        return (/^[0-9]\d{0,7}(\.\d{1,2})?$/.test(value) && (parseFloat(value) > 0));
    }

    function refreshRegularFeeAndCommission() {
        // check the validation of amount fields
        if (!checkAmountFields()) {
            return false;
        }
        var currency = dropDownCurrency.value();
        var amount = parseFloat($("#fromAmount").val());
        var rate = parseFloat($("#conversionRate").val());
        showLoadingSpinner(true);
        $.ajax({
            type: 'post',
            dataType: 'json',
            url: "${createLink(controller: 'exhTask',action: 'calculateFeesAndCommission')}?amount=" + amount + "&currency=" + currency + "&rate=" + rate,
            success: function (data, textStatus) {
                setRegularFeeAndCommission(data);
                refreshGrandTotalAmount();
            },
            complete: function (XMLHttpRequest, textStatus) {
                $('span.send').show();
                onCompleteAjaxCall();
            }
        });
        return false;
    }
    function setRegularFeeAndCommission(entity) {
        $('#lblRegularFee').text(parseFloat(entity.regularFee).toFixed(2));
    }

    function goBack() {
        var loc = "${createLink(controller: 'sarbTaskModel', action: 'showForReplaceTask')}";
        $.history.load(formatLink(loc));
        return false;
    }
</script>