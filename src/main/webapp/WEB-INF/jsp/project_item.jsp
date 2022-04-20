<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="en">

	<head>
		<jsp:include page="header.jsp" />
		<style type="text/css">
			body {
				display: none;
				/*全部先隱藏*/
			}
		</style>
		<script type="text/javascript">
			var isFinishLoad = false;
			var account = '<%out.print(session.getAttribute("Account"));%>';
			var authorise = "<%= session.getAttribute("Authorise") %>";
			var items;
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

				getProjects();
				$('#dataTable').DataTable({
					"bAutoWidth": false,
					"bSort": false,
					"searching": false,
					"bLengthChange": false,
					"pageLength": 10,
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

				$('#detailTable').DataTable({
					"bAutoWidth": false,
					"bSort": false,
					"searching": false,
					"bLengthChange": false,
					"pageLength": 10,
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
				$("body").show();

				$("#filter").change(function () {
					doFilter();
				});

				$("#infinity").change(function () {
					if ($("#infinity").prop("checked")) {
						$("#hour").attr("disabled", true);
						$("#hour").val("");
					} else {
						$("#hour").removeAttr("disabled");
						$("#hour").val(0);
					}
				});
			});

			function getRecords() {
				timeoutCheck();
				$.ajax({
					type: 'post',
					url: "/rest/projectItem/getRecords",
					datatype: 'json',
					success: function (data) {
						var response = JSON.parse(data);
						items = JSON.parse(response.entity);
						doFilter();
					},
					error: function () {
						confirm("訊息", "取得公告失敗");
					}
				});
			};

			function doFilter() {
				var record = items;
				var t = $('#dataTable').DataTable();
				var projectId = $("#filter").val();
				if (projectId != "all") {
					record = items.filter(function (item, index, array) {
						return item.projectId == projectId;
					});
				}
				t.clear().draw();
				for (var i = 0; i < record.length; i++) {
					var status = "未簽核";
					var action;
					var width = 8;
					if (record[i]["status"] == "SIGNED") {
						status = "已簽核";
						if (account == record[i]["projectPM"] || authorise == 1) {
							width = 15;
							action = "<button class='signbutton' type='button' style='background-color:#007bff59' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "編輯" + "</button>";
							action += "<button class='signbutton' type='button' style='margin-left:5px' onclick='detailShow(" + JSON.stringify(record[i]) + ")'>" + "詳細" + "</button>";
						} else {
							action = "<button class='signbutton' type='button' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "檢視" + "</button>";
						}
					} else if (record[i]["status"] == "DELETED") {
						status = "已刪除";
						action = "<button class='signbutton' type='button' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "檢視" + "</button>";
						if (account == record[i]["projectPM"] || authorise == 1) {
							width = 15;
							action += "<button class='signbutton' type='button' style='margin-left:5px' onclick='detailShow(" + JSON.stringify(record[i]) + ")'>" + "詳細" + "</button>";
						}
					} else {
						if (account == record[i]["projectPM"] || authorise == 1) {
							width = 15;
							action = "<button class='signbutton' type='button' style='background-color:#35d02ba6;width:6rem;' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "簽核/刪除" + "</button>";
						}
						else if (account == record[i]["creator"]) {
							action = "<button class='signbutton' type='button' style='background-color:#007bff59' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "編輯" + "</button>";
						}
						else {
							action = "<button class='signbutton' type='button' onclick='editShow(" + JSON.stringify(record[i]) + ")'>" + "檢視" + "</button>";
						}
					}
					var estimateHour = "不設限";
					var actualHour = parseFloat(record[i]["actualHour"]);
					if (record[i]["estimateHour"].length != 0) {
						estimateHour = record[i]["estimateHour"];
						if (record[i]["estimateHour"] < record[i]["actualHour"]) {
							actualHour = "<font color='red'>" + actualHour + "(超時)" + "</font>";
						}
					}
					var temp = t.row.add([
						record[i]["item"],
						estimateHour,
						actualHour,
						status,
						action
					])
						.draw().nodes().to$();
					$(temp).find('td:eq(4)').attr("style", "text-align:center;width:" + width + "%;");
				}
			}

			function getProjects() {
				timeoutCheck();
				$.ajax({
					type: 'post',
					url: "/rest/projectItem/getProjects",
					datatype: 'json',
					success: function (data) {
						var response = JSON.parse(data);
						var record = JSON.parse(response.entity);
						var visibleCount = 0;
						for (var i = 0; i < record.length; i++) {
							if (record[i]["visible"] == "Y") {
								$("#filter").append('<option value="' + record[i]["id"] + '">' + record[i]["name"] + '</option>');
								$('#project').append('<option value="' + record[i]["id"] + '">' + record[i]["name"] + '</option>');
								visibleCount++;
							} else {
								$('#project').append('<option hidden value="' + record[i]["id"] + '">' + record[i]["name"] + '</option>');
							}
						}
						if (visibleCount == 0) {
							$("#filter").append('<option>' + '無' + '</option>');
						} else {
							getRecords();
						}
					},
					error: function () {
						confirm("訊息", "取得專案資料失敗");
					}
				});
			}

			function addShow() {
				$("#modelLabel").html("新增項目");
				$('#project').val(0);
				$("#name").val("");
				$("#hour").removeAttr("disabled");
				$("#hour").val(0);
				$("#infinity").prop("checked", false);
				$("#infinity").removeAttr("disabled");
				$(".add").show();
				$(".edit").hide();
				$(".sign").hide();
				$("#creator").hide();
				$("#signer").hide();
				$("#project").removeAttr("disabled");
				$("#name").removeAttr("disabled");
				$("#itemModal").modal("show");
			}

			function editShow(record) {
				$("#id").val(record["id"]);
				$("#version").val(record["version"]);
				$("#project").val(record["projectId"]);
				$("#name").val(record["item"]);
				$("#hour").val(record["estimateHour"]);
				var status = "未簽核";
				if (record["status"] == "SIGNED") {
					status = "已簽核";
				} else if (record["status"] == "DELETED") {
					status = "已刪除";
				}
				$("#status").html(status);
				$("#creator").html(record["creator"]);
				$("#creator").show();
				if (record["signer"].length == 0) {
					$("#signer").hide();
				} else {
					$("#signer").html(record["signer"]);
					$("#signer").show();
				}
				$(".add").hide();
				$("#project").removeAttr("disabled");
				$("#name").removeAttr("disabled");
				$("#hour").removeAttr("disabled");
				$("#infinity").removeAttr("disabled");
				if (record["estimateHour"].length != 0) {
					$("#infinity").prop("checked", false);
				} else {
					$("#infinity").prop("checked", true);
					$("#hour").attr("disabled", "disabled");
				}
				if (account != record["creator"] && account != record["projectPM"] && authorise != 1 || record["status"] == "DELETED") {
					$("#modelLabel").html("檢視項目");
					$("#project").attr("disabled", "disabled");
					$("#name").attr("disabled", "disabled");
					$("#hour").attr("disabled", "disabled");
					$("#infinity").attr("disabled", "disabled");
					$(".add").hide();
					$(".edit").hide();
					$(".sign").hide();
				} else if (record["status"] != "SIGNED") {
					if (account != record["projectPM"] && authorise != 1) {
						$("#modelLabel").html("編輯項目");
						$(".edit").show();
						$(".sign").hide();
					}
					else {
						$("#modelLabel").html("簽核項目");
						$(".edit").hide();
						$(".sign").show();
						$("#project").attr("disabled", "disabled");
						$("#name").attr("disabled", "disabled");
						$("#hour").attr("disabled", "disabled");
						$("#infinity").attr("disabled", "disabled");
					}
				} else {
					$(".sign").hide();
					$("#project").attr("disabled", "disabled");
					if (account == record["projectPM"] || authorise == 1) {
						$("#modelLabel").html("編輯項目");
						$(".edit").show();
					} else {
						$("#modelLabel").html("檢視項目");
						$(".edit").hide();
						$("#name").attr("disabled", "disabled");
						$("#hour").attr("disabled", "disabled");
						$("#infinity").attr("disabled", "disabled");
					}
				}
				$("#itemModal").modal("show");
			}

			function update(status) {
				var id = $("#id").val();
				var version = $("#version").val();
				var project = $("#project").val();
				var name = $("#name").val();
				var hour = $("#hour").val();
				if (project == 0) {
					confirm("訊息", "請選擇專案");
					return;
				}
				if (name.length == 0) {
					confirm("訊息", "請填寫項目名稱");
					return;
				}
				if (!$("#infinity").prop("checked")) {
					if (hour == 0) {
						confirm("訊息", "請填寫預估時數");
						return;
					}
				}
				$("button").attr("disabled", true);
				if (status == "DELETED") {
					$.confirm({
						title: '刪除確認',
						content: '確定刪除此專案項目?',
						buttons: {
							"刪除": {
								btnClass: 'btn-red',
								action: function () {
									$("button").attr("disabled", true);
									timeoutCheck();
									$.ajax({
										type: 'post',
										url: "/rest/projectItem/update",
										datatype: 'json',
										data: {
											id: id,
											version: version,
											status: status,
											project: project,
											name: name,
											hour: hour
										},
										success: function (data) {
											$("button").removeAttr("disabled");
											var response = JSON.parse(data).entity;
											confirm("訊息", response);
											$("#itemModal").modal("hide");
											getRecords();
											getUnsign();
										},
										error: function () {
											$("button").removeAttr("disabled");
											confirm("訊息", "更新專案項目失敗");
										}
									});
								}
							}
							,
							"取消": {
								action: function () {
									$("button").removeAttr("disabled");
									return;
								}
							}
						}
					});
				} else {
					timeoutCheck();
					$.ajax({
						type: 'post',
						url: "/rest/projectItem/update",
						datatype: 'json',
						data: {
							id: id,
							version: version,
							status: status,
							project: project,
							name: name,
							hour: hour
						},
						success: function (data) {
							$("button").removeAttr("disabled");
							var response = JSON.parse(data).entity;
							confirm("訊息", response);
							$("#itemModal").modal("hide");
							getRecords();
							getUnsign();
						},
						error: function () {
							$("button").removeAttr("disabled");
							confirm("訊息", "更新專案項目失敗");
						}
					});
				}
			}

			function detailShow(record) {
				var title = record["projectName"] + " - " + record["item"] + " 工時詳細資料";
				$("#detailTitle").html(title);
				$.ajax({
					type: 'post',
					url: "/rest/projectItem/getDetail",
					datatype: 'json',
					data: {
						id: record["id"]
					},
					success: function (data) {
						var response = JSON.parse(data);
						var record = JSON.parse(response.entity);
						var t = $('#detailTable').DataTable();
						t.clear().draw();
						for (var i = 0; i < record.length; i++) {
							var temp = t.row.add([
								record[i]["name"],
								record[i]["date"],
								record[i]["hour"],
								record[i]["sort"],
								record[i]["note"]
							])
								.draw().nodes().to$();
						}
						$("#detailModal").modal("show");
						$(temp).find('td:eq(4)').attr("style", "overflow-wrap: break-word; word-break: break-all;");
					},
					error: function () {
						confirm("訊息", "取得專案資料失敗");
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
						<div class="card-header">專案項目管理</div>
						<div class="card-body">
							<div style="width: 100%; height: 4rem;" class="d-flex align-items-center">
								<div class="mr-auto p-3">
									<button class="userbutton" onclick="addShow()">新增</button>
								</div>
								<div class="p-2">
									<select id="filter" style="width:20rem;">
									</select>
								</div>
							</div>
							<div class="table-responsive">
								<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th>專案項目</th>
											<th>預估工時</th>
											<th>實際工時</th>
											<th>狀態</th>
											<th style="text-align: center">操作</th>
										</tr>
									</thead>
									<tbody id="tbody">

									</tbody>
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
			data-backdrop='static' style="text-align: center;width:100%;height:100%;padding-left:0px;">
			<div class="modal-dialog">
				<img src="./Style/images/loading.gif" style="padding-top:12rem;">
			</div>
		</div>
		<div class="modal fade" id="itemModal" tabindex="-1" role="dialog" aria-labelledby="editLabel"
			aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="modelLabel"></h5>
						<button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body">
						<input id="id" type="text" disabled="disabled" style="display: none">
						<input id="version" type="text" disabled="disabled" style="display: none">
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">專案
								: </span> <select id='project' style="margin-left: 3%; margin-right: 1.5%; width: 65%">
								<option style="display:none;" value="0" selected>請選擇專案</option>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">專案項目:
							</span> <input id="name" type="text"
								style="margin-left: 3%; margin-right: 1.5%; width: 50%">
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">預估工時:
							</span> <input id="hour" type="number"
								style="margin-left: 3%; margin-right: 1.5%; width: 15%"
								oninput="this.value = Math.abs(this.value);if(value.length>6)value=value.slice(0,6);">
							<input type="checkbox" id="infinity">不設限
						</p>
						<p style="padding-top: 1%;" id="status">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">狀態:
							</span> <span class="short_label status"
								style="margin-left: 3%; margin-right: 1.5%; width: 50%"></span>
						</p>
						<p style="padding-top: 1%;" id="creator">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">建立人:
							</span> <span class="short_label creator"
								style="margin-left: 3%; margin-right: 1.5%; width: 50%"></span>
						</p>
						<p style="padding-top: 1%;" id="signer">
							<span class="short_label"
								style="text-align: right; width: 25%; height: 2rem; display: block; vetical-align: top; float: left;">簽核人:
							</span> <span class="short_label signer"
								style="margin-left: 3%; margin-right: 1.5%; width: 50%"></span>
						</p>
					</div>
					<div class="modal-footer">
						<div class="add">
							<button class="btn btn-secondary" type="button" data-dismiss="modal"
								style="margin-right: 5px;">取消</button>
							<button class="btn btn-primary" type="button" onclick="update('CREATED')">新增</button>
						</div>
						<div class="edit">
							<button class="btn btn-secondary" type="button" data-dismiss="modal"
								style="margin-right: 5px;">取消</button>
							<button class="btn btn-danger" type="button" onclick="update('DELETED')"
								style="margin-right: 5px;">刪除</button>
							<button class="btn btn-primary" type="button" onclick="update('UPDATED')">更新</button>
						</div>
						<div class="sign">
							<button class="btn btn-secondary" type="button" onclick="punchRejectDivHide()"
								style="margin-right: 5px;">取消</button>
							<button class="btn btn-danger" type="button" onclick="update('DELETED')"
								style="margin-right: 5px;">刪除</button>
							<button class="btn btn-success" type="button" onclick="update('SIGNED')">簽核</button>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="modal fade" id="detailModal" tabindex="-1" role="dialog" aria-labelledby="detailLabel"
			aria-hidden="true">
			<div class="modal-dialog modal-lg" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="detailTitle"></h5>
						<button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body">
						<table class="table table-bordered" id="detailTable" width="100%" cellspacing="0">
							<thead>
								<tr bgcolor="#E6E6E6">
									<th>姓名</th>
									<th>資料日期</th>
									<th>工時</th>
									<th>產出物</th>
									<th style="width: 35%">說明</th>
								</tr>
							</thead>
							<tbody>

							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="JSfooter.jsp" />
	</body>

	</html>