package rainbow.kuzwlu.web.model.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 16:44
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sys_user")
public class SysUser implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer roleId;

    private String user;

    private String userName;

    private String password;

    private Integer status;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String comment;

}
