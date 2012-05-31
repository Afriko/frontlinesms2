package frontlinesms2

class FsmsTagLib {
	static namespace = 'fsms'
	def expressionProcessorService

	def unbroken = { att, body ->
		if(att.value) out << att.value.replaceAll(' ', '&nbsp;')
		if(body) out << body().replaceAll(' ', '&nbsp;')
	}

	def render = { att ->
		out << g.render(att)
	}

	def i18n = { att ->
		r.script(disposition:'head') {
			att.keys.tokenize(',')*.trim().each {
				def propVal = g.message(code:it)
				propVal = propVal.replaceAll("\\'", "\\\\'")
				out << "\ti18nStrings['$it'] = '${propVal}';\n"
			}
		}
	}
	
	def confirmTable = { att ->
		out << '<table id="' + (att.instanceClass.simpleName.toLowerCase() - 'fconnection') + '-confirm" class="connection-confirm-table">'
		out << confirmTypeRow(att)
		def fields = getFields(att)
		if(fields instanceof Map) {
			generateConfirmSection(att, fields)
		} else {
			fields.each { out << confirmTableRow(att + [field:it.trim()]) }
		}
		out << '</table>'
	}
	
	def confirmTypeRow = { att ->
		out << '<tr>'
		out << '<td class="field-label">'
		out << g.message(code:"${att.instanceClass.simpleName.toLowerCase()}.type.label")
		out << '</td>'
		out << '<td id="confirm-type"></td>'
		out << '</tr>'
	}
	
	def confirmTableRow = { att ->
		out << '<tr>'
		out << '<td class="field-label">'
		out << getFieldLabel(att.instanceClass, att.field)
		out << '</td>'
		out << '<td id="confirm-' + att.field + '"></td>'
		out << '</tr>'
	}
	
	def inputs = { att ->
		println "att is $att"
		def fields = getFields(att)
		if(fields instanceof Map) {
			generateSection(att, fields)
			
		} else {
			fields.each {
				out << input(att + [field:it])
			}
		}
		
	}
	
	def input = { att, body ->
		def groovyKey = att.field
		// TODO remove references to att.instanceClass and make sure that all forms in app
		// have an instance supplied - whether it is retrieved from the database or created
		// specially for the view
		def instanceClass = att.instance?.getClass()?: att.instanceClass
		def htmlKey = (att.fieldPrefix!=null? att.fieldPrefix: instanceClass?instanceClass.shortName:'') + att.field
		def val = att.instance?."$groovyKey"
		
		['instance', 'instanceClass'].each { att.remove(it) }
		att += [name:htmlKey, value:val]
		out << '<div class="field">'
		out << '<label for="' + htmlKey + '">'
		out << '' + getFieldLabel(instanceClass, groovyKey)
		if(isRequired(instanceClass, att.field) && !isBooleanField(instanceClass, att.field)) out << '<span class="required-indicator">*</span>'
		out << '</label>'
		if(att.class) att.class += addValidationCss(instanceClass, att.field)
		else att.class = addValidationCss(instanceClass, att.field)
		
		if(att.password || isPassword(instanceClass, groovyKey)) {	
			out << g.passwordField(att)
		} else if(instanceClass.metaClass.hasProperty(null, groovyKey)?.type.enum) {
			out << g.select(att + [from:instanceClass.metaClass.hasProperty(null, groovyKey).type.values(),
						noSelection:[null:'- Select -']])
		} else if(isBooleanField(instanceClass, groovyKey)) {
			out << g.checkBox(att)
		} else out << g.textField(att)
		out << body()
		out << '<div style="clear:both" class="clearfix"></div>'
		out << '</div>'
	}

	def checkBox = { att -> out << checkbox(att) }
	def checkbox = { att ->
		if(att.remove('disabled') in [true, 'disabled']) att.disabled = 'disabled'
		out << g.checkBox(att)
	}

	def magicWand = { att ->
		def controller = att.controller
		def target = att.target
		def fields = expressionProcessorService.findByController(controller)
		target = target?: "messageText"

		out << '<div class="magicwand-container">'
		// TODO change this to use g.select if appropriate
		out << "<select id='magicwand-select$target' onchange=\"magicwand.wave('magicwand-select$target', '$target')\">"
		out << '<option value="na" id="magic-wand-na$target" class="not-field">Select option</option>'
		fields.each {
			out << '<option class="predefined-field" value="'+it.key+'" ' + (it.value?'':'disabled="disabled" ') + '>' + g.message(code:"dynamicfield.${it.key}.label") + '</option>'
		}
		out << '</select>'
		out << '</div>'
	}

	def trafficLightStatus = { att ->
		out << '<span id="status-indicator" class="indicator '
		def connections = Fconnection.list()
		def color = (connections && connections.status.any {(it == RouteStatus.CONNECTED)}) ? 'green' : 'red'
		out << color
		out << '"></span>'
	}
	
	private def getFields(att) {
		def fields = att.remove('fields')
		if(!fields) fields = att.instanceClass?.configFields
		if(fields instanceof String) fields = fields.tokenize(',')*.trim()
		return fields
	}
	
	private def getFieldLabel(clazz, fieldName) {
		g.message(code:"${clazz.simpleName.toLowerCase()}.${fieldName}.label")
	}
	
	private def isPassword(instanceClass, groovyKey) {
		return instanceClass.metaClass.hasProperty(null, 'passwords') &&
				groovyKey in instanceClass.passwords
	}
	
	private def isBooleanField(instanceClass, groovyKey) {
		return instanceClass.metaClass.hasProperty(null, groovyKey).type in [Boolean, boolean]
	}
	
	private def generateSection(att, fields) {
		def keys = fields.keySet()
		keys.each { key ->
			if(fields[key]) {
				out << "<div id=\"$key-subsection\">"
				out << "<fieldset>"
				out << "<legend>"
				out << input(att + [field:key])
				out << "</legend>"
				
				//handle subsections within a subsection
				if(fields[key] instanceof LinkedHashMap) {
					generateSection(att, fields[key])
				} else {
					fields[key].each {field ->
						if(field instanceof String) {
							 out << input(att + [field:field] + [class:"$key-subsection-member"])
						}
					}
				}
				out << "</fieldset>"
				out << "</div>"
			} else {
				out << input(att + [field:key])
			}
			
		}
	}
	
	private def generateConfirmSection(att, fields) {
		def keys = fields.keySet()
		keys.each { key ->
			if(fields[key]) {
				out << "<div class=\"confirm-$key-subsection\">"
				out << confirmTableRow(att + [field:key])
				
				//handle subsections within a subsection
				if(fields[key] instanceof LinkedHashMap) {
					generateConfirmSection(att, fields[key])
				} else {
					fields[key].each {field ->
						if(field instanceof String) {
							 out << confirmTableRow(att + [field:field] + [class:"subsection-member"])
						}
					}
				}
				out << "</div>"
			} else {
				out << confirmTableRow(att + [field:key])
			}
			
		}
	}

	private def isRequired(instanceClass, field) {
		!instanceClass.constraints[field].nullable
	}

	private def isInteger(instanceClass, groovyKey) {
		return instanceClass.metaClass.hasProperty(null, groovyKey).type in [Integer, int]
	}

	private def addValidationCss(instanceClass, field) {
		def cssClasses = ""
		if(isRequired(instanceClass, field) && !isBooleanField(instanceClass, field)) {
			cssClasses += " required "
		}

		if(isInteger(instanceClass, field)) {
			cssClasses += " digits "
		}
		cssClasses
	}
}
