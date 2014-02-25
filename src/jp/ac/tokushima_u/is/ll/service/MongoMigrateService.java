//package jp.ac.tokushima_u.is.ll.service;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
//import jp.ac.tokushima_u.is.ll.dao.ItemDao;
//import jp.ac.tokushima_u.is.ll.entity.FileData;
//
//import org.apache.commons.io.FilenameUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.mongodb.DB;
//import com.mongodb.gridfs.GridFS;
//import com.mongodb.gridfs.GridFSInputFile;
//
//@Service
//@Transactional(readOnly = true)
//public class MongoMigrateService {
//	
//	@Autowired
//	private MongoTemplate mongoTemplate;
//	@Autowired
//	private GridFsTemplate gridFsTemplate;
//	@Autowired
//	private ItemDao itemDao;
//	@Autowired
//	private FileDataDao fileDataDao;
//	@Autowired
//	private PropertyService propertyService;
//	
//	@Transactional(readOnly = false)
//	public void migrate() throws ClientProtocolException, IOException{
//		List<FileData> fileDataList = fileDataDao.findListAll();
//		for(FileData fileData:fileDataList){
//			if(fileData.getFileId()!=null){
//				continue;
//			}
//			if(null!=gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(fileData.getFileId()))))){
//				continue;
//			}
//			String ext = FilenameUtils.getExtension(fileData.getOrigName());
//			String url = "http://ll.is.tokushima-u.ac.jp/static/"+propertyService.getProjectName()+"/orig/"+fileData.getId()+"."+ext;
//			HttpClient client = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet(url);
//			HttpResponse response = client.execute(httpGet);
//			if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){  
//				HttpEntity entity = response.getEntity();
//				if (entity != null) {
//					try(InputStream input = new BufferedInputStream(entity.getContent())){
//						DB db = this.mongoTemplate.getDb();
//						GridFSInputFile file = new GridFS(db).createFile(input);
//						file.setContentType(fileData.getFileType());
//						file.setFilename(fileData.getOrigName());
//						file.save();
//						fileData.setFileId(file.getId().toString());
//						fileDataDao.updateFileId(fileData);
//					}
//				}
//			}
//		}
//	}
//}
