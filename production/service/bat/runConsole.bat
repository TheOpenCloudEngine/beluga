@echo off
call %~dp0setenv.bat
%wrapper_bat% -c %conf_file%
pause
