package jp.ac.tokushima_u.is.ll.service.pacall;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallGpsDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoCompItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoCompSelfDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoDao;
import jp.ac.tokushima_u.is.ll.dao.PacallSensorDao;
import jp.ac.tokushima_u.is.ll.dto.pacall.SensorDataItem;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.PacallGps;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.entity.PacallPhotoCompItem;
import jp.ac.tokushima_u.is.ll.entity.PacallPhotoCompSelf;
import jp.ac.tokushima_u.is.ll.entity.PacallSensor;
import jp.ac.tokushima_u.is.ll.entity.PacallSimilar;
import jp.ac.tokushima_u.is.ll.service.ImageIndexService;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.LuceneIndexService;
import jp.ac.tokushima_u.is.ll.service.PropertyService;
import jp.ac.tokushima_u.is.ll.service.StaticServerService;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.imgproc.BlurrinessMeasureUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.BoxesDetectionUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.BrightnessMeasureUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.FaceDetectionUtil;
import jp.ac.tokushima_u.is.ll.util.imgproc.FeatureDetectionUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.HistogramUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.OpenCVUtils;
import jp.ac.tokushima_u.is.ll.util.imgproc.TextDetectionUtils;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.SentenceValidator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;

@Service
@Transactional(readOnly = true)
public class PacallProcessService {
	
	private static final Logger logger = LoggerFactory.getLogger(PacallProcessService.class);
	
	public static final int STEP_COMPOSE_SENSORDATA = 0;
	public static final int STEP_ANALYZE_PHOTO = 1;
	
	public static final int SETTING_MIN_FACE_SIZE = 30;
	public static final int SETTING_MIN_FEATURE_SIZE = 30;
	public static final int SETTING_MIN_MATCHES_LENGTH = 30;
	public static final int SETTING_MIN_BRIGHTNESS = 50;
	
	static{
		IIORegistry registry = IIORegistry.getDefaultInstance();  
		registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());  
		registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());
		registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.png.CLibPNGImageWriterSpi());  
		registry.registerServiceProvider(new com.sun.media.imageioimpl.plugins.png.CLibPNGImageReaderSpi()); 
	}
	
	@Autowired
	private PacallSensorDao pacallSensorDao;
	@Autowired
	private PacallGpsDao pacallGpsDao;
	@Autowired
	private PacallPhotoDao pacallPhotoDao;
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private PacallPhotoCompSelfDao pacallPhotoCompSelfDao;
	@Autowired
	private PacallPhotoCompItemDao pacallPhotoCompItemDao;
	@Autowired
	private StaticServerService staticServerService;
	@Autowired
	private PropertyService propertyService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private PacallSimilarService pacallSimilarService;
	@Autowired
	private PacallLogService pacallLogService;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ImageIndexService imageIndexService;
	
	private static final Map<String, int[]> processStatus = new ConcurrentHashMap<>();
	
	/**
	 * BlockingQueue<collectionId>
	 */
	private ThreadPoolExecutor executor; 
	
	public PacallProcessService(){
		executor = new ThreadPoolExecutor(12, 12, 0, TimeUnit.MINUTES, new LinkedTransferQueue<Runnable>());
	}

	public int[] findProcessStatus(String collectionId) {
		int[] s = processStatus.get(collectionId);
		return s==null?null:s;
	}

	@Transactional(readOnly = false)
	public void startProcess(final String collectionId) {
		
		executor.execute(new Runnable(){
			@Override
			public void run() {
				try{
					long composeStart = System.currentTimeMillis();
					processStatus.put(collectionId, new int[]{0,0});
					List<PacallSensor> sensorList = pacallSensorDao.findListByCollectionId(collectionId);
					if(sensorList.size()<1) {
						return;
					}
					List<PacallPhoto> photoList = pacallPhotoDao.findListInCollection(collectionId, new PageRequest(0, Integer.MAX_VALUE));
					if(photoList.size()<1) {
						return;
					}
					composeSensorData(sensorList, photoList);
					long composeEnd = System.currentTimeMillis();
					sensorList = null;
					long analyzeStart = System.currentTimeMillis();
					processStatus.put(collectionId, new int[]{1,0});
//						composeGpsData(collectionId);
					processStatus.put(collectionId, new int[]{2,0});
					analyzePhotos(photoList);
	//					reindexPhotos(collectionId);
	//					refreshPhotoTag(collectionId);
					long analyzeEnd = System.currentTimeMillis();
					pacallLogService.logProcessStatus(collectionId, composeStart, composeEnd, analyzeStart, analyzeEnd);
				}finally{
					processStatus.remove(collectionId);
				}
			}
		});
		
		
	}

	@Transactional(readOnly = false)
	private void analyzePhotos(List<PacallPhoto> photoList) {
		
		PacallPhoto lastPhoto = null;
		long lastTimestamp = 0l;
		Mat lastHist = null;
		
//		List<Item> itemList = itemService.findItemListHasImage();
		
		for(int i=0;i<photoList.size();i++){
			PacallPhoto photo = photoList.get(i);
			
			photo.setBrightness(null);
			photo.setBlurriness(null);
			photo.setParentId(null);
			photo.setFacenum(0);
			photo.setFacepos(null);
			photo.setTextcontent("");
			photo.setTextpos(null);
			photo.setFeaturenum(0);
			photo.setFeaturepos(null);
			//清空相似图片
			pacallSimilarService.deleteByPhotoId(photo.getId());
			
			//更新进度
			processStatus.put(photoList.get(0).getCollectionId(), new int[]{STEP_ANALYZE_PHOTO, i*100/photoList.size()});
			File file = null;
			try {
				file = staticServerService.downloadToCache(propertyService.getPacallProject(), photo.getId()+"_320x240.png");
			} catch (IOException e1) {
				logger.error("File not exist"+photo.getId());
				continue;
			}
			//开始分析图片
			//1.分析亮度
			try {
				double brightness = BrightnessMeasureUtils.judgeBrightness(new FileInputStream(file));
				photo.setBrightness(brightness);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//2.分析模糊度
			try {
				double blurriness = BlurrinessMeasureUtils.judgeBlurriness(new FileInputStream(file));
				photo.setBlurriness(blurriness);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//3.提取Histogram
			//和前一张图对比，分析是否重复
			if(!"M".equals(photo.getReason()) && !"MAN".equals("")){
				Mat hist = HistogramUtils.extractHist(Highgui.imread(file.getAbsolutePath()));
				
				if(lastPhoto==null || lastTimestamp == 0l || lastHist==null){
					//如果上一张图片不存在，或者上一张图片与本张时间在半小时以上，认为是两个时间段
					//记录本图片信息
					lastPhoto = photo;
					lastTimestamp = photo.getPhotodate().getTime();
					lastHist = hist;
				}else{
					//与上一张匹配，如果匹配成功，则记录本图片时间信息，并将上一张图片ID做为本图片父ID
					double similarity = HistogramUtils.compareHist(hist, lastHist);
					if(similarity > 0.85){
						//相似
						lastTimestamp = photo.getPhotodate().getTime();
						photo.setParentId(lastPhoto.getId());
					}else{
						lastPhoto = photo;
						lastTimestamp = photo.getPhotodate().getTime();
						lastHist = hist;
					}
				}
			}
			
			//不好的照片不用去做图像分析
			if(photo.getBrightness()>SETTING_MIN_BRIGHTNESS && StringUtils.isBlank(photo.getParentId())){
				//4.分析是否含有脸及脸部位置
				List<Rect> factList = FaceDetectionUtil.detect(file, SETTING_MIN_FACE_SIZE);
				if(factList ==null || factList.size()<1){
					photo.setFacenum(0);
					photo.setFacepos("");
				}else{
					photo.setFacenum(factList.size());
					List<Map<String, Integer>> rectMapList = new ArrayList<>();
					for(Rect rect: factList){
						Map<String, Integer> rectMap = new LinkedHashMap<>();
						rectMap.put("x", rect.x);
						rectMap.put("y", rect.y);
						rectMap.put("width", rect.width);
						rectMap.put("height", rect.height);
						rectMapList.add(rectMap);
					}
					photo.setFacepos(new Gson().toJson(rectMapList));
				}
				
				//5.分析是否含有文字及文字位置
				String ocr = TextDetectionUtils.ocr(file);
				if(StringUtils.isNotBlank(ocr)){
					photo.setTextcontent(ocr);
				}
				
				//6.查找相似的学习记录:by text
				List<String> textList = TextDetectionUtils.splitToSegments(ocr);
				if(textList.size()>0){
					List<Item> items = itemDao.searchItemByTitles(null, textList, new PageRequest(0, Integer.MAX_VALUE, new Sort(Sort.Direction.DESC, "update_time")));
					for(Item item: items){
						if(item.getImage()==null)continue;
						PacallSimilar similar = new PacallSimilar();
						similar.setId(KeyGenerateUtil.generateIdUUID());
						similar.setItemId(item.getId());
						similar.setPhotoId(photo.getId());
						similar.setReason(PacallSimilar.REASON_TEXT);
						pacallSimilarService.create(similar);
					}
				}
				
				//7.分析是否含有图形, 存储图像特征点
				Mat mat = Highgui.imread(file.getAbsolutePath());
				List<Rect> featureList = BoxesDetectionUtils.detect(mat, SETTING_MIN_FEATURE_SIZE);
				if(featureList ==null || featureList.size()<=1){
					photo.setFeaturenum(0);
					photo.setFeaturepos("");
				}else{
					photo.setFeaturenum(featureList.size());
					List<Map<String, Integer>> rectMapList = new ArrayList<>();
					for(Rect rect: featureList){
						Map<String, Integer> rectMap = new LinkedHashMap<>();
						rectMap.put("x", rect.x);
						rectMap.put("y", rect.y);
						rectMap.put("width", rect.width);
						rectMap.put("height", rect.height);
						rectMapList.add(rectMap);
					}
					photo.setFeaturepos(new Gson().toJson(rectMapList));
				}
				
				if(photo.getFeaturenum()>0){
					//没有找到图形就别对比相似图片
					//8.查找相似的学习记录:by feature
					try {
						BufferedImage image = ImageIO.read(file);
						LinkedHashMap<String, Double> itemIds = imageIndexService.searchImage(image);
						for(Map.Entry<String, Double> entry: itemIds.entrySet()){
							//匹配数达标，认为匹配
							PacallSimilar similar = new PacallSimilar();
							similar.setId(KeyGenerateUtil.generateIdUUID());
							similar.setItemId(entry.getKey());
							similar.setPhotoId(photo.getId());
							similar.setReason(PacallSimilar.REASON_FEATURE);
							similar.setExtra(String.valueOf(entry.getValue()));
							pacallSimilarService.create(similar);
						}
					} catch (Exception e) {
						logger.error("IOException", e);;
					}
				}
			}
			
			//更新图片信息
			pacallPhotoDao.updateExtraInfo(photo);
		}
	}

	
	@Autowired
	private CacheManager cacheManager;
	/**
	 * 查找已经登录到系统中的图像文件的描述文件
	 * @param image
	 * @return
	 * @throws InvalidProtocolBufferException 
	 * @throws IOException 
	 */
	private Mat findScrollImageDesc(String imageId) throws InvalidProtocolBufferException {
		//从缓存里查找
		Cache cache = cacheManager.getCache("surfdesc");
		ValueWrapper wrapper = cache.get(imageId);
		if(wrapper!=null && wrapper.get()!=null){
			byte[] b = (byte[]) wrapper.get();
			if(b!=null && b.length>0){
				return OpenCVUtils.deserialize(b);
			}else{
				return null;
			}
		}
		
		//缓存中不存在时，从远程服务器上下载
		File file = null;
		try {
			file = staticServerService.downloadToCache(propertyService.getProjectName(), imageId+"_320x240.png");
		} catch (IOException e) {
			cache.put(imageId, new byte[]{});
			logger.error("File does not exist, id="+imageId);
			return null;
		}
		if(!file.exists()){
			
		}
		Mat mat = Highgui.imread(file.getAbsolutePath());
		Mat desc = null;
		try {
			desc = FeatureDetectionUtils.extractDescription(mat);
		} catch (Exception e) {
			logger.error("Error on extractDescription, imageId="+imageId, e);;
			return null;
		}
		byte[] serialzed = OpenCVUtils.serialize(desc);
		cache.put(imageId, serialzed);
		return desc;
	}

//	@Transactional(readOnly = false)
//	private void refreshPhotoTag(String collectionId) {
//		initPacallTag();
//		pacallPhotoTagDao.deleteAllPacallPhotoTagByCollectionId(collectionId);
//		
//		List<PacallPhoto> photoList = this.pacallPhotoDao.findListInCollection(collectionId, new PageRequest(0, Integer.MAX_VALUE));
//		for(PacallPhoto photo:photoList){
//			String filename = photo.getId()+"_640_480.png";
//			File file = null;
//			try {
//				file = staticServerService.downloadToCache(propertyService.getPacallProject(), filename);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			if(file==null || !file.exists())continue;
//			//Manual
//			boolean isManual = false;
//			if(photo.getPhototype()==0 || "M".equals(photo.getReason()) || "MAN".equals(photo.getReason())){
//				isManual = true;
//				PacallPhotoTag photoTag = new PacallPhotoTag();
//				photoTag.setId(KeyGenerateUtil.generateIdUUID());
//				photoTag.setPhotoId(photo.getId());
//				PacallTag tag = pacallTagDao.findByTag(PacallTag.TAG_MANUAL);
//				photoTag.setTagId(tag.getId());
//				pacallPhotoTagDao.insert(photoTag);
//			}
//			
//			//Brightness
//			boolean isDark = false;
//			try {
//				double brightness = BrightnessMeasureUtils.judgeBrightness(new BufferedInputStream(new FileInputStream(file)));
//				if(brightness<20){
//					isDark = true;
//					PacallPhotoTag photoTag = new PacallPhotoTag();
//					photoTag.setId(KeyGenerateUtil.generateIdUUID());
//					photoTag.setPhotoId(photo.getId());
//					PacallTag tag = pacallTagDao.findByTag(PacallTag.TAG_DARK);
//					photoTag.setTagId(tag.getId());
//					photoTag.setExtra(String.valueOf(brightness));
//					pacallPhotoTagDao.insert(photoTag);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			//Face detection
//			boolean isFace = false;
//			if(!isDark){
//				List<Rect> faces = FaceDetectionUtil.detect(file, 30);
//				if(faces.size()>0){
//					isFace = true;
//					PacallPhotoTag photoTag = new PacallPhotoTag();
//					photoTag.setId(KeyGenerateUtil.generateIdUUID());
//					photoTag.setPhotoId(photo.getId());
//					PacallTag tag = pacallTagDao.findByTag(PacallTag.TAG_FACE);
//					photoTag.setTagId(tag.getId());
//					photoTag.setExtra(String.valueOf(faces.size()));
//					pacallPhotoTagDao.insert(photoTag);
//				}
//			}
//			
//			//Feature Detection
//			@SuppressWarnings("unused")
//			boolean isFeature = false;
//			if(!isDark && !isFace){
//				try {
//					Surf surf = new Surf(ImageIO.read(file));
//					int pointNumber = surf.getUprightInterestPoints().size();
//					if(pointNumber > 50){
//						isFeature = true;
//						PacallPhotoTag photoTag = new PacallPhotoTag();
//						photoTag.setId(KeyGenerateUtil.generateIdUUID());
//						photoTag.setPhotoId(photo.getId());
//						PacallTag tag = pacallTagDao.findByTag(PacallTag.TAG_FACE);
//						photoTag.setTagId(tag.getId());
//						photoTag.setExtra(String.valueOf(pointNumber));
//						pacallPhotoTagDao.insert(photoTag);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		initCompDataInCollection(collectionId);
//		addDuplicatedTagsToPhotos(collectionId);
//	}
	
	private void reindexPhotos(String collectionId) {
		List<PacallPhoto> photoList = this.pacallPhotoDao.findListInCollection(collectionId, new PageRequest(0, Integer.MAX_VALUE));
		luceneIndexService.addPacallPhotoListToImageIndex(photoList);
	}
	
	@Transactional(readOnly = false)
	private void composeGpsData(String collectionId) {
		List<PacallGps> gpsList = pacallGpsDao.findListByCollectionId(collectionId);
		List<PacallPhoto> photoList = this.pacallPhotoDao.findAllByCollectionIdNoGps(collectionId);
		
		for(PacallGps gps: gpsList){
			File file = null;
			try {
				file = staticServerService.downloadToCache(propertyService.getPacallProject(), gps.getId()+".csv");
			} catch (IOException e1) {
				e1.printStackTrace();
				continue;
			}
			
			Long startTime = null;
			Date startDate = null;
			Date endDate = null;
			TreeMap<Double, Double> latMap = new TreeMap<>();
			TreeMap<Double, Double> lngMap = new TreeMap<>();
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
				String line = null;
				GGASentence gga = null;
				
				while((line = reader.readLine())!=null){
					if(SentenceValidator.isValid(line)){
						Sentence s= SentenceFactory.getInstance().createParser(line);
						if(SentenceId.GGA.equals(s.getSentenceId())){
							gga = (GGASentence)s;
						}else if(SentenceId.RMC.equals(s.getSentenceId())){
							RMCSentence rmc = (RMCSentence)s;
							if(gga==null || !gga.getTime().equals(rmc.getTime())){
								continue;
							}
							Calendar calendar = Calendar.getInstance();
							calendar.set(rmc.getDate().getYear(), rmc.getDate().getMonth(), rmc.getDate().getDay(), rmc.getTime().getHour(), rmc.getTime().getMinutes(), Double.valueOf(rmc.getTime().getSeconds()).intValue());
							calendar.set(Calendar.MILLISECOND, Long.valueOf(rmc.getTime().getMilliseconds()).intValue());
							if(startTime == null) {
								startTime = calendar.getTimeInMillis();
								startDate = calendar.getTime();
							}
							endDate = calendar.getTime();
							latMap.put(Long.valueOf(calendar.getTimeInMillis()-startTime).doubleValue()/1000, gga.getPosition().getLatitude());
							lngMap.put(Long.valueOf(calendar.getTimeInMillis()-startTime).doubleValue()/1000, gga.getPosition().getLongitude());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(startTime == null || startDate==null || endDate==null){
				continue;
			}
			
			PolynomialSplineFunction latForm = null, lngForm = null;
			try {
				latForm = new SplineInterpolator().interpolate(ArrayUtils.toPrimitive(latMap.keySet().toArray(new Double[latMap.size()])), ArrayUtils.toPrimitive(latMap.values().toArray(new Double[latMap.size()])));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				lngForm = new SplineInterpolator().interpolate(ArrayUtils.toPrimitive(lngMap.keySet().toArray(new Double[lngMap.size()])), ArrayUtils.toPrimitive(lngMap.values().toArray(new Double[lngMap.size()])));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for(PacallPhoto photo: photoList){
				Date d = photo.getPhotodate();
				if(d.before(startDate) || d.after(endDate)){
					continue;
				}
				double photodate = (d.getTime()-startTime)/1000;
				
				try {
					if(latForm!=null) photo.setLat(latForm.value(photodate));
					if(lngForm!=null) photo.setLng(lngForm.value(photodate));
					this.pacallPhotoDao.update(photo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 将传感器数据拼装到图片的信息中，重要的是图片的时间信息
	 * @param collectionId
	 */
	@Transactional(readOnly = false)
	private void composeSensorData(List<PacallSensor> sensorList, List<PacallPhoto> photoList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		List<SensorDataItem> sensorDataList = new ArrayList<>();
		
		processStatus.put(photoList.get(0).getCollectionId(), new int[]{STEP_COMPOSE_SENSORDATA,0});
		
		for(PacallSensor sensor: sensorList){
			try {
				URL url = new URL(staticServerService.accessurl(propertyService.getPacallProject(), sensor.getId()+".csv"));
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))){
					String line = null;
					while((line = reader.readLine())!=null){
						SensorDataItem item = new SensorDataItem();
						List<String> data = Lists.newArrayList(Splitter.on(',').trimResults().split(line));
						if(data.size()<3){
							continue;
						}
						String flag = data.get(0).toUpperCase();
						if(!"CAM".equals(flag) && !"CLR".equals(flag) && !"TMP".equals(flag) && !"ACC".equals(flag) && !"MAG".equals(flag)){
							continue;
						}
						item.setFlag(flag);
						try {
							item.setDate(dateFormat.parse(data.get(1)));
						} catch (ParseException e) {
							e.printStackTrace();
							continue;
						}
						item.setV1(data.get(2));
						if(data.size()>3) item.setV2(data.get(3));
						if(data.size()>4) item.setV3(data.get(4));
						sensorDataList.add(item);
					}
				}
			} catch (IOException e) {
				logger.error("Error when access sensor file, sensorId:"+sensor.getId(), e);
				continue;
			}
		}
		
		if(sensorDataList.size()<1) {
			logger.debug("SensorDataList size <1, collectionId="+photoList.get(0).getCollectionId());
			return;
		}
		
		Collections.sort(sensorDataList);
		
		Map<String, PacallPhoto> photoMap = new HashMap<>();
		for(PacallPhoto photo: photoList){
			photoMap.put(photo.getFilename(), photo);
		}
		
		
		for(int i=0;i<sensorDataList.size();i++){
			//Update progress
			processStatus.put(photoList.get(0).getCollectionId(), new int[]{STEP_COMPOSE_SENSORDATA, i*100/sensorDataList.size()});
			
			SensorDataItem data = sensorDataList.get(i);
			if(!"CAM".equals(data.getFlag())){
				continue;
			}
			//当数据为照片数据时，找到数据库中的相应条目
			String filename = FilenameUtils.getBaseName(data.getV1()).toLowerCase();
			PacallPhoto photo = photoMap.get(filename);
			if(photo==null || StringUtils.isNotBlank(photo.getReason())) continue;
			//组装各个数据
			//组装拍照理由
			photo.setPhotodate(data.getDate());
			photo.setReason(data.getV2());
			
			long phototime = photo.getPhotodate().getTime();
			
			//查前后分别查找传感器数据，找到最临近的数据，组装到Photo上
			//向前查找CLR, TMP, ACC, MAG
			SensorDataItem pCLR = null, pTMP = null, pACC = null, pMAG = null;
			for(int j = i-1; j>=0;j--){
				SensorDataItem c = sensorDataList.get(j);
				if(pCLR==null && "CLR".equals(c.getFlag())){
					pCLR = c;
				}else if(pTMP==null && "TMP".equals(c.getFlag())){
					pTMP = c;
				}else if(pACC==null && "ACC".equals(c.getFlag())){
					pACC = c;
				}else if(pMAG==null && "MAG".equals(c.getFlag())){
					pMAG = c;
				}else{
				}
				//都找到,或者时间超出60秒范围时中断查找
				if((pCLR!=null && pTMP!=null && pACC!=null && pMAG!=null) || Math.abs(c.getDate().getTime()-phototime)>60000){
					break;
				}
			}
			
			SensorDataItem nCLR = null, nTMP = null, nACC = null, nMAG = null;
			//向后查找CLR, TMP, ACC, MAG
			for(int j = i+1; j<sensorDataList.size();j++){
				SensorDataItem c = sensorDataList.get(j);
				if(nCLR==null && "CLR".equals(c.getFlag())){
					nCLR = c;
				}else if(nTMP==null && "TMP".equals(c.getFlag())){
					nTMP = c;
				}else if(nACC==null && "ACC".equals(c.getFlag())){
					nACC = c;
				}else if(nMAG==null && "MAG".equals(c.getFlag())){
					nMAG = c;
				}else{
				}
				//都找到,或者时间超出60秒范围时中断查找
				if((nCLR!=null && nTMP!=null && nACC!=null && nMAG!=null) || Math.abs(c.getDate().getTime()-phototime)>60000){
					break;
				}
			}
			
			//选取数据
			SensorDataItem clr = null, tmp = null, acc = null, mag = null;
			if(pCLR==null && nCLR!=null){
				clr = nCLR;
			}else if(pCLR!=null && nCLR==null){
				clr = pCLR;
			}else if(pCLR!=null && nCLR!=null){
				if(Math.abs(pCLR.getDate().getTime()-phototime) > Math.abs(nCLR.getDate().getTime()-phototime)){
					clr = nCLR;
				}else{
					clr = pCLR;
				}
			}else{
			}
			
			if(pTMP==null && nTMP!=null){
				tmp = nTMP;
			}else if(pTMP!=null && nTMP==null){
				tmp = pTMP;
			}else if(pTMP!=null && nTMP!=null){
				if(Math.abs(pTMP.getDate().getTime()-phototime) > Math.abs(nTMP.getDate().getTime()-phototime)){
					tmp = nTMP;
				}else{
					tmp = pTMP;
				}
			}else{
			}
			
			if(pACC==null && nACC!=null){
				acc = nACC;
			}else if(pACC!=null && nACC==null){
				acc = pACC;
			}else if(pACC!=null && nACC!=null){
				if(Math.abs(pACC.getDate().getTime()-phototime) > Math.abs(nACC.getDate().getTime()-phototime)){
					acc = nACC;
				}else{
					acc = pACC;
				}
			}else{
			}
			
			if(pMAG==null && nMAG!=null){
				mag = nMAG;
			}else if(pMAG!=null && nMAG==null){
				mag = pMAG;
			}else if(pMAG!=null && nMAG!=null){
				if(Math.abs(pMAG.getDate().getTime()-phototime) > Math.abs(nMAG.getDate().getTime()-phototime)){
					mag = nMAG;
				}else{
					mag = pMAG;
				}
			}else{
			}
			
			if(clr!=null){
				try {
					photo.setClr(Double.valueOf(clr.getV1()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if(tmp!=null){
				try {
					photo.setTmp(Double.valueOf(tmp.getV1()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if(acc!=null){
				try {
					photo.setAccX(Double.valueOf(acc.getV1()));
					photo.setAccY(Double.valueOf(acc.getV2()));
					photo.setAccZ(Double.valueOf(acc.getV3()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if(mag!=null){
				try {
					photo.setMagX(Double.valueOf(mag.getV1()));
					photo.setMagY(Double.valueOf(mag.getV2()));
					photo.setMagZ(Double.valueOf(mag.getV3()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}

			//Update photo
			this.pacallPhotoDao.update(photo);
		}
	}
	
	public void initCompDataInCollection(String collectionId) {
		double similar = 0.5;
//			List<PacallPhoto> photos =  pacallPhotoDao.findListAllPhotosInDay(userId, date);
		Pageable page = new PageRequest(0, Integer.MAX_VALUE);
		List<PacallPhoto> photos =  pacallPhotoDao.findListForNormalInCollection(collectionId, page);
		
		for(PacallPhoto photo: photos){
			try{
				if(!luceneIndexService.isPacallPhotoIndexed(photo)){
					luceneIndexService.addPacallPhotoIdToIndex(photo.getId());
				}
				pacallPhotoCompSelfDao.deleteAllBySelfId(photo.getId());
				LinkedHashMap<String, Float> scores = luceneIndexService.searchPacallPhotoByIndexedPacallPhoto(photo, 50);
				
				for(Map.Entry<String, Float> score: scores.entrySet()){
					if(score.getValue()>similar){
						if(photo.getId().equals(score.getKey()))continue;
						PacallPhotoCompSelf comp = new PacallPhotoCompSelf();
						comp.setId(KeyGenerateUtil.generateIdUUID());
						comp.setSelfId(photo.getId());
						comp.setOtherId(score.getKey());
						comp.setScore(score.getValue().doubleValue());
						pacallPhotoCompSelfDao.insert(comp);
					}
				}
				
				pacallPhotoCompItemDao.deleteAllByPhotoId(photo.getId());
				LinkedHashMap<String, Float> scores1 = luceneIndexService.searchItemIdsByIndexedPacallPhoto(photo, 50);
				for(Map.Entry<String, Float> score: scores1.entrySet()){
					if(score.getValue()>similar){
						if(photo.getId().equals(score.getKey()))continue;
						PacallPhotoCompItem comp = new PacallPhotoCompItem();
						comp.setId(KeyGenerateUtil.generateIdUUID());
						comp.setPhotoId(photo.getId());
						comp.setItemId(score.getKey());
						comp.setScore(score.getValue().doubleValue());
						pacallPhotoCompItemDao.insert(comp);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
//	private void addDuplicatedTagsToPhotos(String collectionId){
//		List<PacallPhotoCompSelf> comps = pacallPhotoCompSelfDao.findListALLInCollection(collectionId);
//		Map<String, Set<String>> cl = new HashMap<String, Set<String>>();
//		PacallTag tag = pacallTagDao.findByTag(PacallTag.TAG_DUPLICATED);
//		for(PacallPhotoCompSelf c: comps){
//			String selfId = c.getSelfId();
//			if(cl.containsKey(selfId)){
//				cl.get(selfId).add(c.getOtherId());
//				cl.get(selfId).add(c.getSelfId());
//			}else{
//				String leaderId = null;
//				for(Map.Entry<String, Set<String>> entry: cl.entrySet()){
//					if(entry.getValue().contains(selfId)){
//						leaderId = entry.getKey();
//					}
//				}
//				if(leaderId == null){
//					Set<String> list = new HashSet<String>();
//					list.add(c.getOtherId());
//					list.add(c.getSelfId());
//					cl.put(selfId, list);
//				}else{
//					cl.get(leaderId).add(selfId);
//					cl.get(leaderId).add(c.getOtherId());
//				}
//			}
//		}
//		
//		for(Map.Entry<String, Set<String>> entry: cl.entrySet()){
//			for(String otherId: entry.getValue()){
//				if(otherId.equals(entry.getKey()))continue;
//				PacallPhotoTag photoTag = new PacallPhotoTag();
//				photoTag.setId(KeyGenerateUtil.generateIdUUID());
//				photoTag.setPhotoId(otherId);
//				photoTag.setTagId(tag.getId());
//				photoTag.setExtra(entry.getKey());
//				pacallPhotoTagDao.deleteByUniqkey(photoTag.getPhotoId(), photoTag.getTagId());
//				pacallPhotoTagDao.insert(photoTag);
//			}
//		}
//	}
	
//	@Transactional(readOnly = false)
//	private void initPacallTag() {
//		long tagNumber = pacallTagDao.countAll();
//		if(tagNumber>0)return;
//		Map<String, String> tags = new HashMap<String, String>();
//		tags.put("dark", "Dark");
//		tags.put("defocused", "Defocused");
//		tags.put("duplicated", "Duplicated");
//		tags.put("feature", "Has Feature");
//		tags.put("manual", "Manual");
//		tags.put("text", "Text");
//		tags.put("face", "Face");
//		for(Map.Entry<String, String> entry: tags.entrySet()){
//			PacallTag tag = new PacallTag();
//			tag.setId(KeyGenerateUtil.generateIdUUID());
//			tag.setTag(entry.getKey());
//			tag.setName(entry.getValue());
//			pacallTagDao.insert(tag);
//		}
//	}
}
