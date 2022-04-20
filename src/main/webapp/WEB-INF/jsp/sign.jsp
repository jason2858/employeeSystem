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
        <style>
            body {
                display: none;
                /*全部先隱藏*/
            }

            #map {
                height: 100%;
            }

            .gmap_canvas {
                overflow: hidden;
                background: none !important;
                width: 100%;
            }
        </style>
        <script type="text/javascript">
            var isFinishLoad = false;
            var Account = '<%out.print(session.getAttribute("Account"));%>';
            var authorise = '<%out.print(session.getAttribute("Authorise"));%>';
            var HRM = '<%out.print(session.getAttribute("HRM"));%>';
            $(document).ready(function () {
                $('#loading').modal('show');
                $("#loading").on('shown.bs.modal', function () {
                    isFinishLoad = true;
                });
                closeLoading();

                if (authorise != 1 && authorise != 2 && authorise != 3) {
                    window.location.href = "timeout.do";
                }
                if (authorise == 1 || authorise == 2 || authorise == 3) {
                    appendUnsign();
                    getUnsign();
                }

                getPunchRecords();
                getAttRecords();

                $('#PunchdataTable').DataTable({
                    "bAutoWidth": false,
                    "columnDefs": [{
                        "orderable": false,
                        "targets": 0
                    }, {
                        "orderable": false,
                        "targets": 2
                    }, {
                        "orderable": false,
                        "targets": 3
                    }, {
                        "orderable": false,
                        "targets": 6
                    }],
                    "order": [
                        [4, "desc"]
                    ],
                    "bLengthChange": false,
                    "pageLength": 10,
                    "oLanguage": {
                        "sProcessing": "讀取中...",
                        "sLengthMenu": "Show _MENU_ entries",
                        "sZeroRecords": "查無相符的資料",
                        "sEmptyTable": "無補打卡申請",
                        "sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
                        "sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
                        "sInfoFiltered": "(filtered from _MAX_ total entries)",
                        "sInfoPostFix": "",
                        "sSearch": "關鍵字搜尋:",
                        "sUrl": "",
                        "oPaginate": {
                            "sFirst": "第一頁",
                            "sPrevious": "上一頁",
                            "sNext": "下一頁",
                            "sLast": "最末頁"
                        }
                    }
                });

                $('#AttdataTable').DataTable({
                    "bAutoWidth": false,
                    "columnDefs": [{
                        "orderable": false,
                        "targets": 0
                    }, {
                        "orderable": false,
                        "targets": 2
                    }, {
                        "orderable": false,
                        "targets": 3
                    }, {
                        "orderable": false,
                        "targets": 7
                    }],
                    "order": [
                        [5, "desc"]
                    ],
                    "bLengthChange": false,
                    "pageLength": 10,
                    "oLanguage": {
                        "sProcessing": "讀取中...",
                        "sLengthMenu": "Show _MENU_ entries",
                        "sZeroRecords": "查無相符的資料",
                        "sEmptyTable": "無差勤申請",
                        "sInfo": "顯示 _START_ 到 _END_ 共 _TOTAL_ 筆資料",
                        "sInfoEmpty": "顯示 0 到 0 共 0  筆資料",
                        "sInfoFiltered": "(filtered from _MAX_ total entries)",
                        "sInfoPostFix": "",
                        "sSearch": "關鍵字搜尋:",
                        "sUrl": "",
                        "oPaginate": {
                            "sFirst": "第一頁",
                            "sPrevious": "上一頁",
                            "sNext": "下一頁",
                            "sLast": "最末頁"
                        }
                    }
                });
                $("body").show();

            });

            function getPunchRecords() {
                timeoutCheck();
                $.ajax({
                    type: 'post',
                    url: "./rest/sign/getUnsignPunchRecords",
                    datatype: 'json',
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {
                        var response = JSON.parse(data);
                        var record = JSON.parse(response.entity);
                        var t = $('#PunchdataTable').DataTable();
                        t.clear().draw();
                        var type = "",
                            position = "";
                        for (var i = 0; i < record.length; i++) {
                            type = "補上班打卡";
                            if (record[i]["type"] == "makeupout")
                                type = "補下班打卡";
                            record[i]["type"] = type;
                            position = "(" + record[i]["latitude"] + "," + record[i]["longitude"] + ")";
                            var temp = t.row.add(
                                ['<input type="checkbox" style="text-align-last: center; width: 32px" id="punchCheckBox_' + i + '" value=' + record[i] + '></input>',
                                record[i]["user"],
                                record[i]["department"],
                                    type,
                                record[i]["punchTime"],
                                record[i]["time"],
                                record[i]["note"].replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;"),
                                ])
                                .draw().nodes().to$();
                            $(temp).find('td:eq(6)').attr("style", "width:23%;word-wrap:break-word;word-break:break-all;");
                            $(temp).find('td:eq(6)').linkify();

                            for (var j = 1; j < 7; j++) {
                                $(temp).find('td:eq(' + j + ')').attr("class", "mouseover");
                                $(temp).find('td:eq(' + j + ')').attr("onclick", "punchDetail(" + JSON.stringify(record[i]) + ")");
                            }
                            $("#allCheckedPunch").attr("onclick", "allCheckedPunch(" + record.length + ")");
                            $("#unCheckedPunch").attr("onclick", "unCheckedPunch(" + record.length + ")");
                            $("#punchCheckboxSign").attr("onclick", "punchCheckboxSign(" + JSON.stringify(data) + ")");

                            // 依權限顯示補打卡勾選按鈕
                            if (authorise == 2) {
                                if (HRM == "true") {
                                    if (record["user"] != Account && record["department"] == "人資部") {
                                        $("#allCheckedPunch").show();
                                        $("#unCheckedPunch").show();
                                        $("#punchCheckboxSign").show();
                                    } else {
                                        $("#allCheckedPunch").show();
                                        $("#unCheckedPunch").hide();
                                        $("#punchCheckboxSign").hide();
                                    }
                                } else {
                                    $("#allCheckedPunch").show();
                                    $("#unCheckedPunch").hide();
                                    $("#punchCheckboxSign").hide();
                                }
                            } else {
                                $("#allCheckedPunch").show();
                                $("#unCheckedPunch").show();
                                $("#punchCheckboxSign").show();
                            }
                        }
                    },
                    error: function () {
                        confirm("訊息", "取得補打卡申請失敗");
                    }
                });
            };


            //	全部勾選
            function allCheckedPunch(length) {
                for (var i = 0; i < length; i++) {
                    $("#punchCheckBox_" + i).prop("checked", true);
                }
            }

            //	取消勾選
            function unCheckedPunch(length) {
                for (var i = 0; i < length; i++) {
                    $("#punchCheckBox_" + i).prop("checked", false);
                }
            }

            //	勾選全部簽核
            function punchCheckboxSign(punchData) {
                var checkedRecord = [];
                var response = JSON.parse(punchData);
                var record = JSON.parse(response.entity);
                for (var i = 0; i < record.length; i++) {
                    if ($("#punchCheckBox_" + i).is(":checked")) {
                        checkedRecord.push(i);
                    }
                }
                var data = [];
                checkedRecord.forEach(number => {
                    id = record[number]["id"];
                    data.push({
                        id: id,
                        status: "SIGNED"
                    });
                })

                $.confirm({
                    title: '簽核確認',
                    content: '確定簽核此補打卡申請?',
                    buttons: {
                        "簽核": {
                            btnClass: 'btn-green',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/multiCheckMakeUpUpdate",
                                    contentType: 'application/json',
                                    data: JSON.stringify(data),
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        checkResponse = response.entity;

                                        var currectNumber = 0;
                                        var error = 0
                                        var errorMsg = "";
                                        for (const id in checkResponse) {
                                            if (checkResponse[id] == 1) {
                                                currectNumber++;
                                            } else if (checkResponse[id] == 0) {
                                                error++;
                                                for (const i in checkedRecord) {
                                                    if (record[i]["id"] == id) {
                                                        type = "補上班打卡";
                                                        if (record[i]["type"] == "makeupout") {
                                                            type = "補下班打卡";
                                                        }
                                                        let err = error + ". 員工：" + record[i]["user"] +
                                                            "，類型：" + type +
                                                            "，補打卡時間：" + record[i]["punchTime"] +
                                                            " <br> ***該申請已被修改,刪除,簽核或駁回*** <br>";
                                                        errorMsg += err;
                                                    }
                                                }
                                            }
                                        }

                                        if (currectNumber == Object.keys(checkResponse).length & error == 0) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "簽核成功");
                                        } else if (error !== 0) {
                                            var returnMsg = "共 " + currectNumber + " 項補打卡簽核成功 <br>"
                                            returnMsg += errorMsg;
                                            confirm("訊息", returnMsg);
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "簽核失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                    },
                                    error: function () {
                                        confirm("訊息", "補打卡簽核失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }

            function punchDetail(record) {
                if (record["latitude"] == null) {
                    $("#noPosition").show();
                    $("#gmap_check").hide();
                } else {
                    $("#noPosition").hide();
                    $("#gmap_check").attr("src", 'https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=(' + record["latitude"] + '%2C' + record["longitude"] + ')&t=&z=15&ie=UTF8&iwloc=&output=embed');
                    $("#gmap_check").show();
                }
                $("#punchId").val(record["id"]);
                $("#punchName").html(record["user"]);
                $("#punchDep").html(record["department"]);
                $("#punchType1").html(record["type"]);
                $("#punchTime").html(record["punchTime"]);
                $("#punchCreate").html(record["time"]);
                $("#punchNote").html(record["note"].replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;"));
                if (authorise == 2) {
                    if (HRM == "true") {
                        if (record["user"] != Account && record["department"] == "人資部") {
                            $("#punchCommonDiv").show();
                        } else {
                            $("#punchCommonDiv").hide();
                        }
                    } else {
                        $("#punchCommonDiv").hide();
                    }
                } else {
                    $("#punchCommonDiv").show();
                }
                $("#punchRejectInput").hide();
                $("#punchRejectReason").val("");
                $("#punchRejectDiv").hide();
                $("#punchDetail").modal("show");
            }

            function showmap(inl, lon) {
                $("#gmap_canvas")
                    .attr(
                        "src",
                        "https://maps.google.com/maps?f=q&hl=zh-TW&geocode=&q=(" +
                        inl + "%2C" + lon +
                        ")&t=&z=15&ie=UTF8&iwloc=&output=embed");
                $("#map").modal("show");
            }

            function punchSignShow() {
                var id = $("#punchId").val();
                $.confirm({
                    title: '簽核確認',
                    content: '確定簽核此補打卡申請?',
                    buttons: {
                        "簽核": {
                            btnClass: 'btn-green',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/checkMakeUpAndUpdate",
                                    datatype: 'text',
                                    data: {
                                        id: id,
                                        status: "SIGNED"
                                    },
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        msg = response.entity;
                                        if (msg == 1) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "簽核成功");
                                        } else if (msg == 0) {
                                            confirm("訊息", "該申請已被刪除,簽核或駁回");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "簽核失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                        $("#punchDetail").modal("hide");
                                    },
                                    error: function () {
                                        confirm("訊息", "補打卡簽核失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }

            function punchRejectDivShow() {
                $("#punchCommonDiv").hide();
                $("#punchRejectInput").slideDown();
                $("#punchRejectDiv").show();
                $("#punchRejectReason").focus();
                $('#punchDetail').animate({
                    scrollTop: $("#punchDetail .modal-dialog").height()
                }, 500);
            }

            function punchRejectDivHide() {
                $("#punchCommonDiv").show();
                $("#punchRejectInput").slideUp();
                $("#punchRejectDiv").hide();
            }

            function punchRejectShow() {
                var id = $("#punchId").val();
                var reason = $("#punchRejectReason").val();
                if (reason == 0) {
                    confirm("訊息", "請填寫駁回原因");
                    return;
                }
                $.confirm({
                    title: '駁回確認',
                    content: '確定駁回此補打卡申請?',
                    buttons: {
                        "駁回": {
                            btnClass: 'btn-red',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/checkMakeUpAndUpdate",
                                    datatype: 'text',
                                    data: {
                                        id: id,
                                        reason: reason,
                                        status: "REJECTED"
                                    },
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        msg = response.entity;
                                        if (msg == 1) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "駁回成功");
                                        } else if (msg == 0) {
                                            confirm("訊息", "該申請已被刪除,簽核或駁回");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "駁回失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                        $("#punchDetail").modal("hide");
                                    },
                                    error: function () {
                                        confirm("訊息", "補打卡駁回失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }

            function getAttRecords() {
                timeoutCheck();
                $.ajax({
                    type: 'post',
                    url: "./rest/sign/getUnsignAttRecords",
                    datatype: 'json',
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {
                        var response = JSON.parse(data);
                        var record = JSON.parse(response.entity);
                        var t = $('#AttdataTable').DataTable();
                        t.clear().draw();
                        var type = "";
                        for (var i = 0; i < record.length; i++) {
                            var originaltype = record[i]["type"];
                            switch (originaltype) {
                                case 1:
                                    type = "";
                                    break;
                                case 2:
                                    type = "出差";
                                    break;
                                case 3:
                                    type = "特休";
                                    break;
                                case 4:
                                    type = "事假";
                                    break;
                                case 5:
                                    type = "病假";
                                    break;
                                case 6:
                                    type = "公假";
                                    break;
                                case 7:
                                    type = "婚假";
                                    break;
                                case 8:
                                    type = "喪假";
                                    break;
                                case 9:
                                    type = "加班";
                                    break;
                                case 10:
                                    type = "補休";
                                    break;
                                case 11:
                                    type = "外出";
                                    break;
                            }
                            record[i]["type"] = type;
                            var deputy = "";
                            if (record[i]["deputy"] != null) {
                                deputy = record[i]["deputy"];
                            }
                            record[i]["deputy"] = deputy;
                            var temp = t.row.add(
                                ['<input type="checkbox" style="text-align-last: center; width: 32px"  id="attCheckBox_' + i + '" value=' + record[i] + '></input>',
                                record[i]["user"],
                                record[i]["department"],
                                    type,
                                record[i]["startTime"],
                                record[i]["endTime"],
                                record[i]["createdAt"],
                                record[i]["time"]
                                ])
                                .draw().nodes().to$();

                            for (var j = 1; j < 8; j++) {
                                $(temp).find('td:eq(' + j + ')').attr("class", "mouseover");
                                $(temp).find('td:eq(' + j + ')').attr("onclick", "attDetail(" + JSON.stringify(record[i]) + ")");
                            }
                        }
                        $("#allCheckedAtt").attr("onclick", "allCheckedAtt(" + record.length + ")");
                        $("#unCheckedAtt").attr("onclick", "unCheckedAtt(" + record.length + ")");
                        $("#attCheckboxSign").attr("onclick", "attCheckboxSign(" + JSON.stringify(data) + ")");

                        // 依權限顯示差勤勾選按鈕
                        if (authorise == 2) {
                            if (HRM == "true") {
                                if (record["user"] != Account && record["department"] == "人資部") {
                                    $("#allCheckedAtt").show();
                                    $("#unCheckedAtt").show();
                                    $("#attCheckboxSign").show();
                                } else {
                                    $("#allCheckedAtt").show();
                                    $("#unCheckedAtt").hide();
                                    $("#attCheckboxSign").hide();
                                }
                            } else {
                                $("#allCheckedAtt").show();
                                $("#unCheckedAtt").hide();
                                $("#attCheckboxSign").hide();
                            }
                        } else {
                            $("#allCheckedAtt").show();
                            $("#unCheckedAtt").show();
                            $("#attCheckboxSign").show();
                        }
                    },
                    error: function () {
                        confirm("訊息", "取得差勤申請失敗");
                    }
                });

            };

            // 全部勾選
            function allCheckedAtt(length) {
                for (var i = 0; i < length; i++) {
                    $("#attCheckBox_" + i).prop("checked", true);
                }
            }

            // 取消勾選
            function unCheckedAtt(length) {
                for (var i = 0; i < length; i++) {
                    $("#attCheckBox_" + i).prop("checked", false);
                }
            }

            // 勾選全部簽核
            function attCheckboxSign(attData) {
                var checkedRecord = [];
                var response = JSON.parse(attData);
                var record = JSON.parse(response.entity);
                for (var i = 0; i < record.length; i++) {
                    if ($("#attCheckBox_" + i).is(":checked")) {
                        checkedRecord.push(i);
                    }
                }

                var data = [];
                checkedRecord.forEach(number => {
                    id = record[number]["id"];
                    updatedAt = record[number]["updatedAt"];
                    data.push({
                        id: id,
                        updatedAt: updatedAt,
                        status: "SIGNED"
                    });
                })

                $.confirm({
                    title: '勾選簽核確認',
                    content: '確定簽核勾選差勤申請?',
                    buttons: {
                        "簽核": {
                            btnClass: 'btn-green',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/multiCheckScheduleUpdata",
                                    contentType: 'application/json',
                                    data: JSON.stringify(data),
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        checkResponse = response.entity;

                                        var currectNumber = 0;
                                        var error = 0
                                        var errorMsg = "";
                                        for (const id in checkResponse) {
                                            if (checkResponse[id] == 1) {
                                                currectNumber++;
                                            } else if (checkResponse[id] == 0) {
                                                error++;
                                                for (const i in checkedRecord) {
                                                    if (record[i]["id"] == id) {
                                                        var originaltype = record[i]["type"];
                                                        switch (originaltype) {
                                                            case 1:
                                                                type = "";
                                                                break;
                                                            case 2:
                                                                type = "出差";
                                                                break;
                                                            case 3:
                                                                type = "特休";
                                                                break;
                                                            case 4:
                                                                type = "事假";
                                                                break;
                                                            case 5:
                                                                type = "病假";
                                                                break;
                                                            case 6:
                                                                type = "公假";
                                                                break;
                                                            case 7:
                                                                type = "婚假";
                                                                break;
                                                            case 8:
                                                                type = "喪假";
                                                                break;
                                                            case 9:
                                                                type = "加班";
                                                                break;
                                                            case 10:
                                                                type = "補休";
                                                                break;
                                                            case 11:
                                                                type = "外出";
                                                                break;
                                                        }
                                                        let err = error + ". 員工：" + record[i]["user"] +
                                                            "，類型：" + type +
                                                            "，時間：" + record[i]["startTime"] + " to " + record[i]["endTime"] +
                                                            " <br> ***該申請已被修改,刪除,簽核或駁回*** <br>";
                                                        errorMsg += err;
                                                    }
                                                }
                                            }
                                        }

                                        if (currectNumber == Object.keys(checkResponse).length & error == 0) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "簽核成功");
                                        } else if (error !== 0) {
                                            var returnMsg = "共 " + currectNumber + " 項差勤簽核成功 <br>"
                                            returnMsg += errorMsg;
                                            confirm("訊息", returnMsg);
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "簽核失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                    },
                                    error: function () {
                                        confirm("訊息", "差勤簽核失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }


            function attDetail(record) {
                $("#attId").val(record["id"]);
                $("#attUpdatedAt").val(record["updatedAt"]);
                $("#attName").html(record["user"]);
                $("#attDep").html(record["department"]);
                $("#attType").html(record["type"]);
                $("#attStart").html(record["startTime"]);
                $("#attEnd").html(record["endTime"]);
                $("#attCreate").html(record["createdAt"]);
                $("#attNote").html(record["note"].replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;"));
                if (record["deputy"].length > 0) {
                    $("#deputyDiv").show();
                    $("#attDeputy").html(record["deputy"]);
                } else {
                    $("#deputyDiv").hide();
                }
                if (record["time"].length > 0) {
                    $("#timeDiv").show();
                    $("#attTime").html(record["time"]);
                } else {
                    $("#timeDiv").hide();
                }
                var updatedAt = record["updatedAt"];
                if (authorise == 2) {
                    if (HRM == "true") {
                        if (record["user"] != Account && record["department"] == "人資部") {
                            $("#attCommonDiv").show();
                        } else {
                            $("#attCommonDiv").hide();
                        }
                    } else {
                        $("#attCommonDiv").hide();
                    }
                } else {
                    $("#attCommonDiv").show();
                }
                $("#attRejectInput").hide();
                $("#attRejectReason").val("");
                $("#attRejectDiv").hide();
                $("#attDetail").modal("show");
            }

            function attSignShow() {
                timeoutCheck();
                var id = $("#attId").val();
                var updatedAt = $("#attUpdatedAt").val();
                $.confirm({
                    title: '簽核確認',
                    content: '確定簽核此差勤申請?',
                    buttons: {
                        "簽核": {
                            btnClass: 'btn-green',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/checkScheduleAndUpdate",
                                    datatype: 'text',
                                    data: {
                                        id: id,
                                        updatedAt: updatedAt,
                                        status: "SIGNED"
                                    },
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        msg = response.entity;
                                        if (msg == 1) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "簽核成功");
                                        } else if (msg == 0) {
                                            confirm("訊息", "該申請已被修改,刪除,簽核或駁回 ");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "簽核失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                        $('#attDetail').modal('hide');
                                    },
                                    error: function () {
                                        confirm("訊息", "差勤簽核失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }

            function attRejectDivShow() {
                $("#attCommonDiv").hide();
                $("#attRejectInput").slideDown();
                $("#attRejectDiv").show();
                $("#attRejectReason").focus();
                $('#attDetail').animate({
                    scrollTop: $("#attDetail .modal-dialog").height()
                }, 500);
            }

            function attRejectDivHide() {
                $("#attCommonDiv").show();
                $("#attRejectInput").slideUp();
                $("#attRejectDiv").hide();
            }

            function attRejectShow() {
                timeoutCheck();
                var id = $("#attId").val();
                var updatedAt = $("#attUpdatedAt").val();
                var attReason = $("#attRejectReason").val();
                if (attReason == 0) {
                    confirm("訊息", "請填寫駁回原因");
                    return;
                }
                $.confirm({
                    title: '駁回確認',
                    content: '確定駁回此差勤申請?',
                    buttons: {
                        "駁回": {
                            btnClass: 'btn-red',
                            action: function () {
                                timeoutCheck();
                                $.ajax({
                                    type: 'post',
                                    url: "./rest/sign/checkScheduleAndUpdate",
                                    datatype: 'text',
                                    data: {
                                        id: id,
                                        updatedAt: updatedAt,
                                        reason: attReason,
                                        status: "REJECTED"
                                    },
                                    success: function (msg) {
                                        var response = JSON.parse(msg);
                                        msg = response.entity;
                                        if (msg == 1) {
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                            confirm("訊息", "駁回成功");
                                        } else if (msg == 0) {
                                            confirm("訊息", "該申請已被修改或簽核 ");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        } else {
                                            confirm("訊息", "駁回失敗");
                                            getPunchRecords();
                                            getAttRecords();
                                            getUnsign();
                                        }
                                        $('#attDetail').modal('hide');
                                    },
                                    error: function () {
                                        confirm("訊息", "差勤駁回失敗");
                                    }
                                });
                            }
                        },
                        "取消": {
                            action: function () {
                                return;
                            }
                        }
                    }
                });
            }
        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid">
                <div class="container-fluid">
                    <div class="card mb-3">
                        <div class="card-header">未簽核補打卡記錄</div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="PunchdataTable" width="100%" cellspacing="0">
                                    <thead>
                                        <tr bgcolor="#E6E6E6">
                                            <th>勾選</th>
                                            <th>員工姓名</th>
                                            <th>部門</th>
                                            <th>類別</th>
                                            <th>補打卡時間</th>
                                            <th>申請時間</th>
                                            <th>備註</th>
                                    </thead>
                                    <tbody>

                                    </tbody>
                                </table>
                                <button type="button" class="btn btn-secondary"
                                    style="margin-right: 5px; margin-left: 15px; background-color:#00559A;"
                                    id="allCheckedPunch">全部勾選</button>
                                <button type="button" class="btn btn-secondary" style="margin-right: 5px;"
                                    id="unCheckedPunch">取消勾選</button>
                                <button type="button" class="btn btn-success" id="punchCheckboxSign">勾選簽核</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="container-fluid">
                    <div class="card mb-3">
                        <div class="card-header">未簽核差勤記錄</div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered" id="AttdataTable" width="100%" cellspacing="0">
                                    <thead>
                                        <tr bgcolor="#E6E6E6">
                                            <th>勾選</th>
                                            <th>員工姓名</th>
                                            <th>部門</th>
                                            <th>類別</th>
                                            <th>起始時間</th>
                                            <th>結束時間</th>
                                            <th>申請日期</th>
                                            <th>時數</th>
                                    </thead>
                                    <tbody>

                                    </tbody>
                                </table>
                                <button type="button" class="btn btn-secondary"
                                    style="margin-right: 5px; margin-left: 15px; background-color:#00559A;"
                                    id="allCheckedAtt">全部勾選</button>
                                <button type="button" class="btn btn-secondary" style="margin-right: 5px;"
                                    id="unCheckedAtt">取消勾選</button>
                                <button type="button" class="btn btn-success" id="attCheckboxSign">勾選簽核</button>

                            </div>
                        </div>
                    </div>
                </div>
                <jsp:include page="footer.jsp" />
                <!-- Scroll to Top Button-->
                <a class="scroll-to-top rounded" href="#page-top"> <i class="fa fa-angle-up"></i>
                </a>
            </div>
        </div>

        <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
            data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
            <div class="modal-dialog">
                <img src="./Style/images/loading.gif" style="padding-top: 12rem;">
            </div>
        </div>

        <div class="modal fade" id="punchDetail" tabindex="-1" role="dialog" aria-labelledby="attDetailLabel"
            aria-hidden="true" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="MakeUpLabel">補打卡明細</h5>
                        <button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <input id="punchId" type="text" disabled="disabled" style="display: none">
                        <div id="gmap_err" class="gmap_canvas"
                            style="height: 133px; text-align: center; line-height: 133px;">
                            <b id="noPosition">無位置資訊</b>
                            <iframe width="100%" height="130" id="gmap_check" frameborder="0" scrolling="no"
                                marginheight="0" marginwidth="0"> </iframe>
                            <a href="https://www.crocothemes.net"></a>
                        </div>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">姓名
                                : </span><span class="short_text" style="margin-left: 3%;" id="punchName"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">部門
                                : </span><span class="short_text" style="margin-left: 3%;" id="punchDep"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
                                : </span><span class="short_text" style="margin-left: 3%;" id="punchType1"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">補打卡時間
                                : </span><span class="short_text" style="margin-left: 3%;" id="punchTime"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請時間
                                : </span><span class="short_text" style="margin-left: 3%;" id="punchCreate"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
                                : </span> <span id="punchNote"
                                style="display: block; width: 50%; word-wrap: break-word; word-break: break-all; margin-left: 33%;"></span>
                        </p>
                        <div id="punchRejectInput" style="display:none">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
                                : </span> <span class="short_text" style="margin-left: 3%;"><textarea rows="3"
                                    id="punchRejectReason" placeholder="請填寫駁回原因..."
                                    style="resize: none; width: 50%;"></textarea></span>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div id="punchCommonDiv">
                            <button class="btn btn-secondary" type="button" data-dismiss="modal"
                                style="margin-right: 5px;">取消</button>
                            <button class="btn btn-danger" type="button" onclick="punchRejectDivShow()"
                                style="margin-right: 5px;">駁回</button>
                            <button class="btn btn-success" type="button" onclick="punchSignShow()">簽核</button>
                        </div>
                        <div id="punchRejectDiv" style="display:none">
                            <button class="btn btn-secondary" type="button" onclick="punchRejectDivHide()"
                                style="margin-right: 5px;">取消</button>
                            <button class="btn btn-danger" type="button" onclick="punchRejectShow()"
                                style="margin-right: 5px;">駁回</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="modal fade" id="attDetail" tabindex="-1" role="dialog" aria-labelledby="attDetailLabel"
            aria-hidden="true" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="MakeUpLabel">差勤明細</h5>
                        <button class="close" id="closeUandD" type="button" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <input id="attId" type="text" disabled="disabled" style="display: none"> <input
                            id="attUpdatedAt" type="text" disabled="disabled" style="display: none">
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">姓名
                                : </span><span class="short_text" style="margin-left: 3%;" id="attName"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">部門
                                : </span><span class="short_text" style="margin-left: 3%;" id="attDep"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">類別
                                : </span><span class="short_text" style="margin-left: 3%;" id="attType"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">起始時間
                                : </span><span class="short_text" style="margin-left: 3%;" id="attStart"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">結束時間
                                : </span><span class="short_text" style="margin-left: 3%;" id="attEnd"></span>
                        </p>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請日期
                                : </span><span class="short_text" style="margin-left: 3%;" id="attCreate"></span>
                        </p>
                        <div style="display: none;" id="deputyDiv">
                            <p style="padding-top: 1%;">
                                <span class="short_label"
                                    style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">代理人
                                    : </span><span class="short_text" style="margin-left: 3%;" id="attDeputy"></span>
                        </div>
                        <div style="display: none;" id="timeDiv">
                            <p style="padding-top: 1%;">
                                <span class="short_label"
                                    style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">申請時數
                                    : </span> <span class="short_text" style="margin-left: 3%;" id="attTime"></span>
                        </div>
                        <p style="padding-top: 1%;">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">備註
                                : </span> <span id="attNote"
                                style="display: block; width: 50%; word-wrap: break-word; word-break: break-all; margin-left: 33%;"></span>
                        </p>
                        <div id="attRejectInput" style="display:none">
                            <span class="short_label"
                                style="text-align: right; width: 30%; display: block; vetical-align: top; float: left;">駁回原因
                                : </span> <span class="short_text" style="margin-left: 3%;"><textarea rows="3"
                                    id="attRejectReason" placeholder="請填寫駁回原因..."
                                    style="resize: none; width: 50%;"></textarea></span>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <div id="attCommonDiv">
                            <button class="btn btn-secondary" type="button" data-dismiss="modal"
                                style="margin-right: 5px;">取消</button>
                            <button class="btn btn-danger" type="button" onclick="attRejectDivShow()"
                                style="margin-right: 5px;">駁回</button>
                            <button class="btn btn-success" type="button" onclick="attSignShow()">簽核</button>
                        </div>
                        <div id="attRejectDiv" style="display:none">
                            <button class="btn btn-secondary" type="button" onclick="attRejectDivHide()"
                                style="margin-right: 5px;">取消</button>
                            <button class="btn btn-danger" type="button" onclick="attRejectShow()"
                                style="margin-right: 5px;">駁回</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="JSfooter.jsp" />
    </body>

    </html>