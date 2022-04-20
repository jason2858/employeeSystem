<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<!DOCTYPE html>
<meta charset="utf-8" />
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" id="mainNav">
  <img src="../../Style/images/logo-md.png" style="margin-left: 1rem" />
  <button
    class="navbar-toggler navbar-toggler-right"
    type="button"
    data-toggle="collapse"
    data-target="#navbarResponsive"
    aria-controls="navbarResponsive"
    aria-expanded="false"
    aria-label="Toggle navigation"
  >
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarResponsive">
    <jsp:include page="sidebar.jsp" />
    <ul class="navbar-nav ml-auto">
      <li class="nav-item" style="margin-right: 2rem">
        <a class="nav-link" id="punchType" href="punch_management.do"> </a>
      </li>
      <li class="nav-item dropdown">
        <a
          class="nav-link dropdown-toggle mr-lg-2"
          id="userDropdown"
          href="#"
          data-toggle="dropdown"
          aria-haspopup="true"
          aria-expanded="false"
        >
          <i class="fa fa-fw far fa-user"></i>
          <% out.println(session.getAttribute("Account")); %>
        </a>

        <div class="dropdown-menu" aria-labelledby="userDropdown">
          <a class="dropdown-item" style="text-align: center" href="setting.do">
            <strong> 偏好設定 </strong>
            <div class="dropdown-message small">個人化設定</div>
          </a>
          <% int authorise=(Integer) session.getAttribute("Authorise"); if
          (authorise==1) { %>
          <div class="dropdown-divider"></div>
          <div
            class="dropdown-item"
            style="text-align: center; cursor: pointer"
            onclick="reloadConfig()"
          >
            <strong> Config Reload </strong>
            <div class="dropdown-message small">重新載入Config</div>
          </div>
          <% } %>
        </div>
      </li>

      <li class="nav-item">
        <a class="nav-link" onclick="logout()">
          <i class="fa fa-fw fa-sign-out"></i>Logout
        </a>
      </li>
    </ul>
  </div>
</nav>
