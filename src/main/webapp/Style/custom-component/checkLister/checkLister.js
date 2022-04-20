function useCheckLister(
  infos = {
    data: [
      {
        cName: "資產類",
        itemList: [
          {
            selected: false,
            iId: "",
            iName: "",
          },
        ],
      },
    ],
    checkList: {
      el: "", //bind ID key
      MAX_SELECTED_NUM: 10,
      input_placeholder: "",
      div_class: "drop",
      input_class: "form-control",
      input_style: ""
    },
    itemArea: {
      el: "",
      delIconPath: "../../Style/images/del.png",
      area_title_class: "title",
      area_title_style: "",
      area_clear_btn_class: "clean",
    },
  },
) {
  let { data, checkList, itemArea } = infos;
  data = JSON.parse(JSON.stringify(data));
  // checkLister
  let itemAreaIsBind = false;
  let drop = null;
  let drop_menu = null;
  //template
  //1 dropbox
  const DROP_TEMPLATE = `
    <label for="drop-input">項目 : </label>
    <div class="${checkList.div_class || 'drop'}">
        <input
            type="text"
            id="drop-input"
            class="${checkList.input_class || 'form-control'}"
            style="${checkList.input_style}"
            placeholder="${checkList.input_placeholder}"
            autocomplete="off"
        />
        <ul class="drop-menu"></ul>
    </div>
  `;
  //2 item area
  const ITEM_AREA_TEMPLATE = `
      <div class="header">
        <div class="${itemArea.area_title_class || 'title'}">
          <h2
            class="${itemArea.area_title_class}"
            style="${itemArea.area_title_style}"
          >
            項目區
          </h2>
        </div>
        <div class="btns">
        <a class="${itemArea.area_clear_btn_class || 'clean'}" href="javascript:;">
          清除項目區
        </a>
      </div>
      </div>
      <div class="content">
        <ul class="list"></ul>
      </div>
      <div class="infos">
        <p>共選取 <span class="total"> 0 </span> 個項目</p>
      </div>
  `;
  //1. fn: 改變 item 勾選狀態 from hedge_num
  function setItemStatus(item) {
    const { iId, selected } = item;
    if (iId) {
      data.forEach((item) => {
        item.itemList.forEach((item) => {
          if (item.iId === iId) item.selected = selected;
        });
      });
    }
  }
  //2: fn: 取得總勾選數量
  function getTotalItemSelected() {
    return data.reduce((total, item) => {
      const item_list_total = item.itemList.reduce((total, item) => {
        if (item.selected) ++total;
        return total;
      }, 0);
      total += +item_list_total;
      return total;
    }, 0);
  }
  //3: fn: 取得某個主選項底下未勾選的項目總數
  function getUnselectedItemByCName(cName = "") {
    if (cName) {
      const filter = data.filter((item) => item.cName === cName);
      const row = filter[0].itemList;
      return row.reduce((total, item) => {
        if (!item.selected) ++total;
        return total;
      }, 0);
    }
  }
  //VIEW
  //渲染包含 keyword 的項目編號及項目名稱
  function renderSearchItem(keyword = "") {
    let dom = "";
    data.forEach((item) => {
      const filter = item.itemList.filter(
        (item) => item.iName.includes(keyword) || item.iId.includes(keyword),
      );
      if (filter.length > 0) {
        const cName = item.cName;
        let firstNode = `<label>
                            <input 
                                type="checkbox" 
                                ${
                                  getUnselectedItemByCName(cName)
                                    ? ""
                                    : "checked"
                                } 
                                name="search_item_cName" 
                                value="${cName}" 
                            />
                            ${cName}             
                        </label>`;

        const checkbox = filter.reduce((total, item) => {
          //渲染 checkbox 勾選與否
          return (total += `
                            <label>
                                <input
                                    ${item.selected ? "checked" : ""}
                                    type="checkbox"
                                    name="search_item"
                                    data-c-name="${cName}"
                                    data-i-name="${item.iName}"
                                    value="${item.iId}"
                                />
                                ${item.iName}
                            </label>`);
        }, "");
        dom += ` <li class="drop-item">${firstNode}${checkbox}</li>`;
      }
    });
    // 沒內容就去掉 border
    if (dom) drop_menu.classList.remove("border-0");
    else drop_menu.classList.add("border-0");
    drop_menu.innerHTML = dom;
  }
  //init check_list
  function bindCheckListEvent() {
    if (!Array.isArray(data[0].itemList)) return console.log("data格式錯誤");
    if (checkList.el.slice(0, 1) !== "#") checkList.el = "#" + checkList.el;
    $(checkList.el).html(DROP_TEMPLATE);

    drop = document.querySelector("#drop-input");
    drop_menu = document.querySelector("#drop-input + ul");
    //1 輸入文字時出現對應選項
    drop.addEventListener("input", function (e) {
      const word = this.value;
      renderSearchItem(word);
    });
    //2-1 控制 drop 顯示
    //2-2-1 開啟、保持開啟
    drop.addEventListener("click", (e) => {
      e.stopPropagation();
      drop_menu.classList.toggle("show");
    });
    drop_menu.addEventListener("click", (e) => {
      e.stopPropagation();
    });
    //2-2 隱藏
    window.addEventListener("click", (e) => {
      drop_menu.classList.remove("show");
    });
    //3 下拉 item 功能
    //3-1 勾選 item 子項目(檢查總數，更改資料)
    drop_menu.addEventListener("click", (e) => {
      if (e.target.nodeName === "INPUT" && e.target.dataset.iName) {
        const MAX_SELECTED_NUM = checkList.MAX_SELECTED_NUM;
        const totalSelected = getTotalItemSelected();

        const checked = e.target.checked;
        const cName = e.target.dataset.cName;
        const iId = e.target.value;
        const selected = e.target.checked;

        // 超過數量
        if (checked && totalSelected >= MAX_SELECTED_NUM) {
          confirm("訊息", `最多選取項目為${MAX_SELECTED_NUM}項`);
          e.target.checked = false;
          return;
        }
        setItemStatus({
          cName,
          iId,
          selected,
        });
        if (itemAreaIsBind) renderItemArea();
      }
    });
    drop_menu.addEventListener("click", (e) => {
      if (e.target.getAttribute("name") === "search_item_cName") {
        const checked = e.target.checked;
        const cName = e.target.value;
        //勾全選:判斷剩餘勾選扣打 -> 更改資料 -> 渲染畫面
        if (checked) {
          const totalSelected = getTotalItemSelected();
          const MAX_SELECTED_NUM = checkList.MAX_SELECTED_NUM;
          if (totalSelected >= MAX_SELECTED_NUM) {
            e.target.checked = false;
            confirm("訊息", `項目最多勾選數量為${MAX_SELECTED_NUM}`);
            return;
          } else {
            const quota = MAX_SELECTED_NUM - +totalSelected;
            const totalUnselected = getUnselectedItemByCName(cName);
            const filter = data.filter((item) => item.cName === cName);
            const row = filter[0].itemList;
            const unselected = row.filter((item) => item.selected === false);
            //如果扣打 > 剩餘未選取數量 -> 全部選取
            if (quota > totalUnselected) {
              unselected.forEach((item) => {
                item.selected = true;
              });
            } else {
              //如果扣打不夠勾完全不，盡力把剩下的勾完
              for (let i = 0; i < quota; i++) {
                unselected[i].selected = true;
              }
            }
          }
        } else {
          data.forEach((item) => {
            if (item.cName === cName) {
              item.itemList.forEach((item) => {
                item.selected = false;
              });
            }
          });
        }
        renderSearchItem();
        if (itemAreaIsBind) renderItemArea();
      }
    });

    //將所有 itemList 內先加上selected: false
    data.forEach((item) => {
      item.itemList.forEach((item) => (item.selected = false));
    });
    renderSearchItem();
  }
  //待新增：return 被選取的項目

  //項目區功能
  //init item_area
  function bindItemAreaEvent() {
    if (itemArea.el.slice(0, 1) !== "#") itemArea.el = "#" + itemArea.el;
    $(itemArea.el).html(ITEM_AREA_TEMPLATE);
    console.log();
    //clean
    $(`${itemArea.el} .clean`).on("click", (e) => {
      //1 清掉資料
      data.forEach((item) => {
        item.itemList.forEach((innerItem) => {
          innerItem.selected = false;
        });
      });
      //2 重新渲染
      renderSearchItem();
      renderItemArea();
    });
    //del single item
    $(`${itemArea.el} .list`).on("click", (e) => {
      if (e.target.nodeName === "IMG") {
        //1.刪去資料 2. 重新渲染DOM(下拉選單及項目區)
        setItemStatus({
          iId: e.target.dataset.iId,
          selected: false,
        });
        //2.重新render
        renderSearchItem();
        renderItemArea();
      }
    });
    itemAreaIsBind = true;
  }
  //VIEW
  //fn1: 根據data渲染勾選項目
  function renderItemArea() {
    let dom = "";
    let count = 0;
    const totalDom = document.querySelector(`${itemArea.el} .total`);
    const listDom = document.querySelector(`${itemArea.el} .list`);

    data.forEach((row) => {
      const cName = row.cName;
      const nodes = row.itemList.reduce((total, item) => {
        if (item.selected) {
          count++;
          dom += `
                          <li class="list-item">
                              <a href="javascript:;" class="del_icon" ">
                                  <img 
                                      src="${itemArea.delIconPath}" 
                                      data-i-id="${item.iId}" 
                                      data-c-name="${cName}"
                                  >
                              </a>
                              <p>
                                  ${item.iId}  ${item.iName}
                              </p>
                          </li>
                          `;
          return total;
        }
      }, "");
    });
    totalDom.textContent = count;
    listDom.innerHTML = dom;
  }

  //return function
  function getSelectedItem() {
    return data.reduce((total, item) => {
      let items = item.itemList.reduce((total, item) => {
        if (item.selected) total.push(item);
        return total;
      }, []);

      total.push(...items);
      return total;
    }, []);
  }
  //init All
  (function init() {
    bindCheckListEvent();
    bindItemAreaEvent();
  })();
  return {
    getSelectedItem,
  };
}
