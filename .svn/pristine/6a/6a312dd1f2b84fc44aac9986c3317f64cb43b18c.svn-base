<script language="javascript">

    var moduleListModel = false;

    $(document).ready(function () {
        onLoadModulePage();
    });

    // method called on page load
    function onLoadModulePage() {

        initializeForm($("#moduleForm"), onSubmitModule);

        var output =${output ? output : ''};

        moduleListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        } else {
            moduleListModel = output.gridObj;  // set data in a global variable to populate
        }
        initFlex();
        populateFlex();
        $(document).attr('title', "MIS - Create Module");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptModule/show");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#moduleForm"))) {
            return false;
        }

        return true;
    }

    // method called  on submit of the form
    function onSubmitModule() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'ptModule', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'ptModule', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#moduleForm").serialize(),  // serialize data from UI and send as parameter
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

                    var previousTotal = parseInt(moduleListModel.total);
                    var firstSerial = 1;

                    if (moduleListModel.rows.length > 0) {
                        firstSerial = moduleListModel.rows[0].cell[0];
                        regenerateSerial($(moduleListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    moduleListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        moduleListModel.rows.pop();
                    }

                    moduleListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(moduleListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(moduleListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(moduleListModel);
                }

                resetForm();    // reset the form
                showSuccess(result.message);    // show success message

            } catch (e) {
                // Do Nothing
            }
        }
    }

    // reset the form
    function resetForm() {
        clearForm($("#moduleForm"), $('#name'));  // clear errors & form values
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
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptModule/select,/ptModule/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectModule},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptModule/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteModule},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}

                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Name',
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

    // reload grid
    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    // custom populate the grid
    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            moduleListModel = null;
        } else {
            moduleListModel = data;
        }
        return data;
    }

    // delete module object
    function deleteModule(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var moduleId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'ptModule', action:  'delete')}?id=" + moduleId,
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
        if (!confirm('Are you sure you want to delete the selected module?')) {
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
            moduleListModel.total = parseInt(moduleListModel.total) - 1;
            removeEntityFromGridRows(moduleListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    // select module object for update
    function selectModule(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'module') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptModule', action: 'select')}?id=" + id,
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
            showModule(data);
        }
    }

    // show property of module object on UI
    function showModule(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#code').val(entity.code);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    // set link to grid url to populate data
    function populateFlex() {
        var strUrl = "${createLink(controller:'ptModule',action:  'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (moduleListModel) {
            $("#flex1").flexAddData(moduleListModel);
        }
    }

</script>
