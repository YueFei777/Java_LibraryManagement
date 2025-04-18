package redlib.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redlib.backend.dao.ManageMapper;
import redlib.backend.dao.UserPrivilegeMapper;
import redlib.backend.dto.ReaderCreationDTO;
import redlib.backend.dto.ReaderDTO;
import redlib.backend.dto.SupervisorDTO;
import redlib.backend.model.Page;
import redlib.backend.model.Readers;
import redlib.backend.model.UserType;
import redlib.backend.service.AdminService;
import redlib.backend.utils.FormatUtils;
import redlib.backend.vo.ReaderVO;
import redlib.backend.vo.SupervisorVO;

import java.lang.RuntimeException;
import java.util.List;

import static redlib.backend.model.UserType.root;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserPrivilegeMapper userPrivMapper;

    private final ManageMapper manageMapper;

    @Override
    public void suspendPrivileges(UserType userType, Integer userId, String modId, String priv) {
        if (root.equals(userType)) {
            throw new IllegalArgumentException("root can not be suspended");
        }

        userPrivMapper.suspendUser(
                userType,
                userId,
                modId != null ? modId.trim() : null,
                priv != null ? priv.trim() : null
        );
    }

    @Override
    public void empowerPrivileges(UserType userType, Integer userId, String modId, String priv) {
        if (root.equals(userType)) {
            throw new IllegalArgumentException("operation invalid");
        }
        userPrivMapper.buildPrivilege(userType, userId, modId, priv);
    }

    @Override
    public void deletePrivileges(UserType userType, Integer userId, String modId, String priv) {
        if (UserType.root.equals(userType)) {
            throw new IllegalArgumentException("privileges from root can not be deleted");
        }
        userPrivMapper.deletePrivilege(userType, userId, modId, priv);
    }

    @Override
    public Page<SupervisorVO> listSupervisors(int pageNum, int pageSize){
        int offset = (pageNum - 1) * pageSize;
        List<SupervisorVO> content = manageMapper.listSupervisors(pageSize, offset);
        int total = manageMapper.countSupervisors();
        return new Page<>(pageNum, pageSize, total, content);
    }

    @Transactional
    @Override
    public void addSupervisor(String username, String rawPassword){
        if(manageMapper.selectByUsername(username) != null){
            throw new RuntimeException("用户名已存在");
        }

        String encryptedPassword = FormatUtils.password(rawPassword);
        manageMapper.insertSupervisor(username, encryptedPassword);

    }

    @Transactional
    @Override
    public void deleteSupervisor(String username){
        manageMapper.deleteSupervisor(username);
        manageMapper.deleteSupervisorPriv(username);
    }

    @Transactional
    @Override
    public void updateCredentials(
            String oldUsername,
            String oldRawPassword,
            String newUsername,
            String newRawPassword
    ){
        SupervisorDTO supervisorDTO = manageMapper.selectByUsername(oldUsername);
        if(supervisorDTO == null){
            throw new RuntimeException("c");
        }
        if(!FormatUtils.password(oldRawPassword).equals(supervisorDTO.getPassword())){
            throw new RuntimeException("密码错误");
        }

        String encryptedNewPassword = FormatUtils.password(newRawPassword);
        manageMapper.updateSupervisorNameAndPassword(oldUsername, supervisorDTO.getPassword(), newUsername, encryptedNewPassword);
    }

    @Transactional
    @Override
    public void addReader(ReaderCreationDTO readerCreationDTO){
        if(manageMapper.selectByReaderName(readerCreationDTO.getUsername(), readerCreationDTO.getStudent_id()) != null){
            throw new RuntimeException("用户已存在");
        }

        String encryptedPwd = FormatUtils.password(readerCreationDTO.getRawPassword());
        manageMapper.insertReader(readerCreationDTO.getUsername(), readerCreationDTO.getStudent_id(), readerCreationDTO.getContact(), encryptedPwd);
    }

    @Override
    public Page<ReaderVO> listReaders(int pageNum, int pageSize){
        int offset = (pageNum - 1) * pageSize;
        List<ReaderVO> content = manageMapper.listReaders(pageSize, offset);
        int total = manageMapper.countReaders();
        return new Page<>(pageNum, pageSize, total, content);
    }

    @Transactional
    @Override
    public void deleteReader(String username){
        manageMapper.deleteReaderPriv(username);
        manageMapper.deleteReader(username);
    }

    @Transactional
    @Override
    public void updateReaderCredentials(
            String oldUserName,
            String student_id,
            String oldRawPassword,
            String newUserName,
            String newRawPassword
    ){
        Readers reader = manageMapper.selectByReaderName(oldUserName, student_id);
        if(reader == null){
            throw new RuntimeException("用户不存在");
        }
        if(!FormatUtils.password(oldRawPassword).equals(reader.getPassword())){
            throw new RuntimeException("密码错误");
        }

        String encryptedNewPassword = FormatUtils.password(newRawPassword);
        manageMapper.updateReaderNameAndPassword(oldUserName, reader.getPassword(), newUserName, encryptedNewPassword);
    }

    @Override
    public ReaderVO getReaderDetails(String username, String studentID){
        ReaderDTO readerDTO = manageMapper.selectByReaderName(username, studentID);
        if(readerDTO == null){
            throw new RuntimeException("用户不存在");
        }
        ReaderVO readerVO = new ReaderVO();
        BeanUtils.copyProperties(readerDTO, readerVO);
        return readerVO;
    }
}