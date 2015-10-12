<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create DB Instance
        </div>
    </div>

    <form id="dbInstanceForm" name="dbInstanceForm" class="form-horizontal form-widgets" role="form" method="post">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-12">

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="instanceName">Instance Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="instanceName" name="instanceName" tabindex="1"
                                   placeholder="DB Instance Name (e.g. Postgres)" required
                                   validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="instanceName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="vendorId">Vendor:</label>

                        <div class="col-md-3">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownDbVendor"
                                    required="true"
                                    validationMessage="Required"
                                    name="vendorId"
                                    tabindex="2"
                                    onchange="setDriverName();"
                                    typeId="${SystemEntityTypeCacheUtility.DOC_DATABASE_VENDOR}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="vendorId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="driver">Driver:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="driver" name="driver" tabindex="3"
                                   placeholder="Driver (e.g. org.postgresql.Driver)" required
                                   validationMessage="Required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required"
                               for="connectionString">Connection String:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="connectionString" name="connectionString"
                                   tabindex="4"
                                   placeholder="Connection String" required validationMessage="Required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="sqlQuery">SQL Query:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="sqlQuery" name="sqlQuery" tabindex="4" rows="4"
                                      placeholder="Query Max 2000 characters (e.g. select * from tableName)" required
                                      validationMessage="Required"
                                      maxlength="2000"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button name="create" id="create" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="5"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span> Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="6"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>

