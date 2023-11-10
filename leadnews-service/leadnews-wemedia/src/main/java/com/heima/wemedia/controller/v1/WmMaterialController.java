package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Description 自媒体素材控制器
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/9
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    @PostMapping("upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        log.info("请求上传图片中... {}",multipartFile.getName());
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult listMaterial(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.listMaterial(dto);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable("id") String id) {
        return wmMaterialService.collect(id);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollect(@PathVariable("id") String id) {
        return wmMaterialService.cancelCollect(id);
    }

}
