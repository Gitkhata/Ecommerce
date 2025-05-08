package np.com.jp.app.ecommerce.service;

import np.com.jp.app.ecommerce.entity.Role;
import np.com.jp.app.ecommerce.entity.User;
import np.com.jp.app.ecommerce.exception.UserNotFoundException;


import java.util.List;


public interface UserService {
    public List<User> listAll();

    public List<Role> listRoles();

    User save(User user);

    void encodePassword(User user);

    public Boolean isUniqueEmail(Integer id, String email);

    User getUserById(Integer id) throws UserNotFoundException;

    void delete(Integer id) throws UserNotFoundException;

    void changeEnableStatus(Integer id, Boolean status);
}
