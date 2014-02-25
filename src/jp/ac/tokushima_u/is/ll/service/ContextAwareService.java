package jp.ac.tokushima_u.is.ll.service;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.ItemAlarmDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemNotifyDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dao.StudyAreaDao;
import jp.ac.tokushima_u.is.ll.dao.StudySpeedDao;
import jp.ac.tokushima_u.is.ll.dao.StudyTimeDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemAlarm;
import jp.ac.tokushima_u.is.ll.entity.ItemNotify;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.SendTime;
import jp.ac.tokushima_u.is.ll.entity.StudyArea;
import jp.ac.tokushima_u.is.ll.entity.StudySpeed;
import jp.ac.tokushima_u.is.ll.entity.StudyTime;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ContextAwareForm;
import jp.ac.tokushima_u.is.ll.form.NotifyForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.helper.QuizCondition;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("contextAwareService")
@Transactional(readOnly = true)
public class ContextAwareService {
	
	public static float MinSpeed = 5; 
	@SuppressWarnings("deprecation")
	public static Time LatestTime = new Time(22,30,0);
	@SuppressWarnings("deprecation")
	public static Time EarlyTime = new Time(07,30,0);
	
	@Autowired
	private StudyAreaDao studyAreaDao;
	@Autowired
	private ItemNotifyDao itemNotifyDao;
	@Autowired
	private ItemAlarmDao itemAlarmDao;
	@Autowired
	private StudyTimeDao studyTimeDao;
	@Autowired
	private StudySpeedDao studySpeedDao;
	@Autowired
	private MyQuizDao myquizDao;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
	private LLQuizService quizSerivice;
	@Autowired
	private AutoQuizSendService autoSendService;
	
		
	/**
	 * @param form
	 * <p>This method is used to record the alarmed items</p>
	 */
	@Transactional(readOnly = false)
	public void feedBackContextAware(NotifyForm form){
		Integer alarmType = form.getAlarmType();
		Users author = usersDao.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		
		ItemAlarm itemalarm = null;
		
		if(form.getNotifyCode()!=null)
			itemalarm = this.itemAlarmDao.findByUnicode(form.getNotifyCode());
		
		if(itemalarm==null){
			itemalarm = new ItemAlarm();
			itemalarm.setUnicode(form.getNotifyCode());
		}
		
		if(form.getItemid()!=null){
			Item item = this.itemDao.findById(form.getItemid());
			if(item!=null)
				itemalarm.setItem(item.getId());
		}
		itemalarm.setAuthorId(author.getId());
		itemalarm.setCreateTime(new Date(form.getCreatetime()));
		itemalarm.setAlarmType(alarmType);
		itemalarm.setLat(form.getLat());
		itemalarm.setLng(form.getLng());
		itemalarm.setSpeed(form.getSpeed());
		itemalarm.setFeedback(form.getFeeback());
		itemalarm.setUpdateTime(new Date(form.getUpdatetime()));
		if(itemalarm.getId()==null){
			itemalarm.setId(KeyGenerateUtil.generateIdUUID());
			this.itemAlarmDao.insert(itemalarm);
		}else{
			this.itemAlarmDao.update(itemalarm);
		}
		
	}
	
	/*
	public void feedBackContextAware(NotifyForm form){
		Integer alarmType = form.getAlarmType();
		Users author = userService.getById(SecurityUserHolder.getCurrentUser()
				.getId());
		ItemAlarm itemalarm = new ItemAlarm();
		if(form.getItemid()!=null){
			Item item = this.itemService.findById(form.getItemid());
			if(item!=null)
				itemalarm.setItem(item);
		}
		itemalarm.setAuthor(author);
		itemalarm.setCreateTime(new Date(form.getCreatetime()));
		itemalarm.setAlarmType(alarmType);
		itemalarm.setLat(form.getLat());
		itemalarm.setLng(form.getLng());
		itemalarm.setSpeed(form.getSpeed());
		itemalarm.setFeedback(form.getFeeback());
		this.itemAlarmDao.save(itemalarm);
	}
	*/
	
	public Map<String, Integer> findAlaramedTimesByLocation(Users user, Double lat, Double lng, double distance){
//		DetachedCriteria dc = DetachedCriteria.forClass(ItemAlarm.class);
//		dc.add(Restrictions.eq("author", user));
		Map<String, Integer> map = new HashMap<String,Integer>();
		
//		String sql = "select a.* from t_itemalarm a where a.author_id = '"+user.getId()+"' and get_distance("+lat.toString()+", "+lng.toString()+", a.lat, a.lng) < "+distance;
//		String sql_quiz = sql +"  and a.alarm_type = 1 and a.feedback = 1";
//		String sql_item = sql +"  and a.alarm_type = 2 and a.feedback = 1";
//		String sql_quiz_all = sql +"  and a.alarm_type = 1";
//		String sql_item_all = sql +"  and a.alarm_type = 2";
		//List<ItemAlarm> quiz_alarms = this.sessionFactory.getCurrentSession().createSQLQuery(sql_quiz).addEntity(ItemAlarm.class).list();
		List<ItemAlarm> quiz_alarms = itemAlarmDao.findListForQuiz(user.getId(), lat, lng, distance, 1, 1);
		map.put("quiznum", quiz_alarms.size());
//		List<ItemAlarm> quiz_alarms_all =  this.sessionFactory.getCurrentSession().createSQLQuery(sql_quiz_all).addEntity(ItemAlarm.class).list();
		List<ItemAlarm> quiz_alarms_all = itemAlarmDao.findListForQuiz(user.getId(), lat, lng, distance, 1, null);
		map.put("quiz_all_num", quiz_alarms_all.size());
//		List<ItemAlarm> item_alarms = this.sessionFactory.getCurrentSession().createSQLQuery(sql_item).addEntity(ItemAlarm.class).list();
		List<ItemAlarm> item_alarms = itemAlarmDao.findListForQuiz(user.getId(), lat, lng, distance, 2, 1);
		map.put("itemnum", item_alarms.size());
//		List<ItemAlarm> item_alarms_all = this.sessionFactory.getCurrentSession().createSQLQuery(sql_item_all).addEntity(ItemAlarm.class).list();
		List<ItemAlarm> item_alarms_all = itemAlarmDao.findListForQuiz(user.getId(), lat, lng, distance, 2, null);
		map.put("item_all_num", item_alarms_all.size());
		return map;
	}
	
	
//	public List searchContextedItems(ContextAwareForm contextform) {
//		Users user = userService.findById(SecurityUserHolder.getCurrentUser()
//				.getId());
//
//		Page<Item>items = this.itemService.searchNearestItems(null, contextform.getLatitude(),
//				contextform.getLongitude(), 0.1, null, 100);
//		
//		return null;
//	}
	
	@Transactional(readOnly = false)
	public ItemNotify contextAnalyze(ContextAwareForm contextform){
		Users user = usersDao.findById(SecurityUserHolder.getCurrentUser()
				.getId());

		//30分以内で推薦されて、答えはない場合
//		DetachedCriteria notifyCrietira = DetachedCriteria.forClass(ItemNotify.class);
//		notifyCrietira.add(Restrictions.eq("author", user));
//		notifyCrietira.addOrder(Order.desc("createTime"));
		List<ItemNotify> notifylist = this.itemNotifyDao.findByAuthor(user.getId());
		if(notifylist!=null&&notifylist.size()>0){
			ItemNotify itemnotify =notifylist.get(0);
			if(itemnotify.getFeedback()==0&&Utility.getDifferMinuteTwoDays(new Date(), itemnotify.getCreateTime())<30){
				return null;
			}	
		}
		
//		DetachedCriteria studyCrietira = DetachedCriteria.forClass(MyQuiz.class);
//		studyCrietira.add(Restrictions.eq("author", user));
//		studyCrietira.addOrder(Order.desc("createDate"));
		List<MyQuizDTO> myquizlist = this.myquizDao.findListByAuthor(user.getId());
		if(myquizlist!=null&&myquizlist.size()>0){
			MyQuiz myquiz =myquizlist.get(0);
			if(Utility.getDifferMinuteTwoDays(new Date(), myquiz.getCreateDate())<20){
				return null;
			}	
		}

		//Speedは大丈夫かどうかをチェックする
		Float speed = contextform.getSpeed();
		Double lat = contextform.getLatitude();
		Double lng = contextform.getLongitude();
		Time t = contextform.getCurrent();
		if(t==null)
			t = Utility.getNowTime();
		
		//Too early or Too late
		if(t.after(LatestTime)||t.before(EarlyTime)){
			return null;
		}
		
		QuizCondition quizCon = new QuizCondition();
		quizCon.setUserId(user.getId());
		quizCon.setLat(lat);
		quizCon.setSpeed(speed);
		quizCon.setLng(lng);
		
		@SuppressWarnings("unused")
		//TODO rightspeed is not used
		boolean rightspeed = false;
		List<StudySpeed> speedList = null;
		if(speed!=null){
			speedList = this.studySpeedDao.findListByAuthor(user.getId()); //user.getSpeedList();
			if(speedList!=null&&speedList.size()>0){
//				DetachedCriteria speedcriteria = DetachedCriteria.forClass(StudySpeed.class);
//				speedcriteria.add(Restrictions.eq("author", user));
//				speedcriteria.add(Restrictions.lt("mixspeed", speed));
//				speedcriteria.add(Restrictions.gt("maxspeed", speed));
//				speedcriteria.add(Restrictions.eq("disabled", 0));
				List<StudySpeed> sp =this.studySpeedDao.findListBySpeed(user.getId(), speed);
				if(sp!=null&&sp.size()>0)
					rightspeed = true;
			}else{
				if(speed<MinSpeed)
					rightspeed = true;
			}
		}
		
		List<SendTime> sendtimes = this.autoSendService.findUserSendTime(user.getId());
		if(sendtimes!=null&sendtimes.size()>0){
			System.out.println("auto send time to send quizzes");
			quizCon.setAlarmtype(Constants.TimeReuestType);
			MyQuiz quiz = this.quizSerivice.findQuiz(quizCon);
			if(quiz!=null){
				ItemNotify notify = new ItemNotify();
				notify.setAlarmType(Constants.TimeReuestType);
				notify.setQuizid(quiz.getId());
				notify.setAuthorId(user.getId());
				notify.setCreateTime(new Date());
				notify.setFeedback(0);
				notify.setNotifyMode(Constants.NotifyTypeTextQuiz);
				notify.setId(KeyGenerateUtil.generateIdUUID());
				this.itemNotifyDao.insert(notify);
				this.itemNotifyDao.insertNofityItems(quiz.getItemId(), notify.getId());
				return notify;
			}
		}
		
		if(lat!=null&&lng!=null){
//			DetachedCriteria areacriteria = DetachedCriteria.forClass(StudyArea.class);
//			areacriteria.add(Restrictions.eq("author", user));
//			areacriteria.add(Restrictions.lt("minlat", lat));
//			areacriteria.add(Restrictions.gt("maxlat", lat));
//			areacriteria.add(Restrictions.lt("minlng", lng));
//			areacriteria.add(Restrictions.gt("maxlng", lng));
//			areacriteria.add(Restrictions.eq("disabled", 0));
			List<StudyArea> sa =this.studyAreaDao.findListByArea(user.getId(), lat, lng);
			
//			DetachedCriteria timecriteria = DetachedCriteria.forClass(StudyTime.class);
//			timecriteria.add(Restrictions.eq("author", user));
//			timecriteria.add(Restrictions.lt("starttime", t));
//			timecriteria.add(Restrictions.gt("endtime", t));
//			timecriteria.add(Restrictions.eq("disabled", 0));
			List<StudyTime> st = this.studyTimeDao.findListByTime(user.getId(), t);
			
			if(sa!=null&&sa.size()>0&&st!=null&&st.size()>0){
				quizCon.setAlarmtype(Constants.FixLocationRequestType);
				MyQuiz quiz = this.quizSerivice.findQuiz(quizCon);
				if(quiz!=null){
					ItemNotify notify = new ItemNotify();
					notify.setAlarmType(Constants.FixLocationRequestType);
					notify.setQuizid(quiz.getId());
					notify.setAuthorId(user.getId());
					notify.setCreateTime(new Date());
					notify.setFeedback(0);
					notify.setNotifyMode(Constants.NotifyTypeTextQuiz);
					this.itemNotifyDao.insert(notify);
					this.itemNotifyDao.insertNofityItems(quiz.getItemId(), notify.getId());
					return notify;
				}
			}else{
				try{
					List<Item> myitems = this.itemService.findItemByLocation(lat, lng, Boolean.TRUE);
					if(myitems!=null&&myitems.size()>0){
						quizCon.setAlarmtype(Constants.LocationRequestType);
						for(int i=0;i<myitems.size();i++){
							Item item = myitems.get(i);
							quizCon.setItemId(item.getId());
							MyQuiz quiz = this.quizSerivice.findItemQuiz(quizCon);
							if(quiz!=null){
								ItemNotify notify = new ItemNotify();
								notify.setAlarmType(Constants.LocationRequestType);
								notify.setQuizid(quiz.getId());
								notify.setAuthorId(user.getId());
								notify.setCreateTime(new Date());
								notify.setFeedback(0);
								notify.setNotifyMode(Constants.NotifyTypeTextQuiz);
								this.itemNotifyDao.insert(notify);
								this.itemNotifyDao.insertNofityItems(quiz.getItemId(), notify.getId());
								return notify;
							}
						}
					}
					
					List<Item> otheritems = this.itemService.findItemByLocation(lat, lng, Boolean.FALSE);
					if(otheritems!=null&&otheritems.size()>0){
						ItemNotify notify = new ItemNotify();
						notify.setAuthorId(user.getId());
						notify.setCreateTime(new Date());
						notify.setFeedback(0);
						notify.setNotifyMode(Constants.NotifyTypeMessage);
						this.itemNotifyDao.insert(notify);
						
						for(Item item: otheritems){
							this.itemNotifyDao.insertNofityItems(item.getId(), notify.getId());
						}
						return notify;
					}
				}catch(Exception e){
				}
			}
		}
		
		return null;
	}

	@Transactional(readOnly = false)
	public void updateItemNotify(String notifyid){
		ItemNotify itemnotify = this.itemNotifyDao.findById(notifyid);
		if(itemnotify!=null){
			itemnotify.setFeedback(1);
			this.itemNotifyDao.update(itemnotify);
			System.out.println(notifyid+"has been feedbacked");
		}
	}
}
