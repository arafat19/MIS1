<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Create Customer
        </div>
    </div>

    <form name='customerForm' id="customerForm" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>

            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="fullName">Full Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="fullName" name="fullName" tabindex="1"
                                   placeholder="Full Name" required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="fullName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="nickName">Nick Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="nickName" name="nickName" tabindex="2"
                                   placeholder="Nick Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="nickName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="phoneNo">Phone No:</label>

                        <div class="col-md-6">
                            <input type="tel" class="k-textbox" id="phoneNo" name="phoneNo"  pattern="\d{11}"
                                   placeholder="Customer Phone No" validationMessage="Invalid No." tabindex="3"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="phoneNo"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="email">Email:</label>

                        <div class="col-md-6">
                            <input type="email" class="k-textbox" id="email" name="email" tabindex="4"
                                   placeholder="Email Address" validationMessage="Invalid email"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="email"></span>
                        </div>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="dateOfBirth">Date of Birth:</label>

                        <div class="col-md-6">
                            <app:dateControl
                                    name="dateOfBirth"
                                    tabindex="5"
                                    required="true"
                                    value=""
                                    placeholder="dd/MM/yyyy">
                            </app:dateControl>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="dateOfBirth"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="address">Address:</label>

                        <div class="col-md-6">
                            <textarea type="text" class="k-textbox" id="address" name="address" rows="3"
                                      placeholder="255 Char Max" tabindex="6" ></textarea>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="address"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer">
            <button id="create" name="create" type="submit" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" tabindex="7"><span class="k-icon k-i-plus"></span>Create
            </button>

            <button id="clearFormButton" name="clearFormButton" type="button" data-role="button" class="k-button k-button-icontext" role="button"
                    aria-disabled="false" onclick='resetForm();' tabindex="8"><span class="k-icon k-i-close"></span>Cancel
            </button>
        </div>
    </form>
</div>
