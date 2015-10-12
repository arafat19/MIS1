<%@ page import="com.athena.mis.application.utility.NoteEntityTypeCacheUtility" %>

<script type="text/javascript">
    var dropDownBank, dropDownBankBranch, dropDownDistrict;
    $(document).ready(function () {
        var isForwardable = "${model.isForwardable}";
        if (isForwardable == 'false') {
            $('#forwardPanel').remove();
            //$('#taskDetailsPanel').attr('class', 'col-md-12');
        }
    });

    function executePreConditionForForward() {
        $('#idType').val($.trim($('#idType').val()));
        $('#idNo').val($.trim($('#idNo').val()));
        if ($('#taskId').val() == '') {
            showError("Please search task first");
            return false;
        }
        if (dropDownBank.value() == '') {
            showError('Please select bank');
            return false;
        }
        if (dropDownDistrict.value() == '') {
            showError('Please select district');
            return false;
        }
        if (dropDownBankBranch.value() == '') {
            showError('Please select bank branch');
            return false;
        }
        showLoadingSpinner(true);
        return true;
    }

    function executePostConditionForForward(data) {
        var isError = data.isError;
        var message = data.message;
        if (isError) {
            showError(message);
        } else {
            showSuccess(message);
            $('#taskDetails').reloadMe();
            dropDownBank.readonly();
            dropDownBankBranch.readonly();
            dropDownDistrict.readonly();
            $('#panel-footer').remove();
        }
    }

    function onChangeBank() {
        dropDownBankBranch.value('');
        dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch, null));
        if (dropDownBank.value() == '') {
            dropDownDistrict.setDataSource(getKendoEmptyDataSource(dropDownDistrict, null));
            return false;
        }
        var bankId = dropDownBank.value();
        $('#districtId').attr('bank_id', bankId);
        $('#districtId').reloadMe();
    }

    function populateBranch() {
        if (dropDownDistrict.value() == '') {
            dropDownBankBranch.setDataSource(getKendoEmptyDataSource(dropDownBankBranch, null));
            return false;
        }
        var bankId = dropDownBank.value();
        var districtId = dropDownDistrict.value();
        $('#branchId').attr('bank_id', bankId);
        $('#branchId').attr('district_id', districtId);
        $('#branchId').reloadMe();
    }
</script>

<div>
    <div class="col-md-7" style="padding-left: 0; padding-right: 5px" id="taskDetailsPanel">
        <div class="table-responsive" id="taskDetailsContainer">
            <rms:taskDetails task_object="${model?.taskObject}" id="taskDetails"
                             url="${createLink(controller: 'rmsTask', action: 'reloadTaskDetailsTagLib')}">
            </rms:taskDetails>
        </div>
    </div>

    <div class="col-md-5" style="padding-right: 0; padding-left: 5px" id="forwardPanel">
        <div class='panel panel-primary'>
            <div class="panel-heading">
                <div class="panel-title">
                    Forward Task
                </div>
            </div>
            <g:formRemote name='taskDetailsForm' class="form-horizontal form-widgets" role="form"
                          before="if (!executePreConditionForForward()) {return false;}"
                          onSuccess="executePostConditionForForward(data)" onComplete="onCompleteAjaxCall()"
                          url="${[controller: 'rmsTask', action: 'forwardRmsTask']}">
                <input type="hidden" id="taskId" name="taskId" value="${model?.taskId}" />
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="bankId">Bank:</label>

                        <div class="col-md-8">
                            <app:dropDownBank data_model_name="dropDownBank" id="bankId" name="bankId"
                                              tabindex="6" onchange="onChangeBank();">
                            </app:dropDownBank>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="districtId">District:</label>

                        <div class="col-md-8">
                            <app:dropDownDistrict data_model_name="dropDownDistrict" name="districtId" id="districtId"
                                                  tabindex="7" onchange="populateBranch();" bank_id=""
                                                  url="${createLink(controller: 'district', action: 'reloadDistrictDropDown')}">
                            </app:dropDownDistrict>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="branchId">Branch:</label>

                        <div class="col-md-8">
                            <app:dropDownBranchesByBankAndDistrict data_model_name="dropDownBankBranch" name="branchId"
                                                                   id="branchId"
                                                                   tabindex="8"
                                                                   url="${createLink(controller: 'bankBranch', action: 'reloadBranchesDropDownByBankAndDistrict')}">
                            </app:dropDownBranchesByBankAndDistrict>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" id="panel-footer">
                    <button id="add" name="forward" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="9"
                            aria-disabled="false"><span class="k-icon k-i-ok"></span>Forward
                    </button>
                </div>
            </g:formRemote>
        </div>
    </div>
</div>