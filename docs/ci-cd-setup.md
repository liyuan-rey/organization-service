# CI/CD 配置说明

本项目使用 GitHub Actions 进行持续集成和持续部署。

## 工作流

### 1. CI - 持续集成 (`ci.yml`)

**触发条件：**
- 推送到 `main` 或 `develop` 分支
- 针对 `main` 或 `develop` 的 Pull Request

**作业：**

#### build-and-test
- ✅ 代码检出
- ✅ 设置 JDK 17 (Temurin)
- ✅ 缓存 Gradle 依赖
- ✅ 构建项目
- ✅ 运行单元测试
- ✅ 运行集成测试（使用 PostgreSQL 服务）
- ✅ 生成测试报告
- ✅ 上传构建产物和测试结果

#### code-quality
- ✅ Checkstyle 检查（如配置）
- ✅ SpotBugs 静态分析（如配置）
- ✅ 依赖更新检查

#### security-scan
- ✅ Trivy 漏洞扫描
- ✅ 结果上传到 GitHub Security

### 2. Release - 发布流程 (`release.yml`)

**触发条件：**
- 推送版本标签（如 `v1.0.0`）

**作业：**

#### build-and-release
- ✅ 构建 production JAR
- ✅ 创建 GitHub Release
- ✅ 上传 JAR 文件到 Release

#### docker-build-and-push
- ✅ 构建多架构 Docker 镜像 (amd64, arm64)
- ✅ 推送到 GitHub Container Registry (ghcr.io)
- ✅ 更新 Release 信息

## 使用方法

### 触发 CI

```bash
# 推送到 main 分支
git push origin main

# 或者创建 PR
gh pr create --base main --head feature-branch
```

### 创建 Release

```bash
# 打标签并推送
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions 会自动：
# 1. 构建 JAR
# 2. 创建 Release
# 3. 构建 Docker 镜像
# 4. 推送到 GHCR
```

### 使用 Docker 镜像

```bash
# 拉取镜像
docker pull ghcr.io/liyuan-rey/organization-service:v1.0.0

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/organization_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  ghcr.io/liyuan-rey/organization-service:v1.0.0
```

## 环境变量

### CI/CD 自动配置

CI 流水线自动配置以下环境变量：
- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/organization_db`
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=postgres`
- `SPRING_PROFILES_ACTIVE=test`

### Production (Release)

构建 production JAR 时：
- `SPRING_PROFILES_ACTIVE=prod`

## 状态徽章

在 README.md 中添加：

```markdown
![CI](https://github.com/liyuan-rey/organization-service/actions/workflows/ci.yml/badge.svg)
![Release](https://github.com/liyuan-rey/organization-service/actions/workflows/release.yml/badge.svg)
```

## 本地测试

```bash
# 运行所有测试
./gradlew test

# 运行集成测试
./gradlew testIntegration

# 构建项目
./gradlew build

# 构建 Docker 镜像
docker build -t organization-service:test .
```

## 高级配置

### 启用 Checkstyle

在 `build.gradle` 中添加：
```groovy
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '10.12.0'
    configFile = file('config/checkstyle/checkstyle.xml')
}
```

### 启用 SpotBugs

在 `build.gradle` 中添加：
```groovy
plugins {
    id 'com.github.spotbugs' version '6.0.0'
}

spotbugs {
    ignoreFailures = false
}
```

### 配置依赖更新提醒

```bash
# 创建 GitHub Actions Secret
gh secret set DEPENDENCYBOT_TOKEN --body "your-token"
```

## 故障排查

### 测试失败
- 查看测试报告 artifact
- 检查 GitHub Actions 日志
- 本地运行 `./gradlew test --info`

### Docker 构建失败
- 检查 Dockerfile 语法
- 验证多架构构建支持
- 查看 buildx 日志

### 发布失败
- 确认标签格式（如 `v1.0.0`）
- 检查 GITHUB_TOKEN 权限
- 验证 build.gradle 版本号

## 性能优化

- ✅ Gradle 依赖缓存
- ✅ Docker 层缓存
- ✅ 并行作业执行
- ✅ 增量构建

## 安全性

- ✅ Trivy 漏洞扫描
- ✅ 依赖更新检查
- ✅ 非_root 用户运行 Docker
- ✅ 健康检查配置

---

**最后更新：** 2026-02-22
