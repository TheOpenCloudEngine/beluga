@echo off
call %~dp0setenv.bat
%wrapper_bat% -t %conf_file%
pause


