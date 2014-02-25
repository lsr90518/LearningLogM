package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.entity.Message;

public interface MessageDao {

	Long countAll();

	List<Message> findListByExample(Message message);

	void insert(Message message);

}
