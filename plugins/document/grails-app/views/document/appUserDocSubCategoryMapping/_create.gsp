<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Add Member In <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                             key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>
        </div>
    </div>

    <form id="userSubCategoryMappingForm" name="userSubCategoryMappingForm" class="form-horizontal form-widgets"
          role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <input type="hidden" id="subCategoryId" name="subCategoryId"/>

            <div class="form-group">
                <div class="col-md-12">
                    <div class="form-group">
                        <div class="col-md-6">
                            <label class="col-md-3 control-label label-optional"><app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                                                                                    key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig> :</label>

                            <div class="col-md-6">
                                <span id="subCategoryName"></span>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6">
                            <label class="col-md-3 control-label label-required" for="userId">Member :</label>

                            <div class="col-md-6">
                                <doc:dropDownAppUserForSubCategory
                                        data_model_name="dropDownAppUser"
                                        name="userId"
                                        id="userId"
                                        sub_category_id="${subCategoryId}"
                                        url="${createLink(controller: 'docAllCategoryUserMapping', action: 'dropDownAppUserForSubCategoryReload')}"
                                        required="true"
                                        validationmessage="Required"
                                        tabindex="1">
                                </doc:dropDownAppUserForSubCategory>
                            </div>

                            <div class="col-md-3 pull-left">
                                <span class="k-invalid-msg" data-for="userId"></span>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6">
                            <label class="col-md-3 control-label" for="isSubCategoryManager">Manager :</label>

                            <div class="col-md-1">
                                <g:checkBox tabindex="2" name="isSubCategoryManager"/>
                            </div>
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
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>