package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class OCR {
	private final String LANG_OPTION = "-l";  //英文字母小写l，并非数字1
	private final String EOL = System.getProperty("line.separator");
	private String tessPath = null;
	
	public OCR(String tessPath){
		this.tessPath = tessPath;
	}
	
	public String recognizeText(File file, String lang)throws Exception{
//		File tempImage = ImageIOHelper.createImage(imageFile,imageFormat);
		File outputFile = new File(file.getParentFile(),"output");
		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();
		cmd.add(tessPath);
		cmd.add("");
		cmd.add(outputFile.getAbsolutePath());
		cmd.add(LANG_OPTION);
		cmd.add(lang);
		//cmd.add("eng");
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(file.getParentFile());
		
		cmd.set(1, file.getName());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		
		Process process = pb.start();
		//tesseract.exe 1.jpg 1 -l chi_sim
		int w = process.waitFor();
		
		//删除临时正在工作文件
		
		if(w==0){
			try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath()+".txt"),"UTF-8"))){
				String str;
				while((str = in.readLine())!=null){
					strB.append(str).append(EOL);
				}
			}
		}else{
			String msg;
			switch(w){
				case 1:
					msg = "Errors accessing files.There may be spaces in your image's filename.";
					break;
				case 29:
					msg = "Cannot recongnize the image or its selected region.";
					break;
				case 31:
					msg = "Unsupported image format.";
					break;
				default:
					msg = "Errors occurred.";
			}
			throw new RuntimeException(msg);
		}
		new File(outputFile.getAbsolutePath()+".txt").delete();
		return strB.toString();
	}
	
	public static void main(String[] args) {  
        String path = "/Users/houbin/Desktop/testphotos/_detection.png";     
        try {     
            String valCode = new OCR("/usr/").recognizeText(new File(path), "chi_sim");     
            System.out.println(valCode);     
        } catch (IOException e) {     
            e.printStackTrace();     
        } catch (Exception e) {  
            e.printStackTrace();  
        }      
    }  
}
