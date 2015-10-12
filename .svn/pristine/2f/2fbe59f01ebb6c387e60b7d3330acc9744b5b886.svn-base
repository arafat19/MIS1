<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div>
    <div class="panel-body">
        <form id="frmProduction" name="frmProduction" class="form-horizontal form-widgets" role="form">
            <input type="hidden" id="idCon" name="idCon"/>
            <input type="hidden" id="idProd" name="idProd"/>
            <input type="hidden" id="versionCon" name="versionCon"/>
            <input type="hidden" id="versionProd"/>

            <div class="form-group">
                <div class="form-group">
                    <label class="col-md-2 control-label label-required"
                           for="inventoryTypeId">Inventory Type:</label>

                    <div class="col-md-3">
                        <app:dropDownSystemEntity
                                dataModelName="dropDownInventoryType"
                                name="inventoryTypeId"
                                tabindex="1"
                                onchange="javascript:updateInventoryList();"
                                typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                        </app:dropDownSystemEntity>

                    </div>

                    <div class="col-md-1 pull-left">
                        <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                    </div>

                    <label class="col-md-2 control-label label-required"
                           for="productionLineItemId">Production Line Item:</label>

                    <div class="col-md-3">
                        <inv:dropDownInventoryProductionLineItem
                                dataModelName="dropDownProdLineItem"
                                name="productionLineItemId"
                                onchange="onChangeLineItem();">
                        </inv:dropDownInventoryProductionLineItem>
                    </div>

                    <div class="col-md-1 pull-left">
                        <span class="k-invalid-msg" data-for="productionLineItemId"></span>
                    </div>
                </div>


                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="inventoryId">Inventory:</label>

                    <div class="col-md-3">
                        <select id="inventoryId"
                                name="inventoryId"
                                onchange="onChangeInventory();"
                                tabindex="2">
                        </select>
                    </div>

                    <div class="col-md-1 pull-left">
                        <span class="k-invalid-msg" data-for="inventoryId"></span>
                    </div>

                    <label class="col-md-2 control-label label-required"
                           for="transactionDate">Production Date:</label>

                    <div class="col-md-3">
                        <app:dateControl
                                name="transactionDate"
                                tabindex="3"
                                disabled="true">
                        </app:dateControl>
                    </div>

                    <div class="col-md-1 pull-left">
                        <span class="k-invalid-msg" data-for="transactionDate"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-optional">Comments:</label>

                    <div class="col-md-3">
                        <span id="comments"></span>
                    </div>

                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="adjComments">Adj. Comments:</label>

                    <div class="col-md-9">
                        <textarea type="text" class="k-textbox" id="adjComments" name="adjComments" rows="4"
                                  required="required" validationMessage="Required"
                                  placeholder="255 Char Max"></textarea>
                    </div>

                </div>
            </div>

        </form>
    </div>
</div>


