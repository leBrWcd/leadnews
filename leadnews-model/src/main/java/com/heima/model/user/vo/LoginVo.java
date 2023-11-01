package com.heima.model.user.vo;

import lombok.Data;

/**
 * Description 用户登录返回
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@Data
public class LoginVo {

    private Integer id;

    private String phone;

    private String password;
}
