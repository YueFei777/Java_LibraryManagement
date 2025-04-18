package redlib.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redlib.backend.annotation.BackendModule;
import redlib.backend.annotation.Privilege;
import redlib.backend.annotation.Reader;
import redlib.backend.dto.RecordingsDTO;
import redlib.backend.model.ApiResponse;
import redlib.backend.model.Page;
import redlib.backend.service.RecordingsService;
import redlib.backend.service.utils.ListType;
import redlib.backend.utils.ThreadContextHolder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recordings")
@BackendModule({"recordings.list:列出当前用户全部借还情况", "recordings.listUnreturned:列出当前用户未还书目", "recordings.borrow:借阅", "recordings.return:还书", "recordings.listAll:列出全部借还情况"})
public class RecordingsController {

    private final RecordingsService recordingService;

    @GetMapping
    @Reader
    @Privilege({"recordings.list"})
    public Page<RecordingsDTO> getRecordings(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        String username = ThreadContextHolder.getToken().getUserName();
        return recordingService.getRecordingsByUser(username, current, pageSize);
    }

    @GetMapping("/unreturned")
    @Reader
    @Privilege({"recordings.listUnreturned"})
    public Page<RecordingsDTO> getUnreturnedRecordings(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        String username = ThreadContextHolder.getToken().getUserName();
        return recordingService.getUnreturnedRecordingsByUser(username, current, pageSize);
    }

    @PostMapping("/borrow")
    @Reader
    @Privilege({"recordings.borrow"})
    public ResponseEntity<ApiResponse> borrowBook(
            @RequestParam String isbn
    ){
        String currentUser = ThreadContextHolder.getToken().getUserName();
        recordingService.borrowBook(currentUser, isbn);
        return ResponseEntity.ok()
                .body(ApiResponse.success("book borrowed."));
    }

    @PostMapping("/return")
    @Reader
    @Privilege({"recordings.return"})
    public ResponseEntity<ApiResponse> returnBook(
            @RequestParam String isbn
    ){
        String currentUser = ThreadContextHolder.getToken().getUserName();
        recordingService.returnBook(currentUser, isbn);
        return ResponseEntity.ok()
                .body(ApiResponse.success("book returned."));
    }

    @GetMapping("/listAll/{returned}")
    @Privilege("recordings.listAll")
    public Page<RecordingsDTO> listAllRecordings(
            @PathVariable Boolean returned,
            @RequestParam(defaultValue = "DESC") ListType listType,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return recordingService.listAll(returned, listType, current, pageSize);
    }
}
