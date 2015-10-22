package com.zhenhui.essay.spider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "categories")
public class Category {

    @Id
    private String _id;
    @Indexed
    private String title;

    private long createDate;
    private long updateDate;

}

