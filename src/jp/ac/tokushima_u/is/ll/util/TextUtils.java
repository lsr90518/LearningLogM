package jp.ac.tokushima_u.is.ll.util;

import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class TextUtils {
	public static List<String> splitString(String str){
		Iterable<String> strs = Splitter.on(CharMatcher.anyOf(",，、;；")).trimResults().omitEmptyStrings().split(str);
		return Lists.newArrayList(strs);
	}
}
