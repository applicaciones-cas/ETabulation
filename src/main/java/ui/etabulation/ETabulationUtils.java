package ui.etabulation;


import java.util.List;
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
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javafx.application.Platform;

import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;


public class ETabulationUtils {


    
    public static final double PERFECT_SCORE = 100.0;
    public static final double W_SW = 15.0;  
    public static final double W_FL = 15.0;  
    public static final double W_TA = 20.0;  
    public static final double W_PE = 25.0;  
    public static final double W_BE = 25.0;  
    public static final double W_AU = 10.0;  
    
    
    // Utility method for converting Color to CSS RGB string
    public static String toRgbString(Color c) {
        return String.format("rgb(%d, %d, %d)",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255));
    }
    
    
    public static double computeWeightedPercent(double raw, double weight) {
        double pct = (raw / PERFECT_SCORE) * weight;
        // round to 1 decimal: multiply, round, divide
        return Math.round(pct * 10.0) / 10.0;
    }
    
    public static void recomputeTotalAsSum(TableView<TableModelETabulation.Result> table) {
        for (TableModelETabulation.Result r : table.getItems()) {
            double total = r.getSportswear()
                      + r.getFilipiniana()
                      + r.getTalent()
                      + r.getPersonality()
                      + r.getBeauty()
                      + r.getAudience();
            total = Math.round(total * 10.0) / 10.0;
            r.setTotal(total);
        }
    }

    
    public static <R> void bindPercentageColumn(
            TableView<R> table,
            TableColumn<R, Double> column,
            BiConsumer<R, Double> setter,
            double weight
    ) {
        column.setOnEditCommit(ev -> {
            R rowObj = ev.getRowValue();
            Object newVal = ev.getNewValue();

            // 1) Extract a raw double from whatever newVal is
            double raw;
            if (newVal instanceof Number) {
                raw = ((Number)newVal).doubleValue();
            } else {
                try {
                    raw = Double.parseDouble(newVal.toString().trim());
                } catch (Exception ex) {
                    raw = 0.0;
                }
            }

            // 2) Clamp with Math, not sun.internal clamp
            raw = Math.max(0.0, Math.min(PERFECT_SCORE, raw));

            // 3) Compute weighted percentage (1-decimal place)
            double pct = Math.round((raw / PERFECT_SCORE) * weight * 10.0) / 10.0;

            // 4) Write back the percent into the model
            setter.accept(rowObj, pct);

            // 5) Recompute TOTAL and refresh
            @SuppressWarnings("unchecked")
            TableView<TableModelETabulation.Result> t =
                (TableView<TableModelETabulation.Result>) (TableView<?>) table;
            recomputeTotalAsSum(t);
            table.refresh();
        });
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

                // Helper to parse whatever's in the text field into a double safely
                Supplier<Double> parseRaw = () -> {
                    try {
                        return Double.parseDouble(textField.getText().trim());
                    } catch (Exception ex) {
                        return 0.0;
                    }
                };

                // 1) When they press Enter, parse to a Double and commit THAT
                textField.setOnAction(e -> {
                    double raw = parseRaw.get();
                    @SuppressWarnings("unchecked")
                    T boxed = (T) (Double) raw;
                    commitEdit(boxed);
                });

                // 2) When focus is lost (e.g. click or Tab), do the exact same
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        double raw = parseRaw.get();
                        @SuppressWarnings("unchecked")
                        T boxed = (T) (Double) raw;
                        commitEdit(boxed);
                    }
                });

                // 3) **ONLY** handle Tab for navigation—no commit here!
                textField.setOnKeyPressed(evt -> {
                    if (evt.getCode() == KeyCode.TAB) {
                        // compute next editable column (skip first & last)
                        TableView<TableModelETabulation.Result> tv = getTableView();
                        int row = getIndex();
                        List<TableColumn<TableModelETabulation.Result, ?>> cols = tv.getVisibleLeafColumns();
                        int colCount = cols.size();
                        int current = cols.indexOf(getTableColumn());
                        int next = (current + 1) % colCount;
                        while (next == 0 || next == colCount - 1 || !cols.get(next).isEditable()) {
                            next = (next + 1) % colCount;
                        }
                        TableColumn<TableModelETabulation.Result, ?> nextCol = cols.get(next);
                        Platform.runLater(() -> {
                            tv.edit(row, nextCol);
                            tv.getFocusModel().focus(row, nextCol);
                        });
                        evt.consume();
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
