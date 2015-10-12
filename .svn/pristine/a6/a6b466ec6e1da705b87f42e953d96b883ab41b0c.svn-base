<%@ page import="com.athena.mis.inventory.utility.InvInventoryTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Item Consumption Report
        </div>
    </div>

    <form id="consumptionSearchForm" name="consumptionSearchForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="hidInventoryId" name="hidInventoryId"/>
            <input type="hidden" id="hidInventoryTypeId" name="hidInventoryTypeId"/>
            <input type="hidden" id="hidProjectId" name="hidProjectId"/>
            <input type="hidden" id="hidFromDate" name="hidFromDate"/>
            <input type="hidden" id="hidToDate" name="hidToDate"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="projectId">Project:</label>
                <div class="col-md-3">
                    <app:dropDownProject tabindex="1"
                                         name="projectId"
                                         dataModelName="dropDownProject"
                                         addAllAttributes="true"
                                         onchange="updateFromDate();">
                    </app:dropDownProject>
                </div>
                <label class="col-md-1 control-label label-optional" for="inventoryTypeId">Inv. Type:</label>
                <div class="col-md-3">
                    <app:dropDownSystemEntity name="inventoryTypeId"
                                                 tabindex="2"
                                                 dataModelName="dropDownInventoryType"
                                                 onchange="javascript:updateInventoryList();"
                                                 showHints="false"
                                                 typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-1 control-label label-optional" for="inventoryId">Inventory:</label>

                <div class="col-md-3">
                    <inv:dropDownInventory dataModelName="dropDownInventory"
                                           name="inventoryId"
                                           tabindex="3"
                                           typeId="${InvInventoryTypeCacheUtility.TYPE_SITE}"
                                           hintsText="ALL">
                    </inv:dropDownInventory>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="fromDate">From :</label>

                <div class="col-md-3">
                    <app:dateControl name="fromDate"
                                     tabindex="4">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-required" for="toDate">To :</label>

                <div class="col-md-3">
                    <app:dateControl name="toDate"
                                     tabindex="5">
                    </app:dateControl>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAllUrl urls="/invReport/downloadForConsumedItemList">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printPdfReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>

    </form>
</div>
