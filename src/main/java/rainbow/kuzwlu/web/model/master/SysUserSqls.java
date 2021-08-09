package rainbow.kuzwlu.web.model.master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 11:54
 * @Email kuzwlu@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUserSqls {

    private SysUser sysUser;

    private List<String> dataSourceName;

    private SysRole sysRole;

}
