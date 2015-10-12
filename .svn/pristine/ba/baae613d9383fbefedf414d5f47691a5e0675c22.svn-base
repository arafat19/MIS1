<%@ page import="com.athena.mis.utility.DateUtility; com.athena.mis.application.utility.ItemCategoryCacheUtility;com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Inventory Transaction
        </div>
    </div>

    <form id="searchForm" name="searchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="startDate">Start:</label>

                <div class="col-md-2">
                    <app:dateControl name="startDate"
                                     tabindex="1"
                                     diffWithCurrent="${DateUtility.DATE_RANGE_THIRTY * -1}">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-required" for="endDate">End:</label>

                <div class="col-md-2">
                    <app:dateControl name="endDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>
                <label class="col-md-2 control-label label-optional" for="projectId">Project:</label>

                <div class="col-md-3">
                    <app:dropDownProject tabindex="4"
                                         dataModelName="dropDownProject"
                                         name="projectId"
                                         hintsText="ALL"
                                         onchange="javascript:updateInventoryType();">
                    </app:dropDownProject>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="transactionTypeId">Transaction:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity name="transactionTypeId"
                                              tabindex="3"
                                              dataModelName="dropDownTransType"
                                              hintsText="ALL"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_INV_TRANSACTION_TYPE}">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-1 control-label label-optional" for="inventoryTypeId">Inv. Type:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity name="inventoryTypeId"
                                              tabindex="5"
                                              dataModelName="dropDownInventoryType"
                                              onchange="javascript:updateInventoryList();"
                                              hintsText="ALL"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-2 control-label label-optional" for="inventoryId">Inventory Name:</label>

                <div class="col-md-3">
                    <select id="inventoryId" name="inventoryId"
                            tabindex="6"></select>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-optional" for="itemTypeId">Item Type:</label>

                <div class="col-md-2">
                    <app:dropDownItemType
                            dataModelName="dropDownItemType"
                            name="itemTypeId"
                            hintsText="ALL"
                            tabindex="7"
                            categoryId="${ItemCategoryCacheUtility.INVENTORY}">
                    </app:dropDownItemType>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl
                    urls="/invReport/downloadInventoryTransactionList,/invReport/downloadInventoryTransactionListCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadInventoryTransactionList">
                            <li><a href="javascript:void(0)" id="printReport" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadInventoryTransactionListCsv">
                            <li><a href="javascript:void(0)" id="printCSVBtn" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
