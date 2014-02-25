package jp.ac.tokushima_u.is.ll.service;

import javax.annotation.PostConstruct;

import jp.ac.tokushima_u.is.ll.dao.UsersDao;
import jp.ac.tokushima_u.is.ll.entity.Users;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ShiroDbRealm extends AuthorizingRealm {
	
	private static final String HASH_ALGORITHM = "SHA-1";
	private static final Logger logger = LoggerFactory.getLogger(ShiroDbRealm.class);
	@Autowired
	private UsersDao usersDao;
	
	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(HASH_ALGORITHM);
		setCredentialsMatcher(matcher);
	}

	/**
	 * Login用
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		try {
			UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
			
			Users user = usersDao.findByEmail(token.getUsername());
			
			if (user != null) {
				if (!user.getEnabled()) {
					throw new DisabledAccountException();
				}
				if (!user.getAccountNotLocked()){
					throw new LockedAccountException();
				}
				return new SimpleAuthenticationInfo(user, user.getPassword(),
						ByteSource.Util.bytes(""), getName());
			} else {
				return null;
			}
		} catch (AuthenticationException e) {
			logger.debug("Login error", e);
			return null;
		} catch (Exception e){
			logger.error("Error", e);
			return null;
		}
	}
	
	/**
	 * 権限チェック用
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Users shiroUser = (Users) principals.getPrimaryPrincipal();
		Users user = usersDao.findByEmail(shiroUser.getPcEmail());

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if(Users.USERS_AUTH_ADMIN == user.getAuth()){
			info.addRole("admin");
		}
		return info;
	}
}
