<script language="javascript">
    var isError, bugId, owner,lstBugContent;
    $(document).ready(function () {
        isError = "${result.isError}";
        bugId = "${result.bugMap?.id}";
        owner = "${result.bugMap?.owner}";
        var sprintId = "${result.bugMap?.sprintId}";
        lstBugContent = ${result.contentMapList?result.contentMapList:[]};
        var message = "${result.message}";

        if (isError == 'true') {
            showError(message);
            $('#updateBugDiv').hide();
            $('.download_icon_set').hide();
            return false;
        }
        if (bugId > 0) {
            $('#updateBugDiv').show();
            $('#divBug').show();
            $('#hidBugId').val(bugId);
            $('#id').val(bugId);
            $('.download_icon_set').show();
        }
        if (owner == '' && sprintId > 0) {
            $('#addBug').show();
        } else {
            $('#addBug').hide();
        }

        $("#lstAttachment").kendoListView({
            dataSource: lstBugContent,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td><a href='#:link#' title='Click to download'>#:fileName#</a></td>" +
                    "</tr>"
        });
    });
</script>

<div id="divBug" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Details</div>

    <div class="panel-body">
        <div class="panel panel-default">
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td class="active">Project</td>
                    <td>${result.bugMap?.project}</td>
                    <td class="active">Module</td>
                    <td>${result.bugMap?.module}</td>
                </tr>
                <tr>
                    <td class="active">Sprint</td>
                    <td>${result.bugMap?.sprint}</td>
                    <td class="active">Type</td>
                    <td>${result.bugMap?.type}</td>
                </tr>
                <tr>
                    <td class="active">Status</td>
                    <td><b>${result.bugMap?.status}</b></td>
                    <td class="active">Severity</td>
                    <td>${result.bugMap?.severity}</td>
                </tr>

                <tr>
                    <td class="active">Created By</td>
                    <td>${result.bugMap?.createdBy}</td>
                    <td class="active">Created On</td>
                    <td>${result.bugMap?.createdOn}</td>
                </tr>
                <tr>
                    <td class="active">Fixed By</td>
                    <td>${result.bugMap?.fixedBy}</td>
                    <td class="active">Fixed On</td>
                    <td>${result.bugMap?.fixedOn}</td>
                </tr>
                <tr>
                    <td class="active">Closed By</td>
                    <td>${result.bugMap?.closedBy}</td>
                    <td class="active">Closed On</td>
                    <td>${result.bugMap?.closedOn}</td>
                </tr>
                <tr>
                    <td class="active">Use Case ID</td>
                    <td>${result.bugMap?.useCaseId}</td>
                    <td class="active">Updated By</td>
                    <td>${result.bugMap?.updatedBy}</td>
                </tr>
                <tr>
                    <td class="active">Owner</td>
                    <td>${result.bugMap?.owner}
                        <span id="loggedUser" hidden="hidden">${result.bugMap?.loggedUser}</span>
                        <sec:access url="/ptBug/addToMyBug">
                            <button id="addBug" name="addBug" type="button" data-role="button"
                                    class="k-button k-button-icontext" role="button"
                                    aria-disabled="false" onclick='addToMyBug();'><span class="k-icon k-i-plus"></span>
                                Add to my Bug List
                            </button>
                        </sec:access>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="panel panel-default">
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td class="active">Task</td>
                    <td><pt:backlogIdea id="idea" backlogId="${result.bugMap?.backlogId}">
                    </pt:backlogIdea></td>
                </tr>

                <tr>
                    <td class="active">Title</td>
                    <td>${result.bugMap?.title}</td>
                </tr>
                <tr>
                    <td class="active">Step to Reproduce</td>
                    <td>${result.bugMap?.stepToReproduce}</td>
                </tr>
                <tr>
                    <td class="active">Note</td>
                    <td>${result.bugMap?.note}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div id="listAttachment" class="panel panel-default" style="display: none;">
            <div class="panel-heading">Attachment List</div>

            <div class="panel-body">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>SL#</th>
                        <th>File Name</th>
                    </tr>
                    </thead>
                    <tbody id="lstAttachment">
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>


