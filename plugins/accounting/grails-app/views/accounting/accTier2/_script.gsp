<script language="javascript">
    var dropDownAccType, dropDownTier1;
    var accTier2ListModel = false;

    $(document).ready(function () {
        onLoadAccTier2Page();
    });

    function onLoadAccTier2Page() {
        initializeForm($("#accTier2Form"), onSubmitAccTier2);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accTier2ListModel = output.lstTier2;
        }
        dropDownTier1 = initKendoDropdown($('#accTier1Id'), null, null, null);  // set empty tier 1 drop-down

        initFlexGrid();
        populateFlexGrid();

        // update page title
        $(document).attr('title', "MIS - Create Tier-2");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accTier2/show");
    }

    function populateTier1List() {
        var accTypeId = dropDownAccType.value();
        if (accTypeId == '') {
            dropDownTier1.setDataSource(getKendoEmptyDataSource());
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accTier1', action: 'getTier1ByAccTypeId')}?accTypeId=" + accTypeId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownTier1.setDataSource(getKendoEmptyDataSource());
                } else {
                    dropDownTier1.setDataSource(data.lstTier1);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreCondition() {
        if (!validateForm($("#accTier2Form"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccTier2() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accTier2', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accTier2', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accTier2Form").serialize(),
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
                var newEntry = result.accTier2;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accTier2ListModel.total);
                    var firstSerial = 1;

                    if (accTier2ListModel.rows.length > 0) {
                        firstSerial = accTier2ListModel.rows[0].cell[0];
                        regenerateSerial($(accTier2ListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accTier2ListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accTier2ListModel.rows.pop();
                    }

                    accTier2ListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accTier2ListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accTier2ListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accTier2ListModel);
                }

                resetFormForCreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accTier2Form"), dropDownAccType);
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        dropDownTier1.value('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetFormForCreate() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#isActive').attr('checked', false);
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        $('#id').val('');
        $('#version').val('');
        $('#name').val('');
        $('#name').focus();
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
                        {display: "Tier-1", name: "accTier1Id", width: 180, sortable: false, align: "left"},
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Account Type", name: "accTypeId", width: 180, sortable: false, align: "left"},
                        {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accTier2/select,/accTier2/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectAccTier2},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accTier2/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAccTier2},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                        {display: "Account Type", name: "accType", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Tier-2',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    preProcess: onLoadListJSON
                }
        );
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accTier2ListModel = null;
        } else {
            accTier2ListModel = data;
        }
        return data;
    }

    function deleteAccTier2(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accTier2Id = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accTier2', action: 'delete')}?id=" + accTier2Id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'tier-2') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected Tier-2?')) {
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
            accTier2ListModel.total = parseInt(accTier2ListModel.total) - 1;
            removeEntityFromGridRows(accTier2ListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectAccTier2(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' tier-2') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accTier2', action: 'select')}?id="
                    + inventoryId,
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
            showAccTier2(data);
        }
    }

    function showAccTier2(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownAccType.value(entity.accTypeId);
        dropDownTier1.setDataSource(data.lstTier1);
        dropDownTier1.value(entity.accTier1Id);
        $('#name').val(entity.name);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateFlexGrid() {
        var strUrl = "${createLink(controller:'accTier2', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (accTier2ListModel) {
            $("#flex1").flexAddData(accTier2ListModel);
        }
    }

</script>
