package jp.ac.tokushima_u.is.ll.service;

import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Hou Bin
 */
@Service
@Transactional(readOnly = true)
public class LanguageService {

	@Autowired
    private LanguageDao languageDao;
    
    public List<Language> searchAllLanguage() {
        List<Language> languageList = languageDao.findAll();

        if (languageList == null || languageList.isEmpty()) {
        	//Initialize Language Table
            Language lang1 = new Language();
            lang1.setCode("en");
            lang1.setName("English");

            Language lang2 = new Language();
            lang2.setCode("ja");
            lang2.setName("Japanese");

            Language lang3 = new Language();
            lang3.setCode("zh");
            lang3.setName("Chinese");
            lang1.setId(KeyGenerateUtil.generateIdUUID());
            lang2.setId(KeyGenerateUtil.generateIdUUID());
            lang3.setId(KeyGenerateUtil.generateIdUUID());
            this.languageDao.insert(lang1);
            this.languageDao.insert(lang2);
            this.languageDao.insert(lang3);
            languageList = this.languageDao.findAll();
        }


        return languageList;
    }

    @Transactional(readOnly = true)
    public List<Language> searchAllLanguageOrderedBy(String property, boolean isAsc) {
        return languageDao.findAll();
    }

    @Transactional(readOnly = true)
    public Language findLangByCode(String code) {
        return languageDao.findByCode(code);
    }

    @Transactional(readOnly = true)
	public Language findById(String id) {
		return languageDao.findById(id);
	}
    
    public List<Language> findALl(){
    	return this.languageDao.findAll();
    }

    @Transactional(readOnly = false)
	public void createByAdmin(Language language) {
		language.setCode(language.getCode().toLowerCase());
		language.setId(KeyGenerateUtil.generateIdUUID());
		languageDao.insert(language);
	}

    @Transactional(readOnly = false)
	public void editByAdmin(Language language) {
		language.setCode(language.getCode().toLowerCase());
		languageDao.update(language);
	}

    @Transactional(readOnly = false)
	public void delete(String id) {
		languageDao.delete(id);
	}
    
    public List<Language> findMyLangs(String userId){
    	return languageDao.findListUsersMyLangs(userId);
    }
    
    public List<Language> findStudyLangs(String userId){
    	return languageDao.findListUsersStudyLangs(userId);
    }
}
