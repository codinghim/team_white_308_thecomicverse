
package com.white.thecomicverse.webapp.database.controller;

import com.white.thecomicverse.webapp.database.model.*;
import com.white.thecomicverse.webapp.database.repositories.EpisodeImageRepository;
import com.white.thecomicverse.webapp.database.repositories.LikesRepository;
import com.white.thecomicverse.webapp.database.repositories.SeriesRepository;
import com.white.thecomicverse.webapp.database.repositories.DislikeRepository;
import com.white.thecomicverse.webapp.database.repositories.DerivedEpiRepository;
import com.white.thecomicverse.webapp.database.repositories.CommentsRepository;
import com.white.thecomicverse.webapp.database.repositories.DerivedLikesRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.white.thecomicverse.webapp.database.repositories.EpisodeRepository;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

//@RequestMapping(path = "/episode") already exists somewhere
@Controller
@RequestMapping(path = "/episodes")
public class EpisodeController {

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

    @RequestMapping(value = "/upload_episode")
    public ModelAndView uploadEpisode(HttpServletRequest req, @RequestParam(value = "username") String username) {
        ModelAndView mv = new ModelAndView("upload_episode");

        List<Series> seriesList = new ArrayList<>();
        for (Series series : seriesRepository.findAll()) {
            if (series.getAuthor().equals(username)) {
                // System.out.println(series.getSeriesID());
                seriesList.add(series);
            }
        }
        // ra.addFlashAttribute("series", seriesList);
        mv.addObject("series", seriesList);
        return mv;

    }



    @RequestMapping(value = "/addEpisode") // Map ONLY GET Requests
    public ModelAndView addEpisode(HttpServletRequest req, @RequestParam(value = "seriesID") int SeriesID,
            @RequestParam(value = "episodeName") String episodeName,
            @RequestParam(value = "thumbnail") String thumbnail, @RequestParam(value = "canDerived") int canDerived,
    @RequestParam(value = "episodeImage") String image){
                                   // {

        //System.out.println("canDerived is "+ canDerived );

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeName().equals(episodeName)) {
                return new ModelAndView("redirect:/upload_episode?episodeExist");
            }

        }

        //ModelAndView mv = new ModelAndView("home");

        byte[] thumbnailByteArr = thumbnail.getBytes();
        Date d = new Date();
        Episode epi = new Episode();
        epi.setSeriesID(SeriesID);
        epi.setEpisodeName(episodeName);
        epi.setNumDislikes(0);
        epi.setNumLikes(0);
        epi.setNumView(0);
        epi.setThumbnail(thumbnailByteArr);
        epi.setDateCreated(d.toGMTString());


        if (canDerived == 0){
            epi.setCanDerive(false);
        }
        else{
            epi.setCanDerive(true);
        }



        int max = -1;

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == SeriesID && episode.getIndices() > max) {
                max = episode.getIndices();
            }
        }
        epi.setIndices(max + 1);
        this.EpiRepository.save(epi);
        addImage(epi.getEpisodeID(), image);

        return allEpisodes(req, SeriesID);

       // return mv;

    }


    @RequestMapping(value = "/loadOrignalEpi") // Map ONLY GET Requests
    public ModelAndView loadOriginalEpi(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID){

      System.out.println("loadORIGINAL HERE");
      System.out.println(episodeID);

      ModelAndView mv = new ModelAndView("drawing_page_derivedEpi");

      mv.addObject("episodeID", episodeID);
      return mv;

}


    @RequestMapping(value = "/addDerivedEpi") // Map ONLY GET Requests
    public ModelAndView addDrivedEpi(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                   @RequestParam(value = "username") String username,
                                     @RequestParam(value = "endingScene") String ending ){

        //System.out.println("add Epi:epi ID = " + episodeID);
        //System.out.println("add Epi:username = " + username);

        DerivedEpi dEpi = new DerivedEpi();

        Episode epi = new Episode();

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeID() == episodeID){
                epi = episode;
                break;
            }
        }

        dEpi.setAuthor(username);
        dEpi.setOriginalID(episodeID);
        dEpi.setNumLikes(0);

        byte[] endingByteArr = ending.getBytes();

        dEpi.setEndingScene(endingByteArr);

        derivedEpiRepository.save(dEpi);

        return readEpisode2(req, episodeID, username);

    }


    /**
     * retrieve all episodes with derived episodes
     */
    @RequestMapping(value = "/allEpiWithDerivedEpi") // Map ONLY GET Requests
    public ModelAndView allEpisodesWDerived(HttpServletRequest req) {

        ModelAndView mv = new ModelAndView("browse_derivedEpi");

        List<Episode> episodeList = new ArrayList<>();

        for (Episode episode : EpiRepository.findAll()) {

            if (episode.getCanDerive() == true) {
                episode.setImageData(new String(episode.getThumbnail()));
                episodeList.add(episode);
            }

        }

        mv.addObject("episodes", episodeList);
        mv.addObject("episodes", episodeList);
        return mv;
    }


    /**
     * redirect to previous episode
     **/
    @RequestMapping(value = "/prevEp")
    public ModelAndView prevEpisode(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
            @RequestParam(value = "episodeIndex") int episodeIndex, @RequestParam(value = "username") String username) {

        int seriesID = -1;
        int prevEpID = -1;
        int epiIndex = episodeIndex;

        for (Episode episode : EpiRepository.findAll()) {
            if (episodeID == episode.getEpisodeID()) {
                seriesID = episode.getSeriesID();
                break;
            }
        }


        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == seriesID && episode.getIndices() == (episodeIndex - 1)) {
                prevEpID = episode.getEpisodeID();
                break;
            }
        }

        return readEpisode(req, prevEpID, username );

    }

    /**
     * redirect to next episode
     **/
    @RequestMapping(value = "/nextEp") // Map ONLY GET Requests
    public ModelAndView nextEpisode(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "episodeIndex") int episodeIndex, @RequestParam(value = "username") String username) {
        int seriesID = -1;
        int nextEpID = -1;
        int epiIndex = episodeIndex;


        for (Episode episode : EpiRepository.findAll()) {
            if (episodeID == episode.getEpisodeID()) {
                seriesID = episode.getSeriesID();
                break;
            }
        }

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == seriesID && episode.getIndices() == (episodeIndex + 1)) {
                nextEpID = episode.getEpisodeID();
                break;
            }
        }

        return readEpisode(req,nextEpID, username );

    }


    @RequestMapping(value = "/addLike") // Map ONLY GET Requests
    public ModelAndView addLikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {
        Likes l = new Likes();
        l.setEpisodeID(episodeID);
        l.setUsername(username);

        for (Episode epi : EpiRepository.findAll()){
            if (epi.getEpisodeID() == episodeID){
                epi.setNumLikes(epi.getNumLikes() + 1 );
                break;
            }

        }


        likesRepository.save(l);
        System.out.println();

        return readEpisode(req, episodeID, username);
    }

    @RequestMapping(value = "/removeLike") // Map ONLY GET Requests
    public ModelAndView removeLikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {
        for (Likes like : likesRepository.findAll()){
            if (like.getEpisodeID() == episodeID){

                if (like.getUsername().equalsIgnoreCase(username)) {
                    likesRepository.delete(like);
                    break;
                }
            }
        }

        for (Episode epi : EpiRepository.findAll()){
            if (epi.getEpisodeID() == episodeID){
                epi.setNumLikes(epi.getNumLikes() - 1 );
            }
        }


        return readEpisode(req, episodeID, username);
    }


    @RequestMapping(value = "/addComments") // Map ONLY GET Requests
    public ModelAndView addComments(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                 @RequestParam(value = "username") String username,
                                    @RequestParam(value = "text") String text) {


        Comments c = new Comments();
        c.setAuthor(username);
        c.setText(text);
        c.setEpisodeID(episodeID);

        commentsRepository.save(c);

        return readEpisode(req, episodeID, username);
    }

    @RequestMapping(value = "/removeComments") // Map ONLY GET Requests
    public ModelAndView removeComments(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                       @RequestParam(value = "username") String username,
                                       @RequestParam(value = "commentID") int commentID) {

        for (Comments comments : commentsRepository.findAll()){
            if (comments.getCommentID() == commentID){
                commentsRepository.delete(comments);
            }
        }

        return readEpisode(req, episodeID, username);
    }

    @RequestMapping(value = "/addDerivedLikes") // Map ONLY GET Requests
    public ModelAndView addDerivedLikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                        @RequestParam(value = "username") String username) {



        DerivedLikes dl = new DerivedLikes();
        dl.setEpisodeID(episodeID);
        dl.setUsername(username);

        int oriEpiID = 0;
        for (DerivedEpi de : derivedEpiRepository.findAll()){
            if (de.getDerivedEpiID() == episodeID){
                oriEpiID = de.getOriginalID();
                de.setNumLikes(de.getNumLikes() + 1);
            }
        }

        derivedLikesRepository.save(dl);

        return readEpisode(req, oriEpiID, username);
    }

    @RequestMapping(value = "/removeDerivedLike") // Map ONLY GET Requests
    public ModelAndView removeDerivedLikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {

        for (DerivedLikes dlike : derivedLikesRepository.findAll()){
            if (dlike.getEpisodeID() == episodeID){

                if (dlike.getUsername().equalsIgnoreCase(username)) {
                    derivedLikesRepository.delete(dlike);
                    break;
                }
            }
        }
        int oriEpiID = 0;
        for (DerivedEpi de : derivedEpiRepository.findAll()){
            if (de.getDerivedEpiID() == episodeID){
                oriEpiID = de.getOriginalID();
                de.setNumLikes(de.getNumLikes() - 1);
            }
        }


        return readEpisode(req, oriEpiID, username);
    }




    /**
     * retrieve episodes of a specific series
     */
    @RequestMapping(value = "/allEpisodes") // Map ONLY GET Requests
    public ModelAndView allEpisodes(HttpServletRequest req, @RequestParam(value = "seriesID") int seriesID) {
        // System.out.println("ALL EPISODES of series ID = " + seriesID + " in int " +
        // Integer.parseInt(seriesID));
        ModelAndView mv = new ModelAndView("manage_my_episodes");
        List<Episode> episodeList = new ArrayList<>();

        for (Series series : seriesRepository.findAll()) {
            if (series.getSeriesID() == seriesID) {
                series.setImageData(new String(series.getThumbnail()));
                mv.addObject("series", series); // single serie
                break;
            }
        }

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == seriesID) {
                episode.setImageData(new String(episode.getThumbnail()));
                //System.out.println(episode.getImageData());
                episodeList.add(episode);

            }
        }

        mv.addObject("episodes", episodeList);
        return mv;
    }


    /**
    read Episode with top 3(most liked) derived epi
     */
    @RequestMapping(value = "/readEpisode") // Map ONLY GET Requests
    public ModelAndView readEpisode(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {

        //System.out.println("READEPI: received episode ID: " + episodeID);


        ModelAndView mv = new ModelAndView("read_episode"); // ("redirect:/read_episode");

        Episode epi = new Episode();
        List<EpisodeImage> imageList = new ArrayList<>();

        //Initalize
        int epiIndex = -1;
        int seriesID = -1;

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeID() == episodeID) {
                epiIndex = episode.getIndices();
                seriesID = episode.getSeriesID();
                mv.addObject("episode", episode);
                for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
                    if (episodeImage.getEpisodeID() == episodeID) {
                        episodeImage.setImageString(new String(episodeImage.getImageData()));
                        imageList.add(episodeImage.getIndices(), episodeImage);
                    }
                }

                mv.addObject("imageList", imageList);

                for (Series series : seriesRepository.findAll()) {
                    if (series.getSeriesID() == episode.getSeriesID()) {
                        series.setImageData(new String(series.getThumbnail()));
                        mv.addObject("series", series); // single serie
                        break;
                    }
                }

            }
        }


        boolean l = false;
        boolean dl = false;

        for (Likes like : likesRepository.findAll()){
            if (like.getEpisodeID() == episodeID){
                if (like.getUsername().equalsIgnoreCase(username)) {
                    l = true;
                    break;
                }
            }
        }

        for (Dislike dislike : dislikeRepository.findAll()){
            if (dislike.getEpisodeID() == episodeID){
                if (dislike.getUsername().equalsIgnoreCase(username)) {
                    dl = true;
                    break;
                }
            }
        }

        mv.addObject("like", l);
        mv.addObject("dislike", dl);

        List<Comments> commentsList = new ArrayList<>();

        for (Comments c : commentsRepository.findAll()){
            if (c.getEpisodeID() == episodeID){
                commentsList.add(c);
            }
        }

        mv.addObject("comments", commentsList);


        List<DerivedEpi> dEpiList = new ArrayList<>();
        List<DerivedEpi> temp_dEpiList = new ArrayList<>();

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeID() == episodeID) {
                mv.addObject("episode", episode);
                for (DerivedEpi dEpi : derivedEpiRepository.findAll()) {
                    if (dEpi.getOriginalID() == episodeID) {
                        dEpi.setImageData(new String(dEpi.getEndingScene()));
                        dEpiList.add(dEpi);
                    }
                }
                // System.out.println("ImageList: "+ imageList);

                temp_dEpiList = sortDerivedEpiByNumLikes(dEpiList);

                if(temp_dEpiList.size()>3){
                  dEpiList = temp_dEpiList.subList(0, 3);
                } else{
                  dEpiList = temp_dEpiList;
                }

                mv.addObject("dEpiList", dEpiList);
                // ra.addFlashAttribute("imageList",imageList);

            }
        }

        int max = -1;

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == seriesID && episode.getIndices() > max) {
                max = episode.getIndices();
            }
        }

        boolean first = false;
        boolean last = false;

        if(epiIndex == 0){
          first = true;
        }

        if(epiIndex >= max){
          last = true;
        }

        mv.addObject("firstEpi", first);
        mv.addObject("lastEpi", last);

        return mv;
    }


    /**
    read Episode with all derived epi
     */
    @RequestMapping(value = "/readEpisode2") // Map ONLY GET Requests
    public ModelAndView readEpisode2(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {

        //System.out.println("READEPI: received episode ID: " + episodeID);

        ModelAndView mv = new ModelAndView("read_episode2"); // ("redirect:/read_episode");

        Episode epi = new Episode();
        List<EpisodeImage> imageList = new ArrayList<>();

        //Initalize
        int epiIndex = -1;
        int seriesID = -1;

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeID() == episodeID) {
                epiIndex = episode.getIndices();
                seriesID = episode.getSeriesID();
                mv.addObject("episode", episode);
                for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
                    if (episodeImage.getEpisodeID() == episodeID) {
                        episodeImage.setImageString(new String(episodeImage.getImageData()));
                        imageList.add(episodeImage.getIndices(), episodeImage);
                    }
                }

                mv.addObject("imageList", imageList);
                // ra.addFlashAttribute("imageList",imageList);

                for (Series series : seriesRepository.findAll()) {
                    if (series.getSeriesID() == episode.getSeriesID()) {
                        series.setImageData(new String(series.getThumbnail()));
                        mv.addObject("series", series); // single serie
                        break;
                    }
                }

            }
        }


        boolean l = false;
        boolean dl = false;

        for (Likes like : likesRepository.findAll()){
            if (like.getEpisodeID() == episodeID){
                if (like.getUsername().equalsIgnoreCase(username)) {
                    l = true;
                    break;
                }
            }
        }

        for (Dislike dislike : dislikeRepository.findAll()){
            if (dislike.getEpisodeID() == episodeID){
                if (dislike.getUsername().equalsIgnoreCase(username)) {
                    dl = true;
                    break;
                }
            }
        }

        mv.addObject("like", l);
        mv.addObject("dislike", dl);

        List<Comments> commentsList = new ArrayList<>();

        for (Comments c : commentsRepository.findAll()){
            if (c.getEpisodeID() == episodeID){
                commentsList.add(c);
            }
        }

        mv.addObject("comments", commentsList);


        List<DerivedEpi> dEpiList = new ArrayList<>();
        List<DerivedEpi> temp_dEpiList = new ArrayList<>();

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getEpisodeID() == episodeID) {
                mv.addObject("episode", episode);
                for (DerivedEpi dEpi : derivedEpiRepository.findAll()) {
                    if (dEpi.getOriginalID() == episodeID) {
                        dEpi.setImageData(new String(dEpi.getEndingScene()));
                        dEpiList.add(dEpi);
                    }
                }
                // System.out.println("ImageList: "+ imageList);

                dEpiList = sortDerivedEpiByNumLikes(dEpiList);

                mv.addObject("dEpiList", dEpiList);
                // ra.addFlashAttribute("imageList",imageList);

            }
        }

        int max = -1;

        for (Episode episode : EpiRepository.findAll()) {
            if (episode.getSeriesID() == seriesID && episode.getIndices() > max) {
                max = episode.getIndices();
            }
        }

        boolean first = false;
        boolean last = false;

        if(epiIndex == 0){
          first = true;
        }

        if(epiIndex >= max){
          last = true;
        }

        mv.addObject("firstEpi", first);
        mv.addObject("lastEpi", last);


        return mv;
    }


    /**
     *  Sort the list of derived episodes.
     * @param dEpiList
     * @return dEpiList
     */
    public List<DerivedEpi> sortDerivedEpiByNumLikes(List<DerivedEpi> dEpiList){
        Collections.sort(dEpiList, new SortByNumLikes());
        return dEpiList;
    }

    /**
     *  Comparator class to sort by numLikes
     */
    class SortByNumLikes implements Comparator<DerivedEpi>{
        public int compare(DerivedEpi a, DerivedEpi b){
            return b.getNumLikes() - a.getNumLikes();
        }
    }

    public void addImage(int episodeID, String imageData) {
        //System.out.println("imageData passed: " + imageData);
        //System.out.println("episodeID passed: " + episodeID);
        int max = -1;
        for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
            if (episodeImage.getEpisodeID() == episodeID && episodeImage.getIndices() > max) {
                max = episodeImage.getIndices();
            }
        }
        byte[] imageDataBytes = imageData.getBytes();
        // System.out.println("imageDataBytes: " + imageDataBytes);
        EpisodeImage newEpisodeImage = new EpisodeImage();
        newEpisodeImage.setEpisodeID(episodeID);
        newEpisodeImage.setIndices(max + 1);
        newEpisodeImage.setImageData(imageDataBytes);

        this.episodeImageRepository.save(newEpisodeImage);
    }

    @RequestMapping(value = "/deleteEpisode") // Map ONLY GET Requests
    public ModelAndView deleteEpisode(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
            @RequestParam(value = "seriesID") int seriesID) {

        System.out.println("deleting EPISODE of series ID = " + seriesID);

        for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
            if (episodeImage.getEpisodeID() == episodeID) {
                episodeImageRepository.delete(episodeImage);
            }
        }
        for (Episode epi : EpiRepository.findAll()) {
            if (epi.getEpisodeID() == episodeID) {
                EpiRepository.delete(epi);
            }
        }
        for (Likes l : likesRepository.findAll()){
            if (l.getEpisodeID() == episodeID) {
                likesRepository.delete(l);
            }
        }
        for (Dislike dl : dislikeRepository.findAll()){
            if (dl.getEpisodeID() == episodeID) {
                dislikeRepository.delete(dl);
            }
        }

        return allEpisodes(req, seriesID);

    }
}

/*
    @RequestMapping(value = "/addDislike") // Map ONLY GET Requests
    public ModelAndView addDislikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {

        for (Likes like : likesRepository.findAll()){
            if (like.getEpisodeID() == episodeID){
                if (like.getUsername().equalsIgnoreCase(username)) {
                    likesRepository.delete(like);
                    break;
                }
            }
        }

        Dislike dl = new Dislike();
        dl.setEpisodeID(episodeID);
        dl.setUsername(username);

        dislikeRepository.save(dl);

        return readEpisode(req, episodeID, username);
    }
*/

/*
    @RequestMapping(value = "/removeDislike") // Map ONLY GET Requests
    public ModelAndView removeDislikes(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                    @RequestParam(value = "username") String username) {

        for (Dislike dislike : dislikeRepository.findAll()){
            if (dislike.getEpisodeID() == episodeID){
                if (dislike.getUsername().equalsIgnoreCase(username)) {
                    dislikeRepository.delete(dislike);
                    break;
                }
            }
        }

        return readEpisode(req, episodeID, username);
    }
*/


/*

@RequestMapping(value = "/readEpisode2") // Map ONLY GET Requests
public ModelAndView readEpisode2(HttpServletRequest req, @RequestParam(value = "episodeID") int episodeID,
                                @RequestParam(value = "username") String username) {


    System.out.println("received episode ID: readepi2 = " + episodeID);

    ModelAndView mv = new ModelAndView("read_episode2"); // ("redirect:/read_episode");

    Episode epi = new Episode();
    List<EpisodeImage> imageList = new ArrayList<>();

    for (Episode episode : EpiRepository.findAll()) {
        if (episode.getEpisodeID() == episodeID) {
            mv.addObject("episode", episode);
            for (EpisodeImage episodeImage : episodeImageRepository.findAll()) {
                if (episodeImage.getEpisodeID() == episodeID) {
                    episodeImage.setImageString(new String(episodeImage.getImageData()));
                  //  System.out.println("epiString length = " + episodeImage.getImageString().length());
                //    System.out.println("epiImageData length = " + episodeImage.getImageString().length());
                    imageList.add(episodeImage.getIndices(), episodeImage);
                }
            }
            // System.out.println("ImageList: "+ imageList);
            mv.addObject("imageList", imageList);
            // ra.addFlashAttribute("imageList",imageList);

            for (Series series : seriesRepository.findAll()) {
                if (series.getSeriesID() == episode.getSeriesID()) {
                    series.setImageData(new String(series.getThumbnail()));
                    mv.addObject("series", series); // single serie
                    break;
                }
            }

        }
    }

    boolean l = false;
    boolean dl = false;

    for (Likes like : likesRepository.findAll()){
        if (like.getEpisodeID() == episodeID){
            if (like.getUsername().equalsIgnoreCase(username)) {
                l = true;
                break;
            }
        }
    }

    for (Dislike dislike : dislikeRepository.findAll()){
        if (dislike.getEpisodeID() == episodeID){
            if (dislike.getUsername().equalsIgnoreCase(username)) {
                dl = true;
                break;
            }
        }
    }

    mv.addObject("like", l);
    mv.addObject("dislike", dl);

    List<Comments> commentsList = new ArrayList<>();

    for (Comments c : commentsRepository.findAll()){
        if (c.getEpisodeID() == episodeID){
            commentsList.add(c);
        }
    }

    mv.addObject("comments", commentsList);







    // Same as readEpisode1


    List<DerivedEpi> dEpiList = new ArrayList<>();


    for (Episode episode : EpiRepository.findAll()) {
        if (episode.getEpisodeID() == episodeID) {
            mv.addObject("episode", episode);
            for (DerivedEpi dEpi : derivedEpiRepository.findAll()) {
                if (dEpi.getOriginalID() == episodeID) {
                    dEpi.setImageData(new String(dEpi.getEndingScene()));
                    dEpiList.add(dEpi);
                }
            }
            // System.out.println("ImageList: "+ imageList);
            dEpiList = sortDerivedEpiByNumLikes(dEpiList);
            mv.addObject("dEpiList", dEpiList);
            // ra.addFlashAttribute("imageList",imageList);

        }
    }


  return mv;

}


*/
