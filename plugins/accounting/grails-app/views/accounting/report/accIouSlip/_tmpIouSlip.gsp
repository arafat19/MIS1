<script language="javascript">
    var isError, iouSlipId, lstPurpose, isApproved;
    $(document).ready(function () {
        isError = "${result.isError}";
        iouSlipId = "${result.iouSlipMap?.iouSlipId}";
        isApproved = "${result.iouSlipMap?.approvedBy}";
        lstPurpose = ${result.purposeList?result.purposeList:[]};
        var message = "${result.message}";

        if (isError == 'true') {
            showError(message);
            $('#updateIouSlipDiv').hide();
            return false;
        }
        if (iouSlipId > 0) {
            $('#updateIouSlipDiv').show();
            $('#hideAccIouSlipId').val(iouSlipId);
        }
        if (lstPurpose == 0) {
            $('#tagPurposeList').hide();
            $('#budgetDetailsWithItem').hide();
        }
        if (isApproved == '') {
            $('.download_icon_set').hide();
        }
        $("#tblItems").kendoListView({
            dataSource: lstPurpose,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:purpose#</td>" +
                    "<td>#:amount#</td>" +
                    "</tr>"
        });

    });
</script>

<div id="divIouSlip" class="table-responsive">
    <table class="table table-bordered">
        <tbody>
        <tr>
            <td class="active">PROJECT</td>
        </tr>
        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Name</td>
                        <td>${result?.iouSlipMap?.projectName}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr>
            <td class="active">IOU Details</td>
        </tr>
        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Trace No</td>
                        <td>${result?.iouSlipMap?.iouSlipId}</td>
                        <td class="active">Created On</td>
                        <td>${result?.iouSlipMap?.createdOn}</td>
                    </tr>
                    <tr>
                        <td class="active">Employee Name</td>
                        <td>${result?.iouSlipMap?.employeeName}</td>
                        <td class="active">Designation</td>
                        <td>${result?.iouSlipMap?.designation}</td>
                    </tr>
                    <tr>
                        <td class="active">Total Amount</td>
                        <td>${result?.iouSlipMap?.totalAmount}</td>
                        <td class="active">Approved By</td>
                        <td>${result?.iouSlipMap?.approvedBy}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr>
            <td class="active">Indent Details</td>
        </tr>
        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Indent Trace No</td>
                        <td>${result?.iouSlipMap?.indentId}</td>
                        <td class="active">Indent Date</td>
                        <td>${result?.indentDate}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr id="tagPurposeList">
            <td class="active">Purpose List</td>
        </tr>
        <tr>
            <td id="budgetDetailsWithItem">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>SL#</th>
                        <th>Purpose</th>
                        <th class="align_right">Amount</th>
                    </tr>
                    </thead>
                    <tbody id="tblItems">
                    </tbody>
                </table>
            </td>
        </tr>
        </tbody>
    </table>
</div>
