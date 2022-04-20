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
  var data = "";
	$(document).ready(function() {
		$('#loading').modal('show');
		$("#loading").on('shown.bs.modal', function() {
			isFinishLoad = true;
		});
		closeLoading();

		if (authorise != 1 && authorise != 2 && authorise != 3) {
			window.location.href = "timeout.do";
		}
		if (authorise == 1 || authorise == 2 || authorise == 3) {
			appendUnsign();
			getUnsign();
		}
		
		getRecords();
		getProject();
		$('#dataTable').DataTable({
			"bAutoWidth" : false,
			"bSort":false,
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
		
		$("#projectSelect").change(function(){
			$('#memberSelect')[0].sumo.unload();
			$('#memberSelect').empty();
  			getEmployee();
       	});
		
		$("#filter").change(function(){
	    	getRecords();
       	});
		
		$("body").show();
	});

	function getRecords() {
		timeoutCheck();
		var projectId = $("#filter").val();
		$.ajax({
			type : 'post',
			url : "/rest/projectMember/getRecords",
			datatype : 'json',
			data : {
				projectId : projectId
			},
			success : function(data) {
				var record = JSON.parse(JSON.parse(data).entity);
				data = JSON.parse(JSON.parse(data).entity);
				var t = $('#dataTable').DataTable();
				t.clear().draw();
				for (var i = 0; i < record.length; i++) {
					var button = "<button class='signbutton' type='button' onclick='del(" + JSON.stringify(record[i]) + ")'>刪除</button>";
					var temp = t.row.add([ 
								record[i]["projectName"], 
								record[i]["member"],
								button 
								]).draw().nodes().to$();
					$(temp).find('td:eq(2)').attr("align","center");
				}
			},
			error : function() {
				confirm("訊息", "取得專案人員失敗");
			}
		});
	};
	
	function getProject(){
		$.ajax({
			type : 'post',
			url : "/rest/projectMember/getProjects",
			datatype : 'json',
			success : function(data) {
				var record = JSON.parse(JSON.parse(data).entity);
				for (var i = 0; i < record.length; i++) {
					$("#filter").append('<option value="' + record[i]["id"] + '">' + record[i]["name"] + '</option>');
					$('#projectSelect').append('<option value="' + record[i]["id"] + '">' + record[i]["name"] + '</option>');
				}
			},
			error : function() {
				confirm("訊息", "取得專案資料失敗");
			}
		});
	}
	
	function getEmployee(){
		timeoutCheck();
		var projectId = $("#projectSelect").val();
		$("#memberSelect").empty();
		$.ajax({
			type : 'post',
			url : "/rest/projectMember/getEmployee",
			datatype : 'json',
			data : {
				projectId : projectId
			},
			success : function(data) {
				var record = JSON.parse(JSON.parse(data).entity);
				for(var i=0;i<record.length;i++){
					var option;
					if(nameSelect=="TW"){
					option = "<option selected value='" + record[i]["value"] + "' id='" + record[i]["name"] + "'>" + record[i]["chineseName"] + "</option>";
					} else {
					option = "<option selected value='" + record[i]["value"] + "' id='" + record[i]["name"] + "'>" + record[i]["name"] + "</option>";
					}
					$("#memberSelect").append(option);
				}
				if(record.length == 0){
					$("#memberSelect").SumoSelect({placeholder: '無可新增的人員'});
					$("#memberSelect")[0].sumo.disable();
				}else{
					$("#memberSelect").SumoSelect({placeholder: '請選擇員工',captionFormat:'已選{0}位員工',selectAll: true,locale: ['確定', '取消', '全選'],csvDispCount: 2,okCancelInMulti:true, captionFormatAllSelected: "全選"});
					$("#memberSelect")[0].sumo.enable();
				}
				$(".optWrapper").css("margin-left", "7%");
			},
			error : function() {
				confirm("訊息", "取得員工資料失敗");
			}
		});
	}
	
	function addShow(){
		$("#projectSelect").val("-1");
		$("#memberSelect").SumoSelect({placeholder: '請選擇專案',captionFormat:'已選{0}位員工',selectAll: false,locale: ['確定', '取消', '全選'],csvDispCount: 2,okCancelInMulti:true, captionFormatAllSelected: "全選"});
		$("#memberSelect")[0].sumo.disable();
		$("#addModal").modal("show");
	}
	
	function add(){
		timeoutCheck();
		var projectId = $("#projectSelect").val();
		var member = $("#memberSelect").val().toString();
		if(projectId == -1){
			confirm("訊息", "請選擇專案");
			return;
		}
		if(member.length == 0){
			confirm("訊息", "請選擇參與人員");
			return;
		}
		$("button").attr("disabled", true);
		$.ajax({
			type : 'post',
			url : "/rest/projectMember/addMember",
			datatype : 'json',
			data : {
				projectId : projectId,
				member : member
			},
			success : function(data) {
				$("button").removeAttr("disabled");
				confirm("訊息", "新增成功");
				if($("#filter").val() != "all"){
					$("#filter").val(projectId);
				}
				getRecords();
				$("#addModal").modal("hide");
			},
			error : function() {
				$("button").removeAttr("disabled");
				confirm("訊息", "新增失敗");
			}
		});
	}
	
	function del(record){
		$.confirm({
			title: '刪除',
			content: '確定刪除此專案人員?',
		    buttons: {
		        "刪除":{btnClass: 'btn-red',
		        	action: function () {
		        		$("button").attr("disabled", true);
		        		timeoutCheck();
		        		$.ajax({
		        			type : 'post',
		        			url : "/rest/projectMember/delMember",
		        			datatype : 'json',
		        			data : {
		        				id : record.id
		        			},
		        			success : function(data) {
		        				$("button").removeAttr("disabled");
		        				confirm("訊息", "刪除成功");
		        				getRecords();
		        			},
		        			error : function() {
		        				$("button").removeAttr("disabled");
		        				confirm("訊息", "刪除失敗");
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
</script>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
	<!-- Navigation-->
	<jsp:include page="navbar.jsp" />
	<div class="content-wrapper">
		<div class="container-fluid">
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">專案人員管理</div>
					<div class="card-body">
						<div class="table-responsive">
							<div style="width: 100%; height: 4rem;" class="d-flex align-items-center">
								<div class="mr-auto p-3">
									<button class="userbutton" id="announceBtn" onclick="addShow()">新增</button>
								</div>
								<div class="p-2">
									<select id="filter" class="selectBox" style="width:20rem;">
										<option value="all">全選</option>
									</select>
								</div>
							</div>
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="width:40%">專案名稱</th>
										<th>人員姓名</th>
										<th style="text-align:center;width:10%;">操作</th>
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
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog"
		aria-labelledby="editLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="addLabel">新增專案人員</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<p style="padding-top: 1%;">
						<span class="short_label" style="text-align:right;width:25%;display:block;vetical-align:top;float:left;">專案名稱 : </span>
						<select id='projectSelect' class="selectBox" style="margin-left:3%;margin-right:1.5%;width:65%">
							<option hidden value="-1">請選擇專案</option>
						</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label" style="text-align:right;width:25%;display:block;vetical-align:top;float:left;">參與人員 : </span>
						<select id='memberSelect' class="memberClass" multiple="multiple" style="margin-left:7%;margin-right:1.5%;width:180px">
						</select>
					</p>
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" style="margin-right:5px;">取消</button>
					<button class="btn btn-primary" type="button" onclick="add()">新增</button>
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