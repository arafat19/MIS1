<!-- FOR APPLICATION MODULE -->

<app:ifPlugin name="Application">
    <!-- For Reset module for all Roles Confirmation -->
    <div class="modal fade" id="appResetAllRolesConfirmationModal" tabindex="-1" role="dialog"
         aria-labelledby="appResetAllRolesConfirmationModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title">Confirm Following Issues and Enter Password:</h4>
                    <h6 class="modal-title">1. Any Custom Role(s) Will Be De-associated From All Features.</h6>
                    <h6 class="modal-title">2. All System Roles And Corresponding Features Will Be Reset.</h6>
                    <h6 class="modal-title">3. User Authentication Will Be Required.</h6>
                </div>

                <div class="modal-body">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="txtAppPassword">Password:</label>

                        <div class="col-md-6">
                            <input style="width: 50%" type="password" class="k-textbox" id="txtAppPassword"
                                   name="txtAppPassword" required="" validationMessage="Required"
                                   placeholder="Enter Password.."/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="txtAppPassword"></span>
                        </div>

                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"
                            onclick="onSubmitResetReqMapConfirmation();">Reset All Roles</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="exitResetReqMapConfirmForm();">Close</button>
                </div>
            </div>
        </div>
    </div>
</app:ifPlugin>

<!-- FOR BUDGET MODULE -->
<app:ifPlugin name="Budget">
    <!-- For Generate Budget Requirements' Confirmation -->
    <div class="modal fade" id="viewRequirementsConfirmationModalBudget" tabindex="-1" role="dialog"
         aria-labelledby="viewRequirementsConfirmationModalBudget" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelGenerateBudgetRequirements">Budget Line Item : <span
                            id="lblBudgetId"></span></h4>
                </div>

                <div class="modal-body">
                    <div>Material has been generated for - <span id="lblLineItem"></span></div>
                    <div>Would you like to see the generated budget requirements now?</div>
                    <div>&nbsp;</div>
                    <div style="font-style: italic">You can also see the budget requirements by navigating from budget to items.</div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"
                            onclick="viewBudgetRequirements();">YES</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="cleanViewBudgetRequirementsForm();">NO</button>
                </div>
            </div>
        </div>
    </div>
</app:ifPlugin>

<!-- FOR PROCUREMENT MODULE -->

<app:ifPlugin name="Procurement">
    <!-- For PO Cancellation Confirmation -->
    <div class="modal fade" id="cancelConfirmationModalPO" tabindex="-1" role="dialog"
         aria-labelledby="cancelConfirmationModalPOLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelCancelPO">Cancel Purchase Order No:<span
                            id="lblProcCancelPoNo"></span></h4>
                </div>

                <div class="modal-body">
                    <textarea style="width: 100%" type="text" class="k-textbox" id="txtProcPoCancelReason"
                              name="txtProcPoCancelReason" rows="3"
                              placeholder="Enter Cancellation Reason.."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="procProcessCancelPO();">Cancel PO</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="procCleanPOCancelForm();">Close</button>
                </div>
            </div>
        </div>
    </div>
</app:ifPlugin>


<!-- FOR ACCOUNTING MODULE -->
<app:ifPlugin name="Accounting">
    <!-- For Voucher(Pay/Receive-Cash/Bank, Journal) Cancellation Confirmation -->
    <div class="modal fade" id="cancelConfirmationModalVoucher" tabindex="-1" role="dialog"
         aria-labelledby="cancelConfirmationModalVoucherLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelCancelVoucher">Cancel Voucher : <span
                            id="lblAccCancelVoucherTrace"></span></h4>
                </div>

                <div class="modal-body">
                    <textarea style="width: 100%" type="text" class="k-textbox" id="txtAccVoucherCancelReason"
                              name="txtAccVoucherCancelReason" rows="3"
                              placeholder="Enter Cancellation Reason.."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"
                            onclick="accProcessCancelVoucher();">Cancel Voucher</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="accCleanVoucherCancelForm();">Close</button>
                </div>
            </div>
        </div>
    </div>
</app:ifPlugin>


<!-- FOR ExchangeHouse MODULE -->
<app:ifPlugin name="ExchangeHouse">
	<!-- For (exchangeHouse,agent,customer) Task Cancellation Confirmation -->
	<div class="modal fade" id="exhCancelTaskConfirmationModal" tabindex="-1" role="dialog" aria-labelledby="exhCancelTaskConfirmationModal" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<span>Are you sure you want to cancel selected task(s)?</span>
				</div>
				<div class="modal-body">
					<textarea style="width: 100%" type="text" class="k-textbox" id="txtExhCancellationReason" name="txtExhCancellationReason" rows="3"
					          placeholder="Enter Cancellation Reason.." ></textarea>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="onSubmitExhTaskCancelConfirmation();">Cancel Task(s)</button>
					<button type="button" class="btn btn-default" data-dismiss="modal" onclick="exitExhTaskCancelForm();">Close</button>
				</div>
			</div>
		</div>
	</div>

	<!-- For Task create Confirmation -->
	<div class="modal fade" id="taskCreateConfirmationDialog" tabindex="-1" role="dialog" aria-labelledby="exhCancelTaskConfirmationModal" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4>Create Task Confirmation</h4>
				</div>
				<div class="modal-body">
					<div id="taskCreateConfirmationErrorList">

					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="btnConfirmCreateTask" onclick="onSubmitTaskConfirmation();">Create Task</button>
					<button type="button" class="btn btn-default" id="btnExitConfirmCreateTask" data-dismiss="modal" onclick="exitTaskCreateConfirmForm();">Don't Create Task</button>
				</div>
			</div>
		</div>
	</div>
</app:ifPlugin>


<!-- FOR ARMS MODULE -->

<app:ifPlugin name="ARMS">
    <!-- For Revise Task Confirmation -->
    <div class="modal fade" id="reviseConfirmationModalTask" tabindex="-1" role="dialog"
         aria-labelledby="reviseConfirmationModalTaskLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelRevise">Revise Task:<span
                            id="lblReviseTask"></span></h4>
                </div>

                <div class="modal-body">
                    <textarea style="width: 100%" type="text" class="k-textbox" id="txtTaskReviseReason"
                              name="txtTaskReviseReason" rows="3"
                              placeholder="Enter Revision Note"></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="reviseTask();">Revise</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="cancelReviseTask();">Close</button>
                </div>
            </div>
        </div>
    </div>
    <!-- For Task cancel Confirmation -->
    <div class="modal fade" id="noteForCancelModalTask" tabindex="-1" role="dialog"
         aria-labelledby="cancelConfirmationModalTaskLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelCancel">Cancel Task:<span
                            id="lblCancelTask"></span></h4>
                </div>

                <div class="modal-body">
                    <textarea style="width: 100%" type="text" class="k-textbox" id="txtTaskCancelReason"
                              name="txtTaskCancelReason" rows="3"
                              placeholder="Enter Cancellation Note"></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="cancelTask();">Cancel</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="closeCancelModal();">Close</button>
                </div>
            </div>
        </div>
    </div>

    <!-- For Task Remove From List Confirmation -->
    <div class="modal fade" id="noteForRemoveFromListModal" tabindex="-1" role="dialog"
         aria-labelledby="removeFromListConfirmationModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="modalLabelRemoveFromList">Remove From List:<span
                            id="lblRemoveFromList"></span></h4>
                </div>

                <div class="modal-body">
                    <textarea style="width: 100%" type="text" class="k-textbox" id="txtTaskRemoveFromListReason"
                              name="txtTaskRemoveFromListReason" rows="3"
                              placeholder="Note for remove from list"></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="removeFromList();">Remove From List</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"
                            onclick="closeRemoveListModal();">Close</button>
                </div>
            </div>
        </div>
    </div>
</app:ifPlugin>






