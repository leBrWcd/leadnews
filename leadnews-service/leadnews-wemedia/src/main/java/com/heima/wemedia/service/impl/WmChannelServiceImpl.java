package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description 频道业务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/10
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    @Override
    public ResponseResult findAll() {
        // 查询所有非禁用的频道
        List<WmChannel> list = lambdaQuery().eq(WmChannel::getStatus, 1).list();
        return ResponseResult.okResult(list);
    }
}
