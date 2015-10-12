<script type="text/javascript">
    var dbInstanceId;

    $(document).ready(function () {
        var modelJson = ${modelJson};
        var isError = modelJson.isError;
        if(isError) {
            showError(modelJson.message);
            $('.download_icon_set').hide();
        }
        else{
            dbInstanceId = modelJson.dbInstanceId;
            initResultGrid();
        }
        $(document).attr('title', "DOC - Show Query Result");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docDbInstance/show");
    });

    function initResultGrid() {
        $("#gridDocSqlResult").kendoGrid({
            dataSource: {

                transport: {
                    read: {
                        url: "/docDbInstance/listResult?dbInstanceId=" + dbInstanceId,
                        dataType: "json",
                        type: "post"
                    }
                }
            },
            sortable: false,
            selectable: false,
            resizable: true
        });
    }

    function downloadCsv() {
        if (confirm('Do you want to download the CSV now?')) {
            var url = "${createLink(controller: 'docDbInstance', action: 'downloadResultCsv')}?dbInstanceId=" + dbInstanceId;
            document.location = url;
        }
    }
</script>
