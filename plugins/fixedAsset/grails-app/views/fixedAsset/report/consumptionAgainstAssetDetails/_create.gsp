<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Search Consumption Against Asset(Details)
        </div>

    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidProjectId" value=""/>
            <g:hiddenField name="hidFixedAssetDetailsId" value=""/>
            <g:hiddenField name="hidItemId" value=""/>
            <g:hiddenField name="hidFromDate" value=""/>
            <g:hiddenField name="hidToDate" value=""/>
            <div class="form-group">
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="fromDate">From Date:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="fromDate"
                                    diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                    tabindex="1">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="toDate">To Date:</label>

                        <div class="col-md-8">
                            <app:dateControl
                                    name="toDate"
                                    tabindex="2">
                            </app:dateControl>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="projectId">Project:</label>

                        <div class="col-md-9">
                            <app:dropDownProject
                                    name="projectId"
                                    tabindex="3"
                                    hintsText="ALL"
                                    dataModelName="dropDownProject"
                                    onchange="javascript:updateFixedAssetList();">
                            </app:dropDownProject>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-required" for="itemId">Item:</label>

                        <div class="col-md-8">
                            <fxd:dropDownFxdItemForConsumption
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="4"
                                    name="itemId"
                                    onchange="javascript:updateFixedAssetList();"
                                    dataModelName="dropDownItemId">
                            </fxd:dropDownFxdItemForConsumption>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="form-group">
                        <label class="col-md-4 control-label label-optional"
                               for="fixedAssetDetailsId">Fixed Asset Name:</label>

                        <div class="col-md-8">
                            <select id="fixedAssetDetailsId"
                                    name="fixedAssetDetailsId"
                                    tabindex="5">
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReportDetails" name="showReportDetails" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl
                    urls="/fixedAssetReport/downloadConsumptionAgainstAssetDetails,/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/fixedAssetReport/downloadConsumptionAgainstAssetDetails">
                            <li><a href="javascript:void(0)" id="printReport" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv">
                            <li><a href="javascript:void(0)" id="printReportCSV" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>