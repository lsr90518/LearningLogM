package jp.ac.tokushima_u.is.ll.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import jp.ac.tokushima_u.is.ll.dao.CategoryDao;
import jp.ac.tokushima_u.is.ll.dao.InterestDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dao.UsersMyLangsDao;
import jp.ac.tokushima_u.is.ll.dao.UsersStudyLangsDao;
import jp.ac.tokushima_u.is.ll.entity.Category;
import jp.ac.tokushima_u.is.ll.entity.Interest;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.exception.NotFoundException;
import jp.ac.tokushima_u.is.ll.form.SignupForm;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;
import jp.ac.tokushima_u.is.ll.util.TextUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import freemarker.template.TemplateException;

/**
 *
 * @author houbin
 */
@Service
@Transactional(readOnly = true)
public class SignupService {

    @Autowired
    private MailService mailService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private UsersDao usersDao;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private InterestService interestService;
    @Autowired
    private UsersMyLangsDao usersMyLangsDao;
    @Autowired
    private UsersStudyLangsDao usersStudyLangsDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private InterestDao interestDao;

    @Transactional(readOnly = false)
    public void registerNewUser(String email) {
        Users users = usersDao.findByEmail(email);
        if (users == null) {
            Date current = Calendar.getInstance().getTime();
            users = new Users();
            users.setPcEmail(email);
            users.setEnabled(false);
            users.setAccountNotLocked(false);
            users.setCreateTime(current);
            users.setUpdateTime(current);
            users.setPassword("");


            // ■wakebe レベルと経験値の初期化
            users.setExperiencePoint(0);
            users.setUserLevel(1);


            users.setAuth(Users.USERS_AUTH_MEMBER);
            users.setActivecode(UUID.randomUUID().toString().replaceAll("-", ""));
            if(users.getId() == null){
            	users.setId(KeyGenerateUtil.generateIdUUID());
            	usersDao.insert(users);
            }else{
            	usersDao.update(users);
            }
        } else {
//            users.setActivecode(UUID.randomUUID().toString().replaceAll("-", ""));
//            usersDao.save(users);
        }
        //Send mail
        sendActivationMail(email, users.getActivecode());
    }

    
    public void sendActivationMail(String sendTo, String activecode) {
        try {
            ModelMap model = new ModelMap();
            model.addAttribute("activationUrl", propertyService.getSystemUrl() + "/signup/" + activecode);
            model.addAttribute("siteUrl", propertyService.getSystemUrl());
            model.addAttribute("sendFrom", propertyService.getSystemMailAddress());
            mailService.sendSysMail(sendTo, "[Learning Log]Please activate your account", "activationMail", model);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TemplateException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Transactional(readOnly = false)
    public void addProfile(Users u, SignupForm signupForm) {
        Date current = Calendar.getInstance().getTime();
        Users user = usersDao.findById(u.getId());
        user.setNickname(signupForm.getNickname());
        user.setFirstName(signupForm.getFirstName());
        user.setLastName(signupForm.getLastName());

		if (user.getPassword() != null && user.getPassword().length() != 40) {
			user.setPassword(DigestUtils.sha1Hex(signupForm.getPassword()));
		}
        user.setActivecode(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setUpdateTime(current);
        user.setEnabled(true);
        user.setAccountNotLocked(true);
        
        usersDao.update(user);
        
        usersMyLangsDao.deleteAllByUserId(user.getId());
        int i = 0;
        for (String code : signupForm.getMyLangs()) {
            if (StringUtils.isBlank(code)) {
                continue;
            }
            Language lang = languageService.findLangByCode(code);
            if (lang == null) {
                throw new RuntimeException("Language Not Exist(code:[" + code + "])");
            }
            
            usersMyLangsDao.insert(user.getId(), lang.getId(), i);
            i++;
        }

        usersStudyLangsDao.deleteAllByUserId(user.getId());
        i = 0;
        for (String code : signupForm.getStudyLangs()) {
            if (StringUtils.isBlank(code)) {
                continue;
            }
            Language lang = languageService.findLangByCode(code);
            if (lang == null) {
                throw new RuntimeException("Language Not Exist(code:[" + code + "])");
            }
            usersStudyLangsDao.insert(user.getId(), lang.getId(), i);
            i++;
        }

        categoryDao.deleteAllUsersMyCategoryListByUserId(user.getId());
        List<Category> categoryList = categoryDao.findListByUserId(user.getId());
        if(categoryList==null || categoryList.size()==0){
        	categoryList = new ArrayList<Category>();
        	categoryList.addAll(categoryService.findByName("Languages", null));
        	for(Category c: categoryList){
        		categoryDao.insertUsersMyCategoryList(user.getId(), c.getId());
        	}
        }
        
        List<String> interestings = TextUtils.splitString(signupForm.getInteresting());
        for(String interest: interestings){
        	Interest in = interestDao.findByName(interest);
        	if(in == null){
        		in = new Interest();
        		in.setName(interest);
        		in.setId(KeyGenerateUtil.generateIdUUID());
        		interestDao.insert(in);
        	}
        	interestDao.insertUsersInterest(user.getId(), in.getId());
        }
    }

    public Users getUserByActivecode(String activecode) {
        return this.usersDao.findByActiveCode(activecode);
    }

	public void sendResetPasswordMail(String email) throws NotFoundException {
		Users user = usersDao.findByEmail(email);
		if(user==null)throw new NotFoundException("email");
		user.setActivecode(UUID.randomUUID().toString().replaceAll("-", ""));
		usersDao.update(user);
        try {
            ModelMap model = new ModelMap();
            model.addAttribute("activationUrl", propertyService.getSystemUrl() + "/signup/resetpassword/" + user.getActivecode());
            model.addAttribute("siteUrl", propertyService.getSystemUrl());
            model.addAttribute("sendFrom", propertyService.getSystemMailAddress());
            model.addAttribute("nickname", user.getNickname());
            mailService.sendSysMail(user.getPcEmail(), "Reset your Learning Log password", "resetpasswordMail", model);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TemplateException ex) {
            ex.printStackTrace();
            Logger.getLogger(SignupService.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	@Transactional(readOnly = false)
	public void resetPassword(Users u, String password) {
		Users user = usersDao.findById(u.getId());
		if (user.getPassword() != null && user.getPassword().length() != 40) {
			user.setPassword(password);
		}
		usersDao.update(user);
	}
}
