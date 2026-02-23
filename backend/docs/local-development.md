# 本地开发指南

## 环境要求

- **Java 17+** (必需)
- **Gradle 8.14+** (项目自带)
- **Docker** (可选，用于集成测试)
- **PostgreSQL 15** (可选，Docker 会自动启动)

## 快速开始

### 1. 安装 Java 17

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y openjdk-17-jdk

# 验证安装
java -version
```

### 2. 设置环境变量

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# 重新加载
source ~/.bashrc
```

### 3. 运行测试

```bash
cd /home/sam/.openclaw/workspace/organization-service

# 方式1: 使用便捷脚本
./run-tests.sh

# 方式2: 直接使用 Gradle
./gradlew test --no-daemon
```

## 测试类型

### 单元测试

**运行：**
```bash
./gradlew test --no-daemon
```

**特点：**
- 使用 Mockito 模拟依赖
- 不需要数据库
- 快速执行

**位置：** `src/test/java/com/reythecoder/organization/`

### 集成测试 (暂时跳过)

**运行：**
```bash
./gradlew testIntegration --no-daemon
```

**注意：**
- 需要启动 Docker 数据库: `docker compose up -d`
- Hibernate + Record 兼容性问题（待解决）

## 常见问题

### 问题1: Java 未安装

**错误：**
```
ERROR: JAVA_HOME is not set and no 'java' command could be found
```

**解决：**
```bash
sudo apt install -y openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### 问题2: 测试失败 - Hibernate + Record

**错误：**
```
org.hibernate.PropertyAccessException
java.lang.IllegalAccessException
```

**说明：**
这是已知的 Java Record 与 Hibernate 兼容性问题。当前解决方案：
- 单元测试可以正常运行（使用 Mockito）
- 集成测试暂时跳过

**长期方案：**
- 等待 Hibernate 6.5+ 更好的 Record 支持
- 或使用专门的 Record-Hibernate 集成库

### 问题3: Gradle Daemon 问题

**错误：**
```
Gradle daemon is stopping
```

**解决：**
```bash
# 停止所有 daemon
./gradlew --stop

# 重新运行
./gradlew test --no-daemon
```

### 问题4: 依赖下载慢

**解决：**
项目已配置阿里云镜像，但如有需要可在 `~/.gradle/init.gradle` 添加：

```groovy
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        mavenCentral()
    }
}
```

## 运行特定测试

### 运行单个测试类

```bash
./gradlew test --tests DepartmentServiceTest --no-daemon
```

### 运行特定测试方法

```bash
./gradlew test --tests "DepartmentServiceTest.getAllDepartments_shouldReturnAllDepartments" --no-daemon
```

### 运行带名称模式的测试

```bash
# 所有 Controller 测试
./gradlew test --tests "*Controller" --no-daemon

# 所有 Service 测试
./gradlew test --tests "*Service" --no-daemon
```

## 查看测试报告

```bash
# HTML 报告
firefox build/reports/tests/test/index.html

# 或在 VS Code 中
code build/reports/tests/test/index.html
```

## 调试测试

### 添加调试信息

在测试中添加日志：

```java
@Slf4j
class DepartmentServiceTest {
    @Test
    void testSomething() {
        log.info("测试开始...");
        // ...
    }
}
```

### 运行测试并显示标准输出

```bash
./gradlew test --info --no-daemon
```

### 使用 IDE 调试

在 IDEA/VS Code 中：
1. 右键点击测试类
2. 选择 "Debug" 或 "调试"

## 开发工作流

### 1. 修改代码

```bash
# 编辑源代码
vim src/main/java/com/reythecoder/organization/service/...
```

### 2. 运行测试

```bash
./run-tests.sh
```

### 3. 查看结果

```bash
# 查看报告
firefox build/reports/tests/test/index.html

# 或查看控制台输出
./gradlew test --no-daemon
```

### 4. 提交代码

```bash
git add .
git commit -m "feat: 添加新功能"
git push origin main
```

## 性能优化

### 并行运行测试

在 `build.gradle` 中已启用：
```groovy
org.gradle.parallel=true
```

### 启用构建缓存

```bash
./gradlew test --no-daemon --build-cache
```

### 跳过某些测试

```bash
# 跳过集成测试
./gradlew test --exclude-task testIntegration --no-daemon
```

## 数据库相关

### 启动测试数据库

```bash
cd /home/sam/.openclaw/workspace/organization-service
docker compose up -d
```

### 查看数据库日志

```bash
docker compose logs -f postgres
```

### 连接数据库

```bash
psql -h localhost -U postgres -d organization_db
```

### 停止数据库

```bash
docker compose down
```

## 下一步

- [ ] 安装 Java 17
- [ ] 运行单元测试确认环境
- [ ] 查看测试报告
- [ ] 开始实现新功能

## 相关文档

- [项目架构](docs/project-architecture.md)
- [CI/CD 配置](docs/ci-cd-setup.md)
- [开发规范](docs/development-guidelines.md)

---

**最后更新：** 2026-02-22
