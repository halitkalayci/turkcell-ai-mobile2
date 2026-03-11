---
description: Clean Code Bölüm 10 — Sınıflar; SRP, OCP, yüksek uyum, küçük sınıf ve değişime hazır organizasyon.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 10: Sınıflar

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 10: Classes

---

## 1. Sınıf Organizasyonu (Class Organization)

Java'da sınıf şu sırayla organize edilir (gazete metaforu):

1. `public static final` sabitler
2. `private static` değişkenler
3. `private` instance değişkenler
4. `public` constructor(lar)
5. `public` metodlar
6. Her `public` metodun hemen altında çağırdığı `private` yardımcı metodlar

```java
// DOĞRU: Sıralı sınıf organizasyonu
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.18"); // (1) sabit

    private final ProductRepository productRepository;                           // (3) instance field

    @Override
    public ProductResponse createProduct(ProductRequest request) {              // (5) public metot
        assertSkuIsUnique(request.getSku());
        return toResponse(productRepository.save(toEntity(request)));
    }

    private void assertSkuIsUnique(String sku) { ... }                         // (6) private yardımcı

    private Product toEntity(ProductRequest request) { ... }                   // (6) private yardımcı

    private ProductResponse toResponse(Product product) { ... }               // (6) private yardımcı
}
```

---

## 2. Küçük Sınıflar (Classes Should Be Small)

Fonksiyonlarda satır sayısını ölçerken sınıflarda **sorumluluğu** ölçeriz.

**Kural:** Sınıfın sorumluluğunu tek cümleyle ifade edebilmelisin. "Ve", "ya da", "ancak" gibi bağlaçlar birden fazla sorumluluk olduğuna işaret eder.

```java
// YANLIŞ: Çok fazla sorumluluk
public class ProductManager {
    public ProductResponse createProduct(ProductRequest request) { ... }
    public void sendProductCreatedEmail(Product product) { ... }   // bildirim sorumluluğu
    public void exportProductToCsv(List<Product> products) { ... } // export sorumluluğu
    public BigDecimal calculateTax(BigDecimal price) { ... }       // vergi hesaplama
}

// DOĞRU: Her sınıf tek sorumluluk
@Service public class ProductService { ... }
@Service public class ProductNotificationService { ... }
@Component public class ProductExportService { ... }
@Component public class TaxCalculator { ... }
```

---

## 3. Tek Sorumluluk İlkesi (Single Responsibility Principle — SRP)

> **"Bir sınıfın değişmesi için tek bir neden olmalıdır."**

Değişim nedeni = sorumluluk. İki farklı neden değişim gerektiriyorsa sınıfta iki sorumluluk var demektir.

```java
// YANLIŞ: ProductServiceImpl hem iş mantığı hem stok yönetimi hem bildirim içeriyor
@Service
public class ProductServiceImpl {
    public ProductResponse createProduct(ProductRequest request) {
        // iş mantığı
        Product product = productRepository.save(toEntity(request));
        // stok güncelleme
        inventoryClient.registerProduct(product.getId(), request.getStock());
        // email bildirimi
        emailService.sendNewProductAlert(product.getName());
        return toResponse(product);
    }
}

// DOĞRU: Sorumluluk şeması
// ProductServiceImpl  → ürün CRUD iş mantığı
// InventoryService    → stok işlemleri
// NotificationService → bildirim mekanizması
// ProductFacade       → koordinasyon (opsiyonel)
```

---

## 4. Uyum (Cohesion)

Bir sınıfın metodları ne kadar çok instance değişkeni kullanıyorsa sınıf o kadar yüksek uyumludur.

**Kural:** Her metot sınıfın tüm instance değişkenlerini kullanmalıdır. Bir metot grup yalnızca bir alt küme değişkeni kullanıyorsa, bu değişkenler başka bir sınıfa taşınabilir.

```java
// YANLIŞ: Düşük uyum — printReport sadece reportPages kulllanıyor
public class SuperDashboard {
    public String getVersion() { ... }       // version kullanıyor
    public void printReport() {              // yalnızca reportPages kullanıyor
        System.out.println(reportPages);
    }
    private int version;
    private String reportPages;
}

// DOĞRU: Ayrı sınıflara böl
public class VersionManager { private int version; ... }
public class ReportPrinter { private String reportPages; ... }
```

---

## 5. Değişime Hazır Organizasyon (Organizing for Change)

Her değişiklik riski doğurur. Sistemi değişime izolasyonlu sınıflarla hazırlarız.

**Kural:** Yeni davranış eklenmesi mevcut sınıfları değiştirmeden yeni sınıf eklemeyle yapılabilmelidir (OCP).

```java
// YANLIŞ: Yeni ürün tipinde SQL ekleniyor — mevcut sınıf değişiyor
public class ProductDao {
    public List<Product> findByType(String type) {
        if ("PHYSICAL".equals(type)) {
            return db.query("SELECT * FROM products WHERE type = 'PHYSICAL'");
        } else if ("DIGITAL".equals(type)) {
            return db.query("SELECT * FROM products WHERE type = 'DIGITAL'");
        }
        throw new IllegalArgumentException("Bilinmeyen tip: " + type);
    }
}

// DOĞRU: Yeni tip için yeni sınıf eklenir; mevcut değişmez
public interface ProductQuery {
    List<Product> execute();
}

public class PhysicalProductQuery implements ProductQuery {
    @Override
    public List<Product> execute() {
        return db.query("SELECT * FROM products WHERE type = 'PHYSICAL'");
    }
}

public class DigitalProductQuery implements ProductQuery {
    @Override
    public List<Product> execute() {
        return db.query("SELECT * FROM products WHERE type = 'DIGITAL'");
    }
}
```

---

## 6. Değişim İçin İzolasyon (Isolating from Change)

Somut implementasyona bağımlı olmak, teslim olduğumuz değişime karşı bizi kırılgan yapar. Arayüz ve soyut sınıflara bağımlı olmak bu kırılganlığı azaltır. Bu aynı zamanda DIP (Dependency Inversion Principle) temelidir.

```java
// YANLIŞ: Somut repository'ye doğrudan bağımlılık
public class ProductServiceImpl {
    private final ProductRepositoryImpl repository; // somut impl
}

// DOĞRU: Arayüze bağımlı; impl değişince servis etkilenmiyor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository; // arayüz
}

// Test'te stub enjekte edilebilir
public class ProductServiceImplTest {
    @Mock
    private ProductRepository repository; // mock işe yarıyor çünkü arayüz

    @InjectMocks
    private ProductServiceImpl productService;
}
```

---

## 7. Çok Küçük Sınıf Kaygısı

"Binlerce küçük sınıf sistemi anlamayı zorlaştırmaz mı?" sorusu sıkça sorulur.

**Cevap:** Büyük sınıflarda da anlamak zorunda olduğun değişken ve metot sayısı aynıdır. Küçük sınıflar bu karmaşıklığı organize paketlere, anlamlı isimlere ve net sözleşmelere dönüştürür.

Karşılaştırma:
- Büyük araç kutusu: Her şey tek kutuda, arama uzun.
- Küçük kutular, kategorize: İstediğini hemen bulursun.

---

## 8. Özet — Bölüm 10 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Sınıf organizasyonu | Sabitler → fields → ctor → public → private sırası |
| Küçük sınıf | Sorumluluk tek cümleyle anlatılır; bağlaç yoktur |
| SRP | Değişim için tek neden |
| Yüksek uyum | Metodlar sınıfın değişkenlerini kullanır |
| OCP hazırlığı | Yeni davranış = yeni sınıf; mevcut değişmez |
| DIP | Arayüze bağımlı ol; implementasyona değil |
| Sınıf başına tek sorumluluk | "Ve/ya da" sinyali SRP ihlaline işaret eder |
