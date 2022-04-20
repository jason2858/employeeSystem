
/*
 * @Description Ajax datalist
 */

class Datalist {
    constructor() {
        this.data = {};
        this.keys = {};
    }

    updateData(listID = '', data = [], keys = []) {
        this.data[listID] = data;
        this.keys[listID] = keys;
    }

    setDatalist(listID, keys = [], skipKey, skipVal = '') {
        const datalistDOM = document.createElement('datalist');
        let options = '';

        this.data[listID].forEach(item => {
            if (skipKey === undefined || item[skipKey] !== skipVal) {
                options += `<option>${keys.map(key => item[key]).join(' ')}</option>`;
            }
        })
        datalistDOM.innerHTML = options;
        datalistDOM.setAttribute('id', listID);
        document.body.append(datalistDOM);
    }

    getProjects(listID) {
        $.ajax({
            type:'post',
            url:"/rest/workItem/getProjects",
            datatype:'json',
            success: (data) => {
                const response = JSON.parse(data);
                const record = JSON.parse(response.entity);
                const keys = ['name'];

                this.updateData(listID, record, keys);
                this.setDatalist(listID, keys, 'deleted', 'Y');
            },
            error() {
                confirm('訊息', '取得專案清單失敗');
            },
        });
    }

    getEmployee(listID) {
        $.ajax({
            type: 'post',
            url: '/rest/attendance/nameFilter',
            datatype: 'json',
            success: (data) => {
                const record = JSON.parse(data.entity);
                const keys = ['chineseName'];

                this.updateData(listID, record, keys);
                this.setDatalist(listID, keys);
            },
            error() {
                confirm('訊息', '取得員工資料失敗');
            },
        });
    }

    getItemDrop(listID) {
        $.ajax({
            type: 'get',
            url: '/rest/accounting/itemDrop',
            datatype: 'json',
            success: (data) => {
                const record = JSON.parse(data).data;
                const keys = ['iId', 'iName'];

                this.updateData(listID, record, keys);
                this.setDatalist(listID, keys, 'enable', 'N');
            },
            error() {
                confirm('訊息', '取得員工資料失敗');
            },
        });
    }

    record(listID, val) {
        const value = val.split(' ')[0];

        return this.data[listID].find(item => {
            for (let i = 0; i < this.keys[listID].length; i += 1) {
                const key = this.keys[listID][i];
                if (item[key] === value) return true;
            }
        });
    }
}
