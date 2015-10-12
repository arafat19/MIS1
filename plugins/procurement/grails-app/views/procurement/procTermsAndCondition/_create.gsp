<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Terms and Condition For Purchase Order
        </div>
    </div>
    <form id="procTermsAndConditionForm" name="procTermsAndConditionForm" class="form-horizontal form-widgets"
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
                </div>

                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required"
                               for="details">Details:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="details" name="details" rows="1" tabindex="1"
                                      placeholder="255 Char Max" required validationMessage="Short details is Required"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="3"
                    aria-disabled="false" onclick='resetCreateForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
