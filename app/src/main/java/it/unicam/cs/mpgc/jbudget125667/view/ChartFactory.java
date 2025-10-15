package it.unicam.cs.mpgc.jbudget125667.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

import java.util.Map;
import java.util.stream.Collectors;

public final class ChartFactory {

    private ChartFactory() {}

    public static ObservableList<PieChart.Data> mapToPieChartData(Map<String, Double> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return FXCollections.observableArrayList();
        }
        return dataMap.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public static PieChart createPieChart(String title, ObservableList<PieChart.Data> data) {
        PieChart chart = new PieChart(data);
        chart.setTitle(title);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("category-chart");

        // 1. Calcola il totale per poter calcolare le percentuali.
        double total = data.stream().mapToDouble(PieChart.Data::getPieValue).sum();

        int i = 0;
        for (PieChart.Data slice : chart.getData()) {
            slice.getNode().getStyleClass().add("data" + (i % 8));
            i++;

            // Aggiunge un tooltip che mostra nome e valore al passaggio del mouse
            String tooltipText = String.format("%s: %.2f €", slice.getName(), slice.getPieValue());
            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.getStyleClass().add("chart-tooltip");
            Tooltip.install(slice.getNode(), tooltip);

            double percentage = (slice.getPieValue() / total) * 100;
            String percentageText = percentage > 1 ? String.format("%.1f%%", percentage) : "";

            slice.nameProperty().bind(
                Bindings.concat(
                    slice.getName(), " ", percentageText
                )
            );
        }
        chart.setLabelsVisible(true);
        chart.setLabelLineLength(10);
        return chart;
    }
}