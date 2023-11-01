package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.dto.LoginDto;
import com.heima.model.user.vo.LoginVo;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Description App用户业务实现类
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@Slf4j
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    /**
     *
     * @param dto
     * @return ResponseResult
     * @description:
     * 1，用户输入了用户名和密码进行登录，校验成功后返回jwt(基于当前用户的id生成)
     *
     * 2，用户游客登录，生成jwt返回(基于默认值0生成)
     */
    @Override
    public ResponseResult login(LoginDto dto) {
        // 1、判断手机号密码是否为空
        String phone = dto.getPhone();
        String password = dto.getPassword();

        if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(password)) {
            // 2、为空返回0生成jwt返回
            String token = AppJwtUtil.getToken(0L);
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            return ResponseResult.okResult(map);
        } else {
            // 3、不为空处理逻辑
            // 4、查询数据库中该手机号是否存在，不存在返回 用户不存在
            LambdaQueryWrapper<ApUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApUser::getPhone,phone);
            ApUser apUser = baseMapper.selectOne(wrapper);
            if (apUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.NOT_USER);
            }
            // 4.1 存在，校验密码，密码不正常，返回 密码 错误
            String salt = apUser.getSalt();
            password = DigestUtils.md5DigestAsHex((password+salt).getBytes());  //获得用户的盐
            if (!password.equals(apUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            // 4.2 密码正确，生成jwt返回
            String token = AppJwtUtil.getToken(apUser.getId().longValue());
            Map<String,Object> map = new HashMap<>();
            LoginVo loginVo = new LoginVo();
            BeanUtils.copyProperties(apUser,loginVo);
            map.put("token",token);
            // 加密返回
            loginVo.setPassword("******");
            map.put("user",loginVo);
            return ResponseResult.okResult(map);
        }
    }
}
