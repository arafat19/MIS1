<script language="javascript" type="text/javascript">
    var queryType = null;
    var queryString = null;
    var customerListModel;
    var dropDownSearchTypeId;

    $(document).ready(function () {
        onLoadSearchCustomerPage();
    });

    function onLoadSearchCustomerPage() {

        initializeForm($('#frmCustomerSearch'), onSubmitCustomerSearch);


        var output = ${output ? output : ''};
        if (output.isError) {
            showError(data.message);
            return false;
        }

        dropDownSearchTypeId = initKendoDropdown($('#searchTypeId'), null, null, output.lstSearchType);

        initFlexCustomer();
        setUrlFlex1();
        //update page title
        $(document).attr('title', "ARMS - Search Customer");
        loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhCustomer/showForCustomerByNameAndCode");
    }

    function onSubmitCustomerSearch() {
        trimFormValues($("#frmCustomerSearch"));

        if (dropDownSearchTypeId.value() == '') {
            showError("Select search option");
            dropDownSearchTypeId.focus();
            dropDownSearchTypeId.open();
            return false;
        }
        if ($("#queryString").val().length < 3) {
            showError("Search keyword must be 3 character or higher");
            $("#queryString").focus();
            return false;
        }

        setButtonDisabled($('#search'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);
        var searchTypeId = $('#searchTypeId').val();
        queryType = searchTypeId;
        queryString = $('#queryString').val();
        var actionUrl = "${createLink(controller:'exhCustomer', action: 'searchForCustomerByNameAndCode')}?queryType=" + queryType + "&queryString=" + queryString;

        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionForSearch(data);
            },
            complete: onCompleteAjaxCall(),
            dataType: 'json'
        });
        setButtonDisabled($('#search'), false);
        return false;
    }

    function executePostConditionForSearch(data) {
        customPopulateCustomerGrid(data);
        setUrlFlex1();
    }

    function initFlexCustomer() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 70, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: false, align: "right", hide: true},
                        {display: "Full Name", name: "fullName", width: 275, sortable: false, align: "left"},
                        {display: "Customer A/C No", name: "code", width: 100, sortable: false, align: "left"},
                        {display: "Address", name: "address", width: 250, sortable: false, align: "left"},
                        {display: "Phone Number", name: "phone", width: 100, sortable: false, align: "left"},
                        {display: "Photo Id No", name: "photoIdNo", width: 100, sortable: false, align: "left"}
                    ],
                    buttons: [
                        %{--<app:ifAllUrl urls="/exhBeneficiary/show">--}%
                        {name: 'Beneficiary', bclass: 'edit', onpress: showBeneficiary},
                        %{--</app:ifAllUrl>--}%
                        {separator: true}
                    ],
                    sortname: "name",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Customer List',
                    useRp: true,
                    rp: 20,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateCustomerGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function customPopulateCustomerGrid(data) {
        var gridObj = null
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObjCustomer;
        }
        $("#flex1").flexAddData(gridObj);
    }

    function setUrlFlex1() {
        if (queryType == null || queryString == null)
            return false;
        var strUrl = "${createLink(controller:'exhCustomer', action: 'searchForCustomerByNameAndCode')}?queryType=" + queryType + "&queryString=" + queryString;
        $("#flex1").flexOptions({url: strUrl});
    }

    function showBeneficiary(com, grid) {

        if(executeCommonPreConditionForSelect($('#flex1'),'customer')==false){
            return;
        }
        showLoadingSpinner(true);
        var customerId=getSelectedIdFromGrid($('#flex1'));
        var loc = "${createLink(controller: 'exhBeneficiary', action: 'show')}?customerId=" + customerId;
        $.history.load(formatLink(loc));
        return false;
    }

</script>