<script type="text/javascript">
    var output = false;
    var entityNoteListModel = false;
    var entityTypeId, entityId, pluginId;

    $(document).ready(function () {
        onLoadEntityNotePage();
    });

    function onLoadEntityNotePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#entityNoteForm"), onSubmitEntityNote);
        output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            entityNoteListModel = output.lstEntityNote;
        }
        entityTypeId = output.entityTypeId;
        entityId = output.entityId;
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        var entityNoteMap = output.entityNoteMap;
        $("#lblEntityTypeName").text(entityNoteMap.entityTypeName);
        $("#lblEntityName").text(entityNoteMap.entityName);
        $("#lblFormTitle").text(entityNoteMap.panelTitle);
        pluginId = entityNoteMap.pluginId;
        var leftMenu = entityNoteMap.leftMenu;
        $("#pluginId").val(pluginId);

        initFlexGrid();
        populateFlexGrid();

        // update page title
        $(document).attr('title', 'MIS - ' + entityNoteMap.panelTitle);
        loadMenu(pluginId, leftMenu);
    }

    // method called  on submit of the form
    function onSubmitEntityNote() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);    // disable the save button
        showLoadingSpinner(true);   // show loading spinner
        var actionUrl = null;
        // set link for create or update if there is data in hidden field id
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'entityNote', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'entityNote', action: 'update')}";
        }

        // fire ajax method
        jQuery.ajax({
            type: 'post',
            data: jQuery("#entityNoteForm").serialize(),  // serialize data from UI and send as parameter
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

    function executePreCondition() {
        // validate form data
        if (!validateForm($("#entityNoteForm"))) {
            return false;
        }
        return true;
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

                    var previousTotal = parseInt(entityNoteListModel.total);
                    var firstSerial = 1;

                    if (entityNoteListModel.rows.length > 0) {
                        firstSerial = entityNoteListModel.rows[0].cell[0];
                        regenerateSerial($(entityNoteListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    entityNoteListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        entityNoteListModel.rows.pop();
                    }

                    entityNoteListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(entityNoteListModel);

                } else if (newEntry.entity != null) { // updated existing object data in the grid
                    updateListModel(entityNoteListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(entityNoteListModel);
                }

                resetEntityNoteForm();    // reset the form
                showSuccess(result.message);    // show success message
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetEntityNoteForm() {
        clearForm($("#entityNoteForm"), $("#note"));
        $("#entityTypeId").val(entityTypeId);
        $("#entityId").val(entityId);
        $("#pluginId").val(pluginId);
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Note", name: "note", width: 600, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/entityNote/select,/entityNote/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectEntityNote},
                        </app:ifAllUrl>
                        <sec:access url="/entityNote/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteEntityNote},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ], searchitems: [
                    {display: "Note", name: "note", width: 180, sortable: true, align: "left"}
                ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Note List',
                    useRp: true,
                    rp: 20,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateEntityContentGrid
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function customPopulateEntityContentGrid(data) {
        if (data.isError) {
            showError(data.message);
            entityNoteListModel = getEmptyGridModel();
        } else {
            entityNoteListModel = data.lstEntityNote;
        }
        $('#flex1').flexAddData(entityNoteListModel);
        return false;
    }

    function selectEntityNote(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'note') == false) {
            return;
        }

        resetEntityNoteForm();
        showLoadingSpinner(true);
        var entityNoteId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'entityNote', action: 'select')}?id=" + entityNoteId,
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
            showEntityNote(data);
        }
    }

    function showEntityNote(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#note').val(entity.note);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteEntityNote(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'entityNote', action:'delete')}?id=" + id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'note') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected note?')) {
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
            resetEntityNoteForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            entityNoteListModel.total = parseInt(entityNoteListModel.total) - 1;
            removeEntityFromGridRows(entityNoteListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'entityNote', action: 'list')}?entityId=" + entityId + "&entityTypeId=" + entityTypeId;
        $("#flex1").flexOptions({url: strUrl});
        if (entityNoteListModel) {
            $("#flex1").flexAddData(entityNoteListModel);
        }
    }

    function loadMenu(pluginId, leftMenu) {
        var MENU_ID;
        switch (pluginId) {
            case 10:
                MENU_ID = MENU_ID_PROJECT_TRACK
                break
            default:
                MENU_ID = MENU_ID_APPLICATION
                break
        }
        loadNumberedMenu(MENU_ID, leftMenu);
    }

</script>