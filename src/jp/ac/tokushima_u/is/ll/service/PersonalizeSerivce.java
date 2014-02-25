package jp.ac.tokushima_u.is.ll.service;

import java.io.PrintStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.LogUserReadItemDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dao.SendTimeDao;
import jp.ac.tokushima_u.is.ll.dao.SettingDao;
import jp.ac.tokushima_u.is.ll.dao.StudyActivityDao;
import jp.ac.tokushima_u.is.ll.dao.StudyAreaDao;
import jp.ac.tokushima_u.is.ll.dao.StudyPhaseDao;
import jp.ac.tokushima_u.is.ll.dao.StudySpeedDao;
import jp.ac.tokushima_u.is.ll.dao.StudyTimeDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.GeoPoint;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.LogUserReadItem;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.SendTime;
import jp.ac.tokushima_u.is.ll.entity.StudyActivity;
import jp.ac.tokushima_u.is.ll.entity.StudyArea;
import jp.ac.tokushima_u.is.ll.entity.StudyPhase;
import jp.ac.tokushima_u.is.ll.entity.StudySpeed;
import jp.ac.tokushima_u.is.ll.entity.StudyTime;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.PersonalSyncCondForm;
import jp.ac.tokushima_u.is.ll.form.SendTimeForm;
import jp.ac.tokushima_u.is.ll.form.StudyAreaForm;
import jp.ac.tokushima_u.is.ll.form.StudyTimeForm;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.FormatUtil;
import jp.ac.tokushima_u.is.ll.util.GeoClustering;
import jp.ac.tokushima_u.is.ll.util.GeoUtils;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.SpeedClustering;
import jp.ac.tokushima_u.is.ll.util.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("personalizeService")
@Transactional(readOnly = true)
public class PersonalizeSerivce {
	
	@Autowired
	private StudyAreaDao studyAreaDao;
	@Autowired
	private StudyTimeDao studyTimeDao;
	@Autowired
	private SendTimeDao sendTimeDao;
	@Autowired
	private StudySpeedDao studySpeedDao;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private MyQuizDao myquizDao;
	@Autowired
	private UsersDao userDao;
	@Autowired
	private SettingDao settingDao;
	@Autowired
	private LogUserReadItemDao itemlogDao;
	@Autowired
	private StudyActivityDao studyActivityDao;
	@Autowired
	private StudyPhaseDao studyPhaseDao;

	@Transactional(readOnly = false)
	public void personlize() {
		PrintStream out = System.out;
		Date start = new Date();
		List<Users> users = this.userDao.findAll();
		for (Users user : users) {
			out.println("user analyzing: "+user.getNickname());
			
			List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
			List<Float> speedPoints = new ArrayList<Float>();
//			DetachedCriteria activityCriteria = DetachedCriteria.forClass(StudyActivity.class);
//			activityCriteria.add(Restrictions.eq("author", user));
//			List<StudyActivity> activities = this.studyActivityDao.find(activityCriteria);
			List<StudyActivity> activities = this.studyActivityDao.findListByAuthorId(user.getId());
			
			for(StudyActivity sa:activities){
				Double lat = sa.getLatitude();
				Double lng = sa.getLongitude();
				if(GeoUtils.validateLatLng(lat, lng)&&GeoUtils.isHighPrecision(lat, lng)){
					GeoPoint gp = new GeoPoint(lat,lng);
					geoPoints.add(gp);
				}
				if(sa.getSpeed()!=null&&sa.getSpeed()>0){
					speedPoints.add(sa.getSpeed());
				}
			}
			
//			DetachedCriteria areaCriteria = DetachedCriteria.forClass(StudyArea.class);
//			areaCriteria.add(Restrictions.eq("author", user));
//			areaCriteria.add(Restrictions.eq("disabled",0));
//			areaCriteria.add(Restrictions.gt("createDate", Utility.getYesterday()));
			StudyArea studyAreaExample = new StudyArea();
			studyAreaExample.setAuthorId(user.getId());
			studyAreaExample.setDisabled(0);
			studyAreaExample.setCreateDate(Utility.getYesterday());
			List<StudyArea> oldAreas = this.studyAreaDao.findListByExample(studyAreaExample);
			
			if(oldAreas==null||oldAreas.size()==0){
//				String areaHql = "update StudyArea sa set sa.disabled = 1 where sa.author=:author";
				Map<String,Object> areaValues = new HashMap<String,Object>();
				areaValues.put("author", user);
//				this.studyAreaDao.batchExecute(areaHql, areaValues);
				this.studyAreaDao.updateSetDisabledByAuthorId(1, user.getId());
				
				List<Map<String, Double>> geoRanges = GeoClustering.getRange(geoPoints);
				for(Map<String, Double> geoRange:geoRanges){
					if (geoRange != null && geoRange.get(Constants.MAX_LAT) != null
							&& geoRange.get(Constants.MAX_LNG) != null
							&& geoRange.get(Constants.MIN_LAT) != null
							&& geoRange.get(Constants.MIN_LNG) != null) {
						StudyArea studyarea = new StudyArea();
						studyarea.setAuthorId(user.getId());
						studyarea.setCreateDate(new Date());
						studyarea.setDisabled(0);
						if(geoRange.get(Constants.MAX_LAT).equals(geoRange.get(Constants.MIN_LAT))){
							studyarea.setMinlat(geoRange.get(Constants.MIN_LAT));
							studyarea.setMaxlat(geoRange.get(Constants.MAX_LAT)+0.0002);
						}else{
							studyarea.setMinlat(geoRange.get(Constants.MIN_LAT));
							studyarea.setMaxlat(geoRange.get(Constants.MAX_LAT));
						}
						if(geoRange.get(Constants.MAX_LNG).equals(geoRange.get(Constants.MIN_LNG))){
							studyarea.setMaxlng(geoRange.get(Constants.MAX_LNG)+0.0002);
							studyarea.setMinlng(geoRange.get(Constants.MIN_LNG));
						}else{
							studyarea.setMaxlng(geoRange.get(Constants.MAX_LNG));
							studyarea.setMinlng(geoRange.get(Constants.MIN_LNG));
						}
						studyarea.setId(KeyGenerateUtil.generateIdUUID());
						this.studyAreaDao.insert(studyarea);
					}
				}
			}
			
//			DetachedCriteria speedCriteria = DetachedCriteria.forClass(StudySpeed.class);
//			speedCriteria.add(Restrictions.eq("author", user));
//			speedCriteria.add(Restrictions.eq("disabled",0));
//			speedCriteria.add(Restrictions.gt("createDate", Utility.getYesterday()));
//			List<StudySpeed> oldSpeeds = this.studySpeedDao.find(speedCriteria);

			StudySpeed studySpeedExample = new StudySpeed();
			studySpeedExample.setAuthorId(user.getId());
			studySpeedExample.setDisabled(0);
			studySpeedExample.setCreateDate(Utility.getYesterday());
			List<StudySpeed> oldSpeeds = this.studySpeedDao.findListByExample(studySpeedExample);
			
			if(oldSpeeds==null||oldSpeeds.size()==0){
//				String speedHql = "update StudySpeed ss set ss.disabled = 1 where ss.author=:author";
//				Map<String,Object> speedValues = new HashMap<String,Object>();
//				speedValues.put("author", user);
//				this.studySpeedDao.batchExecute(speedHql, speedValues);
				this.studySpeedDao.updateSetDisabledByAuthorId(1, user.getId());
				
				List<Map<String, Float>> speedRanges = SpeedClustering
				.findSpeedPhase(speedPoints);
				if(speedRanges!=null){
					for(Map<String,Float>speedRange:speedRanges){
						if(speedRange.get(Constants.MAX_SPEED)!=null&&speedRange.get(Constants.MIN_SPEED)!=null){
							StudySpeed studyspeed = new StudySpeed();
							studyspeed.setAuthorId(user.getId());
							studyspeed.setCreateDate(new Date());
							studyspeed.setDisabled(0);
							studyspeed.setMaxspeed(speedRange.get(Constants.MAX_SPEED));
							studyspeed.setMixspeed(speedRange.get(Constants.MIN_SPEED));
							studyspeed.setId(KeyGenerateUtil.generateIdUUID());
							this.studySpeedDao.insert(studyspeed);
						}
					}
				}
			}
		}
		Date over = new Date();
		out.println("It costs "+(over.getTime()-start.getTime())/1000+" seconds");
	}

	@Transactional(readOnly = false)
	public void findLearningHabit(){
		PrintStream out = System.out;
		Date start = new Date();
		List<Users> users = this.userDao.findAll();
//		List<Users> users = this.userDao.findBy("id", "ff80818125899fb501258acaadc10001");
		for (Users user : users) {
			out.println("user analyzing: "+user.getNickname());
			
//			DetachedCriteria activityCriteria = DetachedCriteria.forClass(StudyActivity.class);
//			activityCriteria.add(Restrictions.eq("author", user));
//			activityCriteria.addOrder(Order.desc("createtime"));
			List<StudyActivity> activities = this.studyActivityDao.findListByAuthorId(user.getId());
			Date lastDate = null;
			if(activities!=null&&activities.size()>0)
				lastDate = activities.get(0).getCreatetime();
			
			
//			List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
//			List<TimeNode> timePoints = new ArrayList<TimeNode>();
//			List<Float> speedPoints = new ArrayList<Float>();

//			DetachedCriteria itemCrit = DetachedCriteria.forClass(Item.class);
//			itemCrit.add(Restrictions.eq("author", user));
//			itemCrit.add(Restrictions.isNull("relogItem"));
//			if(lastDate != null)
//				itemCrit.add(Restrictions.gt("createTime", lastDate));
			
			List<Item> items = this.itemDao.findListByAuthorAndCreatedFromNotRelog(user.getId(), lastDate);

			for (Item item : items) {
//				if (GeoUtils.isHighPrecision(item.getItemLat(), item
//						.getItemLng())) {
//					geoPoints.add(new GeoPoint(item.getItemLat(), item
//							.getItemLng()));
//				}
//				if (item.getSpeed() != null)
//					speedPoints.add(item.getSpeed());
//				if (item.getCreateTime() != null) {
//					timePoints.add(new TimeNode(Utility.getTime(item
//							.getCreateTime())));
//				}
				StudyActivity sa = new StudyActivity();
				sa.setAuthorId(user.getId());
				sa.setCreatetime(item.getCreateTime());
				if(GeoUtils.validateLatLng(item.getItemLat(), item.getItemLng())){
					sa.setLatitude(item.getItemLat());
					sa.setLongitude(item.getItemLng());
				}
				sa.setSpeed(item.getSpeed());
				sa.setActivity_type(Constants.StudyActivityAddItem);
				this.studyActivityDao.insert(sa);
			}

//			DetachedCriteria itemlogCrit = DetachedCriteria
//					.forClass(LogUserReadItem.class);
//			itemlogCrit.add(Restrictions.eq("user", user));
//			if(lastDate != null)
//				itemlogCrit.add(Restrictions.gt("createTime", lastDate));
			List<LogUserReadItem> itemlogs = this.itemlogDao.findListByUserAndCreatedAfter(user.getId(), lastDate);

			for (LogUserReadItem itemlog : itemlogs) {
//				if (GeoUtils.isHighPrecision(itemlog.getLatitude(), itemlog
//						.getLongitude())) {
//					geoPoints.add(new GeoPoint(itemlog.getLatitude(), itemlog
//							.getLongitude()));
//				}
//				if (itemlog.getSpeed() != null)
//					speedPoints.add(itemlog.getSpeed());
//				if (itemlog.getCreateTime() != null) {
//					timePoints.add(new TimeNode(Utility.getTime(itemlog
//							.getCreateTime())));
//				}
				
				StudyActivity sa = new StudyActivity();
				sa.setAuthorId(user.getId());
				sa.setCreatetime(itemlog.getCreateTime());
				
				if(GeoUtils.validateLatLng(itemlog.getLatitude(), itemlog.getLongitude())){
					sa.setLatitude(itemlog.getLatitude());
					sa.setLongitude(itemlog.getLongitude());
				}
				
				sa.setSpeed(itemlog.getSpeed());
				sa.setActivity_type(Constants.StudyActivityViewItem);
				this.studyActivityDao.insert(sa);
			}

//			DetachedCriteria myquizCrit = DetachedCriteria
//					.forClass(MyQuiz.class);
//			myquizCrit.add(Restrictions.eq("author", user));
//			myquizCrit.add(Restrictions.ne("answerstate",
//					Constants.NotAnsweredState));
//			if(lastDate != null)
//				myquizCrit.add(Restrictions.gt("updateDate", lastDate));
			List<MyQuizDTO> myquizzes = this.myquizDao.findListByAuthorAndAnsweredAfter(user.getId(), lastDate);
			for (MyQuiz myquiz : myquizzes) {
//				if (GeoUtils.isHighPrecision(myquiz.getLatitude(), myquiz
//						.getLongitude())) {
//					geoPoints.add(new GeoPoint(myquiz.getLatitude(), myquiz
//							.getLongitude()));
//				}
//				if (myquiz.getSpeed() != null)
//					speedPoints.add(myquiz.getSpeed());
//				if (myquiz.getUpdateDate() != null) {
//					timePoints.add(new TimeNode(Utility.getTime(myquiz
//							.getUpdateDate())));
//				}

				StudyActivity sa = new StudyActivity();
				sa.setAuthorId(user.getId());
				if(myquiz.getUpdateDate()!=null)
					sa.setCreatetime(myquiz.getUpdateDate());
				else
					sa.setCreatetime(myquiz.getCreateDate());
				if(GeoUtils.validateLatLng(myquiz.getLatitude(), myquiz.getLongitude())){
					sa.setLatitude(myquiz.getLatitude());
					sa.setLongitude(myquiz.getLongitude());
				}
				
				sa.setSpeed(myquiz.getSpeed());
				sa.setActivity_type(Constants.StudyActivityDoQuiz);
				this.studyActivityDao.insert(sa);
			}
			
//			DetachedCriteria phaseCriteria = DetachedCriteria.forClass(StudyPhase.class);
//			phaseCriteria.add(Restrictions.eq("user", user));
//			phaseCriteria.addOrder(Order.desc("endTime"));
//			
			List<StudyPhase>oldPhases =  this.studyPhaseDao.findByUserId(user.getId(), new Sort(Sort.Direction.DESC, "end_time"));
			Date lastPhaseDate = null;
			if(oldPhases!=null && oldPhases.size()>0){
				lastPhaseDate = oldPhases.get(0).getEndTime();
			}
			
//			activityCriteria = DetachedCriteria.forClass(StudyActivity.class);
//			activityCriteria.add(Restrictions.eq("author", user));
//			if(lastPhaseDate != null)
//				activityCriteria.add(Restrictions.gt("createtime", lastPhaseDate));
//			activityCriteria.addOrder(Order.asc("createtime"));
			List<StudyActivity> allactivities = this.studyActivityDao.findListByAuthorAndCreatedAfter(user.getId(), lastPhaseDate, new Sort(Sort.Direction.ASC, "createtime"));
			List<StudyPhase> phases = findSameActivity(allactivities);
			for(StudyPhase phase:phases){
				if(phase.getId()!=null){
					this.studyPhaseDao.update(phase);
				}else{
					phase.setId(KeyGenerateUtil.generateIdUUID());
					this.studyPhaseDao.insert(phase);
				}
			}
			
		}
		Date over = new Date();
		out.println("It costs "+(over.getTime()-start.getTime())/1000+" seconds");
	}
	
	public void findTimeHabits(){
		List<Users> users = this.userDao.findAll();
//		List<Users> users = this.userDao.findBy("id", userid);
		Date start = new Date();
		for (Users user : users) {
			findUserTimeHabit(user);
		}
		Date end = new Date();
		System.out.println((end.getTime()-start.getTime())/1000+" seconds");
	}
	
	@Transactional(readOnly = false)
	public void findUserTimeHabit(Users user){
//		DetachedCriteria phaseCriteria = DetachedCriteria.forClass(StudyPhase.class);
//		phaseCriteria.add(Restrictions.eq("user", user));
		List<StudyPhase> allPhases = this.studyPhaseDao.findByUserId(user.getId(), null);
		Map<Integer, Double> phaseValues = new HashMap<Integer, Double>();
		for(StudyPhase sp:allPhases){
			Time startTime = new Time(sp.getStartTime().getTime());
			Time endTime = new Time(sp.getEndTime().getTime());
			addPhaseValue(phaseValues, startTime, endTime, sp.getNum());
		}
		
		System.out.println("user phase weight "+user.getNickname());
		
		int maxkey = 0;
		double maxvalue = 0.0;
		
		for(Integer key:phaseValues.keySet()){
			if(phaseValues.get(key)>maxvalue){
				maxvalue = phaseValues.get(key);
				maxkey = key;
			}
		}
		
//		DetachedCriteria timeCriteria = DetachedCriteria.forClass(StudyTime.class);
//		timeCriteria.add(Restrictions.eq("author", user));
//		timeCriteria.add(Restrictions.eq("disabled",0));
//		timeCriteria.add(Restrictions.gt("createDate", Utility.getYesterday()));
		List<StudyTime> oldTimes = this.studyTimeDao.findListByAuthorAndCreatedAfter(user.getId(), Utility.getYesterday());
		if(oldTimes==null||oldTimes.size()==0){
//			String timeHql = "update StudyTime st set st.disabled = 1 where st.author=:author";
//			Map<String,Object> timeValues = new HashMap<String,Object>();
//			timeValues.put("author", user);
			this.studyTimeDao.updateDisabledByAuthor(1, user.getId());
//			this.studyTimeDao.batchExecute(timeHql, timeValues);
			
			
			StudyTime studytime = new StudyTime();
			studytime.setAuthorId(user.getId());
			studytime.setCreateDate(new Date());
			studytime.setDisabled(0);
			studytime.setStarttime(getStartTimeFromKey(maxkey));
			studytime.setEndtime(getEndTimeFromKey(maxkey));
			studytime.setId(KeyGenerateUtil.generateIdUUID());
			this.studyTimeDao.insert(studytime);
			
//			List<Map<String, Time>> timeRanges = TimeRankClustering.findRange(timePoints);
//			for(Map<String, Time> timeRange:timeRanges){
//				if(timeRange!=null&&timeRange.get(Constants.MAX_TIME)!=null&&timeRange.get(Constants.MIN_TIME)!=null){
//					StudyTime studytime = new StudyTime();
//					studytime.setAuthor(user);
//					studytime.setCreateDate(new Date());
//					studytime.setDisabled(0);
//					studytime.setStarttime(timeRange.get(Constants.MIN_TIME));
//					studytime.setEndtime(timeRange.get(Constants.MAX_TIME));
//					this.studyTimeDao.save(studytime);
//				}
//			}
		}
	}
	
	private void addPhaseValue(Map<Integer, Double>phaseValues, Time startTime, Time endTime, Integer number){
		double length = FormatUtil.getSeconds(endTime)
				- FormatUtil.getSeconds(startTime);
		int phaseLegth = 15*60;
		if(length < phaseLegth)
			length = phaseLegth;
//		double weight = number/length;
		double weight = 1;
		
		Integer startIndex = getKye(startTime);
		Integer endIndex = getKye(endTime);
		
		if(startIndex == endIndex){
			Double value = phaseValues.get(startIndex);
			if(value == null)
				phaseValues.put(startIndex, weight);
			else
				phaseValues.put(new Integer(startIndex), value+weight);
		}else{
			Time sTime = startTime;
			for(int i = startIndex;i<=endIndex;i++){
				int leg = 0;
				if(i == getKye(endTime))
					leg = getSeconds(endTime)- getSeconds(sTime);
				else
					 leg = i*30*60-getSeconds(sTime);
				Double value = phaseValues.get(new Integer(i));
				if(value == null)
					phaseValues.put(new Integer(i), leg*weight/length);
				else
					phaseValues.put(new Integer(i), value+leg*weight/length);
				sTime = getKyeTime(i);
			}
		}
	}
	
	public static Time getKyeTime(int key){
		Time t = Time.valueOf("00:00:00");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(t.getTime());
		cal.add(Calendar.SECOND, key*30*60);
		return new Time(cal.getTimeInMillis());
	}
	
	public static Integer getKye(Time t){
		int division = 30*60;
		Integer l = getSeconds(t);
		int key = l/division;
		if(l%division > 0)
			key++;
		return key;
	}
	
	public static Time getStartTimeFromKey(int key){
		int min = key*30;
		int h = min/60;
		int m = min%60;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		cal.set(Calendar.SECOND, 0);
		return new Time(cal.getTimeInMillis());
	}
	
	public static Time getEndTimeFromKey(int key){
		int min = (key+1)*30;
		int h = min/60;
		int m = min%60;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		cal.set(Calendar.SECOND, 0);
		return new Time(cal.getTimeInMillis());
	}
	
	public static Integer getSeconds(Time t1){
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(t1.getTime());
		int h1 = c1.get(Calendar.HOUR_OF_DAY);
		int m1 = c1.get(Calendar.MINUTE);
		int s1 = c1.get(Calendar.SECOND);
		return h1*60*60+m1*60+s1;
	}
	
	public Map<String,List<?>> syncPersonlize(PersonalSyncCondForm form, Users user){
		Map<String,List<?>> map = new HashMap<String,List<?>>();
		
//		DetachedCriteria area_dc = DetachedCriteria.forClass(StudyArea.class);
//		area_dc.add(Restrictions.eq("author", user));
//		area_dc.add(Restrictions.ne("disabled", new Integer(1)));
//		if(form.getAreaUpdateTime()!=null)
//			area_dc.add(Restrictions.gt("createDate", form.getAreaUpdateTime()));
		
		List<StudyArea> areas = this.studyAreaDao.findListByAuthorAndCreateAfter(user.getId(), form.getAreaUpdateTime());
		if(areas!=null&&areas.size()>0){
			List<StudyAreaForm> areaForms = new ArrayList<StudyAreaForm>(); 
			for(StudyArea area:areas){
				areaForms.add(new StudyAreaForm(area));
			}
			map.put("areas", areaForms);
		}
		
//		DetachedCriteria time_dc = DetachedCriteria.forClass(StudyTime.class);
//		time_dc.add(Restrictions.eq("author", user));
//		time_dc.add(Restrictions.ne("disabled", new Integer(1)));
//		if(form.getTimeUpdateTime()!=null)
//			time_dc.add(Restrictions.gt("createDate", form.getTimeUpdateTime()));
		List<StudyTime> times = this.studyTimeDao.findListByAuthorAndCreatedAfter(user.getId(), form.getTimeUpdateTime());
		if(times!=null&&times.size()>0){
			List<StudyTimeForm> timeForms = new ArrayList<StudyTimeForm>(); 
			for(StudyTime time:times){
				timeForms.add(new StudyTimeForm(time));
			}
			map.put("times", timeForms);
		}
		
//		DetachedCriteria send_dc = DetachedCriteria.forClass(SendTime.class);
//		send_dc.add(Restrictions.eq("author", user));
//		if(form.getSendUpdateTime()!=null)
//			send_dc.add(Restrictions.gt("createDate", form.getSendUpdateTime()));
		List<SendTime> sends = this.sendTimeDao.findListByAuthorAndCreatedAfter(user.getId(), form.getSendUpdateTime());
		if(sends!=null){
			List<SendTimeForm> sendForms = new ArrayList<SendTimeForm>();
			for(SendTime send:sends){
				sendForms.add(new SendTimeForm(send));
			}
			map.put("sends", sendForms);
		}
		
		return map;
	}
	
	
	public List<StudyPhase> findSameActivity(List<StudyActivity> allactivities ){
		List<StudyPhase> phases = new ArrayList<StudyPhase>();
		if(allactivities == null || allactivities.size()==0)
			return phases;
		
		StudyActivity fsa = allactivities.get(0);
		Date startTime = fsa.getCreatetime();
		Double minLat =  fsa.getLatitude();
		Double minLng =  fsa.getLongitude();
		
		Date endTime = startTime;
		Double maxLat =  fsa.getLatitude();
		Double maxLng =  fsa.getLongitude();
		
		int quiznumber = 0;
		int viewitemnumber = 0;
		int additemnumber = 0;
		
		if(Constants.StudyActivityAddItem.equals(fsa.getActivity_type()))
			additemnumber++;
		if(Constants.StudyActivityDoQuiz.equals(fsa.getActivity_type()))
			quiznumber++;
		if(Constants.StudyActivityViewItem.equals(fsa.getActivity_type()))
			viewitemnumber++;
		
		int phaseLength = 1000*60*15;
		
		
		for(int i=1; i<allactivities.size();i++){
			StudyActivity sa = allactivities.get(i);
			if(Math.abs((startTime.getTime()-sa.getCreatetime().getTime()))<phaseLength){
				endTime = sa.getCreatetime();
				if(minLat == null){
					minLat = sa.getLatitude();
				}else if(sa.getLatitude()!=null && minLat>sa.getLatitude())
					minLat = sa.getLatitude();
				if(maxLat == null){
					maxLat = sa.getLatitude();
				}else if(sa.getLatitude()!=null && maxLat<sa.getLatitude())
					maxLat = sa.getLatitude();
				
				if(minLng == null)
					minLng = sa.getLongitude();
				else if(sa.getLongitude()!=null && minLng>sa.getLongitude())
					minLng = sa.getLongitude();
				if(maxLng == null)
					maxLng = sa.getLongitude();
				else if(sa.getLongitude()!=null && maxLng<sa.getLongitude())
					maxLng = sa.getLongitude();
				
				if(Constants.StudyActivityAddItem.equals(sa.getActivity_type()))
					additemnumber++;
				if(Constants.StudyActivityDoQuiz.equals(sa.getActivity_type()))
					quiznumber++;
				if(Constants.StudyActivityViewItem.equals(sa.getActivity_type()))
					viewitemnumber++;
			}else{
				StudyPhase sp = new StudyPhase(startTime,endTime,minLat, minLng, maxLat, maxLng, quiznumber, additemnumber, viewitemnumber, sa.getAuthorId());
				phases.add(sp);
				
				startTime = sa.getCreatetime();
				minLat =  sa.getLatitude();
				minLng =  sa.getLongitude();
				
				endTime = startTime;
				maxLat =  sa.getLatitude();
				maxLng =  sa.getLongitude();
				
				quiznumber = 0;
				viewitemnumber = 0;
				additemnumber = 0;
				
				if(Constants.StudyActivityAddItem.equals(sa.getActivity_type()))
					additemnumber++;
				if(Constants.StudyActivityDoQuiz.equals(sa.getActivity_type()))
					quiznumber++;
				if(Constants.StudyActivityViewItem.equals(sa.getActivity_type()))
					viewitemnumber++;
			}
		}
		
		StudyPhase sp = new StudyPhase(startTime,endTime,minLat, minLng, maxLat, maxLng, quiznumber, additemnumber, viewitemnumber, fsa.getAuthorId());
		phases.add(sp);
		
		return phases;
	}
	
}
