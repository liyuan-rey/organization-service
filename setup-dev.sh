#!/bin/bash
# 本地开发环境设置脚本

set -e

echo "=== Organization Service 本地开发环境设置 ==="
echo ""

# 检查是否安装了 Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    echo "✅ 已检测到 Java 版本: $JAVA_VERSION"

    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo "⚠️  需要 Java 17 或更高版本"
        echo "当前版本: $JAVA_VERSION"
        echo ""
        echo "请运行以下命令安装 Java 17:"
        echo "  sudo apt update"
        echo "  sudo apt install -y openjdk-17-jdk"
        exit 1
    fi
else
    echo "❌ 未检测到 Java"
    echo ""
    echo "请先安装 Java 17:"
    echo "  sudo apt update"
    echo "  sudo apt install -y openjdk-17-jdk"
    echo ""
    echo "安装后设置环境变量:"
    echo "  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
    echo "  export PATH=\$JAVA_HOME/bin:\$PATH"
    exit 1
fi

echo ""
echo "=== 运行单元测试 ==="
cd /home/sam/.openclaw/workspace/organization-service

echo "📦 清理旧的构建..."
./gradlew clean --no-daemon

echo ""
echo "🧪 运行单元测试..."
./gradlew test --no-daemon

echo ""
echo "=== 生成测试报告 ==="
echo "📄 测试报告位置: build/reports/tests/test/index.html"

echo ""
echo "=== 完成 ==="
echo "✅ 单元测试执行完毕"
echo ""
echo "查看详细报告:"
echo "  firefox build/reports/tests/test/index.html  # 或使用你喜欢的浏览器"
