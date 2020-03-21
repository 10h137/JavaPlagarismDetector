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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static main.ExpandedGUI.expandedWindow;

public class GUI extends Application {


    private static final EnumSet<Normaliser.Features> enabled_features = EnumSet.noneOf(Normaliser.Features.class);
    static ListView<String> comparisons;
    private static ProgressBar progress = new ProgressBar(0);
    private final ObservableList<String> comparison_name_strings = FXCollections.observableArrayList();
    private AtomicReference<File> input_dir;
    private AtomicReference<File> output_dir;
    private FileComparison selected_comparison = null;
    private List<FileComparison> file_comparison_objects = new ArrayList<>();
    private Text file_count = new Text();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initUI(stage);
    }

    /**
     * Initialises the main window with all elements
     *
     * @param stage - current stage
     */
    private void initUI(Stage stage) {

        // Dropdown algorithm selection
        ComboBox<String> comparison_selector = new ComboBox<>();
        comparison_selector.getItems().add("Fingerprint");
        comparison_selector.getItems().add("String");

        // Comparison info text window
        ScrollPane comparison_info = new ScrollPane();
        comparison_info.setPrefHeight(200);
        comparison_info.setPrefWidth(150);
        Text info = new Text();
        comparison_info.setContent(info);

        // list view containing selectable comparison results
        comparisons = getComparisonListView(info);

        // button to expand the selected comparison in a new window
        Button btn_expand_comparison = new Button("Expand Comparison");
        GridPane.setHalignment(btn_expand_comparison, HPos.CENTER);
        btn_expand_comparison.setOnAction(x -> expandedWindow(selected_comparison));

        // creates layout
        GridPane grid_pane = new GridPane();
        grid_pane.setHgap(5);
        grid_pane.setVgap(15);
        grid_pane.setPadding(new Insets(10, 10, 10, 10));

        VBox file_count_box = new VBox();


        // adds elements tp grid
        grid_pane.add(getNormalisationCheckBoxes(), 0, 0);
        grid_pane.add(getDirectoryEntryBox(stage), 1, 0);
        grid_pane.add(comparison_selector, 1, 1);
        grid_pane.add(comparisons, 0, 2);
        grid_pane.add(comparison_info, 1, 2);
        grid_pane.add(btn_expand_comparison, 1, 3);
        grid_pane.add(getRunButton(info, comparison_selector), 0, 1);
        grid_pane.add(getSaveButtons(), 0, 3);
        grid_pane.add(progress, 0, 4);

        grid_pane.setMinWidth(Control.USE_PREF_SIZE);
        var scene = new Scene(grid_pane, 700, 500);

        stage.setTitle("Java Plagiarism Detector");
        stage.setScene(scene);
        stage.show();


    }

    /**
     * Creates a list view to contain any file comparisons that are produced, these can be selected
     *
     * @param info - text field displaying the currently selected comparison
     * @return - List View to display all the file comparison results
     */
    public ListView<String> getComparisonListView(Text info) {

        // List of file comparisons
        ListView<String> comparisons = new ListView<>();
        comparisons.setPrefWidth(400);
        comparisons.setPrefHeight(200);
        comparisons.setItems(comparison_name_strings);

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
        return comparisons;
    }

    /**
     * Creates box of checkboxes, one for eah normalisation feature
     *
     * @return - VBox containing check boxes
     */
    private VBox getNormalisationCheckBoxes() {

        VBox vbox = new VBox(10);
        for (Normaliser.Features value : Normaliser.Features.values()) {
            String text = value.toString().toLowerCase().replace('_', ' ');
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
            CheckBox btn = new CheckBox(text);
            btn.setOnAction(x -> {
                if (btn.isSelected()) enabled_features.add(value);
                else enabled_features.remove(value);
            });
            vbox.getChildren().add(btn);
        }
        return vbox;

    }

    /**
     * Creates a VBox containing a text box for the current selected directory and a file select button
     *
     * @param stage - current stage
     * @return - VBox containing file selection buttons and text boxes
     */
    private VBox getDirectoryEntryBox(Stage stage) {

        VBox container = new VBox(10);
        // input directory

        HBox hb_in = new HBox(10);
        Text t1 = new Text("Input dir");
        t1.setUnderline(true);

        DirectoryChooser input_dir_chooser = new DirectoryChooser();
        Button btn_input_dir = new Button("Select Input Directory");
        input_dir = new AtomicReference<>();
        btn_input_dir.setOnAction(e -> {
            input_dir.set(input_dir_chooser.showDialog(stage));
            t1.setUnderline(false);
            t1.setText("../" + input_dir.get().getName());
        });

        hb_in.getChildren().add(t1);
        hb_in.getChildren().add(btn_input_dir);
        hb_in.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().add(hb_in);


        //output directory
        HBox hb_out = new HBox(10);
        Text t2 = new Text("Output dir");
        t2.setUnderline(true);

        DirectoryChooser output_dir_chooser = new DirectoryChooser();
        Button btn_output_dir = new Button("Select Output Directory");
        output_dir = new AtomicReference<>();
        btn_output_dir.setOnAction(e -> {
            output_dir.set(output_dir_chooser.showDialog(stage));
            t2.setUnderline(false);
            t2.setText("../" + output_dir.get().getName());
        });

        hb_out.getChildren().add(t2);
        hb_out.getChildren().add(btn_output_dir);
        hb_out.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().add(hb_out);

        return container;

    }

    /**
     * Creates the run button to perform the comparisons
     *
     * @param info      - text field displaying the currently selected comparison
     * @param -         list view of all file comparisons
     * @param selection - dropdown for selecting the algorithm to use
     * @return - run button object
     */
    public HBox getRunButton(Text info, ComboBox<String> selection) {
        HBox box = new HBox(10);
        file_comparison_objects.clear();
        comparison_name_strings.clear();
        comparisons.refresh();
        // map mapping dropdown selection strings to class objects
        Map<String, Class<? extends ComparisonAlgorithm>> class_map = new HashMap<>();
        class_map.put("Fingerprint", FingerprintComparison.class);
        class_map.put("String", StringComparison.class);

        // run button to perform comparisons
        Button btn_run = new Button("Run");
        // performs file comparisons
        btn_run.setOnAction(x -> {
            ComparisonAlgorithm selected_class;
            // TODO update selected item for expand button
            info.setText("");
            selected_comparison = null;
            try {
                if (progress.progressProperty().isBound()) progress.progressProperty().unbind();
                // creates instance of selected class
                selected_class = class_map.get(selection.getValue()).getConstructor().newInstance();
                // generates comparisons
                Runner runner = new Runner(enabled_features, input_dir.get(), selected_class, this, file_count);
                progress.progressProperty().bind(runner.progressProperty().asObject());

                Thread task = new Thread(runner);
                task.start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        box.getChildren().addAll(btn_run, file_count);
        return box;
    }

    public HBox getSaveButtons() {
        HBox h_box = new HBox(10);
        Button btn_save = new Button("Save Selected");
        btn_save.setOnAction(x -> {
            writeComparison(selected_comparison);


        });
        Button btn_save_all = new Button("Save All");
        btn_save_all.setOnAction(x -> {
            file_comparison_objects.forEach(this::writeComparison);
        });

        h_box.getChildren().addAll(btn_save, btn_save_all);
        return h_box;
    }

    void writeComparison(FileComparison comparison) {
        List<MethodComparison> method_comparisons = comparison.getMethod_comparisons();
        StringBuilder sb = new StringBuilder();
        sb.append(comparison.getName() + "\n");
        sb.append("Algorithm score --> " + comparison.getScore() + "%\n\n");
        for (MethodComparison method_comparison : method_comparisons) {
            sb.append(method_comparison.getReport() + "\n");
        }

        try {
            FileOutputStream out = new FileOutputStream(output_dir.get() + "/" + comparison.getName() + ".txt");
            out.write(sb.toString().getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateList(List<FileComparison> list) {
        file_comparison_objects = list;
        // clears old comparisons string list
        comparison_name_strings.clear();
        // fills list with new strings
        for (FileComparison file_comparison : file_comparison_objects) {
            comparison_name_strings.add(file_comparison.getName());
        }
        comparisons.refresh();
    }


}
