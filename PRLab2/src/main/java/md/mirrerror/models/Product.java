package md.mirrerror.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import md.mirrerror.utils.CurrencyUtils;

import java.util.List;

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

    @Min(value = 0, message = "Price mustn't be less than 0")
    @Column(name = "price_in_gbp")
    private double priceInGbp;

    @NotEmpty(message = "URL must not be empty")
    @Size(min = 1, max = 255, message = "URL must be between 1 and 255 characters")
    @Column(name = "url")
    private String url;

    @Column(name = "product_details")
    private String productDetails;

    @Transient
    public double getPriceInMdl() {
        return CurrencyUtils.convertGbpToMdl(priceInGbp);
    }

    @Transient
    public static Product fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(json, Product.class);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

    @Transient
    public static List<Product> fromJsonArray(String jsonArray) {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode rootNode = objectMapper.readTree(jsonArray);
            JsonNode productsNode = rootNode.get("products");
            return objectMapper.readValue(productsNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

}
