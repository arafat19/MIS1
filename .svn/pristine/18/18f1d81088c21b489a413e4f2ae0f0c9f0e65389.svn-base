<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.exchangehouse.utility.ExhTaskTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.exchangehouse.utility.ExhTaskStatusCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
	<div class="panel-heading">
		<div class="panel-title">
			Filter Task
		</div>
	</div>
    <input id="hidLocalCurrency" type="hidden" value="<app:localCurrency property="symbol"/>">
	<form id='filterTaskForm' name='filterTaskForm' class="form-horizontal form-widgets" role="form">
		<div class="panel-body">

			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="createdDateFrom">Start Date:</label>

				<div class="col-md-3">
					<app:dateControl name="createdDateFrom" value="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}" tabindex="1" onchange="populateBank()">
					</app:dateControl>
				</div>
				<label class="col-md-2 control-label label-required" for="createdDateTo">End Date:</label>

				<div class="col-md-3">
					<app:dateControl name="createdDateTo" tabindex="2" onchange="populateBank()">
					</app:dateControl>
				</div>
			</div>

			<div class="form-group">
				<label class="col-md-2 control-label label-required" for="taskStatus">Status:</label>

				<div class="col-md-3">
					<app:dropDownSystemEntity dataModelName="dropDownTaskStatus" name="taskStatus"
                                              onchange="populateBank()" tabindex="3"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_EXH_TASK_STATUS}"
                    elements="${[
					        ExhTaskStatusCacheUtility.STATUS_NEW_TASK,
							ExhTaskStatusCacheUtility.STATUS_SENT_TO_BANK,
							ExhTaskStatusCacheUtility.STATUS_SENT_TO_OTHER_BANK,
							ExhTaskStatusCacheUtility.STATUS_RESOLVED_BY_OTHER_BANK,
							ExhTaskStatusCacheUtility.STATUS_CANCELLED_TASK
					]}">
					</app:dropDownSystemEntity>
				</div>
				<label class="col-md-2 control-label label-required" for="bankId">Bank:</label>

				<div class="col-md-3">
                    <exh:dropDownBankByTaskStatusAndTaskType data_model_name="dropDownBank" tabindex="4"
                                                             task_type="${ExhTaskTypeCacheUtility.TYPE_EXH_TASK}"
                                                             id="bankId" name="bankId"
                                                             from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                                             to_date="${DateUtility.getToDateForUI()}"
                                                             url="${createLink(controller: 'exhTask', action: 'reloadBankByTaskStatusAndTaskType')}">
                    </exh:dropDownBankByTaskStatusAndTaskType>
                </div>
			</div>
		</div>
		<div class="panel-footer">
			<button id="create" name="searchTask" id="searchTask" type="submit" data-role="button"
			        class="k-button k-button-icontext"
			        role="button" tabindex="5"
			        aria-disabled="false"><span class="k-icon k-i-search"></span>Search
			</button>
		</div>

	</form>

</div>

<table id="flex1" style="display:none"></table>