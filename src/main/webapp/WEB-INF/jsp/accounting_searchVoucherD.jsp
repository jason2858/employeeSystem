<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <jsp:include page="header.jsp" />
        <!-- Datetimepicker-->
        <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
        <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        <style type="text/css">
            body {
                display: none;
                /*全部先隱藏*/
            }

            label {
                margin: 0 8px 0 0;
            }
            
            th, td {
                text-align:center;
                white-space: nowrap;
            }

            .table-x {
                flex: 1 0 90%;
                overflow-x: auto;
                box-shadow: inset 0 0 24px #0002;
                -webkit-overflow-scrolling: touch;
                -ms-overflow-style: -ms-autohiding-scrollbar;
            }

            table.dataTable {
                margin-top: 0 !important;
            }
        </style>


        <script src="../../Style/js/accounting.js?ver=20220314"></script>
        <script type="text/javascript">
            
        var isFinishLoad = false;
        var account = '<%out.print(session.getAttribute("Account"));%>';
        var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
        var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
        $(document).ready(function () {
            $("#loading").modal("show");
            $("#loading").on("shown.bs.modal", function () {
                isFinishLoad = true;
            });
            closeLoading();

            if (authorise == 4) {
                $("#userFilter").css("visibility", "hidden");
            }

            if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
                window.location.href = "timeout.do";
            }
            if (authorise == 1 || authorise == 2 || authorise == 3) {
                appendUnsign();
                getUnsign();
            }

            $("body").show();

            const companyList = getDatalist("POST", "/rest/department/getCompanys")
            companyList.setDatalist('companyList', 'name')
            const applicantList = getDatalist("POST", "/rest/attendance/nameFilter")
            applicantList.setDatalist('applicantList', 'chineseName', 'name')
            const projectNameList = getDatalist("POST", "/rest/workItem/getProjects")
            $("#projectNameList").prepend('<option value="非專案">')
            projectNameList.setDatalist('projectNameList', ['id', 'name'])
            const itemList = getDatalist("GET", "/rest/accounting/itemDrop")
            itemList.setDatalist('itemList', ['iId', 'iName'])
            const customerList = getDatalist("POST", "/rest/project/getCustomer")
            customerList.setDatalist('customerList', 'name')
            customerList.setDatalist('cus_tax_idList', 'ein')

            initSearchDetail({companyList, applicantList, projectNameList, itemList, customerList})
        });

        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="card m-3">
                    <header class="card-header">傳票明細查詢</header>

                    <div class="mb-2">
                        <jsp:include page="accounting_voucherD.jsp">
                            <jsp:param name="render" value="page" />
                        </jsp:include>
                    </div>
                    <datalist id="companyList"></datalist>
                    <datalist id="applicantList"></datalist>
                    <datalist id="projectNameList"></datalist>
                    <datalist id="itemList"></datalist>
                    <datalist id="customerList"></datalist>
                    <datalist id="cus_tax_idList"></datalist>
                </div>
            </div>
            <jsp:include page="footer.jsp" />
            </div>
        </div>
        <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
            data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
            <div class="modal-dialog">
                <img src="/Style/images/loading.gif" style="padding-top: 12rem;">
            </div>
        </div>
        <jsp:include page="JSfooter.jsp" />
    </body>
</html>