package redlib.backend.service;

import redlib.backend.dto.RecordingsDTO;
import redlib.backend.model.Page;
import redlib.backend.model.Recordings;
import redlib.backend.service.utils.ListType;

import java.util.List;

public interface RecordingsService {
    public Page<RecordingsDTO> getRecordingsByUser(String username, int pageNum, int pageSize);
    public Page<RecordingsDTO> getUnreturnedRecordingsByUser(String username, int pageNum, int pageSize);
    public List<RecordingsDTO> convertToDTOList(List<Recordings> RecordingList);
    public void borrowBook(String username, String isbn);
    public void returnBook(String username, String isbn);

    Page<RecordingsDTO> listAll(Boolean returned, ListType listType, int pageNum, int pageSize);
}
