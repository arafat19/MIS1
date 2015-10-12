<script language="javascript">
    var dropDownIdType;
    var backlogIdForNavigation = "${result.backlogMap?.id}";
    var noteCountForNavigation = "${result.backlogMap?.noteCount}";
    var countFlowForNavigation = "${result.backlogMap?.countFlow}";

    $(document).ready(function () {
        onLoadSearchBacklogDetails();

        $('#printPdfBtn').click(function () {
            downloadBacklogDetails();
        });
    });

    function onLoadSearchBacklogDetails() {
        $("#searchTaskForm").kendoValidator({validateOnBlur: false});
        dropDownIdType = initKendoDropdown($('#idType'), 'name', 'name', [
            {'name': 'Use Case ID'},
            {'name': 'Backlog ID'},
            {'name': 'URL'}
        ]);

        $("#backlogId").val(backlogIdForNavigation);
        if (backlogIdForNavigation) {
            dropDownIdType.value('Backlog ID');
            $('.download_icon_set').show();
            if (noteCountForNavigation > 0) {
                $('#lstNotesBacklog').show();
            }
        }

        // update page title
        $(document).attr('title', "MIS - Search Task");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#${leftMenu}");
    }

    function executePreConditionForSearchBacklogDetails() {
        showLoadingSpinner(true);
        // validate form data
        if ($("#backlogId").val() == '') {
            $('#updateBacklogDetailsDiv').hide();
            $('.download_icon_set').hide();
            showLoadingSpinner(false);
            return false;
        }
        return true;
    }
    // fires as onComplete action of searchTaskForm
    function executePostCondition() {
        showLoadingSpinner(false);
        if (isError == 'true') {
            $('.download_icon_set').hide();
        } else {
            $('.download_icon_set').show();
            if (noteCount > 0) {
                $('#lstNotesBacklog').show();
            }
        }
    }

    function downloadBacklogDetails() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'ptReport', action: 'downloadBacklogDetailsReport')}?backlogId=" + backlogId;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function addToMyTask() {
        showLoadingSpinner(true);
        if (!confirm('Do you want to add this task?')) {
            showLoadingSpinner(false);
            return false;
        }
        $.ajax({
            url: "${createLink(controller: 'ptBacklog', action: 'addToMyBacklog')}?ids=" + backlogId,
            success: function (data, textStatus) {
                executePostConditionForAddTask(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForAddTask(result) {
        if (result.isError) {
            showError(result.message);  // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            $('#addTask').hide();
            showSuccess(result.message);    // show success message
            showLoadingSpinner(false);  // stop loading spinner
            $('#loggedUser').show();
        }
    }

</script>