# sqlRun
## 简介
    基于Springboot和dynamic-datasource-boot-starter多数据源的SQL代码执行器
    
    1、SpringBoot 2.4.0，
    
    2、dynamic-datasource-boot-starter 1.2 
        <dependency>
            <groupId>rainbow.kuzwlu</groupId>
            <artifactId>dynamic-datasource-boot-starter</artifactId>
            <version>1.2</version>
        </dependency>
    
    详情见项目：https://github.com/kuzwlu/dynamic-datasource-boot-starter
    [一个基于SpringBoot的Mybatis-Plus多数据源依赖包]

    3、redis
    4、前后端分离（Nginx）
    5、Mysql
    6、Authentication——Token认证
    7、AOP切面，使用@Log注解，将操作记录到数据库中
    8、统一API风格
    9、CORS跨域


## 使用方法

## 配合前端项目使用 项目地址：https://github.com/kuzwlu/sqlRun-HTML

### 1、application.yml详解 (配置数据源,log目录,Mapper包名)
    logging:
        file:
        path: /Users/kuzwlu/Downloads

    mybatis-plus:
        configuration:
        # 是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN（下划线命名） 到经典 Java 属性名 aColumn（驼峰命名） 的类似映射
        map-underscore-to-camel-case: false    

    rainbow:
      kuzwlu:
        #mapper包名
        mapper-package: rainbow.kuzwlu.web.mapper
        mapper-xml: classpath*:mapper/*.xml
        #设置数据库(动态数据源)
        # 一、无Service层（无事物管理）
        # 1、需要mapper接口上    标明@DataSource注解
        # 二、有Service层（有事物管理，可在Serviec层使用@Transactional注解）
        # 1、需要在service接口或者service接口的实现类    标明@DataSource注解
        datasource:
        #四种状态  一、数据库中读取副数据源 二、配置文件读取数据源 三、两者一起 四、没有副数据源(默认  NONE)
        #在DataSourceType枚举类下
        #DB PROPERTY TOGETHER NONE
        type: TOGETHER

      #如果不设置则不设置事务
      transaction:
        #切面
        aop-point-out: execution(* ${rainbow.kuzwlu.mapper-package}..*.*(..))
        #方法名规则限制，以下会加入事务管理当中
        require: add*,save*,create*,insert*,submit*,del*,remove*,update*,exec*,set*
        #对于查询方法，根据实际情况添加事务管理 可能存在查询多个数据时，已查询出来的数据刚好被改变的情况
        require-readonly: get*,select*,query*,find*,list*,count*,is*
      #主
      master:
        #副数据源---表 如果type为NONE可以不填写或者删除datasource-table等五个配置
        datasource-table: t_sys_sql
        #数据库自定义字段名，用来判断类型，数据库名称，状态
        datasource-table-type: type
        datasource-table-DBName: DBName
        datasource-table-status: status
        #status启用状态
        datasource-table-status-enabled: 1
        ###########################################################
        # mysql默认数据库的配置
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sql?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
        username: root
        password: 20001102zzw
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL

      #副---未声明则数据库的数据源数据不生效，如果type为NONE可以不填写subsidiary
      subsidiary: sqls,postgresql,test2
      sqls:
        # mysql默认数据库的配置
        driverClassName: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sqls?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
        username: sqls
        password: sqls
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL
      postgresql:
        # mysql默认数据库的配置
        driverClassName: org.postgresql.Driver
        url: jdbc:postgresql://localhost:5432/postgres
        username: postgres
        password: 20001102zzw
        initialSize: 5
        minIdle: 5
        maxActive: 20
        # 配置获取连接等待超时的时间
        maxWait: 60000
        # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        timeBetweenEvictionRunsMillis: 60000
        # 配置一个连接在池中最小生存的时间，单位是毫秒
        minEvictableIdleTimeMillis: 300000
        validationQuery: SELECT 1 FROM DUAL

### 2、下拉后可直接打成jar包运行  API接口端口为8080

## 效果图：
