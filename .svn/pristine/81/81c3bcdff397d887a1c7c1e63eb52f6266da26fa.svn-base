<%@ page import="com.athena.mis.inventory.utility.InvProductionItemTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Production Details
        </div>
    </div>

    <form id="productionDetailsForm" name="productionDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <app:systemEntityByReserved
                    name="finishedProductId"
                    reservedId="${InvProductionItemTypeCacheUtility.TYPE_FINISHED_MATERIAL_ID}"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE}">
            </app:systemEntityByReserved>
            <app:systemEntityByReserved
                    name="rawMaterialId"
                    reservedId="${InvProductionItemTypeCacheUtility.TYPE_RAW_MATERIAL_ID}"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="productionLineItemId">Prod. Line Item:</label>

                <div class="col-md-2">
                    <span id="productionLineItemName"></span>
                </div>
                <input type="hidden" id="productionLineItemId" name="productionLineItemId"/>

            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="productionItemTypeId">Prod. Item Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity
                            dataModelName="dropDownProdItemType"
                            name="productionItemTypeId"
                            required="true"
                            validationMessage="Required"
                            tabindex="1"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_INV_PRODUCTION_LINE_ITEM_TYPE}"
                            onchange="javascript:updateMaterialList();">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="productionItemTypeId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="materialId">Material:</label>

                <div class="col-md-3">
                    <select id="materialId"
                            name="materialId"
                            tabindex="2"
                            required="required"
                            validationMessage="Required">
                    </select>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="materialId"></span>
                </div>
            </div>

            <div class="form-group" id="trOverheadCost" hidden="hidden">
                <label class="col-md-2 control-label label-optional" for="overheadCost">Overhead Cost:</label>

                <div class="col-md-3">
                    <input type="text" id="overheadCost" name="overheadCost" tabindex="3"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>



