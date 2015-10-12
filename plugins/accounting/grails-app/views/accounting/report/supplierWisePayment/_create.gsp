<%@ page import="com.athena.mis.utility.DateUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Supplier Wise Payment Report
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="hidProjectId" id="hidProjectId"/>
        <input type="hidden" name="hidSupplierId" id="hidSupplierId"/>
        <input type="hidden" name="hideFromDate" id="hideFromDate"/>
        <input type="hidden" name="hideToDate" id="hideToDate"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-2">
                    <app:dateControl name="fromDate"
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}"
                                     tabindex="1">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-2">
                    <app:dateControl name="toDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-required">Supplier:</label>

                <div class="col-md-2">
                    <app:dropDownSupplier dataModelName="dropDownSupplier"
                                          required="true"
                                          validationMessage="Required"
                                          tabindex="3"
                                          name="supplierId">
                    </app:dropDownSupplier>
                </div>

                <label class="col-md-1 control-label label-optional">Project:</label>

                <div class="col-md-2">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         hintsText="ALL"
                                         tabindex="4"
                                         name="projectId">
                    </app:dropDownProject>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="searchSupplierPayment" name="searchSupplierPayment" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/accReport/downloadSupplierWisePayment,/accReport/downloadSupplierWisePaymentCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/accReport/downloadSupplierWisePayment">
                            <li><a href="javascript:void(0)" id="printSupplierPayment" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/accReport/downloadSupplierWisePaymentCsv">
                            <li><a href="javascript:void(0)" id="printSupplierPaymentCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>

<div id="divLedger" class="table-responsive">
    <table class="table table-bordered">
        <tbody>
        <tr class="active">
            <td colspan="6">Supplier PO Information</td>
        </tr>
        <tr>
            <td class="active" style="width: 15%">Total Paid:</td>
            <td><span id='lblTotalPaid'></span></td>
        </tr>
        </tbody>
    </table>
</div>

<table id="flex1" style="display:none"></table>