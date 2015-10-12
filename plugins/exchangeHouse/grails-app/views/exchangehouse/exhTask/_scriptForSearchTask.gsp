<script type="text/javascript">
    $(document).ready(function () {
        onLoadTopSearchPanel();
    });

    function onLoadTopSearchPanel() {
	    initializeForm($('#frmSearchTaskDetails'),searchTaskDetailsByPinOrRefNoFromTopPanel);
	    $('#securityType').kendoDropDownList();

	    // update page title
	    $(document).attr('title', "ARMS - Search Task");
	    loadNumberedMenu(MENU_ID_EXCHANGE_HOUSE, "#exhTask/showForTaskSearch");
    }

    function searchTaskDetailsByPinOrRefNoFromTopPanel() {

        if (checkDates($('#createdDateFrom'), $('#createdDateTo')) == false) return false;

        var securityType = $('#securityType option:selected').text();
        var securityNo = $.trim($('#securityNo').val());
        $('#securityNo').val(securityNo);
        if (securityNo.length == 0) {
            showError('Please enter ' + securityType);
            return false;
        }
        if (securityNo.length < 4) {
            showError(securityType + ' must have 4 characters or more');
            return false;
        }
        showLoadingSpinner(true);
        jQuery.ajax({
            type:'post',
            data:jQuery('#frmSearchTaskDetails').serialize(),
            url: "${createLink(controller: 'exhTask', action: 'searchTaskWithRefOrPin')}",
            success:
                    function(data, textStatus) {
                        try {
                            var jsonData = $.parseJSON(data);
                            if (jsonData.isError == true) {
                                showError(jsonData.message);
                                return false;
                            }
                        } catch(e) {
                            executePostConditionForSearchTaskTopPanel(data);
                            return false;
                        }

                    },
            error:function(data, XMLHttpRequest, textStatus, errorThrown) {
            },
            complete:onCompleteAjaxCall
        });
        return false;
    }
    function executePostConditionForSearchTaskTopPanel(data) {
        showLoadingSpinner(true);
        $('#taskDetailsContainer').html(data);
        showLoadingSpinner(false);
    }
</script>