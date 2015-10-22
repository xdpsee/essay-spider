package com.zhenhui.essay.spider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "sections")
public class Section {
    @Id
    private String _id;
    @Indexed
    private String title;
    @Indexed
    private String category;

    private long createDate;
    private long updateDate;
}
