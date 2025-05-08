package np.com.jp.app.ecommerce.user;

import jakarta.transaction.Transactional;
import np.com.jp.ecommerce.common.entity.Role;
import np.com.jp.ecommerce.common.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    Boolean check = userEmail == null ? false : true;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (existingUser.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword()); // don't update the password
            } else {
                encodePassword(user); // update the password
            }
        } else {
            encodePassword(user);  // new user
        }
        return userRepository.save(user);
    }

    /**
     * A method to encode password using bcrypt encoder.
     *
     * @param user
     */
    @Override
    public void encodePassword(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    @Override
    public User getUserById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException noSuchElementException) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }


    @Override
    public void delete(Integer id) throws UserNotFoundException {
        Long count = userRepository.countById(id);
        if (count == null || count == 0) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void changeEnableStatus(Integer id, Boolean status) {
        userRepository.updateEnabledStatus(id, status);
    }


    @Override
    public Boolean isUniqueEmail(Integer id, String email) {
        User userEmail = userRepository.findUserByEmail(email);
        if (userEmail == null) {
            return true;
        }

        boolean isNewUser = (id == null);
        if (isNewUser) {
            if (userEmail != null) {
                return false;
            }
        } else {
            if (!Objects.equals(userEmail.getId(), id)) {
                return false;
            }
        }
        return true;
    }
}
