<script language="javascript">
    var dropDownSupplierType;
    var supplierListModel = false;

    $(document).ready(function () {
        onLoadSupplierPage();
    });
    function onLoadSupplierPage() {

        // initialize form with kendo validator & bind onSubmit method
        initializeForm($("#supplierForm"), onSubmitSupplier);

        var output =${output ? output : ''};

        if (output.isError) {
            showError(output.message);
        } else {
            supplierListModel = output.supplierList;
        }

        // update page title
        $(document).attr('title', "MIS - Create Supplier");
        loadNumberedMenu(MENU_ID_APPLICATION, "#supplier/show");

    }
    function executePreCondition() {
        if (!validateForm($("#supplierForm"))) {  // check kendo validation
            return false;
        }
        return true;
    }

    function onSubmitSupplier() {

        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'supplier', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'supplier', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#supplierForm").serialize(),
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


    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.supplier;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created
                    var previousTotal = parseInt(supplierListModel.total);

                    // re-arranging serial
                    var firstSerial = 1;
                    if (supplierListModel.rows.length > 0) {
                        firstSerial = supplierListModel.rows[0].cell[0];
                        regenerateSerial($(supplierListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    supplierListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        supplierListModel.rows.pop();
                    }

                    supplierListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(supplierListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(supplierListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(supplierListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        // clear all errors, validation messages & form values and bind onFocus method
        clearForm($("#supplierForm"), dropDownSupplierType);
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
                    {display: "Supplier Type", name: "supplier_type", width: 120, sortable: true, align: "left"},
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Account Name", name: "accountName", width: 200, sortable: true, align: "left"},
                    {display: "Address", name: "address", width: 200, sortable: true, align: "left"},
                    {display: "Account No", name: "bankAccount", width: 120, sortable: true, align: "left"} ,
                    {display: "Item(s)", name: "itemCount", width: 80, sortable: false, align: "right"}

                ],
                buttons: [
                    <app:ifAllUrl urls="/supplier/select,/supplier/update">
                    {name: 'Edit', bclass: 'edit', onpress: editSupplier},
                    </app:ifAllUrl>
                    <sec:access url="/supplier/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteSupplier},
                    </sec:access>
                    <app:ifAllUrl urls="/supplierItem/show">
                    {name: 'Item(s)', bclass: 'addItem', onpress: addMaterial},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ], searchitems: [
                {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
            ],
                sortname: "name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Suppliers',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 20,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadSupplierListJSON
            }
    );

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function addMaterial(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier') == false) {
            return;
        }
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'supplierItem', action: 'show')}?supplierId=" + supplierId;
        $.history.load(formatLink(loc));

        return false;
    }

    function addWork(com, grid) {
        var ids = $('.trSelected', grid);
        if (!executePreConditionForAddDetails(ids)) {
            return false;
        }
        showLoadingSpinner(true);
        var supplierId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var loc = "${createLink(controller: 'supplierItem', action: 'showWithWork')}?supplierId=" + supplierId;
        $.post(loc, function (data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        });
        return false;
    }

    function onLoadSupplierListJSON(data) {
        if (data.isError) {
            showError(data.message);
            supplierListModel = null;
        } else {
            supplierListModel = data;
        }
        return data;
    }


    function deleteSupplier(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }

        showLoadingSpinner(true);

        var supplierId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'supplier', action: 'delete')}?id=" + supplierId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Supplier?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            supplierListModel.total = parseInt(supplierListModel.total) - 1;
            removeEntityFromGridRows(supplierListModel, selectedRow);

        } else {
            // show delete error
            showError(data.message);
        }
    }


    function editSupplier(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'supplier') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var supplierId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'supplier', action: 'select')}?id="
                    + supplierId,
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
            showSupplier(data);
        }
    }

    function showSupplier(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#name').val(entity.name);
        dropDownSupplierType.value(entity.supplierTypeId);
        $('#accountName').val(entity.accountName);
        $('#address').val(entity.address);
        $('#bankName').val(entity.bankName);
        $('#bankAccount').val(entity.bankAccount);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    <%-- End: Edit operation --%>

    <sec:access url="/supplier/list">
    var strUrl = "${createLink(controller: 'supplier', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (supplierListModel) {
        $("#flex1").flexAddData(supplierListModel);
    }
    </sec:access>
</script>
