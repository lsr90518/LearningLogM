package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.TaskDao;
import jp.ac.tokushima_u.is.ll.dao.TaskScriptDao;
import jp.ac.tokushima_u.is.ll.entity.TaskScript;
import jp.ac.tokushima_u.is.ll.form.TaskScriptForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskScriptService {

	@Autowired
	private UserService userService;

	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private TaskScriptDao taskScriptDao;
	@Autowired
	private FileDataDao fileDataDao;

	

	public List<TaskScript> findTaskScript(String id) {
		// TODO Auto-generated method stub
//		TaskItem taskitem=new TaskItem();
		
		
		return this.taskScriptDao.findTaskscriptselect(id);
	}

	public List<TaskScriptForm> findTaskScript2(String id) {
		// TODO Auto-generated method stub
//		TaskItem taskitem=new TaskItem();
		
		
		return this.taskScriptDao.findTaskscriptselect2(id);
	}

	public List<TaskScript> findTaskScriptcollaborate(String id) {
		// TODO Auto-generated method stub
		return this.taskScriptDao.findTaskscriptcollaborateselect(id);
	}
	
	
}