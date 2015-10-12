<script language="javascript">
    var output =${output ? output : ''};
    var chartOfAccountListModel = false;
    var tier1Id, tier2Id, tier3Id, dropDownAccType, dropDownTier1, dropDownTier2, dropDownTier3,
            dropDownSource, dropDownSourceCategory, dropDownAccGroup, dropDownAccCustomGroup;

    $(document).ready(function () {
        onLoadChartOfAccountPage();
    });

    function onLoadChartOfAccountPage() {
        initializeForm($("#chartOfAccountForm"), onSubmitChartOfAccount);

        dropDownTier1 = initKendoDropdown($('#tier1'), null, null, null);   // set empty tier 1 drop-down
        dropDownTier2 = initKendoDropdown($('#tier2'), null, null, null);   // set empty tier 2 drop-down
        dropDownTier3 = initKendoDropdown($('#tier3'), null, null, null);   // set empty tier 3 drop-down
        dropDownSourceCategory = initKendoDropdown($('#sourceCategoryId'), null, null, null);   // set empty source category drop-down

        // update page title
        $(document).attr('title', "MIS - Create chart of account");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accChartOfAccount/show");

        if (output.isError) {
            showError(output.message);
        } else {
            chartOfAccountListModel = output.accChartOfAccountList;
            $('#codeSpan').text('(Auto Generated)');
        }
    }

    function onSubmitChartOfAccount() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'accChartOfAccount', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'accChartOfAccount', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#chartOfAccountForm").serialize(),
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
        if (!validateForm($("#chartOfAccountForm"))) {
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
                var newEntry = result.accChartOfAccount;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(chartOfAccountListModel.total);
                    var firstSerial = 1;

                    if (chartOfAccountListModel.rows.length > 0) {
                        firstSerial = chartOfAccountListModel.rows[0].cell[0];
                        regenerateSerial($(chartOfAccountListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    chartOfAccountListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        chartOfAccountListModel.rows.pop();
                    }

                    chartOfAccountListModel.total = ++previousTotal;
                    $("#flex1").flexAddData(chartOfAccountListModel);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(chartOfAccountListModel, newEntry.entity, 0);
                    $("#flex1").flexAddData(chartOfAccountListModel);
                }

                resetForm();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function printChartOfAccounts() {
        showLoadingSpinner(true);

        if (confirm('Do you want to download Chart of Accounts?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadChartOfAccounts')}";
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function resetAllDropDown() {
        dropDownAccType.value('');
        dropDownAccGroup.value('');
        dropDownAccCustomGroup.value('');
        dropDownSource.value('');
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        dropDownTier2.setDataSource(getKendoEmptyDataSource());
        dropDownTier3.setDataSource(getKendoEmptyDataSource());
        dropDownSourceCategory.setDataSource(getKendoEmptyDataSource());
        dropDownSourceCategory.value('');
        dropDownTier1.value('');
        dropDownTier2.value('');
        dropDownTier3.value('');
    }

    function resetForm() {
        tier1Id = '';
        tier2Id = '';
        tier3Id = '';
        resetAllDropDown();
        clearForm($("#chartOfAccountForm"), dropDownAccType);
        $('#codeSpan').text('(Auto Generated)');
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
                    {display: "Code", name: "code", width: 70, sortable: false, align: "left" },
                    {display: "Head Name", name: "description", width: 150, sortable: false, align: "left" },
                    {display: "Source", name: "source_name", width: 120, sortable: true, align: "left" },
                    {display: "Source Category", name: "sourceCategoryId", width: 120, sortable: false, align: "left" },
                    {display: "Type", name: "accTypeId", width: 120, sortable: false, align: "left" },
                    {display: "Group", name: "accGroupId", width: 120, sortable: false, align: "left"},
                    {display: "Custom Group", name: "accCustomGroupId", width: 150, sortable: false, align: "left"},
                    {display: "Active", name: "isActive", width: 40, sortable: false, align: "center"}
                ],
                buttons: [
                    <app:ifAllUrl urls="/accChartOfAccount/select,/accChartOfAccount/update">
                    {name: 'Edit', bclass: 'edit', onpress: selectChartOfAccount},
                    </app:ifAllUrl>
                    <app:ifAllUrl urls="/accChartOfAccount/delete">
                    {name: 'Delete', bclass: 'delete', onpress: deleteChartOfAccount},
                    </app:ifAllUrl>
                    <sec:access url="/accReport/showLedger">
                    {name: 'Ledger', bclass: 'view', onpress: viewLedgerReport},
                    </sec:access>
                    {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                    {separator: true}
                ],
                searchitems: [
                    {display: "Head Name", name: "coa.description", width: 30, sortable: false, align: "left" },
                    {display: "Code", name: "coa.code", width: 30, sortable: false, align: "left" }
                ],
                sortname: "coa.code",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'All Chart of Accounts',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 30,
                afterAjax: function (XMLHttpRequest, textStatus) {
                    afterAjaxError(XMLHttpRequest, textStatus);
                    showLoadingSpinner(false);
                },
                preProcess: onLoadListJSON
            }
    );

    function viewLedgerReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' chart of account') == false) {
            return;
        }
        showLoadingSpinner(true);

        var coaId = getSelectedIdFromGrid($('#flex1'));

        var loc = "${createLink(controller:'accReport', action: 'showLedger')}?coaId=" + coaId;
        $.history.load(formatLink(loc));
        return false;
    }

    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            chartOfAccountListModel = null;
        } else {
            chartOfAccountListModel = data;
        }
        return data;
    }

    function deleteChartOfAccount(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var chartOfAccountId = getSelectedIdFromGrid($('#flex1'));

        $.ajax({
            url: "${createLink(controller: 'accChartOfAccount', action: 'delete')}?id=" + chartOfAccountId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'chart of account') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected chart of account?')) {
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
            resetForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            chartOfAccountListModel.total = parseInt(chartOfAccountListModel.total) - 1;
            removeEntityFromGridRows(chartOfAccountListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function selectChartOfAccount(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), ' chart of account') == false) {
            return;
        }

        resetForm();
        showLoadingSpinner(true);
        var chartOfAccountId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller: 'accChartOfAccount', action: 'select')}?id=" + chartOfAccountId,
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
            showChartOfAccount(data);
        }
    }

    function showChartOfAccount(data) {
        var entity = data.entity;

        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#codeSpan').text(entity.code);
        $('#code').val(entity.code);
        $('#description').val(entity.description);

        dropDownSource.value(entity.accSourceId);
        dropDownTier1.setDataSource(data.accTier1List);
        dropDownTier2.setDataSource(data.accTier2List);
        dropDownTier3.setDataSource(data.accTier3List);

        dropDownTier1.value(entity.tier1);
        dropDownAccType.value(entity.accTypeId);

        if (entity.tier2 <= 0) {
            dropDownTier2.value('');
        } else {
            dropDownTier2.value(entity.tier2);
        }
        if (entity.tier3 <= 0) {
            dropDownTier3.value('');
        } else {
            dropDownTier3.value(entity.tier3);
        }
        if (entity.accGroupId <= 0) {
            dropDownAccGroup.value('');
        } else {
            dropDownAccGroup.value(entity.accGroupId);
        }

        dropDownSourceCategory.setDataSource(getKendoEmptyDataSource());
        dropDownSourceCategory.setDataSource(data.sourceCategoryList);
        if (entity.sourceCategoryId > 0) {
            dropDownSourceCategory.value(entity.sourceCategoryId);
        }

        dropDownAccCustomGroup.value(entity.accCustomGroupId);
        $('#isActive').attr('checked', entity.isActive);
        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    var strUrl = "${createLink(controller: 'accChartOfAccount', action: 'list')}";
    $("#flex1").flexOptions({url: strUrl});

    if (chartOfAccountListModel) {
        $("#flex1").flexAddData(chartOfAccountListModel);
    }

    function populateTier1List() {
        var accTypeId = dropDownAccType.value();

        dropDownTier1.value('');
        dropDownTier2.value('');
        dropDownTier3.value('');
        dropDownTier1.setDataSource(getKendoEmptyDataSource());
        dropDownTier2.setDataSource(getKendoEmptyDataSource());
        dropDownTier3.setDataSource(getKendoEmptyDataSource());

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
        var tier1 = dropDownTier1.value();

        dropDownTier2.value('');
        dropDownTier3.value('');
        dropDownTier2.setDataSource(getKendoEmptyDataSource());
        dropDownTier3.setDataSource(getKendoEmptyDataSource());

        if (tier1 == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accTier2', action: 'getTier2ByAccTier1Id')}?accTier1Id=" + tier1,
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

    function populateTier3List() {
        var tier2 = dropDownTier2.value();

        dropDownTier3.value('');
        dropDownTier3.setDataSource(getKendoEmptyDataSource());
        if (tier2 == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accTier3', action: 'getTier3ByAccTier2Id')}?accTier2Id=" + tier2,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownTier3.setDataSource(getKendoEmptyDataSource());
                } else {
                    dropDownTier3.setDataSource(data.accTier3List);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function populateSourceCategoryList() {
        dropDownSourceCategory.value('');
        dropDownSourceCategory.setDataSource(getKendoEmptyDataSource());
        var sourceId = dropDownSource.value();
        if (sourceId == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accChartOfAccount', action: 'getSourceCategoryByAccSource')}?sourceId=" + sourceId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                    dropDownSourceCategory.setDataSource(getKendoEmptyDataSource());
                } else {
                    dropDownSourceCategory.setDataSource(data.sourceCategoryList);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }
</script>