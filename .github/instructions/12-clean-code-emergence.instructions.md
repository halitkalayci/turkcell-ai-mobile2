---
description: Clean Code Bölüm 12 — Ortaya Çıkan Tasarım; Kent Beck'in 4 Basit Tasarım Kuralı ve uygulaması.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 12: Ortaya Çıkan Tasarım

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 12: Emergence

---

## 1. Basit Tasarım Yoluyla Ortaya Çıkış

Martin, Kent Beck'in **4 Basit Tasarım Kuralı**nı "iyi tasarımın ortaya çıkmasını kolaylaştıran en etkili pratikler" olarak sunar.

---

## 2. Kent Beck'in 4 Basit Tasarım Kuralı

Bir tasarım aşağıdaki kurallara uyuyorsa **basit** sayılır (öncelik sırası önemlidir):

| # | Kural |
|---|---|
| 1 | Tüm testleri geçer |
| 2 | Tekrarı yoktur (DRY) |
| 3 | Programcı niyetini ifade eder |
| 4 | Sınıf ve metot sayısını minimize eder |

---

## 3. Kural 1: Tüm Testleri Geçer (Runs All the Tests)

Test geçen bir sistem **doğrulanabilir** bir sistemdir. Test yazmak tasarımı düşünmeye zorlar; test edilemeyen kod = tasarım hatası.

**Kural:** Testi olmayan kod production'a giremez.

```java
// Test edilemeyen tasarım sinyali: Doğrudan bağımlılık
public class ProductServiceImpl {
    private final ProductRepository repository = new ProductRepositoryImpl(); // test edilemez
}

// Test edilebilir tasarım: Enjekte edilebilir bağımlılık
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository; // mock edilebilir
}

// Test:
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock ProductRepository repository;
    @InjectMocks ProductServiceImpl service;

    @Test
    void getProductById_throwsNotFoundException_whenNotExists() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,
            () -> service.getProductById(UUID.randomUUID()));
    }
}
```

---

## 4. Kural 2: Tekrarsız (No Duplication)

Tekrarlama çabalar, hata riski ve karmaşıklık artışının kaynağıdır. Duplication yalnızca kopya-yapıştır kodu değil; **aynı işi yapan farklı kod parçaları** da dahildir.

```java
// YANLIŞ: Aynı eşleme mantığı iki yerde
public ProductResponse createProduct(ProductRequest request) {
    Product product = Product.builder()
        .name(request.getName())
        .price(request.getPrice())
        .stock(request.getStock())
        .sku(request.getSku())
        .build();
    return toResponse(productRepository.save(product));
}

public ProductResponse updateProduct(UUID id, ProductRequest request) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    product.setName(request.getName());
    product.setPrice(request.getPrice());
    // aynı mapping tekrar ettiriyor
    ...
}

// DOĞRU: Ortak mapper metotu
private Product applyRequest(Product product, ProductRequest request) {
    product.setName(request.getName());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setSku(request.getSku());
    return product;
}
```

### Template Method ile Duplikasyonu Gider

```java
public abstract class BaseValidator<T> {
    public final void validate(T request) {
        validateNotNull(request);
        validateSpecific(request);  // alt sınıf implemente eder
    }

    private void validateNotNull(T request) {
        if (request == null) throw new IllegalArgumentException("Request boş olamaz");
    }

    protected abstract void validateSpecific(T request);
}

public class ProductRequestValidator extends BaseValidator<ProductRequest> {
    @Override
    protected void validateSpecific(ProductRequest request) {
        if (request.getSku() == null || request.getSku().isBlank())
            throw new IllegalArgumentException("SKU boş olamaz");
    }
}
```

---

## 5. Kural 3: Niyeti İfade Et (Expressive)

Yazılımın bakım maliyetinin büyük kısmı okumaktan gelir. Kod yazarken isimler, küçük fonksiyonlar, standart kalıplar ve iyi testler okunabilirliği artırır.

**Kural:** Kodu yazar gibi değil, okuyucu için yaz.

```java
// YANLIŞ: Niyet gizli
public List<Product> f(boolean b, int t) {
    return productRepository.findAll().stream()
        .filter(p -> b ? p.getStock() > 0 : p.getStock() == t)
        .toList();
}

// DOĞRU: Niyet açık
public List<Product> findAvailableProducts() {
    return productRepository.findAll().stream()
        .filter(product -> product.getStock() > 0)
        .toList();
}

public List<Product> findProductsWithExactStock(int targetStock) {
    return productRepository.findAll().stream()
        .filter(product -> product.getStock() == targetStock)
        .toList();
}
```

**İfade araçları:**

| Araç | Örnek |
|---|---|
| İyi isimler | `assertSkuIsUnique()` |
| Küçük fonksiyonlar | `toEntity()`, `toResponse()` |
| Standart kalıp adları | `ProductServiceImpl`, `ProductRepository` |
| İyi yazılmış testler | `createProduct_throwsException_whenSkuDuplicate()` |

---

## 6. Kural 4: Sınıf ve Metot Sayısını Minimize Et (Minimal Classes and Methods)

SRP ve DRY adına aşırı soyutlama yapmak da zararlıdır. Her interface için ayrı bir sınıf, her şey için ayrı bir yardımcı — gereksiz karmaşıklık üretir.

**Kural:** Pragmatik ol. Soyutlama gerçek bir gereksinimi karşılıyorsa ekle; yoksa ekleme.

```java
// YANLIŞ: Gereksiz soyutlama — tek implementasyonlu interface anlamsız
public interface ProductNameFormatter {
    String format(String name);
}

public class ProductNameFormatterImpl implements ProductNameFormatter {
    @Override
    public String format(String name) {
        return name.trim().toLowerCase();
    }
}

// DOĞRU: Yeterince basit private metot
private String normalizeProductName(String name) {
    return name.trim().toLowerCase();
}
```

---

## 7. Kural Önceliği

Dört kural **öncelik sırasına göre uygulanır**:

```
1. Testler geçer  (doğruluk)
2. Tekrar yok    (DRY)
3. Niyet ifade   (okunabilirlik)
4. Minimal       (sadelik)

→ 4. kural, 2. ve 3. kurala asla kurban edilemez.
```

---

## 8. Özet — Bölüm 12 Temel Kurallar

| Kural | Özet |
|---|---|
| Testleri geçir | Testi olmayan kod production'a giremez |
| Tekrar yok | DRY: aynı mantık tek yerde |
| Niyeti ifade et | Okuyucu için yaz; isimler, küçük fonksiyonlar, testler |
| Minimal | Gereksiz soyutlama ve sınıf ekleme |
| Öncelik sırası | Test > DRY > İfade > Minimal |
