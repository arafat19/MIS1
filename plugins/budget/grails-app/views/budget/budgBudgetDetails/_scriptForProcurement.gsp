<script language="javascript">
    var budgetDetailsListModel, budgetDetailsGrid, budgetId, projectId, budgetItem;
    var dropDownItemType, dropDownItem;

    $(document).ready(function () {
        onLoadBudgetDetails();
    });

    function onLoadBudgetDetails() {
        initFlexGrid();
        budgetDetailsListModel = ${output ? output : ''};
        var isError = budgetDetailsListModel.isError;
        if (isError == 'true') {
            var message = budgetDetailsListModel.message;
            showError(message);
            return;
        }
        if (budgetDetailsListModel.budgetInfo) {
            setBudgetInfo(budgetDetailsListModel.budgetInfo);
        }
        budgetDetailsGrid = budgetDetailsListModel.budgetDetailsList;

        // update page title
        $(document).attr('title', "MIS - Create Budget Details");
        loadNumberedMenu(MENU_ID_BUDGET, budgetDetailsListModel.budgetInfo.leftMenu);
    }

    function setBudgetInfo(budgetInfo) {
        $('#projectName').text(budgetInfo.projectName);
        $('#budgetScope').text(budgetInfo.budgetScope);
        $('#budgetItemSpan').text(budgetInfo.budgetItem);
        $('#detailsSpan').text(budgetInfo.details);
        budgetId = budgetInfo.budgetId;
    }

    function resetFormBudgetDetails() {
        clearForm($("#budgetDetailsForm"));
        $('#unit').text('');
        $('#itemTypeId').text('');
        $('#itemId').text('');
        $('#total').text('');
        $('#quantity').text('');
        $('#rate').text('');
        $('#comments').text('');
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 35, sortable: false, align: "right"},
                        {display: "Item Type", name: "itemTypeId", width: 120, sortable: false, align: "left"},
                        {display: "Item", name: "itemId", width: 220, sortable: false, align: "left"},
                        {display: "Quantity", name: "quantity", width: 100, sortable: false, align: "right"},
                        {display: "Total Consumption", name: "totalConsumption", width: 110, sortable: false, align: "right"},
                        {display: "Balance", name: "balance", width: 100, sortable: false, align: "right"},
                        {display: "Estimated Rate", name: "rate", width: 120, sortable: false, align: "right"},
                        {display: "Total", name: "total", width: 130, sortable: false, align: "right"},
                        {display: "Up to date", name: "isUpToDate", width: 100, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgBudgetDetails/select,/budgBudgetDetails/update">
                        {name: 'Details', bclass: 'details', onpress: selectBudgetDetails},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Item", name: "itemId", width: 150, sortable: true, align: "left"},
                        {display: "Item Type", name: "itemTypeId", width: 150, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Budget Details',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 15,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    },
                    preProcess: onLoadBudgetDetailsListJSON
                }
        );
    }

    function onLoadBudgetDetailsListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetDetailsGrid = null;
        } else {
            budgetDetailsGrid = data;
        }
        return budgetDetailsGrid;
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No budget details data found');
        }
    }

    function selectBudgetDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget details') == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetDetailsId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgBudgetDetails', action: 'select')}?id=" + budgetDetailsId,
            success: executePostConditionForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(result.message);
        } else {
            populateBudgetDetailsForEdit(data);
        }
    }

    function populateBudgetDetailsForEdit(data) {
        resetFormBudgetDetails();
        var entity = data.entity;

        $('#comments').text(entity.comments ? entity.comments : '');
        $('#itemTypeId').text(data.itemType.name);
        $('#itemId').text(data.item.name);
        $('#quantity').text(entity.quantity + ' ' + data.item.unit);
        $('#rate').text(entity.rate + ' per ' + data.item.unit);
        var total = parseFloat(entity.rate * entity.quantity);
        $('#total').text(total.toFixed(4));
    }

    window.onload = populateBudgetDetailsGridOnPageLoad();

    function populateBudgetDetailsGridOnPageLoad() {
        var strUrl = "${createLink(controller:'budgBudgetDetails', action: 'list')}?budgetId=" + budgetId;
        $("#flex1").flexOptions({url: strUrl});

        if (budgetDetailsGrid) {
            $("#flex1").flexAddData(budgetDetailsGrid);
        }
        var gridTitle;
        if (budgetId) {
            gridTitle = "Budget Details for [Budget No - " + budgetId + " ]";
        }
        else {
            gridTitle = "All Budget Details";
        }
        $('div.mDiv > div.ftitle').text(gridTitle);
    }

</script>