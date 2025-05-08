package np.com.jp.app.ecommerce.user;

import np.com.jp.ecommerce.common.entity.Role;
import np.com.jp.ecommerce.common.entity.User;

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
