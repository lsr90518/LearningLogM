//package jp.ac.tokushima_u.is.ll.controller.api;
//
//import jp.ac.tokushima_u.is.ll.service.task.MongoDataMigrateService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//@RequestMapping("/api/mongodatamigratetask")
//public class MongoDataMigrateTaskController {
//	
//	@Autowired
//	private MongoDataMigrateService mongoDataMigrateService;
//	
//	@RequestMapping
//	@ResponseBody
//	public String execute(){
//		mongoDataMigrateService.execute();
//		return "success";
//	}
//}
