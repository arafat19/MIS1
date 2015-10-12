<script type="text/javascript">
    var tmplPurchase, tmplBranch, tmplPrinciple, tmplSme;
    var dataSourcePurchase, dataSourceBranch, dataSourcePrinciple, dataSourceSme;

    $(document).ready(function () {
        var modelIsError = "${model.isError}";
        var modelMsgError = "${model.message}";
        $('#taskListPlanDiv').show();
        if (modelIsError == 'true') {
            showError(modelMsgError);
            $('#taskListPlanDiv').hide();
            return false;
        }

        tmplPurchase = "<tr><td>#:bank#</td><td class='align_right'>#:amount#</td></tr>";
        tmplBranch = "<tr><td>#:branch#</td><td class='align_right'>#:amount#</td></tr>";
        tmplPrinciple = "<tr><td>#:instrument#</td><td class='align_right'>#:amount#</td></tr>";
        tmplSme = "<tr><td>#:sme#</td><td class='align_right'>#:amount#</td></tr>";

        dataSourcePurchase = ${model.purchaseListViewObj};
        dataSourceBranch = ${model.branchListViewObj};
        dataSourcePrinciple = ${model.principleBranchListViewObj};
        dataSourceSme = ${model.smeListViewObj};

        populatePurchaseListView();
        populateBranchListView();
        populatePrincipleBranchListView();
        populateSmeListView();
    });

    function populatePurchaseListView() {
        $("#purchaseListView").kendoListView({
            dataSource: dataSourcePurchase,
            template: tmplPurchase
        });
    }

    function populateBranchListView() {
        $("#branchListView").kendoListView({
            dataSource: dataSourceBranch,
            template: tmplBranch
        });
    }

    function populatePrincipleBranchListView() {
        $("#principleBranchListView").kendoListView({
            dataSource: dataSourcePrinciple,
            template: tmplPrinciple
        });
    }

    function populateSmeListView() {
        $("#smeListView").kendoListView({
            dataSource: dataSourceSme,
            template: tmplSme
        });
    }

</script>

<table class="table table-bordered table-condensed">
    <tbody>
    <tr>
        <td width="50%">
            <table class="table table-bordered table-condensed">
                <thead>
                <tr><td colspan="2" class="success">Purchase</td></tr>
                <tr>
                    <td>&nbsp</td>
                    <td class="align_right">Amount</td>
                </tr>
                </thead>
                <tbody id="purchaseListView"></tbody>
                <tfoot>
                <tr><td colspan="2" class="align_right"><b>Sub Total: ${model.purchaseSubTotal}</b>
                </td></tr>
                </tfoot>
            </table>
        </td>
        <td>
            <table class="table table-bordered table-condensed">
                <thead>
                <tr><td colspan="2" class="success">Branch</td></tr>
                <tr>
                    <td>&nbsp</td>
                    <td class="align_right">Amount</td>
                </tr>
                </thead>
                <tbody id="branchListView"></tbody>
                <tfoot>
                <tr><td colspan="2" class="align_right"><b>Sub Total: ${model.branchSubTotal}</b>
                </td></tr>
                </tfoot>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <table class="table table-bordered table-condensed">
                <thead>
                <tr><td colspan="2" class="success">Principle Branch</td></tr>
                <tr>
                    <td>&nbsp</td>
                    <td class="align_right">Amount</td>
                </tr>
                </thead>
                <tbody id="principleBranchListView"></tbody>
                <tfoot>
                <tr><td colspan="2" class="align_right"><b>Sub Total: ${model.principleBranchSubTotal}</b>
                </td></tr>
                </tfoot>
            </table>
        </td>
        <td>
            <table class="table table-bordered table-condensed">
                <thead>
                <tr><td colspan="3" class="success">SME Unit</td></tr>
                <tr>
                    <td>&nbsp</td>
                    <td class="align_right">Amount</td>
                </tr>
                </thead>
                <tbody id="smeListView"></tbody>
                <tfoot>
                <tr><td colspan="3" class="align_right"><b>Sub Total: ${model.smeSubTotal}</b>
                </td></tr>
                </tfoot>
            </table>
        </td>

    </tr>
    <tr>
        <td colspan="2" class="align_right"><b>Grand Total: ${model.grandTotal}</b></td>
    </tr>
    </tbody>
</table>