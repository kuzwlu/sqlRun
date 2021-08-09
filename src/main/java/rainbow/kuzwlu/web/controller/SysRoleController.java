package rainbow.kuzwlu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import rainbow.kuzwlu.framework.common.BootPage;
import rainbow.kuzwlu.framework.common.ErrorResult;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.utils.Resource;
import rainbow.kuzwlu.web.mapper.master.SysRoleMapper;
import rainbow.kuzwlu.web.mapper.master.SysUserMapper;
import rainbow.kuzwlu.web.model.master.SysRole;
import rainbow.kuzwlu.web.model.master.SysUser;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 12:15
 * @Email kuzwlu@gmail.com
 */
@RestController
@RequestMapping("/admin/role")
@RolesAllowed(Resource.ADMIN)
public class SysRoleController {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/getList")
    @Log("获取用户分组列表")
    public BootPage<SysRole> getList(BootPage<SysRole> page){
        QueryWrapper queryWrapper = new QueryWrapper();
        if (!StringUtils.isEmpty(page.getSearch())) {
            String search = page.getSearch();
            queryWrapper.like("name",search);
        }
        IPage<SysRole> userPage = new Page<>(page.getOffset(), page.getLimit());
        userPage = sysRoleMapper.selectPage(userPage,queryWrapper);
        page.setData(userPage.getRecords());
        page.setRecordsTotal((int) userPage.getTotal());
        return page;
    }

    @PostMapping("/add")
    @Log("添加用户分组")
    public JsonResult addRole(@Valid @RequestBody SysRole sysRole){
        if (hasRoleName(sysRole.getName())) {
            int insert = sysRoleMapper.insert(sysRole);
            if (insert == 1) {
                return SuccessResult.jsonSuccess("添加成功");
            }
        }else{
            return ErrorResult.jsonError("分组名重复");
        }
        return ErrorResult.jsonError("添加失败");
    }

    @PostMapping("/update")
    @Log("修改用户分组")
    public JsonResult updateRole(@Valid @RequestBody SysRole sysRole) {
        if (hasRoleName(sysRole.getName())){
            int i = sysRoleMapper.updateById(sysRole);
            if (i == 1) {
                return SuccessResult.jsonSuccess("修改成功");
            }
        }else {
            return ErrorResult.jsonError("分组名重复");
        }
        return ErrorResult.jsonError("修改失败");
    }

    @PostMapping("/delete/{id}")
    @Log("删除用户分组")
    public JsonResult deleteRole(@PathVariable("id") Integer id){
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("roleId",id);
        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);
        if (sysUsers.size() == 0) {
            int i = sysRoleMapper.deleteById(id);
            if (i == 1) {
                return SuccessResult.jsonSuccess("删除成功");
            }else{
                return ErrorResult.jsonError("删除失败");
            }
        }
        return ErrorResult.jsonError("该分组内还存在用户");
    }

    public boolean hasRoleName(String roleName){
        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("name",roleName);
        SysRole sysRole = sysRoleMapper.selectOne(sysRoleQueryWrapper);
        if (sysRole == null) {
            return true;
        }
        return false;
    }

}
