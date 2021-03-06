<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <jsp:include page="header.jsp" />
        <style type="text/css">
            body {
                display: none;
            }

            label {
                margin: 0 8px 0 0;
            }

            th, td {
                text-align:center
            }
            
            input::-webkit-calendar-picker-indicator {
                margin-bottom: 6px;
            }
            
            .disabled {
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
                content: attr(data-on);
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
                background: #bbf;
                transform: translate(0, -50%);
                transition: .3s;
            }

            input:checked + .slider::before {
                content: attr(data-off);
                background: #faa;
                transform: translate(18px, -50%);
            }
        </style>

        <script src="../../Style/js/accounting.js"></script>
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
                { data: "??????" }, 
                { data: "??????" }, 
                { data: "??????" }
            ]
            const dataTableOptions = {
                pageLength: 12,
                bPaginate: false,
                bInfo: false,
                order: [[0, 'desc']]
            }
            const closedTable = cusDataTable('closedTable', columns, dataTableOptions)

            const now = new Date(), year_now = now.getFullYear(), month_now = now.getMonth() + 1;
            (function() {
                let yearOptions = ''
                for (let i = year_now; i > (year_now - 5); i--) {
                    yearOptions += `<option value="\${i}"/>`
                }
                $("#yearList").append(yearOptions)
            })()
            
            function closedTableRender(closedList) {
                const closedJSON = []
                for (let i = 0; i < closedList.length; i++) {

                    const { month, status } = closedList[i]
                    const year = $("#year").val()
                    if (year >= year_now && month > month_now) continue
                    const switchHTML = `
                        <div class="\${status === 'L' ? 'disabled' : ''}">
                            <label class="switch" for="\${year + '_' + month}">
                                <input 
                                    type="checkbox" 
                                    class="d-none" 
                                    id="\${year + '_' + month}"
                                    \${status === 'O'? '' : 'checked'}
                                />
                                <span class="slider" data-on="Open" data-off="Close"></span>
                            </label>
                        </div>
                    `
                    const statusStr = status === 'O' ? '??????'
                                    : status === 'C' ? '??????' : '?????????'
    
                    closedJSON.push({
                        "??????": month + ' ???',
                        "??????": statusStr,
                        "??????": switchHTML
                    })
                }
                closedTable.clear();
                closedTable.rows.add(closedJSON).draw().nodes().to$();
            }

            function searchClosed() {
                const year = $("#year").val()
                const d = new Date();
                if (year > d.getFullYear()) {
                    return confirm('??????', '????????????????????????????????????????????????')
                } else if (!year) {
                    return confirm('??????', '?????????????????????')
                }

                closedTable.clear();
                closedTable.draw();


                $.ajax({
                    type: 'GET',
                    url: '/rest/accounting/closed?year=' + year,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        if (record.status === '200') {
                            const closedList = record.data
                            closedTableRender(closedList)
                        } else {
                            confirm('??????', record.message)
                        }
                    },
                    error: function () {
                        confirm("??????", "??????????????????");
                    }
                })
            }

            function toggleClosed(e) {
                if (e.target.nodeName !== 'INPUT') return
                
                const year = e.target.id.slice(0,4);
                const month = e.target.id.slice(5,7);
                const status = e.target.checked ? 'C' : 'O';
                const params = {year, month, status}

                const d = new Date();

                if (year === d.getFullYear() && month < d.getMonth() + 1) {
                    return confirm('??????', '???/??????????????????????????????')
                }
                
                $.confirm({
                    title: "??????",
                    content: `?????????\${e.target.checked ? '??????' : '??????'} \${month} ??????????????????`,
                    draggable: false,
                    buttons: {
                        '??????': function () {

                            $.ajax({
                                type: 'PUT',
                                url: '/rest/accounting/closed',
                                data: JSON.stringify(params),
                                contentType: 'application/json;charset=UTF-8',
                                datatype: "json",
                                success: function (data) {
                                    const record = JSON.parse(data)
                                    if (record.status === '200') {
                                        confirm('??????', `\${e.target.checked ? '??????' : '??????'} \${month} ????????????`)
                                        closedTable.cell(month - 1, 1).data(`\${e.target.checked ? '??????' : '??????'}`).draw(false)
                                    } else {
                                        e.target.checked = !e.target.checked
                                        confirm('??????', record.message)
                                    }
                                },
                                error: function () {
                                    e.target.checked = !e.target.checked
                                    confirm("??????", "???/??????????????????");
                                }
                            })
                        },
                        '??????': function () {
                            e.target.checked = !e.target.checked
                        }
                    }
                })
            }

            const searchBtn = document.getElementById('searchBtn')
            searchBtn.addEventListener('click', searchClosed)
            const closedList = document.getElementById('closedList');
            closedList.addEventListener('click', toggleClosed)
            $("#year").on('keyup', (e) => {
                e.target.value = e.target.value.replace(/[^\d]/g,'')
                if (e.which == 13) {
                    searchClosed()
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
                    <header class="card-header">????????????</header>

                    <div class="card-body d-flex flex-column align-items-center px-2">
                        <div class="row align-items-center w-100 py-2 px-3" style="max-width: 500px;">
                            <div class="col">
                                <div class="input-group align-items-center mb-2">
                                    <label for="year">?????? : </label>
                                    <input 
                                        list="yearList"
                                        type="text" 
                                        id="year" 
                                        class="form-control" 
                                        maxlength="4" 
                                        placeholder="??????????????????"
                                    />
                                    <datalist id="yearList"></datalist>
                                </div>
                            </div>

                            <div class="col text-right">
                                <button class="btn btn--primary" id="searchBtn">
                                    ??????
                                </button>
                            </div>
                        </div>

                        <!--id="dataTable"-->
                        <div class="w-100" style="max-width: 500px;">
                            <table class="table table-bordered" id="closedTable" cellspacing="0">
                                <thead>
                                    <tr style="background: #E6E6E6;">
                                        <th title="??????" width="50%">??????</th>
                                        <th title="??????" width="50%" class="no-sort">??????</th>
                                        <th class="no-sort"></th>
                                    </tr>
                                </thead>
                                <tbody id="closedList"></tbody>
                            </table>
                        </div>
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