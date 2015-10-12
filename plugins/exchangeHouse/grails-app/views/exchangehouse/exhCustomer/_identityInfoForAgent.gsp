<%@ page import="com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf" %>
<form name="identityInfoForm" id="identityInfoForm" class="form-horizontal form-widgets" role="form" method="post">
    <div class="panel-body">
        <div class="col-md-6">

            <div class="form-group">
                <label class="col-md-4 control-label label-required" for="photoIdTypeId">Photo ID Type:</label>

                <div class="col-md-5">
                    <exh:dropDownPhotoIdType
                            dataModelName="dropDownPhotoIdType" name="photoIdTypeId"
                            required="true"
                            tabindex="1">

                    </exh:dropDownPhotoIdType>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="photoIdTypeId"></span>
                </div>
            </div>

            <div class="form-group">
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_PHOTO_ID_NO_REQUIRED}" value="0">
                    <label class="col-md-4 control-label label-optional" for="photoIdNo">Photo ID No.:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="photoIdNo" name="photoIdNo" tabindex="2"/>
                    </div>
                </exh:checkSysConfig>
                <exh:checkSysConfig key="${ExhSysConfigurationIntf.EXH_PHOTO_ID_NO_REQUIRED}" value="1">
                    <label class="col-md-4 control-label label-required" for="photoIdNo">Photo ID No.:</label>

                    <div class="col-md-5">
                        <input type="text" class="k-textbox" id="photoIdNo" pattern="^\w{2,20}$"
                               required validationMessage="Required" name="photoIdNo" tabindex="2"/>
                    </div>
                </exh:checkSysConfig>
                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="photoIdNo"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-optional"
                       for="customerPhotoIdExpiryDate">Photo ID Expiry Date:</label>

                <div class="col-md-5">
                    <app:dateControl name="customerPhotoIdExpiryDate" tabindex="3" placeholder="dd/MM/yyyy" value="">
                    </app:dateControl>

                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-required" id="lblVisaExpireDate"
                       for="visaExpireDate">Visa Expiry Date:</label>

                <div class="col-md-5">
                    <app:dateControl name="visaExpireDate" tabindex="4" required="true"
                                     placeholder="dd/MM/yyyy" validationMessage="Required"
                                     value="">
                    </app:dateControl>

                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="visaExpireDate"></span>
                </div>
            </div>

        </div>

        <div class="col-md-6">

            <div class="form-group">
                <label class="col-md-4 control-label label-optional"
                       for="isCorporate">Corporate Customer:</label>

                <div class="col-md-5">
                    <input type="checkbox" id="isCorporate" name="isCorporate" tabindex="5"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-optional" id="lblCompanyRegNo"
                       for="companyRegNo">Company Reg No.:</label>

                <div class="col-md-5">
                    <input type="text" class="k-textbox" id="companyRegNo" name="companyRegNo"
                           validationMessage="Required" tabindex="6"/>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="companyRegNo"></span>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-4 control-label label-optional" id="lblDateOfIncorporation"
                       for="dateOfIncorporation">Date of Incorporation:</label>

                <div class="col-md-5">
                    <app:dateControl name="dateOfIncorporation" validationMessage="Required" tabindex="7" value=""
                                     disabled="true" placeholder="dd/MM/yyyy">
                    </app:dateControl>
                </div>

                <div class="col-md-3 pull-left">
                    <span class="k-invalid-msg" data-for="dateOfIncorporation"></span>
                </div>
            </div>

        </div>
    </div>
</form>