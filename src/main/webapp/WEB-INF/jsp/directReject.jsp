<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="refresh" content="" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="icon" href="./Style/images/favicon.ico" type="image/x-icon" />
<title>越世實業員工日程管理系統</title>
<!-- Bootstrap core CSS-->
<link href="./Style/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Custom fonts for this template-->
<link href="./Style/vendor/font-awesome/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">
<!-- Custom styles for this template-->
<link href="./Style/css/sb-admin.css" rel="stylesheet">
<script src="./Style/vendor/jquery/jquery.min.js"></script>

<script type="text/javascript">

	$(document).ready(function() {
		
		$( "#attReject" ).click(function() {
			
			var reason = document.getElementById("reason").value;
					
			$.ajax({
			type : 'post',
				url : "/rest/directReject/getStatus",
				datatype : 'json',
				data:{reason : reason},
				success : function(msg) {
					document.getElementById("rejectContent").innerHTML = msg;$(".btn").toggle();
					$("#textArea *").toggle();
				},
				error : function() {
					$(".btn").toggle();
					$("#textArea *").toggle();
					document.getElementById("rejectContent").innerHTML = "駁回失敗!!";
				}
			});
		});

	});
</script>

</head>
<body background="./Style/images/bg-main.png">
	<nav class="navbar fixed-top flex-md-nowrap p-2 navbar-dark bg-dark"
		id="mainNav"> <img src="./Style/images/logo-md.png"
		style="margin-left: 1.5rem;"> </nav>
	<div class="container"
		style="display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; padding-top: 5%;">
		<div class="card card-login mx-auto mt-5"
			style="width: 70%; min-width: 18rem; height: 60%;">
			<div class="card-header" id="rejectContent"
				style="text-align: center; background-color: #F5F5DC;">駁回確認</div>
			<p id='textArea' style="padding-top: 10%;">
				<span class="short_label"
					style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
					: </span> <span class="short_text" style="margin-left: 3%;"><textarea
						rows="1" id="reason" style="resize: none; width: 50%;"></textarea></span>
			</p>
			<br> <a style="text-align: center;" href="/">返回登入頁面</a>
			<div class="modal-footer">
				<button class="btn btn-primary" type="button" id="attReject">確認</button>
			</div>
		</div>
	</div>

	<footer class="sticky-footer" style="width: 100%;">
	<div class="container">
		<div class="text-center">
			<small>All rights reserved. © 2018 YESEE 越世</small>
		</div>
	</div>
	</footer>
</body>
</html>