package cc.mrbird.febs.school.service;


import cc.mrbird.febs.common.exception.FebsException;

public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {
    /**
     * 插入一条记录
     *
     * @param entity
     * @return
     */

    default boolean insert(T entity) throws Exception {
        return false;
    }


    /**
     * 更新一条记录
     *
     * @param entity
     * @return
     */

    default boolean modify(T entity) throws Exception {
        return false;
    }


}
