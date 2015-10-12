<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Inventory Stock
        </div>
    </div>

    <form id="stockSearchForm" name="stockSearchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>
            <input type="hidden" id="hidInventoryTypeId" name="hidInventoryTypeId"/>
            <input type="hidden" id="hidInventoryId" name="hidInventoryId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject
                            dataModelName="dropDownProject"
                            tabindex="1"
                            name="projectId"
                            hintsText="ALL"
                            onchange="javascript:updateInventoryType();">
                    </app:dropDownProject>
                </div>
                <label class="col-md-1 control-label label-optional" for="inventoryTypeId">Type:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity dataModelName="dropDownInventoryType"
                                              name="inventoryTypeId"
                                              tabindex="2"
                                              hintsText="ALL"
                                              onchange="javascript:updateInventoryList();"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-2 control-label label-optional" for="inventoryId">Inventory Name:</label>

                <div class="col-md-3">
                    <select id="inventoryId"
                            name="inventoryId"
                            tabindex="3"></select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadInventoryStock,/invReport/downloadInventoryStockCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadInventoryStock">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadInventoryStockCsv">
                            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
