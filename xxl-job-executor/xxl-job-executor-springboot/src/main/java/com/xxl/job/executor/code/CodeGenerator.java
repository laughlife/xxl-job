package com.xxl.job.executor.code;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * MyBatis-Plus 代码生成器
 * <p>
 * 支持按业务模块分包生成 Controller/Service/Mapper/Entity
 * 目录结构示例（业务包名为 order）：
 * <pre>
 * com.xxl.job.executor.biz
 *   └── order
 *       ├── controller
 *       │   └── OrderController.java
 *       ├── service
 *       │   ├── OrderService.java
 *       │   └── impl
 *       │       └── OrderServiceImpl.java
 *       ├── mapper
 *       │   └── OrderMapper.java
 *       └── entity
 *           └── Order.java
 * resources/mapper/order
 *   └── OrderMapper.xml
 * </pre>
 *
 * @author LiWei
 */
public class CodeGenerator {

    // ==================== Main 方法示例 ====================

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        /*
         * 示例1：生成 order 业务模块
         * 会在以下路径生成代码：
         * - com.xxl.job.executor.biz.order.controller
         * - com.xxl.job.executor.biz.order.service
         * - com.xxl.job.executor.biz.order.service.impl
         * - com.xxl.job.executor.biz.order.mapper
         * - com.xxl.job.executor.biz.order.entity
         * - resources/mapper/order/*.xml
         */
        CodeGenerator.create()
                // 数据库配置（如果 application.properties 中已配置，可省略）
                .dbConfig(
                        "jdbc:mysql://localhost:3306/ruoyi-vue-pro?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai",
                        "root",
                        "Liv88625200@@"
                )
                // 业务模块名
                .module("order")
                // 要生成的表（支持多个）
                .tables("xxl_job_info", "xxl_job_log")
                // 表前缀（生成时去除，xxl_job_info -> Info）
                .tablePrefixes("xxl_job_")
                // 作者
                .author("Li Wei")
                // 执行生成
                .execute();

        /*
         * 示例2：生成 user 业务模块
         */
        // CodeGenerator.create()
        //         .module("user")
        //         .tables("sys_user", "sys_role")
        //         .tablePrefixes("sys_")
        //         .execute();

        /*
         * 示例3：自定义基础包名
         */
        // CodeGenerator.create()
        //         .basePackage("com.example.app")
        //         .module("product")
        //         .tables("t_product")
        //         .tablePrefixes("t_")
        //         .execute();
    }

    // ==================== 配置项 ====================

    /**
     * 数据库连接配置
     */
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    /**
     * 基础包名
     */
    private String basePackage = "com.xxl.job.executor.biz";

    /**
     * 业务模块名（如 order, user, product）
     */
    private String moduleName;

    /**
     * 要生成的表名列表
     */
    private String[] tableNames;

    /**
     * 表前缀（生成时会去除）
     */
    private String[] tablePrefixes = {};

    /**
     * 作者
     */
    private String author = "LiWei";

    /**
     * 项目根路径
     */
    private String projectPath;

    // ==================== 构造方法 ====================

    public CodeGenerator() {
        // 自动获取当前模块路径
        this.projectPath = getModulePath();
        // 尝试从配置文件加载数据库配置
        loadDatabaseConfig();
    }

    // ==================== 链式配置方法 ====================

    public CodeGenerator dbConfig(String url, String username, String password) {
        this.dbUrl = url;
        this.dbUsername = username;
        this.dbPassword = password;
        return this;
    }

    public CodeGenerator basePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public CodeGenerator module(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public CodeGenerator tables(String... tableNames) {
        this.tableNames = tableNames;
        return this;
    }

    public CodeGenerator tablePrefixes(String... prefixes) {
        this.tablePrefixes = prefixes;
        return this;
    }

    public CodeGenerator author(String author) {
        this.author = author;
        return this;
    }

    public CodeGenerator projectPath(String projectPath) {
        this.projectPath = projectPath;
        return this;
    }

    // ==================== 核心生成方法 ====================

    /**
     * 执行代码生成
     */
    public void execute() {
        // 参数校验
        validateConfig();

        // 构建输出路径
        String javaOutputDir = projectPath + "/src/main/java";
        String mapperXmlOutputDir = projectPath + "/src/main/resources/mapper/" + moduleName;

        // 确保目录存在
        ensureDirectoryExists(javaOutputDir);
        ensureDirectoryExists(mapperXmlOutputDir);

        // 完整包名（包含业务模块）
        String fullPackage = basePackage + "." + moduleName;

        // 配置各层输出路径
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.entity, javaOutputDir + "/" + packageToPath(fullPackage + ".entity"));
        pathInfo.put(OutputFile.mapper, javaOutputDir + "/" + packageToPath(fullPackage + ".mapper"));
        pathInfo.put(OutputFile.service, javaOutputDir + "/" + packageToPath(fullPackage + ".service"));
        pathInfo.put(OutputFile.serviceImpl, javaOutputDir + "/" + packageToPath(fullPackage + ".service.impl"));
        pathInfo.put(OutputFile.controller, javaOutputDir + "/" + packageToPath(fullPackage + ".controller"));
        pathInfo.put(OutputFile.xml, mapperXmlOutputDir);

        // 确保所有输出目录存在
        pathInfo.values().forEach(this::ensureDirectoryExists);

        System.out.println("========== 代码生成配置 ==========");
        System.out.println("项目路径: " + projectPath);
        System.out.println("基础包名: " + fullPackage);
        System.out.println("业务模块: " + moduleName);
        System.out.println("生成表: " + String.join(", ", tableNames));
        System.out.println("Java输出: " + javaOutputDir);
        System.out.println("Mapper XML输出: " + mapperXmlOutputDir);
        System.out.println("==================================");

        // 执行生成
        FastAutoGenerator.create(dbUrl, dbUsername, dbPassword)
                // 全局配置
                .globalConfig(builder -> builder
                        .author(author)
                        .outputDir(javaOutputDir)
                        .disableOpenDir()  // 生成后不打开文件夹
                        .dateType(DateType.TIME_PACK)  // 使用 Java 8 时间类型
                        .commentDate("yyyy-MM-dd")
                )
                // 包配置
                .packageConfig(builder -> builder
                        .parent(fullPackage)
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .pathInfo(pathInfo)
                )
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames);

                    // 表前缀处理
                    if (tablePrefixes != null && tablePrefixes.length > 0) {
                        builder.addTablePrefix(tablePrefixes);
                    }

                    // Entity 策略
                    builder.entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .logicDeleteColumnName("deleted")
                            .logicDeletePropertyName("deleted");

                    // Mapper 策略
                    builder.mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList();

                    // Service 策略
                    builder.serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl");

                    // Controller 策略
                    builder.controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle();
                })
                // 模板引擎
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        System.out.println("========== 代码生成完成 ==========");
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取当前模块路径
     */
    private String getModulePath() {
        // 获取当前类所在路径，推导模块根路径
        String classPath = CodeGenerator.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();

        // 处理 Windows 路径
        if (classPath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
            classPath = classPath.substring(1);
        }

        // 如果是在 IDE 中运行，路径可能是 target/classes
        if (classPath.contains("target")) {
            classPath = classPath.substring(0, classPath.indexOf("target"));
        }

        // 尝试使用用户工作目录
        String userDir = System.getProperty("user.dir");

        // 如果用户目录包含模块名，直接使用
        if (userDir.contains("xxl-job-executor-springboot")) {
            return userDir;
        }

        // 否则尝试拼接完整路径
        Path modulePath = Paths.get(userDir, "xxl-job-executor", "xxl-job-executor-springboot");
        if (Files.exists(modulePath)) {
            return modulePath.toString();
        }

        // 兜底：使用用户目录
        return userDir;
    }

    /**
     * 从配置文件加载数据库配置
     */
    private void loadDatabaseConfig() {
        // 尝试加载 application.yml 或 application.properties
        try {
            Properties props = new Properties();

            // 优先尝试 properties 文件
            Path propsPath = Paths.get(projectPath, "src/main/resources/application.properties");
            if (Files.exists(propsPath)) {
                try (InputStream is = new FileInputStream(propsPath.toFile())) {
                    props.load(is);
                }
            }

            // 从 properties 读取
            String url = props.getProperty("spring.datasource.url");
            String username = props.getProperty("spring.datasource.username");
            String password = props.getProperty("spring.datasource.password");

            if (url != null && username != null && password != null) {
                this.dbUrl = url;
                this.dbUsername = username;
                this.dbPassword = password;
                System.out.println("从配置文件加载数据库配置成功");
            }
        } catch (IOException e) {
            System.out.println("加载配置文件失败，请手动配置数据库连接: " + e.getMessage());
        }
    }

    /**
     * 参数校验
     */
    private void validateConfig() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IllegalArgumentException("数据库URL未配置，请调用 dbConfig() 方法配置");
        }
        if (dbUsername == null || dbUsername.isEmpty()) {
            throw new IllegalArgumentException("数据库用户名未配置");
        }
        if (dbPassword == null) {
            throw new IllegalArgumentException("数据库密码未配置");
        }
        if (moduleName == null || moduleName.isEmpty()) {
            throw new IllegalArgumentException("业务模块名未配置，请调用 module() 方法配置");
        }
        if (tableNames == null || tableNames.length == 0) {
            throw new IllegalArgumentException("表名未配置，请调用 tables() 方法配置");
        }
    }

    /**
     * 包名转路径
     */
    private String packageToPath(String packageName) {
        return packageName.replace(".", "/");
    }

    /**
     * 确保目录存在
     */
    private void ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("创建目录: " + path);
            }
        }
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建代码生成器实例
     */
    public static CodeGenerator create() {
        return new CodeGenerator();
    }

    /**
     * 快速创建并配置数据库
     */
    public static CodeGenerator create(String url, String username, String password) {
        return new CodeGenerator().dbConfig(url, username, password);
    }


}
