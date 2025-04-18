package redlib.backend.dao;

import lombok.Builder;
import org.apache.ibatis.annotations.Param;
import redlib.backend.model.Recordings;

import java.util.List;

public interface RecordingsMapper {
    List<Recordings> listRecordingByUserName(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    List<Recordings> listUnreturnedByUserName(
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    int countByUserName(@Param("username") String username);

    int countUnreturnedByUserName(@Param("username") String username);

    int countUnreturnedByIsbn(@Param("isbn") String isbn);

    int borrowBook(@Param("username") String username,
                   @Param("isbn") String isbn);

    int returnBook(@Param("username") String username,
                   @Param("isbn") String isbn);

    int checkUnreturned(@Param("username") String username,
                        @Param("isbn") String isbn);

    List<Recordings> listAll(Boolean returned, String sortOrder);

}
