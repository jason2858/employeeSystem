# CheckLister

checkList + itemArea

## Dependency

- jQuery
- Bootstrap 4 (just use `form-control` class)

## Start

### javascript

```js
const checkLister = useCheckLister({
  data: [
    {
      cName: "資產類",
      itemList: [
        {
          iId: "1000",
          iName: "壹零零零",
        },
      ],
    },
    {
      cName: "負債類",
      itemList: [
        {
          iId: "2020",
          iName: "二零二零",
        },
      ],
    },
  ],
  checkLister: {
    el: "checkLister_el",
    MAX_SELECTED_NUM: 10,
    input_placeholder: "請輸入",
  },
  itemArea: {
    el: "itemArea_el",
    delIconPath: "../delete/icon/path.png",
  },
});
```

### HTML

1. checkLister

```HTML
<div class="form_search">
    <label for="checkLister_el">...</label>
    <div class="drop">
        <input
            type="text"
            id="checkLister_el"
            class="form-control"
            placeholder=""
            autocomplete="off"
        />
        <ul class="drop-menu"></ul>
    </div>
</div>
```

2. itemArea

```HTML
<section id="itemArea_el" class="item_area">
  <div class="header">
    <div class="title">
      <h2>...</h2>
    </div>
    <div class="btns">
      <a class="clean" href="javascript:;">清除全部</a>
    </div>
  </div>
  <div class="content">
      <ul class="list"></ul>
  </div>
  <div class="infos">
      <p>共選取 <span class="total"> 0 </span> 個項目</p>
  </div>
</section>
```
