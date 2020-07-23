# maktub-demo

> maktub项目后端基础工程

## 开发须知 ##

* 环境搭建: jdk1.8、gradle4.0（或以上）
* IDE: idea（推荐）、eclipse
* 其他开发工具: postman（用于测试接口）、git（用于代码和版本控制）
* 后端相关技术: springboot、mybatis plus、mysql、redis、rocketmq等。

-----

> * 开发前，请具备常用的java后端技术，之后熟悉本项目涉及的相关技术，主要为springboot和mybatis plus相关技术。
> * 项目导入并编译后，直接到maktub-service-core模块下的CoreApplication类中，使用run运行即可启动。
> * 使用postman请求现有接口，体会项目中接口的请求和返回逻辑，可配合debug模式熟悉整个流程。
> * 所有代码和数据库开发请务必遵循《阿里巴巴Java开发手册》的规范，以便其他开发人员阅读和运维部署。现有代码中已有一些简单的示例，可参考。

-----

## 项目搭建

-----

- 启动idea，使用open打开maktub-boot项目即可。

-----

## 目录结构介绍 ##
	|--maktub-common
	|  |--constant
	|  |--exceptions
	|  |--models
	|  |--utils
	|  |--valid
	|--maktub-common-db
	|  |--conf
	|  |--datasources
	|  |--entity
	|  |--mapper
	|  |--service
	|--maktub-common-shiro
	|--maktub-service-core
	|  |--controlle

## 模块释义 ##

**maktub-boot项目暂时包含4个模块。
maktub-common-xxx命名的模块为公共模块。所有公共操作都放在这些模块中，后续新增的公共模块，也必须以maktub-common-xxx命名。
maktub-service-xxx命名的模块为服务模块。与具体业务关联，后续新增的服务模块，也必须以maktub-service-xxx命名。
**

-----

### maktub-common-db ###

#### conf
redis,druid,mybatis-plus等基础配置

#### datasources
##### datasources.annotation
- 多数据源注解，拟用注解的方式```@DataSource```在service中标注当前方法所操纵的数据源
- 默认以空标识,无需做出注解会自动匹配默认数据源；

##### datasources.aspect
 - 多数据源，切面处理类，通过注解例如```@DataSource(name = DataSourceNames.DIM)```在service层中来指定当前方法所需的具体数据源
eg:
        @DataSource(name = DataSourceNames.DIM)
		public String getServiceId(String appkey) {
			return otherMapper.getServiceId(appkey);
		}

 - 结合切面编程的方式，调用方法时自动匹配相应的数据源，无注解时默认数据源为```DataSourceNames.DEFAULT```，其他根据注解去配置数据源，并结合logger在切换数据源时进行日志打印
 - 由于项目中实现动态多数据源的随意切换，所以在代码中已实现调用完数据源后会自动关闭当前切换的数据源，无需手动配置

增加多数据源，可在```DataSourceNames```方法中配置，其他方法为动态多数据源的具体实现以及相应配置

#### entity、mapper、service
- 具体业务逻辑以及一些数据操作
- 由于部分crud操作存在简单重复性问题，拟采用[codex]自动生成工具，来自动生成代码，具体步骤如下：
 - 在首页进行个人账户的注册
 - 选中crud服务，在右侧数据源中点击```没有数据源点我创建数据源```注册想要的数据源信息，填入相应的连接名，URL，用户名，密码
 - 选中数据源会出现数据库中表的相应信息，然后在右侧模板点击```创建模板```，配置工程相应的包路径信息，具体可看提示信息
 - 选中设置的模板，然后选择想要生成的表项，点击```生成代码```，会自动完成压缩包下载，只需将解压后文件按路径复制到工程中即可
- 注意：由于codex只能代替完成简单的crud代码生成，如存在复杂逻辑需要手动进行修改或者自行编码 

#### valid
当前工具类用于校验输入参数的权限分组，包含jsr自定义校验bean，使用jsr-bean验证规范，在使用时以VG类（使用jsr-bean验证规范）中interface 标识作为校验权限分组，例如Add，Get，Delete等，

		/**
		 * 使用jsr-bean验证规范
		 */
		public interface VG {
		    // 新增
		    interface Add {
		    }
		
		    // 删除
		    interface Delete {
		    }
		
		    // 查询单条记录
		    interface Get {
		    }
			......
		}

具体使用可参考以下案例
eg:

		@PostMapping("get")
		public R get(@RequestBody @Validated(VG.Get.class) AppkeyRatelimiteInfo appkeyRatelimiteInfo){
	    	AppkeyRatelimiteInfo reInfo = ratelimiteInfoService.selectById(appkeyRatelimiteInfo.getAppkey());
	    	return R.ok().put("reInfo",reInfo);
		}

由示例可见，当前方法输入实体参数appkeyRatelimiteInfo的校验权限分组为```Get（VG.Get.class）```，具体实体内变量的分组表示方式以```appkey```和```limiter```为例，具体如下：

		/**
	 	* appkey
	 	*/
	    @NotBlank( groups = {VG.Add.class, VG.Delete.class, VG.Get.class})
	    private String appkey;
	    /**
	     * limiter
	     */
	    @NotNull( groups = VG.Add.class)
	    private Long limiter;

appkey校验分组为Add，Delete，Get，当前为多分组权限表示方法，limiter校验分组为Add，当前为单分组权限表示方法
分组校验的含义以案例中get方法为例，当前get方法的分组为Get，那么实体appkeyRatelimiteInfo中变量的校验分组含有Get的变量的注解就会生效，反之，不生效，以此来控制不同方法对于相同实体参数内不同变量值的不同必需要求。

-----

### maktub-common

#### constant 
- CommonConstant类
 - 公共常量，基础常量可以放在此类中
 - 实现通用常量接口的方法也可以写在此类中 
- IEnum接口
 - 定义通用常量的接口
		
#### exceptions
- CodeDefined

 - 实现消息常量接口的方法，返回的成功或者失败的消息都可以写在这里面。 
	```
	eg: SUCCESS("0000", "成功")
	```
	
- GlobalExceptionHandler类

 - 全局统一异常处理类，例如自定义异常处理、参数验证异常处理、未知异常处理等都可以放在全局统一异常类中，eg:  

 	 - 请求参数语法错误 
		```java
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	@ResponseBody
	public R httpMessageNotReadableExceptionHandler(HttpServletRequest req, Exception ex) throws Exception {
	    logger.error("json语法错误异常:", ex);
	    return R.error(CodeDefined.ERROR_SYNTAX);
	}
	```
	404异常处理
	
	```java
	@ExceptionHandler(value = NoHandlerFoundException.class)
	@ResponseBody
	public R noHandlerExceptionHandler(HttpServletRequest req, Exception ex) throws Exception {
	    return R.error(CodeDefined.URL_NOT_FOUND);
	}
	```
	
- RException类

 - 自定义异常类，继承了输出时异常，方法格式例如
		```java
	public RException(String msg, String code) {
	    super(msg);
	    this.code = code;
	    this.msg = msg;
	}
	public RException(String msg) {
	    super(msg);
	    this.msg = msg;
	}
	```
   其中参数msg是异常消息描述，code是异常码。传参不同，会重写对应的异常方法。
   
- IMessageEnum

 - 定义消息常量的接口

#### models
- Query
 - 去map里查找分页条件以及参数信息
- R
 - 返回数据信息，包括成功数据和失败数据的信息，传参不同，会重写返回数据信息方法。
 eg:
```java
public static R error(String code, String msg) {
    R r = new R();
    r.put("code", code);
    r.put("msg", msg);
    return r;
}
public static R error() {
    return error(CodeDefined.ERROR.getValue(), CodeDefined.ERROR.getDesc());
}
public static R ok() {
    return new R();
}
public static R ok(String msg) {
    R r = new R();
    r.put("msg", msg);
    return r;
}
```

#### utils
- 一般都是放各种工具类，例如日期时间转换工具类、分页工具类、获取上下文工具类等。

-----

### maktub-service-core

#### controller
主要包含工程中所有controller，作为对外的接口，完整的实现逻辑以```UserController```中的register方法为例，具体如下：<br>
eg:

```java
/**
  * 注册
  *
  * @return
  */
@PostMapping("register")
@ResponseBody
public R register(@RequestBody @Validated(VG.Add.class) UserEntity userEntity) {

    userService.insert(userEntity);

    return R.ok();
}
```

该方法中```userEntity```包含在request请求体内，并使用了校验权限分组，该权限分组为```Add(VG.Add.class)```，实体中部分变量权限如下
	
```Java
/**
  * id
  */
@NotBlank(groups = {VG.Passwd.class, VG.Update.class})
private String id;
/**
  * 用户名
  */
@NotBlank(groups = {VG.Login.class, VG.Add.class})
@Length(min = 5, max = 20, groups = {VG.Add.class})
@Pattern(regexp = "^[a-zA-Z](\\w{0,19})$", groups = {VG.Add.class})
private String userName;
/**
  * 密码
  */
@NotBlank(groups = {VG.Login.class, VG.Add.class, VG.Passwd.class})
private String password;

......
```

按照案例中校验要求，可验证用户名和密码等信息，同时相应的注解将生效。当参数校验完毕，会将实体参数```userEntity```传入service层，由于工程架构中使用了mybatis-plus，部分常规的crud操作可以直接通过继承```IService<T>```来调用mybatis-plus中自带的一些service、mapper以及*.xml方法，无需手动编写，其中```T```为泛型实体，在当前案例中以```UserEntity```替换，```UserService extends IService<UserEntity>```

```java
/**
  * 顶级 Service
  */
public interface IService<T> {

    /**
	  * 插入一条记录（选择字段，策略插入）
	  *
      * @param entity 实体对象
	  * @return boolean
	  */
    boolean insert(T entity);

    ......
}
```

在serviceImpl中会继承```ServiceImpl<M extends BaseMapper<T>, T>```,其中```M 是 mapper 对象，T 是实体```，在当前案例中依次以```UserMapper```和```UserEntity```替换，```UserMapper extends BaseMapper<UserEntity>```，```UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>```

```java
/**
  * Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
  * 这个 Mapper 支持 id 泛型
  */
public interface BaseMapper<T> {

    /**
	  * 插入一条记录
	  *
	  * @param entity 实体对象
	  * @return int
	  */
    Integer insert(T entity);

    ......
}
```


```java
/**
  * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
  */
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {
    ......
}
```

注意：

- 在使用自带的mapper以及.xml 前，需要在serviceImpl中重载方法对数据进行详细处理，然后调用```super.```完成数据层操作，如果部分自定义方法想要调用自带数据层操作可以使用```baseMapper```来调用
- 由于mybatis-plus只能代替完成简单的crud操作，如存在复杂逻辑需要自行编码完成service、mapper以及*.xml等
- 即使调用mybatis-plus自带方法，可以无需完成编写具体的实现代码，但是相应的类文件还是需要编写在项目中，mybatis-plus会根据方法名去自动匹配相应的类文件，至于相应的方法会自动调用自己的库方法

#### CoreApplication

工程的入口，springboot的启动类。
