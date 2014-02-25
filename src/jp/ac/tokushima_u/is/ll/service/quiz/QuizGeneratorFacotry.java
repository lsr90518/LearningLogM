package jp.ac.tokushima_u.is.ll.service.quiz;

import jp.ac.tokushima_u.is.ll.util.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quizGeneratorFacotry")
public class QuizGeneratorFacotry {
    @Autowired
    private TextChoiceQuizGenerator textQuizGenerator;
    @Autowired
    private ImageChoiceQuizGenerator imageQuizGenerator;
    @Autowired
    private YesNoQuizGenerator yesnoQuizGenerator;
    
	public QuizGenerator getQuizGeneratorByQuestionTypId(Long questionTypeId){
		if(Constants.QuizTypeImageMutiChoice.equals(questionTypeId)){
			return  this.imageQuizGenerator;
		}
		if (Constants.QuizTypeTextMutiChoice.equals(questionTypeId)){
			return this.textQuizGenerator;
		}
		else 
		if(Constants.QuizTypeYesNoQuestion.equals(questionTypeId)){
			return this.yesnoQuizGenerator;
		}
		return null;
	}
}
