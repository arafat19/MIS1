<script type="text/javascript">

    var validatorCompanyUser, dropDownCompany, uploading, output, gridModelUserInfo;

    $(document).ready(function () {
        $('#confirmPassword').keypress(function (event) {
            $('#retypePassError').hide();
        });

        onLoadAppUser();
    });

    function onLoadAppUser() {
        validatorCompanyUser = $("#companyUserForm").kendoValidator({
            validateOnBlur: false
        }).data("kendoValidator");

        $("#signatureImage").kendoUpload({
            multiple: false
        });

        output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        }

        gridModelUserInfo = output.gridObject;

        bindUserFormEvents();
        initFlexUser();
        setUrlUserGrid();

        // update page title
        $(document).attr('title', "MIS - Company User");
        loadNumberedMenu(MENU_ID_APPLICATION, "#appUser/showForCompanyUser");
    }

    function bindUserFormEvents() {
        var actionUrl = "${createLink(controller: 'appUser',action: 'createForCompanyUser')}";
        $("#companyUserForm").attr('action', actionUrl);

        $('#companyUserForm').iframePostForm({
            post: function () {
                uploading = true;
                showLoadingSpinner(true);
                setButtonDisabled($('#create'), true);
            },
            complete: function (response) {
                if (uploading == true) {
                    showLoadingSpinner(false);
                    onSaveUser(response);
                    uploading = false;
                    setButtonDisabled($('#create'), false);
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

    function setUrlUserGrid() {
        var strUrl = "${createLink(controller: 'appUser', action: 'listForCompanyUser')}";
        $("#flex1").flexOptions({url: strUrl});
        if (gridModelUserInfo) {
            $('#flex1').flexAddData(gridModelUserInfo);
        }
    }

    function executePreConForSubmitUser() {

        if (!confirm('After creating a new company user all cache utility will be re-pulled. ' +
                'Please, make sure that all application related activities are stopped during this period of time.')) {
            return false;
        }

        clearErrors($("#companyUserForm"));
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

        if (!validateForm($("#companyUserForm"))) {
            return false;
        }
        // now check two password fields
        if (checkPasswordMatch() == false) return false;

        return true;
    }

    function checkPasswordMatch() {
        if ($("#password").val() != $("#confirmPassword").val()) {
            showError("Password mismatched");
            $("#confirmPassword").focus();
            return false;
        } else {
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

                resetCompanyUserForm();
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

    function resetCompanyUserForm() {
        // reset kando upload
        $(".k-delete").parent().click();

        clearForm($("#companyUserForm"), $('#name'));
        $('#signatureImage').val('');
        $('#create').html("<span class='k-icon k-i-plus'></span>Create");
        $('#editPassMessage').hide();
        $('#retypePassError').hide();
        $('#companyId').removeAttr('disabled');
        var actionUrl = "${createLink(controller: 'appUser',action: 'createForCompanyU-ser')}";
        $("#companyUserForm").attr('action', actionUrl);

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
                        {display: "Login ID", name: "loginId", width: 210, sortable: true, align: "left"},
                        {display: "Enabled", name: "enabled", width: 100, sortable: true, align: "center"},
                        {display: "Locked", name: "accountLocked", width: 100, sortable: true, align: "center"},
                        {display: "Expired", name: "accountExpired", width: 100, sortable: true, align: "center"},
                        {display: "Company", name: "companyId", width: 210, sortable: true, align: "left"}
                    ],
                    buttons: [
                        {name: 'Edit', bclass: 'edit', onpress: editUser},
                        %{--<sec:access url="/appUser/deleteForCompanyUser">
                        {name: 'Delete', bclass: 'delete', onpress: deleteUser},
                        </sec:access>--}%
                        {name: 'Clear Results', bclass: 'clear-results', onpress: reloadGrid},
                        {separator: true}
                    ],
                    searchitems: [
                        {display: "User Name", name: "username", width: 180, sortable: true, align: "left"},
                        {display: "Company", name: "company.name", width: 180, sortable: true, align: "left"}
                    ],
                    sortname: "id",
                    sortorder: "desc",
                    usepager: true,
                    singleSelect: true,
                    title: 'All Company Users List',
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
        var ids = $('.trSelected', grid);
        if (executeCommonPreConditionForSelect($('#flex1'), 'company user') == false) {
            return;
        }
        if (ids.length > 1) {
            showError("Only one company user can be edited at a time");
            return;
        }

        var userId = getSelectedIdFromGrid($('#flex1'));

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var url = "${createLink(controller: 'appUser', action: 'selectForCompanyUser')}?id=" + userId;
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
        resetCompanyUserForm();
        var entity = data.entity;
        $('#id').val(entity.id);
        $('#version').val(data.version);
        $('#loginId').val(entity.loginId);
        $('#username').val(entity.username);
        $('#existingPass').val(entity.password);
        $('#password').val('');
        $('#cellNumber').val(entity.cellNumber);
        $('#enabled').attr('checked', entity.enabled);

        dropDownCompany.value(entity.companyId);

        $('#ipAddress').val(entity.ipAddress);
        $('#accountLocked').attr('checked', entity.accountLocked);
        $('#accountExpired').attr('checked', entity.accountExpired);
        $('#companyId').attr('disabled', 'disabled');

        $('#create').html("<span class='k-icon k-i-plus'></span>Update");
        $('#editPassMessage').show();
        var actionUrl = "${createLink(controller: 'appUser',action: 'updateForCompanyUser')}";
        $("#companyUserForm").attr('action', actionUrl);

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

        if (ids.length == 0) {
            showError("Please select a company user to delete");
            return;
        }

        if (!confirm('Are you sure you want to delete the selected company user?')) {
            return;
        }

        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller: 'appUser', action: 'deleteForCompanyUser')}?id=" + ids,
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
            resetCompanyUserForm();
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
