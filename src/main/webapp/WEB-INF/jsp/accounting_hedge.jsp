<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <jsp:include page="header.jsp" />
    <!-- Datetimepicker-->
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
    <!-- comfirm button -->
    <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>
    
    <!-- checkLister -->
    <script src="../../Style/custom-component/checkLister/checkLister.js"></script>
    <link rel="stylesheet" href="../../Style/custom-component/checkLister/checkLister.css">

    <!-- custom -->
    <script src="../../Style/js/accounting.js"></script>
    <script type="text/javascript">
        var isFinishLoad = false;
        var account = '<%out.print(session.getAttribute("Account"));%>';
        var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
        var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
        
        $(document).ready(function () {
            {
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
            }       
            
            // Model
            // 1.store
            const store = (function(){
                const state = {
                    item :{
                        MAX_SELECTED_NUM: 10,
                        item_data: null,
                        // item_data_selected: () => checkLister.getSelectedItem(),
                        checkList: null,
                    },
                    hedge: {
                        hedge_data: null
                    }
                }
                const actions = {
                    api_get_hedge: (filter) => {
                        return $.ajax({
                            method: "GET",
                            url: `/rest/accounting/hedge?\${filter}`,
                            dataType: "json",
                            contentType: "application/json",
                            success: (res) => {
                                if(res.status == 200) {
                                    const dataList = res.data.map((item, index) => {
                                        return{
                                            hedgeNo: item.hedgeNo,
                                            hedgeItem: item.hedgeItem,
                                            directions: item.directions,
                                            creditDate: item.creditDate,
                                            amount: item.amount,
                                            selected: true,
                                            id: index
                                        }
                                    });
                                    mutation("set_hedge_data", dataList)
                                }
                            },
                            err: (res) => {},
                        })
                    },
                    api_post_hedge: (data) => {
                        return $.ajax({
                            method: "POST",
                            url: "/rest/accounting/hedge",
                            dataType: "json",
                            headers: { "Content-Type": "application/json" },
                            data: JSON.stringify(data),
                        })
                    },
                    api_get_manager: () => {
                        return $.ajax({
                            url: "/rest/accounting/manager",
                            method: "GET",
                            dataType: "json",
                            success: (res) => {
                                const { status, data } = res;
                                if (status == 200) mutation("set_item_data", data)
                                // renderItemDatalist()
                            }
                        });
                    }
                }
                const mutations = {
                    set_hedge_data: (data)=>{
                        state.hedge.hedge_data = data;
                    },
                    set_hedge_amount: (obj)=>{
                        const { id, amount } = obj;
                        const { hedge_data } = state.hedge
                        hedge_data.forEach(item=>{
                            if(item.id === +id) item.amount = amount
                        })
                    },
                    set_hedge_selected: (obj) =>{
                        const { id, bool} = obj;
                        state.hedge.hedge_data.forEach(item=>{
                            if(item.id === +id) {
                                item.selected = bool
                            }
                        })
                    },
                    set_hedge_all_selected: (bool)=>{
                        state.hedge.hedge_data.forEach(item=>{
                            item.selected = bool;
                        })
                    },
                    set_hedge_iId: (obj)=>{
                        const { id, iId } = obj;
                        const { hedge_data } = state.hedge
                        hedge_data.forEach(item=>{
                            if(item.id === +id) item.iId = iId
                        })
                    },
                    set_item_data: (data) =>state.item.item_data = data,
                    set_item_checkList: (data) => state.item.checkList = data,
                }
                const getters = {
                    //item
                    get_MAX_SELECTED_NUM: () => state.item.MAX_SELECTED_NUM,
                    get_hedge_data: () => getChangeObjInstance(state.hedge.hedge_data),
                    get_hedge_data_post_format: function(){
                        const hedge_data =  this.get_hedge_data();
                
                        if(!hedge_data) return []
                        return hedge_data.reduce((array, item)=>{
                            if(item.selected){
                                array.push({
                                    hedge_no: item.hedgeNo,
                                    hedge_item: item.hedgeItem,
                                    amount: String(item.amount),
                                    directions: item.directions || "",
                                })
                            }
                            return array
                        },[])
                    },
                    get_item_data_oneline: () => {
                        const { item_data } = state.item;
                        return item_data.reduce((data, item)=>{
                            const cName = item.cName;
                            const items =  item.itemList.reduce((total, item)=>{
                                item.cName = cName;
                                total.push(item)
                                return total
                            },[])
                            data.push(...items)
                            return data
                        },[])
                    },
                    get_item_data: () => getChangeObjInstance(state.item.item_data),
                    get_item_data_selected: ()=>  state.item.checkList.getSelectedItem(),
                    get_item_checkList: () => state.item.checkList,
                    get_iName_by_iId: function(iId){
                        const data = this.get_item_data_oneline();
                        let target;
                        data.forEach((item, index) => {
                            if(item.iId == iId) target = item.iName
                        })
                        return target
                    },
                    get_hedge_is_all_checked: function(){
                        let all_checked = true;
                        const data = this.get_hedge_data();
                        data.forEach(item=>{
                            if(!item.selected) all_checked = false
                        })
                        return all_checked
                    },
                    get_hedge_total_amount: () => {
                        return state.hedge.hedge_data.reduce((total, item)=>{
                            if(item.selected) (total += +item.amount)
                            return total
                        },0)
                    },
                    get_item_data_enable: () =>{
                        const data = JSON.parse(JSON.stringify(state.item.item_data))
                        data.forEach(item=>{
                            const length = item.itemList.length;
                            for(let i=length-1;i>=0;i--){
                                if(item.itemList[i].enable === "N"){
                                    item.itemList.splice(i,1)
                                }
                            }
                        })
                        return data
                    }
                }            
                getter = (fn, params = null) => { 
                    if(params) return getters[fn](params)
                    else return getters[fn]()
                } ,
                mutation = (fn, params = null) => {
                    if(params) return mutations[fn](params)
                    else return mutations[fn]()
                },
                action = (fn, params = null) => {
                    if(params) return actions[fn](params)
                    else return actions[fn]()
                }
                return {
                    getter,
                    mutation,
                    action,
                }
            })()
            // 2.transfer format
            const getChangeObjInstance = (obj) => JSON.parse(JSON.stringify(obj))

            // VIEW
            function renderTotalAmount(){
                const total = store.getter("get_hedge_total_amount");
                $("#tableInfos_total").val( accountFormat(total) );
            }
            function renderTable() {
                let dom = "";    
                const data = store.getter("get_hedge_data");
                if(!data) return
                data.forEach((item, index) => {
                    const iName = store.getter("get_iName_by_iId",item.hedgeItem);
                    dom += `
                            <tr>
                                <td>
                                    <input 
                                        type="checkbox" 
                                        \${item.selected ? 'checked' : ''} 
                                        value="\${item.id}"
                                    >
                                </td>
                                <td>
                                    \${item.hedgeNo}
                                </td>
                                <td>
                                    \${item.hedgeItem} \${ iName || '' }
                                </td>
                                <td>
                                    <input 
                                        type="text" 
                                        class="form-control" 
                                        value="\${ accountFormat(item.amount) }"
                                        data-id="\${item.id}" 
                                        data-type="amount"
                                    >
                                </td>
                                <td>
                                    <textarea class="form-control" readonly>\${item.directions || "無對沖說明"}</textarea>
                                </td>
                                <td>
                                    \${item.creditDate}
                                </td>
                            </tr>
                        `;
                });
                $("tbody").html(dom);
                $("#all_check")[0].checked = store.getter("get_hedge_is_all_checked");
                renderTotalAmount();
                bindTableInputAmount()
            }    
            function renderAllCheck(bool){
                const tbodyCheckbox = document.querySelectorAll(
                    'tbody input[type="checkbox"]',
                );
                tbodyCheckbox.forEach((item) => {
                    item.checked = bool;
                });
            }
            function renderItemDatalist(){
                const datalist = document.createElement("datalist");
                datalist.setAttribute("id", "itemList");

                const items = store.getter("get_item_data_oneline");
                const dom = items.reduce((nodes, item)=>{
                    if(item.enable === "Y") nodes += `<option value="\${item.iId}">\${item.iName}</option>`
                   return nodes
                },'')

                datalist.innerHTML = dom;
                $('body').append(datalist)
            }
            function renderCompany(){
                $.ajax({
                    url: '/rest/department/getCompanys',
                    method: 'POST',
                    dataType: "json",
                    contentType: "application/json;charset=utf-8",
                    success: (res)=>{
                        
                        const options = res.reduce((html, item)=>{
                            html+= `<option 
                                        \${item.name === "越世實業" ? 'selected' : ''} 
                                        value="\${item.name}"
                                    >
                                        \${item.name}
                                    </option>`
                            return html
                        },'')
                        $('#search_company').html(options)
                    }

                })
            }
            
            // EVENT
            // Handler
            //1 返回
            $("#leave").on("click", (e) => window.location.href = "../home.do");
            //2 儲存
            $("#store").on("click", handTableSubmit);
            function handTableSubmit(e){
                const data = {
                    data: null,
                    footer: null,
                };

                const selected = store.getter("get_hedge_data_post_format");
                const hasEmpty = selected.find(item=> (item.hedgeItem === ""))
                const hedge_item = $('#tableInfos_item').val().split(' ').slice(0,1)[0]

                if(selected.length === 0) return confirm("訊息", "請勾選一個以上的對沖項目")
                if(hasEmpty) return confirm("訊息", "請填寫對沖項目及金額")
                if(hedge_item === "") return confirm("訊息", "項目未填寫")

                data.data = selected;
                data.footer = {
                    hedge_item,
                    amount: $("#tableInfos_total").val().split(",").join(""),
                    directions: $("#tableInfos_directions").val().trim(),
                };

                store.action("api_post_hedge", data)
                .then(res=>{
                    if (res.status == 200) confirm("訊息", "成功儲存")
                    else confirm("訊息", res.message)
                })
                .catch(err=>{
                    if(res.status != 200) confirm("訊息", err.message)
                })
            
            }
            //3 查詢
            $('.form_search input[type="submit"]').on("click", handSearchSubmit);
            function handSearchSubmit() {
                //日期
                const MAX_DATE_RANGE = 90;
                const date_start = $("#search_date_start").val();
                const date_end = $("#search_date_end").val();
                const dateDiff = getDateDiff(date_start, date_end);

                if (dateDiff > MAX_DATE_RANGE) return confirm("訊息", `時間區間超出\${MAX_DATE_RANGE}天`)
                else if (date_end - date_start < 0) return confirm("訊息", "日期區間不得小於一日")
                
                const filter = {
                    credit_date_start: date_start.split('-').join(''),
                    credit_date_end: date_end.split('-').join(''),
                };

                //對沖編號
                const hedge_no = $("#search_hedge_no").val().trim();
                if (hedge_no) filter.hedge_no = hedge_no;

                //勾選項目
                const item = store.getter("get_item_data_selected");
                if (item.length > 0) filter.item = item.map(item=>item.iId).join(',');

                //公司
                const company = $('#search_company').val().trim()
                if(company) filter.company = company;

                //query
                const keys = Object.keys(filter);
                const data = keys.reduce((q, item, index) => {
                    if (index == 0) q += `\${item}=\${filter[item]}`;
                    else q += `&\${item}=\${filter[item]}`;
                    return q
                }, "");

                store.action("api_get_hedge", data)
                .then((res)=>{
                    if(res.status == 200){
                        renderTable();
                        renderTotalAmount();
                    }else{
                        confirm("訊息", res.message)
                    }
                    
                })
                .catch((err)=>{
                    if(err.status != 200) confirm("訊息", err.message)
                })
            }
            //4 checkbox: all
            $("#all_check").on('change', handAllCheck)
            function handAllCheck(e){
                const bool = e.target.checked;
                renderAllCheck(bool)

                store.mutation("set_hedge_all_selected", bool);
                renderTotalAmount()
            }
            //5 checkbox: single
            $("tbody").on("click", handTBodyCheckbox);
            function handTBodyCheckbox(e){
                if (e.target.nodeName !== "INPUT") return

                const { checked: bool, value: id } = e.target;
                store.mutation("set_hedge_selected", {
                    id,
                    bool
                })
                
                all_check.checked = store.getter("get_hedge_is_all_checked");
                renderTotalAmount();

            }
            //6 table Event
            function bindTableInputAmount(){
                // 1.amount 離焦儲存、number only
                const amount = document.querySelectorAll('tbody input[data-type="amount"]');
                amount.forEach((item) => {
                    item.addEventListener("keyup", (e) => {

                        const { id } = e.target.dataset;
                        let { value } = e.target;

                        value = value.replace(/[^\-?\d]/g, "") || 0
                        if (value.length > 1 && value[0] === '0') {
                            value = value.substring(1)
                        }
                        if (value.length > 10) {
                            value = value.slice(0, -1)
                        }
                        e.target.value = accountFormat(value);

                        store.mutation("set_hedge_amount",{
                            id,
                            amount: value
                        })

                        let timer = null;
                        if (timer) {
                            clearTimeout(timer);
                            timer = null;
                        }
                        timer = setTimeout(renderTotalAmount(), 500);
                    });
                });
                
            
            }  
            // 下方項目 - 聚焦清值 + 補上項目名稱
            $('#tableInfos_item').on('click',(e)=> e.target.value = '');
            $('#tableInfos_item').on('change', (e)=>{
                const value = e.target.value;
                const content = $(`#itemList [value="\${value}"]`)[0]?.textContent;
                if(content) e.target.value = `\${value}  \${content}`
                else e.target.value = '';
            })
            function init(){
                
                renderCompany()
                store.action("api_get_manager")
                .then(res=>{
                    store.mutation("set_item_checkList", useCheckLister({
                        data: store.getter("get_item_data_enable"),
                        checkList:{
                            el: "checkLister_drop",
                            MAX_SELECTED_NUM: store.getter("get_MAX_SELECTED_NUM"),
                            input_placeholder: "輸入「項目編號」或「項目名稱」查詢"
                        },
                        itemArea: {
                            el: "checkLister_itemArea",
                            delIconPath: "../../Style/images/del.png",
                        }
                    }))
                    renderItemDatalist()
                })
                .catch(err=>{
                    confirm("訊息", "下拉選單取得失敗")
                })

                cusDatepicker('search_date_end')
                cusDatepicker('search_date_start', -90)
            }
            init()
        });
    </script>
    <style>
        body {
            display: none;
        }
        /* base */
        .btn {
            cursor: pointer;
        }
        select{
            cursor: pointer;
        }
        label{
            margin: 0;
            padding: 0;
        }
        h2{
            font-weight: 500;
            font-size: 24px;
            padding: 0;
            margin: 0;
        }
        p{
            padding: 0;
            margin: 0;
        }
        a{
            color: #000;
            text-decoration: none;
        }
        a:hover{
            text-decoration: none;
            color: #000;
        }
        ul,ol,li{
            list-style: none;
            margin: 0;
            padding: 0;
        }
        /* table */
        thead th,tbody td{
            text-align: center;
        }
        
        /* form_search */
        #search_date_end,
        #search_date_start{
            background-color: #fff;
        }
        .search{
            display: flex;
            flex-wrap: wrap;
            padding: 20px;
        }
        .search label{
            flex-shrink: 0;
            margin-right: 8px;
        }
        .search .search-item {
            display: flex;
            align-items: center;
            margin-bottom: 16px;
        }
        .search select,
        .search input{
            flex-grow: 1;
        }
        .search .btns{
            margin-left: auto;
            display: flex;
        }
        .search .search-item:nth-child(3) label,
        .search .search-item:nth-child(4) label{
            margin-left: auto;
        }
        /* search line共同設定 */
        #search_company{
            height: 38px;
        }
        .form_search .line{
            width: 100%;
            display: flex;
            flex-wrap: wrap;
        }
        .form_search .line-1 .search-item{
            width: 25%;
        }
        .form_search .line-1 .search-item + .search-item{
            margin-left: 16px;
        }
        .form_search .line-2{
            justify-content: space-between;
        }
        .form_search .line-2 .search-item{
            min-width: 500px;
        }
        .form_search .line-2 .search-item + .search-item{
            margin-left: 16px;
        }
        .search .hasDatepicker {
            cursor: pointer;
        }

        /* form_search dropdown */
        .drop{
            width: 320px;
        }
        .drop-menu {
            background-color: #fff;
            max-width: 70vw;
            margin-top: 8px;
            padding: 8px;
            border: 2px solid #ccc;
            border-radius: 4px;
            display: none;
            flex-wrap: wrap;
            position: absolute;
            z-index: 2000;
        }
        .drop-menu.show {
            display: flex;  
        }
        .drop-menu  h3 {
            text-align: center;
        }
        .drop-item {
            display: flex;
            flex-direction: column;
            padding: 16px;
        }
        .drop-item label {
            padding: 8px 16px 8px 0;
            margin: 0;
        }
        .drop-item :first-child{
            font-size: 24px;
            font-weight: 700;
        }
        /* item_area */
        .clean{
            font-size: 14px;
            color: #555;
        }
        .item_area {
            display: block;
            padding-left: 24px;
            padding-right: 24px;
            padding-bottom: 24px;
        }
        .item_area .header{
            display: flex;
            align-items: end;
            margin-bottom: 8px;
        }
        .item_area .btns{
            margin-left: 8px;
        }
        .item_area .content{
            padding: 15px 20px;
            margin-bottom: 16px;
            background-color: #eee;
            min-height: 41px;
        }
        .item_area .list{
            display: flex;
            flex-wrap: wrap;
        }
        .item_area .list-item{
            position: relative;
            width: 20%;
            padding: 8px;
            cursor: default;
            display: flex;
            align-items: center;
        }

        .item_area .hoverText{
            font-size: 14px;
            visibility: hidden;
            position: absolute;
            bottom: 100%;
            left: 50%;
            display: flex;
            flex-direction: column;
            min-width: 150px;
            max-width: 200px;
            padding: 8px;
            color: #fff;
            border-radius: 4px;
            background-color: #bbb;
        }
        .item_area .hoverText p{
            text-indent: -2rem;
            padding-left: 2rem;
        }
        .item_area .list-item:hover .hoverText{
            visibility: visible;
        }
        .del_icon{
            flex-shrink: 0;
            width: 26px;
            margin-right: 4px;
            display: inline-block;
        }
        .del_icon img{
            display: block;
            width: 100%;
            height: 100%;
        }
        .item_area .infos{
            margin-top: -10px;
        }
        /* table */
        .table td {
            vertical-align: middle;
        }
        thead th:first-child{
            width: 10%;
        }
        /* form_tableInfos */
        .form_tableInfos .form-group{
            width: 30%;
            display: flex;
            align-items: flex-start;
        }
        .form_tableInfos label{
            flex-shrink: 0;
            margin-top: 6px;
            margin-right: 8px;
        }
        /* table btns */
        .table_btns{
            margin-left: auto;
        }
        .table_btns .btn{
            float: right;
            margin: 8px;
        }
    </style>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
    <!-- Navigation-->
    <jsp:include page="navbar.jsp" />
    <div class="content-wrapper">
        <div class="container-fluid">
            <div class="card m-3">
                <div class="card-header">
                    <h1>對沖傳票</h1>
                </div>
                <div class="card-body px-2">
                    <!-- search form -->
                    <form action="javascript:;" class="form_search">
                        <div class="search">
                            <div class="line line-1">
                                <div class="search-item">
                                    <label for="search_company">公司 : </label>
                                    <select id="search_company" class="form-control"></select>
                                    <!-- <input list="company_list" id="search_company" class="form-control" name="search_company" />   
                                    <datalist id="company_list">
                                        <option value="越世實業"/>
                                        <option value="A公司"/>
                                        <option value="B公司"/>
                                    </datalist> -->
                                </div>
                                <div class="search-item">
                                    <label>對沖單號 : </label>
                                    <input type="text" id="search_hedge_no" class="form-control">
                                </div>
                                <div class="search-item" style="flex: 1 0 300px;">
                                    <div class="input-group align-items-center">
                                        <label for="credit_date_f">
                                            入帳日期 :
                                        </label>
                                        <div class="form-control d-flex">
                                            <input 
                                                id="search_date_start" 
                                                type="text" 
                                                class="date text-center w-50 border-0"
                                                readonly
                                            />
                                            <label for="credit_date_t" class="m-0 px-1">
                                                至
                                            </label>
                                            <input 
                                                id="search_date_end" 
                                                type="text" 
                                                class="date text-center w-50 border-0"
                                                readonly
                                            />
                                        </div>
                                    </div>
                                </div>
                                <!-- <div class="search-item">
                                    <label>入帳日期範圍(起) : </label>
                                    <input id="search_date_start" type="text" class="form-control text-center" readonly/> 
                                </div>
                                <div class="search-item">
                                    <label>入帳日期範圍(迄) : </label>
                                    <input id="search_date_end" type="text" class="form-control text-center" readonly/>
                                </div> -->
                               
                            </div>   
                            <div class="line line-2">
                                <div class="form_search">
                                    <div class="search-item" id="checkLister_drop"></div>
                                </div>
                               
                                <div class="btns">
                                    <input type="submit" value="查詢" class="btn btn-primary  ml-auto mt-auto"></input>
                                </div>
                            </div>
                             
                        </div>
                    </form>
                    <!-- item-list area -->
                    <section class="item_area" id="checkLister_itemArea"></section>
                    <!--id="dataTable"-->
                    <table class="table table-bordered" width="100%" cellspacing="0">
                        <thead>
                            <tr bgcolor="#E6E6E6">
                                <th>
                                    <label for="all_check">
                                        <input type="checkbox" id="all_check">
                                        全選
                                    </label>
                                </th>
                                <th>對沖單號</th>
                                <th>對沖項目</th>
                                <th width="20%">金額</th>
                                <th width="30%">說明</th>
                                <th width="10%">入帳日期</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                    <form class="form_tableInfos">
                        <div class="d-flex justify-content-around">
                            <div class="form-group">
                               <label for="tableInfos_item">項目 : </label>
                                <input 
                                    name="tableInfos_item" 
                                    list="itemList" 
                                    id="tableInfos_item" 
                                    class="form-control"
                                    autocomplete="off"  
                                />
                            </div>
                            <div class="form-group">
                                <label for="tableInfos_total">總金額 : </label>
                                <input type="text" value="0" class="form-control" id="tableInfos_total" placeholder="總金額" readonly>
                            </div>
                            <div class="form-group">
                                <label for="tableInfos_directions">說明 : </label>
                                <textarea type="text" class="form-control" id="tableInfos_directions" placeholder="說明"></textarea>
                            </div>
                        </div>
                    </form>
                    <div class="table_btns">
                        <button id="leave" class="btn btn-secondary">
                            離開
                        </button>
                        <button id="store" class="btn btn-primary">
                            儲存
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="footer.jsp" />
        </div>
    </div>
    <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
        data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
        <div class="modal-dialog">
            <img src="../../Style/images/loading.gif" style="padding-top: 12rem;">
        </div>
    </div>
    <jsp:include page="JSfooter.jsp" />
</body>
</html>