<script language="javascript" type="text/javascript">
    var output = false;
    var accLcListModel = false;
    var itemTypeId, itemId, supplierId, dropDownProject, dropDownItemType, dropDownItem, dropDownSupplier, amount;

    $(document).ready(function () {
        onLoadAccLcPage();
    });

    function onLoadAccLcPage() {
        initializeForm($("#accLcForm"), onSubmitAccLc);
        output = ${output ? output : ''};
        dropDownItem = initKendoDropdown($('#itemId'), null, null, null);

        $('#amount').kendoNumericTextBox({
            min: 0,
            max: 999999999999.99,
            format: "#.##",
            step: 100
        });
        amount = $("#amount").data("kendoNumericTextBox");

        if (output.isError) {
            showError(data.message);
        } else {
            accLcListModel = output.accLcList;
        }
        initGrid();
        populateFlex1();

        //update page title
        $(document).attr('title', "Accounting - Create LC");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accLc/show");
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, hide: true, align: "right"},
                        {display: "LC No", name: "lc_no", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 120, sortable: false, align: "right"},
                        {display: "Bank", name: "bank", width: 150, sortable: false, align: "left"},
                        {display: "Item", name: "itemId", width: 250, sortable: false, align: "left"},
                        {display: "Supplier", name: "supplierId", width: 300, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accLc/select,/accLc/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccLc},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accLc/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccLc},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accLc/list">
                        {name: 'Clear Result', bclass: 'clear-results', onpress: reloadGrid},
                        </app:ifAllUrl>
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "LC No", name: "alc.lc_no", width: 30, sortable: false, align: "left" },
                        {display: "Item", name: "it.name", width: 50, sortable: false, align: "left" },
                        {display: "Supplier", name: "sup.name", width: 50, sortable: false, align: "left" },
                        {display: "Bank", name: "alc.bank", width: 50, sortable: false, align: "left" }
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All LC List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25, 30],
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateGridForAccLc
                }
        );
    }

    function selectAccLc(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' LC') == false) {
            return;
        }

        resetAccLcForm();
        showLoadingSpinner(true);
        var accLcId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accLc', action: 'select')}?id=" + accLcId,
            success: executePostConditionForEdit,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }


    function resetAccLcForm() {
        clearForm($("#accLcForm"), $("#lcNo"));
        dropDownItem.setDataSource(getKendoEmptyDataSource());
        dropDownItem.value('');

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showAccLc(data);
        }
    }

    function showAccLc(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownItemType.value(data.item.itemTypeId);
        dropDownItem.setDataSource(data.itemList);
        dropDownItem.value(entity.itemId);
        dropDownSupplier.value(entity.supplierId);
        $('#lcNo').val(entity.lcNo);
        amount.value(entity.amount);
        $('#bank').val(entity.bank);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteAccLc(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accLcId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accLc', action:'delete')}?id=" + accLcId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'LC') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected LC?')) {
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
            resetAccLcForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            accLcListModel.total = parseInt(accLcListModel.total) - 1;
            removeEntityFromGridRows(accLcListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function populateGridForAccLc(data) {
        if (data.isError) {
            showError(data.message);
            accLcListModel = getEmptyGridModel();
        } else {
            accLcListModel = data.accLcList;
        }
        $("#flex1").flexAddData(accLcListModel);
    }

    function onSubmitAccLc() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accLc', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accLc', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accLcForm").serialize(),
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

    function executePreCondition() {
        if (!validateForm($("#accLcForm"))) {
            return false;
        }
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

                    var previousTotal = parseInt(accLcListModel.total);
                    var firstSerial = 1;

                    if (accLcListModel.rows.length > 0) {
                        firstSerial = accLcListModel.rows[0].cell[0];
                        regenerateSerial($(accLcListModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;
                    accLcListModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accLcListModel.rows.pop();
                    }

                    accLcListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accLcListModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(accLcListModel, newEntry, 0);
                    $("#flex1").flexAddData(accLcListModel);
                }

                resetAccLcForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function populateFlex1() {
        var strUrl = "${createLink(controller:'accLc', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accLcListModel) {
            $("#flex1").flexAddData(accLcListModel);
        }
    }

    // update item drop down list by item type id
    function updateItemList() {
        var itemTypeId = dropDownItemType.value();
        if (itemTypeId == '') {
            dropDownItem.setDataSource(getKendoEmptyDataSource());
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller: 'item', action: 'listItemByItemTypeId')}?itemTypeId=" + itemTypeId,
            success: function (data) {
                updateItemListForDropDown(data);
            }, complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        return true;
    }

    function updateItemListForDropDown(data) {
        if (data.isError) {
            showError(data.message);
            dropDownItem.setDataSource(getKendoEmptyDataSource());
        } else {
            dropDownItem.setDataSource(data.itemList);
        }
    }

</script>