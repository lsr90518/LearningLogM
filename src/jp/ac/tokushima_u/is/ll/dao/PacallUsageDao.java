package jp.ac.tokushima_u.is.ll.dao;

import jp.ac.tokushima_u.is.ll.entity.PacallUsage;

public interface PacallUsageDao {

	PacallUsage findById(String collectionId);

	void insert(PacallUsage usage);

	void update(PacallUsage usage);
	
}
