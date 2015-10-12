<script type="text/javascript">
    var indentIdForNavigation = "${result.indentMap?.indentId}";
    var lstItemsForNavigation = ${result.itemList?result.itemList:[]};

    $(document).ready(function () {
        onLoadIndent();

        $('#printIndentReport').click(function () {
            printIndent();
            return false;
        });
    });

    function onLoadIndent() {
        $("#searchForm").kendoValidator({validateOnBlur: false});

        $("#tblItems").kendoListView({
            dataSource: lstItemsForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:name#</td>" +
                    "<td>#:quantity#</td>" +
                    "<td>#:rate#</td>" +
                    "<td>#:amount#</td>" +
                    "</tr>"
        });
        if (!indentIdForNavigation) {
            $('.download_icon_set').hide();
        }
        // update page title
        $(document).attr('title', "MIS - Indent");
        loadNumberedMenu(MENU_ID_PROCUREMENT, "#procReport/showIndentRpt");
    }

    function executePreConditionToGetIndent(ids) {
        if (!validateForm($('#searchForm'))) {
            return false;
        }
        return true;
    }

    function executePostConditionForIndent() {
        if (isError == 'true') {
            showError(message);
            $('#divIndent').hide();
            $('.download_icon_set').hide();
            return;
        }
    }


    function printIndent() {
        var indentId = $('#hideIndentId').val();
        if (indentId.length <= 0) {
            showError('First populate indent details then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?hideIndentId=" + indentId;
        if (confirm('Do you want to download the indent now?')) {
            var url = "${createLink(controller: 'procReport', action: 'downloadIndentRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>