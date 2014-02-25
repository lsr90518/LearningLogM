package jp.ac.tokushima_u.is.ll.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class SystemPathUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemPathUtils.class);
	
	public static void addJavaLibraryPath(String path){
		String syspath = System.getProperty("java.library.path");
		List<String> paths = Lists.newArrayList(syspath.split(File.pathSeparator));
		if(paths.contains(path)){
			return;
		}else{
			paths.add(path);
			System.setProperty( "java.library.path", Joiner.on(File.pathSeparator).skipNulls().join(paths));
			try {
				Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
				fieldSysPath.setAccessible( true );
				fieldSysPath.set( null, null );
			} catch (NoSuchFieldException | SecurityException
					| IllegalArgumentException | IllegalAccessException e) {
				logger.error("Error on adding java.library.path:"+path, e);
			}
		}
	}
}
