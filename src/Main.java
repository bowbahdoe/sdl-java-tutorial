import bindings.sdl.SDL_Event;
import bindings.sdl.SDL_FPoint;
import bindings.sdl.SDL_FRect;

import java.lang.foreign.Arena;

import static bindings.sdl.SDL_h.*;

public class Main {
    public static void main(String[] args) {
        try (var arena = Arena.ofConfined()) {
            SDL_SetAppMetadata(
                    arena.allocateFrom("Example Renderer Primitives"),
                    arena.allocateFrom("1.0"),
                    arena.allocateFrom("com.example.renderer-primitives")
            );

            if (!SDL_Init(SDL_INIT_VIDEO())) {
                System.err.println(
                        "Couldn't initialize SDL: "
                                + SDL_GetError().getString(0));
                return;
            }


            var windowPtr = arena.allocate(C_POINTER);
            var rendererPtr = arena.allocate(C_POINTER);
            if (!SDL_CreateWindowAndRenderer(
                    arena.allocateFrom("examples/renderer/clear"),
                    640,
                    480,
                    0,
                    windowPtr,
                    rendererPtr
            )) {
                System.err.println(
                        "Couldn't create window/renderer: "
                                + SDL_GetError().getString(0));
                return;
            }

            var window = windowPtr.get(C_POINTER, 0);
            var renderer = rendererPtr.get(C_POINTER, 0);
            try {

                int numberOfPoints = 500;
                var points = SDL_FPoint.allocateArray(numberOfPoints, arena);
                for (int i = 0; i < numberOfPoints; i++) {
                    var point = SDL_FPoint.asSlice(points, i);
                    SDL_FPoint.x(
                            point,
                            (SDL_randf() * 440.0f) + 100.0f
                    );
                    SDL_FPoint.y(
                            point,
                            (SDL_randf() * 280.0f) + 100.0f
                    );
                }

                var event = SDL_Event.allocate(arena);
                var rect = SDL_FRect.allocate(arena);

                program:
                while (true) {
                    while (SDL_PollEvent(event)) {
                        var type = SDL_Event.type(event);
                        if (type == SDL_EVENT_QUIT()) {
                            System.err.println("Quitting");
                            break program;
                        }
                    }

                    /* as you can see from this, rendering draws over whatever was drawn before it. */
                    SDL_SetRenderDrawColor(
                            renderer,
                            (byte) 33, (byte) 33, (byte) 33, (byte) SDL_ALPHA_OPAQUE()
                    );  /* dark gray, full alpha */
                    SDL_RenderClear(renderer);  /* start with a blank canvas. */

                    /* draw a filled rectangle in the middle of the canvas. */
                    SDL_SetRenderDrawColor(
                            renderer,
                            (byte) 0, (byte) 0, (byte) 255, (byte) SDL_ALPHA_OPAQUE()
                    );  /* blue, full alpha */
                    SDL_FRect.x(rect, 100);
                    SDL_FRect.y(rect, 100);
                    SDL_FRect.w(rect, 440);
                    SDL_FRect.h(rect, 280);

                    SDL_RenderFillRect(renderer, rect);

                    /* draw some points across the canvas. */
                    SDL_SetRenderDrawColor(
                            renderer,
                            (byte) 255, (byte) 0, (byte) 0, (byte) SDL_ALPHA_OPAQUE()
                    );  /* red, full alpha */
                    SDL_RenderPoints(renderer, points, numberOfPoints);

                    /* draw a unfilled rectangle in-set a little bit. */
                    SDL_SetRenderDrawColor(
                            renderer,
                            (byte) 0, (byte) 255, (byte) 0, (byte) SDL_ALPHA_OPAQUE()
                    );  /* green, full alpha */
                    SDL_FRect.x(
                            rect,
                            SDL_FRect.x(rect) + 30
                    );
                    SDL_FRect.y(
                            rect,
                            SDL_FRect.y(rect) + 30
                    );
                    SDL_FRect.w(
                            rect,
                            SDL_FRect.w(rect) - 60
                    );
                    SDL_FRect.h(
                            rect,
                            SDL_FRect.h(rect) - 60
                    );
                    SDL_RenderRect(renderer, rect);

                    /* draw two lines in an X across the whole canvas. */
                    SDL_SetRenderDrawColor(
                            renderer,
                            (byte) 255, (byte) 255, (byte) 0, (byte) SDL_ALPHA_OPAQUE()
                    );  /* yellow, full alpha */
                    SDL_RenderLine(renderer, 0, 0, 640, 480);
                    SDL_RenderLine(renderer, 0, 480, 640, 0);

                    SDL_RenderPresent(renderer);  /* put it all on the screen! */
                }

            } finally {
                SDL_DestroyRenderer(renderer);
                SDL_DestroyWindow(window);
                SDL_Quit();
            }
        }
    }
}