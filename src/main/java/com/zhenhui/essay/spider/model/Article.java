package com.zhenhui.essay.spider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="articles")
@CompoundIndexes(
        @CompoundIndex(name="c_s_t_index", def = "{category:1, section:1, title:1}")
)
public class Article {
    @Id
    private String _id;
    private String title;
    private String author;
    @Indexed private long time;
    private String source;
    private String preview;

    private String category;
    private String section;
    @Indexed private String content;
}