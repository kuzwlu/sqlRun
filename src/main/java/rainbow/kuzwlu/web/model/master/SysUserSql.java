package rainbow.kuzwlu.web.model.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 11:03
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sys_user_sql")
public class SysUserSql {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String sqlId;

}
