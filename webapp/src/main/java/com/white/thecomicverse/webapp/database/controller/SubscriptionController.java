//DONT USE SUBSCRIPTION CONTROLLER. ALL THE METHODS ARE IN SERIES CONTROLLER

/*
package com.white.thecomicverse.webapp.database.controller;


import com.white.thecomicverse.webapp.database.model.Login;
import com.white.thecomicverse.webapp.database.repositories.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.white.thecomicverse.webapp.database.model.Series;
import com.white.thecomicverse.webapp.database.model.Subscription;

import com.white.thecomicverse.webapp.database.repositories.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/subs")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private SeriesRepository seriesRepository;

    @RequestMapping(value="/addSubscription")
    public ModelAndView subscribe (HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {
        Date d = new Date();
        Subscription sub = new Subscription();
        sub.setDate(d.toGMTString());
        sub.setSeriesID(seriesID);
        sub.setUsername(username);
        this.subscriptionRepository.save(sub);

        List<Series> se = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            series.setImageData(new String(series.getThumbnail()));
            se.add(series);
        }

        ModelAndView mv = new ModelAndView("browse");
        mv.addObject("series", se);
        return mv;
    }

    @RequestMapping(value="/deleteSubscription")
    public ModelAndView unsubscribe (HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        Subscription sub = new Subscription();
        for (Subscription s : subscriptionRepository.findAll()){
            if (s.getSeriesID() == seriesID){
                if (s.getUsername().equals(username)){
                    sub = s;
                }
            }
        }

        this.subscriptionRepository.delete(sub);

        List<Series> se = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            series.setImageData(new String(series.getThumbnail()));
            se.add(series);
        }

        ModelAndView mv = new ModelAndView("browse");
        mv.addObject("series", se);
        return mv;




    }

    @RequestMapping(value="/checkAndSubscribe")
    public ModelAndView checkAndSub(HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        Subscription sub = new Subscription();
        for (Subscription s : subscriptionRepository.findAll()){
            if (s.getSeriesID() == seriesID){
                if (s.getUsername().equals(username)){
                    return unsubscribe(req, username, seriesID);
                }
            }
        }


        return subscribe(req, username, seriesID);


    }

    @RequestMapping(value="/checkSubscription")
    public ModelAndView checkSub(HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        Subscription sub = new Subscription();
        for (Subscription s : subscriptionRepository.findAll()){
            if (s.getSeriesID() == seriesID){
                if (s.getUsername().equals(username)){
                    return unsubscribe(req, username, seriesID);
                }
            }
        }


        return subscribe(req, username, seriesID);


    }

    @GetMapping(path = "/allSub")
    public @ResponseBody Iterable<Subscription> getAllSub() {
        // This returns a JSON or XML with the users
        return this.subscriptionRepository.findAll();
    }



/*
    @RequestMapping(value="/addLogin") // Map ONLY GET Requests
    public String addNewLogin (HttpServletRequest req, @RequestParam(value = "email") String email, @RequestParam(value = "username") String username
            ,@RequestParam(value = "password") String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        for (Login login : loginRepository.findAll()){
            if (login.getUsername().equals(username)){
                return "redirect:/sign_up_form?userNameExist";
            }
            if (login.getEmail().equals(email)){
                return "redirect:/sign_up_form?EmailExist";
            }
        }

        Login l = new Login();
        l.setEmail(email);
        l.setusername(username);
        l.setPassword(password);
        this.loginRepository.save(l);
        return "redirect:/home";

    }



}

*/