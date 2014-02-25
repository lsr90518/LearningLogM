package jp.ac.tokushima_u.is.ll.dao;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.Users;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;

public interface UsersDao {

	void insert(Users user);
	int update(Users user);
	
	Long count();
	Users findById(String id);
	Users findByEmail(String email);
	Users findByActiveCode(String activecode);
	List<Users> findAll();
	List<Users> findPage(@Param("user") Users user, @Param("pageRequest") PageRequest pageRequest);
	Long countPage(Users user);
	List<Users> searchList(Users user);
	List<Users> findUsersHaveItemsFrom(Date startDate);
}
