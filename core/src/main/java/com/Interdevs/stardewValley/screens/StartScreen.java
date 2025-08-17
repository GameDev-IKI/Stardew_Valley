package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.StardewValley;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StartScreen implements Screen {

    private final StardewValley game;
    private Stage stage;
    private AssetManager assetManager;

    private final String BACKGROUND_PATH = "StartScreen/Stardew_Background.png";
    private final String LOGO_PATH = "StartScreen/logo.png";
    private final String BUTTON_ATLAS_PATH = "StartScreen/textureButtons.atlas";

    private final String NEW_BUTTON_BASE = "new_button";
    private final String LOAD_BUTTON_BASE = "load_button";
    private final String EXIT_BUTTON_BASE = "exit_button";

    private float buttonScale = 1.0f;
    private float hoverScale = 1.2f;
    private ImageButton hoveredButton = null;

    private Music backgroundMusic;
    private float musicTargetVolume = 0.5f; // Final volume
    private float musicFadeSpeed = 0.3f;

    public StartScreen(StardewValley game) {
        this.game = game;
        this.assetManager = new AssetManager();
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        assetManager.load(BACKGROUND_PATH, Texture.class);
        assetManager.load(LOGO_PATH, Texture.class);
        assetManager.load(BUTTON_ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();

        buildUI();
    }

    private void buildUI() {
        stage.clear();

        Texture backgroundTexture = assetManager.get(BACKGROUND_PATH, Texture.class);
        Texture logoTexture = assetManager.get(LOGO_PATH, Texture.class);
        TextureAtlas buttonAtlas = assetManager.get(BUTTON_ATLAS_PATH, TextureAtlas.class);

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(50); // Align buttons to the bottom

        Image logoImage = new Image(logoTexture);
        Table logoTable = new Table();
        logoTable.setFillParent(true);
        logoTable.top().padTop(100); // Align logo to the top
        logoTable.add(logoImage).padBottom(80).width(1200).height(600); // Set size

        stage.addActor(logoTable);


        ImageButton newGameButton = createButton(buttonAtlas, NEW_BUTTON_BASE, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("New Game button clicked.");
                game.setScreen(new CharacterCreationScreen(game));
                System.out.println("Switching to Character Creation Screen (Not Implemented Yet)");
            }
        });


        ImageButton exitButton = createButton(buttonAtlas, EXIT_BUTTON_BASE, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Exit button clicked.");
                Gdx.app.exit();
            }
        });

        if (newGameButton != null) {
            table.add(newGameButton).padRight(20);
            newGameButton.setScale(1.2f);
        }

        if (exitButton != null) {
            table.add(exitButton);
            exitButton.setScale(1.2f);
        }

        stage.addActor(table);
    }

    private ImageButton createButton(TextureAtlas atlas, String baseRegionName, ClickListener clickListener) {
        String upRegionName = baseRegionName + "_up";
        String downRegionName = baseRegionName + "_down";

        TextureAtlas.AtlasRegion upRegion = atlas.findRegion(upRegionName);
        TextureAtlas.AtlasRegion downRegion = atlas.findRegion(downRegionName);

        if (upRegion == null) {
            System.err.println("Mandatory UP Region '" + upRegionName + "' not found in atlas: " + BUTTON_ATLAS_PATH);
            return null;
        }

        TextureRegionDrawable upDrawable = new TextureRegionDrawable(upRegion);
        TextureRegionDrawable downDrawable = (downRegion != null)
            ? new TextureRegionDrawable(downRegion)
            : upDrawable;

        ImageButton button = new ImageButton(upDrawable, downDrawable);
        button.addListener(clickListener);

        //
        button.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setScale(1.1f);
                button.setChecked(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setScale(1.0f);
                button.setChecked(false);
            }
        });

        return button;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);

        for (Actor actor : stage.getActors()) {
            if (actor instanceof ImageButton) {
                ImageButton button = (ImageButton) actor;
                if (button == hoveredButton) {
                    button.setScale(hoverScale);
                    button.setChecked(true);
                } else {
                    button.setScale(buttonScale);
                    button.setChecked(false);
                }
            }
        }

        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Optional: Implement pause functionality
    }

    @Override
    public void resume() {
        // Optional: Implement resume functionality
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        stage.dispose();
    }
}
