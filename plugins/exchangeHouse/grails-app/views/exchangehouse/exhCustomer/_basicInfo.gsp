<%@ page import="com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<form name="basicInfoForm" id="basicInfoForm" class="form-horizontal form-widgets" role="form" method="post">
    <div class="panel-body">
        <g:hiddenField name="id"/>
        <g:hiddenField name="version"/>
        <g:hiddenField name="hidName"/>
        <g:hiddenField name="agentId" value="0"/>
        <div class="col-md-6">
            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="name">Name:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="name"></span>
                </div>
            </div>

            <div class="form-group">

                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_SURNAME_REQUIRED}" value="0">
                    <label class="col-md-4 control-label label-optional" for="surname">Surname:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="surname" name="surname" tabindex="2"/>
                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_SURNAME_REQUIRED}" value="1">
                    <label class="col-md-4 control-label label-required" for="surname">Surname:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="surname"
                               required validationMessage="Required" name="surname" tabindex="2"/>
                    </div>
                </exh:checkSysConfig>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="surname"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="countryId">Nationality:</label>

                <div class="col-md-5">
                    <app:dropDownCountry
                            name="countryId"
                            required="true"
                            validationMessage="Required"
                            dataModelName="dropDownCountry"
                            textMember="nationality"
                            onchange="checkVisaExpire()"
                            tabindex="3">
                    </app:dropDownCountry>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="countryId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="postCode">Post Code:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="postCode" name="postCode" tabindex="4"
                           required validationMessage="Required"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="postCode"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="customerDateOfBirth">Date of Birth:</label>

                <div class="col-md-5">
                    <app:dateControl name="customerDateOfBirth" tabindex="5" value="" required="true"
                                     placeholder="dd/MM/yyyy" validationMessage="Required" id="customerDateOfBirth">
                    </app:dateControl>

                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="customerDateOfBirth"></span>
                </div>
            </div>

        </div>

        <div class="col-md-6">

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="genderId">Gender:</label>

                <div class="col-md-5">
                    <app:dropDownSystemEntity
                            tabindex="6"
                            name="genderId"
                            dataModelName="dropDownGender"
                            required="true"
                            validationMessage="Required"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_GENDER}"
                            showHints="false">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="genderId"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="address">Address:</label>

                <div class="col-md-5">
                    <textarea tabindex="7" class='required k-textbox'
                              id="address" name="address" rows="2" required validationMessage="Required"></textarea>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="address"></span>
                </div>
            </div>


            <div class="form-group">
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_ADDRESS_VERIFICATION_REQUIRED}"
                                    value="0">
                    <label class="col-md-4 control-label label-optional"
                           for="addressVerifiedBy">Address Verified:</label>

                    <div class="col-md-5">
                        <input type="checkbox" id="addressVerifiedBy" name="addressVerifiedBy" tabindex="8"/>

                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_CUSTOMER_ADDRESS_VERIFICATION_REQUIRED}"
                                    value="1">
                    <label class="col-md-4 control-label label-required"
                           for="addressVerifiedBy">Address Verified:</label>

                    <div class="col-md-5">
                        <input type="checkbox" id="addressVerifiedBy" name="addressVerifiedBy" tabindex="8" required
                               validationMessage="Required"/>

                    </div>
                </exh:checkSysConfig>
                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="addressVerifiedBy"></span>
                </div>
            </div>

            <div class="form-group">
                <label  <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_VERIFY_CUSTOMER_SANCTION}" value="1">
                    class="col-md-4 control-label label-required"
                </exh:checkSysConfig>
                    <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_VERIFY_CUSTOMER_SANCTION}" value="0">
                        class="col-md-4 control-label label-optional"
                    </exh:checkSysConfig>
                        for="isSanctionException">Sanction Verified:</label>

                <div class="col-md-8">
                    <input type="checkbox" id="isSanctionException" name="isSanctionException" tabindex="9"/>
                    <span id="sanctionVerifierNameDisplay"></span>
                </div>
            </div>

        </div>
    </div>
</form>