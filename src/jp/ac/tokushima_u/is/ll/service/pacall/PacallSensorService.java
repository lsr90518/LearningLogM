package jp.ac.tokushima_u.is.ll.service.pacall;

import jp.ac.tokushima_u.is.ll.dao.PacallSensorDao;
import jp.ac.tokushima_u.is.ll.entity.PacallSensor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacallSensorService {

	@Autowired
	private PacallSensorDao pacallSensorDao;
	
	public PacallSensor findSensorByIdAndUser(String sensorId, String userId) {
		PacallSensor sensor = pacallSensorDao.findById(sensorId);
		if(sensor!=null && userId.equals(sensor.getUserId())){
			return sensor;
		}else{
			return null;
		}
	}

}
