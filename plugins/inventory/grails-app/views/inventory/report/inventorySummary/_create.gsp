<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Search Inventory Summary
        </div>
    </div>

    <form id="frmInventorySummary" name="frmInventorySummary" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="startDate">Start:</label>

                <div class="col-md-2">
                    <app:dateControl name="startDate"
                                     tabindex="1">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-required" for="endDate">End:</label>

                <div class="col-md-2">
                    <app:dateControl name="endDate"
                                     tabindex="2">
                    </app:dateControl>
                </div>
                <label class="col-md-1 control-label label-required" for="inventoryTypeId">Inv. Type:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity
                            name="inventoryTypeId"
                            dataModelName="dropDownInventoryType"
                            tabindex="3"
                            onchange="javascript:updateInventoryList();"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                    </app:dropDownSystemEntity>
                </div>
                <label class="col-md-1 control-label label-required" for="inventoryId">Inventory:</label>

                <div class="col-md-2">
                    <select id="inventoryId" tabindex="4" name="inventoryId"></select>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="showReport" name="showReport" type="submit" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
            <app:ifAnyUrl urls="/invReport/downloadInventorySummary,/invReport/downloadInventorySummaryCsv">
                <span class="download_icon_set">
                    <ul>
                        <li>Save as :</li>
                        <app:ifAllUrl urls="/invReport/downloadInventorySummary">
                            <li><a href="javascript:void(0)" id="printBtn" class="pdf_icon"></a></li>
                        </app:ifAllUrl>
                        <app:ifAllUrl urls="/invReport/downloadInventorySummaryCsv">
                            <li><a href="javascript:void(0)" id="printBtnCsv" class="csv_icon"></a></li>
                        </app:ifAllUrl>
                    </ul>
                </span>
            </app:ifAnyUrl>
        </div>

    </form>
</div>
