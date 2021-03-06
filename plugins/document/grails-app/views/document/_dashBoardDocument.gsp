<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.document.config.DocSysConfigurationCacheUtility; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var output =${output ? output: ''};
    var favouriteListModel = false;
    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );

        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        if (output.isError) {
            showError(output.message);
            return;
        }

        %{--<sec:access url="">--}%
        initFavouriteListGrid();
        setUrlFavouriteListGrid();
        populateFavouriteListGrid(output);
//        favouriteListModel = output.gridObj;
        %{--</sec:access>--}%
    }

    // For favourite point grid Grid
    function initFavouriteListGrid() {
        $("#flexFavouriteList").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
//                        {display: "ID", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "CategoryId", name: "categoryid", width: 50, sortable: false, align: "right", hide: true},
                        {display: "SubCategoryId", name: "subcategoryid", width: 50, sortable: false, align: "right", hide: true},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "category_name", width: 236, sortable: false, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "name", width: 261, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <app:ifAllUrl urls="/docSubCategory/viewSubCategoryDetails">
                        {name: 'View Details', bclass: 'clear-add', onpress: showSubCategoryDetails},
                        </app:ifAllUrl>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadFavouriteListGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "dc.name", width: 125, sortable: true, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "dsc.name", width: 125, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'My Sub Category Favourite List',
                    useRp: false,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() + 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateFavouriteListGrid
                }
        );
    }

    function setUrlFavouriteListGrid() {
        var strUrl = "${createLink(controller: 'docSubCategory', action: 'listSubCategoryFavourite')}";
        $("#flexFavouriteList").flexOptions({url: strUrl});
    }

    function reloadFavouriteListGrid() {
        $('#flexFavouriteList').flexOptions({query: ''}).flexReload();
    }

    function populateFavouriteListGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexFavouriteList").flexAddData(gridObj);
    }

    function executePreConditionForDelete() {
        if (executeCommonPreConditionForSelect($('#flexFavouriteList'), ' favourite items') == false) {
            return false;
        }
        return true;
    }

    function showSubCategoryDetails(com, flexGrid) {
        if (executePreConditionForDelete() == false) {
            return;
        }
        var selectedIds = $('.trSelected', flexGrid);
        var categoryId = $(selectedIds[selectedIds.length - 1]).find("td").eq(1).text();
        var subCategoryId = $(selectedIds[selectedIds.length - 1]).find("td").eq(2).text();

        showLoadingSpinner(true);
        $.ajax({
            url: "/docSubCategory/showSubCategories?categoryId=" + categoryId + "&subCategoryId=" + subCategoryId,
            success: executePostForShowDetails,
            complete:  showLoadingSpinner(false),
            type: 'post'
        });
    }
    function executePostForShowDetails(data) {
        $('#contentHolder').html(data);
//        $("#subCategoryDetails").show();
    }

</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/docSubCategory/listSubCategoryFavourite">
                <li>
                    <a href="javascript:;">
                        <span class="dashboard_icon unapproved_po"></span>
                        <h5 class="feature">MY SUB CATEGORY FAVOURITES</h5>
                        <span>List of Sub Category</span>
                    </a>
                </li>
                </app:ifAnyUrl>

                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>Your favourite content here</span></a>
                </li>
            </ul>
            <ul id="output">
                <app:ifAnyUrl urls="/docSubCategory/listSubCategoryFavourite">
                <li style="height: 100%">
                    <div class="dashboardContainer">
                        <table id="flexFavouriteList" style="display:none"></table>
                    </div>
                </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/>
                </li>
            </ul>
        </div>
    </div>
</div>
