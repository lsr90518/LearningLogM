package jp.ac.tokushima_u.is.ll.service.pacall;

import java.util.Date;

import jp.ac.tokushima_u.is.ll.dao.PacallAccessDao;
import jp.ac.tokushima_u.is.ll.dao.PacallUsageDao;
import jp.ac.tokushima_u.is.ll.entity.PacallAccess;
import jp.ac.tokushima_u.is.ll.entity.PacallUsage;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PacallLogService {
	
	@Autowired
	private PacallUsageDao pacallUsageDao;
	@Autowired
	private PacallAccessDao pacallAccessDao;
	
	@Transactional(readOnly = false)
	public void uploadStarted(String collectionId) {
		PacallUsage usage = findOrCreate(collectionId);
		usage.setUploadStart(new Date());
		this.pacallUsageDao.update(usage);
	}
	
	@Transactional(readOnly = false)
	public void uploadFinished(String collectionId) {
		PacallUsage usage = findOrCreate(collectionId);
		usage.setUploadEnd(new Date());
		this.pacallUsageDao.update(usage);
	}

	@Transactional(readOnly = false)
	public void updateUserActiveStatus(String userId) {
		PacallAccess access = pacallAccessDao.findLast(userId);
		//30分钟
		if(access!=null && System.currentTimeMillis()-access.getEndtime().getTime() < 1800000){
			pacallAccessDao.updateEndtime(access.getId(), new Date());
		}else{
			access = new PacallAccess();
			access.setId(KeyGenerateUtil.generateIdUUID());
			access.setUserId(userId);
			access.setStarttime(new Date());
			access.setEndtime(access.getStarttime());
			pacallAccessDao.insert(access);
		}
		
	}

	@Transactional(readOnly = false)
	public void logProcessStatus(String collectionId, long composeStart,
			long composeEnd, long analyzeStart, long analyzeEnd) {
		PacallUsage usage = findOrCreate(collectionId);
		usage.setComposeStart(new Date(composeStart));
		usage.setComposeEnd(new Date(composeEnd));
		usage.setAnalyzeStart(new Date(analyzeStart));
		usage.setAnalyzeEnd(new Date(analyzeEnd));
		this.pacallUsageDao.update(usage);
	}
	
	@Transactional(readOnly = false)
	private PacallUsage findOrCreate(String collectionId) {
		PacallUsage usage = pacallUsageDao.findById(collectionId);
		if(usage == null){
			usage = new PacallUsage();
			usage.setCollectionId(collectionId);
			pacallUsageDao.insert(usage);
		}
		return usage;
	}
}
