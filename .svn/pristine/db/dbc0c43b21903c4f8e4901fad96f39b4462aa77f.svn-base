<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Budget Details
        </div>
    </div>

    <form id="budgetDetailsForm" name="budgetDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="projectId" id="projectId"/>
            <input type="hidden" name="budgetId" id="budgetId"/>
            <input type="hidden" name="budgetItem" id="budgetItem"/>

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
                        <label class="col-md-3 control-label label-required" for="itemTypeId">Item Type:</label>

                        <div class="col-md-6">
                            <app:dropDownItemType
                                    tabindex="1"
                                    required="true"
                                    validationMessage="Required"
                                    name="itemTypeId"
                                    onchange="onChangeItemType();"
                                    dataModelName="dropDownItemType">
                            </app:dropDownItemType>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemId">Item:</label>

                        <div class="col-md-6">
                            <select id="itemId"
                                    name="itemId"
                                    required="required"
                                    validationMessage="Required"
                                    tabindex="2"
                                    onchange="onChangeItem();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="quantity">Quantity:</label>

                        <div class="col-md-4">
                            <input type="text" id="quantity" name="quantity" tabindex="3"
                                   placeholder="Quantity of Item" required validationMessage="Required"
                                   maxlength="50" onchange="calculateTotal();"/>
                        </div>

                        <div class="col-md-2">
                            <span id="unit"></span>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="rate">Estimated Rate:</label>

                        <div class="col-md-4">
                            <input type="text" id="rate" name="rate" tabindex="4" maxlength="50"
                                   placeholder="Rate of Item" required validationMessage="Required"
                                   onchange="calculateTotal();"/>
                        </div>

                        <div class="col-md-2">
                            <span id="perUnitName"></span>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="rate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="total">Total Amount:</label>

                        <div class="col-md-9">
                            <span id="total"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label"
                               for="isConsumedAgainstFixedAsset">Use Against Asset:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="5" name="isConsumedAgainstFixedAsset"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="2" tabindex="6"
                                      placeholder="255 Char Max" maxlength="255"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetFormBudgetDetails();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>