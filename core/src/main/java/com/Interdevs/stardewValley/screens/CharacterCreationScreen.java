package com.Interdevs.stardewValley.screens;

import com.Interdevs.stardewValley.Player;
import com.Interdevs.stardewValley.PlayerData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.Interdevs.stardewValley.StardewValley;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;


public class CharacterCreationScreen implements Screen {



    private final StardewValley game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Texture boxTexture;
    private Texture maleTexture;
    private Texture femaleTexture;
    private Texture shirtsTexture;
    private Texture pantsTexture;
    private Texture hairstylesTexture;
    private Texture hatsTexture;
    private TextureAtlas buttonsAtlas;
    private SpriteBatch batch;
    private PlayerData playerData;


    private Image previewFarmer;
    private Image textBg1;
    private Image textBg2;
    private Image textBg3;


    private Group farmerGroup;
    private Image baseImage;
    private Image shirtImage;
    private Image pantsImage;
    private Image hairImage;
    private Image bgImage;
    private boolean isMale = true;

    private int currentShirt = 0;
    private int currentPants = 0;
    private int currentHair = 0;

    private Image maleIcon;
    private Image femaleIcon;

    private TextField nameField;
    private TextField farmNameField;
    private TextField favThingField;

    public CharacterCreationScreen(final StardewValley game) {
        this.game = game;
        this.batch = new SpriteBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Better VCR 9.0.1.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20; // font size in pixels
        parameter.color = Color.WHITE;

        BitmapFont font = generator.generateFont(parameter); // creates a font
        generator.dispose(); // don't forget to dispose it

        skin.add("default", font);

        // Load assets
        backgroundTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/Stardew_Background.png"));
        boxTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/Character_Creation_Box.png"));
        maleTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/farmer_base.png"));
        femaleTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/farmer_girl_base.png"));
        shirtsTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/shirts.png"));
        pantsTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/1_pant.png"));
        hairstylesTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/hairstyles.png"));
        hatsTexture = new Texture(Gdx.files.internal("CharacterCreationScreen/hats.png"));
        buttonsAtlas = new TextureAtlas(Gdx.files.internal("CharacterCreationScreen/CharacterCreation_Buttons.atlas"));

        batch = new SpriteBatch();

        // Background
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        // Box (centered manually)
        Image box = new Image(boxTexture);
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float boxWidth = 700;
        float boxHeight = 650;
        box.setSize(boxWidth, boxHeight);
        box.setPosition((screenWidth - boxWidth) / 2, (screenHeight - boxHeight) / 2);
        stage.addActor(box);

        createFormInputs();
        createFarmerPreview();
        createGenderSelection();
        createControlButtons();
    }

    private void createFormInputs() {

        float boxX = (Gdx.graphics.getWidth() - 700) / 2;
        float boxY = (Gdx.graphics.getHeight() - 650) / 2;

        float labelX = (boxX + 140)-10;
        float fieldX = boxX + 250;
        float startY = boxY + 500;
        float gapY = 70;

        Texture textBackground = new Texture(Gdx.files.internal("CharacterCreationScreen/textBox.png"));
        TextureRegionDrawable textBg1Region = new TextureRegionDrawable(textBackground);
        textBg1 = new Image(textBg1Region);
        textBg1.setSize(325,48);
        textBg1.setPosition(boxX+230,boxY+500);
        stage.addActor(textBg1);

        TextureRegionDrawable textBg2Region = new TextureRegionDrawable(textBackground);
        textBg2 = new Image(textBg2Region);
        textBg2.setSize(325,48);
        textBg2.setPosition(boxX+230,boxY+430);
        stage.addActor(textBg2);

        TextureRegionDrawable textBg3Region = new TextureRegionDrawable(textBackground);
        textBg3 = new Image(textBg3Region);
        textBg3.setSize(325,48);
        textBg3.setPosition(boxX+230,boxY+360);
        stage.addActor(textBg3);


        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.BROWN;

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.BROWN;



        // Name field
        Label nameLabel = new Label("Name:", labelStyle);
        nameLabel.setPosition(labelX, startY+10);
        stage.addActor(nameLabel);


        nameField = new TextField("", textFieldStyle);
        nameField.setSize(300, 50);
        nameField.setPosition(fieldX, startY);
        stage.addActor(nameField);

        // Farm Name field
        Label farmLabel = new Label("Farm\nName:", labelStyle);
        farmLabel.setPosition(labelX, startY - gapY);
        stage.addActor(farmLabel);

        farmNameField = new TextField("", textFieldStyle);
        farmNameField.setSize(300, 50);
        farmNameField.setPosition(fieldX, startY - gapY);
        stage.addActor(farmNameField);

        // Favorite Thing field
        Label favLabel = new Label("Favorite\nThing:", labelStyle);
        favLabel.setPosition(labelX, startY - (gapY * 2));
        favLabel.setFontScale(0.9f);
        stage.addActor(favLabel);

        favThingField = new TextField("", textFieldStyle);
        favThingField.setSize(300, 50);
        favThingField.setPosition(fieldX, startY - (gapY * 2));
        stage.addActor(favThingField);
    }

    private void createFarmerPreview() {
        float boxX = (Gdx.graphics.getWidth() - 700) / 2;
        float boxY = (Gdx.graphics.getHeight() - 650) / 2;
        float previewX = boxX + 180;
        float previewY = boxY + 150;

        int selectedShirtIndex = 0; // Shirt #2
        int selectedPantsIndex = 0; // Pants #1
        int selectedHairIndex = 4;  // Hair #3

        int shirtWidth = 8, shirtHeight = 8;
        int pantsWidth = 8, pantsHeight = 5; // choose correct pants height if multiple
        int hairWidth = 13, hairHeight = 11;


        farmerGroup = new Group();
        farmerGroup.setPosition(previewX, previewY);
        farmerGroup.setScale(4f);

        //bg
        Texture backGround = new Texture(Gdx.files.internal("CharacterCreationScreen/daybg.png"));
        TextureRegion bgRegion = new TextureRegion(backGround);
        bgImage = new Image(bgRegion);
        bgImage.setSize(110, 170);
        bgImage.setPosition(boxX+157,boxY+130);
        stage.addActor(bgImage);

        // Add invisible dummy
        Image dummy = new Image(new TextureRegionDrawable(new TextureRegion(maleTexture, 0, 0, 1, 1)));
        dummy.setSize(14f, 27f);
        dummy.setColor(1, 1, 1, 0);
        farmerGroup.addActor(dummy);

        // Base character
        TextureRegion baseRegion = new TextureRegion(maleTexture, 0, 0, 16, 33);
        baseImage = new Image(new TextureRegionDrawable(baseRegion));
        baseImage.setPosition(0, 0); // center base inside 14px
        farmerGroup.addActor(baseImage);

        // Shirt
        TextureRegion shirtRegion = new TextureRegion(shirtsTexture, currentShirt * 8, 0, 8, 8);
        shirtImage = new Image(new TextureRegionDrawable(shirtRegion));
        shirtImage.setPosition(4, 10);
        farmerGroup.addActor(shirtImage);

        // Pants
        Texture pantsTextureSingle = new Texture(Gdx.files.internal("CharacterCreationScreen/1_pant.png"));
        TextureRegion pantsRegion = new TextureRegion(pantsTextureSingle);
        pantsImage = new Image(pantsRegion);
        pantsImage.setPosition(4, 6);
        farmerGroup.addActor(pantsImage);

        // Hair
        TextureRegion hairRegion = new TextureRegion(hairstylesTexture, currentHair * 14, 0, 14, 9);
        hairImage = new Image(new TextureRegionDrawable(hairRegion));
        hairImage.setPosition(1, 22);
        farmerGroup.addActor(hairImage);

        stage.addActor(farmerGroup);
    }



    private void createGenderSelection() {
        float boxX = (Gdx.graphics.getWidth() - 700) / 2;
        float boxY = (Gdx.graphics.getHeight() - 650) / 2;
        float genderY = boxY + 80;

        maleIcon = new Image(buttonsAtlas.findRegion("male"));
        maleIcon.setSize(39,39);
        maleIcon.setPosition(boxX + 300, genderY+150);
        maleIcon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMale = true;
                updateFarmerPreview();
            }
        });
        stage.addActor(maleIcon);

        femaleIcon = new Image(buttonsAtlas.findRegion("female"));
        femaleIcon.setSize(30,48);
        femaleIcon.setPosition(boxX + 300, genderY+ 50);
        femaleIcon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMale = false;
                updateFarmerPreview();
            }
        });
        stage.addActor(femaleIcon);
    }

    private void createControlButtons() {
        float boxX = (Gdx.graphics.getWidth() - 700) / 2;
        float boxY = (Gdx.graphics.getHeight() - 650) / 2;
        Image backBtn = new Image(buttonsAtlas.findRegion("Back_front"));
        backBtn.setPosition(Gdx.graphics.getWidth() - 300, 50);
        backBtn.setSize(197, 80);
        backBtn.setScale(1.5f);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    if (game.getStartScreen() != null) {
                        game.setScreen(game.getStartScreen());
                    } else {
                        Gdx.app.log("CharacterCreationScreen", "StartScreen is not initialized!");
                    }
                } catch (Exception e) {
                    game.setScreen(game.getStartScreen());
                    Gdx.app.error("CharacterCreationScreen", "Exception while handling Back button: " + e.getMessage(), e);
                }
            }
        });
        stage.addActor(backBtn);

        Image okBtn = new Image(buttonsAtlas.findRegion("ok"));
        okBtn.setPosition(boxX+600 , boxY+ 30);
        okBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText();
                String farmName = farmNameField.getText();
                String favoriteThing = favThingField.getText();
                String gender = isMale ? "male" : "female";

                playerData = new PlayerData(name, farmName, favoriteThing, gender);
                playerData.saveToFile();
                playerData.loadFromFile();

                Gdx.app.log("Player Data", "Name: " + playerData.getName());
                Gdx.app.log("Player Data", "Farm Name: " + playerData.getFarmName());
                Gdx.app.log("Player Data", "Favorite Thing: " + playerData.getFavoriteThing());
                Gdx.app.log("Player Data", "Gender: " + playerData.getGender());
                // Now you can pass playerData to your game
                // For example, go to the next screen:
                Player player = new Player(playerData); // assuming your Player class has a constructor that accepts PlayerData
                game.setScreen(new BusStopScreen(game, "FromCharacter", player));


            }
        });
        stage.addActor(okBtn);
    }

    private void updateFarmerPreview() {
        Texture currentTexture = isMale ? maleTexture : femaleTexture;
        TextureRegion baseRegion = new TextureRegion(currentTexture, 0, 0, 16, 33);
        TextureRegion shirtRegion = new TextureRegion(shirtsTexture, currentShirt * 8, 0, 8, 8);
        TextureRegion pantsRegion = new TextureRegion(pantsTexture, currentPants * 8, 0, 8, 5); // adjust size if needed
        TextureRegion hairRegion = new TextureRegion(hairstylesTexture, currentHair * 14, 0, 14, 9);

        // Create a new group or use batching to layer them
        // For now, we just update the base character
        baseImage.setDrawable(new TextureRegionDrawable(baseRegion));
        // TODO: properly layer shirt, pants, and hair if needed
    }



    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        boxTexture.dispose();
        maleTexture.dispose();
        femaleTexture.dispose();
        shirtsTexture.dispose();
        pantsTexture.dispose();
        hairstylesTexture.dispose();
        hatsTexture.dispose();
        buttonsAtlas.dispose();
    }
}
