<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var output =${output ? output: ''};
    var distributionPointListModel = false;

    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item:0, transition_interval:-1}
        );

        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        if (output.isError) {
            showError(output.message);
            return;
        }

        <sec:access url="/bankBranch/listDistributionPoint">
            initDPGrid();
            setUrlDPGrid();
            populateDPGrid(output);
            distributionPointListModel = output.gridObj;
        </sec:access>
	    $(document).attr('title', "Welcome to ARMS(Agent)");
    }

    // For distribution point grid Grid
    function initDPGrid() {
        $("#flexDistributionPoint").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "ID", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "Name", name: "name", width: 175, sortable: false, align: "left"},
                        {display: "District", name: "districtId", width: 155, sortable: false, align: "left"},
                        {display: "Bank", name: "bankId", width: 165, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadDPGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Name", name: "name", width: 125, sortable: true, align: "left"},
                        {display: "District", name: "districtId", width: 125, sortable: true, align: "left"},
                        {display: "Bank", name: "bankId", width: 125, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Distribution Points',
                    useRp: false,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height()+30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateDPGrid
                }
        );
    }


    function setUrlDPGrid() {
        var strUrl = "${createLink(controller: 'bankBranch', action: 'listDistributionPoint')}";
        $("#flexDistributionPoint").flexOptions({url: strUrl});
    }

    function reloadDPGrid() {
        $('#flexDistributionPoint').flexOptions({query: ''}).flexReload();
    }

    function populateDPGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexDistributionPoint").flexAddData(gridObj);
    }

</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/bankBranch/listDistributionPoint">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unapproved_po"></span>
                            <h5 class="feature">Distribution Point</h5>
                            <span>List of Distribution Point</span>
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
                <app:ifAnyUrl urls="/bankBranch/listDistributionPoint">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexDistributionPoint" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>

                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/></li>
            </ul>
        </div>
    </div>
</div>
