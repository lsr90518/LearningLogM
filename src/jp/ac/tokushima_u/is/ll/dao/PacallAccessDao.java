package jp.ac.tokushima_u.is.ll.dao;

import java.util.Date;

import jp.ac.tokushima_u.is.ll.entity.PacallAccess;

import org.apache.ibatis.annotations.Param;

public interface PacallAccessDao {

	PacallAccess findLast(String userId);

	void insert(PacallAccess access);

	void updateEndtime(@Param("id")String id, @Param("date")Date date);

}
