import 'dart:convert';
import 'package:http/http.dart' as http;
import '../model/product_model.dart';
import '../../../core/network/app_constants.dart';

class ProductService {
  Future<List<ProductModel>> fetchAll() async {
    final uri = Uri.parse('${AppConstants.baseUrl}${AppConstants.productsPath}');
    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception('Ürünler yüklenemedi: ${response.statusCode}');
    }

    final List<dynamic> body = jsonDecode(response.body) as List<dynamic>;
    return body
        .map((item) => ProductModel.fromJson(item as Map<String, dynamic>))
        .toList();
  }
}
