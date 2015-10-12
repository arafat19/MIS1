<script type="text/javascript">
    var dropDownIdType;
    var transactionIdForNavigation = "${result.transactionMap?.transactionId}";
    var lstRMForNavigation = ${result.lstRawMaterial?result.lstRawMaterial:[]};
    var lstFPForNavigation = ${result.lstFinishedProduct?result.lstFinishedProduct:[]};

    $(document).ready(function () {
        onLoadProduction();

        $('#printInventoryProductionReport').click(function () {
            printProduction();
            return false;
        });
    });

    function onLoadProduction() {
        $("#searchForm").kendoValidator({validateOnBlur: false});

        $('#IdType').kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownIdType = $('#IdType').data("kendoDropDownList");

        $('#transactionId').val(transactionIdForNavigation);

        $("#tbodyMaterials").kendoListView({
            dataSource: lstRMForNavigation,
            template: "<tr>" +
                    "<td>#:id#</td>" +
                    "<td>#:raw_material#</td>" +
                    "<td>#:str_quantity#</td>" +
                    "<td>#:str_rate#</td>" +
                    "<td>#:str_total_amount#</td>" +
                    "</tr>"
        });

        $("#tbodyFinishedProduct").kendoListView({
            dataSource: lstFPForNavigation,
            template: "<tr>" +
                    "<td>#:id#</td>" +
                    "<td>#:finished_product#</td>" +
                    "<td>#:str_quantity#</td>" +
                    "<td>#:str_rate#</td>" +
                    "<td>#:str_overhead_cost#</td>" +
                    "<td>#:str_total_amount#</td>" +
                    "</tr>"
        });
        if (!transactionIdForNavigation) {
            $('.download_icon_set').hide();
        }
        // update page title
        $(document).attr('title', "MIS - Inventory Production Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showInventoryProductionRpt");
    }

    function executePreCondition() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        return true;
    }

    function executePostCondition() {
        if (isError == 'true') {
            showError(message);
            $('#transactionDetails').hide();
            $('.download_icon_set').hide();
            return false;
        }

        $('.download_icon_set').show();

        return true;
    }

    function printProduction() {
        var transactionId = $('#hidTransactionId').val();
        if (transactionId.length <= 0) {
            showError('First populate production details then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?transactionId=" + transactionId;
        if (confirm('Do you want to download the production report now?')) {
            var url = "${createLink(controller: 'invReport', action: 'downloadInventoryProductionRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>