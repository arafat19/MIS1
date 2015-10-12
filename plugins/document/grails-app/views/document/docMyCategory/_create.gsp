<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            My <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>(s)
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-md-7">
                <doc:listMyCategories name="listMyCategory" category_id="${category_id}"></doc:listMyCategories>
            </div>

            <div class="col-md-5" id="myCategoryDetails">
                <g:include view="/document/docMyCategory/_showMyCategoryDetails.gsp"/>
            </div>
        </div>
    </div>
</div>
