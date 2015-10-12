<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create L.C.
        </div>
    </div>

    <form id="accLcForm" name="accLcForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="lcNo">LC No:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="lcNo" name="lcNo"
                           placeholder="LC No" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="lcNo"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="itemTypeId">Item Type:</label>

                <div class="col-md-3">
                    <app:dropDownItemType dataModelName="dropDownItemType"
                                          required="true"
                                          validationMessage="Required"
                                          tabindex="4"
                                          name="itemTypeId"
                                          onchange="updateItemList();">
                    </app:dropDownItemType>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="itemTypeId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-3">
                    <input type="text" class="" id="amount" name="amount"
                           placeholder="Amount" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="amount"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="itemId">Item:</label>

                <div class="col-md-3">
                    <select id="itemId"
                            name="itemId"
                            tabindex="5"
                            required=""
                            validationMessage="Required">
                    </select>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="itemId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="bank">Bank:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="bank" name="bank"
                           placeholder="Bank Name" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="bank"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="supplierId">Supplier:</label>

                <div class="col-md-3">
                    <app:dropDownSupplier required="true"
                                          validationMessage="Required"
                                          dataModelName="dropDownSupplier"
                                          tabindex="6"
                                          name="supplierId">
                    </app:dropDownSupplier>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="supplierId"></span>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetAccLcForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>