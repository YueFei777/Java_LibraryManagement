package redlib.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redlib.backend.annotation.BackendModule;
import redlib.backend.annotation.Privilege;
import redlib.backend.dao.ManageMapper;
import redlib.backend.dto.ReaderCreationDTO;
import redlib.backend.dto.RecordingsDTO;
import redlib.backend.model.ApiResponse;
import redlib.backend.model.Page;
import redlib.backend.model.UserType;
import redlib.backend.service.AdminService;
import redlib.backend.service.RecordingsService;
import redlib.backend.utils.ThreadContextHolder;
import redlib.backend.vo.ReaderVO;
import redlib.backend.vo.SupervisorVO;

import static ch.qos.logback.core.joran.JoranConstants.NULL;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@BackendModule({"admin.suspend: 挂起", "admin.list: 查询用户日志",
        "admin.restore: 赋予权力", "admin.delete: 删除权限", "admin.supervisor: 对主管的CRUD", "admin.reader: 对读者的CRUD"})
public class AdminController {

    private final RecordingsService recordingsService;

    private final ManageMapper manageMapper;

    private final AdminService adminService;

    @PostMapping("/suspend")
    @Privilege("admin.suspend")
    public ResponseEntity<ApiResponse> suspendUserPrivileges(
            @RequestParam(name = "user_type") UserType userType,
            @RequestParam(name = "user_id") Integer userId,
            @RequestParam(name = "mod_id", required = false) String modId,
            @RequestParam(name = "priv", required = false) String priv) {

        try {
            adminService.suspendPrivileges(userType, userId, modId, priv);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("user suspended"));
        } catch (DataAccessException e) {
            throw new RuntimeException("suspension failed, " + e.getMessage());
        }
    }

    @PostMapping("/restore")
    @Privilege("admin.restore")
    public ResponseEntity<ApiResponse> restoreUserPrivileges(
            @RequestParam(name = "user_type") UserType userType,
            @RequestParam(name = "user_id") Integer userId,
            @RequestParam(name = "mod_id") String modId,
            @RequestParam(name = "priv") String priv) {

        adminService.empowerPrivileges(userType, userId, modId, priv);
        return ResponseEntity.ok()
                .body(ApiResponse.success("restoring successfully"));
    }

    @GetMapping("/recordings/listByName")
    @Privilege("admin.list")
    public Page getRecordings(
            @RequestParam String username,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return recordingsService.getRecordingsByUser(username, current, pageSize);
    }

    @PostMapping("/delete")
    @Privilege("admin.delete")
    public ResponseEntity<ApiResponse> deletePrivileges(
            @RequestParam(name = "user_type") UserType userType,
            @RequestParam(name = "user_id") Integer userId,
            @RequestParam(name = "mod_id") String modId,
            @RequestParam(name = "priv") String priv) {

        try {
            adminService.deletePrivileges(userType, userId, modId, priv);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("privilege deleted"));
        } catch (DataAccessException e) {
            throw new RuntimeException("privilege deletion failed: " + e.getMessage());
        }
    }


    @GetMapping("/supervisors/list")
    @Privilege("admin.supervisor")
    public Page<SupervisorVO> listSupervisors(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return adminService.listSupervisors(pageNum, pageSize);
    }

    @PostMapping("/supervisors/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Privilege("admin.supervisor")
    public ResponseEntity<ApiResponse> addSupervisor(
            @RequestParam String username,
            @RequestParam String password // 按应用规范这里应该用POST body传输敏感数据
    ) {
        adminService.addSupervisor(username, password);
        return ResponseEntity.ok()
                .body(ApiResponse.success("supervisor adding successfully"));
    }

    @DeleteMapping("/supervisors/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Privilege("admin.supervisor")
    public ResponseEntity<ApiResponse> deleteSupervisor(
            @PathVariable String username) {
        String currentUsername = ThreadContextHolder.getToken().getUserName();
        if(currentUsername.equals(username)) {
            throw new RuntimeException("self deletion is not allowed");
        }
        if(manageMapper.selectByUsername(username) == null) {
            throw new RuntimeException("supervisor doesn't exist");
        }
        adminService.deleteSupervisor(username);
        return ResponseEntity.ok()
                .body(ApiResponse.success("supervisor deleting successfully"));
    }

    @PutMapping("/supervisors/credentials")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Privilege("admin.supervisor")
    public ResponseEntity<ApiResponse> updateSupervisorCredentials(
            @RequestParam String oldUsername,
            @RequestParam String oldPassword,
            @RequestParam String newUsername,
            @RequestParam String newPassword
    ) {
        adminService.updateCredentials(
                oldUsername,
                oldPassword,
                newUsername,
                newPassword
        );
        return ResponseEntity.ok()
                .body(ApiResponse.success("supervisor updating successfully"));
    }

    @PostMapping("/readers/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Privilege("admin.reader")
    public ResponseEntity<ApiResponse> createReader(@RequestBody ReaderCreationDTO readerCreationDTO) {
        adminService.addReader(readerCreationDTO);
        restoreUserPrivileges(UserType.reader,
                manageMapper.selectReaderIdByStudentId(readerCreationDTO.getStudent_id()), "reader", NULL);
        return ResponseEntity.ok()
                .body(ApiResponse.success("reader creating successfully"));
    }

    @GetMapping("/readers/list")
    @Privilege("admin.reader")
    public Page<ReaderVO> listReaders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return adminService.listReaders(pageNum, pageSize);
    }

    @DeleteMapping("/readers/delete/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Privilege("admin.reader")
    public ResponseEntity<ApiResponse> deleteReader(@PathVariable String username) {

        Page page = recordingsService.getUnreturnedRecordingsByUser(username, 1, 10);

        if (page.getTotal() > 0) {
            throw new RuntimeException("reader" + username + "cannot be deleted. " + "he/she " +
                            "still have " + page.getTotal() + " book(s) to be returned.");
        }
        adminService.deleteReader(username);
        return ResponseEntity.ok()
                .body(ApiResponse.success("reader deleting successfully"));
    }

    @PutMapping("/readers/credentials")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Privilege("admin.reader")
    public ResponseEntity<ApiResponse> updateReaderCredentials(
            @RequestParam String oldUsername,
            @RequestParam String oldPassword,
            @RequestParam String studentId,
            @RequestParam String newUsername,
            @RequestParam String newPassword
    ){
        if(oldPassword.equals(newPassword)) {
            throw new RuntimeException("old and new passwords could not be the same");
        }
        adminService.updateReaderCredentials(
                oldUsername,
                studentId,
                oldPassword,
                newUsername,
                newPassword
        );
        return ResponseEntity.ok()
                .body(ApiResponse.success("reader update successfully"));
    }

    @GetMapping("/readers/details")
    @Privilege("admin.reader")
    public ReaderVO getReaderDetails(
            @RequestParam String username,
            @RequestParam String studentID
    ) {
        return adminService.getReaderDetails(username, studentID);
    }
}