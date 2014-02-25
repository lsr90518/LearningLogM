package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.PacallGps;

import org.apache.ibatis.annotations.Param;

public interface PacallGpsDao {

	List<PacallGps> findListByUserIdAndHash(@Param("userId") String userId, @Param("hash")String hash);

	void insert(PacallGps gps);

	List<PacallGps> findListByUserId(String userId);

	List<PacallGps> findListByCollectionIdAndHash(@Param("collectionId")String collectionId, @Param("hash")String md5);

	List<PacallGps> findListByCollectionId(String collectionId);

}
