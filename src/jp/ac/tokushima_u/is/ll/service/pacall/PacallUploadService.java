package jp.ac.tokushima_u.is.ll.service.pacall;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.PacallGpsDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoDao;
import jp.ac.tokushima_u.is.ll.dao.PacallSensorDao;
import jp.ac.tokushima_u.is.ll.entity.PacallGps;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.entity.PacallSensor;
import jp.ac.tokushima_u.is.ll.service.LuceneIndexService;
import jp.ac.tokushima_u.is.ll.service.PropertyService;
import jp.ac.tokushima_u.is.ll.service.StaticServerService;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.HashUtils;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

@Service
@Transactional(readOnly = true)
public class PacallUploadService {
	
	public static final Logger logger = LoggerFactory.getLogger(PacallUploadService.class);
	
	@Autowired
	private StaticServerService staticServerService;
	@Autowired
	private PacallPhotoDao pacallPhotoDao;
	@Autowired
	private PacallSensorDao pacallSensorDao;
	@Autowired
	private PacallGpsDao pacallGpsDao;
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private PropertyService propertyService;

	@Transactional(readOnly = false)
	public String uploadPhoto(byte[] b, String filename, String ext, String userId, String collectionId) throws IOException, ImageProcessingException {
		
		if("SENSOR.CSV".equals((filename+"."+ext).toUpperCase())){
			this.uploadSensorCsv(b, filename, ext, userId, collectionId);
			return "success";
		}
		
		if(!FilenameUtil.IMAGE.equals(FilenameUtil.checkMediaType(filename+"."+ext))){
			return "no_image";
		}
		
		//Check if file is already exist
		String id = KeyGenerateUtil.generateIdUUID();
		String md5 = HashUtils.md5Hex(b);
		
		List<PacallPhoto> photoList = pacallPhotoDao.findListByCollectionIdAndHash(collectionId, md5);
		if(photoList!=null && photoList.size()>0){
			return "exist";
		}
		
		staticServerService.upload(propertyService.getPacallProject(), b, id, ext);
		
		PacallPhoto photo = new PacallPhoto();
		photo.setId(id);
		photo.setUserId(userId);
		Date current = new Date();
		photo.setCreateTime(current);
		photo.setHash(md5);
		photo.setFilename(StringUtils.trimToEmpty(filename).toLowerCase());
		photo.setExt(StringUtils.trimToEmpty(ext).toLowerCase());
		photo.setCollectionId(collectionId);
		Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(b)), true);
		if(metadata==null){
			photo.setPhototype(1);
			photo.setPhotodate(current);
		}else{
			photo.setPhototype(0);
			ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			if(directory!=null){
				Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				if(date!=null){
					photo.setPhotodate(date);
				}
			}else{
				photo.setPhototype(1);
			}
			if(photo.getPhotodate()==null){
				photo.setPhotodate(current);
			}
			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
			if(gpsDirectory!=null){
				GeoLocation geoLocation = gpsDirectory.getGeoLocation();
				if(geoLocation!=null){
					photo.setLat(geoLocation.getLatitude());
					photo.setLng(geoLocation.getLongitude());
				}
			}
		}
		pacallPhotoDao.insert(photo);
		
		return "success";
	}

	@Transactional(readOnly = false)
	public String uploadSensorCsv(byte[] b, String filename, String ext, String userId, String collectionId) throws IOException {
		String md5 = HashUtils.md5Hex(b);
		final List<PacallSensor> list = pacallSensorDao.findListByCollectionIdAndHash(collectionId, md5);
		if(list!=null && list.size()>0){
			return list.get(0).getId();
		}
		String id = KeyGenerateUtil.generateIdUUID();
		staticServerService.upload(propertyService.getPacallProject(), b, id, ext);
		
		PacallSensor pacallSensor = null;
		pacallSensor = new PacallSensor();
		pacallSensor.setId(id);
		pacallSensor.setUserId(userId);
		pacallSensor.setHash(md5);
		pacallSensor.setFilename(StringUtils.trimToEmpty(filename).toLowerCase());
		pacallSensor.setExt(StringUtils.trimToEmpty(ext).toLowerCase());
		pacallSensor.setCreateTime(new Date());
		pacallSensor.setCollectionId(collectionId);
		pacallSensorDao.insert(pacallSensor);
		
		return "success";
	}
	
	@Transactional(readOnly = false)
	public String uploadGps(byte[] file, String filename, String ext, String userId, String collectionId) throws IOException {
		String md5 = HashUtils.md5Hex(file);
		List<PacallGps> list = this.pacallGpsDao.findListByCollectionIdAndHash(collectionId, md5);
		if(list!=null && list.size()>0){
			return list.get(0).getId();
		}
		PacallGps pacallGps = new PacallGps();
		pacallGps.setId(KeyGenerateUtil.generateIdUUID());
		staticServerService.upload(propertyService.getPacallProject(), file, pacallGps.getId(), ext);
		pacallGps.setUserId(userId);
		pacallGps.setHash(md5);
		pacallGps.setFilename(StringUtils.trimToEmpty(filename).toLowerCase());
		pacallGps.setExt(StringUtils.trimToEmpty(ext).toLowerCase());
		pacallGps.setCreateTime(new Date());
		pacallGps.setCollectionId(collectionId);
		pacallGpsDao.insert(pacallGps);
		return pacallGps.getId();
	}
}
