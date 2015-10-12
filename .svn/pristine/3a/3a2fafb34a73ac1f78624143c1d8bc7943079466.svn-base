<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Purchase Request Details
        </div>

    </div>
    <form id="purchaseRequestDetailsForm" name="purchaseRequestDetailsForm" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">

            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="projectName">Project:</label>

                        <div class="col-md-9">
                            <span id="projectName"></span><g:hiddenField name="projectId"/>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="purchaseRequestItem">P.R No:</label>

                        <div class="col-md-9">
                            <span id="purchaseRequestItem"></span><input type="hidden" id="purchaseRequestId" name="purchaseRequestId"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="comments">Comments:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="comments" name="comments" rows="4"
                                      placeholder="255 Char Max" tabindex="1"
                                      validationMessage="Short Description is Required"></textarea>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="itemTypeId">Item Type:</label>

                        <div class="col-md-6">
                            <app:dropDownItemType name="itemTypeId"
                                                  tabindex="2"
                                                  required="true"
                                                  validationMessage="Required"
                                                  onchange="onChangeItemType();"
                                                  dataModelName="dropDownItemType">
                            </app:dropDownItemType>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="itemId">Item:</label>

                        <div class="col-md-6">
                            <select name="itemId"
                                    id="itemId"
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="3"
                                    onchange="onChangeItemDropDown();"></select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="quantity">Quantity:</label>

                        <div class="col-md-4">
                            <input type="text"
                                   placeholder="Quantity"
                                   tabindex="4"
                                   id="quantity"
                                   name="quantity"
                                   onchange="calculateTotal()"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3">
                            <span id="itemUnitName"></span>
                        </div>

                        <div class="col-md-2">
                            <span class="k-invalid-msg" data-for="quantity"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="rate">Estimated Rate:</label>

                        <div class="col-md-4">
                            <input type="text" placeholder="Rate" id="rate" tabindex="5"
                                   name="rate" onchange="calculateTotal()" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3">
                            <span id="perItemUnitName"></span>
                        </div>

                        <div class="col-md-2">
                            <span class="k-invalid-msg" data-for="rate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label"
                               for="total">Total Amount:</label>

                        <div class="col-md-9">
                            <span id="total"></span>
                        </div>
                    </div>

                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="6"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="7"
                    aria-disabled="false" onclick='resetCreateForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>



