<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Move Fixed Asset
        </div>

    </div>
    <form id="fixedAssetTraceForm" name="fixedAssetTraceForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="currentInventoryId"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="categoryId">Category:</label>

                        <div class="col-md-6">
                            <fxd:dropDownFxdItemForFADetails
                                    dataModelName="dropDownCategoryId"
                                    name="categoryId"
                                    tabindex="1"
                                    onchange="updateModelList();"
                                    required="true"
                                    validationMessage="Required">
                            </fxd:dropDownFxdItemForFADetails>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="categoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="fixedAssetDetailsId">Model/Serial No:</label>

                        <div class="col-md-6">
                            <select id="fixedAssetDetailsId"
                                    tabindex="2"
                                    name="fixedAssetDetailsId"
                                    required="required"
                                    validationMessage="Required"
                                    onchange="updateCurrentInventoryLbl();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="lblCurrentInventory">Current Inventory:</label>

                        <div class="col-md-6">
                            <span id="lblCurrentInventory"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionDate">Transaction Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="transactionDate"
                                    tabindex="3"
                                    required="true"
                                    validationMessage="Required"
                                    value=""
                                    placeholder="dd/mm/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDate"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="inventoryTypeId">Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownInventoryTypeId"
                                    tabindex="4"
                                    name="inventoryTypeId"
                                    onchange="updateInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="inventoryId">Move To:</label>

                        <div class="col-md-6">
                            <select name="inventoryId"
                                    tabindex="5"
                                    id="inventoryId"
                                    required="required"
                                    validationMessage="Required"></select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-6">
                            <textarea class="k-textbox"
                                      tabindex="6"
                                      rows="3"
                                      id="comments"
                                      name="comments"
                                      validationMessage="Required"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>

    </form>
</div>

