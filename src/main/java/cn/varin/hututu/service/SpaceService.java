package cn.varin.hututu.service;

import cn.varin.hututu.model.dto.space.SpaceAddRequest;
import cn.varin.hututu.model.entity.Space;
import cn.varin.hututu.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author varya
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-11-10 13:59:04
*/
public interface SpaceService extends IService<Space> {
    /**
     * 判断是add还是edit，分别进行对象校验
      * @param space
     * @param add
     */
    void validSpace(Space space,Boolean add);

    /**
     * 如果空间中的容量和条数为空是，根据级别进行填充
     * @param space
     * @return
     */
    Space fillSpaceBySpaceLevel(Space space);

    /**
     * 添加空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
