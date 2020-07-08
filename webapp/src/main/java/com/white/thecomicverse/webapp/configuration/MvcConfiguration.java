package com.white.thecomicverse.webapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer{

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/account_settings").setViewName("account_settings");
        registry.addViewController("/browse").setViewName("browse");
        registry.addViewController("/browse_derivedEpi").setViewName("browse_derivedEpi");
        registry.addViewController("/contest_info").setViewName("contest_info");
        registry.addViewController("/create_comic_series").setViewName("create_comic_series");
        registry.addViewController("/drawing_page").setViewName("drawing_page");
        registry.addViewController("/drawing_page_derivedEpi").setViewName("drawing_page_derivedEpi");
        registry.addViewController("/manage_my_episodes").setViewName("manage_my_episodes");
        registry.addViewController("/manage_my_series").setViewName("manage_my_series");
        registry.addViewController("/read_episode").setViewName("read_episode");
        registry.addViewController("/upload_episode").setViewName("upload_episode");
        registry.addViewController("/view_author_page").setViewName("view_author_page");
        registry.addViewController("/view_comic_series").setViewName("view_comic_series");
        registry.addViewController("/sign_up_form").setViewName("sign_up_form");
        registry.addViewController("/signout").setViewName("signout");
    }
}
