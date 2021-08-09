package rainbow.kuzwlu.web.model.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 16:44
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sys_resource")
public class SysResource implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String sign;

    private String name;

    private Integer parentResourceId;

    private String url;

    private Integer seq;

    private Integer status;

}
