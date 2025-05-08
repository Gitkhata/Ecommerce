package np.com.jp.app.ecommerce.controller;

import np.com.jp.app.ecommerce.entity.Role;
import np.com.jp.app.ecommerce.entity.User;
import np.com.jp.app.ecommerce.exception.UserNotFoundException;
import np.com.jp.app.ecommerce.repository.UserRepository;
import np.com.jp.app.ecommerce.service.UserService;
import np.com.jp.app.ecommerce.utils.FileUploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("/users/new")
    public String createUser(Model model) {
        List<Role> listRoles = userService.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create User");
        model.addAttribute("listRoles", listRoles);

        return "create-user";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam(name = "useImage") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDIR = "user-image/" + savedUser.getId();
            FileUploadUtils.cleanDir(uploadDIR);
            FileUploadUtils.saveFile(uploadDIR, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            List<Role> listRoles = userService.listRoles();

            model.addAttribute("pageTitle", "Update User (Id: " + id + ")");
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("user", user);
            return "create-user";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            String userDeletionMessage = "The user ID " + id + " has been deleted successfully.";
            redirectAttributes.addFlashAttribute("message", userDeletionMessage);
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{enableStatus}")
    public String updateEnableStatus(RedirectAttributes redirectAttributes, @PathVariable(name = "id") Integer id, @PathVariable(name = "enableStatus") Boolean status) {
        userService.changeEnableStatus(id, status);
        String statusMessage = status ? "enabled" : "disabled";
        String message = "The user Id " + id + " has been " + statusMessage;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }
}
