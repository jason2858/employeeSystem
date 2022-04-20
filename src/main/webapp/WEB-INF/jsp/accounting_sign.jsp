<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <jsp:include page="header.jsp" />
        <style type="text/css">
            body {
                display: none;
            }

            input::-webkit-calendar-picker-indicator {
                margin-bottom: 6px;
            }

            label {
                margin: 0 8px 0 0;
            }

            th, td {
                text-align:center
            }
            
            .table td {
                padding: .5rem;
            }

            .btn:disabled {
                pointer-events: none;
                cursor: none;
                opacity: .4;
            }
        </style>

        <script src="../../Style/js/accounting.js?date=20220318"></script>

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
            const columns = [
                { data: "單號" }, 
                { data: "簽核角色" }, 
                { data: "簽核人員" }, 
                { data: "簽核狀態" }, 
                { data: "送簽" }
            ]
            const dataTableOptions = {
                order: [
                    [0, 'desc'],
                ],
            }
            cusDataTable('signTable', columns, dataTableOptions)
            
            function sendSign(e) {
                if (e.target.nodeName !== 'BUTTON') return
                timeoutCheck();
                const voucher_no = e.target.parentNode.parentNode.children[0].innerText

                const params = {
                    voucher_no,
                    type: 'A'
                }
                $("#loading").modal('show');
                $.ajax({
                    type: 'POST',
                    url: "/rest/voucher/send",
                    data: JSON.stringify(params),
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        $("#loading").modal('hide');
                        if (record.status === "200") {
                            confirm("訊息", "簽程送出成功");
                            e.target.disabled = true;
                        } else if (record.status === "700") {
                            confirm("訊息", record.message);
                        }
                    },
                    error: function () {
                        $("#loading").modal('hide')
                        confirm("訊息", "送簽失敗");
                    }
                })
            }

            function signTableRender({signPersonList}) {
                const signPersonJSON = []
                signPersonList.sort((a, b) => b.voucherNo - a.voucherNo)
                let isCurrent = true;
                let isCurrentNo = '';
                signPersonList.forEach( signPerson => {
                    const {
                        voucherNo,
                        role,
                        signUser,
                        status,
                    } = signPerson
                    
                    if (status === 'UNDONE') {
                        isCurrent = false;
                    }
                    if (isCurrentNo !== voucherNo) {
                        isCurrent = true;
                    }
                    isCurrentNo = voucherNo
                    const btnHTML = `
                        <button 
                            type="button" 
                            class="btn btn-sm btn--info"
                            \${
                                status !== 'UNDONE' ? 'disabled' : 
                                isCurrent ? '' : 'disabled'
                            }
                        >
                            送簽
                        </button>
                    `
                    const statusType = status === 'UNDONE' ? '尚未簽核'
                                        : status === 'REJECT' ? '駁回'
                                        : status === 'FINISHED' ? '已簽核' : '狀態異常'
                    signPersonJSON.push({
                        "單號": voucherNo,
                        "簽核角色": role,
                        "簽核人員": signUser,
                        "簽核狀態": statusType,
                        "送簽": btnHTML
                    })
                })
                const signTable = $('#signTable').DataTable();
                signTable.clear();
                signTable.rows.add(signPersonJSON).draw().nodes().to$();
            }

            function searchVoucherSign() {
                const voucher_no = $("#voucher_no").val()

                const signTable = $('#signTable').DataTable();
                signTable.clear();
                signTable.draw();

                $.ajax({
                    type: 'GET',
                    url: '/rest/accounting/sign?voucher_no=' + voucher_no,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        if (record.status === '200') {
                            const signPersonList = record.data.set
                            signTableRender({signPersonList})
                        }
                    },
                    error: function () {
                        confirm("訊息", "搜尋資料失敗");
                    }
                })
            }

            const signPersonList = document.getElementById('signPersonList');
            signPersonList.addEventListener('click', sendSign)
            const searchBtn = document.getElementById('searchBtn')
            searchBtn.addEventListener('click', searchVoucherSign)
            $("#voucher_no").on('keyup', (e) => {
                e.target.value = e.target.value.replace(/[^\d]/g,'')
                if (e.which == 13) {
                    searchVoucherSign()
                }
            })
        });

        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="card m-3">
                    <header class="card-header">傳票簽程查詢</header>

                    <div class="card-body px-2">
                        <div class="row no-gutters align-items-center mb-2 px-3">

                            <div class="col">
                                <div class="input-group align-items-center">
                                    <label for="voucher_no">單號 : </label>
                                    <input 
                                        type="text" 
                                        id="voucher_no" 
                                        class="form-control" 
                                        maxlength="20" 
                                        required
                                    />
                                </div>
                            </div>

                            <div class="col text-right">
                                <button class="btn btn--primary" id="searchBtn">
                                    查詢
                                </button>
                            </div>
                        </div>

                        <!--id="dataTable"-->
                        <table class="table table-bordered" id="signTable" cellspacing="0">
                            <thead>
                                <tr style="background: #E6E6E6;">
                                    <th title="單號">單號</th>
                                    <th title="簽核角色">簽核角色</th>
                                    <th title="簽核人員">簽核人員</th>
                                    <th title="簽核狀態">簽核狀態</th>
                                    <th class="no-sort"></th>
                                </tr>
                            </thead>
                            <tbody id="signPersonList"></tbody>
                        </table>
                    </div>
                </div>
            </div>
            <jsp:include page="footer.jsp" />
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