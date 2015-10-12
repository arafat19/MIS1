<%@ page import="com.athena.mis.utility.DateUtility" %>
<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Search Supplier Wise PO List
                </div>
            </div>

            <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="hidProjectId" id='hidProjectId'/>
                    <input type="hidden" name="hidItemTypeId" id='hidItemTypeId'/>
                    <input type="hidden" name="hidFromDate" id='hidFromDate'/>
                    <input type="hidden" name="hidToDate" id='hidToDate'/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required"
                               for="fromDate">From Date:</label>

                        <div class="col-md-2">
                            <app:dateControl name="fromDate" tabindex="1" diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}">
                            </app:dateControl>
                        </div>

                        <label class="col-md-1 control-label label-required" for="toDate">To Date:</label>

                        <div class="col-md-2">
                            <app:dateControl name="toDate" tabindex="2">
                            </app:dateControl>
                        </div>

                        <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                        <div class="col-md-2">
                            <app:dropDownProject
                                    addCreatedOn="true"
                                    tabindex="3"
                                    name="projectId"
                                    hintsText="ALL"
                                    dataModelName="dropDownProject">
                            </app:dropDownProject>
                        </div>

                        <label class="col-md-1 control-label label-optional" for="itemTypeId">Item Type:</label>

                        <div class="col-md-2">
                            <app:dropDownItemType
                                    hintsText="ALL"
                                    tabindex="4"
                                    name="itemTypeId"
                                    dataModelName="dropDownItemType">
                            </app:dropDownItemType>
                        </div>
                    </div>
                </div>

                <app:ifAllUrl
                        urls="/procPurchaseRequest/create,/procPurchaseRequest/update, /procPurchaseRequest/select">
                    <div class="panel-footer">

                        <button id="searchSupplierWisePOReport" name="searchSupplierWisePOReport" type="submit"
                                data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="5"
                                aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                        </button>

                        <app:ifAnyUrl urls="/procReport/downloadSupplierWisePO,/procReport/downloadSupplierWisePOCsv">
                            <span class="download_icon_set" id="download_icon_set">
                                <ul>
                                    <li>Save as :</li>
                                    <app:ifAllUrl urls="/procReport/downloadSupplierWisePO">
                                        <li><a href="javascript:void(0)" id="printSupplierWisePOReportPdf"
                                               class="pdf_icon"></a></li>
                                    </app:ifAllUrl>
                                    <app:ifAllUrl urls="/procReport/downloadSupplierWisePOCsv">
                                        <li><a href="javascript:void(0)" id="printSupplierWisePOReportCsv"
                                               class="csv_icon"></a></li>
                                    </app:ifAllUrl>
                                </ul>
                            </span>
                        </app:ifAnyUrl>
                    </div>
                </app:ifAllUrl>
            </form>
        </div>

        <div class="panel panel-primary" id="supplierInfo">
            <div class="panel-heading">
                <div class="panel-title">
                    Supplier Information
                </div>
            </div>

            <div class="panel-body">
                <div class="form-group">
                    <label class="col-md-2 control-label label-optional"
                           for="fromDate">Supplier Name:</label>

                    <div class="col-md-9">
                        <span id='lblSupplierName'></span>
                        <input type="hidden" name="supplierId" id='supplierId'/>
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group">
            <table id="flex1" style="display:none"></table>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexSupplier" style="display:none"></table>
    </div>
</div>

<g:render template="/procurement/report/supplierWisePO/script"/>