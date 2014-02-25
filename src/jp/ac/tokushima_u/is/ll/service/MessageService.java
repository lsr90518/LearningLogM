package jp.ac.tokushima_u.is.ll.service;

import java.sql.Time;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.MessageDao;
import jp.ac.tokushima_u.is.ll.entity.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

//@Service("messageservice")
@Transactional(readOnly = true)
public class MessageService {
	public static final Integer MorningType = 1;
	public static final Time MorningTimeStart= Time.valueOf("06:30:00");
	public static final Time MorningTimeEnd= Time.valueOf("08:30:00");
	public static final Integer EveningType = 2;
	public static final Time EveningTimeStart= Time.valueOf("20:00:00");
	public static final Time EveningTimeEnd= Time.valueOf("21:30:00");
	public static final Integer NoonType = 3;
	public static final Time NoonTimeStart = Time.valueOf("12:00:00");
	public static final Time NoonTimeEnd = Time.valueOf("13:30:00");
	
	public static final Long NoItemNoticeID = 1l;
	public static final String NoItemNotice = "What did you learn today? ";
	public static final Long QuestionNoticeID = 2l;
	public static final String QuestionNotice = "Did you have any questions/problems in your learning process today?"; 
	public static final Long NoQuizNoticeID = 3l; 
	public static final String NoQuizNotice = "What about doing some quizzes?";
	
	@Autowired
	private MessageDao messageDao;
	
	@Transactional(readOnly = false)
	private void initMessage(){
		Long messageCount = this.messageDao.countAll();
		if(messageCount==0){
			return;
		}
		
		Message noitemMessage= new Message();
		noitemMessage.setId(NoItemNoticeID);
		noitemMessage.setContent(NoItemNotice);
		noitemMessage.setTyp(EveningType);
		this.messageDao.insert(noitemMessage);
		
		Message questionMessage = new Message();
		questionMessage.setContent(QuestionNotice);
		questionMessage.setId(QuestionNoticeID);
		questionMessage.setTyp(EveningType);
		this.messageDao.insert(questionMessage);
		
		Message noquizMessage = new Message();
		noquizMessage.setContent(NoQuizNotice);
		noquizMessage.setId(NoQuizNoticeID);
		noquizMessage.setTyp(EveningType);
		this.messageDao.insert(noquizMessage);		
	}
	
	public List<Message> findMesage(Message m){
//		DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
//		if(m.getId()!=null)
//			criteria.add(Restrictions.eq("id", m.getId()));
//		if(m.getTyp()!=null)
//			criteria.add(Restrictions.eq("typ", m.getTyp()));
		return this.messageDao.findListByExample(m);
	}
	
}
