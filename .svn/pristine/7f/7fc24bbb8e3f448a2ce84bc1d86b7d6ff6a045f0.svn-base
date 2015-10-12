<script language="javascript">
    var dropDownItemType;
    var itemListModel = false;

    $(document).ready(function () {
        onLoadFixedAssetPage();
    });
    function onLoadFixedAssetPage() {
        var output =${output ? output : ''};

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#fixedAssetItemForm"), onSubmitItem);

        if (output.isError) {
            showError(output.message);
        } else {
            itemListModel = output.itemList;
        }

        initFlexGrid();
        populateFlex1();

        // update page title
        $(document).attr('title', "MIS - Create Fixed Asset Item");
        loadNumberedMenu(MENU_ID_FIXED_ASSET, "#item/showFixedAssetItem");

    }

    function executePreCondition() {
        if (!validateForm($("#fixedAssetItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitItem() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'item', action: 'createFixedAssetItem')}";
        } else {
            actionUrl = "${createLink(controller:'item', action: 'updateFixedAssetItem')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#fixedAssetItemForm").serialize(),
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

                    var previousTotal = parseInt(itemListModel.total);
                    var firstSerial = 1;

                    if (itemListModel.rows.length > 0) {
                        firstSerial = itemListModel.rows[0].cell[0];
                        regenerateSerial($(itemListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    itemListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        itemListModel.rows.pop();
                    }

                    itemListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(itemListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(itemListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(itemListModel);
                }

                resetFixedAssetItemForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetFixedAssetItemForm() {
        clearForm($("#fixedAssetItemForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
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
                        {display: "Individual Entity", name: "isIndividualEntity", width: 100, sortable: false, align: "center"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/item/select,/item/updateFixedAssetItem">
                        {name: 'Edit', bclass: 'edit', onpress: selectItem},
                        </app:ifAllUrl>
                        <sec:access url="/item/deleteFixedAssetItem">
                        {name: 'Delete', bclass: 'delete', onpress: deleteItem},
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
                    title: 'All Fixed Asset Items',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight()-15,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadFiexedAssetItemListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadFiexedAssetItemListJSON(data) {
        if (data.isError) {
            showError(data.message);
            itemListModel = null;
        } else {
            itemListModel = data;
        }
        return data;
    }


    function deleteItem(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var itemId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'item', action: 'deleteFixedAssetItem')}?id=" + itemId,
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
        if (!confirm('Are you sure you want to delete the selected Item?')) {
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
            resetFixedAssetItemForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            itemListModel.total = parseInt(itemListModel.total) - 1;
            removeEntityFromGridRows(itemListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function selectItem(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item') == false) {
            return;
        }

        resetFixedAssetItemForm();
        showLoadingSpinner(true);
        var fxdItemId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'item', action: 'select')}?id=" + fxdItemId,
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
            showItem(data);
        }
    }

    function showItem(data) {

        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#unit').val(entity.unit);
        $('#code').val(entity.code);
        dropDownItemType.value(entity.itemTypeId);
        $('#isIndividualEntity').attr('checked', entity.isIndividualEntity);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlex1() {
        var strUrl = "${createLink(controller:'item', action: 'listFixedAssetItem')}";
        $("#flex1").flexOptions({url: strUrl});

        if (itemListModel) {
            $("#flex1").flexAddData(itemListModel);
        }
    }


</script>
