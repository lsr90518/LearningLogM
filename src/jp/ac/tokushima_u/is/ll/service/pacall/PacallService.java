package jp.ac.tokushima_u.is.ll.service.pacall;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallGpsDao;
import jp.ac.tokushima_u.is.ll.dao.PacallItemRecordDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoCompItemDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoCompSelfDao;
import jp.ac.tokushima_u.is.ll.dao.PacallPhotoDao;
import jp.ac.tokushima_u.is.ll.dao.PacallSensorDao;
import jp.ac.tokushima_u.is.ll.dao.PacallSimilarDao;
import jp.ac.tokushima_u.is.ll.entity.PacallGps;
import jp.ac.tokushima_u.is.ll.entity.PacallItemRecord;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.entity.PacallSensor;
import jp.ac.tokushima_u.is.ll.entity.PacallSimilar;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.LuceneIndexService;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false)
public class PacallService {
	
	@Autowired
	private PacallPhotoDao pacallPhotoDao;
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private PacallPhotoCompItemDao pacallPhotoCompItemDao;
	@Autowired
	private PacallPhotoCompSelfDao pacallPhotoCompSelfDao;
	@Autowired
	private PacallSensorDao pacallSensorDao;
	@Autowired
	private PacallItemRecordDao pacallItemRecordDao;
	@Autowired
	private PacallGpsDao pacallGpsDao;
	@Autowired
	private PacallSimilarDao pacallSimilarDao;
	@Autowired
	private ItemDao itemDao;

	public Page<Map<String, Object>> findDateList() {
		List<Map<String,Object>> result = pacallPhotoDao.findMapListForCountPerDay(SecurityUserHolder.getCurrentUser().getId());
		return new PageImpl<Map<String, Object>>(result);
	}

	public Page<PacallPhoto> findPhotosInDay(Date date, int pageNumber, String tag) {
		String userId = SecurityUserHolder.getCurrentUser().getId();
		if(pageNumber<1)pageNumber = 1;
		Pageable page = new PageRequest(pageNumber-1, 60);
		List<PacallPhoto> result = null;
		Long count = null;
		
		switch(tag){
		case "recommended":
			result = pacallPhotoDao.findListForRecommended(userId, date, page);
			count = pacallPhotoDao.countListForRecommended(userId, date);
			break;
		case "normal":
			result = pacallPhotoDao.findListForNormal(userId, date, page);
			count = pacallPhotoDao.countListForNormal(userId, date);
			break;
		case "dark":
		case "defocused":
		case "duplicated":
		case "feature":
		case "manual":
		case "text":
		case "face":
			result = pacallPhotoDao.findListByTag(userId, date, page, tag);
			count = pacallPhotoDao.countListByTag(userId, date, tag);
			break;
		default:
			result =  pacallPhotoDao.findList(userId, date, page);
			count = pacallPhotoDao.countList(userId, date);
		}
		return new PageImpl<PacallPhoto>(result, page, count);
	}
	
	public Page<PacallPhoto> findPhotosByCollctionId(String collectionId,
			Integer pageNumber, String tag, String userId) {
		if(pageNumber==null || pageNumber<1)pageNumber = 1;
		Pageable page = new PageRequest(pageNumber-1, 60);
		List<PacallPhoto> result = null;
		Long count = null;
		
		switch(tag){
		case "recommended":
			result = pacallPhotoDao.findListForRecommendedInCollection(collectionId, page);
			count = pacallPhotoDao.countListForRecommendedInCollection(collectionId);
			break;
		case "normal":
			result = pacallPhotoDao.findListForNormalInCollection(collectionId, page);
			count = pacallPhotoDao.countListForNormalInCollection(collectionId);
			break;
		case "manual":
			result = pacallPhotoDao.findListForManualInCollection(collectionId, page);
			count = pacallPhotoDao.countListForManualInCollection(collectionId);
			break;
		case "face":
			result = pacallPhotoDao.findListForFaceInCollection(collectionId, page);
			count = pacallPhotoDao.countListForFaceInCollection(collectionId);
			break;
		case "text":
			result = pacallPhotoDao.findListForTextInCollection(collectionId, page);
			count = pacallPhotoDao.countListForTextInCollection(collectionId);
			break;
		case "feature":
			result = pacallPhotoDao.findListForFeatureInCollection(collectionId, page);
			count = pacallPhotoDao.countListForFeatureInCollection(collectionId);
			break;
		case "ullolike":
			result = pacallPhotoDao.findListForUllolikeInCollection(collectionId, page);
			count = pacallPhotoDao.countListForUllolikeInCollection(collectionId);
			break;
		case "dark":
			result = pacallPhotoDao.findListForDarkInCollection(collectionId, page);
			count = pacallPhotoDao.countListForNormalInCollection(collectionId);
			break;
//		case "defocused":
		case "duplicated":
			result = pacallPhotoDao.findListForDuplicatedInCollection(collectionId, page);
			count = pacallPhotoDao.countListForDuplicatedInCollection(collectionId);
			break;
		default:
			result =  pacallPhotoDao.findListInCollection(collectionId, page);
			count = pacallPhotoDao.countListInCollection(collectionId);
		}
		
		for(PacallPhoto photo: result){
			List<PacallPhoto> children = new ArrayList<>();
			if(StringUtils.isNotBlank(photo.getParentId())){
				children.add(pacallPhotoDao.findById(photo.getParentId()));
				children.addAll(pacallPhotoDao.findListByParentId(photo.getId()));
			}else{
				children = pacallPhotoDao.findListByParentId(photo.getId());
			}
			photo.setChildren(children);
			List<PacallSimilar> mySimilars = pacallSimilarDao.findListByPhotoIdByUser(photo.getId(), userId);
			photo.setMySimilars(mySimilars);
			List<PacallSimilar> otherSimilars = pacallSimilarDao.findListByPhotoIdByOthers(photo.getId(), userId);
			photo.setOtherSimilars(otherSimilars);
		}
		
		return new PageImpl<PacallPhoto>(result, page, count);
	}
	
	public Map<String, Long> findTagsAndNumber(String collectionId) {
		Map<String, Long> result = new LinkedHashMap<>();
		result.put("all", pacallPhotoDao.countListInCollection(collectionId));
		result.put("recommended", pacallPhotoDao.countListForRecommendedInCollection(collectionId));
		result.put("normal", pacallPhotoDao.countListForNormalInCollection(collectionId));
		result.put("manual", pacallPhotoDao.countListForManualInCollection(collectionId));
		result.put("face", pacallPhotoDao.countListForFaceInCollection(collectionId));
		result.put("text", pacallPhotoDao.countListForTextInCollection(collectionId));
		result.put("feature", pacallPhotoDao.countListForFeatureInCollection(collectionId));
		result.put("duplicated", pacallPhotoDao.countListForDuplicatedInCollection(collectionId));
		result.put("dark", pacallPhotoDao.countListForDarkInCollection(collectionId));
		result.put("ullolike", pacallPhotoDao.countListForUllolikeInCollection(collectionId));
		return result;
	}

//	public Map<String, PacallTagCount> findTagsAndNumber(Date date, String userId) {
//		Map<String, PacallTagCount> result = new HashMap<>();
//		long all = pacallPhotoDao.countList(userId, date);
//		result.put("all", new PacallTagCount("all", "All", all));
//		
//		long recommended = pacallPhotoTagDao.countRecommened(date, userId);
//		result.put("recommended", new PacallTagCount("recommended", "Recommended", recommended));
//		
//		long normal = pacallPhotoTagDao.countNormal(date, userId);
//		result.put("normal", new PacallTagCount("normal", "Normal", normal));
//		
//		List<PacallTagCount> tagsResult = pacallPhotoTagDao.findMapListTagsAndNumber(date, userId);
//		for(PacallTagCount c: tagsResult){
//			result.put(c.getTag(), c);
//		}
//		return result;
//	}
	
//	public Map<String, PacallExtraInfo> addExtraInfoToPhoto(List<PacallPhoto> list) {
//		Map<String, PacallExtraInfo> result = new HashMap<String, PacallExtraInfo>();
//		for(PacallPhoto photo: list){
//			PacallExtraInfo extraInfo = new PacallExtraInfo();
//			List<Map<String, Object>> tagInfo =  pacallTagDao.findTagsInfoForPhoto(photo.getId());
//			for(Map<String, Object> info: tagInfo){
//				if(PacallTag.TAG_DARK.equals(info.get("tag"))){
//					extraInfo.setDark(true);
//				}else if(PacallTag.TAG_MANUAL.equals(info.get("tag"))){
//					extraInfo.setManual(true);
//				}else if(PacallTag.TAG_DUPLICATED.equals(info.get("tag"))){
//					extraInfo.setGroupId(info.get("extra").toString());
//				}else if(PacallTag.TAG_FACE.equals(info.get("tag"))){
//					extraInfo.setFace(true);
//				}else if(PacallTag.TAG_FEATURE.equals(info.get("tag"))){
//					extraInfo.setFeature(true);
//				}
//				//TODO other info from tag
//			}
//			List<PacallPhotoCompItem> compItems = pacallPhotoCompItemDao.findListByPhotoId(photo.getId());
//			List<ItemDTO> otherList = new ArrayList<ItemDTO>();
//			List<ItemDTO> selfList = new ArrayList<ItemDTO>();
//			for(PacallPhotoCompItem comp: compItems){
//				ItemDTO dto = itemDao.findDTOById(comp.getItemId());
//				if(photo.getUserId().equals(dto.getAuthorId())){
//					selfList.add(dto);
//				}else{
//					otherList.add(dto);
//				}
//			}
//			extraInfo.setUploadedByOthers(otherList);
//			extraInfo.setUploadedBySelf(selfList);
//			result.put(photo.getId(), extraInfo);
//		}
//		return result;
//	}

	public PacallPhoto findById(String id, String userId) {
		PacallPhoto photo =  pacallPhotoDao.findById(id);
		List<PacallPhoto> children = new ArrayList<>();
		if(StringUtils.isNotBlank(photo.getParentId())){
			children.add(pacallPhotoDao.findById(photo.getParentId()));
			children.addAll(pacallPhotoDao.findListByParentId(photo.getId()));
		}else{
			children = pacallPhotoDao.findListByParentId(photo.getId());
		}
		photo.setChildren(children);
		List<PacallSimilar> mySimilars = pacallSimilarDao.findListByPhotoIdByUser(photo.getId(), userId);
		photo.setMySimilars(mySimilars);
		List<PacallSimilar> otherSimilars = pacallSimilarDao.findListByPhotoIdByOthers(photo.getId(), userId);
		photo.setOtherSimilars(otherSimilars);
		return photo;
	}

	public List<PacallPhoto> findDuplicatedListByPhotoId(String id) {
		return pacallPhotoDao.findDuplicatedListByPhotoId(id);
	}

	public List<PacallSensor> findSensorFilesByCollectionId(String collectionId) {
		return pacallSensorDao.findListByCollectionId(collectionId);
	}
	
	public List<PacallGps> findGpsFileByCollectionId(String collectionId) {
		return pacallGpsDao.findListByCollectionId(collectionId);
	}

	@Transactional(readOnly = false)
	public void recordUploadedPhoto(String itemId, String photoId, int know,
			int what) {
		PacallItemRecord record = new PacallItemRecord();
		record.setId(KeyGenerateUtil.generateIdUUID());
		record.setItemId(itemId);
		record.setPhotoId(photoId);
		record.setKnow(know);
		record.setWhat(what);
		pacallItemRecordDao.insert(record);
	}
}
