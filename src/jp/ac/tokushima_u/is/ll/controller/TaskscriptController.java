package jp.ac.tokushima_u.is.ll.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.service.TaskSyncronizeService;
import jp.ac.tokushima_u.is.ll.service.TaskscriptSyncronizeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
@RequestMapping("/TaskscriptSync")
public class TaskscriptController {
	
		@Autowired
		private TaskSyncronizeService tasksyncronizeservice;
		@Autowired
		private TaskscriptSyncronizeService taskscriptsyncronizeservice;
		public String nitizi_controller;
		public String aite_controller;
		public String place_controller;
		public String old_controller;
		public String ninzu_controller;
		public String action_controller;
		public String important_controller;
		public String opinion_controller;
		public String user_name_controller;
		byte[] bytes;


//		@RequestMapping(value = "/task", method = RequestMethod.POST)
//		@ResponseBody
//	    public String items(String sample, ModelMap model, HttpServletRequest request) {
//			model.clear();
//			ArrayList list=new ArrayList();
//			list=tasksyncronizeservice.Info();
//			Map<String,String[]> map = new HashMap<String,String[]>();
//			map.put("level",(String[]) list.get(0));
//			map.put("id",(String[]) list.get(1));
//			map.put("create_time",(String[]) list.get(2));
//			map.put("lat",(String[]) list.get(3));
//			map.put("lng",(String[]) list.get(4));
//			map.put("place",(String[]) list.get(5));
//			map.put("title",(String[]) list.get(6));
//			map.put("update_time",(String[]) list.get(7));
////			map.put("author_id",(String[]) list.get(8));
////			map.put("language_id",(String[]) list.get(9));
////			map.put("location_base",(String[]) list.get(10));
//			Gson g = new Gson();
//			
//	        return g.toJson(map);
//	    }

		@RequestMapping(value = "/taskscript", method = RequestMethod.POST)
		@ResponseBody
	    public String sample(String taskId, ModelMap model, HttpServletRequest request) {
			model.clear();
			ArrayList list=new ArrayList();
			list=taskscriptsyncronizeservice.Info(taskId);
			Map<String,String[]> map = new HashMap<String,String[]>();
			map.put("id",(String[]) list.get(0));
			map.put("lat",(String[]) list.get(1));
			map.put("lng",(String[]) list.get(2));
			map.put("num",(String[]) list.get(3));
			map.put("script",(String[]) list.get(4));
			map.put("task_id",(String[]) list.get(5));
			map.put("image_id",(String[]) list.get(6));
			map.put("location",(String[]) list.get(7));
			map.put("image_name",(String[])list.get(8));
			Gson g = new Gson();
			
	        return g.toJson(map);
	    }
		
		
	}