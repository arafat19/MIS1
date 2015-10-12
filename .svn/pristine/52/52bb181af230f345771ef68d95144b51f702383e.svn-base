<script language="javascript">
    var isError, transactionId, lstRawMaterials, lstFinishedProducts;

    $(document).ready(function () {
        isError = "${result.isError}";
        transactionId = "${result.transactionMap?.transactionId}";
        lstRawMaterials = ${result.lstRawMaterial?result.lstRawMaterial:[]};
        lstFinishedProducts = ${result.lstFinishedProduct?result.lstFinishedProduct:[]};
        var message = "${result.message}";

        if (isError == 'true') {
            showError(message);
            $('.download_icon_set').hide();
            return false;
        }
        if (transactionId > 0) {
            $('#updateProductionDiv').show();
            $('#transactionDetailsDiv').show();
            $('#hidTransactionId').val(transactionId);
            $('.download_icon_set').show();
        }

        if (lstRawMaterials == 0) {
            $('#tagRMList').hide();
            $('#rawMaterial').hide();
        }
        if (lstFinishedProducts == 0) {
            $('#tagFinishedProductList').hide();
            $('#finishedProduct').hide();
        }

        $("#tbodyMaterials").kendoListView({
            dataSource: lstRawMaterials,
            template: "<tr>" +
                    "<td>#:id#</td>" +
                    "<td>#:raw_material#</td>" +
                    "<td>#:str_quantity#</td>" +
                    "<td>#:str_rate#</td>" +
                    "<td>#:str_total_amount#</td>" +
                    "</tr>"
        });

        $("#tbodyFinishedProduct").kendoListView({
            dataSource: lstFinishedProducts,
            template: "<tr>" +
                    "<td>#:id#</td>" +
                    "<td>#:finished_product#</td>" +
                    "<td>#:str_quantity#</td>" +
                    "<td>#:str_rate#</td>" +
                    "<td>#:str_overhead_cost#</td>" +
                    "<td>#:str_total_amount#</td>" +
                    "</tr>"
        });
    });
</script>

<div id="transactionDetailsDiv" class="table-responsive" style="display: none;">
    <table class="table table-bordered">
        <tbody>
        <tr>
            <td class="active">Production Information</td>
        </tr>
        <tr>
            <td>
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="active">Production ID</td>
                        <td>${result.transactionMap?.transactionId}</td>
                        <td class="active">Production Date</td>
                        <td>${result.transactionMap?.transactionDate}</td>
                    </tr>
                    <tr>
                        <td class="active">Inventory Name</td>
                        <td>${result.transactionMap?.inventoryName}</td>
                        <td class="active">Production Line Item</td>
                        <td>${result.transactionMap?.productionLineItem}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>

        <tr id="tagRMList">
            <td class="active">Raw Materials</td>
        </tr>
        <tr>
            <td id="rawMaterial">
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>Trace No</th>
                        <th>Item Name</th>
                        <th class="align_right">Quantity</th>
                        <th class="align_right">Rate</th>
                        <th class="align_right">Amount</th>
                    </tr>
                    </thead>
                    <tbody id="tbodyMaterials">
                    </tbody>
                    <tr>
                        <td colspan="3" rowspan="2"></td>
                        <td class="active">Total Amount</td>
                        <td>${result.transactionMap?.totalAmount}</td>
                    </tr>
                    <tr>
                        <td class="active">Rate Per Unit</td>
                        <td>${result.transactionMap?.ratePerUnit}</td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr id="tagFinishedProductList">
            <td class="active">Finished Products</td>
        </tr>
        <tr>
            <td id="finishedProduct">
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>Trace No</th>
                        <th>Item Name</th>
                        <th>Quantity</th>
                        <th>Rate</th>
                        <th>Overhead Cost</th>
                        <th>Amount</th>
                    </tr>
                    </thead>
                    <tbody id="tbodyFinishedProduct">
                    </tbody>
                </table>
            </td>
        </tr>
        </tbody>
    </table>
</div>


