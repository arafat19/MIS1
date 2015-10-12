<script type="text/javascript">
    var dropDownSearchFieldName, noteModel;

    $(document).ready(function() {
        onLoadTaskDetailsPage();
        $('#taskNoteContainer').hide();
    });

    function onLoadTaskDetailsPage() {
        $("#property_name").kendoDropDownList({
            dataTextField: "name",
            dataValueField: "id"
        });
        dropDownSearchFieldName = $("#property_name").data("kendoDropDownList");
        $('#searchField').focus();
        $(document).attr('title', "ARMS - Task Details");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showTaskDetailsWithNote");
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
        if ($("#property_value").val().length < 4) {
            showError('Please enter at least 4 characters');
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }
    function executePostConditionForSearchTask() {
        $('#divTaskDetailsWithNote').show();
        onCompleteAjaxCall();
        var taskDetailsContent = $.trim($('#taskDetailsContainer').html());
        var taskGridContent = $.trim($('#taskGrid').html());
        if (taskDetailsContent.length == 0 && taskGridContent.length == 0){
            showError('Task not found');
            $('#divTaskDetailsWithNote').hide();
            return;
        }
        if (taskDetailsContent.length == 0){
            $('#taskNoteContainer').hide();
            return;
        }
        $('#taskId').val($('#hiddenTaskId').val());
        $('#taskNoteContainer').show();
    }
</script>