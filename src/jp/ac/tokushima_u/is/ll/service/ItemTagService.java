package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.ItemTagDao;
import jp.ac.tokushima_u.is.ll.entity.ItemTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ItemTagService {

	@Autowired
	private ItemTagDao itemTagDao;

	public List<ItemTag> search(String q) {
		return itemTagDao.searchByTag(q+"%");
	}
}
