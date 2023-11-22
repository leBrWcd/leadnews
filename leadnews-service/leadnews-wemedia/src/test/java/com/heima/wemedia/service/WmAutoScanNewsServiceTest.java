package com.heima.wemedia.service;

import com.heima.wemedia.WemediaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author lebrwcd
 * @date 2023/11/22
 * @note
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class WmAutoScanNewsServiceTest {

    @Autowired
    private WmAutoScanNewsService wmAutoScanNewsService;

    @Test
    public void autoScanNews() {
        wmAutoScanNewsService.autoScanNews(6232);
    }
}