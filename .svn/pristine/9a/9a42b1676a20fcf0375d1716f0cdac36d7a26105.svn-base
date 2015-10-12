<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Quantity Survey Measurement(Govt.)
                </div>
            </div>
            <form id="qsMeasurementForm" name="qsMeasurementForm" class="form-horizontal form-widgets"
                    role="form">
                <div class="panel-body">
                    <input type="hidden" name="id" id="id"/>
                    <input type="hidden" name="version" id="version"/>
                    <g:hiddenField name="projectId"/>
                    <g:hiddenField name="budgetId"/>
                    <g:hiddenField name="isGovtQs" value='true'/>
                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="lblProjectName">Project Name:</label>

                                <div class="col-md-9">
                                    <span id="lblProjectName"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="lblBudgetItem">Budget Item:</label>

                                <div class="col-md-9">
                                    <span id="lblBudgetItem"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="qsMeasurementDate">QS Date:</label>


                                <div class="col-md-6">
                                    <app:dateControl
                                            name="qsMeasurementDate"
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="1">
                                    </app:dateControl>
                                </div>

                                <div class="col-md-3">
                                    <span class="k-invalid-msg" data-for="qsMeasurementDate"></span>
                                </div>

                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="siteId">Site:</label>

                                <div class="col-md-6">
                                    <qs:dropDownQsSite
                                            tabindex="2"
                                            required="true"
                                            validationMessage="Required"
                                            dataModelName="dropDownSite"
                                            name="siteId"
                                            onchange="onChangeSite();">
                                    </qs:dropDownQsSite>
                                </div>

                                <div class="col-md-3">
                                    <span class="k-invalid-msg" data-for="siteId"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="quantity">Quantity:</label>

                                <div class="col-md-5">
                                    <input type="text"
                                           tabindex="3"
                                           id="quantity"
                                           name="quantity"
                                           placeholder="Quantity"
                                           required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3">
                                    <span id="lblUnit"></span>
                                </div>

                                <div class="col-md-2 pull-left">
                                    <span class="k-invalid-msg" data-for="quantity"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="comments">Comments:</label>

                                <div class="col-md-9">
                                    <textarea type="text" class="k-textbox" id="comments" name="comments" rows="3"
                                              placeholder="255 Char Max" tabindex="4"
                                              validationMessage="Short Description is Required"></textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">

                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetCreateForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>

        </div>

        <div class="form-group">
            <table id="flex1" style="display:none"></table>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexBudget" style="display:none"></table>
    </div>
</div>





