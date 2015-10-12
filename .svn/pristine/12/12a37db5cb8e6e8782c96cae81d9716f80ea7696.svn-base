<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            QS Measurement
        </div>

    </div>
    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>
            <input type="hidden" id="hidFromDate" name="hidFromDate"/>
            <input type="hidden" id="hidToDate" name="hidToDate"/>
            <input type="hidden" id="hidIsGovtQs" name="hidIsGovtQs"/>

            <div class="form-group">
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-9">
                            <app:dropDownProject addAllAttributes="true"
                                                 dataModelName="dropDownProject"
                                                 name="projectId"
                                                 tabindex="1"
                                                 onchange="updateFromDate();">
                            </app:dropDownProject>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="fromDate">From:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="fromDate"
                                    tabindex="2">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="toDate">To:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="toDate"
                                    tabindex="3">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-2">
                    <div class="form-group">
                        <label class="col-md-6 control-label label-optional" for="isGovtQs">Is Govt.:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="4" name="isGovtQs"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchQsMeasurement" name="searchQsMeasurement" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

            <app:ifAnyUrl urls="/qsReport/downloadQsMeasurementRpt,/qsReport/downloadQsMeasurementCsvRpt">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/qsReport/downloadQsMeasurementRpt">
                            <li><a href="javascript:void(0)" id="printQsMeasurement" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/qsReport/downloadQsMeasurementCsvRpt">
                            <li><a href="javascript:void(0)" id="printQsMeasurementCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Budget Information
        </div>
    </div>

    <div class="panel-body">
        <div class="form-group">
            <label class="col-md-2 control-label label-optional" for="lblBudgetDetails">Budget Details:</label>

            <div class="col-md-10">
                <span id='lblBudgetDetails'></span>
            </div>
        </div>
    </div>
</div>
<table id="flex1" style="display:none"></table>