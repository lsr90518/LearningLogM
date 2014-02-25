package jp.ac.tokushima_u.is.ll.service;

import jp.ac.tokushima_u.is.ll.dao.InterestDao;
import jp.ac.tokushima_u.is.ll.entity.Interest;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InterestService {
	
	@Autowired
	private InterestDao interestDao;

	@Transactional(readOnly = false)
	public Interest findOrAdd(String name){
		if(StringUtils.isBlank(name)) return null;
		Interest interest = interestDao.findByName(name.trim());
		if(interest==null){
			interest = new Interest();
			interest.setName(name);
			interest.setId(KeyGenerateUtil.generateIdUUID());
			interestDao.insert(interest);
		}
		return interest;
	}
}
