@echo off
call %~dp0setenv.bat
%wrapper_bat% -q %conf_file%
pause

