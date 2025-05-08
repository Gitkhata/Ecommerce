package np.com.jp.app.ecommerce.user;

import np.com.jp.app.ecommerce.repository.RoleRepository;
import np.com.jp.app.ecommerce.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreatFirstRole() {
        Role roleAdmin = new Role("ROLE_ADMIN", "Manages everything");
        Role savedRole = roleRepository.save(roleAdmin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRemainingRoles() {
        Role roleSalesperson = new Role("ROLE_SALESPERSON", "Manage products, customers, shipping, orders and sales report");
        Role roleEditor = new Role("ROLE_EDITOR", "Manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("ROLE_SHIPPER", "View products, view orders and update order status");
        Role roleAssistant = new Role("ROLE_ASSISTANT", "Manage questions and reviews");

        roleRepository.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
//        logger.info("Role saved: }" + roleSalesperson.toString(), roleEditor.toString(), roleShipper.toString(), roleAssistant.toString());
    }
}
