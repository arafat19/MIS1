<div id="application_top_panel" class="panel panel-primary">
<div class="panel-heading">
    <div class="panel-title">
        Add Item(s) from Supplier
    </div>
</div>

<form id="inventoryInDetailsForm" name="inventoryInDetailsForm" class="form-horizontal form-widgets" role="form">
<div class="panel-body">
    <input type="hidden" id="id" name="id"/>
    <input type="hidden" id="version" name="version"/>
    <input type="hidden" id="inventoryTransactionId" name="inventoryTransactionId"/>
    <input type="hidden" id="inventoryId" name="inventoryId"/>
    <input type="hidden" id="purchaseOrderId" name="purchaseOrderId"/>

    <div class="form-group">
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="transactionDate">Trans. Date:</label>

                <div class="col-md-6">
                    <app:dateControl name="transactionDate"
                                     value="" tabindex="1"
                                     required="true" validationMessage="Required"
                                     placeholder="dd/MM/yyyy">
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
                    <input type="text" class="k-textbox" id="supplierChalan" name="supplierChalan" tabindex="2"
                           required="required" validationMessage="Required" placeholder="Supplier Chalan No."/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="supplierChalan"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label label-required" for="vehicleId">Vehicle:</label>

                <div class="col-md-6">
                    <app:dropDownVehicle
                            tabindex="3"
                            dataModelName="dropDownVehicle"
                            name="vehicleId"
                            required="true"
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
                           placeholder="Vehicle Number" tabindex="4"/>
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
                    <inv:dropDownInventoryItemInFromSupplier
                            id="itemId"
                            name="itemId"
                            tabindex="6"
                            data_model_name="dropDownItemId"
                            transaction_id="${transactionId}"
                            url="${createLink(controller: 'invInventoryTransactionDetails', action: 'dropDownInventoryItemInFromSupplierReload')}"
                            validation_message="Required"
                            required="required"
                            onchange="onChangeItem()">
                    </inv:dropDownInventoryItemInFromSupplier>
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
                           tabindex="7"
                           required="required" validationMessage="Required"
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
                           tabindex="8"
                           required="required" validationMessage="Required"
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

                <div class="col-md-9">
                    <textarea class="k-textbox" id="comments" name="comments" tabindex="9"
                              placeholder="Place your comments here..." rows="3"></textarea>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="panel-footer">

    <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
            role="button" tabindex="10"
            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
    </button>

    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
            class="k-button k-button-icontext" role="button" tabindex="11"
            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
    </button>
</div>
</form>
</div>




