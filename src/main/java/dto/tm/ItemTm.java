package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ItemTm extends RecursiveTreeObject<ItemTm> {
    private String code;
    private String description;
    private double price;
    private int qty;
    private JFXButton btn;

    public ItemTm(String code, String description, double price, int qty) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.qty = qty;
    }
}
