@echo off
call %~dp0setenv.bat
%wrapper_bat% -p %conf_file%
pause


