package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.tm.CustomerTm;
import dto.tm.OrderTm;
import dto.tm.PlaceOrderTm;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.OrderDetailModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import model.impl.OrderDetailModelImpl;
import model.impl.OrderModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;

public class OrderDetailController {

    public JFXTextField txtSearch;
    @FXML
    private AnchorPane orderDetailPane;

    @FXML
    private JFXTreeTableView<OrderTm> tblOrder;

    @FXML
    private TreeTableColumn<?, ?> colOrderId;

    @FXML
    private TreeTableColumn<?, ?> colDate;

    @FXML
    private TreeTableColumn<?, ?> colCustId;

    @FXML
    private TreeTableColumn<?, ?> colCustName;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private JFXTreeTableView<PlaceOrderTm> tblPlaceOrder;

    @FXML
    private TreeTableColumn<?, ?> colCode;

    @FXML
    private TreeTableColumn<?, ?> colDesc;

    @FXML
    private TreeTableColumn<?, ?> colQty;

    @FXML
    private TreeTableColumn<?, ?> colAmount;

    @FXML
    private TreeTableColumn<?, ?> colPlaceOption;

    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
    private OrderModel orderModel = new OrderModelImpl();
    private OrderDetailModel orderDetailModel = new OrderDetailModelImpl();

    public void initialize() {
        colOrderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderId"));
        colDate.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        colCustId.setCellValueFactory(new TreeItemPropertyValueFactory<>("custId"));
        colCustName.setCellValueFactory(new TreeItemPropertyValueFactory<>("custName"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colPlaceOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        loadOrderTable();

        tblOrder.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue!=null){
                loadPlaceOrderTable(newValue);
            }
        });

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String newValue) {
                tblOrder.setPredicate(new Predicate<TreeItem<OrderTm>>() {
                    @Override
                    public boolean test(TreeItem<OrderTm> treeItem) {
                        return treeItem.getValue().getOrderId().contains(newValue) ||
                                treeItem.getValue().getOrderId().toLowerCase().contains(newValue) ||
                                treeItem.getValue().getCustId().contains(newValue) ||
                                treeItem.getValue().getCustId().toLowerCase().contains(newValue) ||
                                treeItem.getValue().getCustName().contains(newValue) ||
                                treeItem.getValue().getCustName().toLowerCase().contains(newValue);
                    }
                });
            }
        });
    }

    private void loadPlaceOrderTable(TreeItem<OrderTm> newValue) {
        ObservableList<PlaceOrderTm> placeOrderTmList = FXCollections.observableArrayList();

        try {
            List<OrderDetailDto> dtoList = orderDetailModel.allOrderDetails(newValue.getValue().getOrderId());

            for (OrderDetailDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setStyle("-fx-background-color: #af0c0c; -fx-text-fill: white; ");
                PlaceOrderTm placeOrderTm = new PlaceOrderTm(
                        dto.getItemCode(),
                        itemModel.getItem(dto.getItemCode()).getDescription(),
                        dto.getQty(),
                        dto.getQty() * dto.getUnitPrice(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteOrderDetail(dto.getOrderId(),dto.getItemCode());
                });

                placeOrderTmList.add(placeOrderTm);
            }
            TreeItem<PlaceOrderTm> treeItem = new RecursiveTreeItem<>(placeOrderTmList, RecursiveTreeObject::getChildren);
            tblPlaceOrder.setRoot(treeItem);
            tblPlaceOrder.setShowRoot(false);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrderDetail(String orderId, String itemCode) {
        try {
            boolean isDeleted = orderDetailModel.deleteOrderDetail(orderId,itemCode);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                tblOrder.refresh();
                tblPlaceOrder.refresh();
                txtSearch.clear();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadOrderTable() {
        ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

        try {
            List<OrderDto> dtoList = orderModel.allOrders();

            for (OrderDto dto:dtoList) {
                JFXButton btn = new JFXButton("Delete");
                btn.setStyle("-fx-background-color: #af0c0c; -fx-text-fill: white; ");
                OrderTm orderTm = new OrderTm(
                        dto.getOrderId(),
                        dto.getDate(),
                        dto.getCustId(),
                        customerModel.getCustomer(dto.getCustId()).getName(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteOrder(orderTm.getOrderId());
                });

                tmList.add(orderTm);
            }
            TreeItem<OrderTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteOrder(String orderId) {
        try {
            boolean isDeleted = orderModel.deleteOrder(orderId);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Order Deleted!").show();
                loadOrderTable();
                txtSearch.clear();
                tblOrder.refresh();
                tblPlaceOrder.refresh();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong!").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) tblOrder.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void reloadButtonOnAction(ActionEvent event) {
        loadOrderTable();
        tblOrder.refresh();
        txtSearch.clear();
    }

}
