<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!doctype html>
<meta charset="utf-8">


<footer class="sticky-footer">
	<div class="container">
		<div class="text-center">
			<small>All rights reserved. © 2018 YESEE 越世</small>
		</div>
	</div>
</footer>
<input id="announceCheck" value='<%out.print(session.getAttribute("announceCheck"));%>' type="text" disabled="disabled" style="display: none">
<input id="getAuthorise" value='<%out.print(session.getAttribute("Authorise"));%>' type="text" disabled="disabled" style="display: none">