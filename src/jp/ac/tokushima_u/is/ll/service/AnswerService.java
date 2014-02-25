package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.AnswerDao;
import jp.ac.tokushima_u.is.ll.entity.Answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnswerService {
	
	@Autowired
	private AnswerDao answerDao;
	public List<Answer> findByQuestionId(String questionId){
		return this.answerDao.findListByQuestionId(questionId);
	}
}
