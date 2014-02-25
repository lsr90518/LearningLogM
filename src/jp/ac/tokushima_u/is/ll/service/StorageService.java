//package jp.ac.tokushima_u.is.ll.service;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
//import jp.ac.tokushima_u.is.ll.entity.FileData;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.tika.detect.DefaultDetector;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.mime.MediaType;
//import org.apache.tika.mime.MimeTypes;
//import org.bson.types.ObjectId;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.core.query.Update;
//import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.alibaba.druid.util.IOUtils;
//import com.google.common.base.CharMatcher;
//import com.google.common.base.Splitter;
//import com.google.common.collect.Lists;
//import com.google.common.hash.HashCode;
//import com.google.common.hash.Hashing;
//import com.google.common.io.ByteStreams;
//import com.google.common.io.InputSupplier;
//import com.mongodb.DB;
//import com.mongodb.gridfs.GridFS;
//import com.mongodb.gridfs.GridFSDBFile;
//import com.mongodb.gridfs.GridFSFile;
//import com.mongodb.gridfs.GridFSInputFile;
//
//@Service
//@Transactional(propagation=Propagation.SUPPORTS)
//public class StorageService {
//	@Autowired
//	private MongoTemplate mongoTemplate;
//	@Autowired
//	private GridFsTemplate gridFsTemplate;
//	@Autowired
//	private PropertyService propertyService;
//	@Autowired
//	private FileDataDao fileDataDao;
//	@Autowired
//	private ThumbnailService thumbnailService;
//	
//	@Value("${system.command.handBrakeCLI}")
//	private String handBrakeCLI;
//	
//	@Value("${system.command.ffmpeg}")
//	private String ffmpeg;
//	
//	private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
//	
//	public GridFSFile storeOneFile(InputStream is, String filename, String contentType){
//		File tmpFile = null;
//		try {
//			tmpFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString()+filename);
//			
//			FileUtils.copyInputStreamToFile(is, tmpFile);
//			String md5 = "";
//			try(FileInputStream fis = new FileInputStream(tmpFile)){
//				md5 = DigestUtils.md5Hex(fis);
//			}
//			GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("md5").is(md5)));
//			if(file!=null){
//				return file;
//			}
//			
//			DB db = this.mongoTemplate.getDb();
//			
//			if(StringUtils.isBlank(contentType)){
//				Metadata metadata = new Metadata();
//				metadata.set(Metadata.RESOURCE_NAME_KEY, filename);
//				try(FileInputStream fis = new FileInputStream(tmpFile)){
//					contentType = new DefaultDetector(MimeTypes.getDefaultMimeTypes()).detect(fis, metadata).toString();
//				}
//			}
//			
//			GridFSInputFile inputFile = null;
//			try(FileInputStream fis = new FileInputStream(tmpFile)){
//				inputFile = new GridFS(db).createFile(fis);
//				inputFile.setContentType(contentType);
//				inputFile.setFilename(filename.toLowerCase());
//				inputFile.save();
//			}
//			if(inputFile.getLength() == 0){
//				new GridFS(db).remove(inputFile);
//				return null;
//			}
//			return inputFile;
//		} catch (IOException e) {
//			logger.error("Error", e);
//			return null;
//		} finally {
//			if(tmpFile !=null && tmpFile.exists()){
//				tmpFile.delete();
//			}
//		}
//	}
//	
//	public GridFSDBFile findOneFile(String id){
//		return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
//	}
//	
//	private static final Set<String> processingIds = new HashSet<>();
//
//	public File prepareFile(String filename) {
//		if(StringUtils.isBlank(filename)){
//			logger.debug("filename is empty");
//			return null;
//		}
//		String basename = FilenameUtils.getBaseName(filename);
//		String ext = FilenameUtils.getExtension(filename);
//		if(StringUtils.isBlank(basename) || StringUtils.isBlank(ext)){
//			logger.debug("Filename["+filename+"]:basename is empty or ext is empty");
//			return null;
//		}
//		List<String> params = Lists.newArrayList(Splitter.on(CharMatcher.anyOf("_xX")).trimResults().omitEmptyStrings().split(basename));
//		
//		String fileId = params.get(0);
//		int width = 640;
//		int height = 480;
//		if(params.size()==3){
//			width = Integer.valueOf(params.get(1));
//			height = Integer.valueOf(params.get(2));
//		}
//		if(fileId.length()==32){
//			FileData fileData = fileDataDao.findById(fileId);
//			if(fileData==null || StringUtils.isBlank(fileData.getFileId())){
//				logger.debug("Filename["+filename+"]:Filedata is empty or fileData.fileId is empty");
//				return null;
//			}else{
//				fileId = fileData.getFileId();
//			}
//		}
//		if(fileId.length()!=24){
//			logger.debug("filename["+filename+"]: fileId length is invalid");
//			return null;
//		}
//		
//		logger.debug("ProcessingIds size:"+ processingIds.size());
//		
//		try{
//		long current = System.currentTimeMillis();
//		while(processingIds.contains(fileId)){
//			logger.debug("processingIds already exist blocking "+ (System.currentTimeMillis()-current));
//			if(System.currentTimeMillis()-current > 120000){//Time out 2min
//				logger.debug("Waiting time out, leave blocking");
//				processingIds.remove(fileId);
//			}
//		}
//		processingIds.add(fileId);
//		String dir1 = fileId.substring(0, 3);
//		String dir2 = fileId.substring(3, 6);
//		String dir3 = fileId.substring(6, 9);
//		String dir4 = fileId.substring(9, 12);
//		String dir5 = fileId.substring(12, 15);
//		String dir6 = fileId.substring(15, 18);
//		String dir7 = fileId.substring(18, 21);
//		String dir8 = fileId.substring(21, 24);
//		File dir =FileUtils.getFile(FileUtils.getTempDirectoryPath(), propertyService.getProjectName(), dir1, dir2, dir3, dir4, dir5, dir6, dir7, dir8);
//		
//		File file = new File(dir, fileId+"."+ext);
//		if(file.exists() && file.length()==0){
//			file.delete();
//		}
//		String contentType = null;
//		if(!file.exists()){
//			try {
//				FileUtils.touch(file);
//				GridFSDBFile mongoFile = findOneFile(fileId);
//				if(mongoFile!=null){
//					try{
//						mongoFile.writeTo(file);
//						logger.info("File exist?"+file.exists());
//					} catch (IOException e) {
//						logger.error("Error on save mongo file to local storage", e);
//					}
//					contentType = mongoFile.getContentType();
//					if(StringUtils.isBlank(contentType)){
//						Metadata metadata = new Metadata();
//						metadata.set(Metadata.RESOURCE_NAME_KEY, mongoFile.getFilename());
//						try {
//							contentType = new DefaultDetector(MimeTypes.getDefaultMimeTypes()).detect(new BufferedInputStream(new FileInputStream(file)), metadata).toString();
//							mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(new ObjectId())), Update.update("contentType", contentType), "fs.files");
//						} catch (IOException e) {
//							logger.error("Error on detecting file type", e);
//						}
//					}
//				}else{
//					logger.error("filename["+filename+"]: file is not exist");
//					return null;
//				}
//			} catch (IOException e) {
//				logger.error("Error on creating file type", e);
//			}
//		}
//		String cachefilename = "";
//		
//		File outputFile = null;
//		
//		if(contentType == null){
//			Metadata metadata = new Metadata();
//			metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
//			try {
//				contentType = new DefaultDetector(MimeTypes.getDefaultMimeTypes()).detect(new BufferedInputStream(new FileInputStream(file)), metadata).toString();
//				mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(new ObjectId())), Update.update("contentType", contentType), "fs.files");
//			} catch (IOException e) {
//				logger.error("Error on detecting file type", e);
//			}
//		}
//		
//		MediaType mediaType = MediaType.parse(contentType);
//		
//		switch(mediaType.getType()){
//		case "image":
//			cachefilename = fileId+"_"+width+"_"+height+"."+ext;
//			outputFile = new File(dir, cachefilename);
//			if(!outputFile.exists() || outputFile.length() == 0){
//					try {
//						FileUtils.forceMkdir(dir);
//						thumbnailService.thumbnail(file, outputFile, width, height);
//					} catch (IOException e) {
//						logger.error("Error", e);
//					}
//			}
//			break;
//		case "audio":
//			try {
//				cachefilename = fileId+".mp3";
//				outputFile = new File(dir, cachefilename);
//				if(!outputFile.exists() || outputFile.length() == 0){
//					if("mp3".equals(FilenameUtils.getExtension(file.getName()))){
//						outputFile = file;
//						break;
//					}else{
//						String cmd = ffmpeg
//								+" -ab 64k -i "
//								+file.getCanonicalPath()+" "+outputFile.getCanonicalPath();
//						ProcessBuilder builder = new ProcessBuilder(cmd);
//						builder.redirectErrorStream(true);
//						Process p = builder.start();
//						BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//						while(null!=br.readLine()){}
//						p.waitFor();
//					}
//				}
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			break;
//		case "video":
//			cachefilename = filename;
//			outputFile = new File(dir, cachefilename);
//			if(!outputFile.exists() || outputFile.length() == 0){
//				File outputVideo = new File(dir, fileId+".mp4");
//				if(!outputVideo.exists() ||outputVideo.length()==0){
//					try {						
//						String cmd = handBrakeCLI
//								+ " -e x264  -q 20.0 -a 1 -E faac -B 128 -6 dpl2 -R 48 -D 0.0 -f mp4 -X 480 -m -x cabac=0:ref=2:me=umh:bframes=0:subme=6:8x8dct=0:trellis=0 --vb 200 --width 320 --two-pass --turbo --optimize --input "
//								+ file.getCanonicalPath() + " --output " + file.getCanonicalPath();
//						ProcessBuilder builder = new ProcessBuilder(cmd);
//						builder.redirectErrorStream(true);
//						Process p = builder.start();
//						BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//						while(null!=br.readLine()){}
//						p.waitFor();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				if(!outputFile.exists()){
//					//Image
//					try {
//						File origImage = new File(dir, fileId+"_"+width+"_"+height+".png");
//						if(!origImage.exists() || origImage.length()==0){
//							List<String> command = new java.util.ArrayList<String>();
//							command.add(ffmpeg);
//							command.add("-i");
//							command.add(file.getCanonicalPath());
//							command.add("-y");
//							command.add("-f");
//							command.add("image2");
//							command.add("-vcodec");
//							command.add("png");
//							command.add("-ss");
//							command.add("1");
//							command.add("-t");
//							command.add("0.001");
//							command.add("-s");
//							command.add(width+"x"+height);
//							command.add(origImage.getCanonicalPath());
//							ProcessBuilder builder = new ProcessBuilder(command);
//							builder.redirectErrorStream(true);
//							Process p = builder.start();
//							BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//							while(null!=br.readLine()){}
//							p.waitFor();
//						}
//						if(!outputFile.exists() && origImage.exists()){
//							thumbnailService.thumbnail(origImage, outputFile, width, height);
//						}
//					} catch (IOException | InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			break;
//		default:
//			outputFile = file;
//		}
//		return outputFile;
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}finally{
//			processingIds.remove(fileId);
//			logger.debug("Left size: "+ processingIds.size());
//		}
//	}
//	
//	public static void main(String[] args){
//		try {
//			FileInputStream fis = new FileInputStream("/Users/houbin/Desktop/favicon.png");
//			byte[]  b = IOUtils.readByteArray(fis);
//			InputSupplier<ByteArrayInputStream> isr = ByteStreams.newInputStreamSupplier(b);
//			HashCode code = ByteStreams.hash(isr, Hashing.md5());
//			FileUtils.copyInputStreamToFile(fis, new File("/Users/houbin/Desktop/favicon123.png"));
//			System.out.println(code);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}