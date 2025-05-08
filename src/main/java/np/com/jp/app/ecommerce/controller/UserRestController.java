package np.com.jp.app.ecommerce.controller;

import np.com.jp.app.ecommerce.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/checkEmail")
    public String checkDuplicateMail(@Param("id") Integer id, @Param("email") String email) {
        return userService.isUniqueEmail(id, email) ? "Email OK" : "Duplicated";
    }
}

