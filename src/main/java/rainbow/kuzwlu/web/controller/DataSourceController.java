package rainbow.kuzwlu.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import rainbow.kuzwlu.DataSourceRunTime;
import rainbow.kuzwlu.core.datasource.DynamicDataSource;
import rainbow.kuzwlu.core.datasource.SimpleDataSource;
import rainbow.kuzwlu.framework.common.BootPage;
import rainbow.kuzwlu.framework.common.ErrorResult;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.framework.common.SuccessResult;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.utils.DataSourceUtil;
import rainbow.kuzwlu.utils.Resource;
import rainbow.kuzwlu.web.mapper.master.SysSqlMapper;
import rainbow.kuzwlu.web.model.master.SysSql;

import javax.annotation.security.RolesAllowed;
import javax.sql.DataSource;
import javax.validation.Valid;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 11:36
 * @Email kuzwlu@gmail.com
 */
@RestController
@RolesAllowed(Resource.ADMIN)
@RequestMapping("/admin/dataSource")
public class DataSourceController {

    private Logger log = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private SysSqlMapper sysSqlMapper;

    @PostMapping("/getList")
    @Log("获取数据库列表")
    public BootPage<SysSql> getList(BootPage<SysSql> page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (!StringUtils.isEmpty(page.getSearch())) {
            String search = page.getSearch();
            queryWrapper.like("DBName", search);
        }
        IPage<SysSql> userPage = new Page<>(page.getOffset(), page.getLimit());
        userPage = sysSqlMapper.selectPage(userPage, queryWrapper);
        page.setData(userPage.getRecords());
        page.setRecordsTotal((int) userPage.getTotal());
        return page;
    }

    @PostMapping("/getDataSource/{id}")
    public JsonResult getDataSource(@PathVariable("id") Integer id){
        SysSql sysSql = sysSqlMapper.selectById(id);
        if (sysSql != null) {
            return SuccessResult.jsonSuccess("查询成功","data",sysSql);
        }
        return ErrorResult.jsonError("查询失败");
    }

    @PostMapping("/add")
    @Log("添加数据库")
    public JsonResult add(@Valid @RequestBody SysSql sysSql) {
        if (hasDBName(sysSql.getDBName())) {
            int insert = sysSqlMapper.insert(sysSql);
            if (insert == 1) {
                DataSource dataSource = DataSourceUtil.createDataSource(createDataSource(sysSql));
                DataSourceRunTime.getRuntime().registerDataSource(sysSql.getDBName(), dataSource);
                log.info("Injected dataSource: [{}]", sysSql.getDBName());
                return SuccessResult.jsonSuccess("添加成功");
            }
        } else {
            return ErrorResult.jsonError("数据库名已存在");
        }
        return ErrorResult.jsonError("添加失败");
    }

    @PostMapping("/update")
    @Log("修改数据库")
    public JsonResult update(@Valid @RequestBody SysSql sysSql) {
        String dbName = sysSql.getDBName();
        sysSql.setDBName(null);
        int update = sysSqlMapper.updateById(sysSql);
        if (update == 1) {
            DataSource dataSource = DataSourceUtil.createDataSource(createDataSource(sysSql));
            DataSourceRunTime.getRuntime().registerDataSource(dbName, dataSource);
            log.info("Update dataSource: [{}]", dbName);
            return SuccessResult.jsonSuccess("修改成功");
        }
        return ErrorResult.jsonError("修改失败");
    }

    @PostMapping("/delete/{id}")
    @Log("删除数据库")
    public JsonResult delete(@PathVariable("id") Integer id) {
        SysSql sysSql = sysSqlMapper.selectById(id);
        int i = sysSqlMapper.deleteById(id);
        if (i == 1) {
            DynamicDataSource.removeDataSource(sysSql.getDBName());
            return SuccessResult.jsonSuccess("删除成功");
        }
        return ErrorResult.jsonError("删除失败");
    }

    public boolean hasDBName(String DBName) {
        QueryWrapper<SysSql> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("DBName", DBName);
        SysSql dbName = sysSqlMapper.selectOne(objectQueryWrapper);
        if (dbName == null) {
            return true;
        }
        return false;
    }

    public SimpleDataSource createDataSource(SysSql sysSql) {
        if (sysSql.getInitialSize() == null) {
            sysSql.setInitialSize(5);
        }
        if (sysSql.getMinIdle() == null) {
            sysSql.setMinIdle(5);
        }
        if (sysSql.getMaxActive() == null) {
            sysSql.setMaxActive(20);
        }
        if (sysSql.getTimeBetweenEvictionRunsMillis() == null || sysSql.getTimeBetweenEvictionRunsMillis() == 0) {
            sysSql.setTimeBetweenEvictionRunsMillis(60000L);
        }
        if (sysSql.getMinEvictableIdleTimeMillis() == null || sysSql.getMinEvictableIdleTimeMillis() == 0) {
            sysSql.setMinEvictableIdleTimeMillis(300000L);
        }
        if (sysSql.getValidationQuery() == null) {
            sysSql.setValidationQuery("SELECT 1 FROM DUAL");
        }
        if (sysSql.getMaxWait() == null) {
            sysSql.setMaxWait(60000);
        }
        return SimpleDataSource.builder().driverClassName(sysSql.getDriverClassName())
                .initialSize(sysSql.getInitialSize())
                .maxActive(sysSql.getMaxActive())
                .maxWait(Long.parseLong(sysSql.getMaxWait() + ""))
                .minEvictableIdleTimeMillis(sysSql.getMinEvictableIdleTimeMillis())
                .minIdle(sysSql.getMinIdle())
                .password(sysSql.getPassword())
                .timeBetweenEvictionRunsMillis(sysSql.getTimeBetweenEvictionRunsMillis())
                .url(sysSql.getUrl())
                .username(sysSql.getUsername())
                .validationQuery(sysSql.getValidationQuery()).build();
    }

}
