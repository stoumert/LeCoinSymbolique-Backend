package com.example.demo.configuration;

import com.sipios.springsearch.configuration.SearchSpecificationResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class SpringSearchResolverConf implements WebMvcConfigurer {
    /**
     * Configure les résolveurs d'arguments personnalisés pour le traitement des requêtes.
     *
     * @param argumentResolvers la liste des résolveurs d'arguments à laquelle ajouter les résolveurs personnalisés,
     *                          tels que ceux utilisés pour intercepter et traiter les spécifications de recherche.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SearchSpecificationResolver());
    }

}
