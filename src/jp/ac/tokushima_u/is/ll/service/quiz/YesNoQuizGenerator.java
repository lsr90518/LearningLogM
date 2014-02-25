package jp.ac.tokushima_u.is.ll.service.quiz;

import java.util.Date;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.ItemQuestionTypeDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemQuestionType;
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

@Service("yesnoQuizGenerator")
@Transactional(readOnly = true)
public class YesNoQuizGenerator implements QuizGenerator {
    private static final Integer RememberAnswer = 1;
    private static final Integer ForgetAnswer = 0;
    
	@Value("${system.staticserverImageUrl}")
	private String staticserverImageUrl;
    @Autowired
    private ItemQueueService itemQueueService;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private MyQuizDao myquizDao;
    @Autowired
    private ItemService itemService;
	@Autowired
	private ItemQuestionTypeDao itemQuestionTypeDao;
	@Autowired
	private ReviewHistoryService reviewHistoryService;

	@Override
	@Transactional(readOnly = false)
	public MyQuizDTO generate(String itemId, String questionTypeId, String userId, QuizCondition qc) {
		ItemQuestionType questionType = itemQuestionTypeDao.findById(questionTypeId);
		MyQuizDTO myquiz = new MyQuizDTO();
		Item item = itemDao.findById(itemId);
		if(!userId.equals(item.getAuthorId()))
			return null;
		myquiz.setAuthorId(userId);
		myquiz.setCreateDate(new Date());
		myquiz.setAlarmtype(qc.getAlarmtype());

	    if(qc.getLng()!=null&&qc.getLat()!=null){
        	myquiz.setLongitude(qc.getLng());
        	myquiz.setLatitude(qc.getLat());
        }
        if(qc.getSpeed()!=null)
        	myquiz.setSpeed(qc.getSpeed());
		myquiz.setItemId(itemId);
		myquiz.setAnswerstate(Constants.NotAnsweredState);
		myquiz.setQuestiontypeid(questionType.getQuestiontypeId());
		myquiz.setUpdateDate(new Date());
		myquiz.setId(KeyGenerateUtil.generateIdUUID());
		myquiz.setItem(itemDao.findDTOById(itemId));
		this.myquizDao.insert(myquiz);
		return myquiz;
	}
	
	@Override
	@Transactional(readOnly = false)
	public MyQuizDTO checkAnswer(MyQuizDTO quiz) {
		Item item = itemDao.findById(quiz.getItemId());
		int queuetype = QuizConstants.QueueTypeRightQuiz;
		int times = 0;
		Integer answerState = null;
		if(!Constants.PassAnsweredState.equals(quiz.getAnswerstate())){
			Integer myanswer = Integer.valueOf(quiz.getMyanswer());
			if(myanswer.equals(RememberAnswer)){
				quiz.setAnswerstate(Constants.CorrectAnsweredState);
				queuetype = QuizConstants.QueueTypeRightQuiz;
				answerState = Constants.CorrectAnsweredState;
				if(item.getRighttimes()==null)
					times = 0;
				else 
					times = item.getRighttimes();
				item.setRighttimes(times+1);
			}else if(myanswer.equals(ForgetAnswer)){
				quiz.setAnswerstate(Constants.WrongAnsweredState);
				queuetype = QuizConstants.QueueTypeWrongQuiz;
				answerState = Constants.WrongAnsweredState;
				if(item.getWrongtimes()==null)
					times = 0;
				else 
					times = item.getWrongtimes();
				item.setWrongtimes(times+1);
			}
		}else{
			queuetype = QuizConstants.QueueTypeObjectPass;
			answerState = Constants.PassAnsweredState;
			if(item.getPass()==null)
				times = 0;
			else 
				times = item.getPass();
			item.setPass(times+1);
		}
		
		quiz.setUpdateDate(new Date());
		if(quiz.getId()==null){
			quiz.setId(KeyGenerateUtil.generateIdUUID());
			myquizDao.insert(quiz);
		}else{
			myquizDao.update(quiz);
		}
		itemQueueService.updateItemQueue(quiz.getItemId(), quiz.getAuthorId(), queuetype);
		reviewHistoryService.updateItemState(quiz.getItemId(), quiz.getAuthorId(), null, answerState);
		itemDao.update(item);
		return quiz;
	}

	@Override
	public QuizWrapper convertQuiz(MyQuizDTO quiz) {
		QuizWrapper wrapper = new QuizWrapper();
		wrapper.setQuizid(quiz.getId());
		wrapper.setAnswerstate(Constants.NotAnsweredState);
//		String photoUrl = null;
//		Item item = quiz.getItem();
//		if(item.getImage()!=null)
//			photoUrl = item.getImage().getId();
		Item item = itemDao.findById(quiz.getItemId());
		wrapper.setItemform(itemService.convertItemToItemForm(item));
		wrapper.setImgServerUrl(staticserverImageUrl);
		return wrapper;
	}
}
