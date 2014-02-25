package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.entity.PacallTag;

public interface PacallTagDao {

	Long countAll();

	void insert(PacallTag pacallTag);

	PacallTag findByTag(String tag);

	List<Map<String, Object>> findTagsInfoForPhoto(String photoId);

}
