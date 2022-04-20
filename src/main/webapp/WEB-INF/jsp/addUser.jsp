<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <jsp:include page="header.jsp" />
  <!-- Datetimepicker-->
  <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>  
  <style>
            body
            {
               display:none;/*全部先隱藏*/
            }
			
			td:hover
            {
               cursor: pointer;
            }
  </style>
  <script type="text/javascript">
  var isFinishLoad = false;
  var authorise = "<%= session.getAttribute( "Authorise" ) %>";
  var companyId = "<%= session.getAttribute( "companyId" ) %>";
  var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
  		$(document).ready(function () {
  			$('#loading').modal('show');
			$("#loading").on('shown.bs.modal', function () {
				 isFinishLoad = true;
		    });
			closeLoading();
			
			if(authorise != 1 && authorise != 2){
				window.location.href = "timeout.do";
			}
			if(authorise == 1 || authorise == 2 || authorise == 3){
				appendUnsign();
	  			getUnsign();
		   	}
  	        
  			$.ajax({
	            type: "POST",
	            url:  '/rest/addUser/getDepList',
	            datatype:'json',
	            data : {
	            	authorise : authorise,
	            	companyId : companyId
				},
	            success: function(data)
	            {
	            	$("#dep").get(0).options.length = 0;
	                $("#dep").get(0).options[0] = new Option("Select ...", "-1");   
	     
	                $.each(data, function(index, item) {
	                    $("#dep").get(0).options[$("#dep").get(0).options.length] = new Option(item.name,item.id);
	                });
	                
	            	$("#depEdit").get(0).options.length = 0;
	                $("#depEdit").get(0).options[0] = new Option("Select ...", "-1");   
	     
	                $.each(data, function(index, item) {
	                    $("#depEdit").get(0).options[$("#depEdit").get(0).options.length] = new Option(item.name,item.id);
	                });
	                
	            }
	        });
  			
  			$(function() {
				$("#onBoardDate").datepicker(
						{
							dateFormat : 'yy-mm-dd',
							monthNames : [ '一月', '二月', '三月', '四月', '五月',
									'六月', '七月', '八月', '九月', '十月', '十一月',
									'十二月' ],
							dayNamesMin : [ '日', '一', '二', '三', '四', '五',
									'六' ],
							firstDay : 1
						});
  			});
  			
  			getRecords();
  			$('#dataTable').DataTable({ 
  				"bAutoWidth" : false,
  				"bLengthChange" : false,
  				"pageLength" : 20,
  				"columnDefs":[
  					{"orderable": false,"targets" : 3},
 					{"targets": [ 0 ],
 		                "visible": false,
 		                "searchable": false}
  				],
  				"oLanguage": {
  	                "sProcessing": "讀取中...",
  	                "sLengthMenu": "Show _MENU_ entries",
  	                "sZeroRecords": "查無相符的資料",
  	                "sEmptyTable": "無資料可顯示",
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
  			
  			$("body").show();
  		});
  			
  			function add(){
  				timeoutCheck();
	  			        $.ajax({
	  						type:'post',
	  						url:"/rest/addUser/addUser",
	  						dataType: "text",
	  						data:{
	  							empEnName:$("#empEnName").val(),
	  							empChName:$("#empChName").val(),
	  							dep:$("#dep").val(),
	  							onBoardDate:$("#onBoardDate").val(),
	  						},
	  							
	  						success:function(msg){
	  								$.confirm({
	  									title:'訊息',
	  									content: '新增成功',
	  								    buttons: {
	  								    	OK: function () {
	  								    		$('#MakeUp').modal('toggle');
	  			  								getRecords();
	  								        },
	  								    }
	  								});
	  								
	  						},
	  						error:function(){ 
	  							confirm("訊息","新增失敗");
	  							    }
	  							});
  	        	};
  	        function check(){
  	        	timeoutCheck();
  	        	if($("#empEnName").val().length<1){
  	        		$.confirm({
						title:'提示',
						content: '英文姓名不能為空',
					    buttons: {
					    	OK: function () {
					    		$("#name").focus();
					        },
					    }
					});
  	        		return;
  	        	}else if($("#empChName").val().length<1){
  	        		$.confirm({
						title:'提示',
						content: '中文姓名不能為空',
					    buttons: {
					    	OK: function () {
					    		$("#name").focus();
					        },
					    }
					});
  	        		return;
  	        	}else if ($("#dep").val()==-1){
  	        		confirm('提示','請選擇部門');
  	        		return;
  	        	}
  	        	add();
			}

			function keyLogin() {
				if (event.keyCode == 13)
					check();
			}

			function getRecords() {
				timeoutCheck();
				$.ajax({
					type : 'post',
					url : "/rest/addUser/getEmployeesList",
					datatype : 'json',
					 data : {
			            	authorise : authorise,
			            	companyId : companyId
						},
					success : function(data) {
						var record = JSON.parse(data);
						var t = $('#dataTable').DataTable();
						t.clear().draw();
						for (var i = 0; i < record.length; i++) {
							var status = "在職";
							if(record[i]["status"] == "N"){
								status = "離職";
							}
							var nameTrans ="";
							if(nameSelect=="TW"){
								nameTrans = record[i]["chineseName"];
							}else {
								nameTrans = record[i]["username"];
							}
							var temp = t.row.add([
								record[i]["username"],
								nameTrans,                  
								record[i]["department"],
								status,
								record[i]["onBoardDate"]
								]).draw().nodes().to$();
							var data = "('" + record[i]["username"] + "','" 
									+ nameTrans + "','"
									+ record[i]["department"] + "',"
									+ record[i]["groupId"] + ",'"
									+ record[i]["status"] + "');";
							$(temp).attr("onclick", "show" + data + ";");
						}
					},
					error : function() {
						confirm("訊息","取得員工資料失敗");
							}
						});
			};

			function show(accountName, name, depEdit, id, status) {
				timeoutCheck();
				$("#Name").val(name);
				$("#accountName").val(accountName);
				var depVal = $('#depEdit option').filter(function() {
					return $(this).text() === depEdit;
				}).val();
				$("#depEdit").val(depVal);
				$("#GroupId").val(id);
				$("#Status").val(status);
				$("#Employees").modal("show");
			};

			function edit() {
				timeoutCheck();
				if ($("#GroupId").val() == '3' || $("#GroupId").val() == '2') {
					$.confirm({
						title : '訊息',
						content : '主管修改部門時，權限將變成一般職員',
						buttons : {
							OK : function() {
								upd();
							},
							close : function() {
								$("#Employees").modal("hide");
							}
						}
					});
				} else {
					upd();
				}

			};

			function upd() {
				timeoutCheck();
				var dep = $("#depEdit").val();
				if(dep==-1){
					confirm('提示','請選擇部門');
					return;
				}
				$.ajax({
					type : 'post',
					url : "/rest/addUser/edit",
					datatype : 'text',
					data : {
						name : $("#accountName").val(),
						dep : $("#depEdit").val(),
						groupId : $("#GroupId").val(),
						status : $("#Status").val()
					},
					success : function(msg) {
						$.confirm({
							title : '訊息',
							content : '修改成功',
							buttons : {
								OK : function() {
								},
							}
						});
						$("#Employees").modal("hide");
						getRecords();
					},
					error : function() {
						confirm("訊息","修改失敗");
							}
						});
					}
			
			$(document).on("click", "#addAttInfoBtn", function() {
				$(".modal-body #empChName").val("");
				$(".modal-body #empEnName").val("");
				$(".modal-body #dep").val("-1");
				$(".modal-body #onBoardDate").val("");
			});

			function del() {
				timeoutCheck();
				var name = $("#accountName").val();
				$.confirm({
					title: '刪除確認',
					content: '確定刪除此員工?',
				    buttons: {
				    	 "刪除": {btnClass: 'btn-red',
					        	action:function () {
				        			timeoutCheck();
							$.ajax({
								type : 'post',
								url : "/rest/addUser/del",
								datatype : 'text',
								async : false,
								data : {
									name : name
								},
								success : function(msg) {
											confirm("訊息","刪除成功");
												$("#Employees").modal("hide");
												getRecords();
											},
								error : function() {
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
					<div class="card-header">員工列表</div>
					<div class="card-body">
						<div class="table-responsive">
							<div class="mr-auto p-3">
									<button class="userbutton" id="addAttInfoBtn"
										data-toggle="modal" data-target="#MakeUp">新增</button>
							</div>
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="display:none;">帳號</th>
										<th>員工姓名</th>
										<th>部門</th>
										<th>狀態</th>
										<th>到職日期</th>
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

	<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'  style="text-align: center;width:100%;height:100%;padding-left:0px;">
      <div class="modal-dialog">  
        <img src="./Style/images/loading.gif" style="padding-top:12rem;">
      </div>
  </div>
  
  <div class="modal fade" id="Check" tabindex="-1" role="dialog" aria-labelledby="CheckLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="CheckLabel">資料確認</h5>
            <button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">×</span>
            </button>
          </div>
          <div class="modal-body">
          	<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">員工姓名 : </span>
				<span class="short_text" style="margin-left:3%;"><a id="nameCheck" type="text" style="width:50%;"></a>
				</span>
			</p>
			<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">部門 : </span>
				<span class="short_text" style="margin-left:3%;"><a id="depCheck" type="text" style="width:50%;"></a>
				</span>
			</p>
			<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">職稱 : </span>
				<span class="short_text" style="margin-left:3%;"><a id="groupIdCheck" type="text" style="width:50%;"></a>
				</span>
			</p>
          </div>
          <div style="text-align:center;width:100%;padding-top:3rem;">
          	請確認資料是否無誤
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" type="button" data-dismiss="modal" id="cancel" style="margin-right:5px;">取消</button>
            <button class="btn btn-primary" type="button" onclick="add()" id="send">送出</button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="modal fade" id="Employees" tabindex="-1" role="dialog" aria-labelledby="EmployeesLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="EmployeesLabel">員工資料修改</h5>
            <button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">×</span>
            </button>
          </div>
          <div class="modal-body">    				
			<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">員工姓名 : </span>
				<span class="short_text" style="margin-left:3%;"><input id="Name" type="text" disabled="disabled" style="width:40%;"></span>
			</p>
			<p style="display:none;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">員工姓名 : </span>
				<span class="short_text" style="margin-left:3%;"><input id="accountName" type="text" disabled="disabled" style="width:40%;"></span>
			</p>
			<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">部門 : </span>
				<span class="short_text" style="margin-left:3%;">
					<select id='depEdit'  style="width:40%;height:2rem;">
					</select>
				</span>
			</p>
			<p style="padding-top:1%;display:none;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">權限 : </span>
				<span class="short_text" style="margin-left:3%;">
					<select id='GroupId'  style="width:8rem;height:2rem;" disabled>
						<option style="display:none;" value='4'>一般職員</option>
						<option value='4'>一般職員</option>
						<option value='3'>主管</option>
						<option value='2'>人事</option>
					</select>
				</span>	
			</p>
			<p style="padding-top:1%;">
				<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">狀態 : </span>
				<span class="short_text" style="margin-left:3%;">
					<select id="Status" style="width:15%;">
						<option value='Y'>在職</option>
						<option value='N'>離職</option>
					</select>
				</span>
			</p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" type="button" onclick="edit()" style="margin-right:5px;">修改</button>
            <button class="delbutton" type="button"  onclick="del()">刪除</button>
          </div>
    	 </div>
    	</div>
    </div>
    
    <div class="modal fade" id="MakeUp" tabindex="-1" role="dialog"
		aria-labelledby="MakeUpLabel" aria-hidden="true"
		data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="MakeUpLabel">新增員工</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body add">
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
							style="color: red">*</span>員工帳號 : </span> <span class="short_text"
							style="margin-left: 3%;"><input id="empEnName"
								style="resize: none; width: 50%; overflow: hidden;"></input></span>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
							style="color: red">*</span>員工中文名字 : </span> <span class="short_text"
							style="margin-left: 3%;"><input id="empChName"
								style="resize: none; width: 50%; overflow: hidden;"></input></span>
					</p>
					<p style="padding-top: 1%;">
					<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;"><span
							style="color: red">*</span>部門: </span> <span class="short_text"
							style="margin-left: 3%;"> 
					<select id='dep' style="width: 12rem; height: 2rem;"></select>
					</p>
					<p style="padding-top: 1%;">
						<span class="short_label"
							style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">到職日期
							: </span> <span class="short_text" style="margin-left: 3%;"><input
							id="onBoardDate" type="text" readonly="readonly" size="15" /></span>
					</p>
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
						data-dismiss="modal" style="margin-right: 5px;">取消</button>
					<button class="btn btn-primary" type="button" onclick="check()">送出</button>
				</div>
			</div>
		</div>
	</div>
    
    <jsp:include page="JSfooter.jsp" />
</body>

</html>