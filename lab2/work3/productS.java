// SRP 5 үндсэн үйлдэл хийж байна.
// KISS

public class ProductService {
    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        // бусад үйлдэлүүдийг event driven байдлаар хийнэ.
        eventPublisher.publish(new ProductsViewedEvent(category, products));
        return products;
    }
}

public class ProductEventPublisher {
    private final List<ProductEventHandler> handlers;

    public void publish(ProductsViewedEvent event) {
        handlers.forEach(h -> h.handle(event));
    }
}

public interface ProductEventHandler {
    void handle(ProductsViewedEvent event);
}

// Тус бүр өөрийн үүрэгтэй, бие даасан класс

// n + 1 problem бүх product уудыг хадгалахад ачаалал их орно???
// category гоор авж байгаа блхоор зөвхөн category count маягийн юм байж болно.
public class ViewCountHandler implements ProductEventHandler {
    for (Product p : products) {
        p.setViewCount(p.getViewCount() + 1);
    }
    productRepository.saveAll(products);
}

// анхнаасаа product.size ийг авдаг байх хргтэй. memory хрггүй үрж байна.
public class PopularityHandler implements ProductEventHandler {
    updatePopularProducts(category, products);

    private void updatePopularProducts(String category, List<Product> products) {
        PopularityData data = new PopularityData();
        data.setCategory(category);
        data.setProductCount(products.size());
        data.setLastAccessed(new Date());
        popularityRepository.save(data);
    }
}

// cache ашиглаж байгаа ч шалгалт ашиглалт байхгүй.
public class CacheHandler implements ProductEventHandler {
     cacheService.put("products_" + category, products);
}

// ok
public class SearchHistoryHandler implements ProductEventHandler {
    searchHistoryService.recordSearch(category, products.size());
}