<script type="text/javascript">
    jQuery(function ($) {
        $('#recalculateValuation').click(function () {
            recalculateAllValuation();
            return false;
        });
        onLoadForRecalculation();
    });

    function onLoadForRecalculation() {
        initializeForm($("#searchForm"), recalculateAllValuation);
        // update page title
        $(document).attr('title', "MIS - Recalculate All Valuation");
        loadNumberedMenu(MENU_ID_INVENTORY, "#invInventoryTransaction/showReCalculateValuation");
    }

    function recalculateAllValuation() {
        if (!confirm('Are you sure you want to re-calculate inventory valuation?\nThis may take several minutes.')) {
            return false;
        }
        showLoadingSpinner(true);
        $.ajax({
            url: "${createLink(controller:'invInventoryTransaction', action: 'reCalculateValuation')}",
            success: executePostCondition,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
        return false;
    }

    function executePostCondition(data) {
        if (data.isError) {
            showError(data.message);
            return;
        }
        showSuccess(data.message);
        return false;
    }

</script>