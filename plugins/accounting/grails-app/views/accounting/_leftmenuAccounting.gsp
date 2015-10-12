<app:ifAnyUrl urls="/accVoucher/show,/accVoucher/showPayCash,/accIouSlip/show,/accVoucher/showPayBank,/accVoucher/showReceiveCash,
                  /accVoucher/showReceiveBank">
    <a href="#"><span><i class="tap-pre accounting-tab"></i></span><span class="tabText">Accounting</span></a>

    <div id='0'>

        <ul class="menuDiv">
            <app:ifPlugin name="Procurement">
                <app:ifAnyUrl urls="/accVoucher/show,/accVoucher/showPayCash">
                    <li><span><i class="pre-icon accountin_voucher"></i></span>
                        <span class="menuText" style="padding-left:36px">Voucher</span>

                        <ul class="menuDivSub">
                            <sec:access url="/accVoucher/showPayCash">
                                <li><a class='autoload' href="#accVoucher/showPayCash"><span><i
                                        class="pre-icon-sub pay-cash"></i></span> <span
                                        class="menuTextSub">Pay Cash</span></a></li>
                            </sec:access>

                            <sec:access url="/accVoucher/showPayBank">
                                <li><a class='autoload' href="#accVoucher/showPayBank"><span><i
                                        class="pre-icon-sub pay-cheque"></i></span> <span
                                        class="menuTextSub">Pay Cheque</span></a></li>
                            </sec:access>

                            <sec:access url="/accVoucher/showReceiveCash">
                                <li><a class='autoload' href="#accVoucher/showReceiveCash"><span><i
                                        class="pre-icon-sub receive-cash"></i></span> <span
                                        class="menuTextSub">Receive Cash</span></a></li>
                            </sec:access>
                            <sec:access url="/accVoucher/showReceiveBank">
                                <li><a class='autoload' href="#accVoucher/showReceiveBank"><span><i
                                        class="pre-icon-sub receive-cheque"></i></span> <span
                                        class="menuTextSub">Receive Cheque</span></a></li>
                            </sec:access>
                            <sec:access url="/accVoucher/show">
                                <li><a class='autoload' href="#accVoucher/show"><span><i
                                        class="pre-icon-sub pre-icon material"></i></span> <span
                                        class="menuTextSub">Journal</span></a></li>
                            </sec:access>
                            <li>&nbsp;</li>
                        </ul>
                    </li>
                </app:ifAnyUrl>


                <app:ifAnyUrl urls="/accCancelledVoucher/showCancelledVoucher,/accIouSlip/show">
                    <ul class="menuDivSub">
                        <sec:access url="/accCancelledVoucher/showCancelledVoucher">
                            <li><a class='autoload' href="#accCancelledVoucher/showCancelledVoucher"><span><i
                                    class="pre-icon voucher-list"></i></span> <span
                                    class="menuText">Cancelled Voucher</span></a></li>
                        </sec:access>
                        <sec:access url="/accIouSlip/show">
                            <li><a class='autoload' href="#accIouSlip/show"><span><i
                                    class="pre-icon acc-iou-slip"></i></span> <span
                                    class="menuText">IOU Slip</span></a></li>

                        </sec:access>
                    </ul>
                </app:ifAnyUrl>

            </app:ifPlugin>
        </ul>

    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/accReport/showVoucher,/accReport/showLedger,/accReport/showVoucherList,/accReport/showForGroupLedgerRpt,
        /accReport/showSourceLedger,/accReport/showSupplierWisePayable,/accReport/showSupplierWisePayment,
        /accReport/showProjectWiseExpense,/accReport/showSourceWiseBalance,/accReport/showAccIouSlipRpt,
        /accReport/showCustomGroupBalance,/accReport/showBankReconciliationCheque">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <sec:access url="/accReport/showVoucher">
                <li><a class='autoload' href="#accReport/showVoucher"><span><i
                        class="pre-icon voucher"></i></span> <span
                        class="menuText">Voucher</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showLedger">
                <li><a class='autoload' href="#accReport/showLedger"><span><i
                        class="pre-icon ledger"></i></span> <span
                        class="menuText">Ledger</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showVoucherList">
                <li><a class='autoload' href="#accReport/showVoucherList"><span><i
                        class="pre-icon voucher-list"></i></span> <span
                        class="menuText">Voucher List</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showForGroupLedgerRpt">
                <li><a class='autoload' href="#accReport/showForGroupLedgerRpt"><span><i
                        class="pre-icon group-ledger"></i></span> <span
                        class="menuText">Group Ledger</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showSourceLedger">
                <li><a class='autoload' href="#accReport/showSourceLedger"><span><i
                        class="pre-icon source-ledger"></i></span> <span
                        class="menuText">Source Ledger</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showSourceWiseBalance">
                <li><a class='autoload' href="#accReport/showSourceWiseBalance"><span><i
                        class="pre-icon source-balance"></i></span> <span
                        class="menuText">Source Wise Balance</span></a></li>
                <li>&nbsp;</li>
            </sec:access>

            <app:ifAllPlugins names="Procurement,Inventory,FixedAsset">
                <app:ifAnyUrl urls="/accReport/showSupplierWisePayment,/accReport/showSupplierWisePayable">
                    <li><span><i class="pre-icon supplier"></i></span> <span class="menuText"
                                                                             style="padding-left:36px">Supplier</span>
                        <ul class="menuDivSub">
                            <app:ifAllPlugins names="Procurement,Inventory,FixedAsset">
                                <sec:access url="/accReport/showSupplierWisePayable">
                                    <li><a class='autoload' href="#accReport/showSupplierWisePayable"><span><i
                                            class="pre-icon-sub pre-icon payable"></i></span> <span
                                            class="menuTextSub">Payable</span></a></li>
                                </sec:access>
                            </app:ifAllPlugins>

                            <app:ifPlugin name="Procurement">
                                <sec:access url="/accReport/showSupplierWisePayment">
                                    <li><a class='autoload' href="#accReport/showSupplierWisePayment"><span><i
                                            class="pre-icon-sub pre-icon  paid"></i></span> <span
                                            class="menuTextSub">Paid</span></a></li>
                                </sec:access>
                            </app:ifPlugin>
                        </ul>
                    </li>
                    <li>&nbsp;</li>
                </app:ifAnyUrl>
            </app:ifAllPlugins>

            <sec:access url="/accReport/showProjectFundFlowReport">
                <li><a class='autoload' href="#accReport/showProjectFundFlowReport"><span><i
                        class="pre-icon voucher-list"></i></span> <span
                        class="menuText">Project Fund Flow</span></a></li>
            </sec:access>
            <sec:access url="/accReport/showProjectWiseExpense">
                <li><a class='autoload' href="#accReport/showProjectWiseExpense"><span><i
                        class="pre-icon project_wise_exp"></i></span> <span
                        class="menuText">Project Wise Expense</span></a></li>
            </sec:access>

            <sec:access url="/accReport/showAccIouSlipRpt">
                <li><a class='autoload' href="#accReport/showAccIouSlipRpt"><span><i
                        class="pre-icon acc-iou-slip"></i></span> <span
                        class="menuText">IOU Slip</span></a></li>
                <li>&nbsp;</li>
            </sec:access>

            <app:ifAnyUrl
                    urls="/accReport/showCustomGroupBalance">
                <li><span><i class="pre-icon financial"></i></span> <span class="menuText"
                                                                          style="padding-left:36px">Financial</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accReport/showCustomGroupBalance">
                            <li><a class='autoload' href="#accReport/showCustomGroupBalance"><span><i
                                    class="pre-icon-sub pre-icon custom-group-balance"></i></span> <span
                                    class="menuTextSub">Custom Group Balance</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/accReport/showBankReconciliationCheque">
                <li><span><i class="pre-icon bank-reconciliation"></i></span> <span class="menuText"
                                                                                    style="padding-left:36px">Bank Reconciliation</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accReport/showBankReconciliationCheque">
                            <li><a class='autoload' href="#accReport/showBankReconciliationCheque"><span><i
                                    class="pre-icon-sub pre-icon cheque"></i></span> <span
                                    class="menuTextSub">Cheque</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/accReport/showTrialBalanceOfLevel2,/accReport/showTrialBalanceOfLevel3,
                  /accReport/showTrialBalanceOfLevel4,/accReport/showTrialBalanceOfLevel5,
                  /accReport/showFinancialStatementOfLevel2,/accReport/showFinancialStatementOfLevel3,
                  /accReport/showFinancialStatementOfLevel4,/accReport/showFinancialStatementOfLevel5,
                  /accReport/showIncomeStatementOfLevel4,/accReport/showIncomeStatementOfLevel5">
    <a href="#"><span><i class="tap-pre budget-tab"></i></span><span class="tabText">Financial Report</span></a>

    <div id='2'>
        <ul class="menuDiv">

            <app:ifAnyUrl
                    urls="/accReport/showTrialBalanceOfLevel2,/accReport/showTrialBalanceOfLevel3,
                          /accReport/showTrialBalanceOfLevel4,/accReport/showTrialBalanceOfLevel5">
                <li><span><i class="pre-icon chart-of-account"></i></span>
                    <span class="menuText" style="padding-left:36px">Trial Balance</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accReport/showTrialBalanceOfLevel2">
                            <li><a class='autoload' href="#accReport/showTrialBalanceOfLevel2"><span><i
                                    class="pre-icon-sub pre-icon h-22"></i></span> <span
                                    class="menuTextSub">Hierarchy 2</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showTrialBalanceOfLevel3">
                            <li><a class='autoload' href="#accReport/showTrialBalanceOfLevel3"><span><i
                                    class="pre-icon-sub pre-icon h-33"></i></span> <span
                                    class="menuTextSub">Hierarchy 3</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showTrialBalanceOfLevel4">
                            <li><a class='autoload' href="#accReport/showTrialBalanceOfLevel4"><span><i
                                    class="pre-icon-sub pre-icon h-44"></i></span> <span
                                    class="menuTextSub">Hierarchy 4</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showTrialBalanceOfLevel5">
                            <li><a class='autoload' href="#accReport/showTrialBalanceOfLevel5"><span><i
                                    class="pre-icon-sub pre-icon h-55"></i></span> <span
                                    class="menuTextSub">Hierarchy 5</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl
                    urls="/accReport/showIncomeStatementOfLevel4,/accReport/showIncomeStatementOfLevel5">
                <li><span><i class="pre-icon voucher-list"></i></span>
                    <span class="menuText" style="padding-left:36px">Income Statement</span>
                    <ul class="menuDivSub">

                        <sec:access url="/accReport/showIncomeStatementOfLevel4">
                            <li><a class='autoload' href="#accReport/showIncomeStatementOfLevel4"><span><i
                                    class="pre-icon-sub pre-icon h-4444"></i></span> <span
                                    class="menuTextSub">Hierarchy 4</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showIncomeStatementOfLevel5">
                            <li><a class='autoload' href="#accReport/showIncomeStatementOfLevel5"><span><i
                                    class="pre-icon-sub pre-icon h-5555"></i></span> <span
                                    class="menuTextSub">Hierarchy 5</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl
                    urls="/accReport/showFinancialStatementOfLevel2,/accReport/showFinancialStatementOfLevel3,
                          /accReport/showFinancialStatementOfLevel4,/accReport/showFinancialStatementOfLevel5">
                <li><span><i class="pre-icon financial"></i></span>
                    <span class="menuText" style="padding-left:36px">Financial Statement</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accReport/showFinancialStatementOfLevel2">
                            <li><a class='autoload' href="#accReport/showFinancialStatementOfLevel2"><span><i
                                    class="pre-icon-sub pre-icon h-222"></i></span> <span
                                    class="menuTextSub">Hierarchy 2</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showFinancialStatementOfLevel3">
                            <li><a class='autoload' href="#accReport/showFinancialStatementOfLevel3"><span><i
                                    class="pre-icon-sub pre-icon h-333"></i></span> <span
                                    class="menuTextSub">Hierarchy 3</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showFinancialStatementOfLevel4">
                            <li><a class='autoload' href="#accReport/showFinancialStatementOfLevel4"><span><i
                                    class="pre-icon-sub pre-icon h-444"></i></span> <span
                                    class="menuTextSub">Hierarchy 4</span></a></li>
                        </sec:access>

                        <sec:access url="/accReport/showFinancialStatementOfLevel5">
                            <li><a class='autoload' href="#accReport/showFinancialStatementOfLevel5"><span><i
                                    class="pre-icon-sub pre-icon h-555"></i></span> <span
                                    class="menuTextSub">Hierarchy 5</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/accType/show,/systemConfiguration/show,/sms/showSms,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>


    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/accType/show">
                <li><a class='autoload' href="#accType/show"><span><i class="pre-icon source-balance"></i>
                </span> <span
                        class="menuText">Account Type</span></a></li>
            </sec:access>

            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=2"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=2"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=2"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/accLc/show,/accCustomGroup/show,/accChartOfAccount/show,/accGroup/show,/accTier2/show,/accTier1/show,/accTier3/show,
        /accVoucherTypeCoa/show,/accSubAccount/show,/accDivision/show,/accFinancialYear/show,/accBankStatement/show,/accIpc/show,/accLeaseAccount/show">
    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Setting</span></a>


    <div id='4'>
        <ul class="menuDiv">
            <sec:access url="/accChartOfAccount/show">
                <li><a class='autoload' href="#accChartOfAccount/show"><span><i class="pre-icon chart-of-account"></i>
                </span> <span
                        class="menuText">Chart of Account</span></a></li>
            </sec:access>
            <sec:access url="/accVoucherTypeCoa/show">
                <li><a class='autoload' href="#accVoucherTypeCoa/show"><span><i
                        class="pre-icon voucher-type-mapping"></i>
                </span> <span
                        class="menuText">Voucher Type Mapping</span></a></li>
            </sec:access>
            <sec:access url="/accSubAccount/show">
                <li><a class='autoload' href="#accSubAccount/show"><span><i class="pre-icon sub-account"></i>
                </span> <span
                        class="menuText">Sub Account</span></a></li>
                <li>&nbsp;</li>
            </sec:access>


            <sec:access url="/accGroup/show">
                <li><a class='autoload' href="#accGroup/show"><span><i class="pre-icon acc-group"></i>
                </span> <span
                        class="menuText">Group</span></a></li>
            </sec:access>

            <sec:access url="/accCustomGroup/show">
                <li><a class='autoload' href="#accCustomGroup/show"><span><i class="pre-icon custom-group"></i>
                </span> <span
                        class="menuText">Custom Group</span></a></li>
            </sec:access>
            <sec:access url="/accDivision/show">
                <li><a class='autoload' href="#accDivision/show"><span><i class="pre-icon division"></i>
                </span> <span
                        class="menuText">Division</span></a></li>
            </sec:access>
            <sec:access url="/accFinancialYear/show">
                <li><a class='autoload' href="#accFinancialYear/show"><span><i class="pre-icon financial"></i>
                </span> <span
                        class="menuText">Financial Year</span></a></li>
                <li>&nbsp;</li>
            </sec:access>
            <sec:access url="/accIpc/show">
                <li><a class='autoload' href="#accIpc/show"><span><i
                        class="pre-icon inventory-stock-summary-report"></i>
                </span> <span
                        class="menuText">I.P.C</span></a></li>
            </sec:access>
            <sec:access url="/accLc/show">
                <li><a class='autoload' href="#accLc/show"><span><i class="pre-icon group-ledger"></i>
                </span> <span
                        class="menuText">LC</span></a></li>
            </sec:access>
            <sec:access url="/accLeaseAccount/show">
                <li><a class='autoload' href="#accLeaseAccount/show"><span><i class="pre-icon income-statement"></i>
                </span> <span
                        class="menuText">Lease Account</span></a></li>
            </sec:access>

            <app:ifAnyUrl urls="/accTier1/show,/accTier2/show,/accTier3/show">
                <li>&nbsp;</li>
                <li><span><i class="pre-icon head-tier"></i></span> <span class="menuText"
                                                                          style="padding-left:36px">Tier</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accTier1/show">
                            <li><a class='autoload' href="#accTier1/show"><span><i
                                    class="pre-icon-sub pre-icon hierarchy-1"></i></span> <span
                                    class="menuTextSub">Hierarchy-1</span></a></li>
                        </sec:access>

                        <sec:access url="/accTier2/show">
                            <li><a class='autoload' href="#accTier2/show"><span><i
                                    class="pre-icon-sub pre-icon hierarchy-2"></i></span> <span
                                    class="menuTextSub">Hierarchy-2</span></a></li>
                        </sec:access>

                        <sec:access url="/accTier3/show">
                            <li><a class='autoload' href="#accTier3/show"><span><i
                                    class="pre-icon-sub pre-icon hierarchy-3"></i></span> <span
                                    class="menuTextSub">Hierarchy-3</span></a></li>
                        </sec:access>
                    </ul>
                </li>

                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/accBankStatement/show">
                <li><span><i class="pre-icon file-import"></i></span> <span class="menuText"
                                                                            style="padding-left:36px">Import</span>
                    <ul class="menuDivSub">
                        <sec:access url="/accBankStatement/show">
                            <li><a class='autoload' href="#accBankStatement/show"><span><i
                                    class="pre-icon-sub pre-icon bank-statement"></i></span> <span
                                    class="menuTextSub">Bank Statement</span></a></li>
                        </sec:access>
                    </ul>
                </li>
            </app:ifAnyUrl>
        </ul>
    </div>

</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



