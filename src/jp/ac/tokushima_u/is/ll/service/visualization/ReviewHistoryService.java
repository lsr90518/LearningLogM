package jp.ac.tokushima_u.is.ll.service.visualization;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemStateDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.ItemState;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.service.UserService;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author li
 */
@Service("reviewHistoryService")
@Transactional
public class ReviewHistoryService {
	
		@Autowired
	    private ItemDao itemDao;
		@Autowired
	    private UsersDao userDao;
		@Autowired
	    private ItemStateDao itemStateDao;
	    @Autowired
	    private UserService userService;
	    
	    public MyQuiz findItemState() {
	    	List<Users> users = userService.findAllUsers();
	    	for(Users user:users){
	    		List<ItemState> states = itemStateDao.findListByUserId(user.getId());
	    		if(states.size()>0)
	    			continue;
	    		searchUserItemHistory(user.getId());
	    	}
	    	
	    	return null;
	    }
	    
	    public ItemState searchUserItemState(String userId, String itemId){
//	    	DetachedCriteria dc = DetachedCriteria.forClass(ItemState.class); 
//	    	dc.add(Restrictions.eq("user", user));
////	    	dc.add(Restrictions.eq("item", itemDao.findUniqueBy("id", itemId)));
//	    	dc.add(Restrictions.eq("item", item));
	    	List<ItemState> isList = this.itemStateDao.findListByUserIdAndItemId(userId, itemId);
	    	return isList.isEmpty()?null:isList.get(0);
	    }
	    
	    public void updateItemState(String itemId, String userId, Integer experState, Integer quizAnswerState){
//	    	DetachedCriteria dc = DetachedCriteria.forClass(ItemState.class); 
//	    	dc.add(Restrictions.eq("user", user));
//	    	dc.add(Restrictions.eq("item", item));
	    	List<ItemState> isList = this.itemStateDao.findListByUserIdAndItemId(userId, itemId);
	    	ItemState is = null;
	    	String quizState = "";
	    	if(isList != null && isList.size()>0){
	    		is = isList.get(0);
	    		quizState = is.getQuizState();
	    	}else{
	    		is = new ItemState();
	    		is.setItemId(itemId);
	    		is.setAuthorId(userId);
	    		is.setCreateDate(new Date());
	    	}
	    	is.setUpdateDate(new Date());
	    	if(experState != null)
	    		is.setExperState(experState);
	    	if(quizAnswerState != null){
	    		if(quizState!=null && quizState.length()>0)
		    		quizState = quizAnswerState.toString()+", "+quizState;
		    	else
		    		quizState = quizAnswerState.toString();
		    	
	    		is.setQuizState(quizState);
	    		is.setRememberState(quizAnswerState);
	    	}
	    	is.setUpdateDate(new Date());
	    	if(is.getId()==null){
	    		is.setId(KeyGenerateUtil.generateIdUUID());
	    		itemStateDao.insert(is);
	    	}else{
	    		itemStateDao.update(is);
	    	}
	    }
	   
	    
	    public List<ItemState> findUserItemState(String userId, Integer experState, Integer rememberState){
//	    	DetachedCriteria dc = DetachedCriteria.forClass(ItemState.class); 
//	    	dc.add(Restrictions.eq("user", user));
//	    	dc.add(Restrictions.eq("experState", experState));
//	    	dc.add(Restrictions.eq("rememberState", rememberState));
	    	ItemState itemState = new ItemState();
	    	itemState.setAuthorId(userId);
	    	itemState.setExperState(experState);
	    	itemState.setRememberState(rememberState);
	    	return itemStateDao.findListByExample(itemState);
	    }
	    
	    public void searchUserItemHistory(String authorId){
//	    	String sql = "select item_id, q.author_id, i.author_id as itemauthor, group_concat(conv(oct(answerstate),8,10) order by update_date desc ) as state, max(q.update_date) as update_date " +
//	    			"from t_myquiz q, t_item i " +
//	    			"where q.answerstate != -1 " +
//	    			"and q.answerstate != -1 and q.item_id = i.id  and q.item_id is not null ";
//	    	String group_sql = " group by q.item_id order by q.item_id ";
//	    	if(authorId != null && authorId.length()>0)
//	    		sql = sql +" and q.author_id = '"+authorId+"' ";
////	    	if(itemId != null && itemId.length()>0)
////	    		sql = sql +" and i.id = '"+itemId+"' ";
//	    	sql = sql + group_sql;
	    	
//	    	Session session = this.sessionFactory.getCurrentSession();
//			List<ItemState> itemList = session.createSQLQuery(sql).addEntity(ItemState.class).list();
//			List itemList = session.createSQLQuery(sql).list();
//			for(Object itemState:itemList){
//				Object[] items = (Object[])itemState;
//				if(items != null && items.length == 5){
//					ItemState state = new ItemState();
//					state.setItem(itemDao.findUniqueBy("id", items[0]));
//					state.setUser(userDao.findUniqueBy("id", items[1]));
//					if(items[1].equals(items[2]))
//						state.setExperState(1);
//					else
//						state.setExperState(0);
//					
//					Integer answerState = analyzeQuizState((String)items[3]);
//					state.setRememberState(answerState);
//					state.setQuizState((String)items[3]);
//					state.setCreateDate(new Date());
//					state.setUpdateDate((Date)items[4]);
//					
//					itemStateDao.save(state);
//				}
//			}
			
//			session.close();
			
			List<ItemState> list = this.itemStateDao.findListUserItemHistory(authorId);
			for(ItemState state:list){
				state.setId(KeyGenerateUtil.generateIdUUID());
				itemStateDao.insert(state);
			}
	    }

	    @SuppressWarnings("unused")
		private Integer analyzeQuizState(String state){
	    	Integer result = Constants.NotAnsweredState;
	    	if(state!=null&&state.length()>0){
	    		try{
	    			String[] states = state.split(",");
	    			Integer s = Integer.valueOf(states[0]);
	    			if(Constants.WrongAnsweredState.equals(s))
	    				return Constants.WrongAnsweredState;
	    			else if(Constants.EasyAnsweredState.equals(s))
	    				return Constants.EasyAnsweredState;
	    			else if(Constants.DifficultAnsweredState.equals(s))
	    				return Constants.DifficultAnsweredState;
	    			else if(Constants.PassAnsweredState.equals(s))
	    				return Constants.PassAnsweredState;
	    			else if(Constants.CorrectAnsweredState.equals(s)){
	    				return Constants.CorrectAnsweredState;
	    			}
	    		}catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    	return result;
	    }
}
