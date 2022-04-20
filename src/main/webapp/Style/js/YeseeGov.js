/*側邊攔點亮*/
$(document).ready(function () {
  var url = location.pathname.slice(1);
  var selector = document.getElementById(url);
  $(selector).css("background-color", "rgb(64,72,130)");
  $(selector).css("color", "rgb(255,255,255)");
  var parent = $(selector).parent().parent().parent();
  if ($(parent)[0] != null) {
    if ($(parent)[0].tagName == "LI") {
      $(selector).parent().parent().collapse("toggle");
      $(parent).css("background-color", "rgb(64,72,130)");
      $(parent).find("a:eq(0)").css("color", "rgb(255,255,255)");
    }
  }
  //控制 scroll 高度
  const sidebar_infos = sessionStorage.getItem("sidebar_infos");
  if (sidebar_infos) {
    const obj = JSON.parse(sidebar_infos);
    const height = +obj.scrollTop;
    setTimeout(() => {
      $("#sidenav").scrollTop(height);
    }, 800);
  }
  getPunchStatus(url);

  var announceCheck = $("#announceCheck").val();
  if ("Y" == announceCheck) {
    getUnreadAnnounce();
  }
});

window.addEventListener("beforeunload", function (event) {
  const data = {
    scrollTop: $("#sidenav").scrollTop(),
  };
  sessionStorage.setItem("sidebar_infos", JSON.stringify(data));
});

/*關閉loading modal*/
function closeLoading() {
  var tid;
  $(document).ajaxStop(function () {
    if (isFinishLoad) {
      $("#loading").modal("hide");
    } else {
      var tid = setInterval(checkLoad, 100);
    }
  });

  function checkLoad() {
    if (isFinishLoad) {
      $("#loading").modal("hide");
      clearInterval(tid);
    }
  }
}

function confirm(title, msg) {
  $.confirm({
    title: title,
    content: msg,
    draggable: false,
    buttons: {
      確認: function () {},
    },
  });
}

function confirmWithFunction(title, msg, func) {
  $.confirm({
    title: title,
    content: msg,
    draggable: false,
    buttons: {
      確認: function () {
        func();
      },
    },
  });
}

function reloadConfig() {
  $.ajax({
    type: "post",
    url: "/rest/login/reloadConfig",
    datatype: "json",
    success: function (msg) {
      confirm("訊息", "Config重置成功");
    },
    error: function () {},
  });
}

/*取得當下狀態*/
function getPunchStatus(url) {
  $.ajax({
    type: "post",
    url: "/rest/punch/getPunchStatus",
    datatype: "json",
    success: function (type) {
      if (type == "in" || type == "makeupin") {
        if (url == "punch_management.do") $("#status").html("現在狀態 : 上班");
        $("#punchType").html("現在狀態 : 上班");
      } else if (type == "out" || type == "makeupout") {
        if (url == "punch_management.do") $("#status").html("現在狀態 : 下班");
        $("#punchType").html("現在狀態 : 下班");
      } else {
        if (url == "punch_management.do")
          $("#status").html("現在狀態 : 未到班");
        $("#punchType").html("現在狀態 : 未到班");
      }
    },
    error: function () {
      confirm("訊息", "取得打卡狀態失敗");
    },
  });
}

var scheduleUnsign;
var projectUnsign;
var cunstomerUnsign;
var Authorise = $("#getAuthorise").val();

/*取得未簽核數量*/
function getUnsign() {
  $.ajax({
    type: "post",
    url: "/rest/login/getUnsign",
    datatype: "text",
    success: function (data) {
      var record = JSON.parse(data);
      if (data != 0) {
        scheduleUnsign = record.entity.attCount;
        projectUnsign = record.entity.projectAndCustomer.detail.projectCount;
        customerUnsign = record.entity.projectAndCustomer.detail.customerCount;
        if (scheduleUnsign > 0) {
          $("#Unsign").css("display", "inline-block");
          $("#unsign").html(record.entity.attCount);
          $("#SchedulesUnsign").css("display", "inline-block");
          $("#schedulesUnsign").html(scheduleUnsign);
        } else {
          $("#Unsign").hide();
          $("#SchedulesUnsign").hide();
        }
        if (!$("#signManagement").hasClass("collapsed")) {
          $("#Unsign").hide();
        }
        if (Authorise == 1) {
          if (record.entity.projectAndCustomer.sum > 0) {
            $("#ProjectUnsign").css("display", "inline-block");
            $("#projectUnsign").html(record.entity.projectAndCustomer.sum);
          } else {
            $("#ProjectUnsign").hide();
          }
          if (!$("#projectManagement").hasClass("collapsed")) {
            $("#ProjectUnsign").hide();
          }
          if (projectUnsign > 0) {
            $("#ProjectsUnsign").css("display", "inline-block");
            $("#projectsUnsign").html(projectUnsign);
          } else {
            $("#ProjectsUnsign").hide();
          }
          if (customerUnsign > 0) {
            $("#CustomersUnsign").css("display", "inline-block");
            $("#customersUnsign").html(customerUnsign);
          } else {
            $("#CustomersUnsign").hide();
          }
        }
      }
    },
    error: function () {},
  });
}

$("#signManagement").on("click", function () {
  if (scheduleUnsign != 0) {
    if ($("#signManagement").hasClass("collapsed")) {
      $("#SchedulesUnsign").show();
      $("#Unsign").hide();
    } else {
      $("#SchedulesUnsign").hide();
      $("#Unsign").show();
    }
  }
});

$("#projectManagement").on("click", function () {
  if (Authorise == 1) {
    if ($("#projectManagement").hasClass("collapsed")) {
      if (projectUnsign != 0) {
        $("#ProjectsUnsign").show();
      }
      if (customerUnsign != 0) {
        $("#CustomersUnsign").show();
      }
      $("#ProjectUnsign").hide();
    } else {
      $("#ProjectsUnsign").hide();
      $("#CustomersUnsign").hide();
      if (projectUnsign != 0 || customerUnsign != 0) {
        $("#ProjectUnsign").show();
      }
    }
  }
});

/*登出*/
function logout() {
  $.ajax({
    type: "post",
    url: "/rest/login/logoutDo",
    datatype: "json",
    success: function (data) {
      window.location.href = "/";
    },
    error: function () {},
  });
}

/*逾時判斷*/
function timeoutCheck() {
  $.ajax({
    type: "post",
    url: "/rest/login/timeoutCheck",
    dataType: "text",
    async: false,
    success: function (msg) {
      if (msg == "null") {
        confirmWithFunction("訊息", "連線逾時，請重新登入", function () {
          window.location.href = "/";
        });
      }
    },
    error: function () {},
  });
}

function appendUnsign() {
  $("#signManagement").append(
    "<span id = 'Unsign' style='border-radius: 50%;height: 20px;width: 20px; display: none; background: rgb(237,28,36);vertical-align: top;margin-left:45%;'>" +
      "<span id = 'unsign' style='display: block;color: #FFFFFF;height: 20px;line-height: 20px;text-align: center'>" +
      "</span>" +
      "</span>",
  );
  $("#sign\\.do").append(
    "<span id = 'SchedulesUnsign' style='border-radius: 50%;height: 20px;width: 20px; display: none; background: rgb(237,28,36);vertical-align: top;margin-left:45%;'>" +
      "<span id = 'schedulesUnsign' style='display: block;color: #FFFFFF;height: 20px;line-height: 20px;text-align: center'>" +
      "</span>" +
      "</span>",
  );
  $("#projectManagement").append(
    "<span id = 'ProjectUnsign' style='border-radius: 50%;height: 20px;width: 20px; display: none; background: rgb(237,28,36);vertical-align: top;margin-left:31%;'>" +
      "<span id = 'projectUnsign' style='display: block;color: #FFFFFF;height: 20px;line-height: 20px;text-align: center'>" +
      "</span>" +
      "</span>",
  );
  $("#project_management\\.do").append(
    "<span id = 'ProjectsUnsign' style='border-radius: 50%;height: 20px;width: 20px; display: none; background: rgb(237,28,36);vertical-align: top;margin-left:53%;'>" +
      "<span id = 'projectsUnsign' style='display: block;color: #FFFFFF;height: 20px;line-height: 20px;text-align: center'>" +
      "</span>" +
      "</span>",
  );
  $("#customer\\.do").append(
    "<span id = 'CustomersUnsign' style='border-radius: 50%;height: 20px;width: 20px; display: none; background: rgb(237,28,36);vertical-align: top;margin-left:53%;'>" +
      "<span id = 'customersUnsign' style='display: block;color: #FFFFFF;height: 20px;line-height: 20px;text-align: center'>" +
      "</span>" +
      "</span>",
  );
}

function getUnreadAnnounce() {
  $.ajax({
    type: "post",
    url: "/rest/login/getUnreadAnnounce",
    dataType: "text",
    async: false,
    success: function (data) {
      var response = JSON.parse(data);
      var record = JSON.parse(response.entity);
      if (record.length > 0) {
        var id = record[0]["id"];
        var modal =
          '<div class="modal fade" id="unreadModal" tabindex="-1" role="dialog" aria-labelledby="unreadLabel" aria-hidden="true" data-backdrop="static">' +
          '<div class="modal-dialog modal-lg" role="document" style="overflow-y: initial;">' +
          '<div class="modal-content">' +
          '<div class="modal-header">' +
          '<h5 class="modal-title">' +
          "未讀公告" +
          "</h5>" +
          '<button class="close" type="button" data-dismiss="modal" aria-label="Close">' +
          '<span aria-hidden="true">' +
          "x" +
          "</span>" +
          "</button>" +
          "</div>" +
          '<div class="modal-body" style="height: 400px; overflow-y:auto;">';
        for (var i = 0; i < record.length; i++) {
          if (i != 0) {
            modal += "<br>" + "<br>" + '<hr size="8">';
          }
          modal +=
            '<h5 align="center">' +
            record[i]["subject"] +
            "</h5>" +
            '<h6 align="center">' +
            "公告日期 :" +
            record[i]["time"] +
            "</h6>" +
            record[i]["content"]
              .replace(/\r\n/g, "<br>")
              .replace(/\n/g, "<br>")
              .replace(/\s/g, "&nbsp;");
        }
        modal +=
          "</div>" +
          '<div class="modal-footer">' +
          '<button class="btn btn-secondary" type="button" onclick="read(1,' +
          id +
          ')" style="margin-right: 5px;">前往公告頁</button>' +
          '<button class="btn btn-success" type="button" onclick="read(2,' +
          id +
          ')" style="margin-right: 5px;">確認已讀</button>' +
          "</div>" +
          "</div>" +
          "</div>" +
          "</div>";
        $("body").append(modal);
        $("#unreadModal").linkify();
        $("#unreadModal").modal("show");
      }
    },
    error: function () {},
  });
}

function read(x, id) {
  $.ajax({
    type: "post",
    url: "/rest/login/read",
    dataType: "text",
    data: { id: id },
    success: function (msg) {
      if (x == "1") {
        window.location.href = "/announce.do";
      } else {
        $("#unreadModal").modal("hide");
      }
    },
    error: function () {},
  });
}
