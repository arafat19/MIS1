<script language="javascript">

    var dropDownModuleId, projectId, selectedIndex;
    var projectModuleListModel = false;

    $(document).ready(function () {
        onLoadProjectModulePage();
    });

    // method called on page load
    function onLoadProjectModulePage() {

        initializeForm($("#projectModuleForm"), onSubmitProjectModule);

        var output =${output ? output : ''};

        dropDownModuleId = initKendoDropdown($("#moduleId"),null,null,output.lstModule);

        projectModuleListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            projectModuleListModel = output.gridObj;  // set data in a global variable to populate
            projectId = output.projectId;
            $('#projectId').val(projectId);
            $('#projectName').text(output.projectName);
        }
        initFlex();
        populateFlex();
        $(document).attr('title', "MIS - Create ProjectModule");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptProject/show");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#projectModuleForm"))) {
            return false;
        }
        return true;
    }

    // method called  on submit of the form
    function onSubmitProjectModule() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'ptProjectModule', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'ptProjectModule', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#projectModuleForm").serialize(),  // serialize data from UI and send as parameter
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

                    var previousTotal = parseInt(projectModuleListModel.total);
                    var firstSerial = 1;

                    if (projectModuleListModel.rows.length > 0) {
                        firstSerial = projectModuleListModel.rows[0].cell[0];
                        regenerateSerial($(projectModuleListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    projectModuleListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        projectModuleListModel.rows.pop();
                    }

                    projectModuleListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(projectModuleListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(projectModuleListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(projectModuleListModel);
                }
                // remove from dropDownList after create
                var selectedIdx = dropDownModuleId.select();
                var itemToRemove = dropDownModuleId.dataSource.data()[selectedIdx];
                dropDownModuleId.dataSource.remove(itemToRemove);

                resetFormForCreate();    // reset the form
                showSuccess(result.message);    // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetFormForCreate() {
        clearForm($("#projectModuleForm"), dropDownModuleId);  // clear errors & form values
        $('#projectId').val(projectId); // re-assign hidden field value
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");   // reset create button text
    }

     // reset the form
    function resetForm() {
        // re-assign dropDown for cancel action.
        if (!$('#id').val().isEmpty()) {
            var itemToRemove = dropDownModuleId.dataSource.at(selectedIndex);
            dropDownModuleId.dataSource.remove(itemToRemove);
            dropDownModuleId.refresh();
        }
        clearForm($("#projectModuleForm"), dropDownModuleId);  // clear errors & form values
        $('#projectId').val(projectId); // re-assign hidden field value
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
                        {display: "Serial", name: "serial", width: 50, sortable: false, align: "right"},
                        {display: "Module", name: "moduleName", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptProjectModule/select,/ptProjectModule/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectProjectModule},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptProjectModule/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteProjectModule},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Module Name", name: "m.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Name',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight(5) - 35,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            projectModuleListModel = null;
        } else {
            projectModuleListModel = data;
        }
        return data;
    }

    // delete project module object
    function deleteProjectModule(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectModuleId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptProjectModule', action:  'delete')}?id=" + projectModuleId,
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
        if (executeCommonPreConditionForSelect($('#flex1'), 'module') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected project module?')) {
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

            dropDownModuleId.setDataSource(data.lstModule); // re-populate module dropDown
            resetFormForCreate();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            projectModuleListModel.total = parseInt(projectModuleListModel.total) - 1;
            removeEntityFromGridRows(projectModuleListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    // select module object for update
    function selectProjectModule(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'module') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptProjectModule', action: 'select')}?id=" + id,
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
            showProjectModule(data);
        }
    }

    // show property of module object on UI
    function showProjectModule(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        dropDownModuleId.setDataSource(data.lstModule);
        dropDownModuleId.value(entity.moduleId);
        selectedIndex = dropDownModuleId.select();
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'ptProjectModule',action:  'list')}?projectId=" + projectId;
        $("#flex1").flexOptions({url: strUrl});

        if (projectModuleListModel) {
            $("#flex1").flexAddData(projectModuleListModel);
        }
    }

</script>
