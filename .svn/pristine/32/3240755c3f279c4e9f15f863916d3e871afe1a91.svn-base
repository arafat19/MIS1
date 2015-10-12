<script language="javascript">
    var lstBudgetItemModel, lstBudgetContentModel, isError, budgetId;
    var lstBudgetItem, lstBudgetContent;
    $(document).ready(function () {
        isError = "${result.isError}";
        budgetId = "${result.budgetMap?.budgetId}";
        lstBudgetItem = ${result.itemMapList?result.itemMapList:[]};
        lstBudgetContent = ${result.contentMapList?result.contentMapList:[]};
        var message = "${result.message}";
        $('#hideBudgetId').val(budgetId);

        if (isError == 'true') {
            showError(message);
            $('#updateBudgetDetailsDiv').hide();
            return false;
        }
        if (budgetId > 0) {
            $('#updateBudgetDetailsDiv').show();
        }

        lstBudgetItemModel = $("#lstBudgetDetailsWithItem").kendoListView({
            dataSource: lstBudgetItem,
            template: "<tr>" +
                        "<td>#:sl#</td>" +
                        "<td>#:itemType#</td>" +
                        "<td>#:itemName#</td>" +
                        "<td>#:code#</td>" +
                        "<td>#:quantity#</td>" +
                        "<td>#:estimateRate#</td>" +
                        "<td>#:totalCost#</td>" +
                    "</tr>"
        }).data("lstBudgetItemModel");


        lstBudgetContentModel = $("#lstAttachment").kendoListView({
            dataSource: lstBudgetContent,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:caption#</td>" +
                    "<td><a href='#:link#' title='Click to download'>#:fileName#</a></td>" +
                    "</tr>"
        }).data("lstBudgetContentModel");

    });
</script>

<div class="panel panel-default">
    <div class="panel-heading">Budget Details</div>

    <div class="panel-body">
        <table class="table table-bordered">
            <tbody>
            <tr>
                <td class="active">Project</td>
                <td>${result.budgetMap?.projectName}</td>
                <td class="active">Created On</td>
                <td>${result.budgetMap?.createdOn}</td>
            </tr>
            <tr>
                <td class="active">Trace No</td>
                <td>${result.budgetMap?.budgetId}</td>
                <td class="active">Budget Quantity</td>
                <td>${result.budgetMap?.budgetQuantity}</td>
            </tr>
            <tr>
                <td class="active">Budget Scope</td>
                <td>${result.budgetMap?.budgetScope}</td>
                <td class="active">No. of Item(s)</td>
                <td>${result.budgetMap?.itemCount}</td>
            </tr>

            <tr>
                <td class="active">Budget Line Item</td>
                <td>${result.budgetMap?.budgetItem}</td>
                <td class="active">Cost Per Unit</td>
                <td>${result.budgetMap?.costPerUnit}</td>
            </tr>
            <tr>
                <td class="active">Contract Rate</td>
                <td>${result.budgetMap?.contractRate}</td>
                <td class="active">Total Cost</td>
                <td>${result.budgetMap?.totalCost}</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<div id="budgetDetails" class="panel panel-default">
    <div class="panel-heading">Description</div>

    <div class="panel-body">
        <div id="detailBudget">${result.budgetMap?.budgetDetails}</div>
    </div>
</div>

<div id="listBudgetDetailsWithItem" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Item List</div>

    <div class="panel-body">
        <table class="table table-striped">
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
            <tbody id="lstBudgetDetailsWithItem">
            </tbody>
        </table>
    </div>
</div>


<div id="listAttachment" class="panel panel-default" style="display: none;">
    <div class="panel-heading">Attachment List</div>

    <div class="panel-body">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>SL#</th>
                <th>Caption</th>
                <th>File Name</th>
            </tr>
            </thead>
            <tbody id="lstAttachment">
            </tbody>
        </table>
    </div>
</div>