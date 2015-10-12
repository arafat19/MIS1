<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Add Item(s) For Inventory-Out
        </div>
    </div>

    <form id="inventoryOutDetailsForm" name="inventoryOutDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
            <input type="hidden" id="inventoryId" name="inventoryId"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Chalan No:</label>

                        <div class="col-md-6">
                            <span id="chalanNo"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">From Inventory:</label>

                        <div class="col-md-6">
                            <span id="fromInventory"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">To Inventory:</label>

                        <div class="col-md-6">
                            <span id="siteName"></span>
                            <g:hiddenField name="transactionEntityId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">BudgLine Item:</label>

                        <div class="col-md-6">
                            <span id="lblBudgetLine"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="vehicleId">Vehicle:</label>

                        <div class="col-md-6">
                            <app:dropDownVehicle dataModelName="dropDownVehicle"
                                                 name="vehicleId"
                                                 required="true"
                                                 validationMessage="Required"/>

                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="vehicleId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="vehicleNumber">Vehicle Number:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="vehicleNumber" name="vehicleNumber"
                                   placeholder="Vehicle Number"/>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="mrfNo">MRF No:</label>

                        <div class="col-md-6">
                            <input type="text" tabindex="5" class='k-textbox'
                                   id="mrfNo"
                                   name="mrfNo"
                                   required="required" validationMessage="Required"
                                   placeholder="MRF No"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="mrfNo"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemId">Item:</label>

                        <div class="col-md-6">
                            <inv:dropDownInventoryItemOut
                                    id="itemId"
                                    name="itemId"
                                    tabindex="6"
                                    data_model_name="dropDownItemId"
                                    transaction_id="${transactionId}"
                                    url="${createLink(controller: 'invInventoryTransactionDetails', action: 'dropDownInventoryItemOutReload')}"
                                    validation_message="Required"
                                    required="required"
                                    onchange="onChangeItem();">
                            </inv:dropDownInventoryItemOut>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="actualQuantity">Actual Quantity:</label>

                        <div class="col-md-3">
                            <input type="text"
                                   id="actualQuantity"
                                   name="actualQuantity"
                                   required="required" validationMessage="Required"
                                   placeholder="Quantity"/>
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
                            <textarea class="k-textbox" id="comments" name="comments"
                                      placeholder="Place your comments here..." rows="3"></textarea>
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
                    aria-disabled="false" onclick='resetInventoryOutForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
