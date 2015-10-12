<script type="text/javascript">
    function onSubmitExhTaskCancelConfirmation() {
        var reason = $('#txtExhCancellationReason').attr('value');
        if (reason == null || reason.trim() == "") {
            showError('Enter cancellation reason');
            return false;
        }
        else {
	        $('#exhCancelTaskConfirmationModal').modal('hide');
            executeCancelTask(reason);
        }

        $('#txtExhCancellationReason').val('');
        $('#cancelConfirmationDialog').dialog('close');
        return false;
    }
    function exitExhTaskCancelForm() {
	    $('#txtExhCancellationReason').val('');
	    $('#exhCancelTaskConfirmationModal').modal('hide');
        return false;
    }

</script>

