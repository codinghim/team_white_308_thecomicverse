package com.white.thecomicverse.webapp.database.controller;

import com.white.thecomicverse.webapp.database.model.*;

import com.white.thecomicverse.webapp.database.repositories.EpisodeImageRepository;
import com.white.thecomicverse.webapp.database.repositories.EpisodeRepository;
import com.white.thecomicverse.webapp.database.repositories.SeriesRepository;
import com.white.thecomicverse.webapp.database.repositories.SubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.white.thecomicverse.webapp.database.model.Subscription;
import com.white.thecomicverse.webapp.database.repositories.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

@Controller // This means that this class is a Controller
@RequestMapping(path = "/series")
public class SeriesController {
    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private EpisodeImageRepository episodeImageRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;



    @RequestMapping(value = "/createSeries") // Map ONLY GET Requests
    public ModelAndView createSeries(HttpServletRequest req, @RequestParam(value = "seriesName") String seriesName,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "categories") String categories, @RequestParam(value = "author") String author,
            @RequestParam(value = "thumbnail") String thumbnail, RedirectAttributes redir) {


        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesName().equals(seriesName)) {

                ModelAndView mv2 = new ModelAndView("create_comic_series?seriesNameExist");
                return mv2;
            }
        }

        byte[] b = thumbnail.getBytes();
        Series newSeries = new Series();
        newSeries.setSeriesName(seriesName);
        newSeries.setAuthor(author);
        newSeries.setDescription(description);
        newSeries.setCategories(categories);
        newSeries.setThumbnail(b);
        newSeries.setImageData(null);
        newSeries.setSumLikes(0);
        this.seriesRepository.save(newSeries);

        return getMySeries(req, author);
    }


    @RequestMapping(value = "/deleteSeries") // Map ONLY GET Requests
    public ModelAndView deleteEpisode(HttpServletRequest req, @RequestParam(value = "username") String username,
            @RequestParam(value = "seriesID") int seriesID) {

        List<Integer> episodeIDList = new ArrayList<Integer>();

        for (Episode epi : episodeRepository.findAll()) {
            if (epi.getSeriesID() == seriesID) {
                episodeIDList.add(epi.getEpisodeID());
                episodeRepository.delete(epi);
            }
        }

        for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
            for (int epiID : episodeIDList) {
                if (episodeImage.getEpisodeID() == epiID) {
                    episodeImageRepository.delete(episodeImage);
                }
            }
        }

        for (Series s : seriesRepository.findAll()) {
            if (seriesID == s.getSeriesID()) {
                seriesRepository.delete(s);
            }
        }

        return getMySeries(req, username);

    }


    @GetMapping(path = "/view_series")
    public @ResponseBody ModelAndView viewSeries(HttpServletRequest req,
                                                 @RequestParam(value = "seriesID") int seriesID, @RequestParam(value = "username") String username) {

        return checkSubscription(req, username, seriesID);
    }

    @RequestMapping(value="/addSubscription")
    public ModelAndView subscribe (HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {

        ModelAndView mv = new ModelAndView("manage_my_series");


        Date d = new Date();
        Subscription sub = new Subscription();
        sub.setDate(d.toGMTString());
        sub.setSeriesID(seriesID);
        sub.setUsername(username);
        this.subscriptionRepository.save(sub);

        return checkSubscription(req, username, seriesID);
    }

    @RequestMapping(value="/deleteSubscription")
    public ModelAndView unsubscribe (HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        ModelAndView mv = new ModelAndView("manage_my_series");

        Subscription sub = new Subscription();
        for (Subscription s : subscriptionRepository.findAll()){
            if (s.getSeriesID() == seriesID){
                if (s.getUsername().equals(username)){
                    sub = s;
                }
            }
        }
        this.subscriptionRepository.delete(sub);

        return checkSubscription(req, username, seriesID);

    }

    @RequestMapping(value="/checkSubscription")
    public ModelAndView checkSubscription (HttpServletRequest req, @RequestParam(value = "username") String username, @RequestParam(value = "SeriesID") int seriesID) {

        updateSumLikes(seriesID);

        boolean subs = false;
        Subscription sub = new Subscription();
        for (Subscription s : subscriptionRepository.findAll()){
            if (s.getSeriesID() == seriesID){
                if (s.getUsername().equals(username)){
                    subs = true;
                }
            }
        }


        ModelAndView mv = new ModelAndView("view_comic_series");
        List<Episode> episodeList = new ArrayList<>();



        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesID() == seriesID) {
                series.setImageData(new String(series.getThumbnail()));
                mv.addObject("series", series);
            }
        }

        for (Episode episode : episodeRepository.findAll()) {
            if (episode.getSeriesID() == seriesID) {
                episode.setImageData(new String(episode.getThumbnail()));
                episodeList.add(episode);
            }
        }

        mv.addObject("sub", subs);

        mv.addObject("episodes", episodeList);

        return mv;
    }

    @RequestMapping(value = "/subscriptedSeries")
    public ModelAndView getSeriesBySubscription(HttpServletRequest req,
            @RequestParam(value = "username") String username) {

        List<Series> s = new ArrayList<Series>();
        List<Integer> seriesIDs = new ArrayList<Integer>();

        for (Subscription sub : subscriptionRepository.findAll()) {
            if (sub.getUsername().equalsIgnoreCase(username)) {
                for (Series series : seriesRepository.findAll()) {
                    if (series.getSeriesID() == sub.getSeriesID()) {
                        s.add(series);
                        break;
                    }
                }
            }
        }

        ModelAndView mv = new ModelAndView("home");
        mv.addObject("series", s);
        return mv;

    }

    // For Browse
    @RequestMapping(value = "/allSeries")
    public ModelAndView getAllSeries(HttpServletRequest req, @RequestParam(value = "username") String username) {
        List<Series> s = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            series.setImageData(new String(series.getThumbnail()));
            s.add(series);
        }

        ModelAndView mv = new ModelAndView("browse");
        mv.addObject("series", s);
        mv.addObject("username",username);
        return mv;
    }

    @RequestMapping(value = "/search")
    public ModelAndView search(HttpServletRequest req, String searchOption, String keyword){

      ModelAndView mv = new ModelAndView("browse");

      mv.addObject("series", searchByOption(req, searchOption, keyword));
      mv.addObject("th_searchOption", searchOption);
      mv.addObject("th_keyword", keyword);

      return mv;

    }

    public List<Series> searchByOption(HttpServletRequest req,
            @RequestParam(value = "searchOption") String searchOption,
            @RequestParam(value = "keyword") String keyword) {

        List<Series> seriesList = new ArrayList<Series>();

        if (searchOption.equals("title")) {
            seriesList =  getSeriesByTitle(req, searchOption, keyword);
        } else if (searchOption.equals("author")) {
            seriesList =  getSeriesByAuthor(req, searchOption, keyword);
        } else{
          seriesList =  getSeriesByAll(req, searchOption, keyword);
        }
        return seriesList;
    }

    public List<Series> getSeriesByAll(HttpServletRequest req,  @RequestParam(value = "searchOption") String searchOption, @RequestParam(value = "authorName") String seriesInfo) {

        List<Series> s = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            if (series.getAuthor().toLowerCase().contains(seriesInfo.toLowerCase())) {
                series.setImageData(new String(series.getThumbnail()));
                s.add(series);
            } else if (series.getSeriesName().toLowerCase().contains(seriesInfo.toLowerCase())) {
                series.setImageData(new String(series.getThumbnail()));
                s.add(series);
            }
        }

        return s;
    }

    public List<Series> getSeriesByTitle(HttpServletRequest req, @RequestParam(value = "searchOption") String searchOption, @RequestParam(value = "seriesName") String seriesName) {

        List<Series> s = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesName().toLowerCase().contains(seriesName.toLowerCase())) {
                series.setImageData(new String(series.getThumbnail()));
                s.add(series);

            }
        }

        return s;
    }

    public List<Series> getSeriesByAuthor(HttpServletRequest req,  @RequestParam(value = "searchOption") String searchOption,
            @RequestParam(value = "authorName") String seriesAuthor) {
        List<Series> s = new ArrayList<Series>();

        for (Series series : seriesRepository.findAll()) {
            if (series.getAuthor().toLowerCase().contains(seriesAuthor.toLowerCase())) {
                series.setImageData(new String(series.getThumbnail()));
                s.add(series);
            }
        }

        return s;
    }


    @RequestMapping(value = "/categoryBrowse")
    public ModelAndView getSeriesByCategories(HttpServletRequest req,
    @RequestParam(value = "searchOption") String searchOption,  @RequestParam(value = "keyword") String keyword,
    @RequestParam(value = "category1") String category1, @RequestParam(value = "category2") String category2,
     @RequestParam(value = "category3") String category3,@RequestParam(value = "category4") String category4,
      @RequestParam(value = "category5") String category5,@RequestParam(value = "category6") String category6){

        List<String> categoryList = new ArrayList<String>();

        if(!category1.equals("")){
          categoryList.add(category1);
        }
        if(!category2.equals("")){
          categoryList.add(category2);
        }
        if(!category3.equals("")){
          categoryList.add(category3);
        }
        if(!category4.equals("")){
          categoryList.add(category4);
        }
        if(!category5.equals("")){
          categoryList.add(category5);
        }
        if(!category6.equals("")){
          categoryList.add(category6);
        }

        // Search only
        if (categoryList.size() == 0){
          return search( req, searchOption, keyword);
        }

        // Apply filters
        List<Series> searchByseriesList = new ArrayList<Series>();
        searchByseriesList = searchByOption(req, searchOption, keyword);

        List<Series> s = new ArrayList<Series>();

        for (Series series : searchByseriesList) {
            if (categoryList.contains(series.getCategories())) {
                series.setImageData(new String(series.getThumbnail()));
                s.add(series);
            }
        }


        ModelAndView mv = new ModelAndView("browse");

        mv.addObject("series", s);

        mv.addObject("th_category1", category1);
        mv.addObject("th_category2", category2);
        mv.addObject("th_category3", category3);
        mv.addObject("th_category4", category4);
        mv.addObject("th_category5", category5);
        mv.addObject("th_category6", category6);

        mv.addObject("th_searchOption", searchOption);
        mv.addObject("th_keyword", keyword);


        return mv;

        }



    @RequestMapping(value = "/mySeries")
    public ModelAndView getMySeries(HttpServletRequest req, @RequestParam(value = "username") String author) {

        List<Series> seriesList = new ArrayList<Series>();


        for (Series series : seriesRepository.findAll()) {
            series.setImageData(new String(series.getThumbnail()));
        }

        for (Series s : seriesRepository.findAll()) {
            if (s.getAuthor().equals(author)) {
                seriesList.add(s);
            }
        }

        ModelAndView mv = new ModelAndView("manage_my_series");
        mv.addObject("series", seriesList);

        return mv;
    }


    public void updateSumLikes(int SeriesID){

      System.out.println("sum likes here");

      for (Series se : seriesRepository.findAll()){
        int sum = 0;

        if(se.getSeriesID() == SeriesID){
          se.setSumLikes(0);
          //System.out.println("sum likes before" + se.getSumLikes());

          for (Episode ep : episodeRepository.findAll()){
              if (ep.getSeriesID() == se.getSeriesID()){
                //System.out.println("this epi num" + ep.getNumLikes());
                  sum  = sum + ep.getNumLikes();
                  //System.out.println("sum likes en cours" + sum);
              }
          }
          se.setSumLikes(sum);
          //System.out.println("sum likes done" + se.getSumLikes());
          break;
        }

      }

    }



    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Series> getAllSeries() {
        // This returns a JSON or XML with the users
        return seriesRepository.findAll();
    }


    // Clean code until here
/*
    //loged in user
    @GetMapping(path = "/home_series")
    public @ResponseBody ModelAndView homeSeries(HttpServletRequest req, @RequestParam(value = "username") String username) {
        // System.out.println("view_Epi :series ID = " + seriesID);
        List<Series> s = new ArrayList<Series>();
        for (Subscription sub : subscriptionRepository.findAll()) {
            if (sub.getUsername().equals(username)) {
                for (Series se : seriesRepository.findAll()){
                    if (se.getSeriesID() == sub.getSeriesID()){
                        s.add(se);
                    }
                }

            }
        }

        ModelAndView mv = new ModelAndView("home");
        // mv.addObject(s);
        mv.addObject("series", s);
        mv.addObject("username", username);
        return mv;

    }*/

    //unlogged in user
    /*@GetMapping(path = "/seriesByNumLikes")
    public @ResponseBody ModelAndView homeSeries2(HttpServletRequest req) {
        List<Series> s = new ArrayList<Series>();


        for (Series se : seriesRepository.findAll()){
            se.setSumLikes(0);
            for (Episode ep : episodeRepository.findAll()){
                if (ep.getSeriesID() == se.getSeriesID()){
                    se.setSumLikes(se.getSumLikes() + ep.getNumLikes());
                }
            }
        }


        for (Series se : seriesRepository.findAll()){
          updateSumLikes(se.getSeriesID());
          s.add(se);
        }

        s.sort(new Comparator<Series>() {
            @Override
            public int compare(Series o1, Series o2) {
                Integer num1 = o1.getSumLikes();
                Integer num2 = o2.getSumLikes();
                return num1.compareTo(num2);
            }
        });

        ModelAndView mv = new ModelAndView("home");
        // mv.addObject(s);
        mv.addObject("series", s);
        mv.addObject("username", null);
        return mv;

    }*/



    /*@GetMapping(path = "/editSeries")
    public @ResponseBody ModelAndView editSeries(HttpServletRequest req,
                                                 @RequestParam(value = "seriesID") int seriesID) {
        // System.out.println("view_Epi :series ID = " + seriesID);

        ModelAndView mv = new ModelAndView("edit_series");

        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesID() == seriesID) {
                series.setImageData(new String(series.getThumbnail()));
                mv.addObject("series", series);
            }
        }

        return mv;
    }

    @GetMapping(path = "/updateSeries")
    public @ResponseBody ModelAndView updateSeries(HttpServletRequest req,
                                                   @RequestParam(value = "authorname") String authorName,
                                                   @RequestParam(value = "seriesID") int seriesID,
                                                   @RequestParam(value = "seriesName") String seriesName,
                                                   @RequestParam(value = "description") String description,
                                                   @RequestParam(value = "thumbnail") String thumbnail,
                                                   @RequestParam(value = "category") String category) {
        System.out.println("view_Epi :series ID = " + seriesID);
        System.out.println("view_Epi :series name = " + seriesName);
        System.out.println("view_Epi :series des = " + description);
        System.out.println("view_Epi :series thu = " + thumbnail.substring(0,10));
        System.out.println("view_Epi :series category = " + category);


        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesID() == seriesID) {
                series.setSeriesName(seriesName);
                series.setDescription(description);
                byte[] b = thumbnail.getBytes();
                series.setThumbnail(b);
                series.setCategories(category);
            }
        }

        return getMySeries(req, authorName);
    }*/



}
