package jp.ac.tokushima_u.is.ll.controller.pacall;

import java.util.ArrayList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.Item;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ItemService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/pacall/item_upload")
public class PacallItemUploadController {
	
	@Autowired
	private PacallService pacallService;
	@Autowired
	private ItemService itemService;

	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public String show(@PathVariable String id, ModelMap model){
		PacallPhoto photo = pacallService.findById(id, SecurityUserHolder.getCurrentUser().getId());
		if(photo == null){
			return "redirect:/pacall/phase";
		}
		model.addAttribute("photo", photo);
		List<PacallPhoto> list = new ArrayList<PacallPhoto>();
		list.add(photo);
//		PacallExtraInfo extraInfo = pacallService.addExtraInfoToPhoto(list).get(photo.getId());
//		model.addAttribute("extraInfo", extraInfo);
//		String group = extraInfo.getGroupId();
//		if(group==null){
//			group = photo.getId();
//		}
//		model.addAttribute("duplicatedList", pacallService.findDuplicatedListByPhotoId(group));
		
		return "pacall/item_upload";
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public String upload(@PathVariable String id, String know, String what, ModelMap model){
		if(StringUtils.isBlank(id) || StringUtils.isBlank(what) || StringUtils.isBlank(know)){
			return null;
		}
		PacallPhoto photo = pacallService.findById(id, SecurityUserHolder.getCurrentUser().getId());
		if(photo == null){
			return "redirect:/pacall/phase";
		}
		Item item = itemService.uploadFromPacall(photo.getId());
		pacallService.recordUploadedPhoto(item.getId(), photo.getId(), Integer.valueOf(know), Integer.valueOf(what));
		return "redirect:/item/"+item.getId()+"/edit"; 
	}
}
