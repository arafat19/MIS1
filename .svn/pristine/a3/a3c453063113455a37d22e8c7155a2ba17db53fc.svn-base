<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Purchase Order
        </div>
    </div>
    <form id="purchaseOrderForm" name="purchaseOrderForm" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="projectName">Project:</label>

                        <div class="col-md-9">
                            <span id="projectName"></span>
                            <input type="hidden" name="projectId" id="projectId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="purchaseRequestId">PR No:</label>

                        <div class="col-md-9">
                            <span id="lblPurchaseRequestId"></span>
                            <input type="hidden" name="purchaseRequestId" id="purchaseRequestId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="supplierId">Supplier:</label>

                        <div class="col-md-6">
                            <app:dropDownSupplier
                                    name="supplierId"
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownSupplier"
                                    tabindex="1">
                            </app:dropDownSupplier>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="supplierId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="paymentMethodId">Payment Method:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    tabindex="2"
                                    name="paymentMethodId"
                                    validationMessage="Required"
                                    required="true"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_PAYMENT_METHOD}"
                                    dataModelName="dropDownPaymentMethod">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="paymentMethodId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="discount">Discount:</label>

                        <div class="col-md-6">
                            <input type="text"
                                   tabindex="3"
                                   id="discount"
                                   name="discount"
                                   validationMessage="Required"
                                   placeholder="Discount"/>
                        </div>

                        <div class="col-md-4">
                            <span class="k-invalid-msg" data-for="discount"></span>
                        </div>

                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="modeOfPayment">Mode Of Payment:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="modeOfPayment" name="modeOfPayment"
                                      rows="3" tabindex="4"
                                      placeholder="255 Char Max"
                                      required validationMessage="Mode Of Payment is Required"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2"
                                      placeholder="255 Char Max" tabindex="5"
                                      validationMessage="Short Comments is Required"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="7"
                    aria-disabled="false" onclick='resetPOForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
