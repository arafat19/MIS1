<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task Instrument Mapping
        </div>
    </div>

    <form id='filterPanelMapTaskForm' name='filterPanelMapTaskForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <app:systemEntityByReserved name="currentStatus"
                                            reservedId="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST}"
                                            typeId="${SystemEntityTypeCacheUtility.ARMS_TASK_STATUS}">
                </app:systemEntityByReserved>
                <label class="col-md-2 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     tabindex="1" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse()">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate" tabindex="2" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse()">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="isRevised">Revised:</label>

                <div class="col-md-1">
                    <g:checkBox tabindex="3" name="isRevised"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="exhHouseId">Exchange House:</label>

                <div class="col-md-3">
                    <rms:dropDownExchangeHouseFiltered task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST}"
                                                       from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                                       to_date="${DateUtility.getToDateForUI()}"
                                                       data_model_name="dropDownExchangeHouse" name="exhHouseId" id="exhHouseId"
                                                       add_balance="true"
                                                       url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}"
                                                       onchange="populateOnchangeExhHouse();" tabindex="4">
                    </rms:dropDownExchangeHouseFiltered>
                </div>

                <label class="col-md-2 control-label label-required" for="taskListId">Task List:</label>

                <div class="col-md-3">
                    <rms:dropDownTaskList data_model_name="dropDownTaskList" task_status_list="${RmsTaskStatusCacheUtility.INCLUDED_IN_LIST}"
                                          url="${createLink(controller: 'rmsTaskList', action: 'reloadTaskListDropDown')}"
                                          id="taskListId" name="taskListId">
                    </rms:dropDownTaskList>
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="paymentMethod">Payment Method:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownPaymentMethod" name="paymentMethod"
                                              typeId="${SystemEntityTypeCacheUtility.ARMS_PAYMENT_METHOD}"
                                              tabindex="6" showHints="false">
                    </app:dropDownSystemEntity>
                </div>

                <label class="col-md-2 control-label" style="font-weight: bold;font-size: 13px;color: #0000CC">Balance:</label>

                <div class="col-md-2">
                    <span id="lblBalance" style="font-weight: bold;font-size: 13px;color: #0000CC"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

        </div>
    </form>

</div>