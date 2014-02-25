package jp.ac.tokushima_u.is.ll.controller.pacall;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.ac.tokushima_u.is.ll.entity.PacallCollection;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.service.ImageIndexService;
import jp.ac.tokushima_u.is.ll.service.PacallCollectionService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallProcessService;
import jp.ac.tokushima_u.is.ll.service.pacall.PacallUploadService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
@RequestMapping("/pacall/collection")
public class PacallCollectionController {
	
	private static final Logger logger = LoggerFactory.getLogger(PacallCollectionController.class); 

	
	@Autowired
	private PacallCollectionService pacallCollectionService;
	@Autowired
	private PacallUploadService pacallUploadService;
	@Autowired
	private PacallProcessService pacallProcessService;
	@Autowired
	private ImageIndexService imageIndexService;


	@RequestMapping(method= RequestMethod.POST)
	@ResponseBody
	public String create(String name){
		if(StringUtils.isBlank(name))return null;
		Users user = SecurityUserHolder.getCurrentUser();
		pacallCollectionService.createCollection(name, user.getId());
		return "success";
	}
	
	@RequestMapping("/reindexAllIndex")
	@ResponseBody
	public String reindex() throws IOException{
		imageIndexService.recreateIndex();
		return "success";
	}
	
	@RequestMapping(value="/{collectionId}/gps", method = RequestMethod.POST)
	@ResponseBody
	public String uploadGPS(@PathVariable String collectionId, MultipartFile file, Model model){
		
		Users user = SecurityUserHolder.getCurrentUser();
		PacallCollection collection = pacallCollectionService.findById(collectionId);
		if(collection==null || !collection.getUserId().equals(user.getId())){
			return "collectionId error";
		}
		
		int[] status = pacallProcessService.findProcessStatus(collectionId);
		if(status!=null){
			return "process_waiting";
		}
		
		String fileName = null;
		if(file!=null){
			fileName = file.getOriginalFilename();
		}
		if(file == null || file.isEmpty() || fileName==null || file.isEmpty()){
			model.addAttribute("error", "File is empty");
			return "file_empty";
		}
		try {
		
			byte[] b = file.getBytes();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)));
				String string = reader.readLine();
				if(!string.startsWith("@Sonygps")){
					model.addAttribute("error", "This file is not supported");
					return "file_not_supported";
				}
			} catch (IOException e) {
				logger.error("IOException", e);
				return e.getMessage();
			}
			pacallUploadService.uploadGps(b, FilenameUtils.getBaseName(file.getOriginalFilename()), FilenameUtils.getExtension(file.getOriginalFilename()), SecurityUserHolder.getCurrentUser().getId(), collectionId);
			model.addAttribute("info", "Upload Successful");
			return "success";
		} catch (IOException e) {
			model.addAttribute("error", "Error when uploading file");
			logger.error("Error when uploading file", e);
			return e.getMessage();
		}
	}
		
	@RequestMapping(value="/{collectionId}/sensor", method = RequestMethod.POST)
	@ResponseBody
	public String uploadSensor(@PathVariable String collectionId, MultipartFile file, Model model){
		Users user = SecurityUserHolder.getCurrentUser();
		PacallCollection collection = pacallCollectionService.findById(collectionId);
		if(collection==null || !collection.getUserId().equals(user.getId())){
			return "collectionId error";
		}
		
		int[] status = pacallProcessService.findProcessStatus(collectionId);
		if(status!=null){
			return "pacall/process_waiting";
		}
		
		String fileName = null;
		if(file!=null){
			fileName = file.getOriginalFilename();
		}
		if(file == null || file.isEmpty() || fileName==null || file.isEmpty()){
			model.addAttribute("error", "File is empty");
		}
		
		if(fileName.toLowerCase().endsWith("sensor.csv")){
			try {
				byte[] b = file.getBytes();
				pacallUploadService.uploadSensorCsv(b, FilenameUtils.getBaseName(file.getOriginalFilename()), FilenameUtils.getExtension(file.getOriginalFilename()), SecurityUserHolder.getCurrentUser().getId(), collectionId);
				model.addAttribute("info", "Upload Successful");
			} catch (IOException e) {
				model.addAttribute("error", "Error when uploading file");
				e.printStackTrace();
				logger.error("Error when uploading file", e);
			}
		}else{
			model.addAttribute("error", "The uploaded file is not a SenseCam SENSOR.CSV file");
		}
		
		return "success";
	}
	
	@RequestMapping(value="/{collectionId}", params="_method=DELETE")
	@ResponseBody
	public String delete(@PathVariable String collectionId){
		this.pacallCollectionService.delete(collectionId);
		return "success";
	}
	
	@RequestMapping(value="/{collectionId}/status")
	@ResponseBody
	public String status(@PathVariable String collectionId){
		int[] status = pacallProcessService.findProcessStatus(collectionId);
		if(status == null) status = new int[]{};
		return new Gson().toJson(status);
	}
}
