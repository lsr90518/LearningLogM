package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.NotificationDao;
import jp.ac.tokushima_u.is.ll.entity.Notification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NotificationService {
	
	private NotificationDao notificationDao;

	public List<Notification> getAll() {
		return this.notificationDao.findListAll();
	}

	public Notification get(String id) {
		return this.notificationDao.findById(id);
	}
}
