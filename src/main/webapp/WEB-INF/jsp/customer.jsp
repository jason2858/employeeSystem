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

			#deleteAttInfo {
				background-color: #f44336;
				border: #f44336;
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
			var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
			$(document).ready(
				function () {
					$('#loading').modal('show');
					$("#loading").on('shown.bs.modal', function () {
						isFinishLoad = true;
					});
					closeLoading();
					if (authorise != '1') {
						$("#signBtn").hide();
					}

					if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
						window.location.href = "timeout.do";
					}
					if (authorise == 1 || authorise == 2 || authorise == 3) {
						appendUnsign();
						getUnsign();
					}

					getRecords();

					$("body").show();

				});


			function getRecords() {
				timeoutCheck();
				var i = 0;

				var dataTable = $('#dataTable').DataTable({
					"bLengthChange": false,
					"pageLength": 10,
					"oLanguage": {
						"sProcessing": "讀取中...",
						"sLengthMenu": "Show _MENU_ entries",
						"sZeroRecords": "查無相符的資料",
						"sEmptyTable": "暫無客戶資料",
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
						"data": "ID"
					}, {
						"data": "客戶名稱"
					}, {
						"data": "類別"
					}, {
						"data": "連絡人"
					}, {
						"data": "連絡電話"
					}, {
						"data": "連絡mail"
					}, {
						"data": "狀態"
					}],
					"order": [[4, "desc"], [2, "desc"]],
					"columnDefs": [
						{ "orderable": false, "targets": 1 },
						{ "orderable": false, "targets": 3 }
					],
				});

				$.ajax({
					type: "POST",
					cache: false,
					contentType: "application/json;charset=UTF-8",
					url: '/rest/customer/getList',
					success: function (data) {
						$.each(data, function () {
							var id = data[i].id;
							var name = data[i].name;
							var type = data[i].type;
							var contactPerson = data[i].contactPerson;
							var contactPhone = data[i].contactPhone;
							var contactEmail = data[i].contactEmail;
							var status;
							if (data[i].status == "signed") {
								status = "已簽核";
							} else {
								status = "未簽核";
							}
							// initiate datatable row info 
							var temp = dataTable.rows.add([{
								"ID": id,
								"客戶名稱": name,
								"類別": $("#typeDropdown option[value='" + type + "']").text(),
								"連絡人": contactPerson,
								"連絡電話": contactPhone,
								"連絡mail": contactEmail,
								"狀態": status,
							}]).draw().nodes().to$();

							$(temp).attr("data-toggle", "modal");
							$(temp).attr("data-target", "#UpdateAndDelete");
							$(temp).addClass("open-UpdateDeleteDialog");

							//initiate data-xxx, class attribute of tr 
							//$(temp).attr("data-id", id);			

							//initiate data-xxx attribute of td 
							//var createdAtDT = data[i].createdAt;

							$(temp).find('td:eq(0)').attr('data-id', id);
							$(temp).find('td:eq(0)').attr("style", "width:1%;padding-left:2%;display:none;");
							$(temp).find('td:eq(1)').attr('data-name', name);
							$(temp).find('td:eq(1)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(2)').attr('data-type', type);
							$(temp).find('td:eq(2)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(3)').attr('data-contactPerson', contactPerson);
							$(temp).find('td:eq(3)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(4)').attr('data-contactPhone', contactPhone);
							$(temp).find('td:eq(4)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(4)').attr('data-contactEmail', contactEmail);
							$(temp).find('td:eq(4)').attr("style", "padding-left:2%;");
							i++;
						});

					},
					error: function (data) {
						confirm("訊息", "取得客戶資料失敗");
					}
				});

			};

			function save() {
				timeoutCheck();
				var customerName = document.getElementById("customerName").value;
				var typeDropdown = document.getElementById("typeDropdown").value;
				var info = document.getElementById("info").value;
				var ein = document.getElementById("ein").value;
				var contactPerson = document.getElementById("contactPerson").value;
				var contactPhone = document.getElementById("contactPhone").value;
				var contactEmail = document.getElementById("contactEmail").value;

				if (customerName == undefined || customerName.trim() == '') {
					confirm("訊息", '請填寫客戶名稱');
					return;
				}
				if (typeDropdown == '-1') {
					confirm("訊息", '請選擇類別');
					return;
				}

				var data = {
					"name": customerName,
					"type": typeDropdown,
					"info": info,
					"ein": ein,
					"contactPerson": contactPerson,
					"contactPhone": contactPhone,
					"contactEmail": contactEmail,
				};
				$.ajax({
					type: "POST",
					url: '/rest/customer/save',
					dataType: "text",
					contentType: 'application/json;charset=UTF-8',
					data: JSON.stringify(data),
					success: function (data) {
						confirmWithFunction("訊息", "新增成功", function () { window.location.href = "customer.do"; });
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

			function update() {
				timeoutCheck();
				var id = document.getElementById("idE").value;
				var updatedAt = document.getElementById("updated").value;
				var customerName = document.getElementById("customerNameE").value;
				var typeDropdown = document.getElementById("typeDropdownE").value;
				var info = document.getElementById("infoE").value;
				var ein = document.getElementById("einE").value;
				var contactPerson = document.getElementById("contactPersonE").value;
				var contactPhone = document.getElementById("contactPhoneE").value;
				var contactEmail = document.getElementById("contactEmailE").value;

				if (customerName == undefined || customerName.trim() == '') {
					confirm("訊息", '請填寫客戶名稱');
					return;
				}
				if (typeDropdown == '-1') {
					confirm("訊息", '請選擇類別');
					return;
				}

				//initiate json object
				var data = {
					"id": id,
					"updatedAt": updatedAt,
					"name": customerName,
					"type": typeDropdown,
					"info": info,
					"ein": ein,
					"contactPerson": contactPerson,
					"contactPhone": contactPhone,
					"contactEmail": contactEmail,
				};

				$.ajax({
					type: "POST",
					url: '/rest/customer/update',
					dataType: "text",
					contentType: 'application/json;charset=UTF-8',
					data: JSON.stringify(data),
					success: function (data) {
						confirmWithFunction("訊息", JSON.parse(data).entity, function () { window.location.href = "customer.do"; });
					},
					error: function (data) {
						confirm("訊息", "修改失敗");
					}
				});

			};

			function deleteCustomer() {
				var id = document.getElementById("idE").value;
				var name = document.getElementById("customerNameE").value;
				var updated = document.getElementById("updated").value;
				var data = {
					"id": id,
					"updatedAt": updated
				};

				$.confirm({
					title: '刪除',
					content: '確認刪除 ' + name + "?",
					buttons: {
						'確認': function () {
							timeoutCheck();
							$.ajax({
								type: "POST",
								url: '/rest/customer/delete',
								dataType: "text",
								contentType: 'application/json;charset=UTF-8',
								data: JSON.stringify(data),
								success: function (data) {
									confirmWithFunction("訊息", JSON.parse(data).entity, function () { window.location.href = "customer.do"; });
								},
								error: function (data) {
									confirm("訊息", "刪除失敗");
								}
							});
						},
						'取消': {
							action: function () {
								return;
							}
						}
					}
				});

			};

			function sign() {
				var id = document.getElementById("idE").value;
				var updated = document.getElementById("updated").value;
				var data = {
					"id": id,
					"updatedAt": updated
				};
				timeoutCheck();
				$.confirm({
					title: '簽核確認',
					content: '確定簽核此客戶?',
					buttons: {
						"簽核": {
							btnClass: 'btn-green',
							action: function () {
								timeoutCheck();
								$.ajax({
									type: "POST",
									url: '/rest/customer/sign',
									dataType: "text",
									contentType: 'application/json;charset=UTF-8',
									data: JSON.stringify(data),
									success: function (data) {
										confirmWithFunction("訊息", JSON.parse(data).entity, function () { window.location.href = "customer.do"; });
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
			};

			function lock(bool) {
				$("#customerNameE").attr('disabled', bool);
				$("#typeDropdownE").attr('disabled', bool);
				$("#infoE").attr('disabled', bool);
				$("#einE").attr('disabled', bool);
				$("#contactPersonE").attr('disabled', bool);
				$("#contactPhoneE").attr('disabled', bool);
				$("#contactEmailE").attr('disabled', bool);
				if (bool) {
					$("#editDiv").hide();
				} else {
					$("#editDiv").show();
				}
			}

			function encodeHTML(s) {
				if (s === '' || s == null) return '';
				return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;').replace(/>/g, '&gt;').replace(/'/g, '&#x27;');
			}

			$(document).on("click", ".open-UpdateDeleteDialog", function () {
				timeoutCheck();
				$.ajax({
					type: 'POST',
					cache: false,
					url: "/rest/customer/get?id=" + $(this).find('td').eq(0).data('id'),
					contentType: "application/json;charset=UTF-8",
					dataType: "json",
					success: function (data) {//塞資料
						document.getElementById("idE").value = data.id;
						document.getElementById("updated").value = data.updatedAt;
						document.getElementById("customerNameE").value = data.name;
						document.getElementById("typeDropdownE").value = data.type;
						document.getElementById("infoE").value = data.info;
						document.getElementById("einE").value = data.ein;
						document.getElementById("contactPersonE").value = data.contactPerson;
						document.getElementById("contactPhoneE").value = data.contactPhone;
						document.getElementById("contactEmailE").value = data.contactEmail;
						if (data.status == "signed" && authorise != 1) {
							lock(true);
						} else if (data.status == "signed") {
							$("#signBtn").hide();
						} else if (authorise == 1) {
							$("#signBtn").show();
						} else {
							lock(false);
						}
					},

					error: function () {
						confirm("訊息", "取得客戶內容失敗");
					}
				});

			});

			$(document).on("click", "#addAttInfoBtn", function () {
				var i = 0;
				timeoutCheck();
				$(".modal-body #type").val("1");
			});


		</script>
	</head>

	<body class="fixed-nav sticky-footer" id="page-top">
		<!-- Navigation-->
		<jsp:include page="navbar.jsp" />
		<div class="content-wrapper">
			<div class="container-fluid">
				<div class="container-fluid">
					<div class="card mb-3">
						<div class="card-header">客戶管理</div>
						<div class="card-body">
							<div class="table-responsive">
								<div style="width: 100%; height: 4rem;" class="d-flex align-items-center">
									<div class="mr-auto p-3">
										<button class="userbutton" id="addAttInfoBtn" data-toggle="modal"
											data-target="#MakeUp">新增</button>
									</div>
								</div>
								<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th style="display:none;">ID</th>
											<th>客戶名稱</th>
											<th>類別</th>
											<th>連絡人</th>
											<th>連絡電話</th>
											<th>連絡mail</th>
											<th>狀態</th>
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
						<h5 class="modal-title" id="MakeUpLabel">新增客戶</h5>
						<button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body add">
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>客戶名稱 : </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="customerName"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>類別
								: </span>
							<select id="typeDropdown" style="margin-left: 3%;">
								<option selected value="academic">學術</option>
								<option value="government">政府機關</option>
								<option value="mechanism">機構</option>
								<option value="enterprise">企業</option>
								<option value="other">其他</option>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">說明
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="info" style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">統一編號
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="ein" style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">連絡人
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactPerson"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">聯絡電話
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactPhone"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">聯絡mail
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactEmail"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>

					</div>
					<div class="modal-footer">
						<button class="btn btn-primary" type="button" onclick="save()">送出</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="UpdateAndDelete" tabindex="-1" role="dialog" aria-labelledby="MakeUpLabel"
			aria-hidden="true" data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="MakeUpLabel">客戶資料修改</h5>
						<button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body edit">

						<p style="padding-top: 1%;display:none">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>ID : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea rows="1" id="idE"
									style="resize: none; width: 50%;"></textarea></span>
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>ID : </span> <span class="short_text"
								style="margin-left: 3%;"><textarea rows="1" id="updated"
									style="resize: none; width: 50%;"></textarea></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>客戶名稱 : </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="customerNameE"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
									style="color: red">*</span>類別
								: </span>
							<select id="typeDropdownE" style="margin-left: 3%;">
								<option selected value="academic">學術</option>
								<option value="government">政府機關</option>
								<option value="mechanism">機構</option>
								<option value="enterprise">企業</option>
								<option value="other">其他</option>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">說明
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="infoE" style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">統一編號
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="einE" style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">連絡人
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactPersonE"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">聯絡電話
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactPhoneE"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">聯絡mail
								: </span>
							<span class="short_text" style="margin-left: 3%;">
								<input id="contactEmailE"
									style="resize: none; width: 50%; overflow:hidden;"></input></span>
						</p>

					</div>
					<div class="modal-footer">
						<div id="editDiv">
							<button class="btn btn-primary" type="button" onclick="deleteCustomer()" id="deleteAttInfo"
								style="margin-right:5px;">刪除</button>
							<button class="btn btn-primary" type="button" onclick="update()"
								style="margin-right: 5px;">儲存修改</button>
							<button class="btn btn-success" id="signBtn" type="button" onclick="sign()">簽核</button>
						</div>
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