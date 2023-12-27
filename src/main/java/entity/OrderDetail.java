package entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class OrderDetail {
    @EmbeddedId
    private OrderDetailsKey detailsId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "orderId")
    Orders orders;

    @ManyToOne
    @MapsId("code")
    @JoinColumn(name = "itemCode")
    Item item;

    private int qty;
    private double unitPrice;

}
