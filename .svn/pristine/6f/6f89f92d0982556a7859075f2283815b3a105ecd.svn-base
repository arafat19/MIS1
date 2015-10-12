<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Project Fund Flow
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hidProjectId" id="hidProjectId"/>
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject
                            dataModelName="dropDownProject"
                            addAllAttributes="true"
                            hintsText="ALL"
                            tabindex="3"
                            name="projectId"
                            onchange="updateFromDate();">
                    </app:dropDownProject>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchProjectFund" name="searchProjectFund" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/accReport/downloadProjectFundFlowReport">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadProjectFundFlowReport">
                            <li><a href="javascript:void(0)" id="printProjectFundFlow" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>
<table id="flex1" style="display:none"></table>