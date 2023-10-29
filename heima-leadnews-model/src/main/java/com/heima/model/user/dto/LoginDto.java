package com.heima.model.user.dto;

import lombok.Data;

/**
 * Description App用户登录请求传输参数
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@Data
public class LoginDto {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;
}
