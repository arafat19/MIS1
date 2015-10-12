<script type="text/javascript">
    var notesGridModel;
    var modelJson;
    var customerId;
    $(document).ready(function () {

        onLoadDefaultData();

    });

    function onLoadDefaultData() {
        initializeForm($("#createNoteForm"),onSubmitCreateNote);
        modelJson = ${modelJson};
        if (modelJson.customerInfoMap) {
            populateCustomer(modelJson.customerInfoMap);
        }
        notesGridModel = modelJson.gridObject ? modelJson.gridObject : '';
        initNoteFlexGrid();
        loadFlexGrid();

        // update page title
        $(document).attr('title', "ARMS - Customer Notes");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "");
    }


    function populateCustomer(customerInfoMap) {
        // Hidden fields
        $('#customerId').val(customerInfoMap.id ? customerInfoMap.id : '');
        // Labels
        $('#lblCustomerId').text(customerInfoMap.code ? customerInfoMap.code : '');
        $('#lblCustomerName').text(customerInfoMap.name);
        $('#lblNationality').text(customerInfoMap.nationality);
        $('#lblPhone').text(customerInfoMap.phone ? customerInfoMap.phone : '');
        $('#lblPhotoIdType').text(customerInfoMap.photoIdType ? customerInfoMap.photoIdType : '');
        $('#lblEmail').text(customerInfoMap.email);
        $('#lblAddress').text(customerInfoMap.address ? customerInfoMap.address : '');
        $('#lblSourceOfFund').text(customerInfoMap.sourceOfFund ? customerInfoMap.sourceOfFund : '');
        $('#note').focus();
        customerId=customerInfoMap.id;

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
            actionUrl = "${createLink(controller:'exhCustomer', action: 'createEntityNote')}";
        } else {
            actionUrl = "${createLink(controller:'exhCustomer', action: 'updateEntityNote')}";
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

                    var previousTotal = parseInt(notesGridModel.total);
                    var firstSerial = 1;

                    if (notesGridModel.rows.length > 0) {
                        firstSerial = notesGridModel.rows[0].cell[0];
                        regenerateSerial($(notesGridModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    notesGridModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        notesGridModel.rows.pop();
                    }

                    notesGridModel.total = ++previousTotal;
                    $("#flex1").flexAddData(notesGridModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(notesGridModel, newEntry, 0);
                    $("#flex1").flexAddData(notesGridModel);
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
        ($('#customerId').val(customerId));
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
                        <app:ifAllUrl urls="/exhCustomer/deleteEntityNote">
                        {name: 'Delete', bclass: 'delete', onpress: deleteEntityNote},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},

                        {separator: true}
                    ],

                    sortname: "createdOn",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Customer Notes',
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
            notesGridModel = getEmptyGridModel();
        } else {
            notesGridModel = data.gridObject;
        }
        $("#flex1").flexAddData(notesGridModel);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function loadFlexGrid() {
        var customerId = modelJson.customerInfoMap.id;
        var strUrl = "${createLink(controller: 'exhCustomer', action: 'listEntityNote')}?customerId=" + customerId;
        $("#flex1").flexOptions({url: strUrl});
        if (notesGridModel) {
            $("#flex1").flexAddData(notesGridModel);
        }
    }

    function editEntityNote(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'note details')==false){
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var entityId=getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhCustomer', action: 'editEntityNote')}?id=" + entityId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function deleteEntityNote(com, grid) {

        var ids = $('.trSelected', grid);
        if (executePreConditionForDelete(ids) == false) {
            return;
        }
        showLoadingSpinner(true);
        var noteEntityId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller:'exhCustomer', action:'deleteEntityNote')}?id=" + noteEntityId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete(ids) {
        var delCount = ids.length;
        if (delCount == 0) {
            showError("Please select a note details to delete");
            return false;
        }

        if (!confirm('Are you sure you want to delete the selected note details?')) {
            return false;
        }
        return true;
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
            notesGridModel.total = parseInt(notesGridModel.total) - 1;
            removeEntityFromGridRows(notesGridModel, selectedRow);

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