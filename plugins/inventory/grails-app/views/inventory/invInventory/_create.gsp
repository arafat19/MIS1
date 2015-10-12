<%@ page import="com.athena.mis.application.utility.AppUserEntityTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Inventory
        </div>
    </div>

    <form name='inventoryForm' id="inventoryForm"
            class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <app:systemEntityByReserved
                    name="entityTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_USER_ENTITY_TYPE}"
                    reservedId="${AppUserEntityTypeCacheUtility.INVENTORY}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="projectId">Project:</label>

                        <div class="col-md-6">
                            <app:dropDownProject
                                    required="true"
                                    validationMessage="Required"
                                    dataModelName="dropDownProject"
                                    name="projectId">
                            </app:dropDownProject>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="projectId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name"
                                   placeholder="Unique Inventory Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="typeId">Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownType"
                                    name="typeId"
                                    required="true"
                                    validationMessage="Required"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="typeId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="isFactory">Is Factory:</label>

                        <div class="col-md-6">
                            <g:checkBox class="form-control-static" name="isFactory"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="isFactory"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="description">Description:</label>

                        <div class="col-md-9">
                            <textarea type="text" class="k-textbox" id="description" name="description" rows="3"
                                      placeholder="255 Char Max" validationMessage="Short Description"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>



