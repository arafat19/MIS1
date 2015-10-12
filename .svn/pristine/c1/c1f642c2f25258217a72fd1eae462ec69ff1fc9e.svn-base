<%@ page import="com.athena.mis.application.utility.ItemCategoryCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Show Item Received Stock (From Supplier)
        </div>
    </div>

    <form id="stockSearchForm" name="stockSearchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>
            <input type="hidden" id="hidSupplierId" name="hidSupplierId"/>
            <input type="hidden" id="hidFromDate" name="hidFromDate"/>
            <input type="hidden" id="hidToDate" name="hidToDate"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From:</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     tabindex="3">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-required" for="toDate">To:</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="4">
                    </app:dateControl>
                </div>

                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject tabindex="1"
                                         dataModelName="dropDownProject"
                                         name="projectId"
                                         hintsText="ALL">
                    </app:dropDownProject>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="supplierId">Supplier:</label>

                <div class="col-md-3">
                    <app:dropDownSupplier hintsText="ALL"
                                          name="supplierId"
                                          dataModelName="dropDownSupplierId"
                                          tabindex="2">
                    </app:dropDownSupplier>
                </div>

                <label class="col-md-1 control-label label-optional" for="itemTypeId">Item Type:</label>

                <div class="col-md-3">
                    <app:dropDownItemType
                            dataModelName="dropDownItemType"
                            name="itemTypeId"
                            tabindex="6"
                            hintsText="ALL"
                            categoryId="${ItemCategoryCacheUtility.INVENTORY}">
                    </app:dropDownItemType>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="5"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadItemReceivedStock,/invReport/downloadItemReceivedStockCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadItemReceivedStock">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>
