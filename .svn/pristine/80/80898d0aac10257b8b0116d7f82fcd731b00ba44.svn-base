<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Purchase Order Details
        </div>
    </div>
    <form id="purchaseOrderDetailsForm" name="purchaseOrderDetailsForm" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="purchaseRequestId" id="purchaseRequestId"/>
            <input type="hidden" name="purchaseRequestDetailsId" id="purchaseRequestDetailsId"/>
            <input type="hidden" name="purchaseRequestDetailsAvailableQuantity" id="purchaseRequestDetailsAvailableQuantity"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="projectName">Project:</label>

                        <div class="col-md-9">
                            <span id="projectName"></span>
                            <g:hiddenField name="projectId" class="required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="purchaseOrderId">PO ID:</label>

                        <div class="col-md-9">
                            <span id="lblPurchaseOrderId"></span>
                            <g:hiddenField name="purchaseOrderId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="itemTypeId">Item Type:</label>

                        <div class="col-md-5">
                            <app:dropDownItemType name="itemTypeId"
                                                  tabindex="1"
                                                  required="true"
                                                  validationMessage="Required"
                                                  onchange="onChangeItemType();"
                                                  dataModelName="dropDownItemType">
                            </app:dropDownItemType>
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="itemTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="itemId">Item:</label>

                        <div class="col-md-5">
                            <select name="itemId"
                                    id="itemId"
                                    tabindex="2"
                                    required="true"
                                    validationMessage="Required"
                                    onchange="updateMaterial();">
                            </select>
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="vatTax">Vat/Tax:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   tabindex="3"
                                   id="vatTax"
                                   name="vatTax"
                                   placeholder="Vat/Tax"
                                   validationMessage="Required"/>
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="vatTax"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-required"
                               for="rate">Rate:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   tabindex="4"
                                   id="rate"
                                   name="rate"
                                   placeholder="Rate"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3">
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="rate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required"
                               for="quantity">Quantity:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   tabindex="5"
                                   id="quantity"
                                   name="quantity"
                                   placeholder="Quantity"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3">
                            <span id="unit"></span>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>



                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional"
                               for="comments">Comments:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="3"
                                      placeholder="255 Char Max" tabindex="6"
                                      validationMessage="Short Comments is Required"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
