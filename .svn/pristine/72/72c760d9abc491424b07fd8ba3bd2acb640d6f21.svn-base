<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Adjustment of Approved Item For Inventory-Consumption
        </div>
    </div>
    <form id="approvedConsumptionDetailsForm" name="approvedConsumptionDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="availableQuantity" name="availableQuantity"/>
            <input type="hidden" id="budgetId" name="budgetId"/>
            <input type="hidden" id="isReverse" name="isReverse" value="false"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="transactionDate">Consumption Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="transactionDate"
                                    value=""
                                    placeholder="dd/MM/yyyy"
                                    disabled="true">
                            </app:dateControl>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="transactionDate"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="lblInventoryTransactionId">Transaction Id:</label>

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
                            <select id="itemId"
                                    name="itemId"
                                    tabindex="2"
                                    onchange="javascript:onChangeItem();">
                             </select>
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
                                      tabindex="1"
                                      onchange="javascript:onChangeFixedAsset();">
                              </select>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="fixedAssetDetailsId">Fixed Asset Details:</label>

                        <div class="col-md-6">
                              <select id="fixedAssetDetailsId"
                                      name="fixedAssetDetailsId"
                                      tabindex="2">
                              </select>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fixedAssetDetailsId"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="actualQuantity">Quantity:</label>

                        <div class="col-md-4">
                            <input type="text" tabindex="3"
                                   tabindex="3"
                                   id="actualQuantity"
                                   name="actualQuantity"
                                   maxlength="50"/>
                        </div>
                        <div class="col-md-3 pull-left">
                            <span id="unit"></span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Comments:</label>

                        <div class="col-md-9">
                            <span class="lblComments"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="comments">Adj. Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments"
                                      name="comments" rows="2" tabindex="4"
                                      required="required" validationMessage="Required"
                                      placeholder="255 Char Max"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="5"><span class="k-icon k-i-plus"></span><span id="buttonAdjust">Apply Adjustment</span><span id="buttonReverse">Apply Reverse Adjustment</span>
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetCreateForm();' tabindex="6"><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
