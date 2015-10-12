<script type="text/javascript">
    // Global variables
    var payMethodBankDeposit, payMethodCashCollection;
    var modelJsonForInvoice;
    $(document).ready(function (e) {
        onLoadInvoice();
    });

    function onLoadInvoice() {
	    modelJsonForInvoice = ${modelJson};
	    initializeForm($('#frmInvoice'), showInvoice)

	    if (modelJsonForInvoice && modelJsonForInvoice.isError) {
		    showError(modelJsonForInvoice.message);
		    return false;
	    }

        $('#printBtn').click(function () {
            printInvoiceReport();
        });

        $('#refNo').focus();
        if (modelJsonForInvoice.invoiceMap) {
            var invoiceMap = modelJsonForInvoice.invoiceMap;
            if (invoiceMap != null) {
                processInvoiceMap(invoiceMap);
            }
        }
	    $(document).attr('title', "ARMS - Show Invoice");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showInvoice");
    }

    function showInvoice() {

        if (executePreCondition() == false) {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            data: $("#frmInvoice").serialize(),
            url: "${createLink(controller: 'exhReport',action: 'getInvoiceDetails')}",
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    $('#invoicePanel').hide();
                    $('#hidTaskId').val('');
                    $('#hidRefNo').val('');
                } else {
                    processInvoiceMap(data.taskMap);
                }
                return false;
            },
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePreCondition() {
	    trimFormValues($('#frmInvoice'));
	    if($('#refNo').val() == '') {
		    showError("Please enter ref no.");
		    return false;
	    }
        return true;
    }

    function processInvoiceMap(data) {
        // set global variables
        payMethodBankDeposit = parseInt(data.payMethodBankDeposit);
        payMethodCashCollection = parseInt(data.payMethodCashCollection);
        if (data.success == true) {
            var beneficiary = data.beneficiary;
            var task = data.task;
            populateAmountInfo(data);
            populateCustomerInfo(data);
            populateBeneficiaryInfo(beneficiary, task);  // populate info based on payMethod
            populateOtherInfo(data);
            refreshGrandTotalAmount();
            $("#lblCollectionPoint").text(data.collectionPoint);
            $('#invoicePanel').show();
            $('#hidTaskId').val(task.id);
            $('#hidRefNo').val(task.refNo);
        } else {
            showError(data.message);
            $('#invoicePanel').hide();
            $('#hidTaskId').val('');
            $('#hidRefNo').val('');
        }
        return false;
    }

    function populateAmountInfo(data) {
        var task = data.task;
        var amountForeignCurr = parseFloat(task.amountInForeignCurrency).toFixed(2);
        var amountLocalCurr = parseFloat(task.amountInLocalCurrency).toFixed(2);
        var rate = (amountForeignCurr / amountLocalCurr).toFixed(2);
        $("#lblRemittancePurpose").html(data.remittancePurpose);
        $("#lblPaymentMethod").text(data.payMethod);
        $("#lblAmountInForeignCurrency").text(amountForeignCurr + ' (BDT)');
        $("#lblConversionRate").text(rate);
        $("#localCurrencyLabel").text('Equivalent ' + data.currencyName);
        $("#lblAmountInLocalCurrency").text(amountLocalCurr);
        $("#lblRegularFee").text(parseFloat(task.regularFee).toFixed(2));
        $("#lblTotal").text();
        $("#lblDiscount").text(parseFloat(task.discount).toFixed(2));
        $("#lblPaidBy").text(data.paidBy);
    }

    function populateCustomerInfo(data) {
        $("#lblCustomerAccNo").text(data.customerAccNo);
        $("#lblCustomerName").html(data.task.customerName);
        var custAddr = data.customerAddress;
        custAddr = (custAddr ? custAddr.replace(/\n/g, "<br />") : '');
        $("#lblCustomerAddress").html(custAddr);
    }
    function populateBeneficiaryInfo(beneficiary, task) {
        var beneficiaryName = beneficiary.firstName;
        if (beneficiary.middleName) {
            beneficiaryName = beneficiaryName + ' ' + beneficiary.middleName;
        }
        if (beneficiary.lastName) {
            beneficiaryName = beneficiaryName + ' ' + beneficiary.lastName;
        }
        $("#lblBeneficiaryName").html(beneficiaryName);
        $("#lblBeneficiaryAccountNo").text('N/A');      // Default value
        $("#lblBankBrDist").text('N/A');      // Default value
        $("#lblIdentityType").html('N/A');              // Default value
        $("#lblThana").html('N/A');

        $("#lblBeneficiaryMobile").html(beneficiary.phone);     // Common field

        if (task.paymentMethod == payMethodBankDeposit) {
            $("#lblBeneficiaryAccountNo").text(beneficiary.accountNo);           // mandatory field for bank deposit
            var beneficiaryBankDetails = beneficiary.bank + ', ' + beneficiary.bankBranch + ", " + beneficiary.district;
            $("#lblBankBrDist").text(beneficiaryBankDetails); // mandatory field for bank deposit
            var benThana = (beneficiary.thana ? beneficiary.thana : '');
            if (benThana.length == 0) {
                benThana = 'None';
            }
            $("#lblThana").html(benThana);
        } else if (task.paymentMethod == payMethodCashCollection) {
            $("#lblIdentityType").html(beneficiary.photoIdType);                // mandatory field for cash collection
        }
    }

    function populateOtherInfo(data) {
        if ($('#refNo').val() == '') {
            $('#refNo').val(data.task.refNo);
        }
        $("#lblRefNo").text(data.task.refNo);
	    if(data.taskCancelled) {
		    var lblCancel = " <span style='color: red'>(CANCELLED)<span>"
		    $("#lblRefNo").html(data.task.refNo+lblCancel);
	    }
        $("#lblDate").text(data.date);
        $("#lblUserName").html(data.userName);
        var pinNo = data.task.pinNo;
        if ((pinNo == null) || ((pinNo == undefined))) {
            pinNo = 'N/A';
        }
        $("#lblPinNo").text('PIN: ' + pinNo);
        var terms = $('<span/>').html(data.termsAndConditions).text();
        $("#lblTermsAndConditions").text(terms);
    }

    function refreshGrandTotalAmount() {
        var rate , discount, bdtAmount, gbpAmount;
        bdtAmount = parseFloat($('#lblAmountInForeignCurrency').text());
        gbpAmount = parseFloat($('#lblAmountInLocalCurrency').text());
        rate = parseFloat($('#lblConversionRate').text());
        discount = parseFloat($('#lblDiscount').text());
        var regularFee = parseFloat($('#lblRegularFee').text());

        var total = gbpAmount + regularFee;
        $('#lblTotal').text(total.toFixed(2));

        var grandTotal = total - discount;
        $('#lblGrandTotal').text(grandTotal.toFixed(2));

    }

    function printInvoiceReport() {
        var taskId = $('#hidTaskId').val();
        var refNo = $('#hidRefNo').val();
        if ((taskId == '') || (refNo == '')) {
            showError('First populate invoice then download pdf.');
            return false;
        }

        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadInvoice')}" + "?hidTaskId=" + taskId + '&hidRefNo=' + refNo;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

</script>