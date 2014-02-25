package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.ItemTitleDao;
import jp.ac.tokushima_u.is.ll.dao.TaskDao;
import jp.ac.tokushima_u.is.ll.dao.TaskScriptDao;
import jp.ac.tokushima_u.is.ll.dao.TaskcollaborativeDao;
import jp.ac.tokushima_u.is.ll.dao.TaskcollaborativescriptDao;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.Task;
import jp.ac.tokushima_u.is.ll.entity.TaskScript;
import jp.ac.tokushima_u.is.ll.entity.Taskcollaborative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskSyncronizeService {

	@Autowired
	private TaskDao taskDao;
	@Autowired
	private TaskScriptDao taskScriptDao;
	
	@Autowired
	private TaskcollaborativeDao taskcollaborativedao;
	
	@Autowired
	private TaskcollaborativescriptDao taskcolaborativescriptdao;
	@Autowired
	private FileDataDao fileDataDao;
	@Autowired
	private ItemTitleDao itemtitledao;

	public ArrayList Info() {
		// Task task = new Task();
		List<Task> list = this.taskDao.findTaskALL();
		Task task = new Task();
		Iterator<Task> it = list.iterator();
		String[] level = new String[list.size()];
		String[] id = new String[list.size()];
		String[] create_time = new String[list.size()];
		String[] lat = new String[list.size()];
		String[] lng = new String[list.size()];
		String[] place = new String[list.size()];
		String[] title = new String[list.size()];
		String[] update_time = new String[list.size()];
		String[] author_id = new String[list.size()];
		String[] language_id = new String[list.size()];
		String[] location_base = new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			Task element = it.next();
			level[i] = String.valueOf(element.getLevel());
			id[i] = String.valueOf(element.getId());
			create_time[i] = String.valueOf(element.getCreate_time());
			lat[i] = String.valueOf(element.getLat());
			lng[i] = String.valueOf(element.getLng());
			place[i] = String.valueOf(element.getPlace());
			title[i] = String.valueOf(element.getTitle());
			update_time[i] = String.valueOf(element.getUpdate_time());
			author_id[i] = String.valueOf(element.getAuthor_id());
			language_id[i] = String.valueOf(element.getLanguage_id());
			location_base[i] = String.valueOf(element.getLocationBased());
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(level);
		result.add(id);
		result.add(create_time);
		result.add(lat);
		result.add(lng);
		result.add(place);
		result.add(title);
		result.add(update_time);
		result.add(author_id);
		result.add(language_id);
		result.add(location_base);
		return result;
	}

	public ArrayList Item_data(String taskid) {
		// Task task = new Task();
		List<ItemTitle> list = this.taskDao.findTaskrelated(taskid);
		Task task = new Task();
		Iterator<ItemTitle> it = list.iterator();
		String[] content = new String[list.size()];
		String[] Title_id = new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			ItemTitle element = it.next();

			content[i] = String.valueOf(element.getContent());
			Title_id[i] = String.valueOf(element.getItem());
			// id[i]=String.valueOf(element.getId());
			// create_time[i]=String.valueOf(element.getCreate_time());
			// lat[i]=String.valueOf(element.getLat());
			// lng[i]=String.valueOf(element.getLng());
			// place[i]=String.valueOf(element.getPlace());
			// title[i]=String.valueOf(element.getTitle());
			// update_time[i]=String.valueOf(element.getUpdate_time());
			// author_id[i]=String.valueOf(element.getAuthor_id());
			// language_id[i]=String.valueOf(element.getLanguage_id());
			// location_base[i]=String.valueOf(element.getLocationBased());
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(content);
		result.add(Title_id);
		return result;
	}

	public ArrayList image_data(String taskid) {
		// Task task = new Task();
		List<Item> list = this.taskDao.findrelatedimage(taskid);
		Task task = new Task();
		Iterator<Item> it = list.iterator();
		String[] content = new String[list.size()];
		String[] image_id = new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			Item element = it.next();

			content[i] = String.valueOf(element.getImage());

			image_id[i] = String.valueOf(element.getId());
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(content);
		result.add(image_id);
		return result;
	}

	public ArrayList recommendtask(String word1, String word2, String word3,
			String word4, String word5) {
		List<TaskScript> r1 = this.taskScriptDao.findid(word1);
		Iterator<TaskScript> it = r1.iterator();
		String[] task_id = new String[r1.size()];

		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it.next();

			task_id[i] = String.valueOf(element.getTask_id());
			i++;
			if(r1.size()==i){
				break;
			}
			
		}

		ArrayList result = new ArrayList();
		result.add(task_id);

		List<TaskScript> r2 = this.taskScriptDao.findid(word2);
		Iterator<TaskScript> it2 = r2.iterator();
		String[] task_id2 = new String[r2.size()];

		int i2 = 0;
		while (it2.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it2.next();

			task_id2[i2] = String.valueOf(element.getTask_id());
			i2++;
			
			if(r2.size()==i2){
				break;
			}
		}
		result.add(task_id2);

		List<TaskScript> r3 = this.taskScriptDao.findid(word3);
		Iterator<TaskScript> it3 = r3.iterator();
		String[] task_id3 = new String[r3.size()];

		int i3 = 0;
		while (it3.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it3.next();

			task_id3[i3] = String.valueOf(element.getTask_id());
			i3++;
			if(r3.size()==i3){
				break;
			}
		}
		result.add(task_id3);
		List<TaskScript> r4 = this.taskScriptDao.findid(word4);
		Iterator<TaskScript> it4 = r4.iterator();
		String[] task_id4 = new String[r4.size()];

		int i4 = 0;
		while (it4.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it4.next();

			task_id4[i4] = String.valueOf(element.getTask_id());
			i4++;
			if(r4.size()==i4){
				break;
			}
		}
		result.add(task_id4);
		List<TaskScript> r5 = this.taskScriptDao.findid(word5);
		Iterator<TaskScript> it5 = r5.iterator();
		String[] task_id5 = new String[r5.size()];

		int i5 = 0;
		while (it5.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it5.next();

			task_id5[i5] = String.valueOf(element.getTask_id());
			i5++;
			if(r5.size()==i5){
				break;
			}
		}
		result.add(task_id5);
		return result;

	}

	public ArrayList ability(String usermail) {
		// TODO Auto-generated method stub
		List<TaskScript> list = this.taskScriptDao.findtask(usermail);
		Iterator<TaskScript> it = list.iterator();
		
		String[] taskid = new String[list.size()];
		String[] place = new String[list.size()];
		String[] title = new String[list.size()];
		String[] lat = new String[list.size()];
		String[] lng = new String[list.size()];
		String[] level = new String[list.size()];
		String[] related_task = new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it.next();

			taskid[i] = String.valueOf(element.getId());
			place[i] = String.valueOf(element.getPlace());
			title[i] = String.valueOf(element.getTitle());
			lat[i] = String.valueOf(element.getLat());
			lng[i] = String.valueOf(element.getLng());
			level[i] = String.valueOf(element.getLevel());
			related_task[i] = String.valueOf(element.getRelated_task());
			
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(level);
		result.add(taskid);
		result.add(place);
		result.add(lat);
		result.add(lng);
		result.add(title);
		result.add(related_task);

		return result;
	}

	public ArrayList taskimageitems(String usermail, String taskid) {
		// TODO Auto-generated method stub
		List<TaskScript> list = this.taskScriptDao.findtaskimageitems(usermail,taskid);
		Iterator<TaskScript> it = list.iterator();
		
		String[] taskid2 = new String[list.size()];
		String[] image = new String[list.size()];
		String[] image_name = new String[list.size()];
		String[] num = new String[list.size()];
//		String[] lng = new String[list.size()];
//		String[] level = new String[list.size()];
//		String[] related_task = new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it.next();

			taskid2[i] = String.valueOf(element.getId());
			image[i] = String.valueOf(element.getImage());
			image_name[i] = String.valueOf(element.getImage_name());
		
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(taskid2);
		result.add(image);
		result.add(image_name);
	

		return result;
	}

	public ArrayList collaborativetaskall() {
		// TODO Auto-generated method stub
		List<Taskcollaborative> list = this.taskcollaborativedao.getalltask();
		
		Iterator<Taskcollaborative> it = list.iterator();
		String[] level = new String[list.size()];
		String[] id = new String[list.size()];
		String[] create_time = new String[list.size()];
		String[] lat = new String[list.size()];
		String[] lng = new String[list.size()];
		String[] place = new String[list.size()];
		String[] title = new String[list.size()];
		String[] update_time = new String[list.size()];
		String[] author_id = new String[list.size()];
		String[] language_id = new String[list.size()];
		String[] location_base = new String[list.size()];
		String[] number=new String[list.size()];
		String[] time_limit=new String[list.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			Taskcollaborative element = it.next();
			level[i] = String.valueOf(element.getLevel());
			id[i] = String.valueOf(element.getId());
			create_time[i] = String.valueOf(element.getCreate_time());
			lat[i] = String.valueOf(element.getLat());
			lng[i] = String.valueOf(element.getLng());
			place[i] = String.valueOf(element.getPlace());
			title[i] = String.valueOf(element.getTitle());
			update_time[i] = String.valueOf(element.getUpdate_time());
			author_id[i] = String.valueOf(element.getAuthor_id());
			language_id[i] = String.valueOf(element.getLanguage_id());
			location_base[i] = String.valueOf(element.getLocationBased());
			
			number[i] = String.valueOf(element.getNumber());
			time_limit[i] = String.valueOf(element.getTime_limit());
			i++;
		}

		ArrayList result = new ArrayList();
		result.add(level);
		result.add(id);
		result.add(create_time);
		result.add(lat);
		result.add(lng);
		result.add(place);
		result.add(title);
		result.add(update_time);
		result.add(author_id);
		result.add(language_id);
		result.add(location_base);
		result.add(number);
		result.add(time_limit);
		return result;
	}
}
