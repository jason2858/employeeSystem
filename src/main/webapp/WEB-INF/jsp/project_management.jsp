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

			.selectBox {
				display: inline-block;
				height: 30px;
				width: 140px;
				padding: 2px 10px 2px 2px;
				outline: none;
				color: #74646e;
				border: 1px solid #C8BFC4;
				border-radius: 4px;
				box-shadow: inset 1px 1px 2px #ddd8dc;
				background: #fff;
			}

			td:hover {
				cursor: pointer;
			}
		</style>
		<script type="text/javascript">
			var isFinishLoad = false;
			var account = '<%out.print(session.getAttribute("Account"));%>';
			var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
			var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
			$(document).ready(
				function () {

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

					if (authorise == '2' || authorise == '4') {
						document.getElementById("userFilter").hidden = true;
						document.getElementById("addAttInfoBtn").hidden = true;
					} else if (authorise == '3') {
						document.getElementById("userFilter").hidden = true;
					} else {
						document.getElementById("userFilter").hidden = false;
						document.getElementById("addAttInfoBtn").hidden = false;
					}

					$.ajax({
						type: "POST",
						url: '/rest/project/getProjectType',
						success: function (data) {
							$("#typeDropdown").get(0).options.length = 0;
							$("#typeDropdown").get(0).options[0] = new Option("Select ...", "-1");

							$.each(data, function (index, item) {
								$("#typeDropdown").get(0).options[$("#typeDropdown").get(0).options.length] = new Option(item.name, item.id);
							});

							$("#typeDropdownEdit").get(0).options.length = 0;
							$("#typeDropdownEdit").get(0).options[0] = new Option("Select ...", "-1");

							$.each(data, function (index, item) {
								$("#typeDropdownEdit").get(0).options[$("#typeDropdownEdit").get(0).options.length] = new Option(item.name, item.id);
							});
						}
					});
					$.ajax({
						type: "POST",
						url: '/rest/project/getEmpList',
						dataType: "json",
						data: { Live: "Y" },
						success: function (data) {
							data = JSON.parse(data.entity);

							if (nameSelect == "TW") {
								$("#projectPmEdit").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#projectPmEdit").get(0).options[$("#projectPmEdit").get(0).options.length] = new Option(item.chineseName, item.name);
								});
								$("#projectPm").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#projectPm").get(0).options[$("#projectPm").get(0).options.length] = new Option(item.chineseName, item.name);
								});
								$("#userFilter").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#userFilter").get(0).options[$("#userFilter").get(0).options.length] = new Option(item.chineseName, item.name);
								});
							} else {
								$("#projectPmEdit").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#projectPmEdit").get(0).options[$("#projectPmEdit").get(0).options.length] = new Option(item.name, item.name);
								});
								$("#projectPm").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#projectPm").get(0).options[$("#projectPm").get(0).options.length] = new Option(item.name, item.name);
								});
								$("#userFilter").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function (index, item) {
									$("#userFilter").get(0).options[$("#userFilter").get(0).options.length] = new Option(item.name, item.name);
								});
							}
						}
					});

					$.ajax({
						type: "POST",
						url: '/rest/project/getCustomer',
						success: function (data) {
							$("#siDropdown").get(0).options.length = 0;
							$("#siDropdown").get(0).options[0] = new Option("Select ...", "-1");
							$("#endUserDropdown").get(0).options.length = 0;
							$("#endUserDropdown").get(0).options[0] = new Option("Select ...", "-1");
							$("#siDropdownEdit").get(0).options.length = 0;
							$("#siDropdownEdit").get(0).options[0] = new Option("Select ...", "-1");
							$("#endUserDropdownEdit").get(0).options.length = 0;
							$("#endUserDropdownEdit").get(0).options[0] = new Option("Select ...", "-1");

							$.each(data, function (index, item) {
								if (item.status == "signed") {
									$('#siDropdown').append('<option value="' + item.id + '">' + item.name + '</option>');
									$('#endUserDropdown').append('<option value="' + item.id + '">' + item.name + '</option>');
									$('#siDropdownEdit').append('<option value="' + item.id + '">' + item.name + '</option>');
									$('#endUserDropdownEdit').append('<option value="' + item.id + '">' + item.name + '</option>');
								} else {
									$('#siDropdown').append('<option hidden value="' + item.id + '">' + item.name + '</option>');
									$('#endUserDropdown').append('<option hidden value="' + item.id + '">' + item.name + '</option>');
									$('#siDropdownEdit').append('<option hidden value="' + item.id + '">' + item.name + '</option>');
									$('#endUserDropdownEdit').append('<option hidden value="' + item.id + '">' + item.name + '</option>');
								}
							});



							$.each(data, function (index, item) {

							});
						}
					});

					$('#dataTable').DataTable({
						"bLengthChange": false,
						"pageLength": 10,
						"oLanguage": {
							"sProcessing": "讀取中...",
							"sLengthMenu": "Show _MENU_ entries",
							"sZeroRecords": "查無相符的資料",
							"sEmptyTable": "暫無專案",
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
						"bAutoWidth": false,
						"bPaginate": true,
						"retrieve": true,
						"bInfo": true,
						"bSort": true,
						"searching": true,
						"columns": [{
							"data": "專案ID"
						}, {
							"data": "專案名稱"
						}, {
							"data": "類別"
						}, {
							"data": "PM"
						}, {
							"data": "開發狀態"
						}, {
							"data": "預估工時"
						}, {
							"data": "實際工時"
						}],
						"order": [[4, "desc"], [2, "desc"]],
						"columnDefs": [
							{ "orderable": false, "targets": 1 },
							{ "orderable": false, "targets": 3 },
							{ "orderable": false, "targets": 5 },
							{ "orderable": false, "targets": 6 }
						],
					});

					getRecords();

					$("body").show();

				});


			function getRecords() {
				timeoutCheck();
				var i = 0;

				var dataTable = $('#dataTable').DataTable();
				dataTable.clear().draw();

				$.ajax({
					type: "POST",
					cache: false,
					contentType: "application/json;charset=UTF-8",
					url: '/rest/project/getAllProject',
					success: function (data) {
						$.each(data, function () {
							var projectId = data[i].id;
							var name = data[i].projectName;
							var type = data[i].typeDropdown;
							var pm = data[i].projectPm;
							var devStatus = $("#devStatus option[value=" + data[i].devStatus + "]").text();
							var estimateHour = data[i].estimateHour;
							var actualHour = data[i].actualHour;
							// initiate datatable row info 
							var temp = dataTable.rows.add([{
								"專案ID": projectId,
								"專案名稱": name,
								"類別": type,
								"PM": pm,
								"開發狀態": devStatus,
								"預估工時": estimateHour,
								"實際工時": actualHour
							}]).draw().nodes().to$();

							$(temp).attr("data-toggle", "modal");
							$(temp).attr("data-target", "#UpdateAndDelete");
							$(temp).addClass("open-UpdateDeleteDialog");

							//initiate data-xxx, class attribute of tr 
							//$(temp).attr("data-id", id);			

							//initiate data-xxx attribute of td 
							//var createdAtDT = data[i].createdAt;

							$(temp).find('td:eq(0)').attr('data-id', projectId);
							$(temp).find('td:eq(0)').attr("style", "width:1%;padding-left:2%;display:none;");
							$(temp).find('td:eq(1)').attr('data-name', name);
							$(temp).find('td:eq(1)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(2)').attr('data-type', type);
							$(temp).find('td:eq(2)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(3)').attr('data-pm', pm);
							$(temp).find('td:eq(3)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(4)').attr('data-devStatus', devStatus);
							$(temp).find('td:eq(4)').attr("style", "padding-left:2%;");
							i++;
						});

						setUserFilterDefaultVal();

					},
					error: function (data) {
						confirm("訊息", "取得專案資料失敗");
					}
				});

			};

			function setUserFilterDefaultVal() {

				if (authorise == 4) {
					$("#userFilter").css("display", "none");
				}

			}

			function addPro() {
				timeoutCheck();
				var projectName = document.getElementById("projectName").value;
				var typeDropdown = document.getElementById("typeDropdown").value;
				var projectPm = document.getElementById("projectPm").value;
				var siDropdown = document.getElementById("siDropdown").value;
				var endUserDropdown = document.getElementById("endUserDropdown").value;

				if (projectName == undefined || projectName.trim() == '') {
					confirm("訊息", "請填寫專案名稱");
					return;
				}
				if (typeDropdown == '-1') {
					confirm("訊息", "請選擇類別");
					return;
				}
				if (projectPm == '-1') {
					confirm("訊息", "請選擇專案負責人");
					return;
				}
				if (siDropdown == '-1') {
					confirm("訊息", "請選擇客戶名稱");
					return;
				}

				//initiate json object
				var pro_data = {
					"projectName": projectName,
					"typeDropdown": typeDropdown,
					"projectPm": projectPm,
					"siDropdown": siDropdown,
					"endUserDropdown": endUserDropdown,
				};
				$.ajax({
					type: "POST",
					url: '/rest/project/insertProject',
					dataType: "text",
					contentType: 'application/json;charset=UTF-8',
					data: JSON.stringify(pro_data),
					success: function (data) {
						confirm("訊息", "新增成功");
						$("#projectName").val("");
						$("#typeDropdown").val("-1");
						$("#projectPm").val("-1");
						$("#siDropdown").val("-1");
						$("#endUserDropdown").val("-1");
						$("#MakeUp").modal("hide");
						getRecords();
						getUnsign();
					},
					error: function (data) {
						confirm("訊息", "新增失敗");
					}
				});

			};

			//unlock option after send
			function sendAble() {
				document.getElementById("close").disabled = false;
				document.getElementById("typeInsert").disabled = false;
			};

			function updProInfo() {
				//when update, user can not change input!
				//disableUpdField();
				//var project = document.getElementById("projectNameEdit").value;
				timeoutCheck();
				var projectId = document.getElementById("projectIdEdit").value;
				var updatedAt = document.getElementById("projectUpdatedAt").value;
				var projectName = document.getElementById("projectNameEdit").value;
				var typeDropdown = document.getElementById("typeDropdownEdit").value;
				var projectPm = document.getElementById("projectPmEdit").value;
				var siDropdown = document.getElementById("siDropdownEdit").value;
				var endUserDropdown = document.getElementById("endUserDropdownEdit").value;
				var devStatus = document.getElementById("devStatus").value;

				if (projectName == undefined || projectName.trim() == '') {
					confirm("訊息", "請填寫專案名稱");
					return;
				}
				if (typeDropdown == '-1') {
					confirm("訊息", "請選擇類別");
					return;
				}
				if (projectPm == '-1') {
					confirm("訊息", "請選擇專案負責人");
					return;
				}
				if (siDropdown == '-1') {
					confirm("訊息", "請選擇客戶名稱");
					return;
				}

				//initiate json object
				var update_pro_data = {
					"id": projectId,
					"projectName": projectName,
					"typeDropdown": typeDropdown,
					"projectPm": projectPm,
					"siDropdown": siDropdown,
					"endUserDropdown": endUserDropdown,
					"devStatus": devStatus,
					"updatedAt": updatedAt
				};

				$.ajax({
					type: "POST",
					url: '/rest/project/updateProject',
					dataType: "text",
					contentType: 'application/json;charset=UTF-8',
					data: JSON.stringify(update_pro_data),
					success: function (data) {
						confirm("訊息", JSON.parse(data).entity);
						$("#UpdateAndDelete").modal("hide");
						getRecords();
					},
					error: function (data) {
						confirm("訊息", "修改失敗");
					}
				});

			};

			function disableUpdField() {

				document.getElementById("closeUandD").disabled = true;
				document.getElementById("typeUandD").disabled = true;

			}

			//unlock option after upadte
			function updateAble() {
				document.getElementById("closeUandD").disabled = false;
				document.getElementById("typeUandD").disabled = false;
			};

			function delProInfo() {
				var projectId = document.getElementById("projectIdEdit").value;
				var updatedAt = document.getElementById("projectUpdatedAt").value;
				//initiate json object
				var delproject_data = {
					"id": projectId,
					"updatedAt": updatedAt
				};
				$.confirm({
					title: '刪除確認',
					content: '確定刪除此專案?',
					buttons: {
						"刪除": {
							btnClass: 'btn-red',
							action: function () {
								timeoutCheck();
								$.ajax({
									type: "POST",
									url: '/rest/project/deleteProject',
									dataType: "text",
									contentType: 'application/json;charset=UTF-8',
									data: JSON.stringify(delproject_data),
									success: function (data) {
										confirm("訊息", JSON.parse(data).entity);
										$("#UpdateAndDelete").modal("hide");
										getRecords();
										getUnsign();
									},
									error: function (data) {
										confirm("訊息", "刪除失敗");
									}
								});
							}
						}
						,
						"取消": {
							action: function () {
								return;
							}
						}
					}
				});
			};

			function sign() {
				var projectId = document.getElementById("projectIdEdit").value;
				var updatedAt = document.getElementById("projectUpdatedAt").value;
				//initiate json object
				var signproject_data = {
					"id": projectId,
					"updatedAt": updatedAt
				};
				$.confirm({
					title: '簽核確認',
					content: '確定簽核此專案?',
					buttons: {
						"簽核": {
							btnClass: 'btn-green',
							action: function () {
								timeoutCheck();
								$.ajax({
									type: "POST",
									url: '/rest/project/signProject',
									dataType: "text",
									contentType: 'application/json;charset=UTF-8',
									data: JSON.stringify(signproject_data),
									success: function (data) {
										confirm("訊息", JSON.parse(data).entity);
										$("#UpdateAndDelete").modal("hide");
										getRecords();
										getUnsign();
									},
									error: function (data) {
										confirm("訊息", "簽核失敗");
									}
								});
							}
						},
						"取消": {
							action: function () {
								return;
							}
						}
					}
				});
			}

			//filter controller
			$(function () {
				$("#userFilter").change(function () {
					var user;
					if (nameSelect == "TW") {
						user = $("#userFilter option:selected").text();
					} else {
						user = $("#userFilter").val();
					}
					var table = $('#dataTable').DataTable();
					$.fn.dataTable.ext.search.splice(0);//先刪除全部
					if ($("#userFilter").val() != '-1') {
						$.fn.dataTable.ext.search.push(
							function (settings, data, dataIndex) {
								return (data[3] == user)
									? true
									: false
							}
						);
					} else {
						$.fn.dataTable.ext.search.push(
							function (settings, data, dataIndex) {
								return true;
							}
						);
					}
					table.draw();
				});
			});

			function encodeHTML(s) {
				if (s === '' || s == null) return '';
				return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;').replace(/>/g, '&gt;').replace(/'/g, '&#x27;');
			}

			$(document).on("click", ".open-UpdateDeleteDialog", function () {
				timeoutCheck();
				$.ajax({
					type: 'POST',
					cache: false,
					url: "/rest/project/getProject?id=" + $(this).find('td').eq(0).data('id'),
					contentType: "application/json;charset=UTF-8",
					dataType: "json",
					success: function (data) {//塞資料
						document.getElementById("projectIdEdit").value = data.id;
						document.getElementById("projectUpdatedAt").value = data.updatedAt;
						document.getElementById("projectNameEdit").value = data.name;
						document.getElementById("typeDropdownEdit").value = data.type;
						if (nameSelect == "TW") {
							var pmVal = $('#projectPmEdit option').filter(function () {
								return $(this).text() === data.pm.chineseName;
							}).val();
							$("#projectPmEdit").val(pmVal);
						} else {
							$("#projectPmEdit").val(data.pm.username);
						}
						document.getElementById("siDropdownEdit").value = data.siId;
						document.getElementById("endUserDropdownEdit").value = data.endUserId;
						document.getElementById("devStatus").value = data.devStatus;
						$("#devStatus").attr('disabled', true);
						if (data.devStatus == "deleted") {
							lock(true);
							$("#deleteBtn").hide();
							$("#editBtn").hide();
							$("#signBtn").hide();
						} else if (authorise == 1) {
							$("#deleteBtn").show();
							if (data.devStatus == "unsign") {
								$("#signBtn").show();
								if (data.pm == account) {
									$("#editBtn").show();
									lock(false);
								} else {
									$("#editBtn").hide();
									lock(true);
								}
							} else {
								$("#devStatus").attr('disabled', false);
								$("#editBtn").show();
								$("#signBtn").hide();
								lock(false);
							}
						} else {
							$("#signBtn").hide();
							if (data.pm == account && data.devStatus == "unsign") {
								$("#deleteBtn").show();
								$("#editBtn").show();
								lock(false);
							} else {
								$("#deleteBtn").hide();
								$("#editBtn").hide();
								lock(true);
							}
						}

					},

					error: function () {
						confirm("訊息", "取得專案內容失敗");
					}
				});

			});

			$(document).on("click", "#addAttInfoBtn", function () {
				var i = 0;
				timeoutCheck();
				$("#projectPm").val("-1");
				$(".modal-body #type").val("1");
			});

			function autogrow(textarea) {
				var adjustedHeight = textarea.clientHeight;
				adjustedHeight = Math.max(textarea.scrollHeight, adjustedHeight);
				if (adjustedHeight > textarea.clientHeight) {
					textarea.style.height = adjustedHeight + 'px';
				}
			}

			function lock(bool) {
				$("#projectNameEdit").attr('disabled', bool);
				$("#typeDropdownEdit").attr('disabled', bool);
				$("#projectPmEdit").attr('disabled', bool);
				$("#siDropdownEdit").attr('disabled', bool);
				$("#endUserDropdownEdit").attr('disabled', bool);
				if (bool) {
					$("#modalTitle").html("檢視專案");
				} else {
					$("#modalTitle").html("修改專案");
				}
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
						<div class="card-header">專案管理</div>
						<div class="card-body">
							<div class="table-responsive">
								<div style="width: 100%; height: 4rem;" class="d-flex align-items-center">
									<div class="mr-auto p-3">
										<button class="userbutton" id="addAttInfoBtn" data-toggle="modal"
											data-target="#MakeUp">新增</button>
									</div>
									<div class="p-2">
										<select id="userFilter" class="selectBox"></select>
									</div>
								</div>
								<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th style="display: none;">專案ID</th>
											<th>專案名稱</th>
											<th>類別</th>
											<th>PM</th>
											<th>開發狀態</th>
											<th>預估工時</th>
											<th>實際工時</th>
										</tr>
									</thead>
									<tbody id="attendanceInfo">
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
		<div class="modal fade" id="MakeUp" tabindex="-1" role="dialog" aria-labelledby="MakeUpLabel" aria-hidden="true"
			data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="MakeUpLabel">新增專案</h5>
						<button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body add">
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>專案名稱 : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea onkeyup="autogrow(this);" id="projectName"
									style="resize: none; width: 50%; overflow: hidden;"></textarea></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>類別 : </span> <select id="typeDropdown"
								style="margin-left: 3%;"></select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>負責人 : </span> <select style="margin-left: 3%;"
								id="projectPm">
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>客戶名稱 : </span> <select id="siDropdown"
								style="margin-left: 3%;"></select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">EndUser
								: </span> <select id="endUserDropdown" style="margin-left: 3%;"></select>
						</p>

					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal"
							style="margin-right: 5px;">取消</button>
						<button class="btn btn-primary" type="button" onclick="addPro()">送出</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="UpdateAndDelete" tabindex="-1" role="dialog" aria-labelledby="MakeUpLabel"
			aria-hidden="true" data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="modalTitle"></h5>
						<button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body edit">
						<p style="padding-top: 1%; display: none">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>專案ID : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea rows="1" id="projectIdEdit"
									style="resize: none; width: 50%;"></textarea></span>
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>更新時間 : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea rows="1" id="projectUpdatedAt"
									style="resize: none; width: 50%;"></textarea></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>專案名稱 : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea onkeyup="autogrow(this);" id="projectNameEdit"
									style="resize: none; width: 50%; overflow: hidden;"></textarea></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>類別 : </span> <select id="typeDropdownEdit"
								style="margin-left: 3%;"></select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>負責人 : </span> <select style="margin-left: 3%;"
								id="projectPmEdit">
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>客戶名稱: </span> <select id="siDropdownEdit"
								style="margin-left: 3%;"></select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">EndUser
								: </span> <select id="endUserDropdownEdit" style="margin-left: 3%;"></select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">開發狀態
								: </span> <select id="devStatus" style="margin-left: 3%;">
								<option hidden value="unsign">未簽核</option>
								<option value="prepare">籌備期</option>
								<option value="develop">開發期</option>
								<option value="acceptance">交付期</option>
								<option value="close">已結案</option>
								<option hidden value="deleted">已刪除</option>
							</select>
						</p>
					</div>
					<div class="modal-footer">
						<button class="btn btn-danger" id="deleteBtn" type="button" onclick="delProInfo()"
							style="margin-right: 5px;">刪除</button>
						<button class="btn btn-primary" id="editBtn" type="button" onclick="updProInfo()"
							style="margin-right: 5px;">修改</button>
						<button class="btn btn-success" id="signBtn" type="button" onclick="sign()">簽核</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
			<div class="modal-dialog">
				<img src="./Style/images/loading.gif" style="padding-top: 12rem;">
			</div>
		</div>
		<jsp:include page="JSfooter.jsp" />
	</body>

	</html>