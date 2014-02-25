package jp.ac.tokushima_u.is.ll.dao;

import java.util.HashMap;
import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.Userinfo;

public interface RecommendDao {
	List<Userinfo> findUserByNickname(String nickname);
	List<Userinfo> findUsersByNatiModel(HashMap<String,String> params);
	List<HashMap<String,Object>> findContentByUsers(HashMap<String,String> params);
}
