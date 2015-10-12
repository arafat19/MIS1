<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            My <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>(s)

                <a class="label label-primary" style="float: right; cursor: pointer" href='#docCategory/showCategories?categoryId=${params.categoryId ? params.categoryId : 0L}'>
                    Back To <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                               key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>
                </a>

            <span class="glyphicon glyphicon-chevron-left" style="float: right"></span>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-md-7">
                <doc:listSubCategories categoryId="${categoryId}" subCategoryId="${subCategoryId}" name="listMySubCategory"></doc:listSubCategories>
            </div>

            <div class="col-md-5" id="subCategoryDetails">
                <g:include view="/document/docMySubCategory/_mySubCategoryDetails.gsp"></g:include>
            </div>
        </div>
    </div>

</div>
