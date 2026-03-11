---
description: Clean Code Bölüm 9 — Unit Testler; TDD yasaları, F.I.R.S.T. ilkeleri, temiz test yazımı ve tek assert prensibi.
applyTo: "**/*Test.java"
---

# Clean Code — Bölüm 9: Unit Testler

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 9: Unit Tests

---

## 1. TDD'nin Üç Yasası (The Three Laws of TDD)

1. **Başarısız bir test olmadan üretim kodu yazamazsın.**
2. **Derlenemeyen ya da başarısız olan testten fazlasını yazamazsın.**
3. **Başarısız bir testi geçirecek kadardan fazla üretim kodu yazamazsın.**

Bu üç yasa; testi ve kodu neredeyse aynı anda yazmayı, her döngüyü yaklaşık 30 saniyede tamamlamayı gerektirir.

---

## 2. Temiz Test = Okunaklık (Clean Tests)

Test kodunun kalitesi üretim kodunun kalitesi kadar önemlidir. Kirli testler üretim kodunu değiştirmeyi zorlaştırır; zamanla silinir ve güvensiz kalırsın.

**Kural:** Test kodu birinci sınıf vatandaştır. Her refactor döngüsünde test kodu da temizlenir.

```java
// YANLIŞ: Anlamsız değişken adları ve tekrarlayan setup
@Test
public void t1() {
    Product p = new Product();
    p.setName("x");
    p.setPrice(new BigDecimal("10"));
    p.setStock(5);
    p.setSku("SKU1");
    productRepository.save(p);
    Optional<Product> result = productRepository.findBySku("SKU1");
    assertTrue(result.isPresent());
    assertEquals("x", result.get().getName());
}

// DOĞRU: Okunabilir, niyeti açık test
@Test
public void findBySku_returnsProduct_whenSkuExists() {
    // Arrange
    Product savedProduct = productRepository.save(aProduct().withSku("SKU-001").build());

    // Act
    Optional<Product> found = productRepository.findBySku("SKU-001");

    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getSku()).isEqualTo(savedProduct.getSku());
}
```

---

## 3. Bilinç Yaratan Kodu Ön Plana Çıkar (Build a Testing API)

Test'in amacı, üretim kodunu ne kadar verimli çalıştırdığını değil, okuyanın niyeti hemen anlayabilmesini sağlamaktır. Build metodları ve yardımcı fabrikalar testi sadeleştirir.

```java
// Test Builder — tekrarlayan setup'ı gizler
public class ProductTestBuilder {
    private String name = "Test Ürün";
    private BigDecimal price = new BigDecimal("99.99");
    private int stock = 10;
    private String sku = "DEFAULT-SKU";

    public static ProductTestBuilder aProduct() {
        return new ProductTestBuilder();
    }

    public ProductTestBuilder withSku(String sku) {
        this.sku = sku;
        return this;
    }

    public ProductTestBuilder withStock(int stock) {
        this.stock = stock;
        return this;
    }

    public ProductTestBuilder outOfStock() {
        this.stock = 0;
        return this;
    }

    public Product build() {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setSku(sku);
        return product;
    }
}
```

---

## 4. Test Başına Tek Kavram (One Concept per Test)

Her test yalnızca bir kavramı doğrulamalıdır. Yan yana iki farklı davranışı tek testte birleştirme.

```java
// YANLIŞ: Tek testte birden fazla kavram
@Test
public void createAndUpdateProduct() {
    ProductResponse created = productService.createProduct(aRequest().build());
    assertThat(created.getId()).isNotNull();

    ProductRequest updateReq = aRequest().withPrice(new BigDecimal("200")).build();
    ProductResponse updated = productService.updateProduct(created.getId(), updateReq);
    assertThat(updated.getPrice()).isEqualByComparingTo("200");
}

// DOĞRU: Ayrı testler
@Test
public void createProduct_assignsId() {
    ProductResponse response = productService.createProduct(aRequest().build());
    assertThat(response.getId()).isNotNull();
}

@Test
public void updateProduct_changesPrice() {
    ProductResponse created = productService.createProduct(aRequest().build());
    ProductResponse updated = productService.updateProduct(
            created.getId(), aRequest().withPrice(new BigDecimal("200")).build()
    );
    assertThat(updated.getPrice()).isEqualByComparingTo("200");
}
```

---

## 5. F.I.R.S.T. İlkeleri

| Harf | İlke | Açıklama |
|---|---|---|
| **F** | Fast (Hızlı) | Testler saniyeler içinde çalışmalıdır; yavaş testler çalıştırılmaz |
| **I** | Independent (Bağımsız) | Testler birbirine bağlı olmamalı; herhangi sırada çalışabilmeli |
| **R** | Repeatable (Tekrarlanabilir) | Her ortamda (CI, lokal, prod) aynı sonucu vermelidir |
| **S** | Self-Validating (Kendi Kendini Doğrulayan) | Test ya geçer ya başarısız olur; insan yorumu gerekmez |
| **T** | Timely (Zamanında) | Test, üretim kodundan önce ya da hemen sonra yazılır |

### 5.1 Fast — H2 ile İzolasyon

```java
// Yavaş test: Gerçek veritabanı bağlantısı
// Hızlı test: H2 in-memory ile
@DataJpaTest // H2 kullanır, Slice test — hızlı
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void existsBySku_returnTrue_whenSkuExists() {
        productRepository.save(aProduct().withSku("FAST-SKU").build());
        assertThat(productRepository.existsBySku("FAST-SKU")).isTrue();
    }
}
```

### 5.2 Independent — Paylaşılan State Yasak

```java
// YANLIŞ: Testler paylaşılan state'e bağımlı
private static UUID sharedProductId;

@Test
@Order(1)
public void firstCreateProduct() {
    sharedProductId = productService.createProduct(...).getId();
}

@Test
@Order(2)
public void thenDeleteProduct() {
    productService.deleteProduct(sharedProductId); // önceki teste bağımlı
}

// DOĞRU: Her test kendi datasını oluşturur
@Test
public void deleteProduct_removesIt() {
    UUID id = productService.createProduct(aRequest().build()).getId();
    productService.deleteProduct(id);
    assertThat(productRepository.existsById(id)).isFalse();
}
```

### 5.3 Self-Validating — Assert Zorunlu

```java
// YANLIŞ: Assert yok; test her zaman geçer ama bir şey doğrulamıyor
@Test
public void createProduct_doesNotThrow() {
    productService.createProduct(aRequest().build());
    // assert yok!
}

// DOĞRU
@Test
public void createProduct_returnsResponseWithId() {
    ProductResponse response = productService.createProduct(aRequest().build());
    assertThat(response.getId()).isNotNull();
    assertThat(response.getName()).isEqualTo("Test Ürün");
}
```

---

## 6. Test İsimlendirmesi

**Kural:** `methodName_expectedBehavior_condition` formatı kullanılır.

```java
// YANLIŞ
@Test public void test1() { ... }
@Test public void productTest() { ... }

// DOĞRU
@Test public void createProduct_throwsSkuAlreadyExistsException_whenSkuIsDuplicate() { ... }
@Test public void getProductById_returnsProduct_whenIdExists() { ... }
@Test public void getProductById_throwsProductNotFoundException_whenIdNotFound() { ... }
@Test public void getProductsByCategory_returnsEmptyList_whenNoCategoryMatch() { ... }
```

---

## 7. Exception Testi

```java
// DOĞRU: assertThrows ile exception mesajı da doğrulanabilir
@Test
public void createProduct_throwsSkuAlreadyExistsException_whenSkuIsDuplicate() {
    productService.createProduct(aRequest().withSku("DUPLICATE").build());

    SkuAlreadyExistsException exception = assertThrows(
        SkuAlreadyExistsException.class,
        () -> productService.createProduct(aRequest().withSku("DUPLICATE").build())
    );

    assertThat(exception.getMessage()).contains("DUPLICATE");
}
```

---

## 8. Özet — Bölüm 9 Temel Kurallar

| Kural | Açıklama |
|---|---|
| TDD 3 yasa | Önce başarısız test; sonra onu geçiren kod |
| Test = 1. sınıf | Test kodu da temizlenir, refactor edilir |
| Tek kavram per test | Bir test, bir davranış |
| F.I.R.S.T. | Hızlı, bağımsız, tekrarlanabilir, kendi doğrulayan, zamanında |
| İsimlendirme | `method_expected_condition` formatı |
| Builder kullan | Test setup için okunabilir factory metodları |
| Assert zorunlu | Assertsiz test değersizdir |
| Paylaşılan state yasak | Her test kendi datasını oluşturur |
