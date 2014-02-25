package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.TaskDao;
import jp.ac.tokushima_u.is.ll.dao.TaskScriptDao;
import jp.ac.tokushima_u.is.ll.entity.Task;
import jp.ac.tokushima_u.is.ll.entity.TaskScript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskscriptSyncronizeService {


	@Autowired
	private TaskScriptDao taskScriptDao;
	@Autowired
	private FileDataDao fileDataDao;
	public ArrayList Info(String taskId){
//		Task task = new Task();
//		List<TaskScript> list2= this.taskScriptDao.findTaskscriptALL();
		List<TaskScript> list2= this.taskScriptDao.findTaskscriptselect(taskId);
		TaskScript taskscript = new TaskScript();
		 Iterator<TaskScript> it = list2.iterator();
		 String[] id=new String[list2.size()];
		 String[] lat=new String[list2.size()];
		 String[] lng=new String[list2.size()];
		 String[] num=new String[list2.size()];
		 String[] script=new String[list2.size()];
		 String[] image=new String[list2.size()];
		 String[] task_id=new String[list2.size()];
		 String[] location=new String[list2.size()];
		 String[] image_name=new String[list2.size()];
		 int i=0;
		 while(it.hasNext())
         {
               //Listから要素を取り出す
               TaskScript element = it.next();
               id[i]=String.valueOf(element.getId());
               lat[i]=String.valueOf(element.getLat());
               lng[i]=String.valueOf(element.getLng());
               num[i]=String.valueOf(element.getNum());
               script[i]=String.valueOf(element.getScript());
               task_id[i]=String.valueOf(element.getTask_id());
               image[i]=String.valueOf(element.getImage());
               location[i]=String.valueOf(element.getLocation_based());
               image_name[i]=String.valueOf(element.getImage_name());
               i++;
         }

		ArrayList result=new ArrayList();
		result.add(id);
		result.add(lat);
		result.add(lng);
		result.add(num);
		result.add(script);
		result.add(task_id);
		result.add(image);
		result.add(location);
		result.add(image_name);
		return result;
	}
}