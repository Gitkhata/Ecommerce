package np.com.jp.app.ecommerce.service;

import np.com.jp.app.ecommerce.entity.Role;
import np.com.jp.app.ecommerce.entity.User;
import np.com.jp.app.ecommerce.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;


import java.util.List;


public interface UserService {
    /**
     * Returns the specified number of data per page
     */
    Integer DATA_PER_PAGE = 5;

    public List<User> listAll();

    public List<Role> listRoles();

    User save(User user);

    void encodePassword(User user);

    public Boolean isUniqueEmail(Integer id, String email);

    User getUserById(Integer id) throws UserNotFoundException;

    void delete(Integer id) throws UserNotFoundException;

    void changeEnableStatus(Integer id, Boolean status);

    Page<User> listByPage(Integer pageNumber);
}
