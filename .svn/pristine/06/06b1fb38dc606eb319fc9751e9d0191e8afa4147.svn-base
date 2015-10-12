<script type="text/javascript">
    var taskNoteGridModel;
    var modelJson;
    var taskId;
    $(document).ready(function () {
	    onLoadDefaultData();
    });

    function onLoadDefaultData() {

	    modelJson = ${modelJson}
			    initNoteFlexGrid();
	    if (modelJson.isError) {
		    showError(modelJson.message);
		    return false;
	    }

	    initializeForm($('#createNoteForm'),onSubmitCreateNote);

        if (modelJson.taskInfoMap) {
            populateTask(modelJson.taskInfoMap);
        }

        taskNoteGridModel = modelJson.gridObject ? modelJson.gridObject : '';

        loadFlexGrid();

        // update page title
        $(document).attr('title', "ARMS - Task Notes");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "");
    }


    function populateTask(taskInfoMap) {
        // Hidden fields
        $('#taskId').val(taskInfoMap.id);
        // Labels
        $('#lblCustomerId').text(taskInfoMap.customerId);
        $('#lblCustomerName').text(taskInfoMap.customerName);
        $('#lblBeneficiaryName').text(taskInfoMap.beneficiaryName);
        $('#lblRefNo').text(taskInfoMap.refNo);
        $('#lblPaymentType').text(taskInfoMap.paymentType);
        $('#lblAmount').text(taskInfoMap.amount);
        $('#note').focus();
        taskId=taskInfoMap.id;

    }

    function onSubmitCreateNote() {

        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'exhTask', action: 'createEntityNoteForTask')}";
        } else {
            actionUrl = "${createLink(controller:'exhTask', action: 'updateEntityNoteForTask')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#createNoteForm").serialize(),
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

    function executePreCondition() {
        // trim field vales before process.
        trimFormValues($("#createNoteForm"));
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.entity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(taskNoteGridModel.total);
                    var firstSerial = 1;

                    if (taskNoteGridModel.rows.length > 0) {
                        firstSerial = taskNoteGridModel.rows[0].cell[0];
                        regenerateSerial($(taskNoteGridModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    taskNoteGridModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        taskNoteGridModel.rows.pop();
                    }

                    taskNoteGridModel.total = ++previousTotal;
                    $("#flex1").flexAddData(taskNoteGridModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(taskNoteGridModel, newEntry, 0);
                    $("#flex1").flexAddData(taskNoteGridModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#createNoteForm"));
        $('#taskId').val(taskId);
        $('#note').focus();
	    $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }


    function initNoteFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, hide: true, align: "right"},
                        {display: "Note", name: "note", width: 575, sortable: false, align: "left"},
                        {display: "Created By", name: "createdBy", width: 150, sortable: false, align: "left"},
                        {display: "Created On", name: "createdOn", width: 110, sortable: false, align: "left"},
                        {display: "Updated On", name: "updatedOn", width: 110, sortable: false, align: "left"}
                    ],
                    buttons: [

                        {name: 'Edit', bclass: 'edit', onpress: editEntityNote},
                        <app:ifAllUrl urls="/exhTask/deleteEntityNoteForTask">
                        {name: 'Delete', bclass: 'delete', onpress: deleteEntityNote},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},

                        {separator: true}
                    ],

                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Task Notes',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateNoteGrid
                }
        );
    }

    function populateNoteGrid(data) {
        if (data.isError) {
            showError(data.message);
            taskNoteGridModel = getEmptyGridModel();
        } else {
            taskNoteGridModel = data.gridObject;
        }
        $("#flex1").flexAddData(taskNoteGridModel);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function loadFlexGrid() {
        var taskId = modelJson.taskInfoMap.id;
        var strUrl = "${createLink(controller: 'exhTask', action: 'listEntityNoteForTask')}?taskId=" + taskId;
        $("#flex1").flexOptions({url: strUrl});
        if (taskNoteGridModel) {
            $("#flex1").flexAddData(taskNoteGridModel);
        }
    }

    function editEntityNote(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'note')==false){
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var entityId=getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhTask', action: 'selectEntityNoteForTask')}?id=" + entityId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function deleteEntityNote(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'note')==false){
             return;
        }
        if (!confirm('Are you sure you want to delete the selected note details?')) {
            return false;
        }
        showLoadingSpinner(true);
        var noteEntityId=getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhTask', action:'deleteEntityNoteForTask')}?id=" + noteEntityId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            taskNoteGridModel.total = parseInt(taskNoteGridModel.total) - 1;
            removeEntityFromGridRows(taskNoteGridModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select a note details to edit");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showEntityNote(data);
        }
    }

    function showEntityNote(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#note').val(entity.note);
	    $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

</script>