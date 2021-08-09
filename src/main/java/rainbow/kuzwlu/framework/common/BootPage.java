package rainbow.kuzwlu.framework.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 08:20
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
public class BootPage<T> {

    /**
     * 即没有过滤的记录数（数据库里总共记录数）
     */
    protected int recordsTotal;

    /**
     * 过滤后的记录数（如果有接收到前台的过滤条件，则返回的是过滤后的记录数）
     */
    protected int recordsFiltered;

    protected List<T> data;

    /**
     * 每页显示的条数
     */
    protected int limit = 10;

    /**
     * 第一条数据的起始位置
     */
    protected int offset = 0;

    /**
     * 搜索框
     */
    protected String search;

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsTotal;
    }
}

