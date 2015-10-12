<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Transport cost For Purchase Order
        </div>
    </div>
    <form id="transportCostForm" name="transportCostForm" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="lblProjectName">Project:</label>

                        <div class="col-md-9">
                            <span id="lblProjectName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="lblPurchaseOrderId">PO ID:</label>

                        <div class="col-md-9">
                            <span id="lblPurchaseOrderId"></span>
                            <g:hiddenField name="purchaseOrderId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2"
                                      placeholder="255 Char Max" tabindex="1"
                                      validationMessage="Short Comments is Required"></textarea>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="amount">Amount:</label>

                        <div class="col-md-5">
                            <input type="text"
                                   tabindex="2"
                                   id="amount"
                                   name="amount"
                                   required validationMessage="Required"
                                   placeholder="Amount"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="amount"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="quantity">Quantity:</label>

                        <div class="col-md-5">
                            <input type="text"
                                   tabindex="3"
                                   id="quantity"
                                   name="quantity"
                                   required validationMessage="Required"
                                   placeholder="Quantity"/>
                        </div>

                        <div class="col-md-3">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="rate">Rate:</label>

                        <div class="col-md-5">
                            <input type="text"
                                   tabindex="4"
                                   id="rate"
                                   name="rate"
                                   validationMessage="Required"
                                   placeholder="Rate"/>
                        </div>

                        <div class="col-md-4 pull-left">
                            <span class="k-invalid-msg" data-for="rate"></span>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="6"
                    aria-disabled="false" onclick='resetCreateForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
