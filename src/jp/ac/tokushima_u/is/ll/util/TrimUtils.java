package jp.ac.tokushima_u.is.ll.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class TrimUtils {
	public static String trim(String sql, String prefix, String suffix, String prefixesToOverride, String suffixesToOverride){
		if(StringUtils.isBlank(sql))return "";
		if(StringUtils.isBlank(prefix)) prefix = "";
		if(StringUtils.isBlank(suffix)) suffix = "";
		if(StringUtils.isBlank(prefixesToOverride)) prefixesToOverride = "";
		if(StringUtils.isBlank(suffixesToOverride)) suffixesToOverride = "";
		sql = sql.trim();
		Splitter splitter = Splitter.on("|").omitEmptyStrings().trimResults();
		List<String> pOverrides = Lists.newArrayList(splitter.split(prefixesToOverride));
		List<String> sOverrides = Lists.newArrayList(splitter.split(suffixesToOverride));
		for (String toRemove : pOverrides) {
			sql = StringUtils.removeStartIgnoreCase(sql, toRemove).trim();
	    }
		for (String toRemove : sOverrides) {
			sql = StringUtils.removeEndIgnoreCase(sql, toRemove).trim();
	    }
		
		sql = prefix+" "+sql+" "+suffix;
		return sql.trim();
	}
	
	public static void main(String[] args){
		System.out.println(TrimUtils.trim(" and a=1 and b=? or  c=?, ", "where", "set", "and", ","));
	}
}
