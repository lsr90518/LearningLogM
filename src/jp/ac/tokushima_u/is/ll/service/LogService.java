package jp.ac.tokushima_u.is.ll.service;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemQueueDao;
import jp.ac.tokushima_u.is.ll.dao.LogLoginDao;
import jp.ac.tokushima_u.is.ll.dao.LogUserReadItemDao;
import jp.ac.tokushima_u.is.ll.entity.ItemQueue;
import jp.ac.tokushima_u.is.ll.entity.LogLogin;
import jp.ac.tokushima_u.is.ll.entity.LogUserReadItem;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizConstants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Houbin
 */
@Service
@Transactional(readOnly = true)
public class LogService {

	@Autowired
    private LogUserReadItemDao logUserReadItemDao;
	@Autowired
	private LogLoginDao logLoginDao;
	@Autowired
	private ItemQueueDao itemQueueDao;
	@Autowired
	private ItemQueueService itemQueueService;
	
	@Transactional(readOnly = false)
    public void logUserReadItem(String itemId, String userId, Double latitude, Double longitude, Float speed){
        LogUserReadItem logUserReadItem = new LogUserReadItem();
        logUserReadItem.setItemId(itemId);
        logUserReadItem.setUserId(userId);
        logUserReadItem.setCreateTime(new Date(System.currentTimeMillis()));
        logUserReadItem.setLatitude(latitude);
        logUserReadItem.setLongitude(longitude);
        logUserReadItem.setSpeed(speed);
        logUserReadItem.setId(KeyGenerateUtil.generateIdUUID());
        logUserReadItemDao.insert(logUserReadItem);
        
    	List<LogUserReadItem> readlist = this.logUserReadItemDao.findListByUserAndItem(userId, itemId);
    	int count = readlist.size();
    	List<ItemQueue> queueList = this.itemQueueDao.findListByUserAndItem(userId, itemId);
    	if(count>2&&queueList.isEmpty()){
    		this.itemQueueService.updateItemQueue(itemId, userId, QuizConstants.QueueTypeViewObject);
//    		ItemQueue iq = new ItemQueue();
//    		iq.setAuthor(user);
//    		iq.setItem(item);
//    		iq.setCreateTime(new Date());
//    		iq.set
    	}
    }
    
	@Transactional(readOnly = false)
    public void logUserLogin(String userId, int device){
    	LogLogin logLogin = new LogLogin();
    	logLogin.setLoginDevice(device);
    	logLogin.setUser(userId);
    	logLogin.setLoginTime(new Date(System.currentTimeMillis()));
    	logLogin.setId(KeyGenerateUtil.generateIdUUID());
    	logLoginDao.insert(logLogin);
    }
}
