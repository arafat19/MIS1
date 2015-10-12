<script language="javascript" type="text/javascript">
    var output = false;
    var dropDownAgent
    var agentCurrencyPostingModel = false;

    $(document).ready(function () {
        onLoadAgentCurrencyPostingPage();

    });

    function onLoadAgentCurrencyPostingPage() {
        initializeForm($("#agentCurrencyPostingForm"),onSubmitAgentCurrencyPostingForm);

        output = ${output};
        agentCurrencyPostingModel = output.exhAgentCurrencyPostingList? output.exhAgentCurrencyPostingList:false;

        initGrid();
        populateFlex1();

        // update page title

        $(document).attr('title', "Exchange House - Create Agent Currency Posting");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhAgentCurrencyPosting/show");

    }

    function executePreCondition() {
        if(!validateForm("#agentCurrencyPostingForm")) {
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

                    var previousTotal = parseInt(agentCurrencyPostingModel.total);

                    var firstSerial = 1;

                    if (agentCurrencyPostingModel.rows.length > 0) {
                        firstSerial = agentCurrencyPostingModel.rows[0].cell[0];
                        regenerateSerial($(agentCurrencyPostingModel.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    agentCurrencyPostingModel.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        agentCurrencyPostingModel.rows.pop();
                    }

                    agentCurrencyPostingModel.total = ++previousTotal;
                    $("#flex1").flexAddData(agentCurrencyPostingModel);

                } else if (newEntry != null) { // updated existing
                    updateListModel(agentCurrencyPostingModel, newEntry, 0);
                    $("#flex1").flexAddData(agentCurrencyPostingModel);
                }

                resetForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
    }

    function setCurrencySymbol() {
        if ($('#agentId').val() == -1) {
            $('#lblSymbol').text('');
            return false;
        }
        var symbol = $('#agentId option:selected').attr('symbol');
        $('#lblSymbol').text(symbol);
        $("#amount").focus();
    }

    function resetForm() {
        clearForm($("#agentCurrencyPostingForm"));
        $("#create").html("<span class='k-icon k-i-plus'></span>Create");

    }

    function onSubmitAgentCurrencyPostingForm() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('.save'), true);
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller:'exhAgentCurrencyPosting', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhAgentCurrencyPosting', action: 'update')}";
        }

        jQuery.ajax({
            type: 'post',
            data: jQuery("#agentCurrencyPostingForm").serialize(),
            url: actionUrl,
            success: function (data, textStatus) {
                executePostCondition(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
        return false;
    }

    function initGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 40, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Agent Name", name: "name", width: 300, sortable: false, align: "left"},
                        {display: "Amount", name: "amount", width: 120, sortable: false, align: "right"},
                        {display: "Created By", name: "createdBy", width: 150, sortable: false, align: "left"},
                        {display: "Created On", name: "createdOn", width: 150, sortable: false, align: "left"},
                        {display: "Updated On", name: "updatedOn", width: 150, sortable: false, align: "left"}

                    ],
                    buttons: [
                        <app:ifAllUrl urls="/exhAgentCurrencyPosting/select,/exhAgentCurrencyPosting/update">
                        {name: 'Edit', bclass: 'edit', onpress: editAgentCurrencyPosting},
                        </app:ifAllUrl>
                        <sec:access url="/exhAgentCurrencyPosting/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteAgentCurrencyPosting },
                        </sec:access>

                        <sec:access url="/exhAgentCurrencyPosting/list">
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadGrid},
                        </sec:access>
                        {separator: true}

                    ],

                    searchitems: [
                        {display: "Name", name: "name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Agent Currency Posting List',
                    useRp: true,
                    rp: 15,
                    rpOptions: [15, 20, 25],
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: populateCurrencyPostingGrid
                }
        );
    }

    function deleteAgentCurrencyPosting(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'))==false){
            return;
        }
        showLoadingSpinner(true);
        var exhAgentCurrencyPostingId = getSelectedIdFromGrid($('#flex1'));
        if (!confirm('Are you sure you want to delete the selected agent currency posting details?')) {
            return false;
        }
        $.ajax({
            url: "${createLink(controller:'exhAgentCurrencyPosting', action:'delete')}?id=" + exhAgentCurrencyPostingId,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });

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
            agentCurrencyPostingModel.total = parseInt(agentCurrencyPostingModel.total) - 1;
            removeEntityFromGridRows(agentCurrencyPostingModel, selectedRow);

        } else {
            showError(data.message);
        }
    }

    function reloadGrid(com, grid) {
        if (com == 'Refresh') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function editAgentCurrencyPosting(com, grid) {
        if(executeCommonPreConditionForSelect($('#flex1'))==false){
            return;
        }
        resetForm();
        showLoadingSpinner(true);

        var currencyPostingId = getSelectedIdFromGrid($('#flex1'));
        $.ajax({
            url: "${createLink(controller:'exhAgentCurrencyPosting', action: 'select')}?id=" + currencyPostingId,
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
            showError("Please select a currency posting to edit the details");
            return false;
        }
        return true;
    }

    function executePostConditionForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showExhAgentCurrencyPosting(data);
        }
    }

    function showExhAgentCurrencyPosting(data) {
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        dropDownAgent.value( entity.agentId);
        $('#amount').val(entity.amount);
        var symbol = $('#agentId option:selected').attr('symbol');
        $('#lblSymbol').text(symbol);
        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
    }

    function populateCurrencyPostingGrid(data) {
        if (data.isError) {
            showError(data.message);
            agentCurrencyPostingModel = getEmptyGridModel();
        } else {
            agentCurrencyPostingModel = data.exhAgentCurrencyPostingList;

        }
        $("#flex1").flexAddData(agentCurrencyPostingModel);
        return false;
    }

    function populateFlex1() {
        <sec:access url="/exhAgentCurrencyPosting/list">
        var strUrl = "${createLink(controller:'exhAgentCurrencyPosting', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});

        if (agentCurrencyPostingModel) {
            $("#flex1").flexAddData(agentCurrencyPostingModel);
        }
        </sec:access>
    }

</script>