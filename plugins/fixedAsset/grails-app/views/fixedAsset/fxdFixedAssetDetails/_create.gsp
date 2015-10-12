<%@ page import="com.athena.mis.application.utility.OwnerTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Fixed Asset Details
        </div>
    </div>

    <form id="fixedAssetDetailsForm" name="fixedAssetDetailsForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>
            <app:systemEntityByReserved
                    name="ownerTypeRentalId"
                    reservedId="${OwnerTypeCacheUtility.RENTAL}"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_OWNER_TYPE}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="poId">Purchase Order:</label>

                        <div class="col-md-6">
                            <select id="poId"
                                    name="poId"
                                    tabindex="1"
                                    required validationMessage="Required"
                                    onchange="updateItemList();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="poId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="itemId">Category:</label>

                        <div class="col-md-6">
                            <select id="itemId"
                                    name="itemId"
                                    required validationMessage="Required"
                                    tabindex="2"
                                    onchange="updateCost();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="itemId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="cost">Cost:</label>

                        <div class="col-md-9">
                            <span id="cost"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required"
                               for="inventoryTypeId">Inventory Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownInventoryTypeId"
                                    required="true"
                                    validationMessage="Required"
                                    name="inventoryTypeId"
                                    onchange="updateInventoryList();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_INVENTORY_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="inventoryId">Inventory:</label>

                        <div class="col-md-6">
                            <select id="inventoryId"
                                    name="inventoryId"
                                    required validationMessage="Required"
                                    tabindex="4">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="inventoryId"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="purchaseDate">Purchase Date:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="purchaseDate"
                                    tabindex="5"
                                    required="true"
                                    validationMessage="Required"
                                    value=""
                                    placeholder="dd/mm/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="purchaseDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="name">Model / Serial No:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name"
                                   name="name" tabindex="6" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="ownerTypeId">Owner Type:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    name="ownerTypeId"
                                    tabindex="7"
                                    required="true" validationMessage="Required"
                                    onchange="return applyClassOnExpireDate();"
                                    typeId="${SystemEntityTypeCacheUtility.TYPE_OWNER_TYPE}"
                                    dataModelName="dropDownOwnerTypeId">
                            </app:dropDownSystemEntity>

                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="ownerTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group" id="expireDateInfo" hidden="hidden">
                        <label class="col-md-3 control-label label-required" for="expireDate">Expired On:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="expireDate"
                                    tabindex="8"
                                    required="true"
                                    validationMessage="Required"
                                    value=""
                                    placeholder="dd/mm/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="expireDate"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="description">Description:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="description" name="description" rows="3"
                                      placeholder="255 Char Max" tabindex="9"></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="description"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="10"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="11"
                    aria-disabled="false" onclick='resetCreateForm();'><span
                    class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>
</div>

