<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <jsp:include page="header.jsp" />
  <style type="text/css">
      body
      {
        display:none;/*全部先隱藏*/
      }
      .fileList li {
        margin-bottom: 8px;
        list-style: none;
      }
      table.dataTable {
          margin-top: 0 !important;
          border-collapse: collapse !important;
      }
      .dataTable tbody tr {
        cursor: pointer;
        transition: .15s;
      }
      .dataTable tbody tr:hover {
        background-color: #f6f6f6;
      }
      .dataTable td {
        vertical-align: middle;
      }
      .dataTable td:nth-child(2) {
        word-wrap: break-word;
        word-break: break-all;
      }
      .dataTable td:nth-child(4) {
        text-align: center;
      }
      .announceTab {
          border: 1px solid #E6E6E6;
          color: #888;
          border-bottom: none;
          border-radius: 4px 4px 0 0;
          transition: .25s;
          cursor: pointer;
      }
      .announceTab:hover {
          background: #EFEFEF;
          color: #333;
      }
      .announceTab.active {
          color: #000;
          background: #E6E6E6;
          cursor: default;
          z-index: 1;
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
			
			if (authorise != 1 && authorise != 2 && authorise!=3 && authorise!=4) {
				window.location.href = "timeout.do";
			}
			if (authorise == 1 || authorise == 2 || authorise == 3) {
				appendUnsign();
        getUnsign();
			}
			
			if(authorise != 1){
				$("#companyTh").hide();
			}
			
			if(authorise ==1 || authorise == 2){
				$.ajax({
					type:'post',
					url:"/rest/announce/getCompany",
					datatype:'json',
					success:function(data){
						var response = JSON.parse(data);
						var record = JSON.parse(response.entity);
						for(var i=0;i<record.length;i++){
							$("#announceCompany").append("<option value='" + record[i]["id"] + "'>" + record[i]["name"] + "</option>");
						}
					},
					error:function(){ 
						confirm("訊息","取得公司資料失敗");
					} 
				});
			}else{
				$("#announceBtn").hide();
				$("#editTh").hide();
			}

      getRecords();
      const dataTable = $('#dataTable').DataTable({ 
        "bAutoWidth" : false,
        "bSort":false,
        "searching" : false,
        "bLengthChange" : false,
        "pageLength" : 10,
        "dom": `<'row'<'col-sm-12'tr>>
                <'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>`,
        "columnDefs":[
                {"width": "20%","targets" : 0},
                {"width": "50%","targets" : 1},
                {"width": "20%","targets" : 2},
                {"width": "10%","targets" : 3}
              ],
        "oLanguage": {
                  "sProcessing": "讀取中...",
                  "sLengthMenu": "Show _MENU_ entries",
                  "sZeroRecords": "查無相符的資料",
                  "sEmptyTable": "目前無公告",
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
        const announceTabList = document.getElementById('announceTabList');
        const announceTabs = document.querySelectorAll('.announceTab');
        announceTabList.addEventListener('click', (e) => {
          const { type } = e.target.dataset
          for (let i = 0; i < announceTabs.length; i++) {
            announceTabs[i].classList.remove('active');
          }
          e.target.classList.add('active');

          const data = store.getData().filter((value, index) => value[4].indexOf(type) != -1)
          dataTable.clear().rows.add(data).draw()
          for (let i = 0; i < data.length; i++) {
            $('#dataTable tbody').find('tr:eq(' + i + ')').attr('data-value', JSON.stringify(data[ i ][ data[i].length - 1 ]))
            if(authorise != 1){
              $('#dataTable tr').find('td:eq(0)').hide();
            }
            if(authorise != 1 && authorise != 2){
              $('#dataTable tr').find('td:eq(3)').hide();
            }
          }
          if(authorise != 1){
            $('#dataTable').find('th:eq(0)').hide();
          }
          if(authorise != 1 && authorise != 2){
            $('#dataTable').find('th:eq(3)').hide();
          }
        })
        dataTable.on('click', (e) => {
          const node = e.target.parentNode
          const { btn } = e.target.dataset
          const { value } = node.nodeName === 'TR' ? node.dataset : node.parentNode.dataset
          if (!value) return
          switch (btn) {
            case 'edit':
              editShow(value)
              break;
            default:
              detailShow(value)
          }
        })
      });

      const store = (function () {
        const data = {
          data: []
        }
        return {
          getData(){
            return data.data
          },
          setData(value){
            data.data = value
          }
        }
      })()

      function getRecords(){
        timeoutCheck();
        $.ajax({
					type:'post',
					url:"/rest/announce/getRecords",
					datatype:'json',
					success:function(data){
						var response = JSON.parse(data);
						var record = JSON.parse(response.entity);
						var t = $('#dataTable').DataTable();
						t.clear().draw();
						for(var i=0;i<record.length;i++){
              const editBtnHtml = authorise == 1 || authorise == 2 
                                    ? "<button class='signbutton' type='button' data-btn='edit'>編輯</button>"
                                    : ""
							var temp = t.row.add( [
								record[i]["companyName"],
								record[i]["subject"],
								record[i]["time"],
								editBtnHtml,
								record[i]["type"],
                record[i]
                  ] )
              .draw().nodes().to$();
							$(temp).attr("data-value", JSON.stringify(record[i]));
						}
            store.setData(t.data())
            $('#all').click();
					},
					error:function(){ 
						confirm("訊息","取得公告失敗");
					} 
				});
      };

      function detailShow(record){
        record = JSON.parse(record)
        $("#contentTitle").html(record["subject"]);
        $("#contentText").html(record["content"].replace(/\r\n/g,"<br>").replace(/\n/g,"<br>").replace(/\s/g,"&nbsp;"));
        $("#contentText").linkify();
        $('#showFile').empty();
        for(var i=0;i<record["quantity"];i++){
          var fileBtline = 'file'+i;
          var a = document.createElement('a');
          var docpath = "docPath"+i;
          var href = record["docPath"+i];
          var docId =record["docId"+i];
          a.setAttribute("href",href.replaceAll('\\',"\/"));
          a.setAttribute("style"," display: block;");
          a.setAttribute("download",record["docName"+i]);
          a.innerHTML = record["docName"+i];
          document.getElementById('showFile').append(a);
        }
        $("#contentModal").modal('show');
      }

      function addShow(){
        $("#delDoc").val("");
        $("#announceSubject").val("");
        $("#announceContent").val("");
        $("#doc_MultiUpload0").show();
        $("#addBtn").removeAttr('disabled');
        for(var i = 0;i < 5;i++){
          var delfile = "#doc_MultiUpload"+i;
          var fileBtline = 'file'+i;
          $(delfile).val('');
            document.getElementById(fileBtline).innerHTML="";
          if(i>0){
            $(delfile).hide();
          }
        }
        $("#addBtn").show();
        $("#editBtn").hide();
        $("#delBtn").hide();
        $("#controllModal").modal('show');
        $("#announceSubject").focus()
      }

      function add(){
        timeoutCheck();
        $("#addBtn").attr('disabled', true);
        var companyId = $("#announceCompany").val();
        var subject = $("#announceSubject").val();
        var type = $("#announceType").val();
        var content = $("#announceContent").val();
        if(subject.length == 0){
          confirm("訊息","請輸入標題");
            $("#addBtn").removeAttr('disabled');
          return;
        }
        if(content.length == 0){
          confirm("訊息","請輸入內文");
            $("#addBtn").removeAttr('disabled');
          return;
        }
        var formData = new FormData();
        var quantity = 0;
        var fileRepeat = new Array();
        for(var i=0;i<5;++i){
          var docexist = "doc_MultiUpload"+i;
          var file = document.getElementById(docexist).files.length;
          if(file == 1){
            var fileName = document.getElementById(docexist).files.item(0).name;
            quantity++;
            if(fileRepeat.includes(fileName)){
              confirm("訊息","檔名重複");
              $("#addBtn").removeAttr('disabled');
              return;
            }else{
              fileRepeat.push(fileName);
            }
          } 
        }
        formData.append('mode','add');
        formData.append('companyId',companyId);
        formData.append('subject',subject);
        formData.append('type',type);  // 0, 1, 2
        formData.append('content',content);
        formData.append('delDoc',$("#delDoc").val());
        formData.append('quantity',quantity);
        for(let i=0;i<5;++i){
          var docexist = "doc_MultiUpload"+i;
          var docPathid = "#"+docexist;
          var file = document.getElementById(docexist).files.length;
          if(file == 1){
            console.log($(docPathid)[0].files[0])
            formData.append('docPath', $(docPathid)[0].files[0]);
          }
        }
        $.ajax({
					type : 'post',
					url : "/rest/announce/update",
					processData: false,
					contentType: false,
					enctype: 'multipart/form-data;charset=UTF-8',
					data : formData,
					success : function(data) {
						confirm("訊息",JSON.parse(data).entity);
						$("#controllModal").modal('hide');
						getRecords();
					},
					error : function() {
						confirm("訊息","新增失敗");
					}
				});
      }
      
      function editShow(record){
        record = JSON.parse(record)
        $("#editBtn").removeAttr('disabled');
        $("#delBtn").removeAttr('disabled');
        $("#delDoc").val("");
        $("#recordQuentity").val(record["quantity"]);
        $("#key").val(record["id"]);
        $("#updatedAt").val(record["updatedAt"]);
        $("#announceCompany").val(record["companyId"]);
        $("#announceSubject").val(record["subject"]);
        $("#announceType").val(record["type"]).change();
        $("#announceContent").val(record["content"]);
        for(var i = 0;i < 5;i++){
          var delfile = "#doc_MultiUpload"+i;
          var fileBtline = 'file'+i;
          $(delfile).val('');
            document.getElementById(fileBtline).innerHTML="";
          if(i>0){
            $(delfile).hide();
          }
        }
        for(var i=0;i<record["quantity"];i++){
          var delfile = "#doc_MultiUpload"+i;
          $(delfile).hide();
          var fileBtline = 'file'+i;
          var a = document.createElement('a');
          var docpath = "docPath"+i;
          var href = record["docPath"+i];
          var docId =record["docId"+i];
          a.setAttribute("href",href.replaceAll('\\',"\/"));
          a.setAttribute("id",record["docName"+i]);
          a.setAttribute("download",record["docName"+i]);
          a.innerHTML = record["docName"+i];
          document.getElementById(fileBtline).append(a);
          const span = document.createElement('span');
          span.innerHTML ='<button type="button" class="signbutton ml-2" onclick="recordFile('+i+","+docId+')">移除</button>';
          document.getElementById(fileBtline).append(span);
        }
        var delfile = "#doc_MultiUpload"+record["quantity"];
        $(delfile).show();
        $("#addBtn").hide();
        $("#editBtn").show();
        $("#delBtn").show();
        $("#controllModal").modal('show');
      }

      function edit(){
        $("#editBtn").attr('disabled', true);
        $("#delBtn").attr('disabled', true);
        timeoutCheck();
        var id = $("#key").val();
        var updatedAt = $("#updatedAt").val();
        var companyId = $("#announceCompany").val();
        var subject = $("#announceSubject").val();
        var type = $("#announceType").val();
        var content = $("#announceContent").val();
        if(subject.length == 0){
          confirm("訊息","請輸入標題");
            $("#editBtn").removeAttr('disabled');
            $("#delBtn").removeAttr('disabled');
          return;
        }
        if(content.length == 0){
          confirm("訊息","請輸入內文");
            $("#editBtn").removeAttr('disabled');
            $("#delBtn").removeAttr('disabled');
          return;
        }
        var formData = new FormData();
        var quantity = $("#recordQuentity").val();
        var moveQuantity = 0;
        var newQuantity = 0;
        var fileRepeat = new Array();
        formData.append('id',id);
        formData.append('mode','edit');
        formData.append('updatedAt',updatedAt);
        formData.append('type',type);
        formData.append('companyId',companyId);
        formData.append('subject',subject);
        formData.append('content',content);
        formData.append('delDoc',$("#delDoc").val());
        for(var i=0;i<5;i++){
          var docexist = "doc_MultiUpload"+i;
          var file = document.getElementById(docexist).files.length;
          var docPathid = "#"+docexist;
          if(file == 1){
            var fileName = document.getElementById(docexist).files.item(0).name;
            newQuantity++;
            var fileNameCheck = document.getElementById(fileName);
            if(fileNameCheck != null){
              confirm("訊息","檔名重複");
              $("#editBtn").removeAttr('disabled');
                  $("#delBtn").removeAttr('disabled');
              return;
            }
            if(fileRepeat.includes(fileName)){
              confirm("訊息","檔名重複");
              $("#editBtn").removeAttr('disabled');
                  $("#delBtn").removeAttr('disabled');
              return;
            }else{
              fileRepeat.push(fileName);
            }
          }
        } 
        formData.append('newQuantity',newQuantity);
        for(var i=0;i<5;++i){
          var docexist = "doc_MultiUpload"+i;
          var file = document.getElementById(docexist).files.length;
          var docPathid = "#"+docexist;
          if(file == 1){
            formData.append('newDocPath',$(docPathid)[0].files[0]);
          } 
        } 
        $.ajax({
					type : 'post',
					url : "/rest/announce/update",
					processData: false,
					contentType: false,
					enctype: 'multipart/form-data',
					data : formData,
					success : function(data) {
						confirm("訊息",JSON.parse(data).entity);
						$("#controllModal").modal('hide');
						getRecords();
					},
					error : function() {
						confirm("訊息","修改失敗");
					}
				});
      }

      function del(){
        $("#delBtn").attr('disabled', true);
        $("#editBtn").attr('disabled', true);
        timeoutCheck();
        var id = $("#key").val();
        $.confirm({
          title: '刪除',
          content: '確定刪除公告?',
            buttons: {
                "刪除":{btnClass: 'btn-red',
                  action: function () {
                  $.ajax({
                type:'post',
                url:"/rest/announce/delete",
                dataType: "text",
                data:{
                  id : id
                },
                success:function(msg){
                  confirm("訊息","刪除成功");
                  $("#delBtn").removeAttr('disabled');
                  $("#editBtn").removeAttr('disabled');
                  $("#controllModal").modal('hide');
                  getRecords();
                },
                error:function(){ 
                  confirm("訊息","刪除失敗");
                  $("#delBtn").removeAttr('disabled');
                          $("#editBtn").removeAttr('disabled');
                }
              });
                  }
                },
                  "取消": {
                    action: function () {
                      $("#delBtn").removeAttr('disabled');
                      $("#editBtn").removeAttr('disabled');
                      return;
                    }
                }
            }
        });
      }
      function changeFiles(place){
        var fileBtline = 'file'+place;
        var delfile = "doc_MultiUpload"+place;
        var a = document.createElement('a');
				a.innerHTML =  document.getElementById(delfile).files[0].name;
				document.getElementById(fileBtline).append(a);
        const span = document.createElement('span');
        span.innerHTML ='<button type="button" class="signbutton ml-2" onclick="delfile('+place+')">移除</button>';
        document.getElementById(fileBtline).append(span);
				document.getElementById(delfile).style.display="none";
				for(var i = 0;i < 5;i++){
          var checkFile = '#file'+i;
          var hasChildren = $(checkFile).children('a').length;
          if(hasChildren==0){
            var showInputFile = "#doc_MultiUpload"+i;
              $(showInputFile).show();
              return;
          }
        }
      }

      function delfile(place){
        var delfile = "#doc_MultiUpload"+place;
        var fileBtline = 'file'+place;
        for(var i = 0;i < 5;i++){
          var displayFile = "#doc_MultiUpload"+i;
            $(displayFile).hide();
        }
        $(delfile).val('');
        document.getElementById(fileBtline).innerHTML="";
				for(var i = 0;i < 5;i++){
          var checkFile = '#file'+i;
          var hasChildren = $(checkFile).children('a').length;
          if(hasChildren==0){
            var showInputFile = "#doc_MultiUpload"+i;
              $(showInputFile).show();
              return;
          }
        }
      }

      function recordFile(place,docId){
        var delDoc = $("#delDoc").val();
        delDoc += ","+docId;
        $("#delDoc").val(delDoc);
        var delfile = "#doc_MultiUpload"+place;
        var fileBtline = 'file'+place;
        for(var i = 0;i < 5;i++){
          var displayFile = "#doc_MultiUpload"+i;
            $(displayFile).hide();
        }
        $(delfile).val('');
        document.getElementById(fileBtline).innerHTML="";
        for(var i = 0;i < 5;i++){
          var checkFile = '#file'+i;
          var hasChildren = $(checkFile).children('a').length;
          if(hasChildren==0){
            var showInputFile = "#doc_MultiUpload"+i;
              $(showInputFile).show();
              return;
          }
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
					<div class="card-header">公告區</div>
					<div class="card-body">
						<div class="table-responsive" style="overflow-x: hidden;">
              
              <div class="row justify-content-end align-items-end flex-row-reverse px-3 pt-3" style="margin: -1px;">
                <div class="col text-right">
                  <button class="userbutton" id="announceBtn" style="margin-left:1rem;margin-bottom:1rem;"onclick="addShow()">新增</button>
                </div>
                <ul id="announceTabList" class="nav flex-nowrap">
                    <li data-type="" id="all" class="announceTab position-relative px-4 py-2 active">
                        全部
                    </li>
                    <li data-type="1" class="announceTab position-relative px-4 py-2">
                        管理辦法
                    </li>
                    <li data-type="2" class="announceTab position-relative px-4 py-2">
                        活動公告
                    </li>
                    <li data-type="0" class="announceTab position-relative px-4 py-2">
                        其他
                    </li>
                </ul>
              </div>

							<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
								<thead>
									<tr style="background: #E6E6E6;">
										<th id="companyTh">公司名稱</th>
										<th>公告主旨</th>
										<th style="width:15%">公告時間</th>
										<th id="editTh" style="width:10%;text-align:center;">動作</th>
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
  <div class="modal fade" id="contentModal" tabindex="-1" role="dialog"
		aria-labelledby="contentLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="contentTitle"></h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body" id="contentText">
					
				</div>
				<div class="modal-body" id="showFile">
					
				</div>
				<div class="modal-footer">
					<button class="btn btn-secondary" type="button"
							data-dismiss="modal" style="margin-right: 5px;">關閉</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="controllModal" tabindex="-1" role="dialog"
		aria-labelledby="editLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="editLabel">公告管理</h5>
					<button class="close" id="close" type="button" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">×</span>
					</button>
				</div>
				<div class="modal-body">
          <form id="announceForm">
            <input id="key" type="text" disabled="disabled"
              style="display: none">
            <input id="updatedAt" type="text" disabled="disabled"
              style="display: none">
            <p style="padding-top: 1%;">
              <span class="short_label"
                style="text-align: right; width: 20%; height: 2rem; display: block; float: left;">公司名稱
                : </span> <select id='announceCompany' style="margin-left: 3%; margin-right: 1.5%; width: 70%">
              </select>
            </p>
            <p style="padding-top: 1%;">
              <span class="short_label"
                style="text-align: right; width: 20%; display: block; float: left;">標題
                : </span> <input id="announceSubject" type="text" size="15" autocomplete="off" placeholder="請填寫標題"
                style="margin-left: 3%; margin-right: 1.5%; width: 70%">
            </p>
            <p style="padding-top: 1%;">
              <span class="short_label"
                style="text-align: right; width: 20%; display: block; float: left;">類別
                : </span> <select id='announceType' style="margin-left: 3%; margin-right: 1.5%; width: 70%">
                  <option value="1">管理辦法</option>
                  <option value="2">活動公告</option>
                  <option value="0">其他</option>
                </select>
            </p>
            <p style="padding-top: 1%;">
              <span class="short_label"
                style="text-align: right; width: 20%; display: block; float: left;">內文
                : </span> <span class="short_text"><textarea rows="6" id="announceContent"  placeholder="請填寫內文"
                  style="margin-left: 3%; resize: none; width: 70%;">
                </textarea></span>
            </p>
              <input type="text" name="delDoc"  id="delDoc" style="display:none;"/>
              <input type="text" name="recordQuentity"  id="recordQuentity" style="display:none;"/>
            <p style="padding-top: 1%;">
            <span class="short_label"
                style="text-align: right; width: 20%; display: block; float: left;">上傳資料
                : </span>
              <ul style="margin-left: 23%; margin-right: 1.5%; width: 70%" class="fileList">
                <li id="file0"></li>
                <li id="file1"></li>
                <li id="file2"></li>
                <li id="file3"></li>
                <li id="file4"></li>
              </ul>
              <input style="margin-left: 23%; margin-right: 1.5%; width: 70%;" type="file" name="doc_MultiUpload0"  id="doc_MultiUpload0"  onchange="changeFiles(0);"/>
              <input style="margin-left: 23%; margin-right: 1.5%; width: 70%; display:none;" type="file" name="doc_MultiUpload1"  id="doc_MultiUpload1" onchange="changeFiles(1);"/>
              <input style="margin-left: 23%; margin-right: 1.5%; width: 70%; display:none;" type="file" name="doc_MultiUpload2"  id="doc_MultiUpload2" onchange="changeFiles(2);"/>
              <input style="margin-left: 23%; margin-right: 1.5%; width: 70%; display:none;" type="file" name="doc_MultiUpload3"  id="doc_MultiUpload3" onchange="changeFiles(3);"/>
              <input style="margin-left: 23%; margin-right: 1.5%; width: 70%; display:none;" type="file" name="doc_MultiUpload4"  id="doc_MultiUpload4" onchange="changeFiles(4);"/>
            </p>
          </div>
        </form>
				<div class="modal-footer">
					<button class="delbutton" id="delBtn" type="button" onclick="del()" style="margin-right: 5px;">刪除</button>
					<button class="btn btn-primary" id="editBtn" type="button" onclick="edit()" style="margin-right: 5px;">修改</button>
					<button class="btn btn-primary" id="addBtn" type="button" onclick="add()">新增</button>
				</div>
			</div>
		</div>
	</div>
    <jsp:include page="JSfooter.jsp" />
</body>

</html>