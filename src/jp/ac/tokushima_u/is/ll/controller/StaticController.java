//package jp.ac.tokushima_u.is.ll.controller;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import jp.ac.tokushima_u.is.ll.service.StorageService;
//import jp.ac.tokushima_u.is.ll.util.FileContentTypeUtil;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/static")
//public class StaticController {
//	
//	@Autowired
//	private StorageService storageService;
//	
//	@RequestMapping("/{filename}")
//	public void show(@PathVariable String filename, HttpServletResponse response, HttpServletRequest request) throws IOException{
//		if(filename == null){
//			return;
//		}
//		if(StringUtils.isBlank(FilenameUtils.getExtension(filename))){
//			filename+=".jpg";
//		}
//
//		File outputFile = storageService.prepareFile(filename);
//		if(outputFile == null || !outputFile.exists()) {
//			outputFile = FileUtils.getFile(request.getServletContext().getRealPath("/"), "images", "no_image.jpg");
//		}
//		response.setHeader("Cache-Control", "max-age=259200");
//		response.setContentType(FileContentTypeUtil.getMimeType(outputFile));
//		FileUtils.copyFile(outputFile, response.getOutputStream());
//		return;
//	}
//}
