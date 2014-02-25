package jp.ac.tokushima_u.is.ll.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import jp.ac.tokushima_u.is.ll.util.UuidPathUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;

/**
 * 
 * @author Bin Hou
 */
@Service
@Transactional(readOnly = true)
public class StaticServerService {
	
	private static Logger logger = LoggerFactory.getLogger(StaticServerService.class);

	@Autowired
	private PropertyService propertyService;
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void upload(final String project,final byte[] file, final String filename, final String ext) throws IOException{
		if(StringUtils.isBlank(filename) || filename.length()!=32){
			throw new RuntimeException("filename must be uuid(32)");
		}
		if(StringUtils.isBlank(ext)){
			throw new RuntimeException("ext is empty");
		}
		logger.debug("Enter upload, filename="+filename+",ext="+ext);
		jmsTemplate.send(propertyService.getJmsQueueNameUploadFile(), new MessageCreator(){
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage  msg = session.createMapMessage();
				msg.setString("projectName", project);
				msg.setString("fileId", filename);
				msg.setString("extName", ext);
				msg.setBytes("file", file);
				return msg;
			}
		});
	}
	
	public void upload(String project, byte[] file, String fullname) throws IOException{
		String filename = FilenameUtils.getBaseName(fullname);
		String ext = FilenameUtils.getExtension(fullname);
		this.upload(project, file, filename, ext);
	}
	
	public void upload(byte[] file, String filename, String ext) throws IOException{
		String project = this.propertyService.getProjectName();
		this.upload(project, file, filename, ext);
	}
	
	public void upload(byte[] file, String fullname) throws IOException{
		String project = this.propertyService.getProjectName();
		this.upload(project, file, fullname);
	}
	
	//Get FileUrl by filename
	public String accessurl(String filename){
		String project = this.propertyService.getProjectName();
		return this.accessurl(project, filename);
	}
	
	public String accessurl(String project, String filename){
		List<String> elements = new ArrayList<>();
		String staticRoot = MessageFormat.format(this.propertyService.getStaticFileAccessUri(), project);
		elements.add(staticRoot);
		List<String> paths = UuidPathUtils.convertPaths(filename);
		elements.addAll(paths);
		elements.add(filename);
		return Joiner.on("/").join(elements);
	}
	
	public String uploadurl(){
		String project = propertyService.getProjectName();
		return this.uploadurl(project);
	}
	
	public String uploadurl(String project){
		return MessageFormat.format(this.propertyService.getStaticFileUploadUri(), project);
	}
	
	public File findCachedFile(String project, String filename){
		File root = FileUtils.getFile(FileUtils.getTempDirectory(), project);
		File folder = FileUtils.getFile(root, UuidPathUtils.convertPaths(filename).toArray(new String[]{}));
		if(!folder.exists()) folder.mkdirs();
		File file = FileUtils.getFile(folder, filename);
		return file;
	}
	
	public File downloadToCache(String project, String filename) throws IOException{
		File file = findCachedFile(project, filename);
		if(file.exists()){
			return file;
		}
		String url = accessurl(project, filename);
		FileUtils.copyURLToFile(new URL(url), file);
		return file;
	}
}
