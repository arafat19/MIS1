<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Supplier Wise Payable Report
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
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         hintsText="ALL"
                                         tabindex="3"
                                         name="projectId">
                    </app:dropDownProject>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchSupplierPayable" name="searchSupplierPayable" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadSupplierWisePayable,/accReport/downloadSupplierWisePayableCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadSupplierWisePayable">
                            <li><a href="javascript:void(0)" id="printProjectPayable" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadSupplierWisePayableCsv">
                            <li><a href="javascript:void(0)" id="printProjectPayableCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<table id="flex1" style="display:none"></table>
