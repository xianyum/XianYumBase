package cn.xianyum.common.entity.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 通用返回分页数据
 * @Description
 * @Author ZhangWei
 * @Date 2023/10/17 17:13
 */
@Data
public class PageResponse<R extends BaseResponse> {

    private long total;

    private List<R> dataList;

    /** 分页返回额外的数据，一般用于分页返回汇总数据 */
    private Object otherInfo;


    private PageResponse() {
    }

    private PageResponse(long total, List<R> dataList) {
        this.total = total;
        this.dataList = dataList;
    }

    /**
     *
     * @param total 总条数
     * @param responseList response实体list
     * @return
     * @param <R>
     */
    public static <R extends BaseResponse> PageResponse<R> of(long total,List<R> responseList) {
        return new PageResponse(total,responseList);
    }

    /**
     *
     * @param total 总条数
     * @param entityList entity实体list
     * @param rClass 转换为response实体
     * @return
     * @param <R>
     */
    public static <R extends BaseResponse> PageResponse<R> of(long total, List<?> entityList,Class<R> rClass) {
        List<R> rs = BeanUtil.copyToList(entityList,rClass);
        return new PageResponse(total, rs);
    }

    /**
     *
     * @param total 总条数
     * @param entityList entity实体list
     * @param rClass 转换为response实体
     * @param biConsumer （response,entity）-> {}
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T,R extends BaseResponse> PageResponse<R> of(long total, List<T> entityList,Class<R> rClass, BiConsumer<R, T> biConsumer) {
        List<R> rs = new ArrayList<>(entityList.size());
        for (T entity : entityList) {
            R response = BeanUtil.toBean(entity, rClass);
            biConsumer.accept(response, entity);
            rs.add(response);
        }
        return new PageResponse(total, rs);
    }

    /**
     *
     * @param pageResult mybatisPlus分页查询的数据（未经过处理的）
     * @param rClass 转换为response实体
     * @return
     * @param <R>
     */
    public static <R extends BaseResponse> PageResponse<R> of(IPage<?> pageResult, Class<R> rClass) {
        List<R> rs = BeanUtil.copyToList(pageResult.getRecords(),rClass);
        return new PageResponse(pageResult.getTotal(), rs);
    }

    /**
     *
     * @param pageResult mybatisPlus分页查询的数据（未经过处理的）
     * @param rClass 转换为response实体
     * @param biConsumer （response,entity）-> {}
     * @return
     * @param <T>
     * @param <R>
     */
    public static <T, R extends BaseResponse> PageResponse<R> of(IPage<T> pageResult, Class<R> rClass, BiConsumer<R, T> biConsumer) {
        List<R> rs = new ArrayList<>(pageResult.getRecords().size());
        for (T entity : pageResult.getRecords()) {
            R response = BeanUtil.toBean(entity, rClass);
            biConsumer.accept(response, entity);
            rs.add(response);
        }
        return new PageResponse(pageResult.getTotal(), rs);
    }

    /**
     * 返回空页数据
     * @return
     * @param <R>
     */
    public static <R extends BaseResponse> PageResponse<R> EMPTY_PAGE() {
        return new PageResponse(0L, new ArrayList<>());
    }

}
