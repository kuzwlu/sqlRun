package rainbow.kuzwlu.web.model.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 13:15
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_sys_log")
@ToString
public class SysLog {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String username; //用户名

    private String operation; //操作

    private String method; //方法名

    private String params; //参数

    private String ip; //ip地址

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate; //操作时间

    private String userAgent;// 浏览器

    private String requestUrl;

    private String requestMethod;

}
