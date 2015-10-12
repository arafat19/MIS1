<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Company
        </div>
    </div>

    <g:form id='companyForm' name='companyForm' enctype="multipart/form-data" class="form-horizontal form-widgets"
            role="form">
        <div class="panel-body">
            <g:hiddenField name="id"/>
            <g:hiddenField name="version"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="countryId">Country:</label>

                        <div class="col-md-6">
                            <app:dropDownCountry
                                    dataModelName="dropDownCountry"
                                    required="true"
                                    validationMessage="Required"
                                    name="countryId"
                                    tabindex="1">
                            </app:dropDownCountry>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="countryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                   placeholder="Company Name" required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="code">Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="code" name="code" tabindex="3"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="code"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="webUrl">Web URL:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="webUrl" name="webUrl"
                                   pattern="^(https?|ftp|file)://.+$" tabindex="4"
                                   placeholder="Should be Unique" required data-required-msg="Required"
                                   validationMessage="Not valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="webUrl"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="companyLogo">Logo:</label>

                        <div class="col-md-6">
                            <input type="file" tabindex="5" id="companyLogo" name="companyLogo"/>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="email">Email:</label>

                        <div class="col-md-6">
                            <input type="email" class="k-textbox" id="email" name="email" tabindex="6"
                                   placeholder="Should be Unique" validationMessage="Not Valid"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="email"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="address1">Address 1:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="address1" name="address1" rows="2"
                                      placeholder="255 Char Max" required tabindex="7"
                                      validationMessage="Address 1 is Required"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="address2">Address 2:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="address2" name="address2" rows="2"
                                      placeholder="255 Char Max" tabindex="8"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="9"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="10"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
