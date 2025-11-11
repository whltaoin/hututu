package cn.varin.hututu.controller;

import cn.hutool.core.util.ObjectUtil;

import cn.varin.hututu.annotation.AutoCheckRole;
import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.DeleteRequest;
import cn.varin.hututu.common.ResponseUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.picture.SpaceLevel;
import cn.varin.hututu.model.dto.space.*;
import cn.varin.hututu.model.entity.Space;
import cn.varin.hututu.model.entity.User;

import cn.varin.hututu.model.enums.SpaceLevelEnum;
import cn.varin.hututu.model.vo.space.SpaceVo;
import cn.varin.hututu.service.SpaceService;
import cn.varin.hututu.service.UserService;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


// @Api(tags = "空间模块接口")
@RestController
@RequestMapping("/space")
public class SpaceController {
    @Resource
    private SpaceService spaceService;
    @Resource
    private UserService userService;




    @ApiOperation(value ="删除空间")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(deleteRequest)|| deleteRequest.getId()<=0, ResponseCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.NOT_LOGIN_ERROR);

        Space oldSpace = spaceService.getById(deleteRequest.getId());
        ThrowUtil.throwIf(ObjectUtil.isEmpty(oldSpace), ResponseCode.NOT_FOUND_ERROR);

        ThrowUtil.throwIf(!oldSpace.getUserId().equals(loginUser.getId()) || !  userService.isAdmin(loginUser), ResponseCode.NO_AUTH_ERROR);

        boolean deleteStatus = spaceService.removeById(oldSpace.getId());

        ThrowUtil.throwIf(
              !  deleteStatus

                , ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(deleteStatus);


    }

    /**
     * 更新空间只有管理员才可以用
     * @param spaceUpdateRequest
     * @param httpServletRequest
     * @return
     */
    @ApiOperation(value ="更新空间")
    @AutoCheckRole(roleValue =UserConstant.ADMIN_ROLE )
    @PutMapping("/update")
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,HttpServletRequest httpServletRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR);
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);

        // 先填充，后填充
        spaceService.fillSpaceBySpaceLevel(space);
        spaceService.validSpace(space,false);

        // 查询在是否真实存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtil.throwIf(oldSpace == null, ResponseCode.NOT_FOUND_ERROR);
        boolean result = spaceService.updateById(space);
        ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }


    @ApiOperation(value ="根据ID查询空间")
    @GetMapping("/get")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtil.throwIf(id <= 0, ResponseCode.PARAMS_ERROR);

        Space space = spaceService.getById(id);
        ThrowUtil.throwIf(space == null, ResponseCode.NOT_FOUND_ERROR);

        return ResponseUtil.success(space);
    }

    @ApiOperation(value ="根据ID查询空间VO")
    @GetMapping("/get/vo")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<SpaceVo> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtil.throwIf(id <= 0, ResponseCode.PARAMS_ERROR);


        Space space = spaceService.getById(id);
        ThrowUtil.throwIf(space == null, ResponseCode.NOT_FOUND_ERROR);
        SpaceVo spaceVo = SpaceVo.entityToVo(space);

        return ResponseUtil.success(spaceVo);
    }






    /**
     * 编辑空间
     * @param spaceEditRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑空间")

    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR);
        }

        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        // space 中的tags为字符串，

        space.setEditTime(new Date());

        spaceService.validSpace(space,false);
        User loginUser = userService.getLoginUser(request);

        long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtil.throwIf(oldSpace == null, ResponseCode.NOT_FOUND_ERROR);

        ThrowUtil.throwIf(!oldSpace.getUserId().equals(loginUser.getId()) || !  userService.isAdmin(loginUser), ResponseCode.NO_AUTH_ERROR);

        // 管理员自动审核
        spaceService.fillSpaceBySpaceLevel(oldSpace);

        boolean result = spaceService.updateById(space);
        ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }

    /**
     * 获取到级别列表
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResponseUtil.success(spaceLevelList);
    }





}
