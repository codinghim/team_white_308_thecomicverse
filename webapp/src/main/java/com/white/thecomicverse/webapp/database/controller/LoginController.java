
package com.white.thecomicverse.webapp.database.controller;
import com.white.thecomicverse.webapp.database.model.*;

import com.white.thecomicverse.webapp.database.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.white.thecomicverse.webapp.database.model.Login;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import java.security.MessageDigest;

@Controller // This means that this class is a Controller
@RequestMapping(path = "/login")
public class LoginController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private EpisodeRepository EpiRepository;

    @Autowired
    private EpisodeImageRepository episodeImageRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private DislikeRepository dislikeRepository;

    @Autowired
    private DerivedEpiRepository derivedEpiRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private DerivedLikesRepository derivedLikesRepository;


    @Autowired
    private SubscriptionRepository subscriptionRepository;

    /**
     * Sign up method
     */
    @RequestMapping(value = "/addLogin") // Map ONLY GET Requests
    public String addNewLogin(HttpServletRequest req, @RequestParam(value = "email") String email,
            @RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        for (Login login : loginRepository.findAll()) {
            if (login.getUsername().equals(username)) {
                return "redirect:/sign_up_form?userNameExist";
            }
            if (login.getEmail().equals(email)) {
                return "redirect:/sign_up_form?EmailExist";
            }
        }

        Login l = new Login();
        l.setEmail(email);
        l.setusername(username);
        l.setPassword(getHashedPassword(password));
        this.loginRepository.save(l);
        return "redirect:/home?username=" + username;

    }

    /**
     * Login method
     */
    @RequestMapping(value = "/checkLogin") // Map ONLY GET Requests
    public String checkLogin(HttpServletRequest req, @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        for (Login login : loginRepository.findAll()) {
            if (login.getUsername().equals(username)) {
                if (getHashedPassword(password).equals(login.getPassword())) {
                    return "redirect:/home?username=" + username;
                }

            }
        }

        return "redirect:/home?error";

    }

    @RequestMapping(value = "/getUser") // Map ONLY GET Requests
    public ModelAndView checkLogin(HttpServletRequest req, @RequestParam(value = "username") String username) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        Login l = new Login();

        for (Login login : loginRepository.findAll()) {
            if (login.getUsername().equals(username)) {
                l = login;
            }
        }

        ModelAndView mv = new ModelAndView("account_settings");

        mv.addObject("login", l);
        return mv;
    }

    @RequestMapping(value = "/changePassword") // Map ONLY GET Requests
    public ModelAndView changePassword(HttpServletRequest req, @RequestParam(value = "username") String username,
            @RequestParam(value = "newpassword") String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        System.out.println("change password called");

        Login l = new Login();
        for (Login login : loginRepository.findAll()) {
            if (login.getUsername().equals(username)) {
                l = login;
            }
        }
        l.setPassword(getHashedPassword(password));
        this.loginRepository.save(l);

        ModelAndView mv = new ModelAndView("account_settings");
        mv.addObject("login", l);
        return mv;

    }

    @GetMapping(path = "/allLogin")
    public @ResponseBody Iterable<Login> getAllLogin() {
        // This returns a JSON or XML with the users
        return loginRepository.findAll();
    }

    public String getHashedPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/deleteAccount")
    public ModelAndView deleteAccount(HttpServletRequest req, @RequestParam(value = "username") String username) {


        Login l = new Login();

        for (Login login : loginRepository.findAll()) {
            if (login.getUsername().equals(username)) {
                l = login;
            }
        }

        for (Series se : seriesRepository.findAll()){
            if (se.getAuthor().equalsIgnoreCase(username)){
                this.seriesRepository.delete(se);

                for (Episode epi : EpiRepository.findAll()){
                    if (epi.getSeriesID() == se.getSeriesID()){
                        this.EpiRepository.delete(epi);
                    }
                }
            }
        }

        for (Likes li : likesRepository.findAll()){
            if(li.getUsername().equalsIgnoreCase(username)){
                this.likesRepository.delete(li);
            }
        }

        for (Subscription sub : subscriptionRepository.findAll()){
            if (sub.getUsername().equalsIgnoreCase(username)){
                this.subscriptionRepository.delete(sub);
            }
        }

        for (Comments cm : commentsRepository.findAll()){
            if (cm.getAuthor().equalsIgnoreCase(username)){
                this.commentsRepository.delete(cm);
            }
        }








        this.loginRepository.delete(l);

        ModelAndView mv = new ModelAndView("home");

        mv.addObject("signout", l);
        return mv;


    }

}