package rainbow.kuzwlu.web.model.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rainbow.kuzwlu.core.datasource.SimpleDataSource;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 11:09
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_sys_sql")
public class SysSql {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    private Integer initialSize;

    private Integer minIdle;

    private Integer maxActive;

    private Long timeBetweenEvictionRunsMillis;

    private Long minEvictableIdleTimeMillis;

    private String validationQuery;

    private Integer maxWait;

    private String type;

    private Integer status;

    private String DBName;

}
