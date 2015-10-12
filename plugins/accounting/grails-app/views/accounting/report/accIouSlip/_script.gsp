<script type="text/javascript">
    var iouSlipIdForNavigation = "${result.iouSlipMap?.iouSlipId}";
    var lstPurposeForNavigation = ${result.purposeList?result.purposeList:[]};

    $(document).ready(function () {
        onLoadIouSlip();

        $('#printIouSlip').click(function () {
            printIouSlip();
            return false;
        });
    });

    function onLoadIouSlip() {
        $("#searchForm").kendoValidator({validateOnBlur: false});

        if (iouSlipIdForNavigation) {
            $('#updateIouSlipDiv').show();
        }

        $("#tblItems").kendoListView({
            dataSource: lstPurposeForNavigation,
            template: "<tr>" +
                    "<td>#:sl#</td>" +
                    "<td>#:purpose#</td>" +
                    "<td>#:amount#</td>" +
                    "</tr>"
        });

        $("#accIouSlipId").val(iouSlipIdForNavigation);
        $('#hideAccIouSlipId').val(iouSlipIdForNavigation);

        // update page title
        $(document).attr('title', "MIS - IOU Slip");
        loadNumberedMenu(MENU_ID_ACCOUNTING, "#accReport/showAccIouSlipRpt");
    }

    function executePreConditionToGetIouSlip() {
        if (!validateForm($("#searchForm"))) {
            return false;
        }
        return true;
    }

    function executePostConditionForIouSlip() {
        showLoadingSpinner(false);
        if (isError == 'true') {
            $('.download_icon_set').hide();
        } else {
            if(isApproved != ''){
                $('.download_icon_set').show();
            }
        }
    }

    function printIouSlip() {
        var iouSlipId = $('#hideAccIouSlipId').val();
        if (iouSlipId.length <= 0) {
            showError('First populate Iou Slip then click print');
            return false;
        }

        showLoadingSpinner(true);
        var params = "?accIouSlipId=" + iouSlipId;
        if (confirm('Do you want to download the IOU Slip now?')) {
            var url = "${createLink(controller:'accReport', action: 'downloadAccIouSlipRpt')}" + params;
            document.location = url;
        }
        showLoadingSpinner(false);
    }

</script>