package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.JapaneseWordlistDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.JapaneseWordlevel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JapaneseWordlistService {
	@Autowired
	private JapaneseWordlistDao japaneseWordlistDao;
	
	public List<JapaneseWordlevel> getJapanesewordlevellist(){
		return this.japaneseWordlistDao.getJapanesewordlevellist();
	}
}
