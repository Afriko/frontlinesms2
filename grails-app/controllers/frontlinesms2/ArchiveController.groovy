package frontlinesms2

import grails.util.GrailsConfig

class ArchiveController extends MessageController {
	def beforeInterceptor = {
//		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
//		params.offset  = params.offset ?: 0
//		if(params.action == sent || params.action == pending) params.sort = params.sort ?: 'dateSent'
//		else params.sort = params.sort ?: 'dateReceived'
//		params.order = params.order ?: 'desc'
//		params.viewingArchive = true
//		params.viewingMessages = params.viewingMessages ? params.viewingMessages.toBoolean() : true
//		true
		params.viewingArchive = true
	}
	 
	def index = {
		params.sort = 'dateReceived'
		def messageSection = params.messageSection ?: 'inbox'
		println "index: $params"
		redirect(action:messageSection, params:params)
	}
	
	def activityList = {
		def pollInstanceList = Poll.findAllByArchivedAndDeleted(true, false)
		def announcementInstanceList = Announcement.findAllByArchivedAndDeleted(true,false)
		render view:'standard', model:[pollInstanceList: pollInstanceList,
											announcementInstanceList: announcementInstanceList,
											itemInstanceTotal: announcementInstanceList.size() + pollInstanceList.size(),
											messageSection: "poll"]
	}
	
	def folderList = {
		def folderInstanceList = Folder.findAllByArchivedAndDeleted(true, false)
		render view:'standard', model:[folderInstanceList: folderInstanceList,
											itemInstanceTotal: folderInstanceList.size(),
											messageSection: "folder"]
	}
	
	def getShowModel(messageInstanceList) {
		println "Welcome to archive controller"
		def model = super.getShowModel(messageInstanceList)
		model << [viewingArchive: true]
		return model
	}
}
