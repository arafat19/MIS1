
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="registration"/>
    <title>ARMS(Agent)</title>

    <script type="text/javascript">
        $(document).ready(function() {
            onLoadTemplateSanction();
        });

        function onLoadTemplateSanction() {
            showLoadingSpinner(true);
            var page = "${page}";
            var fName = "${fName}";
            var mName = "${mName}";
            var lName = "${lName}";
            var customerName = "${customerName}";
            jQuery.ajax({
                data:{fName:fName, mName:mName, lName:lName, customerName:customerName},
                type:'post',
                url:page,
                success:function (data, textStatus) {
                    execute_post_condition(data);
                },
                error:function (XMLHttpRequest, textStatus, errorThrown) {
                },
                complete:function (XMLHttpRequest, textStatus) {
                    showLoadingSpinner(false);
                }
            });
        }

        function execute_post_condition(data) {
            $('#contentHolder').html(data);
            showLoadingSpinner(false);
        }
    </script>
</head>
<body>
</body>
</html>
