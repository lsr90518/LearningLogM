package jp.ac.tokushima_u.is.ll.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jp.ac.tokushima_u.is.ll.dao.LanguageDao;
import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.dao.UsersMyLangsDao;
import jp.ac.tokushima_u.is.ll.dao.UsersStudyLangsDao;
import jp.ac.tokushima_u.is.ll.entity.Language;
import jp.ac.tokushima_u.is.ll.entity.Users;
import jp.ac.tokushima_u.is.ll.util.KeyGenerateUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author houbin
 */
@Service("userService")
@Transactional(readOnly = true)
public class UserService {
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UsersDao usersDao;
	@Autowired
	private LanguageDao languageDao;
	@Autowired
	private SignupService signupManager;
	@Autowired
	private LanguageService languageService;
	@Autowired
	private UsersMyLangsDao usersMyLangsDao;
	@Autowired
	private UsersStudyLangsDao usersStudyLangsDao;

	// ■wakebe クイズ情報取得のため
	@Autowired
	private MyQuizService myQuizService;
	// ■wakebe 回答数情報取得のため
	@Autowired
	private ItemService itemService;

	public Users validateUser(String email, String password) {
		Users user = this.findByEmail(email);
		if (user != null
				&& user.getPassword().equals(DigestUtils.sha1Hex(password)))
			return user;
		else
			return null;
	}

//	@Transactional(readOnly = false)
//	public boolean initSystemUser() {
//		Long count = usersDao.count();
//		if (count != 0) {
//			return false;
//		}
//		logger.info("Start to init admin user");
//		Users user = new Users();
//		user.setPcEmail("learninglogforyou@gmail.com");
//		user.setPassword(DigestUtils.sha1Hex("learn492357816"));
//		user.setAuth(Users.USERS_AUTH_ADMIN);
//		user.setEnabled(true);
//		user.setAccountNotLocked(true);
//		Date current = Calendar.getInstance().getTime();
//		user.setLastLogin(current);
//		user.setCreateTime(current);
//		user.setUpdateTime(current);
//		usersDao.insert(user);
//		return true;
//	}

	public List<Users> searchAllUsersList() {
		return this.usersDao.findAll();
	}

	public Users findById(String userId) {
		Users user = this.usersDao.findById(userId);
		return user;
	}

	@Transactional(readOnly = false)
	public void registerNewUser(String email) {
		Users users = usersDao.findByEmail(email);
		if (users == null) {
			Date current = Calendar.getInstance().getTime();
			users = new Users();
			users.setPcEmail(email);
			users.setEnabled(false);
			users.setCreateTime(current);
			users.setUpdateTime(current);
			users.setPassword("");
			users.setAuth(Users.USERS_AUTH_MEMBER);
			users.setActivecode(UUID.randomUUID().toString()
					.replaceAll("-", ""));
			usersDao.insert(users);
		} else {
			// users.setActivecode(UUID.randomUUID().toString().replaceAll("-",
			// ""));
			// usersDao.save(users);
		}
		// Send mail
		signupManager.sendActivationMail(email, users.getActivecode());
	}

	public Users getByActivecode(String activecode) {
		return this.usersDao.findByActiveCode(activecode);
	}

	public void save(Users users) {
		// Auto encode password
		if (users.getPassword() != null && users.getPassword().length() != 40) {
			users.setPassword(DigestUtils.sha1Hex(users.getPassword()));
		}
		if(users.getId()!=null){
			this.usersDao.update(users);
		}else{
			this.usersDao.insert(users);
		}
	}

	public Users findByEmail(String email) {
		return usersDao.findByEmail(email);
	}

	public Users findByActiveCode(String activecode) {
		Users user = new Users();
		user.setActivecode(activecode);
		List<Users> users = usersDao.findPage(user, new PageRequest(0, 1));
		return users.isEmpty() ? null : users.get(0);
	}

	public Users findByNickname(String nickname) {
		Users user = new Users();
		user.setNickname(nickname);
		List<Users> users = usersDao.findPage(user, new PageRequest(0, 1));
		return users.isEmpty() ? null : users.get(0);
	}

	@Transactional(readOnly = false)
	public void updateLastLogin(String userId) {
		Users user = usersDao.findById(userId);
		user.setLastLogin(Calendar.getInstance().getTime());
		usersDao.update(user);
	}

	public List<Users> searchAllUsers() {
		return this.usersDao.findAll();
	}

	public Page<Users> searchPage(Users user, PageRequest request) {
		// Search by nickname, pcEmail, firstName, lastName, order by email asc
		List<Users> list = this.usersDao.findPage(user, request);
		Long count = this.usersDao.countPage(user);
		return new PageImpl<Users>(list, request, count);
	}

	@Transactional(readOnly = false)
	public void createByAdmin(Users user) {
		Users u = new Users();
		u.setPcEmail(user.getPcEmail());
		if (user.getPassword() != null && user.getPassword().length() != 40) {
			u.setPassword(DigestUtils.sha1Hex(user.getPassword()));
		}
		u.setAuth(user.getAuth());
		u.setEnabled(true);
		u.setAccountNotLocked(true);
		u.setLastLogin(null);
		Date current = new Date(System.currentTimeMillis());
		u.setCreateTime(current);
		u.setUpdateTime(current);
		u.setActivecode("");
		u.setNickname(user.getNickname());
		u.setFirstName(user.getFirstName());
		u.setLastName(user.getLastName());
		u.setId(KeyGenerateUtil.generateIdUUID());
		usersDao.insert(u);
		
		usersMyLangsDao.insert(u.getId(), languageService.findLangByCode("ja").getId(), 0);
		usersStudyLangsDao.insert(u.getId(), languageService.findLangByCode("en").getId(), 0);
	}

	@Transactional(readOnly = false)
	public void editByAdmin(Users user) {
		Users u = usersDao.findById(user.getId());
		u.setPcEmail(user.getPcEmail());
		u.setAuth(user.getAuth());
		u.setEnabled(true);
		u.setAccountNotLocked(true);
		Date current = new Date(System.currentTimeMillis());
		u.setUpdateTime(current);
		u.setNickname(user.getNickname());
		u.setFirstName(user.getFirstName());
		u.setLastName(user.getLastName());
		if (!StringUtils.isBlank(user.getPassword())) {
			if (user.getPassword() != null && user.getPassword().length() != 40) {
				u.setPassword(DigestUtils.sha1Hex(user.getPassword()));
			}
		}
		usersDao.update(u);
	}

	public List<Users> findAllUserListNew() {
		return usersDao.findAll();
	}

	public Long findAllUserCount() {
		return this.usersDao.count();
	}

	public List<Users> findAllUserListForWeeklyNotification() {
		Users user = new Users();
		user.setReceiveWeeklyNotification(true);
		List<Users> userList = this.usersDao.findPage(user, new PageRequest(0,
				Integer.MAX_VALUE));
		return userList;
	}

	public List<Users> searchAllUsersNew() {
		List<Users> list = new ArrayList<Users>();
		list.add(usersDao.findByEmail("toma.kunita@gmail.com"));
		return list;
	}

	@Transactional(readOnly = false)
	public void checkFlags(String userId) {
		Users user = this.usersDao.findById(userId);
		if (user.getReceiveWeeklyNotification() == null) {
			user.setReceiveWeeklyNotification(true);
			usersDao.update(user);
		}
	}

	// ■wakebe 点数の取得
	public Long getQuizAllScores(String userId) {
		Users user = this.findById(userId);
		Map<String, Long> info = myQuizService.searchOneDayQuiz(new Date(),
				user);

		return info.get("allscores");
	}

	// ■wakebe 次のレベルまでの経験値取得
	public int getNextExperiencePoint(String userId) {
		Users user = usersDao.findById(userId);

		// (現在のレベル*5)^1.4 + 5 (仮)
		if (user.getUserLevel() == null) {
			user.setUserLevel(1);
		}
		double result = Math.pow(user.getUserLevel() * 5, 1.4) + 5;
		// double result = user.getUserLevel() * 10;

		return (int) result;
	}

	// ■wakebe 現在の経験値取得
	public Long getNowExperiencePoint(String userId) {
		Users user = usersDao.findById(userId);

		if (user.getExperiencePoint() == null) {
			user.setExperiencePoint(0);
		}
		return user.getExperiencePoint() + this.getQuizAllScores(userId)
				+ this.itemService.getAnswerCount(userId);
	}

	// ■wakebe 経験値の加算
	@Transactional(readOnly = false)
	public void addExperiencePoint(String id, Integer exp) {
		Users user = usersDao.findById(id);
		if(user.getExperiencePoint()==null){
			user.setExperiencePoint(0);
		}
		user.setExperiencePoint(user.getExperiencePoint() + exp);

		// レベルアップの処理 next経験値を超えたらレベルアップ レベルアップごとにnext経験値を更新
		while (getNowExperiencePoint(id) >= this.getNextExperiencePoint(id)) {
			if(user.getUserLevel()==null){
				user.setUserLevel(0);
			}
			user.setUserLevel(user.getUserLevel() + 1);
		}

		usersDao.update(user);
	}

	// ■wakebe レベルソート
	public List<Users> levelRanking() {
		PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE,
				Direction.DESC, "user_level");
		List<Users> result = this.usersDao.findPage(new Users(), pageRequest);
		return result;
	}

	public List<Users> findAllUsers() {
		return usersDao.findAll();
	}

	public List<Users> searchByEmail(String email) {
		Users user = new Users();
		user.setPcEmail("%"+email+"%");
		return usersDao.searchList(user);
	}

	@Transactional(readOnly = false)
	public void updateUserMyLangs(String userId, List<Language> myLangs) {
		usersMyLangsDao.deleteAllByUserId(userId);
		for(int i=0;i<myLangs.size();i++){
			usersMyLangsDao.insert(userId, myLangs.get(i).getId(), i);
		}
	}

	@Transactional(readOnly = false)
	public void updateUserStudyLangs(String userId, List<Language> studyLangs) {
		usersStudyLangsDao.deleteAllByUserId(userId);
		for(int i=0;i<studyLangs.size();i++){
			usersStudyLangsDao.insert(userId, studyLangs.get(i).getId(), i);
		}
	}
}
