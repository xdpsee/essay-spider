package com.zhenhui.essay.spider;

import com.zhenhui.essay.spider.dao.ArticleService;
import com.zhenhui.essay.spider.model.Article;
import com.zhenhui.essay.spider.model.Category;
import lombok.Setter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class XingfuzhanPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1500);

    @Setter
    private ArticleService articleService;

    private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-dd-MM HH:mm");

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {


    }

    @Override
    public void process(Page page) {
        System.out.println("-----> " + page.getRequest().getUrl());
        final List<Selectable> nodes = page.getHtml().xpath("/html/body/div[@class='center']/div[@class='linkbox']").nodes();
        for (Selectable node : nodes) {
            String url = node.xpath("h3/a/@href").toString();
            String title = node.xpath("h3/a/text()").toString();
            if (null != url && null != title) {
                final Category category = articleService.ensureCategory(title);
                final List<Selectable> sections = node.xpath("ul/li").nodes();
                for (Selectable sec : sections) {
                    url = sec.xpath("a/@href").toString();
                    title = sec.xpath("a/text()").toString();
                    if (url != null && title != null) {
                        articleService.ensureSection(category, title);
                        page.addTargetRequest(url);
                        break;

                    }
                }
            }
        }

        // 文章列表
        List<String> paths = page.getHtml().css("ul.pagelist").links().regex("list_\\d+_\\d+\\.html").all();
        //System.out.println(paths);
        String currUrl = page.getRequest().getUrl();
        int index = currUrl.lastIndexOf("/");
        if (index < (currUrl.length() - 1)) {
            currUrl = currUrl.substring(0, index + 1);
        }
        for (String path : paths) {
            System.err.println("article list url , " + currUrl + path);
            page.addTargetRequest(currUrl + path);
        }

        // 文章列表
        final List<Selectable> list = page.getHtml().xpath(
                "/html/body/div[@class='lm_index']/div[@class='lm_left']/div[@class='list_box']/ul/li").nodes();
        for (Selectable node : list) {
            String articleUrl = node.xpath("h3[@class='title']/a[@class='title']/@href").toString();
            page.addTargetRequest(articleUrl);
        }

        // 文章
        Selectable box = page.getHtml().xpath(
                "/html/body/div[@class='lm_index']/div[@class='lm_left']/div[@class='art_viewbox']");
        if (!box.nodes().isEmpty()) {
            page.putField("category",
                    page.getHtml().xpath("/html/body/div[@class='lm_index']/div[@class='lm_left']/div[@class='place']/a[2]/text()").toString());
            page.putField("section",
                    page.getHtml().xpath("/html/body/div[@class='lm_index']/div[@class='lm_left']/div[@class='place']/a[3]/text()").toString());

            final String category = page.getResultItems().get("category");
            final String section = page.getResultItems().get("section");
            final String title = box.xpath("div[@class='title']/h1/text()").toString();
            System.out.println(category+"/" + section + "/" + title);
            if (category != null && section != null) {
                List<Selectable> ps = box.xpath("div[@class='content']/p").nodes();
                StringBuilder builder = new StringBuilder();
                for(Selectable p : ps) {
                    builder.append(p.toString());
                }

                final String infos = box.xpath("div[@class='info']/text()").toString();
                System.out.println(infos);
                StringTokenizer tokenizer = new StringTokenizer(infos, " ");

                String date = "";
                String author = "佚名";
                int count = tokenizer.countTokens();
                for (int i = 0; i < count; ++i) {
                    if (0 == i) {
                        date = tokenizer.nextToken();
                    } else if (1 == i) {
                        date += (" " + tokenizer.nextToken());
                    } else if (2 == i) {
                        author = tokenizer.nextToken();
                    }
                }

                try {
                    Date time = dateFormatter.parse(date);

                    Article article = articleService.getArticle(category, section, title);
                    if (null == article) {
                        article = articleService.createArticle(category, section, title, author, time, builder.toString());
                        articleService.saveArticle(article);
                    } else {
                        System.out.println("已存在: " + category + "/" + section + "/" + title);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
