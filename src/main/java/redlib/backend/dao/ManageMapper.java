package redlib.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import redlib.backend.dto.ReaderDTO;
import redlib.backend.dto.SupervisorDTO;
import redlib.backend.model.Readers;
import redlib.backend.vo.ReaderVO;
import redlib.backend.vo.SupervisorVO;
import java.util.List;

@Mapper
public interface ManageMapper {

    void deleteSupervisor(@Param("username") String username);
    void deleteSupervisorPriv(@Param("username") String username);

    List<SupervisorVO> listSupervisors(Integer pageSize, Integer offset);

    void insertSupervisor(
            @Param("username") String username,
            @Param("encrypted_password") String encryptedPassword
    );

    void updateSupervisorNameAndPassword(
            @Param("oldUsername") String oldUsername,
            @Param("oldPassword") String oldPassword,
            @Param("newUsername") String newUsername,
            @Param("newPassword") String newPassword
    );

    SupervisorDTO selectByUsername(@Param("username") String username);

    int countSupervisors();

    void insertReader(
            @Param("username") String username,
            @Param("studentId") String studentId,
            @Param("contact") String contact,
            @Param("password") String password
    );

    void deleteReaderPriv(@Param("username") String username);

    void deleteReader(@Param("username") String username);

    List<ReaderVO> listReaders(
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset
    );

    void updateReaderNameAndPassword(
            @Param("oldUsername") String oldUsername,
            @Param("oldPassword") String oldPassword,
            @Param("newUsername") String newUsername,
            @Param("newPassword") String newPassword
    );

    ReaderDTO selectByReaderName(@Param("username") String username, @Param("studentId") String studentId);

    int countReaders();

    Integer selectReaderIdByStudentId(String studentId);
}
