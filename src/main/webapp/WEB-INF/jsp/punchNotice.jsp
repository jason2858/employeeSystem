<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <jsp:include page="header.jsp" />
        <style type="text/css">
            body {
                display: none;
            }

            .date::-webkit-calendar-picker-indicator {
                opacity: 0;
            }

            label, select {
                margin: 0 8px 0 0;
            }

            #dataTable th,
            #dataTable td {
                text-align: center;
            }

            #dataTable tr:hover {
                background: #F7F7F7;
            }

            .selectItem {
                cursor: pointer;
            }

            button:disabled {
                pointer-events: none;
            }
        </style>

        <!-- Datetimepicker-->
        <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
        <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        <script src="../../Style/js/accounting.js"></script>
        <script type="text/javascript">
            
        var isFinishLoad = false;
        var account = '<%out.print(session.getAttribute("Account"));%>';
        var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
        var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
        var voucher_no = '<%= session.getAttribute( "voucher_no" ) %>';
        $(document).ready(function () {
            $("#loading").modal("show");
            $("#loading").on("shown.bs.modal", function () {
                isFinishLoad = true;
            });
            closeLoading();

            if (authorise != 1 && authorise != 2 && authorise != 3 && authorise != 4) {
                window.location.href = "timeout.do";
            }
            if (authorise == 1 || authorise == 2 || authorise == 3) {
                appendUnsign();
                getUnsign();
            }

            $("body").show();
            
            const columns = [
                { data: "checkbox" }, 
                { data: "account" }, 
                { data: "name" }, 
                { data: "punchDate" }, 
                { data: "inStatus" },
                { data: "outStatus" },
                { data: "schedules" }
            ];
            const dataTableOptions = {
                pageLength: -1,
                bPaginate: false,
                bInfo: false,
                order: [[3, 'asc']]
            };
            const $dataTable = cusDataTable('dataTable', columns, dataTableOptions);
            const store = (function() {
                const data = {
                    status: []
                };

                function getData() {
                    return data.status;
                }
                function change(checked, val) {
                    const [account, notice_date] = val.split('_$');
                    const index = data.status.findIndex(item => item.account === account && item.notice_date === notice_date);
                    if (checked && index === -1) data.status.push({ account, notice_date});
                    else if (!checked && index > -1) data.status.splice(index, 1);
                }
                function clear() {
                    data.status = [];
                    $("#sendMail").attr('disabled', true);
                    $("#selectAll")[0].checked = false;
                }
                return { getData, change, clear }
            })()

            function search() {
                const start_date = $("#start_date").val();
                const end_date = $("#end_date").val();
                const params = {
                    start_date: start_date.replaceAll('-', ''),
                    end_date: end_date.replaceAll('-', '')
                }
                let paramStr = '';

                Object.keys(params).forEach(key => {
                    paramStr += `\${key}=\${params[key]}&`
                })
                paramStr = paramStr.slice(0, -1);

                if (getDateDiff(start_date, end_date) > 30) {
                    return confirm('訊息', '日期範圍無法超過 30 天');
                }

                if (new Date(start_date) > new Date(end_date)) {
                    return confirm('訊息', '查詢日期範圍輸入相反');
                }

                if (new Date(end_date) > new Date()) {
                    return confirm('訊息', '查詢日期不可超過當日');
                }

                $dataTable.clear().draw();
                store.clear();

                $.ajax({
                    type: 'GET',
                    url: `/rest/punchNotice?\${paramStr}`,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success(data) {
                        const record = JSON.parse(data);
                        if (record.status === '200') {
                            const { punchList } = record.data, json = [];
                            let skip = 0;

                            punchList.forEach((punch, index) => {
                                if (punch.inStatus === 'X' || punch.outStatus === 'X') {
                                    const { account, punchDate, schedules, schedulesTime } = punch;
                                    punch.punchDate = dateFormat(punchDate);
                                    punch.schedules = typeToSchedules(schedules) + ' ' + (schedulesTime || '');
                                    const checkbox = `
                                        <label for="punch_\${index}" style="display: block; cursor: pointer;">
                                            <input type="checkbox" class="selectItem" id="punch_\${index}" value="\${account}_$\${punchDate}"/>
                                        </label>
                                    `;
                                    const i = index - skip - 1;

                                    if (account === json[i]?.account && punch.punchDate === json[i]?.punchDate) {
                                        json[i].schedules = punch.schedules + '<br/>' + json[i].schedules;
                                        skip += 1;
                                    } else {
                                        const row = { ...punch, checkbox };
    
                                        json.push(row);
                                    }
                                } else {
                                    skip += 1;
                                }
                            })
                            $dataTable.rows.add(json).draw();
                        }
                    },
                    error() {
                        confirm("訊息", "搜尋資料失敗");
                    }
                })
            }

            function typeToSchedules(type) {
                switch (type) {
                    case '1': return "";
                    case '2': return "出差";
                    case '3': return "特休";
                    case '4': return "事假";
                    case '5': return "病假";
                    case '6': return "公假";
                    case '7': return "婚假";
                    case '8': return "喪假";
                    case '9': return "加班";
                    case '10': return "補休";
                    case '11': return "外出";
                    default: return "";
                }
            }

            function sendMail() {
                if (store.getData().length === 0) return;
                const selectList = document.querySelectorAll('.selectItem');
                const $sendMail = $("#sendMail");
                const $loading = $("#loading");

                $sendMail.attr('disabled', true);
                $loading.modal("show");

                $.ajax({
                    type: 'POST',
                    url: '/rest/punchNotice/sendMail',
                    data: JSON.stringify({ notice: store.getData() }),
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success(data) {
                        const record = JSON.parse(data);
                        $loading.modal("hide");

                        if (record.status === '200') {
                            for (const select of selectList) {
                                if (select.checked) {
                                    select.parentNode.parentNode.innerText = '已寄送';
                                }
                            }
                            store.clear();
                            confirm("訊息", record.data);
                        } else {
                            $sendMail.removeAttr('disabled');
                            confirm("訊息", record.message);
                        }
                    },
                    error() {
                        $loading.modal("hide");
                        confirm("訊息", "送信失敗");
                        $sendMail.removeAttr('disabled');
                    }
                })
            }

            function selectCheckbox(e) {
                const { checked, value, className } = e.target;
                const $sendMail = $("#sendMail");
                const selectList = document.querySelectorAll('.selectItem');

                switch (className) {
                    case 'selectItem':
                        store.change(checked, value);
                        $("#selectAll")[0].checked = selectList.length === store.getData().length;
                        break;
                    case 'selectAll':
                        for (const select of selectList) {
                            select.checked = checked;
                            store.change(select.checked, select.value);
                        }
                        break;
                    default:
                        break;
                }
                if (store.getData().length === 0) $sendMail.attr('disabled', true);
                else $sendMail.removeAttr('disabled');
            }

            $dataTable.on('change', selectCheckbox)
            $("#sendMail").on('click', sendMail)
            $("#search").on('click', search)
            cusDatepicker('start_date', -5);
            cusDatepicker('end_date');
            search();
        })
        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="card m-3">
                    <header class="card-header">補打卡通知頁面</header>

                    <div class="px-2 pb-2">
                        <div class="form-row px-3 pt-3 pb-1">
                            <div class="col-2">
                                <button type="button" id="sendMail" class="btn btn--info" disabled>
                                    Email 寄送
                                </button>
                            </div>
                            
                            <div class="col-8 d-flex input-group align-items-center">
                                <label for="start_date"> 查詢日期範圍 : </label>
                                <div class="form-control d-flex align-items-center">
                                <input
                                    type="text"
                                    id="start_date"
                                    class="date text-center w-50 border-0"
                                    readonly
                                    required
                                />
                                <label for="end_date" class="m-0 px-1"> 至 </label>
                                <input
                                    type="text"
                                    id="end_date"
                                    class="date text-center w-50 border-0"
                                    readonly
                                    required
                                />
                                </div>
                            </div>
                            
                            <div class="col-2 text-right">
                                <button type="button" id="search" class="btn btn--info">
                                    查詢
                                </button>
                            </div>
                        </div>

                        <!--id="dataTable"-->
                        <table class="table table-bordered" id="dataTable" cellspacing="0">
                            <thead>
                                <tr style="background: #E6E6E6;">
                                    <th title="全選" class="no-sort">
                                        <label for="selectAll" style="display: block; cursor: pointer;">
                                            <input type="checkbox" id="selectAll" class="selectAll"/>
                                            全選
                                        </label>
                                    </th>
                                    <th title="員工帳號">員工帳號</th>
                                    <th title="員工" class="no-sort">員工</th>
                                    <th title="打卡日期">打卡日期</th>
                                    <th title="上班狀態" class="no-sort">上班狀態</th>
                                    <th title="下班狀態" class="no-sort">下班狀態</th>
                                    <th title="差勤狀態" class="no-sort">差勤狀態</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
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