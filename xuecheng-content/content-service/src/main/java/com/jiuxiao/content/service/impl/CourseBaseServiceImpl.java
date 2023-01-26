package com.jiuxiao.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiuxiao.base.module.PageParams;
import com.jiuxiao.base.module.PageResult;
import com.jiuxiao.base.utils.AssertUtils;
import com.jiuxiao.content.mapper.CourseBaseMapper;
import com.jiuxiao.content.mapper.CourseCategoryMapper;
import com.jiuxiao.content.mapper.CourseMarketMapper;
import com.jiuxiao.content.module.dto.AddCourseDto;
import com.jiuxiao.content.module.dto.CourseBaseInfoDto;
import com.jiuxiao.content.module.dto.QueryCourseParamsDto;
import com.jiuxiao.content.module.po.CourseBase;
import com.jiuxiao.content.module.po.CourseMarket;
import com.jiuxiao.content.service.CourseBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 * @author 悟道九霄
 * @since 2023-01-18
 */
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseMarketMapper courseMarketMapper;

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * @param pageParams
     * @param queryCourseParamsDto
     * @return: com.jiuxiao.base.module.PageResult<com.jiuxiao.content.module.po.CourseBase>
     * @decription 查询课程基本信息列表
     * @date 2023/1/25 15:41
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseInfo(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //课程名称
        queryWrapper.like(
                StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName, queryCourseParamsDto.getCourseName()
        );
        //课程审核状态
        queryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus()
        );
        //课程发布状态
        queryWrapper.eq(
                StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus, queryCourseParamsDto.getPublishStatus()
        );

        //分页结果
        Page<CourseBase> page = new Page<>(pageParams.getCurrentPageNum(), pageParams.getPageSize());
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        return new PageResult<CourseBase>(
                courseBasePage.getRecords(),
                courseBasePage.getTotal(),
                pageParams.getCurrentPageNum(),
                pageParams.getPageSize()
        );
    }

    /**
     * @param companyId
     * @param addCourseDto
     * @return: com.jiuxiao.content.module.dto.CourseBaseInfoDto
     * @decription 新增课程基本信息
     * @date 2023/1/25 15:41
     */
    @Override
    @Transactional
    public CourseBaseInfoDto addCourseBase(Long companyId, AddCourseDto addCourseDto) {
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCompanyName(courseBase.getCompanyName());
        courseBase.setAuditStatus("202002"); //审核状态默认为 “未提交”
        courseBase.setStatus("203001"); //发布状态默认为 “未发布”
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setChangeDate(LocalDateTime.now());
        int insertCourseBase = courseBaseMapper.insert(courseBase);

        //向 courseMarket 表插入信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        //如果课程为收费，则现价与原价必须进行相关判断
        if (addCourseDto.getCharge().equals("201001")) {
            AssertUtils.isTrue(
                    courseMarket.getPrice() != null || courseMarket.getOriginalPrice() != null,
                    "收费课程必须设置原价和现价"
            );
            AssertUtils.isTrue(
                    courseMarket.getPrice() > 0 && courseMarket.getOriginalPrice() > 0,
                    "收费课程的现价或原价必须大于零"
            );
            AssertUtils.isTrue(
                    courseMarket.getPrice() < courseMarket.getOriginalPrice(),
                    "收费课程的原价必须大于或等于现价"
            );
        }
        int insertCourseMarket = courseMarketMapper.insert(courseMarket);
        AssertUtils.isTrue(insertCourseBase > 0 && insertCourseMarket > 0, "课程添加失败");

        return getCourseInfoById(courseId);
    }

    /**
     * @param courseId 课程ID
     * @return: com.jiuxiao.content.module.dto.CourseBaseInfoDto
     * @decription 通过课程ID查询课程的基本信息和营销信息
     * @date 2023/1/25 15:32
     */
    private CourseBaseInfoDto getCourseInfoById(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseResult = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseResult);
        BeanUtils.copyProperties(courseMarket, courseResult);

        String mt = courseBase.getMt();
        String st = courseBase.getSt();
        CourseBase mtCategory = courseBaseMapper.selectById(mt);
        CourseBase stCategory = courseBaseMapper.selectById(st);
        if (null != mtCategory) {
            courseResult.setMtName(mtCategory.getName());
        }
        if (null != stCategory) {
            courseResult.setStName(stCategory.getName());
        }
        return courseResult;
    }
}
