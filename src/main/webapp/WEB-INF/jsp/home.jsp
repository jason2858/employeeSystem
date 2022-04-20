<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <jsp:include page="header.jsp" />
        <!-- select style plugin -->
        <link href="./Style/css/homeSelect.css" rel="stylesheet" type="text/css">
        <link href="./Style/css/event.css" rel="stylesheet" type="text/css">

        <!-- jQuery & css file for fullcalender, bootstrap -->
        <link href="./Style/vendor/fullcalendar/css/fullcalendar.min.css" rel="stylesheet" type="text/css">
        <link href="./Style/vendor/fullcalendar/css/fullcalendar.css" rel="stylesheet" type="text/css">
        <link href="./Style/vendor/fullcalendar/css/fullcalendar.print.min.css" rel="stylesheet" media="print" type="text/css">
        <script src="./Style/vendor/fullcalendar/js/moment.min.js"></script>
        <script src="./Style/vendor/fullcalendar/js/fullcalendar.min.js"></script>
        <script src="./Style/vendor/fullcalendar/js/zh-tw.js"></script>

        <script src="./Style/vendor/Animated-Cross-Device-jQuery-Select-Box-Replacement-sumoselect/jquery.sumoselect.js"></script>
        <link href="./Style/vendor/Animated-Cross-Device-jQuery-Select-Box-Replacement-sumoselect/sumoselect.css" rel="stylesheet" />
        <script type="text/javascript">
            var isFinishLoad = false;
            var calendarEvent = [];
            var count = 0;
            var authorise = "${Authorise}";
            var nameSelect = "${nameSelect}"

            $(document).ready(function() {
                $('#loading').modal('show');
                $("#loading").on('shown.bs.modal', function() {
                    isFinishLoad = true;
                });

                if (authorise != 1 && authorise != 2 && authorise != 3 &&
                    authorise != 4) {
                    window.location.href = "timeout.do";
                }
                if (authorise == 1 || authorise == 2 || authorise == 3) {
                    appendUnsign();
                    getUnsign();
                }

                $.ajax({
                    type: 'get',
                    url: './rest/calendar/records',
                    datatype: 'json',
                    success: function(data) {
                        calendarEvent = data;
                    },
                    error: function() {
                        confirm("訊息", "取得事件失敗");
                    }
                });

                $(document).ajaxStop(function() {
                    $('#calendar').fullCalendar({
                        header: {
                            left: 'prev,next today',
                            center: 'title',
                            right: 'month,agendaWeek,agendaDay,listWeek'
                        },
                        editable: false,
                        navLinks: true, // can click day/week names to navigate views
                        eventLimit: true, // allow "more" link when too many events
                        eventSources: [{
                            events: calendarEvent,
                            color: 'yellow', // a non-ajax option
                            textColor: 'black'
                        }],
                        eventRender: function(event, element, view) {
                            //element is <a>something here</a> of <div class = fc-event-container />
                            element.attr('data-title', event.title);
                            element.attr('data-description', event.description);
                            element.find('.fc-title').after("<span> " + event.description + "</span>");

                            if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
                                //	is mobile..
                                $('.fc-today-button').hide();
                                $('.fc-month-button').hide();
                                $('.fc-agendaWeek-button').hide();
                                $('.fc-agendaDay-button').hide();
                                $("#forDisplaySelect").addClass("ml-auto");
                                $("#forDisplaySelect2").addClass("ml-auto");
                                $("#forDisplaySelect").appendTo(".flex-column");
                                $("#forDisplaySelect2").appendTo(".flex-column");
                                $('#calendar').fullCalendar('changeView', 'listWeek');
                                $(".fc-center > h2").css("font-size", "1.2em");
                            }

                            var dateString = event.start.format("YYYY-MM-DD");
                            var H_style = $(view.el[0]).find('.fc-day[data-date=' + dateString + ']');
                            /*! Holidaytype 1.假日 2.補班	3.公司活動	4.其他*/

                            if (event.type != null) {
                                element.find(".fc-time").remove();
                                $(element).find(".fc-list-item-title").append("<div>" + event.description + "</div>");
                            }

                            if (event.type == '2' || event.type == '11') {
                                H_style.addClass('ys-makeup');
                                element.addClass('ys-makeup');
                            } else if (event.type == '3') {
                                H_style.addClass('ys-event');
                                element.addClass('ys-event');
                            } else if (event.type == '1') {
                                H_style.addClass('ys-holiday');
                                element.addClass('ys-holiday');
                            } else if (event.type == '4') {
                                H_style.addClass('ys-other');
                                element.addClass('ys-other');
                            } else if (event.description == '出差') {
                                element.addClass('ys-travel');
                            } else if (event.description == '加班') {
                                element.addClass('overtime');
                            } else {
                                element.addClass('ys-leave');
                            }
                            element.css("background-color", "");
                            element.css("border-color", "");

                            if (element && event.note) {
                                element.qtip({
                                    content: event.note.replace(/\r\n/g, "<br>").replace(/\n/g, "<br>").replace(/\s/g, "&nbsp;"),
                                    hide: {
                                        fixed: true,
                                        delay: 300
                                    }
                                });
                            }
                        },
                        eventAfterAllRender: function(view) {
                            //六日background-color
                            var sat_style = $(view.el[0]).find('.fc-sat.fc-day');
                            var sun_style = $(view.el[0]).find('.fc-sun.fc-day');
                            sat_style.addClass('ys-weekend');
                            sun_style.addClass('ys-weekend');
                            refreshFilter();
                        }
                    });
                });

                $('#description_selector').change(function() {
                    refreshFilter();
                });

                $('#employeeFilter').change(function() {
                    refreshFilter();
                });

                $('#filterButton').click(function() {

                    //check filter whether display or not 
                    var displayStatus = $('#forDisplaySelect').css('display');

                    //if display:none-> show
                    if (displayStatus == 'none') {

                        $('#forDisplaySelect').show();
                        $('#forDisplaySelect2').show();

                        //and show all event
                        $('#employeeFilter option').each(function() {
                            $("a[data-title='" + $(this).text() + "']").show();
                        });

                    } else {

                        $('#forDisplaySelect').hide();
                        $('#forDisplaySelect2').hide();

                        //and show all event
                        $('#employeeFilter option').each(function() {
                            $("a[data-title='" + $(this).text() + "']").show();
                        });
                    }

                });

                $('#description_selector').SumoSelect({
                    placeholder: '請選擇..',
                    captionFormat: '已選{0}個選項',
                    selectAll: true,
                    locale: ['確定', '取消', '全選'],
                    csvDispCount: 2,
                    okCancelInMulti: true,
                    captionFormatAllSelected: "全選"
                });

                $.ajax({
                    type: 'post',
                    url: './rest/attendance/nameFilter',
                    datatype: 'text',
                    data: {
                        Live: "Y"
                    },
                    success: function(data) {
                        var record = JSON.parse(data.entity);
                        for (var i = 0; i < record.length; i++) {
                            var option;
                            if (nameSelect == "TW") {
                                option = "<option selected value='" + record[i]["name"] + "' id='" + record[i]["name"] + "'>" +
                                    record[i]["chineseName"] +
                                    "</option>";
                            } else {
                                option = "<option selected value='" + record[i]["name"] + "' id='" + record[i]["name"] + "'>" +
                                    record[i]["name"] +
                                    "</option>";
                            }
                            $('#employeeFilter').append(option);
                        }
                        $('#employeeFilter').SumoSelect({
                            placeholder: '請選擇..',
                            captionFormat: '已選{0}個員工',
                            selectAll: true,
                            locale: ['確定', '取消', '全選'],
                            csvDispCount: 2,
                            okCancelInMulti: true,
                            captionFormatAllSelected: "全選"
                        });
                    },
                    error: function() {
                        confirm("訊息", "取得員工資料失敗");
                    }
                });

                closeLoading();
            });

            function refreshFilter() {
                hideNotSelDescriptionSelector()
                hideNotSelAccountSelector();
                showAllOptOfAccountSelector();
                showAllOptOfDescriptionSelector();
            }

            function hideNotSelAccountSelector() {
                $('#employeeFilter').find("option:not(:selected)").each(function() {
                    $("a[data-title='" + $(this).text() + "']").hide();
                    $("a[data-title='*" + $(this).text() + "']").hide(); //*未簽合
                    $("tr[data-title='" + $(this).text() + "']").hide();
                    $("tr[data-title='*" + $(this).text() + "']").hide();
                });
            }

            function hideNotSelDescriptionSelector() {
                $('#description_selector').find("option:not(:selected)").each(
                    function() {
                        $("a[data-description='" + $(this).text() + "']").hide();
                        $("a[data-description='*" + $(this).text() + "']").hide();
                        $("tr[data-description='" + $(this).text() + "']").hide();
                        $("tr[data-description='*" + $(this).text() + "']").hide();
                    });
            }

            function showAllOptOfAccountSelector() {
                $('#employeeFilter').find(":selected").each(function() {
                    $("a[data-title='" + $(this).text() + "']").show();
                    $("a[data-title='*" + $(this).text() + "']").show();
                    $("tr[data-title='" + $(this).text() + "']").show();
                    $("tr[data-title='*" + $(this).text() + "']").show();
                });
            }

            function showAllOptOfDescriptionSelector() {
                $('#description_selector').find(":selected").each(function() {
                    $("a[data-description='" + $(this).text() + "']").show();
                    $("a[data-description='*" + $(this).text() + "']").show();
                    $("tr[data-description='" + $(this).text() + "']").show();
                    $("tr[data-description='*" + $(this).text() + "']").show();
                });
            }

            function uploadHoliday() { //下載googleCalendar InterFace
                $.ajax({
                    type: "GET",
                    url: '/rest/calendar/uploadHoliday',
                    success: function(data) {},
                    error: function(data) {
                        confirm("訊息", "取得節慶假日失敗");
                    }
                });
            }
        </script>
    </head>

    <body class="fixed-nav sticky-footer" id="page-top">
        <!-- Navigation-->
        <jsp:include page="navbar.jsp" />
        <div class="content-wrapper">
            <div class="container-fluid" style="width: 90%">
                <!-- 內容-->
                <div class="card mb-3">
                    <div class="card-header" style="padding: 0rem">
                        <div class="d-flex align-items-center">
                            <div class="mr-auto p-3">行事曆</div>

                            <div class="p-2" id="forDisplaySelect" style="display: none;">
                                <div class="notIE">
                                    <span class="fancyArrow"></span> <select id="employeeFilter" multiple="multiple" class="employeeClass" style="display: inline-block; height: 30px; width: 180px; padding: 2px 10px 2px 2px; outline: none; color: #74646e; border: 1px solid #C8BFC4; border-radius: 4px; box-shadow: inset 1px 1px 2px #ddd8dc; background: #fff;">
								</select>
                                </div>
                            </div>

                            <div class="p-2" id="forDisplaySelect2" style="display: none;">
                                <div class="notIE">
                                    <span class="fancyArrow"></span>
                                    <!--	/*! type 1 2出差 3特休 4事假 5病假 6公假 7婚假 8喪假 9加班 */ -->
                                    <select id="description_selector" multiple="multiple" style="display: inline-block; height: 30px; width: 180px; padding: 2px 10px 2px 2px; outline: none; color: #74646e; border: 1px solid #C8BFC4; border-radius: 4px; box-shadow: inset 1px 1px 2px #ddd8dc; background: #fff;">
									<!-- 增加 id opt0~7避免被多重選擇下拉式選單套件sumoSelect覆蓋元素導致filter功能失效 -->
									<option selected value="ys-travel" id="opt0">出差</option>
									<option selected value="ys-leave" id="opt1">休假</option>
									<option selected value="overtime" id="opt2">加班</option>
									<option selected value="ys-event" id="opt3">公司活動</option>
									<option selected value="ys-holiday" id="opt4">假日</option>
									<option selected value="ys-makeup" id="opt5">補班</option>
									<option selected value="ys-other" id="opt6">其他</option>
								</select>
                                </div>
                            </div>
                            <div class="p-2">
                                <button class="btn" id="filterButton">
								<i class="fa fa-filter"></i>
							</button>
                            </div>
                        </div>
                        <div class="d-flex flex-column"></div>
                    </div>
                    <div class="card-body">
                        <div id="calendar" style="background-color: white;"></div>
                    </div>
                </div>
            </div>
            <jsp:include page="footer.jsp" />
            <!-- Scroll to Top Button-->
            <a class="scroll-to-top rounded" href="#page-top">
                <i class="fa fa-angle-up"></i>
            </a>
            <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static' style="text-align: center; width: 100%; height: 100%; padding-left: 0px;">
                <div class="modal-dialog">
                    <img src="./Style/images/loading.gif" style="padding-top: 12rem;">
                </div>
            </div>
        </div>
        <jsp:include page="JSfooter.jsp" />
        <!-- qtip plugin-->
        <script type="text/javascript" src="./Style/vendor/jquery/plugin/qtip2-2.2.1/jquery.qtip.min.js"></script>
        <link rel="stylesheet" type="text/css" href="./Style/vendor/jquery/plugin/qtip2-2.2.1/jquery.qtip.min.css" />
    </body>

    </html>