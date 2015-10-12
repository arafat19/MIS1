<%@ page import="com.athena.mis.exchangehouse.config.ExhSysConfigurationIntf" %>
<form name="basicInfoForm" id="basicInfoForm"  class="form-horizontal form-widgets" role="form"method="post">
<div class="panel-body">
    <g:hiddenField name="id"/>
    <g:hiddenField name="version"/>
    <g:hiddenField name="customerId" value="" />
    <g:hiddenField name="customerName" value="" />
<div class="col-md-6">
    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="firstName">First Name:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="firstName" name="firstName" tabindex="1"
                   required validationMessage="Required"/>
            <g:hiddenField name="hidFirstName"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="firstName"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional" for="firstName">Middle Name:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="middleName" name="middleName" tabindex="2"/>
            <g:hiddenField name="hidMiddleName"/>
        </div>

    </div>

    <div class="form-group">
        <exh:checkSysConfig value="0" key="${ExhSysConfigurationIntf.EXH_BENEFICIARY_LAST_NAME_REQUIRED}">
	        <label class="col-md-4 control-label label-optional" for="lastName">Last Name:</label>

	        <div class="col-md-5">
		        <input type="text" class="k-textbox" id="lastName" name="lastName" tabindex="3"/>
		        <g:hiddenField name="hidLastName"/>
	        </div>
        </exh:checkSysConfig>
	    <exh:checkSysConfig value="1" key="${ExhSysConfigurationIntf.EXH_BENEFICIARY_LAST_NAME_REQUIRED}">
		    <label class="col-md-4 control-label label-required" for="lastName">Last Name:</label>

		    <div class="col-md-5">
			    <input type="text" class="k-textbox" id="lastName"
			           required validationMessage="Required" name="lastName" tabindex="3"/>
			    <g:hiddenField name="hidLastName"/>
		    </div>
	    </exh:checkSysConfig>

	    <div class="col-md-3 pull-left">
		    <span class="k-invalid-msg" data-for="lastName"></span>
	    </div>
    </div>
    <div class="form-group">
        <label class="col-md-4 control-label label-optional" for="email">Email:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="email" name="email" tabindex="4"/>
        </div>

    </div>

</div>

<div class="col-md-6">
    <div class="form-group">
        <label class="col-md-4 control-label label-required" for="relation">Relation:</label>

        <div class="col-md-5">
            <input type="text" class="k-textbox" id="relation" name="relation" tabindex="5"
                   required validationMessage="Required"/>
        </div>

        <div class="col-md-3 pull-left">
            <span class="k-invalid-msg" data-for="relation"></span>
        </div>
    </div>

    <div class="form-group">
        <label class="col-md-4 control-label label-optional" for="address">Address:</label>

        <div class="col-md-8">
            <textarea tabindex="6" class='required k-textbox'
                      id="address" name="address" rows="2">

            </textarea>
        </div>

    </div>

</div>
</div>
</form>
