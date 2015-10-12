<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Supplier
        </div>

    </div>
    <g:form id="supplierForm" name="supplierForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="supplierTypeId">Supplier Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownSupplierType"
                                    name="supplierTypeId"
                                    required="true"
                                    validationMessage="Required"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_SUPPLIER_TYPE}"
                                    tabindex="1">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="supplierTypeId"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                   placeholder="Unique Supplier Name" required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="bankName">Bank Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="bankName" name="bankName"
                                   placeholder="Bank Name" tabindex="3"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="bankName"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="accountName">Account Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="accountName" name="accountName" tabindex="4"
                                   placeholder="Account Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accountName"></span>
                        </div>

                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="bankAccount">Account No:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="bankAccount" name="bankAccount"
                                   placeholder="Account No" tabindex="5"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="bankAccount"></span>
                        </div>

                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="address">Address:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="address" name="address" rows="4"
                                      placeholder="255 Char Max" tabindex="6"></textarea>
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
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
