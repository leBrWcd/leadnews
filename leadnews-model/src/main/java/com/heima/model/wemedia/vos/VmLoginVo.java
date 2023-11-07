package com.heima.model.wemedia.vos;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description 自媒体端登录返回数据
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/7
 */
@Data
@NoArgsConstructor
public class VmLoginVo {

    /**
     * 姓名
     */
    private String name;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField("image")
    private String image;

    /**
     * 归属地
     */
    @TableField("location")
    private String location;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 状态
     0 暂时不可用
     1 永久不可用
     9 正常可用
     */
    @TableField("status")
    private Integer status;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 账号类型
     0 个人
     1 企业
     2 子账号
     */
    @TableField("type")
    private Integer type;

    /**
     * 运营评分
     */
    @TableField("score")
    private Integer score;

}
