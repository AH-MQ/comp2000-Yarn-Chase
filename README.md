# Yarn Chase Game - Assesment 2 Alleluya Hamisi 48455032

## How to Compile and Run

This program requires Java 11 or Java 21.

**To compile:**
```bash
javac *.java
```

**To run:**
```bash
java Main
```

The game requires an active internet connection to receive weather data from http://13.238.167.130/weather. If the server is unavailable, the game will still run but weather effects won't appear.

The stage configuration is loaded from `data/stage1.rvb`, which defines starting positions and types of actors. The format is: `ColumnRow=ActorType` (e.g., `J4=cat`). Adding `bot` at the end makes it AI-controlled (e.g., `S15=dog bot`).

## How to Play

The game starts with a main menu where you can choose to start playing, read the how-to-play instructions, or exit. Once you start the game, you control one of three animals (cat, dog, or bird) while AI bots control the others. The objective is simple: be the first to collect 20 yarn balls.

To play, click on your animal to select it. Available movement cells will be highlighted on the grid. Click on any highlighted cell to move there. Each animal has different movement ranges - cats can move 2 spaces, dogs can move 1 space, and birds can move 3 spaces. This creates interesting strategic choices about which animal to use in different situations.

The purple yarn balls are scattered around the 20x20 grid. Move onto a cell containing a yarn ball to collect it. The game tracks your personal yarn count in the progress bar at the top of the sidebar, showing how close you are to the winning goal of 20 yarn balls. AI bots will actively hunt for yarn balls too, so you need to move strategically to reach them first.

Weather effects appear on the grid as colored overlays. Rain shows as blue, heat shows as orange, and wind shows as light gray/white. These effects modify your movement in different ways when you step on affected cells. The sidebar displays information about what weather effect is currently at your mouse position, along with the game state and each player's individual yarn count.

The game ends when any player (human or AI) collects 20 yarn balls. A win screen displays the winner and provides an option to return to the main menu.

## Features Added to Base Code

I extended the basic grid from our week 11 classwork by adding several major features that demonstrate the required design patterns, lambdas, and streams. The most significant addition is the real-time weather system that connects to an HTTP server and streams weather data continuously. This weather data affects gameplay by modifying how players move around the grid.

I also implemented a collectible system with purple yarn balls that spawn randomly on the grid. Players compete to collect 20 yarn balls first, which creates an actual win condition for the game. The AI bots use intelligent pathfinding to chase yarn balls rather than moving randomly, making them challenging opponents.

The game now has a proper menu system with a start screen, how-to-play instructions, and a win screen that displays when someone reaches 20 yarn balls. I added visual improvements like distinct color schemes for the grid, weather effect overlays, and a progress bar showing how close the player is to winning.

Overall, I transformed the basic grid demonstration into a complete turn-based strategy game with competitive AI, real-time environmental effects, and clear victory conditions. The implementation showcases how design patterns create flexible, maintainable code while lambdas and streams enable elegant data processing.

## Design Patterns

### Decorator Pattern

The Decorator pattern is one of the core design patterns I implemented for this assignment. I used it to add weather visualization and effects to grid cells without modifying the original Cell class from the week 11 code. This demonstrates one of the key benefits of design patterns - extending functionality while maintaining the open/closed principle.

The `CellDecorator` class wraps a Cell object and adds weather-related functionality on top of it. Each decorator tracks three weather attributes (rain level, wind level, temperature level) and determines which effect is dominant based on threshold values. The decorator can then render colored overlays and apply movement modifications.

Here's how the pattern works in my implementation: when the game starts, cells don't have decorators yet. As weather data arrives from the HTTP server, the Stage class creates CellDecorator instances for the affected grid positions using `computeIfAbsent()`. These decorators wrap the original Cell objects and start tracking weather intensity. The decorators implement their own paint logic by first calling the wrapped cell's paint method (delegating the base rendering), then drawing the weather overlay on top.

The critical design insight here is separation of concerns. The Cell class handles basic grid rendering and coordinate management. The CellDecorator handles weather visualization and effects. Neither class needs to know about the other's implementation details - they just need to follow the same painting interface. This means I could add more decorators (like terrain effects or power-ups) without touching either the Cell or CellDecorator code.

Another major advantage is runtime flexibility. Decorators are added and removed dynamically as weather data flows in and decays. When weather data stops arriving for a particular cell, the weather levels decay over time (multiplied by 0.995 each frame at 50 FPS). This creates natural-looking fade effects where weather patterns gradually disappear rather than vanishing instantly. With traditional inheritance, I'd need to swap between different Cell subclasses, which would be messy and inefficient.

The Decorator pattern also demonstrates composition over inheritance. Instead of creating WeatherCell, RainCell, HeatCell subclasses (which would explode into a combinatorial nightmare if we wanted multiple effects), we compose behavior at runtime. This is exactly the kind of flexible design that patterns enable.

From a software engineering perspective, this pattern made my code more testable too. I can test Cell rendering independently from weather effects, and I can test weather logic independently from grid rendering. The loose coupling between components is a major win for maintainability.

### Strategy Pattern

I used the Strategy pattern in two different contexts, both demonstrating how it enables runtime behavior selection and reduces code complexity.

The first use is for AI movement strategies. The `MoveStrategy` interface defines a contract for choosing the next location an actor should move to. I implemented three concrete strategies:

- `MoveRandomly`: Picks a random cell from available options using Java's Random class
- `MoveLeft`: Always chooses the left-most available cell by comparing column values
- `MoveToYarn`: Calculates Manhattan distances to all yarn balls and moves toward the nearest one

The Actor class has a `mover` field that holds a MoveStrategy reference. During gameplay, bots can switch strategies based on game conditions. The original week 11 code set the mover based on row parity, but I extended this by creating the `MoveToYarn` strategy specifically for the competitive yarn collection game. The BotMoving state explicitly uses MoveToYarn for all bots, which makes them actively hunt for yarn rather than wandering aimlessly.

The design insight here is that Strategy pattern eliminates conditional logic. Without this pattern, the Actor class would need a bunch of if-statements or switch cases checking "what type of AI am I?" and executing different movement code. That approach violates the open/closed principle - adding a new AI behavior requires modifying the Actor class. With Strategy, I just create a new class implementing MoveStrategy and plug it in. The Actor doesn't need to change at all.

This also demonstrates polymorphism properly. The BotMoving state doesn't know or care which specific strategy it's calling - it just invokes `mover.chooseNextLoc()` and gets back a Cell. The behavior varies based on which strategy object is present, but the calling code is identical. This is cleaner and more maintainable than conditional logic scattered throughout the codebase.

The second use of Strategy is for weather effects. The `WeatherEffect` interface defines how different weather conditions modify player movement. I implemented four concrete strategies:

- `NoWeatherEffect`: Returns the current location unchanged (clear weather, normal movement)
- `RainEffect`: Slides the player 2 cells left or right randomly (slippery surface simulation)
- `HeatEffect`: Returns the current location (player can't move due to overheating)
- `WindEffect`: Teleports the player to a completely random grid position (blown away by wind)

Each CellDecorator maintains a reference to a WeatherEffect based on the dominant weather condition at that cell. When a player moves onto a cell, the Stage calls `applyEffect()` on the current weather effect, which returns a modified destination cell. The player then gets moved to this new location, creating interesting and unpredictable movement mechanics.

The key design advantage here is extensibility. If I wanted to add new weather types (like fog that limits vision, or lightning that damages players), I'd just create new classes implementing WeatherEffect. The Stage class doesn't need modification because it already has the infrastructure to apply whatever effect the decorator provides. This demonstrates how patterns create extension points in your architecture.

Another benefit is that weather effects are completely self-contained. Each effect class encapsulates its own logic for modifying movement. There's no central "weather manager" with a giant switch statement. This makes the code easier to understand because related logic is grouped together, and it makes testing easier because each effect can be tested in isolation.

The Strategy pattern also makes the code more declarative. When I read `effect.applyEffect(currentLocation, grid, actors)`, I immediately understand what's happening - some effect is being applied to the current location. I don't need to trace through conditional logic to figure out which effect or how it works. The interface provides a clear contract that all strategies must follow.

### Observer Pattern

The Observer pattern handles the communication between the weather data stream and the game. This pattern was essential because weather data arrives asynchronously from an HTTP server, but multiple parts of the game need to react to it immediately. The pattern solves the problem of how to propagate events to multiple listeners without tight coupling.

I created a `WeatherObserver` interface with a single method: `onWeatherUpdate(WeatherData data)`. The `WeatherStreamClient` class maintains a list of observers and notifies them whenever new weather data arrives. The `Stage` class implements WeatherObserver, so it gets notified of every weather update.

Here's how the data flow works in detail: When the game starts, Stage creates a WeatherStreamClient and registers itself as an observer by calling `addObserver(this)`. The weather client then opens an HTTP connection to the server and starts reading lines asynchronously using CompletableFuture. This happens on a separate thread to avoid blocking the game's render loop. Each line gets parsed into a WeatherData object, and for each valid data point, the client calls `notifyObservers()`. This method loops through all registered observers and calls their `onWeatherUpdate()` method, passing the weather data.

When Stage receives a weather update through `onWeatherUpdate()`, it checks if the coordinates fall within the grid boundaries using `data.isInGrid()`. If they do, it creates or retrieves a CellDecorator for that position (using `computeIfAbsent()` for thread safety) and calls `updateWeather()` with the new data. The decorator then updates its internal weather levels and recalculates which effect is dominant based on threshold comparisons.

The critical design insight here is decoupling. The WeatherStreamClient knows nothing about the game - it doesn't import any game classes, it doesn't know what a Stage or Cell is. It just knows about observers and weather data. Similarly, the Stage doesn't know anything about HTTP connections, parsing, or networking. It just responds to weather updates. This separation of concerns is a fundamental principle of good software design.

This decoupling provides several concrete benefits. First, I can test the networking code independently from the game logic. I can create a mock observer that just logs weather data, and verify that the client is parsing and notifying correctly. Second, I can test the game logic by creating a mock weather source that sends predetermined data, without needing an actual HTTP server. This makes unit testing much more practical.

Another major advantage is extensibility through the one-to-many relationship that Observer naturally handles. If I wanted to add a weather statistics panel showing temperature trends over time, I'd just create a new class implementing WeatherObserver and register it with the client. The existing code wouldn't need any changes at all. The client would automatically notify both the Stage and the statistics panel. This is much cleaner than having the client directly call methods on specific game objects, which would create tight coupling.

The Observer pattern also handles threading cleanly. The HTTP reading happens on a background thread, but the observers get notified on that same thread. Since my ConcurrentHashMap handles thread safety, the weather updates can safely modify game state even though they're coming from a different thread than the render loop. Without this pattern, managing the threading would be much more complex.

From an architectural perspective, Observer creates a publish-subscribe system. The weather client publishes events, and various parts of the game subscribe to those events. This is a common pattern in event-driven systems and shows up everywhere from GUI frameworks to message queues. Understanding this pattern is essential for building scalable applications.

## Lambdas and Streams

I used Java's Streams API and lambda expressions extensively throughout the code, particularly for processing collections and handling the weather data feed. The combination of streams and lambdas demonstrates functional programming concepts within Java's object-oriented framework, and shows how declarative code can be more readable and maintainable than imperative loops.

### Weather Data Processing Pipeline

The most significant use of streams is in `WeatherStreamClient.startStreaming()`. Instead of using traditional loops to process the incoming data, I created a pipeline of stream operations that transforms raw HTTP data into game events:
```java

This assignment demonstrates proficiency in Java through implementing three design patterns (Decorator, Strategy, Observer), extensive use of lambdas and streams for collection processing and data pipelines, and integration with an HTTP server for real-time data streaming.

The Decorator pattern adds weather functionality without modifying existing classes. The Strategy pattern makes AI behaviors and weather effects pluggable. The Observer pattern decouples network communication from game logic. Streams and lambdas simplify collection operations throughout the codebase.

The result is a complete turn-based strategy game where players compete against intelligent AI to collect yarn balls while dealing with dynamic weather conditions. The modular architecture makes it straightforward to extend with new features - adding new weather types, AI strategies, or game mechanics would require minimal changes to existing code.
```