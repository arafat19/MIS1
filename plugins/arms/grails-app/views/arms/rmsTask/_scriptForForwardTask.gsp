<script type="text/javascript">
    var dropDownPropertyName;

    $(document).ready(function() {
        onLoadTaskDetailsPage();
    });

    function onLoadTaskDetailsPage() {
        $("#property_name").kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownPropertyName = $("#property_name").data("kendoDropDownList");
        $('#property_value').focus();
        $(document).attr('title', "ARMS - Task Details");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showTaskDetailsForForward");
    }

    function executePreConditionForSearchTask() {
        if (!customValidateDate($("#from_date"), 'from date', $("#to_date"), 'to date')) {
            return false;
        }
        $('#property_value').val($.trim($('#property_value').val()));
        if ($("#property_value").val() == '') {
            showError('Please enter Ref No or Pin No to search task');
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }
    function executePostConditionForSearchTask() {
        var taskDetailsContent = $.trim($('#taskDetailsContainer').html());
        if (taskDetailsContent.length == 0){
            showError('Task not found');
            $('#divTaskDetailsForForward').hide();
            return;
        }
        $('#divTaskDetailsForForward').show();
    }
</script>