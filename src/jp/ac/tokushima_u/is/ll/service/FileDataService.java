package jp.ac.tokushima_u.is.ll.service;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.entity.FileData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FileDataService {

	@Autowired
	private FileDataDao fileDataDao;
	
	public FileData findById(String id){
		return this.fileDataDao.findById(id);
	}
}
