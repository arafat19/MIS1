<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Company User
        </div>
    </div>

    <g:form name='companyUserForm' id="companyUserForm" enctype="multipart/form-data"
            class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <g:hiddenField name="existingPass"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="loginId">Login ID:</label>

                        <div class="col-md-6">
                            <input type="email" class="k-textbox" id="loginId" name="loginId"
                                   placeholder="Unique Login ID (email)" required data-required-msg="Required"
                                   validationMessage="Invalid email" autofocus tabindex="1"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="loginId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="username">User Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="username" name="username" tabindex="2"
                                   placeholder="User Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="username"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="password">Password:</label>

                        <div class="col-md-6">
                            <input type="password" class="k-textbox" id="password" name="password"
                                   pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                   placeholder="Letters,Numbers & Special Characters" required tabindex="3"
                                   data-required-msg="Required" validationMessage="Invalid Combination or Length"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="password"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="confirmPassword">Confirm Password:</label>

                        <div class="col-md-6">
                            <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword"
                                   tabindex="4"
                                   pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                                   placeholder="Confirm password" required data-required-msg="Required"
                                   validationMessage="Invalid Combination or Length"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="confirmPassword"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="cellNumber">Cell Number:</label>

                        <div class="col-md-6">
                            <input type="tel" class="k-textbox" id="cellNumber" name="cellNumber" pattern="\d{11}"
                                   placeholder="Mobile Number" validationMessage="Invalid phone No." tabindex="5"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="cellNumber"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="ipAddress">IP Address:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="ipAddress" name="ipAddress" tabindex="6"
                                   pattern="((^|\.)((25[0-5])|(2[0-4]\d)|(1\d\d)|([1-9]?\d))){4}$"
                                   placeholder="IP Address" validationMessage="Invalid IP"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="ipAddress"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="companyId">Company:</label>

                        <div class="col-md-6">
                            <app:dropDownCompany
                                    required="true"
                                    tabindex="7"
                                    validationMessage="Required"
                                    dataModelName="dropDownCompany"
                                    name="companyId">
                            </app:dropDownCompany>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="companyId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="enabled">Enabled:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="8" class="form-control-static" name="enabled"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="enabled"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="accountLocked">Account Locked:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="9" class="form-control-static" name="accountLocked"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accountLocked"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="accountExpired">Account Expired:</label>

                        <div class="col-md-6">
                            <g:checkBox tabindex="10" class="form-control-static" name="accountExpired"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accountExpired"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="signatureImage">Signature Image:</label>

                        <div class="col-md-6">
                            <input type="file" tabindex="11" class="form-control-static" id="signatureImage"
                                   name="signatureImage"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="signatureImage"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="12"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="13"
                    aria-disabled="false" onclick='resetCompanyUserForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>


