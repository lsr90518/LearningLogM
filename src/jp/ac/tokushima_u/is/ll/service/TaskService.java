package jp.ac.tokushima_u.is.ll.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.TaskDao;
import jp.ac.tokushima_u.is.ll.dao.TaskItemDao;
import jp.ac.tokushima_u.is.ll.dao.TaskScriptDao;
import jp.ac.tokushima_u.is.ll.dao.TaskcollaborativeDao;
import jp.ac.tokushima_u.is.ll.dao.TaskcollaborativescriptDao;
import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.JapaneseWordlevel;
import jp.ac.tokushima_u.is.ll.entity.Task;
import jp.ac.tokushima_u.is.ll.entity.TaskItem;
import jp.ac.tokushima_u.is.ll.entity.TaskScript;
import jp.ac.tokushima_u.is.ll.entity.Taskcollaborative;
import jp.ac.tokushima_u.is.ll.entity.Tasklevel;
import jp.ac.tokushima_u.is.ll.exception.NotFoundException;
import jp.ac.tokushima_u.is.ll.form.TaskEditForm;
import jp.ac.tokushima_u.is.ll.form.TaskScriptForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.HashUtils;
import jp.ac.tokushima_u.is.ll.util.JapaneseWordlistUtil;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.SenUtil;
import net.java.sen.Token;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class TaskService {

	@Autowired
	private UserService userService;

	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private TaskDao taskDao;

	@Autowired
	private TaskcollaborativeDao taskcollaborativeDao;

	@Autowired
	private TaskcollaborativescriptDao taskcollaborativescriptdao;

	@Autowired
	private TaskScriptDao taskScriptDao;
	@Autowired
	private FileDataDao fileDataDao;
	@Autowired
	private JapaneseWordlistService japaneseWordlistService;

	@Autowired
	private TaskItemDao taskitemDao;

	@Autowired
	private StaticServerService staticServerService;

	public List<Task> findAllTasks() {
		List<Task> tasks = new ArrayList<Task>();
		return tasks;
	}

	public Task createTask(TaskEditForm form) throws NotFoundException {
		Task task = new Task();
		task.setAuthor_id(SecurityUserHolder.getCurrentUser().getId());
		task.setCreate_time(new Date());
		task.setLanguage_id(form.getLanguageId());
		if (form.getLat() != null && form.getLng() != null) {
			task.setLat(form.getLat());
			task.setLng(form.getLng());
		}
		task.setZoom(form.getZoom());
		task.setPlace(form.getPlace());
		task.setLevel(form.getLevel());
		task.setLocationBased(form.getLocationBased());
		task.setIsPublished(form.getIsPublished());
		task.setUpdate_time(new Date());
		task.setTitle(form.getTitle());
		task.setId(KeyGenerateUtil.generateIdUUID());

		this.taskDao.insert(task);
		return task;
	}

	public Taskcollaborative collaborative_createTask(TaskEditForm form)
			throws NotFoundException {
		// TODO Auto-generated method stub
		Taskcollaborative task = new Taskcollaborative();
		task.setAuthor_id(SecurityUserHolder.getCurrentUser().getId());
		task.setCreate_time(new Date());
		task.setLanguage_id(form.getLanguageId());
		if (form.getLat() != null && form.getLng() != null) {
			task.setLat(form.getLat());
			task.setLng(form.getLng());
		}
		task.setZoom(form.getZoom());
		task.setPlace(form.getPlace());
		task.setLevel(form.getLevel());
		task.setLocationBased(form.getLocationBased());
		task.setIsPublished(form.getIsPublished());
		task.setUpdate_time(new Date());
		task.setTitle(form.getTitle());
		task.setId(KeyGenerateUtil.generateIdUUID());
		task.setNumber(form.getNumber());
		task.setTime_limit(form.getTime_limit());

		this.taskcollaborativeDao.insert(task);
		return task;

	}

	public void createTaskScript(TaskScriptForm form) throws NotFoundException,
			IOException {
		Task task = this.findTaskById(form.getTaskId());
		if (task == null)
			throw new NotFoundException("Task");
		TaskScript taskScript = new TaskScript();
		taskScript.setTask_id(form.getTaskId());
		List<TaskScript> scripts = this.findTaskScriptList(form.getTaskId(),
				false);
		String scripts2 = this.findTaskcount(form.getTaskId());
		// int num = 1;
		// if (scripts != null && scripts.size() > 0){
		int num = 1;
		num = Integer.parseInt(scripts2) + 1;
		taskScript.setNum(num);
		// }
		// // num = scripts.get(0).getNum() + 1;
		// num = scripts.get(0).getNum().
		// taskScript.setNum(1);
		if (form.getLat() != null && form.getLng() != null) {
			taskScript.setLat(form.getLat());
			taskScript.setLng(form.getLng());
		}
		if (form.getImage_name() != null) {
			taskScript.setImage_name(form.getImage_name());
		}

		taskScript.setZoom(form.getZoom());
		taskScript.setScript(form.getScript());

		// Attached File
		if (form.getImage() != null) {
			MultipartFile file = form.getImage();
			if (!file.isEmpty() && file.getSize() != 0) {
				FileData image = new FileData();
				MultipartFile uploadFile = form.getImage();
				image.setOrigName(uploadFile.getOriginalFilename());
				image.setCreateAt(new Date());
				String fileType = "";
				if (!StringUtils.isBlank(uploadFile.getOriginalFilename())) {
					fileType = FilenameUtil.checkMediaType(uploadFile
							.getOriginalFilename());
					image.setFileType(fileType);
				}

				image.setId(KeyGenerateUtil.generateIdUUID());
				byte[] b = form.getImage().getBytes();
				image.setMd5(HashUtils.md5Hex(b));
				fileDataDao.insert(image);
				staticServerService.upload(b, image.getId(), FilenameUtils
						.getExtension(form.getImage().getOriginalFilename()));
				taskScript.setImage(image.getId());
			}
		}
		taskScript.setId(KeyGenerateUtil.generateIdUUID());

		this.taskScriptDao.insert(taskScript);
	}

	public void createTaskcollaborateScript(TaskScriptForm form)
			throws NotFoundException, IOException {
		Taskcollaborative task = this.findTaskcollaborativeById(form.getTaskId());
		if (task == null)
			throw new NotFoundException("Task");
		TaskScript taskScript = new TaskScript();
		taskScript.setTask_id(form.getTaskId());
		List<TaskScript> scripts = this.findTaskcollaborativeScriptList(form.getTaskId(),
				false);
		
		String scripts2 = this.findTaskcollaborativecount(form.getTaskId());
		// int num = 1;
		// if (scripts != null && scripts.size() > 0){
		int num = 1;
		num = Integer.parseInt(scripts2) + 1;
		taskScript.setNum(num);
		// }
		// // num = scripts.get(0).getNum() + 1;
		// num = scripts.get(0).getNum().
		// taskScript.setNum(1);
		if (form.getLat() != null && form.getLng() != null) {
			taskScript.setLat(form.getLat());
			taskScript.setLng(form.getLng());
		}
		if (form.getImage_name() != null) {
			taskScript.setImage_name(form.getImage_name());
		}

		taskScript.setZoom(form.getZoom());
		taskScript.setScript(form.getScript());

		// Attached File
		if (form.getImage() != null) {
			MultipartFile file = form.getImage();
			if (!file.isEmpty() && file.getSize() != 0) {
				FileData image = new FileData();
				MultipartFile uploadFile = form.getImage();
				image.setOrigName(uploadFile.getOriginalFilename());
				image.setCreateAt(new Date());
				String fileType = "";
				if (!StringUtils.isBlank(uploadFile.getOriginalFilename())) {
					fileType = FilenameUtil.checkMediaType(uploadFile
							.getOriginalFilename());
					image.setFileType(fileType);
				}

				image.setId(KeyGenerateUtil.generateIdUUID());
				byte[] b = form.getImage().getBytes();
				image.setMd5(HashUtils.md5Hex(b));
				fileDataDao.insert(image);
				staticServerService.upload(b, image.getId(), FilenameUtils
						.getExtension(form.getImage().getOriginalFilename()));
				taskScript.setImage(image.getId());
			}
		}
		taskScript.setId(KeyGenerateUtil.generateIdUUID());

		this.taskcollaborativescriptdao.insert(taskScript);
	}

	private String findTaskcount(String taskId) {
		// TODO Auto-generated method stub
		return this.taskScriptDao.findTaskscriptcount(taskId);

	}

	public Task findTaskById(String taskId) {
		
			return this.taskDao.findTaskById(taskId);
	}

	public List<TaskScript> findTaskScriptList(String taskId,
			boolean isAscending) {
		return this.taskScriptDao.findTaskScriptByTaskId(taskId, isAscending);
	}

	public void addTaskItems(String id, String itemIds) {
		TaskItem taskitem = new TaskItem();
		// for(int i=0;i<itemIds.length;i++){
		taskitem.setId(KeyGenerateUtil.generateIdUUID());
		taskitem.setTask_id(id);
		taskitem.setItem_id(itemIds);
		this.taskitemDao.insert(taskitem);
		// }
	}
	
	public void addTaskItemscollaborative(String id, String itemIds) {
		TaskItem taskitem = new TaskItem();
		// for(int i=0;i<itemIds.length;i++){
		taskitem.setId(KeyGenerateUtil.generateIdUUID());
		taskitem.setTask_id(id);
		taskitem.setItem_id(itemIds);
		this.taskitemDao.collaborativeinsert(taskitem);
		// }
	}

	public void removeTaskItems(String id, String itemIds) {
		// TODO Auto-generated method stubdelete
		TaskItem taskitem = new TaskItem();

		this.taskitemDao.delete(itemIds);

	}
	public void removeTaskItemscollaborative(String id, String itemIds) {
		// TODO Auto-generated method stubdelete
		TaskItem taskitem = new TaskItem();

		this.taskitemDao.deletecollaborative(itemIds);

	}

	public List<TaskItem> findTaskItems(String id) {
		// TODO Auto-generated method stub
		// TaskItem taskitem=new TaskItem();

		return this.taskitemDao.findItemid(id);
	}

	public int resetLevel(List<TaskScript> scripts) {

		// getJapanesewordlist
		if (JapaneseWordlistUtil.wordlevel.size() < 1) {
			List<JapaneseWordlevel> tempWordlist = japaneseWordlistService
					.getJapanesewordlevellist();
			for (int i = 0; i < tempWordlist.size(); i++) {
				JapaneseWordlistUtil.wordlevel.put(tempWordlist.get(i)
						.getKanji(), tempWordlist.get(i).getLevel());
			}
		}
		HashSet<String> words = new HashSet<String>();

		// use sen to analytics
		SenUtil su = new SenUtil();
		for (int i = 0; i < scripts.size(); i++) {
			Token[] token = su.Analyze(scripts.get(i).getScript());
			for (int j = 0; j < token.length; j++) {
				words.add(token[j].getSurface());
			}

		}

		// look up level value
		ArrayList<String> levellist = new ArrayList<String>();
		for (String word : words) {
			levellist.add(JapaneseWordlistUtil.wordlevel.get(word) + "");
		}

		int level1 = Collections.frequency(levellist, "1");
		int level2 = Collections.frequency(levellist, "2");
		int level3 = Collections.frequency(levellist, "3");
		int level4 = Collections.frequency(levellist, "4");
		int sum = level1 + level2 + level3 + level4;
		float score = 0;
		if (0 != level1) {
			float temp = (float) level1 / (float) sum;
			score += temp;
		}
		if (0 != level2) {
			float temp = (float) level2 / (float) sum;
			temp = temp * 2;
			score += temp;
		}
		if (0 != level3) {
			float temp = (float) level3 / (float) sum;
			temp = temp * 3;
			score += temp;
		}
		if (0 != level4) {
			float temp = (float) level4 / (float) sum;
			temp = temp * 4;
			score += temp;
		}

		return (int) score;
	}

	public void updateLevelById(String id, int level) {
		Tasklevel tl = new Tasklevel();
		tl.setId(id);
		tl.setLevel(level);
		System.out.println(id);
		System.out.println(level);
		this.taskDao.updateLevelById(tl);
	}

	public List<Task> GetTaskAll() {
		// TODO Auto-generated method stub

		return this.taskDao.getalltask();
	}

	public Task taskfindunitily(String id) {
		// TODO Auto-generated method stub

		return this.taskDao.getsingletask(id);
	}

	public List<Task> SerachTaskAll(String title, String place, String level) {
		// TODO Auto-generated method stub
		return this.taskDao.getsearchtask(title, place, level);
	}

	public void update(TaskEditForm form) {
		// TODO Auto-generated method stub

		this.taskDao.updatetask(form.getTitle(), form.getPlace(),
				String.valueOf(form.getLevel()), form.getTaskId());

	}

	public void update2(TaskScriptForm form1) {
		// TODO Auto-generated method stub

		this.taskDao.updatetask2(form1.getProcess1(), form1.getTaskId(), "1");
	}

	public void update3(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess2(), form1.getTaskId(), "2");
	}

	public void update4(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess3(), form1.getTaskId(), "3");
	}

	public void update5(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess4(), form1.getTaskId(), "4");
	}

	public void update6(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess5(), form1.getTaskId(), "5");
	}

	public void update7(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess6(), form1.getTaskId(), "6");
	}

	public void update8(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess7(), form1.getTaskId(), "7");
	}

	public void update9(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess8(), form1.getTaskId(), "8");
	}

	public void update10(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess9(), form1.getTaskId(), "9");
	}

	public void update11(TaskScriptForm form1) {
		this.taskDao.updatetask2(form1.getProcess10(), form1.getTaskId(), "10");
	}

	public Taskcollaborative findTaskcollaborativeById(String id)
			throws NotFoundException {
		// TODO Auto-generated method stub
		Taskcollaborative task = this.taskcollaborativeDao
				.findTaskcollaborativeById(id);
		if (task == null)
			throw new NotFoundException();
		else
			return task;
	}
	
	private String findTaskcollaborativecount(String taskId) {
		// TODO Auto-generated method stub
		return this.taskcollaborativescriptdao.findTaskscriptcount(taskId);

	}

	public List<TaskScript> findTaskcollaborativeScriptList(String taskId,
			boolean isAscending) {
		// TODO Auto-generated method stub
		return this.taskcollaborativescriptdao.findTaskScriptByTaskId(taskId,
				isAscending);
	}

	public void deletetask(String id) {
		// TODO Auto-generated method stub
		this.taskDao.deleteitem(id);
		this.taskDao.delete(id);
		this.taskDao.detelescript(id);
		
		this.taskcollaborativeDao.delete(id);
		this.taskcollaborativeDao.deleteitem(id);
		
		this.taskcollaborativeDao.detelescript(id);
		
	}

	public List<Taskcollaborative> GetcollaborativeTaskAll() {
		// TODO Auto-generated method stub
		return this.taskcollaborativeDao.getalltask();
	}

	public List<Taskcollaborative> SerachTaskcollaborativeAll(String title,
			String place, String level) {
		// TODO Auto-generated method stub
		return this.taskcollaborativeDao.getsearchtask(title, place, level);
	}

	public Taskcollaborative findcollaborativetask(String taskId) {
		 return this.taskcollaborativeDao.findTaskById(taskId);
	
		
	}

	public Page<Task> searchtaskpage(TaskEditForm form) {
		// TODO Auto-generated method stub
		
		int page = 0;
		if(form.getPage()!=null && form.getPage()>=1){
			page = form.getPage()-1;
		}
		Pageable pageable = new PageRequest(page, 10, new Sort(new Sort.Order(Sort.Direction.DESC, "t.create_time")));
		List<Task> list = this.taskDao.searchListByCond(form, pageable);
		Long count = this.taskDao.countByCond(form);
		return new PageImpl<Task>(list, pageable, count);
		
		
		
	}

	public Page<Task> searchtaskpageselect(TaskEditForm form, String title,
			String place, String level) {
		// TODO Auto-generated method stub
		int page = 0;
		if(form.getPage()!=null && form.getPage()>=1){
			page = form.getPage()-1;
		}
		Pageable pageable = new PageRequest(page, 10, new Sort(new Sort.Order(Sort.Direction.DESC, "create_time")));
		List<Task> list = this.taskDao.searchListselect(form, pageable,title,place,level);
		Long count = this.taskDao.countByCondselect(form,title,place,level);
		return new PageImpl<Task>(list, pageable, count);
	}

}
