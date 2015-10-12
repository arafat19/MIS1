<%@ page import="com.athena.mis.application.utility.NoteEntityTypeCacheUtility" %>
<div>
    <div class="col-md-7" style="padding-left: 0; padding-right: 5px" id="taskDetailsPanel">
        <div class="table-responsive" id="taskDetailsContainer">
            <rms:taskDetails task_object="${model?.taskObject}" id="taskDetails"
                             url="${createLink(controller: 'rmsTask', action: 'reloadTaskDetailsTagLib')}">
            </rms:taskDetails>
        </div>
    </div>
    <div class="col-md-5" style="padding-right: 0; padding-left: 5px" id="disbursePanel">
        <div class='panel panel-primary'>
            <div class="panel-heading">
                <div class="panel-title">
                    Disburse Task
                </div>
            </div>
            <g:formRemote name="disburseTaskForm" class="form-horizontal form-widgets" role="form"
                          before="if (!executePreConditionForDisburse()) {return false;}" method="post"
                          onSuccess="executePostConditionForDisburse(data)" onComplete="onCompleteAjaxCall()"
                          url="${[controller: 'rmsTask', action: 'disburseCashCollectionRmsTask']}">
                <div class="panel-body">
                    <input type="hidden" id="taskId" name="taskId" value="${model?.taskId}" />
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="idType">Identity Type:</label>
                        <div class="col-md-6">
                            <input class="k-textbox" id="idType" name="idType" tabindex="6"
                                   value= "${model?.idType}"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional" for="idNo">Identity No:</label>
                        <div class="col-md-6">
                            <input class="k-textbox" id="idNo" name="idNo" tabindex="7"
                                   value= "${model?.idNo}" />
                        </div>
                    </div>
                </div>
                <div class="panel-footer" id="panel-footer">
                    <button id="disburse" name="disburse" type="submit" data-role="button" class="k-button k-button-icontext"
                            role="button" tabindex="8"
                            aria-disabled="false"><span class="k-icon k-i-ok"></span>Disburse
                    </button>
                </div>
            </g:formRemote>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function() {
        var isDisbursable = "${model.isDisbursable}";
        if(isDisbursable == 'false') {
            $('#disbursePanel').remove();
            //$('#taskDetailsPanel').attr('class', 'col-md-12');
        }
    });

    function executePreConditionForDisburse() {
        $('#idType').val($.trim($('#idType').val()));
        $('#idNo').val($.trim($('#idNo').val()));
        if($('#taskId').val() == '') {
            showError("Please search task first");
            return false;
        }
        if ($("#idType").val() == '') {
            showError('Please enter Identity type');
            $('#note').focus();
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }

    function executePostConditionForDisburse(data) {
        data = eval("(" + data + ")");
        var isError = data.isError;
        var message = data.message;
        if(isError) {
            showError(message);
        } else {
            showSuccess(message);
            $('#taskDetails').reloadMe();
            $('#idType').attr('readOnly', true);
            $('#idNo').attr('readOnly', true);
            $('#panel-footer').remove();
        }
    }
</script>