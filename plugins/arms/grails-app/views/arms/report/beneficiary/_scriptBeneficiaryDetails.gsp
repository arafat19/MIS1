<script type="text/javascript">
    var dropDownSearchFieldName;
    var lstBeneficiary = false;

    $(document).ready(function() {
        initFlex();
        var fromDate = $("#from_date").val();
        var toDate = $("#to_date").val();
        var propertyValue = $("#property_value").val();
        var propertyName = $("#property_name").val();
        if(propertyValue == '') {
            return;
        }
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&propertyValue=" + propertyValue + "&propertyName=" + propertyName;
        var strUrl = "${createLink(controller: 'rmsReport', action: 'searchBeneficiaryDetails')}" + params;
        $("#flex").flexOptions({url: strUrl});
        $('#flex').flexOptions({query: ''}).flexReload();
    });

    // initialize the grid
    function initFlex(strUrl) {
        $("#flex").flexigrid
        (
            {
                url: strUrl,
                dataType: 'json',
                colModel: [
                    {display: "SL", name: "serial", width: 30, sortable: false, align: "right"},
                    {display: "Ref No", name: "refNo", width: 90, sortable: false, align: "left"},
                    {display: "Beneficiary Name", name: "beneficiaryName", width: 120, sortable: false, align: "left"},
                    {display: "Amount", name: "amount", width: 85, sortable: false, align: "right"},
                    {display: "Outlet", name: "outlet", width: 170, sortable: false, align: "left"}
                ],
                buttons: [
                    {name:'View Details', bclass: 'view', onpress: viewTaskDetails}
                ],
                sortname: "beneficiaryName",
                sortorder: "asc",
                usepager: true,
                singleSelect: true,
                title: 'Beneficiary Information',
                useRp: true,
                rp: 15,
                showTableToggleBtn: false,
                height: getGridHeight() - 10,
                afterAjax: function () {
                    afterAjaxError();
                    showLoadingSpinner(false);
                },
                customPopulate: populateFlex
            }
        );
    }
    // set link to grid url to populate data
    function populateFlex(data) {
        if (data.isError) {
            showError(data.message);
            lstBeneficiary = getEmptyGridModel();
        } else {
            lstBeneficiary = data.gridObj;
        }
        $("#flex").flexAddData(lstBeneficiary);
    }
    function viewTaskDetails() {
        if (executeCommonPreConditionForSelect($('#flex'), 'beneficiary') == false) {
            return;
        }
        showLoadingSpinner(true);       // start loading spinner
        var id = getSelectedIdFromGrid($('#flex'));
        var params = "?property_name=" + "id" +  "&property_value=" + id;
        $.ajax({
            url: "${createLink(controller:'rmsTask', action: 'renderTaskDetails')}" + params,
            success: function (data, textStatus) {$('#beneficiaryDetailsContainer').html(data)} ,
            complete: onCompleteAjaxCall(),
            dataType: 'html',
            type: 'post'
        });
    }
</script>