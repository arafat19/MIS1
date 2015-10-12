<script language="javascript">
    var dropDownProdLineItem,dropDownFinishedMaterial, numericOverHeadCost ;
    var transactionDetailsListModel = false;

    $(document).ready(function () {
        onLoadProdModifyOverheadCostPage();
    });

    function onLoadProdModifyOverheadCostPage() {
        var output =${output ? output : ''};

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#searchProdTransactionDetails"), onSubmitProdTransactionDetails);
        initializeForm($("#frmModifyOverheadCost"), onSubmitModifyOverheadCost);

        $("#overheadCost").kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        numericOverHeadCost = $("#overheadCost").data("kendoNumericTextBox");

        dropDownFinishedMaterial = initKendoDropdown($('#finishedMaterialId'), null, null, null);

        if (output.isError) {
            showError(output.message);
        } else {
            $("#fromDate").val(output.fromDate);
            $("#toDate").val(output.toDate);
        }
        // update page title
        $(document).attr('title', "MIS - Modify Overhead Cost of Production");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransactionDetails/showInvModifyOverheadCost");
    }

    function hideOverheadCostChangingForm() {
        $('#hidFromDate').val('');
        $('#hidToDate').val('');
        $('#hidProdLineItemId').val('');
        $('#hidFinishedMaterialId').val('');
        numericOverHeadCost.value('');
    }

    function showOverheadCostChangingForm(data) {
        $('#hidFromDate').val(data.fromDate);
        $('#hidToDate').val(data.toDate);
        $('#hidProdLineItemId').val(data.prodLineItemId);
        $('#hidFinishedMaterialId').val(data.finishedMaterialId);
        numericOverHeadCost.value('');
    }

    function executePreConditionForSearch() {
        if(!validateForm($("#searchProdTransactionDetails"))){
            return false;
        }

        if (!customValidateDate($('#fromDate'), 'From Date', $('#toDate'), 'To Date')) {
            return false;
        }
        return true;
    }

    function onSubmitProdTransactionDetails() {
        if (executePreConditionForSearch() == false) {
            return false;
        }
        setButtonDisabled($('#search'), true);
        showLoadingSpinner(true);
        hideOverheadCostChangingForm();
        doGridEmpty();
        var actionUrl = "${createLink(controller:'invInventoryTransactionDetails', action: 'searchInvModifyOverheadCost')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#searchProdTransactionDetails").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForSearch(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#search'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostConditionForSearch(result) {
        if (result.isError == true) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            showOverheadCostChangingForm(result);
            populateProdTransactionDetailsList(result);
        }
    }

    function populateProdTransactionDetailsList(data) {
        var strUrl = "${createLink(controller: 'invInventoryTransactionDetails', action: 'searchInvModifyOverheadCost')}?fromDate=" + data.fromDate + "&toDate=" + data.toDate + "&prodLineItemId=" + data.prodLineItemId + "&finishedMaterialId=" + data.finishedMaterialId;

        $("#flex1").flexOptions({url: strUrl});
        $("#flex1").flexAddData(data.transactionDetailsList);
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Chalan", name: "id", width: 100, sortable: false, align: "right"},
                    {display: "Inventory", name: "inventory", width: 300, sortable: true, align: "left"},
                    {display: "Transaction Date", name: "transaction_date", width: 100, sortable: false, align: "center"},
                    {display: "Rate", name: "rate", width: 100, sortable: false, align: "right"},
                    {display: "Overhead Cost", name: "overhead_cost", width: 100, sortable: false, align: "right"}
                ],
                sortname: "viitd.transaction_date",
                sortorder: "ASC",
                usepager: true,
                singleSelect: true,
                title: 'All Transaction of Production',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 60,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                customPopulate: onLoadProdTransactionDetailsListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadProdTransactionDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            transactionDetailsListModel = null;
        } else {
            transactionDetailsListModel = data.transactionDetailsList;
        }
        $("#flex1").flexAddData(transactionDetailsListModel);
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function executePreConditionForModify() {
        var msg = "Please populate All Transaction of Production";
        if ($('#hidProdLineItemId').val().isEmpty()) {
            showError(msg);
            return false;
        }
        if ($('#hidFinishedMaterialId').val().isEmpty()) {
            showError(msg);
            return false;
        }

        if ($('#hidFromDate').val().isEmpty()) {
            showError(msg);
            return false;
        }
        if ($('#hidToDate').val().isEmpty()) {
            showError(msg);
            return false;
        }

        if (!customValidateDate($('#hidFromDate'), 'From Date', $('#hidToDate'), 'To Date')) {
            return false;
        }

        if (!validateForm($("#frmModifyOverheadCost"))) {
            return false;
        }


        if (!confirm('Change in Overhead Cost will be effective only after Valuation Re-calculation. Do you like to change Overhead Cost?')) {
            return false;
        }
        return true;
    }

    function onSubmitModifyOverheadCost() {
        if (executePreConditionForModify() == false) {
            return false;
        }
        setButtonDisabled($('#createModifyOverheadCost'), true);
        showLoadingSpinner(true);
        doGridEmpty();
        var actionUrl = "${createLink(controller:'invInventoryTransactionDetails', action: 'updateInvModifyOverheadCost')}";

        jQuery.ajax({
            type: 'post',
            data: jQuery("#frmModifyOverheadCost").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForModify(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#createModifyOverheadCost'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostConditionForModify(data) {
        if (data.isError == true) {
            showError(data.message);
            showLoadingSpinner(false);
        } else {
            showSuccess(data.message);
            populateProdTransactionDetailsList(data);

        }
    }

    function updateFinishedMaterialList() {
        var prodLineItemId = dropDownProdLineItem.value();
        dropDownFinishedMaterial.setDataSource(getKendoEmptyDataSource(dropDownFinishedMaterial));
        dropDownFinishedMaterial.value('');
        if (prodLineItemId == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invInventoryTransactionDetails', action: 'getInvProdFinishedMaterialByLineItemId')}?prodLineItemId=" + prodLineItemId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownFinishedMaterial.setDataSource(getKendoEmptyDataSource(dropDownFinishedMaterial,null));
                } else {
                    dropDownFinishedMaterial.setDataSource(data.finishedMaterialList);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
        $('#finishedMaterialId').focus();
    }
</script>
