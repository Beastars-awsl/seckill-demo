package com.dxt.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 * 
 * </p>
 *
 * @author dxt
 * @since 2022-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private LocalDateTime registerDate;

    private LocalDateTime lastLoginDate;

    private Integer loginCount;

}
