public class ProductService {
    // "Энгийн" бүтээгдэхүүний жагсаалт авах функц
    public List<Product> getProductsByCategory(String category) {
        // 1. Database-аас татах
        List<Product> products = productRepository.findByCategory(category);
        // 2. "Үзсэн тоог нэмэх"
        for (Product p : products) {
            p.setViewCount(p.getViewCount() + 1);
        }
        productRepository.saveAll(products);
        // 3. "Мөн популяр бүтээгдэхүүн шинэчлэх"
        updatePopularProducts(category, products);
        // 4. "Cache-д хадгалах"
        cacheService.put("products_" + category, products);
        // 5. "Хайлтын түүхэнд нэмэх"
        searchHistoryService.recordSearch(category, products.size());
        return products;
    }
    private void updatePopularProducts(String category, List<Product> products) {
        PopularityData data = new PopularityData();
        data.setCategory(category);
        data.setProductCount(products.size());
        data.setLastAccessed(new Date());
        popularityRepository.save(data);
    }
}