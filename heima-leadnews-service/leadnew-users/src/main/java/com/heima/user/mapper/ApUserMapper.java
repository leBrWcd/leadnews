package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.user.pojos.ApUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description App用户数据配置
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/8/13
 */
@Mapper
public interface ApUserMapper extends BaseMapper<ApUser> {
}
