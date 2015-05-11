@echo off
call %~dp0setenv.bat
%wrapperw_bat% -c %conf_file%
