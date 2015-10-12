<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Chart of Account
        </div>
    </div>

    <form id="chartOfAccountForm" name="chartOfAccountForm" class="form-horizontal form-widgets" role="form">
        <input type="hidden" name="id" id="id"/>
        <input type="hidden" name="version" id="version"/>

        <div class="panel-body">
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="accTypeId">Account Type:</label>

                        <div class="col-md-6">
                            <acc:dropDownAccType dataModelName="dropDownAccType"
                                                 name="accTypeId"
                                                 tabindex="1"
                                                 required="true"
                                                 validationMessage="Required"
                                                 onchange="populateTier1List();">
                            </acc:dropDownAccType>
                        </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="tier1">Tier 1:</label>

                        <div class="col-md-6">
                            <select id='tier1'
                                    name="tier1"
                                    tabindex="2"
                                    required=""
                                    validationMessage="Required"
                                    onchange="populateTier2List();">
                            </select>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="tier1"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="tier2">Tier 2:</label>

                        <div class="col-md-6">
                            <select id='tier2'
                                    name="tier2"
                                    tabindex="3"
                                    onchange="populateTier3List();">
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="tier3">Tier 3:</label>

                        <div class="col-md-6">
                            <select id='tier3'
                                    name="tier3"
                                    tabindex="4">
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Group:</label>

                        <div class="col-md-6">
                            <acc:dropDownAccGroup dataModelName="dropDownAccGroup"
                                                  tabindex="5"
                                                  name="accGroupId">
                            </acc:dropDownAccGroup>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional"
                               for="isActive">Is Active:</label>

                        <div class="col-md-6">
                            <g:checkBox class="form-control-static" name="isActive" tabindex="6"/>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="accSourceId">Source:</label>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity dataModelName="dropDownSource"
                                                      required="true"
                                                      validationMessage="Required"
                                                      tabindex="7"
                                                      name="accSourceId"
                                                      onchange="populateSourceCategoryList();"
                                                      typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_SOURCE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="accSourceId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Source Category:</label>

                        <div class="col-md-6">
                            <select id="sourceCategoryId"
                                    name="sourceCategoryId"
                                    tabindex="8">
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional">Custom Group:</label>

                        <div class="col-md-6">
                            <acc:dropDownAccCustomGroup dataModelName="dropDownAccCustomGroup"
                                                        tabindex="9"
                                                        name="accCustomGroupId">
                            </acc:dropDownAccCustomGroup>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="description">Head Name:</label>

                        <div class="col-md-6">
                            <textarea class="k-textbox"
                                      id="description"
                                      name="description"
                                      required
                                      validationMessage="Required"
                                      tabindex="10" rows="3"></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="description"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required">Code:</label>

                        <div class="col-md-6">
                            <span id="codeSpan"></span><input type="hidden" name="code" id="code"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="11"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="12"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>
            <app:ifAllUrl urls="/accReport/downloadChartOfAccounts">
                <span class="download_icon_set" onclick="javascript:printChartOfAccounts()">
                    <ul>
                        <li>Save as :</li>
                        <li><a href="javascript:void(0)" id="printReport" class="pdf_icon"></a></li>
                    </ul>
                </span>
            </app:ifAllUrl>
        </div>
    </form>
</div>
