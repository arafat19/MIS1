<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility; com.athena.mis.application.utility.RoleUtility; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var poListModel = false;
    var prListModel = false;
    var indentListModel = false;
    var poStatusListModel = false;

    var outputPO =${outputPO ? outputPO : ''};
    var outputPR =${outputPR ? outputPR : ''};

    var outputIndent =${outputIndent ? outputIndent : ''};
    var outputPOStatus =${outputPOStatus ? outputPOStatus : ''};

    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();
    });

    function onLoadDashBoard() {
        if (outputPO.isError) {
            showError(outputPO.message);
            return;
        }
        if (outputPR.isError) {
            showError(outputPR.message);
            return;
        }

        if (outputIndent.isError) {
            showError(outputIndent.message);
            return;
        }

        if (outputPOStatus.isError) {
            showError(outputPOStatus.message);
            return;
        }

        <sec:access url="/procPurchaseOrder/listUnApprovedPO">
        initPOGrid();
        setUrlPOGrid();
        populatePOGrid(outputPO);
        poListModel = outputPO.gridObj;
        </sec:access>

        <sec:access url="/procPurchaseRequest/listUnApprovedPR">
        initPRGrid();
        setUrlPRGrid();
        populatePRGrid(outputPR);
        prListModel = outputPR.gridObj;
        </sec:access>

        <sec:access url="/procIndent/listOfUnApprovedIndent">
        initIndentGrid();
        setUrlIndentGrid();
        populateIndentGrid(outputIndent);
        indentListModel = outputIndent.gridObj;
        </sec:access>

        <sec:access url="/procPurchaseOrder/getPOStatusForDashBoard">
        initPOStatusGrid();
        setUrlPOStatusGrid();
        populatePOStatusGrid(outputPOStatus);
        poStatusListModel = outputPOStatus.poStatusList;
        </sec:access>
    }

    // For Po Grid
    function initPOGrid() {
        $("#flexUnapprovedPO").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "PO No", name: "id", width: 50, sortable: false, align: "right"},
                        {display: "Created By", name: "id", width: 170, sortable: false, align: "left"},
                        {display: "Approved(Director)", name: "id", width: 105, sortable: false, align: "left"},
                        {display: "Approved(PD)", name: "id", width: 105, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <sec:access url="/procReport/showPurchaseOrderRpt">
                        {name: 'Report', bclass: 'report', onpress: viewPurchaseOrderReport},
                        </sec:access>
                        <sec:access url="/procPurchaseOrder/approve">
                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DIRECTOR}">
                        {name: 'Approve as Director', bclass: 'approve', onpress: processPOApprovalForDirector},
                        </app:hasRoleType>
                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR}">
                        {name: 'Approve as PD', bclass: 'approve', onpress: processPOApprovalForProjectDirector},
                        </app:hasRoleType>
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadPOGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Un-Approved PO',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populatePOGrid
                }
        );
    }

    function reloadPOGrid() {
        $('#flexUnapprovedPO').flexOptions({query: ''}).flexReload();
    }

    function setUrlPOGrid() {
        var strUrl = "${createLink(controller: 'procPurchaseOrder', action: 'listUnApprovedPO')}";
        $("#flexUnapprovedPO").flexOptions({url: strUrl});
    }

    function populatePOGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedPO").flexAddData(gridObj);
    }

    // For PR Grid
    function initPRGrid() {
        $("#flexUnapprovedPR").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "PR No", name: "id", width: 50, sortable: false, align: "right"},
                        {display: "Created By", name: "id", width: 170, sortable: false, align: "left"},
                        {display: "Approved(Director)", name: "id", width: 105, sortable: false, align: "left"},
                        {display: "Approved(PD)", name: "id", width: 105, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <sec:access url="/procReport/showPurchaseRequestRpt">
                        {name: 'Report', bclass: 'report', onpress: viewPurchaseRequestReport},
                        </sec:access>
                        <sec:access url="/procPurchaseRequest/approve">
                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DIRECTOR}">
                        {name: 'Approve as Director', bclass: 'approve', onpress: processPRApprovalForDirector},
                        </app:hasRoleType>
                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_PROJECT_DIRECTOR}">
                        {name: 'Approve as PD', bclass: 'approve', onpress: processPRApprovalForProjectDirector},
                        </app:hasRoleType>
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadPRGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Un-Approved PR',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populatePRGrid
                }
        );
    }

    function reloadPRGrid() {
        $('#flexUnapprovedPR').flexOptions({query: ''}).flexReload();
    }

    function setUrlPRGrid() {
        var strUrl = "${createLink(controller: 'procPurchaseRequest', action: 'listUnApprovedPR')}";
        $("#flexUnapprovedPR").flexOptions({url: strUrl});
    }

    function populatePRGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedPR").flexAddData(gridObj);
    }

    // For Indent Grid
    function initIndentGrid() {
        $("#flexUnapprovedIndent").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Trace No", name: "id", width: 50, sortable: false, align: "right"},
                        {display: "Created By", name: "created_by", width: 170, sortable: false, align: "left"},
                        {display: "Item Count", name: "itemCount", width: 105, sortable: false, align: "left"},
                        {display: "Created On", name: "createdOn", width: 105, sortable: false, align: "left"}
                    ],
                    buttons: [
                        <sec:access url="/procReport/showIndentRpt">
                        {name: 'Report', bclass: 'report', onpress: viewIndentReport},
                        </sec:access>
                        <sec:access url="/procIndent/approve">
                        {name: 'Approve', bclass: 'approve', onpress: processIndentApproval},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadIndentGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Un-Approved Indent',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateIndentGrid
                }
        );
    }

    function reloadIndentGrid() {
        $('#flexUnapprovedIndent').flexOptions({query: ''}).flexReload();
    }

    function setUrlIndentGrid() {
        var strUrl = "${createLink(controller: 'procIndent', action: 'listOfUnApprovedIndent')}";
        $("#flexUnapprovedIndent").flexOptions({url: strUrl});
    }

    function populateIndentGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedIndent").flexAddData(gridObj);
    }


    /******************************* For PO Status Grid ***************************/
    function initPOStatusGrid() {
        $("#flexPOStatus").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 10, sortable: false, align: "right", hide: true},
                        {display: "id", name: "id", width: 50, sortable: false, align: "right", hide: true},
                        {display: "Project Code", name: "projectCode", width: 80, sortable: false, align: "left"},
                        {display: "Total Budget", name: "totalBudget", width: 155, sortable: false, align: "right"},
                        {display: "PO Count", name: "poCount", width: 60, sortable: false, align: "left"},
                        {display: "Total PO", name: "totalPO", width: 155, sortable: false, align: "right"}
                    ],
                    buttons: [
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadPOStatusGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'PO Status',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populatePOStatusGrid
                }
        );
    }

    function reloadPOStatusGrid() {
        $('#flexPOStatus').flexOptions({query: ''}).flexReload();
    }

    function setUrlPOStatusGrid() {
        var strUrl = "${createLink(controller: 'procPurchaseOrder', action: 'getPOStatusForDashBoard')}";
        $("#flexPOStatus").flexOptions({url: strUrl});
    }

    function populatePOStatusGrid(data) {
        var poStatusList = null;
        if (data.isError) {
            showError(data.message);
            poStatusList = getEmptyGridModel();
        } else {
            poStatusList = data.poStatusList;
        }
        $("#flexPOStatus").flexAddData(poStatusList);
    }

    // To view report
    function viewPurchaseOrderReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPO'), 'purchase order') == false) {
            return;
        }

        showLoadingSpinner(true);

        var purchaseOrderId = getSelectedIdFromGrid($('#flexUnapprovedPO'));

        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseOrderRpt')}?purchaseOrderId=" + purchaseOrderId;
        $.history.load(formatLink(loc));
        return false;
    }

    function viewPurchaseRequestReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPR'), 'purchase request') == false) {
            return;
        }

        showLoadingSpinner(true);

        var purchaseRequestId = getSelectedIdFromGrid($('#flexUnapprovedPR'));

        var loc = "${createLink(controller: 'procReport', action: 'showPurchaseRequestRpt')}?purchaseRequestId=" + purchaseRequestId;
        $.history.load(formatLink(loc));
        /*$.post(loc, function (data) {
         $('#contentHolder').html(data);
         showLoadingSpinner(false);
         });*/
        return false;
    }

    function viewIndentReport(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedIndent'), 'indent') == false) {
            return;
        }
        showLoadingSpinner(true);

        var indentId = getSelectedIdFromGrid($('#flexUnapprovedIndent'));

        var loc = "${createLink(controller: 'procReport', action: 'showIndentRpt')}?indentId=" + indentId;
        $.history.load(formatLink(loc));
        return false;
    }

    function processPOApprovalForDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPO'), 'purchase order') == false) {
            return;
        }
        var approvedByDirector = $(ids[ids.length - 1]).find('td').eq(3).find('div').text();
        if (approvedByDirector == "YES") {
            showError("Purchase Order already approved by director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_DIRECTOR}';
        approvePurchaseOrder(com, grid, approveAs);
    }

    function processPRApprovalForDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPR'), 'purchase request') == false) {
            return;
        }

        var approvedByDirector = $(ids[ids.length - 1]).find('td').eq(3).find('div').text();
        if (approvedByDirector == "YES") {
            showError("Purchase Request already approved by director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_DIRECTOR}';
        approvePurchaseRequest(com, grid, approveAs);
    }

    function processPOApprovalForProjectDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPO'), 'purchase order') == false) {
            return;
        }
        var approvedByProjectDirector = $(ids[ids.length - 1]).find('td').eq(4).find('div').text();
        if (approvedByProjectDirector == "YES") {
            showError("Purchase Order already approved by project director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_PROJECT_DIRECTOR}';
        approvePurchaseOrder(com, grid, approveAs);
    }
    function processPRApprovalForProjectDirector(com, grid) {
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flexUnapprovedPR'), 'purchase request') == false) {
            return;
        }

        var approvedByProjectDirector = $(ids[ids.length - 1]).find('td').eq(4).find('div').text();
        if (approvedByProjectDirector == "YES") {
            showError("Purchase Request already approved by project director.");
            return;
        }
        var approveAs = '${RoleUtility.ROLE_PROJECT_DIRECTOR}';
        approvePurchaseRequest(com, grid, approveAs);
    }
    function processIndentApproval(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedIndent'), 'indent') == false) {
            return;
        }
        approveIndent(com, grid);
    }

    function approvePurchaseOrder(com, grid, approveAs) {
        var ids = $('.trSelected', grid);
        showLoadingSpinner(true);
        var purchaseOrderId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller: 'procPurchaseOrder', action: 'approvePODashBoard')}?id=" + purchaseOrderId + '&approveAs=' + approveAs,
            success: executePostConditionForApprovePO,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }
    function approvePurchaseRequest(com, grid, approveAs) {
        var ids = $('.trSelected', grid);
        showLoadingSpinner(true);
        var purchaseRequestId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller: 'procPurchaseRequest', action: 'approvePRDashBoard')}?id=" + purchaseRequestId + '&approveAs=' + approveAs,
            success: executePostConditionForApprovePR,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }
    function approveIndent(com, grid) {
        var ids = $('.trSelected', grid);
        showLoadingSpinner(true);
        var indentId = $(ids[ids.length - 1]).attr('id').replace('row', '');
        $.ajax({
            url: "${createLink(controller: 'procIndent', action: 'approveIndentDashBoard')}?id=" + indentId,
            success: executePostConditionForApproveIndent,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }


    function executePostConditionForApprovePO(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                if (result.isBothApproved == true) {
                    var selectedRow = null;
                    $('.trSelected', $('#flexUnapprovedPO')).each(function (e) {
                        selectedRow = $(this).remove();
                    });
                    poListModel.total = parseInt(poListModel.total) - 1;
                    removeEntityFromGridRows(poListModel, selectedRow);
                    reloadPOGrid()
                } else {
                    updateListModel(poListModel, result.entity.id, 0);
                    reloadPOGrid()

                }
            } catch (e) {
                // Do Nothing
            }
        }
    }
    function executePostConditionForApprovePR(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                if (result.isBothApproved == true) {
                    var selectedRow = null;
                    $('.trSelected', $('#flexUnapprovedPR')).each(function (e) {
                        selectedRow = $(this).remove();
                    });
                    prListModel.total = parseInt(prListModel.total) - 1;
                    removeEntityFromGridRows(prListModel, selectedRow);
                    reloadPRGrid()
                } else {
                    updateListModel(prListModel, result.entity, 0);
                    reloadPRGrid()
                }
            } catch (e) {
                // Do Nothing
            }
        }
    }
    function executePostConditionForApproveIndent(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            try {
                showSuccess(result.message);
                var selectedRow = null;
                $('.trSelected', $('#flexUnapprovedIndent')).each(function (e) {
                    selectedRow = $(this).remove();
                });
                indentListModel.total = parseInt(indentListModel.total) - 1;
                removeEntityFromGridRows(indentListModel, selectedRow);
                $('#flexUnapprovedIndent').flexAddData(indentListModel);
            } catch (e) {
                // Do Nothing
            }
        }
    }

</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/procPurchaseOrder/listUnApprovedPO">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unapproved_po"></span>
                            <h5 class="feature">Un-Approved PO</h5>
                            <span>Summary of unapproved purchase order</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procPurchaseRequest/listUnApprovedPR">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unapproved_pr"></span>
                            <h5 class="feature">Un-Approved PR</h5>
                            <span>Summary of unapproved purchase request</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procIndent/listOfUnApprovedIndent">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon unapproved_indent"></span>
                            <h5 class="feature">Un-Approved Indent</h5>
                            <span>Summary of unapproved indent</span>
                        </a>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procPurchaseOrder/getPOStatusForDashBoard">
                    <li>
                        <a href="javascript:;">
                            <span class="dashboard_icon po_status"></span>
                            <h5 class="feature">PO Status</h5>
                            <span>Purchase Order Status List</span>
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
                <app:ifAnyUrl urls="/procPurchaseOrder/listUnApprovedPO">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedPO" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procPurchaseRequest/listUnApprovedPR">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedPR" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procIndent/listOfUnApprovedIndent">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedIndent" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/procPurchaseOrder/getPOStatusForDashBoard">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexPOStatus" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/>
                </li>
            </ul>
        </div>
    </div>
</div>