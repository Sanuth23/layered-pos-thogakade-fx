package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.tm.PlaceOrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import model.impl.OrderModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {

    public AnchorPane orderPane;
    public Label lblOrderId;
    @FXML
    private JFXComboBox<?> cmbCustId;

    @FXML
    private JFXComboBox<?> cmbCode;

    @FXML
    private JFXTextField txtCustName;

    @FXML
    private JFXTextField txtDesc;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private Label lblTotal;

    @FXML
    private JFXTreeTableView<PlaceOrderTm> tblOrder;

    @FXML
    private TreeTableColumn<?, ?> colCode;

    @FXML
    private TreeTableColumn<?, ?> colDesc;

    @FXML
    private TreeTableColumn<?, ?> colQty;

    @FXML
    private TreeTableColumn<?, ?> colAmount;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    private List<CustomerDto> customers = new ArrayList<>();
    private List<ItemDto> items = new ArrayList<>();

    private double tot = 0;

    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
    private OrderModel orderModel = new OrderModelImpl();

    private ObservableList<PlaceOrderTm> tmList = FXCollections.observableArrayList();

    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateId();

        loadCustomerIds();
        loadItemCodes();

        cmbCustId.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, id) -> {
            for (CustomerDto dto:customers) {
                if (dto.getId().equals(id)){
                    txtCustName.setText(dto.getName());
                }
            }
        });
        cmbCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, code) -> {
            for (ItemDto dto:items) {
                if (dto.getCode().equals(code)){
                    txtDesc.setText(dto.getDescription());
                    txtUnitPrice.setText(String.format("%.2f",dto.getPrice()));
                }
            }
        });
    }

    private void generateId() {
        try {
            OrderDto dto =orderModel.lastOrder();
            if (dto!=null){
                String id = dto.getOrderId();
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d",num));
            }else {
                lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadItemCodes()  {
        try {
            items = itemModel.allItems();
            ObservableList list = FXCollections.observableArrayList();
            for (ItemDto dto:items){
                list.add(dto.getCode());
            }
            cmbCode.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerIds() {
        try {
            customers = customerModel.allCustomers();
            ObservableList list = FXCollections.observableArrayList();
            for (CustomerDto dto:customers){
                list.add(dto.getId());
            }
            cmbCustId.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) orderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void addToCartButtonOnAction(ActionEvent event) {

        try {
            double amount = itemModel.getItem(cmbCode.getValue().toString()).getPrice() * Integer.parseInt(txtQty.getText());
            JFXButton btn = new JFXButton("Delete");
            btn.setStyle("-fx-background-color: #af0c0c; -fx-text-fill: white; ");

            PlaceOrderTm orderTm = new PlaceOrderTm(
                    cmbCode.getValue().toString(),
                    txtDesc.getText(),
                    Integer.parseInt(txtQty.getText()),
                    amount,
                    btn
            );

            btn.setOnAction(actionEvent -> {
                tmList.remove(orderTm);
                tot -= orderTm.getAmount();
                tblOrder.refresh();
                lblTotal.setText(String.format("%.2f",tot));
            });

            boolean isExist =false;

            for (PlaceOrderTm order:tmList) {
                if (order.getCode().equals(orderTm.getCode())){
                    order.setQty(order.getQty()+orderTm.getQty());
                    order.setAmount(order.getAmount()+orderTm.getAmount());
                    isExist = true;
                    tot += orderTm.getAmount();
                }
            }

            if (!isExist){
                tmList.add(orderTm);
                tot += orderTm.getAmount();
            }

            TreeItem<PlaceOrderTm> treeItem = new RecursiveTreeItem<PlaceOrderTm>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeItem);
            tblOrder.setShowRoot(false);

            lblTotal.setText(String.format("%.2f",tot));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void placeOrderButtonOnAction(ActionEvent event) {
        List<OrderDetailDto> list = new ArrayList<>();
        for (PlaceOrderTm tm:tmList) {
            list.add(new OrderDetailDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount() / tm.getQty()
            ));
        }

        boolean isSaved = false;
        try {
            isSaved = orderModel.saveOrder(new OrderDto(
                    lblOrderId.getText(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    cmbCustId.getValue().toString(),
                    list
            ));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Order Saved!").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
