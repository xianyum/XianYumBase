package cn.xianyum.common.entity.base;

import cn.xianyum.common.utils.BeanUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/10/17 17:13
 */
@Data
public class PageResponse<R extends BaseResponse> {

    private long total;

    private List<R> dataList;

    public PageResponse() {
    }

    private PageResponse(long total, List<R> dataList) {
        this.total = total;
        this.dataList = dataList;
    }

    public static <R extends BaseResponse> PageResponse<R> of(Long total,List<R> dataList) {
        return new PageResponse(total,dataList);
    }

    public static <R extends BaseResponse> PageResponse<R> of(IPage<?> pageResult, Class<R> rClass) {
        List<R> rs = BeanUtils.copyList(pageResult.getRecords(),rClass);
        return new PageResponse(pageResult.getTotal(), rs);
    }

    public static <T, R extends BaseResponse> PageResponse<R> of(IPage<T>  pageResult, Class<R> rClass, BiConsumer<R, T> biConsumer) {
        List<R> rs = BeanUtils.copyList(pageResult.getRecords(),rClass,biConsumer);
        return new PageResponse(pageResult.getTotal(), rs);
    }

    public static <R extends BaseResponse> PageResponse<R> EMPTY_PAGE() {
        return new PageResponse(0L, new ArrayList<>());
    }

}
