package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile 文件传输对象
     * @return ResponseResult
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);


    /**
     * 列表查询
     * @param dto dto
     * @return ResponseResult
     */
    ResponseResult listMaterial(WmMaterialDto dto);

    /**
     * 收藏素材
     * @param id
     * @return
     */
    ResponseResult collect(String id);

    /**
     * 取消收藏
     * @param id
     * @return
     */
    ResponseResult cancelCollect(String id);
}