package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQueueDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizChoiceDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemQuestionType;
import jp.ac.tokushima_u.is.ll.entity.ItemQueue;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.MyQuizChoice;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.QuizChoiceForm;
import jp.ac.tokushima_u.is.ll.form.QuizForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.helper.QuizCondition;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizGenerator;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizGeneratorFacotry;
import jp.ac.tokushima_u.is.ll.service.quiz.QuizWrapper;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.StaticImageUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author lemonrain
 */
@Service("llquizService")
@Transactional
public class LLQuizService {
	@Autowired
    private MyQuizDao myquizDao;
	@Autowired
	private MyQuizChoiceDao myQuizChoiceDao;
	@Autowired
    private ItemDao itemDao;
	@Autowired
	private ItemService itemService;
	@Autowired
    private ItemQueueDao itemQueueDao;
	@Autowired
    private QuestionTypeDao questionTypeDao;
	@Autowired
    private ItemTitleDao itemTitleDao;
	@Autowired
    private ItemQuestionTypeDao itemQuestionTypeDao;
	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private FileDataDao fileDataDao;

    @Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;
    
//    private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat(
//	"yyyy/MM/dd HH:mm:ss");
    
    @Autowired
    private QuizGeneratorFacotry quizGeneratorFactory; 
    @Autowired
    GoogleTranslateService translateService;
    
	@Autowired
	private ItemQueueService itemQueueService;

//	private static final String WithImageContent = "Please select a word to describe the photo";
//	private static final String WithoutImageContent = "";

    @Autowired
    private UserService userService;

    @Transactional(readOnly = false)
    public QuizWrapper findQuizWrapper(QuizCondition quizCon) {
    	MyQuizDTO quiz = this.findQuiz(quizCon);
    	if (quiz != null ) {
    		QuizGenerator quizGenerator= quizGeneratorFactory.getQuizGeneratorByQuestionTypId(quiz.getQuestiontypeid());
			if(quizGenerator!=null){
				return quizGenerator.convertQuiz(quiz);
			}
    	}
    	return null;
    }
    
    @Transactional(readOnly = false)
    public MyQuizDTO findQuiz(QuizCondition quizCon) {
    	String userId = quizCon.getUserId();
    	if(userId == null){
    		userId = SecurityUserHolder.getCurrentUser().getId();
    	}
		if(quizCon.getQuizid()!=null&&quizCon.getQuizid().length()>0){
			MyQuizDTO myquiz = this.myquizDao.findById(quizCon.getQuizid());
			if(myquiz!=null)
				return myquiz;
		}
//		DetachedCriteria dc = DetachedCriteria.forClass(ItemQueue.class);
//		dc.add(Restrictions.eq("author", user));
//		dc.createAlias("item", "i", JoinType.LEFT_OUTER_JOIN); 
//		dc.add(Restrictions.eq("disabled", 0));
//		dc.add(Restrictions.lt("startTime", new Date()));
//		dc.addOrder(Order.desc("i.rating"));
//		dc.addOrder(Order.asc("weight"));
//		dc.addOrder(Order.asc("startTime"));
//		List<ItemQueue> itemqueues = this.itemQueueDao.findListProperItemQueues(user.getId(), new Date());
		List<Item> items = this.itemDao.findListWaitItemsThroughItemQueue(userId, new Date());
		MyQuizDTO myquiz = null;
		for(Item waitItem:items){
			quizCon.setItemId(waitItem.getId());
			myquiz = this.findItemQuiz(quizCon);
			if(myquiz!=null)
				return myquiz;
		}
    	return null;
    }
    
 
    public List<QuizForm> findAllSyncQuizzes(QuizCondition quizCon) {
    	String userId = quizCon.getUserId();
//		String sql = "select iq.* from t_itemqueue iq, ( " +
//						"select  max(create_time) as create_time, item_id "+
//						"from t_itemqueue "+
//						"where start_time<='"+sTimeFormat.format(new Date())+
//						"' and author_id='"+user.getId()+"'";
//		if(quizCon.getCreateDateFrom()!=null)
//			sql = sql + " and update_time>'"+sTimeFormat.format(quizCon.getCreateDateFrom())+"' and disabled = 0 ";
//		else
//			sql = sql +" and disabled = 0 ";
//		
//		sql += " group by item_id order by create_time desc)x " +
//				"where iq.item_id = x.item_id and iq.create_time = x.create_time " +
//				"order by iq.weight asc, iq.start_time desc ";	
		List<ItemQueue> itemqueues = this.itemQueueDao.findListForSyncQuizzes(userId, new Date(), quizCon.getCreateDateFrom()); //this.sessionFactory.getCurrentSession().createSQLQuery(sql).addEntity(ItemQueue.class).list();
		
		List<QuizForm> quizzes = new ArrayList<QuizForm>();
    	if(itemqueues!=null){
    		for(ItemQueue waitItem:itemqueues){
    			if(waitItem.getDisabled()==1){
    				QuizForm q = new QuizForm();
    				q.setItemid(waitItem.getItemId());
    				q.setDisabled(1);
    				quizzes.add(q);
    			}else{
    				quizCon.setItemId(waitItem.getItemId());
    				MyQuiz myquiz = this.findItemQuiz(quizCon);
    				if(myquiz!=null){
    					myquiz.setCreateDate(waitItem.getUpdateTime());
    					quizzes.add(convertMyQuizToQuizForm(myquiz, waitItem.getWeight()));
    				}	
    			}
    		}
    	}
    	
    	if(quizzes.size()<15){
    		if(quizCon.getIsRecommended()!=null&&quizCon.getIsRecommended().booleanValue()){
    			this.itemQueueService.searchUserRecommendedItems(userService.findById(SecurityUserHolder.getCurrentUser()
    					.getId()));
    		}
    	}
    	
    	return quizzes;
    }
    
//    public List<QuizForm> findAllSyncQuizzes(QuizCondition quizCon) {
//    	Users user = quizCon.getUser();
//		DetachedCriteria dc = DetachedCriteria.forClass(ItemQueue.class);
//		dc.add(Restrictions.eq("author", user));
//		if(quizCon.getDisabled()!=null)
//			dc.add(Restrictions.eq("disabled", quizCon.getDisabled()));
//		dc.add(Restrictions.lt("startTime", new Date()));
//		if(quizCon.getCreateDateFrom()!=null)
//			dc.add(Restrictions.gt("createTime", quizCon.getCreateDateFrom()));
//		dc.addOrder(Order.asc("weight"));
//		dc.addOrder(Order.asc("startTime"));
//		List<ItemQueue> itemqueues = this.itemQueueDao.find(dc);
//		List<QuizForm> quizzes = new ArrayList<QuizForm>();
//    	if(itemqueues!=null){
//    		for(ItemQueue waitItem:itemqueues){
//    			quizCon.setItem(waitItem.getItem());
//    			MyQuiz myquiz = this.findItemQuiz(quizCon);
//    			if(myquiz!=null){
//    				myquiz.setCreateDate(waitItem.getCreateTime());
//    				quizzes.add(new QuizForm(myquiz));
//    			}	
//    		}
//    	}
//    	return quizzes;
//    }
    
    @Transactional(readOnly = false)
	public MyQuizDTO findItemQuiz(QuizCondition quizCon) {
		String userId = quizCon.getUserId();
		if(userId == null){
			userId = SecurityUserHolder.getCurrentUser().getId();
		}
		String itemId = quizCon.getItemId();

//		Set<QuestionType> questionTypes = item.getQuestionTypes();
//		DetachedCriteria dc_qt = DetachedCriteria.forClass(ItemQuestionType.class);
//		dc_qt.add(Restrictions.eq("item", item));
//		dc_qt.add(Restrictions.or(
//				Restrictions.in("language", user.getStudyLangs()), 
//				Restrictions.isNull("language")));
//		dc_qt.addOrder(Order.asc("questionType"));
		List<Language> studyLangs = languageDao.findListUsersStudyLangs(userId);
		List<ItemQuestionType> questionTypes = this.itemQuestionTypeDao.findListByItemAndLangs(itemId, studyLangs);
		MyQuizDTO myquiz = null;
		
		if(questionTypes.isEmpty())
			return myquiz;
		
//		DetachedCriteria myq_dc = DetachedCriteria.forClass(MyQuiz.class);
//		myq_dc.add(Restrictions.eq("item", item));
//		myq_dc.add(Restrictions.eq("author", user));
////		myq_dc.add(Restrictions.in("questionType", questionTypes));
////		myq_dc.add(Restrictions.isNotNull("questionType"));
//		myq_dc.add(Restrictions.ne("questionType", this.questionTypeDao.findUniqueBy("id", Constants.QuizTypeYesNoQuestion)));
//		myq_dc.add(Restrictions.eq("answerstate", Constants.NotAnsweredState));
//		myq_dc.addOrder(Order.desc("createDate"));
		
		//Delete 6/6
		MyQuiz example = new MyQuiz();
		example.setItemId(itemId);
		example.setAuthorId(userId);
		example.setQuestiontypeid(Constants.QuizTypeYesNoQuestion);
		example.setAnswerstate(Constants.NotAnsweredState);
		List<MyQuizDTO> myquizs = this.myquizDao.findListByExample(example, new Sort(Sort.Direction.DESC, "create_date"));
		
		if(myquizs!=null&&myquizs.size()>0){
			MyQuizDTO t_mq = myquizs.get(0);
			if(checkQuizQuality(t_mq))
			return t_mq;
		}
		
		for(ItemQuestionType qt:questionTypes){
			QuizGenerator quizGenerator= quizGeneratorFactory.getQuizGeneratorByQuestionTypId(qt.getQuestiontypeId());
			if(quizGenerator==null)
				continue;
			
			myquiz = quizGenerator.generate(itemId, qt.getId(), userId, quizCon);
			if(myquiz!=null)
				return myquiz;
		}
		return null;
	}
	
	private boolean checkQuizQuality(MyQuiz quiz){
		if(quiz.getQuestiontypeid().equals(Constants.QuizTypeImageMutiChoice)){
			List<MyQuizChoice>choices = myQuizChoiceDao.findListByMyQuizId(quiz.getId());
			for(MyQuizChoice choice:choices){
				if(!StaticImageUtil.isFileExisting(staticserverImageUrl+choice.getContent(), Constants.SmartPhoneLevel))
					return false;
			}
		}
		return true;
	}
	
	
	public MyQuizDTO checkQuiz(QuizCondition quizCon){
		MyQuizDTO myquiz = this.myquizDao.findById(quizCon.getQuizid());
		if(myquiz==null)
			return null;
		if(Constants.QuizPass.equals(quizCon.getPass()))
			myquiz.setAnswerstate(Constants.PassAnsweredState);
		else if(Constants.QuizEasy.equals(quizCon.getPass()))
			myquiz.setAnswerstate(Constants.EasyAnsweredState);
		else if(Constants.QuizDifficult.equals(quizCon.getPass()))
			myquiz.setAnswerstate(Constants.DifficultAnsweredState);
		
		myquiz.setMyanswer(quizCon.getAnswer());
		
       if(quizCon.getLng()!=null&&quizCon.getLat()!=null){
        	myquiz.setLongitude(quizCon.getLng());
        	myquiz.setLatitude(quizCon.getLat());
        }
        
        if(quizCon.getSpeed()!=null)
        	myquiz.setSpeed(quizCon.getSpeed());
        
        if((quizCon.getAlarmtype()!=null))
        	myquiz.setAlarmtype(quizCon.getAlarmtype());
		
		QuizGenerator quizGenerator= quizGeneratorFactory.getQuizGeneratorByQuestionTypId(myquiz.getQuestiontypeid());
		if(quizGenerator==null)
			return null;
		
		myquiz = quizGenerator.checkAnswer(myquiz);
		
//		if(Constants.PassAnsweredState.equals(myquiz.getAnswerstate())||StringUtils.isBlank(myquiz.getAnswer())){
		if(Constants.QuizPass.equals(quizCon.getPass())||StringUtils.isBlank(myquiz.getAnswer())){	
			quizCon.setUserId(myquiz.getAuthorId());
			quizCon.setItemId(null);
			quizCon.setQuizid(null);
			return this.findQuiz(quizCon);
		}else
		return myquiz;
	}
	
	
	public QuizWrapper checkQuizWrapper(QuizCondition quizCon){
		MyQuizDTO quiz = this.checkQuiz(quizCon);
    	if (quiz != null ) {
    		QuizGenerator quizGenerator= quizGeneratorFactory.getQuizGeneratorByQuestionTypId(quiz.getQuestiontypeid());
			if(quizGenerator!=null){
				return quizGenerator.convertQuiz(quiz);
			}
    	}
    	return null;
	}
	
	public List<QuizForm> findNearestQuiz(double lat, double lng,
			double distance, int itemSize, Users user) {

//		String sql = "select t.* "
//				+ "from "
//				+ "("
//				+ "select i.*, get_distance("+lat+","+lng+", i.item_lat, i.item_lng) as distance from t_item i,"
//				+ "( select itq.item_id, max(itq.author_id) from t_itemqueue itq "
//				+ "where itq.author_id = '"+user.getId()+"' group by itq.item_id "
//				+ ") as iq "
//				+ "where i.disabled!=1 and i.item_lat is not null and i.item_lng is not null and i.relog_item is null and (i.locationbased is null or i.locationbased =1) and i.id = iq.item_id "
//				+ " 	and i.id not in ( " +
//						"	select distinct ia.item  from t_itemalarm ia " +
//						"where ia.author_id = '"+user.getId()+"'  and i.id = ia.item and ia.alarm_type = 1)"
//				+ "order by distance asc "
//				+ ") as t where t.distance<="+distance
//				+" limit 0, "+itemSize;
//		Session session = this.sessionFactory.getCurrentSession();
		
		List<QuizForm> quizs = new ArrayList<QuizForm>();
		List<Item> items = itemDao.findListNearestItems(user.getId(), lng, lat, distance, new PageRequest(0, itemSize)); //session.createSQLQuery(sql).addEntity(Item.class).list();
		QuizCondition quizCon = new QuizCondition();
		quizCon.setUserId(user.getId());
		for(Item item:items){
			quizCon.setItemId(item.getId());
			MyQuiz myquiz = this.findItemQuiz(quizCon);
			if(myquiz!=null){
				Integer weight = Constants.PriorityReferItemWeight;
				Item i = itemDao.findById(myquiz.getItemId());
 				if(i.getAuthorId().equals(user.getId()))
 					weight = Constants.PriorityMyItemWeight;
				quizs.add(convertMyQuizToQuizForm(myquiz,weight));
			}	
		}
		return quizs;
	}

//	public LLQuizDTO findNewQuiz(QuizCondition quizCon) {


//	    	Users user = quizCon.getUser();
//	        if (user == null) {
//	           user = userService.getById(SecurityUserHolder.getCurrentUser().getId());
//	        }
//	        Set<Integer>courseSet = new HashSet<Integer>();
//	        String lanCode = quizCon.getLanCode();
//	        if(lanCode!=null&&lanCode.length()>0)
//	        	courseSet.add(CourseUtil.converCourseIdFromString(lanCode));
//	        else
//	        {        	
//	        	List<Language> studylanguages = user.getStudyLangs();
//	        	for(Language lan:studylanguages){
//	        		courseSet.add(CourseUtil.converCourseIdFromString(lan.getCode()));
//	        	}
//	        }
//	        
//	        List<Category> myCategory = user.getMyCategoryList();
//	      
//	
//	        List<Language> mylans = user.getMyLangs();
//	        Language motherlan = null;
//	        if (mylans != null && mylans.size() > 0) {
//	            motherlan = mylans.get(0);
//	        }
//	
//	        String hql = "from LLQuiz llquiz where llquiz.courseid in (:courseSet ) and not exists (select myquiz from MyQuiz myquiz where myquiz.author =:author and myquiz.llquiz = llquiz)";
//	        Map<String, Object> params = new HashMap<String, Object>();
//	        params.put("courseSet", courseSet);
//	        params.put("author", user);
//	        
//	        if(quizCon.getVersioncode()==null||quizCon.getVersioncode()<Constants.AndroidNewVersion)
//	        	hql = hql +" and llquiz.quiztypeid=1 ";
//	        
//			if(Boolean.TRUE.equals(quizCon.getIsMyLog())){
//				hql = hql + " and llquiz.item.author=:user ";
//				params.put("user", user);
//			}
//		
////			if(myCategory!=null&&myCategory.size()>0){
//			if(Constants.usingCategory){
//				if(myCategory!=null&&myCategory.size()>0){
//					hql = hql + " and llquiz.item.category in (:categorySet) ";
//					params.put("categorySet", myCategory);
//				}
//			}
//	        
//			hql = hql + " order by llquiz.pass asc ";
//	      
//	        List<LLQuiz> results = this.llquizDao.find(hql, params);
//	        if(results!=null&&results.size()>0){
//	        	int count = (int) (Math.random() * results.size());
//	        	LLQuiz llquiz = results.get(count);
//	        	
//	        	this.setLLQuizContent(llquiz, motherlan, quizCon.getImageLevel());
//	        	
//	        	if(llquiz!=null){
//	        		MyQuiz myquiz = new MyQuiz();
//					myquiz.setAuthor(user);
//					myquiz.setAnswerstate(Constants.NotAnsweredState);
//					myquiz.setCreateDate(new Date());
//					myquiz.setUpdateDate(new Date());
//					myquiz.setLlquiz(llquiz);
//					myquiz.setAlarmtype(quizCon.getAlarmtype());
//					myquiz.setLatitude(quizCon.getLat());
//					myquiz.setLongitude(quizCon.getLng());
//					myquiz.setSpeed(quizCon.getSpeed());
//					this.myquizDao.save(myquiz);
//	
//	        		LLQuizDTO llquizDto = new LLQuizDTO();
//	        		llquizDto.setLlquiz(llquiz);
//	        		llquizDto.setMyquiz(myquiz);
//	        		
//	        		return llquizDto;
//	        	}else
//	        		return null;
//	        	
//	        }
//	        else
//	        	return null;
//	    }
	
	
//    public MyQuiz checkAnswer(QuizCondition quizCon){
//    	MyQuiz myquiz = this.myquizDao.findUniqueBy("id", quizCon.getQuizid());
//        Assert.notNull(myquiz);
//        LLQuiz llquiz = myquiz.getLlquiz();
//    	Integer rightanswer = Integer.valueOf(llquiz.getAnswer());
//    	Integer wrongtimes = llquiz.getWrongtimes();
//    	Integer righttimes = llquiz.getRighttimes();
//    	Integer passtimes = llquiz.getPass();
//    	
//    	Integer answerstate = Constants.NotAnsweredState;
//    	if(Constants.QuizPass.equals(quizCon.getPass())){
//    		if(passtimes==null)
//    			passtimes = new Integer(0);
//    		passtimes = passtimes.intValue()+1;
//    		llquiz.setPass(passtimes);
//    		answerstate = Constants.PassAnsweredState;
//    	}else if(rightanswer.equals(quizCon.getAnswer())){
//    		answerstate = Constants.CorrectAnsweredState;
//    		if(righttimes==null)
//    			righttimes = new Integer(1);
//    		else
//    			righttimes = righttimes.intValue()+1;
//    		llquiz.setRighttimes(righttimes);
//    	}else{
//    		answerstate = Constants.WrongAnsweredState;
//    		if(wrongtimes==null)
//    			wrongtimes = new Integer(1);
//    		else
//    			wrongtimes = wrongtimes.intValue()+1;
//    		llquiz.setWrongtimes(wrongtimes);
//    	}
//    	
//    	
//
//    	myquiz.setAnswerstate(answerstate);
//        myquiz.setUpdateDate(new Date());
//        
//        if(quizCon.getLng()!=null&&quizCon.getLat()!=null){
//        	myquiz.setLongitude(quizCon.getLng());
//        	myquiz.setLatitude(quizCon.getLat());
//        }
//        
//        if(quizCon.getSpeed()!=null)
//        	myquiz.setSpeed(quizCon.getSpeed());
//        
//        myquiz.setAnswer(quizCon.getAnswer()+"");
//        myquiz.setAlarmtype(quizCon.getAlarmtype());
//        
//        this.myquizDao.save(myquiz);
//        this.llquizDao.save(llquiz);
//
//        
//        DetachedCriteria dc = DetachedCriteria.forClass(ItemQueue.class); 
//        dc.add(Restrictions.eq("item", myquiz.getLlquiz().getItem()));
//        dc.add(Restrictions.eq("author", myquiz.getAuthor()));
//        dc.add(Restrictions.eq("disabled", 0));
//        List<ItemQueue>itemqueuelist = this.itemQueueDao.find(dc);
//        for(ItemQueue iq:itemqueuelist){
//        	if(iq.getCourseid()==null||myquiz.getLlquiz().getCourseid().equals(iq.getCourseid())){
//        		iq.setDisabled(1);
//        		this.itemQueueDao.save(iq);
//        	}
//        }
//        
//        return myquiz;
//    }
    
//    private void setLLQuizContent(LLQuiz llquiz, Language lan, Integer imagelevel){
//    	if(Constants.QuizTypeImageMutiChoice.equals(llquiz.getQuiztypeid())){
//    		llquiz.setQuizcontent("Which image can be linked to this word: "+llquiz.getContent()+"");
//    	}else{
//    		String url = null;
//        	boolean flg = false;
//        	try{
//        		if(llquiz.getItem()!=null&&llquiz.getItem().getImage()!=null){
//        			url = this.staticServerService.getImageFileURL(llquiz.getItem().getImage().getId(), imagelevel);
//        			flg = this.staticServerService.isImageFileExist(llquiz.getItem().getImage().getId(), imagelevel);
//        		}
//        		if(url!=null&&url.length()>0)
//        			llquiz.setPhotoUrl(url);
//        	}catch(Exception e){
//        	}
//        	
//        	int v = (int)(Math.random()*2);
//        	if(v==1&&flg){
//        		llquiz.setQuizcontent(WithImageContent);
//        	}else{
//        		llquiz.setQuizcontent(this.getWithoutImageContent(llquiz, lan.getCode()));
//        	}
//    	}
//    }
    
//    private String getWithoutImageContent(LLQuiz llquiz, String motherlan){
//    	Item item = llquiz.getItem();
//    	String motTarget = item.getTitleByCode(motherlan);
//    	if(motTarget==null||motTarget.length()<=0){
//    		String from = CourseUtil.convertCodeFromId(llquiz.getCourseid());
//    		String text = item.getTitleByCode(from);
//    		motTarget = this.translateService.translateByCode(text, from, motherlan);
//    	}
//    	return "How is  "+motTarget+" translated in "+CourseUtil.convertLanguageFromId(llquiz.getCourseid())+" ?";
//    }

	@Transactional(readOnly = false)
    public void deleteQuizzes(Item it){
//    	Item item = itemDao.get(it.getId());
//    	String hql = "from LLQuiz quiz where quiz.item =:item ";
//    	Map<String, Object> params = new HashMap<String, Object>();
//    	params.put("item", item);
//    	List<LLQuiz> quizzes = this.llquizDao.find(hql, params);
//    	for(LLQuiz quiz:quizzes){
//    		this.llquizDao.delete(quiz);
//    	}
    }
    
	@Transactional(readOnly = false)
    public void updateQuizzes(Item item){
//    	String hql = "from LLQuiz quiz where quiz.item =:item and quiz.courseid =:courseid ";
//
//    	Map<String, Object> params = new HashMap<String, Object>();
//    	params.put("item", item);
//    	params.put("courseid", Constants.EnglishCourse);	
//    	List<LLQuiz> quizzes = this.llquizDao.find(hql, params);
//    	if(item.getEnTitle()!=null&&item.getEnTitle().length()>0&&quizzes.size()==0){
//    		this.composeTextMutiChoiceQuizByCode("en", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("en", item);
//    	}else if((item.getEnTitle() ==null||item.getEnTitle().length()==0)&&quizzes.size()>0){
//    		for(LLQuiz q:quizzes)
//    			this.llquizDao.delete(q);
//    	}
//    
//    	params.put("courseid", Constants.JapaneseCourse);	
//    	quizzes = this.llquizDao.find(hql, params);
//    	if(item.getJpTitle()!=null&&item.getJpTitle().length()>0&&quizzes.size()==0){
//    		this.composeTextMutiChoiceQuizByCode("ja", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("ja", item);
//    	}else if((item.getJpTitle() ==null||item.getJpTitle().length()==0)&&quizzes.size()>0){
//    		this.llquizDao.delete(quizzes.get(0));
//    		for(LLQuiz q:quizzes)
//    			this.llquizDao.delete(q);
//    	}
//    	
//    	params.put("courseid", Constants.ChineseCourse);	
//    	quizzes = this.llquizDao.find(hql, params);
//    	if(item.getZhTitle()!=null&&item.getZhTitle().length()>0&&quizzes.size()==0){
//    		this.composeTextMutiChoiceQuizByCode("zh", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("zh", item);
//    	}else if((item.getZhTitle() ==null||item.getZhTitle().length()==0)&&quizzes.size()>0){
//    		this.llquizDao.delete(quizzes.get(0));
//    		for(LLQuiz q:quizzes)
//    			this.llquizDao.delete(q);
//    	}
    }
    
	@Transactional(readOnly = false)
    public void insertQuizzes(Item item) {
//    	Set<QuestionType> questionTypes = item.getQuestionTypes();
//    	item.getTitles();
    	
//    	if(item.getEnTitle()!=null&&item.getEnTitle().length()>0){
//    		this.composeTextMutiChoiceQuizByCode("en", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("en", item);
//    	}
//    	if (item.getJpTitle()!=null&&item.getJpTitle().length()>0){
//    		this.composeTextMutiChoiceQuizByCode("ja", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("ja", item);
//    	}
//    	if(item.getZhTitle()!=null&&item.getZhTitle().length()>0){
//    		this.composeTextMutiChoiceQuizByCode("zh", item);
//    		if(item.getImage()!=null)
//    			this.composeImageMutiChoiceQuizByCode("zh", item);
//    	}
  	}
	
	@Transactional(readOnly = false)
	private void composeImageMutiChoiceQuizByCode(String lancode, Item item) {
//		String hql = "from Item item where item.image is not null";
//   		Map<String, Object> params = new HashMap<String, Object>();
//   		List<Item> results = this.itemDao.find(hql, params);
//		
//		LLQuiz quiz = new LLQuiz();
//		quiz.setQuiztypeid(Constants.QuizTypeImageMutiChoice);
//
//		if ("en".equals(lancode)) {
//			quiz.setCourseid(Constants.EnglishCourse);
//			quiz.setContent(item.getEnTitle());
//		} else if ("ja".equals(lancode)) {
//			quiz.setCourseid(Constants.JapaneseCourse);
//			quiz.setContent(item.getJpTitle());
//		} else if ("zh".equals(lancode)) {
//			quiz.setCourseid(Constants.ChineseCourse);
//			quiz.setContent(item.getZhTitle());
//		}
//
//		int rightanswernum = (int) (Math.random() * 4) + 1;
//		quiz.setAnswer(rightanswernum + "");
//		quiz.setCreateDate(new Date());
//		quiz.setItem(item);
//		quiz.setLatitude(item.getItemLat());
//		quiz.setLongitude(item.getItemLng());
//		quiz.setRighttimes(0);
//		quiz.setWrongtimes(0);
//		quiz.setLevel(10);
//
//		Set<Item> choices = new HashSet<Item>();
//		int c = 0;
//		while (choices.size() < 3) {
//			int ran = (int) (Math.random() * results.size());
//			Item i = results.get(ran);
//			if (!item.equals(i) || !item.getEnTitle().equals(i.getEnTitle()))
//				choices.add(i);
//
//			c++;
//			if (c > 30)
//				return;
//		}
//
//		Iterator<Item> itemIterator = choices.iterator();
//		int count = 1;
//		boolean flg = true;
//		while (itemIterator.hasNext()) {
//			if (count == rightanswernum) {
//				flg = false;
//				MyQuizChoice ac = new MyQuizChoice();
//				ac.setContent(item.getImage().getId());
//				ac.setLlquiz(quiz);
//				ac.setNumber(rightanswernum);
//				ac.setNote(this.getNoteFromItem(item));
//				ac.setItem_id(item.getId());
//				quiz.addLLQuizChoice(ac);
//				count++;
//			}
//			Item tempItem = itemIterator.next();
//			MyQuizChoice ac = new MyQuizChoice();
//			ac.setContent(tempItem.getImage().getId());
//			ac.setLlquiz(quiz);
//			ac.setItem_id(tempItem.getId());
//			ac.setNote(this.getNoteFromItem(tempItem));
//			ac.setNumber(count);
//			quiz.addLLQuizChoice(ac);
//			count++;
//		}
//
//		if (flg) {
//			MyQuizChoice ac = new MyQuizChoice();
//			ac.setContent(item.getImage().getId());
//			ac.setLlquiz(quiz);
//			ac.setNumber(rightanswernum);
//			ac.setNote(this.getNoteFromItem(item));
//			ac.setItem_id(item.getId());
//			quiz.addLLQuizChoice(ac);
//		}
//
//		this.llquizDao.save(quiz);

	}
    
    
	@Transactional(readOnly = false)
    private void composeTextMutiChoiceQuizByCode(String lancode, Item item){
//		LLQuiz quiz = new LLQuiz();
//		int rightanswernum = (int) (Math.random() * 4) + 1;
//		quiz.setAnswer(rightanswernum + "");
//		quiz.setCreateDate(new Date());
//		quiz.setItem(item);
//		quiz.setLatitude(item.getItemLat());
//		quiz.setLongitude(item.getItemLng());
//		quiz.setRighttimes(0);
//		quiz.setWrongtimes(0);
//		quiz.setLevel(10);
//		quiz.setQuiztypeid(Constants.QuizTypeTextMutiChoice);
//
//    	String hql = "";
//    	if("en".equals(lancode)){
//        	quiz.setCourseid(Constants.EnglishCourse);
//    		hql = "from Item item where item.enTitle is not null and item.enTitle !='' and item.relogItem is null and  item !=:item";
//    	}else if("ja".equals(lancode)){
//        	quiz.setCourseid(Constants.JapaneseCourse);
//    		hql = "from Item item where item.jpTitle is not null and item.jpTitle !='' and item.relogItem is null and  item !=:item";
//    	}else if("zh".equals(lancode)){
//        	quiz.setCourseid(Constants.ChineseCourse);
//    		hql = "from Item item where item.zhTitle is not null and item.zhTitle !=''  and item.relogItem is null and  item !=:item";
//    	}
//    	
//    	Map<String, Object> params = new HashMap<String, Object>();
//    	params.put("item", item);
//  		List<Item> results = this.itemDao.find(hql, params);
//  		
//  		if(results==null||results.size()<3)
//  			return ;
//    	
//  		
//  		Set<Item>choices= new HashSet<Item>();
//		while(choices.size()<3){
//			int ran = (int)(Math.random()*results.size());
//			Item i = results.get(ran);
//			if(item.equals(i))
//				continue;
//			
//			if(this.contains(i, choices)){
//				results.remove(i);
//				
//				if(results.size()<(3-choices.size()))
//					return;
//				
//				continue;
//			}
//			
//			if(!item.equals(i)){
//				choices.add(i);
//			}	
//		}
//		
//		Iterator<Item> itemIterator = choices.iterator();
//		int count = 1;
//		boolean flg = true;
//		while (itemIterator.hasNext()) {
//			if (count == rightanswernum) {
//				flg = false;
//				MyQuizChoice ac = new MyQuizChoice();
//				ac.setContent(item.getTitleByCode(lancode));
//				ac.setLlquiz(quiz);
//				ac.setNumber(rightanswernum);
//				ac.setNote(this.getNoteFromItem(item));
//				ac.setItem_id(item.getId());
//				quiz.addLLQuizChoice(ac);
//				count++;
//			}
//			Item tempItem = itemIterator.next();
//			MyQuizChoice ac = new MyQuizChoice();
//			ac.setContent(tempItem.getTitleByCode(lancode));
//			ac.setLlquiz(quiz);
//			ac.setItem_id(tempItem.getId());
//			ac.setNote(this.getNoteFromItem(tempItem));
//			ac.setNumber(count);
//			quiz.addLLQuizChoice(ac);
//			count++;
//		}
//
//		if (flg) {
//			MyQuizChoice ac = new MyQuizChoice();
//			ac.setContent(item.getTitleByCode(lancode));
//			ac.setLlquiz(quiz);
//			ac.setNumber(rightanswernum);
//			ac.setNote(this.getNoteFromItem(item));
//			ac.setItem_id(item.getId());
//			quiz.addLLQuizChoice(ac);
//		}
//
//		this.llquizDao.save(quiz);
    }
//
//    private boolean contains(Item item, Set<Item> items){
//    	for(Item i:items){
//    		if(this.sameItem(item, i))
//    			return true;
//    	}
//    	return false;
//    }
//    
//    private boolean sameItem(Item item1, Item item2){
//    	if(item1.equals(item2))
//    		return true;
//    	if(item1.getEnTitle()!=null&&item1.getEnTitle().length()>0&&item1.getEnTitle().equals(item2.getEnTitle()))
//			return true;
//		if(item1.getJpTitle()!=null&&item1.getJpTitle().length()>0&&item1.getJpTitle().equals(item2.getJpTitle()))
//			return true;
//		if(item1.getZhTitle()!=null&&item1.getZhTitle().length()>0&&item1.getZhTitle().equals(item2.getZhTitle()))
//			return true;
//		return false;
//    }
//    
//    
//    private String getNoteFromItem(Item item){
//        if(item == null)
//            return "";
//        String note = "";
//
//        String t = item.getJpTitle();
//        if(t!=null&&t.length()>0)
//            note = note+t+"(jp) ";
//
//         t = item.getEnTitle();
//        if(t!=null&&t.length()>0)
//            note = note+t+"(en) ";
//
//          t = item.getZhTitle();
//        if(t!=null&&t.length()>0)
//            note = note+t+"(zh) ";
//          return note;
//    }
	
	public QuizForm convertMyQuizToQuizForm(MyQuiz quiz, Integer weight) {
		QuizForm form = new QuizForm();
		
		form.setQuizid(quiz.getId());
		form.setAuthorid(quiz.getAuthorId());
		form.setQuiztype(quiz.getQuestiontypeid());
		form.setContent(quiz.getContent());
		form.setAnswer(quiz.getAnswer());
		form.setItemid(quiz.getItemId());
		form.setCreateDate(quiz.getUpdateDate().getTime());
		Item item = itemDao.findById(quiz.getItemId());
		if(item!=null&&item.getImage()!=null){
			FileData fileData = fileDataDao.findById(item.getImage());
			form.setFiletype(fileData.getFileType());
			form.setPhotourl(fileData.getId());
		}
		
		form.setQuizLat(item.getItemLat());
		form.setQuizLng(item.getItemLng());
		form.setWeight(weight);
		
		List<MyQuizChoice> quizChoices = myQuizChoiceDao.findListByMyQuizId(quiz.getId());
		if(!quizChoices.isEmpty()){
			List<QuizChoiceForm> choices = new ArrayList<QuizChoiceForm>();
			for(MyQuizChoice quizchoice:quizChoices){
				QuizChoiceForm choice = new QuizChoiceForm();
				choice.setChoiceid(quizchoice.getId());
				choice.setContent(quizchoice.getContent());
				Item choiceItem = itemDao.findById(quizchoice.getItemId());
				if(choiceItem!=null&&choiceItem.getImage()!=null)
					choice.setFiletype(fileDataDao.findById(choiceItem.getImage()).getFileType());
				choice.setNote(quizchoice.getNote());
				choice.setNumber(quizchoice.getNumber());
				choices.add(choice);
			}
			form.setChoices(choices);
		}
		
		if(quiz.getQuestiontypeid().equals(Constants.QuizTypeYesNoQuestion)){
			form.setItemform(itemService.convertItemToItemForm(item));
		}
		return null;
	}
}
