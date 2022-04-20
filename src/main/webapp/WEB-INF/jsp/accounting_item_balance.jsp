<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <jsp:include page="header.jsp" />
    <!-- Datetimepicker-->
    <link
      rel="stylesheet"
      href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css"
    />
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
    <!-- checkLister -->
    <script src="../../Style/custom-component/checkLister/checkLister.js?date=20220323"></script>
    <link
      rel="stylesheet"
      href="../../Style/custom-component/checkLister/checkLister.css"
    />
    <style type="text/css">
      body {
        display: none;
      }

      label {
        margin: 0 8px 0 0;
      }

      th,
      td {
        text-align: center;
        white-space: nowrap;
      }

      .table-x {
        flex: 1 0 90%;
        overflow-x: auto;
        box-shadow: inset 0 0 24px #0002;
        -webkit-overflow-scrolling: touch;
        -ms-overflow-style: -ms-autohiding-scrollbar;
      }

      .table-bordered,
      .table-bordered th {
        border-top: none;
      }

      table.dataTable {
        margin-top: 0 !important;
      }

      #itemBalanceList td:nth-child(8),
      #itemBalanceList td:nth-child(9) {
        text-align: right;
      }

      .item_area .content {
        padding: 8px 12px;
      }

      .sheetList {
        overflow-x: auto;
      }
      .sheet {
        border: 1px solid #e6e6e6;
        color: #888;
        border-bottom: none;
        border-radius: 4px 4px 0 0;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        transition: 0.25s;
        cursor: pointer;
      }
      .sheet:hover {
        background: #efefef;
        color: #333;
      }
      .sheet.active {
        color: #000;
        background: #e6e6e6;
        cursor: default;
        z-index: 1;
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

        if (
          authorise != 1 &&
          authorise != 2 &&
          authorise != 3 &&
          authorise != 4
        ) {
          window.location.href = "timeout.do";
        }
        if (authorise == 1 || authorise == 2 || authorise == 3) {
          appendUnsign();
          getUnsign();
        }

        $("body").show();
      });
    </script>
    <!-- FileSaver & xlsx-style -->
    <script src="../../Style/vendor/excel/file-saver/FileSaver.js"></script>
    <script src="../../Style/vendor/excel/xlsx-style/xlsx.full.min.js"></script>
    <script type="module">
      import * as SHEET from "../../Style/vendor/excel/xlsx/xlsx.js";
      import Workbook, {
        get_excel_title_array,
        getSheetRowNum,
        ce2Roc,
        getTime,
      } from "../../Style/js/module/excel.js";
      $(document).ready(function () {
        const columns = [
          { data: "傳票日期" },
          { data: "傳票號碼" },
          { data: "廠商統編" },
          { data: "廠商名稱" },
          { data: "對沖單號" },
          { data: "專案代號" },
          { data: "說明" },
          { data: "金額" },
          { data: "餘額" },
          { data: "公司別" },
          { data: "簽核" },
        ];
        const dataTableOptions = {
          pageLength: -1,
          dom: `<'row'<'col-sm-12 table-x px-0 mx-3 mb-2'tr>>
                        <'row'<'col-sm-12 text-right pb-2'i>>`,
          infoCallback(settings, start, end, max, total, pre) {
            return (
              "顯示 " +
              start +
              " 到 " +
              (end - 2) +
              " 共 " +
              (total - 2) +
              " 筆資料"
            );
          },
          createdRow(row, data, index) {
            const voucherNo = data["傳票號碼"];
            // const isHedge = voucherNo.slice(0, 1) === "H";
            const isHedge = data["金額"].slice(0, 1) === "(";
            const tableJson = $("#itemBalanceTable").DataTable().data();

            if (voucherNo) {
              const preVoucherNo = tableJson[index - 1]["傳票號碼"];
              if (preVoucherNo !== voucherNo) {
                store.color(true);
              }
              $("td", row).css(
                "background-color",
                store.color() ? "#ddebf7" : "#fce4d6",
              );
            } else if (index === 0) {
              $("td", row).css("border", "none");
              $("td", row).css("border-bottom", "3px double #CCC");
            } else {
              $("td", row).css("border", "none");
              $("td", row).css("border-top", "3px double #CCC");
            }

            if (isHedge) $("td", row)[7].style.color = "red";
          },
        };

        cusDataTable("itemBalanceTable", columns, dataTableOptions);

        const now = new Date();
        cusDatepicker(
          "credit_date_start",
          new Date(now.getFullYear(), now.getMonth(), 1),
        );
        cusDatepicker("credit_date_end");

        const store = (function () {
          const data = {
            balance: 0,
            tables: {},
          };

          const style = {
            toggle: false,
          };

          return {
            getTables(key) {
              if (key) {
                return data.tables[key];
              } else {
                return data.tables;
              }
            },
            setTables(key, value) {
              data.tables[key] = value;
            },
            balance(value) {
              if (value) {
                data.balance += value;
              } else if (value === null) {
                data.balance = 0;
              }
              return data.balance;
            },
            reset() {
              data.balance = 0;
              data.tables = {};
            },
            color(bool) {
              if (bool) {
                style.toggle = !style.toggle;
              }
              return style.toggle;
            },
          };
        })();

        const itemDrop = (function () {
          let result = null;
          $.ajax({
            url: "/rest/accounting/manager",
            method: "GET",
            dataType: "json",
            contentType: "application/json",
          }).then((res) => {
            if (res.status == "200") {
              result = useCheckLister({
                data: res.data,
                checkList: {
                  el: "checkLister_drop",
                  MAX_SELECTED_NUM: 10,
                  input_placeholder: "請輸入...",
                  div_class: "form-control position-static border-0 p-0",
                  input_class: "form-control form-control-sm",
                  input_style: "",
                },
                itemArea: {
                  el: "checkLister_itemArea",
                  delIconPath: "/Style/images/del.png",
                  area_title_class: "mt-2",
                  area_title_style: "font-size: 24px; line-height: 1;",
                  area_clear_btn_class:
                    "clean btn btn-sm btn--danger px-1 py-0",
                },
              });
            }
          });

          return {
            get() {
              return result;
            },
          };
        })();

        const companyList = getDatalist("POST", "/rest/department/getCompanys");
        companyList.setDatalist("companyList", "name");

        function getVoucherDate(voucherNo) {
          const isHedge = voucherNo.slice(0, 1) === "H";
          const voucherDate = isHedge
            ? voucherNo.slice(1, 9)
            : voucherNo.slice(0, 8);
          return voucherDate;
        }

        function setItemBalanceTable(itemObj) {
          const { balance, item: itemList, itemNo } = itemObj;
          const itemJSON = [
            {
              傳票日期: "",
              傳票號碼: "",
              廠商統編: "",
              廠商名稱: "",
              對沖單號: "",
              專案代號: "",
              說明: "前期餘額",
              金額: "",
              餘額: accountFormat(balance),
              公司別: "",
              簽核: "",
            },
          ];

          store.balance(balance);
          itemList.sort((pre, cur) => {
            const newPre = getVoucherDate(pre.voucherNo);
            const newCur = getVoucherDate(cur.voucherNo);
            return newPre - newCur;
          });
          itemList.forEach((item) => {
            const {
              voucherNo,
              cusTaxId,
              customer,
              hedgeNo,
              project,
              directions,
              amount,
              company,
              signStatus,
              type,
            } = item;
            const voucherDate = getVoucherDate(voucherNo);
            // const isHedge = voucherNo.slice(0, 1) === "H";
            const isHedge = type === "M";
            const newBalance = store.balance(isHedge ? amount * -1 : amount);

            itemJSON.push({
              傳票日期: dateFormat(voucherDate),
              傳票號碼: voucherNo,
              廠商統編: cusTaxId || "",
              廠商名稱: customer || "",
              對沖單號: hedgeNo,
              專案代號: project || "非專案",
              說明: directions || "",
              金額: isHedge
                ? "(" + accountFormat(amount) + ")"
                : accountFormat(amount),
              餘額: accountFormat(newBalance),
              公司別: company,
              簽核: signStatus == 2 ? "＊" : "",
            });
          });
          itemJSON.push({
            傳票日期: "",
            傳票號碼: "",
            廠商統編: "",
            廠商名稱: "",
            對沖單號: "",
            專案代號: "",
            說明: "",
            金額: "",
            餘額: accountFormat(store.balance()),
            公司別: "",
            簽核: "",
          });
          store.setTables(itemNo, itemJSON);
          store.balance(null);
        }

        function itemBalanceTableRender(itemNo) {
          const itemBalanceTable = $("#itemBalanceTable").DataTable();
          const itemBalanceJSON = store.getTables(itemNo);

          itemBalanceTable.clear();
          itemBalanceTable.rows.add(itemBalanceJSON).draw();
          console.log(store.getTables());
        }

        function searchItemBalance() {
          const company = $("#company").val();
          const credit_date_start = $("#credit_date_start").val();
          const credit_date_end = $("#credit_date_end").val();
          let items = itemDrop
            .get()
            .getSelectedItem()
            .reduce((pre, cur) => pre + cur.iId + ",", "");
          items = items.slice(0, -1);

          const params = {
            company,
            credit_date_start,
            credit_date_end,
            item: items,
            fun: 1, // 1 分類帳 / 2 餘額帳
          };

          if (new Date(credit_date_start) > new Date(credit_date_end)) {
            return confirm("訊息", "注意入帳起訖日輸入相反！");
          } else if (getDateDiff(credit_date_start, credit_date_end) > 30) {
            return confirm("訊息", "最大範圍為 30 天");
          }

          let paramsStr = "";
          for (const key in params) {
            if (!params[key]) return confirm("訊息", "所有查詢條件都必填");
            paramsStr += key + "=" + params[key] + "&";
          }
          paramsStr = paramsStr.slice(0, -1);

          const itemBalanceTable = $("#itemBalanceTable").DataTable();
          itemBalanceTable.clear().draw();
          store.reset();
          $("#sheetList").empty();

          $.ajax({
            type: "GET",
            url: "/rest/accounting/report/itemBalance?" + paramsStr,
            contentType: "application/json;charset=UTF-8",
            datatype: "json",
            success: function (data) {
              const record = JSON.parse(data);
              if (record.status === "200") {
                const itemList = record.data;

                itemList.forEach((itemObj) => {
                  const { itemName, itemNo } = itemObj;
                  const title = itemNo + " " + itemName;
                  const sheetList = document.getElementById("sheetList");
                  const sheet = document.createElement("li");

                  setItemBalanceTable(itemObj);
                  sheet.classList.add(
                    "sheet",
                    "position-relative",
                    "px-2",
                    "py-1",
                  );
                  sheet.setAttribute("data-no", itemNo);
                  sheet.setAttribute("title", title);
                  sheet.innerText = title;
                  sheetList.append(sheet);
                });

                recordTableInfos(); //excel
                const sheetDOM = document.querySelector(".sheet");
                sheetDOM.click();
              } else {
                confirm("訊息", record.message);
              }
            },
            error: function () {
              confirm("訊息", "搜尋資料失敗");
            },
          });
        }

        $("#sheetList").on("click", (e) => {
          const sheets = document.querySelectorAll(".sheet");
          const { no } = e.target.dataset;
          const itemBalanceTable = $("#itemBalanceTable").DataTable();

          if (!no) return;
          for (let i = 0; i < sheets.length; i++) {
            sheets[i].classList.remove("active");
          }
          e.target.classList.add("active");
          itemBalanceTableRender(no);
        });

        $("#searchBtn").on("click", searchItemBalance);
        $("#exportExcelBtn").on("click", handExportExcel);
        const table_infos = {
          date: {
            credit_date_start: "",
            credit_date_end: "",
          },
        };
        function recordTableInfos() {
          const credit_date_start = $("#credit_date_start").val();
          const credit_date_end = $("#credit_date_end").val();
          table_infos.date.credit_date_start = ce2Roc(credit_date_start);
          table_infos.date.credit_date_end = ce2Roc(credit_date_end);
        }
        function handExportExcel() {
          //1. data to aoa
          const workbook = new Workbook();
          const tableHead = [
            "傳票日期",
            "傳票號碼",
            "廠商統編",
            "廠商名稱",
            "對沖單號",
            "專案代號",
            "說明",
            "金額",
            "餘額",
            "公司別",
            "簽核",
          ];
          const data = store.getTables();
          const keys = Object.keys(data);
          const emptyRow = new Array(11);
          const { credit_date_start, credit_date_end } = table_infos.date;

          function isCredit(voucherNo) {
            if (typeof voucherNo === "string") {
              const bool = voucherNo.slice(0, 1) == "H" ? true : false;
              return bool;
            }
          }

          keys.forEach((key) => {
            const col_char = [];
            const i_name = $(`.drop-menu input[value="\${String(key)}"]`)[0]
              .dataset.iName;
            const accountingName = `\${key} \${i_name}`;
            let aoa = [];
            const sheetHead = get_excel_title_array(11, {
              formName: "分類帳",
              predictDate: "",
              credit_date_start,
              credit_date_end,
              accountingName,
            });
            const sheetContent = [];

            let credit_total = 0; //貸方 - 扣
            let debit_total = 0; //借方 - 增
            //style array
            const color_red_arr = [];
            const fgColor_odd_row_arr = [];
            const fgColor_even_row_arr = [];
            const border_arr = [];

            for (let i = 0; i < 76; i++) {
              col_char.push(String.fromCharCode(i));
            }

            data[key].forEach((item, index) => {
              //基本資料、算借貸方金額、加上借貸方金額、標注借貸方 style
              //index + 7 為該 row
              sheetContent.push([
                ce2Roc(item["傳票日期"]) ?? "",
                item["傳票號碼"],
                item["廠商統編"],
                item["廠商名稱"],
                item["對沖單號"],
                item["專案代號"],
                item["說明"],
                item["金額"],
                item["餘額"],
                item["公司別"],
                item["簽核"],
              ]);
              // border
              col_char.forEach((item) => {
                const position = item + (index + 7);
                border_arr.push(position);
              });
              // bgColor
              if (index !== 0 && index + 1 !== data[key].length) {
                if (index % 2 === 0) fgColor_even_row_arr.push(index + 7);
                else fgColor_odd_row_arr.push(index + 7);
              }

              //借貸方 total & color
              if (item["金額"].slice(0, 1) === "(") {
                color_red_arr.push("H" + (index + 7));
                const amount = +item["金額"]
                  .replaceAll(",", "")
                  .replaceAll("(", "")
                  .replaceAll(")", "");
                credit_total += amount;
              } else {
                const amount = +item["金額"].replaceAll(",", "");
                debit_total += amount;
              }
            });

            const credit_row = new Array(11);
            const debit_row = new Array(11);

            credit_row[6] = "借方金額";
            credit_row[7] = accountFormat(credit_total);
            debit_row[6] = "貸方金額";
            debit_row[7] = accountFormat(debit_total);

            //aoa to sheet
            aoa = [
              ...sheetHead,
              tableHead,
              ...sheetContent,
              emptyRow,
              emptyRow,
              credit_row,
              debit_row,
            ];
            console.log("color_red_arr", color_red_arr);
            const sheet = SHEET.utils.aoa_to_sheet(aoa);
            //sheet style #ddebf6,#fce4d6
            //1. !merge
            sheet["!merges"] = [
              //Global
              //首列合併
              {
                s: "A1",
                e: "K1",
              },
              //次列合併
              {
                s: "A2",
                e: "K2",
              },
              //帳款日期
              {
                s: "A4",
                e: "C4",
              },
              //會計科目 列印日期
              {
                s: "A5",
                e: "C5",
              },
              {
                s: "I5",
                e: "K5",
              },
            ];
            //2 !col
            sheet["!cols"] = [
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 16 },
              { wch: 5 },
            ];
            //3 color
            color_red_arr.forEach((item) => {
              if (sheet[item]?.s?.font?.color) {
                sheet[item].s.font.color = {
                  rgb: "FFFF0000",
                };
              } else if (sheet[item]?.s?.font) {
                sheet[item].s.font.color = {
                  rgb: "FFFF0000",
                };
              } else if (sheet[item]?.s) {
                sheet[item].s.font = {
                  color: {
                    rgb: "FFFF0000",
                  },
                };
              } else if (sheet[item]) {
                sheet[item].s = {
                  font: {
                    color: {
                      rgb: "FFFF0000",
                    },
                  },
                };
              }
            });
            //4 fg_color
            //odd
            fgColor_odd_row_arr.forEach((row_number) => {
              col_char.forEach((char) => {
                const position = char + row_number;
                if (sheet[position]?.s) {
                  sheet[position].s.fill = {
                    fgColor: {
                      rgb: "FFFCE4D6",
                    },
                  };
                } else if (sheet[position]) {
                  sheet[position].s = {
                    fill: {
                      fgColor: {
                        rgb: "FFFCE4D6",
                      },
                    },
                  };
                }
              });
            });
            //even
            fgColor_even_row_arr.forEach((row_number) => {
              col_char.forEach((char) => {
                const position = char + row_number;
                if (sheet[position]?.s) {
                  sheet[position].s.fill = {
                    fgColor: {
                      rgb: "FFDDEBF6",
                    },
                  };
                } else if (sheet[position]) {
                  sheet[position].s = {
                    fill: {
                      fgColor: {
                        rgb: "FFDDEBF6",
                      },
                    },
                  };
                }
              });
            });
            //text center
            const t_center_arr = ["A1", "A2"];
            t_center_arr.forEach((item) => {
              if (sheet[item]?.s?.alignment?.horizontal) {
                sheet[item].s.alignment.horizontal = "center";
              } else if (sheet[item]?.s?.alignment) {
                sheet[item].s.alignment.horizontal = "center";
              } else if (sheet[item]?.s) {
                sheet[item].s = {
                  alignment: {
                    horizontal: "center",
                  },
                };
              } else if (sheet[item]) {
                sheet[item].s = {
                  alignment: {
                    horizontal: "center",
                  },
                };
              }
            });
            //border
            border_arr.forEach((item) => {
              if (sheet[item]?.s?.border) {
                sheet[item].s.border = {
                  top: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  bottom: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  left: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  right: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                };
              } else if (sheet[item]?.s) {
                sheet[item].s.border = {
                  top: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  bottom: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  left: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                  right: {
                    style: "thick",
                    color: {
                      rgb: "00000000",
                    },
                  },
                };
              } else if (sheet[item]) {
                sheet[item].s = {
                  border: {
                    top: {
                      style: "thick",
                      color: {
                        rgb: "00000000",
                      },
                    },
                    bottom: {
                      style: "thick",
                      color: {
                        rgb: "00000000",
                      },
                    },
                    left: {
                      style: "thick",
                      color: {
                        rgb: "00000000",
                      },
                    },
                    right: {
                      style: "thick",
                      color: {
                        rgb: "00000000",
                      },
                    },
                  },
                };
              } else {
                sheet[item] = {
                  s: {
                    border: {
                      top: {
                        style: "thick",
                        color: {
                          rgb: "00000000",
                        },
                      },
                      bottom: {
                        style: "thick",
                        color: {
                          rgb: "00000000",
                        },
                      },
                      left: {
                        style: "thick",
                        color: {
                          rgb: "00000000",
                        },
                      },
                      right: {
                        style: "thick",
                        color: {
                          rgb: "00000000",
                        },
                      },
                    },
                  },
                };
              }
            });
            console.log("SHEET", sheet);
            //append to workbook
            workbook.appendSheet(sheet, accountingName);
          });

          saveAs(workbook.toBlob(), "分類帳.xlsx");
        }
      });
    </script>
  </head>

  <body class="fixed-nav sticky-footer" id="page-top">
    <!-- Navigation-->
    <jsp:include page="navbar.jsp" />
    <div class="content-wrapper">
      <div class="container-fluid">
        <div class="card m-3 pb-2">
          <header class="card-header">分類帳</header>

          <div>
            <div class="form-row align-items-center px-3 pt-3">
              <div class="col-3">
                <div class="input-group align-items-center">
                  <label for="company">公司 : </label>
                  <input
                    type="text"
                    list="companyList"
                    id="company"
                    class="form-control form-control-sm"
                    placeholder="請輸入..."
                    required
                  />
                  <datalist id="companyList"></datalist>
                </div>
              </div>

              <div class="col-3 position-static">
                <div
                  class="input-group align-items-center position-static"
                  id="checkLister_drop"
                ></div>
              </div>

              <div class="col-6 d-flex input-group align-items-center">
                <label for="credit_date_start"> 入帳日期範圍 : </label>
                <div
                  class="form-control form-control-sm d-flex align-items-center"
                >
                  <input
                    id="credit_date_start"
                    type="text"
                    class="date text-center w-50 border-0"
                    readonly
                    required
                  />
                  <label for="credit_date_end" class="m-0 px-1"> 至 </label>
                  <input
                    id="credit_date_end"
                    type="text"
                    class="date text-center w-50 border-0"
                    readonly
                    required
                  />
                </div>
              </div>
            </div>

            <div class="px-3">
              <!-- item-list area -->
              <section
                class="item_area px-0 pb-0"
                id="checkLister_itemArea"
              ></section>
            </div>

            <div
              class="row justify-content-between align-items-end flex-row-reverse mx-3"
            >
              <div class="col text-right mb-2">
                <button
                  type="button"
                  id="searchBtn"
                  class="btn btn-sm btn--primary"
                >
                  查詢
                </button>
                <a id="exportExcelBtn" href="#" class="btn btn-sm btn--info">
                  匯出
                </a>
              </div>
              <ul id="sheetList" class="nav sheetList flex-nowrap"></ul>
            </div>

            <!--id="dataTable"-->
            <table
              class="table table-bordered"
              id="itemBalanceTable"
              cellspacing="0"
            >
              <thead>
                <tr style="background: #e6e6e6">
                  <th title="傳票日期" class="no-sort">傳票日期</th>
                  <th title="傳票號碼" class="no-sort">傳票號碼</th>
                  <th title="廠商統編" class="no-sort">廠商統編</th>
                  <th title="廠商名稱" class="no-sort">廠商名稱</th>
                  <th title="對沖單號" class="no-sort">對沖單號</th>
                  <th title="專案代號" class="no-sort">專案代號</th>
                  <th title="說明" class="no-sort">說明</th>
                  <th title="金額" class="no-sort">金額</th>
                  <th title="餘額" class="no-sort">餘額</th>
                  <th title="公司別" class="no-sort">公司別</th>
                  <th title="簽核" class="no-sort">簽核</th>
                </tr>
              </thead>
              <tbody id="itemBalanceList"></tbody>
            </table>
          </div>
        </div>
      </div>
      <jsp:include page="footer.jsp" />
    </div>
    <div
      class="modal fade"
      id="loading"
      tabindex="-1"
      role="dialog"
      aria-labelledby="myModalLabel"
      data-backdrop="static"
      style="text-align: center; width: 100%; height: 100%; padding-left: 0px"
    >
      <div class="modal-dialog">
        <img src="/Style/images/loading.gif" style="padding-top: 12rem" />
      </div>
    </div>
    <jsp:include page="JSfooter.jsp" />
  </body>
</html>
