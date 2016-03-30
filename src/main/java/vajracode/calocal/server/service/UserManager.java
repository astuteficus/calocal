package vajracode.calocal.server.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vajracode.calocal.server.dao.UserDao;
import vajracode.calocal.server.dto.UserDTO;
import vajracode.calocal.server.exceptions.ConflictException;
import vajracode.calocal.server.security.PasswordEncoder;
import vajracode.calocal.shared.model.Role;

@Component
public class UserManager {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public void createUser(String login, String pass) {
		UserDTO u = userDao.getUserByName(login);
		if (u != null)
			throw new ConflictException();
		u = new UserDTO();
		u.setCreated(new Date());
		u.setName(login);
		u.setPassword(passwordEncoder.encodePassword(pass));
		u.setRole(Role.USER);
		userDao.persist(u);
	}

}
