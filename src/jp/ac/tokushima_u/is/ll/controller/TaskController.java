package jp.ac.tokushima_u.is.ll.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.ItemTitle;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Task;
import jp.ac.tokushima_u.is.ll.entity.TaskItem;
import jp.ac.tokushima_u.is.ll.entity.TaskScript;
import jp.ac.tokushima_u.is.ll.entity.Taskcollaborative;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.exception.NotFoundException;
import jp.ac.tokushima_u.is.ll.form.ItemEditForm;
import jp.ac.tokushima_u.is.ll.form.ItemSearchCondForm;
import jp.ac.tokushima_u.is.ll.form.TaskEditForm;
import jp.ac.tokushima_u.is.ll.form.TaskScriptForm;
import jp.ac.tokushima_u.is.ll.form.validator.TaskEditFormValidator;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.LanguageService;
import jp.ac.tokushima_u.is.ll.service.TaskScriptService;
import jp.ac.tokushima_u.is.ll.service.TaskService;
import jp.ac.tokushima_u.is.ll.service.UserService;
import jp.ac.tokushima_u.is.ll.util.JapaneseWordlistUtil;
import jp.ac.tokushima_u.is.ll.ws.service.model.ItemForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private UserService userService;

	@Autowired
	private LanguageService languageService;
	@Autowired
	private ItemService itemService;

	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskScriptService taskscriptService;
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String create(@ModelAttribute("task") TaskEditForm form,
			BindingResult result, ModelMap model) {
		new TaskEditFormValidator().validate(form, result);
		Task task = null;

		try {
			task = this.taskService.createTask(form);
		} catch (NotFoundException nfe) {
			result.reject("languageId", nfe.getMessage() + " is not found.");
		}

		if (result.hasErrors()) {
			return this.add(form, model);
		}
		if (task != null && task.getId() != null) {
			return "redirect:/task/" + task.getId() + "/addscript";
		} else
			return this.add(form, model);
	}
	
	
	
	

	@RequestMapping
	public String index(@ModelAttribute("searchCond") TaskEditForm form,
			ModelMap model) {

		model.addAttribute("title", form);
		return "item/taskitem";
	}

	@RequestMapping(value = "/searchItem", method = RequestMethod.GET)
	@ResponseBody
	public String test(String queryvalue, String taskId, Integer num,
			ModelMap model, HttpServletResponse response) {
		model.clear();
		// List<Item> items = itemService.searchRelatedItemForTask(taskId,
		// queryvalue, num);
		List<ItemTitle> itemtitles = itemService.searchRelated(taskId,
				queryvalue, num);

		List<Item> items = itemService.searchRelatedItemForTask(taskId,
				queryvalue, num);

		Map<String, String[]> map = new HashMap<String, String[]>();

		Map<String, String[]> map2 = new HashMap<String, String[]>();
		Map<String, List> map3 = new HashMap<String, List>();
		ArrayList list = new ArrayList();
		Gson g = new Gson();
		try {
			Item item = new Item();
			Iterator<Item> it2 = items.iterator();
			String[] item_id = new String[items.size()];
			String[] item_content = new String[items.size()];

			int i = 0;
			while (it2.hasNext()) {
				// Listから要素を取り出す
				Item element = it2.next();
				item_id[i] = String.valueOf(element.getId());

				i++;
			}

			ItemTitle title = new ItemTitle();
			Iterator<ItemTitle> it = itemtitles.iterator();
			String[] title_id = new String[itemtitles.size()];
			String[] title_content = new String[itemtitles.size()];
			String[] title_item = new String[itemtitles.size()];
			String[] title_language = new String[itemtitles.size()];

			int i2 = 0;

			while (it.hasNext()) {
				// Listから要素を取り出す
				ItemTitle element = it.next();
				title_id[i2] = String.valueOf(element.getId());
				title_content[i2] = String.valueOf(element.getContent());
				title_item[i2] = String.valueOf(element.getItem());
				title_language[i2] = String.valueOf(element.getLanguage());

				i2++;
			}

			for (int j = 0; j < item_id.length; j++) {
				for (int c = 0; c < title_item.length; c++) {
					if (item_id[j].equals(title_item[c])) {
						item_id[j] = title_item[c];
						item_content[j] = title_content[c];
					}

				}

			}

			// List<Item> all= new List;

			String[] a = { "test", "test2" };
			model.addAttribute("items", a);
			// }
			// model.addAttribute("items",items);

			map.put("item_id", item_id);
			map2.put("content", item_content);
			// list.add(item_id);
			list.add(map);
			list.add(map2);
			// Gson g = new Gson();
			map3.put("items", list);
			System.out.println(g.toJson(list));
			System.out.println(g.toJson(map3));
		} catch (ArrayIndexOutOfBoundsException e) {

		}
		return g.toJson(map3);

	}

	@RequestMapping(value = "/{id}/additem", method = RequestMethod.POST)
	@ResponseBody
	public String addRelatedItem(@PathVariable String id, String itemIds,
			ModelMap model) {

		this.taskService.addTaskItems(id, itemIds);

		return "";
	}

	@RequestMapping(value = "/{id}/removeitem", method = RequestMethod.POST)
	@ResponseBody
	public String removeRelatedItem(@PathVariable String id, String itemIds) {

		this.taskService.removeTaskItems(id, itemIds);
		// try{
		// this.taskService.removeTaskItems(id, itemIds);
		// }catch(NotFoundException e){
		//
		// }
		return null;
	}
	
	@RequestMapping(value = "/{id}/additemcollaborative", method = RequestMethod.POST)
	@ResponseBody
	public String addRelatedItemcollaborative(@PathVariable String id, String itemIds,
			ModelMap model) {

		this.taskService.addTaskItemscollaborative(id, itemIds);

		return "";
	}

	@RequestMapping(value = "/{id}/removeitemcollaborative", method = RequestMethod.POST)
	@ResponseBody
	public String removeRelatedItemcollaborative(@PathVariable String id, String itemIds) {

		this.taskService.removeTaskItemscollaborative(id, itemIds);
		// try{
		// this.taskService.removeTaskItems(id, itemIds);
		// }catch(NotFoundException e){
		//
		// }
		return null;
	}

	@RequestMapping(value = "/{id}/items", method = RequestMethod.GET)
	@ResponseBody
	public String getRelatedItem(@PathVariable String id, ModelMap model) {
		// try{
		Map<String, String[]> map = new HashMap<String, String[]>();

		Map<String, String[]> map2 = new HashMap<String, String[]>();
		Map<String, List> map3 = new HashMap<String, List>();
		ArrayList list = new ArrayList();
		List<TaskItem> taskItems = this.taskService.findTaskItems(id);
		List<ItemTitle> itemtitles = this.itemService.findContentAndItemId(id);

		Iterator<ItemTitle> it2 = itemtitles.iterator();
		String[] item_id = new String[itemtitles.size()];
		String[] item_content = new String[itemtitles.size()];

		int i = 0;
		while (it2.hasNext()) {
			// Listから要素を取り出す
			ItemTitle element = it2.next();
			item_id[i] = String.valueOf(element.getItem());
			item_content[i] = String.valueOf(element.getContent());
			i++;
		}

		map.put("item_id", item_id);
		map2.put("content", item_content);
		// list.add(item_id);
		list.add(map);
		list.add(map2);
		// Gson g = new Gson();
		map3.put("items", list);
		Gson g = new Gson();
		// List<ItemForm> items = new ArrayList<ItemForm>();
		// for(TaskItem ti:taskItems){
		// ItemForm itemform = new ItemForm(ti.getItem());
		// items.add(itemform);
		// }
		// model.addAttribute("items", items);
		// }catch(NotFoundException e){
		// model.put("result", "failed");
		// }
		//
		// model.put("result", "success");

		return g.toJson(map3);
	}

	@RequestMapping(value = "/{id}/publish", method = RequestMethod.POST)
	public String publish(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form, ModelMap model,
			BindingResult result) {

		Task task = null;
	
			task = this.taskService.findTaskById(id);
	
		Integer stepNum = 1;

		// if (task != null) {
		// List<TaskScript> scripts = this.taskService.findTaskScriptList(
		// task.getId(), false);
		// // if (scripts != null && scripts.size() > 0)
		// // stepNum = scripts.size() + 1;
		// // form.setTaskId(id);
		// model.addAttribute("taskscripts", scripts);
		// }

		// scripts analytics

		List<TaskScript> scripts = this.taskscriptService.findTaskScript(id);
		int level = taskService.resetLevel(scripts);
		task.setLevel(level);
		taskService.updateLevelById(id, level);
		model.addAttribute("task", task);
		model.addAttribute("taskscript", scripts);

		return "task/publish";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(@ModelAttribute("task") TaskEditForm form, ModelMap model) {
		Users user = userService.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		model.addAttribute("user", user);

		List<Language> langs = new ArrayList<Language>();
		// for(Language lang: user.getMyLangs()){
		// if(!langs.contains(lang))langs.add(lang);
		// }
		for (Language lang : this.languageService
				.findStudyLangs(SecurityUserHolder.getCurrentUser().getId())) {
			if (!langs.contains(lang))
				langs.add(lang);
		}
		model.addAttribute("langs", langs);

		return "task/add";
	}

	

	@RequestMapping(value = "/{id}/addscript", method = RequestMethod.GET)
	public String getScriptForm(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form,
			BindingResult result, ModelMap model) {
		Task task = null;
		
			task = this.taskService.findTaskById(id);
		
			
		

		Integer stepNum = 1;

		if (task != null) {
			List<TaskScript> scripts = this.taskService.findTaskScriptList(
					task.getId(), false);
			if (scripts != null && scripts.size() > 0)
				stepNum = scripts.size() + 1;
			form.setTaskId(id);
			model.addAttribute("task", task);
			model.addAttribute("taskscripts",stepNum);
		}

		form.setNum(stepNum);
		return "task/addstep";
	}
	



	@RequestMapping(value = "/{id}/preview", method = RequestMethod.GET)
	public String preview(@PathVariable String id, ModelMap model) {
		Task task = null;
		
			task = this.taskService.findTaskById(id);
		

		if (task != null) {
			List<TaskScript> scripts = this.taskService.findTaskScriptList(
					task.getId(), false);
			model.addAttribute("task", task);
			model.addAttribute("scripts", scripts);
		}
		return "task/preview";
	}

	@RequestMapping(value = "/{id}/addscript", method = RequestMethod.POST)
	public String addScript(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form,
			BindingResult result, ModelMap model) {
		try {
			this.taskService.createTaskScript(form);
		} catch (IOException ioe) {
			result.reject("image", "File error");
		} catch (NotFoundException nfe) {
			result.reject("title", nfe.getMessage() + " is not found");
		}

		if (!result.hasErrors()) {
			form.clear();
		}
		return this.getScriptForm(id, form, result, model);
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public String edit(@PathVariable String id,
			@ModelAttribute("task") TaskEditForm form,
			@ModelAttribute("taskscript") TaskScriptForm form1, ModelMap model)
			throws NotFoundException {
		Task task = this.taskService.findTaskById(id);

		model.addAttribute("tasksingleitem", task);
		List<TaskScript> scripts = this.taskscriptService.findTaskScript(id);

		Iterator<TaskScript> it = scripts.iterator();
		String[] content = new String[scripts.size()];
		String[] image_id = new String[scripts.size()];
		int i = 0;
		while (it.hasNext()) {
			// Listから要素を取り出す
			TaskScript element = it.next();

			if (String.valueOf(element.getNum()).equals("1")) {
				form1.setProcess1(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("2")) {
				form1.setProcess2(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("3")) {
				form1.setProcess3(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("4")) {
				form1.setProcess4(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("5")) {
				form1.setProcess5(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("6")) {
				form1.setProcess6(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("7")) {
				form1.setProcess7(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("8")) {
				form1.setProcess8(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("9")) {
				form1.setProcess9(String.valueOf(element.getScript()));
			}
			if (String.valueOf(element.getNum()).equals("10")) {
				form1.setProcess10(String.valueOf(element.getScript()));
			}

			i++;
		}

		model.addAttribute("taskscriptallitem", scripts);

		// form1.setProcess1("test");
		model.addAttribute("form", form1);
		return "task/edit";
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
	public String update(@PathVariable String id,
			@ModelAttribute("task") TaskEditForm form,
			@ModelAttribute("taskscript") TaskScriptForm form1,
			BindingResult result, ModelMap model) {
		form.setTaskId(id);
		this.taskService.update(form);
		form1.setTaskId(id);
		if (form1.getProcess1() != null) {
			this.taskService.update2(form1);
		}
		if (form1.getProcess2() != null) {
			this.taskService.update3(form1);
		}
		if (form1.getProcess3() != null) {
			this.taskService.update4(form1);
		}
		if (form1.getProcess4() != null) {
			this.taskService.update5(form1);
		}
		if (form1.getProcess5() != null) {
			this.taskService.update6(form1);
		}
		if (form1.getProcess6() != null) {
			this.taskService.update7(form1);
		}
		if (form1.getProcess7() != null) {
			this.taskService.update8(form1);
		}
		if (form1.getProcess8() != null) {
			this.taskService.update9(form1);
		}
		if (form1.getProcess9() != null) {
			this.taskService.update10(form1);
		}
		if (form1.getProcess10() != null) {
			this.taskService.update11(form1);
		}
		// this.taskService.update2(form1);

		return "redirect:/task/" + id;
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
	public String delete(@PathVariable String id,@ModelAttribute("task") TaskEditForm form,
			ModelMap model) {
		
		this.taskService.deletetask(id);
		// Item item = itemService.findById(id);
		// if (item == null ||
		// (!SecurityUserHolder.getCurrentUser().getId().equals(item.getAuthor().getId())
		// &&
		// !SecurityUserHolder.getCurrentUser().getAuth().equals(Users.UsersAuth.ADMIN)))
		// {
		// return "redirect:/item";
		// }
		// this.itemService.delete(item);
		return this.taskitem(form, model);
	}

	@RequestMapping(value = "/topmenu", method = RequestMethod.GET)
	public String topmenu(@ModelAttribute("task") TaskEditForm form,
			ModelMap model) {

		return "task/topmenu";
	}

	@RequestMapping(value = "/topmenu", method = RequestMethod.POST)
	public String topmenu2(@ModelAttribute("task") TaskEditForm form,
			ModelMap model) {

		return "task/topmenu";
	}

	@RequestMapping(value = "/taskitem", method = RequestMethod.GET)
	public String taskitem(@ModelAttribute("task") TaskEditForm form,
			ModelMap model) {

		List<Task> task = this.taskService.GetTaskAll();
		
		
		Page<Task> task2= this.taskService.searchtaskpage(form);
		
		 model.addAttribute("itemPage", task2);

		model.addAttribute("taskitems", task);
		model.addAttribute("title", form);
		
		List<Taskcollaborative> taskcollaborative = this.taskService.GetcollaborativeTaskAll();

		model.addAttribute("collaborativetaskitems", taskcollaborative);
		model.addAttribute("collaborativetitle", form);
		
		return "task/taskitem";
	}

	@RequestMapping(value = "/taskitem", method = RequestMethod.POST)
	public String taskitem2(@ModelAttribute("task") TaskEditForm form,
			ModelMap model) {

		List<Task> task;
		List<Taskcollaborative> taskcollaborative;
		Page<Task> task2;
		
		

		// model.addAttribute("title",form.getTitle());
		//
		// model.addAttribute("place",form.getPlace());
		// model.addAttribute("level",form.getLevel());
		int count = form.getTitle().length();
		int count2 = form.getPlace().length();

		if (form.getTitle().length() == 0 && form.getPlace().length() == 0
				&& form.getLevel() == null) {

			task = this.taskService.GetTaskAll();
			task2= this.taskService.searchtaskpage(form);

			taskcollaborative=this.taskService.GetcollaborativeTaskAll();
		} else {

			String level = String.valueOf(form.getLevel());
			if (level.equals("null")) {
				level = "";
			}
			task = this.taskService.SerachTaskAll(form.getTitle(),
					form.getPlace(), level);
			task2= this.taskService.searchtaskpageselect(form,form.getTitle(),
					form.getPlace(), level);
			taskcollaborative = this.taskService.SerachTaskcollaborativeAll(form.getTitle(),
					form.getPlace(), level);
		}
		 model.addAttribute("itemPage", task2);
		model.addAttribute("taskitem", task);
		model.addAttribute("taskcollaborativeitems", taskcollaborative);
		model.addAttribute("collaborativetitle", form);

		return "task/taskitem";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String viewtask(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form, ModelMap model,
			BindingResult result) throws NotFoundException {

		Task task = this.taskService.findTaskById(id);
		model.addAttribute("tasksingleitem", task);
		if(task==null){
			Taskcollaborative taskcollaborative=this.taskService.findcollaborativetask(id);
			model.addAttribute("tasksingleitem", taskcollaborative);
		}

		List<TaskScript> scripts = this.taskscriptService.findTaskScript(id);
		model.addAttribute("taskscriptallitem", scripts);
		
		if(scripts.size()==0){
			List<TaskScript> script = this.taskscriptService.findTaskScriptcollaborate(id);
			model.addAttribute("taskscriptallitem", script);
			
		}
		return "task/show";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String viewtask2(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form, ModelMap model,
			BindingResult result) {

		return "task/show";
	}

	
	
	//Collaborative learning task based programming code
	
	@RequestMapping(value = "/collaborative_add", method = RequestMethod.GET)
	public String collaborativeadd(@ModelAttribute("collaborativetask") TaskEditForm form, ModelMap model) {
		Users user = userService.findById(SecurityUserHolder.getCurrentUser()
				.getId());
		model.addAttribute("user", user);

		List<Language> langs = new ArrayList<Language>();
		// for(Language lang: user.getMyLangs()){
		// if(!langs.contains(lang))langs.add(lang);
		// }
		for (Language lang : this.languageService
				.findStudyLangs(SecurityUserHolder.getCurrentUser().getId())) {
			if (!langs.contains(lang))
				langs.add(lang);
		}
		model.addAttribute("langs", langs);

		return "task/collaborative_add";
	}

//	@RequestMapping(value = "/collaborative_add",method = RequestMethod.POST)
//	public String collaborativeadd(@ModelAttribute("task") TaskEditForm form,
//			BindingResult result, ModelMap model) {
//	
//		
//		return "task/collaborative_add";
//	}
	@RequestMapping(value = "/collaborative_add",method = RequestMethod.POST)
	public String collaborative_create(@ModelAttribute("collaborativetask") TaskEditForm form,
			BindingResult result, ModelMap model) {
		new TaskEditFormValidator().validate(form, result);
		Taskcollaborative task = null;

		try {
			task = this.taskService.collaborative_createTask(form);
		} catch (NotFoundException nfe) {
			result.reject("languageId", nfe.getMessage() + " is not found.");
		}

		if (result.hasErrors()) {
			return this.add(form, model);
		}
		if (task != null && task.getId() != null) {
			return "redirect:/task/" + task.getId() + "/collaborative_addscript";
		} else
			return this.add(form, model);
	}
	
	@RequestMapping(value = "/{id}/collaborative_addscript", method = RequestMethod.GET)
	public String collaborative_getScriptForm(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form,
			BindingResult result, ModelMap model) throws NotFoundException{
		Taskcollaborative task = null;
		try {
			task = this.taskService.findTaskcollaborativeById(id);
		} catch (NotFoundException e) {
			result.reject("title", e.getMessage() + " is not found");
		}

		Integer stepNum = 1;

		if (task != null) {
			List<TaskScript> scripts = this.taskService.findTaskcollaborativeScriptList(
					task.getId(), false);
			if (scripts != null && scripts.size() > 0)
				stepNum = scripts.size() + 1;
			form.setTaskId(id);
			model.addAttribute("task", task);
		}

		form.setNum(stepNum);
		return "task/collaborative_addscript";
	}


	@RequestMapping(value = "/{id}/collaborative_addscript", method = RequestMethod.POST)
	public String collaborative_addScript(@PathVariable String id,
			@ModelAttribute("script") TaskScriptForm form,
			BindingResult result, ModelMap model) throws NotFoundException {
		try {
			this.taskService.createTaskcollaborateScript(form);
		} catch (IOException ioe) {
			result.reject("image", "File error");
		} catch (NotFoundException nfe) {
			result.reject("title", nfe.getMessage() + " is not found");
		}

		if (!result.hasErrors()) {
			form.clear();
		}
		return this.collaborative_getScriptForm(id, form, result, model);
	}

	
	
}
