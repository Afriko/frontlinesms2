<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<r:require module="settings"/>
		<fsms:render template="/includes"/>
		<fsms:i18n keys="popup.cancel, popup.back, wizard.cancel, wizard.back, wizard.next, smallpopup.cancel, popup.help.title, connection.edit, connection.add, smallpopup.test.message.title, popup.done, wizard.create, smallpopup.send, popup.ok,logs.download.continue,logs.download.title"/>
		<r:script>
			$(function() {
				$(window).resize(new Resizer("#body-content-container", "#body-content-head"));
			});
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<fsms:render template="/head"/>
		<div id="body" class="settings">
			<fsms:render template="/settings/menu"/>
			<div id="body-content-container">
				<g:layoutBody/>
			</div>
		</div>
		<fsms:render template="/system"/>
	</body>
</html>
