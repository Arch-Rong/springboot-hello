package com.example.bookmanager.repository;

import com.example.bookmanager.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// 告诉 Spring「这是数据访问层」，Spring 会帮你创建和管理这个对象
@Repository 
public class BookRepository {
    private final Map<Long, Book> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Book save(Book book) {
        if(book.getId() == null) {
            Long id = idGenerator.getAndIncrement();
            book.setId(id);
        }
        store.put(id, book);
        return book;
    }

    public Book findById(Long id) {
        return store.get(id);
    }

    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    public Book update(Book book) {
        if (store.containsKey(book.getId())) {
            store.put(book.getId(), book);
            return book;
        }
        return null;
    }

    // 按照作者查询
    public List<Book> findByAuthor(String author) {
        return store.values().stream()
            .filter(book -> book.getAuthor().equals(author))
            .collect(Collectors.toList());
    }

    // 按照价格范围查询
    public List<Book> findByPriceRange(Double minPrice, Double maxPrice) {
        return store.values().stream()
            .filter(book -> book.getPrice() >= minPrice && book.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }
    
}