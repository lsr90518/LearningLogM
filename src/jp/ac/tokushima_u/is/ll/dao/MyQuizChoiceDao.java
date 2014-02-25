package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.MyQuizChoice;

public interface MyQuizChoiceDao {

	List<MyQuizChoice> findListByMyQuizId(String myQuizId);

	void insert(MyQuizChoice choice);
	
}
