package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description TODO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/10
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/channel/")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("channels")
    public ResponseResult findAll() {
        return wmChannelService.findAll();
    }

}
