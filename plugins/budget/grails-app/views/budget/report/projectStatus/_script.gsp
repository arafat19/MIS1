<script type="text/javascript">

    $(document).ready(function () {
        onLoadProjectStatus();
        $('#printProjectStatus').click(function () {
            printProjectStatus();
            return false;
        });
    });

    function onLoadProjectStatus() {
        $('.download_icon_set').hide();
        $("#searchForm").kendoValidator({validateOnBlur: false});
        // update page title
        $(document).attr('title', "MIS - Project Status");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgReport/showProjectStatus");
    }

    function printProjectStatus() {
        var projectId = $('#hideProjectId').val();
        if (projectId.length <= 0) {
            showError('First populate project status click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?projectId=" + projectId;
        if (confirm('Do you want to download the project status now?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadProjectStatus')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }
    function executePreCondition() {
        showLoadingSpinner(true);
        // validate form data
        if ($("#projectId").val() == '') {
            $('#divProject').hide();
            $('#divProjectStatus').hide();
            $('.download_icon_set').hide();
            showLoadingSpinner(false);
            return false;
        }
        return true;
    }
    function executePostCondition() {
        showLoadingSpinner(false);
        if (isError == 'true') {
            $('#divProjectStatus').hide();
            $('.download_icon_set').hide();
            return;
        }else{
            $('#divProjectStatus').show();
            $('.download_icon_set').show();
        }
        return false;
    }
</script>