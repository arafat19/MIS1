<script type="text/javascript">
    var dropDownProject;

    $(document).ready(function () {
        onLoadProjectBudgetScopePage();
    });

    function onLoadProjectBudgetScopePage() {
        initializeForm($("#frmSearchProject"), onSubmitProjectBudgetScope);
        $('#frmSaveBudgetScope').submit(function (e) {
            onSubmitFrmSaveBudgetScope();
            return false;
        });
        // update page title
        $(document).attr('title', "MIS - Project Scope Mapping");
        loadNumberedMenu(MENU_ID_BUDGET, "#projectBudgetScope/show");

        // SET HEIGHT OF ListBox
        var listHeight = getGridHeight() - 55;
        $('#availableBudgetScope').css('height', listHeight);
        $('#selectedBudgetScope').css('height', listHeight);
    }

    function onSubmitProjectBudgetScope() {
        var projectId = dropDownProject.value();
        if (projectId == '') {
            $('#availableBudgetScope').refreshDropDown(null, {unselectedValue: false});
            $('#selectedBudgetScope').refreshDropDown(null, {unselectedValue: false});
            $('#hidProject').val('');
            showError('Please select a project');
            return false;
        }

        var url = "${createLink(controller: 'projectBudgetScope',action: 'select')}?projectId=" + projectId;
        jQuery.ajax({
            type: 'post',
            url: url,
            success: function (data, textStatus) {
                executePostConditionForSearchProject(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForSearchProject(data) {
        $('#hidProject').val(dropDownProject.value());

        $('#availableBudgetScope').refreshDropDown(data.lstAvailableBudgetScope, {textMember: 'name', unselectedValue: false});
        $('#selectedBudgetScope').refreshDropDown(data.lstAssignedBudgetScope, {textMember: 'name', unselectedValue: false});
    }

    function onSubmitFrmSaveBudgetScope() {

        var projectId = dropDownProject.value();
        if (projectId == '') {
            showError('Please select a project');
            return false;
        }

        //check search
        var budgetScopeCount = $("#availableBudgetScope option").length;
        budgetScopeCount = budgetScopeCount + $("#selectedBudgetScope option").length;

        if (budgetScopeCount <= 0) {
            showError('Budget scope not found');
            return false;
        }

        var assignedBudgetScopeIds = '';

        $("#selectedBudgetScope option").each(function () {
            assignedBudgetScopeIds += $(this).attr('value') + '_';
        });

        var url = "${createLink(controller: 'projectBudgetScope',action: 'update')}?assignedBudgetTypeIds=" + assignedBudgetScopeIds + '&projectId=' + $('#hidProject').val();
        jQuery.ajax({
            type: 'post',
            url: url,
            success: function (data, textStatus) {
                executePostConditionForUpdate(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForUpdate(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
    }

    function addDataToSelectedProject() {
        $("#availableBudgetScope > option:selected").each(function () {
            $(this).remove().appendTo("#selectedBudgetScope");
        });
        return false;
    }

    function addAllDataToSelectedProject() {
        $("#availableBudgetScope > option").each(function () {
            $(this).remove().appendTo("#selectedBudgetScope");
        });
        return false;
    }

    function removeDataFromSelectedProject() {
        $("#selectedBudgetScope > option:selected").each(function () {
            $(this).remove().appendTo("#availableBudgetScope");
        });
        return false;
    }

    function removeAllDataFromSelectedProject() {
        $("#selectedBudgetScope > option").each(function () {
            $(this).remove().appendTo("#availableBudgetScope");
        });
        return false;
    }

    function discardChanges() {
        if (isEmpty($('#hidProject').val())) {
            return false;
        }
        dropDownProject.value($('#hidProject').val());
        onSubmitProjectBudgetScope();
        return false;
    }

</script>
