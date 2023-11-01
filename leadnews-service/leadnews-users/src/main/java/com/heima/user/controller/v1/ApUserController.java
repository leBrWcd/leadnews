package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dto.LoginDto;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description App用户控制器
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@Slf4j
@RestController
@RequestMapping("api/v1/login")
@Api(value = "App用户端登录",tags = "App用户端登录")
public class ApUserController {

    @Autowired
    private ApUserService userService;

    @ApiOperation("App用户登录")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto dto) {
        return  userService.login(dto);
    }

}
