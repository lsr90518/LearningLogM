package jp.ac.tokushima_u.is.ll.service;

import java.util.HashMap;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.RecommendDao;
import jp.ac.tokushima_u.is.ll.entity.Userinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecommendService {
	
	@Autowired
	RecommendDao recommendDao;
	
	public List<Userinfo> findUserByNickname(String nickname){
		return recommendDao.findUserByNickname(nickname);
	}
	
	public List<Userinfo> findUsersByNatiModel(HashMap<String, String> params){
		return recommendDao.findUsersByNatiModel(params);
	}
	
	public List<HashMap<String,Object>> findContentByUsers(HashMap<String,String> params){
		return recommendDao.findContentByUsers(params);
	}
}
