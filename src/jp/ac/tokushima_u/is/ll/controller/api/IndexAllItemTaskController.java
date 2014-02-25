package jp.ac.tokushima_u.is.ll.controller.api;

import jp.ac.tokushima_u.is.ll.service.task.IndexAllItemTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/indexallitemtask")
public class IndexAllItemTaskController {
	
	@Autowired
	private IndexAllItemTaskService indexAllItemTaskService;
	
	@RequestMapping
	@ResponseBody
	public String execute(){
		indexAllItemTaskService.execute();
		return "success";
	}
}
