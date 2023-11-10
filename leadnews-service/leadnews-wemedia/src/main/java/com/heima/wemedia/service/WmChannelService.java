package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * Description 频道接口
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/10
 */
public interface WmChannelService extends IService<WmChannel> {
    ResponseResult findAll();
}
