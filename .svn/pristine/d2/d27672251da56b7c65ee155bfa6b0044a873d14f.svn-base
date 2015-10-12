<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    var outputCon =${outputCon ? outputCon : ''};
    var outputSupplier =${outputSupplier ? outputSupplier : ''};
    var outputInvOut =${outputInvOut ? outputInvOut : ''};
    var outputInFromInventory =${outputInFromInventory ? outputInFromInventory : ''};

    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item: 0, transition_interval: -1}
        );
        onLoadDashBoard();

    });

    function onLoadDashBoard() {
        if (outputCon.isError) {
            showError(outputCon.message);
            return;
        }
        <sec:access url="/invInventoryTransaction/listOfUnApprovedConsumption">
        initConsumptionGrid();
        setUrlConsumptionGrid();
        populateConsumptionGrid(outputCon);
        </sec:access>

        <sec:access url="/invInventoryTransaction/listOfUnApprovedInFromSupplier">
        initSupplierGrid();
        setUrlSupplierGrid();
        populateSupplierGrid(outputSupplier);
        </sec:access>

        <sec:access url="/invInventoryTransaction/listOfUnApprovedInventoryOut">
        initInvOutGrid();
        setUrlInvOutGrid();
        populateInvOutGrid(outputInvOut);
        </sec:access>

        <sec:access url="/invInventoryTransaction/listOfUnApprovedInFromInventory">
        initInFromInventoryGrid();
        setUrlInFromInventoryGrid();
        populateInFromInventoryGrid(outputInFromInventory);
        </sec:access>
    }

    // --------- For listOfUnApprovedConsumption ---------
    function initConsumptionGrid() {
        $("#flexUnapprovedConsumption").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Transaction ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Inventory Name", name: "inventory_name", width: 210, sortable: false, align: "left"},
                        {display: "Item Count", name: "item_count", width: 80, sortable: false, align: "right"},
                        {display: "Pending", name: "total_pending", width: 80, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/invInventoryTransactionDetails/approveInventoryConsumptionDetails">
                        {name: 'Details', bclass: 'details', onpress: showInvConsumptionDetails},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadConsumptionGrid},
                        {separator: true}
                    ],
                    sortname: "iit.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Pending Consumptions',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateConsumptionGrid
                }
        );
    }

    function populateConsumptionGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedConsumption").flexAddData(gridObj);
    }

    function setUrlConsumptionGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listOfUnApprovedConsumption')}";
        $("#flexUnapprovedConsumption").flexOptions({url: strUrl});
    }

    // --------- For listOfUnApprovedInFromSupplier ---------
    function initSupplierGrid() {
        $("#flexUnapprovedInFromSupplier").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Transaction ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Inventory Name", name: "inventory_name", width: 210, sortable: false, align: "left"},
                        {display: "Item Count", name: "item_count", width: 80, sortable: false, align: "right"},
                        {display: "Pending", name: "total_pending", width: 80, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/invInventoryTransactionDetails/approveInventoryInDetailsFromSupplier">
                        {name: 'Details', bclass: 'details', onpress: showInFromSupplierDetails},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadSupplierGrid},
                        {separator: true}
                    ],
                    sortname: "iit.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Pending in From Supplier',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateSupplierGrid
                }
        );
    }

    function setUrlSupplierGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listOfUnApprovedInFromSupplier')}";
        $("#flexUnapprovedInFromSupplier").flexOptions({url: strUrl});
    }

    function populateSupplierGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedInFromSupplier").flexAddData(gridObj);
    }

    // --------- For listOfUnApprovedInventoryOut ---------
    function initInvOutGrid() {
        $("#flexUnapprovedInventoryOut").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Transaction ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Inventory Name", name: "inventory_name", width: 210, sortable: false, align: "left"},
                        {display: "Item Count", name: "item_count", width: 80, sortable: false, align: "right"},
                        {display: "Pending", name: "total_pending", width: 80, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/invInventoryTransactionDetails/approveInventoryOutDetails">
                        {name: 'Details', bclass: 'details', onpress: showInventoryOutDetails},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadInvOutGrid},
                        {separator: true}
                    ],
                    sortname: "iit.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Pending Inventory Out',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateInvOutGrid
                }
        );
    }

    function setUrlInvOutGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listOfUnApprovedInventoryOut')}";
        $("#flexUnapprovedInventoryOut").flexOptions({url: strUrl});
    }

    function populateInvOutGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedInventoryOut").flexAddData(gridObj);
    }

    // --------- For listOfUnApprovedInFromInventory ---------
    function initInFromInventoryGrid() {
        $("#flexUnapprovedInFromInventory").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 20, sortable: false, align: "left", hide: true},
                        {display: "Transaction ID", name: "id", width: 80, sortable: false, align: "left"},
                        {display: "Inventory Name", name: "inventory_name", width: 210, sortable: false, align: "left"},
                        {display: "Item Count", name: "item_count", width: 80, sortable: false, align: "right"},
                        {display: "Pending", name: "total_pending", width: 80, sortable: false, align: "right"}
                    ],
                    buttons: [
                        <sec:access url="/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory">
                        {name: 'Details', bclass: 'details', onpress: showInFromInventoryDetails},
                        </sec:access>
                        {name: 'Refresh', bclass: 'clear-results', onpress: reloadInFromInventoryGrid},
                        {separator: true}
                    ],
                    sortname: "iit.id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Pending Inventory In From Inventory',
                    useRp: true,
                    rp: 10,
                    showTableToggleBtn: false,
                    height: $('.dashboardContainer').height() - 122,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateInFromInventoryGrid
                }
        );
    }

    function setUrlInFromInventoryGrid() {
        var strUrl = "${createLink(controller: 'invInventoryTransaction', action: 'listOfUnApprovedInFromInventory')}";
        $("#flexUnapprovedInFromInventory").flexOptions({url: strUrl});
    }

    function populateInFromInventoryGrid(data) {
        var gridObj = null;
        if (data.isError) {
            showError(data.message);
            gridObj = getEmptyGridModel();
        } else {
            gridObj = data.gridObj;
        }
        $("#flexUnapprovedInFromInventory").flexAddData(gridObj);
    }


    //------------- Reload Grid -----------------
    function reloadConsumptionGrid() {
        $('#flexUnapprovedConsumption').flexOptions({query: ''}).flexReload();
    }

    function reloadSupplierGrid() {
        $('#flexUnapprovedInFromSupplier').flexOptions({query: ''}).flexReload();
    }

    function reloadInvOutGrid() {
        $('#flexUnapprovedInventoryOut').flexOptions({query: ''}).flexReload();
    }

    function reloadInFromInventoryGrid() {
        $('#flexUnapprovedInFromInventory').flexOptions({query: ''}).flexReload();
    }

    // --------- To show Details ---------

    function showInvConsumptionDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedConsumption'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flexUnapprovedConsumption'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnApprovedInventoryConsumptionDetails')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function showInFromSupplierDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedInFromSupplier'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flexUnapprovedInFromSupplier'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnapprovedInvInFromSupplier')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }

    function showInventoryOutDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedInventoryOut'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryOutId = getSelectedIdFromGrid($('#flexUnapprovedInventoryOut'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnApprovedInventoryOutDetails')}?inventoryOutId=" + inventoryOutId;
        $.history.load(formatLink(loc));
        return false;
    }

    function showInFromInventoryDetails(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexUnapprovedInFromInventory'), 'transaction') == false) {
            return;
        }
        showLoadingSpinner(true);
        var inventoryTransactionId = getSelectedIdFromGrid($('#flexUnapprovedInFromInventory'));
        var loc = "${createLink(controller: 'invInventoryTransactionDetails', action: 'showUnapprovedInvInFromInventory')}?inventoryTransactionId=" + inventoryTransactionId;
        $.history.load(formatLink(loc));
        return false;
    }


</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedConsumption">
                    <li><a href="javascript:;">
                        <span class="dashboard_icon pending_consumption"></span>
                        <h5 class="feature">Pending Consumption</h5>
                        <span>Summary of pending consumption</span></a></li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInFromSupplier">
                    <li><a href="javascript:;">
                        <span class="dashboard_icon panding_in_from_supplier"></span>
                        <h5 class="feature">Pending In from Supplier</h5>
                        <span>Summary of pending in From Supplier</span></a></li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInventoryOut">
                    <li><a href="javascript:;">
                        <span class="dashboard_icon pending_inventory_out"></span>
                        <h5 class="feature">Pending Inventory Out</h5>
                        <span>Summary of pending inventory out</span></a></li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInFromInventory">
                    <li><a href="javascript:;">
                        <span class="dashboard_icon pending_in_from_inventory"></span>
                        <h5 class="feature">Pending In From Inventory</h5>
                        <span>Summary of pending in from inventory</span></a></li>
                </app:ifAnyUrl>
                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>Your favourite content here</span></a>
                </li>
            </ul>
            <ul id="output" style="height: 100%">
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedConsumption">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedConsumption" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInFromSupplier">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedInFromSupplier" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInventoryOut">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedInventoryOut" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <app:ifAnyUrl urls="/invInventoryTransaction/listOfUnApprovedInFromInventory">
                    <li style="height: 100%">
                        <div class="dashboardContainer">
                            <table id="flexUnapprovedInFromInventory" style="display:none"></table>
                        </div>
                    </li>
                </app:ifAnyUrl>
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/>
                </li>
            </ul>
        </div>
    </div>
</div>
