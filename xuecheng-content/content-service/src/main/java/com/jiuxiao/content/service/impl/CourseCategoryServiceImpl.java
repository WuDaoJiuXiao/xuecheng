package com.jiuxiao.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiuxiao.content.mapper.CourseCategoryMapper;
import com.jiuxiao.content.module.dto.CourseCategoryTreeDto;
import com.jiuxiao.content.module.po.CourseCategory;
import com.jiuxiao.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author 悟道九霄
 * @since 2023-01-18
 */
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryCourseCategoryList(String id) {
        List<CourseCategoryTreeDto> categoryList = courseCategoryMapper.queryCourseCategoryList(id);
        List<CourseCategoryTreeDto> result = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> hashMap = new HashMap<>();

        categoryList.forEach(item -> {
            hashMap.put(item.getId(), item);
            //先将要查询节点的所有直接子节点放入结果
            if (item.getParentId().equals(id)){
                result.add(item);
            }

            String parentId = item.getParentId();
            CourseCategoryTreeDto parentNode = hashMap.get(parentId);
            //当前节点的父节点若不为空，则将当前节点放入其父节点的 ChildrenTreeNodes 属性中
            if (null != parentNode){
                List<CourseCategoryTreeDto> childrenTreeNodes = parentNode.getChildrenTreeNodes();
                if (null == childrenTreeNodes){
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                parentNode.getChildrenTreeNodes().add(item);
            }
        });
        return result;
    }
}
