<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.application.utility.ItemTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Item Type
        </div>
    </div>

    <g:form name='itemTypeForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="categoryId">Category:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownItemCategory"
                                    required="true"
                                    validationMessage="Required"
                                    name="categoryId"
                                    tabindex="1"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_ITEM_CATEGORY}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="categoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
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
                    aria-disabled="false" onclick='resetItemTypeForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
