<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Inventory-In From Inventory-Out
        </div>
    </div>

    <form id="frmInventoryInFromInventory" name="frmInventoryInFromInventory" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="inventoryTypeId">To Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownInventoryType"
                                    name="inventoryTypeId"
                                    tabindex="1"
                                    required="true"
                                    validationMessage="Required"
                                    onchange="javascript:updateInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>

                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="inventoryId">To Inventory:</label>

                        <div class="col-md-6">
                            <select id="inventoryId"
                                    name="inventoryId"
                                    required="required"
                                    validationMessage="Required"
                                    tabindex="2"
                                    onchange="javascript:updateFromInventoryList();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="fromInventoryId">From Inventory:</label>

                        <div class="col-md-6">
                            <select tabindex="3"
                                    id="fromInventoryId"
                                    name="fromInventoryId"
                                    required="required"
                                    validationMessage="Required"
                                    onchange="javascript:populateTransferDateList();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fromInventoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionId">Transferred Date:</label>

                        <div class="col-md-6">
                            <select id="transactionId"
                                    name="transactionId"
                                    tabindex="4"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionId"></span>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionDate">Received Date:</label>

                        <div class="col-md-6">
                            <input id="transactionDate" name="transactionDate" tabindex="5"
                                   placeholder="dd/MM/yyyy" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="4"
                                      placeholder="255 Char Max" tabindex="6"></textarea>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false" tabindex="7"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="8" onclick='resetTransactionForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>











