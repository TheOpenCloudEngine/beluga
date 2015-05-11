rem usage genconfig.bat <pid> [-d <default configuration file>]<output file> 

@echo off
call %~dp0setenv.bat

%wrapper_bat% -g %1 -d %conf_default_file% %conf_file%
pause
