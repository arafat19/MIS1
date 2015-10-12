<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Inventory In From Supplier
        </div>
    </div>

    <form id="inventoryInForm" name="inventoryInForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="inventoryTypeId">Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    name="inventoryTypeId"
                                    dataModelName="dropDownInventoryType"
                                    tabindex="1"
                                    required="true"
                                    validationMessage="Required"
                                    onchange="updateInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="inventoryId">Inventory Name:</label>

                        <div class="col-md-6">
                            <select id="inventoryId"
                                    name="inventoryId"
                                    tabindex="2"
                                    onchange="updateDependency();"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="transactionEntityId">Supplier:</label>

                        <div class="col-md-6">
                            <app:dropDownSupplier
                                    dataModelName="dropDownSupplier"
                                    tabindex="3"
                                    name="transactionEntityId"
                                    required="true"
                                    onchange="updatePurchaseOrder();"
                                    validationMessage="Required">
                            </app:dropDownSupplier>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionEntityId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="purchaseOrderId">Purchase Order:</label>

                        <div class="col-md-6">
                            <select id="purchaseOrderId"
                                    name="purchaseOrderId"
                                    tabindex="4"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="purchaseOrderId"></span>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="6"
                                      placeholder="255 Char Max" tabindex="5"></textarea>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>








