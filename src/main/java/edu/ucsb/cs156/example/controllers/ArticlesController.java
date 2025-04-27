package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticleRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
 * This is a REST controller for Articles
 */

@Tag(name = "Articles")
@RequestMapping("/api/articles")
@RestController
@Slf4j
public class ArticlesController extends ApiController {

    @Autowired
    ArticleRepository articleRepository;

    /**
     * List all Articles
     * 
     * @return an iterable of Articles
     */
    @Operation(summary = "List all articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Article> allArticles() {
        Iterable<Article> articles = articleRepository.findAll();
        return articles;
    }

    /**
     * Get a single article by id
     * 
     * @param id the id of the date
     * @return a Article
     */
    @Operation(summary = "Get a single article")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public Article getById(
            @Parameter(name = "id") @RequestParam Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Article.class, id));

        return article;
    }

    /**
     * Create a new article
     * 
     * @param title       the article title
     * @param url         the article url
     * @param explanation the explanation
     * @param email       the associated email
     * @param dateAdded   the date added
     * @return the saved article
     */
    @Operation(summary = "Create a new article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Article postArticle(
            @Parameter(name = "title") @RequestParam String title,
            @Parameter(name = "url") @RequestParam String url,
            @Parameter(name = "explanation") @RequestParam String explanation,
            @Parameter(name = "email") @RequestParam String email,
            @Parameter(name = "dateAdded", description = "date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateAdded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAdded)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", dateAdded);

        Article article = new Article();
        article.setTitle(title);
        article.setUrl(url);
        article.setExplanation(explanation);
        article.setEmail(email);
        article.setDateAdded(dateAdded);

        Article savedArticle = articleRepository.save(article);

        return savedArticle;
    }

    // /**
    // * Delete a UCSBDate
    // *
    // * @param id the id of the date to delete
    // * @return a message indicating the date was deleted
    // */
    // @Operation(summary= "Delete a UCSBDate")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("")
    // public Object deleteUCSBDate(
    // @Parameter(name="id") @RequestParam Long id) {
    // UCSBDate ucsbDate = ucsbDateRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

    // ucsbDateRepository.delete(ucsbDate);
    // return genericMessage("UCSBDate with id %s deleted".formatted(id));
    // }

    // /**
    // * Update a single date
    // *
    // * @param id id of the date to update
    // * @param incoming the new date
    // * @return the updated date object
    // */
    // @Operation(summary= "Update a single date")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("")
    // public UCSBDate updateUCSBDate(
    // @Parameter(name="id") @RequestParam Long id,
    // @RequestBody @Valid UCSBDate incoming) {

    // UCSBDate ucsbDate = ucsbDateRepository.findById(id)
    // .orElseThrow(() -> new EntityNotFoundException(UCSBDate.class, id));

    // ucsbDate.setQuarterYYYYQ(incoming.getQuarterYYYYQ());
    // ucsbDate.setName(incoming.getName());
    // ucsbDate.setLocalDateTime(incoming.getLocalDateTime());

    // ucsbDateRepository.save(ucsbDate);

    // return ucsbDate;
    // }
}
