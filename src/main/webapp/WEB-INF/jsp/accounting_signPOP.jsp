<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%-- 傳單簽程設定 POP --%>
<head>
    <script type="text/javascript" defer>
    function initSignModal({applicantList}){
        function signCommonList() {
            $.ajax({
                type: "GET",
                url: "/rest/accounting/signCommon",
                contentType: "application/json;charset=UTF-8",
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    let options = ''
                    if (record.status === '200'){
                        const { common } = record.data
                        common.forEach( sign => {
                            options += `
                                <option>\${sign.signName}</option>
                            `
                        })
                        $("#signSetList").append(options)
                    }
                },
                error: function () {
                    confirm("訊息", "取得常用簽程下拉選單失敗");
                },
            });
        }
        signCommonList()

        const roleList = getDatalist("GET", "/rest/accounting/sign/role")
        roleList.setDatalist('roleList', 'role')

        const signSetModal = document.getElementById('signSetModal');
        const signCreateModal = document.getElementById('signCreateModal');
        signSetModal.addEventListener('click', btnEvent)
        signCreateModal.addEventListener('click', btnEvent)

        function btnEvent(e) {
            const { btn, type } = e.target.dataset;
            if (!btn) return

            const trDOM = e.target.parentNode.parentNode;
            const index = trDOM.rowIndex - 2
            switch (btn) {
                case 'openSignCreateModal':
                    openSignCreateModal()
                    break;
                case 'commonSignSet':
                    commonSignSet(e)
                    break;
                case 'add':
                    addRow(`\${type}_PersonList`, $(`#\${type}TR`).html())
                    $(`#\${type}Table`)[0].scrollTo({
                        top: $(`#\${type}Table`)[0].scrollHeight, 
                        left: 0, 
                        behavior: 'smooth'
                    })
                    break;
                case 'del':
                    delRow(`\${type}_PersonList`, index)
                    break;
                case 'submit':
                    voucherSignSet(e)
                    break;
                case 'leave':
                    leaveSignModal(type)
                    break;
                case 'signCreate':
                    signCreate()
                    break;
                default:
            }
        }

        /*
         * sign API 設置簽程
         */
        function voucherSignSet(e) {
            const tbodyDOM = document.getElementById('signSet_PersonList')

            const saveData = []
            for (let tr = 0; tr < tbodyDOM.rows.length; tr++) {
                const role = tbodyDOM.rows[tr].cells[1].children[0].value.trim(),
                    signUser = tbodyDOM.rows[tr].cells[2].children[0].value.trim();
                if (!roleList.getItem(role, 'role')) {
                    confirm('訊息', '請選擇簽核角色')
                }
                const role_id = roleList.getItem(role, 'role').roleId,
                    sign_user = applicantList.getItem(signUser, 'chineseName', 'name').name || signUser.trim()
                if (role_id && sign_user) {
                    const signSetData = {role_id, sign_user}
                    saveData.push(signSetData)
                }
            }
            if (saveData.length < 1) return confirm('訊息', '至少要有一個簽程人員');
            
            const params = {
                voucher_no: $("#sign_fee_no").val(),
                set: saveData
            }
            $.ajax({
                type: "POST",
                url: "/rest/accounting/sign",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(params),
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    if (record.status === '200') {
                        store.set('status', 'isSignSave', true);
                        leaveSignModal('signSet')
                    }
                },
                error: function () {
                    confirm("訊息", "新增傳單簽程失敗");
                },
            });
        }
    
        /*
         * signCommon API
         */
        
        /* 新增常用簽程 POST /rest/accounting/signCommon */
        function signCreate(e) {
            $.confirm({
                title: "提醒",
                content: "請問確定要新增常用簽程嗎？",
                draggable: false,
                buttons: {
                    '確認': function() {
                        const tbodyDOM = document.getElementById('signCreate_PersonList')
                        const saveData = []
                        for (let tr = 0; tr < tbodyDOM.rows.length; tr++) {
                            const role = tbodyDOM.rows[tr].cells[1].children[0].value.trim(),
                                signUser = tbodyDOM.rows[tr].cells[2].children[0].value.trim();
                            if (!roleList.getItem(role, 'role')) {
                                confirm('訊息', '請選擇簽核角色')
                            }
                            const role_id = roleList.getItem(role, 'role').roleId,
                                sign_user = applicantList.getItem(signUser, 'chineseName', 'name').name || signUser.trim();
                            if (role_id && sign_user) {
                                const signSetData = {role_id, sign_user}
                                saveData.push(signSetData)
                            }
                        }
                        if (saveData.length < 1) return confirm('訊息', '至少要有一個簽程人員');
    
                        if (!$("#signCreate_name").val().trim()) {
                            confirm('訊息', '請填寫簽程名稱')
                        }
                        
                        const params = {
                            sign_name: $("#signCreate_name").val(),
                            set: saveData
                        }
    
                        $.ajax({
                            type: "POST",
                            url: "/rest/accounting/signCommon",
                            data: JSON.stringify(params),
                            contentType: "application/json;charset=UTF-8",
                            datatype: "json",
                            success: function (data) {
                                const record = JSON.parse(data)
                                if (record.status === '200') {
                                    leaveSignModal('signCreate');
                                    $("#signSetList").empty()
                                    signCommonList()
                                }
                            },
                            error: function () {
                                confirm("訊息", "新增常用簽程失敗");
                            },
                        });
                    },
                    '取消': function() {}
                }
            })
        }

        /* 套用常用簽程 GET /rest/accounting/signCommon/set */
        function commonSignSet() {
            const sign_name = $("#signSet_name").val().trim();
            if (!sign_name) { confirm('訊息', '請輸入簽程名稱') }
            

            $.ajax({
                type: "GET",
                url: "/rest/accounting/signCommon/set?sign_name=" + sign_name,
                contentType: "application/json;charset=UTF-8",
                datatype: "json",
                success: function (data) {
                    const record = JSON.parse(data)
                    if (record.status === "200") {
                        const { common } = record.data;
                        
                        $("#signSet_PersonList").empty();

                        // 套用取得簽程設定資料
                        const signSetDOM = document.getElementById("signSet_PersonList");
                        common.forEach( signPerson => {
                            addRow("signSet_PersonList", $("#signSetTR").html());
                            const rowCount = signSetDOM.rows.length;
                            const rowDOM = signSetDOM.rows[rowCount - 1];
                            const {role, signUser} = signPerson
                            rowDOM.cells[1].children[0].value = role;
                            rowDOM.cells[2].children[0].value = signUser;
                        })
                    }
                },
                error: function () {
                    confirm("訊息", "套用常用簽程失敗");
                },
            });
        }

        function leaveSignModal(signType) {
            
            $(`#\${signType}_PersonList`).empty();
            $(`#\${signType}Modal`).modal('hide');

            if (signType === 'signCreate') {
                $("#signCreate_name").val('')
                $("#signSetModal").modal('show')
            }
        }

        function openSignCreateModal() {
            $("#signSetModal").modal('hide')
            $("#signCreateModal").modal('show')
            addRow("signCreate_PersonList", $('#signCreateTR').html());
        }
    }
    </script>
</head>

<div class="modal fade" id="signSetModal" tabindex="-1" role="dialog" aria-labelledby="signSetModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header" style="background: rgba(0, 0, 0, .03)">
                <h5 class="modal-title" id="signSetModalLabel">簽核套用</h5>
            </div>
            <div class="modal-body">
                <form  autocomplete="off">

                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div class="input-group align-items-center w-50">
                            <label for="sign_fee_no">單號 : </label>
                            <input id="sign_fee_no" class="form-control form-control-sm" disabled/>
                        </div>
    
                        <button type="button" class="btn btn--info w-25" data-btn="openSignCreateModal">
                            新增簽程
                        </button>
                    </div>
    
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div class="input-group align-items-center w-50">
                            <label for="signSet_name">簽程 : </label>
                            <input 
                                type="text"
                                list="signSetList" 
                                id="signSet_name"
                                name="signSet_name"
                                class="form-control form-control-sm" 
                                placeholder="請輸入..." 
                                maxlength="20"
                            />
                            <datalist id="signSetList"></datalist>
                        </div>
    
                        <button type="button" class="btn btn--primary w-25" data-btn="commonSignSet">
                            套用
                        </button>
                    </div>
    
                    <!-- 共用簽程角色 datalist -->
                    <datalist id="roleList"></datalist>
    
                    <div id="signSetTable" class="table-responsive">
                        <table class="table table-bordered sign_PersonTable" cellspacing="0">
                            <thead>
                                <tr style="background: #E6E6E6;">
                                    <th class="no-sort" width="57px"></th>
                                    <th class="no-sort text-center" title="簽核角色">簽核角色</th>
                                    <th class="no-sort text-center" title="簽核人員">簽核人員</th>
                                    <th class="no-sort" width="73px"></th>
                                </tr>
                                <tr id="signSetTR" class="d-none">
                                    <td>
                                        <button type="button" class="btn btn-transparent p-0" data-type="signSet" data-btn="add">
                                            <img src="/Style/images/add.png" alt="add">
                                        </button>
                                    </td>
                                    
                                    <td>
                                        <input 
                                            list="roleList" 
                                            name="signSet_lv"
                                            class="form-control form-control-sm" 
                                            placeholder="請輸入..." 
                                        />
                                    </td>
    
                                    <td>
                                        <input 
                                            list="applicantList"
                                            name="signSet_user"
                                            class="form-control form-control-sm" 
                                            placeholder="請輸入..."
                                        />
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-transparent p-0" data-type="signSet" data-btn="del">
                                            <img src="/Style/images/del.png" alt="del">
                                        </button>
                                    </td>
                                </tr>
                            </thead>
    
                            <tbody id="signSet_PersonList" class="sign_PersonList"></tbody>
                        </table>
                    </div>
                    
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn--primary" data-btn="submit">
                    確認
                </button>
                <button type="button" class="btn btn--danger ml-2" data-type="signSet" data-btn="leave">
                    離開
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="signCreateModal" tabindex="-1" role="dialog" aria-labelledby="signCreateModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header" style="background: rgba(0, 0, 0, .03)">
                <h5 class="modal-title" id="signCreateModalLabel">新增簽核</h5>
            </div>
            <div class="modal-body">
                <form  autocomplete="off">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div class="input-group align-items-center w-50">
                            <label for="signCreate_name">簽程名稱 : </label>
                            <input id="signCreate_name" class="form-control form-control-sm" />
                        </div>
    
                        <button type="button" class="btn btn--info w-25" data-btn="signCreate">
                            新增
                        </button>
                    </div>
    
                    <div class="d-flex justify-content-end mb-2">
                        <button type="button" class="btn btn--danger w-25" data-type="signCreate" data-btn="leave">
                            離開
                        </button>
                    </div>
    
                    <div id="signCreateTable" class="table-responsive">
                        <table class="table table-bordered sign_PersonTable" cellspacing="0">
                            <thead>
                                <tr style="background: #E6E6E6;">
                                    <th class="no-sort" width="57px"></th>
                                    <th class="no-sort" title="簽核角色">簽核角色</th>
                                    <th class="no-sort" title="簽核人員">簽核人員</th>
                                    <th class="no-sort" width="73px"></th>
                                </tr>
                                <tr id="signCreateTR" class="d-none">
                                    <td>
                                        <button type="button" class="btn btn-transparent p-0" data-type="signCreate" data-btn="add">
                                            <img src="/Style/images/add.png" alt="add">
                                        </button>
                                    </td>
                                    
                                    <td>
                                        <input 
                                            list="roleList" 
                                            name="signCreate_lv"
                                            class="form-control form-control-sm" 
                                            placeholder="請輸入..." 
                                        />
                                    </td>
    
                                    <td>
                                        <input 
                                            list="applicantList"
                                            name="signCreate_user"
                                            class="form-control form-control-sm" 
                                            placeholder="請輸入..."
                                        />
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-transparent p-0" data-type="signCreate" data-btn="del">
                                            <img src="/Style/images/del.png" alt="del">
                                        </button>
                                    </td>
                                </tr>
                            </thead>
    
                            <tbody id="signCreate_PersonList" class="sign_PersonList"></tbody>
                        </table>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>