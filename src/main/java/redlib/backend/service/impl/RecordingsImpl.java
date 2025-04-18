package redlib.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redlib.backend.dao.BooksMapper;
import redlib.backend.dao.RecordingsMapper;
import redlib.backend.dto.RecordingsDTO;
import redlib.backend.model.Page;
import redlib.backend.model.Recordings;
import redlib.backend.service.RecordingsService;
import redlib.backend.service.utils.ListType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static redlib.backend.Constants.CHECKOUT_PERIOD;

@Service
@RequiredArgsConstructor
public class RecordingsImpl implements RecordingsService {

    private final RecordingsMapper recordingMapper;

    private final BooksMapper bookMapper;

    @Override
    public Page<RecordingsDTO> getRecordingsByUser(String username, int pageNum, int pageSize) {
        int total = recordingMapper.countByUserName(username);
        if (total == 0) {
            throw new RuntimeException("当前用户无借阅记录");
        }
        int offset = (pageNum - 1) * pageSize;

        List<Recordings> content = recordingMapper.listRecordingByUserName(username, offset, pageSize);
        List<RecordingsDTO> content_filter = convertToDTOList(content);
        return new Page<>(pageNum, pageSize, total, content_filter);
    }

    @Override
    public Page<RecordingsDTO> getUnreturnedRecordingsByUser(String username, int pageNum, int pageSize) {
        int total = recordingMapper.countUnreturnedByUserName(username);
        if (total == 0) {
            throw new RuntimeException("当前用户无借在借图书");
        }
        int offset = (pageNum - 1) * pageSize;

        List<Recordings> content = recordingMapper.listUnreturnedByUserName(username, offset, pageSize);
        List<RecordingsDTO> content_filter = convertToDTOList(content);
        return new Page<>(pageNum, pageSize, total, content_filter);
    }

    @Override
    public List<RecordingsDTO> convertToDTOList(List<Recordings> RecordingList) {
        return RecordingList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void borrowBook(String username, String isbn) {
        if(recordingMapper.checkUnreturned(username, isbn) > 0) {
            throw new RuntimeException("该书籍尚未归还，无法重复借阅");
        }

        int copies = bookMapper.checkCopies(isbn);
        if(copies <= 0) {
            throw new RuntimeException("库存不足");
        }

        int unreturned = recordingMapper.countUnreturnedByUserName(username);
        if(unreturned > 5) {
            throw new RuntimeException("未还书籍过多，无法借阅");
        }

        int result = recordingMapper.borrowBook(username, isbn);
        if (result == 0) {
            throw new RuntimeException("借书失败，请联系管理员");
        }

        bookMapper.updateCopies(isbn, copies - 1);
    }

    @Override
    @Transactional
    public void returnBook(String username, String isbn) {
        int result = recordingMapper.returnBook(username, isbn);
        int copies = bookMapper.checkCopies(isbn);
        if (result == 0) {
            throw new RuntimeException("还书失败，无有效借阅记录");
        }
        bookMapper.updateCopies(isbn, copies + 1);
        bookMapper.updateBorrowedNumber(isbn);
    }


    private RecordingsDTO convertToDTO(Recordings recordings){
        RecordingsDTO recordingsDTO = new RecordingsDTO();
        recordingsDTO.setReaderId(recordings.getReaderId());
        recordingsDTO.setUsername(recordings.getUsername());
        recordingsDTO.setBook_title(bookMapper.selectByPrimaryKey(recordings.getIsbn()).getTitle());
        recordingsDTO.setIsbn(recordings.getIsbn());
        recordingsDTO.setBorrowedTime(recordings.getBorrowedTime());
        recordingsDTO.setReturnedTime(recordings.getReturnedTime());

        if (recordings.getReturnedTime() != null) {
            recordingsDTO.setOverdue(Boolean.FALSE);
            return recordingsDTO;
        }

        LocalDateTime dueDate = recordings.getBorrowedTime().plusDays(CHECKOUT_PERIOD);
        recordingsDTO.setOverdue(LocalDateTime.now().isAfter(dueDate));
        return recordingsDTO;
    }

    @Override
    public Page<RecordingsDTO> listAll(Boolean returned, ListType listType, int pageNum, int pageSize) {
        List<Recordings> content = recordingMapper.listAll(returned, listType.toString());
        int total = content.size();
        List<RecordingsDTO> content_filter = convertToDTOList(content);
        return new Page<>(pageNum, pageSize, total, content_filter);
    }
}
