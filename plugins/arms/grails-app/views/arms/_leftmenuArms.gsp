<app:ifAnyUrl urls="/rmsTask/show, /rmsTask/showForExh, /rmsTask/showForUploadTask, /rmsTask/showForUploadTaskForExh,
                    /rmsTask/showForApproveTask, /rmsTask/showForManageTask,
                    /rmsTaskList/showForManageTaskList,/rmsTaskList/show, /rmsTask/showForMapTask, /rmsTransactionDay/show ">
	<a href="#"><span><i class="tap-pre disbursement-tab"></i></span><span class="tabText">Disbursement</span></a>

	<div id='0'>
		<ul class="menuDiv">
			<app:ifAnyUrl urls="/rmsTask/show,/rmsTask/showForExh,/rmsTask/showForUploadTask,/rmsTask/showForUploadTaskForExh,
			/rmsTask/showForApproveTask,/rmsTask/showForManageTask">
                <li>
                    <span><i class="pre-icon ledger"></i></span>
                    <span class="menuText" style="padding-left:36px"><i class="pre-icon chart-of-account"></i>Task</span>
                    <ul class="menuDivSub">
                        <sec:access url="/rmsTask/show">
                            <li><a class='autoload' href="#rmsTask/show"><span><i class="pre-icon-sub process-task"></i></span>
                                <span class="menuTextSub">Create</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsTask/showForExh">
                            <li><a class='autoload' href="#rmsTask/showForExh"><span><i class="pre-icon-sub process-task"></i></span>
                                <span class="menuTextSub">Create</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsTask/showForUploadTask">
                            <li><a class='autoload' href="#rmsTask/showForUploadTask"><span><i class="pre-icon-sub upload-sanction"></i></span>
                                <span class="menuTextSub">Upload</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsTask/showForUploadTaskForExh">
                            <li><a class='autoload' href="#rmsTask/showForUploadTaskForExh"><span><i class="pre-icon-sub upload-sanction"></i></span>
                                <span class="menuTextSub">Upload</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsTask/showForApproveTask">
                            <li><a class='autoload' href="#rmsTask/showForApproveTask"><span><i class="pre-icon-sub approve"></i></span>
                                <span class="menuTextSub">Approve</span></a></li>
                        </sec:access>

                        <sec:access url="/rmsTask/showForManageTask">
                            <li><a class='autoload' href="#rmsTask/showForManageTask"><span><i class="pre-icon-sub income-statement"></i></span>
                                <span class="menuTextSub">Manage</span></a></li>
                        </sec:access>
                    </ul>
                </li>
			</app:ifAnyUrl>
            &nbsp;
            <app:ifAnyUrl urls="/rmsTaskList/show,/rmsTaskList/showForManageTaskList">
                <li>
                   <span><i class="pre-icon ledger"></i></span>
                   <span class="menuText" style="padding-left:36px"><i class="pre-icon item"></i>Task List</span>
                   <ul class="menuDivSub">
                       <sec:access url="/rmsTaskList/show">
                           <li><a class='autoload' href="#rmsTaskList/show"><span><i class="pre-icon-sub combined_qs_mgt"></i></span>
                               <span class="menuTextSub">Create</span></a></li>
                       </sec:access>
                       <sec:access url="/rmsTaskList/showForManageTaskList">
                           <li><a class='autoload' href="#rmsTaskList/showForManageTaskList"><span><i class="pre-icon-sub voucher-list"></i></span>
                               <span class="menuTextSub">Manage</span></a></li>
                       </sec:access>
                    </ul>
                </li>
            </app:ifAnyUrl>
            &nbsp;
            <sec:access url="/rmsTask/showForMapTask">
                <li><a class='autoload' href="#rmsTask/showForMapTask"><span><i class="pre-icon voucher-type-mapping"></i></span>
                    <span class="menuText">Task Instrument Mapping</span></a></li>
            </sec:access>
            <sec:access url="/rmsTransactionDay/show">
                <li><a class='autoload' href="#rmsTransactionDay/show"><span><i class="pre-icon budget"></i></span> <span
                        class="menuText">Transaction Day</span></a></li>
            </sec:access>
		</ul>
	</div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/rmsInstrument/showForIssuePo,/rmsInstrument/showForIssueEft,/rmsInstrument/showForIssueOnline,
                    /rmsTask/showDisburseCashCollection, /rmsTask/showTaskDetailsForForward, /rmsInstrument/showForForwardCashCollection,
                    /rmsInstrument/showForForwardOnline, /rmsInstrument/showForInstrumentPurchase">
    <a href="#"><span><i class="tap-pre help-tab"></i></span><span class="tabText">Instrument</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <app:ifAnyUrl urls="/rmsInstrument/showForIssuePo,/rmsInstrument/showForIssueEft,
            /rmsInstrument/showForIssueOnline">
                <li>
                    <span><i class="pre-icon ledger"></i></span>
                    <span class="menuText" style="padding-left:36px">Issue</span>
                    <ul class="menuDivSub">
                        <sec:access url="/rmsInstrument/showForIssuePo">
                            <li><a class='autoload' href="#rmsInstrument/showForIssuePo"><span><i class="pre-icon-sub consumption_against_assets"></i></span>
                                <span class="menuTextSub">Pay Order</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsInstrument/showForIssueEft">
                            <li><a class='autoload' href="#rmsInstrument/showForIssueEft"><span><i class="pre-icon-sub financial"></i></span>
                                <span class="menuTextSub">EFT</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsInstrument/showForIssueOnline">
                            <li><a class='autoload' href="#rmsInstrument/showForIssueOnline"><span><i class="pre-icon-sub paid"></i></span>
                                <span class="menuTextSub">Online</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/rmsTask/showDisburseCashCollection,/rmsTask/showTaskDetailsForForward,
            /rmsInstrument/showForForwardCashCollection">
                <li>
                    <span><i class="pre-icon voucher-list"></i></span>
                    <span class="menuText" style="padding-left:36px">Cash Collection</span>
                    <ul class="menuDivSub">
                        <sec:access url="/rmsTask/showDisburseCashCollection">
                            <li><a class='autoload' href="#rmsTask/showDisburseCashCollection"><span><i class="pre-icon-sub consumption_against_assets"></i></span>
                                <span class="menuTextSub">Pay Now</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsTask/showTaskDetailsForForward">
                            <li><a class='autoload' href="#rmsTask/showTaskDetailsForForward"><span><i class="pre-icon-sub ledger"></i></span>
                                <span class="menuTextSub">Forward</span></a></li>
                        </sec:access>
                        <sec:access url="/rmsInstrument/showForForwardCashCollection">
                            <li><a class='autoload' href="#rmsInstrument/showForForwardCashCollection"><span><i class="pre-icon-sub approve"></i></span>
                                <span class="menuTextSub">Paid</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <sec:access url="/rmsInstrument/showForForwardOnline">
                <li><a class='autoload' href="#rmsInstrument/showForForwardOnline"><span><i class="pre-icon upload-sanction"></i></span>
                    <span class="menuText">Online Transaction</span></a></li>
            </sec:access>

            <sec:access url="/rmsInstrument/showForInstrumentPurchase">
                <li><a class='autoload' href="#rmsInstrument/showForInstrumentPurchase"><span><i class="pre-icon income-statement"></i></span> <span
                        class="menuText">Instrument Purchase</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/rmsReport/showBeneficiaryDetails,/rmsTask/showTaskDetailsWithNote,/rmsTaskTrace/showRmsTaskHistory,/rmsTask/showForViewNotes">
    <a href="#"><span><i class="tap-pre fixed-asset-tab"></i></span><span class="tabText">Help Desk</span></a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/rmsReport/showBeneficiaryDetails">
                <li><a class='autoload' href="#rmsReport/showBeneficiaryDetails"><span><i class="pre-icon beneficiary"></i></span>
                    <span class="menuText">Search Beneficiary</span></a></li>
            </sec:access>
            <sec:access url="/rmsTask/showTaskDetailsWithNote">
                <li><a class='autoload' href="#rmsTask/showTaskDetailsWithNote"><span><i class="pre-icon cheque"></i></span>
                    <span class="menuText">Task Details</span></a></li>
            </sec:access>
            <sec:access url="/rmsTaskTrace/showRmsTaskHistory">
                <li><a class='autoload' href="#rmsTaskTrace/showRmsTaskHistory"><span><i class="pre-icon consumption-deviation"></i></span>
                    <span class="menuText">Task History</span></a></li>
            </sec:access>
            <sec:access url="/rmsTask/showForViewNotes">
                <li><a class='autoload' href="#rmsTask/showForViewNotes"><span><i class="pre-icon accountin_voucher"></i></span>
                    <span class="menuText">View Notes</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/rmsReport/showForListWiseStatusReport,/rmsTaskList/showSearchTaskList,/rmsReport/showForForwardUnpaidTask,
        /rmsReport/showTaskListPlan,/rmsReport/showForViewCancelTask, /rmsReport/showDecisionSummary">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/rmsReport/showForListWiseStatusReport">
                <li><a class='autoload' href="#rmsReport/showForListWiseStatusReport"><span><i class="pre-icon balance-sheet"></i></span>
                    <span class="menuText">List Wise Status Report</span></a></li>
            </sec:access>
            <sec:access url="/rmsTaskList/showSearchTaskList">
                <li><a class='autoload' href="#rmsTaskList/showSearchTaskList"><span><i class="pre-icon accountin_voucher"></i></span>
                    <span class="menuText">Search Task List</span></a></li>
            </sec:access>
            <sec:access url="/rmsReport/showTaskListPlan">
                <li><a class='autoload' href="#rmsReport/showTaskListPlan"><span><i class="pre-icon budget_con_deatails"></i></span>
                    <span class="menuText">Task List Plan</span></a></li>
            </sec:access>
            <sec:access url="/rmsReport/showForForwardUnpaidTask">
                <li><a class='autoload' href="#rmsReport/showForForwardUnpaidTask"><span><i class="pre-icon task"></i></span>
                    <span class="menuText">Forwarded Unpaid Task</span></a></li>
            </sec:access>
            <sec:access url="/rmsReport/showDecisionSummary">
                <li><a class='autoload' href="#rmsReport/showDecisionSummary"><span><i class="pre-icon budged_financial_sum"></i></span>
                    <span class="menuText">Decision Taken Summary</span></a></li>
            </sec:access>
            <sec:access url="/rmsReport/showForViewCancelTask">
                <li><a class='autoload' href="#rmsReport/showForViewCancelTask"><span><i class="pre-icon project-status"></i></span>
                    <span class="menuText">Canceled Task</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/systemConfiguration/show,/appMail/show,/sms/showSms">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='4'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=11"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            %{--<sec:access url="/appMail/show">--}%
                %{--<li><a class='autoload' href="#appMail/show?plugin=11"><span><i--}%
                        %{--class="pre-icon message"></i></span> <span--}%
                        %{--class="menuText">Mail</span></a></li>--}%
            %{--</sec:access>--}%

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=11"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl urls="/rmsExchangeHouse/show,/rmsExchangeHouseCurrencyPosting/show,rmsTransactionDay/show,
					/rmsProcessInstrumentMapping/show">
    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Settings</span></a>

    <div id='5'>
        <ul class="menuDiv">
            <sec:access url="/rmsExchangeHouse/show">
                <li><a class='autoload' href="#rmsExchangeHouse/show"><span><i class="pre-icon bank"></i></span>
                    <span class="menuText">Exchange House</span></a></li>
            </sec:access>
            <sec:access url="/rmsExchangeHouseCurrencyPosting/show">
                <li><a class='autoload' href="#rmsExchangeHouseCurrencyPosting/show"><span><i class="pre-icon bank-reconciliation"></i></span>
                    <span class="menuText">Ex. House Currency Posting</span></a></li>
            </sec:access>
            <sec:access url="/rmsProcessInstrumentMapping/show">
                <li><a class='autoload' href="#rmsProcessInstrumentMapping/show"><span><i class="pre-icon combined_qs_mgt"></i></span> <span
                        class="menuText">Process Instrument Mapping</span></a></li>
            </sec:access>
            <sec:access url="/rmsPurchaseInstrumentMapping/show">
                <li><a class='autoload' href="#rmsPurchaseInstrumentMapping/show"><span><i class="pre-icon income-statement"></i></span> <span
                        class="menuText">Purchase Instrument Mapping</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style="height: 60px; display: none;">
</div>