<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%-- 傳單明細.jsp 供查詢傳單共用 --%>
<head>
    <style>
        #detailTable_d td:nth-child(4) {
            text-align: right
        }
        #detailTable_d td:nth-child(8) {
            min-width: 150px;
            white-space: normal;
        }
        input::-webkit-calendar-picker-indicator {
            margin-bottom: 6px;
        }
    </style>

    <script type="text/javascript">
        const render = '<%=request.getParameter("render")%>'

        function initSearchDetail(all_datalist) {
            const columns = [
                { data: "單號" },
                { data: "專案" },
                { data: "項目" },
                { data: "金額" },
                { data: "對沖編號" },
                { data: "預期收付日" },
                { data: "入帳日" },
                { data: "說明" },
                { data: "單據狀態" }
            ]
            const dataTableOptions = {
                pageLength: 8,
                order: [[0, 'desc']],
                "dom": `<'row'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>
                        <'row'<'col-sm-12 table-x px-0 mx-3 mb-2'tr>>
                        <'row'<'col-sm-12 col-md-5 pb-2'i><'col-sm-12 col-md-7 pb-2'p>>`
            }

            const detailTable = cusDataTable('detailTable_d', columns, dataTableOptions)

            const form = document.forms['searchDetailForm']
            const pageBtns = document.getElementById('detailTable_d_paginate')
            pageBtns.addEventListener('click', (e) => {
                const { nodeName }  = e.target
                if (nodeName !== 'A') return
                if (detailTable.page() >= detailTable.page.info().pages - 2) {
                    searchDetail(all_datalist, false)
                }
            })
            const btnGroup = document.getElementById('btnGroup_s_d')
            btnGroup.addEventListener('click', (e) => {
                const { btn } = e.target.dataset
                if (!btn) return

                switch (btn) {
                    case 'search':
                        searchDetail(all_datalist)
                        break;
                    case 'leave':
                        form.reset()
                        detailTable.clear()
                        leave()
                        break;
                    default:
                }
            })
            function leave() {
                if (render === 'modal') {
                    $("#voucherDModal").modal('hide');
                } else {
                    window.history.go(-1)
                }
            }
        }
        
        function searchDetail({companyList, applicantList, projectNameList, itemList, customerList}, isFirst = true) {
            const params = {
                page_size: 40,
                page_no: 1
            }
            const detailTable = $('#detailTable_d').DataTable();
            if (isFirst) {
                detailTable.clear();
                detailTable.draw();
            } else {
                params['page_no'] = (detailTable.page.info().pages / 5) + 1  // 頁數
            }

            const keyArr = [
                'voucher_no',
                'company',
                'customer',
                'cus_tax_id',
                'applicant',
                'project_id',
                'status',
                'item'
            ]
            keyArr.forEach(key => {
                let value = $("#" + key + '_s_d').val().trim()
                switch (key) {
                    case 'applicant':
                        value = applicantList.getItem(value, 'chineseName', 'name').name
                        break;
                    case 'project_id':
                        value = value.split(' ')[0] !== '非專案'
                            ? projectNameList.getItem(value.split(' ')[0], 'id', 'name').id
                            : 'N'
                        break;
                    case 'item':
                        value = itemList.getItem(value.split(' ')[0], 'iId', 'iName').iId || null
                        break;
                    default:
                }
                if (value) { params[key] = value }
            })

            let paramsStr = ''
            for (const key in params) {
                paramsStr += (key + '=' + params[key] + '&')
            }
            paramsStr = paramsStr.slice(0, -1)


            $.ajax({
                type: 'GET',
                url: '/rest/voucher/api/d?' + paramsStr,
                contentType: 'application/json;charset=UTF-8',
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    if (record.status === '200') {
                        const detailList = record.data.detail
                        const detailJSON = []
                        Array.isArray(detailList) && detailList.forEach( detail => {
                            const {
                                voucherNo,
                                project,
                                item,
                                amount,
                                hedgeNo,
                                predictDate,
                                creditDate,
                                directions,
                                status
                            } = detail
                            const item_s_d = itemList.getItem(item, 'iId').iId + ' ' + itemList.getItem(item, 'iId').iName
                            detailJSON.push({
                                "單號": voucherNo,
                                "專案": project || '非專案',
                                "項目": item_s_d,
                                "金額": accountFormat(amount),
                                "對沖編號": hedgeNo,
                                "預期收付日": dateFormat(predictDate),
                                "入帳日": dateFormat(creditDate),
                                "說明": directions || '',
                                "單據狀態": statusTypeToggle(status).str
                            })
                        })
                        detailTable.rows.add(detailJSON).draw(isFirst).nodes().to$();
                        window.scrollTo({
                            top: document.body.scrollHeight - window.innerHeight - 32, 
                            behavior: 'smooth'
                        })
                    }
                },
                error: function () {
                    $("#loading").modal('hide');
                    confirm("訊息", "搜尋資料失敗");
                }
            })
        }
        
        function statusTypeToggle(statusType) {
            statusType = statusType.toString()
            switch (statusType) {
                case "0":
                case "暫存":
                    return {val: '0', str: '暫存'}
                case "1":
                case "簽核中":
                    return {val: '1', str: '簽核中'}
                case "2":
                case "簽核完成":
                    return {val: '2', str: '簽核完成'}
                case "3":
                case "刪除":
                    return {val: '3', str: '刪除'}
                case "4":
                case "駁回":
                    return {val: '4', str: '駁回'}
                default:
                    return {val: 'err', str: '狀態異常'}
            }
        }

    </script>
</head>
<form name="searchDetailForm">
    <div class="form-row justify-content-end align-items-center px-3 pt-3">
    
        <div class="col-4 col-md-3 mb-2">
            <div class="input-group align-items-center">
                <label for="company_s_d">公司別 : </label>
                <input 
                    type="text"
                    list="companyList"
                    id="company_s_d"
                    class="form-control form-control-sm"
                    placeholder="請輸入..."
                >
            </div>
        </div>
    
        <div class="col-4 col-md-5 mb-2">
            <div class="input-group align-items-center">
                <label for="project_id_s_d">專案名稱 : </label>
                <input 
                    type="text"
                    list="projectNameList"
                    id="project_id_s_d" 
                    name="project_id_s_d" 
                    class="form-control form-control-sm"
                    placeholder="請輸入..."
                    maxlength="20"
                />
            </div>
        </div>  
    
        <div class="col-4 mb-2">
            <div class="input-group align-items-center">
                <label for="voucher_no_s_d">傳票單號 : </label> 
                <input 
                    id="voucher_no_s_d" 
                    class="form-control form-control-sm" 
                    placeholder="請輸入"
                    maxlength="20"
                />
            </div>
        </div>
        
        <div class="col-4 col-md-3 mb-2">
            <div class="input-group align-items-center">
                <label for="applicant_s_d">申請人 : </label>
                <input
                    list="applicantList"
                    type="text" 
                    id="applicant_s_d"
                    class="form-control form-control-sm"
                    placeholder="請輸入..."
                >
            </div>
        </div>
    
        <div class="col-4 col-md-5 mb-2">
            <div class="input-group align-items-center">
                <label for="customer_s_d">客戶名稱 : </label>
                <input 
                    list="customerList"
                    id="customer_s_d"
                    name="customer_s_d"
                    class="form-control form-control-sm" 
                    placeholder="請輸入..." 
                    maxlength="20"
                />
            </div>
        </div>
        <div class="col-4 mb-2">
            <div class="input-group align-items-center">
                <label for="cus_tax_id_s_d">客戶統編 : </label> 
                <input 
                    type="text"
                    list="cus_tax_idList" 
                    class="form-control form-control-sm" 
                    id="cus_tax_id_s_d" 
                    name="cus_tax_id_s_d" 
                    maxlength="8"
                    placeholder="請輸入..." 
                />
            </div>
        </div>
        
        <div class="col-4 col-md-3 mb-2">
            <div class="input-group align-items-center">
                <label for="status_s_d">單據狀態 : </label>
                <select id="status_s_d" class="form-control form-control-sm">
                    <option value="">全部</option>
                    <option value="0">暫存</option>
                    <option value="1">簽核中</option>
                    <option value="2">簽核完成</option>
                    <option value="3">刪除</option>
                    <option value="4">駁回</option>
                </select>
            </div>
        </div>
    
        <div class="col-4 col-md-5 mb-2">
            <div class="input-group align-items-center">
                <label for="item_s_d">項目名稱 : </label>
                <input 
                    type="text" 
                    list="itemList"
                    id="item_s_d" 
                    name="item_s_d" 
                    class="form-control form-control-sm"
                    placeholder="請輸入..."
                    maxlength="10"
                />
            </div>
        </div>
    
        <div id="btnGroup_s_d" class="col-4 text-right mb-2">
            <button 
                type="button"
                class="btn btn-sm btn--primary ml-2" 
                id="searchDetailBtn" 
                data-btn="search"
            >
                查詢
            </button>
            <button 
                type="button"
                class="btn btn-sm btn--danger ml-2" 
                id="leaveBtn"
                data-btn="leave"
            >
                離開
            </button>
        </div>
    </div>
</form>

<!--id="dataTable"-->
<table class="table table-bordered" id="detailTable_d" cellspacing="0">
    <thead>
        <tr style="background: #E6E6E6;">
            <th title="單號" class="no-sort">單號</th>
            <th title="專案" class="no-sort">專案</th>
            <th title="項目" class="no-sort">項目</th>
            <th title="金額">金額</th>
            <th title="對沖編號">對沖編號</th>
            <th title="預期收付日">預期收付日</th>
            <th title="入帳日">入帳日</th>
            <th title="說明" class="no-sort">說明</th>
            <th title="單據狀態">單據狀態</th>
        </tr>
    </thead>
    <tbody id="detailList_d"></tbody>
</table>