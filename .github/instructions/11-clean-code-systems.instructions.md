---
description: Clean Code Bölüm 11 — Sistemler; inşa ve kullanım ayrımı, bağımlılık enjeksiyonu, AOP ve DSL stratejileri.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 11: Sistemler

> **Kaynak:** Robert C. Martin — *Clean Code*, Bölüm 11: Systems

---

## 1. İnşa ile Kullanımı Ayır (Separate Constructing a System from Using It)

> **"Yazılım sistemleri, nesnelerin inşasını ve bitiştirme süreçlerini birbirinden ayırmalıdır."**

Bir nesne içinde başka bir nesne oluşturmak (sert bağımlılık) test edilebilirliği ve esnekliği yok eder.

```java
// YANLIŞ: Service kendi bağımlılığını oluşturuyor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl() {
        this.productRepository = new ProductRepositoryImpl(); // sert bağımlılık
    }
}

// DOĞRU: Spring IoC konteyneri bağımlılığı enjekte eder
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository; // dışarıdan enjekte edilir
}
```

---

## 2. Main Ayrımı (Separation of Main)

`main` metodu (ya da Spring Boot'ta `@SpringBootApplication`) tüm nesneleri inşa eder ve birbirine bağlar. Uygulama geri kalanı oluşturulmuş nesneleri yalnızca **kullanır**.

```java
// DOĞRU: Spring Boot'ta bu iş IoC konteynerinin sorumluluğu
@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args); // inşa burada
    }
}

// Servis katmanı yalnızca kullanır, inşa etmez
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository; // hazır gelir
    private final StockService stockService;
}
```

---

## 3. Fabrika Yöntemi (Factory Pattern)

Nesnenin *ne zaman* oluşturulacağını kontrol etmek gerektiğinde Abstract Factory kullanılır; ancak *nasıl* oluşturulduğu detayı iş kodundan gizlenir.

```java
// Senaryo: Ürün tipi için farklı fiyatlandırma stratejisi oluşturma

public interface PricingStrategy {
    BigDecimal applyDiscount(BigDecimal price);
}

public interface PricingStrategyFactory {
    PricingStrategy create(String productType);
}

@Component
public class DefaultPricingStrategyFactory implements PricingStrategyFactory {
    @Override
    public PricingStrategy create(String productType) {
        return switch (productType) {
            case "ELECTRONICS" -> new ElectronicsDiscountStrategy();
            case "CLOTHING"    -> new ClothingDiscountStrategy();
            default            -> new NoDiscountStrategy();
        };
    }
}

// İş kodu: ne oluşturulduğunu bilmez
@Service
@RequiredArgsConstructor
public class ProductPricingService {
    private final PricingStrategyFactory pricingStrategyFactory;

    public BigDecimal calculateFinalPrice(Product product) {
        return pricingStrategyFactory
                .create(product.getType())
                .applyDiscount(product.getPrice());
    }
}
```

---

## 4. Bağımlılık Enjeksiyonu (Dependency Injection)

Kontrolün tersine çevrilmesi (IoC) bağımlılık yönetimini nesnenin kendisinden alır ve bir dış mekanizmaya devreder.

**Kural:**
- Constructor injection tercih edilir (immutable, test dostu).
- `@Autowired` field injection üretim kodunda kullanılmaz.
- Çok sayıda bağımlılık (5+) SRP ihlali sinyalidir; sınıfı böl.

```java
// YANLIŞ: Field injection — test edilemez, immutable değil
@Service
public class ProductServiceImpl {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;
}

// DOĞRU: Constructor injection (Lombok @RequiredArgsConstructor ile)
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StockService stockService;
}
```

---

## 5. Java Proxileri ve Cross-Cutting Concerns

Loglama, güvenlik ve transaction yönetimi gibi kesişen kaygılar (cross-cutting concerns) iş koduna gömülmemeli; Spring AOP ile ayrıştırılmalıdır.

```java
// YANLIŞ: Loglama iş koduna gömülü
@Service
public class ProductServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductResponse createProduct(ProductRequest request) {
        log.info("createProduct çağrıldı, sku: {}", request.getSku()); // iş dışı kaygı
        assertSkuIsUnique(request.getSku());
        Product saved = productRepository.save(toEntity(request));
        log.info("Ürün oluşturuldu, id: {}", saved.getId()); // iş dışı kaygı
        return toResponse(saved);
    }
}

// DOĞRU: AOP Aspect ile loglama iş kodundan ayrışıyor
@Aspect
@Component
public class ServiceLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("execution(* com.turkcell.ecommerce.*.service.*.*(..))")
    public Object logServiceCall(ProceedingJoinPoint pjp) throws Throwable {
        log.info("{} çağrıldı", pjp.getSignature().getName());
        Object result = pjp.proceed();
        log.info("{} tamamlandı", pjp.getSignature().getName());
        return result;
    }
}
```

---

## 6. Sistemi Test Edilebilir Tut (Test Drive the System Architecture)

Mimari kararlar ertelenerek akıtılabilir (invasive olmayan) bir yapı kurulursa sistem test edilebilir kalır. POJO tabanlı bir mimari, framework karar noktalarını sınırda tutar.

```java
// DOĞRU: Service POJO — Spring annotation'ları minimum, business logic test edilebilir
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        assertSkuIsUnique(request.getSku());
        return toResponse(productRepository.save(toEntity(request)));
    }
    // ... tamamen @Transactional gibi detaylardan bağımsız test edilebilir
}

// Test: Spring konteyneri başlamadan mock ile
class ProductServiceImplTest {
    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void createProduct_throwsException_whenSkuDuplicate() { ... }
}
```

---

## 7. Kararları Erteleme Sanatı (Postponing Decisions)

> **"İyi mühendislik, bilinçli geciktirilmiş kararlarla karakterize edilir."**

Bugün verilen erken kararlar yarın değişmek zorunda kalabilir. Kararı vermek için gereken bilgiye sahip olana kadar bekle.

**Kural:**
- Veritabanı seçimi mümkün olduğunca geç yapılır (H2 → PostgreSQL geçişi sorunsuz olmalı).
- Servis sözleşmesi (OpenAPI) implementasyondan önce kararlı tutulur.
- Interface'ler somut kararları geciktirir.

---

## 8. Özet — Bölüm 11 Temel Kurallar

| Kural | Açıklama |
|---|---|
| İnşa ≠ Kullanım | Nesne oluşturma ve kullanma karışmaz |
| IoC | Bağımlılık dışarıdan enjekte edilir |
| Constructor injection | Field injection'a tercih edilir |
| 5+ bağımlılık sinyali | SRP ihlali; sınıfı böl |
| Cross-cutting concern | AOP ile iş kodundan ayrıştır |
| POJO service | Framework bağımlılığı minimumda tut |
| Kararı ertele | Erken karar = erken kırılganlık |
