package np.com.jp.app.ecommerce.controller;

import np.com.jp.app.ecommerce.entity.Role;
import np.com.jp.app.ecommerce.entity.User;
import np.com.jp.app.ecommerce.exception.UserNotFoundException;
import np.com.jp.app.ecommerce.repository.UserRepository;
import np.com.jp.app.ecommerce.service.UserService;
import np.com.jp.app.ecommerce.utils.FileUploadUtils;
import org.springframework.data.domain.Page;
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

    /**
     * When users are loaded we want to display only the first by delegating calls to given method.
     *
     * @param model
     * @return
     */
    @GetMapping("/users")
    public String listByFirstPage(Model model) {
        Integer pageNumberToStartFrom = 1;
        /**
         * firstName --> is the exact field of User entity
         */

        return listByPage(pageNumberToStartFrom, model, "firstName", "asc", null);
    }

    /**
     * This method list data by pages.
     *
     * @param pageNumber
     * @param model
     * @return templates to render users
     */
    @GetMapping("/users/page/{pageNumber}")
    public String listByPage(@PathVariable(name = "pageNumber") Integer pageNumber, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortOrder") String sortOrder,
                             @RequestParam(name = "searchKeyword") String searchKeyword) {


        System.out.println("Sort Field:" + sortField);
        System.out.println("Sort Order:" + sortOrder);

        Page<User> page = userService.listByPage(pageNumber, sortField, sortOrder, searchKeyword);

        List<User> listUsers = page.getContent();

/*        System.out.println("Page number =  " + pageNumber);
        System.out.println("Page ize =  " + page.getTotalPages());
        System.out.println("Total page =  " + page.getTotalPages());*/


        /**
         * Calculates the range of items displayed on the current page of a paginated list
         * and adds this information to the Model object for rendering in the view.
         *
         * - Calculates the start and end indices of items on the current page.
         * - Ensures the end index does not exceed the total number of items.
         * - Adds the calculated range and total item count to the Model for use in the view.
         */

        long startCount = (pageNumber - 1) * UserService.DATA_PER_PAGE + 1;
        long endCount = startCount + UserService.DATA_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }


        /**
         * Set reverse sort order.
         */

        String reverseSortOrder = sortOrder.equalsIgnoreCase("asc") ? "desc" : "asc";

        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("sortField", sortField);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
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
            String uploadDIR = "user-photos/" + savedUser.getId();
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
