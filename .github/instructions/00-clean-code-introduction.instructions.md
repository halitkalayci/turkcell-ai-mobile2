---
description: Clean Code Bölüm 1 — Clean Code nedir, teknik borç, Boy Scout Kuralı ve temiz kod yazılımcısının tutumu.
applyTo: "**/*.java"
---

# Clean Code — Bölüm 1: Giriş

> **Kaynak:** Robert C. Martin — *Clean Code: A Handbook of Agile Software Craftsmanship* (2008), Bölüm 1: Clean Code

---

## 1. Temiz Kod Neden Önemlidir?

Kötü kod yazmak kısa vadede hız kazandırıyor gibi görünür; uzun vadede ekibi yavaşlatır, değişikliği imkânsız kılar ve projeyi öldürür. Martin buna **"the wading"** der: çamurda yürümek.

**Kural:** Kodu yavaş yazmak, kodu hızlı yazmaktan daha hızlıdır. Temiz kod yazmak bir lüks değil, profesyonel sorumluluktur.

### 1.1 Teknik Borç (Technical Debt)

- Her birikmiş kötü kod, gelecekteki her değişikliğin maliyetini artırır.
- Ekip büyüdükçe fakat verimlilik sıfıra yaklaştıkça yönetim daha fazla eleman ekler — bu durum teknik borcu daha da artırır.
- "Sonra temizlerim" = temizlemeyeceksin. **LeBlanc Yasası:** *Later equals never.*

```java
// YANLIŞ: "Şimdilik bu olsun, sonra düzeltirim"
public List<Object[]> getP() {  // p ne?
    List<Object[]> list1 = new ArrayList<>();
    for (Object[] x : theList)
        if ((int) x[0] == 4) list1.add(x);
    return list1;
}

// DOĞRU: Sonradan değil şimdi
public List<Cell> getFlaggedCells() {
    List<Cell> flaggedCells = new ArrayList<>();
    for (Cell cell : gameBoard)
        if (cell.isFlagged())
            flaggedCells.add(cell);
    return flaggedCells;
}
```

---

## 2. Temiz Kod Nedir?

Farklı ustalar farklı tanımlar verir, ancak ortak noktalarda buluşurlar:

### Bjarne Stroustrup (C++ yaratıcısı)
> "Temiz kod bir şeyi iyi yapar. Hata yönetimi eksiksizdir. Performans optimale yakındır. Temiz kod odaklanmış olmalıdır."

**Kural:** Her fonksiyon, her sınıf, her modül **tek bir şeye** odaklanmalıdır.

### Grady Booch
> "Temiz kod basit ve doğrudan okunur. Tasarım niyetini gizlemez."

**Kural:** Kod, iyi yazılmış düzyazı gibi okunmalıdır.

### Dave Thomas
> "Temiz kod, orijinal yazarı dışında başka geliştiriciler tarafından da geliştirilebilir ve değiştirilebilir."

**Kural:** Kodu yalnızca derleyici için değil, bir sonraki okuyucu için yaz.

### Michael Feathers
> "Temiz koda bakınca, üzerinde çok çalışıldığını hissedersin."

**Kural:** Temiz kod, özen gösterildiğini belli eder. Hiçbir şey kolayca görmezden gelinmemiştir.

### Ron Jeffries (Extreme Programming kuralları)
- Tüm testler geçer.
- Çoğaltma (duplication) yoktur.
- Sistemdeki her tasarım fikri açıkça ifade edilmiştir.
- Sınıf ve metot sayısı minimumda tutulmuştur.

---

## 3. Kötü Kod Yazan Kim?

Martin'e göre suç proje yöneticisine yüklenmez; temiz kod yazmak **programcının profesyonel sorumluluğudur**.

**Kural:** Zaman baskısı temiz kod yazmamak için geçerli bir özür değildir. Hızlı ilerlemek istiyorsan temiz yaz.

```
"Kötü kodu okumak için harcanan süre, iyi kod yazarak kazanılan süreden
çok daha fazladır."
— Robert C. Martin
```

---

## 4. İzci Kuralı (The Boy Scout Rule)

> **"Kamp alanını bulduğundan daha temiz bırak."**

Her commit'te, dokunduğun kodu biraz daha temiz hale getir. Bu; bir değişken ismini düzeltmek, uzun bir fonksiyonu bölmek, bir tekrarı kaldırmak ya da bir `if` zincirini sadeleştirmek olabilir.

**Kural:** Her PR'da dokunduğun kod alanını daha temiz bırak. Büyük refactor gerekmez; küçük iyileştirmeler birikir.

```java
// Üzerinde çalıştığın metoda dokunurken buldğun belirsiz değişken ismini düzelt
// ÖNCE
public void calc(int x) { ... }

// SONRA (aynı PR içinde düzelt)
public void calculateDiscount(int quantity) { ... }
```

---

## 5. Tıkanan Büyük Yeniden Tasarım (The Grand Redesign in the Sky)

Ekipler zaman zaman kötü kodu tamamen atıp sıfırdan yazmak ister. Ancak:

- Yeni sistem eski sistemi kapsayacak kadar büyür.
- Eski sistemin hataları yenisine taşınır.
- Eski sistem değişmeye devam eder; yeni sistem hep onun peşinden koşar.
- Bu süreç yıllarca sürer ve genellikle başarısız olur.

**Kural:** Büyük yeniden yazım yerine, sürekli küçük iyileştirme (Boy Scout Rule) uygula.

---

## 6. Sanat Olarak Temiz Kod

Temiz kodu tanımak ile temiz kod yazabilmek farklı becerilerdir. Kötü kodu gören ama nasıl düzelteceğini bilmeyen programcı, kodun kötü olduğunu hisseder ancak dönüştüremiyor demektir.

Temiz kod yazmak öğrenilmiş bir **el sanatıdır** (craft). Binlerce küçük karar birikiminden oluşur:
- Hangi ismi seç?
- Bu fonksiyon çok mu uzun?
- Bu yorum gerekli mi?
- Bu bağımlılığı inject mi etsem?

**Kural:** Her küçük kararı bilinçli ver. "Yeterince iyi" diye geçiştirme.

---

## 7. Özet — Bölüm 1 Temel Kurallar

| Kural | Açıklama |
|---|---|
| Tek sorumluluk | Her birim bir şey yapar |
| Okunabilirlik | Kod iyi düzyazı gibi okunur |
| Çoğaltmasızlık | DRY — Don't Repeat Yourself |
| Kapsam | Tüm testler geçer |
| Minimalite | Gereksiz sınıf/metot eklenmez |
| Boy Scout | Dokunduğun kodu daha temiz bırak |
| Profesyonellik | Zaman baskısı temiz kod yazmamak için özür değildir |
