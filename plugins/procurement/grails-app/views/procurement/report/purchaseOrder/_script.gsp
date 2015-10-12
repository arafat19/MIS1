<script type="text/javascript">
    var purchaseOrderIdForNavigation = "${result.purchaseOrderMap?.purchaseOrderId}";
    var cancelledPOIdForNavigation = "${result.purchaseOrderMap?.isCancelled}";
    var lstPOItemForNavigation = ${result.itemList?result.itemList:[]};
    var lstTermsConditionForNavigation = ${result.termsAndConditionList?result.termsAndConditionList:[]};
    $(document).ready(function () {
        onLoadPurchaseOrder();
        $("#lstPOWithItem").kendoListView({
            dataSource: lstPOItemForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:itemType#</td>" +
                    "<td>#:itemName#</td>" +
                    "<td>#:itemCode#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:rateStr#</td>" +
                    "<td>#:totalCostStr#</td>" +
                    "</tr>"
        });

        $("#lstTermsCondition").kendoListView({
            dataSource: lstTermsConditionForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:details#</td>" +
                    "</tr>"
        });

        $('#printPurchaseOrderReport').click(function () {
            printPurchaseOrder();
            return false;
        });

        $('#lblCancel1').hide();
        $('#lblCancel2').hide();
        $('#lblReferenceTitle').text('Reference');  // default value
        $('#lblCancelDate').text('Last Updated On'); // default value
        $('#lblFormHeader').text(''); // default value
        if (cancelledPOIdForNavigation == 'true') {
            $('#cancelledPo').attr('checked', true);
            $('#lblFormHeader').text(' ( Cancelled )');
            $('#lblReferenceTitle').text('Cancelled By');
            $('#lblCancelDate').text('Cancelled On');
            $('#lblCancel1').show();
            $('#lblCancel2').show();
        }
    });

    function onLoadPurchaseOrder() {
        $('#listPOWithItem').hide();
        $('#listPOApproval').hide();
        $('#listTermsCondition').hide();
        $('.download_icon_set').hide();

        $("#purchaseOrderId").val(purchaseOrderIdForNavigation);
        $("#hidPurchaseOrderId").val(purchaseOrderIdForNavigation);
        $("#hidIsCancelled").val(cancelledPOIdForNavigation);
        if (purchaseOrderIdForNavigation) {
            $('.download_icon_set').show();

            if (lstPOItemForNavigation.length > 0) {
                $('#listPOWithItem').show();
                $('#listPOApproval').show();
            }
            if (lstTermsCondition.length > 0) {
                $('#listTermsCondition').show();
            }
        }

        // update page title
        $(document).attr('title', "MIS - Purchase Order");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procReport/showPurchaseOrderRpt");

    }

    function printPurchaseOrder() {
        var purchaseOrderId = $('#hidPurchaseOrderId').val();
        var cancelledPo = $('#hidIsCancelled').val();
        if (purchaseOrderId.length <= 0) {
            showError('First populate PO details then click Print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?purchaseOrderId=" + purchaseOrderId + "&cancelledPo=" + cancelledPo;
        if (confirm('Do you want to download the Purchase Order now?')) {
            var url = "${createLink(controller: 'procReport', action: 'downloadPurchaseOrderRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }


    function executePreConditionForSearchPO() {
        showLoadingSpinner(true);
        // validate form data
        if ($("#purchaseOrderId").val() == '') {
            showError('Please enter a PO No');
            showLoadingSpinner(false);
            return false;
        }
        return true;
    }

    function executePostCondition() {
        showLoadingSpinner(false);
        if (isError == 'true') {
            $('.download_icon_set').hide();
        } else {
            $('.download_icon_set').show();
            if (lstPOItem.length > 0) {
                $('#listPOWithItem').show();
                $('#listPOApproval').show();
            }
            if (lstTermsCondition.length > 0) {
                $('#listTermsCondition').show();
            }
        }
    }

</script>