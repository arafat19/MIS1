<app:ifAnyUrl urls="/exhCustomer/show,/exhCustomer/showForAgent,/exhBeneficiary/show,/exhBeneficiary/showNewForCustomer,/exhBeneficiary/showApprovedForCustomer,
/exhTask/showForCustomer,/exhTask/showForAgent,/exhTask/showExhTaskForCashier,/exhTask/showAgentTaskForCashier,
/exhTask/showCustomerTaskForCashier,/exhTask/showExhTaskForAdmin,/exhTask/showAgentTaskForAdmin,/exhTask/showCustomerTaskForAdmin,/exhTask/showRemittanceForCustomer,
/exhCustomer/showForAdmin,/exhCustomer/showForCustomerByNameAndCode,/exhTask/showForOtherBankUser">

	<a href="#"><span><i class="tap-pre disbursement-tab"></i></span><span class="tabText">Disbursement</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/exhCustomer/show">
                <li>
                    <a class='autoload' href="#exhCustomer/show">
                        <span><i class="pre-icon customer"></i></span>
                        <span class="menuText">Customer</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/exhCustomer/showForAgent">
                <li>
                    <a class='autoload' href="#exhCustomer/showForAgent">
                        <span><i class="pre-icon show-customer"></i></span>
                        <span class="menuText">Customer</span>
                    </a>
                </li>
            </sec:access>

            <app:ifAnyUrl urls="/exhBeneficiary/showNewForCustomer,/exhBeneficiary/showApprovedForCustomer">
                <li>&nbsp;</li>
                <li>
                    <span><i class="pre-icon beneficiary"></i></span>
                    <span class="menuText" style="padding-left:36px">Beneficiary</span>
                    <ul class="menuDivSub">
                        <sec:access url="/exhBeneficiary/showNewForCustomer">
                            <li><a class='autoload' href="#exhBeneficiary/showNewForCustomer"><span><i
                                    class="pre-icon-sub role"></i></span> <span class="menuTextSub">Create New</span>
                            </a>
                            </li>
                        </sec:access>
                        <sec:access url="/exhBeneficiary/showApprovedForCustomer">
                            <li><a class='autoload' href="#exhBeneficiary/showApprovedForCustomer"><span><i
                                    class="pre-icon-sub role-right"></i></span> <span
                                    class="menuTextSub">Approved</span></a>
                            </li>
                        </sec:access>

                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <sec:access url="/exhTask/showForCustomer">
                <li><a class='autoload' href="#exhTask/showForCustomer"><span><i
                        class="pre-icon pay-cash"></i></span> <span class="menuText">Send Remittance</span></a></li>
            </sec:access>

            <app:ifAnyUrl
                    urls="/exhTask/showUnApprovedTaskForCustomer,/exhTask/showApprovedTaskForCustomer,/exhTask/showDisbursedTaskForCustomer">
                <li>&nbsp;</li>
                <li>
                    <span><i class="pre-icon accountin_voucher"></i></span>
                    <span class="menuText" style="padding-left:36px">My Remittance History</span>
                    <ul class="menuDivSub">
                        <sec:access url="/exhTask/showUnApprovedTaskForCustomer">
                            <li><a class='autoload' href="#exhTask/showUnApprovedTaskForCustomer"><span><i
                                    class="pre-icon-sub process-task"></i></span> <span
                                    class="menuTextSub">Un Approved</span></a>
                            </li>
                        </sec:access>

                        <sec:access url="/exhTask/showApprovedTaskForCustomer">
                            <li><a class='autoload' href="#exhTask/showApprovedTaskForCustomer"><span><i
                                    class="pre-icon-sub unposted_cash_receive"></i></span> <span
                                    class="menuTextSub">Approved</span></a>
                            </li>
                        </sec:access>
                        <sec:access url="/exhTask/showDisbursedTaskForCustomer">
                            <li><a class='autoload' href="#exhTask/showDisbursedTaskForCustomer"><span><i
                                    class="pre-icon-sub bank-reconciliation"></i></span> <span
                                    class="menuTextSub">Disbursed</span></a>
                            </li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl
                    urls="/exhTask/showExhTaskForCashier,/exhTask/showAgentTaskForCashier,/exhTask/showCustomerTaskForCashier">
                <li>&nbsp;</li>
                <li>
                    <span><i class="pre-icon accountin_voucher"></i></span>
                    <span class="menuText" style="padding-left:36px">Task</span>
                    <ul class="menuDivSub">
                        <sec:access url="/exhTask/showExhTaskForCashier">
                            <li><a class='autoload' href="#exhTask/showExhTaskForCashier">
                                <span><i class="pre-icon-sub process-task"></i></span>
                                <span class="menuTextSub">Exchange House</span></a></li>
                        </sec:access>
                        <sec:access url="/exhTask/showAgentTaskForCashier">
                            <li><a class='autoload' href="#exhTask/showAgentTaskForCashier"><span>
                                <i class="pre-icon-sub designation"></i></span>
                                <span class="menuTextSub">Agent</span></a></li>
                        </sec:access>
                        <sec:access url="/exhTask/showCustomerTaskForCashier">
                            <li>
                                <a class='autoload' href="#exhTask/showCustomerTaskForCashier">
                                    <span><i class="pre-icon-sub show-customer-remittance"></i></span>
                                    <span class="menuTextSub">Customer</span>
                                </a>
                            </li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>
            <app:ifAnyUrl
                    urls="/exhTask/showExhTaskForAdmin,/exhTask/showAgentTaskForAdmin,/exhTask/showCustomerTaskForAdmin">
                <li>
                    <span><i class="pre-icon accountin_voucher"></i></span>
                    <span class="menuText" style="padding-left:36px">Process Task</span>
                    <ul class="menuDivSub">
                        <sec:access url="${createLink(controller: 'exhTask', action: 'showExhTaskForAdmin')}">
                            <li><a class='autoload' href="#exhTask/showExhTaskForAdmin">
                                <span><i class="pre-icon-sub process-task"></i></span>
                                <span class="menuTextSub">Exchange House</span></a>
                            </li>
                        </sec:access>

                        <sec:access url="/exhTask/showAgentTaskForAdmin">
                            <li><a class='autoload' href="#exhTask/showAgentTaskForAdmin">
                                <span><i class="pre-icon-sub designation"></i></span>
                                <span class="menuTextSub">Agent</span></a>
                            </li>
                        </sec:access>
                        <sec:access url="/exhTask/showCustomerTaskForAdmin">
                            <li><a class='autoload' href="#exhTask/showCustomerTaskForAdmin"><span>
                                <i class="pre-icon-sub show-customer-remittance"></i></span>
                                <span class="menuTextSub">Customer</span></a>
                            </li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <sec:access url="/exhTask/showForOtherBankUser">
                <li><a class='autoload' href="#exhTask/showForOtherBankUser"><span><i
                        class="pre-icon process-task"></i></span> <span class="menuText">Other Bank Task</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhTask/showForAgent">
                <li>
                    <a class='autoload' href="#exhTask/showForAgent">
                        <span><i class="pre-icon process-task"></i></span>
                        <span class="menuText">Task</span>
                    </a>
                </li>
            </sec:access>
            <sec:access url="/exhCustomer/showForAdmin">
                <li><a class='autoload' href="#exhCustomer/showForAdmin"><span><i
                        class="pre-icon show-customer"></i></span> <span class="menuText">Show Customer</span></a>
                </li>
            </sec:access>
            <sec:access url="/exhCustomer/showForCustomerByNameAndCode">
                <li><a class='autoload' href="#exhCustomer/showForCustomerByNameAndCode"><span><i
                        class="pre-icon show-customer"></i></span> <span class="menuText">Search Customer</span></a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/exhReport/showInvoice,/exhReport/showCustomerHistory,/exhReport/showRemittanceSummary,
/exhReport/showCashierWiseReportForCashier,/exhTask/showForTaskSearch,/exhTask/showForTaskSearchForAgent,/exhReport/showSummaryReportForAdmin,
/exhReport/showAgentWiseCommissionForAdmin,/exhReport/showAgentWiseCommissionForAgent,/exhReport/showRemittanceTransaction,/exhReport/showInvoiceForCustomer,/exhReport/downloadCustomerHistory,/exhReport/showTransactionSummary">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>


    <div id='1'>
        <ul class="menuDiv">
            <sec:access url="/exhReport/showInvoice">
                <li><a class='autoload' href="#exhReport/showInvoice"><span><i
                        class="pre-icon show-invoice"></i></span> <span class="menuText">Show Invoice</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhReport/showInvoiceForCustomer">
                <li><a class='autoload' href="#exhReport/showInvoiceForCustomer"><span><i
                        class="pre-icon show-invoice"></i></span> <span class="menuText">Show Invoice</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhReport/showCustomerHistory">
                <li><a class='autoload' href="#/exhReport/showCustomerHistory"><span><i
                        class="pre-icon show-customer-remittance"></i></span> <span
                        class="menuText">Customer History</span>
                </a></li>
            </sec:access>

            <sec:access url="/exhReport/showTransactionSummary">
                <li><a class='autoload' href="#exhReport/showTransactionSummary"><span><i
                        class="pre-icon bank-statement"></i></span> <span
                        class="menuText">Transaction Summary</span>
                </a></li>
            </sec:access>

            <sec:access url="/exhReport/showRemittanceSummary">
                <li><a class='autoload' href="#exhReport/showRemittanceSummary"><span><i
                        class="pre-icon daily-remittance-summery"></i></span> <span
                        class="menuText">Daily Remittance Summary</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhReport/showCashierWiseReportForCashier">
                <li><a class='autoload'
                       href="#exhReport/showCashierWiseReportForCashier"><span><i
                            class="pre-icon cashier-wise-task-report"></i></span> <span
                            class="menuText">Cashier Wise Task Report</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhReport/showCashierWiseReportForAdmin">
                <li><a class='autoload'
                       href="#exhReport/showCashierWiseReportForAdmin"><span><i
                            class="pre-icon cashier-wise-task-report"></i></span> <span
                            class="menuText">Cashier Wise Task Report</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhReport/showSummaryReportForAdmin">
                <li><a class='autoload' href="#exhReport/showSummaryReportForAdmin"><span><i
                        class="pre-icon daily-remittance-summery"></i></span> <span
                        class="menuText">Remittance Summary</span>
                </a></li>
            </sec:access>

            <sec:access url="/exhReport/showAgentWiseCommissionForAdmin">
                <li><a class='autoload' href="#exhReport/showAgentWiseCommissionForAdmin"><span><i
                        class="pre-icon daily-remittance-summery"></i></span> <span
                        class="menuText">Agent Wise Commission</span>
                </a></li>
            </sec:access>

            <sec:access url="/exhReport/showAgentWiseCommissionForAgent">
                <li><a class='autoload' href="#exhReport/showAgentWiseCommissionForAgent"><span><i
                        class="pre-icon daily-remittance-summery"></i></span> <span
                        class="menuText">Commission</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhReport/showRemittanceTransaction">
                <li><a class='autoload'
                       href="#exhReport/showRemittanceTransaction"><span><i
                            class="pre-icon store_summary"></i></span> <span
                            class="menuText">Remittance Transaction</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhTask/showForTaskSearch">
                <li><a class='autoload'
                       href="#exhTask/showForTaskSearch"><span><i
                            class="pre-icon project-status"></i></span> <span
                            class="menuText">Task Status</span>
                </a></li>
            </sec:access>
            <sec:access url="/exhTask/showForTaskSearchForAgent">
                <li><a class='autoload'
                       href="#exhTask/showForTaskSearchForAgent"><span><i
                            class="pre-icon project-status"></i></span> <span
                            class="menuText">Task Status</span>
                </a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl urls="/exhAgent/show,/exhAgentCurrencyPosting/show">
    <a href="#">
        <span><i class="tap-pre agent-tab"></i></span>
        <span class="tabText">Agent</span>
    </a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/exhAgent/show">
                <li><a class='autoload' href="#exhAgent/show"><span><i
                        class="pre-icon bank-branch"></i></span> <span class="menuText">Agent</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhAgentCurrencyPosting/show">
                <li><a class='autoload' href="#exhAgentCurrencyPosting/show"><span><i
                        class="pre-icon currency-conversion"></i></span> <span
                        class="menuText">Agent Currency Posting</span>
                </a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

%{--Development Tab for Super Admin--}%
<app:ifAnyUrl urls="/exhRegularFee/show,/systemConfiguration/show,/sms/showSms,/appMail/show">
    <a href="#">
        <span><i class="tap-pre development-tab"></i></span>
        <span class="tabText">Development</span>
    </a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/exhRegularFee/show">
                <li><a class='autoload' href="#exhRegularFee/show"><span><i
                        class="pre-icon upload-sanction"></i></span> <span class="menuText">Regular Fee</span></a>
                </li>
            </sec:access>

            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=9"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=9"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=9"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>


<app:ifAnyUrl urls="/exhCustomer/showCustomerUser,
/exhCurrencyConversion/show,/exhRemittancePurpose/show,/exhPhotoIdType/show,/exhSanction/show,/exhSanction/showSanctionUpload">

    <a href="#">
        <span><i class="tap-pre settings-tab"></i></span>
        <span class="tabText">Settings</span>
    </a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/exhCustomer/showCustomerUser">
                <li><a class='autoload' href="#exhCustomer/showCustomerUser"><span><i
                        class="pre-icon customer-user-account"></i></span> <span
                        class="menuText">Create Customer Account</span>
                </a></li>
            </sec:access>

            <sec:access url="/exhCurrencyConversion/show">
                <li><a class='autoload' href="#exhCurrencyConversion/show"><span><i
                        class="pre-icon currency-conversion"></i></span> <span
                        class="menuText">Currency Conversion</span>
                </a>
                </li>
            </sec:access>

            <sec:access url="/exhRemittancePurpose/show">
                <li><a class='autoload' href="#exhRemittancePurpose/show"><span><i
                        class="pre-icon remittancePurpose"></i></span> <span class="menuText">Remittance Purpose</span>
                </a>
                </li>
            </sec:access>
            <sec:access url="/exhPhotoIdType/show">
                <li><a class='autoload' href="#exhPhotoIdType/show"><span><i
                        class="pre-icon photoIdType"></i></span> <span class="menuText">Photo ID Type</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhSanction/show">
                <li><a class='autoload' href="#exhSanction/show"><span><i
                        class="pre-icon sanction-info"></i></span> <span class="menuText">Sanction Info</span></a>
                </li>
            </sec:access>
            <sec:access url="/exhSanction/showSanctionUpload">
                <li><a class='autoload' href="#exhSanction/showSanctionUpload"><span><i
                        class="pre-icon upload-sanction"></i></span> <span class="menuText">Upload Sanction</span></a>
                </li>
            </sec:access>

            <sec:access url="/exhPostalCode/show">
                <li><a class='autoload' href="#exhPostalCode/show"><span><i
                        class="pre-icon photoIdType"></i></span> <span class="menuText">Postal Code</span></a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style="height:60px; display: none;">
</div>
