@echo off
echo Running app...

if not exist out (
    echo Error: Output folder 'out' does not exist. Compile first!
    exit /b 1
)

cd out

java -cp ".;../lib/*" com.application.ui.MainFrame

cd ..
echo App exited.
