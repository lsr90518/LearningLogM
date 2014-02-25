package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.QuestionDao;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuestionService {

	@Autowired
	private QuestionDao questionDao;
	@Autowired
	private LanguageDao languageDao;
	
	public List<Map<String, Object>> searchForToAnswer(String userId, PageRequest pageRequest) {
		List<Language> langs = languageDao.findListUsersMyLangs(userId);
		if(langs==null || langs.isEmpty())return new ArrayList<Map<String, Object>>();
		return this.questionDao.findMapListForToAnswer(userId, langs, pageRequest);
	}

	public List<Map<String, Object>> searchForToStudy(String userId, PageRequest pageRequest) {
		List<Language> langs = languageDao.findListUsersStudyLangs(userId);
		if(langs==null || langs.isEmpty())return new ArrayList<Map<String, Object>>();
		return this.questionDao.findMapListForToStudy(userId, langs, pageRequest);
	}

	public List<Map<String, Object>> searchForLatestAnsweredForAuthor(String userId,
			PageRequest pageRequest) {
		return this.questionDao.findMapListForLatestAnsweredForAuthor(userId, pageRequest);
	}

	public Question findById(String questionId) {
		return questionDao.findById(questionId);
	}

}
