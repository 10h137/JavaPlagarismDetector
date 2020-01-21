package main;

import comparison.Runner;
import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.algorithms.StringComparison;
import comparison.resultObjects.FileComparison;
import comparison.resultObjects.MethodComparison;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import normalisation.Normaliser;
import normalisation.elements.elementContainers.Method;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GUI extends Application {


    static final Set<Normaliser.Features> enabled_features = new HashSet<>();


    AtomicReference<File> input_dir;
    AtomicReference<File> output_dir;
    FileComparison selected_comparison = null;
    List<FileComparison> file_comparison_objects = new ArrayList<>();
    ObservableList<String> comparison_name_strings = FXCollections.observableArrayList();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initUI(stage);

    }

    private void initUI(Stage stage) {

        // map mapping dropdown selection strings to class objects
        Map<String, Class<? extends ComparisonAlgorithm>> class_map = new HashMap<>();
        class_map.put("Fingerprint", FingerprintComparison.class);
        class_map.put("String", StringComparison.class);


        // Dropdown algorithm selection
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add("Fingerprint");
        comboBox.getItems().add("String");


        // List of file comparisons
        ListView<String> comparisons = new ListView<>();
        comparisons.setPrefWidth(400);
        comparisons.setPrefHeight(200);
        comparisons.setItems(comparison_name_strings);

        // Comparison info text window
        ScrollPane comparison_info = new ScrollPane();
        comparison_info.setPrefHeight(200);
        comparison_info.setPrefWidth(150);
        Text info = new Text();
        comparison_info.setContent(info);

        // set actions for file comparison list
        // on clicked update info text
        comparisons.setOnMouseClicked(l -> {
            selected_comparison = file_comparison_objects.get(comparisons.getSelectionModel().getSelectedIndex());
            String report_string = selected_comparison.getReport();
            info.setText(report_string);
        });
        // on arrow key actions update info text to new selection
        comparisons.setOnKeyPressed(x -> {
            int current_index = comparisons.getSelectionModel().getSelectedIndex();

            if (x.getCode() == KeyCode.DOWN || x.getCode() == KeyCode.UP) {
                selected_comparison = file_comparison_objects.get(comparisons.getSelectionModel().getSelectedIndex());
                String report_string = selected_comparison.getReport();
                info.setText(report_string);

            }
        });


        // run button to perform comparisons
        Button btn_run = new Button("Run");
        // performs file comparisons
        btn_run.setOnAction(x -> {
            ComparisonAlgorithm selected_class = null;

            try {
                // creates instance of selected class
                selected_class = class_map.get(comboBox.getValue()).getConstructor().newInstance();
                // generates comparisons
                file_comparison_objects = Runner.run(enabled_features, input_dir.get(), selected_class);
                // clears old comparisons string list
                comparison_name_strings.clear();
                // fills list with new strings
                for (FileComparison file_comparison : file_comparison_objects) {
                    comparison_name_strings.add(file_comparison.getName());
                }
                comparisons.refresh();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
                e.printStackTrace();
            }


        });



        // button to expand the selected compariosn in a new window
        Button btn_expand_comparison = new Button("Expand Comparison");
        GridPane.setHalignment(btn_expand_comparison, HPos.CENTER);
        btn_expand_comparison.setOnAction(x -> expandedWindow(selected_comparison));


        // creates layout
        GridPane grid_pane = new GridPane();
        grid_pane.setHgap(5);
        grid_pane.setVgap(15);
        grid_pane.setPadding(new Insets(10, 10, 10, 10));

        grid_pane.add(getNormalisationCheckBoxes(), 0, 0);
        grid_pane.add(getDirectoryEntryBox(stage), 1, 0);
        grid_pane.add(comboBox, 1, 1);
        grid_pane.add(comparisons, 0, 2);
        grid_pane.add(comparison_info, 1, 2);
        grid_pane.add(btn_expand_comparison, 1, 3);
        grid_pane.add(btn_run, 0, 1);


        var scene = new Scene(grid_pane, 700, 500);

        stage.setTitle("Java Plagiarism Detector");
        stage.setScene(scene);
        stage.show();


    }


    public void expandedWindow(FileComparison expanded_comparison){
        TabPane tab_pane = new TabPane();


        Tab normalised_tab = new Tab("Normalised");
        Tab original_tab = new Tab("Original" );
        Tab method_tab = new Tab("Methods" );
        normalised_tab.setClosable(false);
        original_tab.setClosable(false);
        method_tab.setClosable(false);




        tab_pane.getTabs().add(normalised_tab);
        tab_pane.getTabs().add(original_tab);
        tab_pane.getTabs().add(method_tab);




        // normlaised text
        HBox normalised_container = new HBox(25);
        normalised_container.setAlignment(Pos.CENTER);
        normalised_container.setPrefSize(1200, 900);
        ScrollPane normalised_scroll_1 = new ScrollPane();
        normalised_scroll_1.setPrefSize(650, 800);
        Text normalised_txt_1 = new Text(expanded_comparison.getFile1().toString());
        normalised_scroll_1.setContent(normalised_txt_1);
        ScrollPane normalised_scroll_2 = new ScrollPane();
        normalised_scroll_2.setPrefSize(650, 800);
        Text normalised_txt_2 = new Text(expanded_comparison.getFile2().toString());
        normalised_scroll_2.setContent(normalised_txt_2);

        normalised_container.getChildren().addAll(normalised_scroll_1, normalised_scroll_2);
        normalised_tab.setContent(normalised_container);


        // original text
        HBox original_container = new HBox(25);
        original_container.setAlignment(Pos.CENTER);
        original_container.setPrefSize(1200, 900);
        ScrollPane original_scroll_1 = new ScrollPane();
        original_scroll_1.setPrefSize(650, 800);
        Text original_txt_1 = new Text();
        try {
            original_txt_1 = new Text(FileUtils.readFileToString(expanded_comparison.getFile1().getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        original_scroll_1.setContent(original_txt_1);
        ScrollPane original_scroll_2 = new ScrollPane();
        original_scroll_2.setPrefSize(650, 800);
        Text original_txt_2 = new Text();
        try {
            original_txt_2 = new Text(FileUtils.readFileToString(expanded_comparison.getFile2().getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        original_scroll_2.setContent(original_txt_2);

        original_container.getChildren().addAll(original_scroll_1, original_scroll_2);
        original_tab.setContent(original_container);




        // method text
        HBox method_container = new HBox(25);
        method_container.setAlignment(Pos.CENTER);
        method_container.setPrefSize(1200, 900);
        ScrollPane method_scroll_1 = new ScrollPane();
        method_scroll_1.setPrefSize(650, 800);
        Text method_txt_1 = new Text();
        method_scroll_1.setContent(method_txt_1);
        ScrollPane method_scroll_2 = new ScrollPane();
        method_scroll_2.setPrefSize(650, 800);
        Text method_txt_2 = new Text();
        method_scroll_2.setContent(method_txt_2);

        method_container.getChildren().addAll(method_scroll_1, method_scroll_2);


        VBox list_info_container = new VBox(25);

        ListView method_list = new ListView();
        List<MethodComparison> method_comparisons = expanded_comparison.getMethod_comparisons();
        List<String> method_comparison_strings_tmp = method_comparisons.stream().map(MethodComparison::getName).collect(Collectors.toList());

        ObservableList<String> comparison_name_strings = FXCollections.observableArrayList(method_comparison_strings_tmp);

        method_list.setItems(comparison_name_strings);
        method_list.setMinWidth(250);

        Text comparison_info = new Text();
        VBox.setMargin(comparison_info, new Insets(10));

        list_info_container.getChildren().addAll(method_list, comparison_info);

        // set actions for file comparison list
        // on clicked update info text
        method_list.setOnMouseClicked(l -> {
            MethodComparison comparison = method_comparisons.get(method_list.getSelectionModel().getSelectedIndex());
            Method m1 = comparison.getM1();
            Method m2 = comparison.getM2();

            String method_string_1 = m1.toString();
            String method_string_2 = m2.toString();

            method_txt_1.setText(method_string_1);
            method_txt_2.setText(method_string_2);
            comparison_info.setText(comparison.getReport());

        });
        // on arrow key actions update info text to new selection
        method_list.setOnKeyPressed(x -> {

            if (x.getCode() == KeyCode.DOWN || x.getCode() == KeyCode.UP) {
                MethodComparison comparison = method_comparisons.get(method_list.getSelectionModel().getSelectedIndex());
                Method m1 = comparison.getM1();
                Method m2 = comparison.getM2();

                String method_string_1 = m1.toString();
                String method_string_2 = m2.toString();

                method_txt_1.setText(method_string_1);
                method_txt_2.setText(method_string_2);
                comparison_info.setText(comparison.getReport());


            }
        });


        method_container.getChildren().add(0, list_info_container);

        method_tab.setContent(method_container);


        Stage stage2 = new Stage();
        stage2.setTitle(expanded_comparison.getName());
        stage2.setScene(new Scene(tab_pane, 1200, 900));
        stage2.show();
    }


    public VBox getNormalisationCheckBoxes() {

        VBox vbox = new VBox(10);
        for (Normaliser.Features value : Normaliser.Features.values()) {
            String text = value.toString().toLowerCase().replace('_', ' ');
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            CheckBox btn = new CheckBox(text);
            btn.setOnAction(x -> {
                if (btn.isSelected()) enabled_features.add(value);
                else enabled_features.add(value);
            });
            vbox.getChildren().add(btn);
        }
        return vbox;

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
