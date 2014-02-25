package jp.ac.tokushima_u.is.ll.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class UuidPathUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(UuidPathUtils.class);
	
	public static List<String> convertPaths(String filename){
		if(StringUtils.isBlank(filename) || filename.length()<32){
			logger.error("Filename error: "+filename);
			return Lists.newArrayList();
		}
		return Lists.newArrayList(Splitter.fixedLength(3).split(filename.substring(0, 32).toLowerCase()));
	}
	
	public static void main(String[] args){
		String filename = "00f63df662a64cd9b4324aad2116da67";
		String url = "http://ll.is.tokushima-u.ac.jp/static/learninglog_dev";
		List<String> paths = UuidPathUtils.convertPaths(filename);
		List<String> acc = new ArrayList<>();
		acc.add(url);
		acc.addAll(paths);
		acc.add(filename+"_320x240.png");
		String aurl = Joiner.on("/").join(acc);
		System.out.println(aurl);
		try {
			FileUtils.copyURLToFile(new URL(aurl), new File("/Users/houbin/Desktop/abc.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			InputStream is = new URL(aurl).openStream();
//			System.out.println(aurl);
//			FileUtils.copyInputStreamToFile(new BufferedInputStream(is), new File("/Users/houbin/Desktop/abc.png"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
}
