<div class="form-group">
    <div class="form-group col-md-9">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Create Voucher (Receive Bank)
                </div>
            </div>

            <div id="createVoucherPanel" class="panel-body" style="height: 335px">

                <ul class="nav nav-tabs">
                    <li class="active"><a href="#fragment-1" data-toggle="tab">Basic Info</a></li>
                    <li><a href="#fragment-2" data-toggle="tab">Debit</a></li>
                    <li><a href="#fragment-3" data-toggle="tab">Credit</a></li>
                    <li><a href="#fragment-4" data-toggle="tab">Journal</a></li>
                </ul>

                <div class="tab-content">
                    <div class="tab-pane active" id="fragment-1">
                        <g:render template='/accounting/accVoucher/basicInfoReceiveBank'/>
                    </div>

                    <div class="tab-pane" id="fragment-2">
                        <g:render template='/accounting/accVoucher/debitReceiveBank'/>
                    </div>

                    <div class="tab-pane" id="fragment-3">
                        <g:render template='/accounting/accVoucher/creditReceiveBank'/>
                    </div>

                    <div class="tab-pane" id="fragment-4">
                        <table id="flexJournal" style="display:none"></table>
                    </div>
                </div>
            </div>

            <app:ifAllUrl urls="/accVoucher/create,/accVoucher/update,/accVoucher/select">
                <div class="panel-footer">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext" role="button" onclick='onSubmitVoucher();'
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='clearFormVoucher();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </app:ifAllUrl>
        </div>

        <div class="form-group">
            <app:ifAllUrl urls="/accVoucher/listReceiveBank">
                <table id="flex1" style="display:none"></table>
            </app:ifAllUrl>
        </div>
    </div>

    <div class="form-group col-md-3">
        <table id="flexSearchCOA" style="display:none"></table>
    </div>
</div>
<g:render template='/accounting/accVoucher/scriptReceiveBank'/>
