<%@ page import="com.athena.mis.arms.entity.RmsExchangeHouse; com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.utility.DateUtility; com.athena.mis.arms.utility.RmsTaskStatusCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Task List
        </div>
    </div>

    <form id='filterPanelTaskListForm' name='filterPanelTaskListForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <app:systemEntityByReserved name="currentStatus"
                                            reservedId="${RmsTaskStatusCacheUtility.NEW_TASK}"
                                            typeId="${SystemEntityTypeCacheUtility.ARMS_TASK_STATUS}">
                </app:systemEntityByReserved>
                <label class="col-md-1 control-label label-required" for="isRevised">Revised:</label>

                <div class="col-md-1">
                    <g:checkBox tabindex="1" name="isRevised"/>
                </div>

                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-2">
                    <app:dateControl name="fromDate" diffWithCurrent="${DateUtility.DATE_RANGE_SEVEN * -1}"
                                     tabindex="2" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse();">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-2">
                    <app:dateControl name="toDate" tabindex="3" placeholder="dd/MM/yyyy" onchange="populateExchangeHouse();">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="exhHouseId">Exchange House:</label>

                <div class="col-md-3">
                    <rms:dropDownExchangeHouseFiltered task_status_list="${RmsTaskStatusCacheUtility.NEW_TASK}"
                                                       from_date="${DateUtility.getFromDateForUI(DateUtility.DATE_RANGE_SEVEN)}"
                                                       to_date="${DateUtility.getToDateForUI()}"
                                                       data_model_name="dropDownExchangeHouse" name="exhHouseId"
                                                       id="exhHouseId"
                                                       url="${createLink(controller: 'rmsExchangeHouse', action: 'reloadExchangeHouseFilteredDropDown')}"
                                                       tabindex="4">
                    </rms:dropDownExchangeHouseFiltered>
                </div>

                %{--<div class="col-md-1" title="Refresh Exchange House">
                    <a class="k-button" onclick="populateExchangeHouse();">
                        <span class="k-icon k-i-refresh"></span>
                    </a>
                </div>--}%
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

        </div>
    </form>

</div>