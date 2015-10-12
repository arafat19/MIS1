<div id="customerDetails" style="display: none;">
    <div id="application_top_panel" class="panel panel-primary">

        <div class="panel-heading">
            <div class="panel-title">
                Customer Details
            </div>
        </div>

        <div class="panel-body">

            <div class="row">
                <label class="col-md-2">Name:</label>

                <div class="col-md-4">
                    <span id='customerName'></span>
                </div>
                <label class="col-md-2 ">Address:</label>

                <div class="col-md-4">
                    <span id='address'></span>
                </div>
            </div>

            <div class="row">
                <label class="col-md-2 ">Nationality:</label>

                <div class="col-md-4">
                    <span id='nationality'></span>
                </div>
                <label class="col-md-2">Phone:</label>

                <div class="col-md-4">
                    <span id='phone'></span>
                </div>
            </div>


            <div class="row">
                <label class="col-md-2">Date Of Birth:</label>

                <div class="col-md-4">
                    <span id='dateOfBirth'></span>
                </div>
                <label class="col-md-2">Email:</label>

                <div class="col-md-4">
                    <span id='email'></span>
                </div>
            </div>

            <div class="row">
                <label class="col-md-2">Post Code:</label>

                <div class="col-md-4">
                    <span id='postCode'></span>
                </div>
                <label class="col-md-2">Source of Fund:</label>

                <div class="col-md-4">
                    <span id='sourceOfFund'></span>
                </div>
            </div>


            <div class="row">
                <label class="col-md-2">Photo ID Type:</label>

                <div class="col-md-4">
                    <span id='photoIdType'></span>
                </div>
                <label class="col-md-2">Photo ID No:</label>

                <div class="col-md-4">
                    <span id='photoIdNo'></span>
                </div>
            </div>

            <div class="row">
                <label class="col-md-2 ">Photo Id Expiry Date:</label>

                <div class="col-md-4">
                    <span id='photoIdExpiryDate'></span>
                </div>

            </div>
        </div>


        <div class="panel-heading">
            <div class="panel-title">
                Create User Account
            </div>
        </div>

        <form id="customerUserForm" class="form-horizontal form-widgets" role="form">
            <div class="panel-body">

               %{-- <g:hiddenField name="customerId"/>--}%
                <g:hiddenField name="username"/>
                <g:hiddenField name="phoneNo"/>
                <g:hiddenField name="role.id"/>


                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="loginId">Login ID:</label>
                    <div class='col-md-4'>
                        <input type="text" tabindex="3" class="k-textbox"
                               required validationMessage="Required"
                               name="loginId"
                               maxlength="100"
                               id="loginId"
                               value=""/>
                    </div>
                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="loginId"></span>
                    </div>
                    <div class="col-md-3">
                        <span>e.g. example@domain.com</span>

                    </div>
                </div>


                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="password">Password:</label>
                    <div class="col-md-4">
                        <input type="password" tabindex="4" class="k-textbox"
                               required validationMessage="Required"
                               name='password'
                               maxlength="100"
                               id="password"
                               value=""/>
                    </div>
                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="password"></span>
                    </div>
                </div>


                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="confirmPassword">Confirm Password:</label>
                    <div class="col-md-4">
                        <input type="password" tabindex="5" class="k-textbox"
                               class='required'
                               maxlength="100"
                               name='confirmPassword'
                               id="confirmPassword"
                               required validationMessage="Required"
                               value=""/>
                    </div>
                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="confirmPassword"></span>
                    </div>
                    <div class="col-md-3">
                        <span id='retypePassError'>Password mismatch</span>
                    </div>
                </div>


                <div class="form-group">
                    <label class="col-md-2" for="setDefaultPassword">Set Default Password:</label>

                    <div class="col-md-4">
                        <input type="checkbox" tabindex="6"
                               class='checkbox'
                               name='setDefaultPassword'
                               id="setDefaultPassword"
                               value=""/>
                    </div>

                </div>

                <div>
                    <span>Password Hints: Min 8 characters & combination of letters, numbers and special characters.</span>
                </div>
            </div>

            <div class="panel-footer">

                <button id="btnCreateCustomerUser" name="btnCreateCustomerUser" type="submit" data-role="button" class="k-button k-button-icontext"
                        role="button" tabindex="7"
                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                </button>

                <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                        class="k-button k-button-icontext" role="button" tabindex="8"
                        aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
                </button>

            </div>
        </form>

    </div>
</div>