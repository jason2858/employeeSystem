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

            .date::-webkit-calendar-picker-indicator {
                opacity: 0;
            }

            label, select {
                margin: 0 8px 0 0;
            }

            th, td {
                text-align:center
            }

            #voucherList td:nth-child(4) {
                text-align: right
            }
            
            .invalid-feedback {
                position: absolute;
                bottom: 0;
                right: 0;
                background: #fbb6;
                width: max-content;
                pointer-events: none;
                padding: 0 4px;
                border-radius: 4px;
            }
        </style>

        <script src="../../Style/js/accounting.js"></script>
        <script src="../../Style/js/datalistAPI.js"></script>
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
                { data: "????????????" }, 
                { data: "??????" }, 
                { data: "??????" }
            ]
            const dataTableOptions = {
                pageLength: 6,
            }
            const detailTable = cusDataTable('detailTable', columns, dataTableOptions)
            const datalist = new Datalist();
            const ITEM_DROP = 'itemDropList';
            datalist.getItemDrop(ITEM_DROP);

            function render() {
                detailTable.clear().draw();

                $.ajax({
                    type: 'GET',
                    url: '/rest/voucher/api?voucher_no=' + voucher_no,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        if (record.status === '200') {
                            const data = camelCaseToSnakeCase(record.data[0])
                            const dataKeys = Object.keys(data)
                            dataKeys.forEach(key => {
                                if (key !== 'detail') {
                                    let value = '';
                                    
                                    switch (key) {
                                        case 'predict_date':
                                            value = dateFormat(data[key]);
                                            break;
                                        case 'amount_total':
                                            value = accountFormat(data[key]);
                                            break;
                                        case 'head_item':
                                            const headItem = datalist.record(ITEM_DROP, data[key]);
                                            value = headItem.iId + ' ' + headItem.iName
                                            break;
                                        default:
                                            value = data[key];
                                            break;
                                    }
                                    $(`#\${key}`).val(value);
                                }
                            })
                            
                            const detailJSON = []
                            data.detail.forEach( detail => {
                                const {
                                    project_name,
                                    detail_item,
                                    amount,
                                    directions,
                                    hedge_no_d
                                } = detail;
                                const detailItem = datalist.record(ITEM_DROP, detail_item);

                                detailJSON.push({
                                    "??????": project_name || '?????????',
                                    "??????": `\${detailItem.iId} \${detailItem.iName}`,
                                    "????????????": hedge_no_d,
                                    "??????": accountFormat(amount),
                                    "??????": directions || ''
                                })
                            })
                            detailTable.rows.add(detailJSON).draw().nodes().to$();
                        }
                    },
                    error: function () {
                        confirm("??????", "??????????????????");
                    }
                })
            }
            
            function updateSign(e, sign_type) {
                const form = document.forms['signCheckForm']
                timeoutCheck();
                e.preventDefault();
                e.stopPropagation();
                form.classList.add('was-validated');
                const eles = form.elements
                for(var el in eles) {
                    if (eles[el].required) {
                        eles[el].value = eles[el].value.trim()
                    }
                }
                if (!form.checkValidity()) {
                    return $("#rejectModal").modal('hide')
                }

                const params = {
                    voucher_no,
                    sign_type,
                    v_code: $("#v_code").val(),
                    sign_user: $("#username").val()
                }
                if ($("#reason").val().trim()) {
                    params["reason"] = $("#reason").val().trim()
                }
                $.ajax({
                    type: 'PUT',
                    url: '/rest/accounting/sign',
                    data: JSON.stringify(params),
                    contentType: "application/json;charset=UTF-8",
                    datatype: "json",
                    success: function(data) {
                        const record = JSON.parse(data)
                        if (record.status == '200') {
                            confirm("??????", record.data);
                            $("#rejectModal").modal('hide')
                        } else if (record.status == '700') {
                            confirm("??????", record.message);
                        }
                    },
                    error: function () {
                        confirm("??????", "????????????");
                        $("#rejectModal").modal('hide')
                    }
                })
            }
            
            function approvedSign(e) {
                $.confirm({
                    title: "??????",
                    content: `??????????????? \${voucher_no}???`,
                    draggable: false,
                    buttons: {
                        '??????': function () {
                            updateSign(e, 'Y')
                        },
                        '??????': function () {}
                    }
                })
            }
            
            function leave() {
                window.history.go(-1)
            }
            
            $("#approvedBtn").on('click', approvedSign)
            $("#leaveBtn").on('click', leave)
            $("#rejectBtn").on('click', (e) => {
                updateSign(e, 'N')
            })

            function init() {
                let done = datalist.data[ITEM_DROP]?.length, timer = null;
                if (done) render()
                else {
                    if (timer) {
                        clearTimeout(timer)
                        timer = null;
                    }
                    timer = setTimeout(() => init(), 1000)
                }
            }
            init()
        })

        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="card m-3">
                    <header class="card-header">??????????????????</header>

                    <div class="px-2">
                        <form name="signCheckForm" autocomplete="off">
                            <div class="form-row justify-content-end align-items-center px-3 pt-3">
                            
                                <div class="col-3 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="company">????????? : </label>
                                        <input 
                                            type="text"
                                            id="company"
                                            class="form-control form-control-sm"
                                            disabled
                                        >
                                    </div>
                                </div>
    
                                <div class="col-5 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="voucher_no">???????????? : </label> 
                                        <input 
                                            type="text"
                                            id="voucher_no" 
                                            class="form-control form-control-sm" 
                                            disabled
                                        />
                                    </div>
                                </div>
                                
                                <div class="col-4 mb-2" style="flex: 1 0 250px;">
                                    <div class="input-group align-items-center">
                                        <label for="predict_date">?????????/????????? : </label>
                                        <input 
                                            type="text" 
                                            id="predict_date" 
                                            name="predict_date" 
                                            class="date form-control form-control-sm text-center"
                                            disabled
                                        />
                                    </div>
                                </div>
    
                                <div class="col-3 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="applicant">????????? : </label>
                                        <input
                                            type="text" 
                                            id="applicant"
                                            name="applicant"
                                            class="form-control form-control-sm"
                                            disabled
                                        >
                                    </div>
                                </div>
    
                                <div class="col-5 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="customer">???????????? : </label>
                                        <input 
                                            type="text"
                                            id="customer"
                                            name="customer"
                                            class="form-control form-control-sm" 
                                            disabled
                                        />
                                    </div>
                                </div>
                                <div class="col-4 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="cus_tax_id">???????????? : </label> 
                                        <input 
                                            type="text"
                                            id="cus_tax_id" 
                                            name="cus_tax_id" 
                                            class="form-control form-control-sm" 
                                            disabled
                                        />
                                    </div>
                                </div>
    
                                <div class="col-3 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="username">????????? : </label>
                                        <input
                                            list="applicantList"
                                            type="text" 
                                            id="username"
                                            class="form-control form-control-sm"
                                            placeholder="???????????????????????????"
                                            required
                                        >
                                        <div class="invalid-feedback">
                                            ??????????????????????????????
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="col-5 mb-2">
                                    <div class="input-group align-items-center">
                                        <label for="v_code">????????? : </label>
                                        <input
                                            type="text" 
                                            id="v_code"
                                            name="v_code" 
                                            class="form-control form-control-sm"
                                            placeholder="??????????????????"
                                            maxlength="16"
                                            required
                                        >
                                        <div class="invalid-feedback">
                                            ?????????????????????
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="col-4 mb-2 text-right">
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn--primary ml-2" 
                                        id="approvedBtn" 
                                    >
                                        ??????
                                    </button>
    
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn--danger ml-2"
                                        data-toggle="modal" 
                                        data-target="#rejectModal"
                                    >
                                        ??????
                                    </button>
    
                                    <button 
                                        type="button"
                                        class="btn btn-sm btn--info ml-2" 
                                        id="leaveBtn" 
                                    >
                                        ??????
                                    </button>
                                </div>
    
                            </div>
                        </form>

                        <!--id="dataTable"-->
                        <table class="table table-bordered" id="detailTable" cellspacing="0">
                            <thead>
                                <tr style="background: #E6E6E6;">
                                    <th title="??????" class="no-sort">??????</th>
                                    <th title="??????" class="no-sort">??????</th>
                                    <th title="????????????" class="no-sort">????????????</th>
                                    <th title="??????" class="no-sort">??????</th>
                                    <th title="??????" class="no-sort">??????</th>
                                </tr>
                            </thead>
                            <tbody id="detailList"></tbody>
                        </table>

                        <div class="form-row justify-content-end py-2 px-3">
                            <div class="col mb-2" style="flex: 1 0 240px;">
                                <div class="input-group align-items-center">
                                    <label for="hedge_no_h">???????????? : </label>
                                    <input 
                                        type="text" 
                                        id="hedge_no_h"
                                        name="hedge_no_h" 
                                        class="form-control form-control-sm"
                                        disabled
                                    />
                                </div>
                            </div>
                            <div class="col mb-2" style="flex: 1 0 180px;">
                                <div class="input-group align-items-center">
                                    <label for="amount_total">????????? : </label>
                                    <input 
                                        type="text" 
                                        id="amount_total" 
                                        class="form-control form-control-sm text-right"
                                        disabled
                                    />
                                </div>
                            </div>
                            <div class="col mb-2" style="flex: 1 0 240px;">
                                <div class="input-group align-items-center">
                                    <label for="head_item">???????????? : </label>
                                    <input 
                                        id="head_item"
                                        name="head_item"
                                        class="form-control form-control-sm" 
                                        disabled
                                    />
                                </div>
                            </div>
                            <div class="col mb-2" style="flex: 1 0 180px;">
                                <div class="input-group align-items-center">
                                    <label for="directions" class="flex-shrink">?????? : </label>
                                    <textarea 
                                        name="directions" 
                                        id="directions" 
                                        rows="1" 
                                        class="form-control form-control-sm"
                                        disabled
                                    ></textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <jsp:include page="footer.jsp" />
        </div>
        <div class="modal fade" id="rejectModal" tabindex="-1" role="dialog" aria-labelledby="rejectModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="rejectModalLabel">????????????</h5>
                    </div>
                    <div class="modal-body">
                        <div class="input-group mb-3">
                            <textarea name="reason" id="reason" rows="10" class="form-control" maxlength="50"></textarea>
                        </div>
                        <div class="text-right">
                            <button type="button" class="btn btn--primary ml-2" id="rejectBtn">??????</button>
                            <button type="button" class="btn btn--danger ml-2" data-dismiss="modal">??????</button>
                        </div>
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