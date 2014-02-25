package jp.ac.tokushima_u.is.ll.service;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.UserMessageDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.UserMessage;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserMessageService {

	@Autowired
	private UsersDao usersDao;
	@Autowired
	private UserMessageDao userMessageDao;
	
	@Transactional(readOnly = false)
	public void send(String sendFrom, String sendTo, String content) {
		Users sendFromUser = usersDao.findById(sendFrom);
		Users sendToUser = usersDao.findById(sendTo);
		if(sendFromUser == null || sendToUser == null){
			return;
		}
		UserMessage msg = new UserMessage();
		msg.setContent(content);
		msg.setSendFrom(sendFromUser.getId());
		msg.setSendTo(sendToUser.getId());
		msg.setCreateTime(new Date());
		msg.setReadFlag(false);
		msg.setId(KeyGenerateUtil.generateIdUUID());
		userMessageDao.insert(msg);
	}

	public long checkMessage(String userId) {
		Long count = userMessageDao.countNotReadForReceiver(userId);
		return count;
	}

	public List<UserMessage> searchAll(String userId) {
		return userMessageDao.findListAllForReceiver(userId);
	}

	public UserMessage getById(String msgId) {
		return userMessageDao.findById(msgId);
	}

	@Transactional(readOnly = false)
	public void changeToRead(String id) {
		Date date = new Date();
		UserMessage msg = userMessageDao.findById(id);
		if(msg == null) return;
		msg.setReadFlag(true);
		msg.setReadTime(date);
		userMessageDao.update(msg);
	}
}
