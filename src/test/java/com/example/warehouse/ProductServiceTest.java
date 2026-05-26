package com.example.warehouse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.warehouse.ProductService.Product;
import com.example.warehouse.ProductService.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void updateStock_addsQuantityToExistingProduct() {
        Product product = new Product("P001", 10);
        when(productRepository.findById("P001")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        int newStock = productService.updateStock("P001", 5);

        assertThat(newStock).isEqualTo(15);
        assertThat(product.getStockQuantity()).isEqualTo(15);
        verify(productRepository).findById("P001");
        verify(productRepository).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateStock_subtractsQuantityFromExistingProduct() {
        Product product = new Product("P002", 10);
        when(productRepository.findById("P002")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        int newStock = productService.updateStock("P002", -4);

        assertThat(newStock).isEqualTo(6);
        assertThat(product.getStockQuantity()).isEqualTo(6);
        verify(productRepository).findById("P002");
        verify(productRepository).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateStock_rejectsSubtractingMoreThanAvailableStock() {
        Product product = new Product("P003", 3);
        when(productRepository.findById("P003")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock("P003", -4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resulting stock would be negative");

        assertThat(product.getStockQuantity()).isEqualTo(3);
        verify(productRepository).findById("P003");
        verify(productRepository, never()).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateStock_rejectsMissingProduct() {
        when(productRepository.findById("P404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStock("P404", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found with ID: P404");

        verify(productRepository).findById("P404");
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void updateStock_savesProductWithUpdatedStockAfterSuccessfulUpdate() {
        Product product = new Product("P005", 7);
        when(productRepository.findById("P005")).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateStock("P005", 8);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getId()).isEqualTo("P005");
        assertThat(productCaptor.getValue().getStockQuantity()).isEqualTo(15);
    }
}
