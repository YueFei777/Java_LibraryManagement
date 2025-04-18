package redlib.backend.service;

import org.springframework.transaction.annotation.Transactional;
import redlib.backend.dto.ReaderCreationDTO;
import redlib.backend.dto.ReaderDTO;
import redlib.backend.model.Page;
import redlib.backend.model.UserType;
import redlib.backend.vo.ReaderVO;
import redlib.backend.vo.SupervisorVO;

public interface AdminService {
    public void suspendPrivileges(UserType userType, Integer userId, String modId, String priv);
    public void empowerPrivileges(UserType userType, Integer userId, String modId, String priv);

    void deletePrivileges(UserType userType, Integer userId, String modId, String priv);

    Page<SupervisorVO> listSupervisors(int pageNum, int pageSize);

    @Transactional
    void addSupervisor(String username, String rawPassword);

    @Transactional
    void deleteSupervisor(String username);

    @Transactional
    void updateCredentials(
            String oldUsername,
            String oldRawPassword,
            String newUsername,
            String newRawPassword
    );


    @Transactional
    void addReader(ReaderCreationDTO readerCreationDTO);

    Page<ReaderVO> listReaders(int pageNum, int pageSize);

    @Transactional
    void deleteReader(String username);

    @Transactional
    void updateReaderCredentials(
            String oldUserName,
            String student_id,
            String oldRawPassword,
            String newUserName,
            String newRawPassword
    );

    ReaderVO getReaderDetails(String username, String studentID);
}
