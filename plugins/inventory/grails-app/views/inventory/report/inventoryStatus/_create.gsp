<%@ page import="com.athena.mis.application.utility.ItemCategoryCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Inventory Status
        </div>
    </div>

    <form id="inventoryStatusForm" name="inventoryStatusForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject addAllAttributes="true"
                                         tabindex="1"
                                         name="projectId"
                                         dataModelName="dropDownProject"
                                         hintsText="ALL"
                                         onchange="updateFromDateAndInventoryType();">
                    </app:dropDownProject>
                </div>
                <label class="col-md-1 control-label label-optional" for="fromDate">From :</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-optional" for="toDate">To :</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="3">
                    </app:dateControl>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="inventoryTypeId">Inventory Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownInventoryType"
                                              name="inventoryTypeId"
                                              tabindex="4"
                                              hintsText="ALL"
                                              onchange="updateInventoryList();"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>

                <label class="col-md-1 control-label label-optional" for="inventoryId">Inventory:</label>

                <div class="col-md-3">
                    <select id="inventoryId"
                            name="inventoryId"
                            tabindex="5">
                    </select>
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
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="4"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl
                    urls="/invReport/downloadInventoryStatusWithQuantityAndValue,/invReport/downloadInventoryStatusWithQuantityAndValueCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadInventoryStatusWithQuantityAndValue">
                            <li><a href="javascript:void(0)" id="printPdfBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadInventoryStatusWithQuantityAndValueCsv">
                            <li><a href="javascript:void(0)" id="printCsvBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>
    </form>
</div>
