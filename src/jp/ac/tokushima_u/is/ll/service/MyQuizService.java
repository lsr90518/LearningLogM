package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.LogUserReadItemDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.LogUserReadItem;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.service.helper.UserQuizInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyQuizService {

    @Autowired
    private UserService userService;
    @Autowired
    private MyQuizDao myquizDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private LogUserReadItemDao logUserReadItemDao;

    public Map<String,Long> searchOneDayQuiz(Date day, Users user){
    	Map<String,Long>results = new HashMap<String,Long>();
//
//    	String hql = "from MyQuiz myquiz where author=:author ";
//    	Map<String, Object> params = new HashMap<String, Object>();
//		params.put("author", user);
//
//		String allcorrect_hql = hql + " and answerstate = 1";
//    	List<MyQuiz> allcorrects = this.myquizDao.find(allcorrect_hql, params);
//    	results.put("allcorrecttimes", allcorrects.size());
//
//    	String allwrong_hql = hql + " and answerstate = 0";
//    	List<MyQuiz> allwrongs = this.myquizDao.find(allwrong_hql, params);
//    	results.put("allwrongtimes", allwrongs.size());
//
//
//    	String allpass_hql = hql + " and answerstate = 2";
//    	List<MyQuiz> allpass = this.myquizDao.find(allpass_hql, params);
//    	results.put("allpasstimes", allpass.size());
//
//    	results.put("alltimes", allwrongs.size()+allcorrects.size());
//    	results.put("allscores", allwrongs.size()*2+allcorrects.size()*5+allpass.size()*2);
//
//    	if(day!=null){
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(day);
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);

    		Date fromday = cal.getTime();
    		cal.add(Calendar.DATE, 1);
    		Date today = cal.getTime();

//    		params.put("toDay", today);
//    	}
//
//    	String correct_hql = hql + " and answerstate = 1";
//    	List<MyQuiz> corrects = this.myquizDao.find(correct_hql, params);
//    	results.put("correcttimes", corrects.size());
//
//    	String wrong_hql = hql + " and answerstate = 0";
//    	List<MyQuiz> wrongs = this.myquizDao.find(wrong_hql, params);
//    	results.put("wrongtimes", wrongs.size());
//
//    	String pass_hql = hql + " and answerstate = 2";
//    	List<MyQuiz> pass = this.myquizDao.find(pass_hql, params);
//    	results.put("passtimes", pass.size());
//
//    	results.put("daytimes", wrongs.size()+corrects.size());
//    	results.put("scores", wrongs.size()*2+corrects.size()*5+pass.size()*2);


//    	String sql = "select answerstate, count(*) as num from t_myquiz where author_id = '"+user.getId()+"'  group by answerstate";
//    	List<Object[]> states = this.sessionFactory.getCurrentSession().createSQLQuery(sql).list();
    	List<Map<String, Object>> states = this.myquizDao.findMapListAnswerStatesByAuthorId(user.getId());
    	long allwrong = 0l;
    	long allright = 0l;
    	long allpass = 0l;
    	for(Map<String, Object> state:states){
    		Integer answerstate = (Integer)state.get("answerstate");
    		Long num = (Long)state.get("num");
    		if(answerstate == 0){
    			allwrong = num;
    		}else if(answerstate == 1){
    			allright = num;
    		}else if(answerstate == 2){
    			allpass = num;
    		}
    	}

//    	String sql_today = "select answerstate, count(*) as num from t_myquiz where author_id = '"+user.getId()+"' and update_date > '"+FormatUtil.formatYYYYMMDD(fromday)+"' and update_date< '"+FormatUtil.formatYYYYMMDD(today)+"' group by answerstate";
//		List<Object[]> t_states = this.sessionFactory.getCurrentSession()
//				.createSQLQuery(sql_today).list();
		List<Map<String, Object>> t_states = this.myquizDao.findMapListAnswerStatesByAuthorIdAndUpdateDate(user.getId(), new java.sql.Date(fromday.getTime()), new java.sql.Date(today.getTime()));
		long wrong = 0l;
		long right = 0l;
    	long pass = 0l;
    	for(Map<String, Object> state:t_states){
    		Integer answerstate = (Integer)state.get("answerstate");
    		Long num = (Long)state.get("num");
    		if(answerstate == 0){
    			wrong = num;
    		}else if(answerstate == 1){
    			right = num;
    		}else if(answerstate == 2){
    			pass = num;
    		}
    	}
    	results.put("correcttimes", right);
    	results.put("wrongtimes", wrong);
    	results.put("passtimes", pass);
    	results.put("scores", wrong*2+right*5+pass*2);

    	results.put("alltimes", allwrong+allright);
    	results.put("allscores", allwrong*2+allright*5+allpass*2);

    	return results;
    }

    public List<UserQuizInfo> searchAllUsersQuizInfo(){
    	List<Users> users = this.userService.searchAllUsers();
    	List<UserQuizInfo> infos = new ArrayList<UserQuizInfo>();
    	for(Users user:users){
    		UserQuizInfo info = new UserQuizInfo();
//    		String hql = "from MyQuiz myquiz where author=:author and answerstate = 1";
//        	Map<String, Object> params = new HashMap<String, Object>();
//    		params.put("author", user);
//    		List<MyQuiz> rightquizzes = this.myquizDao.find(hql, params);
    		List<MyQuizDTO> rightquizzes = this.myquizDao.findListRightQuizzes(user.getId());
//    		String hql1 = "from MyQuiz myquiz where author=:author and answerstate = 0";
//    		List<MyQuiz> wrongquizzes = this.myquizDao.find(hql1, params);
    		List<MyQuizDTO> wrongquizzes = this.myquizDao.findListWrongQuizzes(user.getId());

    		Integer righttimes = 0;
    		if(rightquizzes!=null)
    			righttimes = rightquizzes.size();
    		Integer wrongtimes = 0;
    		if(wrongquizzes!=null)
    			wrongtimes = wrongquizzes.size();

//    		String hql_item = "from Item item where author=:author and relogItem is null";
//    		List<Item> myitems = this.itemDao.find(hql_item, params);
    		List<Item> myitems = this.itemDao.findListByAuthorNotRelog(user.getId());

//    		String hql_relog = "from Item item where author=:author and relogItem is not null";
//    		List<Item> myrelogs = this.itemDao.find(hql_relog, params);
    		List<Item> myrelogs = this.itemDao.findListByAuthorOnlyRelog(user.getId());
    		if(myitems!=null)
    			info.setUploadnumber(myitems.size());

    		if(myrelogs!=null)
    			info.setRelognumber(myrelogs.size());

//    		String hql_read = "from LogUserReadItem readitem where readitem.user=:author";
//    		Map<String, Object> tempparams = new HashMap<String, Object>();
//    		tempparams.put("author", user);
//    		List<LogUserReadItem> readitems =  this.logUserReadItemDao.find(hql_read, tempparams);
    		List<LogUserReadItem> readitems = this.logUserReadItemDao.findListByUserId(user.getId());
    		info.setReferencenumber(readitems.size());

//    		String hql_myread = "from LogUserReadItem readitem where readitem.user=:author and readitem.item.author=:author";
//    		tempparams.put("author", user);
//    		List<LogUserReadItem> myreaditems =  this.logUserReadItemDao.find(hql_myread, tempparams);
    		List<LogUserReadItem> myreaditems = this.logUserReadItemDao.findListByUserIdSelfRead(user.getId());
    		info.setMyreferencenumber(myreaditems.size());

    		info.setUser(user);
    		info.setWrongtimes(wrongtimes);
    		info.setRighttimes(righttimes);
    		infos.add(info);
    	}
    	return infos;
    }


    // ■wakebe クイズスコアランキング
	public List<Map<String, Object>> getQuizScoreRanking(){
//		String sql =
//			"	SELECT user.*, SUM(CASE SC.answerstate WHEN 1 THEN SC.cnt*5 ELSE SC.cnt*2 END) AS score " +
//			"	FROM t_users AS user " +
//			"		INNER JOIN ( " +
//			"			SELECT " +
//			"				quiz.author_id, answerstate, count(quiz.answerstate) AS cnt " +
//			"				FROM t_myquiz AS quiz " +
//			"				WHERE quiz.answerstate >= 0 AND quiz.answerstate <= 2 " +
//			"				GROUP BY quiz.author_id,quiz.answerstate " +
//			"		) as SC ON(SC.author_id = user.id) " +
//			"		GROUP BY SC.author_id " +
//			"		ORDER BY score DESC ";

		return this.myquizDao.findMapListQuizScoreRanking();
//		return session.createSQLQuery(sql)
//			.addEntity(Users.class).addScalar("score").list();
	}


}
