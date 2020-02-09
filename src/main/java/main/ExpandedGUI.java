package main;

import comparison.resultObjects.FileComparison;
import comparison.resultObjects.MethodComparison;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ExpandedGUI {


    /**
     * Creates a new window expanding the currently selected comparison
     *
     * @param expanded_comparison - currently selected comparison
     */
    static void expandedWindow(FileComparison expanded_comparison) {
        TabPane tab_pane = new TabPane();

        tab_pane.getTabs().add(getNormalisedTab(expanded_comparison));
        tab_pane.getTabs().add(getOriginalTab(expanded_comparison));
        tab_pane.getTabs().add(getMethodTab(expanded_comparison));

        Stage stage2 = new Stage();
        stage2.setTitle(expanded_comparison.getName());
        stage2.setScene(new Scene(tab_pane, 1200, 900));
        stage2.show();
    }

    /**
     * Creates the normalisation tab which displays the two normalised files side by side
     *
     * @param expanded_comparison - comparison being expanded
     * @return - Tab displaying normalised files
     */
    static Tab getNormalisedTab(FileComparison expanded_comparison) {
        // normalised text
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


        Tab normalised_tab = new Tab("Normalised");
        normalised_tab.setClosable(false);
        normalised_tab.setContent(normalised_container);

        return normalised_tab;
    }

    /**
     * Creates the original tab which displays the two original files side by side
     *
     * @param expanded_comparison - comparison being expanded
     * @return - Tab displaying original files
     */
    static Tab getOriginalTab(FileComparison expanded_comparison) {
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

        Tab original_tab = new Tab("Original");
        original_tab.setClosable(false);
        original_tab.setContent(original_container);

        return original_tab;

    }

    /**
     * Creates the method tab which displays a list of method comparisons,
     * when one is selected the method strings are displayed side by side
     *
     * @param expanded_comparison - comparison being expanded
     * @return - Tab displaying method comparisons
     */
    static Tab getMethodTab(FileComparison expanded_comparison) {
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

        ListView<String> method_list = new ListView<>();
        List<MethodComparison> method_comparisons = expanded_comparison.getMethod_comparisons();
        List<String> method_comparison_strings_tmp = method_comparisons.stream()
                .map(MethodComparison::getName)
                .collect(Collectors.toList());

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
            method_txt_1.setText(comparison.getM1().toString());
            method_txt_2.setText(comparison.getM2().toString());
            comparison_info.setText(comparison.getReport());

        });
        // on arrow key actions update info text to new selection
        method_list.setOnKeyPressed(x -> {
            if (x.getCode() == KeyCode.DOWN || x.getCode() == KeyCode.UP) {
                MethodComparison comparison = method_comparisons.get(method_list.getSelectionModel().getSelectedIndex());
                method_txt_1.setText(comparison.getM1().toString());
                method_txt_2.setText(comparison.getM2().toString());
                comparison_info.setText(comparison.getReport());
            }
        });

        method_container.getChildren().add(0, list_info_container);

        Tab method_tab = new Tab("Methods");
        method_tab.setClosable(false);
        method_tab.setContent(method_container);

        return method_tab;
    }
}
