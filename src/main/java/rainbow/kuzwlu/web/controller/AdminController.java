package rainbow.kuzwlu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.framework.security.UserSession;
import rainbow.kuzwlu.utils.Resource;
import rainbow.kuzwlu.web.mapper.master.SysResourceMapper;
import rainbow.kuzwlu.web.model.master.SysResource;
import rainbow.kuzwlu.web.model.master.SysRoleResource;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 11:00
 * @Email kuzwlu@gmail.com
 */
@RestController
public class AdminController {

    @Autowired
    private SysResourceMapper sysResourceMapper;

    /**
     * 获取管理员菜单
     * @param authentication
     * @return
     */
    @PostMapping("/getMenu")
    @Log("获取菜单")
    public JsonResult getMenu(Authentication authentication){
        UserSession userSession = (UserSession) authentication.getPrincipal();
        List<SysRoleResource> roleList = userSession.getRoles();
        List<SysResource> roles = new ArrayList<>();
        for (SysRoleResource sysRoleResource : roleList) {
            if (sysRoleResource.getId() != null) {
                QueryWrapper queryWrapper = new QueryWrapper<>();
                Map<String,Object> map = new HashMap<>();
                map.put("id",sysRoleResource.getResourceId());
                map.put("status",1);
                queryWrapper.allEq(map);
                roles.add(sysResourceMapper.selectOne(queryWrapper));
            }
        }
        return SuccessResult.jsonSuccess("查询成功","data",roles);
    }

}
