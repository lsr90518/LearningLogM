package jp.ac.tokushima_u.is.ll.controller.pacall;

import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 记录用户使用情况
 * @author houbin
 *
 */
@Controller
@RequestMapping("/pacalllog")
public class PacallLogController {
	
	@Autowired
	private PacallLogService pacallLogService;
	
	@RequestMapping("/uploadstarted")
	@ResponseBody
	public String uploadStarted(String collectionId){
		pacallLogService.uploadStarted(collectionId);
		return "success";
	}
	
	@RequestMapping("/uploadfinished")
	@ResponseBody
	public String uploadFinished(String collectionId){
		pacallLogService.uploadFinished(collectionId);
		return "success";
	}
	
	@RequestMapping("/active")
	@ResponseBody
	public String updateUserActiveStatus(){
		String userId = SecurityUserHolder.getCurrentUser().getId();
		pacallLogService.updateUserActiveStatus(userId);
		return "success";
	}
	
}
