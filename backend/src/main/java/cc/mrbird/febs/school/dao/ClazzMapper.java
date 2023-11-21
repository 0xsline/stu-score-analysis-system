package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.Clazz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {

    /**
     * 获取最大的班级编号
     *
     * @return
     */
    @Select("select coalesce(max(CLAZZ_NO) + 1, 10001) from school_clazz")
    Long getMaxClazzNo();
}
