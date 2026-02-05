
    cmake_minimum_required(VERSION 3.22.1)

    project("weatherapp")

    add_library(
            native-lib       # The name of our library
            SHARED           # A shared library (.so file)
            native-lib.cpp)  # The source file

    # Add your API key here as a secure compile-time definition.
    # The key will be injected into the C++ code during compilation.
    target_compile_definitions(native-lib PRIVATE WEATHER_API_KEY="9d59beb66e28647236f0dddc508e3669")



    # Link against the Android logging library (optional but good practice)
    find_library(log-lib log)
    target_link_libraries(native-lib ${log-lib})