package rainbow.kuzwlu.web.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import rainbow.kuzwlu.core.annotation.DataSource;
import rainbow.kuzwlu.web.model.master.SysUser;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 17:04
 * @Email kuzwlu@gmail.com
 */
@Mapper
@DataSource
public interface SysUserMapper extends BaseMapper<SysUser> {

}
