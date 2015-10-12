<script type="text/javascript">
    var remittanceListModel;
    var createdDateFrom, createdDateTo;
    var modelJson;

    $(document).ready(function () {

        initializeForm($("#searchForm"), getCustomer);
        $('#downloadBtn').click(function () {
            downloadCustomerHistory();
        });
        onLoadCustomerRemittanceReport();
        $('#customerId').focus();

        createdDateFrom = $('#createdDateFrom').val();
        createdDateTo = $('#createdDateTo').val();
    });

    function onLoadCustomerRemittanceReport() {

        modelJson = ${modelJson};
        if (modelJson.isError) {
            showError(modelJson.message);
            return false;
        }
        remittanceListModel = modelJson.remittanceListJSON ? modelJson.remittanceListJSON : '';
        if (modelJson.strCustomerCode != "")
            $('#customerId').val(modelJson.strCustomerCode);
        $('#hidCustomerId').val(modelJson.strCustomerId);
        $('#customer_id').text(modelJson.strCustomerCode);
        $('#customer_name').html(modelJson.strCustomerName ? modelJson.strCustomerName : "&nbsp");
        $('#lblDeclarationAmount').html(modelJson.declarationAmount ? modelJson.declarationAmount : "&nbsp");
        $('#currencySymbol').html($('#hidLocalCurrency').val());
        $('#lblNationality').html(modelJson.nationality ? modelJson.nationality : "&nbsp");
        initFlexGrid();
        populateFlexGrid();
        if (modelJson.strCustomerId) {
            $('span#downloadBtnSpan').show();
        }

        // update page title
        $(document).attr('title', "ARMS - Customer Remittance History");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#/exhReport/showCustomerHistory");

    }

    function executePreConditionForEdit(ids) {
        if (ids.length == 0) {
            showError("Please enter a customer A/C no");
            return false;
        }

        if (customValidateDate($('#createdDateFrom'), 'Start Date', $('#createdDateTo'), 'End Date') == false) return false;

        return true;
    }

    function getCustomer() {
	    trimFormValues($("#searchForm"));
	    var customerCode = $('#customerId').val();
	    if(isEmpty(customerCode)){
		    showError("Please enter customer A/C no.");
		    return false;
	    }

        if (executePreConditionForEdit(customerCode) == false) {
            return false;
        }
        var createdDateFrom = $('#createdDateFrom').val();
        var createdDateTo = $('#createdDateTo').val();

        showLoadingSpinner(true);
        $('#customerDetails').attr('property_name', 'code');
        $('#customerDetails').attr('property_value', customerCode);
        $('#customerDetails').reloadMe();
        showLoadingSpinner(true);
        return false;
    }

    function downloadCustomerHistory() {
        var customerCode = $('#customerId').val();
        if (executePreConditionForEdit(customerCode) == false) {
            return false;
        }
        var createdDateFrom = $('#createdDateFrom').val();
        var createdDateTo = $('#createdDateTo').val();

        var paramForSearch = "?customerId=" + customerCode + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;

        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'exhReport', action: 'downloadCustomerHistory')}" + paramForSearch;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function executePostConditionForViewCustomer(data) {
        resetCustomerAndGrid();
        $("#flex1").flexOptions({url: false});

        if (data.isError) {
            showError(data.message);
            $('span#downloadBtnSpan').hide();
            return;
        }
        populateCustomer(data);


        if (data.gridOutput == null) {
            showInfo(data.message);
            return;
        }
        var strUrl = "${createLink(controller: 'exhReport', action: 'listForCustomerRemittance')}?customerId=" + $('#hidCustomerId').val() + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;

        showCustomerReport(data.gridOutput);
        // set grid url

        $("#flex1").flexOptions({url: strUrl});

        return false;

    }


    function resetCustomerAndGrid() {
        $('#customer_id').html('&nbsp');
        $('#customer_name').html('&nbsp');
        $('#lblDeclarationAmount').html('&nbsp');
        $('#currencySymbol').html('&nbsp');
        $('#lblNationality').html('&nbsp');
        var obj = getEmptyGridModel();
        $("#flex1").flexAddData(obj);
    }

    function populateCustomer(data) {
        $('#customer_id').text(data.customer.code);
        $('#customer_name').text(data.customerName);
        $('#lblDeclarationAmount').text(data.declarationAmount);
        $('#currencySymbol').text(data.customer.id ? $('#hidLocalCurrency').val() : '');
        $('#lblNationality').text(data.nationality);
        $('#hidCustomerId').val(data.customer.id);
        $('span#downloadBtnSpan').show();
    }

    function showCustomerReport(gridObject) {
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        $("#flex1").flexAddData(gridObject);
        return false;
    }

    function initFlexGrid() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "Date", name: "created_on", width: 110, sortable: false, align: "left"},
                        {display: "Ref No", name: "ref_no", width: 80, sortable: false, align: "left"},
                        {display: "Beneficiary Name", name: "beneficiary_name", width: 190, sortable: false, align: "left"},
                        {display: "Amount(BDT)", name: "amount_in_foreign_currency", width: 90, sortable: false, align: "right"},
                        {display: "Amount(" + $('#hidLocalCurrency').val() + ")", name: "amount_in_local_currency", width: 90, sortable: false, align: "right"}
                    ],
                    sortname: "task.created_on",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Customer Remittance History Details',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight(),
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                        checkGrid();
                    }
                }
        );
    }


    function populateFlexGrid() {
//        var createdDateFrom = modelJson.createdDateFrom;
//        var createdDateTo = modelJson.createdDateTo;
        var strUrl = "${createLink(controller: 'exhReport', action: 'listForCustomerRemittance')}";
        if (remittanceListModel) {
            strUrl = strUrl + "?customerId=" + $('#hidCustomerId').val() + "&createdDateFrom=" + createdDateFrom + "&createdDateTo=" + createdDateTo;
            $("#flex1").flexAddData(remittanceListModel);
        }
        $("#flex1").flexOptions({url: strUrl});
    }

    function checkGrid() {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length < 1) {
            showInfo('No remittance details found');
        }
    }

</script>