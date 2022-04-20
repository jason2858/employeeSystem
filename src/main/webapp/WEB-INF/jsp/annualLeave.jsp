<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<jsp:include page="header.jsp" />
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

td:hover{
	cursor : pointer;
}
</style>
<script type="text/javascript">
var isFinishLoad = false;
var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
	$(document).ready(
			function() {
				$('#loading').modal('show');
				$("#loading").on('shown.bs.modal', function () {
					 isFinishLoad = true;
			    });
				var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
				var companyId = '<%out.print(session.getAttribute("companyId"));%>';
				closeLoading();
				
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
					data : {Live:"Y"},
					success : function(data) {
							data = JSON.parse(data.entity);
							for(var i=0;i<data.length;i++){
								$("#emp").get(0).options[0] = new Option("Select ...", "-1");
								$.each(data, function(index, item) {
					            	$("#emp").get(0).options[$("#emp").get(0).options.length] = new Option(item.name);
					        	});
							}
						}
				 });
				
				$.ajax({
					type : "POST",
					url : '/rest/project/getEmpList',
					dataType : "json",
					success : function(data) {
							data = JSON.parse(data.entity);
							if(nameSelect=="TW"){
									$.each(data, function(index, item) {
						            	$("#empEdit").get(0).options[$("#empEdit").get(0).options.length] = new Option(item.chineseName,item.name);
						        	});
								} else {
									$.each(data, function(index, item) {
						            	$("#empEdit").get(0).options[$("#empEdit").get(0).options.length] = new Option(item.name,item.name);
						        	});
								}
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
				        "columns": [
				            { "data": "empName" },
				            { "data": "dep" },
				            { "data": "entitledHours" },
				            { "data": "year"  }
				        ]
			    } );
				
				var yearFromNow = (new Date()).getFullYear();
				$('#yearSear').val(yearFromNow);
				
				$.fn.dataTable.ext.search.push(
					      function( settings, data, dataIndex ) {
					          return authorise=='2'&&data[3]==yearFromNow||
					          authorise=='1'&&data[3]==yearFromNow
					              ? true
					              : false
					      }     
					  );
				
				$('#dataTable').on( 'click', 'td', function () {
					var data = table.row( this ).data();
			        $('#UpdateAndDelete').modal("show");
			        
			      //set option by text
			        var depVal = $('#empEdit option').filter(function() {
			        					return $(this).text() === data.empName;
			        			}).val();
			        $("#empEdit").val(depVal);
			        
					document.getElementById("entiHoursEdit").value = data.entitledHours;
					document.getElementById("yearEdit").value = data.year;
			    } );
				
				$('#yearSear').change(function(){
					var year = document.getElementById("yearSear").value;
					var table = $('#dataTable').DataTable();
					
					$.fn.dataTable.ext.search.splice(0);//先刪除全部
					if(year!='0'){
					 $.fn.dataTable.ext.search.push(
						      function( settings, data, dataIndex ) {
						          return data[3]==year
						              ? true
						              : false
						      }     
						  );
					}
					 table.draw();
				});
				
				$("body").show();
				
			});
	
	function encodeHTML(s) {
		if (s==='' || s==null) return '';
	    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/"/g, '&quot;').replace(/>/g, '&gt;').replace(/'/g, '&#x27;');
	}
	
    function save(){
    	timeoutCheck();
	    	var empName = document.getElementById("emp").value;
			var entitledHours = document.getElementById("entiHours").value;
			var year = document.getElementById("year").value;
			
			var ann_data = {
					"empName" : empName,
					"entitledHours" : entitledHours,
					"year" : year
			};
    	
			$.ajax({
			type:'post',
			url:"/rest/acc/annualSave",
			dataType : "text",
			contentType : 'application/json;charset=UTF-8',
			data : JSON.stringify(ann_data),
			success:function(data){
				if(data=="success"){
					confirmWithFunction("訊息","新增成功",function(){history.go(0);});
				}else {
					confirmWithFunction("訊息","新增失敗",function(){history.go(0);});
				}
			},
			error:function(){
				confirmWithFunction("訊息","新增失敗",function(){history.go(0);});
			}
		}); 
	}
    
    function updAnn(){
    	timeoutCheck();
	    	var empName = document.getElementById("empEdit").value;
			var entitledHours = document.getElementById("entiHoursEdit").value;
			var year = document.getElementById("yearEdit").value;
			
			var ann_data = {
					"empName" : empName,
					"entitledHours" : entitledHours,
					"year" : year
			};
    	
			$.ajax({
			type:'post',
			url:"/rest/acc/annualUpd",
			dataType : "text",
			contentType : 'application/json;charset=UTF-8',
			data : JSON.stringify(ann_data),
			success:function(data){
				confirmWithFunction("訊息","修改成功",function(){history.go(0);});
			},
			error:function(){
				cconfirmWithFunction("訊息","修改失敗",function(){history.go(0);});
			}
		}); 
	}
    
    function delAnnInfo() {
		var empName = document.getElementById("empEdit").value;
		var year = document.getElementById("yearEdit").value;
		//initiate json object
		var del_data = {
			"empName" : empName,
			"year": year
		};
		$.confirm({
			title: '刪除確認',
			content: '確定刪除此特休資料?',
		    buttons: {
		    	 "刪除": {btnClass: 'btn-red',
			        	action:function () {
		        			timeoutCheck();
		        			$.ajax({
								type : "POST",
								url : '/rest/acc/deleteAnnual',
								dataType : "text",
								contentType : 'application/json;charset=UTF-8',
								data : JSON.stringify(del_data),
								success : function(data) {
									confirmWithFunction("訊息","刪除成功",function(){history.go(0);});
								},
								error : function(data) {
									confirmWithFunction("訊息","刪除失敗",function(){history.go(0);});
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
    
</script>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
	<!-- Navigation-->
	<jsp:include page="navbar.jsp" />
	<div class="content-wrapper">
		<div class="container-fluid">
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">特休管理</div>
					<div class="card-body">
						<div style="width: 100%; height: 4rem;"
							class="d-flex align-items-center">
							<div class="mr-auto p-3">
								<button class="userbutton" id="addAnnInfoBtn"
									data-toggle="modal" data-target="#MakeUp">新增</button>
							</div>
							<label id='yearSearLa' style="margin-top:0.6%"> 年份: </label>
							<select id='yearSear' style="margin-left:1%;margin-right:1.5%;width:20%" >
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
										<th>員工名稱</th>
										<th>部門</th>
										<th>特休時數</th>
										<th>年份</th>
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
					<h5 class="modal-title" id="MakeUpLabel">新增特休</h5>
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
							style="margin-left: 3%;" id="emp">
						</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">特休時數
							: </span> <span class="short_text" style="margin-left: 3%;"><textarea
								rows="1" id="entiHours" style="resize: none; width: 50%;"></textarea></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">年份
							: </span> 
							<select id='year' style="margin-left: 3%;" >
							<option value='0'>...</option>
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
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" style="margin-right:5px;">取消</button>
					<button class="btn btn-primary" type="button" onclick="save()">送出</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id=UpdateAndDelete tabindex="-1" role="dialog"
		aria-labelledby="MakeUpLabel" aria-hidden="true"
		data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">特休資料修改</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body edit">
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
							style="color: red">*</span>員工 : </span> <select
							style="margin-left: 3%;" id="empEdit" disabled>
						</select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">特休時數
							: </span> <span class="short_text" style="margin-left: 3%;"><textarea
								rows="1" id="entiHoursEdit" style="resize: none; width: 50%;"></textarea></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">年份
							: </span> 
							<select id='yearEdit' style="margin-left: 3%;" disabled>
							<option value='0'>...</option>
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
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" style="margin-right:5px;">取消</button>
						<button class="btn btn-primary" type="button"
						onclick="delAnnInfo()" id="deleteAttInfo" style="margin-right:5px;">刪除</button>
					<button class="btn btn-primary" type="button" onclick="updAnn()">送出</button>
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