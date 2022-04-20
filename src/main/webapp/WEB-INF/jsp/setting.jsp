<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="./Style/images/favicon.ico" type="image/x-icon" />
        <title>越世實業員工日程管理系統</title>
        <!-- Bootstrap core CSS-->
        <link href="../Style/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <!-- Custom fonts for this template-->
        <link href="../Style/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- Page level plugin CSS-->
        <link href="../Style/vendor/datatables/dataTables.bootstrap4.css" rel="stylesheet">
        <!-- Custom styles for this template-->
        <link href="../Style/css/sb-admin.css" rel="stylesheet">
        <script src="../Style/vendor/jquery/jquery.min.js"></script>
        <script src="../Style/vendor/jquery/linkify.min.js"></script>
        <script src="../Style/vendor/jquery/linkify-jquery.min.js"></script>
        <!-- Datetimepicker-->
        <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
        <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        <!-- comfirm button -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
        <style>
            body {
                display: none;
                /*全部先隱藏*/
            }
            
            [class*="col-"] {
                padding: 0px;
            }
        </style>
        <script type="text/javascript">
            var isFinishLoad = false;
            var authorise = "${Authorise}";
            $(document).ready(function() {
                $('#loading').modal('show');
                $("#loading").on('shown.bs.modal', function() {
                    isFinishLoad = true;
                });
                closeLoading();

                if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
                    window.location.href = "timeout.do";
                }
                if (authorise == 1 || authorise == 2 || authorise == 3) {
                    appendUnsign();
                    getUnsign();
                }

                $.ajax({
                    type: 'post',
                    url: './rest/setting/getPersonalSetting',
                    datatype: 'text',
                    success: function(data) {
                        var record = JSON.parse(data);
                        record.entity.forEach(function(element) {
                            if (element.id.configKey.includes('personal_homepage')) {
                                $("#homeSelect").val(element.value);
                            }
                            if (element.id.configKey.includes('nameSelect')) {
                                $("#nameSelect").val(element.value);
                            }
                            if (element.id.configKey.includes('punchOutRemindHour')) {
                                $("#punchOutRemindHour").val(element.value);
                            }
                            if (element.id.configKey.includes('remindPunchOut')) {
                                $("#remindPunchOut").val(element.value);
                            }
                        });
                        if ($("#punchOutRemindHour").val() < 1) {
                            $("#remindPunchOut").hide();
                            $("#punchOutRemindHour").attr('class', 'col-sm-5');
                        }
                    },
                    error: function() {}
                });

                $("body").show();

                $("#punchOutRemindHour").change(function() {
                    if ($("#punchOutRemindHour").val() < 1) {
                        $("#remindPunchOut").hide();
                        $("#remindPunchOut").val('remind');
                        $("#punchOutRemindHour").attr('class', 'col-sm-5');
                    } else {
                        $("#punchOutRemindHour").attr('class', 'col-sm-2');
                        $("#remindPunchOut").show();
                    }
                });
            });

            function saveSetting() {
                timeoutCheck();
                var url = $("#homeSelect").val();
                var nameSelect = $("#nameSelect").val();
                var punchOutRemindHour = $("#punchOutRemindHour").val();
                var remindPunchOut = $("#remindPunchOut").val();
                $.ajax({
                    type: 'post',
                    url: './rest/setting/setPersonalHomepage',
                    datatype: 'text',
                    data: {
                        url: url,
                        nameSelect: nameSelect,
                        punchOutRemindHour: punchOutRemindHour,
                        remindPunchOut: remindPunchOut
                    },
                    success: function(data) {
                        confirmWithFunction('訊息', '儲存成功', function() {
                            window.location.href = "setting.do";
                        });
                    },
                    error: function() {

                    }
                });
            }
        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="container-fluid">
                    <div class="card mb-3">
                        <div class="card-header">偏好設定</div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-sm-8 form-group">
                                    <label class="col-sm-3 control-label text-right">首頁設定 : </label>
                                    <select class="col-sm-5" id="homeSelect" class="mouseover">
								<%
									for (int i = 1; i < (Integer) session.getAttribute("sidebarcount"); i++) {
										if ((Integer) session.getAttribute(i + ".inner") == 0) {
											out.print("<option value=" + session.getAttribute(i + ".url") + ">"
													+ session.getAttribute(i + ".name") + "</option>");
										} else {
											for (int j = 0; j < (Integer) session.getAttribute(i + ".inner"); j++) {
												out.print("<option value=" + session.getAttribute(i + "." + j + ".url") + ">"
														 + (String) session.getAttribute(i + ".name") + "-" + session.getAttribute(i + "." + j + ".name") + "</option>");
											}
										}
									}
								%>
							</select>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-8 form-group">
                                    <label class="col-sm-3 control-label text-right">員工名稱顯示 : </label>
                                    <select class="col-sm-5" id="nameSelect">
							<option value="EN">英文</option>
							<option value="TW">中文</option>
							</select>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-sm-8 form-group">
                                    <label class="col-sm-3 control-label text-right">下班打卡提醒 :</label>
                                    <select class="col-sm-2" id="punchOutRemindHour">
							<option value="0">不檢查</option>
							<%	
								for(int i = 1; i < 9; i++){
									out.print("<option value=" + i + ">滿"
											+ i + "小時</option>");
								}
									%>
							</select>
                                    <select class="col-sm-3" id="remindPunchOut">
								<option value="remind">提示打卡</option>
								<option value="remindPlusPunchOut">提示打卡+打下班卡</option>
							</select>
                                </div>
                            </div>
                            <div style="margin-top:2rem;text-align:center;"><button class="userbutton" onclick="saveSetting()">儲存</button></div>
                        </div>
                    </div>
                </div>
                <jsp:include page="footer.jsp" />
                <!-- Scroll to Top Button-->
                <a class="scroll-to-top rounded" href="#page-top"> <i class="fa fa-angle-up"></i>
                </a>
            </div>
        </div>

        <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
            <div class="modal-dialog">
                <img src="../Style/images/loading.gif" style="padding-top: 12rem;">
            </div>
        </div>

        <!-- Bootstrap core JavaScript-->
        <script src="./Style/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <!-- Core plugin JavaScript-->
        <script src="./Style/vendor/jquery-easing/jquery.easing.min.js"></script>
        <!-- Page level plugin JavaScript-->
        <script src="./Style/vendor/datatables/jquery.dataTables.js"></script>
        <script src="./Style/vendor/datatables/dataTables.bootstrap4.js"></script>
        <!-- Custom scripts for all pages-->
        <script src="./Style/js/sb-admin.min.js"></script>
        <!-- Custom scripts for this page-->
        <script src="./Style/js/sb-admin-datatables.min.js"></script>
        <!-- Setting -->
        <script src="./Style/js/YeseeGov.js"></script>
    </body>

    </html>