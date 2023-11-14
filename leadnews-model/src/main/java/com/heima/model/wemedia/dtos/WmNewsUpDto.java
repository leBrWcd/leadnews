package com.heima.model.wemedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description 文章上下架DTO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/14
 */
@Data
@NoArgsConstructor
public class WmNewsUpDto {

    private Integer id;

    private Short enable;

}
