<script type="text/javascript">
    // Global veriables
    var payMethodBankDeposit, payMethodCashCollection;
    var modelJson;
    $(document).ready(function () {
        modelJson = ${modelJson};

        if (modelJson && modelJson.isError) {
            showError(modelJson.message);
            return false;
        }

        onLoadInvoice();
    });

    function onLoadInvoice() {

        initializeForm($('#frmInvoice'),showInvoice);
        $('#downloadBtn').click(function () {
            downloadInvoice();
        });

        if (modelJson.invoiceMap) {
            var invoiceMap = modelJson.invoiceMap;
            if (invoiceMap != null) {
                processInvoiceMap(invoiceMap);
            }
        }

        $('#refNo').focus();
    }

    function showInvoice() {

        if (executePreCondition() == false) {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            data: $("#frmInvoice").serialize(),
            url: "${createLink(controller: 'exhReport',action: 'invoiceDetailsForCustomer')}",
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
	    trimFormValues($("#frmInvoice"));
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
        $('#downloadBtnSpan').hide();
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

            if (data.isDownloadable) {
                $('#downloadBtnSpan').show();
            }
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
        $("#lblAmountInLocalCurrency").text(amountLocalCurr);
        $("#localCurrencyLabel").text('Equivalent ' + data.currencyName);
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

    function downloadInvoice() {
        var taskId = $('#hidTaskId').val();
        var refNo = $('#hidRefNo').val();
        if ((taskId == '') || (refNo == '')) {
            showError('First populate invoice then download pdf.');
            return false;
        }

        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadInvoiceForCustomer')}" + "?hidTaskId=" + taskId + '&hidRefNo=' + refNo;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }


    // update page title
    $(document).attr('title', "ARMS - Show Invoice");
    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhReport/showInvoiceForCustomer");
</script>

<style type="text/css">
#invoicePanel p {
    padding: 1px 0 !important;
}
</style>


<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Task
        </div>
    </div>

    <form name='searchForm' id='frmInvoice' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidTaskId" value=""/>
            <g:hiddenField name="hidRefNo" value=""/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="refNo">Reference No:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="refNo" name="refNo" tabindex="1"/>
                        </div>
                    </div>



        </div>

        <div class="panel-footer">
            <button  name="showReport" id="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="downloadBtn" class="pdf_icon"></a></li>
                </ul>
            </span>
        </div>
    </form>
</div>


<div id="invoicePanel" class="table-responsive" style="display: none">

    <g:render template='/exchangehouse/report/invoice/invoiceReport'/>

</div>

