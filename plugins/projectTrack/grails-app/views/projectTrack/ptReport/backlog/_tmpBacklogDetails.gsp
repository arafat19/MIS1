<%@ page import="com.athena.mis.projecttrack.utility.PtAcceptanceCriteriaTypeCacheUtility; com.athena.mis.application.utility.NoteEntityTypeCacheUtility" %>
<script language="javascript">
    var linkBugDetails, linkACDetails, isError, backlogId, noteCount;
    $(document).ready(function () {
        isError = "${result.isError}";
        backlogId = "${result.backlogMap?.id}";
        noteCount = "${result.backlogMap?.noteCount}";
        var owner = "${result.backlogMap?.owner}";
        var sprintId = "${result.backlogMap?.sprintId}";
        var message = "${result.message}";
        var bugCount = "${result.backlogMap?.bugCount}";
        var acCount = "${result.backlogMap?.acCount}";
        linkBugDetails = "${createLink(controller: 'ptBug', action: 'show')}?backlogId=" + backlogId + "&leftMenu=" + '${leftMenu}';
        linkACDetails = "${createLink(controller: 'ptAcceptanceCriteria', action: 'show')}?backlogId=" + backlogId + "&leftMenu=" + '${leftMenu}';

        if (isError == 'true') {
            showError(message);
            $('#updateBacklogDetailsDiv').hide();
            return false;
        }
        if(owner == '' && sprintId > 0){
            $('#addTask').show();
        }else{
            $('#addTask').hide();
        }
        if (backlogId > 0) {
            $('#updateBacklogDetailsDiv').show();
        }
        if (bugCount == 0) {
            $('#bugCountZeroLink').show();
            $('#bugCountLink').hide();
        }
        if (acCount == 0) {
            $('#acCountZeroLink').show();
            $('#acCountLink').hide();
        }
    });
</script>

<div class="panel panel-default">
    <div class="panel-heading">Details</div>

    <div class="panel-body">
        <div class="panel panel-default">
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td class="active">Idea</td>
                    <td><pt:backlogIdea id="idea" backlogId="${result.backlogMap?.id}">
                    </pt:backlogIdea></td>
                    <td class="active">Status</td>
                    <td>${result.backlogMap?.status}</td>
                </tr>

                <tr>
                    <td class="active">Bug Count</td>
                    <td><span id="bugCountLink">
                        ${result.backlogMap?.bugCount}<span style="cursor:pointer;"
                                                            onclick="$.history.load(formatLink(linkBugDetails));"><a>&nbsp;&nbsp;( Show details )</a>
                        </span></span>
                        <span id="bugCountZeroLink" hidden="hidden" style="cursor:pointer;"
                              onclick="$.history.load(formatLink(linkBugDetails));">
                            ${result.backlogMap?.bugCount} <a>&nbsp;&nbsp;( Click to post bug )</a>
                        </span>
                    </td>
                    <td class="active">A.C Count</td>
                    <td><span id="acCountLink">
                        ${result.backlogMap?.acCount}<span style="cursor:pointer;"
                                                           onclick="$.history.load(formatLink(linkACDetails));"><a>&nbsp;&nbsp;( Show details )</a>
                        </span></span>
                        <span id="acCountZeroLink" hidden="hidden" style="cursor:pointer;"
                              onclick="$.history.load(formatLink(linkACDetails));">
                            ${result.backlogMap?.acCount} <a>&nbsp;&nbsp;( Click to create Acceptance Criteria )</a>
                        </span>
                    </td>

                </tr>
                <tr>
                    <td class="active">Use Case ID</td>
                    <td><b>${result.backlogMap?.useCaseUrl}</b></td>
                    <td class="active">Owner</td>
                    <td>${result.backlogMap?.owner}
                        <span id="loggedUser" hidden="hidden">${result.backlogMap?.loggedUser}</span>
                        <sec:access url="/ptBacklog/addToMyBacklog">
                            <button id="addTask" name="addTask" type="button" data-role="button"
                                    class="k-button k-button-icontext" role="button"
                                    aria-disabled="false" onclick='addToMyTask();'><span class="k-icon k-i-plus"></span>
                                Add to my Task List
                            </button>
                        </sec:access>
                    </td>
                </tr>
                <tr>
                    <td class="active">Url</td>
                    <td><b>${result.backlogMap?.url}</b></td>
                    <td class="active">Priority</td>
                    <td>${result.backlogMap?.priority}</td>
                </tr>
                <tr>
                    <td class="active">Module</td>
                    <td>${result.backlogMap?.module}</td>
                    <td class="active">Sprint</td>
                    <td>${result.backlogMap?.sprint}</td>
                </tr>

                <tr>
                    <td class="active">Created By</td>
                    <td>${result.backlogMap?.createdBy}</td>
                    <td class="active">Created On</td>
                    <td>${result.backlogMap?.createdOn}</td>
                </tr>
                <tr>
                    <td class="active">Completed By</td>
                    <td>${result.backlogMap?.completedBy}</td>
                    <td class="active">Completed On</td>
                    <td>${result.backlogMap?.completedOn}</td>
                </tr>

                <tr>
                    <td class="active">Accepted By</td>
                    <td>${result.backlogMap?.acceptedBy}</td>
                    <td class="active">Accepted On</td>
                    <td>${result.backlogMap?.acceptedOn}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<pt:flow
        id="lstFlow"
        backlog_id="${result.backlogMap?.id}">
</pt:flow>

<pt:acceptanceCriteria
        id="lstPreCondition"
        type_id="${PtAcceptanceCriteriaTypeCacheUtility.PRE_CONDITION_RESERVED_ID}"
        backlog_id="${result.backlogMap?.id}">
</pt:acceptanceCriteria>

<pt:acceptanceCriteria
        id="lstBusinessLogic"
        type_id="${PtAcceptanceCriteriaTypeCacheUtility.BUSINESS_LOGIC_RESERVED_ID}"
        backlog_id="${result.backlogMap?.id}">
</pt:acceptanceCriteria>

<pt:acceptanceCriteria
        id="lstPostCondition"
        type_id="${PtAcceptanceCriteriaTypeCacheUtility.POST_CONDITION_RESERVED_ID}"
        backlog_id="${result.backlogMap?.id}">
</pt:acceptanceCriteria>

<pt:acceptanceCriteria
        id="lstOthers"
        type_id="${PtAcceptanceCriteriaTypeCacheUtility.OTHERS_RESERVED_ID}"
        backlog_id="${result.backlogMap?.id}">
</pt:acceptanceCriteria>

<div id="lstNotesBacklog" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Notes</div>

    <div class="panel-body">
        <app:entityNote
                id="lstBacklogNotes"
                entity_type_id="${NoteEntityTypeCacheUtility.COMMENT_ENTITY_TYPE_PT_TASK}"
                entity_id="${result.backlogMap?.id}">
        </app:entityNote>
    </div>
</div>