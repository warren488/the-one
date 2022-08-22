## In order for the simulation to run correctly you must place the source files provided
## in their respective directories


# in order to run the various combinations of scenarios use the following commands at the
# root of the one directory (place new settings at the root of the project as well)

.\one.bat -b 22 .\new_settings\barbados_settings.txt .\new_settings\varying_bt_range.txt 
.\one.bat -b 22 .\new_settings\helsinki_settings.txt .\new_settings\varying_bt_range.txt 

.\one.bat -b 22 .\new_settings\barbados_settings.txt .\new_settings\varying_buffer_size.txt 
.\one.bat -b 22 .\new_settings\helsinki_settings.txt .\new_settings\varying_buffer_size.txt 

.\one.bat -b 24 .\new_settings\barbados_settings.txt .\new_settings\varying_node_and_messages.txt 
.\one.bat -b 24 .\new_settings\helsinki_settings.txt .\new_settings\varying_node_and_messages.txt 