package jp.ac.tokushima_u.is.ll.service;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.CycleTimeDao;
import jp.ac.tokushima_u.is.ll.dao.SendTimeDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.CycleTime;
import jp.ac.tokushima_u.is.ll.entity.SendTime;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.util.FormatUtil;
import jp.ac.tokushima_u.is.ll.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("autoQuizSendService")
@Transactional(readOnly = true)
public class AutoQuizSendService {

    private static int beforeStartMinute = -30;
    @Autowired
    UsersDao usersDao;
    @Autowired
    private SendTimeDao sendTimeDao;
    @Autowired
    private CycleTimeDao cycleTimeDao;

    @Transactional(readOnly = false)
    public List<SendTime> findUserSendTime(String userId){
   
    	String checkday = FormatUtil.formatYYYYMMDD(Utility.getYesterday());
    	this.cycleTimeDao.delteByAuthorAndBeforeCheckday(userId, checkday);
    	
    	Users user = usersDao.findById(userId);
    	
		Date today = Utility.getToday();
//		DetachedCriteria criteria = DetachedCriteria.forClass(CycleTime.class);
//		criteria.add(Restrictions.eq("checkday", today));
//		criteria.add(Restrictions.eq("author", user));
//		criteria.addOrder(Order.desc("checktime"));
		
		List<CycleTime> results = this.cycleTimeDao.findByAuthorAndCheckday(userId, today);
		Calendar cal = Calendar.getInstance();
		Time now = new Time(cal.getTimeInMillis());
		cal.add(Calendar.MINUTE, beforeStartMinute);
		Time from = new Time(cal.getTimeInMillis());
		
		if(results!=null&&results.size()>0)
			from = results.get(0).getChecktime();
		
		Integer weektype = cal.get(Calendar.DAY_OF_WEEK);
//		DetachedCriteria sendtimecriteria = DetachedCriteria.forClass(SendTime.class);
//		sendtimecriteria.add(Restrictions.eq("typ", weektype));
//		sendtimecriteria.add(Restrictions.eq("author", user));
//		sendtimecriteria.add(Restrictions.ge("sendtime", from));
//		sendtimecriteria.add(Restrictions.lt("sendtime", now));
		List<SendTime> sendTimes = this.sendTimeDao.findListBetweenTime(weektype, user.getId(), from, now);
		
		CycleTime ct = new CycleTime();
		ct.setCheckday(today);
		ct.setChecktime(now);
		ct.setAuthorId(user.getId());
		this.cycleTimeDao.insert(ct);
		
		return sendTimes;
    }
    
    
//    public MyQuizLog findLatestMyQuizLogByQuiz(LLQuiz llquiz, Users user){
//    	DetachedCriteria criteria = DetachedCriteria.forClass(MyQuiz.class);
//    	criteria.add(Restrictions.eq("author", user));
//    	criteria.add(Restrictions.eq("llquiz", llquiz));
//    	List<MyQuiz> myquizzes = this.myquizDao.find(criteria);
//    	if(myquizzes!=null&&myquizzes.size()>0){
//    		MyQuiz myquiz = myquizzes.get(0);
//    		DetachedCriteria logcriteria = DetachedCriteria.forClass(MyQuizLog.class);
//    		logcriteria.add(Restrictions.eq("myquiz", myquiz));
//    		logcriteria.addOrder(Order.desc("createDate"));
//    		List<MyQuizLog> myquizlogs = this.myquizlogDao.find(logcriteria);
//    		if(myquizlogs!=null&&myquizlogs.size()>0)
//    			return myquizlogs.get(0);
//    	}
//    	return null;
//    }
    
}
