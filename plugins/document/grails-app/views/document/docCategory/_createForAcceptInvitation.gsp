<div class="container-fluid">
    <div class="panel panel-primary">
        <div class="panel-heading">
            <div class="panel-title">
                <div>Accept Invitation For ${result?.categoryLabel} Membership</div>
            </div>
        </div>

        <form onsubmit="" action='${createLink(controller: 'docCategory', action: 'acceptInvitation')}'
              method='POST' id='acceptInvitationForm' class="form-horizontal form-widgets" role="form"
              autocomplete='on'>
            <div class="panel-body">
                <div class="form-group">
                    <label class="col-md-2 control-label" for="name">${result?.categoryLabel} Name:</label>

                    <div class="col-md-10">
                        <span id="name"></span>${result?.category?.name}
                        <input type='hidden' name='code' id='code' value="${result?.code}"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label"
                           for="description">${result?.categoryLabel} Description:</label>

                    <div class="col-md-10">
                        <span id="description">${result?.category?.description}</span>
                    </div>
                </div>

                <div class="form-group" id="subCateContainer">
                    <label class="col-md-2 control-label"
                           for="description">${result?.subCategoryLabel} Details:</label>

                    <div class="col-md-10" id="subCatDiv" style="padding-left: 15px">

                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label"
                           for="loginId">Login ID:</label>

                    <div class="col-md-10">
                        <span id="loginId"></span>${result?.loginId}
                    </div>
                </div>

                <div class="form-group" id="appUserNameDiv">
                    <label class="col-md-2 control-label"
                           for="appUserName">Your Name:</label>

                    <div class="col-md-10">
                        <span id="appUserName"></span>${result?.appUser?.username}
                    </div>
                </div>

                <div class="form-group" id="userNameDiv">
                    <label class="col-md-2 control-label label-required" for="username">Your Name:</label>

                    <div class="col-md-4">
                        <input type="text" class="k-textbox" id="username" name="username" tabindex="1"
                               placeholder="Enter your full name" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="username"></span>
                    </div>
                </div>

                <div class="form-group" id="passwordDiv">
                    <label class="col-md-2 control-label label-required" for="password">Password:</label>

                    <div class="col-md-4">
                        <input type="password" class="k-textbox" id="password" name="password" tabindex="2"
                               placeholder="Letters,Numbers & Special Characters (min 8 characters)" required
                               validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="password"></span>
                    </div>
                </div>

                <div class="form-group" id="confirmPasswordDiv">
                    <label class="col-md-2 control-label label-required"
                           for="confirmPassword">Confirm Password:</label>

                    <div class="col-md-4">
                        <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword"
                               tabindex="3"
                               placeholder="Re-enter password" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="confirmPassword"></span>
                    </div>
                </div>
            </div>

            <div class="panel-footer">
                <button id="create" name="create" type="submit" data-role="button"
                        class="k-button k-button-icontext"
                        role="button" tabindex="4"
                        aria-disabled="false"><span clasisMovedToTrashs="k-icon k-i-plus"></span>Accept
                </button>
                <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                        class="k-button k-button-icontext" role="button" tabindex="5"
                        aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
                </button>
            </div>
        </form>
    </div>

    <div>
        <g:if test="${flash.message && !flash.success}">
            <div class='alert alert-danger col-md-12' id="login_msg_">${flash.message}</div>
        </g:if>
        <g:if test="${flash.message && flash.success}">
            <div class='alert alert-success col-md-12' id="login_msg_">${flash.message}</div>
        </g:if>
    </div>
</div>
