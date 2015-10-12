<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task Details
        </div>
    </div>

    <g:formRemote name='manageTaskForm' class="form-horizontal form-widgets" role="form"
                  update="taskDetailsContainer" method="post"
                  before="if (!executePreConditionForManageTask()) {return false;}"
                  onComplete="executePostConditionForManageTask()"
                  url="${[controller: 'rmsTask', action: 'renderTaskDetails']}">
        <div class="panel-body">
            <div class="form-group">
                <input type="hidden" value="refNo" id="property_name" name="property_name">
                <input type="hidden" value="" id="navTaskId" name="navTaskId">
                %{--2 params for controller to render taskDetails tagLib--}%
                <input type="hidden" value="taskDetails"  name="id">
                <input type="hidden" value="${createLink(controller: 'rmsTask', action: 'reloadTaskDetailsTagLib')}" name="url">
                <label class="col-md-1 control-label label-required" for="property_value">Ref No:</label>

                <div class="col-md-3">
                    <input class="k-textbox" id="property_value" name="property_value" tabindex="1"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            &nbsp;
            <g:remoteLink before="if(!executePreConditionForProcess()){return false;}"
                          controller="rmsTask" action="show" name="editTask"
                          params= "{navTaskId:\$('#navTaskId').val()}"
                          onLoading="showLoadingSpinner(true)"
                          class="k-button k-button-icontext"
                          onComplete="showLoadingSpinner(false)"
                          update="contentHolder">Edit Task</g:remoteLink>
            &nbsp;
            <button id="cancel" name="cancelTask" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3" onclick="addCancelNote()"
                    aria-disabled="false">Cancel Task
            </button>
        </div>
    </g:formRemote>
</div>

<div id="taskDetailsContainer" class="table-responsive" style="display: block">
</div>