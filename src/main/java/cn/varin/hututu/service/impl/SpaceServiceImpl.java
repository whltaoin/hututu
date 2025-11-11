package cn.varin.hututu.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.space.SpaceAddRequest;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.SpaceLevelEnum;
import cn.varin.hututu.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.hututu.model.entity.Space;
import cn.varin.hututu.service.SpaceService;
import cn.varin.hututu.mapper.SpaceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/**
* @author varya
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-11-10 13:59:04
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{
    @Resource
    private UserService userService;

    @Override
    public void validSpace(Space space, Boolean add) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(space), ResponseCode.NOT_FOUND_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if (add) {
            //1. 如果是add，需要判断名称和级别不为空
            ThrowUtil.throwIf(StringUtils.isBlank(spaceName),
                    new CustomizeException(ResponseCode.OPERATION_ERROR,"空间名称不能为空"));
            ThrowUtil.throwIf(ObjectUtil.isEmpty(enumByValue),
                    new CustomizeException(ResponseCode.OPERATION_ERROR,"空间级别不能为空"));


        }
        // 修改
       ThrowUtil.throwIf(
               ObjectUtil.isNotNull(spaceLevel) &&
                       ObjectUtil.isNull(enumByValue),
               ResponseCode.OPERATION_ERROR.getCode(),"空间级别不存在"
       );
        ThrowUtil.throwIf(
                StringUtils.isNotBlank(spaceName) &&
                        spaceName.length()>30,
                ResponseCode.OPERATION_ERROR.getCode(),"空间名称为长"
        );




    }

    /**
     * 根据级别填充容量
     * @param space
     * @return
     */
    @Override
    public Space fillSpaceBySpaceLevel(Space space) {
        SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (enumByValue!=null) {
            long maxSize = enumByValue.getMaxSize();
            if(space.getMaxSize()==null){
                space.setMaxSize(maxSize);

            }
            long maxCount = enumByValue.getMaxCount();
            if(space.getMaxCount()==null){
                space.setMaxCount(maxCount);
            }
        }

        return space;
    }
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
       // 1 填充参数默认值
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        if(StringUtils.isEmpty(space.getSpaceName())) {
            space.setSpaceName(loginUser.getUserName()+"的个人空间");

        }
        if(space.getSpaceLevel()==null) {
            // 普通空间
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());

        }
        // 2 填充参数，校验参数
        this.fillSpaceBySpaceLevel(space);
        space.setId(loginUser.getId());
        this.validSpace(space,true);
          // 3  校验权限，非管理员只能创建普通级别的空间
        if( SpaceLevelEnum.COMMON.getValue() !=space.getSpaceLevel()
        && !userService.isAdmin(loginUser)
        ){
            throw new CustomizeException(ResponseCode.OPERATION_ERROR,"无权限创建指定级别的空间");

        }
        //4    控制同一用户只能创建一个私有空间
        // 将用户id存储到常量池中
        Long userId = loginUser.getId();
        String userIdConstant = String.valueOf(userId).intern();
        // 加锁，

        synchronized (userIdConstant){
            //进行事务管理
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();

                ThrowUtil.throwIf(!exists, ResponseCode.OPERATION_ERROR.getCode(), "每个用户仅能有一个私有空间");

                boolean save = this.save(space);
                ThrowUtil.throwIf(!save, ResponseCode.OPERATION_ERROR);

                return space.getId();
            });
            return Optional.ofNullable( newSpaceId ).orElse(0L);



        }


    }
}




