<script language="javascript">
    var productionLineItemListModel = false;

    $(document).ready(function () {
        onLoadProductionLineItemPage();
    });
    function onLoadProductionLineItemPage() {
        var output =${output ? output : ''};

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#productionLineItemForm"), onSubmitProductionLineItem);

        if (output.isError) {
            showError(output.message);
        } else {
            productionLineItemListModel = output.invProductionLineItemList;
        }
        // update page title
        $(document).attr('title', "MIS - Create Production Line Item");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invProductionLineItem/show");
    }

    function executePreCondition() {
        if (!validateForm($("#productionLineItemForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitProductionLineItem() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'invProductionLineItem', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'invProductionLineItem', action:  'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#productionLineItemForm").serialize(),
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
        if (result.isError == true) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.invProductionLineItem;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(productionLineItemListModel.total);
                    var firstSerial = 1;

                    if (productionLineItemListModel.rows.length > 0) {
                        firstSerial = productionLineItemListModel.rows[0].cell[0];
                        regenerateSerial($(productionLineItemListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    productionLineItemListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        productionLineItemListModel.rows.pop();
                    }

                    productionLineItemListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(productionLineItemListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(productionLineItemListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(productionLineItemListModel);
                }

                resetForm();

                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#productionLineItemForm"), $('#name'));
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    $("#flex1").flexigrid
    (
            {
                url: false,
                dataType: 'json',
                colModel: [
                    {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Raw Material", name: "raw_material", width: 100, sortable: false, align: "center"},
                    {display: "Finished Product", name: "finished_material", width: 100, sortable: false, align: "center"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/invProductionLineItem/select,/invProductionLineItem/update">
                    {name: 'Edit', bclass: 'edit', onpress: SelectProductionLineItem},
                    </app:ifAllUrl>
                    <sec:access url="/invProductionLineItem/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteProductionLineItem},
                    </sec:access>
                    <sec:access url="/invProductionDetails/show">
                    {name: 'Material', bclass: 'addItem', onpress: addProductionDetailsWitehMaterial},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid} ,
                    {separator: true}
                ],
                searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                ],
                sortname: "id",
                sortorder: "desc",
                usepager: true,
                singleSelect: true,
                title: 'All Production Line Items',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 50,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadProductionLineItemListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadProductionLineItemListJSON(data) {
        if (data.isError) {
            showError(data.message);
            productionLineItemListModel = null;
        } else {
            productionLineItemListModel = data;
        }
        return data;
    }


    function deleteProductionLineItem(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var productionLineItemId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'invProductionLineItem', action: 'delete')}?id=" + productionLineItemId,
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
        if (!confirm('Are you sure you want to delete the selected production line item?')) {
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
            productionLineItemListModel.total = parseInt(productionLineItemListModel.total) - 1;
            removeEntityFromGridRows(productionLineItemListModel, selectedRow);

        } else {
            showError(data.message);
        }
    }


    function SelectProductionLineItem(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production line item') == false) {
            return;
        }
        resetForm();

        showLoadingSpinner(true);
        var productionLineItemId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'invProductionLineItem', action: 'select')}?id="
                    + productionLineItemId,

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
            showProductionLineItem(data);
        }
    }

    function addProductionDetailsWitehMaterial(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'production line item') == false) {
            return;
        }
        showLoadingSpinner(true);
        var productionLineItemId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'invProductionDetails', action: 'show')}?productionLineItemId=" + productionLineItemId;
        $.history.load(formatLink(loc));

        return false;
    }

    function showProductionLineItem(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    var strUrl = "${createLink(controller:'invProductionLineItem', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (productionLineItemListModel) {
        $("#flex1").flexAddData(productionLineItemListModel);
    }

</script>
