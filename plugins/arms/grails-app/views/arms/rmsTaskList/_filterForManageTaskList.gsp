<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Manage Task List
        </div>
    </div>

    <form id='filterPanelForManageTaskList' name='filterPanelForManageTaskList' class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     tabindex="1" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse();">
                    </app:dateControl>
                </div>

                <label class="col-md-2 col-md-offset-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate" tabindex="2" placeholder="dd/MM/yyyy"
                                     onchange="populateExchangeHouse();">
                    </app:dateControl>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="currentStatus">Status:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownTaskStatus" id="currentStatus"
                                              name="currentStatus"
                                              typeId="${SystemEntityTypeCacheUtility.ARMS_TASK_STATUS}"
                                              elements="[
                                                      RmsTaskStatusCacheUtility.INCLUDED_IN_LIST,
                                                      RmsTaskStatusCacheUtility.DECISION_TAKEN,
                                                      RmsTaskStatusCacheUtility.DECISION_APPROVED,]"
                                              showHints="false" tabindex="3"
                                              onchange="populateExchangeHouse();">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-3 control-label label-required" for="exchangeHouseId">Exchange House:</label>

                <div class="col-md-3">
                    <rms:dropDownExchangeHouseFiltered task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST}"
                                                       from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                                       to_date="${DateUtility.getToDateForUI()}"
                                                       data_model_name="dropDownExchangeHouse" name="exchangeHouseId"
                                                       onchange="populateTaskList();" tabindex="4"
                                                       id="exchangeHouseId"
                                                       url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}">
                    </rms:dropDownExchangeHouseFiltered>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="taskListId">Task List:</label>

                <div class="col-md-3">
                    <rms:dropDownTaskList task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST}"
                                          from_date="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                          to_date="${DateUtility.DATE_RANGE_SEVEN}"
                                          data_model_name="dropDownTaskList" name="taskListId"
                                          tabindex="5"
                                          id="taskListId"
                                          url="${createLink(controller: 'rmsTaskList', action: 'reloadTaskListDropDown')}">
                    </rms:dropDownTaskList>
                </div>

            </div>

        </div>

        <div class="panel-footer">
            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <span class="pull-right">
                <label class="control-label label-optional" for="newTaskList" id="lblRenameList">New List Name:</label>
                <input type="text" id="newTaskList" name="newTaskList"/>
                <label class="control-label label-optional" for="anotherTaskList"
                       id="lblMoveToAnotherList">Move to:</label>
                <input type="text" id="anotherTaskList" name="anotherTaskList"/>
                <a href="javascript:void(0);" id="closeInput" class="k-button"><span class="k-icon k-i-close"></span>
                </a>
            </span>
        </div>
    </form>
</div>