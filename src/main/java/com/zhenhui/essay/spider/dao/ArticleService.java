package com.zhenhui.essay.spider.dao;

import com.zhenhui.essay.spider.model.Article;
import com.zhenhui.essay.spider.model.Category;
import com.zhenhui.essay.spider.model.Content;
import com.zhenhui.essay.spider.model.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ArticleService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Category ensureCategory(String category) {

        Category cate = mongoTemplate.findOne(new Query(Criteria.where("title").is(category)), Category.class);
        if (null == cate) {
            cate = new Category();
            cate.setTitle(category);
            cate.setCreateDate(new Date().getTime());
            cate.setUpdateDate(new Date().getTime());
            mongoTemplate.insert(cate);
        }
        return cate;
    }

    public Section ensureSection(Category category, String section) {

        Section sec = mongoTemplate.findOne(new Query(Criteria.where("category").is(category.get_id())
                .and("title").is(section)), Section.class);
        if (null == sec) {
            sec = new Section();
            sec.setTitle(section);
            sec.setCategory(category.get_id());
            sec.setCreateDate(new Date().getTime());
            sec.setUpdateDate(new Date().getTime());
            mongoTemplate.insert(sec);
        }

        return sec;
    }

    public Article getArticle(String categoryTitle, String sectionTitle, String articleTitle) {

        final Category category = ensureCategory(categoryTitle);
        final Section section = ensureSection(category, sectionTitle);

        return mongoTemplate.findOne(new Query(Criteria.where("category").is(category.get_id())
                .and("section").is(section.get_id())
                .and("title").is(articleTitle))
                , Article.class);
    }

    public void saveArticle(Article article) {
        mongoTemplate.save(article);
    }

    public Article createArticle(String categoryTitle, String sectionTitle, String title, String author, Date date, String content) {
        final Category category = ensureCategory(categoryTitle);
        final Section section = ensureSection(category, sectionTitle);

        Article article = new Article();
        article.setCategory(category.get_id());
        article.setSection(section.get_id());
        article.setTitle(title);
        article.setAuthor(author);
        article.setTime(date.getTime());
        article.setSource("");

        Content c = new Content();
        c.setText(content);
        mongoTemplate.insert(c);

        article.setContent(c.get_id());

        // preview
        final String delimiter = "</p>";
        int index = 0;
        while (content != null && (index = content.indexOf(delimiter, index+delimiter.length())) < 100 && index >= 0)
            ;
        String preview = (content != null && index >= 0)
                ? content.substring(0, index + delimiter.length())
                : content;
        article.setPreview(preview);

        return article;
    }
}
