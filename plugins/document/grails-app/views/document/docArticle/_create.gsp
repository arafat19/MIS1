<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<div class="row">
    <div class="col-md-7">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Add Article
                </div>
            </div>

            <form id='articleForm' class="form-horizontal form-widgets" name='articleForm' role="form" method="post">
                <input type="hidden" name="id" id="id"/>
                <input type="hidden" name="version" id="version"/>

                <div class="panel-body">
                    <div class="form-group">
                        <div class="col-md-2">
                            <label class="control-label label-required"
                                   for="categoryId"><app:showSysConfig
                                    pluginId="${PluginConnector.DOCUMENT_ID}"
                                    key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>:
                            </label>
                        </div>

                        <div class="col-md-4">
                            <doc:dropDownCategory
                                    dataModelName="dropDownCategory"
                                    name="categoryId"
                                    required="true"
                                    defaultValue="${output.categoryId}"
                                    validationmessage="Required"
                                    onchange="onChangeCategory()"
                                    tabindex="1">
                            </doc:dropDownCategory>
                        </div>

                        <div class="col-md-2">
                            <label class="control-label label-required"
                                   for="subCategoryId"><app:showSysConfig
                                    pluginId="${PluginConnector.DOCUMENT_ID}"
                                    key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>:
                            </label>
                        </div>

                        <div class="col-md-4" id="containerSubCategory">
                            <doc:dropDownSubCategory
                                    data_model_name="dropDownSubCategory"
                                    id="subCategoryId"
                                    name="subCategoryId"
                                    required="true"
                                    category_id="${output.categoryId}"
                                    default_value="${output.subCategoryId}"
                                    url="${createLink(controller: 'docSubCategory', action: 'dropDownSubCategoryReload')}"
                                    validation_message="Required"
                                    tabindex="2">
                            </doc:dropDownSubCategory>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <label class="control-label label-required" for="title">Title:</label>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <input type="text" class="k-textbox" id="title" name="title" tabindex="3" maxlength="255"
                                   placeholder="Article Title (Max 255 Char)" required validationMessage="Required"/>
                            <span class="k-invalid-msg" data-for="title"></span>
                        </div>
                    </div>

                    <div>
                        <textarea id="details" name="details" tabindex="4"
                                  required validationMessage="Required"></textarea>
                        <span class="k-invalid-msg" data-for="details"></span>
                    </div>
                </div>

                <div class="panel-footer">
                    <button name="create" id="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
                    </button>
                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="col-md-5" id="articleGridContainer">
        <table id="flexArticle"></table>
    </div>
</div>