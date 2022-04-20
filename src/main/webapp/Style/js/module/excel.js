/*
 * @Description 獲取 sheet 總列數
 *
 * @param { xlsx }
 */
//
export function getSheetRowNum(sheet) {
  const ref = sheet["!ref"];
  let row = ref.split(":")[1];
  return +row.slice(1);
}

/*
 * @Description 取得公司表頭 aoa 格式(尚未轉xlsx)
 *
 * @param { string } formName
 * @param { string } predictDate
 * @param { string } credit_date_start
 * @param { string } credit_date_end
 * @param { string } accountingName
 */
//取得 excel 表頭 aoa 格式
export function get_excel_title_array(
  length,
  obj = {
    formName: "",
    predictDate: "",
    credit_date_start: "",
    credit_date_end: "",
    accountingName: "",
  },
) {
  if (isNaN(length)) return;
  length = +length;
  const {
    formName,
    predictDate,
    credit_date_start,
    credit_date_end,
    accountingName,
  } = obj;
  const now = dateFormat();
  const array = new Array(5); //表頭五列
  for (let i = 0; i < 5; i++) {
    array[i] = new Array(length);
  }

  array[0][0] = "越世實業股份有限公司";
  array[1][0] = formName;
  array[3][0] = `帳款日期: ${credit_date_start} - ${credit_date_end}`;
  array[3][length - 1] = predictDate ? `帳齡截止日：${predictDate}` : "";
  array[4][0] = `會計科目：${accountingName}`;
  array[4][length - 1] = `列印時間：${ce2Roc(dateFormat())}  ${getTime()}`;

  return array;
}
/*
 * @Description
 *
 * @param { string } yyyymmdd
 */
export function getMonthFromDate(date) {
  const mm = date.slice(4, 6);
  if (mm.slice(0, 1) == 0) return mm.slice(1);
  else return mm;
}

/*
 * @Description 西元轉民國年
 * 1. CE yyyy-mm-dd -> ROC yyy/mm/dd
 * 2. CE yyyymmdd -> ROC yyy/mm/dd
 *
 * @param { string <yyyymmdd> || string <yyyy-mm-dd> } date
 * @param { string }  joinSymbol
 */
export function ce2Roc(date, joinSymbol = "/") {
  if (typeof date === "number") date = String(date);

  if (date.length === 10 && date.slice(4, 5) === "-") {
    const year = date.slice(0, 4);
    const newYear = +year - 1911;
    date = date.split("-");
    date.splice(0, 1, newYear);
    return date.join(joinSymbol);
  } else if (date.length === 8) {
    const arr = [];
    arr[0] = date.slice(0, 4) - 1911;
    arr[1] = date.slice(4, 6);
    arr[2] = date.slice(6, 8);
    return arr.join(joinSymbol);
  }
}
/*
 * @Description 返回當月最後一天日期
 * return yyyy-mm-dd
 */
// 取得當月最後一天日期 yyyy-mm-dd
export function getLastDate() {
  const today = new Date();
  const year = today.getFullYear();
  let month = String(today.getMonth() + 1);
  const lastDay = new Date(year, month, 0).getDate();
  month = month.length === 1 ? "0" + month : month;
  return `${year}-${month}-${lastDay}`;
}
/*
 * @Description 取得時間點
 *
 * @param { Date } param
 */
// 取得時間點 hh:mm:ss
export function getTime(param = new Date()) {
  let hour, minute, second;
  if (param instanceof Date) {
    hour = ("0" + param.getHours()).slice(-2);
    minute = ("0" + param.getMinutes()).slice(-2);
    second = ("0" + param.getSeconds()).slice(-2);
  }
  return hour + ":" + minute + ":" + second;
}

/*
 * @Description 存放 xlsx 工作簿
 *
 *
 */
export default class Workbook {
  constructor() {
    // 使用單例模式，產生唯一的 workbook
    if (!(this instanceof Workbook)) return new Workbook();

    /**
     * SheetNames 與 Sheets 兩者在 Workbook 中不可或缺，
     * 此為 XLSX 工具將 Workbook Object 傳為 Excel blob 時提取資料的位置。
     **/
    this.SheetNames = []; // 儲存 Sheet 的名稱。
    this.Sheets = {}; // 儲存 Sheet 的物件內容

    // 自定義 workbook optional object。
    this.wopts = {
      bookType: "xlsx", // 要生成的文件类型
      bookSST: false, // 是否生成 Shared String Table。官方解釋是如果開啟生成速度會下降，但在低版本IOS設備上有更好的兼容性
      type: "binary",
    };
  }

  /*
   * @Description 存放單頁 xlsx sheet
   *
   * @param { xlsx } sheet
   * @param { string } name
   */
  appendSheet(sheet, name = `sheet${this.SheetNames.length + 1}`) {
    this.SheetNames = [...this.SheetNames, name];
    this.Sheets[name] = sheet;
  }

  toBlob(option = this.wopts) {
    // 字串轉 ArrayBuffer
    function s2ab(s) {
      var buf = new ArrayBuffer(s.length);
      var view = new Uint8Array(buf);
      for (var i = 0; i !== s.length; ++i) view[i] = s.charCodeAt(i) & 0xff;
      return buf;
    }

    var wbout = XLSX.write(this, option);
    var blob = new Blob([s2ab(wbout)], { type: "application/octet-stream" });

    return blob;
  }

  isEmpty() {
    return !this.SheetNames.length && JSON.stringify(this.Sheets === "{}");
  }
}
