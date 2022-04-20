<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<!DOCTYPE html>
	<html lang="en">
		<head>
			<jsp:include page="header.jsp" />
			<style>
				/* 臨時引入 switch css */
				.disable {
					pointer-events: none;
					opacity: .5;
				}

				.switch {
					position: relative;
					display: block;
					margin: 0;
					width: 80px;
					height: 26px;
				}

				.slider {
					position: absolute;
					top: 0;
					left: 0;
					right: 0;
					bottom: 0;
					cursor: pointer;
					background: #888;
					border-radius: 2px;
					transition: .3s;
				}

				.slider::before {
					content: '已停用';
					position: absolute;
					padding-top: 2px;
					top: 50%;
					left: 2px;
					width: 58px;
					height: 22px;
					font-size: 12px;
					font-weight: bold;
					text-align: center;
					border-radius: 2px;
					background: #faa;
					transform: translate(0, -50%);
					transition: .3s;
				}

				input:checked + .slider::before {
					content: '啟用中';
					background: #bbf;
					transform: translate(18px, -50%);
				}
			</style>
			<!-- <script src="../../Style/js/switch-ui.js"></script> -->
    		<script src="../../Style/js/accounting.js?ver=20220420"></script>
			<script type="text/javascript">
				var isFinishLoad = false;
				var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
				let dataTable;
				//新註解：檢查使用者登入狀態 -> 控制loading
				$(document).ready(function () {
					//前人code start
					$("#loading").modal("show");
					getRecordsTable();
					//loading 用 bootstrap 的 modal 來完成
					$("#loading").on("shown.bs.modal", function () {
						isFinishLoad = true;
					});
					closeLoading();
					if (authorise != "1") {
						$("#signBtn").hide();
					}
					if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
						window.location.href = "timeout.do";
					}
					if (authorise == 1 || authorise == 2 || authorise == 3) {
						appendUnsign();
						getUnsign();
					}
					$("body").show();
					const tableInfos = {
						filter: {
							c_id: "",
							i_id: "",
							i_name: "",
						},
						tableRows: [],
					};
					//init
					(function init(){
						renderLoop();
						dropLoop();
					})()

					//解決原first render不明原因報錯的問題 -> 目前沒事，先擺著
					// loop到成功
					async function renderLoop() {
						renderTable(true)
							.then((res) => {
								if (res.status != 200) {
									console.log('+1');
									renderLoop();
								}
							})
							.catch((res) => {
								if (res.status != 200) {
									console.log('+1');
									renderLoop();
								}
							});
					}
					async function dropLoop() {
						//true代表為首次render
						updateAccountingDrop(true)
							.then((res) => {
								if (res.status != 200) {
									console.log('+1');
									dropLoop();
								}
							})
							.catch((res) => {
								if (res.status != 200) {
									console.log('+1');
									dropLoop();
								}
							});
					}

					function render(arr) {
						if (!Array.isArray(arr)) return
						const arrLen = arr.length;
						//清除table
						dataTable.clear().draw();
						arr.forEach((item) => {
							dataTable.row.add([
								item.c_name,
								item.i_id,
								item.i_name,
								'<img data-i-id="' +
									item.i_id +
									'" data-type="update" class="table-icon " src="../../Style/images/edit.png" alt="">',
								`
								<div class="\${status === 'L' ? 'disable' : ''}">
									<label class="switch" for="\${item.i_id}">
										<input 
											type="checkbox" 
											class="d-none" 
											data-type="delete"
											data-i-id="\${item.i_id}"
											data-i-name="\${item.i_name}"
											id="\${item.i_id}"
											\${item.enable === "Y" ? "checked" : ""}
										/>
										<span class="slider"></span>
									</label>
								</div>
								`
							]);
						});
						dataTable.draw();
					}
					
					function getRecordsTable() {
						timeoutCheck();
						// datatable初始化
						dataTable = $("#dataTable").DataTable({
							bLengthChange: false,
							pageLength: 10,
							oLanguage: {
								sProcessing: "讀取中...",
								sLengthMenu: "Show _MENU_ entries",
								sInfo: "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
								sInfoEmpty: "顯示 0 到 0 共 0  筆資料",
								sInfoFiltered: "(從 _MAX_ 過濾結果)",
								sInfoPostFix: "",
								sUrl: "",
								oPaginate: {
									sFirst: "第一頁",
									sPrevious: "上一頁",
									sNext: "下一頁",
									sLast: "最末頁",
								},
							},
							bAutoWidth: false,
							"bFilter": true,
							bPaginate: true,
							retrieve: true,
							bInfo: true,
							bSort: true,
							dom: "ltipr",
							searching: true,
							order: [[0, "desc"]],
							columnDefs: [
								{ orderable: false, targets: 3 },
								{ orderable: false, targets: 4 },
							],
						});
					}
					//firstRender 控制錯誤不要跳出 confirm
					function renderTable(firstRender = false) {
						timeoutCheck()
						const filter = tableInfos.filter;
						const keys = Object.keys(filter);
						const q = keys.reduce((q, item, index) => {
							if (q && filter[item]) q+=`&\${item}=\${filter[item]}`;
							else if(filter[item] !== '') q+=`\${item}=\${filter[item]}`;
							return q
						}, "");
						const url = q
									? "/rest/accounting/manager/?" + q
									: "/rest/accounting/manager";
						return $.ajax({
							type: "GET",
							contentType: "application/json;charset=UTF-8",
							dataType: 'json',
							url: url,
							success: function (res) {
								const data = res;
								if (data.status == 200) {
									//清空 tableInfos.
									console.log('api data', data);
									tableInfos.tableRows.length = 0;
									data.data.forEach((row) => {
										const c_name = row.cName;
										row.itemList.forEach((item) => {
											tableInfos.tableRows.push({
												c_name,
												i_id: item.iId,
												i_name: item.iName,
												directions: item.directions,
												enable: item.enable
											});
										});
									});
									render(tableInfos.tableRows);
								} else if (data.status == 700 && !firstRender) {
									confirm("訊息", data.message);
								} else if (!firstRender) {
									confirm("訊息", "取得客戶資料失敗");
									confirm("訊息", res.message)
								}
							},
							error: function (data) {
								return false;
							},
						});
					}
					
					// modal 關閉事件 - 清除欄位
					// 1. 新增類別關閉
					$("#addAccounting").on("hide.bs.modal", function () {
						$("#addAccounting form")[0].reset();
						//改為預設借方
						$("#accounting_c_type").val("D");
					});
					// 2. 新增項目關閉
					$("#addItem").on("hide.bs.modal", function () {
						$("#addItem form")[0].reset();
					});

					// modal
					//新增類別
					//1. 確定送出
					$('#addAccounting input[type="submit"]').on("click", (e) => {
						const data = {
							c_id: $("#accounting_c_id").val().trim(),
							c_name: $("#accounting_c_name").val().trim(),
							c_type: $("#accounting_c_type").val(),
							directions: $("#accounting_c_direction").val(),
						};

						const errArr = [];
						if (data.c_id === "" || data.c_id.length !== 2) errArr.push("科目編號須為兩碼數字")
						if (data.c_name === "") errArr.push("科目類別未填寫")
						if (data.c_type === "")errArr.push("科目型別未填寫")
						if (errArr.length > 0) {
							const errMsg = errArr.join(",");
							confirm("訊息", errMsg);
							return;
						}

						$.ajax({
							url: "/rest/accounting/c/manager",
							method: "POST",
							dataType: "json",
							headers: { "Content-Type": "application/json" },
							data: JSON.stringify(data),
							success: function (res) {
								console.log("success");
								console.log(res);
								if(res.status == 200){
									//1. 關閉modal、顯示訊息、更新類別下拉選單
									$("#addAccounting").modal("hide");
									confirm("訊息", "科目類別新增成功");
									updateAccountingDrop();
								}else{
									confirm("訊息", res.message);
								}
							},
							error: function (err) {
								if(err.status != 200) confirm("訊息", err.message);
							},
						});
					});
					//新增項目
					//1. 新增項目 - 確定送出
					$('#addItem input[type="submit"]').on("click", (e) => {

						const c_id = $("#item_c_name").val().trim();
						const i_id = $("#item_i_id").val().trim();
						const i_id_pre = $("#item_i_id_pre").val().trim();
						const i_id_full = i_id_pre + i_id;
						const i_name = $("#item_i_name").val();
						const directions = $("#item_i_direction").val().trim();
						const init_amount = $('#item_init_amount').val().replaceAll(',', '').trim();

						const errArr = [];			
						// 檢查c_id、i_id、i_name
						if (c_id === "" || isNaN(c_id) || c_id.length !== 2) 	errArr.push("科目類別格式錯誤")
						if (i_id === "" || i_id_full.length !== 4) 	errArr.push("項目編號總計為4碼數字")
						if (i_name === "" || i_name.length > 20) errArr.push("項目名稱為空或過長(字數應小於20字)")
						if (init_amount === "") errArr.push("初始金額未填寫")
						
						if (errArr.length > 0) {
							const errMsg = errArr.join(",");
							return confirm("訊息", errMsg);
						}
						const data = {
							c_id,
							i_name,
							i_id: i_id_full,
							amount: init_amount,
							directions,
						};
						console.log(data);
						$.ajax({
							url: "/rest/accounting/manager",
							method: "POST",
							dataType: "application/json;charset=UTF-8",
							headers: { "Content-Type": "application/json" },
							data: JSON.stringify(data),
							success: function (res) {
								console.log('res', res);
								if (res.status == 200) {
									console.log("從success出成功");
									console.log('success message', res);
									renderTable();
									$("#addItem").modal("hide");
									confirm("訊息", "項目新增成功");
									console.log(res.message);
								}else{
									confirm("訊息", res.message);
								}
							},
							error: function (err) {
								err = JSON.parse(err.responseText)
								console.log('err', err);
								if (err.status == 200) {
									console.log('從error出成功');
									renderTable();
									$("#addItem").modal("hide");
									confirm("訊息", "項目新增成功");
								} else {
									console.log('從error出失敗');
									console.log('error message', err);
									confirm("新增失敗", err.message);
								}
							},
						});
					});
					//event 每次點擊「新增項目」modal都要更新
					$('#add_btns [data-btn_type="addItem"]').on("click", updateAccountingDrop);
					//3. 自動加上 c_id 到 i_id 前兩碼
					$("#item_c_name").on("change", (e) => {
						blurNumberCheck(e)
						$("#item_i_id_pre").val(e.target.value);
					});
					// table row 1.編輯 2.送出編輯 3.刪除
					$("table tbody").on("click", editRow);
					$('#table_edit input[type="submit"]').on("click", editRowSubmit);
					$("table tbody").on("click", deleteRow);
					function editRow(e) {
						if (e.target.dataset.type !== "update") return;
						const iId = e.target.dataset.iId;
						const targetRows = tableInfos.tableRows.filter((item) => item.i_id == iId);
						if (targetRows.length === 1) {
							$("#edit_i_id").val(targetRows[0].i_id);
							$("#edit_i_name").val(targetRows[0].i_name);
							$("#edit_c_name").val(targetRows[0].c_name);
							$("#edit_directions").val(targetRows[0].directions);
						}
						$("#table_edit").modal("show");
					}
					function editRowSubmit(e) {
						const data = {
							i_id: $("#edit_i_id").val(),
							i_name: $("#edit_i_name").val(),
							directions: $("#edit_directions").val()
						};
						if (data.i_name === "") return confirm("訊息", "項目名稱不可為空")
			
						$.ajax({
							method: "PUT",
							url: "/rest/accounting/manager",
							dataType: "json",
							contentType: "application/json; charset=utf-8",
							data: JSON.stringify(data),
							success: (res) => {
								console.log('res', res);
								if (res.status === "200") {
									renderTable();
									confirm("訊息", "編輯成功");
								} else {
									confirm("訊息", res.message);
								}
								$("#table_edit").modal("hide");
							},
							error: (err) => {
								if(err.status != 200) confirm("訊息", ers.message);
							},
						});
					}
					function deleteRow(e) {
						if (e.target.dataset.type !== "delete") return;
						const i_id = e.target.dataset.iId;
						const iName = e.target.dataset.iName;
						const enable = e.target.checked ? 'Y' : 'N';
						const msg = e.target.checked ? '啟用' : '停用';
						const data = {
							i_id,
							enable
						};
						console.log("delete data", data);
						$.confirm({
							title: "訊息",
							content: `確定「\${msg}」\${iName}?`,
							buttons: {
								確認: function () {
									$.ajax({
										type: "DELETE",
										url: "/rest/accounting/manager",
										data: JSON.stringify(data),
										contentType: "application/json;charset=UTF-8",
										dataType: "json",
										success: function (res) {
											const status = res.status;
											if (status == 200) {
												confirm("訊息", `\${msg}成功`);
												renderTable();
											} else {
												confirm("訊息", res.message);
												e.target.checked = !e.target.checked;
											}
										},
										error: function (msg) {
											const data = JSON.parse(msg);
											const status = data.status;
											if (status == 200) {
												confirm("訊息", `\${msg}成功`);
												renderTable();
											} else {
												confirm("訊息", msg.message);
												e.target.checked = !e.target.checked;
											}
										},
									});
								},
								取消: () => {
									e.target.checked = !e.target.checked
								},
							},
						});
					}
					//限制input 數字
					function inputNumberOnly(e){
						const limitLen = +e.target.getAttribute('numberonly');
						if(limitLen) e.target.value = e.target.value.slice(0, limitLen)
						e.target.value = e.target.value.replace(/[^\-?\d]/g, "");
					}
					$('[numberOnly]').each((index, item)=>{
						item.addEventListener('input', inputNumberOnly)
					})
					// 查詢功能
					$("#searchManager").on("click", (e) => {
						tableInfos.filter.c_id = $("#c_nameSearch").val();
						tableInfos.filter.i_id = $("#i_idSearch").val();
						tableInfos.filter.i_name = $("#i_nameSearch").val();
						renderTable();
					});
					function blurNumberCheck(e){
						e.target.value = e.target.value.slice(0,2)
						e.target.value = e.target.value.replace(/[^\-?\d]/g, "");
					}
					//api 渲染下拉選
					//1. 更新科目下拉選單(search & add item)	
					function updateAccountingDrop(firstRender = false) {
						timeoutCheck()
						return $.ajax({
							method: "GET",
							url: "/rest/accounting/classDrop",
							dataType: "json",
							contentType: "application/json;charset=UTF-8",
							success: (res) => {
								const arr = res.data;
								if (res.status == 200) {
									let node = '';
									arr.forEach((item, index) => {
										node += `<option value="\${item.cId}" >\${item.cName}</option>`
									});
									$("#item_c_name_datalist").html(node);
									$("#c_nameSearch_datalist").html(node);
								} else if (!firstRender) {
									confirm("訊息", "科目類別下拉選單取得失敗");
									confirm("訊息", res.message)
								}
							},
							error: (res) => {
								if (res.status !== '200' && !firstRender) {
									confirm("訊息", "科目類別下拉選單取得失敗");
									confirm("訊息", res.message);
								}
							},
						});
					}
					function apiRenderItemDrop(){
						timeoutCheck();
						$.ajax({
							method: "GET",
							url: "/rest/accounting/manager",
							contentType: "json",
							dataType: "json",
							success: (res)=>{
								console.log('success out');
								console.log(res);
								if(res.status == 200){
									const html = res.data.reduce((nodes, row)=>{
										let node = row.itemList.reduce((html, node)=>{
											if(node.enable === "Y") html += `<option value="\${node.iId}">\${node.iName}</option>`
											return html
										},"")
										return nodes += node;
									},"")
									$('#i_idSearch_datalist').html(html)
								}else{
									confirm('訊息','項目清單渲染失敗')
									confirm('訊息', res.message)
								}
							},
							error: (res)=>{
								console.log('error out');
								if(res.status != 200){
									confirm('訊息','項目清單渲染失敗')
									confirm('訊息', res.message)
								} 
							}
						})
					}
					// 金額檢查 & 金額顯示					
					function checkAccount(e) {
						let { value } = e.target;
						value = value.replace(/[^0-9]/g,'');
						if (value.length > 1 && value[0] === '0') {
							value = value.substring(1)
						}
						if (value.length > 11) {
							value = value.slice(0, -1)
						}
						e.target.value = accountFormat(value);
					}

					$('#item_init_amount').on('keyup', checkAccount)
					apiRenderItemDrop()
				});
			</script>
			<link rel="stylesheet" href="../../Style/css/custom_bootstrap.css">
			<style>
				body {
					display: none;
					/*全部先隱藏*/
				}
				h1{
					font-size: 20px;
					font-weight: normal;
					margin: 0;
				}
				.search{
					display: flex;
					padding-bottom: 16px;
				}
				.search .search-item{
					width: 25%;
					display: flex;
				}
				.search label{
					flex-shrink: 0;
					margin-right: 16px;
					margin-top: auto;
					margin-bottom: auto;
				}
				.search select,
				.search input{
					width: 60%;
				}

				/* table */
				table th,
				table td{
					text-align: center;
				}
				table td:nth-child(4),
				table td:nth-child(5){
					width: 6%;
				}
				.table-icon{
					display: block;
					width: 26px;
					height: 26px;
					cursor: pointer;
					margin: auto;
				}
				/* modal */
				/* modal form */
				.modal .form-group{
					display: flex;
					align-items: center;
					padding-right: 32px;
				}
				.modal .form-group textarea,
				.modal .form-group select,
				.modal .form-group input{
					margin-left: auto;
					width: 75%;
				}
				.modal .required::after{
					content: '*';
					color: #f44336;
					margin-left: 3px;
				}
				/* 新增項目 項目編號特殊處理 */
				.modal #item_i_id_pre{
					width: 15%;
					margin-left: auto;
				}
				.modal #item_i_id_pre + span{
					display: block;
					width: 5%;
					text-align: center;
				}
				.modal #item_i_id{
					width: 55%;
					margin-left: unset;
				}
				
				/* modal cursor */
				.modal button,
				.modal input[type="submit"],
				.modal select{
					cursor: pointer;
				}
				/* 為了與底下動態插入table的 container-fluid對齊 */
				.card-body .btns,.card-body .search{
					padding-left: 15px;
				}
				.modal-footer .btns .btn + .btn {
					margin-left: 16px;
				}

			</style>
		</head>

		<body class="fixed-nav sticky-footer" id="page-top">
			<!-- Navigation-->
			<jsp:include page="navbar.jsp" />
			<div class="content-wrapper">
				<div class="container-fluid">
					<div class="card mb-3">
						<div class="card-header">
							<h1>項目管理</h1>
						</div>
						<div class="card-body">
							<div id="add_btns" class="btns d-flex py-3">
									<button class="userbutton"  data-toggle="modal"data-target="#addAccounting">
										新增類別
									</button>
									<button data-btn_type="addItem" class="userbutton ml-4" data-toggle="modal"data-target="#addItem">
										新增項目
									</button>
									<button class="userbutton ml-4" id="searchManager" data-toggle="modal">
										查詢
									</button>
							</div>
							<form class="search">
								<div class="search-item">
									<label for="c_nameSearch">科目類別 : </label>
									<input list="c_nameSearch_datalist" id="c_nameSearch" class="form-control" />
									<datalist id="c_nameSearch_datalist"></datalist>
								</div>
								<div class="search-item">
									<!-- <label>項目編號 :</label>
									<input numberOnly="4" id="i_idSearch" class="search_bar form-control" placeholder="請輸入四碼以內數字"></input> -->
									
									<label for="i_idSearch">項目編號 : </label>
									<input list="i_idSearch_datalist" name="i_idSearch" id="i_idSearch" class="form-control search_bar"/>
									<datalist id="i_idSearch_datalist"></datalist>
								</div>
								<div class="search-item">
									<label>項目名稱 : </label>
									<input id="i_nameSearch" class="search_bar form-control" placeholder="請輸入項目名稱"></input>
								</div>		
							</form>
							<table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
									<thead>
										<tr bgcolor="#E6E6E6">
											<th>科目類別</th>
											<th>項目編號</th>
											<th>項目名稱</th>
											<th>編輯</th>
											<th>停/啟用</th>
										</tr>
									</thead>
									<tbody id="attendanceInfo"></tbody>
							</table>
						</div>
					</div>
					<jsp:include page="footer.jsp" />
				</div>
			</div>

			<!-- Modal -->
			<!-- 1. 新增會計類別 addAccounting -->
			<div
				class="modal fade"
				id="addAccounting"
				tabindex="-1"
				role="dialog"
				aria-labelledby="exampleModalLabel"
				aria-hidden="true"
			>
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title" id="exampleModalLabel">新增類別</h5>
							<button
								type="button"
								class="close"
								data-dismiss="modal"
								aria-label="Close"
							>
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body p-4">
							<form action="javascript:;">
								<div class="form-group">
									<label for="accounting_c_id" class="required">科目編號:</label>
									<input numberOnly="2" id="accounting_c_id" type="text" class="form-control" aria-describedby="emailHelp" placeholder="請填寫2碼科目編號">
								</div>
								<div class="form-group">
									<label for="accounting_c_name" class="required">科目類別:</label>
									<input id="accounting_c_name" type="text" class="form-control" placeholder="請填寫科目類別">
								</div>
								<div class="form-group">
									<label for="accounting_c_type" class="required">型別:</label>
									<select id="accounting_c_type" class="form-control" id="exampleFormControlSelect1">
										<option value="D" selected>借方</option>
										<option value="C">貸方</option>
									</select>
								</div>
								<div class="form-group">
									<!-- <label for="exampleFormControlTextarea1">說明:</label> -->
									<textarea class="form-control" id="accounting_c_direction" rows="3" placeholder="說明"></textarea>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<div class="btns">
								<!-- submit 確認功能鍵 -->
								<input type="submit" class="btn btn-primary" value="確定"></input>
								<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
							</div>
						</div>
					</div>
			</div>
			</div>
			<!-- 2. 新增項目 -->
			<div
				class="modal fade"
				id="addItem"
				tabindex="-1"
				role="dialog"
				aria-labelledby="exampleModalLabel"
				aria-hidden="true"
			>
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title" id="exampleModalLabel">新增項目</h5>
							<button
								type="button"
								class="close"
								data-dismiss="modal"
								aria-label="Close"
							>
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body p-4">
							<form action="javascript:;">
								<div class="form-group">
									<label for="item_c_name" class="required">科目類別:</label>
									<input list="item_c_name_datalist" id="item_c_name" class="form-control" name="item_c_name" />
									<datalist id="item_c_name_datalist"></datalist>
								</div>
								<div class="form-group">
									<label for="item_i_id" class="required" >項目編號:</label>
									<input id="item_i_id_pre" type="text" class="form-control" disabled>
									<span>-</span>
									<input numberOnly="2" id="item_i_id" type="text" class="form-control" aria-describedby="emailHelp" placeholder="請輸入2碼數字" autocomplete="off">
								</div>
								<div class="form-group">
									<label class="required" for="item_init_amount">初始金額</label>
									<input type="text" id="item_init_amount" class="form-control" placeholder="請填寫初始金額">
								</div>
								<div class="form-group">
									<label for="item_i_name" class="required">項目名稱:</label>
									<input id="item_i_name" type="text" class="form-control" placeholder="請填寫項目名稱">
								</div>
								
								<div class="form-group">
									<!-- <label for="exampleFormControlTextarea1">說明:</label> -->
									<textarea id="item_i_direction" placeholder="說明" class="form-control" rows="3"></textarea>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<div class="btns">
								<input type="submit" class="btn btn-primary" value="確定"></input>
								<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
							</div>
						</div>
					</div>
			</div>
			</div>
			<!-- 3. loading -->
			<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
				data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
				<div class="modal-dialog">
					<img src="/Style/images/loading.gif" style="padding-top: 12rem;">
				</div>
			</div>
			<!-- 4. table edit modal -->
			<div
				class="modal fade"
				id="table_edit"
				tabindex="-1"
				role="dialog"
				aria-labelledby="exampleModalLabel"
				aria-hidden="true"
			>
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title" id="exampleModalLabel">編輯項目</h5>
							<button
								type="button"
								class="close"
								data-dismiss="modal"
								aria-label="Close"
							>
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body p-4">
							<form action="javascript:;">
								<div class="form-group">
									<label for="edit_i_id">項目編號:</label>
									<input numberOnly="2" disabled id="edit_i_id" type="text" class="form-control" aria-describedby="emailHelp">
								</div>
								<div class="form-group">
									<label for="edit_i_name" class="required">項目名稱:</label>
									<input id="edit_i_name" type="text" class="form-control">
								</div>
								<div class="form-group">
									<label for="edit_c_name">科目類別:</label>
									<input disabled id="edit_c_name" type="text" class="form-control">
								</div>
								<div class="form-group">
									<!-- <label for="exampleFormControlTextarea1">說明:</label> -->
									<textarea id="edit_directions" class="form-control" id="exampleFormControlTextarea1" rows="3"></textarea>
								</div>
							</form>
						</div>
						<div class="modal-footer">
							<div class="btns">
								<input type="submit" value="確定" class="btn btn-primary"></input>
								<button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
							</div>
						</div>
					</div>
			</div>
			</div>
			<jsp:include page="JSfooter.jsp" />
		</body>
	</html>