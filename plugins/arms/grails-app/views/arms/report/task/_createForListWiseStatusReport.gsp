<%@ page import="com.athena.mis.utility.Tools; com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            List Wise Status Report
        </div>
    </div>

    <form id='filterPanelListWiseStatusForm' name='filterPanelListWiseStatusForm' class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     onchange="populateExchangeHouse()" tabindex="1" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate" tabindex="2" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse()">
                    </app:dateControl>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="exhHouseId">Exchange House:</label>

                <div class="col-md-3">
                    <rms:dropDownExchangeHouseFiltered
                            task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST + Tools.UNDERSCORE +
                                    RmsTaskStatusCacheUtility.DECISION_TAKEN + Tools.UNDERSCORE +
                                    RmsTaskStatusCacheUtility.DECISION_APPROVED + Tools.UNDERSCORE +
                                    RmsTaskStatusCacheUtility.CANCELED + Tools.UNDERSCORE +
                                    RmsTaskStatusCacheUtility.DISBURSED
                            }"
                            from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                            to_date="${DateUtility.getToDateForUI()}"
                            url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}"
                            data_model_name="dropDownExchangeHouse" name="exhHouseId" id="exhHouseId"
                            onchange="populateTaskList();" tabindex="3">
                    </rms:dropDownExchangeHouseFiltered>
                </div>


                <label class="col-md-2 control-label label-required" for="taskListId">Task List:</label>

                <div class="col-md-3">
                    <rms:dropDownTaskList data_model_name="dropDownTaskList"
                                          task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST + Tools.UNDERSCORE +
                                                  RmsTaskStatusCacheUtility.DECISION_TAKEN + Tools.UNDERSCORE +
                                                  RmsTaskStatusCacheUtility.DECISION_APPROVED + Tools.UNDERSCORE +
                                                  RmsTaskStatusCacheUtility.CANCELED + Tools.UNDERSCORE +
                                                  RmsTaskStatusCacheUtility.DISBURSED
                                          }"
                                          url="${createLink(controller: 'rmsTaskList', action: 'reloadTaskListDropDown')}"
                                          tabindex="4"
                                          id="taskListId" name="taskListId">
                    </rms:dropDownTaskList>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                </ul>
            </span>

        </div>
    </form>

</div>