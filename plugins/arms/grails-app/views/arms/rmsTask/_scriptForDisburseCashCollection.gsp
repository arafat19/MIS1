<script type="text/javascript">
    var dropDownPropertyName;

    $(document).ready(function () {
        onLoadDisburseCashCollection();
    });

    function onLoadDisburseCashCollection() {
        $("#property_name").kendoDropDownList();
        dropDownPropertyName = $("#property_name").data("kendoDropDownList");

        $(document).attr('title', "ARMS - Disburse Cash Collection Task");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showDisburseCashCollection");
    }

    function executePreForDisburseCashCollection() {
        if (!customValidateDate($("#from_date"), 'from date', $("#to_date"), 'to date')) {
            return false;
        }
        $("#property_value").val($.trim($("#property_value").val()));
        var propertyValue = $("#property_value").val();
        if (propertyValue == '') {
            showError('Please enter Ref No or Pin No to search task');
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }

    function executePostForDisburseCashCollection() {
        var taskDetailsContent = $.trim($('#taskDetailsContainer').html());
        if (taskDetailsContent.length == 0){
            showError('Task not found');
            $('#divTaskDetailsWithDisburse').hide();
            return;
        }
        $('#taskId').val($('#hiddenTaskId').val());
        $('#divTaskDetailsWithDisburse').show();
    }
</script>