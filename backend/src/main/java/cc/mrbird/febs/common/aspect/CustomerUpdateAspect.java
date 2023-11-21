package cc.mrbird.febs.common.aspect;


import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.school.entity.BaseEntity;
import cc.mrbird.febs.system.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class CustomerUpdateAspect {

    @Pointcut("@annotation(cc.mrbird.febs.common.annotation.CustomerUpdate)")
    public void pointcut() {
        // do nothing
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        User user = FebsUtil.getCurrentUser();
        System.out.println("customerInsert面向切面" + FebsUtil.getCurrentUser().getUsername());
        Object obj = point.getArgs()[0];
        if (obj instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) obj;
            baseEntity.setModifyTime(LocalDateTime.now());
            baseEntity.setModifyUserId(user.getUserId());
            baseEntity.setModifyUsername(user.getUsername());
        }
        return point.proceed();
    }
}
