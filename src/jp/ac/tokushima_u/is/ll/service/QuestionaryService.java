package jp.ac.tokushima_u.is.ll.service;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.QuestionaryHabitDao;
import jp.ac.tokushima_u.is.ll.entity.QuestionaryHabit;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("questionaryService")
@Transactional(readOnly = true)
public class QuestionaryService {
	
	@Autowired
	private QuestionaryHabitDao questionaryHabitDao;

	@Transactional(readOnly = false)
	public void evaluateLearningHabit(QuestionaryHabit habit){
		if(habit.getId() == null){
			habit.setId(KeyGenerateUtil.generateIdUUID());
			this.questionaryHabitDao.insert(habit);
		}else{
			this.questionaryHabitDao.update(habit);
		}
	}

	public QuestionaryHabit findEvaluation(String userId, Date date){
//		DetachedCriteria dc = DetachedCriteria.forClass(QuestionaryHabit.class);
//		dc.add(Restrictions.eq("user", user));
//		dc.add(Restrictions.gt("createTime", date));
//		dc.addOrder(Order.desc("createTime"));
		List<QuestionaryHabit> qhList = this.questionaryHabitDao.findListForEvaluation(userId, date);
		if(qhList!=null && qhList.size()>0)
			return qhList.get(0);
		else
			return null;
	}

}
