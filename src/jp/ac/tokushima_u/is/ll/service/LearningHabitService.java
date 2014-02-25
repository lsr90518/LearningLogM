package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.StudyAreaDao;
import jp.ac.tokushima_u.is.ll.dao.StudySpeedDao;
import jp.ac.tokushima_u.is.ll.dao.StudyTimeDao;
import jp.ac.tokushima_u.is.ll.entity.StudyArea;
import jp.ac.tokushima_u.is.ll.entity.StudyTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("learningHabitService")
@Transactional(readOnly = true)
public class LearningHabitService {
	@Autowired
	private StudyAreaDao studyAreaDao;
	@Autowired
	private StudyTimeDao studyTimeDao;
	@Autowired
	private StudySpeedDao studySpeedDao;
	
	public List<StudyArea> searchStudyArea(String userId){
//		Users user = this.userDao.findById(userId);
//		DetachedCriteria dc = DetachedCriteria.forClass(StudyArea.class);
//		dc.add(Restrictions.eq("author", user));
//		dc.add(Restrictions.eq("disabled", 0));
		List<StudyArea> areas = this.studyAreaDao.findListByAuthor(userId);
		return areas;
	}
	
	public List<StudyTime> searchStudyTime(String userId){
//		Users user = this.userDao.findById(userId);
//		DetachedCriteria dc = DetachedCriteria.forClass(StudyTime.class);
//		dc.add(Restrictions.eq("author", user));
//		dc.add(Restrictions.eq("disabled", 0));
		List<StudyTime> times = this.studyTimeDao.findListByAuthor(userId);
		return times;
	}

}
