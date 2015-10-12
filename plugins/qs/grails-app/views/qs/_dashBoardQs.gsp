<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var qsStatusListModel = false;
    var outputQsStatus =${outputQsStatus ? outputQsStatus : ''};
    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        if (outputQsStatus.isError) {
            showError(outputQsStatus.message);
            return;
        }

        <sec:access url="/qsMeasurement/getQsStatusForDashBoard">
        initQsStatusGrid();
        setUrlQsStatusGrid();
        qsStatusListModel = outputQsStatus.qsStatusList;
        populateQsStatusGrid(outputQsStatus);
        </sec:access>
    }

    /******************************* For PO Status Grid ***************************/
    function initQsStatusGrid() {
        $("#flexQsStatus").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 10, sortable: false, align: "right", hide: true},
                        {display: "id", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "Project Code", name: "projectCode", width: 80, sortable: false, align: "left"},
                        {display: "Achieved(% Intern)", name: "strAchievedIntern", width: 120, sortable: false, align: "left"},
                        {display: "Certified(Intern)", name: "certifiedIntern", width: 260, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadQsStatusGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Qs Status',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateQsStatusGrid
                }
        );
    }

    function reloadQsStatusGrid() {
        $('#flexQsStatus').flexOptions({query: ''}).flexReload();
    }

    function setUrlQsStatusGrid() {
        var strUrl = "${createLink(controller: 'qsMeasurement', action: 'getQsStatusForDashBoard')}";
        $("#flexQsStatus").flexOptions({url: strUrl});
    }

    function populateQsStatusGrid(data) {
        qsStatusListModel = null;
        if (data.isError) {
            showError(data.message);
            qsStatusListModel = getEmptyGridModel();
        } else {
            qsStatusListModel = data.qsStatusList;
        }
        $("#flexQsStatus").flexAddData(qsStatusListModel);
    }
</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/qsMeasurement/getQsStatusForDashBoard">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon qs_status"></span>
                            <h5 class="feature">Qs Status</h5>
                            <span>Qs Status List</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>Your favourite content here</span></a>
                </li>
            </ul>
            <ul id="output" style="height: 100%">
                <app:ifAnyUrl urls="/qsMeasurement/getQsStatusForDashBoard">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexQsStatus" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/></li>
            </li>

            </ul>
        </div>
    </div>
</div>
