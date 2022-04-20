<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="header.jsp" />
    <!-- Datetimepicker-->
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
    <style type="text/css">
        body {
            display: none;
        }

        th, td {
            text-align:center;
            white-space: nowrap;
        }

        .table-x {
            flex: 1 0 90%;
            overflow-x: auto;
            box-shadow: inset 0 0 24px #0002;
            -webkit-overflow-scrolling: touch;
            -ms-overflow-style: -ms-autohiding-scrollbar;
        }

        table.dataTable {
            margin-top: 0 !important;
        }

        #voucherTable td:nth-child(6) {
            text-align: right
        }

        #voucherTable th {
            background: #E6E6E6;
        }
        #voucherTable td:nth-child(11) {
            background: #FFF;
        }
        #voucherTable th:nth-child(11),
        #voucherTable td:nth-child(11) {
            position: sticky;
            margin-right: -1px;
            border-right: 1px solid #dee2e6;
            right: 0;
            box-shadow: 0 0 24px #0002;
        }

        .btn:disabled {
            pointer-events: none;
            cursor: none;
            opacity: .3;
        }
    </style>

    <script type="text/javascript">
            
        var isFinishLoad = false;
        var account = '<%out.print(session.getAttribute("Account"));%>';
        var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
        var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
        $(document).ready(function () {
            $("#loading").modal("show");
            $("#loading").on("shown.bs.modal", function () {
                isFinishLoad = true;
            });
            closeLoading();
            if (authorise == 4) {
                $("#userFilter").css("visibility", "hidden");
            }
            if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
                window.location.href = "timeout.do";
            }
            if (authorise == 1 || authorise == 2 || authorise == 3) {
                appendUnsign();
                getUnsign();
            }
            $("body").show();
            
            let indexPage = 0;
            const now = new Date()
            cusDatepicker('predict_date_f', -90)
            cusDatepicker('credit_date_f', -90)
            cusDatepicker('predict_date_t',new Date(now.getFullYear() + 3, now.getMonth(), now.getDate()))
            cusDatepicker('credit_date_t',new Date(now.getFullYear(), now.getMonth() + 1, 0))

            const columns = [
                { data: "公司" },
                { data: "單號" },
                { data: "帳目名稱" },
                { data: "申請人" },
                { data: "客戶" },
                { data: "總金額" },
                { data: "小計科目" },
                { data: "預期收付日" },
                { data: "入帳日" },
                { data: "單據狀態" },
                { data: "按鈕區" }
            ]
            const pageLength = Math.ceil(window.innerHeight / $(".no-sort")[0].offsetHeight) > 10 
                                ? (Math.ceil(window.innerHeight / $(".no-sort")[0].offsetHeight) - 5)
                                : 5
            const dataTableOptions = {
                pageLength,
                order: [[1, 'desc']],
                "dom": `<'row'<'col-sm-12 table-x px-0 mx-3 mb-2'tr>>
                        <'row'<'col-sm-12 col-md-5 pb-2'i><'col-sm-12 col-md-7 pb-2'p>>`
            }
            cusDataTable('voucherTable', columns, dataTableOptions)

            const {getVoucher, companyList, applicantList, projectNameList, customerList, itemList} = initVoucherPage(account)
            const all_datalist = {companyList, applicantList, projectNameList, customerList, itemList}
            initSearchDetail(all_datalist)

            const searchVoucherBtn = document.getElementById('searchVoucherBtn')
            searchVoucherBtn.addEventListener('click', searchVoucher)

            const voucherTable = document.getElementById('voucherTable')
            voucherTable.addEventListener('click', (e) => {
                const { btn, voucher, status } = e.target.dataset
                if (!btn) return
                const trDOM = $(`[data-voucher=\${voucher}]`)[0].parentNode.parentNode
                indexPage = $("#voucherTable").DataTable().page()
                switch (btn) {
                    case 'getVoucherDetail':
                        getVoucherDetail(trDOM, all_datalist)
                        $("#voucherDModal").modal('show');
                        break
                    case 'editVoucher':
                        if (status === '0') {
                            getVoucherToken(voucher, (record) => {
                                getVoucher(voucher, false, record.data) // 使用 accounting_editVoucher.jsp function
                                $("#editVoucherModal").modal('show')
                            })
                        } else if (status === '4') {
                            getVoucher(voucher) // 使用 accounting_editVoucher.jsp function
                            $("#editVoucherModal").modal('show')
                        } else {
                            confirm('訊息', '該傳票已無法編輯')
                        }
                        break
                    case 'deleteVoucher':
                        getVoucherToken(voucher, (record) => {
                            deleteVoucher(voucher, record.data, trDOM)
                        })
                        break
                    default:
                }
            })

            
            function searchVoucher() {
                const params = {}
                const dateIdArr = ['credit_date_f', 'credit_date_t', 'predict_date_f', 'predict_date_t']
                for (let i = 0; i < dateIdArr.length; i++) {
                    const value = $("#" + dateIdArr[i]).val().replaceAll('-', '').trim()
                    if (value) {
                        params[dateIdArr[i]] = value
                    } else {
                        return confirm('訊息', '日期必須輸入')
                    }
                }
                if (new Date($("#credit_date_f").val()) > new Date($("#credit_date_t").val())) {
                    return confirm('訊息', '注意入帳起訖日輸入相反！');
                } else if (getDateDiff($("#credit_date_f").val()) > 90) {
                    return confirm('訊息', '入帳日期無法查詢 90 天前');
                }
                if (new Date($("#predict_date_f").val()) > new Date($("#predict_date_t").val())) {
                    return confirm('訊息', '注意預期收/付起訖日輸入相反！');
                } else if (getDateDiff($("#predict_date_f").val()) > 90) {
                    return confirm('訊息', '預期收/付日期無法查詢 90 天前')
                }

                const keyArr = ['voucher_name', 'voucher_no', 'applicant', 'company', 'status']
                keyArr.forEach(key => {
                    const value = key !== 'applicant' 
                        ? $("#" + key + '_s').val().trim()
                        : applicantList.getItem($("#" + key + '_s').val().trim(), 'chineseName', 'name').name
                    if (value) { params[key] = value }
                })

                const voucherTable = $('#voucherTable').DataTable();
                voucherTable.clear();
                voucherTable.draw();

                let paramsStr = ''
                for (const key in params) {
                    paramsStr += (key + '=' + params[key] + '&')
                }
                paramsStr = paramsStr.slice(0, -1)

                $.ajax({
                    type: 'GET',
                    url: '/rest/voucher/api/h?' + paramsStr,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        if (record.status === '200') {
                            const voucherList = record.data.voucher
                            const voucherJSON = []
                            Array.isArray(voucherList) && voucherList.forEach( voucher => {
                                const {    
                                    company,
                                    voucherNo,
                                    voucherName,
                                    applicant,
                                    customer,
                                    amountTotal,
                                    headItem,
                                    creditDate,
                                    predictDate,
                                    status
                                } = voucher
                                
                                const btnsHTML = `
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn-transparent ml-1 p-0"
                                        data-btn="getVoucherDetail"
                                        data-voucher="\${voucherNo}"
                                    >
                                        <img src="/Style/images/book.svg" width="24px" alt="open detail">
                                    </button>
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn-transparent ml-1 p-0"
                                        data-btn="editVoucher"
                                        data-voucher="\${voucherNo}"
                                        data-status="\${status}"
                                        \${status == '0' || status == '4' ? "" : "disabled"}
                                    >
                                        <img src="/Style/images/edit.png" width="24px" alt="edit voucher">
                                    </button>
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn-transparent ml-1 p-0"
                                        data-btn="deleteVoucher"
                                        data-voucher="\${voucherNo}"
                                        data-status="\${status}"
                                        \${status == '0' ? "" : "disabled"}
                                    >
                                        <img src="/Style/images/trash-can.png" width="24px" alt="delete voucher">
                                    </button>
                                `
                                
                                voucherJSON.push({
                                    "公司": company,
                                    "單號": voucherNo,
                                    "帳目名稱": voucherName,
                                    "申請人": applicant,
                                    "客戶": customer || '',
                                    "總金額": accountFormat(amountTotal),
                                    "小計科目": itemList.getItem(headItem, 'iId').iId + ' ' + itemList.getItem(headItem, 'iId').iName,
                                    "預期收付日": dateFormat(predictDate),
                                    "入帳日": dateFormat(creditDate),
                                    "單據狀態": statusTypeToggle(status).str,
                                    "按鈕區": btnsHTML
                                })
                            })
                            voucherTable.rows.add(voucherJSON).draw().nodes().to$();
                            window.scrollTo({
                                top: document.body.scrollHeight - window.innerHeight - 32, 
                                behavior: 'smooth'
                            })
                        }
                    },
                    error: function () {
                        confirm("訊息", "搜尋資料失敗");
                    }
                }).then((res) => {
                    $("#voucherTable").DataTable().page(indexPage).draw(false);
                })
            }

            function getVoucherDetail(trDOM, all_datalist) {
                const data = $("#voucherTable").DataTable().row(trDOM).data()
                const {applicantList, customerList} = all_datalist

                $("#voucher_no_s_d").val(data["單號"]);
                // /* 使用 accounting_voucherD.jsp function */
                searchDetail(all_datalist)
                $("#company_s_d").val(data["公司"]);
                $("#applicant_s_d").val(applicantList.getItem(data["申請人"], 'chineseName', 'name').chineseName);
                $("#customer_s_d").val(data["客戶"]);
                $("#cus_tax_id_s_d").val(customerList.getItem(data["客戶"], 'name').ein || '');
                $("#status_s_d").val(statusTypeToggle(data["單據狀態"]).val).change();
                $("#voucherDModal").modal('show');
                const form = document.forms['searchDetailForm']
                const eles = form.elements
                for(var el in eles) {
                    if (eles[el].nodeName === 'INPUT' || eles[el].nodeName === 'SELECT') {
                        eles[el].disabled = true;
                        eles[el].removeAttribute('placeholder');
                    }
                }
                $("#searchDetailBtn").hide()
            }

            function getVoucherToken(voucher_no, successFunc) {
                return $.ajax({
                    type: 'GET',
                    url: '/rest/voucher/getVoucherMToken?voucher_no=' + voucher_no,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                }).then(
                    (res) => {
                        const record = JSON.parse(res)
                        if (record.status == '200') {
                            successFunc(record)
                        } else {
                            confirm('訊息', record.message)
                        }
                    },
                    (err) => {
                        confirm('訊息', '該傳票已無法變更')
                    }
                )
            }

            function deleteVoucher(voucher_no, m_token, trDOM) {
                $.confirm({
                    title: "訊息",
                    content: `確定要刪除編號 \${voucher_no} 的傳票嗎？`,
                    draggable: false,
                    buttons: {
                        '確認': function () {
                            const params = { voucher_no, m_token }
                            $.ajax({
                                method: 'DELETE',
                                url: '/rest/voucher/api',
                                data: JSON.stringify(params),
                                contentType: "application/json;charset=UTF-8",
                                dataType: "json",
                                success: function(data) {
                                    if (data.status === '200') {
                                        const dataTable = $("#voucherTable").DataTable()
                                        if ($("#status_s").val()) {
                                            dataTable.row(trDOM).remove().draw(false)
                                        } else {
                                            const index = $("#voucherTable").DataTable().row(trDOM).index()
                                            $(`[data-btn="editVoucher"][data-voucher="\${voucher_no}"]`)[0].disabled = true;
                                            $(`[data-btn="deleteVoucher"][data-voucher="\${voucher_no}"]`)[0].disabled = true;
                                            dataTable.cell(index, 9).data('刪除').draw(false)
                                        }
                                        confirm("訊息", "該單據目前已刪除成功");
                                    } else {
                                        confirm("訊息", "該單據刪除失敗");
                                    }
                                },
                                error: function () {
                                    confirm("訊息", "該單據目前已無法刪除");
                                }
                            })
                        },
                        '取消': function () {}
                    }
                })
            }
        
            $('#editVoucherModal').on('hidden.bs.modal', function () {
                searchVoucher()
            })
            $('#voucherDModal').on('hidden.bs.modal', function () {
                document.forms['searchDetailForm'].reset()
                $('#detailTable_d').DataTable().clear();
            })
        });
    </script>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
    <!-- Navigation-->
    <jsp:include page="navbar.jsp" />
    <div class="content-wrapper">
        <div class="container-fluid">
            <div class="card m-3 pb-2">
                <header class="card-header">傳票查詢</header>

                <div>
                    <div class="form-row justify-content-end align-items-center px-3 pt-3">

                        <div class="col-3 mb-2">
                            <div class="input-group align-items-center">
                                <label for="company_s">公司別 : </label>
                                <input 
                                    type="text"
                                    list="companyList"
                                    id="company_s"
                                    class="form-control form-control-sm"
                                    placeholder="請輸入..."
                                >
                            </div>
                        </div>

                        <div class="col-5 mb-2">
                            <div class="input-group align-items-center">
                                <label for="voucher_name_s">帳目名稱 : </label>
                                <input 
                                    type="text" 
                                    id="voucher_name_s" 
                                    name="voucher_name_s" 
                                    class="form-control form-control-sm"
                                    placeholder="請輸入..."
                                    maxlength="10"
                                />
                            </div>
                        </div>  
                        
                        <div class="col-4 mb-2">
                            <div class="input-group align-items-center">
                                <label for="voucher_no_s">傳票單號 : </label> 
                                <input 
                                    id="voucher_no_s" 
                                    class="form-control form-control-sm" 
                                    placeholder="請輸入"
                                    maxlength="20"
                                />
                            </div>
                        </div>

                        <div class="col-3 mb-2">
                            <div class="input-group align-items-center">
                                <label for="applicant_s">申請人 : </label>
                                <input
                                    list="applicantList"
                                    type="text" 
                                    id="applicant_s"
                                    class="form-control form-control-sm"
                                    placeholder="請輸入..."
                                >
                            </div>
                        </div>

                        <div class="col-5 mb-2">
                            <div class="input-group align-items-center">
                                <label for="customer_s">客戶名稱 : </label>
                                <input 
                                    list="customerList"
                                    id="customer_s"
                                    name="customer_s"
                                    class="form-control form-control-sm" 
                                    placeholder="請輸入..." 
                                    maxlength="20"
                                />
                            </div>
                        </div>
                        <div class="col-4 mb-2">
                            <div class="input-group align-items-center">
                                <label for="customerTaxId_s">客戶統編 : </label> 
                                <input 
                                    type="text"
                                    list="cus_tax_idList" 
                                    class="form-control form-control-sm" 
                                    id="customerTaxId_s" 
                                    name="customerTaxId_s" 
                                    maxlength="8"
                                    placeholder="請輸入..." 
                                />
                            </div>
                        </div>

                        <div class="col mb-2" style="flex: 1 0 340px;">
                            <div class="input-group align-items-center">
                                <label for="predict_date_f">
                                    預期收/付日期 :
                                </label>
                                <div class="form-control form-control-sm d-flex">
                                    <input 
                                        id="predict_date_f" 
                                        type="text" 
                                        class="date text-center w-50 border-0"
                                        readonly
                                    />
                                    <label for="predict_date_t" class="m-0 px-1">
                                        至
                                    </label>
                                    <input 
                                        id="predict_date_t" 
                                        type="text" 
                                        class="date text-center w-50 border-0"
                                        readonly
                                    />
                                </div>
                            </div>
                        </div>
                        <div class="col mb-2" style="flex: 1 0 320px;">
                            <div class="input-group align-items-center">
                                <label for="credit_date_f">
                                    入帳日期 :
                                </label>
                                <div class="form-control form-control-sm d-flex">
                                    <input 
                                        id="credit_date_f" 
                                        type="text" 
                                        class="date text-center w-50 border-0"
                                        readonly
                                    />
                                    <label for="credit_date_t" class="m-0 px-1">
                                        至
                                    </label>
                                    <input 
                                        id="credit_date_t" 
                                        type="text" 
                                        class="date text-center w-50 border-0"
                                        readonly
                                    />
                                </div>
                            </div>
                        </div>
                        
                        <div class="col mb-2" style="flex: 0 0 210px;">
                            <div class="input-group align-items-center">
                                <label for="status_s">單據狀態 : </label>
                                <select id="status_s" class="form-control form-control-sm">
                                    <option value="">全部</option>
                                    <option value="0">暫存</option>
                                    <option value="1">簽核中</option>
                                    <option value="2">簽核完成</option>
                                    <option value="3">刪除</option>
                                    <option value="4">駁回</option>
                                </select>
                            </div>
                        </div>

                        <div class="col-auto mb-2">
                            <button type="button" class="btn btn-sm btn--primary" id="searchVoucherBtn">
                                查詢
                            </button>
                        </div>
                    </div>

                    <!--id="dataTable"-->
                    <table class="table table-bordered" id="voucherTable" cellspacing="0">
                        <thead>
                            <tr>
                                <th title="公司" class="no-sort">公司</th>
                                <th title="單號">單號</th>
                                <th title="帳目名稱" class="no-sort">帳目名稱</th>
                                <th title="申請人" class="no-sort">申請人</th>
                                <th title="客戶" class="no-sort">客戶</th>
                                <th title="總金額">總金額</th>
                                <th title="小計科目">小計科目</th>
                                <th title="預期收付日">預期收/付日</th>
                                <th title="入帳日">入帳日</th>
                                <th title="單據狀態">單據狀態</th>
                                <th class="no-sort"></th>
                            </tr>
                        </thead>
                        <tbody id="voucherList"></tbody>
                    </table>
                </div>
            </div>
        </div>
        <jsp:include page="footer.jsp" />
    </div>
    
    <a class="scroll-to-top rounded" href="#page-top"> <i
        class="fa fa-angle-up"></i>
    </a>

    <!-- EditModal -->
    <div class="modal fade pl-4" id="editVoucherModal" data-backdrop="static" data-keyboard="false" tabindex="-1" 
        role="dialog" aria-labelledby="editVoucherModalLabel" aria-hidden="true">
        <div class="modal-dialog m-3" style="max-width: none; height: 94%;" role="document">
            <div class="modal-content" style="height: 100%;">
                <div class="modal-header py-2" style="background: rgba(0, 0, 0, .03)">
                    <h5 class="modal-title" id="editVoucherModalLabel">傳票編輯</h5>
                </div>
                <div class="modal-body py-0" style="overflow-y: auto;">
                    <jsp:include page="accounting_editVoucher.jsp">
                        <jsp:param name="render" value="modal" />
                        <jsp:param name="sendControl" value="A" />
                    </jsp:include>
                </div>
            </div>
        </div>
    </div> 
    
    <jsp:include page="accounting_signPOP.jsp" />

    <!-- DetailModal -->
    <div class="modal fade pl-4" id="voucherDModal" tabindex="-1" 
        role="dialog" aria-labelledby="voucherDModalLabel" aria-hidden="true">
        <div class="modal-dialog m-3" style="max-width: none; height: 94%;" role="document">
            <div class="modal-content" style="height: 100%;">
                <div class="modal-header py-2" style="background: rgba(0, 0, 0, .03)">
                    <h5 class="modal-title" id="voucherDModalLabel">傳票明細查詢</h5>
                </div>
                <div class="modal-body py-0" style="overflow-y: auto;">
                    <jsp:include page="accounting_voucherD.jsp">
                        <jsp:param name="render" value="modal" />
                    </jsp:include>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
        data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
        <div class="modal-dialog">
            <img src="/Style/images/loading.gif" style="padding-top: 12rem;">
        </div>
    </div>
    <jsp:include page="JSfooter.jsp" />
</body>
</html>