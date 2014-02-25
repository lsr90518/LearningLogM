package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.PacallSensor;

import org.apache.ibatis.annotations.Param;

public interface PacallSensorDao {

	List<PacallSensor> findListByUserIdAndHash(@Param("userId") String userId, @Param("hash")String hash);

	void insert(PacallSensor sensor);

	List<PacallSensor> findListByUserId(String userId);

	List<PacallSensor> findListByCollectionIdAndHash(@Param("collectionId")String collectionId, @Param("hash")String md5);

	List<PacallSensor> findListByCollectionId(String collectionId);

	PacallSensor findById(String sensorId);

}
