package jp.ac.tokushima_u.is.ll.service;

import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.PacallCollectionDao;
import jp.ac.tokushima_u.is.ll.dto.PacallCollectionDTO;
import jp.ac.tokushima_u.is.ll.entity.PacallCollection;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacallCollectionService {
	
	@Autowired
	private PacallCollectionDao pacallCollectionDao;

	public Page<PacallCollectionDTO> findListDTOByUserId(String userId, int page) {
		if(page<1) page = 1;
		Pageable pageable = new PageRequest(page-1, Integer.MAX_VALUE, new Sort(new Sort.Order(Sort.Direction.DESC, "c.create_time")));
		List<PacallCollectionDTO> list = this.pacallCollectionDao.findListDTOByUserId(userId, pageable);
		Long count = this.pacallCollectionDao.countByUserId(userId);

		return new PageImpl<PacallCollectionDTO>(list, pageable, count);
	}

	@Transactional(readOnly = false)
	public void createCollection(String name, String userId) {
		PacallCollection collection = new PacallCollection();
		collection.setId(KeyGenerateUtil.generateIdUUID());
		collection.setName(name);
		collection.setUserId(userId);
		collection.setCreateTime(new Date());
		pacallCollectionDao.insert(collection);
	}

	public PacallCollection findById(String collectionId) {
		return pacallCollectionDao.findById(collectionId);
	}

	@Transactional(readOnly = false)
	public void delete(String collectionId) {
		this.pacallCollectionDao.delete(collectionId);
	}
}
