<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility" %>
<div id="application_top_panel"  class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Agent
        </div>
    </div>


    <g:form name='agentForm'  class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
        <g:hiddenField name="id"/>
        <g:hiddenField name="version"/>
          <div class="form-group">

            <div class="col-md-6">

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="name">Name:</label>

                    <div class="col-md-6">
                        <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                               placeholder="Name" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="name"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="city">City:</label>

                    <div class="col-md-6">
                        <input type="text" class="k-textbox" id="city" name="city" tabindex="2"
                               placeholder="city" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="city"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="creditLimit">Credit Limit:</label>

                    <div class="col-md-6">
                        <input type="text" class="k-textbox" id="creditLimit" name="creditLimit" tabindex="3"
                               placeholder="creditLimit" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="creditLimit"></span>
                    </div>
                </div>
                <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DEVELOPMENT_USER}">


                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="commissionLogic">Commission Logic:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="commissionLogic" name="commissionLogic" tabindex="6"
                                      placeholder="" required validationMessage="Required" rows="3"></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="commissionLogic"></span>
                        </div>
                    </div>
                </app:hasRoleType>

               </div>
            <div class="col-md-6">

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="phone">Phone:</label>

                    <div class="col-md-6">
                        <input type="text" class="k-textbox" id="phone" name="phone" tabindex="4"
                               placeholder="phone" required validationMessage="Required"/>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="phone"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-3 control-label label-required" for="address">Address:</label>

                    <div class="col-md-6">
                        <textarea type="text" class="k-textbox" id="address" name="address" tabindex="5"
                               placeholder="address" required validationMessage="Required"></textarea>
                    </div>

                    <div class="col-md-3 pull-left">
                        <span class="k-invalid-msg" data-for="address"></span>
                    </div>
                </div>


            </div>

              </div>


        </div>


        <div class="panel-footer">
        <app:ifAllUrl urls="/exhAgent/create,/exhAgent/update">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="7"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>
        </app:ifAllUrl>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="8"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </g:form>
</div>
