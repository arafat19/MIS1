<script type="text/javascript">
    var validatorUser, dropDownEmployee, uploading, output, gridModelUserInfo;

    $(document).ready(function () {
        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });
        onLoadAppUser();
    });

    function bindUserFormEvents() {
        var actionUrl = "${createLink(controller: 'appUser',action: 'create')}";
        $("#userForm").attr('action', actionUrl);

        $('#userForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('.save'), true);
            },
            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    onSaveUser(response);
                    uploading = false;
                    setButtonDisabled($('.save'), false);
                }
            },
            beforePost: function () {
                if (executePreConForSubmitUser() == false) {
                    return false;
                }
                return true;
            }
        });
    }

    function onLoadAppUser() {
        validatorUser = $("#userForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#signatureImage").kendoUpload({multiple: false});

        output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        }

        gridModelUserInfo = output.gridObject;

        bindUserFormEvents();
        initFlexUser();
        setUrlUserGrid();

        // update page title
        $('span.headingText').html('User');
        $('#icon_box').attr('class', 'pre-icon-header application_user');
        $(document).attr('title', "MIS - User");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/show");
    }

    function setUrlUserGrid() {
        var strUrl = "${createLink(controller: 'appUser', action: 'list')}";
        $("#flex1").flexOptions({url: strUrl});
        if (gridModelUserInfo) {
            $('#flex1').flexAddData(gridModelUserInfo);
        }
    }

    function executePreConForSubmitUser() {

        clearErrors($("#userForm"));
        var passLength = $("#password").val().length;
        var hiddenPassLength = $("#existingPass").val().length;

        if (hiddenPassLength <= 0) {
            $("#password").attr('required', true);
            $("#confirmPassword").attr('required', true);
        }
        else if (hiddenPassLength > 0 && passLength == 0) {
            $("#password").attr('required', false);
            $("#confirmPassword").attr('required', false);
        }
        else {
            $("#password").attr('required', true);
            $("#confirmPassword").attr('required', true);
        }

        trimTextFields();

        if (!validatorUser.validate()) {
            return false;
        }
        // now check two password fields
        if (checkPasswordMatch() == false) return false;

        return true;
    }

    function checkPasswordMatch() {
        if ($("#password").val() != $("#confirmPassword").val()) {
//            $('#retypePassError').show();
            showError("Password mismatched");
            $("#confirmPassword").focus();
            return false;
        } else {
//            $('#retypePassError').hide();
            return true;
        }
    }

    function trimTextFields() {
        $('#loginid').val($.trim($('#loginid').val()));
        $('#username').val($.trim($('#username').val()));
        $('#cellNumber').val($.trim($('#cellNumber').val()));
        $('#ipAddress').val($.trim($('#ipAddress').val()));
    }

    function onSaveUser(data) {
        var result = eval('(' + data + ')');
        $("#errorList").html("");
        if (result.isError) {
            displayArmsUserEntryErrors(result);
        } else {
            try {
                var newEntry = result.gridEntity;
                if ($('#id').val().isEmpty() && newEntry != null) { // newly created

                    var previousTotal = parseInt(gridModelUserInfo.total);
                    var firstSerial = 1;

                    if (gridModelUserInfo.rows.length > 0) {
                        firstSerial = gridModelUserInfo.rows[0].cell[0];
                        regenerateSerial($(gridModelUserInfo.rows), 0);
                    }
                    newEntry.cell[0] = firstSerial;

                    gridModelUserInfo.rows.splice(0, 0, newEntry);

                    if ($('#flex1').countEqualsResultPerPage(previousTotal)) {
                        gridModelUserInfo.rows.pop();
                    }

                    gridModelUserInfo.total = ++previousTotal;
                    $("#flex1").flexAddData(gridModelUserInfo);

                } else if (newEntry != null) { // updated existing
                    updateListModel(gridModelUserInfo, newEntry, 0);
                    $("#flex1").flexAddData(gridModelUserInfo);
                }

                resetAppUserForm();
                showSuccess(result.message);

            } catch (e) {
                // Do Nothing
            }
        }
        $('#loginId').focus();
    }

    function displayArmsUserEntryErrors(result) {
        var errors = $(result.errors);
        errors.each(function (i) {
            var err = $(this);
            showError("'" + err[0] + "' " + err[1]);
            $('#' + err[0]).addClass('error');
        });
        showLoadingSpinner(false);
        if (result.message) {
            showError(result.message);
        }
    }

    function resetAppUserForm() {
        // reset form for kando validator
        validatorUser.hideMessages();
        // reset kando upload
        $(".k-delete").parent().click();

        clearForm($("#userForm"));
        dropDownEmployee.value('');
        $('#signatureImage').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#editPassMessage').hide();
        $('#retypePassError').hide();
        $('#name').focus();
        var actionUrl = "${createLink(controller: 'appUser',action: 'create')}";
        $("#userForm").attr('action', actionUrl);

        $('#password').attr('required', true);
        $('#confirmPassword').attr('required', true);
    }

    function initFlexUser() {
        $("#flex1").flexigrid
        (
                {
                    url: false,
                    dataType: 'json',
                    colModel: [
                        {display: "Serial", name: "serial", width: 30, sortable: false, align: "right"},
                        {display: "ID", name: "id", width: 30, sortable: true, align: "right", hide: true},
                        {display: "User Name", name: "username", width: 180, sortable: true, align: "left"},
                        {display: "Login ID", name: "loginId", width: 180, sortable: true, align: "left"},
                        {display: "Enabled", name: "enabled", width: 80, sortable: true, align: "center"},
                        {display: "Locked", name: "accountLocked", width: 80, sortable: true, align: "center"},
                        {display: "Expired", name: "accountExpired", width: 80, sortable: true, align: "center"},
                        {display: "Employee", name: "employee", width: 200, sortable: false, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editUser},
                        <sec:access url="/appUser/delete">
                        {name: 'Delete', bclass: 'delete', onpress: deleteUser},
                        </sec:access>
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "User Name", name: "username", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Users List',
                    useRp: true,
                    rp: 15,
                    showTableToggleBtn: false,
                    height: getGridHeight() - 20,
                    customPopulate: customPopulateUserGrid,
                    afterAjax: function (XMLHttpRequest, textStatus) {
                        afterAjaxError(XMLHttpRequest, textStatus);
                        showLoadingSpinner(false);
                    }
                }
        );
    }

    function customPopulateUserGrid(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            gridModelUserInfo = data;
        }
        $("#flex1").flexAddData(gridModelUserInfo);
    }

    function reloadGrid(com, grid) {
        $('#flex1').flexOptions({query: ''}).flexReload();
    }

    function editUser(com, grid) {
        if (executeCommonPreConditionForSelect($('#flex1'), 'user') == false) {
            return;
        }
        var ids = $('.trSelected', grid);
        if (ids.length > 1) {
            showError("Only one user can be edited at a time");
            return;
        }

        var userId = getSelectedIdFromGrid($('#flex1'));

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var url = "${createLink(controller: 'appUser', action: 'select')}?id=" + userId;
        $.ajax({
            url: url,
            success: executePostConForEdit,
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConForEdit(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showUserInfo(data);
        }
    }

    function showUserInfo(data) {
        resetAppUserForm();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#loginId').val(entity.loginId);
        $('#username').val(entity.username);
        $('#existingPass').val(entity.password);
        $('#password').val('');
        $('#cellNumber').val(entity.cellNumber);
        $('#ipAddress').val(entity.ipAddress);
        $('#enabled').attr('checked', entity.enabled);
        dropDownEmployee.value(entity.employeeId);

        $('#accountLocked').attr('checked', entity.accountLocked);
        $('#accountExpired').attr('checked', entity.accountExpired);
        $('#isPowerUser').attr('checked', entity.isPowerUser);
        $('#isConfigManager').attr('checked', entity.isConfigManager);
        $('#isDisablePasswordExpiration').attr('checked', entity.isDisablePasswordExpiration);

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $('#editPassMessage').show();
        var actionUrl = "${createLink(controller: 'appUser',action: 'update')}";
        $("#userForm").attr('action', actionUrl);

        $('#password').removeAttr('required');
        $('#confirmPassword').removeAttr('required');
    }

    function deleteUser(com, grid) {
        var delCount = 0;
        var ids = '';
        $('.trSelected', grid).each(function (e) {
            var id = $(this).attr('id').replace('row', '');
            ids = id;
            delCount++;
        });

        if (executeCommonPreConditionForSelect($('#flex1'), 'user') == false) {
            return;
        }

        if (!confirm('Are you sure you want to delete the selected User?')) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'appUser', action: 'delete')}?id=" + ids,
            success: executePostConditionForDelete,
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            }, // Spinner Hide on AJAX Call
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForDelete(data) {
        if (data.deleted) {
            var selectedRow = null;
            $('.trSelected', $('#flex1')).each(function (e) {
                selectedRow = $(this).remove();
            });
            resetAppUserForm();
            $('#flex1').decreaseCount(1);
            showSuccess(data.message);
            gridModelUserInfo.total = parseInt(gridModelUserInfo.total) - 1;
            removeEntityFromGridRows(gridModelUserInfo, selectedRow);
        } else {
            showError(data.message);
        }
    }

    $('b.top').click
    (
            function () {
                $(this).parent().toggleClass('fh');
            }
    );

</script>