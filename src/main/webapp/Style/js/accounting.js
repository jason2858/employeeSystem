/*
 * @Description 初始設置 Datepicker
 *
 * @param {string} elementID
 * @param {string <yyyymmdd> | number <相差天數> | Date Obj} date   設置日期，可選
 */
function cusDatepicker (elementID, date) {
    const $dateDOM = $(`#${elementID}`);
    $dateDOM[0].defaultValue = dateFormat(date);
    $dateDOM.datepicker({
        dateFormat: "yy-mm-dd",
        monthNames: [
            "一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月",
        ],
        dayNamesMin: ["日", "一", "二", "三", "四", "五", "六"],
        firstDay: 1
    });
};

/*
 * @Description 初始設置 DataTable
 *
 * @param {string} elementID
 * @param {array} columns
 * @param {object} options  客製參數，可選
 */
function cusDataTable(elementID, columns, options) {
    
    if (typeof elementID !== 'string') {
        return console.error('Param elementID ' + elementID + ' is not string')
    }
    const initConfig = {
        bLengthChange: false,
        pageLength: 10,
        oLanguage: {
            sProcessing: "讀取中...",
            sLengthMenu: "顯示 _MENU_ 筆資料",
            sZeroRecords: "查無相符的資料",
            sEmptyTable: "無資料",
            sInfo: "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
            sInfoEmpty: "顯示 0 到 0 共 0  筆資料",
            sInfoFiltered: "(filtered from _MAX_ total entries)",
            sInfoPostFix: "",
            sSearch: "關鍵字搜尋:",
            sUrl: "",
            oPaginate: {
                sFirst: "第一頁",
                sPrevious: "上一頁",
                sNext: "下一頁",
                sLast: "最末頁"
            }
        },
        processing: true,
        bAutoWidth: false,
        bPaginate: true,
        retrieve: true,
        bInfo: true,
        bSort: true,
        searching: false,
        columns,
        order: [],
        columnDefs: [{
            targets: 'no-sort',
            orderable: false,
        }]
    }
    
    const dataTableConfig = JSON.parse(JSON.stringify(initConfig))
    $.extend(true, dataTableConfig, options)
    return $('#' + elementID).DataTable(dataTableConfig);
}

/*
 * @Description 日期標準格式化
 * 
 * @param {string <yyyymmdd> | number <相差天數> | Date Obj} dateParam
 */
function dateFormat(dateParam = new Date()){
    let year, month, day;
    if (dateParam instanceof Date) {
        year = dateParam.getFullYear()
        month = ("0" + (dateParam.getMonth() + 1)).slice(-2)
        day = ("0" + dateParam.getDate()).slice(-2)

    } else if (typeof dateParam === 'string' && dateParam.length === 8) {
        year = dateParam.slice(0, 4)
        month = dateParam.slice(4, 6)
        day = dateParam.slice(6, 8)
    
    } else if (typeof dateParam === 'number') {
        const now = new Date() 
        const date = new Date(now.setDate(now.getDate() + dateParam))
        year = date.getFullYear()
        month = ("0" + (date.getMonth() + 1)).slice(-2)
        day = ("0" + date.getDate()).slice(-2)
    
    } else {
        console.error('dateFormat function argument must be a string, number or Date object. 參數必須是字串、數字或 Date 物件')
    }
    return (year + "-" + month + "-" + day);
}

/*
 * @Description 金額千分點標準格式化，支援正負數
 *
 * @param {string | number} account
 */
function accountFormat(account = 0) {
  let num = account.toString().replaceAll(",", "");
  let isMinus = false, result = "";

  if (num[0] === "-") {
    isMinus = true;
    num = num.substring(1);
  } else {
    isMinus = false;
  }
  while (num.length > 3) {
    result = "," + num.slice(-3) + result;
    num = num.slice(0, num.length - 3);
  }
  if (num) {
    result = num + result;
  }
  if (isMinus) {
    result = "-" + result;
  }
  return result;
}

/*
 * @Description Ajax datalist 請求
 *      並返回  data() 取資料
 *              setDatalist(datalistID, title, text, insert) 設置 datalist
 *              getItem(keyword, comparison, comparison2) 資料取值
 *
 * @param {string} type
 * @param {string} url
 */
function getDatalist(type, url) {
    let data = [], done = false
    $.ajax({
        type,
        url,
        contentType: "application/json;charset=UTF-8",
        datatype: "json",
        success(response) {
            let record
            if (typeof response === 'string') {
                record = JSON.parse(response)
            } else {
                record = response
            }
            if (Array.isArray(record)) {
                data = record                         // [...]
            } else if (record.status == '200') {
                if (record.entity) {
                    data = JSON.parse(record.entity)  // {status, entity: '...'}
                } else if (record.data.commonList) {
                    data = record.data.commonList     // {status, message, data: {commonList: [...] } }
                } else {
                    data = record.data                // {status, message, data: [...]}
                }
            }
            done = true
        },
        error() {
            done = true
            confirm("訊息", `取得資料失敗`);
        },
    });
    return {
        data() {
            return data
        },
        /*
         * @Description 設置 datalist 表單，
         *
         * @param {string} datalistID
         * @param {string | array} title 顯示在 input 的 value 值
         * @param {string} text 顯示在 option 中的文本
         * @param {string} insert 當 title 為陣列時，串接的字串
         */
        setDatalist(datalistID, title, text, insert = ' ') {
            let options = '', timer = null;
            if (done) {
                Array.isArray(data) && data.forEach( dataObj => {
                    let value = ''
                    if (Array.isArray(title)) {
                        title.forEach((key, index) => {
                            const str = index === title.length - 1 ? dataObj[key] : dataObj[key] + insert
                            value += str
                        })
                    } else {
                        value = dataObj[title]
                    }
                    if (dataObj.enable !== 'N') {
                        options += `
                            <option value="${ value }">${ dataObj[text] || '' }</option>
                        `
                    }
                })
                $("#" + datalistID).append(options)
            } else {
                if (timer) {
                    clearTimeout(timer)
                    timer = null;
                }
                timer = setTimeout(() => {
                    this.setDatalist(datalistID, title, text, insert)
                }, 1000)
            }
        },
        /*
         * @Description 取得資料的值，返回 list 的 item
         *
         * @param {string} keyword 輸入的關鍵字
         * @param {string} comparison  比對值
         * @param {string} comparison2 比對值（可選）
         */
        getItem(keyword, comparison, comparison2) {
            const keywordVal = keyword.toString().trim();
            for (let i = 0; i < data.length; i++) {
                if (data[i][comparison] === keywordVal || data[i][comparison2] === keywordVal) {
                    return data[i];
                } else if (i === data.length - 1) {
                    return keywordVal;
                }
            }
        }
    }
}

/*
 * @Description 駝峰式命名轉換為蛇形式命名的小工具
 *              可以轉換深層結構
 *
 * @param {json | object} data
 */
function camelCaseToSnakeCase (data, arr) {
    const json = {}
    for (const key in data) {
        const newKey = key.replace(/([A-Z])/g,'_$1').toLowerCase()
        if ( Array.isArray(data[key]) ) {
            camelCaseToSnakeCase(data[key], json[newKey] = [])
        } else if (data[key] && data[key].constructor === Object) {
            arr.push( camelCaseToSnakeCase( data[key] ) )
        } else {
            json[newKey] = data[key]
        }
    }
    return json
}

/*
 * @Description 取得相差天數
 *
 * @params { yyyy-mm-dd } date1
 * @params { yyyy-mm-dd } date2
 */
function getDateDiff(date1, date2 = new Date()) {
  var oDate1 = new Date(date1);
  var oDate2 = new Date(date2);
  var iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24); // 把相差的毫秒數轉換為天數
  return iDays;
}
