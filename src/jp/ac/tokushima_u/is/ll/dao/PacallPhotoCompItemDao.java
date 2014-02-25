package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.PacallPhotoCompItem;

public interface PacallPhotoCompItemDao {

	void deleteAllByPhotoId(String id);

	void insert(PacallPhotoCompItem comp);

	List<PacallPhotoCompItem> findListByPhotoId(String photoId);

}
