package md.mirrerror.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import md.mirrerror.utils.CurrencyUtils;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @NotEmpty(message = "Name must not be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Column(name = "name")
    private String name;

    @Min(value = 0, message = "Price must be greater than 0")
    @Column(name = "price_in_gbp")
    private double priceInGbp;

    @NotEmpty(message = "URL must not be empty")
    @Size(min = 1, max = 255, message = "URL must be between 1 and 255 characters")
    @Column(name = "url")
    private String url;

    @NotEmpty(message = "Product details must not be empty")
    @Size(min = 1, max = 255, message = "Product details must be between 1 and 255 characters")
    @Column(name = "product_details")
    private String productDetails;

    @Transient
    public double getPriceInMdl() {
        return CurrencyUtils.convertGbpToMdl(priceInGbp);
    }

}
