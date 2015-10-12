<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Voucher Type Mapping
        </div>
    </div>

    <form id="accVoucherTypeCoaForm" name="accVoucherTypeCoaForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="accVoucherTypeId">Voucher Type:</label>

                <div class="col-md-3">
                    <app:dropDownSystemEntity dataModelName="dropDownVoucherType"
                                              tabindex="1"
                                              name="accVoucherTypeId"
                                              required="true"
                                              validationMessage="Required"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="accVoucherTypeId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="code">COA Code:</label>

                <div class="col-md-3">
                    <input type="text" class="k-textbox" id="code" name="code"
                           placeholder="Chart of Account Code" required validationMessage="Required"/>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="code"></span>
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
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>

