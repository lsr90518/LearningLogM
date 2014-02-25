package jp.ac.tokushima_u.is.ll.controller;

import java.util.HashMap;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.entity.Userinfo;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ItemSearchCondForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.RecommendService;
import jp.ac.tokushima_u.is.ll.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/recommend")
public class RecommendController {
	
	@Autowired
	RecommendService recommendService;
	@Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
	
	
	@RequestMapping("/{nickname}/getSuggestion")
	public String GetSuggestion(@PathVariable String nickname, ModelMap model){
		
		List<Userinfo> userList = recommendService.findUserByNickname(nickname);
		if(userList.size() < 1){
			System.out.println("donnot exist!");
			return "/";
		} else {
			String nati = userList.get(0).getNatilanguage();
			String modelNo = userList.get(0).getModel();
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("natilanguage", nati);
			params.put("modelNo", modelNo);
			
			//find similar users
			List<Userinfo> similarUserList = recommendService.findUsersByNatiModel(params);
			
			//find learning content
			List<HashMap<String, Object>> similarContents = recommendService.findContentByUsers(params);
			
//			
//			System.out.println(similarContents);
//			System.out.println(similarUserList);
			model.addAttribute("similarContent", similarContents);
			model.addAttribute("similarUserList", similarUserList);
			
			Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
			userService.addExperiencePoint(user.getId(), 0);

			// ■wakebe 次のレベルまでの経験値取得
			model.addAttribute("nextExperiencePoint", this.userService.getNextExperiencePoint(user.getId()));

			// ■wakebe 現在の合計経験値取得
			model.addAttribute("nowExperiencePoint", this.userService.getNowExperiencePoint(user.getId()));
			
			model.addAttribute("user", user);
			
			return "recommend/frequency";
//			return "recommend/imageTest";
		}
		
	}

}
