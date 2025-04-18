@echo off

rem  This software complies with Apache License 2.0,
rem  detail: http://www.apache.org/licenses/LICENSE-2.0
rem
rem ---------------------------------------------------------------------------
rem Stop script for the Dbee Server
rem ---------------------------------------------------------------------------


setlocal

for /f "tokens=1" %%a in ('jps ^| findstr dbee') do (
	taskkill /f /pid %%a
)

pause