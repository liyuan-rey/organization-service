# Load-Env.ps1

param(
    [string]$File = ".env",
    [switch]$Scope = $false,  # 当前会话为默认，$true 表示全局持久化环境变量（需管理员权限）
    [string]$RootPath = (Get-Location).Path
)

$FilePath = Join-Path $RootPath $File

if (-not (Test-Path -Path $FilePath)) {
    Write-Warning "未找到 .env 文件：$FilePath"
    exit 1
}

try {
    $Content = Get-Content -Path $FilePath -Encoding UTF8
    
    foreach ($Line in $Content) {
        $Line = $Line.Trim()
        
        if ([string]::IsNullOrEmpty($Line) -or $Line.StartsWith("#")) {
            continue
        }

        if ($Line -match '^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*(.+?)\s*$') {
            $Key = $Matches[1].Trim()
            $Value = $Matches[2].Trim()

            # 去除两端单引号或双引号
            if (($Value -match '^".*"') -or ($Value -match "^'.*'")) {
                $Value = $Value.Substring(1, $Value.Length - 2)
            }

            try {
                Set-Item -Force -Path "env:$Key" -Value $Value
            } catch {
                Write-Error "设置环境变量失败: $Key - $($_.Exception.Message)"
                exit 1
            }
        } else {
            Write-Warning "跳过无效行: $Line"
        }
    }
    
    Write-Host "成功加载 $($Content.Count) 行配置到环境变量" -ForegroundColor Green
} catch {
    Write-Error "读取 .env 文件时发生错误: $($_.Exception.Message)"
    exit 1
}