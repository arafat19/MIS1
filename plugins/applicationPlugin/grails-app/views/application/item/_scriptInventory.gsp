<script language="javascript">
    var dropDownItemType, dropDownValuationType;
    var itemInventoryListModel = false;

    $(document).ready(function () {
        onLoadItemInventoryPage();
    });

    function onLoadItemInventoryPage() {
        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#itemInventoryForm"), onSubmitItemInventory);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            itemInventoryListModel = output.itemList;
        }

        initFlexGrid();
        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create Inventory Item");
        loadNumberedMenu(MENU_ID_APPLICATION, "#item/showInventoryItem");
    }

    function executePreCondition() {
        if (!validateForm($("#itemInventoryForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitItemInventory() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createInventoryItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateInventoryItem')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#itemInventoryForm").serialize(),
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

                    var previousTotal = parseInt(itemInventoryListModel.total);
                    var firstSerial = 1;

                    if (itemInventoryListModel.rows.length > 0) {
                        firstSerial = itemInventoryListModel.rows[0].cell[0];
                        regenerateSerial($(itemInventoryListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    itemInventoryListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        itemInventoryListModel.rows.pop();
                    }

                    itemInventoryListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(itemInventoryListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(itemInventoryListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(itemInventoryListModel);
                }

                resetItemInventoryForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetItemInventoryForm() {
        clearForm($("#itemInventoryForm"), $("#name"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
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
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Code", name: "code", width: 100, sortable: false, align: "left"},
                        {display: "Unit", name: "unit", width: 80, sortable: false, align: "left"},
                        {display: "Item Type", name: "itemType", width: 100, sortable: false, align: "left"},
                        {display: "Valuation Type", name: "valuationType", width: 100, sortable: false, align: "center"},
                        {display: "Finished Product", name: "isFinishedProduct", width: 100, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/item/select,/item/updateInventoryItem">
                        {name: 'Edit', bclass: 'edit', onpress: selectItemInventory},
                        </app:ifAllUrl>
                        <sec:access url="/item/deleteInventoryItem">
                        {name: 'Delete', bclass: 'delete', onpress: deleteItemInventory},
                        </sec:access>
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
                    title: 'All Inventory Items',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadItemInventoryListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadItemInventoryListJSON(data) {
        if (data.isError) {
            showError(data.message);
            itemInventoryListModel = null;
        } else {
            itemInventoryListModel = data;
        }
        return data;
    }

    function deleteItemInventory(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteInventoryItem')}?itemId=" + itemId,
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
        if (data.deleted == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetItemInventoryForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            itemInventoryListModel.total = parseInt(itemInventoryListModel.total) - 1;
            removeEntityFromGridRows(itemInventoryListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function selectItemInventory(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }

        resetItemInventoryForm();
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
            showItemInventory(data);
        }
    }

    function showItemInventory(data) {

        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#unit').val(entity.unit);
        $('#code').val(entity.code);
        dropDownItemType.value(entity.itemTypeId);
        dropDownValuationType.value(entity.valuationTypeId);
        $('#isFinishedProduct').attr('checked', entity.isFinishedProduct);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlex1() {
        var strUrl = "${createLink(controller:'item', action: 'listInventoryItem')}";
        $("#flex1").flexOptions({url: strUrl});

        if (itemInventoryListModel) {
            $("#flex1").flexAddData(itemInventoryListModel);
        }
    }

</script>
