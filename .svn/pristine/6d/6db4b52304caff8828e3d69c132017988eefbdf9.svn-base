<script type="text/javascript">

	var taskGridModel, bankDepositId, cashCollectionId, onlinePaymentId, regularFee;
	var beneficiaryObject, currentDate;
	var systemCurrency, bdtCurrency, systemCurrencyName;
	var modelJsonForCreateTask = ${modelJson};
	var dropDownPaymentMethod, dropDownRemittancePurpose;
	var dropDownBank, dropDownBankBranch, dropDownDistrict;
	var dropDownRate, dropDownCurrency, dropDownPaidBy;


	$(document).ready(function () {
		onLoadCreateTask();
	});

    function initVariables() {
        taskGridModel = null;
        beneficiaryObject = null;
        currentDate = null;
    }

	function onLoadCreateTask() {

		if (modelJsonForCreateTask.isError) {
			showError(modelJsonForCreateTask.message);
			return false;
		}

		initializeForm($('#basicInfoForm'), isValidBasicInfo);
		initializeForm($('#disbursementInfoForm'), isValidDisbursementInfo);
		initializeForm($('#amountInfoForm'), isValidAmountInfo);

		initVariables();

		$('#popNote').popover();
		$("#popNote").focusout(function () {
			$('#popNote').popover('hide');
		});
		$('#grandTotalLink').click(function () {
			refreshRegularFeeAndCommission();
		});

		systemCurrency = modelJsonForCreateTask.systemCurrency;
		systemCurrencyName = modelJsonForCreateTask.systemCurrencyName ? modelJsonForCreateTask.systemCurrencyName : '';
		bdtCurrency = modelJsonForCreateTask.bdtCurrency;
		$('#localCurrencyLabel').text(systemCurrencyName + ' Amount:');

		var systemCurrencyToBdtRate = modelJsonForCreateTask.initialRate;
		$('#conversionRate').val(systemCurrencyToBdtRate);

		regularFee = parseFloat(modelJsonForCreateTask.regularFee);
		$('#regularFee').text(regularFee.toFixed(2));
		beneficiaryObject = new Object();
		resetBeneficiaryObject(beneficiaryObject);

		bankDepositId = modelJsonForCreateTask.bankDepositId;
		cashCollectionId = modelJsonForCreateTask.cashCollectionId;
		onlinePaymentId = modelJsonForCreateTask.onlinePaymentId;
		taskGridModel = modelJsonForCreateTask.taskListJSON ? modelJsonForCreateTask.taskListJSON : false;
		initTaskGrid();
		loadFlexGrid();

		$("#create").html("<span class='k-icon k-i-plus'></span>Update");
		$(document).attr('title', "ARMS - Create Task");
		loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showAgentTaskForCashier");
	}

	function resetBeneficiaryObject(beneficiaryObject) {
		beneficiaryObject.beneficiaryId = '';
		beneficiaryObject.beneficiaryName = '';
		beneficiaryObject.customerId = '';
		beneficiaryObject.customerName = '';
		beneficiaryObject.photoIdType = '';
		beneficiaryObject.photoIdNo = '';

		beneficiaryObject.accountNo = '';
		beneficiaryObject.bank = '';
		beneficiaryObject.bankBranch = '';
		beneficiaryObject.district = '';
	}

	function isValidBasicInfo() {

		if (validateForm($('#basicInfoForm')) == false) {
			$('a[href=#fragmentBasicInfo]').tab('show');
			return false;
		}

		if (isEmpty($('#customerId').val()) ||
				isEmpty($('#customerName').val()) ||
				isEmpty($('#beneficiaryId').val()) ||
				isEmpty($('#beneficiaryName').val())) {
			$('a[href=#fragmentBasicInfo]').tab('show');
			showError('Please select customer and beneficiary');
			return false;
		}
		return true;
	}


	function isValidDisbursementInfo() {
		if (validateForm($('#disbursementInfoForm')) == false) {
			$('a[href=#fragmentDisbursementInfo]').tab('show');
			return false;
		}
		return true;
	}

	function isValidAmountInfo() {
		if (validateForm($('#amountInfoForm')) == false) {
			$('a[href=#fragmentAmountInfo]').tab('show');
			return false;
		}

		if (!checkAmountFields()) {
			$('a[href=#fragmentAmountInfo]').tab('show');
			return false;
		}

		// Check if hidden fields are empty & switch tab
		var strGBPAmount = $('#hidAmountInLocalCurrency').val();
		if ((isNaN(strGBPAmount)) || (isEmpty(strGBPAmount)) || strGBPAmount <= 0) {
			$('a[href=#fragmentAmountInfo]').tab('show');
			showError('Please Calculate the Fees & Charges');
			return false;
		}

		// now check all 4 hiden field VS UI fields respectively
		var actualVal, hiddenVal;

		actualVal = $('#fromAmount').val();
		hiddenVal = $('#hidFromAmount').val();
		if (actualVal != hiddenVal) {
			showError('Change detected in From Amount field, press calculate to update');
			$('a[href=#fragmentAmountInfo]').tab('show');
			$('#fromAmount').focus();
			return false;
		}

		actualVal = $('#conversionRate').val();
		hiddenVal = $('#hidConversionRate').val();
		if (actualVal != hiddenVal) {
			showError('Change detected in conversion rate field, press calculate to update');
			$('a[href=#fragmentAmountInfo]').tab('show');
			$('#conversionRate').focus();
			return false;
		}

		actualVal = $('#discount').val();
		hiddenVal = $('#hidDiscount').val();
		if (actualVal != hiddenVal) {
			showError('Change detected in discount field, press calculate to update');
			$('a[href=#fragmentAmountInfo]').tab('show');
			$('#discount').focus();
			return false;
		}

		return true;
	}


	function executePreConditionForFormSubmit() {
		if (!isValidBasicInfo()) {
			return false;
		}
		if (!isValidDisbursementInfo()) {
			return false;
		}

		if (!isValidAmountInfo()) {
			return false;
		}
		return true;
	}


    function onSubmitTask(isConfirmed) {
	    if ((isConfirmed == null) || (isConfirmed == undefined)) {
		    isConfirmed = false;
	    }
	    if (executePreConditionForFormSubmit() == false) {
		    return false;
	    }
	    trimFormValues($("#basicInfoForm"));
	    trimFormValues($("#disbursementInfoForm"));
	    trimFormValues($("#amountInfoForm"));

        showLoadingSpinner(true);	// Spinner Show on AJAX Call

	    var formData = $('#basicInfoForm').serializeArray();
	    formData = formData.concat($('#disbursementInfoForm').serializeArray());
	    formData = formData.concat($('#amountInfoForm').serializeArray());

	    var selectedCurrency = dropDownCurrency.value();

        var toCurrencyId = 0;
        if (selectedCurrency == systemCurrency) {
            toCurrencyId = bdtCurrency;
        } else {
            toCurrencyId = systemCurrency;
        }

        formData.push({name: 'toCurrencyId', value: toCurrencyId});  // fromCurrencyId already collected from control
        formData.push({name: 'isConfirmed', value: isConfirmed});

        setButtonDisabled($('.save'), true);

        jQuery.ajax(
                {
                    type: 'post',
                    data: formData,
                    url: "${createLink(controller: 'exhTask', action: 'update')}",
                    success: function (data, textStatus) {
                        executePostCondition(data);
                    },
                    error: function (data, XMLHttpRequest, textStatus, errorThrown) {
                    },
                    complete: function (XMLHttpRequest, textStatus) {
                        setButtonDisabled($('.save'), false);
                        onCompleteAjaxCall();
                    },
                    dataType: 'json'
                });

        return false;
    }


	function disableAccountInfo() {
		$('#accountInfo').hide();

		$('#accountNumber').val('');
		$('#bankName').val('');
		$('#bankBranchName').val('');
		$('#districtName').val('');

	}

	function enableAccountInfo(beneficiaryInstance) {
		$('#accountInfo').show();
		if (beneficiaryInstance != null) {
			$('#accountNumber').val(beneficiaryInstance.accountNo ? beneficiaryInstance.accountNo : '');
			$('#bankName').val(beneficiaryInstance.bank ? beneficiaryInstance.bank : '');
			$('#bankBranchName').val(beneficiaryInstance.bankBranch ? beneficiaryInstance.bankBranch : '');
			$('#districtName').val(beneficiaryInstance.district ? beneficiaryInstance.district : '');
		}
	}

	function disablePinBranchInfo() {
		$('#pinBranchInfo').hide();

		$('#pinBankName').val('');
		$('#pinBankBranchName').val('');
		$('#pinDistrictName').val('');
	}

	function enablePinBranchInfo(beneficiaryInstance) {
		$('#pinBranchInfo').show();
		if (beneficiaryInstance != null) {
			$('#pinBankName').val('SOUTHEAST BANK LTD.');
			$('#pinBankBranchName').val('ANY BRANCH');
			$('#pinDistrictName').val('ANY DISTRICT');
		}
	}

	function disablePinInfo() {
		$('#pinInfo').hide();

		$('#lblPinNo').text('(Auto Generated)');
		$('#identityType').val('');
		$('#identityNo').val('');
	}

	function enablePinInfo(beneficiaryInstance) {
		$('#pinInfo').show();
		if (beneficiaryInstance != null) {
			$('#identityType').val(beneficiaryInstance.photoIdType ? beneficiaryInstance.photoIdType : '');
			$('#identityNo').val(beneficiaryInstance.photoIdNo ? beneficiaryInstance.photoIdNo : '');
		}
	}

	function disableMobileInfo() {
		$('#mobileInfo').hide();

		$('#districtNameMobile').val('');
		$('#thanaNameMobile').val('');
		$('#mobileNo').val('');
	}

	function enableMobileInfo(beneficiaryInstance) {
		$('#mobileInfo').show();
		if (beneficiaryInstance != null) {
			$('#districtNameMobile').val(beneficiaryInstance.district ? beneficiaryInstance.district : '');
			$('#thanaNameMobile').val(beneficiaryInstance.thana ? beneficiaryInstance.thana : '');
			$('#mobileNo').val(beneficiaryInstance.phone ? beneficiaryInstance.phone : '');
		}
	}

	function clearReadOnlyFields() {
		// Hidden fields
		$('#customerId').val('');
		$('#customerName').val('');
		$('#beneficiaryId').val('');
		$('#beneficiaryName').val('');
		// Labels
		$('#lblCustomerId').text('None');
		$('#lblCustomerName').text('None');
		$('#lblBeneficiaryId').text('None');
		$('#lblBeneficiaryName').text('None');
	}

	function populateReadOnlyFields(beneficiaryInstance) {
		if (beneficiaryInstance == null) return false;
		// Hidden fields
		$('#customerId').val(beneficiaryInstance.customerId);
		$('#customerName').val(beneficiaryInstance.customerName);
		$('#beneficiaryId').val(beneficiaryInstance.beneficiaryId);
		$('#beneficiaryName').val(beneficiaryInstance.beneficiaryName);
		// Labels
		$('#lblCustomerId').text(beneficiaryInstance.customerCode);
		$('#lblCustomerName').text(beneficiaryInstance.customerName);
		$('#lblBeneficiaryId').text(beneficiaryInstance.beneficiaryId);
		$('#lblBeneficiaryName').text(beneficiaryInstance.beneficiaryName);
	}

	function clearFormTask() {
		setButtonDisabled($('.save'), false);

		clearForm($('#basicInfoForm'));
		clearForm($('#disbursementInfoForm'));
		clearForm($('#amountInfoForm'));

		clearReadOnlyFields();

		dropDownPaymentMethod.value('');
		dropDownPaidBy.value('');
		dropDownPaymentMethod.trigger("change");
		dropDownPaidBy.trigger("change");

		$('#lblRefNo').text('(Auto Generated)');

		resetBeneficiaryObject(beneficiaryObject);
		resetOtherBankInfo();
		resetCustomerSummary();

		$('#lblAmountInForeignCurrency').text('0.00');
		$('#lblAmountInLocalCurrency').text('0.00');
		$('#conversionRate').val('');
		$('#fromAmount').val('');
		$('#fromCurrencyId').val(systemCurrency);

		$('#hidAmountInForeignCurrency').val('');
		$('#hidAmountInLocalCurrency').val('');
		$('#hidConversionRate').val('');
		$('#hidDiscount').val('0.00');

		$('#discount').val('');
		$('#lblCommission').text('0.00');
		$('#lblRegularFee').text('0.00');
		$('#lblTotal').text('0.00');
		$('#lblGrandTotal').text('0.00');
        $('#lblCommission').text('0.00');

		$('a[href=#fragmentBasicInfo]').tab('show');
		$("#create").html("<span class='k-icon k-i-plus'></span>Update");
	}

	function resetCustomerSummary() {
		$('#lblSum3Months').text('0.00');
		$('#lblSum6Months').text('0.00');
		$('#lblSum12Months').text('0.00');
		$('#popNote').attr('data-content', 'N/A');
		$('#popNote').attr('data-original-title', '');
	}

	function populateCustomerSummary(data) {
		$('#lblSum3Months').text(data.sumOfThreeMoths);
		$('#lblSum6Months').text(data.sumOfSixMonths);
		$('#lblSum12Months').text(data.sumOfOneYear);
		$('#popNote').attr('data-content', data.note);
		//$('#popNote').attr('data-original-title',data.strNoteCreatedBy);
	}

	function resetOtherBankInfo() {
		/*dropDownDistrict.value('');
		 dropDownBank.setDataSource(getKendoEmptyDataSource());
		 dropDownBankBranch.setDataSource(getKendoEmptyDataSource());
		 dropDownBank.value('');
		 dropDownBankBranch.value('');*/
		$('#isOtherBank').attr('checked', false);
		$('#isOtherBank').change();
	}

	function refreshGrandTotalAmount() {

		// check the validation of amount fields
		if (!checkAmountFields()) {
			return false;
		}

		var rate, discount, bdtAmount = 0, localAmount = 0, regularFee;

		// populate unconditional amount fields
		rate = parseFloat($('#conversionRate').val());
		regularFee = parseFloat($('#lblRegularFee').text());
		discount = parseFloat($('#discount').val());

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

		if (discount > regularFee) {
			showError('Discount can not be more than regular fee');
			$('#discount').focus();
			return false;
		}

		discount = discount.toFixed(2);
		$('#hidDiscount').val(discount);
		var grandTotal = total - discount;
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
		$('#discount').val(discount);

		// populate labels
		$('#lblAmountInForeignCurrency').text(bdtAmount);
		$('#lblAmountInLocalCurrency').text(localAmount);
		$('#lblTotal').text(total);
		$('#lblGrandTotal').text(grandTotal);
		return true;
	}

    function isEmpty(val) {
        var val2;
        val2 = $.trim(val);
        return (val2.length == 0);
    }

	function executePostCondition(result) {
		if (result.isError == true) {
			if (result.isConfirmationIssues && result.lstErrors != null) {
				$("#taskCreateConfirmationErrorList").html('');
				// populate divConsiderationIssues and show
				var confirmationErrorList = result.lstErrors;
				var confirmationErrors = null;
				var counter = 1;
				for (var i = 0; i < confirmationErrorList.length; i++) {
					if (confirmationErrors) {
						confirmationErrors = confirmationErrors + "<br />" + counter + ") " + confirmationErrorList[i];
					} else {
						confirmationErrors = counter + ") " + confirmationErrorList[i];
					}
					counter = counter + 1;
				}
				var totalAmount = result.totalAmount;
				confirmationErrors = confirmationErrors + "<br />" + "<br />" + 'Note: Total transaction of the customer will be ' + systemCurrencyName + ' ' + totalAmount;
				$("#taskCreateConfirmationErrorList").html(confirmationErrors);
				$('#btnConfirmCreateTask').html('Update Task');
				$('#btnExitConfirmCreateTask').html('Don\'t Update Task');
				$('#taskCreateConfirmationDialog').modal('show');

			} else {
				showError(result.message);
			}
		} else if (result.isError == false) {
			try {
				if (result.entity != null) { // updated existing
					updateListModel(taskGridModel, result.entity, 0);
					$("#flex1").flexAddData(taskGridModel);
				}
				showSuccess(result.message);
				clearFormTask();
			} catch (e) {
				showError('Error occurred to populate grid');
				clearFormTask();
			}
		} else {
			showError('Failed to update task');
		}
	}

	function displayOtherBankPanel() {
		if ($('#isOtherBank').attr('checked') == 'checked') {
			$('#otherBankInfo').show();
			enableOtherBankRequired();
		} else {
			$('#otherBankInfo').hide();
			disableOtherBankRequired();
            dropDownDistrict.setDataSource(getKendoEmptyDataSource());
            dropDownBankBranch.setDataSource(getKendoEmptyDataSource());
            dropDownBank.value('');
            dropDownDistrict.value('');
            dropDownBankBranch.value('');
		}
	}

    //update district drop down
    function onChangeBank() {
        dropDownDistrict.setDataSource(getKendoEmptyDataSource(dropDownDistrict,null));
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch,null));
        if(dropDownBank.value()==''){
            return;
        }
        var bankId= dropDownBank.value();
        $('#outletDistrictId').attr('bank_id',bankId);
        $('#outletDistrictId').attr('default_value','');
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
        $('#outletBranchId').attr('default_value','');
        $('#outletBranchId').reloadMe();
    }

	function enableBankDepositRequired() {
		$('#accountNumber').attr("required", true);
		$('#bankName').attr("required", true);
		$('#bankBranchName').attr("required", true);
		$('#districtName').attr("required", true);
	}

	function enableCashCollectionRequired() {
		$('#identityType').attr("required", true);
	}

	function enableOtherBankRequired() {
		$('#outletDistrictId').attr("required", true);
		$('#outletBankId').attr("required", true);
		$('#outletBranchId').attr("required", true);
	}

	function disableBankDepositRequired() {
		$('#accountNumber').attr("required", false);
		$('#bankName').attr("required", false);
		$('#bankBranchName').attr("required", false);
		$('#districtName').attr("required", false);
	}

	function disableCashCollectionRequired() {
		$('#identityType').attr("required", false);
	}

	function disableOtherBankRequired() {
		$('#outletDistrictId').attr("required", false);
		$('#outletBankId').attr("required", false);
		$('#outletBranchId').attr("required", false);
	}

	function updatePaymentMethod(paymentMethod, beneficiaryInstance) {
		disableAccountInfo();
		disablePinBranchInfo();
		disablePinInfo();
		disableMobileInfo();

		if ((paymentMethod == undefined) || (paymentMethod == null)) {
			paymentMethod = dropDownPaymentMethod.value();
			beneficiaryInstance = beneficiaryObject;
		}
		if (paymentMethod == bankDepositId) {
			disableCashCollectionRequired();
			enableBankDepositRequired();
		}
		else if (paymentMethod == cashCollectionId) {
			disableBankDepositRequired();
			enableCashCollectionRequired();
		}

		showBeneficiaryData(paymentMethod, beneficiaryInstance);
	}

	function showBeneficiaryData(paymentMethod, beneficiaryInstance) {
		var paymentMethodId = parseInt(paymentMethod);
		switch (paymentMethodId) {
			case bankDepositId:
				enableAccountInfo(beneficiaryInstance);
				break;
			case cashCollectionId:
				enablePinInfo(beneficiaryInstance);
				break;
		}

	}

    function initTaskGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Ref No", name: "refNo", width: 100, sortable: true, align: "right"},
                        {display: "Amount(BDT)", name: "amountInForeignCurrency", width: 90, sortable: true, align: "right"},
                        {display: "Amount(" + systemCurrencyName + ")", name: "amountInLocalCurrency", width: 90, sortable: true, align: "right"},
                        {display: "Total Due", name: "total_due", width: 90, sortable: false, align: "right"},
                        {display: "Customer Name", name: "customerName", width: 150, sortable: true, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: true, align: "left"},
                        {display: "Payment Method", name: "paymentMethod", width: 100, sortable: true, align: "left"},
                        {display: "Regular Fee", name: "regularFee", width: 80, sortable: true, align: "right"},
                        {display: "Discount", name: "discount", width: 80, sortable: true, align: "right"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: doEdit},
                        {name: 'Invoice', bclass: 'rename', onpress: viewInvoice},
                        {name: 'Note', bclass: 'note', onpress: viewTaskNote},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Ref No", name: "refNo", width: 100, sortable: true, align: "left"},
                        {display: "Customer Name", name: "customerName", width: 180, sortable: true, align: "left"},
                        {display: "Amount(BDT)", name: "amountInForeignCurrency", width: 120, sortable: true, align: "left"}
                    ],
                    sortname: "refNo",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Tasks',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    //width: 725,
                    height: getGridHeight() - 15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    }
                }
        );
    }

	function doEdit(com, grid) {
		if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var taskId=getSelectedIdFromGrid($('#flex1'));
		showLoadingSpinner(true);
		$.ajax({
			url: "${createLink(controller: 'exhTask', action: 'edit')}?id="
					+ taskId,
			success: populateTaskFields,
			complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
			dataType: 'json',
			type: 'post'
		});
		return true;
	}
    function populateTaskFields(data){

        if (data.isError == true) {
            showError(data.message);
            return false;
        }
        clearFormTask();

        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);

        $('#refNo').val(entity.refNo);
        $('#lblRefNo').text(entity.refNo);
        // Hidden fields
        $('#customerId').val(entity.customerId);
        $('#lblCustomerId').text(entity.companyId.toString() + entity.customerId.toString());
        $('#customerName').val(entity.customerName);
        $('#lblCustomerName').text(entity.customerName);

        $('#beneficiaryId').val(entity.beneficiaryId);
        $('#lblBeneficiaryId').text(entity.beneficiaryId);
        $('#beneficiaryName').val(entity.beneficiaryName);
        $('#lblBeneficiaryName').text(entity.beneficiaryName);

        // form fields
        dropDownRemittancePurpose.value(entity.remittancePurpose);
        beneficiaryObject = data.beneficiaryInstance;
        dropDownPaymentMethod.value(entity.paymentMethod);
        dropDownPaymentMethod.trigger("change");

        // other bank info
        if (entity.outletDistrictId && entity.outletBranchId) {
            $('#isOtherBank').attr('checked', 'checked');
            $('#isOtherBank').change();

            dropDownBank.value(entity.outletBankId);
            $('#outletDistrictId').attr('bank_id',  entity.outletBankId);
            $('#outletDistrictId').attr('default_value',  entity.outletDistrictId);
            $('#outletDistrictId').reloadMe();
            $('#outletBranchId').attr('bank_id', entity.outletBankId);
            $('#outletBranchId').attr('district_id', entity.outletDistrictId);
            $('#outletBranchId').attr('default_value', entity.outletBranchId);
            $('#outletBranchId').reloadMe();
        }

        $('#lblPinNo').text(entity.pinNo);

        //$('#amountInForeignCurrency').val(entity.amountInForeignCurrency);
        $('#hidAmountInForeignCurrency').val(entity.amountInForeignCurrency);
        $('#hidAmountInLocalCurrency').val(entity.amountInLocalCurrency);
        $('#lblAmountInForeignCurrency').text(entity.amountInForeignCurrency);
        $('#lblAmountInLocalCurrency').text(entity.amountInLocalCurrency);

        $('#lblCommission').text(parseFloat(entity.commission).toFixed(2));

        dropDownCurrency.value(entity.fromCurrencyId);
        dropDownCurrency.trigger("change");
        if (entity.fromCurrencyId == systemCurrency) {
            $('#fromAmount').val(entity.amountInLocalCurrency);
        } else {
            $('#fromAmount').val(entity.amountInForeignCurrency);
        }

        $('#conversionRate').val(entity.conversionRate);
        $('#hidConversionRate').val(entity.conversionRate);


        dropDownPaidBy.value(entity.paidBy);

        showPaidByNo(entity.paidByNo);

        $('#lblRegularFee').text(entity.regularFee);

        var discount = entity.discount ? parseFloat(entity.discount).toFixed(2) : 0.00;

        $('#discount').val(discount);
        $('#hidDiscount').val(discount);

        var regularFee = entity.regularFee ? parseFloat(entity.regularFee).toFixed(2) : 0.00;
        $('#lblRegularFee').text(regularFee);

        refreshGrandTotalAmount();
        populateCustomerSummary(data);
        $('a[href=#fragmentBasicInfo]').tab('show');
    }
	function executePreConditionForSentToBank(selectedIds) {
		if (selectedIds.length == 0) {
			showError("Please select a Task to send");
			return false;
		}

		if (!confirm('Are you sure you want to send ' + selectedIds.length + ' task(s) to Bank?')) {
			return false;
		}
		return true;

	}

    function executePostConditionForSentToBank(selectedIds) {
        selectedIds.each(function (e) {
            $(this).remove();
        });
        $('#flex1').decreaseCount(selectedIds.length);
    }

    function viewInvoice(com, grid) {
        if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var taskId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhReport', action: 'showInvoiceFromTaskGrid')}?taskId=" + taskId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewTaskNote(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'task',true)==false){
            return;
        }
        var taskId = getSelectedIdFromGrid($('#flex1'));

        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhTask', action: 'showEntityNoteForTask')}?taskId=" + taskId;
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
        $('#flex1').flexOptions({query: ''}).flexReload();
    }


    function loadFlexGrid() {
        var strUrl = "${createLink(controller: 'exhTask', action: 'listAgentTaskForCashier')}";
        $("#flex1").flexOptions({url: strUrl});
        if (taskGridModel) {
            $("#flex1").flexAddData(taskGridModel);
        }
    }

	function checkExchangeHouseAmount(value) {
		return (/^[0-9]\d{0,7}(\.\d{1,2})?$/.test(value) && (parseFloat(value) > 0));
	}

	function setConversionRate() {
		var rate = dropDownCurrency.dataItem().rate;
		$('#conversionRate').val(rate);
		//$('#fromAmount').val('');
	}


	function showPaidByNo(paidByNo) {
		var paidBy = dropDownPaidBy.value();
		$('#paidByNo').attr("required",false);
		$('#paidByNo').val('');
		$('#paidByNoSpan').hide();

		if (paidBy == onlinePaymentId) {                   // if online
			$('#paidByNo').attr("required",true);
			var customerCode = '';
			if (paidByNo) {
				customerCode = paidByNo;
			} else {
				customerCode = $('#lblCustomerId').text();
			}
			$('#lblPaidByNo').text('Reference No:');
			$('#paidByNo').val(customerCode);
			$('#paidByNoSpan').show();
		}
	}

	function checkArmsAmountExceptZero(value) {
		return (/^[0-9]\d{0,7}(\.\d{1,2})?$/.test(value));
	}

	function checkAmountFields() {

		var success = true;

		if (!checkArmsAmountExceptZero($('#discount').val())) {
			$('#discount').focus();
			//$('<label></label>').attr('for', 'discount').addClass('error').html('Invalid Amount').insertAfter($('#discount'));
			showError("Invalid amount");
			success = false;
		}

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
        $('#lblCommission').text(parseFloat(entity.commission).toFixed(2));
	}

</script>