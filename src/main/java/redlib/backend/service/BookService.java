package redlib.backend.service;

import redlib.backend.dto.BookDTO;
import redlib.backend.model.Books;
import redlib.backend.vo.BookVO;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO, String currentUser);
    BookDTO updateBook(String isbn, BookDTO bookDTO, String currentUser);

    List<BookVO> listBooks(int page, int size);
    void validateBook(BookDTO bookDTO);
    Books convertToEntity(BookDTO bookDTO);
    BookDTO convertToDto(Books book);
    Books mergeUpdateFields(Books existing, BookDTO bookDTO);

    List<BookVO> searchBooks(String title, String isbn, String author, String publisher);

    void deleteBook(String isbn);
}
