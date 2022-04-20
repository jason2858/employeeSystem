<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <jsp:include page="header.jsp" />
  <style type="text/css">
            body
            {
               display:none;/*全部先隱藏*/
            }
            </style>
  <script type="text/javascript">
  var isFinishLoad = false;
  var authorise = '<%= session.getAttribute( "Authorise" ) %>';
  		$(document).ready(function () {
  			$('#loading').modal('show');
			$("#loading").on('shown.bs.modal', function () {
				 isFinishLoad = true;
		    });
			closeLoading();
			
			if(authorise != 1 && authorise != 2 && authorise != 3){
				window.location.href = "timeout.do";
	  		}
	  		if (authorise == 1 || authorise == 2 || authorise == 3) {
	  			appendUnsign();
	  			getUnsign();
	  		}
  			
  			getRecords();
  			$('#dataTable').DataTable({ 
  				"bAutoWidth" : false,
  				"order":[[2,"desc"],[3,"desc"]],
  				"columnDefs":[
  					{"width": "23%","targets" : 0},
  					{"width": "25%","targets" : 1},
  					{"width": "17%","targets" : 2},
  					{"width": "35%","targets" : 3}
  				],
  				"bLengthChange" : false,
  				"pageLength" : 20,
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
  		
  			function getRecords(){
	  			$.ajax({
					type:'post',
					url:"/rest/status/getRecords",
					datatype:'json',
					data: {Live:"Y"},
					success:function(data){
						var record=JSON.parse(data);
						var t = $('#dataTable').DataTable();
						var absent=0,work=0,rest=0;
						for(var i=0;i<record.length;i++){
							var type = "已下班";
							var time = record[i]["time"];
							if(record[i]["type"]=="in"||record[i]["type"]=="makeupin"){
								type = "上班中";
								work = work + 1 ;
							}
							else if(record[i]["type"]=="absence"){
								type = "未到班";
								time = "尚無紀錄";
								absent = absent + 1;
							}
							else
								rest = rest + 1;
							var temp = t.row.add( [ 
								record[i]["username"],
								record[i]["dep"],
								type,
								time
						    	 ] )
						    .draw().nodes().to$();
							$(temp).find('td:eq(0)').attr("style","padding-left:2%;");
							if(record[i]["type"]=="in"||record[i]["type"]=="makeupin")
								$(temp).find('td:eq(0)').prepend("<img src='./Style/images/in.jpg' style='margin-right:1.5rem;'/>");
							if(record[i]["type"]=="out"||record[i]["type"]=="makeupout")
								$(temp).find('td:eq(0)').prepend("<img src='./Style/images/out.jpg' style='margin-right:1.5rem;'/>");
							if(record[i]["type"]=="absence")
								$(temp).find('td:eq(0)').prepend("<img src='./Style/images/not.jpg' style='margin-right:1.5rem;'/>");
							$(temp).find('td:eq(1)').attr("style","padding-left:2%;");
							$(temp).find('td:eq(2)').attr("style","padding-left:2%;");
							$(temp).find('td:eq(3)').attr("style","padding-left:2%;");
						}
						$("#global").html("總人數 : " + record.length + "&nbsp;&nbsp;&nbsp;上班人數 : " + work + "&nbsp;&nbsp;&nbsp;下班人數 : " + rest + "&nbsp;&nbsp;&nbsp;未到班人數 : " + absent);
					},
					error:function(){ 
						confirm("訊息","取得狀態資料失敗");
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
					<div class="card-header">即時員工狀態</div>
					<div class="card-body">
						<div style="padding-left: 2%; width: 50%;">
							<b id="global"></b>
						</div>
						<div class="table-responsive">
							<table class="table table-bordered" id="dataTable" width="100%"
								cellspacing="0">
								<thead>
									<tr bgcolor="#E6E6E6">
										<th style="padding-left: 5%;">員工名稱</th>
										<th style="padding-left: 2%;">部門</th>
										<th style="padding-left: 2%;">當前狀態</th>
										<th style="padding-left: 2%;">最後打卡時間</th>
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
    <jsp:include page="JSfooter.jsp" />
</body>

</html>