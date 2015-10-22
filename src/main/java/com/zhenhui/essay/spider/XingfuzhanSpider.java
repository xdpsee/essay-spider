package com.zhenhui.essay.spider;


import lombok.Setter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.component.BloomFilterDuplicateRemover;

public class XingfuzhanSpider {

    private Spider spider;
    @Setter
    private PageProcessor pageProcessor;

    public void run() {
        spider = Spider.create(pageProcessor);
        spider.setScheduler(new QueueScheduler()
                .setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));

        spider.addUrl("http://www.xingfuzhan.cn/sitemap.html").run();

    }


    public void shutdown() {
        spider.stop();
    }

    public static void main(String[] args) {
        final ClassPathXmlApplicationContext applicationContext
                = new ClassPathXmlApplicationContext("classpath:application-context.xml");

        //System.out.println(applicationContext);

        XingfuzhanSpider spider = (XingfuzhanSpider)applicationContext.getBean("xingfuzhanSpider");
        spider.run();
    }
}
