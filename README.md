# Counting Up Game
A Java-based counting up game application built using JGameGrid framework.

<img width="556" alt="image" src="https://github.com/user-attachments/assets/d2e963f9-a729-45a5-b6c1-4cbc09277811" />


## Game Instructions

### Overview
The Counting Up (CU) game is played with a standard 52-card deck among four players.

### Setup
- **Cards**: Standard 52-card deck with four suits (A → 2 → ... → 9 → 10 → J → Q → K)
- **Players**: 4 players playing independently
- **Deal**: 13 cards distributed face down to each player at random

### Scoring
- **Number cards**: Worth face value (2 = 2 points, 3 = 3 points, etc.)
- **Picture cards**: A, J, Q, K each worth 10 points

### Gameplay
1. **Starting**: Player with ace of clubs starts the first round
2. **Turn order**: Clockwise rotation
3. **Valid plays**: Put down a card that is either:
   - Same suit with higher rank, OR
   - Same rank with different suit
4. **Passing**: Players must pass if they cannot play a valid card (or choose to pass)
5. **Round end**: When no players can play, the last player to put down a card receives points equal to the sum of all cards played that round
6. **New round**: Starts with the player who won the previous round, who may play any card
7. **Game end**: When one player has no cards remaining

### Winning
- **Remaining cards**: Worth negative points
- **Winner**: Player with the highest total points

## Prerequisites
- Java 18 or higher

## Running the Game

### Basic Execution
Run the game with default settings (game5.properties):

```bash
java -jar counting-up-game.jar
```

### Custom Execution
Run the game with custom settings:

```bash
java -jar counting-up-game.jar properties/game1.properties
```

### Available Configurations
The game includes several built-in configurations:

- `properties/game1.properties` - automatic game with 3 random players and fixed initial cards
- `properties/game2.properties` - automatic game with 3 random players and fixed initial cards
- `properties/game3.properties` - automatic game with 3 random players and fixed initial cards
- `properties/game4.properties` - basic game with 3 random players and random initial cards
- `properties/game5.properties` - advanced game with 1 random player, 1 basic player and 1 clever player with random initial cards

## Development
### Project Structure

```
app/
├── src/main/java          # Java source code
├── src/main/resources     # Resources (properties, images, etc.)
│   ├── properties/         # Game configuration files
│   └── sprites/           # Game images
├── lib/                   # External JAR dependencies
└── build.gradle          # Build configuration
```

### Building for Development
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```
## Notes

The JAR file created includes all dependencies, so it can be run on any system with Java 18+ installed without needing to install additional libraries.
