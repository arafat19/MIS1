<script language="javascript">
    var budgetScopeListModel = false;

    $(document).ready(function () {
        onLoadBudgetScope();
    });

    function onLoadBudgetScope() {
        initializeForm($("#budgetScopeForm"), onSubmitBudgetScope);

        var output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            budgetScopeListModel = output.budgetScopeList;
        }
        initFlexGrid();
        populateFlexGrid();

        // update page title
        $(document).attr('title', "MIS - Create BudgetScope");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgetScope/show");
    }

    function executePreCondition() {
        if (!validateForm($("#budgetScopeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitBudgetScope() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'budgetScope', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'budgetScope', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#budgetScopeForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.budgetScope;

                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(budgetScopeListModel.total);
                    var firstSerial = 1;

                    if (budgetScopeListModel.rows.length > 0) {
                        firstSerial = budgetScopeListModel.rows[0].cell[0];
                        regenerateSerial($(budgetScopeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    budgetScopeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        budgetScopeListModel.rows.pop();
                    }

                    budgetScopeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(budgetScopeListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(budgetScopeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(budgetScopeListModel);
                }
                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#budgetScopeForm"), $('#name'));  // clear errors & form values
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 300, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/budgetScope/select,/budgetScope/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectBudgetScope},
                        </app:ifAllUrl>
                        {name: 'Delete', bclass: 'delete', onpress: deletedBudgetScope},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid },
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Budget Scopes',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadBudgetScopeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadBudgetScopeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            budgetScopeListModel = null;
        } else {
            budgetScopeListModel = data;
        }
        return data;
    }

    function selectBudgetScope(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget Scope') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var budgetScopeId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'budgetScope', action: 'select')}?id="
                    + budgetScopeId,
            success: executePostConditionForSelect,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForSelect(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showBudgetScope(data);
        }
    }

    function showBudgetScope(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deletedBudgetScope(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var budgetScopeId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'budgetScope', action: 'delete')}?id=" + budgetScopeId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'budget Scope') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected budget Scope?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            budgetScopeListModel.total = parseInt(budgetScopeListModel.total) - 1;
            removeEntityFromGridRows(budgetScopeListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'budgetScope', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (budgetScopeListModel) {
            $("#flex1").flexAddData(budgetScopeListModel);
        }
    }

</script>
