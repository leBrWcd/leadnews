package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.mapper.ArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Description TODO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/1
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class ArticleContentTest {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private Configuration configuration;

    @Test
    public void createStaticUrlTest() throws IOException, TemplateException {

        // 1.查询文章内容
        LambdaQueryWrapper<ApArticleContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApArticleContent::getArticleId,"1302862387124125698");
        ApArticleContent apArticleContent = articleContentMapper.selectOne(wrapper);
        if (apArticleContent != null && !StringUtils.isEmpty(apArticleContent.getContent())) {
            // 2.上传到minio
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");

            Map<String,Object> params = new HashMap<>();
            params.put("content", JSONArray.parseArray(apArticleContent.getContent()));
            // 通过模板技术生成静态文件 --> out
            template.process(params,out);
            // out --> is
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());
            // 将生成的静态文件存入MinIO
            String path = fileStorageService.uploadHtmlFile("",apArticleContent.getArticleId() + ".html",is);
            log.info("======生成静态url成功======，地址: {}" ,path);
            // 3.静态url存入文章
            ApArticle apArticle = new ApArticle();
            apArticle.setId(apArticleContent.getArticleId());
            apArticle.setStaticUrl(path);
            articleMapper.updateById(apArticle);
        }
    }



}
