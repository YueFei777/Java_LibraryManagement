package redlib.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import redlib.backend.model.Readers;
import redlib.backend.vo.RecordingsVO;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReaderDTO extends Readers {
    private List<RecordingsVO> recordings;
}
