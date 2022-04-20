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
					
			if(authorise == 1 || authorise == 2){
			 document.getElementById("account").disabled = false;
			};
  	        
 			if(authorise != 4){
 				getEmployees();
 			}else{
				$('#id').append('<option value="' + user + '" selected>' + user + '</option>');
				document.getElementById("id").style.display = "none";
 			}
			
 			if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
 				document.getElementById("selector").style.height = "7rem";
 			}else{
 				document.getElementById("selector").style.height = "4rem";
 			}
 			
  			getRecords();
  			
  			$('#dataTable').DataTable({
  				"bAutoWidth" : false,
  				"columnDefs":[
  					{"width": "15%","targets" : 0},
  					{"width": "13%","targets" : 1},
  					{"width": "16%","orderable": false,"targets" : 2},
  					{"width": "5%","orderable": false,"targets" : 3},
  					{"width": "13%","targets" : 4},
  					{"width": "16%","orderable": false,"targets" : 5},
  					{"width": "5%","orderable": false,"targets" : 6},
  					{"width": "16%","orderable": false,"targets" : 7}
  				],
  				"order":[[0,"desc"],[1,"desc"],[4,"desc"]],
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
  	       	});

  	      $("#startyear").change(function(){
  	    		getRecords();
	       	});
  	      
	  	  $("#startmonth").change(function(){
	  			getRecords();
	       	});
	  	  
	  		function getParameterByName(name, url) {
			    if (!url) url = window.location.href;
			    name = name.replace(/[\[\]]/g, '\\$&');
			    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
			        results = regex.exec(url);
			    if (!results) return null;
			    if (!results[2]) return '';
			    return decodeURIComponent(results[2].replace(/\+/g, ' '));
			}
	  		
			var autoPunch = getParameterByName('autoPunch');
			
			if(autoPunch =='out'){
				punch("out");
			}
  		});
  		
  			function getMakeUpCount(account,date){
  				$.ajax({
					type:'post',
					url:"/rest/punch/getMakeUpCount",
					datatype:'json',
					data:{account : account,
						  date : date},
					success:function(data){
						if(data != 0){
							$("#MakeUpCount").text("當月已有 " + data + " 次補打卡紀錄");
							$("#MakeUpCount").show();
						}
						else
							$("#MakeUpCount").hide();
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
						if(record.length>0){
							var year = record[0]["punchDate"].substring(0, 4);
							var month = parseInt(record[0]["punchDate"].substring(5, 7));
							$("#startyear").val(year);
							$("#startmonth").val(month);
							var inposition,outposition,inwhy,outwhy,expand;
							for(var i=0;i<record.length;i++){
								var date = record[i]["punchDate"];
								date = date.substring(0,10);
								inposition = "("+record[i]["inLatitude"]+","+record[i]["inLongitude"]+")";
								if(record[i]["outLatitude"]!="")
									outposition = "("+record[i]["outLatitude"]+","+record[i]["outLongitude"]+")";
								else
									outposition = "";
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
									"",
									record[i]["outTime"],
									record[i]["outWhy"],
									"",
									expand
							    	 ] )
							    .draw().nodes().to$();
								
								$(temp).find('td:eq(0)').attr("style","padding-left:2%;");
								$(temp).find('td:eq(1)').attr("style","padding-left:2%;");
								if(record[i]["muiTime"] != null){
									var mui = "(" + record[i]["muiId"] + ",'" + record[i]["muiTime"] + "'," + record[i]["muiLatitude"] + "," + record[i]["muiLongitude"] + ",'" + record[i]["muiWhy"] + "','" + record[i]["muiNote"] + "')";
									$(temp).find('td:eq(1)').append("<img src='./Style/images/alert.jpg' id='" + i + "mui' class='mouseover' style='float:right;'/>");
									$("#" + i + "mui").attr("onclick","checkInfo" + mui);
									}
								else if(record[i]["inTime"] == null && id == "<%out.print(session.getAttribute("Account"));%>"){
									$(temp).find('td:eq(1)').attr("class","mouseover");
									$(temp).find('td:eq(2)').attr("class","mouseover");
									$(temp).find('td:eq(3)').attr("class","mouseover");
									$(temp).find('td:eq(1)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(2)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(3)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(1)').attr("onclick","sendDate('" + date +"','makeupin')");
									$(temp).find('td:eq(2)').attr("onclick","sendDate('" + date +"','makeupin')");
									$(temp).find('td:eq(3)').attr("onclick","sendDate('" + date +"','makeupin')");
								}
								$(temp).find('td:eq(2)').attr("style","padding-left:2%;");
								if(record[i]["inNote"] != null){
									$(temp).find('td:eq(2)').attr("class","hoverTd");
									$(temp).find('td:eq(2)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["inNote"]
																	+ "</p>");
									$(temp).find('td:eq(2)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								$(temp).find('td:eq(3)').attr("align","center");
								if(record[i]["inLatitude"]!="" && record[i]["inLatitude"]!=null)
									$(temp).find('td:eq(3)').append("<img class='googlemap' data-toggle='tooltip' title='顯示地圖' src='./Style/images/map.jpg' onclick='showmap" + inposition + "'/>");
								$(temp).find('td:eq(4)').attr("style","padding-left:2%;");
								if(record[i]["muoTime"] != null){
									var mui = "(" + record[i]["muoId"] + ",'" + record[i]["muoTime"] + "','" + record[i]["muoLatitude"] + "','" + record[i]["muoLongitude"] + "','" + record[i]["muoWhy"] + "','" + record[i]["muoNote"] + "')";
									$(temp).find('td:eq(4)').append("<img src='./Style/images/alert.jpg' id='" + i + "muo' class='mouseover' style='float:right;'/>");
									$("#" + i + "muo").attr("onclick","checkInfo" + mui);
									}
								else if(record[i]["outTime"] == null && id == "<%out.print(session.getAttribute("Account"));%>"){
									$(temp).find('td:eq(4)').attr("class","mouseover");
									$(temp).find('td:eq(5)').attr("class","mouseover");
									$(temp).find('td:eq(6)').attr("class","mouseover");
									$(temp).find('td:eq(4)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(5)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(6)').attr("title","點擊空白處補打卡");
									$(temp).find('td:eq(4)').attr("onclick","sendDate('" + date +"','makeupout')");
									$(temp).find('td:eq(5)').attr("onclick","sendDate('" + date +"','makeupout')");
									$(temp).find('td:eq(6)').attr("onclick","sendDate('" + date +"','makeupout')");
								}
								$(temp).find('td:eq(5)').attr("style","padding-left:2%;");
								if(record[i]["outNote"] != null){
									$(temp).find('td:eq(5)').attr("class","hoverTd");
									$(temp).find('td:eq(5)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["outNote"]
																	+ "</p>");
									$(temp).find('td:eq(5)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								$(temp).find('td:eq(6)').attr("align","center");
								if(record[i]["outLatitude"]!=null && record[i]["outLatitude"]!=null)
									$(temp).find('td:eq(6)').append("<img class='googlemap' data-toggle='tooltip' title='顯示地圖' src='./Style/images/map.jpg' onclick='showmap" + outposition + "'/>");
								$(temp).find('td:eq(7)').attr("style","padding-left:2%;");
								if(record[i]["holiday"] != null){
									$(temp).find('td:eq(7)').attr("class","hoverTd");
									$(temp).find('td:eq(7)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["holiday"]
																	+ "</p>");
									$(temp).find('td:eq(7)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								if(record[i]["attendance"] != null){
									$(temp).find('td:eq(7)').attr("class","hoverTd");
									$(temp).find('td:eq(7)').append("<p class='" + i + "punch' style='display:none;word-wrap:break-word;word-break:break-all;font-weight:bold;'><br />"
																	+ record[i]["attendance"]
																	+ "</p>");
									$(temp).find('td:eq(7)').attr("onclick","$('." + i + "punch').slideToggle();");
								}
								if(authorise != 1){
									$(temp).find('td:eq(3)').attr("style","display:none;");
									$(temp).find('td:eq(6)').attr("style","display:none;");
									$("#inMap").attr("style","display:none;");
									$("#outMap").attr("style","display:none;");
								}
								$("p").linkify();
							}
						}
					},
					error:function(){ 
						confirm("訊息","取得打卡紀錄失敗");
					} 
			});
  		};
  		
  		function checkInfo(id,time,latitude,longitude,why,note){
  			if(latitude === 'undefined'){
  				$("#noPosition").show();
  				$("#gmap_check").hide();
  			}
  			else{
  				$("#noPosition").hide();
  				$("#gmap_check").show();
  			}
  			$("#gmap_check").attr("src","https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=(" + latitude + "%2C" + longitude + ")&t=&z=15&ie=UTF8&iwloc=&output=embed");
  			$("#typeCheck").html(why);
	        $("#timeCheck").html(time);
	        $("#noteCheck").empty();
	        $("#noteCheck").append(note);
	        $("#noteCheck").linkify();
	        if($("#id").val() != "<%out.print(session.getAttribute("Account"));%>")
  				$("#deldiv").hide();
	        else{
	        	$("#deldiv").show();
	        	$("#del").attr("onclick","delShow(" + id + ")");
	        }
  			$("#MakeUpCheck").modal("show");
  		};
  		
  		function delShow(id){
  			$.confirm({
  				title: '刪除確認',
  				content: '確定刪除此補打卡申請?',
  			    buttons: {
  			    	 "刪除": {btnClass: 'btn-red',
  				        	action:function () {
  			timeoutCheck();
  			$.ajax({
				type:'post',
				url:"/rest/punch/del",
				datatype:'text',
				data:{id : id},
				success:function(msg){
					if(msg==1){
						confirm("訊息","刪除成功");
						getRecords();
						getPunchStatus();
						$("#MakeUpCheck").modal("hide");
					}
					if(msg==0){
						confirm("訊息","刪除失敗 該申請已被簽核或駁回");
						getRecords();
						getPunchStatus("punch_management.do");
						$("#MakeUpCheck").modal("hide");
					}
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
  		
  		function punch(type){
        	timeoutCheck();
        	document.getElementById("punch_in").disabled = true;
        	document.getElementById("punch_out").disabled = true;
        	document.getElementById("make_up").disabled = true;
        	document.getElementById("id").disabled = true;
        	document.getElementById("startyear").disabled = "disabled";
        	document.getElementById("startmonth").disabled = "disabled";
        	if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(workitemCheck,getPunchFail,{enableHighAccurary:true,timeout:15000});
            } else { 
                confirm("訊息","此瀏覽器不支援位置資訊.");
                document.getElementById("punch_in").disabled = false;
		        document.getElementById("punch_out").disabled = false;
		        document.getElementById("make_up").disabled = false;
		        document.getElementById("id").disabled = false;
		        document.getElementById("startyear").disabled = "";
		        document.getElementById("startmonth").disabled = "";
            }
        	
        	function getPunchFail(){
        		confirm("錯誤","請開啟GPS定位後重試.");
        		document.getElementById("punch_in").disabled = false;
		        document.getElementById("punch_out").disabled = false;
		        document.getElementById("make_up").disabled = false;
		        document.getElementById("id").disabled = false;
		        document.getElementById("startyear").disabled = "";
		        document.getElementById("startmonth").disabled = "";
        	}
        	function workitemCheck(position){
        		var latitude = position.coords.latitude;
	        	latitude = latitude.toFixed(6);
				var longitude = position.coords.longitude;
				longitude = longitude.toFixed(6);
	        	var check = "${workitem_punchcheck}";
	        	if(type == "out" && check == "Y"){
	        		var date = '<%out.print(session.getAttribute("Today"));%>';
	  	        	$.ajax({
						type:'post',
						url:"/rest/workItem/getMainHour",
						datatype:'json',
						data:{
							date : date
						},
						success:function(data){
							data = JSON.parse(data).entity;
							if(data == 8){
								confirmWithFunction("訊息","請先填寫本日工時管理",function(){window.location.href = "working_item.do";});
							}
							else
								doPunch(type,latitude,longitude);
						},
						error:function(){ 
							coconfirm("訊息","取得當日工時失敗");
						} 
					});
	        	}
	        	else
	        		doPunch(type,latitude,longitude);
        	}
        }
        function doPunch(type,latitude,longitude){
				var range = ${legalposition_range};
				var inRange = false;
				var positionDetailList = ${POSITION_DETAIL_LIST};
				for(var i = 0; i < positionDetailList.length; i++){
					var legalLatitude = positionDetailList[i].latitude;
					var legalLongitude = positionDetailList[i].longitude;
					var result = Math.sqrt([Math.pow((latitude-legalLatitude),2) + Math.pow((longitude-legalLongitude),2)]) * 100000;//經緯度*100000大約是公尺
					if(result <= range){
						inRange = true;
						break;
					}
				}
				if(inRange)
					{
						sendPunch(type,latitude,longitude,null,null);
					}
				else{
					$("#positionMap").show();
					$("#gmap_error").attr("src","https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=(" + latitude + "%2C" + longitude + ")&t=&z=15&ie=UTF8&iwloc=&output=embed");
					$("#sendReason").attr("onclick","sendReason('" + type + "','" + latitude + "','" + longitude + "')");
					$("#why").val("2");
					$("#reason").val(null);
					document.getElementById("reasonP").style.display = "none";
					$("#Position").modal("show");
					$("#why").change(function(){
						$("#reason").val(null);
						if($("#why").val() == 5)
							$("#reasonP").slideDown();
						else
							$("#reasonP").slideUp();
					});
				}
		        document.getElementById("punch_in").disabled = false;
		        document.getElementById("punch_out").disabled = false;
		        document.getElementById("make_up").disabled = false;
		        document.getElementById("id").disabled = false;
		        document.getElementById("startyear").disabled = "";
		        document.getElementById("startmonth").disabled = "";
       	};
        	
        	function sendReason(type,latitude,longitude){
   				var why = $("#why").val();
   				var reason = $("#reason").val();
   				if(reason.length < 1 && reason == 5){
   					confirm("訊息","請確實填寫備註");
   				}
   				else
   					sendPunch(type,latitude,longitude,why,reason);
   			};
   			
        function sendPunch(type,latitude,longitude,why,reason){
        	timeoutCheck();
        	$.ajax({
				type:'post',
				url:"/rest/punch/punch",
				dataType: "text",
				data:{
					type : type,
					latitude : latitude,
					longitude : longitude,
					why : why,
					reason : reason
				},
			
				success:function(msg){
					$("#id").val('<%out.print(session.getAttribute("Account"));%>');
					$("#startyear").val(<%out.print(session.getAttribute("start.year"));%>);
		        	$("#startmonth").val(<%out.print(session.getAttribute("start.month"));%>);
					if(msg==0){
						confirm("訊息","打卡失敗");
					}
					if(msg==1){
						confirm("訊息","打卡成功");
						getRecords();
						getPunchStatus("punch_management.do");
						$("#Position").modal("hide");
						$("#PositionErr").modal("hide");
					}
					if(msg==2){
						confirm("訊息","今日已有更早的上班打卡紀錄");
						getRecords();
						getPunchStatus("punch_management.do");
						$("#Position").modal("hide");
						$("#PositionErr").modal("hide");
					}
					if(msg==3){
						confirm("訊息","已更新下班打卡紀錄");
						getRecords();
						getPunchStatus("punch_management.do");
						$("#Position").modal("hide");
						$("#PositionErr").modal("hide");
					}
				},
				error:function(){ 
					confirm("訊息","打卡失敗");
				}
			});
        	document.getElementById("punch_in").disabled = false;
	        document.getElementById("punch_out").disabled = false;
	        document.getElementById("make_up").disabled = false;
	        document.getElementById("id").disabled = false;
	        document.getElementById("startyear").disabled = "";
	        document.getElementById("startmonth").disabled = "";
        };
        
        function sendDate(date,type){
  			$("#date").val(date);
  			$("#type").val(type);
  			if($("#type").val()=="makeupin"){
			    	$("#hour").val("9");
					$("#min").val("0");    	
			    }else {
			    	$("#hour").val("18");
					$("#min").val("0");
			    }
  			$("#note").val("");
  			$("#type").change(function(){
   			    if($(this).val()=="makeupin"){
   			    	$("#hour").val("9");
   					$("#min").val("0");    	
   			    }else {
   			    	$("#hour").val("18");
   					$("#min").val("0");
   			    }
  			});
  			var account = $("#account").val();
  			getMakeUpCount(account,date);
  			showMakeup();
  		};
  		
        function setMakeup(){
        	$("#MakeUpCount").hide();
        	var date = '<%out.print(session.getAttribute("Today"));%>';
        	$("#date").val(date);
  			$("#type").val("makeupin");
  			$("#hour").val("9");
  		    $("#min").val("0");  
  			$("#note").val("");
  			var account = $("#account").val();
  			getMakeUpCount(account,date);
  		    $("#type").change(function(){
  			    if($(this).val()=="makeupin"){
  			    	$("#hour").val("9");
  					$("#min").val("0");    	
  			    }else {
  			    	$("#hour").val("18");
  					$("#min").val("0");
  			    }
  			});
  			showMakeup();
        };
        
        function showMakeup(){
        	$("#MakeUp").modal("show");
  			$("#date").change(function(){
  				var account = $("#account").val();
  				var date = $("#date").val();
  				getMakeUpCount(account,date);
  	        });
  			$("#account").change(function(){
  				var account = $("#account").val();
  				var date = $("#date").val();
  				if(date.length>0)
  					getMakeUpCount(account,date);
  	        });
        };
        
        function makeup(){
        	timeoutCheck();
        	var type = $("#type").val();
        	var check = "${workitem_punchcheck}";
        	if(type == "makeupout" && check == "Y"){
	        	var date = $("#date").val();
	  	        $.ajax({
					type:'post',
					url:"/rest/workItem/getMainHour",
					datatype:'json',
					async: false,
					data:{
						date : date
					},
					success:function(data){
						if(JSON.parse(data).entity == 8){
							confirmWithFunction("訊息","請先填寫該日工時管理",function(){window.location.href = "working_item.do";});
						}
						else
							doMakeup(type);
					},
					error:function(){ 
						confirm("訊息","取得當日工時失敗");
					} 
				});
        	}
        	else
        		doMakeup(type);
        }
        function doMakeup(type){
        	document.getElementById("close").disabled = true;
        	document.getElementById("cancel").disabled = true;
        	document.getElementById("send").disabled = true;
        	document.getElementById("hour").disabled = true;
        	document.getElementById("min").disabled = true;
        	document.getElementById("note").disabled = true;
        	document.getElementById("date").disabled = "disabled";
        	if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(getMakeUpPosition,sendMakeUp,{enableHighAccurary:true,timeout:15000});
            } else { 
                confirm("訊息","此瀏覽器不支援位置資訊.");
            }
        	function getMakeUpPosition(position){
	        	var latitude = position.coords.latitude;
				var longitude = position.coords.longitude;
				sendMakeUp(latitude,longitude);
        	}
			function sendMakeUp(latitude,longitude){
				var account = $("#account").val();
        		var type = $("#type").val();
        		var date = $("#date").val();
        		var phour = $("#hour").val();
        		var pmin = $("#min").val();
        		var note = $("#note").val();
        		if(note.length < 2 || phour == "時" || pmin=="分"){
					setTimeout(function() {
						confirm("訊息","請確實填完資料");
			    	}, 100);
				}
        		else{
					$.ajax({
						type:'post',
						url:"/rest/punch/makeup",
						dataType: "text",
						data:{
							account : account,
							date : date,
							phour : phour,
							pmin : pmin,
							note : note,
							type : type,
							latitude : latitude,
							longitude : longitude
						},
						
						success:function(msg){
							if(msg==1){
								confirm("訊息","補打卡申請成功");
								$("#id").val(account);
								$("#startyear").val(date.substring(0,4));
					        	$("#startmonth").val(parseInt(date.substring(5,7)));
								getRecords();
								getPunchStatus("punch_management.do");
								$("#MakeUp").modal("hide");
							}
							if(msg==0)
								confirm("訊息","當天已有同類型之補打卡紀錄申請");
							if(msg==2)
								confirm("訊息","補打卡時間請勿晚於現在時間");
						},
						error:function(){ 
							confirm("訊息","補打卡申請失敗");
						}
					});
        		}
				document.getElementById("close").disabled = false;
				document.getElementById("cancel").disabled = false;
	        	document.getElementById("send").disabled = false;
	        	document.getElementById("hour").disabled = false;
	        	document.getElementById("min").disabled = false;
	        	document.getElementById("note").disabled = false;
	        	document.getElementById("date").disabled = "";
        	}
        };
        
        function showmap(inl,lon){
	        $("#gmap_canvas").attr("src","https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=(" + inl + "%2C" + lon + ")&t=&z=15&ie=UTF8&iwloc=&output=embed");
	        $("#map").modal("show");
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
					<div class="card-header">打卡管理</div>
					<div class="card-body">
						<div class="table-responsive">
							<font color="blue" size="4" id="status"
								style="margin-left: 1rem; font-weight: bold;"></font>
							<div style="float: right; padding-right: 1rem;">
								<button class="userbutton" id="punch_in" onclick="punch('in')">上班打卡</button>
								<button class="userbutton" id="punch_out" onclick="punch('out')">下班打卡</button>
								<button class="userbutton" id="make_up" onclick="setMakeup()">補打卡</button>
							</div>
						</div>
					</div>
				</div>
			</div>

			<%
				int Authorise = (Integer) session.getAttribute("Authorise");
			%>
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">打卡記錄查詢</div>
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
										<th>資料日期</th>
										<th>上班時間</th>
										<th style="padding-left: 2%;">打卡資訊</th>
										<th id="inMap"></th>
										<th>下班時間</th>
										<th style="padding-left: 2%;">打卡資訊</th>
										<th id="outMap"></th>
										<th>備註</th>
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

	<div class="modal fade" id="MakeUp" tabindex="-1" role="dialog"
		aria-labelledby="MakeUpLabel" aria-hidden="true"
		data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">補打卡</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div id="MakeUpCount"
					style="display: none; text-align: center; width: 100%; padding-top: 1rem;">

				</div>

				<div class="modal-body">
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">員工帳號
							: </span> <span class="short_text" style="margin-left: 3%;"> <select
							id='account' style="width: 50%; height: 1.8rem;" disabled>
								<option style="display: none;"
									value='<%out.print(session.getAttribute("Account"));%>'>
									<%out.print(session.getAttribute("Account"));%>
								</option>
						</select>
						</span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">補打卡類型
							: </span> <select id='type' style="margin-left: 3%; width: 50%;">
							<option value='makeupin'>補上班打卡</option>
							<option value='makeupout'>補下班打卡</option>
						</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">補打卡日期
							: </span> <span class="short_text" style="margin-left: 3%;"><input
							id="date" type="text" size="15" readonly="readonly"></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">補打卡時間
							: </span> <select id='hour' style="margin-left: 3%; margin-right: 1.5%;">
							<option>時</option>
							<%for(int i=0;i<24;i++){ %>
							<option value='<%out.print(i);%>'>
								<%if(i<10)out.print("0" + i);else out.print(i);%>
							</option>
							<%} %>
						</select> : <select id='min' style="margin-left: 1.5%;">
							<option>分</option>
							<%for(int i=0;i<60;i+=5){ %>
							<option value='<%out.print(i);%>'>
								<%if(i<10)out.print("0" + i);else out.print(i);%>
							</option>
							<%} %>
						</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
							: </span> <span class="short_text" style="margin-left: 3%;"><textarea
								rows="3" id="note" style="resize: none; width: 50%;"></textarea></span>
					</p>
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" id="cancel" style="margin-right:5px;">取消</button>
					<button class="btn btn-primary" type="button" onclick="makeup()"
						id="send">送出</button>
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
	<div class="modal fade" id="map" tabindex="-1" role="dialog"
		aria-labelledby="MapLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">打卡位置</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="gmap_canvas">
						<iframe width="100%" height="350" id="gmap_canvas" frameborder="0"
							scrolling="no" marginheight="0" marginwidth="0"> </iframe>
						<a href="https://www.crocothemes.net"></a>
					</div>
				</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="Position" tabindex="-1" role="dialog"
		aria-labelledby="PositionLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="PositionLabel">經緯度位置異常</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="gmap_canvas" style="height: 290px;">
						<iframe width="100%" height="270" id="gmap_error" frameborder="0"
							scrolling="no" marginheight="0" marginwidth="0"> </iframe>
						<a href="https://www.crocothemes.net"></a>
					</div>

					<p>
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">異常原因
							: </span> <select id='why'
							style="margin-left: 3%; margin-right: 1.5%; width: 50%">
							<option value='2' selected>系統定位錯誤</option>
							<option value='3'>駐點</option>
							<option value='6'>外出</option>
							<option value='4'>出差</option>
							<option value='5'>其他</option>
						</select>
					</p>
					<p id="reasonP" style="display: none;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">其他
							: </span> <span class="short_text" style="margin-left: 3%;"><textarea
								rows="3" id="reason" placeholder="請填寫原因..."
								style="resize: none; width: 50%;"></textarea></span>
					</p>
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" id="cancel" style="margin-right:5px;">取消</button>
					<button class="btn btn-primary" type="button" id="sendReason">送出</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="MakeUpCheck" tabindex="-1" role="dialog"
		aria-labelledby="MakeUpCheckLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpCheckLabel">補打卡資訊</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="gmap_err" class="gmap_canvas"
						style="height: 290px; text-align: center; line-height: 290px;">
						<b id="noPosition">無位置資訊</b>
						<iframe width="100%" height="270" id="gmap_check" frameborder="0"
							scrolling="no" marginheight="0" marginwidth="0"> </iframe>
						<a href="https://www.crocothemes.net"></a>
					</div>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">類型
							: </span> <span class="short_text" style="margin-left: 3%;"><span
							id="typeCheck" type="text" style="width: 50%;"></span> </span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; height: 2rem; display: block; vetical-align: top; float: left;">補打卡時間
							: </span> <span class="short_text" style="margin-left: 3%;"><span
							id="timeCheck" type="text" style="width: 50%;"></span> </span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
							: </span> <span id="noteCheck"
							style="display: block; width: 50%; word-wrap: break-word; word-break: break-all; margin-left: 33%;"></span>
					</p>

				</div>
				<div class="modal-footer" id="deldiv">
					<button class="delbutton" type="button" id="del">刪除</button>
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