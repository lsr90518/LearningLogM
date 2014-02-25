package jp.ac.tokushima_u.is.ll.service.quiz;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizChoiceDao;
import jp.ac.tokushima_u.is.ll.dao.MyQuizDao;
import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;
import jp.ac.tokushima_u.is.ll.entity.MyQuizChoice;
import jp.ac.tokushima_u.is.ll.service.ItemQueueService;
import jp.ac.tokushima_u.is.ll.service.visualization.ReviewHistoryService;
import jp.ac.tokushima_u.is.ll.util.Constants;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class MutipleChoiceQuiz implements QuizGenerator{
	
	@Autowired
	private MyQuizChoiceDao myQuizChoiceDao;
	
	public MyQuizDTO checkAnswer(MyQuizDTO quiz, MyQuizDao myquizDao, ItemDao itemDao, ItemQueueService itemQueueService, ReviewHistoryService reviewHistoryService){
		Item item = itemDao.findById(quiz.getItemId());
		int queuetype = QuizConstants.QueueTypeRightQuiz;
		int times = 0;
		Integer answerState = null;
		if(!Constants.PassAnsweredState.equals(quiz.getAnswerstate())){
			if(Constants.DifficultAnsweredState.equals(quiz.getAnswerstate())){
				queuetype = QuizConstants.QueueTypeDifficultQuiz;
				answerState = Constants.DifficultAnsweredState;
			}else if(Constants.EasyAnsweredState.equals(quiz.getAnswerstate())){
				queuetype = QuizConstants.QueueTypeEasyQuiz;
				answerState = Constants.EasyAnsweredState;
			}
			Integer myanswer = Integer.valueOf(quiz.getMyanswer());
			Integer answer = Integer.valueOf(quiz.getAnswer());
			if(myanswer.equals(answer)){
				if(!Constants.DifficultAnsweredState.equals(quiz.getAnswerstate())&&!Constants.EasyAnsweredState.equals(quiz.getAnswerstate())){
					queuetype = QuizConstants.QueueTypeRightQuiz;
					answerState = Constants.CorrectAnsweredState;
				}
				quiz.setAnswerstate(Constants.CorrectAnsweredState);
				times = item.getRighttimes();
				item.setRighttimes(times+1);
			}else{
				if(!Constants.DifficultAnsweredState.equals(quiz.getAnswerstate())&&!Constants.EasyAnsweredState.equals(quiz.getAnswerstate())){
					quiz.setAnswerstate(Constants.WrongAnsweredState);
					answerState = Constants.WrongAnsweredState;
				}
				queuetype = QuizConstants.QueueTypeWrongQuiz;
				times = item.getWrongtimes();
				item.setWrongtimes(times+1);
			}
//			}
		}else{
			queuetype = QuizConstants.QueueTypeObjectPass;
			answerState = Constants.PassAnsweredState;
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
	
	public QuizWrapper convertChoiceQuiz(MyQuiz quiz) {
		QuizWrapper quizWrapper = new QuizWrapper();
		quizWrapper.setAnswer(quiz.getAnswer());
		quizWrapper.setAnswerstate(Constants.NotAnsweredState);
		quizWrapper.setChoices(this.getChoiceContents(myQuizChoiceDao.findListByMyQuizId(quiz.getId())));
		quizWrapper.setContent(quiz.getContent());
		quizWrapper.setCreateDate(quiz.getCreateDate());
		quizWrapper.setQuizid(quiz.getId());
		quizWrapper.setNotes(this.getChoiceNotes(myQuizChoiceDao.findListByMyQuizId(quiz.getId())));
		return quizWrapper;
	}
	
	public String[] getChoiceContents(List<MyQuizChoice> choices){
		String[]contents = new String[choices.size()];
		for(int i=0;i<choices.size();i++){
			contents[i]=choices.get(i).getContent();
		}
		return contents;
	}
	
	public String[] getChoiceNotes(List<MyQuizChoice> choices){
		String[]notes = new String[choices.size()];
		for(int i=0;i<choices.size();i++){
			notes[i]=choices.get(i).getNote();
		}
		return notes;
	}
}
