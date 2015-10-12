<div id="application_top_panel" class="panel panel-primary">
<div class="panel-heading">
    <div class="panel-title">
        Adjustment of Approved Item From Supplier
    </div>
</div>

<form id="inventoryInDetailsForm" name="inventoryInDetailsForm" class="form-horizontal form-widgets" role="form">
<div class="panel-body">
    <input type="hidden" id="id" name="id"/>
    <input type="hidden" id="version" name="version"/>
    <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
    <input type="hidden" id="inventoryId" name="inventoryId"/>
    <input type="hidden" id="purchaseOrderId" name="purchaseOrderId"/>
    <input type="hidden" id="reverse" name="reverse"/>

    <div class="form-group">
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="transactionDate">Trans. Date:</label>

                <div class="col-md-6">
                    <app:dateControl name="transactionDate"
                                     value=""
                                     tabindex="1"
                                     required="true"
                                     validationMessage="Required"
                                     placeholder="dd/MM/yyyy" disabled="true">
                    </app:dateControl>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="transactionDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-optional">Chalan No:</label>

                <div class="col-md-6">
                    <span id="chalanNo"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-optional">Purchase Order:</label>

                <div class="col-md-6">
                    <span id="purchaseOrderLabel"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-optional">Inventory Name:</label>

                <div class="col-md-6">
                    <span id="inventoryName"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-optional">Supplier Name:</label>

                <div class="col-md-6">
                    <span id="supplierName"></span>
                    <g:hiddenField name="transactionEntityId" class="required"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="supplierChalan">Sup.Chalan No:</label>

                <div class="col-md-6">
                    <input type="text" class="k-textbox" id="supplierChalan" name="supplierChalan"
                           required="required" validationMessage="Required" placeholder="Supplier Chalan No."/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="supplierChalan"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="vehicleId">Vehicle:</label>

                <div class="col-md-6">
                    <app:dropDownVehicle dataModelName="dropDownVehicle"
                                         name="vehicleId"
                                         required="required"
                                         validationMessage="Required">
                    </app:dropDownVehicle>
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
                <label class="col-md-3 control-label label-required" for="stackMeasurement">Measurement:</label>

                <div class="col-md-6">
                    <input type="text" tabindex="5" class='k-textbox'
                           id="stackMeasurement"
                           name="stackMeasurement"
                           required="required" validationMessage="Required"
                           placeholder="Measurement"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="stackMeasurement"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="itemId">Item:</label>

                <div class="col-md-6">
                    <select id="itemId"
                            name="itemId"
                            required="required"
                            validationMessage="Required"
                            onchange="updatePODQuantity();">
                    </select>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="itemId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required"
                       for="suppliedQuantity">Supp. Quantity:</label>

                <div class="col-md-4">
                    <input type="text"
                           id="suppliedQuantity"
                           name="suppliedQuantity"
                           required="required"
                           validationMessage="Required"
                           placeholder="Supplied Quantity"/>
                </div>

                <div class="col-md-2 pull-left">
                    <label id="unit"></label>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="suppliedQuantity"></span>
                </div>

            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required"
                       for="actualQuantity">Actual Quantity:</label>

                <div class="col-md-4">
                    <input type="text"
                           id="actualQuantity"
                           name="actualQuantity"
                           required="required"
                           validationMessage="Required"
                           placeholder="Actual Quantity"/>
                </div>

                <div class="col-md-2 pull-left">
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="actualQuantity"></span>
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
            id="buttonAdjust">Apply Adjustment</span><span id="buttonReverse">Apply Reverse Adjustment</span>
    </button>
    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
            class="k-button k-button-icontext" role="button"
            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
    </button>
</div>
</form>
</div>






