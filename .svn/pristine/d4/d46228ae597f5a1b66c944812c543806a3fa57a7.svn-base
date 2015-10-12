<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            <span id="categoryLabel"></span> Details
        </div>
    </div>

    <form id='categoryForm' name='categoryForm' class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-12">
                    <div class="form-group">
                        <label class="col-md-2 control-label" for="name">Name:</label>

                        <div class="col-md-10">
                            <span id="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label" for="description">Description:</label>

                        <div class="col-md-10">
                            <span id="description"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label" for="url">Link:</label>

                        <div class="col-md-10">
                            <span id="url"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<div class="container-fluid">
    <div class="row">
        <h4>Want to be a member?&nbsp;<a style="cursor: pointer" onclick="showDivForApplyMember();">Apply now!</a></h4>
    </div>

    <div class="row panel panel-primary" id="applyForMembershipDiv" style="display: none">
        <div class="panel-heading">
            <div class="panel-title">
                <span></span> Enter your login information</div>
        </div>

        <form onsubmit=""
              action='${createLink(controller: 'docMemberJoinRequest', action: 'applyForMembership')}'
              method='POST' id='applyForm' class="form-horizontal form-widgets" role="form"
              autocomplete='on'>
            <input type="hidden" name="categoryId" id="categoryId"/>

            <div class="panel-body">
                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="email">Email:</label>

                    <div class="col-md-4">
                        <input type="email" class="k-textbox" id="email" name="email"
                               placeholder="yourmail@domain.com" required data-required-msg="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="email"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="userName">Name:</label>

                    <div class="col-md-4">
                        <input type="text" class="k-textbox" id="userName" name="userName"
                               placeholder="Enter your full name" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="userName"></span>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-md-2">&nbsp;</div>

                    <div class="col-md-4">
                        <jcaptcha:jpeg name="image"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="captcha">Security ID:</label>

                    <div class="col-md-4">
                        <input type="text" class="k-textbox" id="captcha" name="captcha"
                               value="" required validationMessage="Required"/>
                    </div>
                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="captcha"></span>
                    </div>
                </div>
            </div>

            <div class="panel-footer">
                <button id="create" name="create" type="submit" data-role="button"
                        class="k-button k-button-icontext"
                        role="button" tabindex="4"
                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Apply
                </button>
                <button id="closeForm" name="closeForm" type="button" data-role="button"
                        class="k-button k-button-icontext" role="button" tabindex="4"
                        aria-disabled="false" onclick='hideDivForApplyMember();'><span
                        class="k-icon k-i-close"></span>Cancel
                </button>
            </div>
        </form>
    </div>

    <div class="row">
        <g:if test="${flash.message && !flash.success}">
            <div class='alert alert-danger col-md-12' id="login_msg_">${flash.message}</div>
        </g:if>
        <g:if test="${flash.message && flash.success}">
            <div class='alert alert-success col-md-12'
                 id="login_msg_">${flash.message}</div>
        </g:if>
    </div>
</div>


