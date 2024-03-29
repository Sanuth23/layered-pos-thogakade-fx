package controller;

import bo.BoFactory;
import bo.custom.ItemBo;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.util.BoType;
import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import dto.ItemDto;
import dto.tm.ItemTm;
import dao.custom.ItemDao;
import dao.custom.impl.ItemDaoImpl;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.function.Predicate;

public class ItemFormController {


    public AnchorPane itemPane;
    public JFXTextField txtSearch;
    @FXML
    private JFXTextField txtCode;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private TreeTableColumn colCode;

    @FXML
    private TreeTableColumn colDescription;

    @FXML
    private TreeTableColumn colPrice;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colOption;

    private ItemBo itemBo = BoFactory.getInstance().getBo(BoType.ITEM);

    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String newValue) {
                tblItem.setPredicate(new Predicate<TreeItem<ItemTm>>() {
                    @Override
                    public boolean test(TreeItem<ItemTm> treeItem) {
                        return treeItem.getValue().getCode().contains(newValue) ||
                                treeItem.getValue().getCode().toLowerCase().contains(newValue) ||
                                treeItem.getValue().getDescription().contains(newValue) ||
                                treeItem.getValue().getDescription().toLowerCase().contains(newValue);
                    }
                });
            }
        });
    }

    private void setData(TreeItem<ItemTm> newValue) {
        if (newValue != null){
            txtCode.setEditable(false);
            txtCode.setText(newValue.getValue().getCode());
            txtDescription.setText(newValue.getValue().getDescription());
            txtPrice.setText(String.valueOf(newValue.getValue().getPrice()));
            txtQty.setText(String.valueOf(newValue.getValue().getQty()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> itemList = FXCollections.observableArrayList();
        try {
            List<ItemDto> dtoList = itemBo.allItems();

            for (ItemDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setStyle("-fx-background-color: #af0c0c; -fx-text-fill: white; ");
                ItemTm itemTm = new ItemTm(dto.getCode(),
                        dto.getDescription(),
                        dto.getPrice(),
                        dto.getQty(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteItem(itemTm.getCode());
                });
                itemList.add(itemTm);
            }
            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(itemList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(String code) {
        try {
            boolean isDeleted = itemBo.deleteItem(code);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadItemTable();
                clearFields();
                tblItem.refresh();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tblItem.refresh();
        txtCode.clear();
        txtDescription.clear();
        txtPrice.clear();
        txtQty.clear();
        txtCode.setEditable(true);
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) {
        try {
            boolean isSaved = itemBo.saveItem(new ItemDto(txtCode.getText(), txtDescription.getText(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQty.getText()))
            );
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
                loadItemTable();
                clearFields();
                tblItem.refresh();
            }
        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateButtonOnAction(ActionEvent event) {
        try {
            boolean isUpdated = itemBo.updateItem(new ItemDto(txtCode.getText(), txtDescription.getText(),
                    Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQty.getText()))
            );
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
                loadItemTable();
                tblItem.refresh();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent event ) {
        Stage stage = (Stage) itemPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadButtonOnAction(ActionEvent actionEvent) {
        loadItemTable();
        tblItem.refresh();
        clearFields();
    }

    public void reportButtonOnAction(ActionEvent actionEvent) {
        try {
            JasperDesign design = JRXmlLoader.load("src/main/resources/reports/Item_Report.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstanceOf().getConnection());
            JasperViewer.viewReport(jasperPrint,false);
        } catch (JRException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
