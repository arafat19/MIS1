<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Add Item(s) For Inventory-Consumption
        </div>
    </div>
    <form id="invConsumptionDetailsForm" name="invConsumptionDetailsForm" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="availableQuantity" name="availableQuantity"/>
            <input type="hidden" id="budgetId" name="budgetId"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="transactionDate">Consumption Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                   name="transactionDate"
                                   value=""
                                   required="true"
                                   validationMessage="Required"
                                   tabindex="1"
                                   placeholder="dd/MM/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="lblInventoryTransactionId">Transaction Id:</label>

                        <div class="col-md-6">
                            <span id="lblInventoryTransactionId"></span>
                            <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="inventoryName">Inventory Name:</label>

                        <div class="col-md-6">
                            <span id="inventoryName"></span>
                            <input type="hidden" id="inventoryId" name="inventoryId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="budgetItem">Budget Item:</label>

                        <div class="col-md-6">
                            <span id="budgetItem"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemId">Item:</label>

                        <div class="col-md-6">
                            <inv:dropDownInventoryItemConsumption
                                    id="itemId"
                                    name="itemId"
                                    tabindex="2"
                                    data_model_name="dropDownItemId"
                                    transaction_id="${transactionId}"
                                    url="${createLink(controller: 'invInventoryTransactionDetails', action: 'dropDownInventoryItemConsumptionReload')}"
                                    validation_message="Required"
                                    required="required"
                                    onchange="onChangeItem()">
                            </inv:dropDownInventoryItemConsumption>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="fixedAssetId">Fixed Asset:</label>

                        <div class="col-md-6">
                            <select id="fixedAssetId"
                                    name="fixedAssetId"
                                    tabindex="3"
                                    onchange="javascript:onChangeFixedAsset();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="fixedAssetDetailsId">Fixed Asset Details:</label>

                        <div class="col-md-6">
                            <select id="fixedAssetDetailsId"
                                    name="fixedAssetDetailsId"
                                    tabindex="4">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="actualQuantity">Quantity:</label>

                        <div class="col-md-3">
                            <input type="text" tabindex="5"
                                   id="actualQuantity"
                                   name="actualQuantity"
                                   required="required"
                                   validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span id="unit"></span>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="actualQuantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Comments:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="comments" name="comments" tabindex="6"
                                      placeholder="Place your comments here..." rows="3"></textarea>
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


