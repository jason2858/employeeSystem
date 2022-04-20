<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <jsp:include page="header.jsp" />
  <!-- Datetimepicker-->
  <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>  
  <!-- comfirm button -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
  <link rel="preload" href="./Style/images/mapover.jpg" as="image">
  <style>
            body
            {
               display:none;/*全部先隱藏*/
            }
            
            #map { 
               height: 100%; 
    	    }
            
            .mapouter{text-align:center;
      	       height:350px;
     	       width:100%;
            }
            
            .gmap_canvas {overflow:hidden;
         	   background:none!important;
         	   height:350px;
       	       width:100%;
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
  <script type="text/javascript">
  var isFinishLoad = false;
  var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
  var depId = '<%out.print(session.getAttribute("depId"));%>';
  var user = '<%out.print(session.getAttribute("Account"));%>';
  var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
  		$(document).ready(function () {
  			$('#loading').modal('show');
			$("#loading").on('shown.bs.modal', function () {
				 isFinishLoad = true;
		    });
			closeLoading();
			
			if(authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4){
				window.location.href = "timeout.do";
			}
			
			if (authorise == 1 || authorise == 2 || authorise == 3) {
				appendUnsign();
	  			getUnsign();
			}
			
 			if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
 				document.getElementById("selector").style.height = "7rem";
 			}else{
 				document.getElementById("selector").style.height = "4rem";
 			}
 			
 			for(var i = 0; i < 24; i++){
 				var hour = i;
 				if(i < 10){
 					hour = "0" + i;
 				}
 				$("#lateHour").append("<option value='" + hour + "'>" + hour + "</option>");
 			}
 			$("#lateHour").val("09");
 			
 			for(var i = 0; i < 60; i = i + 15){
 				var min = i;
 				if(i < 10){
 					min = "0" + i;
 				}
 				$("#lateMin").append("<option value='" + min + "'>" + min + "</option>");
 			}
 			$("#lateMin").val("00");
 			
 			getEmployees();
  			getRecords();
  			getMakeUpCount();
  			$('#dataTable').DataTable({
  				"bAutoWidth" : false,
  				"columnDefs":[
  					{"width": "15%","targets" : 0},
  					{"width": "13%","targets" : 1},
  					{"width": "16%","orderable": false,"targets" : 2},
  					{"width": "13%","targets" : 3},
  					{"width": "16%","orderable": false,"targets" : 4},
  					{"width": "6%","orderable": false,"targets" : 5},
  					{"width": "16%","orderable": false,"targets" : 6}
  				],
  				"order":[[0,"desc"]],
  				"searching" : false,
  				"bLengthChange" : false,
  				"pageLength" : 25,
  				"oLanguage": {
  	                "sProcessing": "讀取中...",
  	                "sLengthMenu": "Show _MENU_ entries",
  	                "sZeroRecords": "查無相符的資料",
  	                "sEmptyTable": "無打卡紀錄",
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
  			$(function() {
				$("#date").datepicker({
					dateFormat: 'yy-mm-dd',
					monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
					dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
					firstDay : 1
					});
				});
  			<%String currentType=(String)session.getAttribute("current.type");%>
  			
  			$("body").show();
  			
  	        $("#id").change(function(){
  	        	getRecords();
  	        	getMakeUpCount();
  	       	});

  	        $("#startyear").change(function(){
  	    		getRecords();
  	    		getMakeUpCount();
	       	});
  	      
	  	    $("#startmonth").change(function(){
	  			getRecords();
	  			getMakeUpCount();
	       	});
	  	    
	  	    $("#lateHour").change(function(){
	    		getRecords();
	       	});
	      
	  	    $("#lateMin").change(function(){
	  			getRecords();
	       	});
  		});
  		
  			function getMakeUpCount(){
  				var account = '<%out.print(session.getAttribute("Account"));%>';
  				if($("#id").val() != null){
  					account = $("#id").val();
  				}
  				var date = $("#startyear").val() + "-";
  				if($("#startmonth").val().length == 1){
  					date += "0";
  				}
  				date += $("#startmonth").val() + "-01";
  				$.ajax({
					type:'post',
					url:"/rest/punch/getMakeUpCount",
					datatype:'json',
					data:{account : account,
						  date : date},
					success:function(data){
						$("#makeup").text("當月有 " + data + " 次補打卡");
					},
					error:function(){
						confirm("訊息","取得補打卡次數失敗");
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
	        	var lateTime = new Date("2000-01-01 " + $("#lateHour").val() + ":" + $("#lateMin").val() + ":00");
	  			$.ajax({
					type:'post',
					url:"/rest/punch/getPunchRecords",
					datatype:'json',
					data:{id : id,
						  year : year,
						  month : month},
					success:function(data){
						var record=JSON.parse(data);
						var t = $('#dataTable').DataTable();
						t.clear().draw();
						var missing = 0;
						var late = 0;
						var remark = "";
						if(record.length>0){
							var year = record[0]["punchDate"].substring(0, 4);
							var month = parseInt(record[0]["punchDate"].substring(5, 7));
							$("#startyear").val(year);
							$("#startmonth").val(month);							
							var inwhy,outwhy,expand;
							for(var i=0;i<record.length;i++){
								var date = record[i]["punchDate"];
								date = date.substring(0,10);
								var workTime = "";
								var inTime = "";
								var outTime = "";
								var mark = false;
								if(record[i]["inTime"] != null){
									inTime = new Date("2000-01-01 " + record[i]["inTime"] + ":00");
								}
								if(record[i]["outTime"] != null){
									outTime = new Date("2000-01-01 " + record[i]["outTime"] + ":00");
								}
								if(record[i]["inTime"] != null && record[i]["outTime"] != null){
									workTime = Math.floor((outTime - inTime) / 36000 )/100;
								}
								if(record[i]["holidayType"] == 1)
									expand = "國定假日";
								else if(record[i]["holidayType"] == 2)
									expand = "彈性補班";
								else if(record[i]["holidayType"] == 3)
									expand = "公司活動";
								else
									expand = "";
								if(expand.length > 1 && record[i]["attendanceType"] != null)
									expand += "/";
								if(record[i]["attendanceType"] != null)
									expand += record[i]["attendanceType"];
								var temp = t.row.add( [ 
									record[i]["punchDate"],
									record[i]["inTime"],
									record[i]["inWhy"],
									record[i]["outTime"],
									record[i]["outWhy"],
									workTime,
									expand
							    	 ] )
							    .draw().nodes().to$();
								
								$(temp).find('td:eq(0)').attr("style","padding-left:2%;");
								$(temp).find('td:eq(1)').attr("style","padding-left:2%;");
								$(temp).find('td:eq(2)').attr("style","padding-left:2%;");
								if(record[i]["inNote"] != null){
									$(temp).find('td:eq(2)').attr("class","hoverTd");
									$(temp).find('td:eq(2)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["inNote"]
																	+ "</p>");
									$(temp).find('td:eq(2)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								$(temp).find('td:eq(3)').attr("style","padding-left:2%;");
								if(record[i]["outNote"] != null){
									$(temp).find('td:eq(3)').attr("class","hoverTd");
									$(temp).find('td:eq(3)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["outNote"]
																	+ "</p>");
									$(temp).find('td:eq(3)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								$(temp).find('td:eq(6)').attr("style","padding-left:2%;");
								if(record[i]["holiday"] != null){
									$(temp).find('td:eq(6)').attr("class","hoverTd");
									$(temp).find('td:eq(6)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["holiday"]
																	+ "</p>");
									$(temp).find('td:eq(6)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								if(record[i]["attendance"] != null){
									$(temp).find('td:eq(6)').attr("class","hoverTd");
									$(temp).find('td:eq(6)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["attendance"]
																	+ "</p>");
									$(temp).find('td:eq(6)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								$("p").linkify();
								
								if(record[i]["inTime"] != null){
									if(inTime.valueOf() > lateTime.valueOf()){
										late ++;
										if(!expand.includes("外出") && !expand.includes("出差") && expand.length != 0){
											mark = true;
										}
										$(temp).find('td:eq(1)').attr("bgcolor","#FFFFCD");
										$(temp).find('td:eq(2)').attr("bgcolor","#FFFFCD");
									}
								}
								if(!expand.includes("外出") && !expand.includes("出差") && expand.length != 0){
									if(record[i]["inTime"] == null && record[i]["outTime"] != null){
										mark = true;
										missing ++;
										$(temp).find('td:eq(1)').attr("bgcolor","#FFc0cB");
										$(temp).find('td:eq(2)').attr("bgcolor","#FFc0cB");
									}else if(record[i]["inTime"] != null && record[i]["outTime"] == null){
										mark = true;
										missing ++;
										$(temp).find('td:eq(3)').attr("bgcolor","#FFc0cB");
										$(temp).find('td:eq(4)').attr("bgcolor","#FFc0cB");
									}
								}else{
									if(record[i]["inTime"] == null){
										missing ++;
										$(temp).find('td:eq(1)').attr("bgcolor","#FFc0cB");
										$(temp).find('td:eq(2)').attr("bgcolor","#FFc0cB");
									}
									if(record[i]["outTime"] == null){
										missing ++;
										$(temp).find('td:eq(3)').attr("bgcolor","#FFc0cB");
										$(temp).find('td:eq(4)').attr("bgcolor","#FFc0cB");
									}
								}
								if(workTime != ""){
									if(workTime < 9){
										if(!expand.includes("外出") && !expand.includes("出差") && expand.length != 0){
											mark = true;
										}
										$(temp).find('td:eq(5)').attr("bgcolor","#FFFFCD");
									}
								}
								if(mark){
									if(remark.length != 0){
										remark += ", ";
									}
									remark += record[i]["punchDate"].substring(5,11);
								}
							}
						}
						$("#late").text(", " + late + " 次遲到");
						$("#missing").text("及 " + missing + " 次未打卡紀錄");
						if(remark.length != 0){
							remark += " 有請假紀錄及打卡異常，請於下方表格再次確認"
						}
						$("#remark").text(remark);
					},
					error:function(){ 
						confirm("訊息","取得打卡紀錄失敗");
					} 
			});
  		};
   </script>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
	<!-- Navigation-->
	<jsp:include page="navbar.jsp" />
	<div class="content-wrapper">
		<div class="container-fluid">
			<%
				int Authorise = (Integer) session.getAttribute("Authorise");
			%>
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">打卡異常統計</div>
					<div class="card-body">
						<div class="table-responsive">
							<div id="selector" style="width: 100%;">
								<select id='id' style="margin: 1rem; width: 8rem;">
								</select>
								<span class="short_label"
									style=" margin-top: 0.5rem; margin-right: 2%;">遲到時間
									: 
									<select id='lateHour' style="width: 4rem;">
									</select>
									: 
									<select id='lateMin' style="width: 4rem;">
									</select>
								</span>
								<span class="short_label"
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
							<span id="makeup" style="margin-left: 1rem;"></span>
							<span id="late"></span>
							<span id="missing"></span>
							<span id="remark" style="margin-left: 2rem;"></span>
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="padding-left: 2%;">資料日期</th>
										<th style="padding-left: 2%;">上班時間</th>
										<th style="padding-left: 2%;">打卡資訊</th>
										<th style="padding-left: 2%;">下班時間</th>
										<th style="padding-left: 2%;">打卡資訊</th>
										<th>工時</th>
										<th style="padding-left: 2%;">備註</th>
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
	<div class="modal fade" id="loading" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" data-backdrop='static'
		style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
		<div class="modal-dialog">
			<img src="./Style/images/loading.gif" style="padding-top: 12rem;">
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