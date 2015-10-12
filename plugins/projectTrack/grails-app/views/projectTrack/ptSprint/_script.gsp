<script language="javascript">

    var sprintListModel = false;
    var dropDownProject, dropDownStatus, startDate, endDate, statusId;

    $(document).ready(function () {
        onLoadSprintPage();
    });

    // method called on page load
    function onLoadSprintPage() {
        initializeForm($("#sprintForm"), onSubmitSprint);

        var output = ${output ? output : ''};
        sprintListModel = false;
        statusId = $('#hidSprintStatusDefined').val();
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            sprintListModel = output.gridObj;    // set data in a global variable to populate
            startDate = $("#startDate").val();
            endDate = $("#endDate").val();
            $("#name").text('( Auto Generated )');
        }
        dropDownStatus.readonly(true);
        dropDownStatus.value(statusId);
        initFlex();
        populateFlex1();
        // update page title
        $(document).attr('title', "MIS - Create Sprint");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptSprint/show");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#sprintForm"))) {
            return false;
        }
        if (!customValidateDate($("#startDate"), 'Start date', $("#endDate"), 'end date')) {
            return false;
        }
        return true;
    }

    // method called  on submit of the form
    function onSubmitSprint() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'ptSprint', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'ptSprint', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#sprintForm").serialize(),  // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('#create'), false);   // enable the save button
                showLoadingSpinner(false);  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);  // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // show newly created object in a grid row

                    var previousTotal = parseInt(sprintListModel.total);
                    var firstSerial = 1;

                    if (sprintListModel.rows.length > 0) {
                        firstSerial = sprintListModel.rows[0].cell[0];
                        regenerateSerial($(sprintListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    sprintListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        sprintListModel.rows.pop();
                    }

                    sprintListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(sprintListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(sprintListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(sprintListModel);
                }

                resetForm();    // reset the form
                reloadGrid();
                showSuccess(result.message);    // show success message
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetForm() {
        clearForm($("#sprintForm"), dropDownProject);  // clear errors & form values
        $('#name').text('( Auto Generated )');
        $("#startDate").val(startDate);
        $("#endDate").val(endDate);
        dropDownStatus.readonly(true);
        dropDownStatus.value(statusId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

    // initialize the grid
    function initFlex() {
        $("#flex1").flexigrid
        (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 40, sortable: false, align: "right"},
                    {display: "Sprint Name", name: "name", width: 400, sortable: false, align: "left"},
                    {display: "Status", name: "status", width: 180, sortable: false, align: "left"},
                    {display: "Active", name: "is_active", width: 80, sortable: false, align: "center"},
                    {display: "Task Count", name: "count", width: 80, sortable: false, align: "right"},
                    {display: "Bug Count", name: "bug_count", width: 80, sortable: false, align: "right"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/ptSprint/select,/ptSprint/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectSprint},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/ptSprint/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteSprint},
                    </app:ifAllUrl>
                    {name: 'Report', bclass: 'note', onpress: viewSprintReport},
                    {name: 'Task', bclass: 'note', onpress: viewTask},
                    {name: 'Bug', bclass: 'note', onpress: viewBug},
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Sprint Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                sortname: "name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Sprints',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadListJSON
            }
        );
    }

    function viewTask(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptBacklog', action: 'showBackLogForSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewBug(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptBug', action: 'showBugForSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewSprintReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'ptReport', action: 'showReportSprint')}?sprintId=" + sprintId;
        $.history.load(formatLink(loc));
        return false;
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            sprintListModel = null;
        } else {
            sprintListModel = data;
        }
        return data;
    }

    function executePreConditionForViewBugReport() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return false;
        }
        return true;
    }

    // delete employee object
    function deleteSprint(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var sprintId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'ptSprint', action:  'delete')}?id=" + sprintId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected ptSprint?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            sprintListModel.total = parseInt(sprintListModel.total) - 1;
            removeEntityFromGridRows(sprintListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    // select ptSprint object for update
    function selectSprint(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'sprint') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptSprint', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute post condition for edit
    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSprint(data);
        }
    }

    // show property of ptSprint object on UI
    function showSprint(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').text(entity.name);
        $('#startDate').val(data.startDate);
        $('#endDate').val(data.endDate);
        dropDownProject.value(entity.projectId);
        dropDownStatus.readonly(false);
        dropDownStatus.value(entity.statusId);
        $('#isActive').attr('checked', entity.isActive);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'ptSprint',action:  'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (sprintListModel) {
            $("#flex1").flexAddData(sprintListModel);
        }
    }

</script>
