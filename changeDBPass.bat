@echo off
setlocal

set /p newpass=Enter new DB password:

set "file=com\subashita\inventory\Util\DBConnection.java"
set "tempfile=%file%.tmp"

echo Updating password in %file% ...

(for /f "usebackq delims=" %%A in ("%file%") do (
    set "line=%%A"
    setlocal enabledelayedexpansion
    if "!line!" neq "" (
        echo !line! | find "private static final String PASSWORD" >nul
        if !errorlevel! == 0 (
            echo     private static final String PASSWORD = "%newpass%";
        ) else (
            echo !line!
        )
    ) else (
        echo.
    )
    endlocal
)) > "%tempfile%"

move /Y "%tempfile%" "%file%" >nul

echo Done! Password updated.

endlocal
