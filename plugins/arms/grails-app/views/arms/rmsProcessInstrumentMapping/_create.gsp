<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Process Instrument Mapping
        </div>
    </div>

    <form id='rmsProInsMappingForm' name='rmsProInsMappingForm' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" id="id" name="id"/>
            <input type="hidden" id="version" name="version"/>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="processType">Process Type:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity dataModelName="dropDownProcessType" name="processType"
                                              typeId="${SystemEntityTypeCacheUtility.ARMS_PROCESS_TYPE}"
                                              tabindex="1" required="true" validationMessage="Required">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="processType"></span>
                </div>

                <label class="col-md-2 control-label label-required" for="instrumentType">Instrument Type:</label>

                <div class="col-md-2">
                    <app:dropDownSystemEntity dataModelName="dropDownInstrumentType" name="instrumentType"
                                              typeId="${SystemEntityTypeCacheUtility.ARMS_INSTRUMENT_TYPE}"
                                              tabindex="2" required="true" validationMessage="Required">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="instrumentType"></span>
                </div>
            </div>

        </div>

        <div class="panel-footer">

            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                    class="k-button k-button-icontext" role="button" tabindex="4"
                    aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
            </button>

        </div>
    </form>

</div>