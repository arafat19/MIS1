<script language="javascript">
    var dropDownItemType;
    var NonInventoryItemListModel = false;

    $(document).ready(function () {
        onLoadNonInventoryItemPage();
    });
    function onLoadNonInventoryItemPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#nonInventoryItemForm"), onSubmitNonInventoryItem);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            NonInventoryItemListModel = output.itemList;
        }

        initGrid();
        setUrlOnGrid();

        // update page title
        $(document).attr('title', "MIS - Create Non-Inventory Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#item/showNonInventoryItem");
    }

    function executePreCondition() {
        if (!validateForm($("#nonInventoryItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitNonInventoryItem() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createNonInventoryItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateNonInventoryItem')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#nonInventoryItemForm").serialize(),
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
                var newEntry = result.item;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(NonInventoryItemListModel.total);
                    var firstSerial = 1;

                    if (NonInventoryItemListModel.rows.length > 0) {
                        firstSerial = NonInventoryItemListModel.rows[0].cell[0];
                        regenerateSerial($(NonInventoryItemListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    NonInventoryItemListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        NonInventoryItemListModel.rows.pop();
                    }

                    NonInventoryItemListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(NonInventoryItemListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(NonInventoryItemListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(NonInventoryItemListModel);
                }

                resetNonInventoryItemForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetNonInventoryItemForm() {
        clearForm($("#nonInventoryItemForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Code", name: "code", width: 100, sortable: false, align: "left"},
                        {display: "Unit", name: "unit", width: 80, sortable: false, align: "left"},
                        {display: "Item Type", name: "itemType", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/item/select,/item/updateNonInventoryItem">
                        {name: 'Edit', bclass: 'edit', onpress: selectNonInventoryItem},
                        </app:ifAllUrl>
                        <sec:access url="/item/deleteNonInventoryItem">
                        {name: 'Delete', bclass: 'delete', onpress: deleteNonInventoryItem},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Code", name: "code", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Non-Inventory Items',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadNonInventoryItemListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadNonInventoryItemListJSON(data) {
        if (data.isError) {
            showError(data.message);
            NonInventoryItemListModel = null;
        } else {
            NonInventoryItemListModel = data;
        }
        return data;
    }

    function deleteNonInventoryItem(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteNonInventoryItem')}?id=" + itemId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected item?')) {
            return false;
        }
        return true;
    }

    <%-- removing selected row and clean input form --%>
    function executePostConditionForDelete(data) {
        if (data.deleted) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetNonInventoryItemForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            NonInventoryItemListModel.total = parseInt(NonInventoryItemListModel.total) - 1;
            removeEntityFromGridRows(NonInventoryItemListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }


    function selectNonInventoryItem(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }

        resetNonInventoryItemForm();
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'item', action: 'select')}?id=" + itemId,
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
            showNonInventoryItem(data);
        }
    }

    function showNonInventoryItem(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#unit').val(entity.unit);
        $('#code').val(entity.code);
        dropDownItemType.value(entity.itemTypeId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function setUrlOnGrid() {
        var strUrl = "${createLink(controller:'item', action: 'listNonInventoryItem')}";
        $("#flex1").flexOptions({url: strUrl});

        if (NonInventoryItemListModel) {
            $("#flex1").flexAddData(NonInventoryItemListModel);
        }
    }


</script>
