<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Show Budget Details
        </div>
    </div>

    <form id="budgetDetailsForm" name="budgetDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="projectName">Project Name:</label>

                        <div class="col-md-9">
                            <span id="projectName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="budgetScope">Budget Scope:</label>

                        <div class="col-md-9">
                            <span id="budgetScope"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="budgetItemSpan">Budget Item:</label>

                        <div class="col-md-9">
                            <span id="budgetItemSpan"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="detailsSpan">Details:</label>

                        <div class="col-md-9">
                            <span id="detailsSpan"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="itemTypeId">Item Type:</label>

                        <div class="col-md-9">
                            <span id="itemTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="itemId">Item:</label>

                        <div class="col-md-9">
                            <span id="itemId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="quantity">Quantity:</label>

                        <div class="col-md-9">
                            <span id="quantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="rate">Estimated Rate:</label>

                        <div class="col-md-9">
                            <span id="rate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="total">Total Amount:</label>

                        <div class="col-md-9">
                            <span id="total"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <span id="comments"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetFormBudgetDetails();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>