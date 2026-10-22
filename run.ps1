$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw "Java compiler (javac) was not found. Install a JDK and add it to PATH."
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java runtime (java) was not found. Install a JDK and add it to PATH."
}

$sources = @(Get-ChildItem -Path src, test -Recurse -Filter *.java |
    ForEach-Object { $_.FullName })
if ($sources.Count -eq 0) {
    throw "No Java source files were found under src or test."
}

New-Item -ItemType Directory -Path build -Force | Out-Null
javac -cp 'lib\*' -d build $sources
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

java --enable-native-access=ALL-UNNAMED -cp 'build;lib\*' Main --demo
exit $LASTEXITCODE
