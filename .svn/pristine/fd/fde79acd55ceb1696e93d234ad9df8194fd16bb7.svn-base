<app:ifAnyUrl urls="/invInventoryTransaction/showInventoryInFromSupplier,
                  /invInventoryTransaction/showInventoryInFromInventory,
                  /invInventoryTransaction/showInventoryOut,
                  /invInventoryTransaction/showInvProductionWithConsumption,
                  /invInventoryTransaction/showApprovedProdWithConsump,
                  /invInventoryTransaction/showInventoryConsumption">
    <a href="#"><span><i class="tap-pre inventory-tab"></i></span><span class="tabText">Inventory</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <app:ifAnyUrl
                    urls="/invInventoryTransaction/showInventoryInFromSupplier,/invInventoryTransaction/showInventoryInFromInventory">
                <li><span><i class="pre-icon inventory-sotore-report"></i></span>
                    <span class="menuText" style="padding-left:36px">In</span>
                    <ul class="menuDivSub">
                        <app:ifPlugin name="Procurement">
                            <sec:access url="/invInventoryTransaction/showInventoryInFromSupplier">
                                <li><a class='autoload'
                                       href="#invInventoryTransaction/showInventoryInFromSupplier"><span><i
                                            class="pre-icon-sub supplier_in"></i></span> <span
                                            class="menuTextSub">From Supplier</span></a></li>
                            </sec:access>
                        </app:ifPlugin>

                        <sec:access url="/invInventoryTransaction/showInventoryInFromInventory">
                            <li><a class='autoload'
                                   href="#invInventoryTransaction/showInventoryInFromInventory"><span><i
                                        class="pre-icon-sub pre-icon site_in"></i></span> <span
                                        class="menuTextSub">From Inventory</span></a></li>
                        </sec:access>
                    </ul>
                </li>

                <div>&nbsp;</div>
            </app:ifAnyUrl>

            <app:ifAnyUrl
                    urls="/invInventoryTransaction/showInvProductionWithConsumption,/invInventoryTransaction/showApprovedProdWithConsump">
                <li><span><i class="pre-icon production"></i></span>
                    <span class="menuText" style="padding-left:36px">Production</span>
                    <ul class="menuDivSub">
                        <sec:access url="/invInventoryTransaction/showInvProductionWithConsumption">
                            <li><a class='autoload'
                                   href="#invInventoryTransaction/showInvProductionWithConsumption"><span><i
                                        class="pre-icon-sub production_new"></i></span> <span
                                        class="menuTextSub">New</span></a></li>
                        </sec:access>
                        <sec:access url="/invInventoryTransaction/showApprovedProdWithConsump">
                            <li><a class='autoload'
                                   href="#invInventoryTransaction/showApprovedProdWithConsump"><span><i
                                        class="pre-icon-sub production_approved"></i></span> <span
                                        class="menuTextSub">Approved</span></a></li>
                        </sec:access>
                    </ul>
                </li>

                <div>&nbsp;</div>
            </app:ifAnyUrl>

            <sec:access url="/invInventoryTransaction/showInventoryOut">
                <li>
                    <a class='autoload' href="#invInventoryTransaction/showInventoryOut">
                        <span><i class="pre-icon store_out"></i></span>
                        <span class="menuText">Out</span>
                    </a>
                </li>
            </sec:access>

            <app:ifPlugin name="Budget">
                <sec:access url="/invInventoryTransaction/showInventoryConsumption">
                    <li>
                        <a class='autoload' href="#invInventoryTransaction/showInventoryConsumption">
                            <span><i class="pre-icon site_consumption"></i></span>
                            <span class="menuText">Consumption</span>
                        </a>
                    </li>
                </sec:access>
            </app:ifPlugin>

        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl urls="/invReport/showInvoice,/invReport/showSupplierChalan,/invReport/inventoryStock,
                  /invReport/showInventoryStatusWithValue,/invReport/showInventoryStatusWithQuantity,/invReport/showInventoryStatusWithQuantityAndValue,
                  /invReport/showForItemReconciliation,/invReport/showInventorySummary,/invReport/showInventoryTransactionList,
                  /invReport/showItemStock,/invReport/showInventoryValuation,/invReport/showConsumedItemList,
                  /invReport/showItemReceivedStock,/invReport/showItemWiseBudgetSummary,/invReport/showInventoryProductionRpt,
                  /invReport/showPoItemReceived">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/invReport/showInvoice">
                <li>
                    <a class='autoload' href="#invReport/showInvoice">
                        <span><i class="pre-icon inventory-chalan-report"></i></span>
                        <span class="menuText">Chalan</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invReport/showSupplierChalan">
                <li>
                    <a class='autoload' href="#invReport/showSupplierChalan">
                        <span><i class="pre-icon inventory-supplier-chalan"></i></span>
                        <span class="menuText">Supplier Chalan</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invReport/inventoryStock">
                <li>
                    <a class='autoload' href="#invReport/inventoryStock">
                        <span><i class="pre-icon inventory-stock-report"></i></span>
                        <span class="menuText">Stock</span>
                    </a>
                </li>
            </sec:access>
            <app:ifAnyUrl urls="/invReport/showInventoryStatusWithValue,
    /invReport/showInventoryStatusWithQuantity,
    /invReport/showInventoryStatusWithQuantityAndValue">
                <div>&nbsp;</div>
                <li><span><i class="pre-icon inventory-status-report"></i></span>
                    <span class="menuText" style="padding-left:36px">Inventory Status</span>
                    <ul class="menuDivSub">
                        <sec:access url="/invReport/showInventoryStatusWithValue">
                            <li><a class='autoload'
                                   href="#invReport/showInventoryStatusWithValue"><span><i
                                        class="pre-icon-sub inventory-status-with-value-report"></i></span> <span
                                        class="menuTextSub">Value</span></a></li>
                        </sec:access>

                        <sec:access url="/invReport/showInventoryStatusWithQuantity">
                            <li><a class='autoload'
                                   href="#invReport/showInventoryStatusWithQuantity"><span><i
                                        class="pre-icon-sub inventory-status-with-quantity-report"></i></span> <span
                                        class="menuTextSub">Quantity</span></a></li>
                        </sec:access>

                        <sec:access url="/invReport/showInventoryStatusWithQuantityAndValue">
                            <li><a class='autoload'
                                   href="#invReport/showInventoryStatusWithQuantityAndValue"><span><i
                                        class="pre-icon-sub inventory-status-value-quentity-report"></i></span> <span
                                        class="menuTextSub">Value And Quantity</span></a></li>
                        </sec:access>

                        <sec:access url="/invReport/showForItemReconciliation">
                            <li><a class='autoload'
                                   href="#invReport/showForItemReconciliation"><span><i
                                        class="pre-icon-sub system-entity-type"></i></span> <span
                                        class="menuTextSub">Item Reconciliation</span></a></li>
                        </sec:access>
                    </ul>
                </li>

                <div>&nbsp;</div>
            </app:ifAnyUrl>
            <sec:access url="/invReport/showInventorySummary">
                <li>
                    <a class='autoload' href="#invReport/showInventorySummary">
                        <span><i class="pre-icon inventory-stock-summary-report"></i></span>
                        <span class="menuText">Summary</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invReport/showInventoryTransactionList">
                <li>
                    <a class='autoload' href="#invReport/showInventoryTransactionList">
                        <span><i class="pre-icon inventory-transaction-report"></i></span>
                        <span class="menuText">Transaction</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invReport/showInventoryValuation">
                <li>
                    <a class='autoload' href="#invReport/showInventoryValuation">
                        <span><i class="pre-icon report_valuation"></i></span>
                        <span class="menuText">Valuation</span>
                    </a>
                </li>
            </sec:access>

            <app:ifPlugin name="Budget">
                <sec:access url="/invReport/showConsumedItemList">
                    <li><a class='autoload' href="#invReport/showConsumedItemList"><span><i
                            class="pre-icon inventory-site-transaction-report"></i></span> <span
                            class="menuText">Consumption</span></a>
                    </li>
                </sec:access>
            </app:ifPlugin>

            <sec:access url="/invReport/showItemStock">
                <li>
                    <a class='autoload' href="#invReport/showItemStock">
                        <span><i class="pre-icon inventory-material-stock-report"></i></span>
                        <span class="menuText" style="padding-left:36px">Item Stock</span>
                    </a>
                </li>
            </sec:access>

            <sec:access url="/invReport/showItemReceivedStock">
                <li>
                    <a class='autoload' href="#invReport/showItemReceivedStock">
                        <span><i class="pre-icon supplier_in"></i></span>
                        <span class="menuText" style="padding-left:36px">Item Received</span>
                    </a>
                </li>
            </sec:access>

            <app:ifAllPlugins names="Budget,Procurement">
                <sec:access url="/invReport/showItemWiseBudgetSummary">
                    <li>
                        <a class='autoload' href="#invReport/showItemWiseBudgetSummary">
                            <span><i class="pre-icon financial"></i></span>
                            <span class="menuText" style="padding-left:36px">Item Wise Budget Summary</span>
                        </a>
                    </li>
                </sec:access>
            </app:ifAllPlugins>

            <sec:access url="/invReport/showInventoryProductionRpt">
                <li>
                    <a class='autoload' href="#invReport/showInventoryProductionRpt">
                        <span><i class="pre-icon production"></i></span>
                        <span class="menuText" style="padding-left:36px">Production</span>
                    </a>
                </li>
            </sec:access>

            <sec:access url="/invReport/showPoItemReceived">
                <li>
                    <a class='autoload' href="#invReport/showPoItemReceived">
                        <span><i class="pre-icon po_item_received"></i></span>
                        <span class="menuText" style="padding-left:36px">PO Item Received</span>
                    </a>
                </li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl urls="/invInventoryTransaction/showReCalculateValuation,/systemConfiguration/show,/appMail/show,/sms/showSms">
    <a href="#">
        <span>
            <i class="tap-pre development-tab"></i>
        </span>
        <span class="tabText">Development</span>
    </a>

    <div id='4'>
        <ul class="menuDiv">
            <sec:access url="/invInventoryTransaction/showReCalculateValuation">
                <li>
                    <a class='autoload' href="#invInventoryTransaction/showReCalculateValuation">
                        <span><i class="pre-icon recalculate-all-valuation"></i></span>
                        <span class="menuText">Recalculate All Valuation</span>
                    </a>
                </li>
            </sec:access>

            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=4"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>
            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=4"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=4"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/invInventory/show,/invUserInventory/show,/invProductionLineItem/show,/invInventoryTransactionDetails/showInvModifyOverheadCost">
    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Setting</span></a>

    <div id='5'>
        <ul class="menuDiv">
            <sec:access url="/invInventory/show">
                <li>
                    <a class="autoload" href="#invInventory/show">
                        <span><i class="pre-icon inventory"></i></span>
                        <span class="menuText">Inventory</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invProductionLineItem/show">
                <li>
                    <a class='autoload' href="#invProductionLineItem/show">
                        <span><i class="pre-icon production_line_Item"></i></span>
                        <span class="menuText">Production Line Item</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/invInventoryTransactionDetails/showInvModifyOverheadCost">
                <li>
                    <a class='autoload' href="#invInventoryTransactionDetails/showInvModifyOverheadCost">
                        <span><i class="pre-icon prod-modify-overhead-cost"></i></span>
                        <span class="menuText">Modify Prod. Overhead Cost</span>
                    </a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



