package jp.ac.tokushima_u.is.ll.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.LogSendMailDao;
import jp.ac.tokushima_u.is.ll.entity.LogSendMail;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LogSendMailService {
	
	@Autowired
	private LogSendMailDao logSendMailDao;

	private static final String WEEKLY_NOTIFICATION_ID = "weeklyNotification";

	public boolean findIsWeeklyNotificationSent(String pcEmail) {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.add(Calendar.DAY_OF_YEAR, -2);
		endCal.add(Calendar.DAY_OF_YEAR, +2);
		List<LogSendMail> list = logSendMailDao.findListByPeriod(pcEmail, WEEKLY_NOTIFICATION_ID, startCal.getTime(), endCal.getTime());

		return list.size()>0;
	}

	@Transactional(readOnly = false)
	public void saveSendWeeklyNotification(String pcEmail) {
		LogSendMail log = new LogSendMail();
		log.setAddress(pcEmail);
		log.setSendId(WEEKLY_NOTIFICATION_ID);
		log.setSendTime(new Date());
		log.setId(KeyGenerateUtil.generateIdUUID());
		logSendMailDao.insert(log);
	}
}
