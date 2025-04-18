package redlib.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redlib.backend.dao.BooksMapper;
import redlib.backend.dao.RecordingsMapper;
import redlib.backend.dto.BookDTO;
import redlib.backend.model.Books;
import redlib.backend.service.BookService;
import redlib.backend.vo.BookVO;
import java.lang.RuntimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BooksMapper booksMapper;
    private final RecordingsMapper recordingsMapper;

    @Override
    @Transactional
    public BookDTO createBook(BookDTO bookDTO, String currentUser) {
        validateBook(bookDTO);
        bookDTO.setAddedAt(LocalDate.now());
        bookDTO.setUpdatedAt(LocalDate.now());
        bookDTO.setUpdatedBy(currentUser);

        Books book = convertToEntity(bookDTO);

        if(booksMapper.insert(book) == 0) {
            throw new RuntimeException("书籍创建失败");
        }
        return convertToDto(book);
    }

    @Override
    @Transactional
    public BookDTO updateBook(String isbn, BookDTO bookDTO, String currentUser) {
        Books existingBook = booksMapper.selectByPrimaryKey(isbn);
        Integer borrowedNumber = booksMapper.checkBorrowedNumber(isbn);
        if(existingBook == null) {
            throw new RuntimeException("书籍不存在"+isbn);
        }
        Books updatedBook = mergeUpdateFields(existingBook, bookDTO);
        updatedBook.setUpdatedAt(LocalDate.now());
        updatedBook.setUpdatedBy(currentUser);
        updatedBook.setBorrowedNumber(borrowedNumber);

        if(booksMapper.updateByPrimaryKeySelective(updatedBook) == 0) {
            throw new RuntimeException("书籍更新失败，可能已被其他用户修改");
        }

        return convertToDto(updatedBook);
    }

    @Override
    public List<BookVO> listBooks(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new RuntimeException("分页参数非法");
        }
        int offset = page * size;
        return booksMapper.list(offset, size).stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    public void validateBook(BookDTO bookDTO) {
        // ISBN verification
        if (bookDTO.getIsbn().length() < 10 || bookDTO.getIsbn().length() > 13) {
            throw new RuntimeException("ISBN长度必须为10-13位");
        }

        //Book title non-empty
        if (bookDTO.getTitle() == null || bookDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("书名不能为空");
        }

        //In-stock verification
        if (bookDTO.getTotalCopiesInStock() != null && bookDTO.getTotalCopiesInStock() < 0) {
            throw new RuntimeException("库存数量不能为负数");
        }
    }

    @Override
    public Books convertToEntity(BookDTO bookDTO) {
        Books entity = new Books();
        entity.setIsbn(bookDTO.getIsbn());
        entity.setTitle(bookDTO.getTitle());
        entity.setAuthor(bookDTO.getAuthor());
        entity.setPublisher(bookDTO.getPublisher());
        entity.setAddedAt(bookDTO.getAddedAt());
        entity.setUpdatedAt(bookDTO.getUpdatedAt());
        entity.setTotalCopiesInStock(bookDTO.getTotalCopiesInStock());
        entity.setCoverUrl(bookDTO.getCoverUrl());
        entity.setBorrowedNumber(bookDTO.getBorrowedNumbers());
        entity.setUpdatedBy(bookDTO.getUpdatedBy());
        return entity;
    }

    @Override
    public BookDTO convertToDto(Books entity) {
        return BookDTO.builder()
                .isbn(entity.getIsbn())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .publisher(entity.getPublisher())
                .addedAt(entity.getAddedAt())
                .updatedAt(entity.getUpdatedAt())
                .totalCopiesInStock(entity.getTotalCopiesInStock())
                .coverUrl(entity.getCoverUrl())
                .borrowedNumbers(entity.getBorrowedNumber())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public BookVO convertToVO(Books entity) {
        int total_copies =
                recordingsMapper.countUnreturnedByIsbn(entity.getIsbn()) +
                entity.getTotalCopiesInStock();
        return BookVO.builder()
                .isbn(entity.getIsbn())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .publisher(entity.getPublisher())
                .addedAt(entity.getAddedAt())
                .updatedAt(entity.getUpdatedAt())
                .totalCopiesInStock(entity.getTotalCopiesInStock())
                .coverUrl(entity.getCoverUrl())
                .borrowedNumbers(entity.getBorrowedNumber())
                .updatedBy(entity.getUpdatedBy())
                .total_copies(total_copies)
                .build();
    }

    @Override
    public Books mergeUpdateFields(Books existing, BookDTO bookDTO) {
        if (bookDTO.getTitle() != null) existing.setTitle(bookDTO.getTitle());
        if (bookDTO.getAuthor() != null) existing.setAuthor(bookDTO.getAuthor());
        if (bookDTO.getPublisher() != null) existing.setPublisher(bookDTO.getPublisher());
        if (bookDTO.getTotalCopiesInStock() != null) existing.setTotalCopiesInStock(bookDTO.getTotalCopiesInStock());
        if (bookDTO.getCoverUrl() != null) existing.setCoverUrl(bookDTO.getCoverUrl());
        return existing;
    }

    @Override
    public List<BookVO> searchBooks(String title, String isbn, String author, String publisher) {
        Map<String, Object> params = new HashMap<>();
        if (title != null && !title.isEmpty()) {
            params.put("title", "%" + title + "%");
        }
        if (isbn != null && !isbn.isEmpty()) {
            params.put("isbn", "%" + isbn + "%");
        }
        if (author != null && !author.isEmpty()) {
            params.put("author", "%" + author + "%");
        }
        if (publisher != null && !publisher.isEmpty()) {
            params.put("publisher", "%" + publisher + "%");
        }
        return booksMapper.searchBooks(params).stream()
                .map(this::convertToVO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteBook(String isbn) {
        Books existingBook = booksMapper.selectByPrimaryKey(isbn);
        if (existingBook == null) {
            throw new RuntimeException("书籍不存在"+isbn);
        }
        //需要查询用户手中是否有尚未归还的该图书！
        int rows = booksMapper.deleteByPrimaryKey(isbn);
        if (rows == 0) {
            throw new RuntimeException("书籍更新失败，可能已被其他用户修改");
        }
    }
}
