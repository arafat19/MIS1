<script type="text/javascript">

    function onSubmitTaskConfirmation() {
        onSubmitTask(true);
	    $('#taskCreateConfirmationDialog').modal('hide');
        return false;
    }
    function exitTaskCreateConfirmForm() {
        $("#taskCreateConfirmationErrorList").html('');
	    $('#taskCreateConfirmationDialog').modal('hide');
        return false;
    }

</script>

