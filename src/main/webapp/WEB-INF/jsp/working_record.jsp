<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<jsp:include page="header.jsp" />
<!-- select style plugin -->
<script
	src="./Style/vendor/Animated-Cross-Device-jQuery-Select-Box-Replacement-sumoselect/jquery.sumoselect.js"></script>
<link
	href="./Style/vendor/Animated-Cross-Device-jQuery-Select-Box-Replacement-sumoselect/sumoselect.css"
	rel="stylesheet" />
<!-- Datetimepicker-->
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<link href="./Style/css/homeSelect.css" rel="stylesheet" type="text/css">
<style type="text/css">
body {
	display: none; /*全部先隱藏*/
}
</style>
<script type="text/javascript">
	var isFinishLoad = false;
	var authorise = "<%= session.getAttribute( "Authorise" ) %>";
	var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
	$(document).ready(
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
		$('#dataTable').DataTable(
			{
				"bAutoWidth" : false,
				"bSort" : false,
				"searching" : false,
				"bLengthChange" : false,
				"pageLength" : 20,
				"oLanguage" : {
					"sProcessing" : "讀取中...",
					"sLengthMenu" : "Show _MENU_ entries",
					"sZeroRecords" : "查無相符的資料",
					"sEmptyTable" : "無資料可顯示",
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
		
		$('#detailTable').DataTable(
				{
					"bAutoWidth" : false,
					"bSort" : false,
					"searching" : false,
					"bLengthChange" : false,
					"pageLength" : 20,
					"oLanguage" : {
						"sProcessing" : "讀取中...",
						"sLengthMenu" : "Show _MENU_ entries",
						"sZeroRecords" : "查無相符的資料",
						"sEmptyTable" : "無資料可顯示",
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
		$("#start").datepicker(
				{
					dateFormat : 'yy-mm-dd',
					monthNames : [ '一月', '二月', '三月', '四月',
							'五月', '六月', '七月', '八月', '九月', '十月',
							'十一月', '十二月' ],
					dayNamesMin : [ '日', '一', '二', '三', '四',
							'五', '六' ],
					firstDay : 1
				});
		$("#end").datepicker(
				{
					dateFormat : 'yy-mm-dd',
					monthNames : [ '一月', '二月', '三月', '四月',
							'五月', '六月', '七月', '八月', '九月', '十月',
							'十一月', '十二月' ],
					dayNamesMin : [ '日', '一', '二', '三', '四',
							'五', '六' ],
					firstDay : 1
				});
		 
		 $.ajax({
			type : 'post',
			url : '/rest/work/getDepartment',
			datatype : 'text',
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				for(var i=0;i<record.length;i++){
					var option = "<option selected value='" + record[i]["id"] + "' id='" + record[i]["id"] + "'>" + record[i]["name"] + "</option>";
					$('#departmentFilter').append(option);
				}
				 $('#departmentFilter').SumoSelect({placeholder: '請選擇部門',captionFormat:'已選{0}個部門',selectAll: true,locale: ['確定', '取消', '全選'],csvDispCount: 2,okCancelInMulti:true, captionFormatAllSelected: "全選"});
				 getEmployees();
			},
			error : function() {
				confirm("訊息","取得部門資料失敗");
			}
		});
		 
		$("body").show();
		
		$("#departmentFilter").change(function(){
			$('#employeeFilter')[0].sumo.unload();
			$('#employeeFilter').empty();
			getEmployees();      	
       	});
		
		$("#employeeFilter").change(function(){
			getRecords();    	
       	});
		
		$("#start").change(function() {
			getRecords();
		  });
		
		$("#end").change(function() {
			getRecords(); 
		  });
	});

	function getRecords() {
		timeoutCheck();
		var employee = $("#employeeFilter").val().toString();
		var start = $("#start").val();
		var end = $("#end").val();
		$.ajax({
			type : 'post',
			url : "/rest/work/getRecords",
			datatype : 'json',
			data:{
				employee : employee,
				start : start,
				end : end
			},
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				var t = $('#dataTable').DataTable();
				t.clear().draw();
				for(var i=0;i<record.length;i++){
					var temp = t.row.add( [ 
						record[i]["project"],
						record[i]["hour"],
						record[i]["hd"]
				    	 ] )
				    .draw().nodes().to$();
					$(temp).attr("class","mouseover");
					$(temp).attr("onclick", "showDetail(" + record[i]["id"] + ",'" + record[i]["project"] + "');");
				}
			},
			error : function() {
				confirm("訊息","取得工時資料失敗");
			}
		});
	};
	
	function getEmployees() {
		timeoutCheck();
		var department = $("#departmentFilter").val().toString();
		$.ajax({
			type : 'post',
			url : "/rest/work/getEmployees",
			datatype:'text',
				data:{department : department
					  },
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				for(var i=0;i<record.length;i++){
					var option;
					if(nameSelect=="TW"){
					option = "<option selected value='" + record[i]["name"] + "' id='" + record[i]["name"] + "'>" + record[i]["chineseName"] + "</option>";
					} else {
					option = "<option selected value='" + record[i]["name"] + "' id='" + record[i]["name"] + "'>" + record[i]["name"] + "</option>";
					}
					$('#employeeFilter').append(option);
				}
				$('#employeeFilter').SumoSelect({placeholder: '請選擇員工',captionFormat:'已選{0}位員工',selectAll: true,locale: ['確定', '取消', '全選'],csvDispCount: 2,okCancelInMulti:true, captionFormatAllSelected: "全選"});
				getRecords();
			},
			error : function() {
				confirm("訊息","取得員工資料失敗");
			}
		});
	};
	
	function showDetail(id,project){
		timeoutCheck();
		$("#detailTitle").empty();
		var employee = $("#employeeFilter").val().toString();
		var start = $("#start").val();
		var end = $("#end").val();
		$.ajax({
			type : 'post',
			url : "/rest/work/getDetail",
			datatype : 'json',
			data:{
				id : id,
				employee : employee,
				start : start,
				end : end
			},
			success : function(data) {
				var response = JSON.parse(data);
				var record = JSON.parse(response.entity);
				var t = $('#detailTable').DataTable();
				t.clear().draw();
				for(var i=0;i<record.length;i++){
					var temp = t.row.add( [ 
						record[i]["name"],
						record[i]["hour"],
						record[i]["hd"]
				    	 ] )
				    .draw().nodes().to$();
				}
				
				$("#detailTitle").html(project + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + start + " ~ " + end);
				$("#detailModal").modal("show");
			},
			error : function() {
				confirm("訊息","取得詳細資料失敗");
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
					<div class="card-header">工時統計</div>
					<div class="card-body">
						<div
							style="float: right; padding-right: 1rem; margin-bottom: 1rem;">
							<span style="vertical-align: top;">部門 : </span>
							<div class="notIE">
								<span class="fancyArrow"></span> <select id="departmentFilter"
									multiple="multiple" class="departmentClass"
									style="display: inline-block; height: 30px; width: 150px; padding: 2px 10px 2px 2px; outline: none; color: #74646e; border: 1px solid #C8BFC4; border-radius: 4px; box-shadow: inset 1px 1px 2px #ddd8dc; background: #fff;">
								</select>
							</div>
							<span style="vertical-align: top; margin-left: 1rem;">員工 :
							</span> <select id="employeeFilter" multiple="multiple"
								class="employeeClass"
								style="display: inline-block; height: 30px; width: 150px; padding: 2px 10px 2px 2px; outline: none; color: #74646e; border: 1px solid #C8BFC4; border-radius: 4px; box-shadow: inset 1px 1px 2px #ddd8dc; background: #fff;">
							</select> <span style="vertical-align: top; margin-left: 2rem;">檢視日期
								: </span> <input id="start" type="text"
								style="width: 6.5rem; text-align: center; vertical-align: top;"
								readonly="readonly" value="${start}"/> <span style="vertical-align: top;">~</span>
							<input id="end" type="text"
								style="width: 6.5rem; text-align: center; vertical-align: top;"
								readonly="readonly" value="${end}"/>
						</div>
						<div class="table-responsive">
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th>專案名稱</th>
										<th>時數</th>
										<th>人天</th>
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
			<a class="scroll-to-top rounded" href="#page-top"> <i
				class="fa fa-angle-up"></i>
			</a>
		</div>
	</div>
	
	<div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
		aria-labelledby="detailLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="detailTitle"></h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<table class="table table-bordered" id="detailTable" width="100%"
						cellspacing="0">
						<thead>
							<tr bgcolor="#E6E6E6">
								<th>姓名</th>
								<th>工時</th>
								<th>人天</th>
							</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="loading" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop='static'
		style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
		<div class="modal-dialog">
			<img src="./Style/images/loading.gif" style="padding-top: 12rem;">
		</div>
	</div>
	<jsp:include page="JSfooter.jsp" />
</body>

</html>