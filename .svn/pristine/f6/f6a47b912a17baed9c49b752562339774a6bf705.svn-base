<%@ page import="com.athena.mis.application.utility.ItemCategoryCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Non-Inventory Item
        </div>
    </div>

    <g:form name='nonInventoryItemForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="code">Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="code"></span>
                        </div>
                    </div>

                </div>

                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="unit">Unit:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="unit" name="unit" tabindex="3"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="unit"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemTypeId">Item Type:</label>

                        <div class="col-md-6">
                            <app:dropDownItemType
                                    name="itemTypeId"
                                    dataModelName="dropDownItemType"
                                    required="true"
                                    validationMessage="Required"
                                    tabindex="4"
                                    categoryId="${ItemCategoryCacheUtility.NON_INVENTORY}">
                            </app:dropDownItemType>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemTypeId"></span>
                        </div>
                    </div>

                </div>

            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="5"
                    aria-disabled="false" onclick='resetNonInventoryItemForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
