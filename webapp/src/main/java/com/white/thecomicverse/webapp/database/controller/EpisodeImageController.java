package com.white.thecomicverse.webapp.database.controller;

import com.white.thecomicverse.webapp.database.model.Episode;
import com.white.thecomicverse.webapp.database.model.EpisodeImage;
import com.white.thecomicverse.webapp.database.repositories.EpisodeImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path="/Image")
public class EpisodeImageController {

    @Autowired
    EpisodeImageRepository episodeImageRepository;



}
