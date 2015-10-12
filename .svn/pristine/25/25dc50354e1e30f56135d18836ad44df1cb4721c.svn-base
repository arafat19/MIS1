<script type="text/javascript">
    $(document).ready(function () {
        $('#refNo').focus();
        $(document).attr('title', "ARMS - Task History");
        loadNumberedMenu(MENU_ID_ARMS, "#rmsTaskTrace/showRmsTaskHistory");
    });

    function executePreConditionForSearchTask() {
        $('#propertyValue').val($.trim($('#propertyValue').val()));
        var refNo = $('#propertyValue').val();
        if (refNo == '') {
            showError("Please enter Ref No");
            return false;
        }
        if (refNo.length < 4) {
            showError('Please enter at least 4 characters');
            return false;
        }
        return true;
    }
</script>

<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">
            Task History
        </div>
    </div>

    <g:formRemote name='taskDetailsForm' class="form-horizontal form-widgets" role="form"
                  update="divTaskHistory" method="post"
                  before="if (!executePreConditionForSearchTask()) {return false;}" onComplete="onCompleteAjaxCall()"
                  url="${[controller: 'rmsTaskTrace', action: 'searchRmsTaskHistory']}">
        <div class="panel-body">
            <input type="hidden" name="propertyName" value="refNo"/>
            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="propertyValue">Ref No:</label>
                <div class="col-md-3">
                    <input class="k-textbox" id="propertyValue" name="propertyValue" value="${params?.propertyValue}" tabindex="1"/>
                </div>
            </div>
        </div>

        <div class="panel-footer">

            <button id="search" name="search" type="submit" data-role="button" class="k-button k-button-icontext"
                    role="button" tabindex="2"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>
        </div>
    </g:formRemote>
</div>

<div id="divTaskHistory" class="table-responsive">
    <g:render template="/arms/rmsTaskTrace/taskHistory"/>
</div>