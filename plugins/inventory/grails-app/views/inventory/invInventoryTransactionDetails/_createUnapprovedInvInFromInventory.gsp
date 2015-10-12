<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Add Item(s) From Inventory
        </div>
    </div>

    <form id="inventoryInDetailsForm" name="inventoryInDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
            <input type="hidden" id="inventoryId" name="inventoryId"/>
            <input type="hidden" id="transactionId" name="transactionId"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="transactionIdLabel">Transaction Id:</label>

                        <div class="col-md-6">
                            <span id="transactionIdLabel"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Inventory:</label>

                        <div class="col-md-6">
                            <span id="inventoryName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="transactionDetailsId">Item:</label>

                        <div class="col-md-6">
                            <inv:dropDownInventoryItemInFromInventory
                                    id="transactionDetailsId"
                                    name="transactionDetailsId"
                                    tabindex="1"
                                    data_model_name="dropDownItemId"
                                    transaction_id="${transactionId}"
                                    url="${createLink(controller: 'invInventoryTransactionDetails', action: 'dropDownInventoryItemInFromInventoryReload')}"
                                    validation_message="Required"
                                    required="required"
                                    onchange="onChangeItem()">
                            </inv:dropDownInventoryItemInFromInventory>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Supp. Quantity:</label>

                        <div class="col-md-4">
                            <span id="suppliedQuantity"></span>
                            <span id="unitForSupplied"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="actualQuantity">Actual Quantity:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   tabindex="2"
                                   id="actualQuantity"
                                   name="actualQuantity"
                                   required="required" validationMessage="Required"
                                   placeholder="Actual Quantity"/>
                        </div>

                        <div class="col-md-2 pull-left">
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="actualQuantity"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Received Date:</label>

                        <div class="col-md-6">
                            <span id="transactionDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">From Inventory:</label>

                        <div class="col-md-6">
                            <span id="fromInventoryName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Comments:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="comments" name="comments" tabindex="3"
                                      placeholder="Place your comments here..." rows="3">
                            </textarea>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="4"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Receive
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>