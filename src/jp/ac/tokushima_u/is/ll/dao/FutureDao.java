package jp.ac.tokushima_u.is.ll.dao;

import jp.ac.tokushima_u.is.ll.entity.Future_data;

public interface FutureDao {

	Future_data findById(String id);

//	void insert(FutureDao futureDao);
	void insert(Future_data future_data);
//	void delete(String id);
//
//	//TODO Temp method
//	List<FileData> findListAll();
//
//	//TODO Temp method
//	void updateFileId(FileData fileData);

}
