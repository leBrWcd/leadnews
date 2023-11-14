package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.dtos.WmNewsUpDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description TODO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/10
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.findAll(dto);
    }

    @PostMapping("down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsUpDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/del_news/{id}")
    public ResponseResult deleteNews(@PathVariable("id") Integer id) {
        return wmNewsService.deleteNews(id);
    }

    @PostMapping("submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {
        return wmNewsService.submitNews(dto);
    }

    @GetMapping("one/{id}")
    public ResponseResult fineOne(@PathVariable("id") Integer id) {
        return ResponseResult.okResult(wmNewsService.getById(id));
    }

}
