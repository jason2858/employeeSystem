<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="en">

	<head>
		<jsp:include page="header.jsp" />
		<!-- switch button -->
		<link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap2-toggle.min.css" rel="stylesheet">
		<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap2-toggle.min.js"></script>
		<style type="text/css">
			body {
				display: none;
				/*全部先隱藏*/
			}
		</style>
		<script type="text/javascript">
			var isFinishLoad = false;
			var account = '<%out.print(session.getAttribute("Account"));%>';
			var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
			$(document).ready(function () {
				$('#loading').modal('show');
				$("#loading").on('shown.bs.modal', function () {
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

				getRecords();

				var haveSearching = true;
				if (authorise == 3 || authorise == 4) {
					haveSearching = false;
				}

				$('#dataTable').DataTable({
					"searching": haveSearching,
					"bAutoWidth": false,
					"bLengthChange": false,
					"pageLength": 20,
					"columnDefs": [
						{ "width": "18%", "targets": 1 },
						{ "width": "22%", "targets": 2 },
						{ "width": "10%", "orderable": false, "targets": 3 },
						{ "width": "10%", "orderable": false, "targets": 4 },
						{ "width": "10%", "orderable": false, "targets": 5 },
						{ "width": "10%", "orderable": false, "targets": 6 },
						{ "width": "10%", "orderable": false, "targets": 7 },
						{ "width": "10%", "orderable": false, "targets": 8 },
						{
							"targets": [0],
							"visible": false,
							"searchable": false
						}
					],
					"order": [[0, "asc"]],
					"oLanguage": {
						"sProcessing": "讀取中...",
						"sLengthMenu": "Show _MENU_ entries",
						"sZeroRecords": "查無相符的資料",
						"sEmptyTable": "無資料可顯示",
						"sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
						"sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
						"sInfoFiltered": "(filtered from _MAX_ total entries)",
						"sInfoPostFix": "",
						"sSearch": "關鍵字搜尋:",
						"sUrl": "",
						"oPaginate": {
							"sFirst": "第一頁",
							"sPrevious": "上一頁",
							"sNext": "下一頁",
							"sLast": "最末頁"
						}
					}
				});

				var table2 = $('#dataTable2').DataTable({
					ajax: {
						url: '/rest/acc/getLeaveCtrList?empName=' + account,
						method: "POST",
						xhrFields: {
							withCredentials: true
						},
						"dataSrc": function (json) {
							return json;
						}
					}, success: function (data) {
					},
					error: function () {
						confirm("訊息", '取得特休資料失敗');
					},
					"oLanguage": {
						"sProcessing": "讀取中...",
						"sLengthMenu": "Show _MENU_ entries",
						"sZeroRecords": "查無相符的資料",
						"sEmptyTable": "暫無資料",
						"sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
						"sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
						"sInfoFiltered": "(filtered from _MAX_ total entries)",
						"sInfoPostFix": "",
						"sSearch": "關鍵字搜尋:",
						"sUrl": "",
						"oPaginate": {
							"sFirst": "第一頁",
							"sPrevious": "上一頁",
							"sNext": "下一頁",
							"sLast": "最末頁"
						}
					},
					"bLengthChange": false,
					"pageLength": 20,
					"searching": true,
					"bSort": false,
					"columns": [
						{ "data": "empName" },
						{ "data": "dep" },
						{ "data": "skdHours" },
						{
							"data": "pastLeave",
							render: function (data, type, row, meta) {
								if (row.skdHours > row.pastLeave) {
									return '<span style="color:red;">' + data + '(未滿)</span>';
								} else {
									return data;
								}
							}
						}
					]
				});

				$('#dataTable2').parents('div.dataTables_wrapper').first().hide();

				$('#detailTable').DataTable({
					"searching": false,
					"bAutoWidth": false,
					"bLengthChange": false,
					"pageLength": 10,
					"columnDefs": [
						{ "width": "8%", "orderable": false, "targets": 0 },
						{ "width": "21%", "targets": 1 },
						{ "width": "21%", "targets": 2 },
						{ "width": "13%", "targets": 3 },
						{ "width": "8%", "orderable": false, "targets": 4 },
						{ "width": "30%", "orderable": false, "targets": 5 }
					],
					"order": [[3, "asc"]],
					"oLanguage": {
						"sProcessing": "讀取中...",
						"sLengthMenu": "Show _MENU_ entries",
						"sZeroRecords": "查無相符的資料",
						"sEmptyTable": "無資料可顯示",
						"sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
						"sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
						"sInfoFiltered": "(filtered from _MAX_ total entries)",
						"sInfoPostFix": "",
						"sSearch": "關鍵字搜尋:",
						"sUrl": "",
						"oPaginate": {
							"sFirst": "第一頁",
							"sPrevious": "上一頁",
							"sNext": "下一頁",
							"sLast": "最末頁"
						}
					}
				});

				$('#toggle').change(function () {
					$('#yearSearLa').toggle(500);
					$('#year').toggle(500);
					$('#dataTable').parents('div.dataTables_wrapper').first().toggle(500);
					$('#dataTable2').parents('div.dataTables_wrapper').first().toggle(500);
				});

				$("body").show();

				$("#year").change(function () {
					getRecords();
				});

			});

			function getRecords () {
				timeoutCheck();
				var year = $("#year").val();
				$.ajax({
					type: 'post',
					url: "/rest/attRecord/getRecords",
					datatype: 'json',
					data: {
						year: year
					},
					success: function (data) {
						var record = JSON.parse(data);
						var t = $('#dataTable').DataTable();
						t.clear().draw();
						for (var i = 0; i < record.length; i++) {
							var annual, personal, sick, statutory, marital, funeral;
							if (record[i]["annualC"] != 0)
								annual = record[i]["annual"] + " (" + record[i]["annualC"] + ")";
							else
								annual = record[i]["annual"];
							if (record[i]["hour"] != 0)
								annual += " / " + record[i]["hour"];
							if (record[i]["personalC"] != 0)
								personal = record[i]["personal"] + " (" + record[i]["personalC"] + ")";
							else
								personal = record[i]["personal"];
							if (record[i]["sickC"] != 0)
								sick = record[i]["sick"] + " (" + record[i]["sickC"] + ")";
							else
								sick = record[i]["sick"];
							if (record[i]["statutoryC"] != 0)
								statutory = record[i]["statutory"] + " (" + record[i]["statutoryC"] + ")";
							else
								statutory = record[i]["statutory"];
							if (record[i]["maritalC"] != 0)
								marital = record[i]["marital"] + " (" + record[i]["maritalC"] + ")";
							else
								marital = record[i]["marital"];
							if (record[i]["funeralC"] != 0)
								funeral = record[i]["funeral"] + " (" + record[i]["funeralC"] + ")";
							else
								funeral = record[i]["funeral"];
							var temp = t.row.add([
								record[i]["accountName"],
								record[i]["name"],
								record[i]["dep"],
								annual,
								personal,
								sick,
								statutory,
								marital,
								funeral
							])
								.draw().nodes().to$();

							if (record[i]["annual"] != 0 || record[i]["annualC"] != 0) {
								var data = "(3,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(2)').attr("class", "mouseover");
								$(temp).find('td:eq(2)').attr("onclick", "getDetail" + data);
							}
							if (personal != 0) {
								var data = "(4,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(3)').attr("class", "mouseover");
								$(temp).find('td:eq(3)').attr("onclick", "getDetail" + data);
							}
							if (sick != 0) {
								var data = "(5,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(4)').attr("class", "mouseover");
								$(temp).find('td:eq(4)').attr("onclick", "getDetail" + data);
							}
							if (statutory != 0) {
								var data = "(6,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(5)').attr("class", "mouseover");
								$(temp).find('td:eq(5)').attr("onclick", "getDetail" + data);
							}
							if (marital != 0) {
								var data = "(7,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(6)').attr("class", "mouseover");
								$(temp).find('td:eq(6)').attr("onclick", "getDetail" + data);
							}
							if (funeral != 0) {
								var data = "(8,'" + record[i]["accountName"] + "');";
								$(temp).find('td:eq(7)').attr("class", "mouseover");
								$(temp).find('td:eq(7)').attr("onclick", "getDetail" + data);
							}
						}
					},
					error: function () {
						confirm("訊息", "取得差勤資料失敗");
					}
				});
			};

			function getDetail (Type, name) {
				timeoutCheck();
				var year = $("#year").val();
				if (Type == 3) {
					$("#detailLabel").html("年度特休紀錄");
					var type = "特休";
				}
				if (Type == 4) {
					$("#detailLabel").html("年度事假紀錄");
					var type = "事假";
				}
				if (Type == 5) {
					$("#detailLabel").html("年度病假紀錄");
					var type = "病假";
				}
				if (Type == 6) {
					$("#detailLabel").html("年度公假紀錄");
					var type = "公假";
				}
				if (Type == 7) {
					$("#detailLabel").html("年度婚假紀錄");
					var type = "婚假";
				}
				if (Type == 8) {
					$("detailLabel").html("年度喪假紀錄");
					var type = "喪假";
				}
				$.ajax({
					type: 'post',
					url: "/rest/attRecord/getDetails",
					datatype: 'json',
					data: {
						type: Type,
						name: name,
						year: year
					},
					success: function (data) {
						var record = JSON.parse(data);
						var t = $('#detailTable').DataTable();
						t.clear().draw();
						for (var i = 0; i < record.length; i++) {
							var temp = t.row.add([
								type,
								record[i]["start"],
								record[i]["end"],
								record[i]["status"],
								record[i]["time"],
								record[i]["note"].replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;")
							])
								.draw().nodes().to$();
							$(temp).find('td:eq(4)').attr("style", "word-wrap:break-word;word-break:break-all;");
							$(temp).find('td:eq(4)').linkify();
						};
						$("#detailModal").modal("show");
					},
					error: function () {
						confirm("訊息", "取得詳細差勤資料失敗");
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
						<div class="card-header">差勤記錄一覽</div>
						<div class="card-body">
							<div class="table-responsive">
								<div style="width: 100%; height: 4rem;" class="d-flex align-items-center">
									<div class="mr-auto p-3">
										<input id="toggle" type="checkbox" checked data-toggle="toggle"
											data-onstyle="info" data-offstyle="warning" data-on="差勤紀錄" data-off="現時特休檢閱"
											data-size="large" data-width="170" data-height="40">
									</div>
									<label id='yearSearLa' style="margin-top: 0.6%"> 檢視年份: </label>
									<select id='year' style="margin-left: 1%; margin-right: 1.5%; width: 20%">
										<option style="display: none;"
											value='<%out.print(session.getAttribute("Year"));%>'>
											<% out.print(session.getAttribute("Year")); %>
										</option>
										<% int year=Integer.parseInt((String) session.getAttribute("Year")); for (int
											i=2016; i <=year; i++) { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
									</select>

								</div>

								<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th style="display:none;">帳號</th>
											<th>員工姓名</th>
											<th>部門</th>
											<th>特休</th>
											<th>事假</th>
											<th>病假</th>
											<th>公假</th>
											<th>婚假</th>
											<th>喪假</th>
										</tr>
									</thead>
									<tbody id="tbody">

									</tbody>
								</table>
								<table class="table table-bordered" id="dataTable2" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th>員工名稱</th>
											<th>部門</th>
											<th>排休時數</th>
											<th>已休時數</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
				<jsp:include page="footer.jsp" />
				<!-- Scroll to Top Button-->
				<a class="scroll-to-top rounded" href="#page-top"> <i class="fa fa-angle-up"></i>
				</a>
			</div>
		</div>
		<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
			<div class="modal-dialog">
				<img src="./Style/images/loading.gif" style="padding-top: 12rem;">
			</div>
		</div>

		<div class="modal fade" id="detailModal" tabindex="-1" role="dialog" aria-labelledby="detailLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="detailLabel"></h5>
						<button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body">
						<table class="table table-bordered" id="detailTable" width="100%" cellspacing="0">
							<thead>
								<tr bgcolor="#E6E6E6">
									<th>種類</th>
									<th>起始時間</th>
									<th>結束時間</th>
									<th>狀態</th>
									<th>時數</th>
									<th>備註</th>
								</tr>
							</thead>
							<tbody id="tbody">

							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="JSfooter.jsp" />
	</body>

	</html>