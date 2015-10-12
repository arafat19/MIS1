<script language="javascript">
    var bugIdForNavigate,ownerForNavigate, actor, purpose, benefit,lstBugContentForNavigation;
    $(document).ready(function () {
        onLoadBugDetailsPage();
    });

    // method called on page load
    function onLoadBugDetailsPage() {
        $("#searchForm").kendoValidator({validateOnBlur: false});
        lstBugContentForNavigation = ${result.contentMapList?result.contentMapList:[]};
        bugIdForNavigate = "${result.bugMap?.id}";
        ownerForNavigate = "${result.bugMap?.owner}";
        var sprintId = "${result.bugMap?.sprintId}";
        actor = "${result.bugMap?.actor}";
        var message = "${result.message}";

        if(lstBugContentForNavigation.length > 0){
            $('#listAttachment').show();
        }else{
            $('#listAttachment').hide();
        }
        if(ownerForNavigate == '' && sprintId > 0){
            $('#addBug').show();
        }else{
            $('#addBug').hide();
        }
        $("#lstAttachment").kendoListView({
            dataSource: lstBugContentForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td><a href='#:link#' title='Click to download'>#:fileName#</a></td>" +
                    "</tr>"
        });

        $('#printPdfBtn').click(function () {
            downloadBugDetails();
        });

        // update page title
        $(document).attr('title', "MIS - Bug Details");
        loadNumberedMenu(MENU_ID_PROJECT_TRACK, "#ptBug/showBugDetails");
    }
    // check pre condition before submitting the form
    function executePreCondition() {
        // validate form data
        if (!validateForm($("#searchForm"))) {
            $('#updateBugDiv').hide();
            $('.download_icon_set').hide();
            return false;
        }
        return true;
    }

    function executePostCondition() {
        if (isError == 'true') {
            showError(message);
            $('#updateBugDiv').hide();
            $('.download_icon_set').hide();
            return false;
        }
        bugIdForNavigate = bugId;
        if(lstBugContent.length > 0){
            $('#listAttachment').show();
        }
    }

    function downloadBugDetails() {
        showLoadingSpinner(true);
        if (confirm('Do you want to download the PDF now?')) {
            var url = "${createLink(controller: 'ptReport', action: 'downloadPtBugDetails')}?bugId=" + bugIdForNavigate;
            document.location = url;
        }
        showLoadingSpinner(false);
        return false;
    }

    function addToMyBug() {
        showLoadingSpinner(true);
        if (!confirm('Do you want to add this bug?')) {
            showLoadingSpinner(false);
            return false;
        }
        if(actor != ''){
            showError('Only orphan bug could be added');
            showLoadingSpinner(false);
            return false;
        }
        $.ajax({
            url: "${createLink(controller: 'ptBug', action: 'addToMyBug')}?ids=" + bugId,
            success: function (data, textStatus) {
                postConditionForAddToMyBug(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json',
            type: 'post'
        });
    }

    function postConditionForAddToMyBug(result) {
        if (result.isError) {
            showError(result.message);  // show error message in case of error
            showLoadingSpinner(false);  // stop loading spinner
        } else {
            $('#addBug').hide();
            showSuccess(result.message);    // show success message
            showLoadingSpinner(false);  // stop loading spinner
            $('#loggedUser').show();
        }
    }

</script>
