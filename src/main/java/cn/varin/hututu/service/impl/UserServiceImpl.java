package cn.varin.hututu.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.user.UserLoginRequest;
import cn.varin.hututu.model.dto.user.UserRegisterRequest;
import cn.varin.hututu.model.enums.UserRoleEnum;
import cn.varin.hututu.model.vo.user.LoginUserVo;
import cn.varin.hututu.util.EncryptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.mapper.UserMapper;
import cn.varin.hututu.service.UserService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author varya
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-10-31 14:41:38
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 1 校验参数
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userRegisterRequest), ResponseCode.OPERATION_ERROR);
        ThrowUtil.throwIf(
                StrUtil.hasBlank(userRegisterRequest.getUserAccount(),userRegisterRequest.getUserPassword(),userRegisterRequest.getCheckPassword()),
                new CustomizeException(ResponseCode.PARAMS_ERROR,"参数为空"));

        ThrowUtil.throwIf(userRegisterRequest.getUserAccount().length()<4,new CustomizeException(ResponseCode.PARAMS_ERROR,"用户账号过短"));
        ThrowUtil.throwIf(userRegisterRequest.getUserAccount().length()<4 || userRegisterRequest.getCheckPassword().length()<4,new CustomizeException(ResponseCode.PARAMS_ERROR,"用户密码过短"));
        ThrowUtil.throwIf(!userRegisterRequest.getUserPassword().equals(userRegisterRequest.getCheckPassword()), new CustomizeException(ResponseCode.PARAMS_ERROR,"两次收入的密码不一致"));
        // 查询用户账号是否存在

        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userRegisterRequest.getUserAccount());
        User user = this.baseMapper.selectOne(userLambdaQueryWrapper);
        if (!ObjectUtil.isEmpty(user)) {
            throw new CustomizeException(ResponseCode.OPERATION_ERROR,"账号重复");
        }
        //注册用户
        String password = EncryptionUtil.getCiphertext(userRegisterRequest.getUserPassword());
         User userInfo = new User();
        userInfo.setUserAccount(userRegisterRequest.getUserAccount());
        userInfo.setUserPassword(password);
        userInfo.setUserName("用户" + RandomUtil.randomNumbers(4));
        userInfo.setUserRole(UserRoleEnum.USER.getValue());
        // 保存用户
        boolean savaUserStatus  = this.save(userInfo);
        ThrowUtil.throwIf(!savaUserStatus, ResponseCode.SYSTEM_ERROR.getCode(),"注册失败，数据库错误");


        return userInfo.getId();
    }

    @Override
    public LoginUserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userLoginRequest), ResponseCode.SYSTEM_ERROR.getCode(),"用户登录对象为空");
        String userPassword = userLoginRequest.getUserPassword();
        String userAccount = userLoginRequest.getUserAccount();
        ThrowUtil.throwIf(StrUtil.hasBlank(userAccount,userPassword),new CustomizeException(ResponseCode.PARAMS_ERROR,"账号或密码为空"));
        ThrowUtil.throwIf(userAccount.length()<4 ,new CustomizeException(ResponseCode.PARAMS_ERROR,"用户名错误"));
        ThrowUtil.throwIf(userPassword.length()<4 ,new CustomizeException(ResponseCode.PARAMS_ERROR,"密码错误"));
        // 构建查询条件
        LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userAccount);
        User user = this.baseMapper.selectOne(eq);
        // 判断为空以及对比密码
        ThrowUtil.throwIf(ObjectUtil.isEmpty(user)|| !EncryptionUtil.checkPlaintext(userPassword, user.getUserPassword()),new CustomizeException(ResponseCode.OPERATION_ERROR,"用户或密码错误"));




        //验证成功
        LoginUserVo loginUserVo = this.getLoginUserVO(user);

        // 将用户存储到cookit中
        request.setAttribute(UserConstant.USER_LOGIN_STATUS,user);


        return  loginUserVo;
    }

    @Override
    public LoginUserVo getLoginUserVO(User user) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(user),ResponseCode.OPERATION_ERROR.getCode(),"用户实体内容为空");
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtils.copyProperties(user,loginUserVo);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUserVo),ResponseCode.OPERATION_ERROR);

        return loginUserVo;
    }

    @Override
    public User getLoginUser(HttpServletRequest httpServletRequest) {
        Object userObject = httpServletRequest.getAttribute(UserConstant.USER_LOGIN_STATUS);
        User user = (User) userObject;;
        ThrowUtil.throwIf(ObjectUtil.isEmpty(userObject)|| ObjectUtil.isEmpty(user.getId()),
                new CustomizeException(ResponseCode.OPERATION_ERROR));

        // 查询一下，确保数据是安全的
        Long id = user.getId();
        User currentUser = this.getById(id);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(currentUser),ResponseCode.OPERATION_ERROR);

        LoginUserVo loginUserVO = this.getLoginUserVO(user);


        return currentUser;
    }

}




