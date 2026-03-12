import 'dart:convert';
import 'package:http/http.dart' as http;
import '../model/product_model.dart';

class ProductService {
  // Web üzerinde test ederken backend localhost:8080'de çalıştığı varsayılır.
  static const String _baseUrl = 'http://localhost:8080/api/v1/products';

  Future<List<ProductModel>> fetchAll() async {
    final response = await http.get(Uri.parse(_baseUrl));

    if (response.statusCode != 200) {
      throw Exception('Ürünler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => ProductModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
