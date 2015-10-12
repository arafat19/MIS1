<%@ page import="com.athena.mis.application.utility.ItemCategoryCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">

        <div class="panel-title">
            Create Supplier's Item
        </div>

    </div>
    <g:form id="supplierItemForm" name="supplierItemForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <g:hiddenField name="hidItemId"/>
            <g:hiddenField name="supplierId"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="supplierName">Supplier Name:</label>

                        <div class="col-md-9">
                            <span id="supplierName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="supplierAddress">Address:</label>

                        <div class="col-md-9">
                            <span id="supplierAddress"></span>
                        </div>
                    </div>


                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="supplierAccount">Bank Account:</label>

                        <div class="col-md-9">
                            <span id="supplierAccount"></span>
                        </div>
                    </div>


                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="supplierBankName">Bank Name:</label>

                        <div class="col-md-9">
                            <span id="supplierBankName"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemTypeId">Item Type:</label>

                        <div class="col-md-6">
                            <app:dropDownItemType
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownItemType"
                                    name="itemTypeId" tabindex="1"
                                    onchange="onChangeItemType();">
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

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="itemCode">Code:</label>

                        <div class="col-md-9">
                            <span id="itemCode"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="itemUnit">Unit:</label>

                        <div class="col-md-9">
                            <span id="itemUnit"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetFormSupplierItem();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>

