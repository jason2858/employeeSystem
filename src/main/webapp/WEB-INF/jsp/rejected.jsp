<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<jsp:include page="header.jsp" />
<!-- Datetimepicker-->
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>

<style>
body {
	display: none; /*全部先隱藏*/
}

#map {
	height: 100%;
}

.gmap_canvas {
	overflow: hidden;
	background: none !important;
	width: 100%;
}
</style>
<script type="text/javascript">
	var isFinishLoad = false;
	var authorise = "<%= session.getAttribute( "Authorise" ) %>";
	$(document)
			.ready(
					function() {
						$('#loading').modal('show');
						$("#loading").on('shown.bs.modal', function() {
							isFinishLoad = true;
						});
						closeLoading();

						if (authorise != 1 && authorise != 2 && authorise != 3){
							window.location.href = "timeout.do";
						}
						if (authorise == 1 || authorise == 2 || authorise == 3) {
							appendUnsign();
				  			getUnsign();
						}

						$("#makeupmonth").val("<%=session.getAttribute("start.month")%>");
						$("#attmonth").val("<%=session.getAttribute("start.month")%>");
						getPunchRecords();
						getAttRecords();
						$('#PunchdataTable')
								.DataTable(
										{
											"bAutoWidth" : false,
											"columnDefs" : [ {
												"orderable" : false,
												"targets" : 0
											}, {
												"orderable" : false,
												"targets" : 1
											}, {
												"orderable" : false,
												"targets" : 2
											}, {
												"orderable" : false,
												"targets" : 5
											} ],
											"order" : [ [ 3, "desc" ] ],
											"bLengthChange" : false,
											"pageLength" : 10,
											"oLanguage" : {
												"sProcessing" : "讀取中...",
												"sLengthMenu" : "Show _MENU_ entries",
												"sZeroRecords" : "查無相符的資料",
												"sEmptyTable" : "無補打卡駁回紀錄",
												"sInfo" : "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
												"sInfoEmpty" : "顯示 0 到 0 共 0  筆資料",
												"sInfoFiltered" : "(filtered from _MAX_ total entries)",
												"sInfoPostFix" : "",
												"sSearch" : "關鍵字搜尋:",
												"sUrl" : "",
												"oPaginate" : {
													"sFirst" : "第一頁",
													"sPrevious" : "上一頁",
													"sNext" : "下一頁",
													"sLast" : "最末頁"
												}
											}
										});

						$('#AttdataTable')
								.DataTable(
										{
											"bAutoWidth" : false,
											"columnDefs" : [ {
												"orderable" : false,
												"targets" : 0
											}, {
												"orderable" : false,
												"targets" : 1
											}, {
												"orderable" : false,
												"targets" : 5
											}, {
												"orderable" : false,
												"targets" : 6
											} ],
											"order" : [ [ 2, "desc" ] ],
											"bLengthChange" : false,
											"pageLength" : 10,
											"oLanguage" : {
												"sProcessing" : "讀取中...",
												"sLengthMenu" : "Show _MENU_ entries",
												"sZeroRecords" : "查無相符的資料",
												"sEmptyTable" : "無差勤駁回紀錄",
												"sInfo" : "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
												"sInfoEmpty" : "顯示 0 到 0 共 0  筆資料",
												"sInfoFiltered" : "(filtered from _MAX_ total entries)",
												"sInfoPostFix" : "",
												"sSearch" : "關鍵字搜尋:",
												"sUrl" : "",
												"oPaginate" : {
													"sFirst" : "第一頁",
													"sPrevious" : "上一頁",
													"sNext" : "下一頁",
													"sLast" : "最末頁"
												}
											}
										});

						$("#makeupyear").change(function() {
							getPunchRecords();
						});

						$("#makeupmonth").change(function() {
							getPunchRecords();
						});

						$("#attyear").change(function() {
							getAttRecords();
						});

						$("#attmonth").change(function() {
							getAttRecords();
						});

						$("body").show();
					});

	function getPunchRecords() {
		timeoutCheck();
		var year = $("#makeupyear").val();
		var month = $("#makeupmonth").val();
		$.ajax({
			type : 'post',
			url : "/rest/sign/getNotCreatedPunchRecords",
			datatype : 'json',
			data : {
				year : year,
				month : month,
				status : "REJECTED"
			},
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				var t = $('#PunchdataTable').DataTable();
				t.clear().draw();
				var type = "";
				for (var i = 0; i < record.length; i++) {
					type = "補上班打卡";
					if (record[i]["type"] == "makeupout")
						type = "補下班打卡";
					record[i]["type"] = type;
					var temp = t.row
							.add(
									[ record[i]["user"],
											record[i]["department"], type,
											record[i]["punchTime"],
											record[i]["time"],
											record[i]["signer"],
											record[i]["signTime"] ]).draw()
							.nodes().to$();
					$(temp).attr("class", "mouseover");
					$(temp).attr("onclick",
							"punchDetail(" + JSON.stringify(record[i]) + ")");
				}
			},
			error : function() {
				confirm("訊息","取得補打卡資料失敗");
			}
		});
	};

	function punchDetail(record) {
		if (record["latitude"] == null) {
			$("#noPosition").show();
			$("#gmap_check").hide();
		} else {
			$("#noPosition").hide();
			$("#gmap_check").attr(
					"src",
					'https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=('
							+ record["latitude"] + '%2C' + record["longitude"]
							+ ')&t=&z=15&ie=UTF8&iwloc=&output=embed');
			$("#gmap_check").show();
		}
		$("#punchName").html(record["user"]);
		$("#punchDep").html(record["department"]);
		$("#punchType1").html(record["type"]);
		$("#punchTime").html(record["punchTime"]);
		$("#punchCreate").html(record["time"]);
		$("#punchNote").html(record["note"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;"));
		$("#punchSigner").html(record["signer"]);
		$("#punchSignTime").html(record["signTime"]);
		$("#punchReason").html(record["reason"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;"));
		$("#punchDetail").modal("show");
	}
	function getAttRecords() {
		timeoutCheck();
		var year = $("#attyear").val();
		var month = $("#attmonth").val();
		$.ajax({
			type : 'post',
			url : "/rest/sign/getNotCreatedAttRecords",
			datatype : 'json',
			data : {
				year : year,
				month : month,
				status : "REJECTED"
			},
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				var t = $('#AttdataTable').DataTable();
				t.clear().draw();
				var type = "";
				for (var i = 0; i < record.length; i++) {
					var originaltype = record[i]["type"];
					switch (originaltype) {
					case 1:
						type = "";
						break;
					case 2:
						type = "出差";
						break;
					case 3:
						type = "特休";
						break;
					case 4:
						type = "事假";
						break;
					case 5:
						type = "病假";
						break;
					case 6:
						type = "公假";
						break;
					case 7:
						type = "婚假";
						break;
					case 8:
						type = "喪假";
						break;
					case 9:
						type = "加班";
						break;
					case 10:
						type = "補休";
						break;
					case 11:
						type = "外出";
						break;
					}
					record[i]["type"] = type;
					var temp = t.row.add(
							[ record[i]["user"], type,
									record[i]["startTime"],
									record[i]["endTime"],
									record[i]["createdAt"],
									record[i]["time"],
									record[i]["signer"],
									record[i]["signTime"] ]).draw()
							.nodes().to$();
					$(temp).attr("class", "mouseover");
					$(temp).attr("onclick",
							"attDetail(" + JSON.stringify(record[i]) + ")");
				}

			},
			error : function() {
				confirm("訊息","取得差勤資料失敗");
			}
		});
	};
	
	function attDetail(record) {
		$("#attName").html(record["user"]);
		$("#attDep").html(record["department"]);
		$("#attType").html(record["type"]);
		$("#attStart").html(record["startTime"]);
		$("#attEnd").html(record["endTime"]);
		$("#attCreate").html(record["createdAt"]);
		$("#attNote").html(record["note"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;"));
		if (record["deputy"].length > 0) {
			$("#deputyDiv").show();
			$("#attDeputy").html(record["deputy"]);
		} else {
			$("#deputyDiv").hide();
		}
		if (record["time"].length > 0) {
			$("#timeDiv").show();
			$("#attTime").html(record["time"]);
		} else {
			$("#timeDiv").hide();
		}
		$("#attSigner").html(record["signer"]);
		$("#attSignTime").html(record["signTime"]);
		$("#attReason").html(record["reason"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;"));
		$("#attDetail").modal("show");
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
					<div class="card-header">已駁回補打卡記錄</div>
					<div class="card-body">
						<div class="table-responsive">

							<span class="short_label" style="margin-left: 2%;">檢視月份 :
								<select id='makeupyear' style="width: 5rem;">
									<option style="display: none;"
										value='<%out.print(session.getAttribute("start.year"));%>'>
										<%
											out.print(session.getAttribute("start.year"));
										%>
									</option>
									<%
										int year = Integer.parseInt((String) session.getAttribute("Year"));
										for (int i = year - 2; i <= year; i++) {
									%>
									<option value='<%out.print(i);%>'>
										<%
											out.print(i);
										%>
									</option>
									<%
										}
									%>
							</select> 年 <select id='makeupmonth' style="width: 5rem;" value='<%out.print(session.getAttribute("start.month"));%>'>
									<%
										for (int i = 1; i <= 12; i++) {
									%>
									<option value='<% if(i<10){
														out.print("0");
													}
													out.print(i);%>'>
										<%
											out.print(i);
										%>
									</option>
									<%
										}
									%>
							</select> 月
							</span>

							<table class="table table-bordered" id="PunchdataTable"
								width="100%" cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th>員工姓名</th>
										<th>部門</th>
										<th>類別</th>
										<th>補打卡時間</th>
										<th>申請時間</th>
										<th>駁回人</th>
										<th>駁回日期</th>
								</thead>
								<tbody>

								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>

			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">已駁回差勤記錄</div>
					<div class="card-body">
						<div class="table-responsive">
							<span class="short_label" style="margin-left: 2%;">檢視月份 :
								<select id='attyear' style="width: 5rem;">
									<option style="display: none;"
										value='<%out.print(session.getAttribute("start.year"));%>'>
										<%
											out.print(session.getAttribute("start.year"));
										%>
									</option>
									<%
										for (int i = year - 2; i <= year; i++) {
									%>
									<option value='<%out.print(i);%>'>
										<%
											out.print(i);
										%>
									</option>
									<%
										}
									%>
							</select> 年 <select id='attmonth' style="width: 5rem; value='<%out.print(session.getAttribute("start.month"));%>'">
									<%
										for (int i = 1; i <= 12; i++) {
									%>
									<option value='<% if(i<10){
														out.print("0");
													}
													out.print(i);%>'>
										<%
											out.print(i);
										%>
									</option>
									<%
										}
									%>
							</select> 月
							</span>
							<table class="table table-bordered" id="AttdataTable"
								width="100%" cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th>員工姓名</th>
										<th>類別</th>
										<th>起始時間</th>
										<th>結束時間</th>
										<th>申請日期</th>
										<th>時數</th>
										<th>駁回人</th>
										<th>駁回日期</th>
								</thead>
								<tbody>

								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="footer.jsp" />
			<!-- Scroll to Top Button-->
			<a class="scroll-to-top rounded" href="#page-top"> <i
				class="fa fa-angle-up"></i>
			</a>
		</div>
	</div>

	<div class="modal fade" id="loading" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop='static'
		style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
		<div class="modal-dialog">
			<img src="./Style/images/loading.gif" style="padding-top: 12rem;">
		</div>
	</div>

	<div class="modal fade" id="punchDetail" tabindex="-1" role="dialog"
		aria-labelledby="attDetailLabel" aria-hidden="true"
		data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">補打卡駁回明細</h5>
					<button class="close" id="closeUandD" type="button"
						data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="gmap_err" class="gmap_canvas"
						style="height: 133px; text-align: center; line-height: 133px;">
						<b id="noPosition">無位置資訊</b>
						<iframe width="100%" height="130" id="gmap_check" frameborder="0"
							scrolling="no" marginheight="0" marginwidth="0"> </iframe>
						<a href="https://www.crocothemes.net"></a>
					</div>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">姓名
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchName"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">部門
							: </span><span class="short_text" style="margin-left: 3%;" id="punchDep"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchType1"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">補打卡時間
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchTime"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請時間
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchCreate"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
							: </span> <span id="punchNote"
							style="display: block; width: 50%; word-wrap: break-word; word-break: break-all; margin-left: 33%;"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回人
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchSigner"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回日期
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchSignTime"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
							: </span><span class="short_text" style="margin-left: 3%;"
							id="punchReason"></span>
					</p>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="attDetail" tabindex="-1" role="dialog"
		aria-labelledby="attDetailLabel" aria-hidden="true"
		data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">差勤駁回明細</h5>
					<button class="close" id="closeUandD" type="button"
						data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">姓名
							: </span><span class="short_text" style="margin-left: 3%;" id="attName"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">部門
							: </span><span class="short_text" style="margin-left: 3%;" id="attDep"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
							: </span><span class="short_text" style="margin-left: 3%;" id="attType"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始時間
							: </span><span class="short_text" style="margin-left: 3%;" id="attStart"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束時間
							: </span><span class="short_text" style="margin-left: 3%;" id="attEnd"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請日期
							: </span><span class="short_text" style="margin-left: 3%;"
							id="attCreate"></span>
					</p>
					<div style="display: none;" id="deputyDiv">
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">代理人
								: </span><span class="short_text" style="margin-left: 3%;"
								id="attDeputy"></span>
					</div>
					<div style="display: none;" id="timeDiv">
						<p style="padding-top: 1%;">
							<span class="short_label"
								style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請時數
								: </span> <span class="short_text" style="margin-left: 3%;"
								id="attTime"></span>
					</div>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
							: </span> <span id="attNote"
							style="display: block; width: 50%; word-wrap: break-word; word-break: break-all; margin-left: 33%;"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回人
							: </span><span class="short_text" style="margin-left: 3%;"
							id="attSigner"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回日期
							: </span><span class="short_text" style="margin-left: 3%;"
							id="attSignTime"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
							: </span><span class="short_text" style="margin-left: 3%;"
							id="attReason"></span>
					</p>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="JSfooter.jsp" />
</body>

</html>