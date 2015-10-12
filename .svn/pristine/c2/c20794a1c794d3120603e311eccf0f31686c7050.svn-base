<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Adjustment of Approved Item For Inventory-Out
        </div>
    </div>

    <form id="approvedInvOutDetailsForm" name="approvedInvOutDetailsForm" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
            <input type="hidden" id="inventoryId" name="inventoryId"/>
            <input type="hidden" id="reverse" name="reverse"/>

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
                            <input type="hidden" id="transactionEntityId" nam="transactionEntityId"/>
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
                            <select id="vehicleId"
                                    name="vehicleId"
                                    tabindex="1"
                                    required="required"
                                    validationMessage="Required">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="vehicleId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="vehicleNumber">Vehicle Number:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="vehicleNumber" name="vehicleNumber"
                                   placeholder="Vehicle Number" tabindex="2"/>
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
                                   tabindex="3"
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
                            <select id="itemId"
                                    name="itemId"
                                    tabindex="4"
                                    required="required"
                                    validationMessage="Required"
                                    onchange="javascript:updateQuantity();"></select>
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
                                   tabindex="5"
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
                            <span id="lblComments"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required">Adj. Comments:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="comments" name="comments" tabindex="7"
                                      placeholder="Place your comments here..." rows="3"/>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false" tabindex="8"><span class="k-icon k-i-plus"></span><span
                    id="buttonAdjust">Apply Adjustment</span><span id="buttonReverse">Apply Reverse Adjustment</span>
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="9" onclick='resetInventoryOutForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
