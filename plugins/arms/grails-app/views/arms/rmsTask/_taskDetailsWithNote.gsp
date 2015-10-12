<%@ page import="com.athena.mis.application.utility.NoteEntityTypeCacheUtility" %>
<div>
    <div id="taskGrid">
        <rms:listTaskByRefOrPin task_list="${model?.task_list}" />
    </div>
    <div class="col-md-6" style="padding-left: 0; padding-right: 5px">
        <div class="table-responsive" id="taskDetailsContainer">
            <rms:taskDetails task_object="${model?.task_object}" id="taskDetails"
                             url="${createLink(controller: 'rmsTask', action: 'reloadTaskDetailsTagLib')}">
            </rms:taskDetails>
        </div>
    </div>
    <div class="col-md-6" style="padding-right: 0; padding-left: 5px; " id="taskNoteContainer">
        <table class="table table-condensed" style="margin-bottom: 0px">
            <tbody>
            <tr>
                <td colspan="2" style="border-top: 0;padding-top:0">
                <g:formRemote name='taskNoteForm' class="form-horizontal form-widgets" role="form" method="post"
                              before="if (!executePreConditionForAddNote()) {return false;}" onSuccess ="executePostConditionForAddNote(data)" onComplete="onCompleteAjaxCall()"
                              url="${[controller: 'rmsTask', action: 'createRmsTaskNote']}">
                        <div class="panel panel-primary">
                            <input type="hidden" value="${model?.task_id}" id="taskId" name="taskId">
                            <div class="panel-heading">
                                <div class="panel-title">
                                    Task Notes
                                </div>
                            </div>
                            <div class="panel-body">
                                <textarea style="width: 100%" class="k-textbox" name="note" id="note" rows="3" tabindex="6"></textarea>
                            </div>
                            <div class="panel-footer">
                                <button aria-disabled="false" tabindex="7" role="button" class="k-button k-button-icontext" data-role="button" type="submit" name="search" id="add"><span class="k-icon k-i-plus"></span>Add Note
                                </button>
                            </div>
                        </div>
                </g:formRemote>
                </td>
            </tr>
            <tr>
                <td colspan="2" style="border-top: 0">
                    <app:entityNote id="listView" entity_id="${model?.task_id}" order="desc"
                                    url="${createLink(controller: 'entityNote', action: 'reloadEntityNote')}"
                                    entity_type_id="${NoteEntityTypeCacheUtility.ENTITY_TYPE_RMS_TASK}">
                    </app:entityNote>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<script type="text/javascript">
    function executePreConditionForAddNote() {
        $('#note').val($.trim($('#note').val()));
        if($('#taskId').val() == '') {
            showError("Please search task first");
            return false;
        }
        if ($("#note").val() == '') {
            showError('Please enter task note');
            $('#note').focus();
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }

    function executePostConditionForAddNote(data) {
        data = eval("(" + data + ")");
        var isError = data.isError;
        var message = data.message;
        if(isError) {
            showError(message);
        } else {
            showSuccess(message);
            $("#note").val('');
            $('#note').focus();
            $('#listView').reloadMe();
        }
    }
</script>