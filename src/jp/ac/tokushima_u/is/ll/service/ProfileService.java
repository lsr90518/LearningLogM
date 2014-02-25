package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.FileDataDao;
import jp.ac.tokushima_u.is.ll.dao.InterestDao;
import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dao.UsersMyLangsDao;
import jp.ac.tokushima_u.is.ll.dao.UsersStudyLangsDao;
import jp.ac.tokushima_u.is.ll.entity.FileData;
import jp.ac.tokushima_u.is.ll.entity.Interest;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.ProfileEditForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.util.FilenameUtil;
import jp.ac.tokushima_u.is.ll.util.HashUtils;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.TextUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author lemonrain
 */
@Service
@Transactional(readOnly = true)
public class ProfileService {

    @Autowired
    private LanguageService languageService;
    @Autowired
    private InterestService interestService;
    @Autowired
    private UsersDao usersDao;
    @Autowired
    private LanguageDao languageDao;
    @Autowired
    private UsersMyLangsDao usersMyLangsDao;
    @Autowired
    private UsersStudyLangsDao usersStudyLangsDao;
    @Autowired
    private InterestDao interestDao;
    @Autowired
    private FileDataDao fileDataDao;
    @Autowired
    private StaticServerService staticServerService;
    
    @Transactional(readOnly = false)
    public void editProfile(ProfileEditForm form) {
        Users user = usersDao.findById(SecurityUserHolder.getCurrentUser().getId());
        if (!user.getId().equals(form.getUserid())) {
            return;
        }
        Date current = Calendar.getInstance().getTime();
        user.setNickname(form.getNickname());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setUpdateTime(current);
        
        usersDao.update(user);
        
        usersMyLangsDao.deleteAllByUserId(user.getId());
        for(int i = 0; i<form.getMyLangs().size();i++){
        	String code = form.getMyLangs().get(i);
            if (StringUtils.isBlank(code)) {
                continue;
            }
            Language lang = languageDao.findByCode(code);
            if (lang == null) {
                throw new RuntimeException("Language Not Exist(code:[" + code + "])");
            }
            usersMyLangsDao.insert(user.getId(), lang.getId(), i);
        }
        
        usersStudyLangsDao.deleteAllByUserId(user.getId());
        for(int i=0; i<form.getStudyLangs().size();i++){
        	String code = form.getStudyLangs().get(i);
            Language lang = languageDao.findByCode(code);
            if(lang == null){
            	throw new RuntimeException("Language Not Exist(code:[" + code + "])");
            }
            usersStudyLangsDao.insert(user.getId(), lang.getId(), i);
        }
        
        List<String> interestings = TextUtils.splitString(form.getInteresting());
        interestDao.deleteAllRelationWithUser(user.getId());
        for(String i: interestings){
        	Interest interest = interestDao.findByName(i);
        	if(interest == null){
    			interest = new Interest();
    			interest.setName(i);
    			interest.setId(KeyGenerateUtil.generateIdUUID());
    			interestDao.insert(interest);
        	}
        	interestDao.insertUsersInterest(user.getId(), interest.getId());
        }
    }
    
    @Transactional(readOnly = false)
	public void editPassword(ProfileEditForm form) {
		Users user = usersDao.findById(SecurityUserHolder.getCurrentUser().getId());
        if (!user.getId().equals(form.getUserid())) {
            return;
        }
        Date current = Calendar.getInstance().getTime();
        if (!StringUtils.isBlank(form.getPassword()) && !StringUtils.isBlank(form.getOldpassword()) && !StringUtils.isBlank(form.getPasswordConfirm()) && form.getPassword().equals(form.getPasswordConfirm())) {
            user.setPassword(form.getPassword());
        }
		if (user.getPassword() != null && user.getPassword().length() != 40) {
			user.setPassword(DigestUtils.sha1Hex(user.getPassword()));
		}
        user.setUpdateTime(current);
        usersDao.update(user);
	}

    @Transactional(readOnly = false)
    public void uploadAvatar(MultipartFile file) throws Exception {
        Users user = usersDao.findById(SecurityUserHolder.getCurrentUser().getId());
        FileData avatar = new FileData();
        Date current = new Date();
        avatar.setOrigName(file.getOriginalFilename());
    	String fileType = "";
        if(!StringUtils.isBlank(file.getOriginalFilename())){
        	fileType = FilenameUtil.checkMediaType(file.getOriginalFilename());
        	avatar.setFileType(fileType);
        }
        avatar.setCreateAt(current);
        avatar.setId(KeyGenerateUtil.generateIdUUID());
        byte[] b = file.getBytes();
        avatar.setMd5(HashUtils.md5Hex(b));
        fileDataDao.insert(avatar);
        if(user.getAvatar()!=null){
        	fileDataDao.delete(user.getAvatar());
        }
        user.setUpdateTime(current);
        user.setAvatar(avatar.getId());
        usersDao.update(user);
        staticServerService.upload(b, avatar.getId(), FilenameUtils.getExtension(file.getOriginalFilename()));
    }

	public ProfileEditForm convertUserToProfileEditForm(String userId) {
		ProfileEditForm form = new ProfileEditForm();
		Users user = usersDao.findById(userId);
		form.setNickname(user.getNickname());
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        List<Language> myLangs = languageDao.findListUsersMyLangs(userId);
        List<String> myLangsCodes = new ArrayList<>();
        for(Language lan:myLangs){
        	myLangsCodes.add(lan.getCode());
        }
        form.setMyLangs(myLangsCodes);
        
        List<Language> studyLangs = languageDao.findListUsersStudyLangs(userId);
        List<String> studyLangsCodes = new ArrayList<String>();
        for(Language lan:studyLangs){
        	studyLangsCodes.add(lan.getCode());
        }
        form.setStudyLangs(studyLangsCodes);
        
        List<Interest> interests = interestDao.findListByUserId(userId);
        List<String> interestNames = new ArrayList<String>();
        for(Interest i: interests){
        	interestNames.add(i.getName());
        }
        form.setInteresting(StringUtils.join(interestNames, ','));
        return form;
	}
}
