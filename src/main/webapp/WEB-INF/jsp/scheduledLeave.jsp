<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<jsp:include page="header.jsp" />
<!-- select style plugin -->
<link href="./Style/css/event.css" rel="stylesheet" type="text/css">
  
<style type="text/css">
body {
	display: none; /*全部先隱藏*/
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

tr[role="row"]:hover:not([bgcolor="#E6E6E6"]){
	cursor : pointer;
}
</style>
<script type="text/javascript">
var isFinishLoad = false;
	$(document).ready(
			function() {
				$('#loading').modal('show');
				$("#loading").on('shown.bs.modal', function () {
					 isFinishLoad = true;
			    });
				closeLoading();
				var account = "<%out.print(session.getAttribute("Account"));%>";
				var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
				var companyId = '<%out.print(session.getAttribute("companyId"));%>';
				var nameSelect = '<%out.print(session.getAttribute("nameSelect"));%>';
				var entiHours = "";
				var depAppend ='';
				
				if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4){
					window.location.href = "timeout.do";
				}
				if (authorise == 1 || authorise == 2 || authorise == 3) {
					appendUnsign();
		  			getUnsign();
				}
	  				
				$.ajax({
					type : "POST",
					url : '/rest/project/getEmpList',
					dataType : "json",
					success : function(data) {
						data = JSON.parse(data.entity);
						for(var i=0;i<data.length;i++){
							$("#emp").get(0).options[0] = new Option("Select ...", "-1");
							if(nameSelect=="TW"){
								$.each(data, function(index, item) {
					           		$("#emp").get(0).options[$("#emp").get(0).options.length] = new Option(item.chineseName,item.name);
					       	 	});
							} else {
								$.each(data, function(index, item) {
					           		$("#emp").get(0).options[$("#emp").get(0).options.length] = new Option(item.name, item.name);
					       	 	});
							}
						}
						//document.getElementById("emp").value = account;
					}
				 });
				
				var user = {
						"empName" : account,
				};
				
				$.ajax({
					type : "POST",
					cache : false,
					contentType : "application/json;charset=UTF-8",
					url : '/rest/acc/getDepListAsBelow',
					dataType : "json",
					data : JSON.stringify(user),
					success : function(data) {
						$.each(data, function(key, value){
						    	depAppend += key+value;
						});
					}
				 });
				
				var table = $('#dataTable').DataTable( {
					 ajax: {
					         url: '/rest/acc/getAnnualLeave?companyId='+ companyId,
					         method: "POST",
					         xhrFields: {
					            withCredentials: true
					         },
					           "dataSrc": function (json) {
					            return json;
					         }
				        },success:function(data){
						},
						error:function(){
							confirm("訊息",'取得特休資料失敗');
						},
				        "oLanguage" : {
							"sProcessing" : "讀取中...",
							"sLengthMenu" : "Show _MENU_ entries",
							"sZeroRecords" : "查無相符的資料",
							"sEmptyTable" : "暫無專案",
							"sInfo" : "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
							"sInfoEmpty" : "顯示 0 到 0 共 0  筆資料",
							"sInfoFiltered" : "",
							"sInfoPostFix" : "",
							"sSearch" : "關鍵字搜尋:",
							"sUrl" : "",
							"oPaginate" : {
								"sFirst" : "第一頁",
								"sPrevious" : "上一頁",
								"sNext" : "下一頁",
								"sLast" : "最末頁"
							}
						},
				        "bLengthChange" : false,
				        "pageLength" : 10,
				        "searching" : true,
				        "bSort" : false,
				        "columnDefs":[
				    					{"targets": [ 0 ],
				    		             "visible": false}
				    				],
				        "columns": [
				            { "data": "accountName" },
				            { "data": "empName" },
				            { "data": "dep" },
				            { "data": "entitledHours" },
				            { "data": "year"  },
				            { "data": "skdHours",
				            	render: function(data, type, row, meta) {
				            		if(row.entitledHours!=row.skdHours){
				            			return	'<span style="color:red;">'+data+'(未滿)</span>';
				            		}else{
			            				return data;
				            		}
			            	 	}
				            }
				        ]
			    } );
				
				var yearFromNow = (new Date()).getFullYear();
				document.getElementById("yearSear").value = yearFromNow;
				
				$.fn.dataTable.ext.search.push(
					      function( settings, data, dataIndex ) {
					          return data[0]==account&&data[4]==yearFromNow||
					          authorise=='2'&&data[4]==yearFromNow||
					          authorise=='1'&&data[4]==yearFromNow||
					          (authorise=='3'&&depAppend.includes(data[2])&&data[2]!='')&&data[4]==yearFromNow
					              ? true
					              : false
					      }     
					  );
				
				$('#dataTable').on( 'click', 'td', function () {
					timeoutCheck();
					var rowData = table.row( this ).data();
					var hr = 0;
					if(rowData != null){
					$.ajax({
						type : "POST",
						cache : false,
						contentType : "application/json;charset=UTF-8",
						url : '/rest/acc/getSkdLeaveInfo',
						dataType : "json",
						data : JSON.stringify(rowData),
						success : function(data) {
					        $('#MakeUp').modal("show");
					        $('input').val('');
					        document.getElementById("emp").value = data.employees;
							document.getElementById("year").value = data.year;
							
							$.each(data.map, function(index, item) {
								var i = parseInt(item.mon);
								document.getElementById("mon"+i).value = item.val;
								hr = hr + Number(item.val);
					        });
							entiHours= rowData.entitledHours;
							$("#entiHours").text(entiHours-hr|0);
							},error:function(){
								confirm("訊息","取得剩餘時數失敗");
							}
					 });
					}
			    } );
				
				$('#yearSear').change(function(){
					var year = document.getElementById("yearSear").value;
					var table = $('#dataTable').DataTable();
					
					$.fn.dataTable.ext.search.splice(0);//先刪除全部
					if(year!='0'){
					 $.fn.dataTable.ext.search.push(
						      function( settings, data, dataIndex ) {
						          return (data[4]==year&&data[0]==account||
						        		  data[4]==year&&authorise=='2'||
						        		  data[4]==year&&authorise=='1')||
								          (data[4]==year&&authorise=='3'&&depAppend.includes(data[2])&&data[2]!='')
						              ? true
						              : false
						      }     
						  );
					}else {
					$.fn.dataTable.ext.search.push(
						      function( settings, data, dataIndex ) {
							      return data[0]==account||authorise=='2'||authorise=='1'||
						          (authorise=='3'&&depAppend.includes(data[2])&&data[2]!='')
							          ? true
							          : false
						      }     
						  );
					}
					 table.draw();
				});
				
				$('#year').change(function(){
					
					var ann_id = {
							"empName" : account,
							"year" : document.getElementById("year").value
					};
					
					$.ajax({
						type : "POST",
						cache : false,
						contentType : "application/json;charset=UTF-8",
						url : '/rest/acc/getAnnYearHours',
						dataType : "json",
						data : JSON.stringify(ann_id),
						success : function(data) {
							$("#entiHours").text(data.entitledHours|0);
							entiHours = (data.entitledHours|0);
							$('input').val('');
							}
					 });
					
				});
				
				$( "input[type='number']" ).change(function() {
					var hr = 0;
					$('input[type=number]').each(function(){
					    hr = hr + Number($(this).val());
					})
					hr = Number(entiHours)- hr;
					$("#entiHours").text(hr|0);
				});
				
				
				$("body").show();
			});
	
	function encodeHTML(s) {
		if (s==='' || s==null) return '';
	    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;').replace(/>/g, '&gt;').replace(/'/g, '&#x27;');
	}
	
	function newSkd() {//新增初始化
		$("#entiHours").text(0);
		document.getElementById("year").value = 0;
		$('input').val('');
		}
	
    function save(){
    	timeoutCheck();
			var year = document.getElementById("year").value;
			var entiHours = $('#entiHours').text();
			var emp = document.getElementById("emp").value;
			
			if(year==0){
				confirm("訊息","請選擇年份");
				return;
			}
			if(entiHours<0){
				confirm("訊息","預排超出剩餘特休");
				return;
			}
			
			var map = [];
			
			$('input[type=number]').each(function(){
				if($(this).val()!=''){
					map.push({mon:$(this).attr('id'),val:$(this).val()});
				}
			})
			
			var skdLeave_data = {
					"employees" : emp,
					"map" : map,
					"year" : year
			};
			
			$.ajax({
				type:'post',
				url:"/rest/acc/skdLeaveSave",
				dataType : "text",
				contentType : 'application/json;charset=UTF-8',
				data : JSON.stringify(skdLeave_data),
				success:function(data){
					if(data=="success"){
						confirmWithFunction("訊息","儲存成功",function(){history.go(0);});
					}else {
						confirm("訊息","儲存失敗");
					}
				},
				error:function(){
					confirm("訊息","儲存失敗");
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
					<div class="card-header">預排特休</div>
					<div class="card-body">
						<div style="width: 100%; height: 4rem;"
							class="d-flex align-items-center">
							<div class="mr-auto p-3">
								<button class="userbutton" id="addskdInfoBtn" onclick="newSkd()"
									data-toggle="modal" data-target="#MakeUp" style="display:none;">新增</button>
							</div>
							<label style="margin-top:0.6%"> 年份: </label>
							<select id='yearSear' style="margin-left:1%;margin-right:1.5%;width:10%" >
							<option value='0'>Select All</option>
							<option value='2017'>2017</option>
							<option value='2018'>2018</option>
							<option value='2019'>2019</option>
							<option value='2020'>2020</option>
							<option value='2021'>2021</option>
							<option value='2022'>2022</option>
							<option value='2023'>2023</option>
							<option value='2024'>2024</option>
							<option value='2025'>2025</option>
							<option value='2026'>2026</option>
							<option value='2027'>2027</option>
							</select>
						</div>
						<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="display:none;">帳號</th>
										<th>員工名稱</th>
										<th>部門</th>
										<th>特休時數</th>
										<th>預排年份</th>
										<th>已排休時數</th>
									</tr>
								</thead>
								<tbody id="annLeaveInfo">
								</tbody>
						</table>
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
					<h5 class="modal-title" id="MakeUpLabel">新增預排特休</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body add">
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
							style="color: red">*</span>員工 : </span> <select
							style="margin-left: 3%;" id="emp" disabled>
						</select>
						
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">
							<span style="color: red">*</span>年份
							: </span> 
							<select id='year' style="margin-left: 3%;" disabled>
							<option value='0'>Select ...</option>
							<option value='2017'>2017</option>
							<option value='2018'>2018</option>
							<option value='2019'>2019</option>
							<option value='2020'>2020</option>
							<option value='2021'>2021</option>
							<option value='2022'>2022</option>
							<option value='2023'>2023</option>
							<option value='2024'>2024</option>
							<option value='2025'>2025</option>
							<option value='2026'>2026</option>
							<option value='2027'>2027</option>
						
							</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">剩餘特休 (hr) : </span> 
							<span style="margin-left: 3%;" id="entiHours">0
						</span>
					</p>
					<div class="mx-auto" style="width:150px">預排月份特休(hr)</div>
				</div>
				
				<div class="modal-body add mx-auto">
					<div class="bg-g">
						<div >
							<input id='mon1' class="no-spinners" type="number" maxlength="3" value="">
							<span>01月</span>
						</div>
						<div >
							<input id='mon2' class="no-spinners" type="number" maxlength="3" value="">
							<span>02月</span>
						</div>
						<div >
							<input id='mon3' class="no-spinners" type="number" maxlength="3" value="">
							<span>03月</span>
						</div>
						<div >
							<input id='mon4' class="no-spinners" type="number" maxlength="3" value="">
							<span>04月</span>
						</div>
					</div>
					<div class="bg-g">
						<div >
							<input id='mon5' class="no-spinners" type="number" maxlength="3" value="">
							<span>05月</span>
						</div>
						<div >
							<input id='mon6' class="no-spinners" type="number" maxlength="3" value="">
							<span>06月</span>
						</div>
						<div >
							<input id='mon7' class="no-spinners" type="number" maxlength="3" value="">
							<span>07月</span>
						</div>
						<div >
							<input id='mon8' class="no-spinners" type="number" maxlength="3" value="">
							<span>08月</span>
						</div>
					</div>
					<div class="bg-g">
						<div >
							<input id='mon9' class="no-spinners" type="number" maxlength="3" value="">
							<span>09月</span>
						</div>
						<div >
							<input id='mon10' class="no-spinners" type="number" maxlength="3" value="">
							<span>10月</span>
						</div>
						<div >
							<input id='mon11' class="no-spinners" type="number" maxlength="3" value="">
							<span>11月</span>
						</div>
						<div >
							<input id='mon12' class="no-spinners" type="number" maxlength="3" value="">
							<span>12月</span>
						</div>
					</div>
				</div>
				
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" style="margin-right:5px;">取消</button>
					<button class="btn btn-primary" type="button" onclick="save()">送出</button>
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