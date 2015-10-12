<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility" %>
<script type="text/javascript">
    var gridModelVoucher, gridModelDr, gridModelCr;
    var sourceTypeNone, voucherDate, voucherTypeId;
    var dropDownInstrumentType, dropDownProject, dropDownSourceDr, dropDownSourceCr, dropDownCoaVoucherType,
            dropDownDivisionDr, dropDownDivisionCr;
    var amountDr, amountCr;

    $(document).ready(function () {
        onLoadVoucher();
        bindEventsWithControl();
    });

    function onLoadVoucher() {
        initializeForm($("#frmVoucher"), onSubmitVoucher);
        initializeForm($("#frmDebit"), onSubmitDebit);
        initializeForm($("#frmCredit"), onSubmitCredit);

        initFlexDebit();
        initFlexCredit();
        initFlexJournal();
        initFlexVoucher();
        initFlexSearchCOA();

        // populate form objects
        populateOnLoadObjects();

        // update page title
        $(document).attr('title', "MIS - Create Voucher (Pay Cash)");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accVoucher/showPayCash");
    }

    function bindEventsWithControl() {
        //Debit event bind
        $('#coaCodeDr').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                searchChartOfAccForDr();
                return false;
            }
        });

        $('#addDr').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                onSubmitDebit();
                $('#coaCodeDr').focus();
                return false;
            }
        });

        //Credit event bind
        $('#addCr').bind('keypress', function (e) {
            if ((e.keyCode || e.which) == 13) {
                onSubmitCredit();
                return false;
            }
        });
    }

    function populateOnLoadObjects() {
        gridModelVoucher = null;
        var output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
            return;
        }
        gridModelVoucher = output.gridObjVoucher;
        setUrlVoucherGrid();

        // initialize Dr Cr grid models
        gridModelDr = getEmptyModelForDrCr();
        gridModelCr = getEmptyModelForDrCr();

        dropDownSourceDr = initKendoDropdown($('#sourceIdDr'), null, null, null);
        dropDownSourceCr = initKendoDropdown($('#sourceIdCr'), null, null, null);
        dropDownDivisionDr = initKendoDropdown($('#divisionIdDr'), null, null, null);
        dropDownDivisionCr = initKendoDropdown($('#divisionIdCr'), null, null, null);
        dropDownCoaVoucherType = initKendoDropdown($('#coaVoucherType'), "customName", null, output.lstCreditCoaVoucherType);
        dropDownCoaVoucherType.value('');

        $('#amountDr').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        amountDr = $("#amountDr").data("kendoNumericTextBox");

        $('#amountCr').kendoNumericTextBox({
            min: 0,
            max: 999999999999.9999,
            format: "#.####"
        });
        amountCr = $("#amountCr").data("kendoNumericTextBox");

        voucherTypeId = $('#voucherTypeId').val();
        sourceTypeNone = $('#sourceTypeNoneId').val();
        voucherDate = $('#voucherDate').val();
    }

    function getEmptyModelForDrCr() {
        var emptyModel = new Object();
        emptyModel.page = 1;
        emptyModel.total = 0;
        emptyModel.rows = new Array();
        return emptyModel;
    }

    function initFlexDebit() {
        $("#flexDebit").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "coaId", width: 20, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 50, sortable: false, align: "left"},
                        {display: "Head Name", name: "headNameDr", width: 200, sortable: true, align: "left"},
                        {display: "Amount", name: "amountDr", width: 80, sortable: true, align: "right"},
                        {display: "Particulars", name: "particularsDr", width: 150, sortable: true, align: "left"},
                        {display: "Source Name", name: "sourceName", width: 200, align: "left"},
                        {display: "Source Type", name: "sourceTypeId", width: 20, align: "left", hide: true},
                        {display: "Source Id", name: "sourceId", width: 20, align: "left", hide: true},
                        {display: "Serial", name: "rowId", width: 20, align: "right", hide: true},
                        {display: "Division", name: "divisionIdDr", width: 20, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editDebit},
                        {name: 'Delete', bclass: 'delete', onpress: deleteDebit},
                        {name: 'Move Up', bclass: 'up', onpress: moveUpDebit},
                        {name: 'Move Down', bclass: 'down', onpress: moveDownDebit}
                    ],
                    searchitems: [
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    singleSelect: true,
                    title: false,
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: 80,
                    resizable: false,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function initFlexCredit() {
        $("#flexCredit").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "coaId", width: 20, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 50, sortable: false, align: "left"},
                        {display: "Head Name", name: "headNameDr", width: 200, sortable: true, align: "left"},
                        {display: "Amount", name: "amountDr", width: 80, sortable: true, align: "right"},
                        {display: "Particulars", name: "particularsDr", width: 150, sortable: true, align: "left"},
                        {display: "Source Name", name: "sourceName", width: 200, align: "left"},
                        {display: "Source Type", name: "sourceTypeId", width: 20, align: "left", hide: true},
                        {display: "Source Id", name: "sourceId", width: 20, align: "left", hide: true},
                        {display: "Serial", name: "rowId", width: 20, align: "right", hide: true},
                        {display: "Division", name: "divisionIdCr", width: 20, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editCredit},
                        {name: 'Delete', bclass: 'delete', onpress: deleteCredit},
                        {name: 'Move Up', bclass: 'up', onpress: moveUpCredit},
                        {name: 'Move Down', bclass: 'down', onpress: moveDownCredit}
                    ],
                    searchitems: [
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    singleSelect: true,
                    title: false,
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: 80,
                    resizable: false,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        )
    }

    function initFlexJournal() {
        $("#flexJournal").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Code", name: "code", width: 80, sortable: true, align: "left"},
                        {display: "Head Name", name: "headNameDr", width: 200, sortable: true, align: "left"},
                        {display: "Debit", name: "amountDr", width: 80, sortable: true, align: "right"},
                        {display: "Credit", name: "amountCr", width: 80, sortable: true, align: "right"}
                    ],

                    searchitems: [
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    singleSelect: true,
                    title: false,
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: 250,
                    resizable: false,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function initFlexSearchCOA() {
        $("#flexSearchCOA").flexigrid
        (
                {
                    url: "${createLink(controller: 'accChartOfAccount', action: 'listForVoucher')}",
                    dataType: 'json',
                    colModel: [
                        {display: "ID", name: "coaId", width: 10, sortable: false, align: "right", hide: true},
                        {display: "Code", name: "code", width: 60, sortable: true, align: "left"},
                        {display: "Head Name", name: "description", width: 200, sortable: true, align: "left"},
                        {display: "Source Id", name: "accSourceTypeId", width: 60, align: "left", hide: true}
                    ],
                    buttons: [
                        {name: 'Debit', bclass: 'debit', onpress: addInDebit},
                        <sec:access url="/accReport/showLedger">
                        {name: 'Ledger', bclass: 'view', onpress: viewLedgerReport},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadCoaGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "ALL", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Search Chart Of Account',
                    useRp: false,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getFullGridHeight() - 50,
                    resizable: false,
                    isSearchOpen: true,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function initFlexVoucher() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Id", name: "id", width: 30, sortable: false, align: "right", hide: "true"},
                        {display: "Trace No", name: "id", width: 100, sortable: false, align: "left"},
                        {display: "Amount", name: "name", width: 120, sortable: false, align: "right"},
                        {display: "Dr Count", name: "drCount", width: 50, sortable: false, align: "right"},
                        {display: "Cr Count", name: "crCount", width: 50, sortable: false, align: "right"},
                        {display: "Voucher Date", name: "voucher_date", width: 100, sortable: true, align: "left"},
                        {display: "Posted", name: "isPosted", width: 40, sortable: false, align: "left"},
                        {display: "Instrument No", name: "instrumentId", width: 90, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/accVoucher/select,/accVoucher/update">
                        {name: 'Edit', bclass: 'edit', onpress: editVoucher},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accVoucher/deleteVoucher">
                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_CFO}">
                        {name: 'Move to Trash', bclass: 'delete', onpress: cancelVoucher},
                        </app:hasRoleType>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accVoucher/postVoucher">
                        {name: 'Post', bclass: 'post', onpress: postVoucher},
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accVoucher/unPostedVoucher">
                        {name: 'Unpost', bclass: 'unPost', onpress: unPostVoucher},
                        </app:ifAllUrl>
                        <sec:access url="/accReport/showVoucher">
                        {name: 'Report', bclass: 'report', onpress: viewVoucherReport},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadVoucherGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Trace No", name: "traceNo", width: 180, sortable: true, align: "left"},
                        {display: "Amount", name: "amount", width: 180, sortable: true, align: "left"},
                        {display: "Instrument No", name: "instrumentId", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Vouchers',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    customPopulate: customPopulateVoucherGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function cancelVoucher(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return;
        }
        var voucherTrace = $(ids[0]).find('td').eq(2).find('div').text();
        var isPostedVoucher = $(ids[ids.length - 1]).find('td').eq(7).find('div').text();

        if (isPostedVoucher == 'YES') {
            showError("Posted voucher could not be moved to Trash");
            return false;
        }

        $('#lblAccCancelVoucherTrace').text(voucherTrace);    // Set Voucher Trace. in Modal form
        $('#cancelConfirmationModalVoucher').modal('show');    // show Modal
        return false;
    }

    // this func. will be called from Modal form
    function accProcessCancelVoucher() {
        var ids = $('.trSelected', $('#flex1'));
        var voucherId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var cancelReason = $('#txtAccVoucherCancelReason').val();
        if (cancelReason === null || cancelReason === false) { // Canceled the action
            return false;
        }
        if (cancelReason === '') {
            showError("Please write down proper cancellation reason");
            return false;
        }

        $.ajax({
            url: "${createLink(controller:'accVoucher', action:  'cancelVoucher')}?id=" + voucherId + "&reason=" + cancelReason,
            success: executePostConditionForCancel,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    // post condition for cancel Voucher
    function executePostConditionForCancel(data) {
        $('#txtAccVoucherCancelReason').val('');
        $('#lblAccCancelVoucherTrace').text('');    // Clean Voucher No. in Modal form
        $('#cancelConfirmationModalVoucher').modal('hide');

        if (data.moved == true) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            $('#flex1').decreaseCount(1);
            gridModelVoucher.total = parseInt(gridModelVoucher.total) - 1;
            removeEntityFromGridRows(gridModelVoucher, selectedRow);
        }
        showSuccess(data.message);
    }

    function accCleanVoucherCancelForm() {
        $('#lblAccCancelVoucherTrace').text('');    // Clean Voucher Trace. in Modal form
        $('#txtAccVoucherCancelReason').val('');       // clean textArea
    }

    //view ledger report
    function viewLedgerReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexSearchCOA'), 'chart of account') == false) {
            return;
        }
        showLoadingSpinner(true);

        var coaId = getSelectedIdFromGrid($('#flexSearchCOA'));
        var loc = "${createLink(controller:'accReport', action: 'showLedger')}?coaId=" + coaId;
        $.history.load(formatLink(loc));
        return false;
    }

    function customPopulateVoucherGrid(data) {
        if (data.isError) {
            showError(data.message);
            gridModelVoucher = getEmptyGridModel();
        } else {
            gridModelVoucher = data;
        }
        $("#flex1").flexAddData(gridModelVoucher);
    }

    function editVoucher(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return;
        }
        var posted = $(ids[ids.length - 1]).find('td').eq(8).find('div').text();
        if (posted == "YES") {
            showError("Selected voucher is posted.");
            return false;
        }
        showLoadingSpinner(true);
        var voucherId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        var url = "${createLink(controller: 'accVoucher', action: 'select')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForEditVoucher,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConForEditVoucher(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showVoucher(data);
        }
    }

    function showVoucher(data) {
        clearFormVoucher();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#hidTraceNo').val(entity.traceNo);
        $('#hidIsVoucherPosted').val(entity.isVoucherPosted);

        $('#lblRefNo').text(entity.traceNo);
        $('#voucherTypeId').val(entity.voucherTypeId);
        $('#voucherDate').val(data.voucherDate);
        dropDownProject.value(data.projectId);
        dropDownDivisionDr.setDataSource(data.lstDivision);
        dropDownDivisionCr.setDataSource(data.lstDivision);
        $('#note').val(entity.note);
        if (entity.instrumentId > 0) {
            $('#instrumentId').val(entity.instrumentId);
            dropDownInstrumentType.value(entity.instrumentTypeId);
        }

        gridModelDr = data.gridModelDr;
        gridModelCr = data.gridModelCr;
        $('#flexDebit').flexAddData(gridModelDr);
        $('#flexCredit').flexAddData(gridModelCr);

        if ($('#createVoucherPanel a[href="#fragment-4"]').tab()) {
            onSubmitJournal();
        }

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateDivision() {
        dropDownDivisionDr.setDataSource(getKendoEmptyDataSource());
        dropDownDivisionDr.value('');
        dropDownDivisionCr.setDataSource(getKendoEmptyDataSource());
        dropDownDivisionCr.value('');
        var projectId = dropDownProject.value();
        if (projectId == '') {
            return;
        }

        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accDivision', action: 'getDivisionListByProjectId')}?projectId=" + projectId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    dropDownDivisionDr.setDataSource(data.accDivisionList);
                    dropDownDivisionDr.value('');
                    dropDownDivisionCr.setDataSource(data.accDivisionList);
                    dropDownDivisionCr.value('');
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post'
        });
    }

    function getSourceListByCoaCode(code) {
        var result = null;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accChartOfAccount', action: 'listSourceByCoaCode')}?code=" + code,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    result = (data);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post',
            async: false
        });
        return result;
    }

    function executePreConForDebit() {
        trimFormValues($("#frmDebit"));

        if (isEmpty($('#hidCoaIdDr').val()) ||
                isEmpty($('#hidCoaCodeDr').val()) ||
                isEmpty($('#hidSourceTypeIdDr').val())
                ) {
            showError('Please enter account code and press ENTER');
            $('#coaCodeDr').focus();
            return false;
        }

        var hiddenCode = $('#hidCoaCodeDr').val();
        var givenCode = $('#coaCodeDr').val();
        if (hiddenCode != givenCode) {
            showError('Account code has been changed, press ENTER to re-populate');
            return false;
        }

        var strAmount = amountDr.value();
        if (isEmpty(strAmount)) {
            showError('Please enter debit amount');
            $('#createVoucherPanel a[href="#fragment-2"]').tab('show');
            $('#amountDr').focus();
            return false;
        }

        if (checkAmountValidity(strAmount) == false) {
            showError('Invalid debit amount');
            $('#createVoucherPanel a[href="#fragment-2"]').tab('show');
            $('#amountDr').focus();
            $('#amountDr').select();
            return false;
        }

        // check particulars
        var particularsDr = $.trim($("#particularsDr").val());
        $("#particularsDr").val(particularsDr);  // set the trimmed value

        var sourceTypeId = $('#hidSourceTypeIdDr').val();
        if ((sourceTypeId.length > 0) && parseInt(sourceTypeId) > 0 && sourceTypeId != sourceTypeNone && $('#sourceIdDr').val() == '') {
            showError('Please enter source for debit');
            $('#createVoucherPanel a[href="#fragment-2"]').tab('show');
            $('#sourceIdDr').focus();
            return false;
        }

        var coaId = $('#hidCoaIdDr').val();
        if (checkDuplicateCodeInCr(coaId) == false) return false;
    }

    function executePreConForCredit() {
        trimFormValues($("#frmCredit"));

        if (isEmpty($('#hidCoaIdCr').val()) ||
                isEmpty($('#hidCoaCodeCr').val()) ||
                isEmpty($('#hidSourceTypeIdCr').val())
                ) {
            showError('Please select an account for credit');
            $('#coaVoucherType').focus();
            return false;
        }

        var strAmount = amountCr.value();
        if (isEmpty(strAmount)) {
            showError('Please enter credit amount');
            $('#createVoucherPanel a[href="#fragment-3"]').tab('show');
            $('#amountCr').focus();
            return false;
        }
        // check amount pattern
        if (checkAmountValidity(strAmount) == false) {
            showError('Invalid credit amount');
            $('#createVoucherPanel a[href="#fragment-3"]').tab('show');
            $('#amountCr').focus();
            $('#amountCr').select();
            return false;
        }

        // check particulars
        var particularsCr = $.trim($("#particularsCr").val());
        $("#particularsCr").val(particularsCr);  // set the trimmed value

        var sourceTypeId = $('#hidSourceTypeIdCr').val();
        if ((sourceTypeId.length > 0) && parseInt(sourceTypeId) > 0 && sourceTypeId != sourceTypeNone && $('#sourceIdCr').val() == '') {
            showError('Please enter source for credit');
            $('#createVoucherPanel a[href="#fragment-3"]').tab('show');
            $('#sourceIdCr').focus();
            return false;
        }

        var coaId = $('#hidCoaIdCr').val();
        if (checkDuplicateCodeInDr(coaId) == false) return false;
    }

    function executePostForDebit() {
        clearForm($('#frmDebit'), $('#coaCodeDr'));
        $('#lblSourceIdDr').removeClass('label-required');
        $('#lblSourceIdDr').addClass('label-optional');
        dropDownSourceDr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceDr.value('');
    }

    function executePostForCredit() {
        amountCr.value('');
        $('#particularsCr').val('');
        $('#amountCr').focus();
        dropDownDivisionCr.value('');
    }

    function onSubmitVoucher() {
        if (executePreConForVoucher() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        showLoadingSpinner(true);
        var formData = $('#frmVoucher').serializeArray();
        formData.push({name: 'gridModelDr', value: JSON.stringify(gridModelDr)});
        formData.push({name: 'gridModelCr', value: JSON.stringify(gridModelCr)});

        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'accVoucher', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller:'accVoucher', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: formData,
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConForVoucher(data);
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

    function resetVoucherToCreateAgain() {
        clearErrors($('#frmVoucher'));
        clearForm($('#frmDebit'), $('#coaCodeDr'));
        clearForm($('#frmCredit'), $('#coaCodeCr'));

        $('#lblRefNo').text('(Auto Generated)');
        gridModelDr = getEmptyModelForDrCr();
        gridModelCr = getEmptyModelForDrCr();
        $('#flexDebit').flexAddData(gridModelDr);
        $('#flexCredit').flexAddData(gridModelCr);
        var emptyModel = getEmptyModelForDrCr();
        $('#flexJournal').flexAddData(emptyModel);

        $('#note').val('');
        $('#id').val('');
        $('#version').val('');
        $('#hidTraceNo').val('');
        $('#hidIsVoucherPosted').val('');
        $('#createVoucherPanel a[href="#fragment-1"]').tab('show');
        $('#voucherDate').focus();
        $('#instrumentId').val('');

        $('#lblSourceIdDr').removeClass('label-optional');
        $('#lblSourceIdDr').removeClass('label-required');
        $('#lblSourceIdDr').addClass('label-optional');
        dropDownSourceDr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceDr.value('');
        $('#lblSourceIdCr').removeClass('label-optional');
        $('#lblSourceIdCr').removeClass('label-required');
        $('#lblSourceIdCr').addClass('label-optional');
        dropDownSourceCr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceCr.value('');
        dropDownDivisionDr.value('');
        dropDownDivisionCr.value('');

        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
    }

    function clearFormVoucher() {
        clearForm($('#frmVoucher'), $('#voucherDate'));
        resetVoucherToCreateAgain();

        $('#voucherDate').val(voucherDate);
        dropDownDivisionDr.setDataSource(getKendoEmptyDataSource());
        dropDownDivisionDr.value('');
        dropDownDivisionCr.setDataSource(getKendoEmptyDataSource());
        dropDownDivisionCr.value('');

        $('#voucherTypeId').val(voucherTypeId);
    }

    function moveUpDebit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexDebit'), 'debit') == false) {
            return;
        }

        if (gridModelDr.total == 1) {
            return;
        }

        var addTo = 0;

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var hidSourceTypeIdDr = $(ids[0]).find('td').eq(5).find('div').text();
        var hidSourceNameDr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceId = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdDr = $(ids[0]).find('td').eq(9).find('div').text();

        for (var i = 0; i < gridModelDr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelDr.rows[i].cell[8])) {
                addTo = i - 1;
                if (addTo < 0) {
                    return;
                }
                gridModelDr.rows.splice(i, 1);
                break;
            }
        }

        $('.trSelected', $('#flexDebit')).each(function (e) {
            $(this).remove();
        });

        var rows = new Array();
        rows.push(coaId);
        rows.push(coaCode);
        rows.push(headName);
        rows.push(amount);
        rows.push(particulars);
        rows.push(hidSourceTypeIdDr);
        rows.push(hidSourceNameDr);
        rows.push(sourceId);
        rows.push(serial);
        rows.push(divisionIdDr);

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = coaId;
        gridModelDr.rows.splice(addTo, 0, gridObj);
        reCalculateSerial(gridModelDr);
        $('#flexDebit').flexAddData(gridModelDr);
        showLoadingSpinner(false);
        return true;
    }

    function moveDownDebit(com, grid) {
        var ids = $('.trSelected', grid);
        var deleteDebit = ids.length;

        if (executeCommonPreConditionForSelect($('#flexDebit'), 'debit') == false) {
            return;
        }

        if (gridModelDr.total == 1) {
            return;
        }

        var addTo = 0;

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var hidSourceTypeIdDr = $(ids[0]).find('td').eq(5).find('div').text();
        var hidSourceNameDr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceId = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdDr = $(ids[0]).find('td').eq(9).find('div').text();

        for (var i = 0; i < gridModelDr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelDr.rows[i].cell[8])) {
                addTo = i + 1;
                if (addTo > gridModelDr.total + 1) {
                    return;
                }
                gridModelDr.rows.splice(i, 1);
                break;
            }
        }

        $('.trSelected', $('#flexDebit')).each(function (e) {
            $(this).remove();
        });

        var rows = new Array();
        rows.push(coaId);
        rows.push(coaCode);
        rows.push(headName);
        rows.push(amount);
        rows.push(particulars);
        rows.push(hidSourceTypeIdDr);
        rows.push(hidSourceNameDr);
        rows.push(sourceId);
        rows.push(serial);
        rows.push(divisionIdDr);

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = coaId;
        gridModelDr.rows.splice(addTo, 0, gridObj);
        reCalculateSerial(gridModelDr);

        $('#flexDebit').flexAddData(gridModelDr);
        showLoadingSpinner(false);
        return true;
    }

    function moveUpCredit(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexCredit'), 'credit') == false) {
            return;
        }

        if (gridModelCr.total == 1) {
            return;
        }

        var addTo = 0;

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var hidSourceTypeIdCr = $(ids[0]).find('td').eq(5).find('div').text();
        var hidSourceNameCr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceId = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdCr = $(ids[0]).find('td').eq(9).find('div').text();

        for (var i = 0; i < gridModelCr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelCr.rows[i].cell[8])) {
                addTo = i - 1;
                if (addTo < 0) {
                    return;
                }
                gridModelCr.rows.splice(i, 1);
                break;
            }
        }

        $('.trSelected', $('#flexCredit')).each(function (e) {
            $(this).remove();
        });

        var rows = new Array();
        rows.push(coaId);
        rows.push(coaCode);
        rows.push(headName);
        rows.push(amount);
        rows.push(particulars);
        rows.push(hidSourceTypeIdCr);
        rows.push(hidSourceNameCr);
        rows.push(sourceId);
        rows.push(serial);
        rows.push(divisionIdCr);

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = coaId;
        gridModelCr.rows.splice(addTo, 0, gridObj);
        reCalculateSerial(gridModelCr);

        $('#flexCredit').flexAddData(gridModelCr);
        showLoadingSpinner(false);
        return true;
    }

    function moveDownCredit(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexCredit'), 'credit') == false) {
            return;
        }

        if (gridModelCr.total == 1) {
            return;
        }

        var addTo = 0;

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var hidSourceTypeIdCr = $(ids[0]).find('td').eq(5).find('div').text();
        var hidSourceNameCr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceId = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdCr = $(ids[0]).find('td').eq(9).find('div').text();

        for (var i = 0; i < gridModelCr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelCr.rows[i].cell[8])) {
                addTo = i + 1;
                if (addTo > gridModelCr.total + 1) {
                    return;
                }
                gridModelCr.rows.splice(i, 1);
                break;
            }
        }

        $('.trSelected', $('#flexCredit')).each(function (e) {
            $(this).remove();
        });

        var rows = new Array();
        rows.push(coaId);
        rows.push(coaCode);
        rows.push(headName);
        rows.push(amount);
        rows.push(particulars);
        rows.push(hidSourceTypeIdCr);
        rows.push(hidSourceNameCr);
        rows.push(sourceId);
        rows.push(serial);
        rows.push(divisionIdCr);

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = coaId;
        gridModelCr.rows.splice(addTo, 0, gridObj);
        reCalculateSerial(gridModelCr);

        $('#flexCredit').flexAddData(gridModelCr);
        showLoadingSpinner(false);
        return true;
    }

    // Re-calculate the rowId of voucherDetails sequentially
    function reCalculateSerial(gridModel) {
        for (var i = 0; i < gridModel.total; i++) {
            gridModel.rows[i].cell[8] = i + 1;
        }
    }

    function executePreConForDeleteDebit() {
        if (executeCommonPreConditionForSelect($('#flexDebit'), 'debit') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected debit?')) {
            return false;
        }
        return true;
    }

    function deleteDebit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executePreConForDeleteDebit() == false) {
            return;
        }
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        $('.trSelected', $('#flexDebit')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelDr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelDr.rows[i].cell[8])) {
                gridModelDr.rows.splice(i, 1);
                gridModelDr.total = gridModelDr.total - 1;
                reCalculateSerial(gridModelDr);
                $('#flexDebit').flexAddData(gridModelDr);
                return;
            }
        }
    }

    function editDebit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexDebit'), 'debit') == false) {
            return;
        }
        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var sourceTypeIdDr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceIdDr = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdDr = $(ids[0]).find('td').eq(9).find('div').text();

        $('#hidCoaIdDr').val(coaId);
        $('#hidCoaCodeDr').val(coaCode);
        $('#hidCoaHeadDr').val(headName);
        $('#hidSourceTypeIdDr').val(sourceTypeIdDr);
        dropDownDivisionDr.value(divisionIdDr);

        $('#coaCodeDr').val(coaCode);
        amountDr.value(amount);
        $('#particularsDr').val(particulars);

        var returnObject = getSourceListByCoaCode(coaCode);
        if (!returnObject) return false;

        var lstSource = returnObject.sourceList;

        populateSourceCollectionForDr(sourceTypeIdDr, lstSource, sourceIdDr);

        if (parseInt(sourceIdDr) == 0) {
            dropDownSourceDr.value('');
        }

        $('.trSelected', $('#flexDebit')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelDr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelDr.rows[i].cell[8])) {
                gridModelDr.rows.splice(i, 1);
                gridModelDr.total = gridModelDr.total - 1;
                return;
            }
        }
    }

    function executePreConForDeleteCredit() {
        if (executeCommonPreConditionForSelect($('#flexCredit'), 'credit') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to delete the selected credit?')) {
            return false;
        }
        return true;
    }

    function deleteCredit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executePreConForDeleteCredit() == false) {
            return;
        }
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        $('.trSelected', $('#flexCredit')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelCr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelCr.rows[i].cell[8])) {
                gridModelCr.rows.splice(i, 1);
                gridModelCr.total = gridModelCr.total - 1;
                reCalculateSerial(gridModelCr);
                $('#flexCredit').flexAddData(gridModelCr);
                return;
            }
        }
    }

    function editCredit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexCredit'), 'credit') == false) {
            return;
        }

        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCode = $(ids[0]).find('td').eq(1).find('div').text();
        var headName = $(ids[0]).find('td').eq(2).find('div').text();
        var amount = $(ids[0]).find('td').eq(3).find('div').text();
        var particulars = $(ids[0]).find('td').eq(4).find('div').text();
        var sourceTypeIdCr = $(ids[0]).find('td').eq(6).find('div').text();
        var sourceIdCr = $(ids[0]).find('td').eq(7).find('div').text();
        var serial = $(ids[0]).find('td').eq(8).find('div').text();
        var divisionIdCr = $(ids[0]).find('td').eq(9).find('div').text();

        $('#hidCoaIdCr').val(coaId);
        $('#hidCoaCodeCr').val(coaCode);
        $('#hidCoaHeadCr').val(headName);
        $('#hidSourceTypeIdCr').val(sourceTypeIdCr);
        dropDownDivisionCr.value(divisionIdCr);

        dropDownCoaVoucherType.value(coaId);
        amountCr.value(amount);
        $('#particularsCr').val(particulars);
        if (parseInt(sourceIdCr) == 0) {
            dropDownSourceCr.value('');
        } else {
            var returnObject = getSourceListByCoaCode(coaCode);
            if (!returnObject) return false;

            var lstSource = returnObject.sourceList;
            populateSourceCollectionForCr(sourceTypeIdCr, lstSource, sourceIdCr);
        }

        $('.trSelected', $('#flexCredit')).each(function (e) {
            $(this).remove();
        });
        for (var i = 0; i < gridModelCr.total; i++) {
            if (parseFloat(serial) == parseFloat(gridModelCr.rows[i].cell[8])) {
                gridModelCr.rows.splice(i, 1);
                gridModelCr.total = gridModelCr.total - 1;
                return;
            }
        }
    }

    function reloadCoaGrid(com, grid) {
        $('#flexSearchCOA').flexOptions({query: ''}).flexReload();
    }

    function reloadVoucherGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function setUrlVoucherGrid() {
        var strUrl = "${createLink(controller:'accVoucher', action: 'listPayCash')}";
        $("#flex1").flexOptions({url: strUrl});

        if (gridModelVoucher) {
            $('#flex1').flexAddData(gridModelVoucher);
        }
    }

    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ VALIDATION SCRIPT
    function addInDebit(com, grid) {
        var ids = $('.trSelected', grid);

        if (executeCommonPreConditionForSelect($('#flexSearchCOA'), 'chart of account') == false) {
            return;
        }
        var coaId = $(ids[0]).find('td').eq(0).find('div').text();
        var coaCodeDr = $(ids[0]).find('td').eq(1).find('div').text();
        var coaHeadDr = $(ids[0]).find('td').eq(2).find('div').text();
        var hidSourceTypeIdDr = $(ids[0]).find('td').eq(3).find('div').text();

        $('#coaCodeDr').val(coaCodeDr);
        $('#hidCoaCodeDr').val(coaCodeDr);
        $('#hidCoaHeadDr').val(coaHeadDr);
        $('#hidCoaIdDr').val(coaId);
        $('#hidSourceTypeIdDr').val(hidSourceTypeIdDr);

        $('#createVoucherPanel a[href="#fragment-2"]').tab('show');
        $('#amountDr').focus();

        var returnObject = getSourceListByCoaCode(coaCodeDr);
        if (!returnObject) return false;

        var lstSource = returnObject.sourceList;

        // now populate source id (if needed)
        populateSourceCollectionForDr(hidSourceTypeIdDr, lstSource, '')
    }

    function populateSourceCollectionForDr(sourceTypeId, lstSource, defaultSourceId) {
        $('#lblSourceIdDr').removeClass('label-optional');
        $('#lblSourceIdDr').removeClass('label-required');

        dropDownSourceDr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceDr.value('');
        if (sourceTypeId == sourceTypeNone) {
            $('#lblSourceIdDr').addClass('label-optional');
        } else {
            $('#lblSourceIdDr').addClass('label-required');
            dropDownSourceDr.setDataSource(lstSource);
            dropDownSourceDr.value(defaultSourceId);
        }
    }

    function populateSourceCollectionForCr(sourceTypeId, lstSource, defaultSourceId) {
        $('#lblSourceIdCr').removeClass('label-optional');
        $('#lblSourceIdCr').removeClass('label-required');
        dropDownSourceCr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceCr.value('');
        if (sourceTypeId == sourceTypeNone) {
            $('#lblSourceIdCr').addClass('label-optional');
        } else {
            $('#lblSourceIdCr').addClass('label-required');
            dropDownSourceCr.setDataSource(lstSource);
            dropDownSourceCr.value(defaultSourceId);
        }
    }

    function getListOfSubAccount(coaId) {
        var lstSubAccount = null;
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'accSubAccount', action: 'getListByCoaId')}?coaId=" + coaId,
            success: function (data) {
                if (data.isError) {
                    showError(data.message);
                } else {
                    lstSubAccount = (data.lstAccSubAccount);
                }
            }, complete: onCompleteAjaxCall(),
            dataType: 'json',
            type: 'post',
            async: false
        });
        return lstSubAccount;
    }

    function searchChartOfAccForDr() {
        var code = $.trim($('#coaCodeDr').val());
        if (code.length <= 0) {
            showError('Please enter account code');
            return false;
        }
        dropDownSourceDr.setDataSource(getKendoEmptyDataSource());
        dropDownSourceDr.value('');
        $('#hidCoaHeadDr').val('');
        $('#hidCoaIdDr').val('');
        $('#hidSourceTypeIdDr').val('');

        var returnObject = getSourceListByCoaCode(code);
        if (!returnObject) return false;
        var coaObject = returnObject.coa;
        var lstSource = returnObject.sourceList;

        if (checkDuplicateCodeInCr(coaObject.id) == false) return false;
        $('#coaCodeDr').val(coaObject.code);    // in case lower case, make it upper
        $('#hidCoaCodeDr').val(coaObject.code);
        $('#hidCoaHeadDr').val(coaObject.description);
        $('#hidCoaIdDr').val(coaObject.id);
        $('#hidSourceTypeIdDr').val(coaObject.accSourceId);

        populateSourceCollectionForDr(coaObject.accSourceId.toString(), lstSource, '');
        $('#amountDr').focus();
        return true;
    }

    function checkDuplicateCodeInDr(coaId) {
        for (var i = 0; i < gridModelDr.total; i++) {
            if (coaId == parseFloat(gridModelDr.rows[i].cell[0])) {
                showError('Account code ' + gridModelDr.rows[i].cell[1] + ' already exists in debit');
                return false;
            }
        }
    }

    function checkDuplicateCodeInCr(coaId) {
        for (var j = 0; j < gridModelCr.total; j++) {
            if (coaId == parseFloat(gridModelCr.rows[j].cell[0])) {
                showError('Account code ' + gridModelCr.rows[j].cell[1] + ' already exists in credit');
                return false;
            }
        }
    }

    function executePreConForVoucher() {
        // trim field vales before process.
        trimFormValues($("#frmVoucher"));

        if (checkVoucherDate($('#voucherDate')) == false) {
            $('#createVoucherPanel a[href="#fragment-1"]').tab('show');
            $('#voucherDate').focus();
            return false;
        }

        var projectId = dropDownProject.value();
        if (projectId == '') {
            $('#createVoucherPanel a[href="#fragment-1"]').tab('show');
            $('#projectId').focus();
            showError('Please select a project');
            return false;
        }

        // check note
        var note = $("#note").val();
        if (note.length == 0) {
            showError('Please enter Note');
            $('#createVoucherPanel a[href="#fragment-1"]').tab('show');
            $('#note').focus();
            return false;
        }

        // check if Dr-total and Cr-total are same ; Also re-calculate serial/rowId
        var drTotal = 0, crTotal = 0;
        for (var i = 0; i < gridModelDr.total; i++) {
            drTotal = drTotal + parseFloat(gridModelDr.rows[i].cell[3]);
            gridModelDr.rows[i].cell[8] = i + 1;
        }
        if (drTotal == 0) {
            showError('Please enter debit');
            $('#createVoucherPanel a[href="#fragment-2"]').tab('show');
            $('#amountDr').focus();
            return false;
        }
        for (var j = 0; j < gridModelCr.total; j++) {
            crTotal = crTotal + parseFloat(gridModelCr.rows[j].cell[3]);
            gridModelCr.rows[j].cell[8] = j + 1;
        }
        if (crTotal == 0) {
            showError('Please enter credit');
            $('#createVoucherPanel a[href="#fragment-3"]').tab('show');
            $('#amountCr').focus();
            return false;
        }
        if (drTotal.toFixed(2) != crTotal.toFixed(2)) {
            showError('Debit and Credit totals are not same');
            return false;
        }
        return true;
    }

    function onSubmitDebit() {
        setButtonDisabled($('#addDr'), true);
        showLoadingSpinner(true);
        if (executePreConForDebit() == false) {
            setButtonDisabled($('#addDr'), false);
            showLoadingSpinner(false);
            return false;
        }

        // add data into grid;
        var sourceName = 'N/A';
        var sourceId = 0;
        var sourceIdDr = dropDownSourceDr.value();
        if (sourceIdDr != '') {
            sourceName = dropDownSourceDr.dataItem().name;
            sourceId = dropDownSourceDr.dataItem().id;
        }
        var rows = new Array();
        rows.push($('#hidCoaIdDr').val());
        rows.push($('#hidCoaCodeDr').val());
        rows.push($('#hidCoaHeadDr').val());
        rows.push(amountDr.value());
        rows.push($('#particularsDr').val());
        rows.push(sourceName);
        rows.push($('#hidSourceTypeIdDr').val());
        rows.push(sourceId);
        rows.push(99);   // dummy serial; it will be re-calculated later
        rows.push(dropDownDivisionDr.value());

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = $('#hidCoaIdDr').val();

        gridModelDr.total = gridModelDr.total + 1;
        gridModelDr.rows.splice(gridModelDr.total - 1, 0, gridObj);
        reCalculateSerial(gridModelDr);
        $('#flexDebit').flexAddData(gridModelDr);
        showLoadingSpinner(false);
        setButtonDisabled($('#addDr'), false);
        executePostForDebit();
        onSubmitJournal();
        return false;
    }

    function onSubmitCredit() {
        setButtonDisabled($('#addCr'), true);
        showLoadingSpinner(true);
        if (executePreConForCredit() == false) {
            setButtonDisabled($('#addCr'), false);
            showLoadingSpinner(false);
            return false;
        }

        // add data into grid;
        var sourceName = 'N/A';
        var sourceId = 0;
        var sourceIdCr = dropDownSourceCr.value();
        if (sourceIdCr != '') {
            sourceName = dropDownSourceCr.dataItem().name;
            sourceId = dropDownSourceCr.dataItem().id;
        }
        var rows = new Array();
        rows.push($('#hidCoaIdCr').val());
        rows.push($('#hidCoaCodeCr').val());
        rows.push($('#hidCoaHeadCr').val());
        rows.push(amountCr.value());
        rows.push($('#particularsCr').val());
        rows.push(sourceName);
        rows.push($('#hidSourceTypeIdCr').val());
        rows.push(sourceId);
        rows.push(99);   // dummy serial; it will be re-calculated later
        rows.push(dropDownDivisionCr.value());

        var gridObj = new Object();
        gridObj.cell = rows;
        gridObj.id = $('#hidCoaIdCr').val();

        gridModelCr.total = gridModelCr.total + 1;
        gridModelCr.rows.splice(gridModelCr.total - 1, 0, gridObj);
        reCalculateSerial(gridModelCr);
        $('#flexCredit').flexAddData(gridModelCr);
        showLoadingSpinner(false);
        setButtonDisabled($('#addCr'), false);
        executePostForCredit();
        onSubmitJournal();
        return false;
    }

    function onSubmitJournal() {
        showLoadingSpinner(true);
        var gridModelJournal = getEmptyModelForDrCr();
        var rowsJournal = new Array();
        var cellJournal = new Array();

        for (var j = gridModelCr.total - 1; j >= 0; j--) {
            rowsJournal = new Array();
            rowsJournal.push(gridModelCr.rows[j].cell[1]);
            rowsJournal.push(gridModelCr.rows[j].cell[2]);
            rowsJournal.push('');
            rowsJournal.push(gridModelCr.rows[j].cell[3]);

            gridModelJournal.total = gridModelJournal.total + 1;
            cellJournal.push(rowsJournal);
        }

        for (var i = gridModelDr.total - 1; i >= 0; i--) {
            rowsJournal = new Array();
            rowsJournal.push(gridModelDr.rows[i].cell[1]);
            rowsJournal.push(gridModelDr.rows[i].cell[2]);
            rowsJournal.push(gridModelDr.rows[i].cell[3]);
            rowsJournal.push('');

            gridModelJournal.total = gridModelJournal.total + 1;
            cellJournal.push(rowsJournal);
        }

        for (var k = 0; k < cellJournal.length; k++) {
            var gridObjJournal = new Object();
            gridObjJournal.cell = cellJournal[k];
            gridModelJournal.rows.splice(0, 0, gridObjJournal);
        }

        $('#flexJournal').flexAddData(gridModelJournal);

        showLoadingSpinner(false);
        return false;
    }

    function executePostConForVoucher(result) {
        if (result.isError) {
            showError(result.message);
            showLoadingSpinner(false);
        } else {
            try {
                var newEntry = result.accVoucher;
                if ($('#id').val().isEmpty() && newEntry.entity != null) { // newly created

                    var previousTotal = parseInt(gridModelVoucher.total);
                    var firstSerial = 1;

                    if (gridModelVoucher.rows.length > 0) {
                        firstSerial = gridModelVoucher.rows[0].cell[0];
                        regenerateSerial($(gridModelVoucher.rows), 0);
                    }
                    newEntry.entity.cell[0] = firstSerial;
                    gridModelVoucher.rows.splice(0, 0, newEntry.entity);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        gridModelVoucher.rows.pop();
                    }

                    gridModelVoucher.total = ++previousTotal;
                    $("#flex1").flexAddData(gridModelVoucher);

                } else if (newEntry.entity != null) { // updated existing
                    updateListModel(gridModelVoucher, newEntry.entity, 0);
                    $("#flex1").flexAddData(gridModelVoucher);
                }

                resetVoucherToCreateAgain();
                showSuccess(result.message);
            } catch (e) {
                // Do Nothing
            }
        }
    }

    function executePreConForPostVoucher() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to post the selected voucher?')) {
            return false;
        }
        return true;
    }

    function postVoucher(com, grid) {
        if (executePreConForPostVoucher() == false) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller:'accVoucher', action: 'postVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForPostVoucher,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForPostVoucher(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            //clearFormVoucher();
            var selectedRows = $('table#flex1 > tbody > tr.trSelected');
            var postedHtml = "<div style='text-align: left;'>" + 'YES' + "</div>";
            $(selectedRows).find("td").eq(7).html(postedHtml);
            showSuccess(data.message);
        }
    }

    function unPostVoucher(com, grid) {
        if (executePreConForUnPostVoucher() == false) {
            return;
        }
        showLoadingSpinner(true);
        var voucherId = getSelectedIdFromGrid($('#flex1'));
        var url = "${createLink(controller:'accVoucher', action: 'unPostedVoucher')}?id=" + voucherId;
        $.ajax({
            url: url,
            success: executePostConForUnPostVoucher,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePreConForUnPostVoucher() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to Unpost the selected voucher?')) {
            return false;
        }
        return true;
    }

    function executePostConForUnPostVoucher(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            //clearFormVoucher();
            var selectedRows = $('table#flex1 > tbody > tr.trSelected');
            var postedHtml = "<div style='text-align: left;'>" + 'NO' + "</div>";
            $(selectedRows).find("td").eq(7).html(postedHtml);
            showSuccess(data.message);
        }
    }

    function viewVoucherReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'voucher') == false) {
            return;
        }
        showLoadingSpinner(true);

        var voucherId = getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller:'accReport', action: 'showVoucher')}?voucherId=" + voucherId;
        $.history.load(formatLink(loc));
        return false;
    }

    function checkVoucherDate(control) {
        if ($.trim(control.val()).length != 10) {
            showError("Please enter voucher date");
            return false;
        }

        var returnDate = getDate(control, "voucher date");
        if (returnDate == false || returnDate == undefined) {
            return false;
        }
        if (returnDate > new Date()) {
            showError('Voucher date can not be future date');
            return false;
        }
        return true;
    }

    function onChangeCoaVoucherType() {
        $('#hidCoaCodeCr').val('');
        $('#hidCoaHeadCr').val('');
        $('#hidCoaIdCr').val('');
        $('#hidSourceTypeIdCr').val('');
        var coaId = dropDownCoaVoucherType.value();
        if (coaId != '') {
            var coaCode = dropDownCoaVoucherType.dataItem().code
            var coaHead = dropDownCoaVoucherType.dataItem().description
            var sourceType = dropDownCoaVoucherType.dataItem().accSourceId
            $('#hidCoaCodeCr').val(coaCode);
            $('#hidCoaHeadCr').val(coaHead);
            $('#hidCoaIdCr').val(coaId);
            $('#hidSourceTypeIdCr').val(sourceType);

            var returnObject = getSourceListByCoaCode(coaCode);
            if (!returnObject) return false;

            var lstSource = returnObject.sourceList;
            populateSourceCollectionForCr(sourceType, lstSource, '');
            $('#amountCr').focus();
        }
    }

</script>
