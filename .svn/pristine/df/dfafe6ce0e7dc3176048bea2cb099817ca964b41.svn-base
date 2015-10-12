<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Lease Account
        </div>
    </div>

    <form id="accLeaseAccountForm" name="accLeaseAccountForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="institution">Institution:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="institution" name="institution"
                           placeholder="Institution" required validationMessage="Required" tabindex="1"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="institution"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="itemTypeId">Item Type:</label>

                <div class="col-md-3">
                    <app:dropDownItemType dataModelName="dropDownItemType"
                                          required="true"
                                          validationMessage="Required"
                                          name="itemTypeId"
                                          onchange="updateItemList();"
                                          tabindex="6">
                    </app:dropDownItemType>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="itemTypeId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="amount">Amount:</label>

                <div class="col-md-3">
                    <input type="text" class="" id="amount" name="amount" tabindex="2"
                           placeholder="Amount" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="amount"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="itemId">Item:</label>

                <div class="col-md-3">
                    <select id="itemId"
                            name="itemId"
                            required="required"
                            validationMessage="Required"
                            tabindex="7">
                    </select>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="itemId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="interestRate">Interest Rate:</label>

                <div class="col-md-3">
                    <input type="text" class="" id="interestRate" name="interestRate" tabindex="3"
                           placeholder="Interest Rate" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="interestRate"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="startDate">Start Date:</label>

                <div class="col-md-3">
                    <app:dateControl name="startDate"
                                     tabindex="8"
                                     required="true"
                                     validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="startDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="noOfInstallment">No Of Installment:</label>

                <div class="col-md-3">
                    <input type="text" class="" id="noOfInstallment" name="noOfInstallment" tabindex="4"
                           placeholder="No. Of Installment" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="noOfInstallment"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="endDate">End Date:</label>

                <div class="col-md-3">
                    <app:dateControl name="endDate"
                                     tabindex="9"
                                     required="true"
                                     validationMessage="Required">
                    </app:dateControl>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="endDate"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="installmentVolume">Installment Volume:</label>

                <div class="col-md-3">
                    <input type="text" class="" id="installmentVolume" name="installmentVolume" tabindex="5"
                           placeholder="Installment Volume" required validationMessage="Required"/>
                </div>

                <div class="col-md-1 pull-left">
                    <span class="k-invalid-msg" data-for="installmentVolume"></span>
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
                    aria-disabled="false" onclick='resetAccLeaseForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
