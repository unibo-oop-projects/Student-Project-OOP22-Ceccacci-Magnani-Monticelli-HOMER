package homer.view.javafx;

import java.util.ArrayList;
import java.util.List;

import homer.controller.api.electricalmeter.ElectricalMeter;
import homer.model.outlets.Outlet;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class ElectricalMeterView extends BorderPane {

    private ElectricalMeter meter;
    private TableView<List<Outlet>> outletTable;
    private TableColumn<List<Outlet>, Double> outletStatusColumn;
    private TableColumn<List<Outlet>, Void> outletActionColumn;
    private Label globalConsumptionLabel;
    private Label averagePowerLabel;

    public ElectricalMeterView(ElectricalMeter meter) {
        this.meter = meter;
        initView();
    }

    private void initView() {
        this.outletTable = new TableView<>();
        ObservableList<List<Outlet>> outlets = FXCollections.observableArrayList();
        for (Outlet outlet : meter.getOutlets()) {
            List<Outlet> outletList = new ArrayList<>();
            outletList.add(outlet);
            outlets.add(outletList);
        }

        // set the table items
        outletTable.setItems(outlets);
        // Initialize outlet table
        outletTable = new TableView<>();
        outletTable.setEditable(false);
        outletTable.setItems(outlets);

        outletStatusColumn = new TableColumn<>("Status");
        outletStatusColumn.setCellFactory(param -> new TableCell<List<Outlet>, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Outlet outlet = getTableRow().getItem().get(0);
                    Double power = outlet.getState().getPower().get();
                    setText(String.format("%.2f", power));
                }
            }
        });

        outletActionColumn = new TableColumn<>("Action");
        outletActionColumn.setCellFactory(param -> new TableCell<List<Outlet>, Void>() {
            private final ToggleButton outletSwitch = new ToggleButton("Switch OFF");
            {
                outletSwitch.setOnAction(event -> {
                    List<Outlet> outlet = getTableView().getItems().get(getIndex());
                    meter.cutPowerTo(outlet.get(0));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    List<Outlet> outlet = getTableView().getItems().get(getIndex());
                    outletSwitch.setSelected(outlet.get(0).getState().getPower().get() > 0.0);
                    setGraphic(outletSwitch);
                }
            }
        });

        List<TableColumn<List<Outlet>, ?>> columns = new ArrayList<>();
        columns.add(outletStatusColumn);
        columns.add(outletActionColumn);

        final int maxColumns = 3;
        final int offset = 30;
        final int height = outlets.size() * offset;
        final int deltaHeight = 34;
        outletTable.getColumns().addAll(columns.subList(0, Math.min(columns.size(), maxColumns)));
        outletTable.setPrefHeight(height + deltaHeight);

        globalConsumptionLabel = new Label("Global Consumption: ");
        globalConsumptionLabel.textProperty()
                .bind(Bindings.format("Global Consumption: %.2f W", meter.getGlobalConsumption()));

        // Initialize average power label
        averagePowerLabel = new Label("Average Power: ");
        averagePowerLabel.textProperty().bind(Bindings.format("Average Power: %.2f Wh", meter.getAveragePower()));

        // Set up view
        this.setTop(outletTable);
        this.setBottom(new HBox(globalConsumptionLabel, averagePowerLabel));
    }
}
