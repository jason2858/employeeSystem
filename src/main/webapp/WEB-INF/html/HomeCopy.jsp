<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
  <head>
	  <meta charset="utf-8">
	  <link rel="./Style/images/icon" href="favicon.ico">
	  <title>越世實業員工日程管理系統</title>
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <link href="./Style/dashboard.css" rel="stylesheet">
	  <link href="./Style/css/bootstrap.min.css" rel="stylesheet">
	  <!-- <link href="css/bootstrap.min.css" rel="stylesheet"> -->
	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	  <script src="./Style/js/bootstrap.min.js"></script>
      <script type="text/javascript">
        $(document).ready(function () {
        	$.ajax({
				type:'post',
				url:"GetSession.do",
				datatype:'text',
				success:function(data){
					if(data != 1)
						history.go(0);
				},
				error:function(){ 
					alert("Get Authorise Error");  
				} 
			});
        });
      </script>
  </head>

  <body background="./Style/images/bg-main.png">
    <nav class="navbar fixed-top flex-md-nowrap p-0" style="background-color: #0B5677; height:3.1rem;">
	  <div>
		<img src="./Style/images/logo-md.png" style="margin: 1rem;">
		<button class="Headbutton" style="margin-left: 2rem;" data-toggle="collapse" data-target="#sidebar">
				<span data-feather="menu" ></span>
		</button>
	  </div>
	  <div>
	    <ul class="list-inline">
		  <button class="Headbutton" disabled>
				<span data-feather="user" ></span>
			  frank.chou
		  </button>
		  <button class="Headbutton" onclick="location.href='logout.do'">
				<span data-feather="power"></span>
			  登出
		  </button>
		</ul>
	  </div>
    </nav>
	 <div id="sidebar" data-toggle="collapse">
		<div class="container-fluid">
		  <div class="row">
			<nav class="d-none d-md-block bg-light sidebar">
			  <div class="sidebar-sticky">
				<ul class="nav flex-column">
				  <li class="nav-item">
					<a class="nav-link" href="#">
					  上班打卡
					</a>
				  </li>
				  <li class="nav-item">
					<a class="nav-link" data-toggle="collapse" data-target="#home" href="#" >
					  首頁<span class="badge badge-danger">4</span>
					</a>
					<div id="home" class="collapse">
						<a class="nav-link" style="padding-left: 3rem;" href="#">
							功能一
						</a>
						<a class="nav-link" style="padding-left: 3rem;" href="#">
							功能二
						</a>
					</div>
				  </li>
				  <li class="nav-item">
					<a class="nav-link" href="#">
					  工時報表
					</a>
				  </li>
				  <li class="nav-item">
					<a class="nav-link" href="#">
					  打卡紀錄查詢
					</a>
				  </li>
				</ul>
			  </div>
			</nav>
		  </div>
		</div>
	</div>
    <!-- Bootstrap core JavaScript
    ================================================== -->


    <!-- Icons -->
    <script src="https://unpkg.com/feather-icons/dist/feather.min.js"></script>
    <script>
      feather.replace()
    </script>
	
  </body>
</html>
