@echo off
echo Cleaning...

if exist out (
    rd /s /q out
)

mkdir out

if exist sources.txt (
    del sources.txt
)

echo Listing .java files...
for /r %%f in (*.java) do echo %%f >> sources.txt

echo Compiling...
javac -d out -cp "lib/*;out" @sources.txt

echo Cleaning up...
if exist sources.txt (
    del sources.txt
)

echo Done compiling!
