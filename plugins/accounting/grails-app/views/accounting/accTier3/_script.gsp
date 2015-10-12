<script language="javascript">
    var dropDownAccType, dropDownTier1, dropDownTier2;
    var accTier3ListModel = false;
    $(document).ready(function () {
        onLoadAccTier3Page();
    });

    function onLoadAccTier3Page() {
        initializeForm($("#accTier3Form"), onSubmitAccTier3);
        var output =${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        } else {
            accTier3ListModel = output.accTier3List;
        }

        dropDownTier1 = initKendoDropdown($('#accTier1Id'), null, null, null);  // set empty tier 1 drop-down
        dropDownTier2 = initKendoDropdown($('#accTier2Id'), null, null, null);  // set empty tier 2 drop-down

        // update page title
        $(document).attr('title', "MIS - Create Tier-3");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accTier3/show");
    }

    function executePreCondition() {
        if (!validateForm($("#accTier3Form"))) {
            return false;
        }
        return true;
    }

    function onSubmitAccTier3() {
        if (executePreCondition() == false) {
            return false;
        }

        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accTier3', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accTier3', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#accTier3Form").serialize(),
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
                var newEntry = result.accTier3;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(accTier3ListModel.total);
                    var firstSerial = 1;

                    if (accTier3ListModel.rows.length > 0) {
                        firstSerial = accTier3ListModel.rows[0].cell[0];
                        regenerateSerial($(accTier3ListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    accTier3ListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        accTier3ListModel.rows.pop();
                    }

                    accTier3ListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(accTier3ListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(accTier3ListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(accTier3ListModel);
                }

                resetFormForCreate();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#accTier3Form"), dropDownAccType);
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        dropDownTier2.setDataSource(getKendoEmptyDataSource());
        dropDownTier1.value('');
        dropDownTier2.value('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function resetFormForCreate() {
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#isActive').attr('checked', false);
        $('#id').val('');
        $('#version').val('');
        $('#name').val('');
        $('#name').focus();
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
                    {display: "Account Type", name: "accTypeId", width: 180, sortable: false, align: "left"},
                    {display: "Tier-1", name: "accTier1Id", width: 180, sortable: false, align: "left"},
                    {display: "Tier-2", name: "accTier2Id", width: 180, sortable: false, align: "left"},
                    {display: "Active", name: "isActive", width: 180, sortable: true, align: "left"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/accTier3/select,/accTier3/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectAccTier3},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/accTier3/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteAccTier3},
                    </app:ifAllUrl>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Name", name: "name", width: 180, sortable: true, align: "left"},
                    {display: "Account Type", name: "accType", width: 180, sortable: true, align: "left"},
                    {display: "Tier-1", name: "accTier1", width: 180, sortable: true, align: "left"},
                    {display: "Tier-2", name: "accTier2", width: 180, sortable: true, align: "left"}
                ],
                sortname: "name",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Tier-3',
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

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            accTier3ListModel = null;
        } else {
            accTier3ListModel = data;
        }
        return data;
    }

    function deleteAccTier3(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var accTier3Id = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller:'accTier3', action: 'delete')}?id=" + accTier3Id,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'tier-3') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected tier-3?')) {
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
            accTier3ListModel.total = parseInt(accTier3ListModel.total) - 1;
            removeEntityFromGridRows(accTier3ListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectAccTier3(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'tier-3') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var inventoryId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'accTier3', action: 'select')}?id="
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
            showAccTier3(data);
        }
    }

    function showAccTier3(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownAccType.value(entity.accTypeId);
        dropDownTier1.setDataSource(data.accTier1List);
        dropDownTier1.value(entity.accTier1Id);
        dropDownTier2.setDataSource(data.accTier2List);
        dropDownTier2.value(entity.accTier2Id);
        $('#name').val(entity.name);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    var strUrl = "${createLink(controller:'accTier3', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (accTier3ListModel) {
        $("#flex1").flexAddData(accTier3ListModel);
    }

    function populateTier1List() {
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        dropDownTier2.setDataSource(getKendoEmptyDataSource());
        var accTypeId = dropDownAccType.value();
        if (accTypeId == '') {
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

    function populateTier2List() {
        var accTier1Id = dropDownTier1.value();
        if (accTier1Id == '') {
            dropDownTier2.setDataSource(getKendoEmptyDataSource());
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accTier2', action: 'getTier2ByAccTier1Id')}?accTier1Id=" + accTier1Id,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownTier2.setDataSource(getKendoEmptyDataSource());
                } else {
                    dropDownTier2.setDataSource(data.accTier2List);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

</script>
