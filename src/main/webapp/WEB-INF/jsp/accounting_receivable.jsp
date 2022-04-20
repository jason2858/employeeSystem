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
    <script src="../../Style/js/accounting.js"></script>
    <script src="../../Style/vendor/excel/file-saver/FileSaver.js"></script>
    <script src="../../Style/vendor/excel/xlsx-style/xlsx.full.min.js"></script>
    <script type="text/javascript">
        var isFinishLoad = false;
        var account = '<%out.print(session.getAttribute("Account"));%>';
        var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
        var nameSelect = '<%= session.getAttribute( "nameSelect" ) %>';
        $(document).ready(function(){
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

        })
    </script>
    <script type="module">
    import * as SHEET from '../../Style/vendor/excel/xlsx/xlsx.js';
    import Workbook, { getSheetRowNum, getMonthFromDate, ce2Roc, getLastDate, getTime, get_excel_title_array } from '../../Style/js/module/excel.js';
    $(document).ready(function () {
        //DATA
        const store = (function(){
            const state = {
                excel:{
                    credit_date_start: '',
                    credit_date_end: '',
                    item: ''
                },
                item:{},
                receivable:{
                    receivable_data: [
                        // {
                        //     selected: true,
                        //     index:0,
                        //     predictDate: '20220223(測試用假資料)',
                        //     customer: '廠商名稱（測試用假資料）',
                        //     cusTaxId: '11111（測試用假資料）',
                        //     hedgeNo: '1（測試用假資料）',
                        //     voucherNo: '333333（測試用假資料）',
                        //     amount: '500',
                        //     directions: '測試用假資料',
                        //     expStatus: 'true'
                        // },
                        // {
                        //     selected: true,
                        //     index:1,
                        //     predictDate: '20220223(測試用假資料)',
                        //     customer: '廠商名稱（測試用假資料）',
                        //     cusTaxId: '11111（測試用假資料）',
                        //     hedgeNo: '2（測試用假資料）',
                        //     voucherNo: '333333（測試用假資料）',
                        //     amount: '400',
                        //     directions: '測試用假資料',
                        //     expStatus: 'true'
                        // },
                        // {
                        //     selected: true,
                        //     index:2,
                        //     predictDate: '20220223(測試用假資料)',
                        //     customer: '廠商名稱（測試用假資料）',
                        //     cusTaxId: '11111（測試用假資料）',
                        //     hedgeNo: '3（測試用假資料）',
                        //     voucherNo: '333333（測試用假資料）',
                        //     amount: '300',
                        //     directions: '測試用假資料',
                        //     expStatus: 'true'
                        // },
                        // {
                        //     selected: true,
                        //     index:3,
                        //     predictDate: '20220223(測試用假資料)',
                        //     customer: '廠商名稱（測試用假資料）',
                        //     cusTaxId: '11111（測試用假資料）',
                        //     hedgeNo: '4（測試用假資料）',
                        //     voucherNo: '333333（測試用假資料）',
                        //     amount: '200',
                        //     directions: '測試用假資料',
                        //     expStatus: 'true'
                        // },
                        // {
                        //     selected: true,
                        //     index:4,
                        //     predictDate: '20220223(測試用假資料)',
                        //     customer: '廠商名稱（測試用假資料）',
                        //     cusTaxId: '11111（測試用假資料）',
                        //     hedgeNo: '5（測試用假資料）',
                        //     voucherNo: '333333（測試用假資料）',
                        //     amount: '100',
                        //     directions: '測試用假資料',
                        //     expStatus: 'true'
                        // },
                    ]
                }
            }
            const actions = {
                api_get_manager: () => {
                    return $.ajax({
                        url: "/rest/accounting/manager",
                        method: "GET",
                        dataType: "json",
                        success: (res) => {
                            const { status, data } = res;
                            if (status == 200) {
                                mutation("set_item_data", data)
                            }
                        }
                    });
                },
                api_get_receivable: (query) => {
                    console.log(query);
                    return $.ajax({
                        url: `/rest/accounting/report/receivable?\${query}`,
                        method: "GET",
                        dataType: "json",
                        contentType: "application/json;charset=utf-8",
                        success: (res) => {
                            const { status, data, message } = res;
                            console.log(res);
                            if (status == 200){
                                const map = data.map((item,index)=>{
                                    item.selected = true;
                                    item.index = index
                                    return item
                                })
                                mutation("set_receivable_data", map)
                            }else {
                                // confirm("訊息", message)
                            }
                        }
                    });
                },
            }
            const mutations = {
                //item
                set_item_data: (data) => state.item.item_data = data,
                //receivable
                set_receivable_data: (data) => state.receivable.receivable_data = data,
                set_receivable_all_selected: (bool) => {
                    state.receivable.receivable_data.forEach(item=>{
                        item.selected = bool
                    })
                },
                set_receivable_selected_by_index: (obj={index, bool})=>{
                    const {index, bool} = obj;
                    state.receivable.receivable_data.forEach(item=>{
                        if(item.index === +index) item.selected = bool;
                    })
                },
                //excel
                set_excel_credit_date_start: (data)=> {state.excel.credit_date_start = data},
                set_excel_credit_date_end: (data)=> {state.excel.credit_date_end = data},
                set_excel_item: (data)=> {state.excel.item = data}
            }
            const getters = {
                //item
                get_item_data_enable: () =>{
                    const data = JSON.parse(JSON.stringify(state.item.item_data))
                    data.forEach(item=>{
                        const length = item.itemList.length;
                        for(let i=length-1;i>=0;i--){
                            if(item.itemList[i].enable === "N"){
                                console.log('in delete');
                                item.itemList.splice(i,1)
                            }
                        }
                    })
                    return data
                },
                get_item_data: () => getChangeObjInstance(state.item.item_data),
                get_item_data_selected: ()=> state.receivable.receivable_data.find(item=> item.selected),
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
                //receivable   
                get_receivable_amount: ()=>{
                    return state.receivable.receivable_data.reduce((total, item)=>{
                        if(item.selected) total+= +item.amount
                        return total
                    },0)
                },
                get_receivable_is_all_selected: () => {
                    let isAllSelected = true;
                    state.receivable.receivable_data.forEach(item=>{
                        if(!item.selected) isAllSelected = false
                    })
                    return isAllSelected
                },
                get_receivable_data: ()=> state.receivable.receivable_data,
                get_receivable_data_selected: ()=> state.receivable.receivable_data.filter(item=>item.selected),
                // excel
                get_excel_export_format: function(){
                    const data = this.get_receivable_data_selected();
                    const table = [
                        ['序號','預計付款日','廠商統編','廠商名稱','對沖單號','立帳傳票','應付金額','立帳傳票說明'],
                    ]
                    const total = new Array(8);
                    const approve = new Array(8)
                    const empty = new Array(8)

                    data.forEach((item, index)=>{
                        table.push([
                                index+1, 
                                ce2Roc(item.predictDate) ?? '', 
                                item.cusTaxId ?? '', 
                                item.customer ?? '', 
                                item.hedgeNo ?? '',
                                item.voucherNo ?? '',
                                item.amount ?? '',
                                item.directions ?? ''
                            ])
                    })

                    
                    total[1] = '合計';
                    total[6] = this.get_receivable_amount()
                    approve[1] = '出納 :'
                    approve[3] = '核准 :'
                    approve[7] = '會計 :'
                    
                    table.push(total)
                    table.push(empty)
                    table.push(approve)

                    return table
                },
                get_excel_credit_date_start: ()=> state.excel.credit_date_start,
                get_excel_credit_date_end: ()=> state.excel.credit_date_end,
                get_excel_item: ()=> state.excel.item
            }

            const getter = (fn, params = null) => { 
                if(params) return getters[fn](params)
                else return getters[fn]()
            } 
            const mutation = (fn, params = null) => {
                if(params) return mutations[fn](params)
                else return mutations[fn]()
            }
            const action = (fn, params = null) => {
                if(params) return actions[fn](params)
                else return actions[fn]()
            }
            return {
                getter,
                mutation,
                action,
            }
        })()
        const getChangeObjInstance = (obj) => JSON.parse(JSON.stringify(obj))
        //Event
        //1 table body: click 全選
        //改為用原資料加上 selected 狀態
        const all_check = document.querySelector('#all_check');
        all_check.addEventListener('change',function(e){
            const bool = e.target.checked;
            // 改 table
            const checkboxs = document.querySelectorAll('tbody input[type="checkbox"]');
            checkboxs.forEach(item => item.checked = bool)
            // 改data
            store.mutation('set_receivable_all_selected', bool)
            // 重新算錢
            renderTotalAmount()
        })
        //2. footer: click 離開（上一頁）
        //未來新增離開詢問條件
        $('#leave').on('click',(e)=>{
            window.location.href = '/home.do';
        })
        //3. 搜尋
        $('.form_search input[type="submit"]').on('click',search)
        function search(){

            //日期區間最大 365 天
            const MAX_DATE_RANGE = 365;
            const date1 = $('#search_date_start').val()
            const date2 = $('#search_date_end').val()
            const dateDiff = getDateDiff(date1, date2);
            const iId =  $('#itemList').val().split(' ').slice(0,1)[0]
            const company = $('#search_company').val()

            if(dateDiff > MAX_DATE_RANGE) return confirm('訊息', `日期間隔不得大於 \${MAX_DATE_RANGE} 天`)
            if(date2 - date1 < 0 ) return confirm('訊息','日期區間不得小於一日')
            if(iId === "") return confirm('訊息', '項目編號未填寫')
            if(company === "") return confirm('訊息', '公司未填寫')
            
            const filter = {
                credit_date_start: date1.split('-').join(''),
                credit_date_end: date2.split('-').join(''),
            }

            if(iId) filter.item = iId
            if(company) filter.company = company

            const keys = Object.keys(filter);
            const query = keys.reduce((total, item, index)=>{
                if(index == 0) total += `\${item}=\${filter[item]}`
                else total += ('&' + `\${item}=\${filter[item]}`)

                return total
            },'')
            console.log('query', query);
            console.log('url', `localhost:8080/rest/accounting/report/receivable?\${query}`);
            store.action('api_get_receivable', query)
            .then(res=>{
                const {status, data, message} = res;
                if(status == 200){
                    renderTable();
                    renderTotalAmount()
                    //excel
                    store.mutation('set_excel_credit_date_start', $('#search_date_start').val())
                    store.mutation('set_excel_credit_date_end', $('#search_date_end').val())
                    store.mutation('set_excel_item', $('#itemList').val()) 
                }else{
                    confirm('訊息', message)
                }
            })
            .catch(err=>{
                if(err.status != 200) confirm("訊息", err.message)
            })
            
        }
    
        //1. bind table checkbox
        function bindTBodyCheckbox(){
            const checkbox = document.querySelectorAll('tbody input[type="checkbox"]');
            checkbox.forEach(item=>{
                item.addEventListener('input', handCheckbox)
            })
            // 綁定tbody checkbox
            function handCheckbox(e){
                const checked = e.target.checked;
                const index = e.target.dataset.index
    
                store.mutation('set_receivable_selected_by_index',{
                    index,
                    bool: checked
                })

                const isAllChecked = store.getter('get_receivable_is_all_selected');
                if(isAllChecked) $('#all_check')[0].checked = true;
                else $('#all_check')[0].checked = false;
    
                renderTotalAmount()
            }
            
        }
        //2. itemList
        $('#itemList').on('change', (e)=>{
            const value = e.target.value;
            const content = $(`#item_datalist [value="\${value}"]`)[0]?.textContent;
            if(content) e.target.value = `\${value}  \${content}`
            else e.target.value = '';
        })
        $('#itemList').on('click',(e)=> e.target.value = '');
        //3. export excel
        $('#export_exl').on('click', handExportExcel)
        function handExportExcel(){
            const workbook = new Workbook();
            const credit_date_start = store.getter('get_excel_credit_date_start')
            const credit_date_end = store.getter('get_excel_credit_date_end')
            const accountingName = store.getter('get_excel_item')
            const title = get_excel_title_array(8,{
                formName: '應付帳款核准明細',
                predictDate: '',
                credit_date_end,
                credit_date_start,
                accountingName
            })
            const contain = store.getter('get_excel_export_format')
            const aoa = [
                ...title,
                ...contain
            ]
            const sheet = SHEET.utils.aoa_to_sheet(aoa);
            const contentRowNum = getSheetRowNum(sheet) - 2
            
            
            const indexArray = [];
            console.log('contentRowNum',contentRowNum);
            for(let i = 7 ; i < contentRowNum;i++){
                indexArray.push(`A\${i}`)
            }
            const t_center_arr = ['A1','A2','A6','B6','C6','D6','E6','F6','G6','H6',...indexArray]
            t_center_arr.forEach(item=>{
                console.log('item', item);
                if(sheet[item]?.s?.alignment){
                    sheet[item].s.alignment.horizontal = 'center'
                }else if(sheet[item]?.s && !sheet[item]?.s?.alignment){
                    sheet[item].s.alignment = {
                        horizontal : 'center'
                    }
                }else if(sheet[item]){
                    sheet[item].s = {
                        alignment: {
                            horizontal: 'center'
                        }
                    }
                }else{
                    sheet[item] = {
                        s:{
                            alignment: {
                                horizontal: 'center'
                            }
                        }
                    }
                }
            })
        
            const border_arr = [];
            //charCode 
            for(let i = 65;i<73;i++){
                const char = String.fromCharCode(i);
                for(let j = 6; j < contentRowNum ;j++){
                    border_arr.push(char + j)
                }
            }
            console.log(border_arr);

            border_arr.forEach(item=>{
                if(sheet[item]?.s?.border){
                    sheet[item].s.border = {
                        top:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        bottom:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        left:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        right:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        
                    }
                }else if(sheet[item]?.s){
                    sheet[item].s.border = {
                        top: {
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        bottom:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        left:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                        right:{
                            style: 'medium',
                            color: {
                                rgb: '00000000'
                            },
                        },
                    }
                }else if(sheet[item]){
                    sheet[item].s = {
                        border: { 
                            top: {
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            },
                            bottom:{
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            },
                            left:{
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            },
                            right:{
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            },
                        }
                    }
                }else{
                    sheet[item] = {
                        s: {
                            border: { 
                                top: {
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                },
                                bottom:{
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                },
                                left:{
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                },
                                right:{
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                },
                            }
                        }
                    }
                }
            })
            sheet['!merges'] = [
                //Global
                //首列合併
                {
                    s: 'A1',
                    e: 'H1'
                },
                //次列合併
                {
                    s: 'A2',
                    e: 'H2'
                },
                //帳款日期
                {
                    s: 'A4',
                    e: 'D4'
                },
                //會計科目 列印日期
                {
                    s: 'A5',
                    e: 'D5'
                },
            ]
            sheet['!cols'] = [
                {wch: 5},
                {wch: 16},
                {wch: 16},
                {wch: 16},
                {wch: 16},
                {wch: 16},
                {wch: 16},
                {wch: 30},
            ]

            workbook.appendSheet(sheet, '應付帳款核准明細');
            saveAs(workbook.toBlob(), '應付帳款核准明細.xlsx')
        }
        //VIEWS
        //1 渲染 table
        function renderTable(){
            const data = store.getter('get_receivable_data')
            const dom = data.reduce((html, item)=>{
                return	html+=`
                    <tr>
                        <td>
                            <input 
                                type="checkbox" 
                                data-index="\${item.index}" 
                                \${item.selected ? 'checked' : ''}
                            >		
                        </td>
                        <td>\${item.predictDate ?? ''}</td>
                        <td>\${item.cusTaxId ?? ''}</td>
                        <td>\${item.customer ?? ''}</td>
                        <td>\${item.hedgeNo ?? ''}</td>
                        <td>\${item.voucherNo}</td>
                        <td style="text-align: right;">\${accountFormat(item.amount) ?? ''}</td>
                        <td>\${item.directions ?? ''}</td>
                    </tr>`
            },'');
            //下周待修
            $('tbody').html(dom);
            $('#all_check')[0].checked = store.getter('get_receivable_is_all_selected')
            //等伺服器好來看
            renderTotalAmount()
            bindTBodyCheckbox()
        }
        
        //2 item drop
        function renderItemDatalist(){
            const datalist = document.createElement("datalist");
            datalist.setAttribute("id", "item_datalist");

            const items = store.getter("get_item_data_oneline");
            const dom = items.reduce((nodes, item)=>{
                if(item.enable === "Y") nodes += `<option value="\${item.iId}">\${item.iName}</option>`
                return nodes
            },'')

            datalist.innerHTML = dom;
            $('body').append(datalist)
        }
        function renderTotalAmount(){
            const total = store.getter('get_receivable_amount')
            $('#tableInfos_total').val( accountFormat(total) )
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
        function getCompany() {
            var companyDOM = $("#search_company");
            $.ajax({
                type: "post",
                url: "/rest/department/getCompanys",
                datatype: "text",
                success: function (data) {
                    const record = JSON.parse(data);
                    let companyOption = '';
                    record.forEach(company => {
                        companyOption += `
                            <option 
                                value=\${company.id} 
                                id="\${company.name === '越世實業' ? 'defaultCompany' : ''}"
                                \${company.name === '越世實業' ? 'selected' : ''}
                            >
                                \${company.name}
                            </option>
                        `
                    })
                    companyDOM.append(companyOption)
                },
                error: function () {
                    confirm("訊息", "取得公司別資料失敗");
                },
            });
        }
         
        //init render
        (function init(){
            // render company to #company select
            const companyList = getDatalist("POST", "/rest/department/getCompanys")
            companyList.setDatalist('companyList', 'name')

            cusDatepicker('search_date_start',-90)
            cusDatepicker('search_date_end',)
            
            renderCompany()
            //excel
            store.mutation('set_excel_credit_date_start', $('#search_date_start').val())
            store.mutation('set_excel_credit_date_end', $('#search_date_end').val())
            store.mutation('set_excel_item', $('#itemList').val()) 

            store.action('api_get_manager')
                .then(res=> {
                    if(res.status == 200) renderItemDatalist()
                    else confirm("訊息", res.message)
                })
                .catch(err=> {
                    if(err.status != 200) confirm("訊息", err.message)
                })

        })()
    })   
    </script>
    <style>
         body {
            display: none;
            /*全部先隱藏*/
        }
        /* base config */
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
        #search_company{
            height: 38px;
        }
        .form_search{
            padding: 20px ;
        }
        #search_date_end,
        #search_date_start{
            background-color: #fff;
        }
        .search{
            display: flex;
            flex-wrap: wrap;
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
        }
        .search .search-item:nth-child(3) label,
        .search .search-item:nth-child(4) label{
            margin-left: auto;
        }
        /* search line共同設定 */
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
            width: 20%;
        }
        .form_search .line-2 .search-item + .search-item{
            margin-left: 16px;
        }
        .search .hasDatepicker {
            cursor: pointer;
        }
        /* table */
        thead th:first-child{
            width: 10%;
        }
        /* form_tableInfos */
        .form_tableInfos .form-group{
            width: 25%;
            display: flex;
            align-items: center;
            margin-left: auto;
            margin-right: 19%;
        }
        .form_tableInfos label{
            flex-shrink: 0;
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
                    <h1>應付帳款核准明細</h1>
                </div>
                <div class="card-body px-2">
                    <!-- search form -->
                    <form action="javascript:;" class="form_search">
                        <div class="search">
                            <div class="line line-1">
                                <div class="search-item">
                                    <label for="company">公司 :</label>
                                    <select id="search_company" class="form-control">
                                        <option value="越世實業">越世實業</option>
                                    </select>
                                    <!-- <input 
                                        type="text"
                                        list="companyList"
                                        id="search_company"
                                        class="form-control"
                                        data-head="company"
                                        value="越世實業"
                                    >
                                    <datalist id="companyList"></datalist> -->
                                </div>
                                <div class="search-item">
                                    <label for="tableInfos_item">項目 : </label>
                                    <input 
                                        name="itemList" 
                                        list="item_datalist" 
                                        id="itemList" 
                                        class="form-control"
                                        placeholder="請輸入項目編號"
                                        maxlength="4"
                                        autocomplete="off"
                                    />
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
                                <div class="btns ml-auto">
                                    <input type="submit" value="查詢" class="btn btn-primary"></input>
                                    <button id="export_exl" class="btn btn-info ml-3">匯出</button>
                                </div>
                            </div>                         
                        </div>   
                    </form>
                    <!-- delete item_area -->
                    <!--id="dataTable"-->
                    <table class="table table-bordered" width="100%" cellspacing="0">
                        <thead>
                            <tr bgcolor="#E6E6E6">
                                <th width="5%">
                                    <label for="all_check">
                                        <input type="checkbox" id="all_check">
                                        全選
                                    </label>
                                </th>
                                <th>預計付款日</th>
                                <th>廠商統編</th>
                                <th>廠商名稱</th>
                                <th>對沖單號</th>
                                <th>立帳傳票</th>
                                <th>應付金額</th>
                                <th>立帳傳票說明</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- <tr>
                                <td>
                                    <input type="checkbox">
                                </td>
                                <td>預計付款日</td>
                                <td>廠商統編</td>
                                <td>廠商名稱</td>
                                <td>對沖單號</td>
                                <td>立帳傳票</td>
                                <td>應付金額</td>
                                <td>立帳傳票說明</td>
                            </tr> -->
                        </tbody>
                    </table>
                    <form class="form_tableInfos">
                        <div class="form-group">
                            <label for="tableInfos_total">總金額 : </label>
                            <input type="text" class="form-control" id="tableInfos_total" placeholder="0" readonly>
                        </div>
                    </form>
                    <div class="table_btns">
                        <button id="leave" class="btn btn-secondary">
                            離開
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="footer.jsp" />
        <!-- Scroll to Top Button-->
        <!--<a class="scroll-to-top rounded" href="#page-top"> <i class="fa fa-angle-up"></i>
            </a>-->
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