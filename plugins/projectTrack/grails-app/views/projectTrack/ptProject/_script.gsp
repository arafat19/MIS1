<script type="text/javascript">
    var validatorPtProject, entityTypeId;
    var ptProjectListModel = false;

    $(document).ready(function () {
        onLoadPtProjectPage();
    });

    // method called on page load
    function onLoadPtProjectPage() {
        var output =${output ? output : ''};
        initializeForm($("#ptProjectForm"), onSubmitPtProject);
        ptProjectListModel = false;
        if (output.isError) {
            showError(output.message);                  // show error message in case of error
        } else {
            ptProjectListModel = output.gridObj;        // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        entityTypeId = $("#entityTypeId").val();
        $(document).attr('title', "MIS - Create Project");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptProject/show");
    }

    // method called  on submit of the form
    function onSubmitPtProject() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);            // disable the save button
        showLoadingSpinner(true);                       // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'ptProject', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'ptProject', action: 'update')}";
        }
        // fire ajax method for create or update
        jQuery.ajax({
            type: 'post',
            data: jQuery("#ptProjectForm").serialize(),     // serialize data from UI and send as parameter
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);       // enable the save button
                showLoadingSpinner(false);                  // stop loading spinner
            },
            dataType: 'json'
        });
        return false;
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#ptProjectForm"))) {
            return false;
        }
        return true;
    }

    // execute post condition after create or update
    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);                      // show error message in case of error
            showLoadingSpinner(false);                      // stop loading spinner
        }
        else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {          // show newly created object in a grid row

                    var previousTotal = parseInt(ptProjectListModel.total);
                    var firstSerial = 1;

                    if (ptProjectListModel.rows.length > 0) {
                        firstSerial = ptProjectListModel.rows[0].cell[0];
                        regenerateSerial($(ptProjectListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    ptProjectListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        ptProjectListModel.rows.pop();
                    }

                    ptProjectListModel.total = ++previousTotal;
                    $("#flex").flexAddData(ptProjectListModel);

                } else if (newEntry.entity != null) {                               // updated existing object data in the grid
                    updateListModel(ptProjectListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(ptProjectListModel);
                }

                resetForm();                                // reset the form
                showSuccess(result.message);                // show success message
            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetForm() {
        clearForm($("#ptProjectForm"), $("#name"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");           // reset create button text
    }

    // initialize the grid
    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Project ID", name: "id", width: 60, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 180, sortable: false, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptProject/select,/ptProject/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectPtProject},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptProject/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deletePtProject},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptProjectModule/show">
                        {name: 'Module', bclass: 'note', onpress: addPtProjectModule},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/appUserEntity/show">
                        {name: 'User', bclass: 'note', onpress: addPtUserProject},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Projects',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            ptProjectListModel = getEmptyGridModel();
        } else {
            ptProjectListModel = data;
        }
        $("#flex").flexAddData(ptProjectListModel);
    }

    function addPtProjectModule(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGrid($('#flex'));
        var loc = "${createLink(controller:'ptProjectModule', action: 'show')}?projectId=" + projectId;
        $.history.load(formatLink(loc));
        return false;
    }

    function addPtUserProject(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'project') == false) {
            return;
        }
        showLoadingSpinner(true);
        var projectId = getSelectedIdFromGrid($('#flex'));
        var loc = "${createLink(controller:'appUserEntity', action: 'show')}?entityId=" + projectId + "&entityTypeId=" + entityTypeId;
        $.history.load(formatLink(loc));
        return false;
    }

    // select ptProject object for update
    function selectPtProject(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'project') == false) {
            return;
        }
        resetForm();                    // reset the form
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for select
        $.ajax({
            url: "${createLink(controller:'ptProject', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function () {
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
            showPtProject(data);
        }
    }
    // show property of selected ptProject object on UI
    function showPtProject(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    // delete ptProject object
    function deletePtProject(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var ptProjectId = getSelectedIdFromGrid($('#flex'));
        // fire ajax method for delete
        $.ajax({
            url: "${createLink(controller:'ptProject', action:  'delete')}?id=" + ptProjectId,
            success: executePostConditionForDelete,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // execute pre condition for delete
    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex'), 'project') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected project?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            ptProjectListModel.total = parseInt(ptProjectListModel.total) - 1;
            removeEntityFromGridRows(ptProjectListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }

    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller: 'ptProject',action: 'list')}";
        $("#flex").flexOptions({url: strUrl});

        if (ptProjectListModel) {
            $("#flex").flexAddData(ptProjectListModel);
        }
    }

</script>