package cn.varin.hututu.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.ResponseUtil;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.user.UserLoginRequest;
import cn.varin.hututu.model.dto.user.UserRegisterRequest;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.vo.user.LoginUserVo;
import cn.varin.hututu.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {

        Long userId = userService.userRegister(registerRequest);
        return ResponseUtil.success(userId);
    }
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest uerLoginRequest, HttpServletRequest request) {

        LoginUserVo loginUserVo = userService.userLogin(uerLoginRequest, request);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUserVo),new CustomizeException(ResponseCode.OPERATION_ERROR));
        return ResponseUtil.success(loginUserVo);
    }

    @PostMapping("/get/loginUser")
    public BaseResponse<LoginUserVo> getLoginUser( HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);
        LoginUserVo loginUserVO = this.userService.getLoginUserVO(user);
        return ResponseUtil.success(loginUserVO);
    }

}
