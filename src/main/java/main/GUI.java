package main;

import comparison.Runner;
import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.algorithms.StringComparison;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import comparison.resultObjects.FileComparison;
import normalisation.Normaliser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GUI extends Application {


    static final Set<Normaliser.Features> enabled_features = new HashSet<>();


    AtomicReference<File> input_dir;
    AtomicReference<File> output_dir;
    List<FileComparison> file_comparisons = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initUI(stage);

    }

    private void initUI(Stage stage) {

        Map<String, Class<? extends ComparisonAlgorithm >> class_map = new HashMap<>();
        class_map.put("Fingerprint", FingerprintComparison.class);
        class_map.put("String", StringComparison.class);


        stage.setTitle("JavaFX App");

        GridPane grid_pane = new GridPane();
        grid_pane.setHgap(5);
        grid_pane.setVgap(15);
        grid_pane.setPadding(new Insets(10, 10, 10, 10));

        grid_pane.add(getNormalisationCheckBoxes(), 0, 0);
        grid_pane.add(getDirectoryEntryBox(stage), 1, 0);

        Button btn_run = new Button("Run");


        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("Fingerprint");
        comboBox.getItems().add("String");
        grid_pane.add(comboBox, 1,1);

        // performs file comparisons
        btn_run.setOnAction(x -> {
            ComparisonAlgorithm c = null;

            try {
                c = class_map.get(comboBox.getValue()).getConstructor().newInstance();
                file_comparisons = Runner.run(enabled_features, input_dir.get(), c);

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        ScrollPane comparisons = new ScrollPane();
        comparisons.setPrefWidth(300);
        comparisons.setPrefHeight(200);

        ScrollPane comparison_info = new ScrollPane();
        comparison_info.setPrefHeight(200);
        comparison_info.setPrefWidth(150);

        Text info = new Text();
        comparison_info.setContent(info);

        Button btn_expand_comparison = new Button("Expand Comparison");
        btn_expand_comparison.setAlignment(Pos.TOP_CENTER);
        GridPane.setHalignment(btn_expand_comparison, HPos.CENTER);

        grid_pane.add(comparisons, 0,2);
        grid_pane.add(comparison_info, 1,2);
        grid_pane.add(btn_expand_comparison, 1,3);




        grid_pane.add(btn_run, 0,1);
        var scene = new Scene(grid_pane, 700, 500);

        stage.setTitle("Java Plagiarism Detector");
        stage.setScene(scene);
        stage.show();


    }





    public TilePane getNormalisationCheckBoxes() {


        TilePane tile_pane = new TilePane();
        tile_pane.setPrefColumns(2);
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
        return tile_pane;

    }

    public VBox getDirectoryEntryBox(Stage stage) {

        VBox container = new VBox(10);
        // input directory

        HBox hb_in = new HBox(10);
        Text t1 = new Text("Input dir");

        DirectoryChooser input_dir_chooser = new DirectoryChooser();
        Button btn_input_dir = new Button("Select Input Directory");
        input_dir = new AtomicReference<>();
        btn_input_dir.setOnAction(e -> {
            input_dir.set(input_dir_chooser.showDialog(stage));
            t1.setText(input_dir.get().toPath().toString());
        });

        hb_in.getChildren().add(t1);
        hb_in.getChildren().add(btn_input_dir);
        hb_in.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().add(hb_in);


        //output directory
        HBox hb_out = new HBox(10);
        Text t2 = new Text("Output dir");

        DirectoryChooser output_dir_chooser = new DirectoryChooser();
        Button btn_output_dir = new Button("Select Output Directory");
        output_dir = new AtomicReference<>();
        btn_output_dir.setOnAction(e -> {
            output_dir.set(output_dir_chooser.showDialog(stage));
            t2.setText(output_dir.get().toPath().toString());
        });

        hb_out.getChildren().add(t2);
        hb_out.getChildren().add(btn_output_dir);
        hb_out.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().add(hb_out);

        return container;

    }
}
