<script language="javascript">
    var isError, purchaseOrderId, isCancelled;
    var lstPOItem, lstTermsCondition;
    $(document).ready(function () {
        isError = "${result.isError}";
        purchaseOrderId = "${result.purchaseOrderMap?.purchaseOrderId}";
        isCancelled = "${result.purchaseOrderMap?.isCancelled}";
        lstPOItem = ${result.itemList?result.itemList:[]};
        lstTermsCondition = ${result.termsAndConditionList?result.termsAndConditionList:[]};
        var message = "${result.message}";
        $("#hidPurchaseOrderId").val(purchaseOrderId);
        $("#hidIsCancelled").val(isCancelled);

        if (isError == 'true') {
            showError(message);
            $('#updatePODiv').hide();
            return false;
        }
        if (purchaseOrderId > 0) {
            $('#updatePODiv').show();
        }

        $("#lstPOWithItem").kendoListView({
            dataSource: lstPOItem,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:itemType#</td>" +
                    "<td>#:itemName#</td>" +
                    "<td>#:itemCode#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:rateStr#</td>" +
                    "<td>#:totalCostStr#</td>" +
                    "</tr>"
        });

        $("#lstTermsCondition").kendoListView({
            dataSource: lstTermsCondition,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:details#</td>" +
                    "</tr>"
        });


        $('#lblCancel1').hide();
        $('#lblCancel2').hide();
        $('#lblReferenceTitle').text('Reference');  // default value
        $('#lblCancelDate').text('Last Updated On'); // default value
        $('#lblFormHeader').text(''); // default value
        if (isCancelled == 'true') {
            $('#cancelledPo').attr('checked', true);
            $('#lblFormHeader').text(' ( Cancelled )');
            $('#lblReferenceTitle').text('Cancelled By');
            $('#lblCancelDate').text('Cancelled On');
            $('#lblCancel1').show();
            $('#lblCancel2').show();
        }

    });
</script>

<div class="panel panel-default">
    <div class="panel-heading">Purchase Order<span style="color: #ff0000" id='lblFormHeader'></span></div>

    <div class="panel-body">
        <table class="table table-bordered">
            <tbody>
            <tr>
                <td class="active">PO Trace No</td>
                <td>${result.purchaseOrderMap?.purchaseOrderId}</td>
                <td class="active"><span id='lblCancelDate'></span></td>
                <td>${result.purchaseOrderMap?.lastUpdatedOn}</td>
            </tr>
            <tr>
                <td class="active">Payment Method</td>
                <td>${result.purchaseOrderMap?.paymentMethod}</td>
                <td class="active"><span id='lblReferenceTitle'></span></td>
                <td>${result.purchaseOrderMap?.lastUpdatedBy}</td>
            </tr>
            <tr>
                <td class="active">Supplier</td>
                <td>${result.purchaseOrderMap?.supplierName}</td>
                <td class="active">Supplier Address</td>
                <td>${result.purchaseOrderMap?.supplierAddress}</td>
            </tr>
            <tr>
                <td class="active">Project Name</td>
                <td>${result.purchaseOrderMap?.projectName}</td>
                <td class="active" id='lblCancel1'>Cancellation Reason</td>
                <td id='lblCancel2'>${result.purchaseOrderMap?.cancelReason}</td>
            </tr>
            <tr>
                <td class="active">Mode of Payment</td>
                <td>${result.purchaseOrderMap?.paymentTerms}</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<div id="listPOWithItem" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Item List</div>

    <div class="panel-body">
        <table class="table table-bordered">
            <thead>
            <tr>
                <th>SL#</th>
                <th>Item Type</th>
                <th>Item Name</th>
                <th>Code</th>
                <th>Quantity</th>
                <th>Estimated Rate</th>
                <th>Total Cost</th>
            </tr>
            </thead>
            <tbody id="lstPOWithItem">
            </tbody>
            <tr>
                <td class="active" colspan="2">No. of Item(s)</td>
                <td colspan="3">${result.purchaseOrderMap?.itemCount}</td>
                <td class="active">Item Total</td>
                <td>${result.purchaseOrderMap?.totalItemAmount}</td>
            </tr>
            <tr>
                <td colspan="5" rowspan="4"></td>
                <td class="active">Discount</td>
                <td>(${result.purchaseOrderMap?.discount})</td>
            </tr>
            <tr>
                <td class="active">Total VAT/Tax</td>
                <td>(${result.purchaseOrderMap?.totalVatTax})</td>
            </tr>
            <tr>
                <td class="active">Total Tr. Cost</td>
                <td>(${result.purchaseOrderMap?.totalTransportCost})</td>
            </tr>
            <tr>
                <td class="active">Grand Total</td>
                <td>${result.purchaseOrderMap?.netPrice}</td>
            </tr>
        </table>
    </div>
</div>

<div id="listTermsCondition" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Terms & Conditions</div>

    <div class="panel-body">
        <table class="table table-bordered">
            <tbody id="lstTermsCondition">
            </tbody>
        </table>
    </div>
</div>

<div id="listPOApproval" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Approval</div>

    <div class="panel-body">
        <table class="table table-bordered">
            <tbody>
            <tr>
                <td class="active">Director</td>
                <td>${result.purchaseOrderMap?.approvedByDirector}</td>
            </tr>
            <tr>
                <td class="active">Project Director</td>
                <td>${result.purchaseOrderMap?.approvedByProjectDirector}</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>