### AGENTS.MD 

Mini E-Ticaret Projesi için anayasa görevi gören, temel kuralların tanımlandığı yönetişim dosyasıdır. Genel kullanımda bu dosya daima tamamen okunmak zorundadır, tüm kurallar istisnasız uygulanmak zorundadır.

---

### 1) TEKNOLOJİ YIĞINI

- Backend: Spring Boot 3.X, Java 21, Maven

- Mobil: Flutter

- Database: PostgreSQL

- Diğer ortam teknolojileri `Docker` üzerinde çalıştırılmalıdır.


--- 

### 2) NASIL ÇALIŞILIR? (ZORUNLU İŞ AKIŞI)

##### 2.1) UYDURMA YOK (NO INVENTING)

Eğer herhangi bir işlemde bilgi ya da referans eksikliği yaşarsan bu bilgiyi varsayım ya da uydurma ile elde etmen yasak. İlgili durumda işlemi durdur, kullanıcıya sor netleştirdikten sonra ilerle. 

#### 2.2) ÖNCE PLANLA, SONRA KODLA

Kod üretmeden önce şunları yapmak zorundasın;

- Bir dosya dökümü hazırla. (hangi dosyalar değişecek/eklenecek + neden)

- Eğer yeni varsa bir bağımlılık matrisi hazırla. (Ne, hangi versiyonla, neden eklendi?)

- Planı sun, onay almadan asla implementasyona başlama.

#### 2.3) KOD OLUŞTURMA STRATEJİSİ

Dosya oluşturma işlemleri bütünsel olarak ele alınamaz. Maksimum 5 dosyalık birbirleri ile ilişki gruplar halinde bölünmelidir. Her grup arası ek onay beklemek zorunludur.


---

### 3) ÇIKTI FORMATI (OUTPUT FORMAT)

Prompta özel çıktıların içerisinde aşağıdaki maddeleri de ekle.

1- Happy-Path Test

2- Dosya dökümü (ne değişti/eklendi ve neden)

## 4) CONTRACT-FIRST YAKLAŞIMI

### 4.1 OpenAPI doğruluğun tek kaynağıdır (Single Source of Truth)

- API sözleşmeleri şu dizinde bulunur: `/docs/openapi`

- Her servis **kendi OpenAPI dosyasına sahip olmalıdır**:
  - `docs/openapi/product-v1.yml`
  - `docs/openapi/order-v1.yml`

- Swagger UI, bu sözleşmelerden üretilir.

- Implementasyon **her zaman sözleşmeye uymalıdır**; sözleşme implementasyona göre değiştirilemez.

### 4.2 Kod Üretim Politikası (Code Generation Policy)

- OpenAPI araçları **yalnızca repoda zaten mevcutsa** kullanılabilir.

- Açık onay olmadan **yeni bir OpenAPI generator bağımlılığı eklenmemelidir**.

- Eğer generator yoksa, controller ve DTO’lar **spec’e uygun olacak şekilde manuel olarak implement edilmelidir**.

### 4.3 Swagger UI

- Tüm servisler Swagger implementasyonu yapmalıdır.
