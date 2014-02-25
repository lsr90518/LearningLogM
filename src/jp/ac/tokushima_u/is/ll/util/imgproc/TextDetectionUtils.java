package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;

public class TextDetectionUtils {
	
	
	public static String ocr(File file){
		try {
//			Tesseract instance = Tesseract.getInstance();
//			instance.setLanguage("jpn");
//			String jpn = instance.doOCR(file);
//			instance.setLanguage("eng");
//			String eng = instance.doOCR(file);
			OCR ocr = new OCR("/usr/local/bin/tesseract");
			String jpn = "";
			try {
				jpn = ocr.recognizeText(file, "jpn");
			} catch (Exception e) {
				e.printStackTrace();
				jpn = "";
			}
			String eng = "";
			try {
				eng = ocr.recognizeText(file, "eng");
			} catch (Exception e) {
				e.printStackTrace();
				eng = "";
			}
			
			String text = ""+jpn+eng;
			text = CharMatcher.WHITESPACE.removeFrom(text);
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<text.length();i++){
				char c = text.charAt(i);
				if(CharMatcher.JAVA_LETTER_OR_DIGIT.matches(c)){
					buffer.append(c);
				}
			}
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
			
	}
	
	public static List<String> splitToSegments(String text){
		List<String> result = new ArrayList<>();
		StringBuffer jpnBuffer = new StringBuffer();
		StringBuffer engBuffer = new StringBuffer();
		for(int i=0;i<text.length();i++){
			if(CharMatcher.ASCII.matches(text.charAt(i))){
				engBuffer.append(text.charAt(i));
			}else{
				jpnBuffer.append(text.charAt(i));
			}
		}
		String jpn = jpnBuffer.toString();
		String eng = engBuffer.toString();
		
		if(jpn.length()>1){
			for(int i=0;i<jpn.length()-1;i++){
				result.add(jpn.substring(i, i+2));
			}
		}
		
		if(eng.length()>2){
			for(int i=0;i<eng.length()-2;i++){
				result.add(eng.substring(i, i+3));
			}
		}
		
		Set<String> hashSet = new HashSet<>();
		for(String s: result){
			if(StringUtils.isNotBlank(s)){
				hashSet.add(s);
			}
		}
		result = new ArrayList<>();
		result.addAll(hashSet);
		return result;
	}
	
	public static void main(String[] args){
//		System.out.println(TextDetection.detect());
//		Tesseract1 instance = new Tesseract1();
//		instance.setLanguage("jpn");
//		try {
//			String abc = instance.doOCR(new File("/Users/houbin/Documents/Vicon Revue Data/453C8352-57E0-A6CD-A037-1A0760045495/B000096f00000018f20110823074828F.JPG"));
//			System.out.println(abc);
//			instance.setLanguage("eng");
//			String cde = instance.doOCR(new File("/Users/houbin/Documents/Vicon Revue Data/453C8352-57E0-A6CD-A037-1A0760045495/B000096f00000018f20110823074828F.JPG"));
//			System.out.println(abc);
//		} catch (TesseractException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(TextDetectionUtils.splitToSegments("12322こんんちは、ほよあ"));
		
	}
}
