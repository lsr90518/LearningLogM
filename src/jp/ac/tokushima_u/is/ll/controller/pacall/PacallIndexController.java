package jp.ac.tokushima_u.is.ll.controller.pacall;

import jp.ac.tokushima_u.is.ll.dto.PacallCollectionDTO;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.PacallCollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pacall")
public class PacallIndexController {
	@Autowired
	private PacallCollectionService pacallCollectionService;
	
	@RequestMapping(value={"", "/", "/index"})
	public String index(ModelMap model){
		Users user = SecurityUserHolder.getCurrentUser();
		Page<PacallCollectionDTO> list = pacallCollectionService.findListDTOByUserId(user.getId(), 1);
		model.addAttribute("result", list);
		return "pacall/collection";
	}
}
