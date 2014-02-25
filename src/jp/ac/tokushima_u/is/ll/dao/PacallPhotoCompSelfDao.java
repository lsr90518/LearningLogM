package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.PacallPhotoCompSelf;

public interface PacallPhotoCompSelfDao {

	void deleteAllBySelfId(String id);

	void insert(PacallPhotoCompSelf comp);

	List<PacallPhotoCompSelf> findListALLInCollection(String collectionId);
}