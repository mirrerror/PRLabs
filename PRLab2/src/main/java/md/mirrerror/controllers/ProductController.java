package md.mirrerror.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.mirrerror.models.Product;
import md.mirrerror.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Optional<Integer> page,
                        @RequestParam(value = "products_per_page", required = false) Optional<Integer> productsPerPage,
                        Model model) {

        if(page.isPresent() && productsPerPage.isPresent()) model.addAttribute("products", productService.getAllProductsPaginated(page.get(), productsPerPage.get()));
        else model.addAttribute("products", productService.getAllProducts());

        return "products/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        Product product = productService.getProductById(id);

        model.addAttribute("product", product);

        return "products/show";
    }

    @GetMapping("/new")
    public String newProduct(@ModelAttribute("product") Product product) {
        return "products/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("product") @Valid Product product,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "products/new";

        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("product") @Valid Product product,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "products/edit";

        product.setProductId(id);
        productService.saveProduct(product);

        return "redirect:/products";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    @GetMapping("/upload")
    public String upload() {
        return "products/upload";
    }

    @PostMapping("/upload")
    public String uploadProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/products/upload";
        }

        try {

            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<Product> products = Product.fromJsonArray(content);

            if (products != null) {
                productService.saveProductList(products);
            } else {
                Product product = Product.fromJson(content);
                if (product != null) {
                    productService.saveProduct(product);
                } else return "redirect:/products/upload";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/products/upload";
        }

        return "redirect:/products";
    }

}