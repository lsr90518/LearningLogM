package jp.ac.tokushima_u.is.ll.util;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.sen.StringTagger;
import net.java.sen.Token;

/**
 *
 * @author LSR
 */
public class SenUtil {

	private String confPath;
	private StringTagger tagger;
	private Token[] token;

	public SenUtil(){
		File path = new File(Thread.currentThread().getContextClassLoader().getResource("").toString());
        String abPath = path.getParentFile().getPath();
        abPath = abPath.substring(5, abPath.length());
        setConfPath(abPath + "/lib/senDic/conf/sen.xml");
        setConfPath(getConfPath().replaceAll("%20", " "));
		try {
			setTagger(StringTagger.getInstance(getConfPath()));
		} catch (IllegalArgumentException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Token[] Analyze(String jpString){
		try {
			return tagger.analyze(jpString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return token;
		}
		
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public StringTagger getTagger() {
		return tagger;
	}

	public void setTagger(StringTagger tagger) {
		this.tagger = tagger;
	}

	public Token[] getToken() {
		return token;
	}

	public void setToken(Token[] token) {
		this.token = token;
	}
}
