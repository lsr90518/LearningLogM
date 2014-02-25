package jp.ac.tokushima_u.is.ll.controller.pacall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.tokushima_u.is.ll.entity.PacallCollection;
import jp.ac.tokushima_u.is.ll.entity.PacallGps;
import jp.ac.tokushima_u.is.ll.entity.PacallPhoto;
import jp.ac.tokushima_u.is.ll.entity.PacallSensor;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.PacallCollectionService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallProcessService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallUploadService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
@RequestMapping("/pacall/collection/{collectionId}/photos")
public class PacallPhotosController {
	private static final Logger logger = LoggerFactory.getLogger(PacallPhotosController.class); 
	
	@Autowired
	private PacallService pacallService;
	@Autowired
	private PacallProcessService pacallProcessService;
	@Autowired
	private PacallCollectionService pacallCollectionService;
	@Autowired
	private PacallUploadService pacallUploadService;

	@RequestMapping(method = RequestMethod.GET)
	public String show(@PathVariable String collectionId, Integer page, String tag, Model model){
		if(StringUtils.isBlank(collectionId)){
			return "redirect:/pacall";
		}
		int[] status = pacallProcessService.findProcessStatus(SecurityUserHolder.getCurrentUser().getId());
		if(status!=null){
			return "pacall/process_waiting";
		}
		if(StringUtils.isBlank(tag)){
			tag = "all";
		}
		model.addAttribute("tag", tag);
		
		Users user = SecurityUserHolder.getCurrentUser();
		PacallCollection collection = pacallCollectionService.findById(collectionId);
		if(collection==null || !collection.getUserId().equals(user.getId())){
			return "redirect:/pacall";
		}
		
		List<PacallSensor> sensors = pacallService.findSensorFilesByCollectionId(collectionId);
		model.addAttribute("sensorFiles", sensors);
		
		//Add gps files
		List<PacallGps> gpsfiles = pacallService.findGpsFileByCollectionId(collectionId);
		model.addAttribute("gpsFiles", gpsfiles);
		
		Page<PacallPhoto> list = pacallService.findPhotosByCollctionId(collectionId, page, tag, user.getId());
		model.addAttribute("result", list);
		
		model.addAttribute("tagnum", pacallService.findTagsAndNumber(collectionId));
//		model.addAttribute("extraInfo", pacallService.addExtraInfoToPhoto(list.getContent()));
//		Map<String, PacallTagCount> tagCount = this.pacallService.findTagsAndNumber(collectionId);
//		model.addAttribute("recommended", tagCount.get("recommended"));
//		model.addAttribute("all", tagCount.get("all"));
//		model.addAttribute("normal", tagCount.get("normal"));
//		tagCount.remove("recommended");
//		tagCount.remove("all");
//		tagCount.remove("normal");
//		model.addAttribute("tagCounts", tagCount);
//		if(StringUtils.isNotBlank(tag)){
//			model.addAttribute("pageParam", "&tag="+tag);
//		}
		model.addAttribute("collectionId", collectionId);
		
		List<Map<String, Object>> locations = new ArrayList<Map<String, Object>>();
		for(PacallPhoto photo: list){
			if(photo.getLat()==null || photo.getLng()==null)continue;
			Map<String, Object> location = new HashMap<String, Object>();
			location.put("lat", photo.getLat());
			location.put("lng", photo.getLng());
			locations.add(location);
		}
		model.addAttribute("locations", new Gson().toJson(locations));
		
		return "pacall/photos";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String upload(@PathVariable String collectionId, MultipartFile file){
		Users user = SecurityUserHolder.getCurrentUser();
		PacallCollection collection = pacallCollectionService.findById(collectionId);
		if(collection==null || !collection.getUserId().equals(user.getId())){
			return "collectionId error";
		}

		int[] status = pacallProcessService.findProcessStatus(SecurityUserHolder.getCurrentUser().getId());
		if(status!=null){
			return "pacall/process_waiting";
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> errors = new ArrayList<String>();
		result.put("errors", errors);
		Gson gson = new Gson();
		
		try {
			String fileName = file.getOriginalFilename();
			if(fileName == null) throw new Exception("File name is empty.");
			return pacallUploadService.uploadPhoto(file.getBytes(),FilenameUtils.getBaseName(file.getOriginalFilename()), FilenameUtils.getExtension(file.getOriginalFilename()),  SecurityUserHolder.getCurrentUser().getId(), collectionId);
		} catch (Exception e) {
			logger.debug("Error when uploading file", e);
			errors.add(e.getMessage());
		}
		return gson.toJson(result);
	}
}
