package jp.ac.tokushima_u.is.ll.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.ac.tokushima_u.is.ll.dao.CategoryDao;
import jp.ac.tokushima_u.is.ll.dao.SendTimeDao;
import jp.ac.tokushima_u.is.ll.dao.SettingDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.Category;
import jp.ac.tokushima_u.is.ll.entity.SendTime;
import jp.ac.tokushima_u.is.ll.entity.Setting;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.form.SettingForm;
import jp.ac.tokushima_u.is.ll.security.SecurityUserHolder;
import jp.ac.tokushima_u.is.ll.util.ConvertUtil;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author li
 */
@Service("settingService")
@Transactional(readOnly = true)
public class SettingService {

	@Autowired
    private SendTimeDao sendTimeDao;
    @Autowired
    private UsersDao userDao;
    @Autowired
    private SettingDao settingDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    UserService userService;

    @Transactional(readOnly = false)
    public void initSettingForm(SettingForm form){
        Users user = userService.findById(SecurityUserHolder.getCurrentUser().getId());
        String userId = user.getId();
        if(user.getReceiveWeeklyNotification()==null){
        	this.userService.checkFlags(userId);
        	user = userService.findById(userId);
        }
        if(user.getReceiveWeeklyNotification()){
        	form.setReceiveMailNotification(true);
        }else{
        	form.setReceiveMailNotification(false);
        }
//        String hql = "from SendTime sendtime where sendtime.author =:author and sendtime.typ =:typ";

        String formatHour = "HH";
        String formatMin = "mm";


        SendTime temp = this.sendTimeDao.findOneByAuthorAndTyp(user.getId(), Calendar.MONDAY);
        if(temp!=null){
            form.setMonstime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setMonsmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.TUESDAY);
        if(temp!=null){
            form.setTuestime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setTuesmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.WEDNESDAY);
        if(temp!=null){
            form.setWedstime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setWedsmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.THURSDAY);
        if(temp!=null){
            form.setThurstime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setThursmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.FRIDAY);
        if(temp!=null){
            form.setFristime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
             form.setFrismin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.SATURDAY);
        if(temp!=null){
            form.setSatstime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setSatsmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

        temp = this.sendTimeDao.findOneByAuthorAndTyp(userId, Calendar.SUNDAY);
        if(temp!=null){
            form.setSunstime1(ConvertUtil.timeToString(temp.getSendtime(), formatHour));
            form.setSunsmin1(ConvertUtil.timeToString(temp.getSendtime(), formatMin));
        }

       Setting setting = settingDao.findByUserId(user.getId());
       if(setting!=null){
            form.setHandsetcd(setting.getHandsetcd());
       }
    }
    
    public List<Setting> findSetting(Integer handsetcd){
    	return this.settingDao.findListByHandsetcd(handsetcd);
    }
    
    @Transactional(readOnly = false)
    public void updateSettingForm2(SettingForm form){
        String userId = SecurityUserHolder.getCurrentUser().getId();
        Time montime = null;
        Time tuetime = null;
        Time wedtime = null;
        Time thurtime = null;
        Time fritime = null;
        Time sattime = null;
        Time suntime = null;
        try {
            if (form.getMonstime1() != null && form.getMonstime1().length() > 0) {
                montime = Time.valueOf(form.getMonstime1()+":"+form.getMonsmin1()+":00");
            }
            if (form.getTuestime1() != null && form.getTuestime1().length() > 0) {
                tuetime = Time.valueOf(form.getTuestime1()+":"+form.getTuesmin1()+":00");
            }
            if (form.getWedstime1() != null && form.getWedstime1().length() > 0) {
                wedtime = Time.valueOf(form.getWedstime1()+":"+form.getWedsmin1()+":00");
            }
            if (form.getThurstime1() != null && form.getThurstime1().length() > 0) {
                thurtime = Time.valueOf(form.getThurstime1()+":"+form.getThursmin1()+":00");
            }
            if (form.getFristime1() != null && form.getFristime1().length() > 0) {
                fritime = Time.valueOf(form.getFristime1()+":"+form.getFrismin1()+":00");
            }
            if (form.getSatstime1() != null && form.getSatstime1().length() > 0) {
                sattime = Time.valueOf(form.getSatstime1()+":"+form.getSatsmin1()+":00");
            }
            if (form.getSunstime1() != null && form.getSunstime1().length() > 0) {
                suntime = Time.valueOf(form.getSunstime1()+":"+form.getSunsmin1()+":00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.sendTimeDao.deleteAllByAuthorId(userId);
        SendTime sendtime = null;
        if (montime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.MONDAY);
            sendtime.setSendtime(montime);
            this.sendTimeDao.insert(sendtime);
        }

        if (tuetime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.TUESDAY);
            sendtime.setSendtime(tuetime);
            this.sendTimeDao.insert(sendtime);
        }

        if (wedtime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.WEDNESDAY);
            sendtime.setSendtime(wedtime);
            this.sendTimeDao.insert(sendtime);
        }

        if (thurtime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.THURSDAY);
            sendtime.setSendtime(thurtime);
            this.sendTimeDao.insert(sendtime);
        }

        if (fritime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.FRIDAY);
            sendtime.setSendtime(fritime);
            this.sendTimeDao.insert(sendtime);
        }

        if (sattime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.SATURDAY);
            sendtime.setSendtime(sattime);
            this.sendTimeDao.insert(sendtime);
        }

        if (suntime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.SUNDAY);
            sendtime.setSendtime(suntime);
            this.sendTimeDao.insert(sendtime);
        }
    }
    
    @Transactional
    public void saveSettingForm(SettingForm form) {
        Time montime = null;
        Time tuetime = null;
        Time wedtime = null;
        Time thurtime = null;
        Time fritime = null;
        Time sattime = null;
        Time suntime = null;
        try {
            if (form.getMonstime1() != null && form.getMonstime1().length() > 0) {
//                montime = Time.valueOf(form.getMonstime1());
                 montime = Time.valueOf(form.getMonstime1()+":"+form.getMonsmin1()+":00");
            }
            if (form.getTuestime1() != null && form.getTuestime1().length() > 0) {
//                tuetime = Time.valueOf(form.getTuestime1());
                tuetime = Time.valueOf(form.getTuestime1()+":"+form.getTuesmin1()+":00");
            }
            if (form.getWedstime1() != null && form.getWedstime1().length() > 0) {
//                wedtime = Time.valueOf(form.getWedstime1());
                wedtime = Time.valueOf(form.getWedstime1()+":"+form.getWedsmin1()+":00");
            }
            if (form.getThurstime1() != null && form.getThurstime1().length() > 0) {
//                thurtime = Time.valueOf(form.getThurstime1());
                thurtime = Time.valueOf(form.getThurstime1()+":"+form.getThursmin1()+":00");
            }
            if (form.getFristime1() != null && form.getFristime1().length() > 0) {
//                fritime = Time.valueOf(form.getFristime1());
                fritime = Time.valueOf(form.getFristime1()+":"+form.getFrismin1()+":00");
            }
            if (form.getSatstime1() != null && form.getSatstime1().length() > 0) {
//                sattime = Time.valueOf(form.getSatstime1());
                 sattime = Time.valueOf(form.getSatstime1()+":"+form.getSatsmin1()+":00");
            }
            if (form.getSunstime1() != null && form.getSunstime1().length() > 0) {
//                suntime = Time.valueOf(form.getSunstime1());
                 suntime = Time.valueOf(form.getSunstime1()+":"+form.getSunsmin1()+":00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String userId = SecurityUserHolder.getCurrentUser().getId();
        this.sendTimeDao.deleteAllByAuthorId(userId);
        SendTime sendtime = null;
        if (montime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.MONDAY);
            sendtime.setSendtime(montime);
            this.sendTimeDao.insert(sendtime);
        }

        if (tuetime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.TUESDAY);
            sendtime.setSendtime(tuetime);
            this.sendTimeDao.insert(sendtime);
        }

        if (wedtime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.WEDNESDAY);
            sendtime.setSendtime(wedtime);
            this.sendTimeDao.insert(sendtime);
        }

        if (thurtime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.THURSDAY);
            sendtime.setSendtime(thurtime);
            this.sendTimeDao.insert(sendtime);
        }

        if (fritime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.FRIDAY);
            sendtime.setSendtime(fritime);
            this.sendTimeDao.insert(sendtime);
        }

        if (sattime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.SATURDAY);
            sendtime.setSendtime(sattime);
            this.sendTimeDao.insert(sendtime);
        }

        if (suntime != null) {
            sendtime = new SendTime();
            sendtime.setAuthorId(userId);
            sendtime.setTyp(Calendar.SUNDAY);
            sendtime.setSendtime(suntime);
            this.sendTimeDao.insert(sendtime);
        }


         Setting setting = this.settingDao.findByUserId(userId);
         if(form.getHandsetcd()!=null){
            if(setting == null){
                 setting = new Setting();
            }
             setting.setHandsetcd(form.getHandsetcd());
             setting.setAuthorId(userId);
             if(setting.getId()==null){
            	 setting.setId(KeyGenerateUtil.generateIdUUID());
            	 this.settingDao.insert(setting);
             }else{
            	 this.settingDao.update(setting);
             }
         }
         
         //Update UsersMyCategoryList
         categoryDao.deleteAllUsersMyCategoryListByUserId(userId);
         if(form.getCategoryIdList()==null){
        	 form.setCategoryIdList(new ArrayList<String>());
         }
         for(String categoryId: form.getCategoryIdList()){
        	 Category category = categoryDao.findById(categoryId);
        	 if(category != null){
        		 categoryDao.insertUsersMyCategoryList(userId, categoryId);
        	 }
         }
         
         Users user = userDao.findById(userId);
         if(!StringUtils.isBlank(form.getDefaultCategoryId())){
        	 Category defaultCategory = categoryDao.findById(form.getDefaultCategoryId());
        	 user.setDefaultCategory(defaultCategory.getId());
         }else{
        	 user.setDefaultCategory(null);
         }
       	 user.setReceiveWeeklyNotification(form.isReceiveMailNotification());
       	 user.setUpdateTime(new Date());
         userDao.update(user);
    }
}
