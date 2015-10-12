<script type="text/javascript">
    var dropDownPaymentMethod, dropDownExchangeHouse, validatorTaskForm,pagerErrors,kendoListViewError;
    var cashCollectionId, bankDepositId;
    var lstErrorTasks, uploading = false;

    $(document).ready(function () {
        onLoadUploadTask();
    });

    function onLoadUploadTask() {
        bindFormSubmit();
        validatorTaskForm = $("#uploadTaskForm").kendoValidator({
            validateOnBlur: false,
            rules: {
                upload: function (input) {
                    if (input[0].type == "file" && ($(input[0]).is('[validationMessage]'))) {
                        return input.closest(".k-upload").find(".k-file").length;
                    }
                    return true;
                }
            }
        }).data("kendoValidator");
        pagerErrors = $("#pager").kendoPager({
            dataSource:  new kendo.data.DataSource({ data: []})
        }).data("kendoPager");
        kendoListViewError =  $("#lstViewTaskErrors").kendoListView({
            dataSource: new kendo.data.DataSource({ data: []}),
            template: "<li class='list-group-item'>#:id#. #:name#</li>"
        }).data("kendoListView");
        $("#taskFile").kendoUpload({multiple: false});

        $(document).attr('title', "ARMS - Upload Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForUploadTask");

        cashCollectionId = $("#hidCashCollectionId");
        bankDepositId = $("#hidBankDepositId");
    }

    function bindFormSubmit() {
        var actionUrl = "${createLink(controller: 'rmsTask',action: 'createForUploadTask')}";
        $("#uploadTaskForm").attr('action', actionUrl);

        $('#uploadTaskForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('.save'), true);
            },
            complete: function (result) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    onTaskFileUpload(result);
                    uploading = false;
                    setButtonDisabled($('.save'), false);
                }
            },
            beforePost: function () {
                $('#errorTaskGrid').hide();
                $("#errorDiv").hide();
                if (dropDownPaymentMethod.value() == cashCollectionId) {
                    initFlexGridForCashCollection();
                }
                else {
                    initFlexGridForBankDeposit();
                }
                $('#flex1').flexAddData(getEmptyGridModel());
                if (!validatorTaskForm.validate()) {
                    return false;
                }
                return true;
            }
        });
    }

    function onTaskFileUpload(result) {
        result = eval('(' + result + ')');
        if (result.isError) {
            showError(result.message);
            if (result.gridObj) {
                $('#errorTaskGrid').show();
                $('#flex1').flexAddData(result.gridObj);
                lstErrorTasks = result.lstErrorTasks;
                $("#flex1 tr").click(function () {
                    showErrorFromGrid();
                });
            }
        }
        else {
            showSuccess(result.message);
        }
    }

    function resetForm() {
        $(".k-delete").parent().click();
        $("#errorDiv").hide();
        $("#errorTaskGrid").hide();
        clearForm($('#uploadTaskForm'));
    }


    function initFlexGridForBankDeposit() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "SL", name: "serial", width: 40, sortable: false, align: "left"},
                        {display: "Value Date", name: "valueDate", width: 100, sortable: false, align: "left"},
                        {display: "Trans Ref No", name: "refNo", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: false, align: "left"},
                        {display: "Acc. No", name: "accNo", width: 110, sortable: false, align: "left"},
                        {display: "Currency", name: "currency", width: 60, sortable: false, align: "left"},
                        {display: "Amount", name: "Amount", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary Bank", name: "beneficiaryBank", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary Branch", name: "beneficiaryBranch", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary District", name: "beneficiaryDistrict", width: 110, sortable: false, align: "left"}
                    ],
                    usepager: false,
                    singleSelect: true,
                    title: 'Following tasks contain error',
                    useRp: false,
                    showTableToggleBtn: false,
                    height: getGridHeight()
//                    height: 50
                }
        );
    }

    function initFlexGridForCashCollection() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "SL", name: "serial", width: 40, sortable: false, align: "left"},
                        {display: "Value Date", name: "valueDate", width: 100, sortable: false, align: "left"},
                        {display: "Trans Ref No", name: "refNo", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiaryName", width: 150, sortable: false, align: "left"},
                        {display: "Bank", name: "bank", width: 110, sortable: false, align: "left"},
                        {display: "Branch", name: "branch", width: 110, sortable: false, align: "left"},
                        {display: "District", name: "district", width: 110, sortable: false, align: "left"},
                        {display: "Beneficiary Phone", name: "beneficiaryPhone", width: 110, sortable: false, align: "left"},
                        {display: "Remitter", name: "Remitter", width: 60, sortable: false, align: "left"},
                        {display: "Amount", name: "Amount", width: 110, sortable: false, align: "left"},
                        {display: "Pin No", name: "pinNo", width: 110, sortable: false, align: "left"},
                        {display: "Identity Type", name: "identityType", width: 110, sortable: false, align: "left"},
                        {display: "Identity No", name: "identityNo", width: 110, sortable: false, align: "left"},
                        {display: "Currency", name: "currency", width: 110, sortable: false, align: "left"}
                    ],
                    usepager: false,
                    singleSelect: true,
                    title: 'Following tasks contain error',
                    useRp: false,
                    showTableToggleBtn: false,
                    height: getGridHeight()
                }
        );
    }

    function showErrorFromGrid() {
        var selectedId = $('.trSelected', $("#flex1"));
        if (selectedId.length <= 0) {
            $("#errorDiv").hide();
            return;
        }
        var serial = $(selectedId).find("td").eq(0).text();
        var index = serial - 1;
        var task = lstErrorTasks[index];
        var dataSource = new kendo.data.DataSource({
            data: task.errors,
            pageSize: 4
        });
        kendoListViewError.setDataSource(dataSource);
        pagerErrors.setDataSource(dataSource);
        pagerErrors.refresh();
        $("#errorDiv").show();
    }
</script>