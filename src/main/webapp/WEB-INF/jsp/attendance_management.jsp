<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="en">

	<head>
		<jsp:include page="header.jsp" />
		<!-- Datetimepicker-->
		<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
		<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
		<!-- comfirm button -->
		<link rel="stylesheet"
			href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
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
		</style>
		<script type="text/javascript">
			var isFinishLoad = false;
			var account = '<%out.print(session.getAttribute("Account"));%>';
			var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
			var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
			$(document).ready(
				function () {

					$('#loading').modal('show');
					$("#loading").on('shown.bs.modal', function () {
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

					getNameFilterData();

					initTimeFilter();


					$(function () {
						$("#startDate").datepicker(
							{
								dateFormat: 'yy-mm-dd',
								monthNames: ['一月', '二月', '三月', '四月', '五月',
									'六月', '七月', '八月', '九月', '十月', '十一月',
									'十二月'],
								dayNamesMin: ['日', '一', '二', '三', '四', '五',
									'六'],
								firstDay: 1
							});
						$("#endDate").datepicker(
							{
								dateFormat: 'yy-mm-dd',
								monthNames: ['一月', '二月', '三月', '四月', '五月',
									'六月', '七月', '八月', '九月', '十月', '十一月',
									'十二月'],
								dayNamesMin: ['日', '一', '二', '三', '四', '五',
									'六'],
								firstDay: 1
							});
						$("#startDateUandD").datepicker(
							{
								dateFormat: 'yy-mm-dd',
								monthNames: ['一月', '二月', '三月', '四月', '五月',
									'六月', '七月', '八月', '九月', '十月', '十一月',
									'十二月'],
								dayNamesMin: ['日', '一', '二', '三', '四', '五',
									'六'],
								firstDay: 1
							});
						$("#endDateUandD").datepicker(
							{
								dateFormat: 'yy-mm-dd',
								monthNames: ['一月', '二月', '三月', '四月', '五月',
									'六月', '七月', '八月', '九月', '十月', '十一月',
									'十二月'],
								dayNamesMin: ['日', '一', '二', '三', '四', '五',
									'六'],
								firstDay: 1
							});
						$("#startDateAttInfo").datepicker({
							dateFormat: 'yy-mm-dd',
							monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
							dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
							firstDay: 1
						});
						$("#endDateAttInfo").datepicker({
							dateFormat: 'yy-mm-dd',
							monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
							dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
							firstDay: 1
						});
						$("#typeInsert").change(function () {
							checkTypeInsert();
						});
						$("#typeUandD").change(function () {
							checkTypeUandD();
						});
					});
					getRecords(account, $("#startDateAttInfo").val(), $("#endDateAttInfo").val());
					getAvailableAll(account);

					$.ajax({
						type: 'post',
						url: "/rest/attendance/getDeputy",
						datatype: 'text',
						data: { Live: "Y" },
						success: function (data) {
							var record = JSON.parse(data.entity);
							for (var i = 0; i < record.length; i++) {

								if (nameSelect == "TW") {
									var option = "<option value='" + record[i]["name"] + "'>" + record[i]["chineseName"] + "</option>";
								} else {
									var option = "<option value='" + record[i]["name"] + "'>" + record[i]["name"] + "</option>";
								}

								$("#deputyInsert").append(option);
								$("#deputyUandD").append(option);
							}
						},
						error: function () {
							confirm("訊息", "取得代理人失敗");
						}
					});
					$("body").show();

					$("#typeInsert").change(function () {
						var val = $('#typeInsert').val();
						if (val == 2 || val == 9 || val == 11) {
							$("#deputyInsert").val("none");
						}
					});
					$("#typeUandD").change(function () {
						var val = $('#typeUandD').val();
						if (val == 2 || val == 9 || val == 11) {
							$("#deputyUandD").val("none");
						}
					});
				});

			function getRecords (user, startDate, endDate) {
				timeoutCheck();
				var i = 0;

				var dataTable = $('#dataTable').DataTable({
					"bLengthChange": false,
					"pageLength": 10,
					"oLanguage": {
						"sProcessing": "讀取中...",
						"sLengthMenu": "Show _MENU_ entries",
						"sZeroRecords": "查無相符的資料",
						"sEmptyTable": "無差勤紀錄",
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
					"searching": false,
					"columns": [{
						"data": "類別"
					}, {
						"data": "起始時間"
					}, {
						"data": "結束時間"
					}, {
						"data": "日期"
					}, {
						"data": "時數"
					}, {
						"data": "狀態"
					}, {
						"data": "備註"
					}, {
						"data": "簽核人"
					}, {
						"data": "代理人"
					}, {
						"data": "申請單號"
					}, {
						"data": "簽核時間"
					}, {
						"data": "原因"
					}],
					"order": [[5, "desc"], [1, "asc"]],
					"columnDefs": [{
						"targets": 'no-sort',
						"orderable": false,
					}]
				});


				$.ajax({
					type: "POST",
					cache: false,
					url: '/rest/attendance/records',
					dataType: "json",
					data: {
						account: user,
						startDate: startDate,
						endDate: endDate
					},
					success: function (data) {
						data = data.entity;
						$.each(data, function () {
							var status = "未簽核";
							if (data[i].status == 'SIGNED') {
								status = '已簽核';
							}
							if (data[i].status == 'REJECTED') {
								status = '已駁回';
							}
							var createdAt = new Date(data[i].createdAt);
							var createdAtForView = convertTimeToYMD(data[i].createdAt);

							//set row data
							var type = data[i].type;
							var startTime = convertTimeToYMDHM(data[i].startTime);
							var endTime = convertTimeToYMDHM(data[i].endTime);
							var note = encodeHTML(data[i].note);
							var time = encodeHTML(data[i].annualLeaveTimes);
							var signer = encodeHTML(data[i].signer);
							var id = data[i].id;
							var alTimes = data[i].annualLeaveTimes;
							// initiate datatable row info 
							var temp = dataTable.rows.add([{
								"類別": type,
								"起始時間": startTime,
								"結束時間": endTime,
								"日期": createdAtForView,
								"時數": time,
								"狀態": status,
								"備註": note.replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;"),
								"簽核人": signer,
								"代理人": data[i].deputy,
								"申請單號": data[i].formNo,
								"簽核時間": data[i].signAt,
								"原因": data[i].reason
							}]).draw().nodes().to$();
							//if status == signed, do not add data-toggle, data-target
							var statusDT = data[i].status;

							$(temp).attr("data-toggle", "modal");
							$(temp).attr("data-target", "#UpdateAndDelete");
							$(temp).addClass("open-UpdateDeleteDialog");
							$(temp).css("cursor", "pointer");

							//initiate data-xxx, class attribute of tr 
							$(temp).attr("data-id", id);
							$(temp).attr("data-alTimes", alTimes);

							//initiate data-xxx attribute of td 
							var createdAtDT = data[i].createdAt;
							$(temp).find('td:eq(0)').attr('data-type', type);
							$(temp).find('td:eq(0)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(1)').attr('data-startTime', startTime);
							$(temp).find('td:eq(1)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(2)').attr('data-endTime', endTime);
							$(temp).find('td:eq(2)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(3)').attr('data-createdAt', createdAtDT);
							$(temp).find('td:eq(3)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(4)').attr('data-annualLeaveTimes', time);
							$(temp).find('td:eq(4)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(5)').attr('data-status', statusDT);
							$(temp).find('td:eq(5)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(6)').attr('data-note', note);
							$(temp).find('td:eq(6)').attr("style", "width:20%;word-wrap:break-word;word-break:break-all;padding-left:2%;");
							$(temp).find('td:eq(6)').linkify();
							$(temp).find('td:eq(7)').attr('data-signer', signer);
							$(temp).find('td:eq(7)').attr("style", "padding-left:2%;");
							$(temp).find('td:eq(8)').attr('data-deputy', data[i].deputy);
							$(temp).find('td:eq(8)').attr("style", "display:none");
							$(temp).find('td:eq(9)').attr('data-formNo', data[i].formNo);
							$(temp).find('td:eq(9)').attr("style", "display:none");
							$(temp).find('td:eq(10)').attr('data-signAt', data[i].signAt);
							$(temp).find('td:eq(10)').attr("style", "display:none");
							$(temp).find('td:eq(11)').attr('data-reason', data[i].reason);
							$(temp).find('td:eq(11)').attr("style", "display:none");
							i++;
						});
					},
					error: function (data) {
						confirm("訊息", "取得差勤紀錄失敗");
					}
				});
			};

			function addAttInfo () {
				timeoutCheck();
				if ($("#deputyDiv").is(":visible") && $("#deputyInsert").val() == "none") {
					confirm("訊息", "請填寫代理人");
					return;
				}
				if ($("#alDisplay").is(":visible") && $("#al").val() == 0) {
					confirm("訊息", "請填寫時數");
					return;
				}
				timeoutCheck();
				document.getElementById("close").disabled = true;
				document.getElementById("note").disabled = true;
				document.getElementById("typeInsert").disabled = true;
				document.getElementById("startHour").disabled = true;
				document.getElementById("startMin").disabled = true;
				document.getElementById("endHour").disabled = true;
				document.getElementById("endMin").disabled = true;
				document.getElementById("startDate").disabled = "disabled";
				document.getElementById("endDate").disabled = "disabled";
				document.getElementById("deputyInsert").disabled = true;
				document.getElementById("al").disabled = true;

				if ((checkModalInputEmpty() == false) || (checkTimeSelectedOrNot() == false) || (checkIfStartTUnderEndT() == false) || (checkAnnualLeaveTimes() == false)) {
					sendAble();
					return false;
				}


				//create attribute for json object
				var type = $("#typeInsert").val();
				var note = encodeHTML($("#note").val());

				var startTime = $("#startDate").val() + " " + $("#startHour").val()
					+ ":" + $("#startMin").val() + ":00";

				var endTime = $("#endDate").val() + " " + $("#endHour").val() + ":"
					+ $("#endMin").val() + ":00";
				var annualLeaveTimes = $("#al").val();

				if ($("#alDisplay").css("display") == "none") {
					annualLeaveTimes = 0;
				}
				var deputy = null;
				if ($("#deputyInsert").val() != "none") {
					deputy = $("#deputyInsert").val();
				}
				//initiate json object
				var attendance_data = {
					"type": type,
					"note": note,
					"startTime": startTime,
					"endTime": endTime,
					"annualLeaveTimes": annualLeaveTimes,
					"deputy": deputy
				};

				$.ajax({
					type: "POST",
					url: '/rest/attendance/insert',
					dataType: "json",
					contentType: 'application/json;charset=UTF-8',
					data: JSON.stringify(attendance_data),
					success: function (data) {
						confirmWithFunction("訊息", "新增成功", function () { history.go(0); });
					},
					error: function (data) {
						confirm("訊息", "新增失敗");
						sendAble();
					}
				});

			};

			//unlock option after send
			function sendAble () {
				document.getElementById("close").disabled = false;
				document.getElementById("note").disabled = false;
				document.getElementById("typeInsert").disabled = false;
				document.getElementById("startHour").disabled = false;
				document.getElementById("startMin").disabled = false;
				document.getElementById("endHour").disabled = false;
				document.getElementById("endMin").disabled = false;
				document.getElementById("startDate").disabled = "";
				document.getElementById("endDate").disabled = "";
				document.getElementById("deputyInsert").disabled = false;
				document.getElementById("al").disabled = false;
			};

			function updAttInfo () {
				timeoutCheck();
				if ($("#deputyDivUandD").is(":visible") && $("#deputyUandD").val() == "none") {
					confirm("訊息", "請填寫代理人");
					return;
				}
				//when update, user can not change input!
				if ($("#alDisplayUandD").is(":visible") && $("#alUandD").val() == 0) {
					confirm("訊息", "請填寫時數");
					return;
				}
				disableUpdField(true);

				//check empty field
				var uAndD = true;
				if ((checkModalInputEmpty(uAndD) == false) || (checkTimeSelectedOrNot(uAndD) == false) || (checkIfStartTUnderEndT(uAndD) == false) || checkAnnualLeaveTimes(uAndD) == false) {
					updateAble();
					return false;
				}


				//create attribute for json object
				var typeUandD = $("#typeUandD").val();
				var noteUandD = encodeHTML($("#noteUandD").val());

				var startTimeUandD = $("#startDateUandD").val() + " "
					+ $("#startHourUandD").val() + ":" + $("#startMinUandD").val()
					+ ":00";

				var endTimeUandD = $("#endDateUandD").val() + " "
					+ $("#endHourUandD").val() + ":" + $("#endMinUandD").val()
					+ ":00";

				var id = $("#id").val();
				var status = $("#statusUandD").val();

				var annualLeaveTimesUandD = $("#alUandD").val();
				if ($("#alDisplayUandD").css("display") == "none") {
					annualLeaveTimesUandD = 0;
				}

				var deputy = null;
				if ($("#deputyDivUandD").is(":visible")) {
					deputy = $("#deputyUandD").val();
				}
				//initiate json object
				var updattendance_data = {
					"id": id,
					"type": typeUandD,
					"note": noteUandD,
					"startTime": startTimeUandD,
					"endTime": endTimeUandD,
					"status": status,
					"annualLeaveTimes": annualLeaveTimesUandD,
					"deputy": deputy
				};

				$.ajax({
					type: 'post',
					url: "/rest/attendance/check",
					datatype: 'text',
					data: { id: id },
					success: function (msg) {
						msg = msg.entity;
						if (msg == 1) {
							$.ajax({
								type: "POST",
								url: '/rest/attendance/update',
								dataType: "json",
								contentType: 'application/json;charset=UTF-8',
								data: JSON.stringify(updattendance_data),
								success: function (data) {
									disableUpdField(false);
									confirm("訊息", "修改成功");
									$('#UpdateAndDelete').modal('toggle');
									refreshTable();
								},
								error: function (data) {
									updateAble();
									confirm("訊息", "修改失敗");
								}
							});
						}
						if (msg == 0) {
							confirmWithFunction("訊息", "修改失敗 該申請已被駁回或簽核", function () { history.go(0); });
						}
					},
					error: function () {
						confirm("訊息", "檢查紀錄失敗");
					}
				});
			};

			function checkIfStartTUnderEndT (uAndD) {

				if (uAndD == true) {
					var startTime = $("#startDateUandD").val().replace(/-/g, "/") + " "
						+ $("#startHourUandD").val() + ":"
						+ $("#startMinUandD").val() + ":00";

					var endTime = $("#endDateUandD").val().replace(/-/g, "/") + " "
						+ $("#endHourUandD").val() + ":" + $("#endMinUandD").val()
						+ ":00";
				} else {

					var startTime = $("#startDate").val().replace(/-/g, "/") + " "
						+ $("#startHour").val() + ":" + $("#startMin").val()
						+ ":00";

					var endTime = $("#endDate").val().replace(/-/g, "/") + " "
						+ $("#endHour").val() + ":" + $("#endMin").val() + ":00";
				}
				if (Date.parse(startTime).valueOf() > Date.parse(endTime).valueOf()) {
					confirm("訊息", "注意 開始時間不能晚於結束時間！");
					return false;
				} else if (Date.parse(startTime).valueOf() == Date.parse(endTime).valueOf()) {
					confirm("訊息", "注意 開始時間不能等於結束時間！");
					return false;
				} else {
					return true;
				}


			}

			function checkAnnualLeaveTimes (uAndD) {

				if (uAndD == true) {
					var annualLeaveTimes = $("#alUandD").val();
					var val = $('#typeUandD').val();
					var startTime = $("#startDateUandD").val().replace(/-/g, "/") + " "
						+ $("#startHourUandD").val() + ":"
						+ $("#startMinUandD").val() + ":00";

					var endTime = $("#endDateUandD").val().replace(/-/g, "/") + " "
						+ $("#endHourUandD").val() + ":" + $("#endMinUandD").val()
						+ ":00";
				} else {
					var annualLeaveTimes = $("#al").val();
					var val = $('#typeInsert').val();
					var startTime = $("#startDate").val().replace(/-/g, "/") + " "
						+ $("#startHour").val() + ":" + $("#startMin").val()
						+ ":00";

					var endTime = $("#endDate").val().replace(/-/g, "/") + " "
						+ $("#endHour").val() + ":" + 00 + ":00";
				}
				if (val != 2 && val != 11) {
					var TimeOf12 = Date.parse($("#startDate").val().replace(/-/g, "/") + " "
						+ 12 + ":" + 00
						+ ":00").valueOf();
					var TimeOf13 = Date.parse($("#endDate").val().replace(/-/g, "/") + " "
						+ 13 + ":" + 00
						+ ":00").valueOf();
					var Time1 = Date.parse(endTime).valueOf();
					var Time2 = Date.parse(startTime).valueOf();
					var Time = Math.abs(Time1 - Time2) / 1000 / 60 / 60;
					if (Time2 <= TimeOf12 && Time1 >= TimeOf13) {
						Time = Time - 1;
					}
					if (Time < 24) {
						if (Time != annualLeaveTimes) {
							confirm("訊息", "注意 開始至結束時間差與請假時數不相符！");
							return false;
						} else if (val == 3) {
							if (annualLeaveTimes % 4 != 0) {
								confirm("訊息", "注意 特休時數應該以4小時為單位！");
								return false;
							}
						} else {
							return true;
						}
					}

				}




			}

			function checkTimeSelectedOrNot (UandD) {

				if (UandD == true) {
					var startHour = $("#startHourUandD").val();
					var startMin = $("#startMinUandD").val();
					var endHour = $("#endHourUandD").val();
					var endMin = $("#endMinUandD").val();
				} else {
					var startHour = $("#startHour").val();
					var startMin = $("#startMin").val();
					var endHour = $("#endHour").val();
					var endMin = $("#endMin").val();
				}

				if (startHour == '時' || startMin == '分') {
					confirm("訊息", "請選擇起始時間");
					return false;
				} else if (endHour == '時' || endMin == '分') {
					confirm("訊息", "請選擇結束時間");
					return false;
				} else {
					return true;
				}

			}

			function checkModalInputEmpty (UandD) {

				if (UandD == true) {
					if ($('#startDateUandD').val() == '') {
						confirm("訊息", '請選擇起始日期');
						return false;
					}
					if ($('#endDateUandD').val() == '') {
						confirm("訊息", '請選擇結束日期');
						return false;
					}
					if ($('#noteUandD').val() == '') {
						confirm("訊息", '請填備註');
						return false;
					}
				} else {
					if ($('#startDate').val() == '') {
						confirm("訊息", '請選擇起始日期');
						return false;
					}
					if ($('#endDate').val() == '') {
						confirm("訊息", '請選擇結束日期');
						return false;
					}
					if ($('#note').val() == '') {
						confirm("訊息", '請填備註');
						return false;
					}
				}
			}

			function disableUpdField (off) {
				if (off) {
					document.getElementById("closeUandD").disabled = true;
					document.getElementById("noteUandD").disabled = true;
					document.getElementById("typeUandD").disabled = true;
					document.getElementById("startHourUandD").disabled = true;
					document.getElementById("startMinUandD").disabled = true;
					document.getElementById("endHourUandD").disabled = true;
					document.getElementById("endMinUandD").disabled = true;
					document.getElementById("startDateUandD").disabled = true;
					document.getElementById("endDateUandD").disabled = true;

					document.getElementById("typeSelector").disabled = true;
					document.getElementById("alUandD").disabled = true;
				} else {
					document.getElementById("closeUandD").disabled = false;
					document.getElementById("noteUandD").disabled = false;
					document.getElementById("typeUandD").disabled = false;
					document.getElementById("startHourUandD").disabled = false;
					document.getElementById("startMinUandD").disabled = false;
					document.getElementById("endHourUandD").disabled = false;
					document.getElementById("endMinUandD").disabled = false;
					document.getElementById("startDateUandD").disabled = false;
					document.getElementById("endDateUandD").disabled = false;

					document.getElementById("typeSelector").disabled = false;
					document.getElementById("alUandD").disabled = false;
				}
			}

			//unlock option after upadte
			function updateAble () {
				document.getElementById("closeUandD").disabled = false;
				document.getElementById("noteUandD").disabled = false;
				document.getElementById("typeUandD").disabled = false;
				document.getElementById("startHourUandD").disabled = false;
				document.getElementById("startMinUandD").disabled = false;
				document.getElementById("endHourUandD").disabled = false;
				document.getElementById("endMinUandD").disabled = false;
				document.getElementById("startDateUandD").disabled = "";
				document.getElementById("endDateUandD").disabled = "";
			};

			function delShow () {
				timeoutCheck();
				var id = $("#id").val();
				//initiate json object
				var delattendance_data = {
					"id": id
				};
				$.confirm({
					title: '刪除確認',
					content: '確定刪除此差勤申請?',
					buttons: {
						"刪除": {
							btnClass: 'btn-red',
							action: function () {
								timeoutCheck();
								$.ajax({
									type: 'post',
									url: "/rest/attendance/check",
									datatype: 'text',
									data: { id: id },
									success: function (msg) {
										msg = msg.entity;
										if (msg == 1) {
											$.ajax({
												type: "POST",
												url: '/rest/attendance/delete',
												dataType: "json",
												contentType: 'application/json;charset=UTF-8',
												data: JSON.stringify(delattendance_data),
												success: function (data) {
													confirmWithFunction("訊息", "刪除成功", function () { history.go(0); });
												},
												error: function (data) {
													confirm("訊息", "刪除失敗");
												}
											});
										}
										if (msg == 0) {
											confirmWithFunction("訊息", "刪除失敗 該申請已被駁回或簽核", function () { history.go(0); });
										}
									},
									error: function () {
										confirm("訊息", "檢查紀錄失敗");
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
			}

			//filter controller
			$(function () {
				$("#userFilter").change(function () {
					refreshTable();
				});

				$('#startDateAttInfo').change(function () {
					var checkResult = checkStartEndDate();
					if (checkResult == false) {
						return;
					}
					refreshTable();
				});

				$('#endDateAttInfo').change(function () {
					var checkResult = checkStartEndDate();
					if (checkResult == false) {
						return;
					}
					refreshTable();
				});

			});

			function refreshTable () {
				var user = "";
				if (authorise == 4) {
					user = account;
				} else {
					user = $("#userFilter").val();
				}

				var startDate = $("#startDateAttInfo").val();
				var endDate = $("#endDateAttInfo").val();
				var table = $('#dataTable').DataTable();
				table.clear();
				table.draw();
				getRecords(user, startDate, endDate);
				getAvailableAll(user);
			}

			function initTimeFilter () {

				var currentStartTime = '<%out.print(session.getAttribute("startAtt.date"));%>';
				var currentEndTime = '<%out.print(session.getAttribute("endAtt.date"));%>';
				if (currentStartTime.length < 8)
					currentStartTime = currentStartTime + '-01';
				if (currentEndTime.length < 8)
					currentEndTime = currentEndTime + '-01';
				$('#startDateAttInfo').val(currentStartTime);

				$('#endDateAttInfo').val(currentEndTime);

				var startTimeForSet = $('#startDateAttInfo').val();
				var endTimeForSet = $('#endDateAttInfo').val();
				var id = '<%out.print(session.getAttribute("Account"));%>';

			}

			function convertTimeToYMD (time) {
				var ymd = time.substring(0, 10);
				return ymd;
			}

			function convertTimeToYMDHM (time) {
				var ymdhm = time.substring(0, 16);
				return ymdhm;
			}

			function checkStartEndDate () {
				var startDate = $("#startDateAttInfo").val();
				var endDate = $("#endDateAttInfo").val();
				if (Date.parse(startDate).valueOf() > Date.parse(endDate).valueOf()) {
					confirm("訊息", "注意開始時間不能晚於結束時間！");
					return false;
				}
				return true;
			}

			//get filter data based on authority
			function getNameFilterData () {
				timeoutCheck();
				$.ajax({
					type: 'post',
					url: "/rest/attendance/nameFilter",
					datatype: 'json',
					success: function (data) {
						var record = JSON.parse(data.entity);
						for (var i = 0; i < record.length; i++) {
							if (nameSelect == "TW") {
								$('#userFilter').append('<option value="' + record[i]["name"] + '">' + record[i]["chineseName"] + '</option>');
							} else {
								$('#userFilter').append('<option value="' + record[i]["name"] + '">' + record[i]["name"] + '</option>');
							}
						}
						var account = '<%out.print(session.getAttribute("Account"));%>';
						$('#userFilter').val(account);
					},
					error: function () {
						confirm("訊息", "取得員工資料失敗");
					}
				});
			}

			function encodeHTML (s) {
				if (s === '' || s == null) return '';
				return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;').replace(/>/g, '&gt;').replace(/'/g, '&#x27;');
			}

			$(document).on("click", ".open-UpdateDeleteDialog", function () {
				var status = $(this).find('td').eq(5).data('status');
				var owner = $("#userFilter").val();
				if (status != "CREATED" || (authorise != 4 && owner != account)) {
					$("#modalLabel").html("檢視行程");
					document.getElementById("noteUandD").disabled = true;
					document.getElementById("typeUandD").disabled = true;
					document.getElementById("startHourUandD").disabled = true;
					document.getElementById("startMinUandD").disabled = true;
					document.getElementById("endHourUandD").disabled = true;
					document.getElementById("endMinUandD").disabled = true;
					document.getElementById("startDateUandD").disabled = true;
					document.getElementById("endDateUandD").disabled = true;
					document.getElementById("typeSelector").disabled = true;
					document.getElementById("alUandD").disabled = true;
					document.getElementById("deputyUandD").disabled = true;
					document.getElementById("signerUandD").disabled = true;
					document.getElementById("signAtUandD").disabled = true;
					document.getElementById("rejecterUandD").disabled = true;
					document.getElementById("rejectAtUandD").disabled = true;
					document.getElementById("reasonUandD").disabled = true;
				} else {
					$("#modalLabel").html("修改行程");
					document.getElementById("noteUandD").disabled = false;
					document.getElementById("typeUandD").disabled = false;
					document.getElementById("startHourUandD").disabled = false;
					document.getElementById("startMinUandD").disabled = false;
					document.getElementById("endHourUandD").disabled = false;
					document.getElementById("endMinUandD").disabled = false;
					document.getElementById("startDateUandD").disabled = false;
					document.getElementById("endDateUandD").disabled = false;
					document.getElementById("typeSelector").disabled = false;
					document.getElementById("alUandD").disabled = false;
					document.getElementById("deputyUandD").disabled = false;
					document.getElementById("signerUandD").disabled = false;
					document.getElementById("signAtUandD").disabled = false;
					document.getElementById("rejecterUandD").disabled = false;
					document.getElementById("rejectAtUandD").disabled = false;
					document.getElementById("reasonUandD").disabled = false;
				}
				if (status == "SIGNED") {
					$("#signDiv").show();
					$("#rejectDiv").hide();
				} else if (status == "REJECTED") {
					$("#signDiv").hide();
					$("#rejectDiv").show();
				} else {
					$("#signDiv").hide();
					$("#rejectDiv").hide();
				}

				$(".modal-body #statusUandD").val(status);

				//get id and set id in modal
				var id = $(this).data('id');
				$(".modal-body #id").val(id);

				//get annualLeaveTimes and set in modal
				var al = $(this).data('altimes');
				$(".modal-body #alUandD").val(al);

				//get type from view and set type of modal
				var type = $(this).children().data('type');
				switch (type) {
					case '出差':
						$(".modal-body #typeUandD").val("2");
						break;
					case '特休':
						$(".modal-body #typeUandD").val("3");
						break;
					case '事假':
						$(".modal-body #typeUandD").val("4");
						break;
					case '病假':
						$(".modal-body #typeUandD").val("5");
						break;
					case '公假':
						$(".modal-body #typeUandD").val("6");
						break;
					case '婚假':
						$(".modal-body #typeUandD").val("7");
						break;
					case '喪假':
						$(".modal-body #typeUandD").val("8");
						break;
					case '加班':
						$(".modal-body #typeUandD").val("9");
						break;
					case '補休':
						$(".modal-body #typeUandD").val("10");
						break;
					case '外出':
						$(".modal-body #typeUandD").val("11");
						break;
				}

				//get start time from view and set start date in modal
				var startTime = $(this).find('td').eq(1).text().trim();
				var ymd = convertTimeToYMD(startTime);
				var startDateTime = new Date(startTime);

				$(".modal-body #startDateUandD").val(ymd);

				//get start hour&min from view and set hour&min in modal
				var startHourUandD = convertMDTUnderTenFormat(startDateTime.getHours());
				var startMinUandD = convertMDTUnderTenFormat(startDateTime.getMinutes());

				$(".modal-body #startHourUandD").val(startHourUandD);
				$(".modal-body #startMinUandD").val(startMinUandD);

				//get end time from view and set start date in modal
				var endTime = $(this).find('td').eq(2).text().trim();
				var endDateTime = new Date(endTime);
				ymd = convertTimeToYMD(endTime);
				$(".modal-body #endDateUandD").val(ymd);

				//set hour&min (from view) in modal
				var endHourUandD = convertMDTUnderTenFormat(endDateTime.getHours());
				var endMinUandD = convertMDTUnderTenFormat(endDateTime.getMinutes());
				$(".modal-body #endHourUandD").val(endHourUandD);
				$(".modal-body #endMinUandD").val(endMinUandD);

				//get note and set note in modal
				var note = $(this).find('td').eq(6).data('note');
				$(".modal-body #noteUandD").val(note);

				var deputy = $(this).find('td').eq(8).data('deputy');
				if (nameSelect == "TW") {
					var deputyVal = $('#deputyUandD option').filter(function () {
						return $(this).text() === deputy;
					}).val();
					$("#deputyUandD").val(deputyVal);
				} else {
					$("#deputyUandD").val(deputy);
				}

				var formNo = $(this).find('td').eq(9).text().trim();
				$(".modal-body #formNoUandD").val(formNo);

				var signer = $(this).find('td').eq(7).text().trim();
				$(".modal-body #signerUandD").val(signer);
				$(".modal-body #rejecterUandD").val(signer);
				var signAt = $(this).find('td').eq(10).text().trim();
				$(".modal-body #signAtUandD").val(signAt);
				$(".modal-body #rejectAtUandD").val(signAt);
				var reason = $(this).find('td').eq(11).text().trim();
				$(".modal-body #reasonUandD").val(reason);
				checkTypeUandD();

			});

			$(document).on("click", ".open-AdminDelAttInfoDialog", function () {

				//get id and set id in modal
				var id = $(this).data('id');
				$(".modal-body #AttId").val(id);

			});

			function convertMDTUnderTenFormat (MonOrDateOrTime) {
				if (MonOrDateOrTime < 10) {
					MonOrDateOrTime = "0" + MonOrDateOrTime;
				}
				return MonOrDateOrTime;
			}


			$(document).on("click", "#addAttInfoBtn", function () {
				$(".modal-body #overTime").val("--");
				$(".modal-body #restTime").val("--");
				$(".modal-body #availableTime").val("--");
				$(".modal-body #typeInsert").val("11");
				$(".modal-body #note").val("");
				$(".modal-body #al").val("");
				$(".modal-body #startDate").val("");
				$(".modal-body #startHour").val("時");
				$(".modal-body #startMin").val("分");
				$(".modal-body #endDate").val("");
				$(".modal-body #endHour").val("時");
				$(".modal-body #endMin").val("分");
				$(".modal-body #deputyInsert").val("none");
				checkTypeInsert();
				getAvailable();
			});

			function checkTypeUandD () {
				var val = $('#typeUandD').val();
				if (val != 2 && val != 11) {
					$("#alDisplayUandD").show();
				} else {
					$("#alDisplayUandD").hide();
				}
				if (val != 2 && val != 9 && val != 11) {
					$("#deputyDivUandD").show();
				} else {
					$("#deputyDivUandD").hide();
				}
				var formNo = $('#formNoUandD').val();
				if (formNo.length != 0) {
					$("#formNoDiv").show();
				} else {
					$("#formNoDiv").hide();
				}
				var status = $("#statusUandD").val();
				var owner = $("#userFilter").val();
				if (authorise == 4) {
					owner = account;
				}
				if (authorise == 1) {
					$("#managerEdit").show();
				} else {
					$("#managerEdit").hide();
				}
				if (account == owner && status == "CREATED") {
					$("#userEdit").show();
					if (authorise == 1) {
						$("#managerEdit").hide();
					}
				} else {
					$("#userEdit").hide();
				}
			}

			function checkTypeInsert () {
				var val = $('#typeInsert').val();
				if (val != 2 && val != 11) {
					$("#alDisplay").show();
				} else {
					$("#alDisplay").hide();
				}
				if (val != 2 && val != 9 && val != 11) {
					$("#deputyDiv").show();
				} else {
					$("#deputyDiv").hide();
				}
				if (val == 10) {
					$("#availableTime").show();
				} else {
					$("#availableTime").hide();
				}
				if (val == 9) {
					$("#alDisplayName").html("加班時數 :");
				}
				else {
					$("#alDisplayName").html("請假時數 :");
				}
			}

			function getAvailable () {
				$.ajax({
					type: 'post',
					cache: false,
					url: "/rest/attendance/getAvailableTime",
					contentType: 'application/json;charset=UTF-8',
					datatype: 'json',
					data: {
						account: null
					},
					success: function (data) {

						var overTime = JSON.stringify(data.entity[0].overtimeTotal);
						var restTime = JSON.stringify(data.entity[0].resttimeTotal);
						var availableTime = JSON.stringify(data.entity[0].availableTime);
						$(".modal-body #overTime").val(overTime);
						$(".modal-body #restTime").val(restTime);
						$(".modal-body #availableTime").val(availableTime);


					},
					error: function (data) {
						confirm("訊息", "取得補修時數資料失敗");
					}

				})
			}

			function getAvailableAll (user) {
				$.ajax({
					type: "POST",
					cache: false,
					url: '/rest/attendance/getAvailableTime',
					datatype: "json",
					data: {
						account: user
					},
					success: function (data) {

						$(".availableTimeAll #overTimeAll").val(0);
						$(".availableTimeAll #restTimeAll").val(0);
						$(".availableTimeAll #availableTimeAll").val(0);

						var overTime = JSON.stringify(data.entity[0].overtimeTotal);
						var restTime = JSON.stringify(data.entity[0].resttimeTotal);
						var availableTime = JSON.stringify(data.entity[0].availableTime);
						if (overTime != "null") {
							$(".availableTimeAll #overTimeAll").val(overTime);
						}
						if (restTime != "null") {
							$(".availableTimeAll #restTimeAll").val(restTime);
						}
						if (availableTime != "null") {
							$(".availableTimeAll #availableTimeAll").val(availableTime);
						}

					},
					error: function (data) {
						confirm("訊息", "取得補修時數資料失敗");
					}

				})
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
						<div class="card-header">差勤記錄查詢</div>
						<div class="card-body">
							<div class="table-responsive">
								<div style="width: 98%; height: 4rem;" class="d-flex align-items-center">
									<div class="mr-auto p-3">
										<button class="userbutton" id="addAttInfoBtn" data-toggle="modal"
											data-target="#MakeUp">新增</button>
									</div>
									<div class="p-2">
										<select id="userFilter" class="selectBox"></select>
									</div>
									<div class="p-2">
										<span class="short_label"
											style="float: right; margin-top: 0.5rem; margin-bottom: 6%;">檢視日期
											:
									</div>
									<div class="p-2">
										<input id="startDateAttInfo" type="text"
											style="width: 6.5rem;text-align:center;" readonly="readonly" />
									</div>
									<div class="p-2">~</div>
									<div class="p-2">
										<input id="endDateAttInfo" type="text" style="width: 6.5rem;text-align:center;"
											readonly="readonly" /> </span>
									</div>
								</div>
								<div class="availableTimeAll" style="padding-top: 1%;padding-bottom: 3%;"
									id="availableTimeAll">
									<!-- <p style="padding-top: 1%;"> -->
									<div><span class="short_label"
											style="text-align: left; width: 15%; display: block; vetical-align: top;float: right;color: #f44336;font-size: 95%;">剩餘可補休時數
											:
											<input id="availableTimeAll" type="text" disabled="disabled"
												style="text-align: center; width: 15%;font-size: 5%;line-height: 5%;border-left:0px;border-top:0px;border-right:0px;border-bottom:1px" />
											hr</span>
									</div>
									<div><span class="short_label"
											style="text-align: left; width: 15%; display: block; vetical-align: top;float: right;color: #2a2828;font-size: 95%;">已補休時數
											: <input id="restTimeAll" type="text" disabled="disabled"
												style="text-align: center; width: 15%;font-size: 5%;line-height: 5%;border-left:0px;border-top:0px;border-right:0px;border-bottom:1px" />
											hr</span>
									</div>
									<div>
										<span
											style="text-align: left; width: 15%; display: block; vetical-align: top; float: right;color: #2a2828;font-size: 95%;">總加班時數
											: <input id="overTimeAll" type="text" disabled="disabled"
												style="text-align: center; width: 15%;font-size:5%;line-height: 5%;border-left:0px;border-top:0px;border-right:0px;border-bottom:1px" />
											hr</span>

									</div>
									<!-- </p> -->
								</div>
								<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th class="no-sort">類別</th>
											<th>起始時間</th>
											<th>結束時間</th>
											<th>申請日期</th>
											<th class="no-sort">時數</th>
											<th>狀態</th>
											<th class="no-sort">備註</th>
											<th class="no-sort">簽核人</th>
											<th style="display:none">代理人</th>
											<th style="display:none">申請單號</th>
											<th style="display:none">簽核時間</th>
											<th style="display:none">原因</th>
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
						<h5 class="modal-title" id="MakeUpLabel">新增行程</h5>
						<button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>

					<div class="modal-body">
						<div style="display: none;padding-top: 1%;padding-bottom: 7%;" id="availableTime">
							<!-- <p style="padding-top: 1%;"> -->
							<div>
								<span
									style="text-align: left; width: 27%; display: block; vetical-align: top; float: left;color: #2a2828;font-size: small;">總加班時數
									: <input id="overTime" type="text" disabled="disabled"
										style="text-align: center; width: 25%;font-size: small;line-height: 3%;" />
									hr</span>

							</div>
							<div><span class="short_label"
									style="padding-left: 2%;text-align: left; width: 30%; display: block; vetical-align: top; float: left;color: #2a2828;font-size: small;">已補休時數
									: <input id="restTime" type="text" disabled="disabled"
										style="text-align: center; width: 24%;font-size: small;line-height: 3%;" />
									hr</span>
							</div>
							<div><span class="short_label"
									style="padding-left: 2%;text-align: left; width: 34%; display: block; vetical-align: top; float: left;color: #f44336;font-size: small;">剩餘可補休時數
									:
									<input id="availableTime" type="text" disabled="disabled"
										style="text-align: center; width: 21%;font-size: small;line-height: 3%;" />
									hr</span>
							</div>
							<!-- </p> -->
						</div>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始日期
								: </span> <span class="short_text" style="margin-left: 3%;"><input id="startDate"
									type="text" readonly="readonly" size="15" /></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始時間
								: </span> <select id='startHour'
								style="margin-left: 3%; margin-right: 1.5%; margin-right: 1.5%;">
								<option>時</option>
								<% for (int i=0; i < 24; i++) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select> : <select id='startMin' style="margin-left: 1.5%;">
								<option>分</option>
								<% for (int i=0; i < 60; i +=15) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束日期
								: </span> <span class="short_text" style="margin-left: 3%;"><input id="endDate"
									type="text" readonly="readonly" size="15" /></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束時間
								: </span> <select id='endHour' style="margin-left: 3%; margin-right: 1.5%;">
								<option>時</option>
								<% for (int i=0; i < 24; i++) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select> : <select id='endMin' style="margin-left: 1.5%;">
								<option>分</option>
								<% for (int i=0; i < 60; i +=15) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
								: </span>
							<select style="margin-left: 3%;" id="typeInsert">
								<option value="11">外出</option>
								<option value="2">出差</option>
								<option value="3">特休</option>
								<option value="4">事假</option>
								<option value="5">病假</option>
								<option value="6">公假</option>
								<option value="7">婚假</option>
								<option value="8">喪假</option>
								<option value="9">加班</option>
								<option value="10">補休</option>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
								: </span> <span class="short_text" style="margin-left: 3%;"><textarea rows="3" id="note"
									style="resize: none; width: 50%;"></textarea></span>
						</p>

						<div style="display: none;" id="deputyDiv">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">代理人
									: </span>
								<select style="margin-left:3%; width:40%;" id="deputyInsert">
									<option selected value="none">請選擇</option>
								</select>
						</div>
						<div style="display: none;" id="alDisplay">
							<p style="padding-top: 1%;">
								<span id=alDisplayName class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">請假時數
									: </span> <span class="short_text" style="margin-left: 3%;">
									<input type="number" min="1" step="1" max="400" id="al" style="width: 3rem;"
										value=0></input></span>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal"
							style="margin-right:5px;">取消</button>
						<button class="btn btn-primary" type="button" onclick="addAttInfo()">送出</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal fade" id="UpdateAndDelete" tabindex="-1" role="dialog" aria-labelledby="MakeUpLabel"
			aria-hidden="true">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="modalLabel"></h5>
						<button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
					</div>
					<div class="modal-body">
						<p style="padding-top: 1%;" id="typeSelector">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
								: </span> <select id='typeUandD' style="margin-left: 3%;">
								<option value="11">外出</option>
								<option value="2">出差</option>
								<option value="3">特休</option>
								<option value="4">事假</option>
								<option value="5">病假</option>
								<option value="6">公假</option>
								<option value="7">婚假</option>
								<option value="8">喪假</option>
								<option value="9">加班</option>
								<option value="10">補休</option>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始日期
								: </span> <span class="short_text" style="margin-left: 3%;"><input id="startDateUandD"
									type="text" readonly="readonly" size="15" /></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始時間
								: </span> <select id='startHourUandD' style="margin-left: 3%; margin-right: 1.5%;">
								<option>時</option>
								<% for (int i=0; i < 24; i++) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select> : <select id='startMinUandD' style="margin-left: 1.5%;">
								<option>分</option>
								<% for (int i=0; i < 60; i +=15) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束日期
								: </span> <span class="short_text" style="margin-left: 3%;"><input id="endDateUandD"
									type="text" readonly="readonly" size="15" /></span>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束時間
								: </span> <select id='endHourUandD' style="margin-left: 3%; margin-right: 1.5%;">
								<option>時</option>
								<% for (int i=0; i < 24; i++) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select> : <select id='endMinUandD' style="margin-left: 1.5%;">
								<option>分</option>
								<% for (int i=0; i < 60; i +=15) { %>
									<% if (i < 10) { %>
										<option value='<%out.print("0" + i);%>'>
											<% out.print("0" + i); %>
										</option>
										<% } else { %>
											<option value='<%out.print(i);%>'>
												<% out.print(i); %>
											</option>
											<% } %>
												<% } %>
							</select>
						</p>
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
								: </span> <span class="short_text" style="margin-left: 3%;"><textarea rows="3"
									id="noteUandD" style="resize: none; width: 50%;"></textarea></span>
						</p>
						<div style="display: none;" id="deputyDivUandD">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">代理人
									: </span>
								<select style="margin-left:3%; width:40%;" id="deputyUandD">
									<option selected value="none">請選擇</option>
								</select>
						</div>
						<div style="display: none;" id="alDisplayUandD">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">特休時數
									: </span> <span class="short_text" style="margin-left: 3%;">
									<input type="number" min="1" step="1" max="400" id="alUandD" style="width: 3rem;"
										value=0></input></span>
						</div>
						<div style="display: none;" id="formNoDiv">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請單號
									: </span> <span class="short_text" style="margin-left: 3%;"><input id="formNoUandD"
										type="text" size="15" disabled="disabled" /></span>
							</p>
						</div>
						<div id="signDiv">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">簽核人
									: </span> <span class="short_text" style="margin-left: 3%;"><input id="signerUandD"
										type="text" size="15" disabled="disabled" /></span>
							</p>
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">簽核時間
									: </span> <span class="short_text" style="margin-left: 3%;"><input id="signAtUandD"
										type="text" size="15" disabled="disabled" /></span>
							</p>
						</div>
						<div id="rejectDiv">
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回人
									: </span> <span class="short_text" style="margin-left: 3%;"><input
										id="rejecterUandD" type="text" size="15" disabled="disabled" /></span>
							</p>
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回時間
									: </span> <span class="short_text" style="margin-left: 3%;"><input
										id="rejectAtUandD" type="text" size="15" disabled="disabled" /></span>
							</p>
							<p style="padding-top: 1%;">
								<span class="short_label"
									style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
									: </span> <span class="short_text" style="margin-left: 3%;"><textarea rows="3"
										id="reasonUandD" style="resize: none; width: 50%;"></textarea></span>
							</p>
						</div>
						<div style="display: none" name="id" id="id" value=""></div>
						<div style="display: none" name="statusUandD" id="statusUandD" value=""></div>
					</div>
					<div class="modal-footer">
						<div id="userEdit">
							<button class="btn btn-secondary" type="button" data-dismiss="modal"
								style="margin-right:5px;">取消</button>
							<button class="btn btn-primary" type="button" onclick="delShow()" id="deleteAttInfo"
								style="margin-right:5px;">刪除</button>
							<button class="btn btn-primary" type="button" onclick="updAttInfo()">儲存修改</button>
						</div>
						<div id="managerEdit">
							<button class="btn btn-secondary" type="button" data-dismiss="modal"
								style="margin-right:5px;">取消</button>
							<button class="btn btn-primary" type="button" onclick="delShow()"
								id="deleteAttInfo">刪除</button>
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