package cn.varin.hututu.model.vo.picture;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.model.vo.user.UserVo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 返回给前端视图
 *
 */
@Data
public class PictureVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private List<String> tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关联创建图片用户信息
     */
    private UserVo user;

    // VO类转实体类静态方法
    public static  Picture voToEntity(PictureVo pictureVo) {
        if (ObjectUtil.isEmpty(pictureVo)) {

            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVo, picture);
        //  标签需要单独处理，
        String tagStr = JSONUtil.toJsonStr(pictureVo.getTags());
        picture.setTags(tagStr);
        return picture;

    }


    /**
     *  实体转vo
     * @param picture 实体
     * @return  pictureVo
     */
    public static  PictureVo entityToVo(Picture picture) {
        if (ObjectUtil.isEmpty(picture)) {

            return null;
        }
        PictureVo pictureVo = new PictureVo();
        BeanUtils.copyProperties(picture, pictureVo);
        //  标签需要单独处理，
        List<String> tatList = JSONUtil.toList(picture.getTags(), String.class);
        pictureVo.setTags(tatList);
        return pictureVo;

    }



}