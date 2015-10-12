<script type="text/javascript">
    var dropDownSearchFieldName , taskId;

    $(document).ready(function () {
        onLoadManageTask();
    });

    function onLoadManageTask() {
        $('#property_value').focus();
        $('[name="editTask"]').hide();
        $('#cancel').hide();
        $(document).attr('title', "ARMS - Task Details");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTask/showForManageTask");
    }

    function executePreConditionForManageTask() {
        $('#property_value').val($.trim($('#property_value').val()));
        if ($("#property_value").val() == '') {
            showError('Please enter Ref No to search task');
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }
    function executePostConditionForManageTask() {
        var taskDetailsContent = $.trim($('#taskDetailsContainer').html());
        $('[name="editTask"]').hide();
        $('#cancel').hide();
        showLoadingSpinner(false);
        if (taskDetailsContent.length == 0) {
            showError('Task not found');
            $('#taskDetailsContainer').hide();
            return;

        }
        $('#navTaskId').val($('#hiddenTaskId').val());
        $('#taskDetailsContainer').show();
        $('[name="editTask"]').show();
        $('#cancel').show();
    }
    function executePreConditionForProcess() {
        if ($('#navTaskId').val() == '') {
            showError('Task not found');
            return false;
        }
        return true;
    }
    function addCancelNote(){
        $('#noteForCancelModalTask').modal('show');    // show Modal
    }
    function cancelTask(){
        if (executePreConditionForCancelTaskFromGrid() == false) {
            return false;
        }
        showLoadingSpinner(true);
        var navTaskId = $('#navTaskId').val();
        var revisionNote= $('#txtTaskCancelReason').val();
        var params = "?navTaskId=" + navTaskId +"&revisionNote="+revisionNote;
        var strUrl = "${createLink(controller:'rmsTask',action: 'cancelRmsTask')}" + params;
        // fire ajax method for getting task
        $.ajax({
            url: strUrl,
            success: executePostConditionForCancelTask,
            complete: function () {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
        $('#txtTaskCancelReason').val('');
        $('#noteForCancelModalTask').modal('hide');
    }
    function executePreConditionForCancelTaskFromGrid() {
        $('#txtTaskCancelReason').val($.trim($('#txtTaskCancelReason').val()));
        if ( $('#txtTaskCancelReason').val() == '') {
            showError('Cancellation note needed');
            return false;
        }
        return true;
    }
    function executePostConditionForCancelTask(data){
        if (data.isError == false) {
            showSuccess(data.message);
        } else {
            showError(data.message);
        }
        $('#taskDetails').reloadMe();
    }
    function closeCancelModal() {
        $('#txtTaskCancelReason').val('');
        $('#noteForCancelModalTask').modal('hide');
        return false;
    }


</script>