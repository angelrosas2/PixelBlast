import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

public class Menu {
    private PixelBlast mainApp;
    private Scene menuScene, configScene;
    private double volume = 0.5;
    private int selectedMap = 0;

    public static final int MENU_WIDTH = 800;
    public static final int MENU_HEIGHT = 600;
    public static final int CONFIG_WIDTH = 800;
    public static final int CONFIG_HEIGHT = 600;
  
    public Menu(PixelBlast mainApp, int width, int height) {
        this.mainApp = mainApp;
        setupMenu(width, height);
        setupConfig(width, height);
    }

    private void setupMenu(int width, int height) {
        VBox menuBox = getVBox();

        Button playBtn = new Button("Jugar");
        Button configBtn = new Button("Configuración");
        Button exitBtn = new Button("Salir");

        Label mapLabel = new Label("Selecciona un mapa:");
        ComboBox<String> mapSelector = new ComboBox<>();
        mapSelector.getItems().addAll("Mapa 1", "Mapa 2", "Mapa 3");
        mapSelector.getSelectionModel().select(0);

        playBtn.setOnAction(e -> {
            selectedMap = mapSelector.getSelectionModel().getSelectedIndex();
            mainApp.startGame(selectedMap);
        });

        configBtn.setOnAction(e -> mainApp.getStage().setScene(configScene));
        exitBtn.setOnAction(e -> mainApp.getStage().close());

        menuBox.getChildren().addAll(playBtn, mapLabel, mapSelector, configBtn, exitBtn);
        menuScene = new Scene(menuBox, width, height);
    }

    private static VBox getVBox() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        // Cargar la imagen de fondo
        Image bgImage = new Image("file:resources/fondo_menu.png");
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        menuBox.setBackground(new Background(backgroundImage));
        return menuBox;
    }

    private void setupConfig(int width, int height) {
        VBox configBox = new VBox(20);
        configBox.setAlignment(Pos.CENTER);

        Label volumeLabel = new Label("Volumen de la música (no disponible):");
        Slider volumeSlider = new Slider(0, 1, volume);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);

        Button backBtn = new Button("Volver al menú");
        backBtn.setOnAction(e -> mainApp.getStage().setScene(menuScene));

        configBox.getChildren().addAll(volumeLabel, volumeSlider, backBtn);
        configScene = new Scene(configBox, width, height);
    }

    public Scene getMenuScene() {
        return menuScene;
    }

    public Scene getConfigScene() {
        return configScene;
    }

    public int getSelectedMap() {
        return selectedMap;
    }
}