package com.dxt.seckill.vo;

import com.dxt.seckill.utils.ValidatorUtil;
import com.dxt.seckill.validator.IsMobile;
import org.thymeleaf.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * @Author : dxt
 * @Date 2022/3/11 15:55
 */

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
       if(required){
           return ValidatorUtil.isMobile(value);
       }else {
           if(StringUtils.isEmpty(value)) {
               return true;
           }else {
               return ValidatorUtil.isMobile(value);
           }
       }
    }
}
