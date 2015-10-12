<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility; com.athena.mis.application.utility.AppUserEntityTypeCacheUtility; com.athena.mis.application.utility.ContentEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility;" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Create Project
                </div>
            </div>

            <form id="projectForm" name="projectForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id"/>
                    <input type="hidden" name="version" id="version"/>

                    <app:systemEntityByReserved
                            name="entityTypeId"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_CONTENT_ENTITY}"
                            reservedId="${ContentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_PROJECT}">
                    </app:systemEntityByReserved>

                    <app:systemEntityByReserved
                            name="appUserEntityTypeId"
                            typeId="${SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE}"
                            reservedId="${AppUserEntityTypeCacheUtility.PROJECT}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           placeholder="Unique Project Name" required validationMessage="Required"
                                           autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="code">Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="startDate">Start Date:</label>

                                <div class="col-md-6">
                                    <app:dateControl
                                            name="startDate"
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="6">
                                    </app:dateControl>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="startDate"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="endDate">End Date:</label>

                                <div class="col-md-6">
                                    <app:dateControl
                                            name="endDate"
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="10">
                                    </app:dateControl>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="endDate"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="description">Description:</label>

                                <div class="col-md-9">
                                    <textarea type="text" class="k-textbox" id="description" name="description" rows="1"
                                              tabindex="3"
                                              placeholder="255 Char Max"
                                              validationMessage="Short Description is Required"></textarea>
                                </div>
                            </div>
                        </div>

                        <app:hasRoleType id="${RoleTypeCacheUtility.ROLE_TYPE_DEVELOPMENT_USER}">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInFromSupplier">Auto Approve(In From Supplier):</label>

                                    <div class="col-md-6">
                                        <g:checkBox tabindex="4" name="isApproveInFromSupplier"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInFromInventory">Auto Approve(In From Inventory):</label>

                                    <div class="col-md-6">
                                        <g:checkBox tabindex="5" name="isApproveInFromInventory"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInvOut">Auto Approve(Inventory Out):</label>

                                    <div class="col-md-6">
                                        <g:checkBox tabindex="6" name="isApproveInvOut"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveConsumption">Auto Approve(Consumption):</label>

                                    <div class="col-md-6">
                                        <g:checkBox tabindex="7" name="isApproveConsumption"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveProduction">Auto Approve(Production):</label>

                                    <div class="col-md-6">
                                        <g:checkBox tabindex="8" name="isApproveProduction"/>
                                    </div>
                                </div>
                            </div>
                        </app:hasRoleType>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button"
                            aria-disabled="false" tabindex="9"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="10"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    %{--<div class="row">--}%
        %{--<div id="gridProject"></div>--}%
    %{--</div>--}%
</div>


<div class="row" style="padding-left: 15px; padding-right: 15px;">
    <div id="gridProject"></div>
</div>

