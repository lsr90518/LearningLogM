package jp.ac.tokushima_u.is.ll.service.quiz;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizChoiceDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.FileData;
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
import jp.ac.tokushima_u.is.ll.util.StaticImageUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("imageQuizGenerator")
@Transactional(readOnly = true)
public class ImageChoiceQuizGenerator extends MutipleChoiceQuiz{
	@Autowired
    private ItemTitleDao itemTitleDao;
	@Autowired
    protected MyQuizDao myquizDao;
	@Autowired
    private ItemDao itemDao;
	@Autowired
	private FileDataDao fileDataDao;
	@Autowired
	private ItemQuestionTypeDao itemQuestionTypeDao;
	@Autowired
	private MyQuizChoiceDao myQuizChoiceDao;
	@Autowired
	private QuestionTypeDao questionTypeDao;
	@Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;
    @Autowired
    protected ItemQueueService itemQueueService;
    @Autowired
    private ItemService itemService;
    
	@Autowired
	private ReviewHistoryService reviewHistoryService;

	@Override
	@Transactional(readOnly = false)
	public MyQuizDTO generate(String itemId, String questionTypeId, String userId, QuizCondition qc) {
		ItemQuestionType questionType = itemQuestionTypeDao.findById(questionTypeId);
		ItemTitle title = this.getItemTitle(itemId, questionType.getLanguageId());
		if(title==null)
			return null;
		MyQuizDTO myquiz = new MyQuizDTO();
		myquiz.setContent(title.getContent());
		myquiz.setAlarmtype(qc.getAlarmtype());

	    if(qc.getLng()!=null&&qc.getLat()!=null){
        	myquiz.setLongitude(qc.getLng());
        	myquiz.setLatitude(qc.getLat());
        }
        if(qc.getSpeed()!=null)
        	myquiz.setSpeed(qc.getSpeed());
		
		myquiz.setAuthorId(userId);
		myquiz.setCreateDate(new Date());
		myquiz.setItemId(itemId);
		myquiz.setAnswerstate(Constants.NotAnsweredState);
		myquiz.setQuestiontypeid(questionType.getQuestiontypeId());
		myquiz.setUpdateDate(new Date());
		myquiz.setLanguageId(title.getLanguage());
		myquiz.setId(KeyGenerateUtil.generateIdUUID());
		myquiz.setItem(itemDao.findDTOById(itemId));
		this.myquizDao.insert(myquiz);
		
		List<MyQuizChoice> choices = this.quizChoicesCreater(userId, itemId, myquiz, questionType);
		if(choices!=null&&choices.size()>3){
			for(MyQuizChoice choice: choices){
				choice.setMyquizId(myquiz.getId());
				choice.setId(KeyGenerateUtil.generateIdUUID());
				myQuizChoiceDao.insert(choice);
			}
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
	

	private List<MyQuizChoice> quizChoicesCreater(String userId, String itemId, MyQuiz myquiz, ItemQuestionType quesType){
		List<MyQuizChoice> choices = new LinkedList<MyQuizChoice>();
		List<Item> itemchoices = new LinkedList<Item>();
		Item item = itemDao.findById(itemId);
		
//		String sql = "select it.*	from t_item it, t_file_data fd  where it.image = fd.id and it.id in ( " +
//				"select distinct(item_id) from t_itemqueue " +
//				" where author_id = '"+user.getId()+"') and it.image is not null and it.id!= '"+item.getId()+"' " +
//				" and fd.file_type='"+item.getImage().getFileType()+"' "+
//				"and exists ( select * from t_item_question_type itq " +
//					"where itq.item_id = it.id and itq.questiontype_id = "+quesType.getQuestionType().getId()+
//					" and itq.language_id = '"+quesType.getLanguage().getId()+"')";
		
		FileData fileData = fileDataDao.findById(item.getImage());
//		Session session = this.sessionFactory.getCurrentSession();
		
		List<Item> items = this.itemDao.findListForImageQuizGenerate(userId, itemId, fileData.getFileType(), quesType.getQuestiontypeId(), quesType.getLanguageId());
		
		System.out.println(" item size "+items.size());
		
		while(itemchoices.size()<3&&items.size()>=(3-itemchoices.size())){
			int index = (int)(items.size()*Math.random());
			Item tempitem = items.get(index);
			if(itemService.inItemList(tempitem, itemchoices)||itemService.itemRealted(item,tempitem)){
				items.remove(tempitem);
				continue;
			}else if(!StaticImageUtil.isFileExisting(staticserverImageUrl+tempitem.getImage(), Constants.SmartPhoneLevel)){
				items.remove(tempitem);
				continue;
			}
			
			itemchoices.add(tempitem);
			items.remove(tempitem);
		}
		
		if(itemchoices.size()<3){
			return choices;
		}
	

		int answer = (int)(Math.random()*4)+1;
		myquiz.setAnswer(answer+"");
		int num = 1;
		boolean toAdd = true;
		for(Item itemchoice:itemchoices){
			if(num==answer){
				MyQuizChoice tempchoice = new MyQuizChoice();
				tempchoice.setItemId(itemId);
				tempchoice.setContent(item.getImage());
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
				choice.setContent(itemDao.findById(choice.getItemId()).getImage());
			choices.add(choice);
			num++;
		}
		
		if(toAdd){
			MyQuizChoice tempchoice = new MyQuizChoice();
			tempchoice.setItemId(itemId);
			tempchoice.setContent(item.getImage());
			tempchoice.setNote(itemService.getItemNote(item));
			tempchoice.setNumber(answer);
			choices.add(tempchoice);
			num++;
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
		wrapper.setQuestionTypeId(Constants.QuizTypeImageMutiChoice);
		return wrapper;
	}
	
	//	public MyQuiz checkAnswer(MyQuiz quiz) {
//		Item item = quiz.getItem();
//		int queuetype = ItemQueueService.QueueTypeRightQuiz;
//		int times = 0;
//		if(!Constants.PassAnsweredState.equals(quiz.getAnswerstate())){
//			Integer myanswer = Integer.valueOf(quiz.getMyanswer());
//			Integer answer = Integer.valueOf(quiz.getAnswer());
//			if(myanswer.equals(answer)){
//				quiz.setAnswerstate(Constants.CorrectAnsweredState);
//				queuetype = ItemQueueService.QueueTypeRightQuiz;
//				if(item.getRighttimes()==null)
//					times = 0;
//				else 
//					times = item.getRighttimes();
//				item.setRighttimes(times+1);
//			}else{
//				quiz.setAnswerstate(Constants.WrongAnsweredState);
//				queuetype = ItemQueueService.QueueTypeWrongQuiz;
//				if(item.getWrongtimes()==null)
//					times = 0;
//				else 
//					times = item.getWrongtimes();
//				item.setWrongtimes(times+1);
//			}
//		}else{
//			queuetype = ItemQueueService.QueueTypeObjectPass;
//			if(item.getPass()==null)
//				times = 0;
//			else 
//				times = item.getPass();
//			item.setPass(item.getPass()+1);
//		}
//		
//		quiz.setUpdateDate(new Date());
//		this.myquizDao.save(quiz);
//		this.itemQueueService.updateItemQueue(quiz.getItem(), quiz.getAuthor(), queuetype);
//		this.itemDao.save(item);
//		return quiz;
//	}
}
