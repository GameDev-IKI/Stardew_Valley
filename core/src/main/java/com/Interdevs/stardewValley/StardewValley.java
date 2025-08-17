package com.Interdevs.stardewValley;

import com.Interdevs.stardewValley.screens.CharacterCreationScreen;
import com.Interdevs.stardewValley.screens.FarmScreen;
import com.Interdevs.stardewValley.screens.InventoryScreen;
import com.Interdevs.stardewValley.screens.StartScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;

/* Main Game Class for Stardew Valley*/
public class StardewValley extends Game {

    private boolean changingScreen = false;
    private Screen pendingScreen = null;
    private StartScreen startscreen;
    private CharacterCreationScreen characterCreationScreen;
    private Music startScreenMusic;
    private Music characterCreationMusic;
    private InventoryScreen inventoryScreen;
    private Player player;
    private PlayerData playerData;
    private FarmScreen farmScreen = new FarmScreen(this);
    @Override
    public void create() {
        startScreenMusic = Gdx.audio.newMusic(Gdx.files.internal("StartScreen/Menu_Theme.mp3"));
        startScreenMusic.setLooping(true);
        startScreenMusic.setVolume(0.5f);

        characterCreationMusic = Gdx.audio.newMusic(Gdx.files.internal("CharacterCreationScreen/CharacterCreation_Music.mp3"));
        characterCreationMusic.setLooping(true);
        characterCreationMusic.setVolume(0.5f);

        this.playerData = PlayerData.loadFromFile();
        if (playerData == null || playerData.getName() == null) {
            playerData = new PlayerData(); // New player
            startscreen = new StartScreen(this);
            characterCreationScreen = new CharacterCreationScreen(this);
            setScreen(startscreen); // Let the player create their character
        } else {
            this.player = new Player(playerData.getName(), playerData, this);
            this.inventoryScreen = new InventoryScreen(this, player, playerData, farmScreen);
            startscreen = new StartScreen(this);
            characterCreationScreen = new CharacterCreationScreen(this);
            setScreen(startscreen); // Skip to game if data is valid
        }

    }
    @Override
    public void setScreen(Screen screen) {
        try {
            if (screen instanceof StartScreen) {
                characterCreationMusic.stop();
                startScreenMusic.play();
            } else if (screen instanceof CharacterCreationScreen) {
                startScreenMusic.stop();
                characterCreationMusic.play();
            }

            if (changingScreen) {
                pendingScreen = screen;
                return;
            }
            if (this.screen != null) {
                this.screen.hide();
                this.screen.dispose();
            }

            // Set new screen
            this.screen = screen;
            if (this.screen != null) {
                this.screen.show();
                this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        } catch (Exception e) {
            Gdx.app.error("SCREEN_TRANSITION", "Error during screen transition", e);
            throw e;
        }
        /*

        changingScreen = true;

        // 1. Clear all input states safely
        try {
            Gdx.input.setInputProcessor(null);
            if (Gdx.input.getInputProcessor() != null) {
                Gdx.input.getInputProcessor().keyUp(Input.Keys.ANY_KEY);
                Gdx.input.getInputProcessor().touchUp(0, 0, 0, 0);
            }
        } catch (Exception e) {
            Gdx.app.error("INPUT", "Error clearing input", e);
        }

        // 2. Handle music transitions
        if (screen instanceof StartScreen) {
            characterCreationMusic.stop();
            startScreenMusic.play();
        } else if (screen instanceof CharacterCreationScreen) {
            startScreenMusic.stop();
            characterCreationMusic.play();
        }

        // 3. Dispose current screen if exists
        if (this.screen != null) {
            this.screen.hide();
            this.screen.dispose();
        }

        // 4. Set new screen (don't call super.setScreen() - we're completely overriding it)
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        changingScreen = false;

        // 5. Process any pending screen change
        if (pendingScreen != null) {
            Screen next = pendingScreen;
            pendingScreen = null;
            setScreen(next);
        }
         */
    }


    public StartScreen getStartScreen() {
        return startscreen;
    }

    public Music getStartScreenMusic() {
        return startScreenMusic;
    }

    public Music getCharacterCreationMusic() {
        return characterCreationMusic;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public InventoryScreen getInventoryScreen() {
        return inventoryScreen;
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
