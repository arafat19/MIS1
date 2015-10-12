<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Show Inventory Valuation
        </div>
    </div>

    <form id="inventoryValuationSearchForm" name="inventoryValuationSearchForm" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="inventoryTypeId">Inventory Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            name="inventoryTypeId"
                            required="true"
                            tabindex="1"
                            dataModelName="dropDownInventoryType"
                            validationMessage="Required"
                            onchange="javascript:updateInventoryList();"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                </div>
                <label class="col-md-2 control-label label-required" for="inventoryId">Inventory Name:</label>

                <div class="col-md-3">
                    <select id="inventoryId"
                            name="inventoryId"
                            required="required"
                            tabindex="2"
                            validationMessage="Required"></select>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="inventoryId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadInventoryValuation,/invReport/downloadInventoryValuationCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadInventoryValuation">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadInventoryValuationCsv">
                            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>
