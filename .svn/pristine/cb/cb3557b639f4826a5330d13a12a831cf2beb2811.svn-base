<script language="javascript">
    var termsAndConditionListModel =${output ? output : ''};
    var termsAndConditionGrid = null;

    $(document).ready(function () {
        onLoadProcTermsAndCondition();
    });

    function onLoadProcTermsAndCondition() {
        initializeForm($("#procTermsAndConditionForm"), onSubmitProcTermsAndCondition);

        var isError = termsAndConditionListModel.isError;
        if (isError == 'true') {
            var message = termsAndConditionListModel.message;
            showError(message);
            return;
        }
        initGrid();
        termsAndConditionGrid = termsAndConditionListModel.termsAndConditionList;
        populatePurchaseOrder(termsAndConditionListModel.purchaseOrderMap);

        // update page title
        $(document).attr('title', "MIS - Terms and Condition For Purchase Order");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#purchaseOrder/show");
    }

    function populatePurchaseOrder(purchaseOrderMap) {
        if (!purchaseOrderMap) {
            return false;
        }

        $('#purchaseOrderId').val(purchaseOrderMap.purchaseOrderId);
        $('#lblPurchaseOrderId').html(purchaseOrderMap.purchaseOrderId);
        $('#lblProjectName').html(purchaseOrderMap.projectName);

        return true;
    }

    function executePreCondition() {
        if (!validateForm($("#procTermsAndConditionForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitProcTermsAndCondition() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call

        var data = jQuery("#procTermsAndConditionForm").serialize();
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'procTermsAndCondition', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'procTermsAndCondition', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: data,
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {

                if ($('#id').val().isEmpty() && result.entity != null) { // newly created
                    var previousTotal = parseInt(termsAndConditionGrid.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (termsAndConditionGrid.rows.length > 0) {
                        firstSerial = termsAndConditionGrid.rows[0].cell[0];
                        regenerateSerial($(termsAndConditionGrid.rows), 0);
                    }
                    result.entity.cell[0] = firstSerial;
                    termsAndConditionGrid.rows.splice(0, 0, result.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        termsAndConditionGrid.rows.pop();
                    }

                    termsAndConditionGrid.total = ++previousTotal;
                    $("#flex1").flexAddData(termsAndConditionGrid);

                } else if (result.entity != null) { // updated existing
                    updateListModel(termsAndConditionGrid, result.entity, 0);
                    $("#flex1").flexAddData(termsAndConditionGrid);
                }
                resetCreateForm(result);
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetCreateForm(result) {
        clearForm($("#procTermsAndConditionForm"), $('#details'));

        var purchaseOrderMap = termsAndConditionListModel.purchaseOrderMap;
        $('#purchaseOrderId').val(purchaseOrderMap.purchaseOrderId);

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    //-------------
    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Details", name: "details", width: 350, sortable: false, align: "left"},
                        {display: "Created By", name: "username", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/procTermsAndCondition/select,/procTermsAndCondition/update">
                        {name: 'Edit', bclass: 'edit', onpress: editprocTermsAndCondition},
                        </app:ifAllUrl>
                        <sec:access url="/procTermsAndCondition/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteprocTermsAndCondition},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Terms and Condition For Purchase Order',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },

                    customPopulate: onLoadProcTermsAndConditionListJSON
                }
        );
    }

    function deleteprocTermsAndCondition(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var procTermsAndConditionId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'procTermsAndCondition', action: 'delete')}?id=" + procTermsAndConditionId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'terms and condition') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Terms and Condition?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            termsAndConditionGrid.total = parseInt(termsAndConditionGrid.total) - 1;
            removeEntityFromGridRows(termsAndConditionGrid, selectedRow);
            resetCreateForm(data);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No terms and condition found');
        }
    }

    function editprocTermsAndCondition(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'terms and condition') == false) {
            return;
        }
        var procTermsAndConditionId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'procTermsAndCondition', action: 'select')}?id=" + procTermsAndConditionId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            populateprocTermsAndCondition(data);
        }
    }

    function populateprocTermsAndCondition(data) {
        resetCreateForm(data);
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#details').val(entity.details);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function onLoadProcTermsAndConditionListJSON(data) {
        if (data.isError) {
            showError(data.message);
            termsAndConditionGrid = getEmptyGridModel();
        } else {
            termsAndConditionGrid = data;
        }
        $("#flex1").flexAddData(termsAndConditionGrid);
    }

    <sec:access url="/procTermsAndCondition/list">
    var purchaseOrderId = $('#purchaseOrderId').val();
    var strUrl = "${createLink(controller: 'procTermsAndCondition', action: 'list')}?purchaseOrderId=" + purchaseOrderId;
    $("#flex1").flexOptions({url: strUrl});

    if (termsAndConditionGrid) {
        $("#flex1").flexAddData(termsAndConditionGrid);
    }
    </sec:access>

</script>
