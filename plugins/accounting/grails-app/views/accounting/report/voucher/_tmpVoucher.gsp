<script language="javascript">
    var isError, voucherId, lstTransactionDetails,isChequePrintable, isCancelled;

    $(document).ready(function () {
        isError = "${result.isError}";
        voucherId = "${result.voucherMap?.voucherId}";
        isCancelled = "${result.voucherMap?.isCancelledVoucher}";
        isChequePrintable = "${result.voucherMap?.isChequePrintable}";
        lstTransactionDetails = ${result.voucherMap?.voucherList ? result.voucherMap?.voucherList:[]};
        var message = "${result.message}";
       // default position
        $('.download_icon_set').hide();
        $('#lblVoucherTypeName').text('Voucher Type Title');
        $('#lblCancelled').hide();
        $('#lblCancelledByOn').hide();
        $('#lblCancelledReason').hide();
        $('#accountNameDiv').hide();

        if (isError == 'true') {
            showError(message);
            return false;
        }
        if (voucherId > 0) {
            $('#hideVoucherId').val(voucherId);
            $('#lblVoucherTypeName').text('');
            $('.download_icon_set').show();
        }
        if (isChequePrintable == 'true') {
            $('#accountNameDiv').show();
        }
        if (isCancelled == 'true') {
            $('#lblCancelled').show();
            $('#lblCancelled').text(' ( Cancelled )');
            $('#cancelledVoucher').attr('checked', true);
            $('#lblCancelledByOn').show();
            $('#lblCancelledReason').show();
            $('#accountNameDiv').hide();
        }
        $("#tblTransactionDetails").kendoListView({
            dataSource: lstTransactionDetails,
            template: "<tr>" +
                    "<td>#:code#</td>" +
                    "<td>#:headName#<br>#:particulars#</td>" +
                    "<td>#:source#</td>" +
                    "<td>#:debit#</td>" +
                    "<td>#:credit#</td>" +
                    "</tr>"
        });

    });
</script>

<div id="divVoucher" class="table-responsive">
    <table class="table table-bordered">
        <tbody>
        <tr>
            <td class="active"><span id='lblVoucherTypeName'></span>
            ${result?.voucherMap?.voucherTypeName}
            <span style="color: #ff0000"  id='lblCancelled'></span></td>
        </tr>
        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Trace No</td>
                        <td>${result?.voucherMap?.traceNo}</td>
                        <td class="active">Voucher Date</td>
                        <td>${result?.voucherMap?.voucherDate}</td>
                    </tr>
                    <tr>
                        <td class="active">Project</td>
                        <td>${result?.voucherMap?.projectName}</td>
                        <td class="active">Created By</td>
                        <td>${result?.voucherMap?.preparedBy}</td>
                    </tr>
                    <tr>
                        <td class="active">Cheque No</td>
                        <td>${result?.voucherMap?.chequeNo}</td>
                        <td class="active">Posted By</td>
                        <td>${result?.voucherMap?.approvedBy}</td>
                    </tr>

                    <tr>
                        <td class="active">Instrument Type</td>
                        <td>${result?.voucherMap?.instrumentType}</td>
                        <td class="active">Instrument No</td>
                        <td>${result?.voucherMap?.instrumentNo}</td>
                    </tr>
                    <tr id='lblCancelledByOn' style="color: #ff0000">
                        <td class="active">Cancelled By</td>
                        <td>${result?.voucherMap?.cancelledBy}</td>
                        <td class="active">Cancelled On</td>
                        <td>${result?.voucherMap?.cancelledOn}</td>
                    </tr>
                    <tr id='lblCancelledReason' style="color: #ff0000">
                        <td class="active">Cancelled Reason</td>
                        <td>${result?.voucherMap?.cancelledReason}</td>
                    </tr>
                    <tr>
                        <table id="accountNameDiv" class="table table-bordered">
                            <tbody>
                            <tr>
                                <td class="active">Account Name:</td>
                                <td colspan="2"><input type="text" class="k-textbox"
                                                       id="accountName" name="accountName"
                                                       placeholder="Account Name"/>
                                </td>
                                <td class="active">Account Payable:</td>
                                <td><g:checkBox name="isAccountPayable"/></td>
                                <td>
                                    <app:ifAnyUrl
                                            urls="/accReport/downloadVoucherBankCheque,/accReport/downloadVoucherBankChequePreview">
                                        <span class="download_icon_set">
                                            <ul>
                                                <li>Print :</li>
                                                <app:ifAllUrl urls="/accReport/downloadVoucherBankCheque">
                                                    <li><a href="javascript:void(0)" id="printVoucherBankCheque"
                                                           class="cheque_icon"></a></li>
                                                </app:ifAllUrl>
                                                <app:ifAllUrl urls="/accReport/downloadVoucherBankChequePreview">
                                                    <li><a href="javascript:void(0)" id="printVoucherChequePreview"
                                                           class="cheque_preview"></a></li>
                                                </app:ifAllUrl>
                                            </ul>
                                        </span>
                                    </app:ifAnyUrl>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </tr>
                    <tr id="tagTransactionDetailsList">
                        <td class="active">Transaction Details</td>
                    </tr>
                    <tr>
                        <td id="transactionDetails">
                            <table class="table table-striped" id="tblTransaction">
                                <thead>
                                <style>
                                    #tblTransaction tr:last-child {font-weight: bold;}
                                </style>
                                <tr>
                                    <th>Code</th>
                                    <th>Head Name & Particulars</th>
                                    <th>Source</th>
                                    <th>Debit</th>
                                    <th>Credit</th>
                                </tr>
                                </thead>
                                <tbody id="tblTransactionDetails">
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        </tbody>
    </table>
</div>