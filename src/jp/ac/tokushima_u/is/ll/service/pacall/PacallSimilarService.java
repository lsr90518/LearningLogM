package jp.ac.tokushima_u.is.ll.service.pacall;

import jp.ac.tokushima_u.is.ll.dao.PacallSimilarDao;
import jp.ac.tokushima_u.is.ll.entity.PacallSimilar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacallSimilarService {
	
	private static final Logger logger = LoggerFactory.getLogger(PacallSimilarService.class);
	
	@Autowired
	private PacallSimilarDao pacallSimilarDao;

	@Transactional(readOnly = false)
	public void create(PacallSimilar pacallSimilar) {
		PacallSimilar test = pacallSimilarDao.findByUnique(pacallSimilar.getPhotoId(), pacallSimilar.getItemId());
		if(test!=null){
			logger.warn("Pacall Similar already exists");
			return;
		}else{
			pacallSimilarDao.insert(pacallSimilar);
		}
	}

	@Transactional(readOnly = false)
	public void deleteByPhotoId(String photoId) {
		pacallSimilarDao.deleteByPhotoId(photoId);
	}

}
