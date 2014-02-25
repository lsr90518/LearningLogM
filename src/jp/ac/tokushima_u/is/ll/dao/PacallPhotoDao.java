package jp.ac.tokushima_u.is.ll.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

public interface PacallPhotoDao {

	List<PacallPhoto> findListByUserIdAndHash(@Param("userId") String userId, @Param("hash")String hash);
	List<PacallPhoto> findListByCollectionIdAndHash(@Param("collectionId")String collectionId, @Param("hash")String md5);
	PacallPhoto findListByCollectionIdAndFilename(@Param("collectionId")String collectionId, @Param("filename")String filename);

	void insert(PacallPhoto photo);

	List<Map<String, Object>> findMapListForCountPerDay(String userId);

	List<PacallPhoto> findList(@Param("userId")String userId, @Param("day")Date day, @Param("page")Pageable page);

	Long countList(@Param("userId")String userId, @Param("day")Date day);

	PacallPhoto findById(String id);

	void updateIndexedFlag(@Param("id")String id, @Param("indexed")Boolean indexed);

	void updatePhotoBasicInfo(@Param("collectionId")String collectionId, @Param("filename")String filename, @Param("photoDate")Date photoDate,
			@Param("reason")String reason);

	List<PacallPhoto> findListSensorPhotoByUserId(String userId);

	void update(PacallPhoto photo);

	List<PacallPhoto> findAllByUserId(@Param("userId")String userId, @Param("date") Date date);

	List<PacallPhoto> findListByTag(@Param("userId")String userId, @Param("date")Date day, @Param("page")Pageable page,
			@Param("tag")String tag);

	Long countListByTag(@Param("userId")String userId, @Param("date")Date day, @Param("tag")String tag);

	List<PacallPhoto> findListForRecommended(@Param("userId")String userId, @Param("date")Date day, @Param("page")Pageable page);

	Long countListForRecommended(@Param("userId")String userId, @Param("date")Date day);

	List<PacallPhoto> findListForNormal(@Param("userId")String userId, @Param("date")Date day, @Param("page")Pageable page);

	Long countListForNormal(@Param("userId")String userId, @Param("date")Date day);

	List<PacallPhoto> findListAllPhotosInDay(@Param("userId") String userId, @Param("data") Date date);

	List<PacallPhoto> findAllByUserIdAndNoGps(@Param("userId")String userId, @Param("date") Date date);
	
	List<PacallPhoto> findAllByCollectionIdNoGps(String collectionId);

	List<PacallPhoto> findDuplicatedListByPhotoId(String photoId);

	//Recommandation
	List<PacallPhoto> findListForRecommendedInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForRecommendedInCollection(String collectionId);
	
	//Normal
	List<PacallPhoto> findListForNormalInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForNormalInCollection(String collectionId);
	
	List<PacallPhoto> findListForManualInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForManualInCollection(String collectionId);
	
	//Face
	List<PacallPhoto> findListForFaceInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForFaceInCollection(String collectionId);
	
	//Text
	List<PacallPhoto> findListForTextInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForTextInCollection(String collectionId);
	
	//Feature
	List<PacallPhoto> findListForFeatureInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForFeatureInCollection(String collectionId);
	
	//Duplicated
	List<PacallPhoto> findListForDuplicatedInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForDuplicatedInCollection(String collectionId);
	
	//Dark
	List<PacallPhoto> findListForDarkInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForDarkInCollection(String collectionId);
	
	//ULLO-like
	List<PacallPhoto> findListForUllolikeInCollection(@Param("collectionId")String collectionId,@Param("page")Pageable page);
	Long countListForUllolikeInCollection(String collectionId);
	
	//All
	List<PacallPhoto> findListInCollection(@Param("collectionId")String collectionId, @Param("page")Pageable page);
	Long countListInCollection(String collectionId);
	
	
	void updateExtraInfo(PacallPhoto photo);
	List<PacallPhoto> findListByParentId(String id);
}
