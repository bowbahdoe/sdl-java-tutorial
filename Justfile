help:
    just --list

# Clone and Build SDL
sdl:
    rm -rf SDL
    git clone https://github.com/libsdl-org/SDL
    cd SDL && mkdir build
    cd SDL/build && cmake -DCMAKE_BUILD_TYPE=Release ..
    cd SDL/build && cmake --build . --config Release --parallel
    cd SDL/build && sudo cmake --install . --config Release

# Dump all the symbols from SDL.h
dump_includes:
    jextract \
      --include-dir SDL/include \
      --dump-includes includes.txt \
      SDL/include/SDL3/SDL.h

# Generate SDL Binding Code
generate_sdl_bindings:
    rm -rf src/bindings
    jextract \
      --include-dir SDL/include \
      --output src \
      --target-package bindings.sdl \
      --library SDL3 \
      --use-system-load-library \
      @includes.txt \
      SDL/include/SDL3/SDL.h

# Run the Demo Program
run:
    java \
        -XstartOnFirstThread \
        --enable-native-access=ALL-UNNAMED \
        -Djava.library.path=SDL/build \
        src/Main.java