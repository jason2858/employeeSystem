<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <!doctype html>
      <meta charset="utf-8">


<ul class="navbar-nav navbar-sidenav" id="sidenav" style=" overflow-y: auto;">
		<%int Authorise = (Integer)session.getAttribute("Authorise");
		  String id;
		  for(int i = 1;i < (Integer)session.getAttribute("sidebarcount");i++){
			if((Integer)session.getAttribute(i + ".inner") == 0){%>
			<li class="nav-item" data-toggle="tooltip" data-placement="right" title="Dashboard">
	          <a class="nav-link" id="<%out.print(session.getAttribute(i + ".url"));%>" href="<%out.print("/"+session.getAttribute(i + ".url"));%>">
	            <span class="nav-link-text"><%out.print(session.getAttribute(i + ".name"));%>
	            </span>
	          </a>
	          <%} %>
	          <%if((Integer)session.getAttribute(i + ".inner") != 0){%>
	          	<li class="nav-item" data-toggle="tooltip" data-placement="right" title="<%out.print(session.getAttribute(i + ".name"));%>">
	              <a class="nav-link nav-link-collapse collapsed" id="<%out.print(session.getAttribute(i + ".url"));%>" data-toggle="collapse" href="#<%out.print(session.getAttribute(i + ".name"));%>">
	                <span class="nav-link-text"><%out.print(session.getAttribute(i + ".name"));%>
	                </span>
	              </a>
	              <ul class="sidenav-second-level collapse" id="<%out.print(session.getAttribute(i + ".name"));%>">
	          	<%for(int j=0;j<(Integer)session.getAttribute(i + ".inner");j++){%>
	          		<li>
	              		<a href="<%out.print("/"+session.getAttribute(i + "." + j + ".url"));%>" id="<%out.print(session.getAttribute(i + "." + j + ".url"));%>"><%out.print(session.getAttribute(i + "." + j + ".name"));%></a>
	            	</li>
	          	<%} %>
	          	</ul>
	          <%} %>
	        </li>
	    <%} %>
      </ul>
      <ul class="navbar-nav sidenav-toggler">
        <li class="nav-item">
          <a class="nav-link text-center" id="sidenavToggler">
            <i class="fa fa-fw fa-angle-left"></i>
          </a>
        </li>
      </ul>

