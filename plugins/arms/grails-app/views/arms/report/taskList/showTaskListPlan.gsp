<%@ page import="com.athena.mis.utility.Tools; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility; com.athena.mis.utility.DateUtility" %>
<g:render template="/arms/report/taskList/scriptTaskListPlan"/>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task List Plan
        </div>
    </div>

    <g:formRemote name='filterTaskListPlanForm' class="form-horizontal form-widgets" role="form"
              update="taskListPlanDiv" method="post"
              before="if (!executePreConditionForSubmit()) {return false;}" onComplete ="onCompleteAjaxCall()"
              url="${[controller:'rmsReport', action: 'searchTaskListPlan']}">
        <app:systemEntityByReserved name="currentStatus" typeId="${RmsTaskStatusCacheUtility.ENTITY_TYPE}"
                                    reservedId="${[
                                            RmsTaskStatusCacheUtility.DECISION_TAKEN,
                                            RmsTaskStatusCacheUtility.DECISION_APPROVED,
                                            RmsTaskStatusCacheUtility.DISBURSED
                                    ]}">
        </app:systemEntityByReserved>

        <div class="panel-body">
            <div class="col-md-7">
                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="fromDate">Start Date:</label>

                    <div class="col-md-5">
                        <app:dateControl name="fromDate" diffWithCurrent="-7" tabindex="1" onchange="populateExchangeHouse()">
                        </app:dateControl>
                    </div>

                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="exhHouseId">Exchange House:</label>

                    <div class="col-md-5">
                        <rms:dropDownExchangeHouseFiltered
                                task_status_list="${RmsTaskStatusCacheUtility.DECISION_TAKEN + Tools.UNDERSCORE +
                                        RmsTaskStatusCacheUtility.DECISION_APPROVED + Tools.UNDERSCORE +
                                        RmsTaskStatusCacheUtility.DISBURSED
                                }"
                                from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                to_date="${DateUtility.getToDateForUI()}"
                                url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}"
                                data_model_name="dropDownExchangeHouse" name="exhHouseId" id="exhHouseId"
                                onchange="populateTaskList();" tabindex="3">
                        </rms:dropDownExchangeHouseFiltered>
                    </div>

                </div>
            </div>

            <div class="col-md-5">
                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="toDate">End Date:</label>

                    <div class="col-md-7">
                        <app:dateControl name="toDate" tabindex="2" onchange="populateExchangeHouse()">
                        </app:dateControl>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="taskListId">Task List:</label>

                    <div class="col-md-7">
                        <rms:dropDownTaskList data_model_name="dropDownTaskList"
                                              task_status_list="${RmsTaskStatusCacheUtility.DECISION_TAKEN + Tools.UNDERSCORE +
                                                      RmsTaskStatusCacheUtility.DECISION_APPROVED + Tools.UNDERSCORE +
                                                      RmsTaskStatusCacheUtility.DISBURSED
                                              }"
                                              url="${createLink(controller: 'rmsTaskList', action: 'reloadTaskListDropDown')}"
                                              tabindex="4"
                                              id="taskListId" name="taskListId">
                        </rms:dropDownTaskList>
                    </div>
                </div>
            </div>

        </div>


        <div class="panel-footer">
            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </g:formRemote>
</div>

<div id="taskListPlanDiv" class="table-responsive" style="display: none">
</div>