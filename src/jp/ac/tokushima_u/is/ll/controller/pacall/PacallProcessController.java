package jp.ac.tokushima_u.is.ll.controller.pacall;

import jp.ac.tokushima_u.is.ll.entity.PacallCollection;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.PacallCollectionService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallProcessService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallService;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/pacall/process")
public class PacallProcessController {
	
	@Autowired
	private PacallProcessService pacallProcessService;
	@Autowired
	private PacallService pacallService;
	@Autowired
	private PacallCollectionService pacallCollectionService;
	
	@RequestMapping("/status/{collectionId}")
	@ResponseBody
	public String status(@Param("collectionId") String collectionId){
		return String.valueOf(pacallProcessService.findProcessStatus(collectionId));
	}
	
	@RequestMapping("/{collectionId}")
	@ResponseBody
	public String process(@PathVariable String collectionId){
		
		String userId = SecurityUserHolder.getCurrentUser().getId();
		PacallCollection collection = pacallCollectionService.findById(collectionId);
		if(collection == null || !userId.equals(collection.getUserId())){
			return "no_auth";
		}
		
		int[] status = pacallProcessService.findProcessStatus(collectionId);
		if(status!=null){
			return String.valueOf(status);
		}
		pacallProcessService.startProcess(collectionId);
		return String.valueOf(pacallProcessService.findProcessStatus(collectionId));
	}
}
