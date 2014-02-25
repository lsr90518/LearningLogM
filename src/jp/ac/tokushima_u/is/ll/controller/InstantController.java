package jp.ac.tokushima_u.is.ll.controller;

import jp.ac.tokushima_u.is.ll.dto.ItemDTO;
import jp.ac.tokushima_u.is.ll.form.ItemSearchCondForm;
import jp.ac.tokushima_u.is.ll.service.ItemService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Instant Search by mobile devices, auto detect mobile
 * @author Houbin
 *
 */
@Controller
@RequestMapping("/instant")
public class InstantController {
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("/item")
	public String item(String qrcode){
		if(StringUtils.isBlank(qrcode)) return "redirect:/item";
		ItemSearchCondForm form = new ItemSearchCondForm();
		form.setQrcode(qrcode.trim());
		Page<ItemDTO> itemList = itemService.searchItemPageByCond(form);
		if(itemList==null || itemList.getTotalElements()==0) return "redirect:/item";
		if(itemList.getTotalElements()==1){
			return "redirect:/item/"+itemList.getContent().get(0).getId();
		}else{
			return "redirect:/item?qrcode="+qrcode;
		}
	}
}
