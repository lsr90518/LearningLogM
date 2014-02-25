package jp.ac.tokushima_u.is.ll.dao;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dto.PacallCollectionDTO;
import jp.ac.tokushima_u.is.ll.entity.PacallCollection;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

public interface PacallCollectionDao {

	List<PacallCollectionDTO> findListDTOByUserId(@Param("userId")String userId, @Param("page")Pageable pageable);

	Long countByUserId(String userId);
	
	void insert(PacallCollection pacallCollection);

	PacallCollection findById(String id);

	void delete(String id);

}
