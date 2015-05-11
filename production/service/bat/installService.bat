@echo off
call %~dp0setenv.bat
%wrapper_bat% -i %conf_file%
pause


