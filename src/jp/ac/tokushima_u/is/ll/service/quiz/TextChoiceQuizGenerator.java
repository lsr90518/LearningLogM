package jp.ac.tokushima_u.is.ll.service.quiz;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizChoiceDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemQuestionType;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.MyQuizChoice;
import jp.ac.tokushima_u.is.ll.service.ItemQueueService;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.helper.QuizCondition;
import jp.ac.tokushima_u.is.ll.service.visualization.ReviewHistoryService;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("textQuizGenerator")
@Transactional(readOnly = true)
public class TextChoiceQuizGenerator extends MutipleChoiceQuiz{
	
	@Autowired
    private ItemTitleDao itemTitleDao;
	@Autowired
    protected MyQuizDao myquizDao;
	@Autowired
    private ItemDao itemDao;
	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private MyQuizChoiceDao myQuizChoiceDao;
	@Autowired
	private ItemQuestionTypeDao itemQuestionTypeDao;
	@Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;
    @Autowired
    private ItemQueueService itemQueueService;
	@Autowired
	private ReviewHistoryService reviewHistoryService;
	@Autowired
	private ItemService itemService;
    
	@Override
	@Transactional(readOnly = false)
	public MyQuizDTO generate(String itemId, String questionTypeId, String userId, QuizCondition qc) {
		ItemQuestionType questionType = itemQuestionTypeDao.findById(questionTypeId);
		ItemTitle title = this.getItemTitle(itemId, questionType.getLanguageId());
		ItemTitle mtitle = this.getMotherLanTitle(itemId, userId);
		if(title==null||mtitle==null)
			return null;
		MyQuizDTO myquiz = new MyQuizDTO();
		myquiz.setAuthorId(userId);
		
		myquiz.setAlarmtype(qc.getAlarmtype());

	    if(qc.getLng()!=null&&qc.getLat()!=null){
        	myquiz.setLongitude(qc.getLng());
        	myquiz.setLatitude(qc.getLat());
        }
        if(qc.getSpeed()!=null)
        	myquiz.setSpeed(qc.getSpeed());
		
		myquiz.setCreateDate(new Date());
		myquiz.setItemId(itemId);
		myquiz.setAnswerstate(Constants.NotAnsweredState);
		myquiz.setQuestiontypeid(questionType.getQuestiontypeId());
		myquiz.setUpdateDate(new Date());
		myquiz.setLanguageId(title.getLanguage());
		myquiz.setItem(itemDao.findDTOById(itemId));
		int r = (int)(Math.random()*2);
		boolean mLanQues = false;
		if(r==0)
			mLanQues = true;
		if(mLanQues){
			myquiz.setContent(mtitle.getContent());
			questionType.setLanguageId(title.getLanguage());
		}else{
			myquiz.setContent(title.getContent());
			questionType.setLanguageId(mtitle.getLanguage());
		}
		myquiz.setId(KeyGenerateUtil.generateIdUUID());
		List<MyQuizChoice> choices = this.quizChoicesCreater(userId, itemId, myquiz, questionType);
		this.myquizDao.insert(myquiz);
		if(choices!=null&&choices.size()>3){
			for(MyQuizChoice choice: choices){
				choice.setMyquizId(myquiz.getId());
				choice.setId(KeyGenerateUtil.generateIdUUID());
				myQuizChoiceDao.insert(choice);
			}
			myquiz.setQuizChoices(choices);
			return myquiz;
		}
		
		return null;
	}
	
	public ItemTitle getItemTitle(String itemId, String languageId){
//		DetachedCriteria dc = DetachedCriteria.forClass(ItemTitle.class);
//		dc.add(Restrictions.eq("item", item));
//		dc.add(Restrictions.eq("language",language));
		List<ItemTitle> titles = this.itemTitleDao.findListByItemAndLanguage(itemId, languageId);
		if(titles!=null&&titles.size()>0)
			return titles.get(0);
		else
			return null;
	}
	
	public ItemTitle getMotherLanTitle(String itemId, String userId){
//		DetachedCriteria dc = DetachedCriteria.forClass(ItemTitle.class);
//		dc.add(Restrictions.eq("item", item));
//		dc.add(Restrictions.in("language", user.getMyLangs()));
		
		List<ItemTitle> titles = this.itemTitleDao.findListByItemAndInLanguages(itemId, languageDao.findListUsersMyLangs(userId));
		if(titles!=null&&titles.size()>0)
			return titles.get(0);
		else
			return null;
	}
	

	private List<MyQuizChoice> quizChoicesCreater(String userId, String itemId, MyQuiz myquiz, ItemQuestionType quesType){
		List<MyQuizChoice> choices = new LinkedList<MyQuizChoice>();
		List<Item> itemchoices = new LinkedList<Item>();
		Item item = itemDao.findById(itemId);
		
//		String sql = "select *	from t_item it  where it.id in ( " +
//				"select distinct(item_id) from t_itemqueue " +
//				" where author_id = '"+user.getId()+"') and it.id!= '"+item.getId()+"' " +
//				"and exists ( select * from t_item_question_type itq " +
//					"where itq.item_id = it.id and itq.questiontype_id = "+quesType.getQuestionType().getId()+
//					" and itq.language_id = '"+quesType.getLanguage().getId()+"')" +
//							" limit 0, 50 ";
		
//		String sql = "select it.* from t_item it, t_itemqueue q " +
//				"where 	q.queuetype in (1, 2, 3) and q.item_id = it.id " +
//				"and it.id!= '"+item.getId()+"' " +
//				"and exists ( select * from t_item_question_type itq where itq.item_id = it.id and itq.questiontype_id = 1 and itq.language_id = '"+quesType.getLanguage().getId()+"') limit 0, 50";
		
		List<Item> items = this.itemDao.findListForTextQuizGenerate(itemId, quesType.getLanguageId());
		
		while(itemchoices.size()<3&&items.size()>=(3-itemchoices.size())){
			int index = (int)(items.size()*Math.random());
			Item tempitem = items.get(index);
			if(itemService.inItemList(tempitem, itemchoices)||itemService.itemRealted(item,tempitem)){
				items.remove(tempitem);
				continue;
			}
			itemchoices.add(tempitem);
			items.remove(tempitem);
		}
	

		int answer = (int)(Math.random()*4)+1;
		myquiz.setAnswer(answer+"");
		int num = 1;
		boolean toAdd = true;
		for(Item itemchoice:itemchoices){
			if(num==answer){
				MyQuizChoice tempchoice = new MyQuizChoice();
				tempchoice.setItemId(itemId);
				ItemTitle title = this.getItemTitle(itemId, quesType.getLanguageId());
				tempchoice.setContent(title.getContent());
				tempchoice.setNote(itemService.getItemNote(item));
				tempchoice.setNumber(num);
				choices.add(tempchoice);
				num++;
				toAdd = false;
			}
			MyQuizChoice choice = new MyQuizChoice();
			choice.setItemId(itemchoice.getId());
			choice.setNote(itemService.getItemNote(itemchoice));
			choice.setNumber(num);
			ItemTitle title = this.getItemTitle(itemchoice.getId(), quesType.getLanguageId());
			choice.setContent(title.getContent());
			choices.add(choice);
			num++;
		}
		if(toAdd){
			MyQuizChoice tempchoice = new MyQuizChoice();
			tempchoice.setItemId(itemId);
			ItemTitle title = this.getItemTitle(itemId, quesType.getLanguageId());
			tempchoice.setContent(title.getContent());
			tempchoice.setNote(itemService.getItemNote(item));
			tempchoice.setNumber(answer);
			choices.add(tempchoice);
			num++;
			toAdd = false;
		}
		
		return choices;
	}

	public MyQuizDTO checkAnswer(MyQuizDTO quiz) {
		return this.checkAnswer(quiz, myquizDao, itemDao, itemQueueService, reviewHistoryService);
	}
	
	@Override
	public QuizWrapper convertQuiz(MyQuizDTO quiz) {
		QuizWrapper wrapper = this.convertChoiceQuiz(quiz);
		wrapper.setImgServerUrl(staticserverImageUrl);
		wrapper.setQuestionTypeId(Constants.QuizTypeTextMutiChoice);
		return wrapper;
	}
}
