<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility; com.athena.mis.arms.utility.RmsProcessTypeCacheUtility; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Issue EFT
        </div>
    </div>

    <app:systemEntityByReserved name="hidApprovedStatus" reservedId="${RmsTaskStatusCacheUtility.DECISION_APPROVED}" typeId="${RmsTaskStatusCacheUtility.ENTITY_TYPE}">
    </app:systemEntityByReserved>
    <form id='filterPanelIssueEftForm' name='filterPanelIssueEftForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="taskStatus">Status:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownTaskStatus" id="taskStatus"
                                              name="taskStatus" typeId="${SystemEntityTypeCacheUtility.ARMS_TASK_STATUS}"
                                              elements="[RmsTaskStatusCacheUtility.DECISION_APPROVED,RmsTaskStatusCacheUtility.DISBURSED]"
                                              onchange="populateExchangeHouse()"
                                              showHints="false" tabindex="1">
                    </app:dropDownSystemEntity>
                </div>
                <div class="col-md-7">
                    <app:systemEntityByReserved name="hidProcessType"
                                                reservedId="${RmsProcessTypeCacheUtility.ISSUE}"
                                                typeId="${SystemEntityTypeCacheUtility.ARMS_PROCESS_TYPE}">
                    </app:systemEntityByReserved>
                    <app:systemEntityByReserved name="hidInstrumentType"
                                                reservedId="${RmsInstrumentTypeCacheUtility.EFT}"
                                                typeId="${SystemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE}">
                    </app:systemEntityByReserved>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     onchange="populateExchangeHouse()" tabindex="2" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate" tabindex="3" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse()">
                    </app:dateControl>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="exhHouseId">Exchange House:</label>

                <div class="col-md-3">

                    <rms:dropDownExchangeHouseFiltered task_status_list="${RmsTaskStatusCacheUtility.DECISION_APPROVED}"
                                                       from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                                       to_date="${DateUtility.getToDateForUI()}"
                                                       process="${RmsProcessTypeCacheUtility.ISSUE}"
                                                       instrument="${RmsInstrumentTypeCacheUtility.EFT}"
                                                       data_model_name="dropDownExchangeHouse" name="exhHouseId"
                                                       id="exhHouseId"
                                                       url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}"
                                                       tabindex="4" onchange="populateTaskList();">
                    </rms:dropDownExchangeHouseFiltered>

                </div>

                <label class="col-md-2 control-label label-required" for="taskListId">Task List:</label>

                <div class="col-md-3">
                    <rms:dropDownTaskList data_model_name="dropDownTaskList"
                                          task_status_list="${RmsTaskStatusCacheUtility.DECISION_APPROVED}"
                                          process="${RmsProcessTypeCacheUtility.ISSUE}"
                                          instrument="${RmsInstrumentTypeCacheUtility.EFT}"
                                          url="${createLink(controller: 'rmsTaskList', action: 'reloadTaskListDropDown')}"
                                          id="taskListId" name="taskListId">
                    </rms:dropDownTaskList>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <span class="download_icon_set">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                </ul>
            </span>

        </div>
    </form>

</div>