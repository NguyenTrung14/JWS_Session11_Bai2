# Phan 1 - Phan tich logic cap nhat ton kho

## 1. Quy tac nghiep vu

Phuong thuc `updateStock(String productId, int quantityChange)` can dam bao cac quy tac sau:

- Khong cho phep so luong ton kho cua san pham nho hon `0`.
- San pham phai ton tai trong he thong thi moi duoc cap nhat ton kho.
- Moi thay doi ton kho phai duoc ghi nhan vao co so du lieu thong qua `productRepository.save(product)`.

## 2. Phan tich logic hien tai

### Truong hop san pham khong ton tai

Code hien tai co goi:

```java
Optional<Product> productOpt = productRepository.findById(productId);
if (productOpt.isEmpty()) {
    throw new IllegalArgumentException("Product not found with ID: " + productId);
}
```

Phan nay ve co ban la dung voi quy tac nghiep vu: neu san pham khong ton tai thi khong duoc cap nhat ton kho va phai nem exception.

Trong test, can kiem tra them rang khi san pham khong ton tai thi `productRepository.save(product)` khong duoc goi.

### Truong hop tru ton kho lon hon so luong hien co

Code hien tai tinh ton kho moi nhu sau:

```java
int newStock = currentStock + quantityChange;
```

Neu `quantityChange` la so am va co gia tri tuyet doi lon hon `currentStock`, `newStock` se nho hon `0`.

Vi du:

```text
currentStock = 10
quantityChange = -15
newStock = -5
```

Theo quy tac nghiep vu, ton kho khong duoc phep am. Code hien tai da co kiem tra:

```java
if (newStock < 0) {
    throw new IllegalStateException("Resulting stock would be negative");
}
```

Tuy nhien, day la loi ve du lieu dau vao/nghiep vu vi nguoi dung yeu cau tru so luong khong hop le. Vi vay nen su dung `IllegalArgumentException` thay vi `IllegalStateException`.

### Truong hop cap nhat thanh cong nhung khong luu vao database

Sau khi tinh duoc `newStock`, code hien tai chi cap nhat gia tri tren object:

```java
product.setStockQuantity(newStock);
```

Nhung lai khong goi:

```java
productRepository.save(product);
```

Day la loi nghiem trong vi thay doi chi ton tai tren object trong bo nho, khong duoc ghi nhan vao co so du lieu. Ket qua la he thong co the tra ve so luong moi, nhung database van giu so luong cu, dan den ton kho bi sai lech.

## 3. Co bao nhieu loi logic trong `updateStock`?

Co **2 loi logic chinh** trong phuong thuc `updateStock`:

1. Su dung sai loai exception khi so luong ton kho sau cap nhat bi am: nen dung `IllegalArgumentException` cho loi nghiep vu/dau vao khong hop le.
2. Quen goi `productRepository.save(product)` sau khi cap nhat `stockQuantity`, lam thay doi khong duoc ghi vao database.

Luu y: Truong hop san pham khong ton tai da duoc xu ly dung bang cach nem `IllegalArgumentException`, nhung van can viet test de dam bao khong co thao tac `save` nao duoc goi trong truong hop loi nay.

## 4. Huong sua de dam bao dung nghiep vu

Phuong thuc `updateStock` nen duoc sua theo huong:

```java
public int updateStock(String productId, int quantityChange) {
    Optional<Product> productOpt = productRepository.findById(productId);
    if (productOpt.isEmpty()) {
        throw new IllegalArgumentException("Product not found with ID: " + productId);
    }

    Product product = productOpt.get();
    int currentStock = product.getStockQuantity();
    int newStock = currentStock + quantityChange;

    if (newStock < 0) {
        throw new IllegalArgumentException("Resulting stock would be negative");
    }

    product.setStockQuantity(newStock);
    productRepository.save(product);

    return newStock;
}
```
