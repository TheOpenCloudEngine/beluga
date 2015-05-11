@echo off
call %~dp0setenv.bat
%wrapper_bat% -r %conf_file%
pause