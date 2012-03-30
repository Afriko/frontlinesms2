<div id="tabs-3">
	<h2 class="bold">Sort messages automatically using a Keyword (optional)</h2>
	<p class="info">If people send in poll responses using a Keyword, FrontlineSMS can automatically sort the messages on your system.</p>
	<ul class="radios">
		<g:radioGroup name="enableKeyword" values="[false, true]" value="${(activityInstanceToEdit?.keyword?.value as boolean) ?: false}" labels="['Do not sort messages automatically', 'Sort messages automatically that have the following Keyword:']">
			<li>
				${it.radio}${it.label}
			</li>
		</g:radioGroup>
	</ul>
	<g:if test="${activityInstanceToEdit?.keyword}">
		<g:textField name="keyword" id="poll-keyword" value="${activityInstanceToEdit?.keyword?.value}"/>
	</g:if>
	<g:else>
		<g:textField name="keyword" id="poll-keyword" disabled="true" value="${activityInstanceToEdit?.keyword?.value}"/>
	</g:else>
	
</div>
