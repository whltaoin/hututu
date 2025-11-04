package cn.varin.hututu.service;

import cn.varin.hututu.model.dto.user.UserLoginRequest;
import cn.varin.hututu.model.dto.user.UserQueryRequest;
import cn.varin.hututu.model.dto.user.UserRegisterRequest;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.vo.user.LoginUserVo;
import cn.varin.hututu.model.vo.user.UserVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author varya
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-10-31 14:41:38
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userRegisterRequest 用户注册信息，包含：用户账号、用户密码、用户确认密码
     * @return 注册成功的用户ID
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     * @param userLoginRequest 用户登录请求类
     * @param request
     * @return
     */
    LoginUserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取脱敏后的用户实体
     * @param user 用户实体
     * @return 脱敏后的用户实体
     */
    LoginUserVo getLoginUserVO(User user);

    /**
     * 获取脱敏后的用户实体
     * @param user 用户实体
     * @return 脱敏后的用户实体
     */
    UserVo getUserVo(User user);

    /**
     * 获取脱敏后的用户实体
     * @param userList 用户实体列表
     * @return 脱敏后的用户实体列表
     */
    List<UserVo> getListUserVO(List<User> userList);
    /**
     * 从cookit中获取登录用户信息
     * @param httpServletRequest
     * @return
     */

    User getLoginUser(HttpServletRequest httpServletRequest);

    /**
     * 用户注销
     * @param httpServletRequest
     * @return
     */
    Boolean userLogout(HttpServletRequest httpServletRequest);

    /**
     * 构造用户查询条件
     * @param userQueryRequest
     * @return
     */

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    /**
     * 判断是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);
}
