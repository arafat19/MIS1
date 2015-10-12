<script language="javascript">
    var accDivisionListModel = false;
    var dropDownProject;

    $(document).ready(function () {
        onLoadAccDivisionPage();
    });

    function onLoadAccDivisionPage() {
        initializeForm($("#accDivisionForm"), onSubmitAccDivision);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accDivisionListModel = output.accDivisionList;
        }
        initFlexGrid()
        populateFlexGrid()
        // update page title
        $(document).attr('title', "MIS - Create Division");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accDivision/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accDivisionForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccDivision() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accDivision', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accDivision', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accDivisionForm").serialize(),
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
                var newEntry = result.accDivision;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accDivisionListModel.total);
                    var firstSerial = 1;

                    if (accDivisionListModel.rows.length > 0) {
                        firstSerial = accDivisionListModel.rows[0].cell[0];
                        regenerateSerial($(accDivisionListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accDivisionListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accDivisionListModel.rows.pop();
                    }

                    accDivisionListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accDivisionListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accDivisionListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accDivisionListModel);
                    resetForm();
                }

                resetFormForCreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accDivisionForm"), dropDownProject);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        dropDownProject.enable(true);
    }

    function resetFormForCreate() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#name').val('');
        $('#id').val('');
        $('#version').val('');
        $('#isActive').attr('checked', false);
        dropDownProject.enable(true);
        $('#name').focus();
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
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Project", name: "projectId", width: 250, sortable: false, align: "left"},
                        {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accDivision/select,/accDivision/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccDivision},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accDivision/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccDivision},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Project", name: "projectName", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Divisions',
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

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accDivisionListModel = null;
        } else {
            accDivisionListModel = data;
        }
        return data;
    }

    function deleteAccDivision(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accDivisionId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accDivision', action: 'delete')}?id=" + accDivisionId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'division') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected division?')) {
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
            accDivisionListModel.total = parseInt(accDivisionListModel.total) - 1;
            removeEntityFromGridRows(accDivisionListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectAccDivision(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'division') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var accDivisionId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accDivision', action: 'select')}?id="
                    + accDivisionId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showAccDivision(data);
        }
    }

    function showAccDivision(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);

        dropDownProject.value(entity.projectId);
        dropDownProject.enable(false);

        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accDivision', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accDivisionListModel) {
            $("#flex1").flexAddData(accDivisionListModel);
        }
    }

</script>
