<script type="text/javascript">
    var modelJsonForCurrencyConversion;
    var currencyConversionListModel = null;
    var dropDownFromCurrency, dropDownToCurrency;

    $(document).ready(function() {
        onLoadCurrencyConversion();
    });

    function onLoadCurrencyConversion() {
	    initializeForm($("#currencyConversionForm"),onSubmitCurrencyConversion);

        modelJsonForCurrencyConversion = ${modelJson};
        currencyConversionListModel = modelJsonForCurrencyConversion.currencyConversionListJSON ? modelJsonForCurrencyConversion.currencyConversionListJSON : false;

	    initFlex();
	    populateOnLoadFlexGrid();

	    $(document).attr('title', "ARMS - Create Currency Conversion");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhCurrencyConversion/show");
    }

    function editCurrencyConversion(com, grid) {
        if(executeCommonPreConditionForSelect($('#flex1'),'currency conversion',true)==false){
            return;
        }
        var conversionId = getSelectedIdFromGrid($('#flex1'));
        showLoadingSpinner(true);
        jQuery.ajax({
                    type:'post',
                    url:  "${createLink(controller: 'exhCurrencyConversion', action: 'edit')}?id=" + conversionId,
                    success:function(data, textStatus) {
                        executePostForEdit(data);
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown) {
                    },
                    complete:onCompleteAjaxCall,
                    dataType:'json'
                });
        return true;
    }

    function executePostForEdit(data) {
        resetCurrencyConversionForm();
        var entity = data.entity;
        if (entity != null) {
            $('#id').val(entity.id);
            $('#version').val(data.version);
            dropDownFromCurrency.value(entity.fromCurrency);
            dropDownToCurrency.value(entity.toCurrency);
            $('#buyRate').val(entity.buyRate);
            $('#sellRate').val(entity.sellRate);
	        $("#create").html("<span class='k-icon k-i-plus'></span>Update");
        }
    }

    function checkRateAmount(value) {
        return (/^[0-9]\d{0,7}(\.\d{1,8})?$/.test(value) && (parseFloat(value) > 0));
    }

    function checkCurrencyRate() {
        var buyRate = $.trim($('#buyRate').val());
        $('#buyRate').val(buyRate);
        if (!checkRateAmount(buyRate)) {

            showError('Currency conversion rate is invalid amount');
            return false;
        }
        var sellRate = $.trim($('#sellRate').val());
        $('#sellRate').val(sellRate);

        if (!checkRateAmount(sellRate)) {
            showError('Currency conversion rate is invalid amount');
            return false;
        }
        return true;
    }

    function executePreForSubmit() {
	    if (!validateForm($("#currencyConversionForm"))) {
		    return false;
	    }

        if (!checkCurrencyRate()) return false;

        var fromCurrency = $('#fromCurrency').val();
        var toCurrency = $('#toCurrency').val();
        if (fromCurrency == toCurrency) {
            showError('Two currencies can not be same');
            $('#toCurrency').focus();
            return false;
        }

        return true;
    }

    function onSubmitCurrencyConversion() {

        if (executePreForSubmit() == false) {
            return false;
        }

        setButtonDisabled($('.save'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var actionUrl = null;
        if ($('#id').val().isEmpty()) {
            actionUrl = "${createLink(controller: 'exhCurrencyConversion', action: 'create')}";
        } else {
            actionUrl = "${createLink(controller: 'exhCurrencyConversion', action: 'update')}";
        }

        jQuery.ajax({
                    type:'post',
                    data:jQuery("#currencyConversionForm").serialize(),
                    url: actionUrl,
                    success:function(data, textStatus) {
                        executePostConditionForSave(data);
                    },
                    error:function(XMLHttpRequest, textStatus, errorThrown) {
                    },
                    complete:function(XMLHttpRequest, textStatus) {
                        setButtonDisabled($('.save'), false);
                        onCompleteAjaxCall();
                    },
                    dataType:'json'
                });
        return false;
    }

    function executePostConditionForSave(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            showSuccess(result.message);
            resetCurrencyConversionForm();
            $("#flex1").flexReload();
        }
    }

    function initFlex() {
	    $("#flex1").flexigrid
	    (
			    {
				    //flexigrid url is false due to remove round trip
				    url: false,
				    dataType: 'json',
				    colModel : [
					    {display: "Serial", name : "serial", width : 30, sortable : false, align: "right"},
					    {display: "ID", name : "id", width : 30, sortable : false, align: "right", hide: true},
					    {display: "From Currency", name : "fromCurrency", width : 220, sortable : true, align: "left"},
					    {display: "To Currency", name : "toCurrency", width : 220, sortable : true, align: "left"},
					    {display: "Buy Rate", name : "buyRate", width : 100, sortable : false, align: "right"},
                        {display: "Sell Rate", name : "sellRate", width : 100, sortable : false, align: "right"},
//                            {display: "Created By", name : "username", width : 180, sortable : false, align: "left"},
					    {display: "Modified On", name : "createdOn", width : 180, sortable : false, align: "left"}
				    ],
				    buttons : [
					    {name: 'Edit', bclass: 'edit', onpress : editCurrencyConversion},
//                            {name: 'Delete', bclass: 'delete', onpress : deleteCurrencyConversion},
					    {name: 'Clear Results', bclass: 'clear-results', onpress : reloadGrid},
					    {separator: true}
				    ],
				    sortname: "fromCurrency",
				    sortorder: "asc",
				    usepager: true,
				    singleSelect: true,
				    title: 'All Currency Conversions',
				    useRp: true,
				    rp: 15,
				    showTableToggleBtn: false,
				    height: getGridHeight()-10
			    }
	    );
    }

    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        if (com == 'Clear Results') {
            $('#flex1').flexOptions({query: ''}).flexReload();
        }
    }

    function resetCurrencyConversionForm() {
        clearForm($("#currencyConversionForm"));
	    $("#create").html("<span class='k-icon k-i-plus'></span>Create");
    }

    function populateOnLoadFlexGrid() {
        var strUrl = "${createLink(controller: 'exhCurrencyConversion', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (currencyConversionListModel) {
            $("#flex1").flexAddData(currencyConversionListModel);
        }
    }


</script>