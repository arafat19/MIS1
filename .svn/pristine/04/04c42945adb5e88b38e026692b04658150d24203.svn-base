<script type="text/javascript">
    var invoiceIdForNavigation = "${result.invoiceMap?.invoiceNo}";

    $(document).ready(function () {
        onLoadInvoice();

        $('#printInvoiceReport').click(function () {
            printInvoice();
            return false;
        });
    });

    function onLoadInvoice() {
        $("#searchForm").kendoValidator({validateOnBlur: false});

        if (!invoiceIdForNavigation) {
            $('.download_icon_set').hide();
        }
        // update page title
        $(document).attr('title', "MIS - Chalan");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInvoice");
    }

    function executePreConditionToGetInvoice() {
        if (!validateForm($('#searchForm'))) {
            return false;
        }
        return true;
    }


    function executePostConditionForInvoice() {
        if (isError == 'true') {
            showError(message);
            return;
        }
        if(isCurrent == 'false'){
            $('span.currentAdjustment').css({'font-weight': 'bold', 'color': 'brown'})   ;
        } else{
            $('span.currentAdjustment').css({'font-weight': '', 'color': ''})   ;
        }

        $('.download_icon_set').show();
        $('#inventoryInvoice').show();
        return false;
    }


    function printInvoice() {
        var invoiceNo = $('#hidInvoiceNo').val();
        if (executePreConditionToGetInvoice(invoiceNo) == false) {
            return false;
        }

        showLoadingSpinner(true);
        var params = "?invoiceNo=" + invoiceNo;
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller:'invReport', action: 'downloadInvoice')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>