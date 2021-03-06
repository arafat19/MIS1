<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.document.config.DocSysConfigurationCacheUtility" %>
<script type="text/javascript">

    $(document).ready(function () {
        onLoadCategory();
    });

    function onLoadCategory() {
        initializeForm($('#outstandingInvitations'), onSearchOutstandingInvitationMembers);

        initFlexGrid();
        $(document).attr('title', "DOC - Show Outstanding Invitations ");
        loadNumberedMenu(MENU_ID_DOCUMENT, "#docInvitedMembers/showOutStandingInvitations");
    }

    function executePreCondition() {
        if (!customValidateDate($("#fromDate"), 'from date', $("#toDate"), 'to date')) {
            return false;
        }
        return true;
    }

    function onSearchOutstandingInvitationMembers() {
        if (executePreCondition() == false) {
            return false;
        }
        setButtonDisabled($('#create'), true);
        // Spinner Show on AJAX Call
        showLoadingSpinner(true);

        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        var params = "?fromDate=" + fromDate + "&toDate=" + toDate + "&acceptedInvitation=" + $("#acceptedInvitation").is(':checked');
        var strUrl = "${createLink(controller: 'docInvitedMembers', action: 'outStandingInvitations')}" + params;
        $("#flexOutstandingInvitations").flexOptions({url: strUrl}).flexReload();
        setButtonDisabled($('#create'), false);
    }

    function resetForm() {
        clearForm($("#outstandingInvitations"), $('#fromDate'));
        setButtonDisabled($('.save'), false);
        $("#create").html("<span class='k-icon k-i-plus'></span>Search");   // reset create button text
    }

    function initFlexGrid() {
        $("#flexOutstandingInvitations").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 42, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 20, sortable: false, align: "right", hide: true},
                        {display: "Email", name: "email", width: 225, sortable: true, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "category_name", width: 200, sortable: true, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "sub_category_name", width: 250, sortable: true, align: "left"},
                        {display: "Sent On", name: "invitationSentOn", width: 100, sortable: false, align: "center"},
                        {display: "Accepted On", name: "invitationAccepted", width: 100, sortable: false, align: "center"},
                        {display: "Expire After", name: "expired_on", width: 100, sortable: true, align: "center"}
                    ],
                    buttons: [
                        {name: 'Resend Invitation', bclass: 'resend-mail', onpress: resendInvitation},
                        {separator: true},
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "Email", name: "email", width: 180, sortable: false, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig>", name: "dc.name", width: 180, sortable: false, align: "left"},
                        {display: "<app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                  key="${DocSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL}"></app:showSysConfig>", name: "dsc.name", width: 180, sortable: false, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "asc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Outstanding Invitation List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 30,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    },
                    customPopulate: customPopulateGrid
                }
        );
    }

    function customPopulateGrid(data) {
        if (data.isError) {
            showError(data.message);
            $("#flexOutstandingInvitations").flexAddData(getEmptyGridModel());
        } else {
            $("#flexOutstandingInvitations").flexAddData(data.gridObj);
        }
    }

    <%-- reloading grid data --%>
    function reloadGrid(com, grid) {
        $('#flexOutstandingInvitations').flexOptions({query: ''}).flexReload();
    }

    function resendInvitation(com, grid) {
        if (executeCommonPreConditionForSelect($('#flexOutstandingInvitations'), 'Outstanding Invitation') == false) {
            return;
        }

        var ids = $('.trSelected', grid);
        var expiredOn = $(ids[ids.length - 1]).find("td").eq(7).find("span").text();
        var acceptedOn = $(ids[ids.length - 1]).find("td").eq(6).find("div").html();

        if (expiredOn == 'N/A') {
            showError("Invitation already accepted.");
            return
        }

        if (!(expiredOn == 'Expired')) {
            showError("Invitation is not expire yet.");
            return
        }

        if (! (acceptedOn == 'Pending')) {
            showError("Invitation already accepted.");
            return
        }

        var id = getSelectedIdFromGrid($('#flexOutstandingInvitations'));
        showLoadingSpinner(true);

        var loc = "${createLink(controller:'docInvitedMembers', action: 'showResendInvitation')}?id=" + id;
        $.history.load(formatLink(loc));
        return false;
    }

    function executePostConditionForResendInvitation(data) {
        if (data.isError == true) {
            showError(data.message);
            return;
        } else {
            $('#flexOutstandingInvitations').flexOptions({query: ''}).flexReload();
            $("#invitationMessage").val('');
            showSuccess(data.message);
        }
    }

</script>
