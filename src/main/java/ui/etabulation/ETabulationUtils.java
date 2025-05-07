package ui.etabulation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

public class ETabulationUtils {

    // Utility method for converting Color to CSS RGB string
    public static String toRgbString(Color c) {
        return String.format("rgb(%d, %d, %d)",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255));
    }
    
    // Create a custom cell factory for your TableView columns.
    public static <T> Callback<TableColumn<TableModelETabulation.Result, T>, TableCell<TableModelETabulation.Result, T>> 
    createEditableCellFactory(double radiusX, double radiusY, Color circleColor) {
        return column -> new TableCell<TableModelETabulation.Result, T>() {
            private final StackPane stack = new StackPane();
            private final Ellipse ellipse = new Ellipse(radiusX, radiusY);
            private final Text text = new Text();
            private TextField textField;
            
            {
                ellipse.setFill(circleColor);
                text.setFill(Color.GREY);
                text.getStyleClass().add("text");
                stack.getChildren().addAll(ellipse, text);
            }
            
            @Override
            public void startEdit() {
                if (!isEmpty() && column.isEditable()) {
                    super.startEdit();
                    createTextField();
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    textField.selectAll();
                    textField.requestFocus();
                }
            }
            
            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setGraphic(stack);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            
            @Override
            public void commitEdit(T newValue) {
                // Update underlying model here if needed before commit
                super.commitEdit(newValue);
                setGraphic(stack);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing() && textField != null) {
                        textField.setText(item.toString());
                        setGraphic(textField);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        text.setText(item.toString());
                        setGraphic(stack);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
            }
            
            private void createTextField() {
                textField = new TextField(getItem() == null ? "" : getItem().toString());
                textField.setOnAction(e -> {
                    commitEdit((T) textField.getText());
                });
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit((T) textField.getText());
                    }
                });
            }
        };
    }
    
    // Animate row style changes: background color and height.
    public static <T> void animateRowStyle(TableRow<T> row, Color targetColor, double targetHeight, Duration duration) {
        // Animate Background Color
        ObjectProperty<Paint> bgColorProperty = (ObjectProperty<Paint>) row.getProperties().get("bgColor");
        if (bgColorProperty == null) {
            bgColorProperty = new SimpleObjectProperty<>(targetColor);
            row.getProperties().put("bgColor", bgColorProperty);
            bgColorProperty.addListener((obs, oldVal, newVal) -> {
                row.setStyle("-fx-background-color: " + toRgbString((Color)newVal) + ";");
            });
            row.setStyle("-fx-background-color: " + toRgbString(targetColor) + ";");
        }
        if (!bgColorProperty.get().equals(targetColor)) {
            Timeline colorTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(bgColorProperty, bgColorProperty.get())),
                new KeyFrame(duration, new KeyValue(bgColorProperty, targetColor))
            );
            colorTimeline.play();
        }
        
        // Animate Row Height using its prefHeightProperty
        double currentHeight = row.getPrefHeight();
        if (currentHeight == 0) {
            currentHeight = targetHeight;
            row.setPrefHeight(currentHeight);
        }
        if (currentHeight != targetHeight) {
            Timeline heightTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(row.prefHeightProperty(), currentHeight)),
                new KeyFrame(duration, new KeyValue(row.prefHeightProperty(), targetHeight))
            );
            heightTimeline.play();
        }
    }
    
    
    public static <T> void updateRowStyle(TableRow<T> row, int index) {
        if (row.isEmpty() || row.getItem() == null) {
            row.setStyle("");
            row.setPrefHeight(40);
        } else {
            Color targetColor;
            double targetHeight;
            if (row.isSelected()) {
                targetColor = Color.web("#cd7f32");
                targetHeight = 60;
            } else {
                targetColor = (index % 2 == 0) ? Color.web("#d8d8d8") : Color.web("#b1b1b1");
                targetHeight = 40;
            }
            animateRowStyle(row, targetColor, targetHeight, Duration.millis(300));
        }
    }
}
