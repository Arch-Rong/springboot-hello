package com.example.bookmanager.service.impl;

import com.example.bookmanager.model.Book;
import com.example.bookmanager.repository.BookRepository;
import com.example.bookmanager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book createBook(Book book) {
        // 业务校验：价格不能为负数
        if (book.getPrice() == null || book.getPrice() < 0) {
            throw new IllegalArgumentException("图书价格不能为空或为负数");
        }
        // 业务校验：库存不能为负数
        if (book.getStock() == null || book.getStock() < 0) {
            throw new IllegalArgumentException("图书库存不能为空或为负数");
        }
        return bookRepository.save(book);
    }

    @Override
    public Book getBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book updateBook(Long id, Book book) {
        if (book.getId() == null) {
            throw new IllegalArgumentException("更新图书时 ID 不能为空");
        }
        Book existing = bookRepository.findById(book.getId());
        if (existing == null) {
            throw new RuntimeException("要更新的图书不存在，ID: " + book.getId());
        }
        return bookRepository.update(book);
    }


    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        bookRepository.delete(book);
    }

    @Override
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    public List<Book> getBooksByPriceRange(Double minPrice, Double maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }
}