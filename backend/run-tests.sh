#!/bin/bash
# 快速运行单元测试

cd /home/sam/.openclaw/workspace/organization-service

echo "=== 运行单元测试 ==="

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 未检测到 Java，请先运行: ./setup-dev.sh"
    exit 1
fi

# 运行测试
./gradlew test --no-daemon "$@"

# 显示结果
TEST_RESULT=$?

echo ""
if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ 所有测试通过！"
    echo ""
    echo "📊 查看测试报告:"
    echo "  build/reports/tests/test/index.html"
else
    echo "❌ 有测试失败"
    echo ""
    echo "📋 查看失败详情:"
    echo "  build/reports/tests/test/index.html"
    echo ""
    echo "🔍 运行特定测试:"
    echo "  ./gradlew test --tests DepartmentServiceTest"
    echo "  ./gradlew test --tests '*Controller'"
fi

exit $TEST_RESULT
