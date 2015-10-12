<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Inventory-Out
        </div>
    </div>

    <form id="inventoryOutForm" name="inventoryOutForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionDate">Transfer Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="transactionDate"
                                    tabindex="1"
                                    value=""
                                    placeholder="dd/MM/yyyy"
                                    required="true"
                                    validationMessage="Required">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="budgetItem">Budget Line Item:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" tabindex="2" id="budgetItem" name="budgetItem"
                                   maxlength="50"/>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="fromInventory">From Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownFromInventoryType"
                                    name="fromInventory"
                                    tabindex="3"
                                    required="true"
                                    validationMessage="Required"
                                    onchange="javascript:updateFromInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fromInventory"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="inventoryId">From Inventory:</label>

                        <div class="col-md-6">
                            <select id="inventoryId"
                                    name="inventoryId"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2"
                                      placeholder="255 Char Max"></textarea>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="toInventory">To Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownToInventoryType"
                                    name="toInventory"
                                    tabindex="6"
                                    required="true"
                                    validationMessage="Required"
                                    onchange="javascript:updateToInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="toInventory"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionEntityId">To Inventory:</label>

                        <div class="col-md-6">
                            <select id="transactionEntityId"
                                    name="transactionEntityId"
                                    tabindex="7"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionEntityId"></span>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>








