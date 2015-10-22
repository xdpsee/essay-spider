package com.zhenhui.essay.spider.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection="contents")
public class Content {

    @Id
    private String _id;

    private String text;

}
