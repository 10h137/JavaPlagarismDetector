import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import normalisation.old.Normaliser;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class GUI extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        initUI(stage);

    }

    static final Set<Normaliser.Features> enabled_features = new HashSet<>();

    private void initUI(Stage stage) {

        VBox vbox = new VBox(10);

        TilePane tile_pane = new TilePane();
        tile_pane.setTileAlignment(Pos.CENTER_LEFT);
        tile_pane.setHgap(20);
        tile_pane.setVgap(10);
        for (Normaliser.Features value : Normaliser.Features.values()) {
            String text = value.toString().toLowerCase().replace('_', ' ');
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            CheckBox btn = new CheckBox(text);
            btn.setOnAction(x -> {
                if (btn.isSelected()) enabled_features.add(value);
                else enabled_features.add(value);
            });
            tile_pane.getChildren().add(btn);

        }

        vbox.getChildren().add(tile_pane);


        stage.setTitle("JavaFX App");


        // input directory

        HBox hb_in = new HBox(10);
        Text t1 = new Text("Input dir");

        DirectoryChooser input_dir_chooser = new DirectoryChooser();
        Button btn_input_dir = new Button("Select Input Directory");
        AtomicReference<File> input_dir = new AtomicReference<>();
        btn_input_dir.setOnAction(e -> input_dir.set(input_dir_chooser.showDialog(stage)));

        hb_in.getChildren().add(t1);
        hb_in.getChildren().add(btn_input_dir);
        hb_in.setAlignment(Pos.CENTER_RIGHT);
        vbox.getChildren().add(hb_in);


        //output directory

        HBox hb_out = new HBox(10);
        Text t2 = new Text("Output dir");

        DirectoryChooser output_dir_chooser = new DirectoryChooser();
        Button btn_output_dir = new Button("Select Output Directory");
        AtomicReference<File> output_dir = new AtomicReference<>();
        btn_output_dir.setOnAction(e -> output_dir.set(output_dir_chooser.showDialog(stage)));

        hb_out.getChildren().add(t2);
        hb_out.getChildren().add(btn_output_dir);
        hb_out.setAlignment(Pos.CENTER_RIGHT);
        vbox.getChildren().add(hb_out);

        Button btn_run = new Button("Run");
        btn_run.setOnAction(x -> {

        });
        vbox.getChildren().add(btn_run);

        tile_pane.setPadding(new Insets(25));


        var scene = new Scene(vbox, 700, 500);

        stage.setTitle("Java Plagiarism Detector");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
