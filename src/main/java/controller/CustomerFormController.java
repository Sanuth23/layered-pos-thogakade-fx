package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.tm.ItemTm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
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
import dto.CustomerDto;
import dto.tm.CustomerTm;
import model.CustomerModel;
import model.impl.CustomerModelImpl;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.function.Predicate;

public class CustomerFormController {

    @FXML
    private AnchorPane customerPane;

    @FXML
    private JFXTextField txtId;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    private JFXTextField txtSearch;

    public JFXTreeTableView<CustomerTm> tblCustomer;

    @FXML
    private TreeTableColumn<?, ?> colId;

    @FXML
    private TreeTableColumn<?, ?> colName;

    @FXML
    private TreeTableColumn<?, ?> colAddress;

    @FXML
    private TreeTableColumn<?, ?> colSalary;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    private CustomerModel customerModel = new CustomerModelImpl();

    public void initialize(){
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new TreeItemPropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new TreeItemPropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String newValue) {
                tblCustomer.setPredicate(new Predicate<TreeItem<CustomerTm>>() {
                    @Override
                    public boolean test(TreeItem<CustomerTm> treeItem) {
                        return treeItem.getValue().getId().contains(newValue) ||
                                treeItem.getValue().getId().toLowerCase().contains(newValue) ||
                                treeItem.getValue().getName().contains(newValue) ||
                                treeItem.getValue().getName().toLowerCase().contains(newValue);
                    }
                });
            }
        });
    }

    private void setData(TreeItem<CustomerTm> newValue) {
        if (newValue != null){
            txtId.setEditable(false);
            txtId.setText(newValue.getValue().getId());
            txtName.setText(newValue.getValue().getName());
            txtAddress.setText(newValue.getValue().getAddress());
            txtSalary.setText(String.valueOf(newValue.getValue().getSalary()));
        }
    }


    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerModel.allCustomers();

            for (CustomerDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setStyle("-fx-background-color: #af0c0c; -fx-text-fill: white; ");
                CustomerTm c = new CustomerTm(dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });

                tmList.add(c);
            }
            TreeItem<CustomerTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblCustomer.setRoot(treeItem);
            tblCustomer.setShowRoot(false);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(String id) {
        try {
            boolean isDeleted = customerModel.deleteCustomer(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                loadCustomerTable();
                clearFields();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void reloadButtonOnAction(ActionEvent event) {
        loadCustomerTable();
        tblCustomer.refresh();
        clearFields();
    }

    private void clearFields() {
        tblCustomer.refresh();
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
        txtId.setEditable(true);
    }

    @FXML
    void saveButtonOnAction(ActionEvent event) {
        try {
            boolean isSaved = customerModel.saveCustomer(new CustomerDto(txtId.getText(), txtName.getText(),
                    txtAddress.getText(), Double.parseDouble(txtSalary.getText()))
            );
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
                loadCustomerTable();
                clearFields();
                tblCustomer.refresh();
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
            boolean isUpdated = customerModel.updateCustomer(new CustomerDto(txtId.getText(), txtName.getText(),
                    txtAddress.getText(), Double.parseDouble(txtSalary.getText()))
            );
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadCustomerTable();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
