package jp.ac.tokushima_u.is.ll.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.form.ItemSyncCondForm;
import jp.ac.tokushima_u.is.ll.form.LearningHabitForm;
import jp.ac.tokushima_u.is.ll.service.TaskSyncronizeService;
import jp.ac.tokushima_u.is.ll.service.TaskscriptSyncronizeService;
import jp.ac.tokushima_u.is.ll.service.FutureLogService;
import jp.ac.tokushima_u.is.ll.ws.service.model.ItemForm;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * 
 * @author mouri 未来館のプログラム
 * 
 */
@Controller
@RequestMapping("/TaskSync")
public class TaskSynchronizeController {

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
	public String word1;
	public String word2;
	public String word3;
	public String word4;
	public String word5;
	byte[] bytes;

	@RequestMapping(value = "/task", method = RequestMethod.POST)
	@ResponseBody
	public String items(String sample, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		ArrayList list = new ArrayList();
		list = tasksyncronizeservice.Info();
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("level", (String[]) list.get(0));
		map.put("id", (String[]) list.get(1));
		map.put("create_time", (String[]) list.get(2));
		map.put("lat", (String[]) list.get(3));
		map.put("lng", (String[]) list.get(4));
		map.put("place", (String[]) list.get(5));
		map.put("title", (String[]) list.get(6));
		map.put("update_time", (String[]) list.get(7));
		// map.put("author_id",(String[]) list.get(8));
		// map.put("language_id",(String[]) list.get(9));
		map.put("location_base", (String[]) list.get(10));
		Gson g = new Gson();

		return g.toJson(map);
	}

	@RequestMapping(value = "/taskability", method = RequestMethod.POST)
	@ResponseBody
	public String ability_items(String usermail,String taskdata, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		ArrayList list = new ArrayList();
		list = tasksyncronizeservice.ability(usermail);
		
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("level", (String[]) list.get(0));
		map.put("taskid", (String[]) list.get(1));
		map.put("place", (String[]) list.get(2));
		map.put("lat", (String[]) list.get(3));
		map.put("lng", (String[]) list.get(4));
		map.put("title", (String[]) list.get(5));
		map.put("related_task", (String[]) list.get(6));
//		map.put("update_time", (String[]) list.get(7));
//		// map.put("author_id",(String[]) list.get(8));
//		// map.put("language_id",(String[]) list.get(9));
//		map.put("location_base", (String[]) list.get(10));
		Gson g = new Gson();

		return g.toJson(map);
	}
	
	@RequestMapping(value = "/taskitemimage", method = RequestMethod.POST)
	@ResponseBody
	public String task_items(String usermail,String taskid, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		ArrayList list = new ArrayList();
		list = tasksyncronizeservice.taskimageitems(usermail,taskid);
		
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("taskid", (String[]) list.get(0));
		map.put("image", (String[]) list.get(1));
		map.put("image_name", (String[]) list.get(2));

		Gson g = new Gson();

		return g.toJson(map);
	}

	
	
	
	@RequestMapping(value = "/relatedtask", method = RequestMethod.POST)
	@ResponseBody
	public String related_items(String taskid, ModelMap model,
			HttpServletRequest request) {
		
		ArrayList list2 = new ArrayList();
		list2 = tasksyncronizeservice.Item_data(taskid);
		ArrayList list3 = new ArrayList();
		list3 = tasksyncronizeservice.image_data(taskid);
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("title", (String[]) list2.get(0));
		map.put("title_id", (String[]) list2.get(1));
		map.put("image", (String[]) list3.get(0));
		map.put("image_id", (String[]) list3.get(1));
		Gson g = new Gson();
		return g.toJson(map);
	}

	@RequestMapping(value = "/recommendtask", method = RequestMethod.POST)
	@ResponseBody
	public String related_task(String item, ModelMap model,
			HttpServletRequest request) {

		try {
			String eucjpStr = new String(item.getBytes("UTF-8"), "UTF-8");
			System.out.println(eucjpStr);
		} catch (UnsupportedEncodingException e) {
			System.out.println("ERROR FUTURE84");

		}

		// Gsonオブジェクトを作成
		Gson gson = new Gson();
		if (item != null) {
			System.out.println(gson.toJson(item));
			// パーサーを取得

			JsonParser jp = new JsonParser();

			// 文字列をパース
			try {
				JsonElement je = jp.parse(item);
				JsonArray trade = je.getAsJsonArray();
				int a1 = trade.size();

				for (int i = 0; i < trade.size(); i++) {
					// for(int i=0;i<=trade.size();i++)
					Set<Map.Entry<String, JsonElement>> entrySet = trade.get(i)
							.getAsJsonObject().entrySet();
					// Set<Map.Entry<String, JsonElement>> entrySet =
					// je.getAsJsonObject()
					// .entrySet();
					Iterator<Map.Entry<String, JsonElement>> it = entrySet
							.iterator();

					// 一覧表示

					while (it.hasNext())

					{

						Map.Entry<String, JsonElement> entry = it.next();

						String key = entry.getKey();

						JsonElement value = entry.getValue();

						switch (key) {
						case "1":
							this.word1 = value.getAsString();
							break;
							
						case "2":
							this.word2 = value.getAsString();
							break;
						case "3":
							this.word3 = value.getAsString();
							break;
						case "4":
							this.word4 = value.getAsString();
							break;
						case "5":
							this.word5 = value.getAsString();
							break;
					
						}

						System.out.print(key + " : ");

						if (value.isJsonObject()) {

							System.out.println("★-->");

						}

						else {

							if (value.isJsonNull()) {

								System.out.println("NULL");

							}

							else {

								System.out.println(value.getAsString());

							}

						}
					}
					
//					future_logservice.Regstertable2(this.nitizi, this.aite,
//							this.place, this.old, this.ninzu, this.action,
//							this.important, this.opinion, this.user_name,
//							this.nitizi_end, this.group, this.interaction,
//							this.comentary, image);
					ArrayList recommendlist = new ArrayList();
					recommendlist=tasksyncronizeservice.recommendtask(word1,word2,word3,word4,word5);
					Map<String, String[]> map = new HashMap<String, String[]>();
					map.put("word1", (String[]) recommendlist.get(0));
					map.put("word2", (String[]) recommendlist.get(1));
					map.put("word3", (String[]) recommendlist.get(2));
					map.put("word4", (String[]) recommendlist.get(3));
					map.put("word5", (String[]) recommendlist.get(4));
					Gson g = new Gson();
					return g.toJson(map);
					
				}
			} catch (JsonSyntaxException e) {

			}

			// future_logservice関数

		}
		
	
		
		
		
		
		//
		return "";
	}
	
	@RequestMapping(value = "/taskcollaborative", method = RequestMethod.POST)
	@ResponseBody
	public String collabotask(String sample, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		ArrayList list = new ArrayList();
		list = tasksyncronizeservice.collaborativetaskall();
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("level", (String[]) list.get(0));
		map.put("id", (String[]) list.get(1));
		map.put("create_time", (String[]) list.get(2));
		map.put("lat", (String[]) list.get(3));
		map.put("lng", (String[]) list.get(4));
		map.put("place", (String[]) list.get(5));
		map.put("title", (String[]) list.get(6));
		map.put("update_time", (String[]) list.get(7));
		// map.put("author_id",(String[]) list.get(8));
		// map.put("language_id",(String[]) list.get(9));
		map.put("location_base", (String[]) list.get(10));
		Gson g = new Gson();

		return g.toJson(map);
	}
	//
	// @RequestMapping(value = "/taskscript", method = RequestMethod.POST)
	// @ResponseBody
	// public String sample(String sample, ModelMap model, HttpServletRequest
	// request) {
	// model.clear();
	// ArrayList list=new ArrayList();
	// list=taskscriptsyncronizeservice.Info();
	// Map<String,String[]> map = new HashMap<String,String[]>();
	// map.put("id",(String[]) list.get(0));
	// map.put("lat",(String[]) list.get(1));
	// map.put("lng",(String[]) list.get(2));
	// map.put("num",(String[]) list.get(3));
	// map.put("script",(String[]) list.get(4));
	// map.put("task_id",(String[]) list.get(5));
	//
	// Gson g = new Gson();
	//
	// return g.toJson(map);
	// }
	//
	//
}
