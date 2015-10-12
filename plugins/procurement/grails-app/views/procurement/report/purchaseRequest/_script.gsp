<script type="text/javascript">
    var purchaseRequestIdForNavigation = "${result.purchaseRequestMap?.purchaseRequestId}";
    var lstPRItemForNavigation = ${result.lstItems?result.lstItems:[]};
    var lstPRItemModelForNavigation;
    $(document).ready(function () {
        onLoadPurchaseRequest();
        lstPRItemModelForNavigation = $("#lstPRWithItem").kendoListView({
            dataSource: lstPRItemForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:itemType#</td>" +
                    "<td>#:itemName#</td>" +
                    "<td>#:itemCode#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:rateStr#</td>" +
                    "<td>#:totalCostStr#</td>" +
                    "</tr>"
        }).data("lstPRItemModelForNavigation");

        $('#printPurchaseRequestReport').click(function () {
            printPurchaseRequest();
            return false;
        });
    });

    function onLoadPurchaseRequest() {
        $('#listPRWithItem').hide();
        $('#listPRApproval').hide();
        $('.download_icon_set').hide();

        $("#purchaseRequestId").val(purchaseRequestIdForNavigation);
        $("#hidPurchaseRequestId").val(purchaseRequestIdForNavigation);
        if (purchaseRequestIdForNavigation) {
            $('.download_icon_set').show();

            if (lstPRItemForNavigation.length > 0) {
                $('#listPRWithItem').show();
                $('#listPRApproval').show();
            }
        }

        // update page title
        $(document).attr('title', "MIS - Purchase Request");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procReport/showPurchaseRequestRpt");

    }

    function printPurchaseRequest() {
        var purchaseRequestId = $('#hidPurchaseRequestId').val();
        if (purchaseRequestId.length <= 0) {
            showError('First populate PR details then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?purchaseRequestId=" + purchaseRequestId;
        if (confirm('Do you want to download the Purchase Request now?')) {
            var url = "${createLink(controller: 'procReport', action: 'downloadPurchaseRequestRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }


    function executePreConditionForSearchPR() {
        showLoadingSpinner(true);
        // validate form data
        if ($("#purchaseRequestId").val() == '') {
            showError('Please enter a PR No');
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
            if (lstPRItem.length > 0) {
                $('#listPRWithItem').show();
                $('#listPRApproval').show();
            }
        }
    }

</script>