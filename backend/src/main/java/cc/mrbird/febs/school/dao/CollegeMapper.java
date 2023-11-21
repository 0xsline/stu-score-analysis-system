package cc.mrbird.febs.school.dao;

import cc.mrbird.febs.school.entity.College;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author IU
 */
@Mapper
public interface CollegeMapper extends BaseMapper<College> {


    @Select("select coalesce(max(COLLEGE_NO) + 1, 10001) from school_college")
    Long getMaxCollegeNo();
}
