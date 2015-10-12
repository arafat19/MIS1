<%@ page import="com.athena.mis.accounting.utility.AccSourceCacheUtility; com.athena.mis.accounting.utility.AccVoucherTypeCacheUtility; com.athena.mis.application.utility.SystemEntityTypeCacheUtility" %>
<div>
    <form name='frmVoucher' id='frmVoucher' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <input type="hidden" name="id" id="id"/>
            <input type="hidden" name="version" id="version"/>
            <input type="hidden" name="hidTraceNo" id="hidTraceNo"/>
            <input type="hidden" name="hidIsVoucherPosted" id="hidIsVoucherPosted"/>

            <app:systemEntityByReserved
                    name="voucherTypeId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_VOUCHER}"
                    reservedId="${AccVoucherTypeCacheUtility.RECEIVED_VOUCHER_CASH_ID}">
            </app:systemEntityByReserved>
            <app:systemEntityByReserved
                    name="sourceTypeNoneId"
                    typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_SOURCE}"
                    reservedId="${AccSourceCacheUtility.ACC_SOURCE_NAME_NONE}">
            </app:systemEntityByReserved>

            <div class="form-group">
                <label class="col-md-2 control-label label-optional">Ref No:</label>

                <div class="col-md-4">
                    <span id="lblRefNo">(Auto Generated)</span>
                </div>

                <div class="col-md-2">
                    <app:dropDownSystemEntity dataModelName="dropDownInstrumentType"
                                              name="instrumentTypeId"
                                              showHints="false"
                                              tabindex="1"
                                              typeId="${SystemEntityTypeCacheUtility.TYPE_ACC_INSTRUMENT_TYPE}">
                    </app:dropDownSystemEntity>
                </div>

                <div class="col-md-4">
                    <input type="text" class="k-textbox" id="instrumentId" name="instrumentId" tabindex="2"
                           maxlength="20"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required"
                       for="voucherDate">Voucher Date:</label>

                <div class="col-md-4">
                    <app:dateControl name="voucherDate"
                                     tabindex="3">
                    </app:dateControl>
                </div>

                <label class="col-md-2 control-label label-required"
                       for="projectId">Project:</label>

                <div class="col-md-4">
                    <app:dropDownProject dataModelName="dropDownProject"
                                         name="projectId"
                                         tabindex="4"
                                         onchange="populateDivision();">
                    </app:dropDownProject>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="note">Note:</label>

                <div class="col-md-10">
                    <textarea type="text" class="k-textbox" id="note" name="note" rows="7" tabindex="5"
                              placeholder="255 Char Max" maxlength="255"></textarea>
                </div>
            </div>
        </div>

        <div class="panel-footer" style="display:none"></div>
    </form>
</div>