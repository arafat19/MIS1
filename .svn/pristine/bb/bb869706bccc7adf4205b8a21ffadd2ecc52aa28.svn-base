<table id="flex1" style="display:none"></table>
<script type="text/javascript">
    $(document).ready(function () {
        initFlexOnlineUser();
        // update page title
        $('span.headingText').html('Online Users');
        $('#icon_box').attr('class', 'pre-icon-header online_user');
        $(document).attr('title', "MIS - Online Users");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/showOnlineUser");

    });
    function initFlexOnlineUser() {
        $("#flex1").flexigrid
        (
                {
                    url: "${createLink(controller: 'appUser', action: 'listOnlineUser')}",
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "UserID", name: "userId", width: 30, sortable: true, align: "right", hide: true},
                        {display: "User Name", name: "userName", width: 150, sortable: false, align: "left"},
                        {display: "Login ID", name: "loginId", width: 140, sortable: false, align: "left"},
                        {display: "Login Time", name: "loginTime", width: 140, sortable: false, align: "left"},
                        {display: "IP Address", name: "ipAddress", width: 100, sortable: false, align: "left"},
                        {display: "Browser", name: "browser", width: 100, sortable: false, align: "left"},
                        {display: "Operating System", name: "operatingSystem", width: 100, sortable: false, align: "left"},
                        {display: "Last Activity", name: "lastActivity", width: 170, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Select All', bclass: 'select-all', onpress: doSelectAll},
                        {name: 'Deselect All', bclass: 'deselect-all', onpress: doDeselectAll},
                        <sec:access url="/appUser/forceLogoutOnlineUser">
                        {name: 'Force Logout', bclass: 'force-logout', onpress: forceLogout},
                        </sec:access>
                        {name: 'Refresh', bclass: 'refresh', onpress: refreshGrid},
                        {separator: true}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All  Online Users',
                    useRp: true,
                    rp: 25,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    afterAjax: function () {
                        showLoadingSpinner(false);// Spinner hide after AJAX Call
                    },
                    customPopulate: populateOnlineUserGrid
                }
        );
    }

    function doSelectAll(com, grid) {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length > 0) {
            rows.addClass('trSelected');
        }
    }
    function doDeselectAll(com, grid) {
        var rows = $('table#flex1 > tbody > tr');
        if (rows && rows.length > 0) {
            rows.removeClass('trSelected');
        }
    }
    function populateOnlineUserGrid(data) {
        var gridData;
        if (data.isError) {
            showError(data.message);
            gridData = getEmptyGridModel();
        } else {
            gridData = data.gridObj;
        }
        $('#flex1').flexAddData(gridData);
        return false;
    }

    function refreshGrid(com, grid) {
        $('#flex1').flexReload();
    }

    function executePreConditionForExpire() {
        if (executeCommonPreConditionForSelect($('#flex1'), 'user') == false) {
            return false;
        }
        if (!confirm('Are you sure you want to force logout selected user?')) {
            return false;
        }
        return true;
    }

    function forceLogout(com, grid) {
        var selectedIds = $('.trSelected', grid);

        if (executePreConditionForExpire() == false) {
            return;
        }

        showLoadingSpinner(true);
        var ids = '';
        selectedIds.each(function (e) {
            var id = $(this).attr('id').replace('row', '');
            ids += id + '_';
        });

        jQuery.ajax({
            type: 'post',
            url: "${createLink(controller:'appUser', action: 'forceLogoutOnlineUser')}?ids=" + ids,
            success: function (data, textStatus) {
                executePostConditionForExpire(data);
            },
            complete: function (XMLHttpRequest, textStatus) {
                setButtonDisabled($('.save'), false);
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForExpire(data) {
        if (data.isError == true) {
            showError(data.message);
            return false;
        }
        var selectedRow = null;
        $('.trSelected', $('#flex1')).each(function (e) {
            selectedRow = $(this).remove();
        });
        $('#flex1').decreaseCount(1);
        showSuccess(data.message);
    }
</script>

