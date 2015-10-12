<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector; com.athena.mis.utility.DateUtility" %>
<ul class="nav nav-pills nav-stacked" style="padding-bottom: 5px">
    <li class="active">
        <a ${(categoryDetails?.subCategoryCount > 0) ? "href='#docSubCategory/showSubCategories?id=${categoryDetails?.id}'" : "&nbsp"}>
            <span class="badge pull-right">${categoryDetails?.subCategoryCount}</span>
            <app:showSysConfig
                    pluginId="${PluginConnector.DOCUMENT_ID}"
                    key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>
        </a>
    </li>
</ul>

<div class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                               key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig> Details
        </div>
    </div>

    <div class="panel-body" id="categoryDetailsContainer">
        <doc:myCategoriesDetails category_id="${categoryId?.categoryId}"
                                 url="${createLink(controller: 'docCategory', action: 'viewMyCategoryDetails')}"/>

        %{--<form id='categoryDetailForm' name='categoryDetailForm' class="form-horizontal" role="form">
            <div class="form-group">
                <label class="col-md-3 control-label" for="name">Name:</label>
                <span id="name" class="col-md-9 pull-left">${categoryDetails?.name}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="createdBy">Created:</label>
                <span id="createdBy"
                      class="col-md-9 pull-left">${categoryDetails?.createdBy}${categoryDetails?.createdOn}</span>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="members">Members:</label>
                <span id="members" class="col-md-9 pull-left">${categoryDetails?.countMember}</span>
            </div>
        </form>--}%
    </div>
</div>

<div class="panel panel-primary" id="lstManagerDiv">
    <div class="panel-heading">
        <div class="panel-title">Manager(s)</div>
    </div>

    <div class="panel-body">
        <div class="form-group">
            <div id="lstManager" style="border-style: none"></div>
        </div>
    </div>
</div>

%{--<script type="text/javascript">
    var lstManagers;
    $(document).ready(function () {
        $('#lstManagerDiv').hide();
        lstManagers = ${(categoryDetails?.lstManager)?categoryDetails.lstManager:[]};
        if (lstManagers.length > 0) {
            $('#lstManagerDiv').show();
            initManagerList();
        }
    });

    function initManagerList() {
        $("#lstManager").kendoListView({
            dataSource: lstManagers,
            template: "<div class='list-group-item'> #:manager#</div>"
        });
    }
</script>--}%

