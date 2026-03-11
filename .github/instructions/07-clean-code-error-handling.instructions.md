---
description: Clean Code Bölüm 7 — Hata Yönetimi; exception kullanımı, null politikası, unchecked exception ve wrap stratejisi.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 7: Hata Yönetimi

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 7: Error Handling

---

## 1. Hata Kodları Değil, Exception Kullan

Hata kodu döndürmek çağıranı anında kontrol etmeye zorlar ve asıl iş mantığını gürültüye gömer.

```java
// YANLIŞ: Hata kodu ile kontrol akışı
public int deleteProduct(UUID id) {
    if (!productRepository.existsById(id)) {
        return ERROR_NOT_FOUND;
    }
    productRepository.deleteById(id);
    return SUCCESS;
}

// Çağıran tarafta:
int result = productService.deleteProduct(id);
if (result == ERROR_NOT_FOUND) { ... }
else if (result == SUCCESS) { ... }

// DOĞRU: Exception fırlatmak yeterli
public void deleteProduct(UUID id) {
    if (!productRepository.existsById(id))
        throw new ProductNotFoundException(id);
    productRepository.deleteById(id);
}
```

---

## 2. Önce Try-Catch-Finally Yaz

Try bloğu bir işlem kapsamı tanımlar. Try içindeki herhangi bir noktada hata olabilir; catch durumu tutarlı bırakmalıdır. Bu nedenle exceptionlı kod için **önce try-catch çerçevesini** yaz, sonra içini doldur.

```java
// DOĞRU: İşlem çerçevesi önce kurulur
public List<ProductResponse> loadProductsFromFile(String path) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
        return reader.lines()
                .map(this::parseProductLine)
                .toList();
    } catch (IOException e) {
        throw new ProductFileReadException("Dosya okunamadı: " + path, e);
    }
}
```

---

## 3. Unchecked Exception Kullan

Checked exception, metot imzasını kirleten ve her katmanın aynı exception'ı declare etmesini zorlayan Açık-Kapalı Prensip (OCP) ihlalidir. Java 8+ projelerinde unchecked (`RuntimeException`) kullanımı tercih edilir.

```java
// YANLIŞ: Checked exception zinciri
public ProductResponse getProduct(UUID id) throws ProductNotFoundException { ... }
public ProductResponse getProductBySku(String sku) throws ProductNotFoundException { ... }
// Controller da throws ProductNotFoundException yazmak zorunda kalır

// DOĞRU: Unchecked exception
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID id) {
        super("Ürün bulunamadı, id: " + id);
    }
    public ProductNotFoundException(String sku) {
        super("Ürün bulunamadı, sku: " + sku);
    }
}

// İmzaları temiz kalır
public ProductResponse getProductById(UUID id) {
    return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

---

## 4. Exception ile Bağlam Sağla

Her exception kağıda dökülecek kadar açıklayıcı olmalıdır. Hata mesajı: **ne yapılmaya çalışıldığını**, **nerede başarısız olduğunu** ve **neden** başarısız olduğunu içermelidir.

```java
// YANLIŞ: Anlamsız mesaj
throw new RuntimeException("Hata");

// DOĞRU: Bağlamsal mesaj
throw new ProductNotFoundException(
    String.format("Ürün bulunamadı — id: %s, işlem: stok güncelleme", id)
);

// DOĞRU: Orijinal exception zinciri korunuyor
catch (DataAccessException e) {
    throw new ProductPersistenceException(
        "Ürün kaydedilemedi — sku: " + product.getSku(), e
    );
}
```

---

## 5. Çağıranın İhtiyacına Göre Exception Sınıflandır

3rd-party kütüphane hataları çoğu zaman tek bir wrapper exception ile sarılabilir. Çağıranın ihtiyaç duyduğu ayrım kadar sınıf oluştur; gereksiz hiyerarşi oluşturma.

```java
// YANLIŞ: Her 3rd-party exception ayrı ayrı yakalanıyor
try {
    inventoryService.updateStock(id, quantity);
} catch (NetworkException e) {
    logger.error(e);
} catch (TimeoutException e) {
    logger.error(e);
} catch (ServiceUnavailableException e) {
    logger.error(e);
}

// DOĞRU: Wrapper ile tek noktaya indir
public class InventoryAdapter {
    private final InventoryService inventoryService;

    public void updateStock(UUID id, int quantity) {
        try {
            inventoryService.updateStock(id, quantity);
        } catch (NetworkException | TimeoutException | ServiceUnavailableException e) {
            throw new InventoryUnavailableException("Stok servisi ulaşılamaz", e);
        }
    }
}
```

---

## 6. Özel Durum Nesnesi ile Akış Kontrolü (Special Case Pattern)

Boş durum ya da hata durumu için exception fırlatmak yerine, özel bir nesne döndürmek akışı basitleştirebilir (Fowler'ın Null Object / Special Case Pattern).

```java
// YANLIŞ: Exception ile akış kontrolü
try {
    MealExpenses expenses = expenseReportDAO.getMeals(employee.getId());
    total += expenses.getTotal();
} catch (MealExpensesNotFound e) {
    total += getMealPerDiem();
}

// DOĞRU: Special Case nesnesi
MealExpenses expenses = expenseReportDAO.getMeals(employee.getId());
total += expenses.getTotal(); // PerDiemMealExpenses getTotal() -> perDiem döner
```

Proje örneği:

```java
// YANLIŞ: Controller'da null kontrolü
ProductResponse product = productService.findProductBySku(sku);
if (product == null) {
    return ResponseEntity.notFound().build();
}
return ResponseEntity.ok(product);

// DOĞRU: Service exception fırlatır; GlobalExceptionHandler yakalar
public ProductResponse getProductBySku(String sku) {
    return productRepository.findBySku(sku)
            .map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(sku));
}
```

---

## 7. Null Döndürme (Don't Return Null)

`null` döndürmek çağıranı null kontrolüne zorlar. Bir tane kontrolün atlanması `NullPointerException`'a yol açar.

**Kural:** Koleksiyon döndüren metodlar `null` değil, **boş koleksiyon** döndürür. Tek nesne döndüren metodlar `Optional` ya da exception kullanır.

```java
// YANLIŞ: null döndür
public List<ProductResponse> getProductsByCategory(String category) {
    List<Product> products = productRepository.findByCategory(category);
    if (products.isEmpty()) return null; // NullPointerException kapısı
    return products.stream().map(this::toResponse).toList();
}

// YANLIŞ: null kontrolü yükü çağıranda
List<ProductResponse> products = productService.getProductsByCategory("electronics");
if (products != null) {
    for (ProductResponse p : products) { ... } // null kontrolü unutulabilir
}

// DOĞRU: Boş liste döndür
public List<ProductResponse> getProductsByCategory(String category) {
    return productRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .toList(); // boş liste döner, null asla
}
```

---

## 8. Null Geçirme (Don't Pass Null)

Metodlara null parametre geçirmek, metodun içine `null` kontrolü yükler ya da NPE'ye yol açar.

**Kural:** API sınırına gelen null'u reddet; iç metodlara null geçirme.

```java
// YANLIŞ: null geçirilebilir
public ProductResponse getProductById(UUID id) {
    if (id == null) throw new IllegalArgumentException("id null olamaz");
    return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
}

// DOĞRU: @NonNull / @NotNull ile API sınırında ilan et (Bean Validation)
public ProductResponse getProductById(@NotNull UUID id) {
    return productRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

---

## 9. Merkezi Hata Yönetimi (GlobalExceptionHandler)

Spring Boot projelerinde tüm exceptionlar `@RestControllerAdvice` ile merkezi olarak yakalanır.

```java
// DOĞRU: Proje GlobalExceptionHandler yapısı
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SkuAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSkuConflict(SkuAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Beklenmeyen bir hata oluştu"));
    }
}
```

---

## 10. Özet — Bölüm 7 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Exception > hata kodu | Hata kodu akışı karmaşıklaştırır |
| Unchecked exception | İmzaları temiz tutar; OCP korunur |
| Bağlamsal mesaj | Ne, nerede, neden — loglanabilir olmalı |
| Wrapper pattern | 3rd-party exceptionları tek noktada sar |
| Null döndürme yasak | Koleksiyonlarda boş liste; tekil için Optional/exception |
| Null geçirme yasak | API sınırında @NotNull; iç metodlara null girmesin |
| GlobalExceptionHandler | Merkezi yakalama; controller'da try-catch yok |
