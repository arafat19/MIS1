<script type="text/javascript">
    var budgetIdForNavigation = "${result.budgetMap?.budgetId}";
    var budgetLineItem = "${result.budgetMap?.budgetItem}";
    var lstBudgetItemForNavigation = ${result.itemMapList?result.itemMapList:[]};
    var lstBudgetContentForNavigation = ${result.contentMapList?result.contentMapList:[]};
    var lstBudgetItemModelForNavigation, lstBudgetContentModelForNavigation;
    $(document).ready(function () {
        onLoadBudget();
        lstBudgetContentModelForNavigation = $("#lstAttachment").kendoListView({
            dataSource: lstBudgetContentForNavigation,
            template: "<tr>" +
                        "<td>#:sl#</td>" +
                        "<td>#:caption#</td>" +
                        "<td><a href='#:link#' title='Click to download'>#:fileName#</a></td>" +
                        "</tr>"
        }).data("lstBudgetContentModelForNavigation");

        lstBudgetItemModelForNavigation = $("#lstBudgetDetailsWithItem").kendoListView({
            dataSource: lstBudgetItemForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:itemType#</td>" +
                    "<td>#:itemName#</td>" +
                    "<td>#:code#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:estimateRate#</td>" +
                    "<td>#:totalCost#</td>" +
                    "</tr>"
        }).data("lstBudgetItemModelForNavigation");

        $('#printBudgetReport').click(function () {
            printBudget();
        });
    });

    function onLoadBudget() {
        $('#listBudgetDetailsWithItem').hide();
        $('#listAttachment').hide();
        $('.download_icon_set').hide();


        $("#budgetLineItem").val(budgetLineItem);
        $('#hideBudgetId').val(budgetIdForNavigation);

        if (budgetIdForNavigation) {
            $('.download_icon_set').show();
            if (lstBudgetItemForNavigation.length > 0) {
                $('#listBudgetDetailsWithItem').show();
            }
            if (lstBudgetContentForNavigation.length > 0) {
                $('#listAttachment').show();
            }
        }

        // update page title
        $(document).attr('title', "MIS - Search Budget");
        loadNumberedMenu(MENU_ID_BUDGET, "#budgReport/showBudgetRpt");
    }


    function printBudget() {
        var budgetId = $('#hideBudgetId').val();
        if (budgetId.length <= 0) {
            showError('First populate budget then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?budgetId=" + budgetId;
        if (confirm('Do you want to download the budget now?')) {
            var url = "${createLink(controller:'budgReport', action: 'downloadBudgetRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

    function executePreConditionForSearchBudget() {
        showLoadingSpinner(true);
        // validate form data
        if ($("#budgetLineItem").val() == '') {
            showError('Please enter a Budget Line Item');
            showLoadingSpinner(false);
            return false;
        }
        return true;
    }

    function executePostCondition() {
        showLoadingSpinner(false);
        if (isError == 'true') {
            $('.download_icon_set').hide();
        } else {
            $('.download_icon_set').show();
            if (lstBudgetItem.length > 0) {
                $('#listBudgetDetailsWithItem').show();
            }
            if (lstBudgetContent.length > 0) {
                $('#listAttachment').show();
            }
        }
    }

</script>