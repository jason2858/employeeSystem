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
                /*全部先隱藏*/
            }
            label {
                margin: 0 8px 0 0;
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
            /* datePicker */
            #ui-datepicker-div .ui-datepicker-calendar{
                display: none;
            }
            #ui-datepicker-div.open .ui-datepicker-calendar{
                display: table;
            }
            /* table */
            /* .predict-table-d-none .ui-datepicker-calendar{
                display: none;
            } */
            thead th,tbody td{
                text-align: center;
            }
            .table thead th{
              vertical-align: middle;
            }
            
            /* form_search */
            .form_search{
                padding: 20px ;
            }
            /* #search_date_end,
            #search_date_start{
                background-color: #fff;
            } */
            #search_company{
                height: 38px;
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
                background-color: #fff;
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
        <script src="../../Style/js/accounting.js?id=socoolwoowowo"></script>
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
                //model
                //1 store
                const store = (function () {
                    const state = {
                        //紀錄匯出 excel 資訊
                        excel:{
                            credit_date_start: '',
                            credit_date_end: '',
                            predictDate: '',
                            item: ''
                        },
                        month: [],
                        item:{},
                        balance:{
                        balance_data:[
                            {
                                selected: true,
                                index:0,
                                predictDate: '20220322',
                                customer: '',
                                cusTaxId: '88881111',
                                creditDate:'測試用假資料',
                                voucherNo: '1101005001',
                                amount: '1000 ',
                                directions: '大明(股)公司-10/22應收款'
                            },
                            {
                                selected: true,
                                index:1,
                                predictDate: '20220131',
                                customer: '',
                                cusTaxId: '11118888',
                                creditDate:'測試用假資料',
                                voucherNo: '1101008001',
                                amount: '2000',
                                directions: '大明(股)公司-10/22應收款'
                            },
                            {
                                selected: true,
                                index:2,
                                predictDate: '20220101',
                                customer: '越世資安',
                                cusTaxId: '83263725',
                                creditDate:'測試用假資料',
                                voucherNo: '測試用假資料',
                                amount: '10000',
                                directions: '110/11應收款'
                            },
                            {
                                selected: false,
                                index:3,
                                predictDate: '20220231',
                                customer: '越世資安',
                                cusTaxId: '83263725',
                                creditDate:'測試用假資料',
                                voucherNo: '1101210001',
                                amount: '30000',
                                directions: '110/12應收款'
                            },
                            {
                                selected: false,
                                index: 4,
                                predictDate: '20221231',
                                customer: '測試用假資料',
                                cusTaxId: '測試用假資料',
                                creditDate:'測試用假資料',
                                voucherNo: '測試用假資料',
                                amount: '40',
                                directions: '不應該顯示這一筆，selected為false'
                            },
                        ],
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
                                    if (status == 200) mutation("set_item_data", data)
                                }
                            });
                        },
                        api_get_balance: (query) => {
                            return $.ajax({
                                url: `/rest/accounting/report/balance?\${query}`,
                                method: "GET",
                                dataType: "json",
                                contentType: "application/json;charset=utf-8",
                                success: (res) => {
                                    const { status, data, message } = res;
                                    if (status == 200){
                                        const map = data.map((item,index)=>{
                                            item.selected = true;
                                            item.index = index
                                            return item
                                        })
                                        mutation("set_balance_data", map)
                                    }
                                }
                            });
                        },
                    }
                    const mutations = {
                        //item
                        set_item_data: (data) => state.item.item_data = data,
                        //balance
                        set_balance_data: (data) => state.balance.balance_data = data,
                        set_balance_all_selected: (bool) => {
                            state.balance.balance_data.forEach(item=>{
                                item.selected = bool
                            })
                        },
                        set_balance_selected_by_index: (obj={index, bool})=>{
                            const {index, bool} = obj;
                            state.balance.balance_data.forEach(item=>{
                                if(item.index === +index) item.selected = bool;
                            })
                        },
                        //month
                        set_month: (data)=> state.month = data,
                        //excel
                        set_excel_credit_date_start: (data)=> {state.excel.credit_date_start = data},
                        set_excel_credit_date_end: (data)=> {state.excel.credit_date_end = data},
                        set_excel_predictDate: (data)=> {state.excel.predictDate = data},
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
                                        item.itemList.splice(i,1)
                                    }
                                }
                            })
                            console.log('final data', data);
                            return data
                        },
                        get_item_data: () => getChangeObjInstance(state.item.item_data),
                        get_item_data_selected: ()=> state.balance.balance_data.find(item=> item.selected),
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
                        //balance   
                        get_balance_amount: ()=>{
                            return state.balance.balance_data.reduce((total, item)=>{
                                if(item.selected) total+= +item.amount
                                return total
                            },0)
                        },
                        get_balance_is_all_selected: () => {
                            const find = state.balance.balance_data.find(item=> item.selected === false)
                            const allSelected =  find ? false : true
                            return allSelected
                        },
                        get_balance_data: ()=> state.balance.balance_data,
                        get_balance_data_selected: ()=> state.balance.balance_data.filter(item=>item.selected),
                        //month
                        get_month: ()=> getChangeObjInstance(state.month),
                        // excel
                        get_excel_export_format: function(){
                            const data = this.get_balance_data_selected();
                            const tableMonth = $('thead [month]');
                            const table = [
                                ['序號','預計收款日','廠商統編','廠商名稱','立帳傳票','30天以內','31-60天','61-90天','90天以上','餘額','立帳傳票說明'],
                                [,,,,,tableMonth[0].textContent,tableMonth[1].textContent,tableMonth[2].textContent,tableMonth[3].textContent,,]
                            ]

                            const months = store.getter("get_month")
                           data.forEach((item, index)=>{
                               const itemMonth = +getMonthFromDate(item.predictDate)
                            table.push([
                                    index+1, 
                                    ce2Roc(item.predictDate) ?? '', 
                                    item.cusTaxId ?? '', 
                                    item.customer ?? '', 
                                    item.voucherNo ?? '',
                                    itemMonth == months[0]
                                        ? item.amount
                                        : '',
                                    itemMonth == months[1]
                                        ? item.amount
                                        : '',
                                    itemMonth == months[2]
                                        ? item.amount
                                        : '',
                                    months.includes(itemMonth)
                                        ? ''
                                        : item.amount,
                                    item.amount ?? '',
                                    item.directions ?? ''
                                ])
                           })

                           const total = new Array(11);
                           total[1] = '合計';
                           total[9] = this.get_balance_amount()
                           table.push(total)

                           return table
                        },
                        get_excel_credit_date_start: ()=> state.excel.credit_date_start,
                        get_excel_credit_date_end: ()=> state.excel.credit_date_end,
                        get_excel_predictDate: ()=> state.excel.predictDate,
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
                //2 instance
                const getChangeObjInstance = (obj) => JSON.parse(JSON.stringify(obj))
                //3 month 從帳齡截止推前四個月
                function getMonthData(){
                    let predict = $('#search_predict_date_end').val().split('-')[1]
                    predict = predict.slice(0,1) === '0'
                              ? +predict.slice(1)
                              : +predict
                    const array = [];
                    console.log('predict', predict);
                    for(let i =0; i<4 ; i++){
                        array.push(predict);
                        predict = predict - 1 > 0
                                ? predict - 1
                                : 12
                    }
                    console.log('array', array);
                    return array
                }
                
                //VIEW
                //1 datalist
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
                //2 table
                //2-1 head
                function renderTHeadMonth(){
                    const months = document.querySelectorAll('thead th[month]')
                    const data_month = store.getter('get_month')
                    
                    months.forEach((item, index)=>{
                        if(index == 3)  item.textContent = data_month[index] + '月以前'
                        else item.textContent = data_month[index] + '月'
                    })
                }
                //2-2 body
                function renderTable(){
                    const data = store.getter('get_balance_data')
                    const months = store.getter('get_month').slice(0,3)
                    console.log('months', months);
                    const dom = data.reduce((html, item)=>{
                        const itemMonth = +getMonthFromDate(item.predictDate)
                        console.log('itemMonth', itemMonth);
                        return	html+=`
                            <tr>
                                <td>
                                    <input 
                                        type="checkbox" 
                                        data-index="\${item.index}" 
                                        \${item.selected ? 'checked' : ''}
                                    >		
                                </td>
                                <td>\${item.predictDate || ''}</td>
                                <td>\${item.cusTaxId || ''}</td>
                                <td>\${item.customer || ''}</td>
                                <td>\${item.voucherNo}</td>
                                <td>
                                    \${
                                        itemMonth == months[0]
                                        ? item.amount
                                        : ''
                                    }
                                </td>
                                <td>
                                    \${
                                        itemMonth == months[1]
                                        ? item.amount
                                        : ''
                                    }
                                </td>
                                <td>
                                    \${
                                        itemMonth == months[2]
                                        ? item.amount
                                        : ''
                                    }
                                </td>
                                <td>
                                    \${
                                        months.includes(itemMonth)
                                        ? ''
                                        : item.amount
                                    }
                                </td>
                                <td>\${item.amount}</td>
                                <td>\${item.directions || ''}</td>
                            </tr>`
                    },'');

                    //VIEW
                    $('tbody').html(dom);
                    renderTotalAmount()
                    $('#all_check')[0].checked = store.getter('get_balance_is_all_selected')
                    //EVENT
                    bindTBodyCheckbox()
                }

                //3 total
                function renderTotalAmount(){
                    const total = store.getter('get_balance_amount')
                    $('#tableInfos_total').val(total)
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
            
                
                //EVENT
                //0
                // $('#test').on('click', renderTable)
                //1. tbody checkbox
                function bindTBodyCheckbox(){
                    const checkbox = document.querySelectorAll('tbody input[type="checkbox"]');
                    checkbox.forEach(item=>{
                        item.addEventListener('input', handCheckbox)
                    })
                    // 綁定tbody checkbox
                    function handCheckbox(e){
                        const checked = e.target.checked;
                        const index = e.target.dataset.index
            
                        console.log('index', index);
                        console.log('store.state...', store.getter('get_balance_data'));
                        store.mutation('set_balance_selected_by_index',{
                            index,
                            bool: checked
                        })
                        const isAllChecked = store.getter('get_balance_is_all_selected');
                        if(isAllChecked) $('#all_check')[0].checked = true;
                        else $('#all_check')[0].checked = false;
                        
                        renderTotalAmount()
                    }
                
                }
                //2. leave
                $('#leave').on('click',handLeave)
                function handLeave(){
                    window.location.href = '/home.do'
                }
                //3. export excel
                $('#export_exl').on('click', handExportExcel)
                // function get_excel_title_array(length, obj = {
                //     formName: '',
                //     predictDate: '',
                //     credit_date_start: '',
                //     credit_date_end: '',
                //     accountingName: '',
                // }){
                //     if(isNaN(length)) return
                //     length = +length
                //     const {
                //         formName,
                //         predictDate,
                //         credit_date_start,
                //         credit_date_end,
                //         accountingName,
                //     } = obj
                //     const now = dateFormat();
                //     const array = new Array(5); //表頭五列
                //     for(let i = 0;i<5;i++){
                //         array[i] = new Array(length)
                //     }


                //     array[0][0] = '越世實業股份有限公司';
                //     array[1][0] = formName;
                //     array[3][0] = `帳款日期: \${credit_date_start} - \${credit_date_end}`
                //     array[3][length -1] = `帳齡截止日：\${predictDate}`
                //     array[4][0] = `會計科目：\${accountingName}`;
                //     array[4][length-1] = `列印時間：\${ce2Roc(dateFormat())}  \${getTime()}`

                //     return array
                // }
                
               
                function handExportExcel(){
                    const workbook = new Workbook();
                    const predictDate = store.getter('get_excel_predictDate')
                    const credit_date_start = store.getter('get_excel_credit_date_start')
                    const credit_date_end = store.getter('get_excel_credit_date_end')
                    const accountingName = store.getter('get_excel_item')

                    const title = get_excel_title_array(11,{
                        formName: '應收帳款帳齡分析表',
                        predictDate: predictDate,
                        credit_date_start: credit_date_start,
                        credit_date_end: credit_date_end,
                        accountingName
                    });
                    const contain = store.getter('get_excel_export_format')
                    const aoa = [
                        ...title,
                        ...contain
                    ]
                    const sheet = SHEET.utils.aoa_to_sheet(aoa);
                    const rowNum = getSheetRowNum(sheet);
                    console.log('SHEET', sheet);

                    /*
                    //border
                    //top
                    const border_top_arr = [];
                    //65+11 = 76
                    for(let i = 65; i<76 ; i++){
                        border_top_arr.push(String.fromCharCode(i) + 6)
                    }
                    border_top_arr.forEach(item=>{
                        if(sheet[item]?.s?.border){
                            sheet[item].s.border.top = {
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            }
                        }else if(sheet[item]?.s){
                            sheet[item].s.border = {
                                top: {
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                }
                            }
                        }else if(sheet[item]){
                            sheet[item].s = {
                                border: { 
                                    top: {
                                        style: 'medium',
                                        color: {
                                            rgb: '00000000'
                                        },
                                    }
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
                                        }
                                    }
                                }
                            }
                        }
                    })
                    
                    //bottom
                    const border_bottom_arr = [];
                    for(let i = 65; i<76 ; i++){
                        border_bottom_arr.push(String.fromCharCode(i) + (rowNum-1))
                    }

                    border_bottom_arr.forEach(item=>{
                        if(sheet[item]?.s?.border){
                            sheet[item].s.border.bottom = {
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            }
                        }else if(sheet[item]?.s && !sheet[item]?.s?.border){
                            sheet[item].s.border = {
                                bottom: {
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                }
                            }
                        }else if(sheet[item]){
                            sheet[item].s = {
                                border: { 
                                    bottom: {
                                        style: 'medium',
                                        color: {
                                            rgb: '00000000'
                                        },
                                    }
                                }
                            }
                        }else{
                            sheet[item] = {
                                s: {
                                    border: { 
                                        bottom: {
                                            style: 'medium',
                                            color: {
                                                rgb: '00000000'
                                            },
                                        }
                                    }
                                }
                            }
                        }
                    })
                    
                    //left
                    const border_left_arr = [];
                    for(let i = 6; i < rowNum ; i++){
                        border_left_arr.push('A' + i)
                    }
                    border_left_arr.forEach(item=>{
                        console.log(item);
                        console.log('sheet[item]?.s',sheet[item]?.s);
                        if(sheet[item]?.s?.border){
                            sheet[item].s.border.left = {
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            }
                        }else if(sheet[item]?.s){
                            sheet[item].s.border = {
                                left: {
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                }
                            }
                        }else if(sheet[item]){
                            sheet[item].s = {
                                border: { 
                                    left: {
                                        style: 'medium',
                                        color: {
                                            rgb: '00000000'
                                        },
                                    }
                                }
                            }
                        }else{
                            sheet[item] = {
                                s: {
                                    border: { 
                                        left: {
                                            style: 'medium',
                                            color: {
                                                rgb: '00000000'
                                            },
                                        }
                                    }
                                }
                            }
                        }
                    })
                    
                    //right
                    const border_right_arr = [];
                    for(let i = 6; i < rowNum ; i++){
                        border_right_arr.push('K' + i)
                    }
                    console.log('border_right_arr',border_right_arr);
                    border_right_arr.forEach(item=>{
                        if(sheet[item]?.s?.border){
                            sheet[item].s.border.right = {
                                style: 'medium',
                                color: {
                                    rgb: '00000000'
                                },
                            }
                        }else if(sheet[item]?.s && !sheet[item]?.s?.border){
                            sheet[item].s.border = {
                                right: {
                                    style: 'medium',
                                    color: {
                                        rgb: '00000000'
                                    },
                                }
                            }
                        }else if(sheet[item]){
                            sheet[item].s = {
                                border: { 
                                    right: {
                                        style: 'medium',
                                        color: {
                                            rgb: '00000000'
                                        },
                                    }
                                }
                            }
                        }else{
                            sheet[item] = {
                                s: {
                                    border: { 
                                        right: {
                                            style: 'medium',
                                            color: {
                                                rgb: '00000000'
                                            },
                                        }
                                    }
                                }
                            }
                        }
                    })
                    
                    */
                    
                    //垂直置中
                    const v_center_arr = ['A6','B6','C6','D6','E6','J6','K6'];
                    v_center_arr.forEach(item => {
                        if(sheet[item]?.s?.alignment){
                            sheet[item].s.alignment.vertical = 'center'
                        }else if(sheet[item]?.s && !sheet[item]?.s?.alignment){
                            sheet[item].s.alignment = {
                                vertical : 'center'
                            }
                        }else {
                            sheet[item].s = {
                                alignment: {
                                    vertical: 'center'
                                }
                            }
                        }
                    })

                    //水平置中
                   
                    const indexArray = [];
                    //A8之後(A9,A10..)跟著撈出的比數做調整
                    for(let i = 8;i < rowNum;i++){
                        indexArray.push(`A\${i}`)
                    }
                    const t_center_arr = ['A1','A2','A6','B6','C6','D6','E6','F6','F7','G6','G7','H6','H7','I6','I7','J6','K6',...indexArray];
                    t_center_arr.forEach(item=>{
                        if(sheet[item]?.s?.alignment){
                            sheet[item].s.alignment.horizontal = 'center'
                        }else if(sheet[item]?.s && !sheet[item]?.s?.alignment){
                            sheet[item].s.alignment = {
                                horizontal : 'center'
                            }
                        }else {
                            sheet[item].s = {
                                alignment: {
                                    horizontal: 'center'
                                }
                            }
                        }
                    })
                    sheet['!merges'] = [
                        //Global
                        //首列合併
                        {
                            s: 'A1',
                            e: 'J1'
                        },
                        //次列合併
                        {
                            s: 'A2',
                            e: 'J2'
                        },
                        //帳款日期
                        {
                            s: 'A4',
                            e: 'D4'
                        },
                        // 會計科目 列印日期
                        {
                            s: 'A5',
                            e: 'D5'
                        },
                        //應收 only
                        //應收表頭
                        {
                            s: 'A6',
                            e: 'A7'
                        },
                        {
                            s: 'B6',
                            e: 'B7'
                        },
                        {
                            s: 'C6',
                            e: 'C7'
                        },
                        {
                            s: 'D6',
                            e: 'D7'
                        },
                        {
                            s: 'E6',
                            e: 'E7'
                        },
                        {
                            s: 'J6',
                            e: 'J7'
                        },
                        {
                            s: 'K6',
                            e: 'K7'
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
                        {wch: 16},
                        {wch: 16},
                        {wch: 16},
                        {wch: 30},
                    ];
                    
                    workbook.appendSheet(sheet, '應收帳款帳齡分析表')
                    saveAs(workbook.toBlob(), '應收帳款帳齡分析表.xlsx')
                }
                
                //4. all_check
                $('#all_check').on('click', handAllCheck)
                function handAllCheck(e){
                    const bool = e.target.checked;
                    const checkboxs = document.querySelectorAll('tbody input[type="checkbox"]')
                    checkboxs.forEach(item=>{
                        item.checked = bool
                    })
                    store.mutation('set_balance_all_selected', bool)
                }
                //5. item 
                $('#itemList').on('change', (e)=>{
                    const value = e.target.value;
                    const content = $(`#item_datalist [value="\${value}"]`)[0]?.textContent;
                    if(content) e.target.value = `\${value}  \${content}`
                    else e.target.value = '';
                })
                $('#itemList').on('click',(e)=> e.target.value = '');
                //6. submit
                $('#search_submit').on('click', handSubmit)
                function handSubmit(e){
                    const MAX_DATE_RANGE = 365;
                    const date1 = $('#search_date_start').val()
                    const date2 = $('#search_date_end').val()
                    const dateDiff = getDateDiff(date1, date2);
                    const iId =  $('#itemList').val().split(' ').slice(0,1)[0]
                    const company = $('#search_company').val()
                    const predict_date_end = $('#search_predict_date_end').val()

                    if(company === '') return confirm('訊息', '公司名稱未填')
                    if(iId === '') return confirm('訊息', '項目編號未填')
                    if(dateDiff > MAX_DATE_RANGE) return confirm('訊息', `日期間隔不得大於 \${MAX_DATE_RANGE} 天`)
                    else if(date2 - date1 < 0 ) return confirm('訊息','日期區間不得小於一日')
                    if(predict_date_end === '') return confirm('訊息', '帳齡截止日未填')

                    
                    const filter = {
                        company,
                        item: iId,
                        credit_date_start: date1.split('-').join(''),
                        credit_date_end: date2.split('-').join(''),
                        predict_date_end: predict_date_end.split('-').join(''),
                    }

                    const keys = Object.keys(filter);
                    const query = keys.reduce((total, item, index)=>{
                        if(index == 0) total += `\${item}=\${filter[item]}`
                        else total += ('&' + `\${item}=\${filter[item]}`)

                        return total
                    },'')

                    //excel
                    store.mutation('set_excel_credit_date_start',ce2Roc($('#search_date_start').val()))
                                store.mutation('set_excel_credit_date_end', ce2Roc($('#search_date_end').val()))
                                store.mutation('set_excel_predictDate', ce2Roc($('#search_predict_date_end').val()))
                                store.mutation('set_excel_item',$('#itemList').val())
                    console.log('query', query);
                    store.action('api_get_balance', query)
                        .then(res=>{
                            const {status, data, message} = res;
                            if(status == 200){
                                store.mutation('set_month', getMonthData())
                                renderTHeadMonth()
                                renderTable();
                                renderTotalAmount()
                                //excel set
                                store.mutation('set_excel_credit_date_start',ce2Roc($('#search_date_start').val()))
                                store.mutation('set_excel_credit_date_end', ce2Roc($('#search_date_end').val()))
                                store.mutation('set_excel_predictDate', ce2Roc($('#search_predict_date_end').val()))
                                store.mutation('set_excel_item',$('#itemList').val())
                            }else{
                                confirm('訊息', message)
                            }
                        })
                        .catch(err=>{
                            if(err.status != 200) confirm("訊息", err.message)
                        })
                }
                //7 datePicker month-only
                $('#search_date_start').on('click',(e)=>{
                    $('#ui-datepicker-div').addClass('open')
                })
                $('#search_date_end').on('click',(e)=>{
                    $('#ui-datepicker-div').addClass('open')
                })
                //init
                {   
                    cusDatepicker('search_date_start',-30)
                    cusDatepicker('search_date_end')
                    $('#search_date_start').datepicker("option", "onClose", function(){
                        $('#ui-datepicker-div').removeClass('open')
                    })
                    $('#search_date_end').datepicker("option", "onClose", function(){
                        $('#ui-datepicker-div').removeClass('open')
                    })

                    const predict = $('#search_predict_date_end');
                    predict.val(getLastDate())
                    predict.datepicker({
                        changeYear: true, // 年下拉選單
                        changeMonth: true, // 月下拉選單
                        showButtonPanel: true, // 顯示介面
                        showMonthAfterYear: true, // 月份顯示在年後面
                        dateFormat: 'yy-mm-dd',
                        showButtonPanel: true,
                        monthNamesShort: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"], // 月名中文
                        prevText: '上月', // 上月按鈕
                        nextText: '下月', // 下月按鈕
                        currentText: "本月", // 本月按鈕
                        closeText: "送出", // 送初選項按鈕
                        onClose: function (dateText, inst) {
                            // $('#ui-datepicker-div').removeClass('open')
                            const year = $('.ui-datepicker-year').val()
                            let month = +$('.ui-datepicker-month').val() + 1;
                            month = String(month).length === 1
                                    ? '0'+ month
                                    : month
                            const lastDay = new Date(year, month, 0).getDate()
                            predict.val(year + '-' + month + '-' + lastDay)

                        }
                    })

                    renderCompany()

                    store.action('api_get_manager')
                        .then(res=> {
                            if(res.status == 200) renderItemDatalist()
                            else confirm("訊息", res.message)
                        })
                        .catch(err=> {
                            if(err.status != 200) confirm("訊息", err.message)
                        })
                    
                    store.mutation('set_month', getMonthData())
                    renderTHeadMonth()
                    
                     //excel set
                    store.mutation('set_excel_credit_date_start',ce2Roc($('#search_date_start').val()))
                    store.mutation('set_excel_credit_date_end', ce2Roc($('#search_date_end').val()))
                    store.mutation('set_excel_predictDate', ce2Roc($('#search_predict_date_end').val()))
                    // store.mutation('set_excel_item','')
                    console.log(store.getter('get_excel_export_format'));
                }
            });
        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
              <div class="card m-3">
                <div class="card-header">
                    <h1>應收帳款帳齡分析表</h1>
                </div>
                <div class="card-body px-2">
                    <!-- search form -->
                    <form action="javascript:;" class="form_search">
                        <div class="search">
                            <div class="line line-1">
                                <div class="search-item">
                                    <label for="company">公司 : </label>
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
                                    <datalist id="companyList">
                                        <option value="越是實業"></option>
                                    </datalist> -->
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
                                <div class="search-item" style="flex: 1 0 215px;">
                                  <label for="search_predict_date_end">帳齡截止日</label>
                                  <input type="text" id="search_predict_date_end" class="form-control text-center" readonly/>
                                </div>
                            </div>
                            <div class="line line-2">
                                <div class="search-item">
                                  <label for="tableInfos_item">項目 : </label>
                                  <input 
                                      name="itemList" 
                                      list="item_datalist" 
                                      id="itemList" 
                                      class="form-control"
                                      placeholder="請輸入項目編號"
                                      autocomplete="off" 
                                  />
                                </div>
                                <div class="btns ml-auto">
                                    <!-- <button class="btn btn-secondary" id="test">渲染假資料</button> -->
                                    <input type="submit" value="查詢" id="search_submit" class="btn btn-primary"></input>
                                    <button class="btn btn-info ml-3" id="export_exl">匯出</button>
                                </div>
                            </div>                              
                          </div>   
                        </form>
                        <!-- delete item_area -->
                        <!--id="dataTable"-->
                        <table class="table table-bordered" id="table" width="100%" cellspacing="0">
                            <thead bgcolor="#E6E6E6">
                                <tr>
                                    <th width="5%" rowspan="2">
                                        <label for="all_check">
                                            <input type="checkbox" id="all_check">
                                            全選
                                        </label>
                                    </th>
                                    <th rowspan="2">預計收款日</th>
                                    <th rowspan="2">廠商統編</th>
                                    <th rowspan="2">廠商名稱</th>
                                    <th rowspan="2">立帳傳票</th>
                                    <th rowspan="1">30天以內</th>
                                    <th rowspan="1">31-60天</th>
                                    <th rowspan="1">61-90天</th>
                                    <th rowspan="1">90天以上</th>
                                    <th rowspan="2">餘額</th>
                                    <th rowspan="2">立帳傳票說明</th>
                                </tr>
                                <tr>
                                  <th rowspan="1" month>月份</th>
                                  <th rowspan="1" month>月份</th>
                                  <th rowspan="1" month>月份</th>
                                  <th rowspan="1" month>月份</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- <tr>
                                    <td width="5%" rowspan="2">
                                        <input type="checkbox" id="all_check">
                                    </td>
                                    <td rowspan="2">預計收款日</td>
                                    <td rowspan="2">廠商統編</td>
                                    <td rowspan="2">廠商名稱</td>
                                    <td rowspan="2">立帳傳票</td>
                                    <td rowspan="1">30天以內</td>

                                    <td rowspan="1">31-60天</td>
                                    <td rowspan="1">61-90天</td>
                                    <td rowspan="2">餘額</td>
                                    <td rowspan="2">立帳傳票說明</td>
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
            <jsp:include page="footer.jsp" />
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