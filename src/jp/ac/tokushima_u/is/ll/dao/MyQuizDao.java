package jp.ac.tokushima_u.is.ll.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.dto.MyQuizDTO;
import jp.ac.tokushima_u.is.ll.entity.MyQuiz;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Sort;

public interface MyQuizDao {

	List<MyQuizDTO> findListByAuthor(String authorId);

	List<MyQuizDTO> findListByAnswerstatus(@Param("author")String authorId, @Param("answerstatus")Integer status);

	List<Map<String, Object>> countAnswerQuizzesOnDayByAuthors(Date date);

	List<Map<String, Object>> countAnswerQuizzesInWeekByAuthors(@Param("start")Date start,
			@Param("end")Date end);

	MyQuizDTO findById(String quizid);

	List<MyQuizDTO> findListByExample(@Param("example")MyQuiz example, @Param("sort")Sort sort);

	List<MyQuizDTO> findListRightQuizzes(String userId);

	List<MyQuizDTO> findListWrongQuizzes(String userId);

	List<Map<String, Object>> findMapListAnswerStatesByAuthorId(String userId);

	List<Map<String, Object>> findMapListAnswerStatesByAuthorIdAndUpdateDate(
			@Param("userId")String userId, @Param("from")java.sql.Date date, @Param("to")java.sql.Date date2);

	List<Map<String, Object>> findMapListQuizScoreRanking();

	List<MyQuizDTO> findListByAuthorAndAnsweredAfter(@Param("authorId")String authorId, @Param("lastDate")Date lastDate);

	void insert(MyQuiz quiz);

	void update(MyQuiz quiz);
}
