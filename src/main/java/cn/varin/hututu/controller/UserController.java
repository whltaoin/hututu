package cn.varin.hututu.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.varin.hututu.annotation.AutoCheckRole;
import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.DeleteRequest;
import cn.varin.hututu.common.PageRequest;
import cn.varin.hututu.common.ResponseUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.user.*;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.UserRoleEnum;
import cn.varin.hututu.model.vo.user.LoginUserVo;
import cn.varin.hututu.model.vo.user.UserVo;
import cn.varin.hututu.service.UserService;
import cn.varin.hututu.util.EncryptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @ApiOperation(value = "用户注册接口")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {

        Long userId = userService.userRegister(registerRequest);
        return ResponseUtil.success(userId);
    }

    @ApiOperation(value = "用户登录接口")

    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest uerLoginRequest, HttpServletRequest request) {

        LoginUserVo loginUserVo = userService.userLogin(uerLoginRequest, request);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUserVo),new CustomizeException(ResponseCode.OPERATION_ERROR));
        return ResponseUtil.success(loginUserVo);
    }


    @ApiOperation(value = "获取用户接口")
    @PostMapping("/get/loginUser")
    public BaseResponse<LoginUserVo> getLoginUser( HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);
        LoginUserVo loginUserVO = this.userService.getLoginUserVO(user);
        return ResponseUtil.success(loginUserVO);
    }
    @ApiOperation(value = "用户退出登录接口")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest httpServletRequest) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(httpServletRequest),new CustomizeException(ResponseCode.OPERATION_ERROR));
        Boolean result = userService.userLogout(httpServletRequest);
        return ResponseUtil.success(result);
    }


    // 管理员用户接口
    @ApiOperation(value = "添加用户")
    @PostMapping("/add")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> userAdd(@RequestBody UserAddRequest userAddRequest) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userAddRequest),new CustomizeException(ResponseCode.PARAMS_ERROR));
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 秘密加密
        user.setUserPassword(EncryptionUtil.getCiphertext(user.getUserPassword()));
        boolean save = userService.save(user);
        ThrowUtil.throwIf(save,new CustomizeException(ResponseCode.OPERATION_ERROR.getCode(),"添加用户失败"));
        return ResponseUtil.success(user.getId());

    }
    @ApiOperation(value = "根据id获取用户")
    @GetMapping("/get")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById( Long userId) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userId),new CustomizeException(ResponseCode.PARAMS_ERROR));
        User user = userService.getById(userId);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(user),new CustomizeException(ResponseCode.OPERATION_ERROR));
        return ResponseUtil.success(user);

    }

    @ApiOperation(value = "根据id获取用户Vo")
    @GetMapping("/get/vo")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVo> getUserVOById(Long userId) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userId),new CustomizeException(ResponseCode.PARAMS_ERROR));
        User user = userService.getById(userId);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(user),new CustomizeException(ResponseCode.OPERATION_ERROR));
        UserVo userVo = userService.getUserVo(user);
        return ResponseUtil.success(userVo);

    }
    @ApiOperation(value = "根据id删除用户")
    @DeleteMapping("/delete")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(deleteRequest)|| deleteRequest.getId()<=0,new CustomizeException(ResponseCode.PARAMS_ERROR));
        boolean deleteStatus= userService.removeById(deleteRequest.getId());
        ThrowUtil.throwIf(!deleteStatus,new CustomizeException(ResponseCode.OPERATION_ERROR));
        return ResponseUtil.success(deleteStatus);

    }


    // 管理员用户接口
    @ApiOperation(value = "添加用户")
    @PutMapping("/update")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userUpdateRequest),new CustomizeException(ResponseCode.PARAMS_ERROR));
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        // 秘密加密
        user.setUserPassword(EncryptionUtil.getCiphertext(user.getUserPassword()));
        boolean save = userService.updateById(user);
        ThrowUtil.throwIf(save,new CustomizeException(ResponseCode.OPERATION_ERROR.getCode(),"添加用户失败"));
        return ResponseUtil.success(true);

    }

    @ApiOperation(value = "分页查询用户VO列表")
    @PostMapping("/list/page/vo")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVo>> getUserById(@RequestBody UserQueryRequest userQueryRequest ) {
        // 校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userQueryRequest),new CustomizeException(ResponseCode.PARAMS_ERROR));
        // 获取第几页，和每页多少条
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();

        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest)
        );
        //查询到的用户
        Page<UserVo> userVoPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVo> records = userService.getListUserVO(userPage.getRecords());
        userVoPage.setRecords(records);



        ThrowUtil.throwIf(ObjectUtil.isEmpty(userPage),new CustomizeException(ResponseCode.OPERATION_ERROR));
        return ResponseUtil.success(userVoPage);

    }








}
