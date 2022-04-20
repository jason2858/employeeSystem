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
<!-- comfirm button -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
<style>
body {
	display: none; /*全部先隱藏*/
}

#map {
	height: 100%;
}

.mapouter {
	text-align: center;
	height: 350px;
	width: 100%;
}

.gmap_canvas {
	overflow: hidden;
	background: none !important;
	height: 350px;
	width: 100%;
}

.tip {
	position: relative;
	display: inline-block;
	border-bottom: 1px dotted black;
}

.tip .tiptext {
	visibility: hidden;
	width: 120px;
	background-color: black;
	color: #fff;
	text-align: center;
	border-radius: 6px;
	padding: 5px 0;
	/* Position the tooltip */
	position: absolute;
	z-index: 1;
}

.tip:hover .tooltiptext {
	visibility: visible;
}
</style>
<script src="../../Style/js/datalistAPI.js?ver=20220419"></script>
<script type="text/javascript">
  var isFinishLoad = false;
  var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
  var depId = '<%out.print(session.getAttribute("depId"));%>';
  var user = '<%out.print(session.getAttribute("Account"));%>';
  var nameSelect = '<%out.print(session.getAttribute("nameSelect"));%>';
  var punchOutRemindHour = '<%out.print(session.getAttribute("punchOutRemindHour"));%>';
  var remindPunchOut = '<%out.print(session.getAttribute("remindPunchOut"));%>';
  var today = '<%out.print(session.getAttribute("Today"));%>';
  var todayWorkHour = 0;
  		$(document).ready(function () {
  			$('#loading').modal('show');
			$("#loading").on('shown.bs.modal', function () {
				 isFinishLoad = true;
		    });
			closeLoading();
			
			if(authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4){
				window.location.href = "404.do";
			}

			if (authorise == 1 || authorise == 2 || authorise == 3) {
				appendUnsign();
	  			getUnsign();
			}
				   
  			if(authorise != 4){
  				getEmployees();
  			}else{
  				document.getElementById("id").style.display = "none";
  			}
  			
  			if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
 				document.getElementById("selector").style.height = "7rem";
 			}else{
 				document.getElementById("selector").style.height = "4rem";
 			}
  			
  			$(function() {
				$("#date").datepicker({
					dateFormat: 'yy-mm-dd',
					monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
					dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
					firstDay : 1
					});
				$("#dateE").datepicker({
					dateFormat: 'yy-mm-dd',
					monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
					dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
					firstDay : 1
					});
				});
  			
  			$("#date").val(today);
  			
  			init();
  			
  			$('#dataTable').DataTable({
  				"bAutoWidth" : false,
  				"columnDefs":[
  					{"targets": [ 0 ],
  		                "visible": false,
  		                "searchable": false},
  					{"width": "15%","targets" : 1},
  					{"width": "15%","orderable": false,"targets" : 2},
  					{"width": "7%","orderable": false,"targets" : 3},
  					{"width": "33%","orderable": false,"targets" : 4},
  					{"width": "30%","orderable": false,"targets" : 5}
  				],
  				"order":[[0,"desc"]],
  				"searching" : false,
  				"bLengthChange" : false,
  				"pageLength" : 25,
  				"oLanguage": {
  	                "sProcessing": "讀取中...",
  	                "sLengthMenu": "Show _MENU_ entries",
  	                "sZeroRecords": "查無相符的資料",
  	                "sEmptyTable": "無工時紀錄",
  	                "sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
  	                "sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
  	                "sInfoFiltered": "(filtered from _MAX_ total entries)",
  	                "sInfoPostFix": "",
  	                "sSearch": "關鍵字搜尋:",
  	                "sUrl": "",
  	                "oPaginate": {
  	                    "sFirst":    "第一頁",
  	                    "sPrevious": "上一頁",
  	                    "sNext":     "下一頁",
  	                    "sLast":     "最末頁"
  	                	}
  					}
  			    });
  			
  			
  	        $("#id").change(function(){
  	        	getRecords();       	
  	       	});

  	      $("#startyear").change(function(){
  	    		getRecords();
	       	});
  	      
	  	  $("#startmonth").change(function(){
	  			getRecords();
	       	});
		
		  $("#date").change(function(){
				getMainHour();
	        });
		  
  			$("body").show();
  		});

			const datalist = new Datalist();

			datalist.getProjects('projectList');

  			function init(){
  				getMainHour();
  				getRecords();
  			}
  			
  			function getMainHour(){
  				timeoutCheck();
  				var date = $("#date").val();
  	        	$.ajax({
					type:'post',
					url:"/rest/workItem/getMainHour",
					datatype:'json',
					data:{
						date : date
					},
					success:function(data){
						var response = JSON.parse(data);
						var hour = response.entity;
						$("#hour").val(hour);
					},
					error:function(){ 
						confirm("訊息","取得時數失敗");
					} 
				});
  	        }
  			
  			function getEmployees(){
  	        	$.ajax({
					type:'post',
					url:"/rest/punch/getEmployees",
					datatype:'json',
					success:function(data){
						var record=JSON.parse(data);
						for(var i=0;i<record.length;i++){
							if(nameSelect=="TW"){
								if(record[i]["name"] == user){
									$('#id').append('<option value="' + record[i]["name"] + '" selected>' + record[i]["chineseName"] + '</option>');
								}
								else{
									$('#id').append('<option value="' + record[i]["name"] + '">' + record[i]["chineseName"] + '</option>');
								}
							} else {
								if(record[i]["name"] == user){
									$('#id').append('<option value="' + record[i]["name"] + '" selected>' + record[i]["name"] + '</option>');
								}
								else{
									$('#id').append('<option value="' + record[i]["name"] + '">' + record[i]["name"] + '</option>');
								}
							}
						}
					},
					error:function(){ 
						confirm("訊息","取得員工資料失敗");
					} 
				});
  	        }
  			
  			function getRecords(){
  				timeoutCheck();
  				var id = '<%out.print(session.getAttribute("Account"));%>';
  				if(authorise != 4 && $("#id").val() != null)
  					id = $("#id").val();
  				var year = $("#startyear").val();
	        	var month = $("#startmonth").val();
	        	if(month.length<2)
	        		month = "0" + month;
	  			$.ajax({
					type:'post',
					url:"/rest/workItem/getRecords",
					datatype:'json',
					data:{id : id,
						  year : year,
						  month : month},
					success:function(data){
						var response = JSON.parse(data);
						var record = JSON.parse(response.entity);
						var t = $('#dataTable').DataTable();
						todayWorkHour = 0;
						t.clear().draw();
						for(var i=0;i<record.length;i++){
							if(Date.parse(record[i]["date"]).valueOf() == Date.parse(today).valueOf()) {
								todayWorkHour += record[i]["hour"];
							}
							var temp = t.row.add( [ 
								record[i]["emp"],
								record[i]["date"],
								nameSelect=="TW"?record[i]["chineseName"]:record[i]["emp"],
								record[i]["hour"],
								record[i]["project"],
								record[i]["note"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;")
						    	 ] )
						    .draw().nodes().to$();
							if(id == user)
								$(temp).attr("onclick", "show(" + JSON.stringify(record[i]) + ");");
							$(temp).attr("class","mouseover");
							$(temp).find('td:eq(4)').attr("style","word-wrap:break-word;word-break:break-all;");
						}
					},
					error:function(){ 
						confirm("訊息","取得工時資料失敗");
					} 
			});
  		};
  		
  		function add(){
  			timeoutCheck();
  			var date = $("#date").val();
  			var project = datalist.record('projectList', $("#project").val())?.id;
  			var hour = $("#hour").val();
  			var note = $("#note").val();
  			if(Date.parse(date).valueOf() > Date.parse(today).valueOf()){
  				confirm("訊息","申請日期請勿超過當下日期");
  			}
  			else if(!project){
  				confirm("訊息","請選擇專案");
  			}
  			else if(hour.length < 1 || hour == 0){
  				confirm("訊息","請填寫時數");
  			}
  			else if(note.length < 1){
  				confirm("訊息","請填寫說明");
  			}else{
  				isFinishLoad = false;
  				$('#loading').modal('show');
  				$("#loading").on('shown.bs.modal', function () {
					 isFinishLoad = true;
			    });
	  			$.ajax({
					type:'post',
					url:"/rest/workItem/add",
					datatype:'text',
					data:{date:date,
						  project : project,
						  hour : hour,
						  note : note},
					success:function(msg){
						$("#note").val("");
						var year = date.substring(0,4);
						var month = parseInt(date.substring(5,7));
						$("#project").val("");
						$("#startyear").val(year);
						$("#startmonth").val(month);
						init();
						if(today == date && punchOutRemindHour != 0){
							todayWorkHour = todayWorkHour + parseInt(hour);
							if(todayWorkHour >= punchOutRemindHour){
									$.confirm({
										title: '訊息',
										content: '新增成功，是否前往打卡頁面?',
									    buttons: {
									        '確認': function () {
									        		if(remindPunchOut=="remindPlusPunchOut"){
									           			return window.location.href = "punch_management.do?autoPunch=out";
									        		}else{
									        			return window.location.href = "punch_management.do";
									        		}
									        },
									       	'取消': {
									  		  	action: function () {
									          	 	return;
								   	    	     }
								  	     	}
								    	}
									});
							}else{
								confirm("訊息","新增成功");
							}
						}else{
							confirm("訊息","新增成功");
						}
		 				closeLoading();
					},
					error:function(){
						confirm("訊息","新增失敗");
						$("#loading").on('shown.bs.modal', function () {
		 					 isFinishLoad = true;
		 			    });
		 				closeLoading();
					} 
				});
  			}
  		}
  		
  		function show(record){
  			timeoutCheck();
  			$("#key").val(record["id"]);
  			if(nameSelect=="TW"){
  				$("#nameE").val(record["chineseName"]);
  			} else {
  				$("#nameE").val(record["emp"]);
  			}
  			$("#englishNameE").val(record["emp"]);
  			$("#dateE").val(record["date"]);
  			$("#hourE").val(record["hour"]);
  			$("#noteE").val(record["note"]);
  			$("#projectE").val(record["project"]);
  			$("#createdAt").val(record["createdAt"]);
  			$("#delbtn").attr("onclick","delShow(" + record["id"] + ")");
  			$("#beforehour").val(record["hour"]);
  			$("#editModal").modal("show");
  		}
  		
  		function edit(){
  			timeoutCheck();
  			var id = $("#key").val();
  			var account = $("#englishNameE").val();
  			var date = $("#dateE").val();
  			var hour = $("#hourE").val();
  			var beforeHour = $("#beforehour").val();
  			var note = $("#noteE").val();
  			var project = datalist.record('projectList', $("#projectE").val())?.id;
  			var createdAt = $("#createdAt").val();
  			if(Date.parse(date).valueOf() > Date.parse(today).valueOf())
  				confirm("訊息","修改日期請勿超過當下日期")
  			else if(!project)
  				confirm("訊息","請選擇專案");
  			else if(hour.length < 1 || hour == 0)
  				confirm("訊息","請填寫時數");
  			else if(note.length < 1)
  				confirm("訊息","請填寫說明");
  			else{
	  			$.ajax({
					type:'post',
					url:"/rest/workItem/edit",
					datatype:'text',
					data:{id : id,
						  account : account,
						  date : date,
						  hour : hour,
						  note : note,
						  project : project,
						  createdAt : createdAt
						  },
					success:function(msg){
						init();
						if(today == date && punchOutRemindHour != 0){
							todayWorkHour = todayWorkHour - parseInt(beforeHour) +  parseInt(hour);
							if(todayWorkHour >= punchOutRemindHour){
								$.confirm({
									title: '訊息',
									content: '修改成功，是否前往打卡頁面?',
								    buttons: {
								        '確認': function () {
								        		if(remindPunchOut=="remindPlusPunchOut"){
							           				return window.location.href = "punch_management.do?autoPunch=out";
							        			}else{
							        				return window.location.href = "punch_management.do";
							        			}
								        },
								       	'取消': {
								  		  	action: function () {
								          	 	return;
								            }
								        }
								    }
								});
							}else{
								confirm("訊息","修改成功");
							}
						}else{
							confirm("訊息","修改成功");
						}
						$("#editModal").modal("hide");
					},
					error:function(){ 
						confirm("訊息","修改失敗");
					} 
				});
  			}
  		}
  		
  		function delShow(id){
  			timeoutCheck();
  			$.confirm({
  				title: '刪除確認',
  				content: '確定刪除此工時紀錄?',
  			    buttons: {
  			    	 "刪除": {btnClass: 'btn-red',
	 				        	action:function () {
  			timeoutCheck();
  			$.ajax({
				type:'post',
				url:"/rest/workItem/del",
				datatype:'text',
				data:{id : id},
				success:function(msg){
					confirm("訊息","刪除成功");
					init();
					$("#editModal").modal("hide");
				},
				error:function(){ 
					confirm("訊息","刪除失敗");
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
		
  		function autogrow(textarea){
  			var adjustedHeight=textarea.clientHeight;		 
  			    adjustedHeight=Math.max(textarea.scrollHeight,adjustedHeight);
  			    if (adjustedHeight>textarea.clientHeight){
  			        textarea.style.height=adjustedHeight+'px';
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
					<div class="card-header">新增工時</div>
					<div class="card-body">
						<form>
							<div class="form-row">
								<div class="form-group col-md-2">
									<label for="inputCity">日期</label> <input type="text" size="13"
										class="form-control" id="date">
								</div>
								<div class="form-group col-md-9">
									<label for="inputState">專案名稱</label> <input id="project" list="projectList"
										class="form-control" placeholder="請選擇專案">
								</div>
								<div class="form-group col-md-1">
									<label for="inputZip">工時</label> <input type="number" min="0" step="1"
										class="form-control" size="2" id="hour">
								</div>
							</div>
							<div class="form-group">
								<label for="inputAddress2">說明</label>
								<textarea rows="2" class="form-control" id="note"
									onkeyup="autogrow(this);" placeholder="必填...."></textarea>
							</div>
						</form>
						<button class="userbutton pull-right" id="addbtn" onclick="add()">新增</button>
					</div>
				</div>
			</div>

			<%
				int Authorise = (Integer) session.getAttribute("Authorise");
			%>
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">工時記錄查詢</div>
					<div class="card-body">
						<div class="table-responsive">
							<div id="selector" style="width: 100%;">
								<select id='id' style="margin: 1rem; width: 8rem;">
								</select> <span class="short_label"
									style="float: right; margin-top: 0.5rem; margin-right: 2%;">檢視月份
									: <select id='startyear' style="width: 5rem;">
										<option style="display: none;"
											value='<%out.print(session.getAttribute("start.year"));%>'>
											<%
												out.print(session.getAttribute("start.year"));
											%>
										</option>
										<%
											int year = Integer.parseInt((String) session.getAttribute("Year"));
											for (int i = 2016; i <= year; i++) {
										%>
										<option value='<%out.print(i);%>'>
											<%
												out.print(i);
											%>
										</option>
										<%
											}
										%>
								</select> 年 <select id='startmonth' style="width: 5rem;">
										<option style="display: none;"
											value='<%out.print(session.getAttribute("start.month"));%>'>
											<%
												out.print(session.getAttribute("start.month"));
											%>
										</option>
										<%
											for (int i = 1; i <= 12; i++) {
										%>
										<option value='<%out.print(i);%>'>
											<%
												out.print(i);
											%>
										</option>
										<%
											}
										%>
								</select> 月
								</span>
							</div>
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="display:none;">帳號</th>
										<th>資料日期</th>
										<th>員工姓名</th>
										<th>工時</th>
										<th>專案名稱</th>
										<th>說明</th>
									</tr>
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

	<div class="modal fade" id="editModal" tabindex="-1" role="dialog"
		aria-labelledby="editLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editLabel">修改工時資料</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<input id="key" type="text" disabled="disabled"
						style="display: none"> <input id="createdAt" type="text"
						disabled="disabled" style="display: none">
					<input id="beforehour" type="text" disabled="disabled"
						style="display: none"> 
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">員工姓名:
						</span> <input id="nameE" type="text" readonly="readonly"
							style="margin-left: 3%; margin-right: 1.5%; width: 50%">
					</p>
					<p style="display:none;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">員工姓名:
						</span> <input id="englishNameE" type="text" readonly="readonly"
							style="margin-left: 3%; margin-right: 1.5%; width: 50%">
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">日期
							: </span> <input id="dateE" type="text" size="15" readonly="readonly"
							style="margin-left: 3%; margin-right: 1.5%; width: 50%">
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">專案名稱
							: </span> <input id='projectE' list="projectList" placeholder="請選擇專案"
							style="margin-left: 3%; margin-right: 1.5%; width: 50%"/>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">工時
							: </span> <input id="hourE" type="number" min="0" step="1"
							style="margin-left: 3%; margin-right: 1.5%; width: 3rem; vertical-align: top;">
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">說明
							: </span> <span class="short_text"><textarea rows="5" id="noteE"
								style="margin-left: 3%; resize: none; width: 50%;"></textarea></span>
					</p>
				</div>
				<div class="modal-footer">
					<button class="delbutton" type="button" id="delbtn"
						style="margin-right: 5px;">刪除</button>
					<button class="btn btn-primary" type="button" onclick="edit()">修改</button>
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