package jp.ac.tokushima_u.is.ll.service;

import java.io.File;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Houbin
 * 
 */
@Service
public class PropertyService {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

	@Value("${system.projectName}")
	private String projectName = "learninglog_dev";
	
	@Value("${system.langrid_username}")
	private String langridUsername;
	
	@Value("${system.langrid_password}")
	private String langridPassword;

	@Value("${url.apk_learninglog}")
	private String urlApkLearningLog;

	@Value("${url.apk_navigator}")
	private String urlApkNavigator;

	@Value("${system.mail.systemMailAddress}")
	private String systemMailAddress;

	@Value("${system.url}")
	private String systemUrl;

	@Value("${system.send_weekly_flag}")
	private Boolean sendWeeklyFlag;
	
	@Value("${system.staticFileDir}")
	private String staticFileDir;

	@Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;

	@Value("${staticserver.file_uri}")
	@Deprecated 
	private String staticImageUri;
	@Value("${system.staticserverUrl}")
	@Deprecated private String staticserverUrl;
	@Value("${system.staticserverService}")
	@Deprecated private String staticserverService;
	
	@Value("${staticserver.file_upload_uri}")
	private String staticFileUploadUri;
	@Value("${staticserver.file_access_uri}")
	private String staticFileAccessUri;
	
	@Value("${jms.queue.name.uploadfile}")
	private String jmsQueueNameUploadFile;

	@Value("${jms.queue.name.sendMail}")
	private String jmsQueueNameSendMail;
	
	@Value("${lucene.image.index}")
	private String luceneImageIndex;
	
	@Value("${lucene.image.surf_data}")
	private String luceneImageSurfData;
	
	@Autowired
	public PropertyService(@Value("${jni.opencv}")String opencvPath){
		super();
            try {
            	File file = FileUtils.getFile(opencvPath);
            	if(file.exists()){
            		SystemPathUtils.addJavaLibraryPath(opencvPath);
            		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            	}
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Deprecated
	public String getStaticserverUrl() {
		return staticserverUrl;
	}

	@Deprecated
	public void setStaticserverUrl(String staticserverUrl) {
		this.staticserverUrl = staticserverUrl;
	}

	public String getUrlApkLearningLog() {
		return urlApkLearningLog;
	}

	public void setUrlApkLearningLog(String urlApkLearningLog) {
		this.urlApkLearningLog = urlApkLearningLog;
	}

	public String getUrlApkNavigator() {
		return urlApkNavigator;
	}

	public void setUrlApkNavigator(String urlApkNavigator) {
		this.urlApkNavigator = urlApkNavigator;
	}

	public String getSystemMailAddress() {
		return systemMailAddress;
	}

	public void setSystemMailAddress(String systemMailAddress) {
		this.systemMailAddress = systemMailAddress;
	}

	public String getSystemUrl() {
		return systemUrl;
	}

	public void setSystemUrl(String systemUrl) {
		this.systemUrl = systemUrl;
	}

	public Boolean getSendWeeklyFlag() {
		return sendWeeklyFlag;
	}

	public void setSendWeeklyFlag(Boolean sendWeeklyFlag) {
		this.sendWeeklyFlag = sendWeeklyFlag;
	}

	@Deprecated
	public String getStaticImageUri() {
		return staticImageUri;
	}

	@Deprecated
	public void setStaticImageUri(String staticImageUri) {
		this.staticImageUri = staticImageUri;
	}

	public String getStaticserverImageUrl() {
		return staticserverImageUrl;
	}

	public void setStaticserverImageUrl(String staticserverImageUrl) {
		this.staticserverImageUrl = staticserverImageUrl;
	}
	
	public String getStaticFileDir() {
		return staticFileDir;
	}

	public void setStaticFileDir(String staticFileDir) {
		this.staticFileDir = staticFileDir;
	}
	
	public String findPacallStaticDir() {
		return this.staticFileDir+"/pacall/"+this.projectName;
	}

	public String getLangridUsername() {
		return langridUsername;
	}

	public void setLangridUsername(String langridUsername) {
		this.langridUsername = langridUsername;
	}

	public String getLangridPassword() {
		return langridPassword;
	}

	public void setLangridPassword(String langridPassword) {
		this.langridPassword = langridPassword;
	}

	@Deprecated
	public String getStaticserverService() {
		return staticserverService;
	}

	@Deprecated
	public void setStaticserverService(String staticserverService) {
		this.staticserverService = staticserverService;
	}

	public String getStaticFileUploadUri() {
		return staticFileUploadUri;
	}

	public void setStaticFileUploadUri(String staticFileUploadUri) {
		this.staticFileUploadUri = staticFileUploadUri;
	}

	public String getStaticFileAccessUri() {
		return staticFileAccessUri;
	}

	public void setStaticFileAccessUri(String staticFileAccessUri) {
		this.staticFileAccessUri = staticFileAccessUri;
	}
	
	public String getPacallProject(){
		return this.getProjectName()+"_pacall";
	}

	public String getJmsQueueNameUploadFile() {
		return jmsQueueNameUploadFile;
	}

	public void setJmsQueueNameUploadFile(String jmsQueueNameUploadFile) {
		this.jmsQueueNameUploadFile = jmsQueueNameUploadFile;
	}

	public String getJmsQueueNameSendMail() {
		return jmsQueueNameSendMail;
	}

	public void setJmsQueueNameSendMail(String jmsQueueNameSendMail) {
		this.jmsQueueNameSendMail = jmsQueueNameSendMail;
	}

	public String getLuceneImageIndex() {
		return luceneImageIndex;
	}

	public void setLuceneImageIndex(String luceneImageIndex) {
		this.luceneImageIndex = luceneImageIndex;
	}

	public String getLuceneImageSurfData() {
		return luceneImageSurfData;
	}

	public void setLuceneImageSurfData(String luceneImageSurfData) {
		this.luceneImageSurfData = luceneImageSurfData;
	}
}
