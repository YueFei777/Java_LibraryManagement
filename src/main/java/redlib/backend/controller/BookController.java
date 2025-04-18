package redlib.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redlib.backend.annotation.BackendModule;
import redlib.backend.annotation.Privilege;
import redlib.backend.annotation.Reader;
import redlib.backend.dto.BookDTO;
import redlib.backend.service.BookService;
import redlib.backend.utils.ThreadContextHolder;
import redlib.backend.vo.BookVO;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@BackendModule({"books.list:查看所有", "books.create:新建", "books.update:更新",
        "books.delete:删除", "books.search:查询"})
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 获取所有书籍（分页示例）
    @GetMapping
    @Reader
    @Privilege({"books.list"})
    public ResponseEntity<List<BookVO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.listBooks(page, size));
    }
    
    // 创建新书（自动填充时间字段）
    @PostMapping
    @Privilege({"books.create"})
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookDTO bookDTO) {
        String currentUser = ThreadContextHolder.getToken().getUserName();
        // 自动填充字段
        bookDTO.setAddedAt(LocalDate.now());
        bookDTO.setUpdatedAt(LocalDate.now());
        bookDTO.setUpdatedBy(currentUser);
        return new ResponseEntity<>(bookService.createBook(bookDTO, currentUser), HttpStatus.CREATED);
    }

    // 更新书籍信息
    @PutMapping("/{isbn}")
    @Privilege({"books.update"})
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable String isbn,
            @Valid @RequestBody BookDTO bookDTO) {
        String currentUser = ThreadContextHolder.getToken().getUserName();
        bookDTO.setUpdatedAt(LocalDate.now());
        bookDTO.setUpdatedBy(currentUser);
        return ResponseEntity.ok(bookService.updateBook(isbn, bookDTO, currentUser));
    }

    // 删除书籍
    @DeleteMapping("/{isbn}")
    @Privilege({"books.delete"})
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        if (isbn == null || isbn.length() < 10 || isbn.length() > 13) {
            throw new java.lang.RuntimeException("ISBN格式错误");
        }

        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

    // 搜索书籍（多条件查询）
    @GetMapping("/search")
    @Reader
    @Privilege({"books.search"})
    public ResponseEntity<List<BookVO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher) {
        return ResponseEntity.ok(bookService.searchBooks(title,isbn, author, publisher));
    }
}
