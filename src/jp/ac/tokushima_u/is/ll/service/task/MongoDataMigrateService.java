//package jp.ac.tokushima_u.is.ll.service.task;
//
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.List;
//
//import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
//import jp.ac.tokushima_u.is.ll.entity.FileData;
//import jp.ac.tokushima_u.is.ll.service.PropertyService;
//import jp.ac.tokushima_u.is.ll.service.StorageService;
//
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.tika.detect.DefaultDetector;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.mime.MimeTypes;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import com.mongodb.gridfs.GridFSFile;
//
///**
// * Migrate file data from harddisk to mongodb
// * @author Houbin
// *
// */
//@Service
//@Transactional
//public class MongoDataMigrateService {
//	
//	@Autowired
//	private FileDataDao fileDataDao;
//	
//	@Autowired
//	private StorageService storageService;
//	
//	@Autowired
//	private PropertyService propertyService;
//	
//	@Transactional
//	public void execute(){
//		
//		RestTemplate restTemplate = new RestTemplate();
//		List<FileData> list = fileDataDao.findListAll();
//		
//		for(FileData fileData: list){
//			
//			if(StringUtils.isBlank(fileData.getOrigName()) || StringUtils.isNotBlank(fileData.getFileId())){
////				fileDataDao.delete(fileData.getId());
//				continue;
//			}
//			String filename = fileData.getId()+"."+FilenameUtils.getExtension(fileData.getOrigName()).toLowerCase();
//			try {
//				byte[] data;
//				try {
//					data = restTemplate.getForObject("http://ll.is.tokushima-u.ac.jp/static/learninglog/orig/"+filename, byte[].class);
//				} catch (Exception e) {
//					filename = fileData.getId()+"."+FilenameUtils.getExtension(fileData.getOrigName()).toUpperCase();
//					data = restTemplate.getForObject("http://ll.is.tokushima-u.ac.jp/static/learninglog/orig/"+filename, byte[].class);
//				}
//				
//				GridFSFile file = null;
//				if(StringUtils.isNotBlank(fileData.getFileId())){
//					file = storageService.findOneFile(fileData.getFileId());
//				}else{
//					Metadata metadata = new Metadata();
//					metadata.set(Metadata.RESOURCE_NAME_KEY, filename);
//					String contentType = new DefaultDetector(MimeTypes.getDefaultMimeTypes()).detect(new BufferedInputStream(new ByteArrayInputStream(data)), metadata).toString();
//					file = storageService.storeOneFile(new BufferedInputStream(new ByteArrayInputStream(data)), filename, contentType);
//					System.out.println("fileId:"+file.getId().toString());
//					fileData.setFileId(file.getId().toString());
//					fileData.setMd5(file.getMD5());
//					fileDataDao.updateFileId(fileData);
//				}
//			} catch (RestClientException | IOException e) {
//				System.out.println("Failed:"+e.getMessage()+filename);
//			}
//		}
//		
//		
//	}
//}
