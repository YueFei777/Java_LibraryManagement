package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.Books;

import java.util.List;
import java.util.Map;

@Mapper
public interface BooksMapper {
    int deleteByPrimaryKey(String isbn);

    int insert(Books record);

    Books selectByPrimaryKey(String isbn);


    int updateByPrimaryKeySelective(Books record);

    int updateByPrimaryKey(Books record);

    /**
     * 分页查询书籍列表
     * @param offset 起始位置（page * size）
     * @param size   每页数量
     * @return 分页后的书籍列表
     */
    List<Books> list(@Param("offset") int offset, @Param("size") int size);

    /**
     * 动态条件搜索书籍
     * @param params 包含 title/author/publisher 的 Map
     * @return 符合条件的书籍列表
     */
    List<Books> searchBooks(@Param("params") Map<String, Object> params);

    int checkCopies(@Param("isbn") String isbn);

    void updateCopies(@Param("isbn") String isbn, @Param("total_copies_in_stock") int total_copies_in_stock);

    Integer checkBorrowedNumber(@Param("isbn") String isbn);

    void updateBorrowedNumber(@Param("isbn") String isbn);

}