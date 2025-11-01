package cn.varin.hututu.aop;


import cn.hutool.core.util.ObjectUtil;
import cn.varin.hututu.annotation.AutoCheckRole;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.UserRoleEnum;
import cn.varin.hututu.model.vo.user.LoginUserVo;
import cn.varin.hututu.service.UserService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthCheckRoleInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(autoCheckRole)")
    public Object doIntercept(ProceedingJoinPoint joinPoint ,AutoCheckRole  autoCheckRole ) throws Throwable {
        // 获取到方法上的用户权限
        String  roleValue = autoCheckRole.roleValue();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // 获取用户信息
        User loginUser = userService.getLoginUser(request);
        // 方法权限
        UserRoleEnum meonthRoleEnum = UserRoleEnum.getUserRoleEnum(roleValue);
        if (meonthRoleEnum==null) {
            // 放行
            joinPoint.proceed();
        }
        // 获取登录用户权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getUserRoleEnum(loginUser.getUserRole());
        // 需要管理员权限的方法遇到没有管理元权限的用户
        if (UserRoleEnum.ADMIN.equals(meonthRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new CustomizeException(ResponseCode.OPERATION_ERROR,"权限等级不够");

        }
        // 其他统统放行
        return joinPoint.proceed();

    }
}
