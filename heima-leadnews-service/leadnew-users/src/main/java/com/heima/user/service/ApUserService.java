package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.dto.LoginDto;

/**
 * Description App用户接口
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
public interface ApUserService extends IService<ApUser> {
    ResponseResult login(LoginDto dto);
}
