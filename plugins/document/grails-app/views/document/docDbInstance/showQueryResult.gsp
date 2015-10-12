<g:render template="/document/docDbInstance/scriptQueryResult" />
<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <div class="panel-title">SQL Result Set
            <span class="label label-primary" style="float: right; cursor: pointer" onclick="window.history.back()">
                Back
            </span>
            <span class="glyphicon glyphicon-chevron-left" style="float: right"></span>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-md-12">
                <div id="gridDocSqlResult"></div>
            </div>
        </div>
    </div>
    <div class="panel-footer" style="height: 40px">
            <span class="download_icon_set" onclick="downloadCsv()">
                <ul>
                    <li>Save as :</li>
                    <li><a href="javascript:void(0)" class="csv_icon"></a></li>
                </ul>
            </span>
    </div>

</div>