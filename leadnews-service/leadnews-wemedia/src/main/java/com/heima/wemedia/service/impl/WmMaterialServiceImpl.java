package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Description 自媒体素材业务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/9
 */
@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {

        //1.检查参数
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //aa.jpg
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}", fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl-上传文件失败");
        }

        //3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setType((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

    /**
     * 根据dto进行分页查询，查询对应用户上传的素材列表
     * @param dto dto
     * @return
     */
    @Override
    public ResponseResult listMaterial(WmMaterialDto dto) {
        // 设置分页参数
        dto.checkParam();
        // 分页对象
        Page<WmMaterial> page = new Page<>(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        // 按照用户查询、创建时间倒叙
        wrapper.eq(dto.getIsCollection() != null && dto.getIsCollection() == 1,WmMaterial::getIsCollection,dto.getIsCollection())
                .eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId())
                .orderByDesc(WmMaterial::getCreatedTime);
        page = baseMapper.selectPage(page,wrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        if (page.getRecords() != null) {
            responseResult.setData(page.getRecords());
        }
        return responseResult;
    }

    @Override
    public ResponseResult collect(String id) {
        // 查询对应素材，更新素材
        WmMaterial material = lambdaQuery().eq(id != null, WmMaterial::getId, id).one();
        material.setIsCollection((short)1);
        updateById(material);
        return ResponseResult.okResult(material);
    }

    @Override
    public ResponseResult cancelCollect(String id) {
        // 查询对应素材，更新素材
        WmMaterial material = lambdaQuery().eq(id != null, WmMaterial::getId, id).one();
        material.setIsCollection((short)0);
        updateById(material);
        return ResponseResult.okResult(material);
    }

}
