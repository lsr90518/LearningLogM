package jp.ac.tokushima_u.is.ll.service.quiz;

import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.service.helper.QuizCondition;

public interface QuizGenerator {
	public MyQuizDTO generate(String itemId, String questionTypeId, String userId, QuizCondition qc);
	public MyQuizDTO checkAnswer(MyQuizDTO quiz);
	public QuizWrapper convertQuiz(MyQuizDTO quiz);
}
