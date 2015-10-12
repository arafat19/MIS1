<script type="text/javascript">
    var dropDownRole, dropDownModule;

    $(document).ready(function () {
        onLoadRequestMapPage();
    });

    function onLoadRequestMapPage() {

        // initialize form with kendo validator & bind onSubmit event
        initializeForm($("#frmSearchRole"), onSubmitFrmSearchRole);

        $('#frmSaveAttribute').submit(function (e) {
            onSubmitFrmSaveAttribute();
            return false;
        });

        var output = ${output ? output : ''};
        if (output.isError) {
            showError(output.message);
        }
        // SET HEIGHT OF ListBox
        var listHeight = getGridHeight()-55;
        $('#availableAttributes').css('height', listHeight);
        $('#selectedAttributes').css('height', listHeight);

        // update page title
        $(document).attr('title', "MIS - Role-Right Mapping");
        loadNumberedMenu(MENU_ID_APPLICATION, "#requestMap/show");
    }


    function onSubmitFrmSearchRole() {

        var roleId = dropDownRole.value();
        var pluginId = dropDownModule.value();

        if (!validateForm($("#frmSearchRole"))) {
            return false;
        }

        var url = "${createLink(controller: 'requestMap',action: 'select')}?roleId=" + roleId + '&pluginId=' + pluginId;
        jQuery.ajax({
            type: 'post',
            url: url,
            success: function (data, textStatus) {
                executePostConditionForSearchRole(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForSearchRole(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        $('#hidRole').val(dropDownRole.value());
        $('#hidPluginId').val(dropDownModule.value());

        $('#availableAttributes').refreshDropDown(data.lstAvailableFeatures, {textMember: 'feature_name', unselectedValue: false});
        $('#selectedAttributes').refreshDropDown(data.lstAssignedFeatures, {textMember: 'feature_name', unselectedValue: false});


    }
    function onSubmitFrmSaveAttribute() {

        if (($("#selectedAttributes option").size() == 0) && ($("#availableAttributes option").size() == 0)) {
            showError('There is no available/assigned rights to save');
            return false;
        }
        var assignedFeatureIds = '';

        $("#selectedAttributes option").each(function () {
            assignedFeatureIds += $(this).attr('value') + '_';
        });

        var roleId = $('#hidRole').val();
        var pluginId = $('#hidPluginId').val();
        var url = "${createLink(controller: 'requestMap',action: 'update')}?assignedFeatureIds=" + assignedFeatureIds + '&roleId=' + roleId + '&pluginId=' + pluginId;

        showLoadingSpinner(true);
        jQuery.ajax({
            type: 'post',
            url: url,
            success: function (data, textStatus) {
                executePostConditionForUpdate(data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function (XMLHttpRequest, textStatus) {
                showLoadingSpinner(false);
            },
            dataType: 'json'
        });
    }

    function executePostConditionForUpdate(data) {
        if (data.isError) {
            showError(data.message);
        } else {
            showSuccess(data.message);
        }
    }


    function resetForm() {
        clearForm($("#requestMapForm"),$('#name'));
        $('#selectedRole').refreshDropDown('', {textMember: 'authority', unselectedValue: false});
        $('#role').refreshDropDown(roleListModel.roleList, {textMember: 'authority', unselectedValue: false});
        $('#create').attr('value', 'Create');
    }


    function addDataToSelectedRole() {
        var selectedSize = $("#availableAttributes > option:selected").size();
        if (selectedSize <= 0) {
            showError('Please select a right to add');
            return false;
        }
        $("#availableAttributes > option:selected").each(function () {
            $(this).remove().appendTo("#selectedAttributes");
        });
        return false;
    }

    function addAllDataToSelectedRole() {
        $("#availableAttributes > option").each(function () {
            $(this).remove().appendTo("#selectedAttributes");
        });
        return false;
    }

    function removeDataFromSelectedRole() {
        var selectedSize = $("#selectedAttributes > option:selected").size();
        if (selectedSize <= 0) {
            showError('Please select a right to remove');
            return false;
        }
        $("#selectedAttributes > option:selected").each(function () {
            $(this).remove().appendTo("#availableAttributes");
        });
        return false;
    }
    function removeAllDataFromSelectedRole() {
        $("#selectedAttributes > option").each(function () {
            $(this).remove().appendTo("#availableAttributes");
        });
        return false;
    }


    function discardChanges() {
        var hidRoleId = $('#hidRole').val();
        var hidPluginName = $('#hidPluginId').val();
        if ((hidRoleId == '') || (hidPluginName == '')) {
            showError('No changes found to discard');
            return false;
        }
        dropDownRole.value(hidRoleId);
        $('#pluginId').val(hidPluginName);
        onSubmitFrmSearchRole();
        return false;
    }


    function authenticateRequestMapReset() {
        dropDownRole.enable(false);
        if(!validateForm($("#frmSearchRole"))){
            return false;
        }

        $('#appResetAllRolesConfirmationModal').modal('show');    // show Modal

        dropDownRole.enable(true);
        return false;
    }

    function checkPassword(tempPass) {
        if (tempPass.length == 0) return false;
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        var actionUrl = "${createLink(controller: 'appUser', action: 'checkPassword')}?pass=" + tempPass;
        jQuery.ajax({
            type: 'post',
            url: actionUrl,
            success: function (data, textStatus) {
                executePostConditionToCheckPassword(data);
            },
            complete: onCompleteAjaxCall, // Spinner Hide on AJAX Call
            dataType: 'json'
        });
    }

    function executePostConditionToCheckPassword(data) {
        if (data.isError) {
            showError(data.message);
            return false;
        }
        resetDefaultRequestMap();
    }

    function resetDefaultRequestMap() {
        var pluginId = $('#pluginId').val();
        showLoadingSpinner(true);	// Spinner Show on AJAX Call
        $.ajax({
            url: "${createLink(controller:'requestMap', action:'resetRequestMap')}?pluginId=" + pluginId,
            success: executePostConditionForResetReqMap,
            complete: onCompleteAjaxCall,
            dataType: 'json',
            type: 'post'
        });
    }

    function executePostConditionForResetReqMap(data) {
        if (data.isError == true) {
            showError(data.message);
        } else {
            //showSuccess(data.message);
            alert('Application Role-Right Mapping Successfully Reset to Default. \nPlease Login Again.');
            var logoutLink = "${createLink(controller:'logout', action:'index')}";
            document.location = logoutLink;
        }
    }

    function onSubmitResetReqMapConfirmation() {
        var password = $('#txtAppPassword').val();
        if (password == '') {
            showError("Please write down legal password");
            return false;
        }else {
            var regPass = /^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$/;
            if (!regPass.test(password)) {
                showError('Invalid Authentication');
            } else {
                checkPassword(password);
                $('#appResetAllRolesConfirmationModal').modal('hide');
            }
        }
        return false;
    }

    function exitResetReqMapConfirmForm() {
        $('#txtAppPassword').val();
        $('#appResetAllRolesConfirmationModal').modal('hide');
        return false;
    }

</script>
