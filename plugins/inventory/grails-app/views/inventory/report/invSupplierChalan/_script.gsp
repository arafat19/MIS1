<script type="text/javascript">
    var dropDownSupplier, dropDownStatus, modelJsonForSupplierChalan,
            hideChalanNo, hideSupplierId, hideStatus;

    jQuery(function ($) {
        modelJsonForSupplierChalan = ${modelJson};

        $('#printPdfBtn').click(function () {
            downloadInventorySupplierChalanReport();
        });

        $('#printCsvBtn').click(function () {
            downloadInventorySupplierChalanCsvReport();
        });

        onLoadSupplierChalan();
    });

    function onLoadSupplierChalan() {
        initializeForm($("#searchForm"), getSupplierChalan);
        dropDownStatus = initKendoDropdown($('#status'), null, null, modelJsonForSupplierChalan.lstStatus);

        $('.download_icon_set').hide();
        initFlexGrid();

        // update page title
        $(document).attr('title', "MIS - Supplier Chalan Report");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invReport/showSupplierChalan");
        $('#chalanNo').focus();
    }

    function executePreConditionToGetSupplierChalan() {
        var chalanNo = $('#chalanNo').val();
        if (chalanNo == '') {
            showError('Please write down Supplier Chalan No.');
            return false;
        }
        if (dropDownSupplier.value() == '') {
            showError('Please select a Supplier.');
            return false;
        }
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        return true;
    }

    function getSupplierChalan() {
        if (executePreConditionToGetSupplierChalan() == false) {
            $('#chalanNo').focus();
            return false;
        }

        var chalanNo = $('#chalanNo').val();
        var supplierId = dropDownSupplier.value();
        var status = dropDownStatus.value();
        doGridEmpty();

        hideChalanNo = chalanNo;
        hideSupplierId = supplierId;
        hideStatus = status;

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invReport', action: 'listSupplierChalan')}?chalanNo=" + chalanNo + "&supplierId=" + supplierId + "&status=" + status,
            success: populateSupplierChalanGrid,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function populateSupplierChalanGrid(data) {
        if (data.isError == true) {
            showError(data.message);
            doGridEmpty();
            $('.download_icon_set').hide();
        } else {
            var supplierChalanListUrl = "${createLink(controller:'invReport', action: 'listSupplierChalan')}?chalanNo=" + hideChalanNo + "&supplierId=" + hideSupplierId + "&status=" + hideStatus;

            $("#flex1").flexOptions({url: supplierChalanListUrl});
            onLoadSupplierChalanListJSON(data);
            $('.download_icon_set').show();
        }
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 25, sortable: false, align: "right", hide: true},
                        {display: "PO Trace", name: "poId", width: 50, sortable: false, align: "center", hide: false},
                        {display: "Tr. Date", name: "transactionDate", width: 70, sortable: false, align: "left"},
                        {display: "Inventory Name", name: "inventoryName", width: 190, sortable: false, align: "left"},
                        {display: "Item Name", name: "itemName", width: 170, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 100, sortable: false, align: "right"},
                        {display: "Rate", name: "rate", width: 90, sortable: false, align: "right"},
                        {display: "Amount", name: "amount", width: 120, sortable: false, align: "right"},
                        {display: "Approved", name: "approved", width: 50, sortable: false, align: "center"},
                        {display: "Acknowledged", name: "invoiceAcknowledgedBy", width: 80, sortable: false, align: "center"}
                    ],
                    buttons: [
                        {name: 'Report', bclass: 'addDoc', onpress: getInvoiceInventory},
                        <sec:access url="/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier">
                        {name: 'Acknowledge', bclass: 'acknowledge', onpress: acknowledgeChalan},
                        </sec:access>
                        {separator: true}
                    ],
                    sortname: "transactionDate",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Supplier Chalan List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [10, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight(5),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    customPopulate: onLoadSupplierChalanListJSON
                }
        );
    }

    function getInvoiceInventory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var transactionId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'invReport', action: 'showInvoice')}?id=" + transactionId;
        $.post(loc, function (data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        });
        return false;
    }

    function onLoadSupplierChalanListJSON(data) {
        if (data.isError) {
            showInfo(data.message);
            doGridEmpty();
            return false;
        }
        $("#flex1").flexAddData(data.lstSupplierChalan);
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('Supplier chalan not found');
        }
    }

    function doGridEmpty() {
        var emptyGridModel = getEmptyGridModel();
        $("#flex1").flexAddData(emptyGridModel);
        $("#flex1").flexOptions({url: false, newp: 1, rp: 20}).flexReload();
        $('select[name=rp]').val(20);
    }

    function executePreConditionForAcknowledge() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production details') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to acknowledge the selected chalan?')) {
            return false;
        }
        return true;
    }

    function acknowledgeChalan(com, grid) {
        var ids = $('.trSelected', grid);
        if (executePreConditionForAcknowledge() == false) {
            return;
        }
        var acknowledgedBy = $(ids[0]).find('td').eq(9).find('div').text();
        if (acknowledgedBy == "YES") {
            showError("Chalan is already acknowledged.");
            return;
        }
        showLoadingSpinner(true);
        var transactionDetailsId = $(ids[0]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller: 'invInventoryTransactionDetails', action: 'acknowledgeInvoiceFromSupplier')}?id="
                    + transactionDetailsId,
            success: executePostConditionForAcknowledge,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForAcknowledge(result) {
        var ids = $('.trSelected', $('#flex1'));
        if (result.isError) {
            showError(result.message);
        } else {
            $(ids[0]).find('td').eq(9).find('div').text('YES');
            showSuccess(result.message);
        }
    }

    function downloadInventorySupplierChalanReport() {
        if (hideChalanNo == null) {
            showError("Please enter a chalan no.");
            return false;
        }
        if (hideSupplierId == '-1') {
            showError("Please select a supplier");
            return false;
        }
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller:'invReport', action: 'downloadSupplierChalanReport')}?chalanNo=" + hideChalanNo + "&supplierId=" + hideSupplierId + "&status=" + hideStatus;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function downloadInventorySupplierChalanCsvReport() {
        if (hideChalanNo == null) {
            showError("Please enter a chalan no.");
            return false;
        }
        if (hideSupplierId == '-1') {
            showError("Please select a supplier");
            return false;
        }

        showLoadingSpinner(true);
        if (confirm('Do you want to download the CSV now?')) {
            var url = "${createLink(controller:'invReport', action: 'downloadSupplierChalanCsvReport')}?chalanNo=" + hideChalanNo + "&supplierId=" + hideSupplierId + "&status=" + hideStatus;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>