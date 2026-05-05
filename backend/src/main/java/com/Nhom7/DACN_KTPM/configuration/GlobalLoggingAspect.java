package com.Nhom7.DACN_KTPM.configuration;

import com.Nhom7.DACN_KTPM.entity.SystemLog;
import com.Nhom7.DACN_KTPM.repository.SystemLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class GlobalLoggingAspect {

    private final SystemLogRepository logRepository;

    @Pointcut("execution(* com.Nhom7.DACN_KTPM.service.*.create*(..)) || " +
            "execution(* com.Nhom7.DACN_KTPM.service.*.update*(..)) || " +
            "execution(* com.Nhom7.DACN_KTPM.service.*.delete*(..))")
    public void serviceMethods() {}

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        saveSystemLog(joinPoint, "SUCCESS");
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logError(JoinPoint joinPoint, Exception ex) {
        saveSystemLog(joinPoint, "ERROR: " + ex.getMessage());
    }

    private void saveSystemLog(JoinPoint joinPoint, String status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String methodName = joinPoint.getSignature().getName();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();

        SystemLog log = SystemLog.builder()
                .username(username)
                .action(methodName)
                .status(status)
                .ipAddress(ipAddress)
                .detail("Hành động thực hiện tại lớp: " + joinPoint.getTarget().getClass().getSimpleName())
                .build();
        logRepository.save(log);
    }
}
