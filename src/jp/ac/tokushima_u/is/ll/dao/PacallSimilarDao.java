package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.PacallSimilar;

import org.apache.ibatis.annotations.Param;

public interface PacallSimilarDao {

	PacallSimilar findByUnique(@Param("photoId")String photoId, @Param("itemId") String itemId);

	void insert(PacallSimilar pacallSimilar);

	List<PacallSimilar> findListByPhotoIdByUser(@Param("photoId")String id, @Param("userId")String userId);

	List<PacallSimilar> findListByPhotoIdByOthers(@Param("photoId")String id, @Param("userId")String userId);

	void deleteByPhotoId(String photoId);

}
