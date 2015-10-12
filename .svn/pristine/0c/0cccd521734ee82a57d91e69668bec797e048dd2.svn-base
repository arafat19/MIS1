<script language="javascript">
    var dropDownItemCategory;

    var itemTypeListModel = false;
    $(document).ready(function () {
        onLoadItemTypePage();
    });
    function onLoadItemTypePage() {
        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#itemTypeForm"), onSubmitItemType);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            itemTypeListModel = output.itemTypeList;
        }

        initGrid();
        setUrlOnGrid();

        // update page title
        $(document).attr('title', "MIS - Create Item Type");
        loadNumberedMenu(MENU_ID_APPLICATION, "#itemType/show");
    }

    function executePreCondition() {
        if (!validateForm($("#itemTypeForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitItemType() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'itemType', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'itemType', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#itemTypeForm").serialize(),
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
                var newEntry = result.itemType;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(itemTypeListModel.total);
                    var firstSerial = 1;

                    if (itemTypeListModel.rows.length > 0) {
                        firstSerial = itemTypeListModel.rows[0].cell[0];
                        regenerateSerial($(itemTypeListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    itemTypeListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        itemTypeListModel.rows.pop();
                    }

                    itemTypeListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(itemTypeListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(itemTypeListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(itemTypeListModel);
                }

                resetItemTypeForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetItemTypeForm() {
        clearForm($("#itemTypeForm"), dropDownItemCategory); // clear errors & form values & bind focus event
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
                        {display: "Category", name: "categoryId", width: 150, sortable: true, align: "left"},
                        {display: "Name", name: "name", width: 300, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/itemType/select,/itemType/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectItemType},
                        </app:ifAllUrl>
                        <sec:access url="/itemType/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteItemType},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Item Type',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadItemTypeListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadItemTypeListJSON(data) {
        if (data.isError) {
            showError(data.message);
            itemTypeListModel = null;
        } else {
            itemTypeListModel = data;
        }
        return data;
    }

    function deleteItemType(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'itemType', action: 'delete')}?id=" + id,
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
        if (!confirm('Are you sure you want to delete the selected item type?')) {
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
            resetItemTypeForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            itemTypeListModel.total = parseInt(itemTypeListModel.total) - 1;
            removeEntityFromGridRows(itemTypeListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }


    function selectItemType(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'item type') == false) {
            return;
        }

        resetItemTypeForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'itemType', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please select an item type to edit");
            return false;
        }
        return true;
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
        dropDownItemCategory.value(entity.categoryId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function setUrlOnGrid() {
        var strUrl = "${createLink(controller:'itemType', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (itemTypeListModel) {
            $("#flex1").flexAddData(itemTypeListModel);
        }
    }

</script>
