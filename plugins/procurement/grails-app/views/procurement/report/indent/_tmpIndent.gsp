<script language="javascript">
    var isError, indentId, lstItems;
    $(document).ready(function () {
        isError = "${result.isError}";
        indentId = "${result.indentMap?.indentId}";
        lstItems = ${result.itemList?result.itemList:[]};
        var message = "${result.message}";

        if (isError == 'true') {
            showError(message);
            $('.download_icon_set').hide();
            return false;
        }
        if(indentId > 0){
            $('#updateIndentDiv').show();
            $('#divIndent').show();
            $('#hideIndentId').val(indentId);
            $('#id').val(indentId);
            $('.download_icon_set').show();
        }
        if(lstItems == 0){
            $('#itemLabelDiv').hide();
            $('#itemsDiv').hide();
        }

        $("#tblItems").kendoListView({
            dataSource: lstItems,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:name#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:rate#</td>" +
                    "<td>#:amount#</td>" +
                    "</tr>"
        });

    });
</script>

<div id="divIndent" class="table-responsive" style="display: none;">
    <table class="table table-striped table-bordered table-condensed col-sm-1">
        <tbody>
        <tr>
            <td class="default" style="text-align:center;"><b>Indent Information</b></td>
        </tr>

        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Trace No:</td>
                        <td>${result?.indentMap?.indentId}</td>
                        <td class="active">Created On</td>
                        <td>${result?.indentMap?.createdOn}</td>
                    </tr>
                    <tr>
                        <td class="active">Created By:</td>
                        <td>${result?.indentMap?.createdBy}</td>
                        <td class="active">From Date:</td>
                        <td>${result?.indentMap?.fromDate}</td>
                    </tr>
                    <tr>
                        <td class="active">To Date:</td>
                        <td>${result?.indentMap?.toDate}</td>
                        <td class="active">Project Name:</td>
                        <td>${result?.indentMap?.projectName}</td>
                    </tr>
                    <tr>
                        <td class="active">Approved By:</td>
                        <td>${result?.indentMap?.approvedBy}</td>
                        <td class="active">Total Amount:</td>
                        <td>${result?.indentMap?.totalAmount}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        <tr id="itemLabelDiv">
            <td class="default" style="text-align:center;"><b>Item Details</b></td>
        </tr>
        <tr id="itemsDiv">
            <td>
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>SL#</th>
                        <th>Name</th>
                        <th>Quantity</th>
                        <th class="align_right">Rate</th>
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

