<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%-- 傳票編輯需引入此 jsp & 初始化 initVoucherPage(account) --%>
<head>
    <style type="text/css">
        label {
            margin: 0 8px 0 0;
        }

        .table-responsive {
            position: relative;
            max-height: 40vh;
            overflow: auto;
            -webkit-overflow-scrolling: touch;
            -ms-overflow-style: -ms-autohiding-scrollbar;
        }

        thead {
            position: sticky;
            top: 0;
            left: 0;
            right: 0;
            z-index: 2;
        }

        input::-webkit-calendar-picker-indicator {
            margin-bottom: 6px;
        }

        .date::-webkit-calendar-picker-indicator {
            opacity: 0;
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

        .was-validated .form-control:valid {
            border-color: #ced4da;
        }

        .ui-datepicker {
            z-index: 1050 !important;
        }

    </style>

    <script src="../../Style/js/accounting.js?ver=20220318"></script>

    <script type="text/javascript" defer>
    /* 頁面資料統一管理 */
    const store = (function() {

        const initData = {
            voucher_no: null,
            company: '越世實業',
            customer: null,
            cus_tax_id: null,
            hedge_no_h: null,
            applicant: null,
            common: 'N',
            tax_id_type: 'Y',
            directions: null,
            detail: [
                {
                    project_id: null,
                    directions: null,
                    hedge_no_d: null
                }
            ]
        }

        const initStatus = {
            archived: false,    // 儲存
            modified: false,    // 更改資料
            openSignSet: false, // 開啟簽呈設定
            isSignSave: false,  // 簽呈設定是否成功
            m_token: null,
            delete_detail: []   // 放刪除的 detail
        }

        const status = {
            status: Object.assign({}, initStatus),
            data: JSON.parse(JSON.stringify(initData))
        }

        return {
            get(data, prop) {
                return status[data][prop]
            },
            set(data, prop, value) {
                if (value === null || value === undefined) return

                if (data === 'status') {
                    status[data][prop] = value
                } else {
                    if ((value.toString().trim() || null) !== status[data][prop]) {
                        status.status.modified = true
                    }
                    if (prop !== 'hedge_no_h_o') {
                        status[data][prop] = value.toString().trim() || null ;
                    } else {
                        delete status[data]['hedge_no_h']
                        status[data][prop] = value.toString().trim() || null ;
                    }
                } 
            },
            setDetail(index, prop, value) {
                if (value === null || value === undefined) return
                if ((value.toString().trim() || null) !== status.data.detail[index][prop]) {
                    status.status.modified = true
                }
                if (prop !== 'hedge_no_d_o') {
                    status.data.detail[index][prop] = value.toString().trim() || null ;
                } else {
                    delete status.data.detail[index]['hedge_no_d']
                    status.data.detail[index][prop] = value.toString().trim() || null ;
                }
            },
            push() {
                status.status.modified = true
                status.data.detail.push({
                    detail_no: '',
                    project_id: null,
                    detail_item: null,
                    amount: 0,
                    directions: null,
                    hedge_no_d: null,
                })
            },
            splice(index) {
                status.status.modified = true
                const deleteDetail = status.data.detail.splice(index, 1)[0]
                if (deleteDetail.hedge_no_d_o) {    // 有 hedge_no_d_o 進入 delete_detail 儲存
                    deleteDetail.hedge_no_d_n = null;
                    status.status.delete_detail.push(deleteDetail)
                }
            },
            getStatus() {
                return status
            },
            mergeDeleteDetail() {
                /* 合併已刪除的 detail 給後端做資料刪除 */
                status.data.detail = [...status.data.detail, ...status.status.delete_detail]
                status.status.delete_detail.length = 0;
            },
            removeOldDetail(length) {
                status.data.detail.length = length  // 根據返回長度，刪除舊的 detail
            },
            setData(data) {
                status.data = Object.assign({}, initData, data)
            },
            reset() {
                status.status = Object.assign({}, initStatus)
                status.data = JSON.parse(JSON.stringify(initData))
            }
        }
    })()

    function initVoucherPage(account) {
        const render = '<%=request.getParameter("render")%>'
        const sendControl = '<%=request.getParameter("sendControl")%>'

        /* 取得日期 */
        cusDatepicker('predict_date')
        cusDatepicker('credit_date')
        
        addRow('projectList', $("#detailTR").html());
        
        /* 取得 datalist */
        const {companyList, applicantList, projectNameList, customerList, itemList, getVoucher} = initDatalist()
        $("#projectNameList").prepend('<option value="非專案">')
        /* 初始化簽程 POP */
        initSignModal({applicantList})

        const form = document.forms['voucherForm']
        form.addEventListener('keyup', controlData)
        form.addEventListener('click', controlData)

        function controlData(e) {
            const { dataset, value, checked, disable } = e.target;
            const { head, detail, btn } = dataset;
            if (!(head || detail || btn)) return
            if (disable) return

            if (head) {
                switch (head) {
                    case 'common':
                        store.set('data', head, checked ? 'Y' : 'N')
                        break;
                    case 'head_item':
                        store.set('data', head, itemList.getItem(value.split(' ')[0], 'iId', 'iName').iId || '')
                        break;
                    case 'applicant':
                        store.set('data', head, applicantList.getItem(value, 'chineseName', 'name').name || value.trim())
                        break;
                    case 'hedge_no_h':
                        store.set('data', $("#voucher_no").val() ? 'hedge_no_h_n' : head, value.trim())
                        break;
                    case 'predict_date':
                    case 'credit_date':
                        store.set('data', head, value.replaceAll('-', '').trim())
                        break;
                    case 'customer':
                    case 'cus_tax_id':
                    case 'tax_id_type':
                        getTax(e, customerList);
                    default:
                        store.set('data', head, value.trim())
                }

            } else if (detail) {
                const trDOM = e.target.parentNode.parentNode
                const index = trDOM.nodeName === 'TR' ? trDOM.rowIndex - 2 : trDOM.parentNode.rowIndex - 2
                switch (detail) {
                    case 'project_name':
                        store.setDetail(index, 'project_id', projectNameList.getItem(value.split(' ')[0], 'id', 'name').id || '')
                        break;
                    case 'detail_item':
                        store.setDetail(index, detail, itemList.getItem(value.split(' ')[0], 'iId', 'iName').iId || '')
                        break;
                    case 'hedge_no_d':
                        store.setDetail(index, store.get('status', 'archived') ? 'hedge_no_d_n' : detail, value.trim())
                        break;
                    case 'amount':
                        checkAccount(e)
                        store.setDetail(index, detail, Number.parseInt(value.replaceAll(',',''), 10) || 0)
                        break;
                    default:
                        store.setDetail(index, detail, value.trim())
                }
            
            } else if (btn) {
                const trDOM = e.target.parentNode.parentNode;
                const index = trDOM.rowIndex - 2
                switch (btn) {
                    case 'add':
                        addRow('projectList', $("#detailTR").html())
                        $("#detailTable")[0].scrollTo({
                            top: $("#detailTable")[0].scrollHeight, 
                            behavior: 'smooth'
                        })
                        store.push()
                        break;
                    case 'del':
                        const tbodyDOM = document.getElementById('projectList')
                        if (tbodyDOM.rows.length < 2) return;
                        delRow('projectList', index)
                        store.splice(index)
                        getTotal()
                        break;
                    case 'reset':
                        $.confirm({
                            title: "警告",
                            content: "請問確定要清除資料嗎？",
                            draggable: false,
                            buttons: {
                                '確認': function () { reset(); },
                                '取消': function () {}
                            }
                        });
                        break;
                    case 'save':
                        checkForm(e)
                        break;
                    case 'signSet':
                        openSignModal();
                        break;
                    case 'getVoucher':
                        getVoucher()
                        break;
                    case 'signSend':
                        signSend()
                        break;
                    case 'leave':
                        leave()
                        break;
                    case 'editHead':
                        $.confirm({
                            title: "訊息",
                            content: `確定要編輯單號 \${$("#voucher_no").val()} 的傳單表頭資料？`,
                            draggable: false,
                            scrollToPreviousElement: false,
                            scrollToPreviousElementAnimate: false,
                            buttons: {
                                '確認': function () {voucherHeadLock(false)},
                                '取消': function () {}
                            }
                        });
                        break;
                    default:
                }
            }
        }
    
        function checkAccount(e) {
            let { value } = e.target;
            value = value.replace(/[^0-9]/g,'');
            if (value.length > 1 && value[0] === '0') {
                value = value.substring(1)
            }
            e.target.value = accountFormat(value);
            
            let timer = null;
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            timer = setTimeout(getTotal(), 500);
        }
    
        function getTax(e, customerList) {
            if (e.target.disabled) return
            const tax = $("input:radio[name=taxId]:checked").val();
            const customer = document.getElementById("customer");
            const cusTaxId = document.getElementById("cus_tax_id");
            const { id, value } =  e.target
            const key = id === 'customer' ? 'name': 'ein'
            const connection = id === 'customer' ? ['cus_tax_id', 'ein'] : ['customer', 'name']
            const connectionValue = customerList.getItem(value, key)[connection[1]] || ''
    
            if (tax === "N") {
                cusTaxId.value = '';
                cusTaxId.removeAttribute('required')
                customer.removeAttribute('required')
                cusTaxId.setAttribute('placeholder', '不需統編')
                cusTaxId.setAttribute('disabled', '')
                store.set('data', 'cus_tax_id', '')
            } else {
                customer.setAttribute('required', '')
                cusTaxId.setAttribute('required', '')
                cusTaxId.setAttribute('placeholder', '請輸入...')
                cusTaxId.removeAttribute('disabled')
                
                if ($("#customer").val().trim()) {
                    const cus_tax_id_val = customerList.getItem($("#customer").val(), 'name').ein
                    $("#cus_tax_id").val(cus_tax_id_val);
                    store.set('data', 'cus_tax_id', cus_tax_id_val)
                }
                if (!$("#" + connection[0]).val().trim()) {
                    $("#" + connection[0]).val(connectionValue);
                    store.set('data', connection[0], connectionValue)
                }
            }
        }

        function checkForm(e) {
            timeoutCheck();
            e.preventDefault();
            e.stopPropagation();
            form.classList.add('was-validated');
            const eles = form.elements
            for(var el in eles) {
                if (eles[el].nodeName === 'INPUT') {
                    eles[el].value = eles[el].value.trim()
                }
            }
            if (!form.checkValidity()) {
                return store.set('status', 'openSignSet', false)
            }
            const predictDate = $("#predict_date").val()
            const creditDate = $("#credit_date").val()
            if (new Date(predictDate) < new Date(creditDate)) {
                $.confirm({
                    title: "訊息",
                    content: "預期收/付日 早於 入帳日，確認要存檔嗎？",
                    draggable: false,
                    scrollToPreviousElement: false,
                    scrollToPreviousElementAnimate: false,
                    buttons: {
                        '確認': function () {
                            save({ predictDate, creditDate })
                        },
                        '取消': function () {}
                    }
                });
            } else {
                save({ predictDate, creditDate })
            }
        }

        function save({ predictDate, creditDate }) {
            store.set('data', 'predict_date', predictDate.replaceAll('-',''))
            store.set('data', 'credit_date', creditDate.replaceAll('-',''))
            store.mergeDeleteDetail()  // 合併要刪除的 detail
            const params = store.getStatus().data

            params.detail.forEach((item, index, arr)=>{
                arr[index].detail_no = ('00' + (index + 1)).slice(-3)
            })

            if (store.get('status', 'archived')) {
                params["voucher_no"] = $("#voucher_no").val();
                params["m_token"] = store.get('status', 'm_token');
            }

            $("#loading").modal("show");
            $.ajax({
                type: store.get('status', 'archived') ? "PUT" : "POST",
                url: "/rest/voucher/api",
                data: JSON.stringify(params),
                contentType: 'application/json;charset=UTF-8',
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    
                    if (record.status === "200") {
                        const { voucherNo, mToken, hedgeNoH, detail } = record.data[0]
                        $("#voucher_no").val(voucherNo)
                        $("#hedge_no_h").val(hedgeNoH)
                        store.set('status', 'm_token', mToken)
                        store.set('status', 'voucher_no', voucherNo)
                        store.set('data', 'hedge_no_h_o', hedgeNoH)
                        store.set('data', 'hedge_no_h_n', hedgeNoH)
                        const projectList = document.getElementById('projectList');
                        const rowCount = projectList.rows.length;
                        for( let tr = 0; tr < rowCount; tr++) {
                            projectList.rows[tr].cells[3].children[0].value = detail[tr].hedgeNoD;
                            store.setDetail(tr, 'hedge_no_d_o', detail[tr].hedgeNoD)
                            store.setDetail(tr, 'hedge_no_d_n', detail[tr].hedgeNoD)
                        }
                        $("#loading").modal("hide");
                        store.set('status', 'archived', true)
                        store.removeOldDetail(detail.length)    // 清除已刪除的舊 detail
                        form.classList.remove('was-validated');
                        store.set('status', 'modified', false)
                        if (store.get('status', 'openSignSet')) {
                            openSignModal();
                        } else {
                            confirm("訊息", "存檔成功")
                        }
                        voucherHeadLock(true)
                        store.set('status', 'modified', false)

                    } else if (record.status === "700") {
                        $("#loading").modal("hide");
                        confirm("訊息", record.message);
                    }
                    store.set('status', 'openSignSet', false)
                },
                error: function () {
                    $("#loading").modal("hide");
                    confirm("訊息", "存檔失敗");
                    store.set('status', 'openSignSet', false)
                },
            });
        }

        function openSignModal() {
            store.set('status', 'openSignSet', true)
            const voucher_no = $("#voucher_no").val()
            $("#sign_fee_no").val(voucher_no)

            if (!store.get('status', 'modified') && store.get('status', 'archived')) {
                $("#signSetModal").modal("show");
                $.ajax({
                    type: 'GET',
                    url: '/rest/accounting/sign?voucher_no=' + voucher_no,
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        if (record.status === "200") {
                            const { set } = record.data;
                            // 套用取得簽程設定資料
                            $("#signSet_PersonList").empty();

                            // 套用取得簽程設定資料
                            const signSetDOM = document.getElementById("signSet_PersonList");
                            set.forEach(({role, signUser}) => {
                                addRow("signSet_PersonList", $("#signSetTR").html());
                                const rowCount = signSetDOM.rows.length;
                                const rowDOM = signSetDOM.rows[rowCount - 1];
                                rowDOM.cells[1].children[0].value = role;
                                rowDOM.cells[2].children[0].value = signUser;
                            })
                            store.set('status', 'isSignSave', true)
                        } else {
                            addRow("signSet_PersonList", $("#signSetTR").html());
                        }
                        store.set('status', 'openSignSet', false)
                    },
                    error: function () {
                        store.set('status', 'openSignSet', false)
                        confirm("訊息", "取得失敗");
                    }
                })
            } else {
                $('[data-btn="save"]').click()
            }
        }

        function signSend() {
            timeoutCheck();
            if (store.get('status', 'modified')) { 
                confirm("訊息", "資料變動過，請重新儲存。");

            } else if (store.get('status', 'isSignSave')) {
                if ($("#credit_date").val().replaceAll('-','') !== store.getStatus().data.credit_date) {
                    return confirm("訊息", "資料變動過，請重新儲存。");
                }
                if ($("#predict_date").val().replaceAll('-','') !== store.getStatus().data.predict_date) {
                    return confirm("訊息", "資料變動過，請重新儲存。");
                }
                const params = {
                    voucher_no: $("#sign_fee_no").val(),
                    type: sendControl
                }
                $("#loading").modal('show');
                $.ajax({
                    type: "POST",
                    url: "/rest/voucher/send",
                    data: JSON.stringify(params),
                    contentType: 'application/json;charset=UTF-8',
                    datatype: "json",
                    success: function (data) {
                        const record = JSON.parse(data)
                        $("#loading").modal('hide');
                        if (record.status === "200") {
                            confirm("訊息", "簽程送出成功");
                        } else if (record.status === "700") {
                            confirm("訊息", record.message);
                        }
                    },
                    error: function () {
                        $("#loading").modal('hide')
                        confirm("訊息", "簽程送出失敗");
                    },
                });
            } else {
                confirm("訊息", "請先設定簽程");
            }
        }
    
        function leave() {
            const controlLeave = () => {
                if (render === 'modal') {
                    reset()
                    $("#editVoucherModal").modal('hide');
                } else {
                    window.history.go(-1)
                }
            }
            if (store.get('status', 'modified')) {
                $.confirm({
                    title: "訊息",
                    content: "資料變動過，尚未儲存，確定要離開？",
                    draggable: false,
                    scrollToPreviousElement: false,
                    scrollToPreviousElementAnimate: false,
                    buttons: {
                        '確認': controlLeave,
                        '取消': function () {}
                    }
                });
            } else {
                controlLeave()
            }
        }
        return {companyList, applicantList, projectNameList, customerList, itemList, getVoucher}
    }

    function voucherHeadLock(bool) {
        if (bool) {
            $("[data-btn='editHead']").show()
        } else {
            $("[data-btn='editHead']").hide()
        }
        const form = document.forms['voucherForm']
        const eles = form.elements
        for(var el in eles) {
            const str = JSON.stringify(eles[el].dataset)
            if (str === '{}' || str === undefined) continue
            if (eles[el].dataset.head && eles[el].id !== 'voucher_no') {
                eles[el].disabled = bool
            }
            if ($("#noTax")[0].checked && eles[el].id === 'cus_tax_id') {
                eles[el].disabled = true
            }
        }
    }
    
    function addRow(tbodyID, template) {
        const tbodyDOM = document.getElementById(tbodyID);
        if (!tbodyDOM) return;
        const newRow = tbodyDOM.insertRow(-1);
        newRow.innerHTML = template;
        if (tbodyID === 'projectList') {
            newRow.cells[2].children[0].children[0].required = true;
            newRow.cells[4].children[0].children[0].required = true;
        }
    }
        
    function delRow(tbodyID, index) {
        const tbodyDOM = document.getElementById(tbodyID)
        if (tbodyDOM.rows.length < 2) return;
        tbodyDOM.deleteRow(index);
    }

    function reset() {
        const form = document.forms['voucherForm']
        voucherHeadLock(false)
        $("#useTax").click()
        store.reset()
        form.reset()
        form.classList.remove('was-validated');
        $("#projectList").empty();
        addRow('projectList', $("#detailTR").html());
    }

    function getTotal() {
        const amountArray = document.querySelectorAll(".amount");
        const total = document.getElementById("total")

        let sum = 0;
        amountArray.forEach( amount => {
            const item_account = amount.value.replaceAll(',','')
            sum += Number.parseInt(item_account || 0, 10);
        })
        store.set('data', 'amount_total', sum)
        total.value = accountFormat(sum);
    }

    function initDatalist() {
        const commonVoucherList = getDatalist("GET", "/rest/voucher/common")
        commonVoucherList.setDatalist('commonVoucherList', 'voucherName')
        const companyList = getDatalist("POST", "/rest/department/getCompanys")
        companyList.setDatalist('companyList', 'name')
        const applicantList = getDatalist("POST", "/rest/attendance/nameFilter")
        applicantList.setDatalist('applicantList', 'chineseName', 'name')
        const projectNameList = getDatalist("POST", "/rest/workItem/getProjects")
        projectNameList.setDatalist('projectNameList', ['id', 'name'])
        const itemList = getDatalist("GET", "/rest/accounting/itemDrop")
        itemList.setDatalist('itemList', ['iId', 'iName'])
        const customerList = getDatalist("POST", "/rest/project/getCustomer")
        customerList.setDatalist('customerList', 'name')
        customerList.setDatalist('cus_tax_idList', 'ein')

        const getVoucher = (voucherNo, isApply = true, m_token = null) => {
            timeoutCheck();
            const voucher_name = $("#common").val()
            const voucher_no = voucherNo || commonVoucherList.getItem(voucher_name, 'voucherName').voucherNo

            $.ajax({
                type: "GET",
                url: `/rest/voucher/api?voucher_no=\${voucher_no}`,
                contentType: "application/json;charset=UTF-8",
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    if (record.status === "200") {

                        reset();
                        const data = jsonToData(record.data[0], isApply)
                        store.set('status', 'm_token', m_token)
                        const dataKeys = Object.keys(data)
                        dataKeys.forEach(key => {
                            if (key !== 'detail') {
                                $(`[data-head="\${key}"]`).val(
                                    key === 'predict_date' || key === 'credit_date' 
                                        ? dateFormat(data[key]) 
                                    : key === 'head_item' 
                                        ?  `\${itemList.getItem(data[key], 'iId').iId || ''} \${itemList.getItem(data[key], 'iId').iName || ''}`
                                        : data[key]
                                );
                            }
                        })
                        // 套用明細
                        const projectListDOM = document.getElementById('projectList');
                        data.detail.forEach((item, index) => {
                            index !== 0 && addRow('projectList', $("#detailTR").html())
                            const tr = projectListDOM.rows.length - 1;
                            projectListDOM.rows[tr].setAttribute('data-no', item.detail_no);
                            const itemKeys = Object.keys(item)
                            itemKeys.forEach(key => {
                                if (key !== 'detail_no') {
                                    $(`[data-no="\${item.detail_no}"] [data-detail=\${key}]`).val(
                                        key === 'amount' 
                                            ? accountFormat(item[key]) 
                                        : key === 'detail_item' 
                                            ?  `\${itemList.getItem(item[key], 'iId').iId || ''} \${itemList.getItem(item[key], 'iId').iName || ''}`
                                        : key === 'project_name'
                                            ? projectNameList.getItem(item[key], 'name').id + ' ' + projectNameList.getItem(item[key], 'name').name
                                            : item[key]
                                    )
                                }
                            })
                        })
                        getTotal();
                        store.set('status', 'modified', false)
                    } else {
                        confirm('訊息', record.message)
                    }
                },
                error: function () {
                    $("#loading").modal("hide");
                    confirm("訊息", "套用傳票清單失敗");
                },
            });
        }

        return {companyList, applicantList, projectNameList, customerList, itemList, getVoucher}
    }

    function jsonToData(json, isApply = true) {
        delete json.status
        if (isApply) {
            delete json.voucherNo
            delete json.hedgeNoH

            json.detail.forEach((item, index, arr) => {
                arr[index].hedgeNoD = null;
                arr[index].amount = item.amount.toString();
            })
        } else {
            json.hedge_no_h_o = json.hedgeNoH
            json.hedge_no_h_n = json.hedgeNoH

            json.detail.forEach((item, index, arr) => {
                arr[index].hedge_no_d_o = item.hedgeNoD;
                arr[index].hedge_no_d_n = item.hedgeNoD;
                arr[index].amount = item.amount.toString();
            })
        }
        const result = camelCaseToSnakeCase(json)
        store.setData(result)
        if (!json.cusTaxId) {
            $("#noTax").click()
            store.set('data', 'tax_id_type', 'N')
        }
        if (!isApply) {
            store.set('status', 'archived', true)
            voucherHeadLock(true)
        }
        return result
    }
    </script>
</head>
<form name="voucherForm" class="needs-validation d-flex flex-column justify-content-between h-100" autocomplete="off" novalidate>
    <div>
        <div class="form-row align-items-center py-2">
        
            <div class="col-3 order-2 input-group align-items-center mb-2">
                <div class="input-group align-items-center">
                    <label for="company">公司別 : </label>
                    <input 
                        type="text"
                        list="companyList"
                        id="company"
                        class="form-control form-control-sm"
                        data-head="company"
                        value="越世實業"
                        required
                    >
                    <div class="invalid-feedback">
                        公司名稱必須填寫
                    </div>
                    <datalist id="companyList"></datalist>
                </div>
            </div>
        
            <div class="col-9 order-2 mb-2">
                <div class="input-group align-items-center">
                    <label for="common">常用傳票清單 : </label>
                    <input 
                        type="text"
                        list="commonVoucherList"
                        id="common"
                        name="common"
                        class="form-control form-control-sm mr-2"
                        placeholder="請選擇"
                    >
                    <datalist id="commonVoucherList"></datalist>
                    <div class="input-group-append">
                        <button 
                            type="button" 
                            class="btn btn-sm btn--primary"
                            data-btn="getVoucher"
                        >
                            套用
                        </button>
                    </div>
                </div>
            </div>
        
            <div class="col-3 order-2 mb-2">
                <div class="input-group align-items-center">
                    <label for="applicant">申請人 : </label>
                    <input 
                        type="text" 
                        list="applicantList" 
                        id="applicant"
                        class="form-control form-control-sm"
                        data-head="applicant"
                        placeholder="請輸入..."
                        required
                    />
                    <div class="invalid-feedback">
                        申請人必須填寫
                    </div>
                    <datalist id="applicantList"></datalist>
                </div>
            </div>
        
            <div class="col-5 order-2 mb-2">
                <div class="input-group align-items-center">
                    <input id="commonly" data-head="common" type="checkbox" class="mr-1" />
                    <label for="commonly" style="font-size: large; font-weight: bold;">
                        常用
                    </label>
                    <label for="voucher_name">帳目名稱 : </label>
                    <input 
                        type="text" 
                        id="voucher_name" 
                        name="voucher_name" 
                        class="form-control form-control-sm"
                        data-head="voucher_name"
                        placeholder="請輸入..."
                        maxlength="25"
                        required
                    />
                    <div class="invalid-feedback">
                        帳目名稱必須填寫
                    </div>
                </div>
            </div>  
    
            <div class="col-4 order-2 input-group align-items-center mb-2">
                <span class="mr-2">單號 : </span> 
                <input id="voucher_no" class="form-control form-control-sm" data-head="voucher_no" disabled/>
            </div>
        
            <div class="col-3 order-2 d-flex justify-content-center mb-2">
                <div class="d-flex align-items-center">
                    <input 
                        type="radio" 
                        id="useTax" 
                        name="taxId" 
                        value="Y"
                        class="mr-1"
                        data-head="tax_id_type"
                        checked
                    >
                    <label style="font-size: small; flex: 0 0 54px;" for="useTax">
                        使用統編
                    </label>
                </div>
                <div class="d-flex align-items-center">
                    <input 
                        type="radio" 
                        id="noTax" 
                        name="taxId" 
                        value="N" 
                        class="mr-1" 
                        data-head="tax_id_type"
                    >
                    <label style="font-size: small; flex: 0 0 54px;" for="noTax">
                        不需統編
                    </label>
                </div>
            </div>
    
            <div class="col-5 order-2 mb-2">
                <div class="input-group align-items-center">
                    <label for="customer">客戶 : </label>
                    <input 
                        list="customerList" 
                        id="customer" 
                        name="customer" 
                        class="form-control form-control-sm" 
                        data-head="customer"
                        placeholder="請輸入..." 
                        maxlength="20" 
                        required
                    >
                    <div class="invalid-feedback">
                        請輸入客戶名稱
                    </div>
                    <datalist id="customerList"></datalist>
                </div>
            </div>
    
            <div class="col-4 order-2 mb-2">
                <div class="input-group align-items-center">
                    <label for="cus_tax_id">客戶統編 : </label> 
                    <input 
                        type="text" 
                        list="cus_tax_idList" 
                        class="form-control form-control-sm" 
                        data-head="cus_tax_id"
                        id="cus_tax_id" 
                        name="cus_tax_id" 
                        maxlength="8" 
                        minlength="8" 
                        placeholder="請輸入..."
                        required 
                    >
                    <div class="invalid-feedback">
                        請輸入完整統編
                    </div>
                    <datalist id="cus_tax_idList"></datalist>
                </div>
            </div>
            
            <div class="col order-2 mb-2" style="flex: 1 0 250px;">
                <div class="input-group align-items-center">
                    <label for="predict_date">預期收/付日期 : </label>
                    <input 
                        id="predict_date" 
                        type="text" 
                        class="date form-control form-control-sm text-center bg-white"
                        data-head="predict_date"
                        maxlength="10"
                        readonly
                        required
                    />
                    <div class="invalid-feedback">
                        請選擇預期收/付日期
                    </div>
                </div>
            </div>
            
            <div class="col order-2 mb-2" style="flex: 1 0 250px;">
                <div class="input-group align-items-center">
                    <label for="credit_date">入帳日期 : </label>
                    <input 
                        id="credit_date" 
                        type="text" 
                        class="date form-control form-control-sm text-center bg-white"
                        data-head="credit_date"
                        maxlength="10"
                        readonly
                        required
                    />
                    <div class="invalid-feedback">
                        請選擇入帳日期
                    </div>
                </div>
            </div>
        
            <div class="col text-right order-2 mb-2" style="flex: 1 0 340px;">

                <button class="btn btn-sm order-2 btn--primary ml-2" type="button" data-btn="save">
                    存檔
                </button>
            
                <button class="btn btn-sm order-2 btn--info ml-2" type="button" data-btn="signSet">
                    簽程設定
                </button>
                
                <button class="btn btn-sm order-2 btn--info ml-2" style="display: none;" type="button" data-btn="editHead">
                    編輯表頭
                </button>
            
                <button class="btn btn-sm order-2 btn--danger ml-2" type="button" data-btn="reset">
                    清除資料
                </button>
            </div>
        </div>
        <!--id="dataTable"-->
        <div id="detailTable" class="table-responsive mb-3">
            <table class="table table-bordered projectTable mb-0" cellspacing="0">
                <thead>
                    <tr style="background: #E6E6E6;">
                        <th class="no-sort" width="57px"></th>
                        <th class="no-sort text-center" title="專案清單" style="min-width: 165px;">專案清單</th>
                        <th class="no-sort text-center" title="科目" style="min-width: 180px;">科目</th>
                        <th class="no-sort text-center" title="對沖編號" style="min-width: 165px;">對沖編號</th>
                        <th class="no-sort text-center" title="金額" style="min-width: 110px;">金額</th>
                        <th class="no-sort text-center" title="說明" style="min-width: 205px;">說明</th>
                        <th class="no-sort" width="73px"></th>
                    </tr>
                    <tr id="detailTR" class="d-none">
                        <td width="57px">
                            <button 
                                type="button" class="btn btn-transparent p-0" data-btn="add"
                            >
                                <img src="/Style/images/add.png" width="28px" alt="add">
                            </button>
                        </td>
                        <td style="min-width: 165px;">
                            <input 
                                type="text"
                                list="projectNameList" 
                                name="projectName"
                                class="form-control form-control-sm" 
                                data-detail="project_name"
                                placeholder="非專案"
                                maxlength="20" 
                            />
                        </td>
                        <td style="min-width: 180px;">
                            <div class="position-relative">
                                <input
                                    type="text" 
                                    list="itemList"
                                    class="form-control form-control-sm" 
                                    data-detail="detail_item"
                                    placeholder="請輸入..."
                                    maxlength="20"
                                />
                                <div class="invalid-feedback">
                                    請輸入科目
                                </div>
                            </div>
                        </td>
                        <td style="min-width: 165px;">
                            <input 
                                type="text"
                                class="form-control form-control-sm"
                                data-detail="hedge_no_d"
                                maxlength="20"
                            />
                        </td>
                        <td style="min-width: 110px;">
                            <div class="position-relative">
                                <input 
                                    type="text"
                                    class="form-control form-control-sm text-right amount"
                                    data-detail="amount"
                                    maxlength="9"
                                    placeholder="請輸入..."
                                />
                                <div class="invalid-feedback">
                                    請輸入金額
                                </div>
                            </div>
                        </td>
                        <td style="min-width: 205px;">
                            <textarea 
                                rows="1" 
                                class="form-control form-control-sm"
                                data-detail="directions" 
                                maxlength="60"
                            ></textarea>
                        </td>
                        <td width="57px">
                            <button type="button" class="btn btn-transparent p-0" data-btn="del">
                                <img src="/Style/images/del.png" width="28px" alt="delete">
                            </button>
                        </td>
                    </tr>
                </thead>
        
                <tbody id="projectList" class="projectList"></tbody>
            </table>
        
            <datalist id="projectNameList"></datalist>
        </div>
    </div>
    
    <div>
        <div class="form-row justify-content-end">
            <div class="col mb-2" style="flex: 1 0 240px;">
                <div class="input-group align-items-center">
                    <label for="hedge_no_h">對沖編號 : </label>
                    <input 
                        type="text" 
                        id="hedge_no_h"
                        name="hedge_no_h" 
                        class="form-control form-control-sm"
                        data-head="hedge_no_h"
                        maxlength="20"
                    />
                </div>
            </div>
            <div class="col mb-2" style="flex: 1 0 180px;">
                <div class="input-group align-items-center">
                    <label for="total">總金額 : </label>
                    <input 
                        type="text" 
                        id="total" 
                        class="form-control form-control-sm text-right"
                        data-head="amount_total"
                        value="0"
                        disabled
                    />
                </div>
            </div>
            <div class="col mb-2" style="flex: 1 0 240px;">
                <div class="input-group align-items-center">
                    <label for="head_item">小計科目 : </label>
                    <input 
                        list="itemList"
                        id="head_item"
                        name="head_item"
                        class="form-control form-control-sm" 
                        data-head="head_item"
                        placeholder="請輸入..."
                        maxlength="20"
                        required
                    />
                    <datalist id="itemList"></datalist>
                    <div class="invalid-feedback">
                        請輸入小計科目
                    </div>
                </div>
            </div>
            <div class="col mb-2" style="flex: 1 0 180px;">
                <div class="input-group align-items-center">
                    <label for="directions" class="flex-shrink">說明 : </label>
                    <textarea 
                        name="directions" 
                        id="directions" 
                        rows="1" 
                        class="form-control form-control-sm"
                        data-head="directions"
                        maxlength="200"
                    ></textarea>
                </div>
            </div>
        </div>
        <div class="d-flex align-items-center justify-content-end pb-2">
            <button 
                class="btn btn-sm btn--primary ml-2"
                type="button" 
                id="signSend" 
                data-btn="signSend"
            >
                送出
            </button>
            <button 
                class="btn btn-sm btn--danger ml-2" 
                type="button" 
                id="leave" 
                data-btn="leave"
            >
                離開
            </button>
        </div>
    </div>
</form>