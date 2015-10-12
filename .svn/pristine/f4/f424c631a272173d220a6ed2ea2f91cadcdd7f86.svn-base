<script language="javascript">

    var acceptanceCriteriaListModel = false;
    var dropDownType, dropDownStatus, backlogId,reservedStatusAccepted,reservedStatusCompleted,currentStatus;

    $(document).ready(function () {
        onLoadAcceptanceCriteriaPage();
    });

    // method called on page load
    function onLoadAcceptanceCriteriaPage() {
        initializeForm($("#acceptanceCriteriaFormForMyBacklog"), onSubmitAcceptanceCriteria);

        var output =${output ? output : ''};
        currentStatus = output.backlogStatus;
        acceptanceCriteriaListModel = false;
        if (output.isError) {
            showError(output.message);  // show error message in case of error
        }
        else {
            acceptanceCriteriaListModel = output.gridObj;    // set data in a global variable to populate
            backlogId = output.backlogId;                    // set backlog id in global variable
        }
        $('#idea').text(output.idea);
        initFlex();
        populateFlex1();
        reservedStatusAccepted = $('#backlogStatusAccepted').val();
        reservedStatusCompleted = $('#backlogStatusCompleted').val();

        // update page title
        $(document).attr('title', "MIS - Update Acceptance Criteria");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptBacklog/showMyBacklog");
    }

    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm("#acceptanceCriteriaFormForMyBacklog")) {
            return false;
        }
        return true;
    }

    // method called  on submit of the form
    function onSubmitAcceptanceCriteria() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            setButtonDisabled($('#create'), false);    // disable the save button
            showLoadingSpinner(false);   // show loading spinner
            showError("Select an acceptance criteria to update");
            return false;
        } else {
            actionUrl = "${createLink(controller:'ptAcceptanceCriteria', action: 'updateForMyBacklog')}";
        }
        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#acceptanceCriteriaFormForMyBacklog").serialize(),  // serialize data from UI and send as parameter
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

                    var previousTotal = parseInt(acceptanceCriteriaListModel.total);
                    var firstSerial = 1;

                    if (acceptanceCriteriaListModel.rows.length > 0) {
                        firstSerial = acceptanceCriteriaListModel.rows[0].cell[0];
                        regenerateSerial($(acceptanceCriteriaListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    acceptanceCriteriaListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        acceptanceCriteriaListModel.rows.pop();
                    }

                    acceptanceCriteriaListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(acceptanceCriteriaListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(acceptanceCriteriaListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(acceptanceCriteriaListModel);
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
        clearForm($("#acceptanceCriteriaFormForMyBacklog"), dropDownType);
        $('#criteria').text('');
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
                        {display: "Type", name: "type", width: 130, sortable: true, align: "left"},
                        {display: "Criteria", name: "criteria", width: 250, sortable: true, align: "left"},
                        {display: "Status", name: "status", width: 150, sortable: false, align: "left"},
                        {display: "Completed On", name: "completedOn", width: 200, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/ptAcceptanceCriteria/select,/ptAcceptanceCriteria/updateForMyBacklog">
                        {name: 'Edit', bclass: 'edit', onpress: selectAcceptanceCriteria},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/ptAcceptanceCriteria/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAcceptanceCriteria},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Acceptance Criteria',
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
            acceptanceCriteriaListModel = null;
        } else {
            acceptanceCriteriaListModel = data;
        }
        return data;
    }

    // delete acceptanceCriteria object
    function deleteAcceptanceCriteria(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptAcceptanceCriteria', action:  'delete')}?id=" + id,
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
        if (executeCommonPreConditionForSelect($('#flex1'), 'acceptance criteria') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected acceptance criteria?')) {
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
            acceptanceCriteriaListModel.total = parseInt(acceptanceCriteriaListModel.total) - 1;
            removeEntityFromGridRows(acceptanceCriteriaListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    // select acceptance criteria object for update
    function selectAcceptanceCriteria(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'acceptance criteria') == false) {
            return;
        }

        if(currentStatus == reservedStatusAccepted || currentStatus == reservedStatusCompleted){
            showError("Completed task's acceptance criteria is not editable.");
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'ptAcceptanceCriteria', action: 'select')}?id=" + id,
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
            showAcceptanceCriteria(data);
        }
    }

    // show property of acceptanceCriteria object on UI
    function showAcceptanceCriteria(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#criteria').text(entity.criteria);
        dropDownType.value(entity.type);
        dropDownStatus.value(entity.statusId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");   // change create button text to update
    }

    // set link to grid url to populate data
    function populateFlex1() {
        var strUrl = "${createLink(controller:'ptAcceptanceCriteria',action:  'listForMyBacklog')}?backlogId=" + backlogId;
        $("#flex1").flexOptions({url: strUrl});

        if (acceptanceCriteriaListModel) {
            $("#flex1").flexAddData(acceptanceCriteriaListModel);
        }
    }

</script>
