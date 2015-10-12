<div id="application_top_panel" class="panel panel-primary">
<div class="panel-heading">

    <div class="panel-title">
        Create Your Account
    </div>

</div>
<form name="customerForm" id="customerForm" action="${createLink(controller: 'exhCustomer', action: 'signup')}"
      enctype="multipart/form-data" method="post" class="form-horizontal form-widgets" role="form">
<div class="panel-body">
<g:hiddenField name="agentId" value="0"/>
<g:hiddenField name="existingPass" value=""/>
<g:hiddenField name="companyId"/>
<div class="col-md-6">
    <div class="form-group">
        <label class="col-md-4 control-label label-optional">Exchange House:</label>

        <div class="col-md-8">
            <span id="companyName" name="companyName"></span>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="name">Name:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                   required validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="name"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="countryId">Nationality:</label>

        <div class="col-md-5">

            <select name="countryId" id="countryId" required="true"
                    onchange="checkVisaExpire()" validationMessage="Required" tabindex="3">

            </select>

        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="countryId"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="visaExpireDate"
               id="lblVisaExpireDate">Visa Expiry Date:</label>

        <div class="col-md-5">
            <app:dateControl name="visaExpireDate" tabindex="4" placeholder="dd/MM/yyyy" required="true"
                             value="" validationMessage="Required">
            </app:dateControl>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="visaExpireDate"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required"
               for="customerDateOfBirth">Date of Birth:</label>

        <div class="col-md-5">

            <app:dateControl name="customerDateOfBirth" tabindex="5" placeholder="dd/MM/yyyy" required="true"
                             value="" validationMessage="Required">
            </app:dateControl>

        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="customerDateOfBirth"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="phone">Phone:</label>

        <div class="col-md-5">
            <div class="input-group">
                <span class="input-group-addon" style="padding: 3px" id="isdCode"></span>
                <input type="text" class="k-textbox" id="phone" name="phone" tabindex="6"
                       required validationMessage="Required"/>
            </div>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="phone"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="postCode">Post Code:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="postCode" name="postCode" tabindex="7"
                   required validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="postCode"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="loginId">Login ID:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="loginId" name="loginId" tabindex="8"
                   required validationMessage="Required" placeholder="example@domain.com"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="loginId"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="password">Password:</label>

        <div class="col-md-5">
            <input type="password" class="k-textbox" id="password" name="password" tabindex="9"
                   pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                   required validationMessage="Invalid combination/length"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="password"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="confirmPassword">Confirm Password:</label>

        <div class="col-md-5">
            <input type="password" class="k-textbox" id="confirmPassword" name="confirmPassword" tabindex="10"
                   pattern="^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\W]))|((?=.*[a-z])(?=.*[\d])(?=.*[\W]))|((?=.*[A-Z])(?=.*[\d])(?=.*[\W]))).*$"
                   required validationMessage="Invalid combination/length"/>
            <span id='retypePassError' class='error' style='display:none;'>Password mismatch</span>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="confirmPassword"></span>
        </div>
    </div>

    <div class="form-group">
        <div class="col-md-4"></div>
        <span class="col-md-8">Password Hints: Min 8 characters and combination of letters, numbers and special characters.</span>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional"
               for="smsSubscription">SMS Subscription:</label>

        <div class="col-md-5">
            <input type="checkbox" id="smsSubscription" name="smsSubscription" tabindex="11"/>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional"
               for="mailSubscription">Mail Subscription:</label>

        <div class="col-md-5">
            <input type="checkbox" id="mailSubscription" name="mailSubscription" tabindex="12"/>
        </div>
    </div>

</div>

<div class="col-md-6">
    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="address">Address:</label>

        <div class="col-md-5">
            <textarea tabindex="13" class='required k-textbox'
                      id="address" name="address" rows="3" required validationMessage="Required"></textarea>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="address"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="sourceOfFund">Source of Fund:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="sourceOfFund" name="sourceOfFund" tabindex="14"
                   required validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="sourceOfFund"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="photoIdTypeId">Photo ID Type:</label>

        <div class="col-md-5">

            <select name="photoIdTypeId" id="photoIdTypeId" required="true" validationMessage="Required"
                    tabindex="15">

            </select>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="photoIdTypeId"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="photoIdNo">Photo ID No.:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="photoIdNo" name="photoIdNo" tabindex="16" required
                   validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="photoIdNo"></span>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required"
               for="photoIdImage" id="lblPhotoId">Photo ID Image:</label>

        <div class="col-md-5">
            <input type="file" id="photoIdImage" name="photoIdImage" validationMessage="Required" tabindex="17"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="photoIdImage"></span>
        </div>

    </div>

    <div class="form-group">
        <div class="col-md-4">

        </div>

        <div class="col-md-5">
            <input type="checkbox" id="sendByEmail" name="sendByEmail" tabindex="18"/>
            <span>I will send photo ID through e-mail</span>

        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required"
               for="customerPhotoIdExpiryDate">Photo ID Expiry Date:</label>

        <div class="col-md-5">
            <app:dateControl name="customerPhotoIdExpiryDate" tabindex="19" placeholder="dd/MM/yyyy" required="true"
                             validationMessage="Required" value="">
            </app:dateControl>

        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="customerPhotoIdExpiryDate"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional"
               for="isCorporate">Corporate Customer:</label>

        <div class="col-md-5">
            <input type="checkbox" id="isCorporate" name="isCorporate" tabindex="20"/>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional" id="lblCompanyRegNo"
               for="companyRegNo">Company Reg No.:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="companyRegNo" name="companyRegNo" validationMessage="required"
                   tabindex="21"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="companyRegNo"></span>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional" id="lblDateOfIncorporation"
               for="dateOfIncorporation">Date of Incorporation:</label>

        <div class="col-md-5">
            <app:dateControl name="dateOfIncorporation" validationMessage="Required" tabindex="22"
                             disabled="true" value="" placeholder="dd/MM/yyyy">
            </app:dateControl>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="dateOfIncorporation"></span>
        </div>

    </div>


    <div class="form-group">
        <div class="col-md-4"></div>

        <div class="col-md-5">
            <jcaptcha:jpeg style=" border: 1px solid #B6C7D8; margin-left: 4px; width: 140px;"
                           name="image"/>
        </div>

    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="captcha">Type Security Text:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="captcha" name="captcha" tabindex="23"
                   required validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="captcha"></span>
        </div>
    </div>

</div>

</div>

<div class="panel-footer">

    <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
            role="button" tabindex="24"
            aria-disabled="false"><span class="k-icon k-i-plus"></span>Sign Up
    </button>
    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
            class="k-button k-button-icontext" role="button" tabindex="25"
            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Reset
    </button>
</div>
</form>
</div>