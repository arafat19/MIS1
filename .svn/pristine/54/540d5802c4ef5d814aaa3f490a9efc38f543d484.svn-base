<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Adjustment of Approved Item From Inventory
        </div>
    </div>

    <form id="inventoryInDetailsForm" name="inventoryInDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
            <input type="hidden" id="inventoryId" name="inventoryId"/>
            <input type="hidden" id="transactionId" name="transactionId"/>
            <input type="hidden" id="reverse" name="reverse"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
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
                            <select id="transactionDetailsId"
                                    name="transactionDetailsId"
                                    required="required"
                                    validationMessage="Required"
                                    onchange="javascript:updateQuantity();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDetailsId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required">Supp. Quantity:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   id="suppliedQuantity"
                                   name="suppliedQuantity"
                                   readonly="true"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span id="unit"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="actualQuantity">Actual Quantity:</label>

                        <div class="col-md-4">
                            <input type="text"
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

                        <div class="col-md-6">
                            <span id="comments"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required">Adj. Comments:</label>

                        <div class="col-md-9">
                            <textarea class="k-textbox" id="adjComments" name="adjComments"
                                      placeholder="Place your comments here..."
                                      required="required" validationMessage="Required" rows="3"></textarea>
                        </div>

                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span><span
                    id="buttonReverse">Apply Reverse Adjustment</span>
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
