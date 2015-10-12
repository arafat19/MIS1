<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var budgetStatusListModel = false;
    var outputBudgetStatus =${outputBudgetStatus ? outputBudgetStatus : ''};
    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        if (outputBudgetStatus.isError) {
            showError(outputBudgetStatus.message);
            return;
        }

        <sec:access url="/budgBudget/getBudgetStatusForDashBoard">
        initBudgetStatusGrid();
        setUrlBudgetStatusGrid();
        budgetStatusListModel = outputBudgetStatus.budgetStatusList;
        populateBudgetStatusGrid(outputBudgetStatus);
        </sec:access>
    }

    /******************************* For PO Status Grid ***************************/
    function initBudgetStatusGrid() {
        $("#flexBudgetStatus").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 10, sortable: false, align: "right", hide: true},
                        {display: "id", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "Project Code", name: "projectCode", width: 80, sortable: false, align: "left"},
                        {display: "Total Budget", name: "totalBudget", width: 130, sortable: false, align: "right"},
                        {display: "Contract Value", name: "contractValue", width: 120, sortable: false, align: "right"},
                        {display: "Revenue Margin", name: "revenueMargin", width: 120, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadBudgetStatusGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'Budget Status',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateBudgetStatusGrid
                }
        );
    }

    function reloadBudgetStatusGrid() {
        $('#flexBudgetStatus').flexOptions({query: ''}).flexReload();
    }

    function setUrlBudgetStatusGrid() {
        var strUrl = "${createLink(controller: 'budgBudget', action: 'getBudgetStatusForDashBoard')}";
        $("#flexBudgetStatus").flexOptions({url: strUrl});
    }

    function populateBudgetStatusGrid(data) {
        budgetStatusListModel = null;
        if (data.isError) {
            showError(data.message);
            budgetStatusListModel = getEmptyGridModel();
        } else {
            budgetStatusListModel = data.budgetStatusList;
        }
        $("#flexBudgetStatus").flexAddData(budgetStatusListModel);
    }
</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/budgBudget/getBudgetStatusForDashBoard">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon budget_status"></span>
                            <h5 class="feature">Budget Status</h5>
                            <span>Budget Status List</span>

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
                <app:ifAnyUrl urls="/budgBudget/getBudgetStatusForDashBoard">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexBudgetStatus" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/></li>
            </ul>
        </div>
    </div>
</div>
