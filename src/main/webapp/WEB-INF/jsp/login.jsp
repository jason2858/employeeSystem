<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">
        <link rel="icon" href="./Style/images/favicon.ico" type="image/x-icon" />
        <title>越世實業員工日程管理系統</title>
        <!-- Bootstrap core CSS-->
        <link href="./Style/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <!-- Custom fonts for this template-->
        <link href="./Style/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <!-- Custom styles for this template-->
        <link href="./Style/css/sb-admin.css" rel="stylesheet">
        <script src="./Style/vendor/jquery/jquery.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                var username = getCookie('account');
                if (username) {
                    document.getElementById("account").value = username;
                }
                $.ajax({
                    type: 'post',
                    url: "./rest/login/getAuthorise",
                    datatype: 'text',
                    success: function(data) {
                        if (data != 1 && data != 2 && data != 3 && data != 4)
                        ;
                        else {
                            $.ajax({
                                type: 'post',
                                url: './rest/setting/getPersonalHomepage',
                                datatype: 'text',
                                success: function(data) {
                                    document.location.href = "./" + data;
                                },
                                error: function() {
                                    document.location.href = "./home.do";
                                }
                            });
                        }
                    },
                    error: function() {

                    }
                });


            });

            function check() {
                $('#loading').modal('show');
                var account = $("#account").val();
                var password = $("#password").val();
                $.ajax({
                    type: 'post',
                    url: "./rest/login/accountCheck",
                    datatype: 'text',
                    data: {
                        account: account,
                        password: password
                    },
                    success: function(data) {
                        var response = JSON.parse(data);
                        data = response.entity;
                        if (data == "true") {
                            setCookie('account', account, 1);
                            $.ajax({
                                type: 'post',
                                url: './rest/setting/getPersonalHomepage',
                                datatype: 'text',
                                success: function(data) {
                                    document.location.href = "./" + data;
                                },
                                error: function() {
                                    document.location.href = "./home.do";
                                }
                            });
                        } else {
                            $("#password").val("");
                            setTimeout(function() {
                                $("#loading").modal('hide');
                            }, 500);
                            $("#errorMsg").html(data);
                            $("#alert").show();
                            setTimeout(function() {
                                $("#alert").fadeOut(1500);
                            }, 1500);
                        }
                    },
                    error: function() {

                    }
                });
            }

            function setCookie(name, value, days) {
                var expires = "";
                if (days) {
                    var date = new Date();
                    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                    expires = "; expires=" + date.toUTCString();
                }
                document.cookie = name + "=" + (value || "") + expires + "; path=/";
            }

            function getCookie(name) {
                var nameEQ = name + "=";
                var ca = document.cookie.split(';');
                for (var i = 0; i < ca.length; i++) {
                    var c = ca[i];
                    while (c.charAt(0) == ' ') c = c.substring(1, c.length);
                    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
                }
                return null;
            }

            function eraseCookie(name) {
                document.cookie = name + '=; Max-Age=-99999999;';
            }

            function keyLogin() {
                if (event.keyCode == 13)
                    check();
            }
        </script>
    </head>

    <body background="./Style/images/bg-main.png" onkeydown="keyLogin();">
        <nav class="navbar fixed-top flex-md-nowrap p-2 navbar-dark bg-dark" id="mainNav">
            <img src="./Style/images/logo-md.png" style="margin-left: 1.5rem;">
        </nav>
        <div class="container" style="display:flex;justify-content:center;align-items:center;width:100%;height:100%;padding-top:5%;">
            <div class="card card-login mx-auto mt-5" style="width:70%;min-width:18rem;height:60%;">
                <div class="card-header" style="text-align:center; background-color:#E9ECEF;">越世員工登入</div>
                <div class="card-body">
                    <form id="login_form">
                        <div class="form-group">
                            <label for="exampleInputEmail1">員工帳號 :</label>
                            <input type="text" class="form-control" id="account" name="account" placeholder="請輸入使用者帳號" value="" required>
                        </div>
                        <div class="form-group">
                            <label for="exampleInputPassword1">員工密碼 :</label>
                            <input type="password" class="form-control" id="password" name="password" placeholder="請輸入使用者密碼" value="" required>
                        </div>
                        <button class="btn btn-primary btn-block" type="button" id="login" onclick="check()">登入</button>
                    </form>
                </div>
            </div>
        </div>
        <!-- Alert-->
        <div id="alert" style="text-align:center;width:100%;padding-top:1%; display:none;">
            <div class="alert alert-danger" id="errorMsg" role="alert" style="text-align:left;width:25%;min-width:10rem;height:1%;margin:0 auto;">
                <i class="fa fa-exclamation-triangle" aria-hidden="true" style="margin-right:0rem;"></i>

            </div>
        </div>
        <footer class="sticky-footer" style="width: 100%;">
            <div class="container">
                <div class="text-center">
                    <small>All rights reserved. © 2018 YESEE 越世</small>
                </div>
            </div>
        </footer>
        <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static' style="text-align: center;width:100%;height:100%;padding-left:0px;">
            <div class="modal-dialog">
                <img src="./Style/images/loading.gif" style="padding-top:12rem;">
            </div>
        </div>
        <!-- Bootstrap core JavaScript-->
        <script src="./Style/vendor/jquery/jquery.min.js"></script>
        <script src="./Style/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
        <!-- Core plugin JavaScript-->
        <script src="./Style/vendor/jquery-easing/jquery.easing.min.js"></script>
    </body>

    </html>