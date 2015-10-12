<script type="text/javascript">
    var dropDownBank, dropDownBankBranch, dropDownDistrict, dropDownInstrument;
    var instrumentMappingListModel = false;
    var entityBankId, entityDistrictId, entityBankBranchId;

    $(document).ready(function () {
        onLoadInstrumentMapping();
    });

    function onLoadInstrumentMapping() {
        var output =${output ? output : ''};

        initializeForm($("#instrumentMappingForm"), onSubmitInstrumentMapping);
        initializeForm($("#logicEvaluationForm"), onSubmitLogicEvaluationForm);
        setDefaultValueForLogicEvaluation();
        dropDownBankBranch = initKendoDropdown($('#bankBranchId'), null, null, null);
        if (output.isError) {
            showError(output.message);
        } else {
            instrumentMappingListModel = output.gridObj;
        }
        initFlex();
        populateFlex();
        $(document).attr('title', "ARMS - Create Purchase Instrument Mapping");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsPurchaseInstrumentMapping/show");
    }
    function onSubmitLogicEvaluationForm() {
        if (executeCommonPreConditionForSelect($('#flex'), 'instrument mapping') == false) {
            return false
        }
        $('#amount').val($.trim($('#amount').val()));
        if ($('#amount').val() == '') {
            showError('Amount is needed to evaluate.')
            return false;
        }
        var id = getSelectedIdFromGrid($('#flex'));
        var amount = $('#amount').val();
        var params = "?id=" + id + "&amount=" + amount;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'rmsPurchaseInstrumentMapping', action:  'evaluateLogic')}" + params,
            success: executePostConditionForEvaluateLogic,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }
    function executePostConditionForEvaluateLogic(data) {
        if (data.isError == true) {
            showError(data.message);
            resetEvaluateForm();
            return false;
        }
        $('#lblCommission').text(data.comm);
        $('#lblPnt').text(data.pNt);
        $('#lblPostage').text(data.postage);
        $('#lblServiceCharge').text(data.serviceCharge);
        $('#lblVat').text(data.vat);
        $('#lblVatOnPnt').text(data.vatOnPnt);
        $('#lblTotal').text(data.total);
        return;
    }
    function setDefaultValueForLogicEvaluation() {
        $('#lblCommission').text('0');
        $('#lblPnt').text('0');
        $('#lblPostage').text('0');
        $('#lblServiceCharge').text('0');
        $('#lblVat').text('0');
        $('#lblVatOnPnt').text('0');
        $('#lblTotal').text('0');
        return;
    }
    function onSubmitInstrumentMapping() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'rmsPurchaseInstrumentMapping', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'rmsPurchaseInstrumentMapping', action: 'update')}";
        }
        $.ajax({
            type: 'post',
            data: $("#instrumentMappingForm").serialize(),
            url: actionUrl,
            success: function (data) {
                executePostCondition(data);
            },
            complete: function () {
                setButtonDisabled($('#create'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function executePreCondition() {
        if (!validateForm($("#instrumentMappingForm"))) {
            return false;
        }
        if ($.trim($('#commissionScript').val()).isEmpty()) {
            showError("Please enter valid commission logic");
            return false;
        }
        return true;
    }

    function executePostCondition(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        }
        else {
            try {
                var newEntry = result;
                if ($('#id').val().isEmpty() && newEntry.entity != null) {

                    var previousTotal = parseInt(instrumentMappingListModel.total);
                    var firstSerial = 1;

                    if (instrumentMappingListModel.rows.length > 0) {
                        firstSerial = instrumentMappingListModel.rows[0].cell[0];
                        regenerateSerial($(instrumentMappingListModel.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;

                    instrumentMappingListModel.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex').countEqualsResultPerPage(previousTotal)) {
                        instrumentMappingListModel.rows.pop();
                    }

                    instrumentMappingListModel.total = ++previousTotal;
                    $("#flex").flexAddData(instrumentMappingListModel);

                } else if (newEntry.entity != null) {
                    updateListModel(instrumentMappingListModel, newEntry.entity, 0);
                    $("#flex").flexAddData(instrumentMappingListModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function resetForm() {
        clearForm($("#instrumentMappingForm"), $('#name'));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }


    function initFlex() {
        $("#flex").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "Instrument", name: "instrument", width: 95, sortable: false, align: "left"},
                        {display: "Bank", name: "bank", width: 190, sortable: false, align: "left"},
                        {display: "Bank Branch", name: "bankBranch", width: 120, sortable: false, align: "left"},
                        {display: "District", name: "district", width: 115, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/rmsPurchaseInstrumentMapping/select,/rmsPurchaseInstrumentMapping/update">
                        {name: 'Edit', bclass: 'edit', onpress: selectInstrumentMapping},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/rmsPurchaseInstrumentMapping/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteInstrumentMapping},
                        </app:ifAllUrl>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Purchase Instrument Mapping',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function () {
                        afterAjaxError();
                        showLoadingSpinner(false);
                    },
                    customPopulate: onLoadListJSON
                }
        );
    }

    function onLoadListJSON(data) {
        if (data.isError) {
            showError(data.message);
            instrumentMappingListModel = getEmptyGridModel();
        } else {
            instrumentMappingListModel = data;
        }
        $("#flex").flexAddData(instrumentMappingListModel);
    }

    function selectInstrumentMapping(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex'), 'instrument mapping') == false) {
            return;
        }
        resetForm();
        showLoadingSpinner(true);
        var id = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'rmsPurchaseInstrumentMapping', action: 'select')}?id=" + id,
            success: executePostConditionForEdit,
            complete: function () {
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
            showInstrumentMapping(data);
        }
    }

    function showInstrumentMapping(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#commissionScript').val(entity.commissionScript);
        dropDownBank.value(entity.bankId);
        $('#districtId').attr('bank_id',  entity.bankId);
        $('#districtId').attr('default_value',  entity.districtId);
        $('#districtId').reloadMe();
        $('#bankBranchId').attr('bank_id', entity.bankId);
        $('#bankBranchId').attr('district_id', entity.districtId);
        $('#bankBranchId').attr('default_value', entity.bankBranchId);
        $('#bankBranchId').reloadMe();
        dropDownInstrument.value(entity.instrumentTypeId);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function deleteInstrumentMapping(com, grid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        showLoadingSpinner(true);
        var instrumentMappingId = getSelectedIdFromGrid($('#flex'));
        $.ajax({
            url: "${createLink(controller:'rmsPurchaseInstrumentMapping', action:  'delete')}?id=" + instrumentMappingId,
            success: executePostConditionForDelete,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flex'), 'instrument mapping') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected instrument mapping?')) {
            return false;
        }
        return true;
    }

    function executePostConditionForDelete(data) {
        if (data.isError == false) {
            var selectedRow = null;
            $('.trSelected', $('#flex')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetForm();
            $('#flex').decreaseCount(1);
            showSuccess(data.message);
            instrumentMappingListModel.total = parseInt(instrumentMappingListModel.total) - 1;
            removeEntityFromGridRows(instrumentMappingListModel, selectedRow);
        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        $('#flex').flexOptions({query: ''}).flexReload();
    }

    function populateFlex() {
        var strUrl = "${createLink(controller:'rmsPurchaseInstrumentMapping',action:  'list')}";
        $("#flex").flexOptions({url: strUrl});

        if (instrumentMappingListModel) {
            $("#flex").flexAddData(instrumentMappingListModel);
        }
    }

    function onChangeBank() {
        dropDownDistrict.value('');
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch, null));
        dropDownBankBranch.value('');
        var bankId = dropDownBank.value();
        $('#districtId').attr('bank_id', bankId);
        $('#districtId').reloadMe();
    }

    function updateBranchList() {
        var bankId = dropDownBank.value();
        var districtId = dropDownDistrict.value();
        if (districtId == '') {
            dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch, null));
            return false;
        }
        $('#bankBranchId').attr('bank_id', bankId);
        $('#bankBranchId').attr('district_id', districtId);
        $('#bankBranchId').reloadMe();
    }

    function resetEvaluateForm() {
        $('#amount').val('');
        setDefaultValueForLogicEvaluation();
        return true;
    }

</script>