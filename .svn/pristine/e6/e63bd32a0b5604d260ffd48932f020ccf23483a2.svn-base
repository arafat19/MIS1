<script type="text/javascript">
    $(document).ready(function () {
        initializeForm('#passwordForm', onSubmitPassword);
        // update page title
        $('span.headingText').html('Change Password');
        $('#icon_box').attr('class', 'pre-icon-header manage-password');
        $(document).attr('title', "MIS - Change Password");
        loadNumberedMenu(currentMenu, null);
    });


    function checkPasswordMatch() {
        if ($("#newPassword").val() != $("#retypePassword").val()) {
            showError('Retyped password mismatched');
            return false;
        }
    }
    function executePreCondition() {
        if (!validateForm($("#passwordForm"))) {
            return false;
        }
        return true;
    }

    function onSubmitPassword() {
        if (executePreCondition() == false) {
            return false;
        }
        if (checkPasswordMatch() == false) return false;

        showLoadingSpinner(true);	// Spinner Show on AJAX Call

        jQuery.ajax({
            type: 'post',
            data: jQuery('#passwordForm').serialize(),
            url: '${createLink(controller: 'appUser', action: 'changePassword')}',
            success: executePostForSavePassword,
            complete: onCompleteAjaxCall,
            dataType: 'json'
        });
        return false;
    }

    function executePostForSavePassword(result) {
        if (result.isError) {
            showError(result.message);
        } else {
            showSuccess(result.message);
            clearFormPassword();
        }
    }
    function clearFormPassword() {
        clearForm('#passwordForm');
    }
</script>


<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Enter Password
        </div>
    </div>
    <form id="passwordForm" name="passwordForm" class="form-horizontal form-widgets" role="form" action="save">
        <div class="panel-body">
            <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="oldPassword">Old Password:</label>

                    <div class="col-md-4">
                        <input type="password" class="k-textbox" id="oldPassword" name="oldPassword"
                               placeholder="Old Password" required validationMessage="Required"
                               autofocus/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="oldPassword"></span>
                    </div>
            </div>

            <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="newPassword">New Password:</label>
                    <div class="col-md-4">
                        <input type="password" class="k-textbox" id="newPassword" name="newPassword"
                               pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                               placeholder="Letters,Numbers & Special Characters" required
                               data-required-msg="Required"
                               validationMessage="Invalid Combination or Length"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="newPassword"></span>
                    </div>
            </div>
            <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="retypePassword">Retype Password:</label>
                    <div class="col-md-4">
                        <input type="password" class="k-textbox" id="retypePassword" name="retypePassword"
                               pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                               placeholder="Must be same as new password" required data-required-msg="Required"
                               validationMessage="Invalid Combination or Length"/>
                    </div>
                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="retypePassword"></span>
                    </div>
            </div>
        </div>
        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Change Password
            </button>
            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='clearFormPassword();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
