package np.com.jp.app.ecommerce.user;


import np.com.jp.ecommerce.common.entity.Role;
import np.com.jp.ecommerce.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User jeevan = new User("jp@jp.np", "jp", "Jeevan", "P");
        jeevan.addRole(roleAdmin);

        User savedUser = userRepository.save(jeevan);
        assertThat(savedUser.getId()).isGreaterThan(0);
        System.out.println("User saved: " + savedUser.toString());
    }

    @Test
    public void testCreateNewUserWithTwoRole() {
        User userRavi = new User("ravi@gmail.com", "ravi", "Ravi", "Kumar");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userRavi.addRole(roleEditor);
        userRavi.addRole(roleAssistant);

        User savedUser = userRepository.save(userRavi);
        assertThat(savedUser.getId()).isGreaterThan(0);
        System.out.println("User saved: " + savedUser.toString());
    }

    @Test
    public void testListAllUsers() {
        List<User> usersList = userRepository.findAll();
        usersList.forEach(user -> System.out.println(user.toString()));
    }

    @Test
    public void testGetUserById() {
        Optional<User> userOptional = userRepository.findById(1);
        assertThat(userOptional).isPresent();
        System.out.println("User  " + userOptional.get());

        User jeevan = userOptional.get();
        System.out.println(jeevan);
        assertThat(jeevan).isNotNull();
        System.out.println("User  " + jeevan);
    }

    @Test
    public void testUpdateUserDetails() {
        User jp = userRepository.findById(1).get();
        jp.setEnabled(true);
        jp.setEmail("jp@np.com");

        User updateUser = userRepository.save(jp);
        System.out.println("User " + updateUser);
    }

    @Test
    public void testUpdateUserRoles() {
        User jp = userRepository.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);

        jp.getRoles().remove(roleEditor);
        jp.addRole(roleSalesperson);

        var roleUpdateForUser = userRepository.save(jp);
        System.out.println("Role updated for " + roleUpdateForUser);
    }

    @Test
    public void testDeleteUser() {
        Integer id = 2;
        userRepository.deleteById(id);

        Optional<User> user = userRepository.findById(2);
        assertThat(user).isNotPresent();
    }

    @Test
    public void testFindUserByEmail() {
        String email = "jeevan@np.com";
        User user = userRepository.findUserByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    void testCountUserById() {
        Integer id = 5;
        Long countById = userRepository.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 11;
        userRepository.updateEnabledStatus(id, true);
    }

}
